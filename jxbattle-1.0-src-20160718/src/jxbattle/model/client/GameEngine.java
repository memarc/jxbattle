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

import java.awt.Point;
import java.util.Iterator;

import jxbattle.bean.common.game.Cell;
import jxbattle.bean.common.game.CellMove;
import jxbattle.bean.common.game.CellState;
import jxbattle.bean.common.game.UserCommand;
import jxbattle.bean.common.game.UserCommand.UserCommandCode;
import jxbattle.bean.common.parameters.game.GameParameters;
import jxbattle.bean.common.parameters.game.WrappingMode;
import jxbattle.bean.common.player.XBColor;
import jxbattle.common.Consts;
import jxbattle.model.common.DebugLog;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.game.GameModel;
import jxbattle.model.common.game.PlayerInfos.SelectedPlayerIterator;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.thread.ThreadUtils;

public class GameEngine extends MVCModelImpl implements MVCModelObserver
{
    //private BoardPanelController boardPanelController;

    private GameModel gameModel;

    private GameUpdater gameUpdater;

    private PlayerInfoModel clientPlayer;

    private boolean watchGame;

    private RandomGen randomGen;

    private UserCommand lastUserCommand;

    private DebugLog debugLog;

    // precomputed values

    private GameParameters gameParameters;

    private int buildSteps;

    //    private int xCellCount; // x board size

    //    private int yCellCount; // y board size

    /**
     * this client's player id
     */
    private int clientPlayerId;

    //public GameEngine( GameModel gm, PlayerInfoModel cp, BoardPanelController bpc )
    //public GameEngine( GameModel gm, PlayerInfoModel cp )
    public GameEngine( GameModel gm )
    {
        gameModel = gm;
        //clientPlayer = cp;
        //boardPanelController = bpc;
        watchGame = false;

        init();
    }

    private void precompute()
    {
        buildSteps = gameParameters.getBaseBuildSteps().getValue();
    }

    private void init()
    {
        randomGen = gameModel.getRandomGenerator();
        gameParameters = gameModel.getGameParametersModel().getGameParameters();

        //        int playerCount = gameModel.getPlayersModel().getSelectedPlayerCount();
        //        gameModel.getGameStatisticsModel().initialiseStatistics( this, playerCount );
        gameModel.getGameStatisticsModel().initialiseStatistics( this );

        gameUpdater = new GameUpdater( this, gameModel.getBoardStateModel(), gameParameters, randomGen );

        precompute();
        setClientPlayer( gameModel.getClientPlayer() );

        subscribeModel();
    }

    private void setClientPlayer( PlayerInfoModel cp )
    {
        //clientPlayer = gameModel.getClientPlayer();
        clientPlayer = cp;
        clientPlayerId = clientPlayer.getPlayerId();
    }

    public boolean isMyCell( int cx, int cy )
    {
        return gameModel.getBoardStateModel().getCell( cx, cy ).cellState.playerId == clientPlayerId;
    }

    //    public void newGame( ClientEngine ce )
    //    {
    //        clientEngine = ce;
    //        //  gameModel.newGame();
    //    }

    //    void run()
    //    {
    //        gameFrameController = new GameFrameController();
    //        gameFrameController.setModel( this );
    //        gameFrameController.run();
    //    }

    private void redrawBoard()
    {
        //System.out.println( "GameEngine.redrawBoard " + Thread.currentThread() );
        //        long start = System.nanoTime();
        notifyObservers( new MVCModelChange( this, this, XBMVCModelChangeId.GameBoardUpdated ) );
        //        MainModel.printDur( "gameengine.redrawboard ms=", start, 1.0f );
        ThreadUtils.sleep( 0, 100 ); // let EDT work (better than Thread.yield())
    }

    public void computeStep()
    {

        //        long start = System.nanoTime();
        gameUpdater.computeStep( clientPlayerId, watchGame );
        //        MainModel.printDur( "gameengine.computestep ms=", start, 0.2f );

        updateStatistics();

        redrawBoard();

        //debugLog( "board update #" + boardUpdateCounter + " dur= " + (System.currentTimeMillis() - t1) );
        //boardUpdateCounter++;
        //debugLog( "computestep = " + (dur1 / 1000.0f) + " us, redraw = " + (dur2 / 1000.0f) + " us, tot = " + ((dur1 + dur2) / 1000) + " us" );
    }

