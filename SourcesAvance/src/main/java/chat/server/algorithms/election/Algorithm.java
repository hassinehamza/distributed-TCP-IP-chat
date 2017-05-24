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
package chat.server.algorithms.election;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import chat.common.AbstractContent;
import chat.common.Action;
import chat.server.State;

/**
 * This Enumeration type declares the algorithm of the election part of the
 * server's state machine. Only one message content type can be received.
 * 
 * TODO rename or add message types when necessary, and remove this comment.
 * 
 * @author Denis Conan
 * 
 */
public enum Algorithm implements Action<State> {
	/**
	 * the enumerator for the action of the token message of the election
	 * algorithm.
	 */
	TOKEN_MESSAGE(ElectionTokenContent.class) {
		/**
		 * executes the action by calling a static method.
		 * 
		 * @param state
		 *            the state of the client.
		 * @param content
		 *            the message to treat.
		 */
		public void execute(final State state, final AbstractContent content) {
			Actions.receiveTokenContent(state, (ElectionTokenContent) content);
		}
	},
	/**
	 * the enumerator for the action of the leader message of the election
	 * algorithm.
	 */
	LEADER_MESSAGE(ElectionLeaderContent.class) {
		/**
		 * executes the action by calling a static method.
		 * 
		 * @param state
		 *            the state of the client.
		 * @param content
		 *            the message to treat.
		 */
		public void execute(final State state, final AbstractContent content) {
			Actions.receiveLeaderContent(state,
					(ElectionLeaderContent) content);
		}
	};

	/**
	 * collection of the actions of this algorithm enumerator of the server. The
	 * collection is built at class loading by adding all the enumerators, which
	 * are actions of the algorithms (subtypes of {@link chat.common.Action});
	 * it is thus {@code static}. The collection is unmodifiable and the
	 * attribute is {@code final} so that no other collection can be substituted
	 * after being statically assigned. Since it is immutable, the attribute can
	 * be {@code public}.
	 */
	public static final Map<Integer, Algorithm> ACTIONS;
	/**
	 * index of the action of this message type.
	 */
	private final int actionIndex;

	/**
	 * the type of the content.
	 */
	private final Class<? extends AbstractContent> contentClass;

	/**
	 * static block to build collections of actions.
	 */
	static {
		Map<Integer, Algorithm> privateMapOfActions = new HashMap<>();
		Arrays.asList(Algorithm.values()).stream().forEach(
				algo -> privateMapOfActions.put(algo.actionIndex, algo));
		ACTIONS = Collections.unmodifiableMap(privateMapOfActions);
	}

	/**
	 * is the constructor of message type object.
	 * 
	 * @param contentClass
	 *            the type of the content.
	 */
	Algorithm(final Class<? extends AbstractContent> contentClass) {
		this.actionIndex = chat.common.Action.OFFSET_SERVER_ALGORITHMS
				+ chat.server.algorithms.ListOfAlgorithms.OFFSET_ELECTION_ALGORITHM
				+ ordinal();
		this.contentClass = contentClass;
	}

	/**
	 * obtains the index of this message type.
	 * 
	 * @return the identifier of the action as an {@code int}.
	 */
	public int identifier() {
		return actionIndex;
	}

	/**
	 * gets the type of the content.
	 * 
	 * @return the type of the content.
	 */
	public Class<? extends AbstractContent> contentClass() {
		return contentClass;
	}

	@Override
	public String toString() {
		return String.valueOf(actionIndex);
	}
}
