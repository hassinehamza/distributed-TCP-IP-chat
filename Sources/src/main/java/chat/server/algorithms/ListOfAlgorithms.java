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
package chat.server.algorithms;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import chat.common.AbstractContent;
import chat.common.Action;
import chat.server.State;

/**
 * This Enumeration type declares the algorithms of the server. For now, there
 * is only one algorithm: the algorithm for the election.
 * 
 * TODO add new algorithms when necessary, update the description of the
 * enumeration, and remove this comment.
 * 
 * @author Denis Conan
 */
public enum ListOfAlgorithms {
	/**
	 * the election algorithm.
	 */
	ALGORITHM_ELECTION(chat.server.algorithms.election.Algorithm.ACTIONS);

	/**
	 * collection of the actions of this algorithm enumerator of the server. The
	 * collection is built at class loading by parsing the collections of
	 * actions of the algorithms; it is thus {@code static}. The collection is
	 * unmodifiable and the attribute is {@code final} so that no other
	 * collection can be substituted after being statically assigned.
	 */
	private final Map<Integer, ? extends Action> mapOfActions;

	/**
	 * index of the first message type of the election algorithm.
	 */
	public static final int OFFSET_ELECTION_ALGORITHM = 0;

	/**
	 * is the constructor of this algorithm object.
	 * 
	 * @param map
	 *            collection of actions of this algorithm.
	 */
	ListOfAlgorithms(final Map<Integer, ? extends Action> map) {
		mapOfActions = Collections.unmodifiableMap(map);
	}

	/**
	 * searches for the action to execute in the collection of actions of the
	 * algorithm of the server.
	 * 
	 * @param state
	 *            state of the server.
	 * @param actionIndex
	 *            index of the action to execute.
	 * @param content
	 *            content of the message just received.
	 */
	public static void execute(final State state, final int actionIndex,
			final Object content) {
		boolean executed = false;
		for (ListOfAlgorithms algorithm : Arrays
				.asList(ListOfAlgorithms.values())) {
			for (Iterator<? extends Action> actions = algorithm.mapOfActions
					.values().iterator(); actions.hasNext();) {
				Action action = actions.next();
				if (action.identifier() == actionIndex) {
					executed = true;
					AbstractContent c;
					if (content instanceof AbstractContent) {
						c = (AbstractContent) content;
					} else {
						throw new IllegalArgumentException(
								"The content is not of type AbstractContent: "
										+ content.getClass().getName());
					}
					action.execute(state, c);
				}
			}
		}
		synchronized (state) {
			state.currKey = null;
		}
		if (!executed) {
			throw new IllegalArgumentException(
					"Unknown action: " + actionIndex);
		}
	}
}