    public void setDebugLog( DebugLog dl )
    {
        debugLog = dl;
    }

    private void debugLog( String m )
    {
        if ( debugLog != null )
            debugLog.log( m );
    }

    //    void countDown()
    //    {
    //        gameFrameController.countDown();
    //    }

    //    void abortGame()
    //    {
    //        if ( gameFrameController != null )
    //        {
    //            gameFrameController.close();
    //            gameFrameController = null;
    //        }
    //    }

    private void startManagedCommand( Cell cell, UserCommand cmd )
    {
        if ( cell.hasManagedCommand() == null )
        {
            UserCommand uc = new UserCommand( cmd );
            switch ( cmd.code )
            {
                case MANAGED_FILL_GROUND:
                case MANAGED_DIG_GROUND:
                case MANAGED_TROOPS_PARA:
                case MANAGED_TROOPS_GUN:
                    uc.ttl = -1;
                    break;

                case MANAGED_BUILD_BASE:
                case MANAGED_SCUTTLE_BASE:
                    uc.ttl = buildSteps;
                    break;

                default:
                    throw new MVCModelError( "unprocessed managed command " + cmd );
            }
            cell.commands.add( uc );
        }
    }

    private static void startUnmanagedCommand( Cell sourceCell, UserCommand cmd )
    {
        UserCommand uc = new UserCommand( cmd );
        uc.ttl = 1;
        sourceCell.commands.add( uc );
    }

    public void cancelManagedCommands( Cell cell )
    {
        Iterator<UserCommand> it = cell.commands.iterator();
        while ( it.hasNext() )
        {
            switch ( it.next().code )
            {
                case MANAGED_FILL_GROUND:
                case MANAGED_DIG_GROUND:
                case MANAGED_BUILD_BASE:
                case MANAGED_SCUTTLE_BASE:
                case MANAGED_TROOPS_PARA:
                case MANAGED_TROOPS_GUN:
                    it.remove();
                    break;

                default:
                    break;
            }
        }
    }

    private static void setTroopsReserve( Cell cell, int level )
    {
        CellState cs = cell.cellState;
        cs.reserveLevel = Consts.maxTroopsLevel / 10.0f * level;
    }

    private static void attackCell( Cell cell, int attackerId )
    {
        CellState cs = cell.cellState;

        for ( CellMove cm : cs.moves )
        {
            if ( cm.targetCell != null ) // border cell have some target cell set to null (no wrap mode)
            {
                CellState dst = cm.targetCell.cellState;

                for ( CellMove dcm : dst.moves )
                {
                    if ( dst.playerId != Consts.NullId && dst.playerId == attackerId )
                        dcm.isSet = dcm.dir == cm.revDir;
                }
            }
        }
    }

    //    public Point getDestinationCell( int sourceCellX, int sourceCellY, int cellMouseDist, int cellMouseAngle, int range )
    //    {
    //        return gameUpdater.getDestinationCell( sourceCellX, sourceCellY, cellMouseDist, cellMouseAngle, range );
    //    }

    public Point getDestinationCell( int sourceCellX, int sourceCellY, int cellMouseDist, int cellMouseAngle, int range )
    {
        if ( cellMouseDist > 100 )
            cellMouseDist = 100;
        double d = cellMouseDist * range / 30.0;
        double a = cellMouseAngle * Math.PI / 180.0;

        int dx = (int)Math.round( d * Math.cos( a ) );
        int dy = (int)Math.round( d * Math.sin( a ) );
        if ( dx == 0 && dy == 0 )
            return null; // destination must be different from source

        return new Point( sourceCellX + dx, sourceCellY + dy );
    }

    public boolean isCellValid( int cx, int cy, WrappingMode wrapMode, int xCellCount, int yCellCount )
    {
        //        WrappingMode wrapMode = (WrappingMode)gameParameters.getBoardParameters().getWrappingMode().getValue();
        boolean res;
        switch ( wrapMode )
        {
            case NONE:
                res = cx >= 0 && cy >= 0 && cx < xCellCount && cy < yCellCount;
                break;

            case LEFT_RIGHT:
                res = cy >= 0 && cy < yCellCount;
                break;

            case TOP_DOWN:
                res = cx >= 0 && cx < xCellCount;
                break;

            case FULL:
                res = true;
                break;

            default:
                throw new MVCModelError( "invalid wrap mode " + wrapMode );
        }

        return res && !gameModel.getBoardStateModel().getCell( cx, cy ).cellState.isSeaCell();
    }

