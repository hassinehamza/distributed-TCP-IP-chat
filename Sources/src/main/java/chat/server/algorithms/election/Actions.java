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

import static chat.common.Log.ELECTION;
import static chat.common.Log.LOGGER_NAME_ELECTION;
import static chat.common.Log.LOG_ON;

import java.io.IOException;

import org.apache.log4j.Level;

import chat.common.Log;
import chat.server.State;

/**
 * This class defines the methods implementing the reaction of the state machine part concerning the
 * reception of election messages. Since only one message content type is declared in the algorithm,
 * there is only one static method in this class.
 *
 * @author Denis Conan , Hamza Hassine, Majdi Haouach
 */
public final class Actions {

  /**
   * Type of status messages.
   */
  private static final String LEADER = "leader";
  private static final String NON_LEADER = "non-leader";

  /**
   * type of message logged.
   */
  private static final String JE_SUIS_PERDANT = "je suis perdant";
  private static final String RECU_DE_TYPE_LEADER = "recu de type Leader";
  private static final String JE_SUIS_GAGNAT = "je suis gagnat";

  /**
   * avoids the creation of instances.
   */
  private Actions() {
  }

  static {
    // whatever code is needed for initialization goes here
    Log.configureALogger(LOGGER_NAME_ELECTION, Level.INFO);
  }

  /**
   * treats a token message of the election algorithm.
   *
   * @param state
   *          the state of the server.
   * @param content
   *          the content of the message to treat.
   */
  public static void receiveTokenContent(final State state, final ElectionTokenContent content) {

    synchronized (state) {
      if (LOG_ON && ELECTION.isInfoEnabled()) {
        ELECTION.info("recu de type token");
      }
      if (state.getCaw() == -1 || content.getInitiator() < state.getCaw()) {
        state.setCaw(content.getInitiator());
        state.setRec(0);
        state.setParent(content.getSender());
        state.setElectionParentKey(state.currKey);
        try {
          state.getServer().sendToAllServersExceptOne(state.getElectionParentKey(),
              Algorithm.TOKEN_MESSAGE.identifier(), state.getIdentity(), state.seqNumber,
              new ElectionTokenContent(state.getIdentity(), content.getInitiator()));
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
      if (state.getCaw() == content.getInitiator()) {
        state.setRec(state.getRec() + 1);
        if (state.getRec() == state.allServerWorkers.size()) {
          if (state.getCaw() == state.getIdentity()) {
            try {
              state.getServer().sendToAllServers(Algorithm.LEADER_MESSAGE.identifier(),
                  state.getIdentity(), state.seqNumber,
                  new ElectionLeaderContent(state.getIdentity(), state.getIdentity()));
            } catch (IOException e) {
              e.printStackTrace();
            }

          } else {
            try {
              state.getServer().sendToAServer(state.getElectionParentKey(),
                  Algorithm.TOKEN_MESSAGE.identifier(), state.getIdentity(), state.seqNumber,
                  new ElectionTokenContent(state.getIdentity(), content.getInitiator()));
            } catch (IOException e) {

              e.printStackTrace();
            }
          }
        }
      }
    }
  }

  /**
   * treats a leader message of the election algorithm.
   *
   * @param state
   *          the state of the server.
   * @param content
   *          the content of the message to treat.
   */
  public static void receiveLeaderContent(final State state, final ElectionLeaderContent content) {

    synchronized (state) {
      if (LOG_ON && ELECTION.isInfoEnabled()) {
        ELECTION.info(RECU_DE_TYPE_LEADER);
      }

      if (state.getLrec() == 0 && state.getIdentity() != content.getInitiator()) {
        try {
          state.getServer().sendToAllServers(Algorithm.LEADER_MESSAGE.identifier(),
              state.getIdentity(), state.seqNumber,
              new ElectionLeaderContent(state.getIdentity(), content.getInitiator()));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      state.setLrec(state.getLrec() + 1);
      state.setWin(content.getInitiator());
      if (state.getLrec() == state.allServerWorkers.size()) {
        if (state.getWin() == state.getIdentity()) {
          state.setStatus(LEADER);
          if (LOG_ON && ELECTION.isInfoEnabled()) {
            ELECTION.info(JE_SUIS_GAGNAT);
          }
        } else {
          state.setStatus(NON_LEADER);
          if (LOG_ON && ELECTION.isInfoEnabled()) {
            ELECTION.info(JE_SUIS_PERDANT);
          }
        }
      }
    }
  }
}
