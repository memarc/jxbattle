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

import org.generic.mvc.model.automaton.StateId;

public enum ClientAutomatonStateId implements StateId
{
    /**
     * initial state
     */
    Init,

    /**
     * not connected, waiting for GUI events
     */
    NotConnected,

    /**
     * try to connect to game server
     */
    TryConnect,

    /**
     * wait for connection attempt to succeed/fail
     */
    WaitConnection,

    /**
     * connection succeeded, request players color
     */
    Connected,

    /**
     * request playable colors from server
     */
    RequestAvailableColors,

    /**
     * connected, setting up (color choice, player name)
     */
    Setup,

    /**
     * request a player color to server
     */
    RequestPlayer,

    /**
     * wait for reply to player request (granted/denied)
     */
    WaitPlayerAck,

    /**
     * cancel currently selected player color
     */
    CancelPlayer,

    /**
     * wait for all players to be ready gaming
     */
    //WaitPlayersReadyGaming,

    /**
     * wait for all players completed setup
     */
    //WaitPlayersCompletedSetup,

    /**
     * setting up is completed, request game data
     */
    RequestGameData,

    /**
     * wait for request game data
     */
    WaitGameData,

    /**
     * preparing game
     */
    PrepareGame,

    /**
     * wait for server to send "game start" signal
     */
    WaitGameStart, // WaitPlayersReadyGaming,

    /**
     * open gaming window and count down
     */
    LaunchGame,

    /**
     * game session is running
     */
    GameStep,

    /**
     * wait for server to send next game state (stay in game session/won or lost game)
     */
    GameNextState,

    /**
     * end of game (client->server notification)
     */
    //NotifyQuit,

    /**
     * end of game (server notification)
     */
    GameEnd,

    /**
     * application cleanup (application not terminated)
     */
    Cleanup,

    /**
     * application cleanup (application terminated)
     */
    ApplicationShutdown,

    /**
     * final state
     */
    Final;
}