    //    /**
    //     * process para troops user command
    //     */
    //    private void paraTroops( Cell sourceCell, int cellMouseDist, int cellMouseAngle )
    //    {
    //        int range = gameParameters.getParaTroopsRange().getValue();
    //
    //        Cell targetCell = getDestinationCell( sourceCell, cellMouseDist, cellMouseAngle, range );
    //        if ( targetCell != null )
    //        {
    //            CellState cs = sourceCell.cellState;
    //            CellState tcs = targetCell.cellState;
    //
    //            //if ( cs.getTroopsLevel() >= paraCost )
    //            if ( cs.getTroopsLevel() >= paraCost + cs.reserveLevel )
    //            {
    //                cs.subTroopsLevel( paraCost );
    //                if ( !tcs.isSeaCell() ) // not sea ?
    //                {
    //                    // attach pending command to target cell
    //
    //                    UserCommand uc = new UserCommand( UserCommandCode.TROOPS_PARA );
    //                    uc.playerId = cs.playerId;
    //                    uc.ttl = 1;
    //                    targetCell.commands.add( uc );
    //                }
    //            }
    //        }
    //    }

    //    /**
    //     * process gun troops user command
    //     */
    //    private void gunTroops( Cell sourceCell, int cellMouseDist, int cellMouseAngle )
    //    {
    //        int range = gameParameters.getGunTroopsRange().getValue();
    //
    //        Cell targetCell = getDestinationCell( sourceCell, cellMouseDist, cellMouseAngle, range );
    //        if ( targetCell != null )
    //        {
    //            CellState cs = sourceCell.cellState;
    //            CellState tcs = targetCell.cellState;
    //
    //            //if ( cs.getTroopsLevel() >= gunCost )
    //            if ( cs.getTroopsLevel() >= gunCost + cs.reserveLevel )
    //            {
    //                cs.subTroopsLevel( gunCost );
    //                if ( !tcs.isSeaCell() && !tcs.fight ) // not sea and no fight ?
    //                {
    //                    // attach pending command to target cell
    //
    //                    UserCommand uc = new UserCommand( UserCommandCode.TROOPS_GUN );
    //                    uc.playerId = cs.playerId;
    //                    uc.ttl = 1;
    //                    targetCell.commands.add( uc );
    //                }
    //            }
    //        }
    //    }

    /*
    cell zones on mouse click
    +-------------------+
    |     \   3   /     |
    |   2  \     /  4   |
    |\__    \___/    __/|
    |   \__ /   \ __/   |
    | 1    |  0  |   5  |
    |    __|     |__    |
    | __/   \___/   \__ |
    |/      /   \      \|
    |  8   /     \  6   |
    |     /   7   \     |
    +-------------------+
    */

    public void issueCellUserCommand( UserCommandCode ucc, int cellX, int cellY )
    {
        lastUserCommand = new UserCommand( ucc );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;

        sendUserCommand( lastUserCommand );

    }

    public void repeatLastUserCommand( int cellX, int cellY )
    {
        if ( lastUserCommand != null )
        {
            lastUserCommand.cellX = cellX;
            lastUserCommand.cellY = cellY;
            sendUserCommand( lastUserCommand );
        }
    }

    //    public void swapVectorsUserCommand( int cellX, int cellY, int zone )
    //    {
    //        lastUserCommand = new UserCommand( UserCommandCode.SWAP_VECTORS );
    //        lastUserCommand.cellX = cellX;
    //        lastUserCommand.cellY = cellY;
    //        lastUserCommand.arg1 = zone;
    //
    //        sendUserCommand( lastUserCommand );
    //    }

    //    public void cancelAllVectorsUserCommand( int cellX, int cellY )
    //    {
    //        lastUserCommand = new UserCommand( UserCommandCode.CANCEL_VECTORS );
    //        lastUserCommand.cellX = cellX;
    //        lastUserCommand.cellY = cellY;
    //
    //        sendUserCommand( lastUserCommand );
    //    }

