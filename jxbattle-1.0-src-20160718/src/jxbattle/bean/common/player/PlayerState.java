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

package jxbattle.bean.common.player;

import org.generic.mvc.model.MVCModelError;

/**
 * player state during game session
 */
public enum PlayerState
{
    Init,

    /**
     * client is choosing player
     */
    SettingUp,

    /**
     * client has completed setup (name/player choice)
     */
    CompletedSetup,

    /**
     * player is setup
     */
    //ReadyGaming,

    /**
     * client waiting for 'start game' signal
     */
    //ReadyToStartGame,
    ReadyStartGaming,

    /**
     * client has initialised and started game
     */
    StartedGame,

    /**
     * player is in game session
     */
    PlayingGame,

    //  /**
    //  * client is in game session
    //  */
    // Gaming,

    /**
     * player stopped playing, watching game
     */
    WatchingGame,

    /**
     * player lost game
     */
    LostGame,

    /**
     * player won game
     */
    WonGame,

    /**
     * game internal error, clients have desynchronised
     */
    Desynchronised,

    /**
     * player has aborted/ended game 
     */
    LeftGame;

    //    /**
    //     * client has aborted/ended game 
    //     */
    //    EndGame;

    public static PlayerState valueOf( int v )
    {
        for ( PlayerState ps : values() )
        {
            if ( ps.ordinal() == v )
                return ps;
        }

        throw new MVCModelError( "no PlayerState with value " + v );
    }

    public static PlayerState initialValue()
    {
        return Init;
    }
}
