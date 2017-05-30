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
package chat.client;

import static chat.common.Log.COMM;
import static chat.common.Log.GEN;
import static chat.common.Log.LOG_ON;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import chat.client.algorithms.chat.Algorithm;
import chat.client.algorithms.chat.ChatMessageContent;

/**
 * This class contains the logic of a client of the chat application. It
 * configures the client, connects to a chat server, launches a thread for
 * reading chat messages from the chat server.
 * 
 * @author Denis Conan
 * 
 */
public class Client {
	/**
	 * the state of the client, that is all the attributes that are available
	 * for managing the chat client. This attribute is {@code final} because it
	 * is used to synchronise code blocks.
	 */
	private final State state;

	/**
	 * the runnable object of the client that receives the messages from the
	 * chat server.
	 */
	private final ReadMessagesFromNetwork runnableToRcvMsgs;

	/**
	 * the thread of the client that receives the messages from the chat server.
	 */
	private final Thread threadToRcvMsgs;

	/**
	 * gets the state of the client.
	 * 
	 * @return the reference to the state.
	 */
	public State getState() {
		return state;
	}

	/**
	 * constructs a client with a connection to the chat server. The connection
	 * to the server is managed in a thread that is also a full message worker.
	 * Before creating the threaded full duplex message worker, the constructor
	 * check for the server host name and open a connection with the chat
	 * server.
	 * 
	 * NB: after the construction of a client object, the thread for reading
	 * messages must be started using the method
	 * {@link startThreadReadMessagesFromNetwork}.
	 * 
	 * @param serverHostName
	 *            the name of the host of the server.
	 * @param serverPortNb
	 *            the port number of the accepting socket of the server.
	 */
	public Client(final String serverHostName, final int serverPortNb) {
		state = new State();
		SocketChannel rwChan = null;
		InetAddress destAddr = null;
		try {
			destAddr = InetAddress.getByName(serverHostName);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("unknown host name provided");
		}
		try {
			rwChan = SocketChannel.open();
		} catch (IOException e) {
			throw new IllegalStateException(
					"cannot open a connection to the server");
		}
		try {
			rwChan.connect(new InetSocketAddress(destAddr, serverPortNb));
		} catch (IOException e) {
			throw new IllegalStateException(
					"cannot open a connection to the server");
		}
		runnableToRcvMsgs = new ReadMessagesFromNetwork(rwChan, state);
		threadToRcvMsgs = new Thread(runnableToRcvMsgs);
		assert invariant();
	}

	/**
	 * checks the invariant of the class.
	 * 
	 * NB: the method is final so that the method is not overriden in potential
	 * subclasses because it is called in the constructor.
	 * 
	 * @return a boolean stating whether the invariant is maintained.
	 */
	public final boolean invariant() {
		return state != null && runnableToRcvMsgs != null
				&& threadToRcvMsgs != null;
	}

	/**
	 * starts the thread that is responible for reading messages from the
	 * server.
	 */
	public void startThreadReadMessagesFromNetwork() {
		threadToRcvMsgs.start();
	}

	/**
	 * treats an input line from the console. For now, it sends the input line
	 * as a chat message to the server.
	 * 
	 * @param line
	 *            the content of the message.
	 */
	public void treatConsoleInput(final String line) {
		String input = Optional.ofNullable(line)
				.orElseThrow(IllegalArgumentException::new);
		if (LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("new command line on console: " + input);
		}
		if (line.equals("quit")) {
			threadToRcvMsgs.interrupt();
			Thread.currentThread().interrupt();
		} else {
			synchronized (state) {
				ChatMessageContent msg = new ChatMessageContent(state.identity,
						input);
				if (LOG_ON && COMM.isTraceEnabled()) {
					COMM.trace("sending chat message: " + msg);
				}
				// The sequence number is irrelevant (assigned to 0) for client
				// messages sent to the server, but will be assigned by the
				// server to control the propagation of client messages.
				// Ideally, there should exist a separate message type for chat
				// messages from clients to their server and this new message
				// type will not contain the sequence number.
				try {
					long sent = runnableToRcvMsgs.sendMsg(
							Algorithm.CHAT_MESSAGE.identifier(), state.identity,
							0, msg);
					state.nbChatMessageContentSent++;
					if (LOG_ON && COMM.isDebugEnabled()) {
						COMM.debug(sent + " bytes sent.");
					}
				} catch (IOException e) {
					COMM.warn(e.getLocalizedMessage());
					e.printStackTrace();
					return;
				}
			}
		}
	}
}
