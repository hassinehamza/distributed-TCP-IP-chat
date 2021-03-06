
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

import java.nio.channels.SelectionKey;

import chat.client.algorithms.chat.ChatMessageContent;
import chat.server.algorithms.election.ElectionTokenContent;

/**
 * 
 * @author Denis Conan
 *
 * @param <S>
 * 
 *          the type of the state of the entity that has to receive the
 * 
 *          delayed message.
 * @param <C>
 * 
 *          the type of the content of the delayed message.
 */
public class TreatDelayedMessage<S extends AbstractState, C extends AbstractContent>
    implements Runnable {
  /**
   * the state of the entity that has to receive this delayed message.
   */
  private S state;
  /**
   * the content of the message.
   */
  private C content;
  /**
   * the selection from which the message should be received later.
   */
  private SelectionKey key;
  /**
   * the delay.
   */
  private static final long DELAY = 50;

  /**
   * the constructor.
   * 
   * @param state
   * 
   *          the state of the receiver.
   * @param content
   * 
   *          the content of the delayed message.
   * @param key
   * 
   *          the selection from which the message should be received later.
   */
  public TreatDelayedMessage(final S state, final C content,
      final SelectionKey key) {
    this.state = state;
    this.content = content;
    this.key = key;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(DELAY);
    } catch (InterruptedException e) {
      e.printStackTrace();
      return;
    }

    // Interceptor.setInterceptionEnabled(true);
    if (content instanceof ElectionTokenContent) {
      ((chat.server.State) state).currKey = key;
      chat.server.algorithms.election.Algorithm.TOKEN_MESSAGE
          .execute((chat.server.State) state, content);
    } else if (content instanceof ChatMessageContent) {
      chat.client.algorithms.chat.Algorithm.CHAT_MESSAGE
          .execute((chat.client.State) state, content);
    }

  }
}
