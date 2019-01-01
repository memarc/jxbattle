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

import jxbattle.bean.common.game.BoardState;
import jxbattle.bean.common.game.CellState;
import jxbattle.bean.common.game.GameState;
import jxbattle.bean.common.game.UserCommand;
import jxbattle.bean.common.parameters.game.BoardParameters;
import jxbattle.bean.common.parameters.game.GameParameters;
import jxbattle.bean.common.parameters.game.InitialisationParameters;
import jxbattle.bean.common.player.ColorId;
import jxbattle.bean.common.player.PlayerState;
import jxbattle.common.net.XBPeer;
import jxbattle.model.MainModel;

import org.generic.bean.parameter.BoolParameter;
import org.generic.bean.parameter.EnumParameter;
import org.generic.bean.parameter.IntParameter;
import org.generic.bean.parameter.LongParameter;
import org.generic.mvc.model.MVCModelError;
import org.generic.net.NetException;
import org.generic.net.NetMessage;
import org.generic.net.NetMessageId;
import org.generic.net.NetPeer;

/**
 * network message between client and server
 */
//public class NetMessage extends Message
public class XBNetMessage extends NetMessage
{
    public XBNetMessage( NetMessageId mid )
    {
        super( mid );
    }

    //    /**
    //     * message id
    //     */
    //    private XBNetMessageId messageId;
    //
    //    /**
    //     * connection to peer 
    //     */
    //    private XBPeer xbPeer;

    public XBNetMessage( NetMessageId mid, NetPeer p )
    {
        super( mid, p );
    }

    /**
     * sequence number for "board update" and "user command" messages
     */
    private int commandSequence;

    /**
     * in case ack is needed, has the client acknowledged the message ?
     */
    private boolean ack;

    //    /**
    //     * creation time stamp 
    //     */
    //    long creationTime;
    //
    //    public XBNetMessage( XBNetMessageId mid, XBPeer peer )
    //    {
    //        messageId = mid;
    //        xbPeer = peer;
    //        ack = false;
    //        sequence = -1;
    //        creationTime = System.currentTimeMillis();
    //    }
    //
    //    public XBNetMessageId getMessageId()
    //    {
    //        return messageId;
    //    }
    //
    //    public XBPeer getXBPeer()
    //    {
    //        return xbPeer;
    //    }
    //
    //    public void setXBPeer( XBPeer peer )
    //    {
    //        xbPeer = peer;
    //    }

    public boolean isAck()
    {
        return ack;
    }

    public void setAck( boolean a )
    {
        ack = a;
    }

    @Override
    public XBPeer getPeer()
    {
        return (XBPeer)peer;
    }

    @Override
    public int getByteSize() throws NetException
    {
        switch ( (XBNetMessageId)getMessageId() )
        {
            case DoBoardUpdate:
            case BoardUpdateAck:
            case UserCommandAck:
                return integerSize;

            case PlayerNameChanged:
                int res = getByteSize( getDataArray().get( 0 ) ); // color id
                res += getNullableByteSize( getDataArray().get( 1 ) ); // , String.class );
                return res;

            default:
                return super.getByteSize();
        }
    }

