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
package chat.common;

/**
 * This class contains the interception of the calls to the actions to receive
 * messages in the client. The behaviour is controlled by the boolean constant
 * {@link chat.common.Action#INTERCEPTION_ON}. When set, the default method
 * {@link chat.common.Action#executeOrIntercept(AbstractState, AbstractContent)}
 * redirects the receipt of the message to the method
 * {@link #intercept(AbstractState, AbstractContent)}.
 * 
 * @author Denis Conan
 */
public final class Interceptor {
	/**
	 * private constructor to avoid instantiation.
	 */
	private Interceptor() {
	}

	/**
	 * intercepts the receipt of a message. This is where is introduced some
	 * non-determinism for integration testing of the distributed algorithms.
	 * This method is called by the default method
	 * {@link chat.common.Action#executeOrIntercept(AbstractState, AbstractContent)}
	 * when the interception mechanism is activated, that is
	 * {@link chat.common.Action#INTERCEPTION_ON} is {@code true}.
	 * 
	 * TODO this is where to program for instance non-FIFO communication,
	 * message loss, etc.
	 * 
	 * @param state
	 *            the state of the receiver.
	 * @param msg
	 *            the message to schedule.
	 */
	public static void intercept(final AbstractState state,
			final AbstractContent msg) {

	}
}
