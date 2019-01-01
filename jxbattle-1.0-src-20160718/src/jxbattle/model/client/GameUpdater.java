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

import org.generic.bean.parameter.IntParameter;
import org.generic.mvc.model.MVCModelError;

import jxbattle.bean.common.game.Cell;
import jxbattle.bean.common.game.CellMove;
import jxbattle.bean.common.game.CellState;
import jxbattle.bean.common.game.UserCommand;
import jxbattle.bean.common.game.UserCommand.UserCommandCode;
import jxbattle.bean.common.parameters.game.GameParameters;
import jxbattle.bean.common.parameters.game.WrappingMode;
import jxbattle.common.Consts;

class GameUpdater
{
    private GameEngine gameEngine;

    private GameParameters gameParameters;

    private RandomGen randomGen;

    // constants

    private final int paraTroopsRenderingDuration = 300; // rendering time of parachute symbol

    private final int gunTroopsRenderingDuration = 300; // rendering time of guns symbol

    // precomputed values

    //    private int clientPlayerId;

    private float moveHinder;

    private float fightIntensity;

    //private int cellSideCount;

    private float kSupplyDecay;

    private float[] hillSteepness;

    private int buildSteps;

    private float buildCost;

    private float scuttleCost;

    private float fillCost;

    private float digCost;

    private float paraCost;

    private float gunCost;

    private int xCellCount; // x board size

    private int yCellCount; // y board size

    private WrappingMode wrapMode;

    private int marchSpeed;

    // models

    private BoardStateModel boardStateModel;

    // temporary

    private int playerCount;

    private float againstPlayer[];

    private float outnumberRatio[];

    private float[] dstRoom;

    private float[] availRoom;

    private float[] inputFlow;

    private float[] kSlope;

    GameUpdater( GameEngine ge, BoardStateModel bsm, GameParameters gp, RandomGen rnd )
    {
        gameEngine = ge;
        gameParameters = gp;
        boardStateModel = bsm;
        randomGen = rnd;
        precompute();
    }

    private void precompute()
    {
        //        clientPlayerId = gameEngine.getGameModel().getClientPlayer().getPlayerId();

        wrapMode = (WrappingMode)gameParameters.getBoardParameters().getWrappingMode().getValue();

        //        cellSideCount = gameParameters.getBoardParameters().getCellTopology().sideCount();
        //        playerCount = gameModel.getPlayersModel().getSelectedPlayerCount();

        buildSteps = gameParameters.getBaseBuildSteps().getValue();
        buildCost = gameParameters.getBaseBuildCost().getValue();
        scuttleCost = gameParameters.getBaseScuttleCost().getValue();

        fillCost = gameParameters.getFillCost().getValue();
        digCost = gameParameters.getFillCost().getValue();

        paraCost = gameParameters.getParaTroopsCost().getValue();
        gunCost = gameParameters.getGunTroopsCost().getValue();

        xCellCount = boardStateModel.getBoardState().getBoardXDim();
        yCellCount = boardStateModel.getBoardState().getBoardYDim();

        // move hinder

        //        IntParameter ip = gameParameters.getTroopsMove();
        //        float k = (float)(ip.getValue() - ip.getMin()) / (ip.getMax() - ip.getMin());
        //        moveHinder = Consts.minTroopsMove + k * (Consts.maxTroopsMove - Consts.minTroopsMove);
        moveHinder = gameParameters.getTroopsMove().scaleValue( Consts.minTroopsMove, Consts.maxTroopsMove );

        // fight intensity

        //        ip = gameParameters.getFightIntensity();
        //        k = (float)(ip.getValue() - ip.getMin()) / (ip.getMax() - ip.getMin());
        //fightIntensity = (int)(Consts.minFightIntensity + k * (Consts.maxFightIntensity - Consts.minFightIntensity));
        fightIntensity = gameParameters.getFightIntensity().scaleValue( Consts.minFightIntensity, Consts.maxFightIntensity );

        // decay

        kSupplyDecay = gameParameters.getSupplyDecay().getValue() * 0.1f;

        // hinder due to uphill or downhill moves

        //        ip = gameParameters.getHillSteepness();
        //        k = (float)(ip.getValue() - ip.getMin()) / (ip.getMax() - ip.getMin());
        //        float kHillSteepness = Consts.minHillSteepness + k * (Consts.maxHillSteepness - Consts.minHillSteepness);
        float kHillSteepness = gameParameters.getHillSteepness().scaleValue( Consts.minHillSteepness, Consts.maxTroopsLevel );

        hillSteepness = new float[ 2 * Consts.maxElevation + 1 ];
        for ( int d = -Consts.maxElevation; d <= Consts.maxElevation; d++ )
        {
            // d = level difference between source and destination cell (> 0 = uphill)
            float k = (float)(2.0 / (1.0 + Math.exp( d * kHillSteepness / Consts.maxElevation )));
            hillSteepness[ d + Consts.maxElevation ] = k;
        }

        playerCount = boardStateModel.getBoardState().getCell( 0, 0 ).cellState.troopsLevel.length;

        marchSpeed = Consts.maxMarchUpdateSpeed - gameParameters.getMarchSpeed().getValue() + 2;

        againstPlayer = new float[ playerCount ];
        outnumberRatio = new float[ playerCount ];
        dstRoom = new float[ Consts.maxCellSideCount ];
        inputFlow = new float[ playerCount ]; // flow entering cell (for each player)
        availRoom = new float[ playerCount ];
        kSlope = new float[ Consts.maxCellSideCount ];
    }

