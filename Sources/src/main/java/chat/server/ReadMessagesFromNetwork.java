/**
This file is part of the muDEBS middleware.

Copyright (C) 2012-2017 Télécom SudParis

This is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This software platform is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the muDEBS platform. If not, see <http://www.gnu.org/licenses/>.

Initial developer(s): Denis Conan
Contributor(s):
 */
package chat.server;

import static chat.common.Log.COMM;
import static chat.common.Log.GEN;
import static chat.common.Log.LOG_ON;

import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

import chat.common.FullDuplexMsgWorker;
import chat.common.ReadMessageStatus;

/**
 * This class defines the main of the chat server. It configures the server,
 * connects to existing chat servers, waits for connections from other chat
 * servers and from chat clients, and forwards chat messages received from chat
 * clients to other 'local' chat clients and to the other chat servers.
 * 
 * The chat servers can be organised into a network topology forming cycles
 * since the method <tt>forward</tt> is only called when the message to forward
 * has not already been received and forwarded.
 * 
 * @author chris
 * @author Denis Conan
 * 
 */
public class ReadMessagesFromNetwork implements Runnable {
	/**
	 * backward reference to the server selector object in order to use its
	 * methods to send messages.
	 */
	private final Server server;

	/**
	 * the selector.
	 */
	private final Selector selector;

	/**
	 * the selection key for accepting client connections.
	 */
	private final SelectionKey acceptClientKey;

	/**
	 * server socket channel for accepting client connections.
	 */
	private ServerSocketChannel listenChanClient;

	/**
	 * the selection key for accepting server connections.
	 */
	private final SelectionKey acceptServerKey;

	/**
	 * server socket channel for accepting server connections.
	 */
	private ServerSocketChannel listenChanServer;

	/**
	 * state of the server object. This is where all the attributes of the chat
	 * server are stored.
	 */
	private State state;

	/**
	 * initialises the collection attributes and the state of the server, and
	 * creates the channels that are accepting connections from clients and
	 * servers.
	 * 
	 * @param server
	 *            the reference to the server.
	 * @param selector
	 *            the selector.
	 * @param acceptClientKey
	 *            the selection key for accepting client connections.
	 * @param listenChanClient
	 *            the server socket channel for accepting client connections.
	 * @param acceptServerKey
	 *            the selection key for accepting server connections.
	 * @param listenChanServer
	 *            the server socket channel for accepting server connections.
	 * @param state
	 *            the reference to the state objec of the server where all the
	 *            attributes are stored.
	 */
	public ReadMessagesFromNetwork(final Server server, final Selector selector,
			final SelectionKey acceptClientKey,
			final ServerSocketChannel listenChanClient,
			final SelectionKey acceptServerKey,
			final ServerSocketChannel listenChanServer, final State state) {
		if (server == null || selector == null || acceptClientKey == null
				|| listenChanClient == null || acceptServerKey == null
				|| listenChanServer == null || state == null) {
			if (LOG_ON) {
				GEN.error("One of the argument is null (" + server + ", "
						+ selector + ", " + acceptClientKey + ", "
						+ listenChanClient + ", " + acceptServerKey + ", "
						+ listenChanClient + ", " + state + ")");
			}
			throw new IllegalArgumentException("One of the argument is null ("
					+ server + ", " + selector + ", " + acceptClientKey + ", "
					+ listenChanClient + ", " + acceptServerKey + ", "
					+ listenChanClient + ", " + state + ")");
		}
		this.state = state;
		this.selector = selector;
		this.acceptClientKey = acceptClientKey;
		this.listenChanClient = listenChanClient;
		this.acceptServerKey = acceptServerKey;
		this.listenChanServer = listenChanServer;
		this.server = server;
	}

