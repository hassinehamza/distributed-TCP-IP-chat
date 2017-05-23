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

import static chat.common.Log.CHAT;
import static chat.common.Log.LOG_ON;

import org.apache.log4j.Level;

import chat.client.State;
import chat.common.AbstractContent;
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
	public static void receiveChatMessageContent(final State state,
			final AbstractContent content) {
		if (content == null) {
			throw new IllegalArgumentException(
					"Try executing action with null content");
		}
		if (state == null) {
			throw new IllegalArgumentException(
					"Try executing action with null state");
		}
		if (content instanceof ChatMessageContent) {
			ChatMessageContent mymsg = (ChatMessageContent) content;
			synchronized (state) {
				state.nbChatMessageContentReceived++;
				System.out.println(
						"client " + state.identity % Server.OFFSET_ID_CLIENT
								+ " of server "
								+ state.identity / Server.OFFSET_ID_CLIENT
								+ " receives " + mymsg);
			}
		} else {
			if (LOG_ON && CHAT.isEnabledFor(Level.ERROR)) {
				CHAT.error(
						"Error when executing action: not right message type ("
								+ content.getClass().getName() + ")");
			}
			throw new IllegalArgumentException(
					"Error when executing action: not right message type ("
							+ content.getClass().getName() + ")");
		}
	}
}
