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
package chat.client.algorithms;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import chat.client.State;
import chat.common.AbstractContent;
import chat.common.Action;

/**
 * This Enumeration type declares the algorithms of the chat client. For now,
 * there is only one algorithm: the algorithm for exchanging chat messages.
 * 
 * TODO add new algorithms when necessary, update the description of the
 * enumeration, and remove this comment.
 * 
 * @author Denis Conan
 */
public enum ListOfAlgorithms {
	/**
	 * the chat algorithm.
	 */
	ALGORITHM_CHAT(chat.client.algorithms.chat.Algorithm.ACTIONS);
	/**
	 * collection of the actions of this algorithm enumerator of the client. The
	 * collection is built at class loading by parsing the collections of
	 * actions of the algorithms; it is thus {@code static}. The collection is
	 * unmodifiable and the attribute is {@code final} so that no other
	 * collection can be substituted after being statically assigned.
	 */
	private final Map<Integer, ? extends Action> mapOfActions;

	/**
	 * index of the first message type of this algorithm.
	 */
	public static final int OFFSET_CHAT_ALGORITHM = 0;

	/**
	 * constructs an enumerator by assigning the map of actions of this
	 * algorithm to the algorithm enumerator. See the enumerations for the
	 * algorithm: e.g. {@link chat.client.algorithms.chat.Algorithm}.
	 * 
	 * @param map
	 *            collection of actions of this algorithm.
	 */
	ListOfAlgorithms(final Map<Integer, ? extends Action> map) {
		mapOfActions = Collections.unmodifiableMap(map);
	}

	/**
	 * obtains the map of actions of the algorithm.
	 * 
	 * @return the map of actions.
	 */
	private Map<Integer, ? extends Action> getMapOfActions() {
		return mapOfActions;
	}

	/**
	 * searches for the action to execute in the collection of algorithms of the
	 * algorithm of the client, each algorithm having a collection of actions.
	 * 
	 * @param state
	 *            state of the client.
	 * @param actionIndex
	 *            index of the action to execute.
	 * @param content
	 *            content of the message just received.
	 */
	public static void execute(final State state, final int actionIndex,
			final Object content) {
		Arrays.asList(values()).stream().map(ListOfAlgorithms::getMapOfActions)
				.map(map -> map.get(actionIndex))
				.filter(action -> content instanceof AbstractContent)
				.forEach(action -> action.executeOrIntercept(state,
						(AbstractContent) content));
	}
}