    //    /**
    //     * set one or more vectors and cancel others
    //     */
    //    public void setVectorsUserCommand( int cellX, int cellY, int zone )
    //    {
    //        lastUserCommand = new UserCommand( UserCommandCode.SET_VECTORS );
    //        lastUserCommand.cellX = cellX;
    //        lastUserCommand.cellY = cellY;
    //        lastUserCommand.arg1 = zone;
    //
    //        sendUserCommand( lastUserCommand );
    //    }

    public void setMarchUserCommand( int cellX, int cellY, int zone )
    {
        lastUserCommand = new UserCommand( UserCommandCode.SET_MARCH );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;
        lastUserCommand.arg1 = zone;

        sendUserCommand( lastUserCommand );
    }

    public void swapMarchUserCommand( int cellX, int cellY, int zone )
    {
        lastUserCommand = new UserCommand( UserCommandCode.SWAP_MARCH );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;
        lastUserCommand.arg1 = zone;

        sendUserCommand( lastUserCommand );
    }

    public void cancelAllMarchUserCommand( int cellX, int cellY )
    {
        lastUserCommand = new UserCommand( UserCommandCode.CANCEL_MARCH );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;

        sendUserCommand( lastUserCommand );
    }

    public void stopMarchUserCommand( int cellX, int cellY )
    {
        lastUserCommand = new UserCommand( UserCommandCode.STOP_MARCH );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;

        sendUserCommand( lastUserCommand );
    }

    /**
     * issue fill ground command
     */
    public void fillGroundUserCommand( int cellX, int cellY, boolean managed )
    {
        if ( managed )
            lastUserCommand = new UserCommand( UserCommandCode.MANAGED_FILL_GROUND );
        else
            lastUserCommand = new UserCommand( UserCommandCode.FILL_GROUND );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;

        sendUserCommand( lastUserCommand );
    }

    /**
     * issue dig ground command
     */
    public void digGroundUserCommand( int cellX, int cellY, boolean managed )
    {
        if ( managed )
            lastUserCommand = new UserCommand( UserCommandCode.MANAGED_DIG_GROUND );
        else
            lastUserCommand = new UserCommand( UserCommandCode.DIG_GROUND );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;

        sendUserCommand( lastUserCommand );
    }

    /**
     * issue build base command
     */
    public void buildBaseUserCommand( int cellX, int cellY, boolean managed )
    {
        if ( managed )
            lastUserCommand = new UserCommand( UserCommandCode.MANAGED_BUILD_BASE );
        else
            lastUserCommand = new UserCommand( UserCommandCode.BUILD_BASE );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;

        sendUserCommand( lastUserCommand );
    }

    /**
     * issue scuttle base command
     */
    public void scuttleBaseUserCommand( int cellX, int cellY, boolean managed )
    {
        if ( managed )
            lastUserCommand = new UserCommand( UserCommandCode.MANAGED_SCUTTLE_BASE );
        else
            lastUserCommand = new UserCommand( UserCommandCode.SCUTTLE_BASE );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;

        sendUserCommand( lastUserCommand );
    }

    //    /**
    //     * issue reserve command
    //     */
    //    public void troopsReserveUserCommand( int cellX, int cellY, int level )
    //    {
    //        lastUserCommand = new UserCommand( UserCommandCode.TROOPS_RESERVE );
    //        lastUserCommand.cellX = cellX;
    //        lastUserCommand.cellY = cellY;
    //        lastUserCommand.arg1 = level;
    //
    //        sendUserCommand( lastUserCommand );
    //    }

    /**
     * issue attack command
     */
    public void cellAttackUserCommand( int cellX, int cellY )
    {
        lastUserCommand = new UserCommand( UserCommandCode.ATTACK_CELL );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;

        sendUserCommand( lastUserCommand );
    }

    /**
     * issue para troops command
     */
    public void cellParaUserCommand( int cellX, int cellY, int cellMouseDist, int cellMouseAngle, boolean managed )
    {
        if ( managed )
            lastUserCommand = new UserCommand( UserCommandCode.MANAGED_TROOPS_PARA );
        else
            lastUserCommand = new UserCommand( UserCommandCode.TROOPS_PARA );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;
        lastUserCommand.arg1 = cellMouseDist;
        lastUserCommand.arg2 = cellMouseAngle;

        sendUserCommand( lastUserCommand );
    }

