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

package jxbattle.model.common.observer;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum XBMVCModelChangeId implements MVCModelChangeId
{
    // list

    /**
     * an element has been added to the list
     */
    //List_AddElement,

    /**
     * an element has been removed from the list
     */
    //List_RemoveElement,

    /**
     * a list of elements has been added to the list
     */
    //   List_AddCollection,

    /**
     * the list has been emptied
     */
    //List_ClearList,

    // common

    /**
     * logging messages
     */
    //LogMessage,
    ErrorMessage, InfoMessage, GuiMessage,

    /**
     * EnumParameter value changed
     */
    //    EnumParameterChanged,

    /**
     * LongParameter value changed
     */
    LongParameterChanged,

    /**
     * player id has changed
     */
    PlayerIdChanged,

    /**
     * player color has changed
     */
    // PlayerColorChanged,

    /**
     * player state has changed 
     */
    PlayerStateChanged,

    /**
     * player name has changed
     */
    PlayerNameChanged,

    /**
     * a net message has been received
     */
    //NetMessageReceived,

    /**
     * a net message acknowledge has been received
     */
    //  NetMessageAck,

    /**
     * numeric id of player has changed
     */
    //PlayerIdChanged,

    /**
     * connection to peer has succeeded
     */
    //PeerConnectionSucceeded,

    /**
     * connection to peer has failed
     */
    //PeerConnectionFailed,

    /**
     * peer has disconnected
     */
    //PeerDisconnected,

    /**
     * network transmission error/peer disconnection
     */
    //NetError,

    /**
     * connection has been shutdown
     */
    //ConnectionShutdown,

    /**
     * game parameters change
     */
    GameParametersChanged,

    //    /**
    //     * state of automaton changed
    //     */
    //    AutomatonStateChanged,

    //
    // client
    //

    /**
     * name of client has changed
     */
    ClientNameChanged,

    /**
     * chosen player has changed
     */
    //ClientPlayerChanged,

    /**
     * peer hostname has changed
     */
    PeerHostChanged,

    /**
     * peer port has changed
     */
    PeerPortChanged,

    /**
     * game server connection infos have changed
     */
    //PeerInfoChanged,

    /**
     * list of game servers changed
     */
    //ServerHistoryChanged,

    /**
     * begin new game
     */
    //    NewGame,

    /**
     * player coverage % of the map has changed
     */
    CoveragePercentChanged,

    /**
     * new user command 
     */
    UserCommand,

    /**
     * game board updated by game engine
     */
    GameBoardUpdated,

    /**
     * watch game mode
     */
    WatchGame,

    /**
     * abort game 
     */
    AbortGame,

    /**
     * parameter "draw grid" (game frame) changed
     */
    DrawGridChanged,

    //
    // server
    //

    /**
     * number of players changed
     */
    //PlayerCountChanged,

    /**
     * listen socket opening failed
     */
    //ListenSocketOpenFailed,

    /**
     * state of client peer has changed
     */
    ClientStateChanged,

    /**
     * current game parameters profile has changed
     */
    GameParametersProfileChanged,

    /**
     * game parameters profile name has changed
     */
    GameParametersProfileNameChanged,

    // menu panel

    /**
     * client engine has started
     */
    ClientEngineStart,

    /**
     * server engine has started
     */
    ServerEngineStart,

    /**
     * open jXBattle web site in system default browser
     */
    BrowseWebSite,

    // game board panel

    /**
     * mouse has moved
     */
    MouseMoved,

    /**
     * mouse wheel event
     */
    MouseWheel,

    /**
     * left mouse has clicked
     */
    LeftMouseClicked,

    /**
     * middle mouse has clicked
     */
    MiddleMouseClicked,

    /**
     * right mouse has clicked
     */
    RightMouseClicked,

    /**
     * a key has been pressed
     */
    KeyPressed,

    /**
     * a key has been released
     */
    KeyReleased,

    /**
     * size of grid cell has changed (zoom in/out)
     */
    GridCellSizeChanged, ;

    // game settings panel

    //  GameSettingsPanelClosed,

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public XBMVCModelChangeId[] getValues()
    {
        return values();
    }

    @Override
    public XBMVCModelChangeId getValueOf( int val )
    {
        for ( XBMVCModelChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for XBMVCModelChangeId enum " + val );
    }

    @Override
    public XBMVCModelChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