    void computeStep( int clientPlayerId, boolean watchGame )
    {
        for ( Cell cell : boardStateModel )
        {
            CellState cs = cell.cellState;

            processPendingCommand( cell );

            if ( cs.paraTroopsId != Consts.NullId )
                if ( System.currentTimeMillis() - cs.paraTroopsStart > paraTroopsRenderingDuration )
                    cs.paraTroopsId = Consts.NullId;

            if ( cs.gunTroopsId != Consts.NullId )
                if ( System.currentTimeMillis() - cs.gunTroopsStart > gunTroopsRenderingDuration )
                    cs.gunTroopsId = Consts.NullId;

            updateCellGrowth( cs );

            updateFightingCell( cs );

            // copy level array
            //            cs.copyLevel();
        }

        for ( Cell cell : boardStateModel )
            cell.cellState.copyLevel();

        for ( Cell cell : boardStateModel )
            possibleMoves( cell.cellState );

        for ( Cell cell : boardStateModel )
            updateCellMoves( cell.cellState );

        boardStateModel.commitLevels();

        boardStateModel.computeVisibility( clientPlayerId, watchGame );
    }

    private boolean processMarch( Cell cell, UserCommand uc )
    {
        CellState cs = cell.cellState;

        if ( uc.playerId == cs.playerId )
        {
            if ( uc.ttl == -1 )
            {
                if ( cs.getTroopsLevel() > 0.0f )
                {
                    uc.ttl = marchSpeed;
                    return true;
                }

                return false;
            }

            if ( uc.ttl == 1 )
            {
                switch ( uc.code )
                {
                    case SET_MARCH:
                        setVectors( cell, uc.arg1 );
                        break;

                    case SWAP_MARCH:
                        swapVectors( cell, uc.arg1 );
                        break;

                    default:
                        throw new IllegalArgumentException( "invalid march command" );
                }

                // next cell

                int mi = -1;
                switch ( uc.arg1 )
                {
                    case 1:
                        mi = 3;
                        break;

                    case 3:
                        mi = 0;
                        break;

                    case 5:
                        mi = 1;
                        break;

                    case 7:
                        mi = 2;
                        break;

                    default:
                        break;
                }
                Cell dst = cell.cellState.moves[ mi ].targetCell;

                // propagate if next cell free/friend/not sea/no other march command
                if ( dst != null )
                {
                    CellState ds = dst.cellState;
                    UserCommand dstCmd = dst.hasMarchCommand();

                    boolean propagate = !ds.isSeaCell();
                    propagate = propagate && (ds.playerId == Consts.NullId || ds.playerId == cs.playerId);
                    propagate = propagate && dstCmd == null;
                    if ( propagate )
                    {
                        dstCmd = new UserCommand( uc );
                        dstCmd.ttl = -1;
                        dst.commands.add( dstCmd );
                    }
                    else if ( dstCmd != null )
                    {
                        if ( dstCmd.playerId == uc.playerId )
                            switch ( dstCmd.code )
                            {
                                case STOP_MARCH:
                                    dst.cancelCommand( UserCommandCode.STOP_MARCH, uc.playerId );
                                    break;

                                case SET_MARCH:
                                case SWAP_MARCH:
                                    if ( dstCmd.ttl == -1 )
                                        dstCmd.ttl = 1;
                                    break;

                                default:
                                    break;
                            }
                    }
                }
            }
            return true;
        }

        return false;
    }