    /**
     * issue gun troops command
     */
    public void cellGunUserCommand( int cellX, int cellY, int cellMouseDist, int cellMouseAngle, boolean managed )
    {
        if ( managed )
            lastUserCommand = new UserCommand( UserCommandCode.MANAGED_TROOPS_GUN );
        else
            lastUserCommand = new UserCommand( UserCommandCode.TROOPS_GUN );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;
        lastUserCommand.arg1 = cellMouseDist;
        lastUserCommand.arg2 = cellMouseAngle;

        sendUserCommand( lastUserCommand );
    }

    /**
     * issue 'cancel management' command
     */
    public void cancelManagementUserCommand( int cellX, int cellY )
    {
        lastUserCommand = new UserCommand( UserCommandCode.CANCEL_MANAGE );
        lastUserCommand.cellX = cellX;
        lastUserCommand.cellY = cellY;

        sendUserCommand( lastUserCommand );
    }

    private boolean isUserCommandAllowed( UserCommand cmd )
    {
        Cell cell = gameModel.getBoardStateModel().getCell( cmd.cellX, cmd.cellY );
        CellState cs = cell.cellState;
        boolean mine = cs.playerId == cmd.playerId;
        boolean opponent = !mine && cs.playerId != Consts.NullId;

        switch ( cmd.code )
        {
        //            case SWAP_VECTORS:
        //            case SET_VECTORS:
            case SET_VECTOR_U:
            case SET_VECTOR_UR:
            case SET_VECTOR_R:
            case SET_VECTOR_DR:
            case SET_VECTOR_D:
            case SET_VECTOR_DL:
            case SET_VECTOR_L:
            case SET_VECTOR_UL:

            case SWAP_VECTOR_U:
            case SWAP_VECTOR_UR:
            case SWAP_VECTOR_R:
            case SWAP_VECTOR_DR:
            case SWAP_VECTOR_D:
            case SWAP_VECTOR_DL:
            case SWAP_VECTOR_L:
            case SWAP_VECTOR_UL:

            case CANCEL_VECTORS:
                return mine;

            case FILL_GROUND:
                return mine && !cs.fight && gameParameters.getEnableFill().getValue();

            case MANAGED_FILL_GROUND:
                return mine && !cs.fight && gameParameters.getEnableFill().getValue() && gameParameters.getEnableManagedCommands().getValue();

            case DIG_GROUND:
                return mine && !cs.fight && gameParameters.getEnableDig().getValue();

            case MANAGED_DIG_GROUND:
                return mine && !cs.fight && gameParameters.getEnableDig().getValue() && gameParameters.getEnableManagedCommands().getValue();

            case BUILD_BASE:
                return mine && !cs.fight && gameParameters.getEnableBaseBuild().getValue();

            case MANAGED_BUILD_BASE:
                return mine && !cs.fight && gameParameters.getEnableBaseBuild().getValue() && gameParameters.getEnableManagedCommands().getValue();

            case SCUTTLE_BASE:
                return mine && !cs.fight && gameParameters.getEnableBaseScuttle().getValue();

            case MANAGED_SCUTTLE_BASE:
                return mine && !cs.fight && gameParameters.getEnableBaseScuttle().getValue() && gameParameters.getEnableManagedCommands().getValue();

            case CANCEL_MANAGE:
                return mine && gameParameters.getEnableManagedCommands().getValue();

                //            case TROOPS_RESERVE:
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
                return mine && !cs.fight && gameParameters.getEnableBaseScuttle().getValue();

            case ATTACK_CELL:
                return opponent && gameParameters.getEnableAttack().getValue();

            case TROOPS_PARA:
                return mine && !cs.fight && gameParameters.getEnableParaTroops().getValue();

            case MANAGED_TROOPS_PARA:
                return mine && !cs.fight && gameParameters.getEnableParaTroops().getValue() && gameParameters.getEnableManagedCommands().getValue();

            case TROOPS_GUN:
                return mine && !cs.fight && gameParameters.getEnableGunTroops().getValue();

            case MANAGED_TROOPS_GUN:
                return mine && !cs.fight && gameParameters.getEnableGunTroops().getValue() && gameParameters.getEnableManagedCommands().getValue();

            case SWAP_MARCH:
            case SET_MARCH:
                //                return mine || cs.playerId == Consts.NullId;
            case STOP_MARCH:
                return gameParameters.getEnableMarching().getValue();

            case CANCEL_MARCH:
                return gameParameters.getEnableMarching().getValue() && cell.hasMarchCommand() != null;

            default:
                break;
        }

        throw new MVCModelError( "userCommandAllowed() : invalid command code " + cmd.code );
    }

