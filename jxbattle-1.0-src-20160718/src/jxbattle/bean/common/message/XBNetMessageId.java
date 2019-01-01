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

package jxbattle.bean.common.message;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.net.NetMessageId;

public enum XBNetMessageId implements NetMessageId
{
    dummy, // used to be able to call getValueOf()

    // client -> server

    /**
     * client name modified
     */
    ClientName,

    /**
     * request player colors just after connection to server
     */
    GetAllPlayers,

    /**
     * player color request by a client
     */
    RequestPlayer,

    /**
     * client canceled previously chosen player color
     */
    CancelPlayer,

    /**
     * player setup completed, waiting for game data
     */
    GetGameData,

    /**
     * all setup/game preparation completed, waiting for game start signal
     */
    ReadyGaming,

    /**
     * game has started
     */
    StartedGame,

    /**
     * board has been updated (acknowledge to DoBoardUpdate signal)
     */
    BoardUpdateAck,

    /**
     * user command has been processed (acknowledge to UserCommand signal)
     */
    UserCommandAck,

    /**
     * game state (board state/coverage %/game state/...) send to server (optionally used by server to check client coherence) 
     */
    GameState,

    /**
     * keep on or stop game session
     */
    GameNextStep,

    /**
     * answer to ping
     */
    PingAck,

    // server -> client

    /**
     * player name changed
     */
    PlayerNameChanged,

    /**
     * a player has been added to list
     */
    //PlayerAdded,

    /**
     * server accepted player color request (acknowledge to RequestPlayer signal)
     */
    PlayerGranted,

    /**
     * server refused player color request (acknowledge to RequestPlayer signal)
     */
    PlayerDenied,

    /**
     * player id changed
     */
    PlayerId,

    /**
     * send game parameters/initial state
     */
    GameData,

    /**
     * game start signal
     */
    StartGame,

    /**
     * board update synchronisation signal
     */
    DoBoardUpdate,

    /**
     * query game state from client
     */
    GameStateQuery,

    /**
     * liveness test
     */
    Ping,

    // server <-> client

    /**
     * player state change
     */
    PlayerState,

    /**
     * user command (mouse, keyboard)
     */
    UserCommand,

    /**
     * client can send commands and acks to server
     */
    DoFlush;

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public XBNetMessageId[] getValues()
    {
        return values();
    }

    @Override
    public XBNetMessageId getValueOf( int val )
    {
        for ( XBNetMessageId id : values() )
        {
            if ( id.ordinal() == val )
                return id;
        }

        throw new MVCModelError( "no XBNetMessageId with value " + val );
    }

    @Override
    public XBNetMessageId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
