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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import chat.common.FullDuplexMsgWorker;
import chat.common.Interceptor;
import chat.server.algorithms.election.Algorithm;
import chat.server.algorithms.election.ElectionTokenContent;

/**
 * This class defines server object. The server ojbect connects to existing chat
 * servers, waits for connections from other chat servers and from chat clients,
 * and forwards chat messages received from chat clients to other 'local' chat
 * clients and to the other chat servers.
 * 
 * The chat servers can be organised into a network topology forming cycles
 * since the method <tt>forward</tt> is only called when the message to forward
 * has not already been received and forwarded.
 * 
 * @author chris
 * @author Denis Conan
 * 
 */
public class Server {
	/**
	 * the base of the port number for connecting to clients.
	 */
	private static final int BASE_PORTNB_LISTEN_CLIENT = 2050;
	/**
	 * the offset of the port number for connecting to servers.
	 */
	private static final int OFFSET_PORTNB_LISTEN_SERVER = 100;
	/**
	 * the number of clients that have openned a connection to this server till
	 * the beginning of its execution. Each client is assigned an identity in
	 * the form of an integer and this identity is provided by the server it is
	 * connected to: it is the current value of this integer.
	 */
	private int clientNumber = 0;
	/**
	 * server's state for the distributed algorithms implemented in the server.
	 * This attribute is {@code final} because it is used to synchronise code
	 * blocks.
	 */
	private final State state;
	/**
	 * the selector.
	 */
	private Selector selector;
	/**
	 * the runnable object of the server that receives the messages from the
	 * chat clients and the other chat servers.
	 */
	private ReadMessagesFromNetwork runnableToRcvMsgs;
	/**
	 * the thread of the server that receives the messages from the chat clients
	 * and the other chat servers.
	 */
	private Thread threadToRcvMsgs;

	/**
	 * initialises the collection attributes and the state of the server, and
	 * creates the channels that are accepting connections from clients and
	 * servers. At the end of the constructor, the server opens connections to
	 * the other servers (hostname, identifier) that are provided in the command
	 * line arguments.
	 * 
	 * NB: after the construction of a client object, the thread for reading
	 * messages must be started using the method
	 * {@link startThreadReadMessagesFromNetwork}.
	 * 
	 * @param args
	 *            java command arguments.
	 */
	public Server(final String[] args) {
		int identity = Integer.parseInt(args[0]);
		int portnum = BASE_PORTNB_LISTEN_CLIENT + Integer.parseInt(args[0]);
		state = new State(identity, this);
		InetSocketAddress rcvAddressClient;
		InetSocketAddress rcvAddressServer;
		try {
			selector = Selector.open();
		} catch (IOException e) {
			throw new IllegalStateException("cannot create the selector");
		}
		ServerSocketChannel listenChanClient = null;
		ServerSocketChannel listenChanServer = null;
		try {
			listenChanClient = ServerSocketChannel.open();
			listenChanClient.configureBlocking(false);
		} catch (IOException e) {
			throw new IllegalStateException("cannot set the blocking option to a server socket");
		}
		try {
			listenChanServer = ServerSocketChannel.open();
		} catch (IOException e) {
			throw new IllegalStateException("cannot open the server socket" + " for accepting server connections");
		}
		try {
			rcvAddressClient = new InetSocketAddress(portnum);
			listenChanClient.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			rcvAddressServer = new InetSocketAddress(portnum + OFFSET_PORTNB_LISTEN_SERVER);
			listenChanServer.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		} catch (IOException e) {
			throw new IllegalStateException("cannot set the SO_REUSEADDR option");
		}
		try {
			listenChanClient.bind(rcvAddressClient);
			listenChanServer.bind(rcvAddressServer);
		} catch (IOException e) {
			throw new IllegalStateException("cannot bind to a server socket");
		}
		try {
			listenChanClient.configureBlocking(false);
			listenChanServer.configureBlocking(false);
		} catch (IOException e) {
			throw new IllegalStateException("cannot set the blocking option");
		}
		SelectionKey acceptClientKey = null;
		SelectionKey acceptServerKey = null;
		try {
			acceptClientKey = listenChanClient.register(selector, SelectionKey.OP_ACCEPT);
			acceptServerKey = listenChanServer.register(selector, SelectionKey.OP_ACCEPT);
		} catch (ClosedChannelException e) {
			throw new IllegalStateException("cannot register a server socket");
		}
		if (LOG_ON && COMM.isInfoEnabled()) {
			COMM.info("  listenChanClient ok on port " + listenChanClient.socket().getLocalPort());
			COMM.info("  listenChanServer ok on port " + listenChanServer.socket().getLocalPort());
		}
		runnableToRcvMsgs = new ReadMessagesFromNetwork(this, selector, acceptClientKey, listenChanClient,
				acceptServerKey, listenChanServer, state);
		threadToRcvMsgs = new Thread(runnableToRcvMsgs);
		for (int i = 1; i < args.length; i = i + 2) {
			try {
				addServer(args[i],
						(BASE_PORTNB_LISTEN_CLIENT + Integer.parseInt(args[i + 1]) + OFFSET_PORTNB_LISTEN_SERVER));
			} catch (IOException e) {
				COMM.error(e.getLocalizedMessage());
				e.printStackTrace();
				return;
			}
		}
		assert invariant();
	}

