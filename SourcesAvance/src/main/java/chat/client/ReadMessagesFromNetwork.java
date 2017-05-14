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

Initial developer(s): Denis Conan, Christian Bac
Contributor(s):
 */
package chat.client;

import static chat.common.Log.COMM;
import static chat.common.Log.GEN;
import static chat.common.Log.LOG_ON;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Optional;

import chat.client.algorithms.ListOfAlgorithms;
import chat.common.FullDuplexMsgWorker;
import chat.common.ReadMessageStatus;

/**
 * This class contains the chat client's thread waiting for messages from its
 * server. The constructor initialises the full message worker and the method
 * {@code run} receives messages from the server and dispatch them according to
 * the message type using the method {@code execute} of the class
 * {@link chat.client.algorithms.ListOfAlgorithms}
 * 
 * @author chris
 * @author Denis Conan
 * 
 */
public class ReadMessagesFromNetwork extends FullDuplexMsgWorker
		implements Runnable {
	/**
	 * state of the client. This is where all the attributes of the chat client
	 * are stored.
	 */
	private State state;

	/**
	 * constructs the thread of a client that is responsible for the reception
	 * of messages from the chat server. This thread is then a full duplex
	 * message worker. After the construction of the full message worker, the
	 * constructor receive its first message from the chat server that contains
	 * the identity of the server.
	 * 
	 * @param chan
	 *            the socket channel connecting the client to the server.
	 * @param state
	 *            the client state ojbect.
	 */
	public ReadMessagesFromNetwork(final SocketChannel chan,
			final State state) {
		super(chan);
		this.state = Optional.ofNullable(state)
				.orElseThrow(IllegalArgumentException::new);
		ReadMessageStatus msgState;
		do {
			msgState = readMessage();
		} while (msgState != ReadMessageStatus.ReadDataCompleted);
		try {
			Integer idFromServer = (Integer) getData();
			state.identity = idFromServer.intValue();
		} catch (IOException e) {
			throw new IllegalStateException(
					"communication problem while getting"
							+ " the identity of the chat server");
		}
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
		return state != null;
	}

	/**
	 * organises an infinite loop to receive messages from the chat server and
	 * to execute the corresponding action. The action is searched for in the
	 * enumeration {@link chat.client.algorithms.ListOfAlgorithms} through the
	 * method
	 * {@link chat.client.algorithms.ListOfAlgorithms#execute(State, int, Object)}.
	 */
	@Override
	public void run() {
		if (LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("Client thread for rcving msgs from the network started");
		}
		ReadMessageStatus messState;
		while (!Thread.interrupted()) {
			try {
				messState = readMessage();
				if (messState == ReadMessageStatus.ChannelClosed) {
					break;
				} else {
					if (messState == ReadMessageStatus.ReadDataCompleted) {
						Object content = getData();
						ListOfAlgorithms.execute(state, getInType(), content);
					}
				}
			} catch (IOException e) {
				COMM.warn(e.getLocalizedMessage());
				e.printStackTrace();
				return;
			}
			if (LOG_ON && COMM.isTraceEnabled()) {
				COMM.trace("End of reception of a message");
			}
		}
	}
}
