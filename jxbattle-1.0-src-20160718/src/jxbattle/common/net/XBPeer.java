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

package jxbattle.common.net;

import java.io.EOFException;
import java.io.IOException;

import jxbattle.bean.common.game.BoardState;
import jxbattle.bean.common.game.Cell;
import jxbattle.bean.common.game.CellState;
import jxbattle.bean.common.game.GameState;
import jxbattle.bean.common.game.UserCommand;
import jxbattle.bean.common.message.XBNetMessage;
import jxbattle.bean.common.message.XBNetMessageId;
import jxbattle.bean.common.parameters.game.BoardParameters;
import jxbattle.bean.common.parameters.game.GameParameters;
import jxbattle.bean.common.parameters.game.InitialisationParameters;
import jxbattle.bean.common.player.ColorId;
import jxbattle.bean.common.player.PlayerState;
import jxbattle.bean.common.stats.PlayerStatistics;
import jxbattle.model.MainModel;
import jxbattle.model.common.DebugLog;

import org.generic.mvc.model.MVCModelError;
import org.generic.net.NetException;
import org.generic.net.NetMessage;
import org.generic.net.NetMessageId;
import org.generic.net.NetPeer;

public class XBPeer extends NetPeer
{
    //    private ChannelUtil channelUtil;
    //    private List<XBNetMessage> netMessages;

    private DebugLog debugLog;

    /**
     * methods may be accessed from main or EDT threads
     * so use lock
     */
    //private ReentrantLock lock;

    //private Mutex lock;