    private void processPendingCommand( Cell cell )
    {
        // user commands 

        Iterator<UserCommand> it = cell.commands.iterator();

        while ( it.hasNext() )
        {
            UserCommand uc = it.next();

            boolean updateTTL = true;
            boolean removeCommand = false;
            if ( uc.ttl > 0 || uc.ttl == -1 )
            {
                switch ( uc.code )
                {
                    case MANAGED_FILL_GROUND:
                        fillGround( cell );
                        removeCommand = cell.cellState.elevation == Consts.maxElevation;
                        break;

                    case MANAGED_DIG_GROUND:
                        if ( !cell.cellState.isSeaCell() )
                            digGround( cell );
                        removeCommand = cell.cellState.isSeaCell();
                        break;

                    case MANAGED_BUILD_BASE:
                        updateTTL = canBuildBase( cell );
                        if ( updateTTL )
                            doBuildBase( cell.cellState );
                        removeCommand = cell.cellState.townBuildStep == buildSteps;
                        break;

                    case MANAGED_SCUTTLE_BASE:
                        updateTTL = canScuttleBase( cell );
                        if ( updateTTL )
                            doScuttleBase( cell.cellState );
                        removeCommand = cell.cellState.townBuildStep == 0;
                        break;

                    case TROOPS_PARA:
                    case MANAGED_TROOPS_PARA:
                        //                        updatePara( cell.cellState, uc.playerId );
                        paraTroops( cell, uc.arg1, uc.arg2 );
                        break;

                    case TROOPS_GUN:
                    case MANAGED_TROOPS_GUN:
                        //                        updateGun( cell.cellState, uc.playerId );
                        gunTroops( cell, uc.arg1, uc.arg2 );
                        break;

                    case SET_MARCH:
                    case SWAP_MARCH:
                        //                        if ( uc.ttl == 1 )
                        updateTTL = processMarch( cell, uc );
                        break;

                    default:
                        break;
                }
            }

            if ( updateTTL && uc.ttl != -1 ) // decrease TTL (except if infinite)
                uc.ttl--;

            if ( uc.ttl == 0 || removeCommand ) // remove command if expired (except if infinite TTL)
                it.remove();
        }
    }

    //    Point getDestinationCell( int sourceCellX, int sourceCellY, int cellMouseDist, int cellMouseAngle, int range )
    //    {
    //        if ( cellMouseDist > 100 )
    //            cellMouseDist = 100;
    //        double d = cellMouseDist * range / 30.0;
    //        double a = cellMouseAngle * Math.PI / 180.0;
    //
    //        int dx = (int)Math.round( d * Math.cos( a ) );
    //        int dy = (int)Math.round( d * Math.sin( a ) );
    //        if ( dx == 0 && dy == 0 )
    //            return null;
    //
    //        int cx = sourceCellX + dx;
    //        int cy = sourceCellY + dy;
    //
    //        WrappingMode wrap = (WrappingMode)gameParameters.getBoardParameters().getWrappingMode().getValue();
    //        boolean hasTarget;
    //        switch ( wrap )
    //        {
    //            case NONE:
    //                hasTarget = cx >= 0 && cy >= 0 && cx < xCellCount && cy < yCellCount;
    //                break;
    //
    //            case LEFT_RIGHT:
    //                hasTarget = cy >= 0 && cy < yCellCount;
    //                if ( hasTarget )
    //                {
    //                    if ( cx < 0 )
    //                        cx += xCellCount;
    //                    else if ( cx >= xCellCount )
    //                        cx -= xCellCount;
    //                }
    //                break;
    //
    //            case TOP_DOWN:
    //                hasTarget = cx >= 0 && cx < xCellCount;
    //                if ( hasTarget )
    //                {
    //                    if ( cy < 0 )
    //                        cy += yCellCount;
    //                    else if ( cy >= yCellCount )
    //                        cy -= yCellCount;
    //                }
    //                break;
    //
    //            case FULL:
    //                hasTarget = true;
    //                if ( cx < 0 )
    //                    cx += xCellCount;
    //                else if ( cx >= xCellCount )
    //                    cx -= xCellCount;
    //                if ( cy < 0 )
    //                    cy += yCellCount;
    //                else if ( cy >= yCellCount )
    //                    cy -= yCellCount;
    //                break;
    //
    //            default:
    //                throw new MVCModelError( "invalid wrap mode " + wrap );
    //        }
    //
    //        //        if ( hasTarget && !(cx == sourceCellX && cy == sourceCellY) )
    //        if ( hasTarget )
    //            //            return boardStateModel.getCell( cx, cy );
    //            return new Point( cx, cy );
    //
    //        return null;
    //    }

