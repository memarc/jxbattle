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

package jxbattle.bean.common.game;

import jxbattle.common.Consts;

import org.generic.mvc.model.MVCModelError;

/**
 * user command (keyboard/mouse action on game board)
 */
public class UserCommand
{
    public enum UserCommandCode
    {
        /**
         * repeat previous command
         */
        REPEAT_LAST_COMMAND,

        /**
         * modify one move vector state
         */
        //  SWAP_VECTORS,
        //SWAP_VECTOR_U, SWAP_VECTOR_D, SWAP_VECTOR_L, SWAP_VECTOR_R,
        SWAP_VECTOR_UL, SWAP_VECTOR_U, SWAP_VECTOR_UR, SWAP_VECTOR_R, SWAP_VECTOR_DR, SWAP_VECTOR_D, SWAP_VECTOR_DL, SWAP_VECTOR_L,

        /**
         * cancel all move vectors in a cell
         */
        CANCEL_VECTORS,

        /**
         * set one or two vectors state, cancel others
         */
        //   SET_VECTORS,
        SET_VECTOR_UL, SET_VECTOR_U, SET_VECTOR_UR, SET_VECTOR_R, SET_VECTOR_DR, SET_VECTOR_D, SET_VECTOR_DL, SET_VECTOR_L,

        /**
         * set marching, setting one move vector, canceling others
         */
        SET_MARCH,

        /**
         * set marching, adding move vector
         */
        SWAP_MARCH,

        /**
         * cancel all march vectors/stop march in a cell
         */
        CANCEL_MARCH,

        /**
         * set "stop march"
         */
        STOP_MARCH,

        /**
         * raise ground by 1 unit
         */
        FILL_GROUND,

        /**
         * managed raise ground command
         */
        MANAGED_FILL_GROUND,

        /**
         * lower ground by 1 unit
         */
        DIG_GROUND,

        /**
         * managed dig command
         */
        MANAGED_DIG_GROUND,

        /**
         * one step base build
         */
        BUILD_BASE,

        /**
         * managed base build
         */
        MANAGED_BUILD_BASE,

        /**
         * one step base destruction
         */
        SCUTTLE_BASE,

        /**
         * managed base destruction
         */
        MANAGED_SCUTTLE_BASE,

        /**
         * cancel managed operation
         */
        CANCEL_MANAGE,

        /**
         * set troops min level
         */
        //TROOPS_RESERVE,
        TROOPS_RESERVE_L1, TROOPS_RESERVE_L2, TROOPS_RESERVE_L3, TROOPS_RESERVE_L4, TROOPS_RESERVE_L5, // 
        TROOPS_RESERVE_L6, TROOPS_RESERVE_L7, TROOPS_RESERVE_L8, TROOPS_RESERVE_L9, TROOPS_RESERVE_L10,

        /**
         * cell attack by surrounding cells
         */
        ATTACK_CELL,

        /**
         * send troops by parachute 
         */
        TROOPS_PARA,

        /**
         * managed "send troops by parachute" 
         */
        MANAGED_TROOPS_PARA,

        /**
         * fire gun troops  
         */
        TROOPS_GUN,

        /**
         * managed "fire gun troops"  
         */
        MANAGED_TROOPS_GUN,

        ;

        public static UserCommandCode valueOf( int v )
        {
            for ( UserCommandCode c : values() )
            {
                if ( c.ordinal() == v )
                    return c;
            }

            throw new MVCModelError( "no UserCommand code with value " + v );
        }
    }

    /**
     * command code
     */
    public UserCommandCode code;

    /**
     * id of player issuing the command
     */
    public int playerId = Consts.NullId;

    /**
     * command arguments
     */
    public int arg1;

    public int arg2;

    /**
     * optional cell affected by command 
     */
    public int cellX;

    public int cellY;

    /**
     * time to live, used for commands affecting cells for more than one update cycle (-1 for infinity)    
     */
    public int ttl;

    public UserCommand( UserCommandCode c )
    {
        code = c;
    }

    public UserCommand( int c )
    {
        code = UserCommand.UserCommandCode.valueOf( c );
    }

    public UserCommand( UserCommand uc )
    {
        code = uc.code;
        playerId = uc.playerId;
        arg1 = uc.arg1;
        arg2 = uc.arg2;
        cellX = uc.cellX;
        cellY = uc.cellY;
        ttl = uc.ttl;
    }

    @Override
    public UserCommand clone()
    {
        //        UserCommand res = new UserCommand( code );
        //        res.playerId = playerId;
        //        res.arg1 = arg1;
        //        res.arg2 = arg2;
        //        res.cellX = cellX;
        //        res.cellY = cellY;
        //        res.ttl = ttl;
        //        return res;
        return new UserCommand( this );
    }

    @Override
    public String toString()
    {
        return code.toString() + " player " + playerId + " ttl " + ttl;
    }
}