    @Override
    public int getByteSize( Object d ) throws NetException
    {
        //        if ( d instanceof PeerDTO )
        //        {
        //            PeerDTO dto = (PeerDTO)d;
        //            int res = integerSize;
        //            if ( dto.getData() != null )
        //                res += getByteSize( dto.getData() );
        //            else
        //                res += integerSize;
        //            return res;
        //        }
        //        else 
        if ( d instanceof PlayerState )
            return integerSize;
        else if ( d instanceof GameState )
        {
            GameState gs = (GameState)d;
            int res = getByteSize( gs.getCurrentPlayerState() );
            // stats
            res += integerSize; // size
            res += gs.getPlayerStatistics().size() * (integerSize + doubleSize);
            if ( MainModel.boardStateFullCheck )
                res += getByteSize( gs.getBoardState() );

            return res;
        }
        else if ( d instanceof ColorId )
            return integerSize;
        //        else if ( d instanceof Pair<?, ?> )
        //        {
        //            Pair<?, ?> p = (Pair<?, ?>)d;
        //            int res = getByteSize( p.getLeft() );
        //            res += getByteSize( p.getRight() );
        //            return res;
        //        }
        else if ( d instanceof IntParameter )
            return integerSize;
        else if ( d instanceof BoolParameter )
            return boolSize;
        else if ( d instanceof EnumParameter )
            return integerSize;
        else if ( d instanceof LongParameter )
            return longSize;
        else if ( d instanceof UserCommand )
        {
            UserCommand cmd = (UserCommand)d;

            int res = integerSize * 3;

            switch ( cmd.code )
            {
            //                case SWAP_VECTORS:
            //                case SET_VECTORS:
            //                case TROOPS_RESERVE:
                case SWAP_MARCH:
                case SET_MARCH:
                    res += 3 * integerSize;
                    break;

                case SWAP_VECTOR_U:
                case SWAP_VECTOR_UR:
                case SWAP_VECTOR_R:
                case SWAP_VECTOR_DR:
                case SWAP_VECTOR_D:
                case SWAP_VECTOR_DL:
                case SWAP_VECTOR_L:
                case SWAP_VECTOR_UL:

                case SET_VECTOR_U:
                case SET_VECTOR_UR:
                case SET_VECTOR_R:
                case SET_VECTOR_DR:
                case SET_VECTOR_D:
                case SET_VECTOR_DL:
                case SET_VECTOR_L:
                case SET_VECTOR_UL:

                case TROOPS_RESERVE_L1:
                case TROOPS_RESERVE_L2:
                case TROOPS_RESERVE_L3:
                case TROOPS_RESERVE_L4:
                case TROOPS_RESERVE_L5:
                case TROOPS_RESERVE_L6:
                case TROOPS_RESERVE_L7:
                case TROOPS_RESERVE_L8:
                case TROOPS_RESERVE_L9:
                case TROOPS_RESERVE_L10:

                case CANCEL_VECTORS:
                case FILL_GROUND:
                case MANAGED_FILL_GROUND:
                case DIG_GROUND:
                case MANAGED_DIG_GROUND:
                case BUILD_BASE:
                case MANAGED_BUILD_BASE:
                case SCUTTLE_BASE:
                case MANAGED_SCUTTLE_BASE:
                case CANCEL_MANAGE:
                case ATTACK_CELL:
                case CANCEL_MARCH:
                case STOP_MARCH:
                    res += 2 * integerSize;
                    break;

                case TROOPS_PARA:
                case MANAGED_TROOPS_PARA:
                case TROOPS_GUN:
                case MANAGED_TROOPS_GUN:
                    res += 4 * integerSize;
                    break;

                default:
                    throw new MVCModelError( "sendUserCommand() : invalid command code " + cmd.code );
            }

            return res;
        }
        else if ( d instanceof GameParameters )
        {
            GameParameters gp = (GameParameters)d;
            int res = getByteSize( gp.getInitialisationParameters() );

            res += getByteSize( gp.getTroopsMove() );

            res += getByteSize( gp.getSupplyFarms() );
            res += getByteSize( gp.getSupplyDecay() );
            res += getByteSize( gp.getFightIntensity() );

            res += getByteSize( gp.getEnableBaseBuild() );
            res += getByteSize( gp.getBaseBuildSteps() );
            res += getByteSize( gp.getBaseBuildCost() );

            res += getByteSize( gp.getEnableBaseScuttle() );
            res += getByteSize( gp.getBaseScuttleCost() );

            res += getByteSize( gp.getEnableFill() );
            res += getByteSize( gp.getFillCost() );

            res += getByteSize( gp.getEnableDig() );
            res += getByteSize( gp.getDigCost() );

            res += getByteSize( gp.getHillSteepness() );

            res += getByteSize( gp.getAllowKeyRepeat() );
            res += getByteSize( gp.getEnableAttack() );
            res += getByteSize( gp.getEnableManagedCommands() );

            res += getByteSize( gp.getEnableParaTroops() );
            res += getByteSize( gp.getParaTroopsRange() );
            res += getByteSize( gp.getParaTroopsCost() );
            res += getByteSize( gp.getParaTroopsDamage() );

            res += getByteSize( gp.getEnableGunTroops() );
            res += getByteSize( gp.getGunTroopsRange() );
            res += getByteSize( gp.getGunTroopsCost() );
            res += getByteSize( gp.getGunTroopsDamage() );

            res += getByteSize( gp.getVisibilityMode() );
            res += getByteSize( gp.getVisibilityRange() );
            res += getByteSize( gp.getVisibilityRememberCells() );
            res += getByteSize( gp.getVisibilityHideVectors() );

            res += getByteSize( gp.getEnableMarching() );
            res += getByteSize( gp.getMarchSpeed() );

            res += getByteSize( gp.getBoardParameters() );

            return res;
        }
        else if ( d instanceof InitialisationParameters )
        {
            InitialisationParameters ip = (InitialisationParameters)d;
            int res = getByteSize( ip.getRandomSeed() );
            res += getByteSize( ip.getBases() );
            res += getByteSize( ip.getRandomBases() );
            res += getByteSize( ip.getTowns() );
            res += getByteSize( ip.getArmies() );
            res += getByteSize( ip.getMilitia() );
            res += getByteSize( ip.getSeaDensity() );
            res += getByteSize( ip.getHillDensity() );
            return res;
        }
        else if ( d instanceof BoardParameters )
        {
            BoardParameters bp = (BoardParameters)d;
            int res = getByteSize( bp.getXCellCount() );
            res += getByteSize( bp.getYCellCount() );
            res += getByteSize( bp.getWrappingMode() );
            return res;
        }
        else if ( d instanceof BoardState )
        {
            BoardState bs = (BoardState)d;
            int res = integerSize * 2; // dimx, dimy
            res += integerSize; // player count
            res += bs.getBoardXDim() * bs.getBoardYDim() * getByteSize( bs.getCell( 0, 0 ).cellState );
            return res;
        }
        else if ( d instanceof CellState )
        {
            CellState cs = (CellState)d;
            int res = integerSize; // player id
            //res += integerSize; // player count
            res += cs.troopsLevel.length * floatSize; // troops levels

            return res;
        }

        return super.getByteSize( d );
    }