    Point wrapCellCoords( Point coords )
    {
        if ( coords == null )
            return null;

        int cx = coords.x;
        int cy = coords.y;

        //        WrappingMode wrap = (WrappingMode)gameParameters.getBoardParameters().getWrappingMode().getValue();
        //        WrappingMode wrap = (WrappingMode)boardParameters.getWrappingMode().getValue();
        switch ( wrapMode )
        {
            case NONE:
                return coords;

            case LEFT_RIGHT:
                if ( cx < 0 )
                    cx += xCellCount;
                else if ( cx >= xCellCount )
                    cx -= xCellCount;
                break;

            case TOP_DOWN:
                if ( cy < 0 )
                    cy += yCellCount;
                else if ( cy >= yCellCount )
                    cy -= yCellCount;
                break;

            case FULL:
                if ( cx < 0 )
                    cx += xCellCount;
                else if ( cx >= xCellCount )
                    cx -= xCellCount;
                if ( cy < 0 )
                    cy += yCellCount;
                else if ( cy >= yCellCount )
                    cy -= yCellCount;
                break;

            default:
                throw new MVCModelError( "invalid wrap mode " + wrapMode );
        }

        return new Point( cx, cy );
    }

    private void updatePara( CellState cs, int paraTroopsId )
    {
        int damage = gameParameters.getParaTroopsDamage().getValue();
        //  int cost = gameParameters.getParaTroopsCost().getDefaultValue();

        // troops variation

        //        if ( cs.playerId != Consts.NullId // occupied cell (ie. not empty and not sea)
        //                && cs.playerId != paraTroopsId // but not by sender player 
        //        )
        //        {
        //            // enemy
        //            cs.addTroopsLevel( paraTroopsId, damage );
        //            cs.fight = true;
        //        }
        //        else
        //        {
        //            // friend or unoccupied
        //            cs.playerId = paraTroopsId;
        //            cs.addTroopsLevel( cost );
        //        }

        if ( !cs.isSeaCell() )
        {
            cs.addTroopsLevel( paraTroopsId, damage );
            if ( cs.playerId == Consts.NullId )
                cs.playerId = paraTroopsId;
            else
                cs.fight = cs.playerId != paraTroopsId;
        }

        cs.paraTroopsId = paraTroopsId;
        cs.paraTroopsStart = System.currentTimeMillis();
    }

    /**
     * process para troops user command
     */
    private void paraTroops( Cell sourceCell, int cellMouseDist, int cellMouseAngle )
    {
        int range = gameParameters.getParaTroopsRange().getValue();

        Point dst = gameEngine.getDestinationCell( sourceCell.gridX, sourceCell.gridY, cellMouseDist, cellMouseAngle, range );
        if ( dst != null && gameEngine.isCellValid( dst.x, dst.y, wrapMode, xCellCount, yCellCount ) )
        {
            Point targetCoords = wrapCellCoords( dst );
            if ( targetCoords != null )
            {
                Cell targetCell = boardStateModel.getCell( targetCoords.x, targetCoords.y );
                CellState cs = sourceCell.cellState;
                CellState tcs = targetCell.cellState;

                if ( cs.getTroopsLevel() >= paraCost + cs.reserveLevel )
                {
                    cs.subTroopsLevel( paraCost );
                    // update destination cell
                    updatePara( tcs, sourceCell.cellState.playerId );
                }
            }
        }
    }

    /**
     * @param cs target cell state
     * @param gunTroopId incoming gun shell player id
     */
    private void updateGun( CellState cs, int gunTroopId )
    {
        /** NOTE: artillery does not damage fighting troops **/

        //        if ( !cs.fight // no fight 
        //                && cs.playerId != Consts.NullId // occupied cell  (ie. not empty and not sea)
        //        //   && cs.playerId != gunTroopId // but not by shooter player 
        //        )
        if ( !cs.fight // no fight 
                && cs.playerId != Consts.NullId ) // occupied cell  (ie. not empty and not sea)
        {
            int damage = gameParameters.getGunTroopsDamage().getValue();
            cs.subTroopsLevel( damage );

            if ( cs.getTroopsLevel() == 0 ) // killed ?
                cs.playerId = Consts.NullId;
            else
                //                cs.fight = true; // no -> fight
                cs.fight = cs.playerId != gunTroopId; // no -> fight

            cs.gunTroopsId = gunTroopId;
            cs.gunTroopsStart = System.currentTimeMillis();
        }
    }

