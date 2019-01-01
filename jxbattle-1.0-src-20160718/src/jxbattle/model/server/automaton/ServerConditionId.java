/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package jxbattle.model.server.automaton;

import org.generic.mvc.model.automaton.ConditionId;

public enum ServerConditionId implements ConditionId
{
    /**
     * message from GUI : request to start listening to client connections
     */
    ListenClientsRequested,

    /**
     * listen socket creation failed
     */
    ListenSocketOpenOk,

    /**
     * listen socket creation failed
     */
    ListenSocketOpenFailed,

    /**
     * client has disconnected
     */
    ClientDisconnected,

    /**
     * all preset clients connected, wait for client to complete setup 
     */
    AllClientsConnected,

    /**
     * all clients have disconnected
     */
    AllClientsDisconnected,

    /**
     * all clients have sent 'ready gaming' signal
     */
    AllClientsReadyGaming,

    /**
     * all clients have sent 'game started' signal
     */
    AllClientsStartedGame,

    /**
     * "update board" timer tick has occurred
     */
    BoardUpdateTick,

    /**
     * user command received from one client
     */
    ReceivedUserCommand,

    /**
     * wait user commands/board update ack
     */
    WaitAllAcks,

    /**
     * all clients have sent ack to user commands/board update/map state
     */
    AllClientsAcked,

    /**
     * stay in game session
     */
    KeepOnGaming,

    /**
     * normal end of game
     */
    GameEndRequested,

    /**
     * message from GUI : "stop" button
     */
    AbortGameRequested,

    /**
     * message from GUI : window close button
     */
    ShutdownRequested,
}
