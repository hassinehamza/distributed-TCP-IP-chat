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

import chat.common.AbstractContent;
import chat.common.AbstractState;

/**
 * This class defines the methods implementing the reaction of the state machine
 * part concerning the reception of election messages. Since only one message
 * content type is declared in the algorithm, there is only one static method in
 * this class.
 * 
 * @author Denis Conan
 */
public final class Actions {

	/**
	 * avoids the creation of instances.
	 */
	private Actions() {
	}

	/**
	 * treats a token message of the election algorithm.
	 * 
	 * @param state
	 *            the state of the server.
	 * @param content
	 *            the content of the message to treat.
	 */
	public static void receiveTokenContent(final AbstractState state,
			final AbstractContent content) {
		// TODO to write. Don't forget to use the synchronized statement for
		// protecting the accesses to state attributes. Please remove this
		// comment when the method is implemented!
	}

	/**
	 * treats a leader message of the election algorithm.
	 * 
	 * @param state
	 *            the state of the server.
	 * @param content
	 *            the content of the message to treat.
	 */
	public static void receiveLeaderContent(final AbstractState state,
			final AbstractContent content) {
		// TODO to write. Don't forget to use the synchronized statement for
		// protecting the accesses to state attributes. Please remove this
		// comment when the method is implemented!
	}
}