    /**
     * process gun troops user command
     */
    private void gunTroops( Cell sourceCell, int cellMouseDist, int cellMouseAngle )
    {
        int range = gameParameters.getGunTroopsRange().getValue();

        //Point targetCoords = wrapCellCoords( GameEngine.getDestinationCell( sourceCell.gridX, sourceCell.gridY, cellMouseDist, cellMouseAngle, range ) );
        Point dst = gameEngine.getDestinationCell( sourceCell.gridX, sourceCell.gridY, cellMouseDist, cellMouseAngle, range );
        if ( dst != null && gameEngine.isCellValid( dst.x, dst.y, wrapMode, xCellCount, yCellCount ) )
        {
            Point targetCoords = wrapCellCoords( dst );
            if ( targetCoords != null )
            {
                Cell targetCell = boardStateModel.getCell( targetCoords.x, targetCoords.y );
                CellState cs = sourceCell.cellState;
                CellState tcs = targetCell.cellState;

                if ( cs.getTroopsLevel() >= gunCost + cs.reserveLevel )
                {
                    cs.subTroopsLevel( gunCost );
                    updateGun( tcs, sourceCell.cellState.playerId );
                }
            }
        }
    }

    // decay, farming, ...
    private void updateCellGrowth( CellState cs )
    {
        if ( cs.playerId != Consts.NullId && !cs.fight )
        {
            // farming

            boolean farmProba;

            if ( cs.isTown && cs.townBuildStep == buildSteps )
                farmProba = randomGen.posRandomInt( Consts.maxTownSize ) < cs.townSize;
            else
            {
                IntParameter farm = gameParameters.getSupplyFarms();
                farmProba = randomGen.posRandomInt( farm.getMax() ) < farm.getValue();
            }

            if ( farmProba )
                cs.addTroopsLevel( Consts.maxTroopsLevel / 100.0f );

            // decay

            float thresh = kSupplyDecay * cs.getTroopsLevel();
            boolean decayProba = randomGen.posRandomFloat( Consts.maxTroopsLevel ) < thresh;

            if ( decayProba )
                cs.subTroopsLevel( Consts.maxTroopsLevel / 100.0f );
        }
    }

    private void updateFightingCell( CellState cs )
    {
        if ( cs.fight )
        {
            //int playerCount = cs.troopsLevel.length;

            // number of attacking forces against each player

            //float againstPlayer[] = new float[ playerCount ];

            for ( int player = 0; player < playerCount; player++ )
            {
                float against = 0.0f;
                for ( int opponent = 0; opponent < playerCount; opponent++ )
                    if ( opponent != player )
                        against += cs.troopsLevel[ opponent ];
                againstPlayer[ player ] = against;
            }

            // compute how badly each player is outnumbered

            for ( int player = 0; player < playerCount; player++ )
            {
                if ( againstPlayer[ player ] == 0.0f ) // no opponent left, fight end
                {
                    cs.setFightWinner( player );
                    return;
                }

                if ( cs.troopsLevel[ player ] > 0.0f )
                    outnumberRatio[ player ] = againstPlayer[ player ] / cs.troopsLevel[ player ];
                else
                    outnumberRatio[ player ] = 0.0f;
            }

            // compute losses (proportional to squared outnumber ratio and 'fight' setting)

            int aliveCount = 0; // survivors :-)
            int fightWinnerId = Consts.NullId;
            for ( int player = 0; player < playerCount; player++ )
            {
                float troops = cs.troopsLevel[ player ];
                if ( troops > 0.0f )
                {
                    float rand = randomGen.posnegRandomFloat( 2 );
                    float playerLosses = (outnumberRatio[ player ] * outnumberRatio[ player ] + rand) * fightIntensity;
                    if ( playerLosses < 0.0f )
                        playerLosses = 0.0f;

                    troops -= playerLosses;
                    if ( troops < 0.0f )
                    {
                        // dead
                        cs.troopsLevel[ player ] = 0.0f;
                    }
                    else
                    {
                        // wounded
                        cs.troopsLevel[ player ] = troops;
                        aliveCount++;
                        fightWinnerId = player; // potential winner, remember in case aliveCount==1
                    }
                }
            }

            // trophy time

            switch ( aliveCount )
            {
                case 0:
                    // everybody is dead
                    cs.setFightWinner( Consts.NullId );
                    break;

                case 1:
                    // there can be only one !
                    cs.setFightWinner( fightWinnerId );
                    break;

                default:
                    // still fighting
                    break;
            }
        }
    }

