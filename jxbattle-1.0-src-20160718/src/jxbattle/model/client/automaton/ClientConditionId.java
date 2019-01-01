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

package jxbattle.model.client.automaton;

import org.generic.mvc.model.automaton.ConditionId;

public enum ClientConditionId implements ConditionId
{
    /**
     * connection to server requested
     */
    ServerConnectionRequested,

    /**
     * disconnection from server requested
     */
    ServerDisconnectionRequested,

    /**
     * server connection attempt has succeeded
     */
    ServerConnectionSucceeded,

    /**
     * server connection attempt has failed
     */
    ServerConnectionFailed,

    /**
     * server has disconnected/transmission error
     */
    NetworkError,

    /**
     * player allocation requested
     */
    RequestPlayer,

    /**
     * player allocation cancellation requested
     */
    CancelPlayer,

    /**
     * request game data from server
     */
    PlayerGranted,

    /**
     * player allocation request refused by server
     */
    PlayerDenied,

    /**
     * all players are in "completed setup" state
     */
    //AllPlayersCompletedSetup,

    /**
     * server sent game parameters and data 
     */
    ReceivedGameData,

    /**
     * server sent "start game" signal
     */
    StartGame,

    /**
     * a game step (compute/display map state) has been completed
     */
    GameStepCompleted,

    /**
     * game session keeps on
     */
    Gaming,

    /**
     * game session end
     */
    GameEndedByServer,

    /**
     * game stopped by user
     */
    //    QuitGame,
    GameAborted,

    /**
     * message from GUI : window close button
     */
    ShutdownRequested,
}
