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

/**
 * This class defines the content of a token message of the election algorithm.
 * 
 * TODO add attributes. done
 * 
 * TODO implement the constructor. done
 * 
 * TODO add getters. done
 * 
 * TODO add the toString method.
 * 
 * TODO add your names in the list of authors. done
 * 
 * @author Denis Conan, Hamza Hassine, Majdi Haouech
 *
 */
public class ElectionTokenContent extends AbstractContent {
	/**
	 * version number for serialization.
	 */
	private static final long serialVersionUID = 1L;
	private int sender, initiator;

	/**
	 * constructs the content of a leader election message.
	 */
	public ElectionTokenContent(int sender, int initiator) {
		this.initiator = initiator;
		this.sender = sender;

	}

	public int getSender() {
		return sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}

	public int getInitiator() {
		return initiator;
	}

	public void setInitiator(int initiator) {
		this.initiator = initiator;
	}

	public String toString() {
		return "";
	}
}