    /*
    vector directions
    +-----------------+
    |        ^        |
    |        |        |
    |        0        |
    |        |        |
    |<---3---+---1--->|
    |        |        |
    |        2        |
    |        |        |
    |        v        |
    +-----------------+
    */
    /**
     * @see BoardController.getCellZone()
     */
    void setVectors( Cell cell, int zone )
    {
        CellMove[] moves = cell.cellState.moves;

        cell.cellState.cancelMoves();

        switch ( zone )
        {
            case 1:
                moves[ 3 ].isSet = true;
                break;

            case 2:
                moves[ 3 ].isSet = true;
                moves[ 0 ].isSet = true;
                break;

            case 3:
                moves[ 0 ].isSet = true;
                break;

            case 4:
                moves[ 0 ].isSet = true;
                moves[ 1 ].isSet = true;
                break;

            case 5:
                moves[ 1 ].isSet = true;
                break;

            case 6:
                moves[ 1 ].isSet = true;
                moves[ 2 ].isSet = true;
                break;

            case 7:
                moves[ 2 ].isSet = true;
                break;

            case 8:
                moves[ 2 ].isSet = true;
                moves[ 3 ].isSet = true;
                break;

            default:
                break;
        }
    }

    void swapVectors( Cell cell, int zone )
    {
        CellMove[] moves = cell.cellState.moves;
        switch ( zone )
        {
            case 1:
                moves[ 3 ].isSet = !moves[ 3 ].isSet;
                break;

            case 2:
                moves[ 3 ].isSet = !moves[ 3 ].isSet;
                moves[ 0 ].isSet = !moves[ 0 ].isSet;
                break;

            case 3:
                moves[ 0 ].isSet = !moves[ 0 ].isSet;
                break;

            case 4:
                moves[ 0 ].isSet = !moves[ 0 ].isSet;
                moves[ 1 ].isSet = !moves[ 1 ].isSet;
                break;

            case 5:
                moves[ 1 ].isSet = !moves[ 1 ].isSet;
                break;

            case 6:
                moves[ 1 ].isSet = !moves[ 1 ].isSet;
                moves[ 2 ].isSet = !moves[ 2 ].isSet;
                break;

            case 7:
                moves[ 2 ].isSet = !moves[ 2 ].isSet;
                break;

            case 8:
                moves[ 2 ].isSet = !moves[ 2 ].isSet;
                moves[ 3 ].isSet = !moves[ 3 ].isSet;
                break;

            default:
                break;
        }
    }

    // determine where it is possible to move
    // keep track of move amount for each possible destination

    /*
       cell1    cell2    cell2 attacks | move ?    cell2 state
                             cell1     |           after move
       --------------------------------+----------------------
       color1   empty                  |   Y         color1
       color1   color1                 |   Y         color1
       color1   color2                 |   Y         fight
       color1   fight                  |   Y         fight
       fight    empty                  |   N         empty
       fight    color1        N        |   N         color1
       fight    color1        Y        |   Y         fight
       fight    fight         N        |   N         fight
       fight    fight         Y        |   Y         fight
     */

    private void possibleMoves( CellState cs )
    {
        //int cellSideCount = cs.moves.length;
        //float[] dstRoom = new float[ cellSideCount ];

        float totalDstRoom = 0.0f;

        // compute available space in neighbours

        for ( CellMove cm : cs.moves )
        {
            cs.possibleMoveAmount[ cm.dir ] = 0;
            dstRoom[ cm.dir ] = 0.0f;

            // not possible to move if :
            // - no move set
            // - troops level under reserve level
            // - target cell outside board or sea
            // - cell outside under fight (see exception below)

            // possible to move if :
            // - moving to empty cell 
            // - moving to same color cell 
            // - moving to different color cell (this will cause fight afterwards) 
            // - cell is already under fight and attacks a cell already attacking it (counter attack)

            boolean srcCond = cm.isSet && cs.getTroopsLevel() > cs.reserveLevel;
            boolean dstCond = cm.targetCell != null && !cm.targetCell.cellState.isSeaCell();

            if ( srcCond && dstCond )
            {
                boolean fightCond = true;
                if ( cs.fight )
                {
                    CellState dst = cm.targetCell.cellState;
                    CellMove reverseMove = dst.moves[ cm.revDir ];

                    fightCond = reverseMove.isSet; // enable counter attack only
                }

                if ( fightCond )
                {
                    float room = Consts.maxTroopsLevel - cm.targetCell.cellState.troopsLevel[ cs.playerId ];
                    dstRoom[ cm.dir ] = room;
                    totalDstRoom += room;
                }
            }
        }

        // each possible move is proportional to available room in neighbours

        if ( totalDstRoom != 0.0f )
        {
            float avail = cs.getTroopsLevel() - cs.reserveLevel;
            if ( avail > 0 )
                for ( int j = 0; j < cs.possibleMoveAmount.length; j++ )
                {
                    float k = dstRoom[ j ] / totalDstRoom;
                    //cs.possibleMoveAmount[ j ] = cs.getTroopsLevel() * k;
                    cs.possibleMoveAmount[ j ] = avail * k;
                }
        }
    }

