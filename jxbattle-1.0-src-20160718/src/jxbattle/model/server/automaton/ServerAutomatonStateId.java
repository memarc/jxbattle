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

import org.generic.mvc.model.automaton.StateId;

public enum ServerAutomatonStateId implements StateId
{
    /**
     * initial state
     */
    Init,

    /**
     * setting up game, not listening to new client connections
     */
    SetupGame,

    /**
     * attempt to open server listen socket
     */
    StartListen,

    /**
     * wait for listen socket opening success/failure
     */
    WaitListenSocket,

    /**
     * listen to incoming client connections
     */
    ListenClients,

    /**
     * stop listening to client connections (close server socket)
     */
    StopListen,

    /**
     * wait until all clients send 'ready to start game signal'
     */
    WaitClientsReadyGaming,

    /**
     * send "start game" signal to clients
     */
    SendGameStart,

    /**
     * wait for all clients to acknowledge to "WaitGameStarted" signal
     */
    WaitGameStarted,

    /**
     * game is running
     */
    GameStep,

    /**
     * send "update board" signal to clients
     */
    SendBoardUpdate,

    /**
     * determine next game state (stay in game session/won or lost game)
     */
    GameNextState,

    /**
     * send user command to clients
     */
    //SendUserCommand,

    /**
     * wait for clients to send ack for board update or user command signals  (used in NOT immediate ack mode)
     */
    WaitClientsAcks,

    /**
     * game end, wait for clients to close game window
     */
    WaitClientClose,

    /**
     * stop current game and close all sockets/channels
     */
    Cleanup,

    /**
     * close connections and stop main loop
     */
    ApplicationShutdown,

    /**
     * final state
     */
    Final,
}
