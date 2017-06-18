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
package chat.client;

import chat.common.AbstractState;
import chat.common.VectorClock;

import java.util.ArrayList;
import java.util.List;

import chat.client.algorithms.chat.ChatMessageContent;
/**
 * This class defines the attributes of the state of the client. Since the class
 * is only a data structure with no methods, all the attributes are set as
 * public, and as a consequence, special care must be taken to access these
 * attributes when multi-threading: The synchronized statement must be used in
 * this case.
 * 
 * @author Denis Conan
 * 
 */
public class State extends AbstractState {
	/**
	 * identity of this client. The identity is computed by the server as
	 * follows: {@code state.identity * OFFSET_ID_CLIENT + clientNumber}.
	 */
	public int identity;
	/**
	 * number of chat messages received.
	 */
	public int nbChatMessageContentReceived;
	/**
	 * number of chat messages sent.
	 */
	public int nbChatMessageContentSent;
	
	public VectorClock horloge = new VectorClock();  
	
	public List<ChatMessageContent> MsgBag = new ArrayList<ChatMessageContent>();
}