    private static void updateCellMove( CellState src, CellState dst, int srcId, int dstId, float troopsMoves )
    {
        if ( troopsMoves == 0.0f )
            return;

        boolean destIsEmpty = dstId == Consts.NullId;
        boolean destIsFriend = srcId == dstId;

        //        // available room in destination cell
        //
        //        float dstRoom = Consts.maxTroopsLevel - dst.troopsLevel[ srcId ];
        //
        //        // don't move more than possible
        //
        //        float moves = Math.min( troopsMoves, src.getTroopsLevel() );
        //        moves = Math.min( moves, dstRoom );
        //        // check reserve
        //        float r = src.troopsLevel[ srcId ] - src.reserveLevel;
        //        if ( r > 0.0f && moves > r )
        //            moves = r;

        // move from source

        if ( destIsEmpty && !src.fight )
        {
            // empty destination -> occupy now
            dst.playerId = srcId;
            src.nextTroopsLevel[ srcId ] -= troopsMoves;
            dst.nextTroopsLevel[ srcId ] += troopsMoves;
        }
        else if ( destIsFriend && !destIsEmpty && !src.fight )
        {
            // destination not empty and same player
            src.nextTroopsLevel[ srcId ] -= troopsMoves;
            dst.nextTroopsLevel[ srcId ] += troopsMoves;
        }
        else if ( !destIsFriend && !destIsEmpty )
        {
            // entering fight, update destination 'fight' flag 
            dst.fight = true;
            src.nextTroopsLevel[ srcId ] -= troopsMoves;
            dst.nextTroopsLevel[ srcId ] += troopsMoves;
        }

        src.cellLimitDown( true );
        dst.cellLimitUp( true );
    }

    private void updateCellMoves( CellState cs )
    {
        if ( cs.isSeaCell() ) // sea not concerned
            return;

        //int playerCount = cs.troopsLevel.length;
        //int cellSideCount = cs.moves.length;

        // available room in this cell for each player

        //float[] availRoom = new float[ playerCount ];
        for ( int pid = 0; pid < playerCount; pid++ )
            availRoom[ pid ] = Consts.maxTroopsLevel - cs.troopsLevel[ pid ];

        // compute entering flow and slope influence

        //float[] inputFlow = new float[ playerCount ]; // flow entering cell (for each player)
        //float[] kSlope = new float[ cellSideCount ];

        System.arraycopy( CellState.zeroes, 0, inputFlow, 0, playerCount );
        for ( CellMove cm : cs.moves ) // for each neighbour
        {
            kSlope[ cm.dir ] = 1.0f;

            if ( cm.targetCell != null ) // if neighbour is defined
            {
                CellState dst = cm.targetCell.cellState;

                float dstAmount = dst.possibleMoveAmount[ cm.revDir ];
                if ( dstAmount > 0.0f )
                {
                    // entering flow

                    inputFlow[ dst.playerId ] += dstAmount;

                    // hinder due to hill slope

                    int levelDiff = cs.elevation - dst.elevation; // > 0 = uphill
                    kSlope[ cm.dir ] = hillSteepness[ levelDiff + Consts.maxElevation ];
                }
            }
        }

        // actually move from surrounding cells

        int cellPid = cs.playerId;
        for ( CellMove cm : cs.moves ) // for each neighbour
        {
            if ( cm.targetCell != null ) // if neighbour is defined
            {
                CellState dst = cm.targetCell.cellState;
                int dstId = dst.playerId;

                if ( dstId != Consts.NullId )
                {
                    //                    float k = 0.0f; // ratio of troops that can really enter
                    //                    float inFlow = inputFlow[ srcId ];
                    //                    if ( inFlow != 0.0f )
                    //                    {
                    //                        k = (float)(availRoom[ dstId ] / Math.sqrt( inFlow )) * kSlope[ cm.dir ];
                    //                        if ( k > 1.0f )
                    //                            k = 1.0f;
                    //                    }
                    //                    if ( k != 0.0f )
                    //                        updateCellMove( dst, cs, dstId, cellPid, dst.possibleMoveAmount[ cm.revDir ] * k * moveHinder );

                    float k = 0.0f; // ratio of neighbour troops that can really enter
                    float inFlow = inputFlow[ dstId ];
                    if ( inFlow != 0.0f )
                        k = availRoom[ dstId ] / inFlow * kSlope[ cm.dir ];

                    if ( k > 1.0f )
                        k = 1.0f;

                    if ( k != 0.0f )
                        updateCellMove( dst, cs, dstId, cellPid, dst.possibleMoveAmount[ cm.revDir ] * k * moveHinder );
                }
            }
        }
    }

