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

import static chat.common.Log.GEN;
import static chat.common.Log.LOG_ON;

import chat.common.AbstractContent;

/**
 * This class defines the content of a chat message.
 * 
 * @author Denis Conan
 *
 */
public class ChatMessageContent extends AbstractContent {
	/**
	 * version number for serialization.
	 */
	private static final long serialVersionUID = 2L;
	/**
	 * the sender of the message.
	 */
	private int sender;
	/**
	 * the content of the message.
	 */
	private String content;

	/**
	 * constructs the message.
	 * 
	 * @param idSender
	 *            the identifier of the sender.
	 * @param content
	 *            the content of the message.
	 */
	public ChatMessageContent(final int idSender, final String content) {
		if (idSender < 0) {
			throw new IllegalArgumentException(
					"invalid id for the sender(" + idSender + ")");
		}
		if (content == null) {
			throw new IllegalArgumentException(
					"invalid content (null)");
		}
		sender = idSender;
		this.content = content;
		assert invariant();
	}

	/**
	 * checks the invariant of the class.
	 * 
	 * NB: the method is final so that the method is not overriden in potential
	 * subclasses because it is called in the constructor.
	 * 
	 * @return the boolean stating the invariant is maintained.
	 */
	public final boolean invariant() {
		return sender >= 0 && content != null;
	}

	/**
	 * gets the identifier of the sender.
	 * 
	 * @return the identifer as an {@code int}.
	 */
	public int getSender() {
		return sender;
	}

	/**
	 * the content of the message.
	 * 
	 * @return the content of the message as a string.
	 */
	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		if (LOG_ON && GEN.isInfoEnabled()) {
			return "sender / content = " + sender + " / " + content;
		} else {
			return content;
		}
	}
}
