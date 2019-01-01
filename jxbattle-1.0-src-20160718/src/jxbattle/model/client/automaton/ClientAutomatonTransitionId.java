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

import org.generic.mvc.model.automaton.AutomatonTransitionId;

public enum ClientAutomatonTransitionId implements AutomatonTransitionId
{
    /**
     * always True
     */
    True,

    /**
     * = ServerConnectionRequested
     */
    AttemptConnectServer,

    /**
     * = ShutdownRequested
     */
    Shutdown,

    /**
     * connection to server not yet succeeded/failed
     */
    NoServerConnectionYet,

    /**
     * = ServerConnectionSucceeded
     */
    ServerConnectionSucceeded,

    /**
     * = ServerConnectionFailed
     */
    ServerConnectionFailed,

    /**
     * = !ShutdownRequested && !NetworkError
     */
    RequestColors,

    /**
     * = !ShutdownRequested && !NetworkError
     */
    WaitPlayerAck,

    /**
     * = !ShutdownRequested
     */
    PlayerCanceled,

    /**
     * = !ShutdownRequested
     */
    DoSetup,

    /**
     * = RequestPlayer
     */
    RequestPlayer,

    /**
     * = ServerDisconnectionRequested || NetworkError
     */
    NetworkError,

    /**
     * = PlayerGranted
     */
    PlayerGranted,

    /**
     * = PlayerDenied
     */
    PlayerDenied,

    /**
     * = AllPlayersReadyGaming
     */
    //PlayersReadyGaming,

    /**
     * = AllPlayersCompletedSetup
     */
    //PlayersCompletedSetup,

    /**
     * = ! ReceivedGameData
     */
    WaitGameData,

    /**
     * = ReceivedGameData
     */
    ReceivedGameData,

    /**
     * = CancelPlayer
     */
    CancelPlayer,

    /**
     * = !ShutdownRequested
     */
    WaitGameStart,

    /**
     * = !ShutdownRequested
     */
    LaunchGame,

    /**
     * = StartGame
     */
    StartGame,

    /**
     * = GameStepCompleted
     */
    GameStep,

    /**
     * error during game
     */
    GameAbort,

    /**
     * = Gaming
     */
    KeepGaming,

    /**
     * user quits game
     */
    //  QuitGame,

    /**
     * = GameEnd
     */
    GameEndByServer,

    ;

    @Override
    public boolean isTrue()
    {
        return ordinal() == True.ordinal();
    }
}