    /**
     * @return true if enough troops to build base
     */
    private boolean canBuildBase( Cell cell )
    {
        CellState cs = cell.cellState;
        return cs.townBuildStep < buildSteps && cs.getTroopsLevel() >= buildCost;
    }

    private void doBuildBase( CellState cs )
    {
        if ( !cs.isTown )
        {
            cs.townSize = Consts.maxTownSize;
            cs.isTown = true;
        }

        cs.townBuildStep++;
        cs.subTroopsLevel( buildCost );
    }

    void buildBase( Cell cell )
    {
        if ( canBuildBase( cell ) )
            doBuildBase( cell.cellState );
    }

    /**
     * @return true if enough troops to scuttle base
     */
    private boolean canScuttleBase( Cell cell )
    {
        CellState cs = cell.cellState;
        return cs.isTown && cs.getTroopsLevel() >= scuttleCost;
    }

    private void doScuttleBase( CellState cs )
    {
        if ( gameParameters.getEnableBaseBuild().getValue() )
        {
            cs.townBuildStep--;
            if ( cs.townBuildStep == 0 )
                cs.isTown = false;
        }
        else
        {
            cs.townSize -= Consts.maxTownSize / buildSteps;
            if ( cs.townSize < Consts.maxTownSize / 4 )
            {
                cs.townSize = 0;
                cs.isTown = false;
            }
        }

        cs.subTroopsLevel( scuttleCost );
    }

    void scuttleBase( Cell cell )
    {
        if ( canScuttleBase( cell ) )
            doScuttleBase( cell.cellState );
    }

    void fillGround( Cell cell )
    {
        digFillGround( cell.cellState, 1 );
    }

    void digGround( Cell cell )
    {
        digFillGround( cell.cellState, -1 );
    }

    //    private void digGround( Cell cell )
    //    {
    //        CellState cs = cell.cellState;
    //        if ( !cs.isTown || cs.elevation > 0 )
    //        {
    //            digFillGround( cs, -1 );
    //
    //            if ( cs.elevation < 0 )
    //                cs.setCellToSea();
    //        }
    //    }

    @SuppressWarnings("null")
    private void digFillGround( CellState cs, int amount )
    {
        int dirCount = 0;
        CellState dcs = null;

        // determine number of directions set
        for ( CellMove cm : cs.moves )
            if ( cm.isSet && cm.targetCell != null )
            {
                dirCount++;
                dcs = cm.targetCell.cellState;
            }

        // no fill or dig if multiple directions set (except ones pointing to border)
        switch ( dirCount )
        {
            case 0:
                // if no move, dig or fill cell itself
                if ( amount < 0 && cs.isTown && cs.elevation == 0 )
                    return; // do not dig town to sea
                dcs = cs;
                break;

            case 1:
                // one move : target must be sea
                if ( !dcs.isSeaCell() )
                    return;
                break;

            default:
                return;
        }

        int id = cs.playerId;
        int dstId = dcs.playerId;

        boolean canFill = dstId == id || dstId == Consts.NullId;
        if ( amount > 0 )
        {
            canFill &= cs.getTroopsLevel() >= fillCost;
            canFill &= dcs.elevation <= Consts.maxElevation - amount;
        }
        else
        {
            canFill &= cs.getTroopsLevel() >= digCost;
            canFill &= dcs.elevation >= -Consts.maxElevation - amount;
        }

        if ( canFill )
        {
            if ( amount > 0 )
                cs.subTroopsLevel( fillCost );
            else
                cs.subTroopsLevel( digCost );

            dcs.elevation += amount;
            if ( dcs.isSeaCell() )
                dcs.initSeaCell();
        }
    }
}
