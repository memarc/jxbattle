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

package jxbattle.model.client;

import jxbattle.bean.common.game.GameState;
import jxbattle.bean.common.game.UserCommand;
import jxbattle.bean.common.message.XBNetMessage;
import jxbattle.bean.common.message.XBNetMessageId;
import jxbattle.bean.common.player.ColorId;
import jxbattle.bean.common.player.PlayerState;
import jxbattle.common.net.XBPeer;
import jxbattle.model.MainModel;
import jxbattle.model.common.DebugLog;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.game.GameModel;
import jxbattle.model.common.game.PlayerInfos.SelectedPlayerIterator;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.net.ClientNetEngine;
import org.generic.net.NetMessage;
import org.generic.net.NetModelChangeId;
import org.generic.net.NetPeer;

//public class XBClientSideConnectionModel extends MVCModelImpl implements MVCModelObserver
public class XBClientNetEngine extends ClientNetEngine implements MVCModelObserver
{
    private ClientEngine clientEngine;

    private GameModel gameModel;

    //private SystemParametersModel systemParametersModel;

    //    /**
    //     * list of pending input network messages
    //     */
    //    private List<XBNetMessage> inputMessages;
    //
    //    /**
    //     * list of pending output network messages
    //     */
    //    private List<XBNetMessage> outputMessages;

    /**
     * automatically flush output messages
     */
    //private boolean autoFlush;

    /**
     * console log facility
     */
    private DebugLog debugLog;

    XBClientNetEngine( ClientEngine ce/*SystemParametersModel sp,GameModel gm */)
    {
        //systemParametersModel = sp;
        clientEngine = ce;
        gameModel = clientEngine.getGameModel();

        subscribeModel();
    }

    @Override
    protected NetPeer createPeer()
    {
        return new XBPeer();
    }

    @Override
    protected void processInputNetMessage( NetMessage nm )
    {
        //        debugLog( "processInputNetMessage" + nm );

        if ( nm.getMessageId() instanceof XBNetMessageId )
            switch ( (XBNetMessageId)nm.getMessageId() )
            {
            //            case PlayerAdded:
            //                PlayerInfo pi = (PlayerInfo)nm.getData();
            //                logMessage( "server added player " + pi.getPlayerColor().toString() );
            //                gameModel.getPlayersModel().addPlayer( this, pi );
            //                break;

                case PlayerNameChanged:
                //                    PeerDTO dto = (PeerDTO)nm.getData();
                //                    ColorId cid = dto.getColorId();
                //                    gameModel.getPlayersModel().setPlayerName( this, cid, (String)dto.getData() );
                {
                    //Pair<ColorId, String> data = (Pair<ColorId, String>)nm.getData();
                    ColorId cid = (ColorId)nm.getDataArray().get( 0 );
                    String name = (String)nm.getDataArray().get( 1 );
                    //                    gameModel.getPlayersModel().setPlayerName( this, data.getLeft(), data.getRight() );
                    gameModel.getPlayersModel().setPlayerName( this, cid, name );
                    break;
                }

                case PlayerId:
                //                    dto = (PeerDTO)nm.getData();
                //                    cid = dto.getColorId();
                //                    int pid = ((Integer)dto.getData()).intValue();
                //                    PlayerInfoModel changedPlayer = gameModel.getPlayersModel().setPlayerId( this, cid, pid );
                //                    logMessage( "color " + changedPlayer.getPlayerColor() + " set with player id " + changedPlayer.getPlayerId() );
                //                   
                {
                    //Pair<ColorId, Integer> data = (Pair<ColorId, Integer>)nm.getData();
                    ColorId cid = (ColorId)nm.getDataArray().get( 0 );
                    Integer playerId = (Integer)nm.getDataArray().get( 1 );

                    //                    PlayerInfoModel changedPlayer = gameModel.getPlayersModel().setPlayerId( this, data.getLeft(), data.getRight() );
                    PlayerInfoModel changedPlayer = gameModel.getPlayersModel().setPlayerId( this, cid, playerId );
                    logMessage( "color " + changedPlayer.getPlayerColor() + " set with player id " + changedPlayer.getPlayerId() );
                    break;
                }

                default:
                    // forward unprocessed message to observers
                    notifyObservers( new MVCModelChange( this, this, NetModelChangeId.NetMessageReceived, nm ) );
            }
    }

