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

import java.util.Optional;

import chat.client.State;
import chat.common.AbstractContent;
import chat.common.AbstractState;
import chat.server.Server;

/**
 * This class defines the methods implementing the reaction of the state machine
 * part concerning the reception of chat messages. Since only one message
 * content type is declared in the algorithm, there is only one static method in
 * this class.
 * 
 * @author Denis Conan
 * 
 */
public final class Actions {

	/**
	 * avoids the creation of instances.
	 */
	private Actions() {
	}

	/**
	 * treats the reception of a chat message: the message is displayed in the
	 * console.
	 * 
	 * @param state
	 *            the state of the client.
	 * @param content
	 *            the content of the message.
	 */
	public static void receiveChatMessageContent(final AbstractState state,
			final AbstractContent content) {
		ChatMessageContent msg = (ChatMessageContent) Optional
				.ofNullable(content)
				.filter(c -> c instanceof ChatMessageContent)
				.orElseThrow(IllegalArgumentException::new);
		State cstate = (State) Optional.ofNullable(state)
				.filter(s -> s instanceof State)
				.orElseThrow(IllegalArgumentException::new);
		synchronized (cstate) {
			cstate.nbChatMessageContentReceived++;
			System.out.println("client "
					+ cstate.identity % Server.OFFSET_ID_CLIENT + " of server "
					+ cstate.identity / Server.OFFSET_ID_CLIENT + " receives "
					+ msg);
		}
	}
}
