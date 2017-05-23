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
package chat.client.algorithms.chat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import chat.client.State;
import chat.common.AbstractContent;
import chat.common.Action;

/**
 * This Enumeration type declares the algorithm of the chat part of the client's
 * state machine. Only one message content type can be received.
 * 
 * @author Denis Conan
 * 
 */
public enum Algorithm implements Action<State> {
	/**
	 * the enumerator for the action of the chat message of the chat algorithm.
	 */
	CHAT_MESSAGE() {
		/**
		 * executes the action by calling a static method.
		 * 
		 * @param state
		 *            the state of the client.
		 * @param content
		 *            the message to treat.
		 */
		public void execute(final State state,
				final AbstractContent content) {
			Actions.receiveChatMessageContent(state, content);
		}
	};

	/**
	 * collection of the actions of this algorithm enumerator of the client. The
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
	 * static block to build the collection attributes when loading the
	 * enumeration in the VM. A modifiable list is built and transformed into an
	 * unmodifiable one.
	 */
	static {
		Map<Integer, Algorithm> privateMapOfActions = new HashMap<>();
		for (Algorithm aa : Algorithm.values()) {
			privateMapOfActions.put(aa.actionIndex, aa);
		}
		ACTIONS = Collections.unmodifiableMap(privateMapOfActions);
	}

	/**
	 * constructs an enumerator by assigning the {@link actionIndex}.
	 */
	Algorithm() {
		this.actionIndex = chat.common.Action.OFFSET_CLIENT_ALGORITHMS
				+ chat.client.algorithms.ListOfAlgorithms.OFFSET_CHAT_ALGORITHM
				+ ordinal();
	}

	/**
	 * obtains the index of this message type.
	 * 
	 * @return the identifier of the action as an {@code int}.
	 */
	public int identifier() {
		return actionIndex;
	}

	@Override
	public String toString() {
		return String.valueOf(actionIndex);
	}
}