    @Override
    public void copyDataFrom( NetMessage m ) throws NetException
    {
        super.copyDataFrom( m );
        XBNetMessage nm = (XBNetMessage)m;
        setCommandSequence( nm.getCommandSequence() );
    }

    public int getCommandSequence()
    {
        return commandSequence;
    }

    public void setCommandSequence( int seq )
    {
        commandSequence = seq;
    }

    //    public long getCreationTime()
    //    {
    //        return creationTime;
    //    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append( getMessageId() );
        switch ( (XBNetMessageId)getMessageId() )
        {
            case RequestPlayer:
            {
                ColorId cid = (ColorId)getDataArray().get( 0 );

                sb.append( " color " );
                sb.append( cid );
                break;
            }

            case PlayerId:
            {
                ColorId cid = (ColorId)getDataArray().get( 0 );
                Integer playerId = (Integer)getDataArray().get( 1 );

                sb.append( " color " );
                sb.append( cid );
                sb.append( " id " );
                sb.append( playerId );
                break;
            }

            case PlayerState:
            {
                ColorId cid = (ColorId)getDataArray().get( 0 );
                PlayerState ps = (PlayerState)getDataArray().get( 1 );

                sb.append( " color " );
                sb.append( cid );
                sb.append( " state " );
                sb.append( ps );
                break;
            }

            case PlayerNameChanged:
            {
                ColorId cid = (ColorId)getDataArray().get( 0 );
                String name = (String)getDataArray().get( 1 );

                sb.append( " col " );
                sb.append( cid );
                sb.append( " name " );
                sb.append( name );
                break;
            }

            case ClientName:
                sb.append( " name " );
                sb.append( getData() );
                break;

            case DoBoardUpdate:
            case BoardUpdateAck:
            case UserCommandAck:
                sb.append( " seq " );
                sb.append( getCommandSequence() );
                break;

            case UserCommand:
                sb.append( " seq " );
                sb.append( getCommandSequence() );
                sb.append( " code " );
                UserCommand cmd = (UserCommand)getData();
                sb.append( cmd.code.ordinal() );
                sb.append( " pid " );
                sb.append( cmd.playerId );
                break;

            case GameNextStep:
                sb.append( " " );
                sb.append( getData() );
                break;

            default:
                break;
        }

        sb.append( ack ? " ACK" : " !ack" );

        return sb.toString();
    }
}
