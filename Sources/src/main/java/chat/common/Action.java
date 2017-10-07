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

import java.util.ArrayList;
import java.util.List;

/**
 * This interface defines the interface of the actions of the algorithms of the client or the
 * server. An action has an identifier and is obtained with the method {@link identifier}. The
 * identifiers are computed in the enumerations {@link chat.client.algorithms.ListOfAlgorithms} and
 * {@link chat.server.algorithms.ListOfAlgorithms}. The second method ({@link execute}) is called
 * for executing the action. The context of the call are the state of the entity (client or server)
 * and the message that has just been received.
 *
 * @param <S>
 *          the type of the state on which the action is executed.
 *
 * @author Denis Conan
 *
 */
public interface Action<S extends AbstractState> {
  /**
   * gets the identifier (integer) of the action, which can be attached to a message dispatcher.
   * 
   * @return the identifier of the action.
   */
  int identifier();

  /**
   * index of the first message type of the first algorithm of the server.
   */
  int OFFSET_SERVER_ALGORITHMS = 0;

  /**
   * index of the first message type of the first algorithm of the client.
   */
  int OFFSET_CLIENT_ALGORITHMS = 1000;

  /**
   * gets the type of the content/message to be treated.
   *
   * @return the type of the content/message.
   */
  Class<? extends AbstractContent> contentClass();

  /**
   * executes the algorithmic part corresponding to this action.
   *
   * @param state
   *          the state of the process.
   * @param msg
   *          the message in treatment.
   */
  void execute(S state, AbstractContent msg);

  /**
   * executes the action due to the receipt of the message {@code msg} or intercepts the call of the
   * action for instance to eventually re-schedule the receipt of the message so that some
   * non-determinism is introduced. The behavior is controlled by the boolean value
   * {@link Interceptor#isInterceptionEnabled()}.
   *
   * @param state
   *          the current state of the entity that receives the message.
   * @param msg
   *          the message to treat.
   */
  default void executeOrIntercept(final S state, final AbstractContent msg) {
    List<AbstractContent> set = new ArrayList<>();
    if (Interceptor.isInterceptionEnabled()) {
      set = Interceptor.intercept(state, msg);
    } else {
      set.add(msg);
    }
    for (AbstractContent m : set) {
      execute(state, m);
    }
  }
}
