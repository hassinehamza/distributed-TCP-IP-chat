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

Initial developer(s): Christian Bac, Denis Conan
Contributor(s):
 */
package chat.common;

/**
 * This Enumeration type declares the status of the reception of a message.
 * 
 * @author chris
 * @author Denis Conan
 * 
 */
public enum ReadMessageStatus {
	/**
	 * read has not started, yet.
	 */
	ReadUnstarted,
	/**
	 * reading the header.
	 */
	ReadHeaderStarted,
	/**
	 * the reading of the header has been completed. The header is available for
	 * analysis.
	 */
	ReadHeaderCompleted,
	/**
	 * reading the body of the message.
	 */
	ReadDataStarted,
	/**
	 * the reading of the body has been completed. The message is entirely
	 * available.
	 */
	ReadDataCompleted,
	/**
	 * the channel has been closed while reading.
	 */
	ChannelClosed;
}