	/**
	 * is the infinite loop organised around the call to select.
	 */
	@Override
	public void run() {
		if (LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("Server thread for rcving msgs from the network started");
		}
		while (!Thread.interrupted()) {
			try {
				selector.select();
			} catch (IOException e) {
				COMM.fatal(e.getLocalizedMessage());
				e.printStackTrace();
				return;
			}
			Set<SelectionKey> readyKeys = selector.selectedKeys();
			Iterator<SelectionKey> readyIter = readyKeys.iterator();
			while (readyIter.hasNext()) {
				SelectionKey key = readyIter.next();
				readyIter.remove();
				if (key.isAcceptable()) {
					try {
						if (key.equals(acceptServerKey)) {
							server.acceptNewServer(listenChanServer);
						} else if (key.equals(acceptClientKey)) {
							server.acceptNewClient(listenChanClient);
						} else {
							COMM.fatal("unknown accept");
							return;
						}
					} catch (IOException e) {
						COMM.error(e.getLocalizedMessage());
						e.printStackTrace();
					}
				}
				if (key.isReadable()) {
					FullDuplexMsgWorker serverWorker = null;
					synchronized (state) {
						state.currKey = key;
						serverWorker = state.allServerWorkers.get(key);
					}
					if (serverWorker != null) {
						treatMessageFromNeighbouringServer(key, serverWorker);
					}
					FullDuplexMsgWorker clientWorker = null;
					synchronized (state) {
						clientWorker = state.allClientWorkers.get(key);
					}
					if (clientWorker != null) {
						treatMessageFromLocalClient(key, clientWorker);
					}
				}
			}
		}
	}

	/**
	 * treats the messages received from a neighbouring server.
	 * 
	 * @param key
	 *            the selection key corresponding to the worker.
	 * @param readWorker
	 *            the worker to read the message from.
	 */
	private void treatMessageFromNeighbouringServer(final SelectionKey key,
			final FullDuplexMsgWorker readWorker) {
		// message comes from another server
		try {
			ReadMessageStatus status;
			status = readWorker.readMessage();
			if (status == ReadMessageStatus.ChannelClosed) {
				// remote end point has been closed
				readWorker.close();
				synchronized (state) {
					state.allServerWorkers.remove(key);
					if (LOG_ON && COMM.isInfoEnabled()) {
						COMM.info("Closing a channel");
						COMM.debug("  allServerWorkers.size() = "
								+ state.allServerWorkers.size());
					}
				}
			}
			if (status == ReadMessageStatus.ReadDataCompleted) {
				int messType = readWorker.getInType();
				Serializable msg = readWorker.getData();
				if (LOG_ON && COMM.isInfoEnabled()) {
					COMM.info("Message received " + readWorker.getInSeqNumber()
							+ ", " + msg + ", " + msg.getClass().getName());
				}
				if (messType < chat.common.Action.OFFSET_CLIENT_ALGORITHMS) {
					// message for server
					if (LOG_ON && COMM.isTraceEnabled()) {
						COMM.trace("Going to execute action"
								+ " for message type #" + messType
								+ " on content " + msg);
					}
					chat.server.algorithms.ListOfAlgorithms.execute(state,
							messType, msg);
				} else {
					// client message to forward
					int identity = readWorker.getInIdentity();
					int seqNumber = readWorker.getInSeqNumber();
					synchronized (state) {
						if (state.clientSeqNumbers.get(identity) == null) {
							state.clientSeqNumbers.put(identity, seqNumber);
							server.forward(key, messType, identity, seqNumber,
									msg);
						} else {
							if (seqNumber > state.clientSeqNumbers
									.get(identity)) {
								// not already forwarded
								state.clientSeqNumbers.put(identity, seqNumber);
								server.forward(key, messType, identity,
										seqNumber, msg);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			COMM.error(e.getLocalizedMessage());
		}
	}

	/**
	 * treats the messages received from a neighbouring server.
	 * 
	 * @param key
	 *            the selection key corresponding to the worker.
	 * @param readWorker
	 *            the worker to read the message from.
	 */
	private void treatMessageFromLocalClient(final SelectionKey key,
			final FullDuplexMsgWorker readWorker) {
		try {
			ReadMessageStatus status;
			status = readWorker.readMessage();
			if (status == ReadMessageStatus.ChannelClosed) {
				readWorker.close();
				synchronized (state) {
					state.allClientWorkers.remove(key);
					if (LOG_ON && COMM.isInfoEnabled()) {
						COMM.info("Closing a channel");
						COMM.debug("  allClientWorkers.size() = "
								+ state.allClientWorkers.size());
					}
				}
			}
			if (status == ReadMessageStatus.ReadDataCompleted) {
				int messType = readWorker.getInType();
				Serializable msg = readWorker.getData();
				if (LOG_ON && COMM.isInfoEnabled()) {
					COMM.info("Message received " + msg + " "
							+ msg.getClass().getName());
				}
				int identity = readWorker.getInIdentity();
				synchronized (state) {
					int seqNumber = state.seqNumber++;
					state.clientSeqNumbers.put(identity, seqNumber);
					server.forward(key, messType, identity, seqNumber, msg);
					
				}
			}
		} catch (IOException e) {
			COMM.error(e.getStackTrace());
			e.printStackTrace();
		}
	}
}