	/**
	 * checks the invariant of the class.
	 * 
	 * @return a boolean stating whether the invariant is maintained.
	 */
	public boolean invariant() {
		return clientNumber >= 0 && state != null && runnableToRcvMsgs != null && threadToRcvMsgs != null
				&& state.invariant();
	}

	/**
	 * starts the thread that is responible for reading messages from the
	 * clients and the other servers.
	 */
	public void startThreadReadMessagesFromNetwork() {
		threadToRcvMsgs.start();
	}

	/**
	 * treats an input line from the console.
	 * 
	 * @param line
	 *            the content of the message
	 */
	public void treatConsoleInput(final String line) {

		if (line == null) {
			throw new IllegalArgumentException("no command line");
		} else {

			if (LOG_ON && GEN.isDebugEnabled()) {

				GEN.debug("new command line on console");

			}
			synchronized (state) {
				state.setStatus("Initiator");
				state.setCaw(state.getIdentity());
				System.out.println("status :" + state.getStatus());
				try {
					Thread.sleep(10000);

					sendToAllServers(Algorithm.TOKEN_MESSAGE.identifier(), state.getIdentity(), state.seqNumber,
							new ElectionTokenContent(state.getIdentity(), state.getIdentity()));

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		if (line.equals("quit")) {
			threadToRcvMsgs.interrupt();
			Thread.currentThread().interrupt();
			return;
		}
	}

	public State getState() {
		return state;
	}

	/**
	 * connects socket, creates MsgWorker, and registers selection key of the
	 * remote server. This method is called when connecting to a remote server.
	 * Connection data are provided as arguments to the main.
	 * 
	 * @param host
	 *            remote host's name.
	 * @param port
	 *            remote port's number.
	 * @throws IOException
	 *             the exception thrown in case of communication problem.
	 */
	public void addServer(final String host, final int port) throws IOException {
		Socket rwSock;
		SocketChannel rwChan;
		InetSocketAddress rcvAddress;
		if (LOG_ON && COMM.isInfoEnabled()) {
			COMM.info("Opening connection with server on host " + host + " on port " + port);
		}
		InetAddress destAddr = InetAddress.getByName(host);
		rwChan = SocketChannel.open();
		rwSock = rwChan.socket();
		// obtain the IP address of the target host
		rcvAddress = new InetSocketAddress(destAddr, port);
		// connect sending socket to remote port
		rwSock.connect(rcvAddress);
		FullDuplexMsgWorker worker = new FullDuplexMsgWorker(rwChan);
		worker.configureNonBlocking();
		SelectionKey serverKey = rwChan.register(selector, SelectionKey.OP_READ);
		synchronized (state) {
			state.allServerWorkers.put(serverKey, worker);
			if (LOG_ON && COMM.isDebugEnabled()) {
				COMM.debug("allServerWorkers.size() = " + state.allServerWorkers.size());
			}
		}
	}

	/**
	 * accepts connection (socket level), creates MsgWorker, and registers
	 * selection key of the remote server. This method is called when accepting
	 * a connection from a remote server.
	 * 
	 * @param sc
	 *            server socket channel.
	 * @throws IOException
	 *             the exception thrown in case of communication problem.
	 */
	public void acceptNewServer(final ServerSocketChannel sc) throws IOException {
		SocketChannel rwChan;
		SelectionKey newKey;
		rwChan = sc.accept();
		if (rwChan != null) {
			try {
				FullDuplexMsgWorker worker = new FullDuplexMsgWorker(rwChan);
				worker.configureNonBlocking();
				newKey = rwChan.register(selector, SelectionKey.OP_READ);
				synchronized (state) {
					state.allServerWorkers.put(newKey, worker);
					if (LOG_ON && COMM.isDebugEnabled()) {
						COMM.debug("allServerWorkers.size() = " + state.allServerWorkers.size());
					}
				}
			} catch (ClosedChannelException e) {
				COMM.error(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * the offset to compute the identity of the new client has a function of
	 * the identity of the server and the number of connected clients.
	 */
	public static final int OFFSET_ID_CLIENT = 100;

	/**
	 * accepts connection (socket level), creates MsgWorker, and registers
	 * selection key of the local client. This method is called when accepting a
	 * connection from a local client.
	 * 
	 * @param sc
	 *            server socket channel.
	 * @throws IOException
	 *             the exception thrown in case of communication problem.
	 */
	public void acceptNewClient(final ServerSocketChannel sc) throws IOException {
		SocketChannel rwChan;
		SelectionKey newKey;
		rwChan = sc.accept();
		if (rwChan != null) {
			try {
				FullDuplexMsgWorker worker = new FullDuplexMsgWorker(rwChan);
				worker.configureNonBlocking();
				newKey = rwChan.register(selector, SelectionKey.OP_READ);
				synchronized (state) {
					state.allClientWorkers.put(newKey, worker);
					worker.sendMsg(0, state.getIdentity(), state.seqNumber,
							Integer.valueOf(state.getIdentity() * OFFSET_ID_CLIENT + clientNumber));
					clientNumber++;
				}
			} catch (ClosedChannelException e) {
				COMM.error(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * sends a message to all the remote servers / neighbours connected to this
	 * server. This is a utility method for implementing distributed algorithms
	 * in the servers' state machine: use this method when this server needs
	 * sending messages to its neighbours.
	 * 
	 * @param type
	 *            message's type.
	 * @param identity
	 *            sender's identity, that is the identity of this server.
	 * @param seqNumber
	 *            message's sequence number.
	 * @param msg
	 *            message as a serializable object.
	 * @throws IOException
	 *             the communication exception thrown when sending the message.
	 */
	public void sendToAllServers(final int type, final int identity, final int seqNumber, final Serializable msg)
			throws IOException {
		synchronized (state) {
			state.seqNumber++;
			// send to all the servers, thus first argument is null
			forwardServers(null, type, identity, seqNumber, msg);
		}
	}

	/**
	 * sends a message to a particular remote server / neighbour. This is a
	 * utility method for implementing distributed algorithms in the servers'
	 * state machine: use this method when this server needs sending messages to
	 * a given neighbour.
	 * 
	 * @param targetKey
	 *            selection key of the neighbour.
	 * @param type
	 *            message's type.
	 * @param identity
	 *            sender's identity, that is the identity of this server.
	 * @param seqNumber
	 *            message's sequence number.
	 * @param mgg
	 *            message as a serializable object.
	 * @throws IOException
	 *             the communication exception thrown when sending the message.
	 */
	public void sendToAServer(final SelectionKey targetKey, final int type, final int identity, final int seqNumber,
			final Serializable mgg) throws IOException {
		synchronized (state) {
			state.seqNumber++;
			FullDuplexMsgWorker sendWorker = state.allServerWorkers.get(targetKey);
			if (sendWorker == null) {
				COMM.warn("Bad receiver for server key " + targetKey);
			} else {
				sendWorker.sendMsg(type, identity, seqNumber, mgg);
			}
		}
		if (LOG_ON && COMM.isInfoEnabled()) {
			COMM.info("Send message of type " + type + " to server of identity " + identity);
		}
	}

	/**
	 * sends a message to all the remote servers / neighbours connected to this
	 * server, except one. This is a utility method for implementing distributed
	 * algorithms in the servers' state machine: use this method when this
	 * server needs sending messages to all its neighbours, except one.
	 * 
	 * @param exceptKey
	 *            the selection key of the server to exclude in the forwarding.
	 * @param type
	 *            message's type.
	 * @param identity
	 *            sender's identity, that is the identity of this server.
	 * @param seqNumber
	 *            message's sequence number.
	 * @param s
	 *            message as a serializable object.
	 * @throws IOException
	 *             the communication exception thrown when sending the message.
	 */
	public void sendToAllServersExceptOne(final SelectionKey exceptKey, final int type, final int identity,
			final int seqNumber, final Serializable s) throws IOException {
		synchronized (state) {
			state.seqNumber++;
			forwardServers(exceptKey, type, identity, seqNumber, s);
		}
	}

	/**
	 * forwards a message to all the clients and the servers, except the entity
	 * (client or server) from which the message has just been received.
	 * 
	 * @param exceptKey
	 *            selection key to exclude from the set of target connections,
	 *            e.g., selection key of the entity from which the message has
	 *            been received.
	 * @param type
	 *            message's type.
	 * @param identity
	 *            sender's identity.
	 * @param seqNumber
	 *            message's sequence number.
	 * @param msg
	 *            message as a serializable object.
	 * @throws IOException
	 *             the communication exception thrown when sending the message.
	 */
	void forward(final SelectionKey exceptKey, final int type, final int identity, final int seqNumber,
			final Serializable msg) throws IOException {
		forwardServers(exceptKey, type, identity, seqNumber, msg);
		forwardClients(exceptKey, type, identity, seqNumber, msg);
	}

	/**
	 * forwards a message to all the servers, except the server from which the
	 * message has just been received.
	 * 
	 * @param exceptKey
	 *            selection key to exclude from the set of target connections,
	 *            e.g., selection key of the entity from which the message has
	 *            been received.
	 * @param type
	 *            message's type.
	 * @param identity
	 *            sender's identity.
	 * @param seqNumber
	 *            message's sequence number.
	 * @param msg
	 *            message as a serializable object.
	 * @throws IOException
	 *             the communication exception thrown when sending the message.
	 */
	private void forwardServers(final SelectionKey exceptKey, final int type, final int identity, final int seqNumber,
			final Serializable msg) throws IOException {
		int nbServers = 0;
		synchronized (state) {
			for (SelectionKey target : state.allServerWorkers.keySet()) {
				if (target == exceptKey) {
					if (LOG_ON && COMM.isDebugEnabled()) {
						COMM.debug("do not send to a server " + "because (target == exceptKey)");
					}
					continue;
				}
				FullDuplexMsgWorker worker = state.allServerWorkers.get(target);
				if (worker == null) {
					COMM.warn("Bad worker for server key " + target);
				} else {
					worker.sendMsg(type, identity, seqNumber, msg);
					nbServers++;
				}
			}
		}
		if (LOG_ON && COMM.isInfoEnabled()) {
			COMM.info("Send message to " + nbServers + " server end points");
		}
	}

	/**
	 * forwards a message to all the clients, except the client from which the
	 * message has just been received.
	 * 
	 * @param exceptKey
	 *            selection key to exclude from the set of target connections,
	 *            e.g., selection key of the entity from which the message has
	 *            been received.
	 * @param type
	 *            message's type.
	 * @param identity
	 *            sender's identity.
	 * @param seqNumber
	 *            message's sequence number.
	 * @param msg
	 *            message as an serializable object.
	 * @throws IOException
	 *             the communication exception thrown when sending the message.
	 */
	private void forwardClients(final SelectionKey exceptKey, final int type, final int identity, final int seqNumber,
			final Serializable msg) throws IOException {
		int nbClients = 0;
		synchronized (state) {
			for (SelectionKey target : state.allClientWorkers.keySet()) {
				if (target == exceptKey) {
					if (LOG_ON && COMM.isDebugEnabled()) {
						COMM.debug("do not send to a client " + "because (target == exceptKey)");
					}
					continue;
				}
				FullDuplexMsgWorker clientWorker = state.allClientWorkers.get(target);
				if (clientWorker == null) {
					COMM.warn("Bad receiver for key " + target);
				} else {
					clientWorker.sendMsg(type, identity, seqNumber, msg);
					nbClients++;
				}
			}
		}
		if (LOG_ON && COMM.isInfoEnabled()) {
			COMM.info("Send message to " + nbClients + " client end points");
		}
	}
}
