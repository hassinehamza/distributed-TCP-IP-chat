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
 * This class defines the content of a leader message of the election algorithm.
 * @author Denis Conan, Hamza Hassine, Majdi Haouech
 *
 */
public class ElectionLeaderContent extends AbstractContent {
  /**
   * version number for serialization.
   */
  private static final long serialVersionUID = 1L;
  /**
   * sender sends the leader message.
   * initiator is the leader.
   */
  private int sender, initiator;

  /**
   * constructs the content of a leader election message.
   * @param
   *    sender
   * @param
   *    initiator
   */
  public ElectionLeaderContent(int sender, int init) {
    this.sender = sender;
    this.initiator = init;
  }
  /**
   * sender getter.
   * @return sender
   */
  public int getSender() {
    return sender;
  }

  /**
   * sender setter.
   * @param sender Integer
   */
  public void setSender(final int sender) {
    this.sender = sender;
  }

  /**
   * initiator getter.
   * @return initiator
   */
  public int getInitiator() {
    return initiator;
  }

  /**
   * initiator sender.
   * @param initiator Integer
   */
  public void setInitiator(final int initiator) {
    this.initiator = initiator;
  }
}