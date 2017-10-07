
/**
This file is part of the muDEBS middleware.

Copyright (C) 2012-2017 Télécom SudParis

This is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This software platform is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the muDEBS platform. If not, see <http://www.gnu.org/licenses/ >.

Initial developer(s): Denis Conan
Contributor(s):
*/
package chat.common;

import java.util.ArrayList;
import java.util.List;
import chat.client.algorithms.chat.ChatMessageContent;
import chat.server.algorithms.election.ElectionTokenContent;

/**
 * This class contains the interception of the calls to the actions to receive messages in the
 * client. The behaviour is controlled by the boolean constant {@link interceptionEnabled}. When
 * set, the default method
 * {@link chat.common.Action#executeOrIntercept(AbstractState, AbstractContent)} redirects the
 * receipt of the message to the method {@link #intercept(AbstractState, AbstractContent)}.
 *
 * @author Denis Conan
 */
public final class Interceptor {

  /**
   * states whether some non-determinism is introduced to test distributed algorithms. This is done
   * by rerouting in the default method
   * {@link Action#executeOrIntercept(AbstractState, AbstractContent)}.
   */
  private static boolean interceptionEnabled = false;

  /**
   * number of milliseconds for delaying messages.
   */
  @SuppressWarnings("unused")
  private static final int DELAY = 500;

  /**
   * private constructor to avoid instantiation.
   */
  private Interceptor() {
  }

  /**
   * gets the boolean value of the attribute {@link #interceptionEnabled}.
   *
   * @return the boolean value.
   */
  public static boolean isInterceptionEnabled() {
    return interceptionEnabled;
  }

  /**
   * sets the boolean value of the attribute {@link #interceptionEnabled}.
   *
   * @param interceptionEnabled
   *
   *          the new boolean value.
   */
  public static void setInterceptionEnabled(final boolean interceptionEnabled) {
    Interceptor.interceptionEnabled = interceptionEnabled;
  }

  /**
   * intercepts the receipt of a message. This is where is introduced some non-determinism for
   * integration testing of the distributed algorithms. This method is called by the default method
   * {@link chat.common.Action#executeOrIntercept(AbstractState, AbstractContent)} when the
   * interception mechanism is activated, that is {@link #isInterceptionEnabled} is {@code true}.
   *
   * @param state
   * 
   *          the state of the receiver.
   * @param msg
   * 
   *          the message to schedule.
   * @return the set of messages to treat now.
   */
  public static List<AbstractContent> intercept(final AbstractState state,
      final AbstractContent msg) {
    ArrayList<AbstractContent> set = new ArrayList<>();
    if (msg instanceof ElectionTokenContent) {
      ElectionTokenContent content = (ElectionTokenContent) msg;
      chat.server.State st = (chat.server.State) state;
      if (content.getInitiator() == content.getSender()) {
        new Thread(
            new TreatDelayedMessage<chat.server.State, ElectionTokenContent>(st,
                content, st.currKey))
                .start();
      } else {
        set.add(msg);
      }
    } else if (msg instanceof ChatMessageContent) {
      ChatMessageContent content = (ChatMessageContent) msg;
      chat.client.State st = (chat.client.State) state;
      if (content.getSender() == 0 && st.identity == 2) {
        new Thread(
            new TreatDelayedMessage<chat.client.State, ChatMessageContent>(st, content, null))
            .start();
      } else {
        set.add(msg);
      }
    } else {
      set.add(msg);
    }

    return set;
  }

}