    //    @Override
    //    public XBPeer getPeer()
    //    {
    //        return (XBPeer)super.getPeer();
    //    }

    //    void doFlushOutputMessages() throws EOFException, IOException
    //    {
    //        while ( outputMessages.size() > 0 )
    //            doSendNetMessage();
    //    }

    //    private void logMessage( String s )
    //    {
    //        Global.logMessageModel.infoMessage( this, s );
    //    }
    //
    //    //    private void logMessage( String s, Exception e )
    //    //    {
    //    //        clientModel.getLogMessageModel().logMessage( this, s, e );
    //    //    }

    void sendClientName( String name )
    {
        //        getPeer().sendClientName( name );
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.ClientName );
        nm.addData( name );
        enqueueOutputMessage( nm );
    }

    void requestPlayableColors()
    {
        //        getPeer().requestPlayableColors();
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.GetAllPlayers );
        enqueueOutputMessage( nm );
    }

    void sendPlayerAllocationRequest( ColorId id )
    {
        //        getPeer().sendPlayerAllocationRequest( id );
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.RequestPlayer );
        nm.addData( id );
        enqueueOutputMessage( nm );

    }

    void sendPlayerCancellation()
    {
        //        getPeer().sendPlayerCancellation();
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.CancelPlayer );
        enqueueOutputMessage( nm );
    }

    void sendGetGameData()
    {
        //        getPeer().sendGetGameData();
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.GetGameData );
        enqueueOutputMessage( nm );
    }

    void sendReadyGaming()
    {
        //        getPeer().sendReadyGaming();
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.ReadyGaming );
        enqueueOutputMessage( nm );
    }

    void sendStartedGame()
    {
        //        getPeer().sendStartedGame();
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.StartedGame );
        enqueueOutputMessage( nm );
    }

    private void sendUserCommand( UserCommand cmd )
    {
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.UserCommand );
        nm.addData( cmd );
        enqueueOutputMessage( nm );
    }

    void sendBoardUpdateAck( int seq )
    {
        //     debugLog( "send net message BoardUpdateAck seq=" + seq );

        XBNetMessage nm = new XBNetMessage( XBNetMessageId.BoardUpdateAck );
        nm.setCommandSequence( seq );
        enqueueOutputMessage( nm );
    }

    void sendUserCommandAck( int seq )
    {
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.UserCommandAck );
        //        nm.setPeer( getPeer() );
        nm.setCommandSequence( seq );
        enqueueOutputMessage( nm );
    }

    void sendGameState( PlayerState playerState )
    {
        XBNetMessage nm = new XBNetMessage( XBNetMessageId.GameState );
        //        nm.setPeer( getPeer() );

        GameState gs = new GameState();

        gs.setCurrentPlayerState( playerState );

        SelectedPlayerIterator it = gameModel.getPlayersModel().selectedPlayers();
        while ( it.hasNext() )
        {
            int pid = it.next().getPlayerId();
            PlayerStatisticsModel psm = gameModel.getGameStatisticsModel().getPlayerStatisticsModel( pid );
            gs.getPlayerStatistics().add( psm.getPlayerStatistics() );
        }
        //        it.close();

        if ( MainModel.boardStateFullCheck )
            gs.setBoardState( gameModel.getBoardStateModel().getBoardState() );

        nm.addData( gs );

        enqueueOutputMessage( nm );
    }

    //    private void flushOutputs() throws IOException
    //    {
    //        if ( serverPeer != null )
    //            serverPeer.flushOutputBuffer();
    //    }

    public void setDebugLog( DebugLog dl )
    {
        debugLog = dl;
    }

    private void debugLog( String m )
    {
        if ( debugLog != null )
            debugLog.log( clientEngine.toString() + " : " + m );
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( MVCModelChange change )
    {
        switch ( (XBMVCModelChangeId)change.getChangeId() )
        {
            case UserCommand:
                sendUserCommand( (UserCommand)change.getData() );
                break;

            default:
                break;
        }
    }

    @Override
    public void subscribeModel()
    {
        gameModel.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        gameModel.removeObserver( this );
    }

    //    @Override
    //    public void close()
    //    {
    //        unsubscribeModel();
    //    }
}
