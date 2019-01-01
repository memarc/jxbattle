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

import org.generic.mvc.model.automaton.AutomatonTransitionId;

public enum ServerTransitionComputationId implements AutomatonTransitionId
{
    /**
     * always True
     */
    True,

    /**
     * = ShutdownRequested
     */
    Shutdown,

    /**
     * = ListenClientsRequested 
     */
    StartListen,

    /**
     * listen socket not yet open
     */
    NotListenSocketYet,

    /**
     * = !ShutdownRequested
     */
    OpenListenSuccess,

    /**
     * = ListenSocketOpenFailed
     */
    OpenListenFailed,

    /**
     * = AllClientsConnected
     */
    AllClientsConnected,

    WaitGameStarted,

    /**
     * = !ShutdownRequested
     */
    WaitClientsReadyGaming,

    /**
     * = ClientDisconnected
     */
    ClientDisconnected,

    /**
     * = AbortGameRequested
     */
    AbortGame,

    /**
     * = AbortGameRequested || AllClientsDisconnected
     */
    AbortGameOrClientsDisconnected,

    /**
     * = AllClientsReadyGaming
     */
    AllClientsReadyGaming,

    /**
     * = AllClientsStartedGame
     */
    AllClientsStartedGame,

    /**
     * = !(AllClientsDisconnected || ShutdownRequested)
     */
    GameStep,

    /**
     * = BoardUpdateTick
     */
    BoardUpdateTick,

    /**
     * = ReceivedUserCommand
     */
    //ReceivedUserCommand,

    /**
     * = !ShutdownRequested
     */
    SendUserCommand,

    /**
     * = WaitAllAcks
     */
    WaitAllAcks,

    /**
     * = AllClientsAcked
     */
    AllClientsAcked,

    /**
     * = KeepOnGaming
     */
    KeepOnGaming,

    /**
     * = GameEndRequested
     */
    GameEnd;

    @Override
    public boolean isTrue()
    {
        return ordinal() == True.ordinal();
    }
}