    private void sendUserCommand( UserCommand cmd )
    {
        cmd.playerId = clientPlayerId;

        if ( !isWatchMode() && isUserCommandAllowed( cmd ) )
            //clientEngine.sendUserCommand( cmd );
            gameModel.setUserCommand( this, cmd );
    }

    /*
    cell zones
    
    +-------------------+
    |     \   3   /     |
    |   2  \     /  4   |
    |\__    \___/    __/|
    |   \__ /   \ __/   |
    | 1    |  0  |   5  |
    |    __|     |__    |
    | __/   \___/   \__ |
    |/      /   \      \|
    |  8   /     \  6   |
    |     /   7   \     |
    +-------------------+
    */

    /**
     * process user command received from server
     */
    void processUserCommand( UserCommand cmd )
    {
        Cell cell = gameModel.getBoardStateModel().getCell( cmd.cellX, cmd.cellY );

        //if ( userCommandAllowed( cmd ) )
        switch ( cmd.code )
        {
        //            case SWAP_VECTORS:
        //                gameUpdater.swapVectors( cell, cmd.arg1 );
        //                cell.cancelMarch( cmd.playerId, true );
        //                break;

            case SWAP_VECTOR_U:
                gameUpdater.swapVectors( cell, 3 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SWAP_VECTOR_UR:
                gameUpdater.swapVectors( cell, 4 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SWAP_VECTOR_R:
                gameUpdater.swapVectors( cell, 5 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SWAP_VECTOR_DR:
                gameUpdater.swapVectors( cell, 6 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SWAP_VECTOR_D:
                gameUpdater.swapVectors( cell, 7 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SWAP_VECTOR_DL:
                gameUpdater.swapVectors( cell, 8 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SWAP_VECTOR_L:
                gameUpdater.swapVectors( cell, 1 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SWAP_VECTOR_UL:
                gameUpdater.swapVectors( cell, 2 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case CANCEL_VECTORS:
                cell.cellState.cancelMoves();
                cell.cancelMarch( cmd.playerId, true );
                break;

            //            case SET_VECTORS:
            //                gameUpdater.setVectors( cell, cmd.arg1 );
            //                cell.cancelMarch( cmd.playerId, true );
            //                break;

            case SET_VECTOR_U:
                gameUpdater.setVectors( cell, 3 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SET_VECTOR_UR:
                gameUpdater.setVectors( cell, 4 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SET_VECTOR_R:
                gameUpdater.setVectors( cell, 5 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SET_VECTOR_DR:
                gameUpdater.setVectors( cell, 6 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SET_VECTOR_D:
                gameUpdater.setVectors( cell, 7 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SET_VECTOR_DL:
                gameUpdater.setVectors( cell, 8 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SET_VECTOR_L:
                gameUpdater.setVectors( cell, 1 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SET_VECTOR_UL:
                gameUpdater.setVectors( cell, 2 );
                cell.cancelMarch( cmd.playerId, true );
                break;

            case SWAP_MARCH:
            case SET_MARCH:
                cell.cancelMarch( cmd.playerId, false );
                //                IntParameter ms = gameParameters.getMarchSpeed();
                //                cmd.ttl = ms.getMax() - ms.getValue() + ms.getMin();
                //                if ( cell.cellState.playerId == cmd.playerId )
                //                    cmd.ttl = gameParameters.getMarchSpeed().getValue();
                //                else
                cmd.ttl = -1;
                cell.commands.add( cmd );
                break;

            case STOP_MARCH:
                UserCommand uc = cell.hasCommand( UserCommandCode.STOP_MARCH, cmd.playerId );
                if ( uc == null )//|| uc.code != UserCommandCode.STOP_MARCH || uc.playerId != cmd.playerId )
                {
                    //cell.cancelMarch( false );
                    cmd.ttl = -1;
                    cell.commands.add( cmd );
                }
                else
                    //if ( uc.code == UserCommandCode.STOP_MARCH && uc.playerId == cmd.playerId )
                    cell.commands.remove( uc );
                break;

            case CANCEL_MARCH:
                cell.cancelMarch( cmd.playerId, true );
                break;

            case FILL_GROUND:
                gameUpdater.fillGround( cell );
                break;

            case DIG_GROUND:
                gameUpdater.digGround( cell );
                break;

            case BUILD_BASE:
                gameUpdater.buildBase( cell );
                break;

            case MANAGED_FILL_GROUND:
            case MANAGED_DIG_GROUND:
            case MANAGED_BUILD_BASE:
            case MANAGED_SCUTTLE_BASE:
            case MANAGED_TROOPS_PARA:
            case MANAGED_TROOPS_GUN:
                startManagedCommand( cell, cmd );
                break;

            case SCUTTLE_BASE:
                gameUpdater.scuttleBase( cell );
                break;

            case CANCEL_MANAGE:
                cancelManagedCommands( cell );
                break;

            //            case TROOPS_RESERVE:
            //                setTroopsReserve( cell, cmd.arg1 );
            //                break;

            case TROOPS_RESERVE_L1:
                setTroopsReserve( cell, 0 );
                break;

            case TROOPS_RESERVE_L2:
                setTroopsReserve( cell, 1 );
                break;

            case TROOPS_RESERVE_L3:
                setTroopsReserve( cell, 2 );
                break;

            case TROOPS_RESERVE_L4:
                setTroopsReserve( cell, 3 );
                break;

            case TROOPS_RESERVE_L5:
                setTroopsReserve( cell, 4 );
                break;

            case TROOPS_RESERVE_L6:
                setTroopsReserve( cell, 5 );
                break;

            case TROOPS_RESERVE_L7:
                setTroopsReserve( cell, 6 );
                break;

            case TROOPS_RESERVE_L8:
                setTroopsReserve( cell, 7 );
                break;

            case TROOPS_RESERVE_L9:
                setTroopsReserve( cell, 8 );
                break;

            case TROOPS_RESERVE_L10:
                setTroopsReserve( cell, 9 );
                break;

            case ATTACK_CELL:
                attackCell( cell, cmd.playerId );
                break;

            case TROOPS_PARA:
            case TROOPS_GUN:
                startUnmanagedCommand( cell, cmd );
                break;

            default:
                throw new MVCModelError( "processUserCommand() : invalid command code " + cmd.code );
        }
    }

    //static int n = 0;

    private void updateStatistics()
    {
        int maxPlayerCount = Consts.playerColors.length;
        int[] occupiedCellCount = new int[ maxPlayerCount ];

        // compute ground/occupied cell count

        int groundCellCount = 0;
        for ( Cell cell : gameModel.getBoardStateModel() )
        {
            CellState cs = cell.cellState;

            if ( !cs.isSeaCell() )
                groundCellCount++;

            int playerId = cs.playerId;
            if ( playerId != Consts.NullId )
                occupiedCellCount[ playerId ]++;
        }

        // coverage %

        //for ( PlayerInfoModel pim : gameModel.getPlayersModel().selectedPlayers() )
        SelectedPlayerIterator it = gameModel.getPlayersModel().selectedPlayers();
        while ( it.hasNext() )
        {
            int pid = it.next().getPlayerId();
            int occ = occupiedCellCount[ pid ];
            double op = (double)occ / (double)groundCellCount * 100.0;
            gameModel.getGameStatisticsModel().getPlayerStatisticsModel( pid ).setCoveragePercent( this, op );
        }
        //        it.close();
    }

    public boolean isWatchMode()
    {
        switch ( clientPlayer.getPlayerState() )
        {
            case WatchingGame:
            case WonGame:
            case LostGame:
                return true;

            default:
                return false;
        }
    }

    //    private void setWatchMode( String message, float a )
    //    {
    //        watchGame = true;
    //        gameModel.getBoardStateModel().computeVisibility( clientPlayerId, watchGame );
    //        gameFrameController.setGlassPaneText( message, a );
    //        //gameFrameController.redrawBoard();
    //        boardPanelController.redrawBoard();
    //    }

    public boolean allowWatch()
    {
        return !gameModel.hasWinner() && !isWatchMode() && gameModel.getPlayersModel().getActivePlayerCount() > 1;
    }

    void setWatchMode()
    {
        watchGame = true;
        gameModel.getBoardStateModel().computeVisibility( clientPlayerId, watchGame );
        //        boardPanelController.redrawBoard();
        //        boardPanelController.disableView();
    }

    //    public void setWatchGame()
    //    {
    //        setWatchMode( "watching", 0.5f );
    //        clientEngine.watchGame();
    //    }
    //
    //    void setLostGame()
    //    {
    //        if ( !gameModel.getPlayersModel().hasWinner() )
    //            setWatchMode( "you loose", 0.5f );
    //    }
    //
    //    public void setWinner( PlayerInfoModel winner ) //int winnerId )
    //    {
    //        String m = null;
    //        if ( winner.getPlayerId() == clientPlayerId )
    //            m = "you win !";
    //        else
    //            m = winner.getPlayerName() + " wins !";
    //
    //        // make all map visible
    //
    //        setWatchMode( m, 0.9f );
    //    }

    //    public void setWatchGame()
    //    {
    //        setWatchMode();
    //        clientEngine.watchGame();
    //    }

    //    public void setCloseGame()
    //    {
    //        //abortGame();
    //        clientEngine.abortGame();
    //        close();
    //    }

    //    void setLostGame()
    //    {
    //        if ( !gameModel.getPlayersModel().hasWinner() )
    //            setWatchMode( "you loose", 0.5f );
    //    }

    //    public void setWinner( PlayerInfoModel winner ) //int winnerId )
    //    {
    //        String m = null;
    //        if ( winner.getPlayerId() == clientPlayerId )
    //            m = "you win !";
    //        else
    //            m = winner.getPlayerName() + " wins !";
    //
    //        // make all map visible
    //
    //        setWatchMode( m, 0.9f );
    //    }

    //    private void cellLimitUp( CellState cs, boolean updateNext )
    //    {
    //        if ( updateNext )
    //        {
    //            for ( int i = 0; i < playerCount; i++ )
    //                if ( cs.nextTroopsLevel[ i ] > Consts.maxTroopsLevel )
    //                    cs.nextTroopsLevel[ i ] = Consts.maxTroopsLevel;
    //        }
    //        else
    //        {
    //            for ( int i = 0; i < playerCount; i++ )
    //                if ( cs.troopsLevel[ i ] > Consts.maxTroopsLevel )
    //                    cs.troopsLevel[ i ] = Consts.maxTroopsLevel;
    //        }
    //    }
    //
    //    private void cellLimitDown( CellState cs, boolean updateNext )
    //    {
    //        if ( updateNext )
    //        {
    //            for ( int i = 0; i < playerCount; i++ )
    //                if ( cs.nextTroopsLevel[ i ] < 0.0f )
    //                    cs.nextTroopsLevel[ i ] = 0.0f;
    //        }
    //        else
    //        {
    //            for ( int i = 0; i < playerCount; i++ )
    //                if ( cs.troopsLevel[ i ] < 0.0f )
    //                    cs.troopsLevel[ i ] = 0.0f;
    //        }
    //    }

    public GameModel getGameModel()
    {
        return gameModel;
    }

    public int getClientPlayerId()
    {
        return clientPlayerId;
    }

    public XBColor getClientPlayerColor()
    {
        return clientPlayer.getPlayerColor();
    }

    public RandomGen getRandomGenerator()
    {
        return randomGen;
    }

    // MVCModelObserver interface

    @Override
    public void close()
    {
        //        if ( boardPanelController != null )
        //        {
        //            boardPanelController.close();
        //            boardPanelController = null;
        //        }

        unsubscribeModel();
    }

    @Override
    public void modelChanged( MVCModelChange change )
    {
        switch ( (XBMVCModelChangeId)change.getChangeId() )
        {
        //            case NewGame:
        //                setClientPlayer( gameModel.getClientPlayer() );
        //                break;

            case WatchGame:
                setWatchMode();
                break;

            case AbortGame:
                close();
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
}
