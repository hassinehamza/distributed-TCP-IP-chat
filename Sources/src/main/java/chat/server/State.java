// CHECKSTYLE:OFF
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

import java.nio.channels.SelectionKey;
import java.util.HashMap;
import java.util.Map;

import chat.common.AbstractState;
import chat.common.FullDuplexMsgWorker;

/**
 * This class defines the state of the server.
 * 
 * @author Denis Conan
 */
public class State extends AbstractState {
	/**
	 * selection keys of the server message workers.
	 */
	public Map<SelectionKey, FullDuplexMsgWorker> allServerWorkers;
	/**
	 * selection keys of the client message workers.
	 */
	public Map<SelectionKey, FullDuplexMsgWorker> allClientWorkers;
	/**
	 * selection key of the connection from which the last message was received.
	 */
	public SelectionKey currKey;
	/**
	 * identity of this server.
	 */
	private int identity;
	
	public static int caw, parent, rec, lrec, win;
	public static String status;
	/**
	 * one counter per client in order to control the propagation of client
	 * messages: stop forward to remote servers when the message has already
	 * been forwarded ; the counter is set by the server receiving the message
	 * from the client.
	 */
	public Map<Integer, Integer> clientSeqNumbers;
	/**
	 * seqNumber is equal to the maximum of counters of clientSeqNumbers set.
	 */
	public int seqNumber;

	/**
	 * initialises the collection attributes.
	 * 
	 * @param identity
	 *            the identity of this server.
	 */
	public State(final int identity) {
		this.identity = identity;
		allServerWorkers = new HashMap<>();
		allClientWorkers = new HashMap<>();
		clientSeqNumbers = new HashMap<>();
		assert invariant();
	}

	/**
	 * checks the invariant of the class.
	 * 
	 * @return a boolean stating whether the invariant is maintained.
	 */
	public boolean invariant() {
		return allServerWorkers != null && allClientWorkers != null
				&& clientSeqNumbers != null;
	}

	/**
	 * gets the identity of the server.
	 * 
	 * @return the identity of the server.
	 */
	public int getIdentity() {
		return identity;
	}
}