    public XBPeer()
    {
        //        channelUtil = new ChannelUtil( channel );
        //        netMessages = new ArrayList<>();
        //        lock = new ReentrantLock();
        //lock = new Mutex();
    }

    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }

    //    @Override
    //    public void close()
    //    {
    //        if ( channelUtil != null )
    //        {
    //            channelUtil.close();
    //            channelUtil = null;
    //        }
    //
    //        netMessages.clear();
    //    }

    //    private ChannelUtil getChannelUtil() throws IOException
    //    {
    //        if ( !isConnected() )
    //            throw new IOException( "connection not open/already closed" );
    //
    //        return channelUtil;
    //    }

    //    @Override
    //    public boolean isConnected()
    //    {
    //        if ( channelUtil == null )
    //            return false;
    //
    //        return channelUtil.isConnected();
    //    }

    //    @Override
    //    public String getIPPort()
    //    {
    //        try
    //        {
    //            return getChannelUtil().getIP() + ":" + getChannelUtil().getPort();
    //        }
    //        catch( IOException e )
    //        {
    //            return "<not connected>";
    //        }
    //    }

    //    @Override
    //    public int getPort() throws IOException
    //    {
    //        return getChannelUtil().getPort();
    //    }

    //    @Override
    //    public int getLocalPort() throws IOException
    //    {
    //        return getChannelUtil().getLocalPort();
    //    }

    //    @Override
    //    public void setNetworkBias( int bias ) throws IOException
    //    {
    //        getChannelUtil().setNetworkBias( bias );
    //    }

    //    @Override
    //    public void flushOutputBuffer() throws EOFException, IOException
    //    {
    //        try
    //        {
    //            lock.lock();
    //
    //            getChannelUtil().flushOutputBuffer();
    //        }
    //        finally
    //        {
    //            lock.unlock();
    //        }
    //    }

    //    @Override
    //    public void setSocketTimeout( int timeout ) throws IOException
    //    {
    //        getChannelUtil().setSocketTimeout( timeout );
    //    }

    //    private void receiveIntParameter( IntParameter param )
    //    {
    //        param.setValue( receiveInt() );
    //    }

    //    private Integer receiveInteger() throws SocketTimeoutException, EOFException, IOException
    //    {
    //        return Integer.valueOf( getChannelUtil().receiveInt( true ) );
    //    }

    //    private void receiveEnumParameter( EnumParameter param, Class<? extends EnumValue> clss ) throws IOException
    //    {
    //        param.setValue( receiveEnum( clss ) );
    //    }

    //    private void sendEnum( EnumValue v ) throws EOFException, IOException
    //    {
    //        getChannelUtil().sendInt( v.getOrdinal() );
    //    }

    //    private void receiveLongParameter( LongParameter param )
    //    {
    //        param.setValue( receiveLong() );
    //    }

    //    private void sendLong( long l ) throws EOFException, IOException
    //    {
    //        getChannelUtil().sendLong( l );
    //    }

    //    private void receiveBoolParameter( BoolParameter param )
    //    {
    //        param.setValue( receiveBool() );
    //    }

    //    private Boolean receiveBoolean() throws SocketTimeoutException, EOFException, IOException
    //    {
    //        return Boolean.valueOf( getChannelUtil().receiveInt( true ) == 1 );
    //    }

    //    private void sendBoolean( boolean b ) throws EOFException, IOException
    //    {
    //        sendInt( b ? 1 : 0 );
    //    }

    //    private void sendString( String s ) throws EOFException, IOException
    //    {
    //        getChannelUtil().sendString( s );
    //    }

    //    private void sendDouble( double d ) throws EOFException, IOException
    //    {
    //        getChannelUtil().sendDouble( d );
    //    }

    //    private void requestPlayableColors()
    //    {
    //        sendNetMessageId( XBNetMessageId.GetAllPlayers );
    //        //        try
    //        //        {
    //        //            lock.lock();
    //        //
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    private void sendPlayerAllocationRequest( ColorId id )
    {
        //        sendNetMessageId( XBNetMessageId.RequestPlayer );
        sendInt( id.value );
        //        try
        //        {
        //            lock.lock();
        //
        //        }
        //        finally
        //        {
        //            lock.unlock();
        //        }
    }

    //    private void sendPlayerCancellation()
    //    {
    //        sendNetMessageId( XBNetMessageId.CancelPlayer );
    //        //        try
    //        //        {
    //        //            lock.lock();
    //        //
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    //    private void sendReadyGaming()
    //    {
    //        sendNetMessageId( XBNetMessageId.ReadyGaming );
    //        //        try
    //        //        {
    //        //            lock.lock();
    //        //
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    //    private void sendStartedGame()
    //    {
    //        sendNetMessageId( XBNetMessageId.StartedGame );
    //        //        try
    //        //        {
    //        //            lock.lock();
    //        //
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    private void sendClientName( String name ) throws NetException
    {
        //        sendNetMessageId( XBNetMessageId.ClientName );
        //sendInt( name.getPlayerId() );
        sendString( name );
        //        try
        //        {
        //            lock.lock();
        //
        //        }
        //        finally
        //        {
        //            lock.unlock();
        //        }
    }

    //    private void sendPlayerId( PeerDTO id )
    //    {
    //        //        sendNetMessageId( XBNetMessageId.PlayerId );
    //        sendInt( id.getColorId().value );
    //        sendInt( ((Integer)id.getData()).intValue() );
    //        //        try
    //        //        {
    //        //            lock.lock();
    //        //
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    //    private void sendPlayerId( Pair<ColorId, Integer> data )
    //    {
    //        sendColorId( data.getLeft() );
    //        sendInt( data.getRight() );
    //    }

    private void sendPlayerId( NetMessage nm )
    {
        sendColorId( (ColorId)nm.getDataArray().get( 0 ) );
        sendInt( (Integer)nm.getDataArray().get( 1 ) );
    }

    private void sendGrantPlayer( ColorId colorId )
    {
        //        sendNetMessageId( XBNetMessageId.PlayerGranted );
        sendInt( colorId.value );
        //        try
        //        {
        //            lock.lock();
        //
        //        }
        //        finally
        //        {
        //
        //            lock.unlock();
        //        }
    }

    //    private void sendDenyPlayer()
    //    {
    //        sendNetMessageId( XBNetMessageId.PlayerDenied );
    //        //        try
    //        //        {
    //        //            lock.lock();
    //        //
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    //    private void sendGetGameData()
    //    {
    //        sendNetMessageId( XBNetMessageId.GetGameData );
    //        //        try
    //        //        {
    //        //            lock.lock();
    //        //
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    //    private void sendGameStart()
    //    {
    //        sendNetMessageId( XBNetMessageId.StartGame );
    //        //        try
    //        //        {
    //        //            lock.lock();
    //        //
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    private void sendBoardUpdateCommand( int seq )
    {
        //        sendNetMessageId( XBNetMessageId.DoBoardUpdate );
        sendInt( seq );
        //        try
        //        {
        //            lock.lock();
        //
        //        }
        //        finally
        //        {
        //            lock.unlock();
        //        }
    }

    private void sendBoardUpdateAck( int seq )
    {
        //        sendNetMessageId( XBNetMessageId.BoardUpdateAck );
        sendInt( seq );
        //        try
        //        {
        //            lock.lock();
        //
        //        }
        //        finally
        //        {
        //            lock.unlock();
        //        }
    }

    private int receiveSequence()
    {
        return receiveInt();
    }

    private ColorId receiveColorId()
    {
        return new ColorId( receiveInt() );
    }

    private void sendColorId( ColorId id )
    {
        sendInt( id.value );
    }

    //    @Override
    //    protected NetMessage createMessage( NetMessageId mid )
    //    {
    //        return new XBNetMessage( mid );
    //    }

    @Override
    protected NetMessage createMessage( NetMessageId mid, NetPeer peer )
    {
        return new XBNetMessage( mid, peer );
    }

    @Override
    protected XBNetMessageId receiveNetMessageId()
    {
        int v = receiveInt();
        return XBNetMessageId.dummy.getValueOf( v );
        //        return (XBNetMessageId)receiveEnum( XBNetMessageId.class );
    }

    @Override
    protected void receiveNetMessage( NetMessage m ) throws NetException
    {
        XBNetMessage nm = (XBNetMessage)m;
        XBNetMessageId mid = (XBNetMessageId)nm.getMessageId();

        if ( mid != XBNetMessageId.Ping && mid != XBNetMessageId.PingAck )
            debugLog( "received message " + mid );

        switch ( mid )
        {
            case GetAllPlayers:
                break;

            case PlayerGranted:
                ColorId id = new ColorId( receiveInt() );

                nm.addData( id );
                break;

            case ClientName:
                nm.addData( receiveString() );
                break;

            case PlayerId:
                //                PeerDTO data = new PeerDTO();
                //                data.getColorId().value = receiveInt();
                //                data.setData( receiveInteger() ); // player id
                //                Pair<ColorId, Integer> data = new Pair<ColorId, Integer>( receiveColorId(), receiveInteger() );
                //                nm.addData( data );
                nm.addData( receiveColorId() );
                nm.addData( receiveInteger() );
                break;

            case PlayerState:
                //                nm.addData( receiveColorIdPlayerState() );
                receiveColorIdPlayerState( nm );
                break;

            case PlayerNameChanged:
                //                nm.addData( receivePlayerName() );
                receivePlayerName( nm );
                break;

            case RequestPlayer:
                id = new ColorId( receiveInteger() );
                nm.addData( id );
                break;

            case CancelPlayer:
                break;

            case GetGameData:
                break;

            case GameData:
                GameParameters gp = new GameParameters();
                receiveGameParameters( gp );
                nm.addData( gp );
                break;

            case ReadyGaming:
            case StartGame:
            case StartedGame:
                break;

            case UserCommand:
                nm.setCommandSequence( receiveSequence() );
                nm.addData( receiveUserCommand() );
                break;

            case UserCommandAck:
                nm.setCommandSequence( receiveSequence() );
                break;

            case DoBoardUpdate:
                nm.setCommandSequence( receiveSequence() );
                break;

            case BoardUpdateAck:
                nm.setCommandSequence( receiveSequence() );
                break;

            case GameStateQuery:
                break;

            case GameState:
                nm.addData( receiveGameState() );
                break;

            case GameNextStep:
                nm.addData( receiveBoolean() );
                break;

            case Ping:
                // answer at once
                int seq = receiveSequence();
                debugLog( "received Ping seq=" + seq );
                sendPingAck( seq );
                break;

            case PingAck:
                seq = receiveSequence();
                debugLog( "received PingAck seq=" + seq );
                nm.setCommandSequence( seq );
                break;

            default:
                throw new NetException( "non processed input network message " + mid, this );
        }
    }

    @Override
    protected void sendNetMessage( NetMessage m ) throws NetException
    {
        XBNetMessage nm = (XBNetMessage)m;
        XBNetMessageId mid = (XBNetMessageId)nm.getMessageId();

        switch ( mid )
        {
            case GetAllPlayers:
                //                requestPlayableColors();
                break;

            case RequestPlayer:
                sendPlayerAllocationRequest( (ColorId)nm.getData() );
                break;

            case PlayerGranted:
                sendGrantPlayer( (ColorId)nm.getData() );
                break;

            case PlayerDenied:
                //                sendDenyPlayer();
                break;

            case PlayerState:
                //                sendPlayerState( (Pair<ColorId, PlayerState>)nm.getData() );
                sendPlayerState( nm );
                break;

            case PlayerNameChanged:
                //                sendPlayerName( (Pair<ColorId, String>)nm.getData() );
                sendPlayerName( nm );
                break;

            case PlayerId:
                //                sendPlayerId( (Pair<ColorId, Integer>)nm.getData() );
                sendPlayerId( nm );
                break;

            case CancelPlayer:
                //                sendPlayerCancellation();
                break;

            case ReadyGaming:
                //                sendReadyGaming();
                break;

            case GameData:
                sendGameParameters( (GameParameters)nm.getData() );
                break;

            case StartGame:
                //                sendGameStart();
                break;

            case StartedGame:
                //                sendStartedGame();
                break;

            case GetGameData:
                //                sendGetGameData();
                break;

            case DoBoardUpdate:
                sendBoardUpdateCommand( nm.getCommandSequence() );
                //ackPendingMessages.add( nm );
                break;

            case UserCommand:
                sendUserCommand( (UserCommand)nm.getData(), nm.getCommandSequence() );
                //ackPendingMessages.add( nm );
                break;

            case UserCommandAck:
                sendUserCommandAck( nm.getCommandSequence() );
                break;

            case BoardUpdateAck:
                sendBoardUpdateAck( nm.getCommandSequence() );
                break;

            case GameNextStep:
                sendGameNextStep( (Boolean)nm.getData() );
                break;

            case GameStateQuery:
                //                sendGameStateQuery();
                break;

            case GameState:
                sendGameState( (GameState)nm.getData() );
                break;

            case ClientName:
                sendClientName( (String)nm.getData() );
                break;

            default:
                throw new NetException( "non processed output network message " + mid, this );
        }
    }

    @Override
    protected void sendNetMessageId( NetMessageId id )
    {
        XBNetMessageId mid = (XBNetMessageId)id;
        if ( mid != XBNetMessageId.Ping && mid != XBNetMessageId.PingAck )
            debugLog( "send message id " + mid );
        super.sendNetMessageId( mid );
    }

    private UserCommand receiveUserCommand()
    {
        int code = receiveInt();
        UserCommand res = new UserCommand( code );

        res.playerId = receiveInt();

        switch ( res.code )
        {
        //            case SWAP_VECTORS:
        //            case SET_VECTORS:
        //            case TROOPS_RESERVE:
            case SWAP_MARCH:
            case SET_MARCH:
                res.arg1 = receiveInt();
                res.cellX = receiveInt();
                res.cellY = receiveInt();
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
                res.cellX = receiveInt();
                res.cellY = receiveInt();
                break;

            case TROOPS_PARA:
            case MANAGED_TROOPS_PARA:
            case TROOPS_GUN:
            case MANAGED_TROOPS_GUN:
                res.cellX = receiveInt();
                res.cellY = receiveInt();
                res.arg1 = receiveInt();
                res.arg2 = receiveInt();
                break;

            default:
                throw new MVCModelError( "receiveUserCommand() : invalid command code " + res.code );
        }

        return res;
    }

    private void sendUserCommand( UserCommand cmd, int seq )
    {
        //        try
        //        {
        //            lock.lock();

        //        sendNetMessageId( XBNetMessageId.UserCommand );
        sendInt( seq );
        sendInt( cmd.code.ordinal() );
        sendInt( cmd.playerId );

        switch ( cmd.code )
        {
        //            case SWAP_VECTORS:
        //            case SET_VECTORS:
        //            case TROOPS_RESERVE:
            case SWAP_MARCH:
            case SET_MARCH:
                sendInt( cmd.arg1 );
                sendInt( cmd.cellX );
                sendInt( cmd.cellY );
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
                sendInt( cmd.cellX );
                sendInt( cmd.cellY );
                break;

            case TROOPS_PARA:
            case MANAGED_TROOPS_PARA:
            case TROOPS_GUN:
            case MANAGED_TROOPS_GUN:
                sendInt( cmd.cellX );
                sendInt( cmd.cellY );
                sendInt( cmd.arg1 );
                sendInt( cmd.arg2 );
                break;

            default:
                throw new MVCModelError( "sendUserCommand() : invalid command code " + cmd.code );
        }
        //        }
        //        finally
        //        {
        //            lock.unlock();
        //        }
    }

    private void sendUserCommandAck( int seq )
    {
        //        try
        //        {
        //            lock.lock();

        //        sendNetMessageId( XBNetMessageId.UserCommandAck );
        sendInt( seq );

        debugLog( " seq=" + seq );
        //        }
        //        finally
        //        {
        //            lock.unlock();
        //        }
    }

    private void sendGameNextStep( Boolean keepGaming )
    {
        //        try
        //        {
        //            lock.lock();

        //        sendNetMessageId( XBNetMessageId.GameNextStep );
        sendBoolean( keepGaming );
        //        }
        //        finally
        //        {
        //            lock.unlock();
        //        }
    }

    //    private void sendGameStateQuery()
    //    {
    //        //        try
    //        //        {
    //        //            lock.lock();
    //
    //        sendNetMessageId( XBNetMessageId.GameStateQuery );
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    private void receiveInitialisationParameters( InitialisationParameters ip )
    {
        receiveLongParameter( ip.getRandomSeed() );
        receiveIntParameter( ip.getBases() );
        receiveIntParameter( ip.getRandomBases() );
        receiveIntParameter( ip.getTowns() );
        receiveIntParameter( ip.getArmies() );
        receiveIntParameter( ip.getMilitia() );
        receiveIntParameter( ip.getSeaDensity() );
        receiveIntParameter( ip.getHillDensity() );
    }

    private void sendInitialisationParameters( InitialisationParameters ip )
    {
        sendLong( ip.getRandomSeed().getValue() );
        sendInt( ip.getBases().getValue() );
        sendInt( ip.getRandomBases().getValue() );
        sendInt( ip.getTowns().getValue() );
        sendInt( ip.getArmies().getValue() );
        sendInt( ip.getMilitia().getValue() );
        sendInt( ip.getSeaDensity().getValue() );
        sendInt( ip.getHillDensity().getValue() );
    }

    private void receiveBoardParameters( BoardParameters bp ) // throws NetException
    {
        bp.getXCellCount().setValue( receiveInt() );
        bp.getYCellCount().setValue( receiveInt() );
        receiveEnumParameter( bp.getWrappingMode() ); //, WrappingMode.class );
    }

    private void sendBoardParameters( BoardParameters bp )
    {
        sendInt( bp.getXCellCount().getValue() );
        sendInt( bp.getYCellCount().getValue() );
        sendEnum( bp.getWrappingMode().getValue() );
    }

    private void receiveGameParameters( GameParameters gp ) //throws NetException
    {
        // initialisation parameters

        receiveInitialisationParameters( gp.getInitialisationParameters() );

        // game parameters

        receiveIntParameter( gp.getTroopsMove() );
        receiveIntParameter( gp.getSupplyFarms() );
        receiveIntParameter( gp.getSupplyDecay() );
        receiveIntParameter( gp.getFightIntensity() );

        receiveBoolParameter( gp.getEnableBaseBuild() );
        receiveIntParameter( gp.getBaseBuildSteps() );
        receiveIntParameter( gp.getBaseBuildCost() );

        receiveBoolParameter( gp.getEnableBaseScuttle() );
        receiveIntParameter( gp.getBaseScuttleCost() );

        receiveBoolParameter( gp.getEnableFill() );
        receiveIntParameter( gp.getFillCost() );

        receiveBoolParameter( gp.getEnableDig() );
        receiveIntParameter( gp.getDigCost() );

        receiveIntParameter( gp.getHillSteepness() );

        receiveBoolParameter( gp.getAllowKeyRepeat() );
        receiveBoolParameter( gp.getEnableAttack() );
        receiveBoolParameter( gp.getEnableManagedCommands() );

        receiveBoolParameter( gp.getEnableParaTroops() );
        receiveIntParameter( gp.getParaTroopsRange() );
        receiveIntParameter( gp.getParaTroopsCost() );
        receiveIntParameter( gp.getParaTroopsDamage() );

        receiveBoolParameter( gp.getEnableGunTroops() );
        receiveIntParameter( gp.getGunTroopsRange() );
        receiveIntParameter( gp.getGunTroopsCost() );
        receiveIntParameter( gp.getGunTroopsDamage() );

        receiveEnumParameter( gp.getVisibilityMode() ); //, InvisibilityMode.class );
        receiveIntParameter( gp.getVisibilityRange() );
        receiveBoolParameter( gp.getVisibilityRememberCells() );
        receiveBoolParameter( gp.getVisibilityHideVectors() );

        receiveBoolParameter( gp.getEnableMarching() );
        receiveIntParameter( gp.getMarchSpeed() );

        // board parameters

        receiveBoardParameters( gp.getBoardParameters() );
    }

    private void sendGameParameters( GameParameters gp )
    {
        //        try
        //        {
        //            lock.lock();

        //        sendNetMessageId( XBNetMessageId.GameData );

        // initialisation parameters

        sendInitialisationParameters( gp.getInitialisationParameters() );

        // game parameters

        sendInt( gp.getTroopsMove().getValue() );
        sendInt( gp.getSupplyFarms().getValue() );
        sendInt( gp.getSupplyDecay().getValue() );
        sendInt( gp.getFightIntensity().getValue() );

        sendBool( gp.getEnableBaseBuild().getValue() );
        sendInt( gp.getBaseBuildSteps().getValue() );
        sendInt( gp.getBaseBuildCost().getValue() );

        sendBool( gp.getEnableBaseScuttle().getValue() );
        sendInt( gp.getBaseScuttleCost().getValue() );

        sendBool( gp.getEnableFill().getValue() );
        sendInt( gp.getFillCost().getValue() );

        sendBool( gp.getEnableDig().getValue() );
        sendInt( gp.getDigCost().getValue() );

        sendInt( gp.getHillSteepness().getValue() );

        sendBool( gp.getAllowKeyRepeat().getValue() );
        sendBool( gp.getEnableAttack().getValue() );
        sendBool( gp.getEnableManagedCommands().getValue() );

        sendBool( gp.getEnableParaTroops().getValue() );
        sendInt( gp.getParaTroopsRange().getValue() );
        sendInt( gp.getParaTroopsCost().getValue() );
        sendInt( gp.getParaTroopsDamage().getValue() );

        sendBool( gp.getEnableGunTroops().getValue() );
        sendInt( gp.getGunTroopsRange().getValue() );
        sendInt( gp.getGunTroopsCost().getValue() );
        sendInt( gp.getGunTroopsDamage().getValue() );

        sendEnum( gp.getVisibilityMode().getValue() );
        sendInt( gp.getVisibilityRange().getValue() );
        sendBool( gp.getVisibilityRememberCells().getValue() );
        sendBool( gp.getVisibilityHideVectors().getValue() );

        sendBool( gp.getEnableMarching().getValue() );
        sendInt( gp.getMarchSpeed().getValue() );

        // board parameters

        sendBoardParameters( gp.getBoardParameters() );

        //        if ( autoFlush )
        //            flush();
        //        }
        //        finally
        //        {
        //            lock.unlock();
        //        }
    }

    private PlayerState receivePlayerState()
    {
        PlayerState res = PlayerState.valueOf( receiveInt() );
        return res;
    }

    private void sendPlayerState( PlayerState cs )
    {
        sendInt( cs.ordinal() );
    }

    //    private PeerDTO receivePlayerStateDTO()
    //    {
    //        PeerDTO dto = new PeerDTO();
    //        dto.getColorId().value = receiveInt();
    //        dto.setData( receivePlayerState() );
    //        return dto;
    //    }

    //    private Pair<ColorId, PlayerState> receiveColorIdPlayerState()
    //    {
    //        return new Pair<ColorId, PlayerState>( receiveColorId(), receivePlayerState() );
    //    }

    private void receiveColorIdPlayerState( NetMessage nm )
    {
        nm.addData( receiveColorId() );
        nm.addData( receivePlayerState() );
    }

    //    private PeerDTO receivePlayerName() throws NetException
    //    {
    //        PeerDTO dto = new PeerDTO();
    //        dto.getColorId().value = receiveInt();
    //        dto.setData( receiveString() );
    //        return dto;
    //    }

    //    private Pair<ColorId, String> receivePlayerName() throws NetException
    //    {
    //        return new Pair<ColorId, String>( receiveColorId(), receiveString() );
    //    }

    private void receivePlayerName( NetMessage nm ) throws NetException
    {
        nm.addData( receiveColorId() );
        nm.addData( receiveNullableObject( String.class ) );
        //        nm.addData( receiveNullableObject() ); // String
    }

    //    private void sendPlayerName( PeerDTO name ) throws NetException
    //    {
    //        //        try
    //        //        {
    //        //            lock.lock();
    //
    //        //        sendNetMessageId( XBNetMessageId.PlayerName );
    //        sendInt( name.getColorId().value );
    //        sendString( (String)name.getData() );
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    //    private void sendPlayerName( Pair<ColorId, String> data ) throws NetException
    //    {
    //        sendColorId( data.getLeft() );
    //        sendString( data.getRight() );
    //    }

    private void sendPlayerName( NetMessage nm ) throws NetException
    {
        sendColorId( (ColorId)nm.getDataArray().get( 0 ) );
        //        sendString( (String)nm.getDataArray().get( 1 ) );
        sendNullableObject( nm.getDataArray().get( 1 ) ); //, String.class );
    }

    //    private void sendPlayerState( PeerDTO state )
    //    {
    //        //        try
    //        //        {
    //        //            lock.lock();
    //
    //        //        sendNetMessageId( XBNetMessageId.PlayerState );
    //        sendInt( state.getColorId().value );
    //        sendPlayerState( (PlayerState)state.getData() );
    //        //        }
    //        //        finally
    //        //        {
    //        //            lock.unlock();
    //        //        }
    //    }

    //    private void sendPlayerState( Pair<ColorId, PlayerState> data )
    //    {
    //        sendColorId( data.getLeft() );
    //        sendPlayerState( data.getRight() );
    //    }

    private void sendPlayerState( NetMessage nm )
    {
        sendColorId( (ColorId)nm.getDataArray().get( 0 ) );
        sendPlayerState( (PlayerState)nm.getDataArray().get( 1 ) );
    }

    private void receiveCellState( CellState cs )
    {
        cs.playerId = receiveInt();
        for ( int i = 0; i < cs.troopsLevel.length; i++ )
            cs.troopsLevel[ i ] = receiveFloat();
    }

    private void sendCellState( CellState cs )
    {
        sendInt( cs.playerId );
        //sendInt( cs.troopsLevel.length );
        for ( float tl : cs.troopsLevel )
            sendFloat( tl );
    }

    private BoardState receiveBoardState()
    {
        int xDim = receiveInt();
        int yDim = receiveInt();
        BoardState res = new BoardState( xDim, yDim );

        int playerCount = receiveInt();
        for ( Cell c : res )
            c.cellState.setPlayerCount( playerCount );

        for ( Cell c : res )
            //c.cellState.playerId = receiveInt();
            receiveCellState( c.cellState );

        return res;
    }

    private void sendBoardState( BoardState bs )
    {
        // dimx, dimy
        sendInt( bs.getBoardXDim() );
        sendInt( bs.getBoardYDim() );

        sendInt( bs.getCell( 0, 0 ).cellState.troopsLevel.length ); // player count

        for ( Cell c : bs )
            //sendInt( c.cellState.playerId );
            sendCellState( c.cellState );
    }

    private GameState receiveGameState()
    {
        GameState res = new GameState();

        // current client state

        res.setCurrentPlayerState( receivePlayerState() );

        // players stats

        int psCount = receiveInt();
        for ( int i = 0; i < psCount; i++ )
        {
            int pid = receiveInt();
            PlayerStatistics ps = new PlayerStatistics( pid );
            double cov = receiveDouble();
            ps.setCoveragePercent( cov );

            res.getPlayerStatistics().add( ps );
        }

        // full board state

        if ( MainModel.boardStateFullCheck )
            res.setBoardState( receiveBoardState() );

        return res;
    }

    private void sendGameState( GameState gs )
    {
        //        try
        //        {
        //            lock.lock();

        //        sendNetMessageId( XBNetMessageId.GameState );

        // current client state

        sendPlayerState( gs.getCurrentPlayerState() );

        // stats

        sendInt( gs.getPlayerStatistics().size() );
        for ( PlayerStatistics ps : gs.getPlayerStatistics() )
        {
            sendInt( ps.getPlayerId() );
            sendDouble( ps.getCoveragePercent() );
        }

        // full board state

        if ( MainModel.boardStateFullCheck )
            sendBoardState( gs.getBoardState() );
        //        }
        //        finally
        //        {
        //            lock.unlock();
        //        }
    }

    //    private XBNetMessage receiveNetMessage() throws SocketTimeoutException, EOFException, IOException
    //    {
    //        XBNetMessage res = null;
    //
    //        XBNetMessageId mid = receiveNetMessageId();
    //
    //        if ( mid != XBNetMessageId.Ping )
    //            res = new XBNetMessage( mid );
    //
    //        if ( mid != XBNetMessageId.Ping && mid != XBNetMessageId.PingAck )
    //            debugLog( "received message " + mid );
    //
    //        switch ( mid )
    //        {
    //            case PlayerGranted:
    //                ColorId id = new ColorId( receiveInt( true ) );
    //
    //                res.setData( id );
    //                break;
    //
    //            case ClientName:
    //                res.setData( receiveString( true ) );
    //                break;
    //
    //            case PlayerId:
    //                PeerDTO dto = new PeerDTO();
    //                dto.getColorId().value = receiveInt( true );
    //                dto.setData( receiveInteger( true ) ); // player id
    //                res.setData( dto );
    //                break;
    //
    //            case PlayerState:
    //                res.setData( receivePlayerStateDTO() );
    //                break;
    //
    //            case PlayerName:
    //                res.setData( receivePlayerName() );
    //                break;
    //
    //            case RequestPlayer:
    //                id = new ColorId( receiveInteger( true ) );
    //                res.setData( id );
    //                break;
    //
    //            case GameData:
    //                GameParameters gp = new GameParameters();
    //                receiveGameParameters( gp );
    //                res.setData( gp );
    //                break;
    //
    //            case UserCommand:
    //                res.setSequence( receiveSequence() );
    //                res.setData( receiveUserCommand() );
    //                break;
    //
    //            case UserCommandAck:
    //                res.setSequence( receiveSequence() );
    //                break;
    //
    //            case DoBoardUpdate:
    //                res.setSequence( receiveSequence() );
    //                break;
    //
    //            case BoardUpdateAck:
    //                res.setSequence( receiveSequence() );
    //                break;
    //
    //            case GameState:
    //                res.setData( receiveGameState() );
    //                break;
    //
    //            case GameNextStep:
    //                res.setData( receiveBoolean( true ) );
    //                break;
    //
    //            case Ping:
    //                // answer at once
    //                int seq = receiveSequence();
    //                debugLog( "received Ping seq=" + seq );
    //                sendPingAck( seq );
    //                break;
    //
    //            case PingAck:
    //                seq = receiveSequence();
    //                debugLog( "received PingAck seq=" + seq );
    //                res.setSequence( seq );
    //                break;
    //
    //            default:
    //                break;
    //        }
    //
    //        return res;
    //    }

    //    @Override
    //    public void receiveNetMessages() throws SocketTimeoutException, EOFException, IOException
    //    {
    //        while ( true )
    //        {
    //            try
    //            {
    //                lock.lock();
    //
    //                XBNetMessage nm = receiveNetMessage();
    //                if ( nm != null )
    //                    netMessages.add( nm );
    //            }
    //            finally
    //            {
    //                lock.unlock();
    //            }
    //        }
    //    }

    //    @Override
    //    public XBNetMessage hasNetMessage()
    //    {
    //        if ( netMessages.size() > 0 )
    //            return netMessages.remove( 0 );
    //
    //        return null;
    //    }

    /**
     * send a "ping" message to peer, to verify liveness
     * @throws IOException 
     * @throws EOFException 
     */
    //    private void sendPing( int seq )
    //    {
    //        //channelUtil.directSendInt( NetMessageId.Ping.ordinal() );
    //        //        sendNetMessageId( XBNetMessageId.Ping );
    //        debugLog( "send message Ping seq=" + seq );
    //        sendInt( seq );
    //    }

    /**
     * send answer to "ping" message
     * @throws IOException 
     * @throws EOFException 
     */
    private void sendPingAck( int seq )
    {
        //channelUtil.directSendInt( NetMessageId.PingAck.ordinal() );
        //        sendNetMessageId( XBNetMessageId.PingAck );
        debugLog( "send message PingAck seq=" + seq );
        sendInt( seq );
    }

    private void debugLog( String m )
    {
        if ( debugLog != null )
            debugLog.log( "[XBPeer] " + toString() + " thread id=" + Thread.currentThread().getId() + " " + m );
    }

    //    @Override
    //    protected Object receiveObject( Class<?> clazz ) throws NetException
    //    {
    //        throw new NetException( "receiveNullable : unhandled " + clazz, this );
    //    }
    //
    //    @Override
    //    protected void sendObject( Object o, Class<?> clazz ) throws NetException
    //    {
    //        throw new NetException( "sendNullable : unhandled " + clazz, this );
    //    }

    //    @Override
    //    public String toString()
    //    {
    //        if ( channelUtil == null )
    //            return "<not connected>";
    //
    //        if ( logLocalPort )
    //            return channelUtil.toStringLocalPort();
    //
    //        return channelUtil.toString();
    //    }

    //    public void setDebugLog( DebugLog dl )
    //    {
    //        debugLog = dl;
    //        channelUtil.setDebugLog( dl );
    //    }
}
