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
 * This abstract class defines the type for a state of a client or a server. The
 * state is the first part of the context for the execution of actions (of
 * algorithms): see the method
 * {@link chat.common.Action#execute(AbstractState, AbstractContent)}.
 *
 * @author Denis Conan
 * @author Hamza Hassine
 * @author Majdi Haouech
 *
 */
public abstract class AbstractState {
}
