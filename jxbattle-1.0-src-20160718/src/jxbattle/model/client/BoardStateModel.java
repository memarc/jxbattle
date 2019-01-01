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

import java.util.ArrayList;
import java.util.List;

import jxbattle.bean.common.game.BoardState;
import jxbattle.bean.common.game.BoardState.CellIterator;
import jxbattle.bean.common.game.Cell;
import jxbattle.bean.common.game.CellState;
import jxbattle.bean.common.parameters.game.GameParameters;
import jxbattle.bean.common.parameters.game.InitialisationParameters;
import jxbattle.bean.common.parameters.game.InvisibilityMode;
import jxbattle.bean.common.parameters.game.WrappingMode;
import jxbattle.common.Consts;
import jxbattle.model.common.game.PlayerInfos;
import jxbattle.model.common.game.PlayerInfos.SelectedPlayerIterator;

import org.generic.bean.parameter.IntParameter;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelImpl;

public class BoardStateModel extends MVCModelImpl implements Iterable<Cell>
{
    private GameParameters gameParameters;

    private int xCellCount;

    private int yCellCount;

    private int cellSideCount;

    private RandomGen randomGen;

    private PlayerInfos players;

    private int playerCount;

    private int buildSteps;

    // observed beans

    private BoardState boardState;

    // models

    //private GameModel gameModel;

    public BoardStateModel( GameParameters gp, PlayerInfos plyrs, RandomGen rndGen )
    {
        init( gp, plyrs, rndGen );
        initBoardState();
    }

    private void init( GameParameters gp, PlayerInfos plyrs, RandomGen rnd )
    {
        // gameModel = gm;
        // gameParameters = gameModel.getGameParametersModel().getGameParameters();
        gameParameters = gp;

        xCellCount = gameParameters.getBoardParameters().getXCellCount().getValue();
        yCellCount = gameParameters.getBoardParameters().getYCellCount().getValue();
        cellSideCount = gameParameters.getBoardParameters().getCellTopology().sideCount();
        buildSteps = gameParameters.getBaseBuildSteps().getValue();
        //randomGen = gameModel.getRandomGenerator();
        randomGen = rnd;
        players = plyrs;
        //playerCount = gameModel.getPlayersModel().getSelectedPlayerCount();
        playerCount = players.getSelectedPlayerCount();

        //        initBoardState();
    }

    private void initBoardState()
    {
        WrappingMode wrap = (WrappingMode)gameParameters.getBoardParameters().getWrappingMode().getValue();

        boardState = new BoardState( xCellCount, yCellCount );
        boardState.initNeighbours( cellSideCount, wrap );

        for ( Cell cell : this )
            cell.cellState.setPlayerCount( playerCount );
    }

    public void reset( GameParameters gp, PlayerInfos plyrs, RandomGen rnd )
    {
        init( gp, plyrs, rnd );
        initBoardState();

        // setup board

        //        if ( MainModel.debug )
        //            boardState.test();
        //        else
        randomBoardSetup();
        initVisibility();
    }

    /**
     * get cell from its coordinates
     */
    public Cell getCell( int cx, int cy )
    {
        return boardState.getCell( cx, cy );
    }

    public BoardState getBoardState()
    {
        return boardState;
    }

    void commitLevels()
    {
        // copy back level array

        for ( Cell cell : this )
            cell.cellState.copyBackLevel();
    }

    private void setVisibilityAround( Cell cell, int radius, boolean mapOrEnemy )
    {
        int xc = cell.gridX;
        int yc = cell.gridY;
        int xMin = xc - radius;
        int xMax = xc + radius;
        int yMin = yc - radius;
        int yMax = yc + radius;
        int r2 = radius * radius;

        int dx = -radius;
        for ( int x = xMin; x <= xMax; x++ )
        {
            int dy = -radius;
            if ( x >= 0 && x < xCellCount )
                for ( int y = yMin; y <= yMax; y++ )
                {
                    if ( y >= 0 && y < yCellCount )
                    {
                        if ( dx * dx + dy * dy <= r2 )
                            if ( mapOrEnemy )
                                getCell( x, y ).cellState.cellVisible = true;
                            else
                                getCell( x, y ).cellState.enemyVisible = true;
                    }
                    dy++;
                }
            dx++;
        }
    }

    void computeVisibility( int clientPlayerId, boolean watchGame )
    {
        if ( watchGame )
        {
            for ( Cell cell : this )
            {
                cell.cellState.cellVisible = true;
                cell.cellState.enemyVisible = true;
            }
        }
        else
        {
            int r = gameParameters.getVisibilityRange().getValue();

            switch ( (InvisibilityMode)gameParameters.getVisibilityMode().getValue() )
            {
                case INVISIBLE_ENEMY:
                    if ( !gameParameters.getVisibilityRememberCells().getValue() )
                        for ( Cell cell : this )
                            cell.cellState.enemyVisible = false;

                    for ( Cell cell : this )
                        if ( cell.cellState.playerId == clientPlayerId )
                            setVisibilityAround( cell, r, false );
                    break;

                case INVISIBLE_MAP:
                    if ( !gameParameters.getVisibilityRememberCells().getValue() )
                        for ( Cell cell : this )
                            cell.cellState.cellVisible = false;

                    for ( Cell cell : this )
                        if ( cell.cellState.playerId == clientPlayerId )
                            setVisibilityAround( cell, r, true );
                    break;

                default:
                    break;
            }
        }
    }

    /*
     * board generation
     */

    private int randomCellX()
    {
        //        return randomGen.posRandomFloat() * xCellCount;
        return randomGen.posRandomInt( xCellCount );
    }

    private int randomCellY()
    {
        //        return randomGen.posRandomFloat() * yCellCount;
        return randomGen.posRandomInt( yCellCount );
    }

    private void gaussElevation( float xCenter, float yCenter, float xRadius, float yRadius, float amplitude )
    {
        for ( int y = 0; y < yCellCount; y++ )
            for ( int x = 0; x < xCellCount; x++ )
            {
                double x1 = x - xCenter;
                double y1 = y - yCenter;
                double e = amplitude * Math.exp( -(x1 * x1 / xRadius + y1 * y1 / yRadius) );

                getCell( x, y ).cellState.elevation += (int)e;
            }
    }

    private void makePeaks( IntParameter peak, float elevationMax )
    {
        float xc, yc; // center
        float xr, yr; // radii
        float amp; // amplitude

        int peakDensity = peak.getValue();
        float pPeak = (float)peakDensity / (float)peak.getMax();
        float kxr = pPeak * xCellCount * 0.6f;
        float kyr = pPeak * yCellCount * 0.6f;

        int peakCount = 0;
        if ( peakDensity > 0 )
            peakCount = (int)(2.0f + pPeak * xCellCount * yCellCount * 0.02f) + randomGen.posRandomInt( peakDensity / 2 + 1 );

        for ( int p = 0; p < peakCount; p++ )
        {
            xc = randomCellX();
            yc = randomCellY();
            //            xr = randomGen.posRandomFloat() * kxr + 1.0f;
            xr = randomGen.posRandomFloat( kxr ) + 1.0f;
            //            yr = randomGen.posRandomFloat() * kyr + 1.0f;
            yr = randomGen.posRandomFloat( kyr ) + 1.0f;
            amp = randomGen.posRandomFloat() * elevationMax;

            gaussElevation( xc, yc, xr, yr, amp );
        }
    }

    private void randomBoardElevation( InitialisationParameters ip )
    {
        IntParameter sea = ip.getSeaDensity();
        IntParameter hill = ip.getHillDensity();

        for ( Cell cell : this )
        {
            cell.cellState.playerId = Consts.NullId;
            cell.cellState.elevation = 0;
            cell.cellState.isTown = false;
        }

        makePeaks( sea, Consts.maxElevation * -2.0f );
        makePeaks( hill, Consts.maxElevation * 2.0f );

        for ( Cell cell : this )
        {
            int e = cell.cellState.elevation;

            if ( e > Consts.maxElevation )
                e = Consts.maxElevation;
            if ( e < -Consts.maxElevation )
                e = -Consts.maxElevation;

            cell.cellState.elevation = e;
        }
    }

    private void initBaseOrArmy( CellState cs, int playerId, boolean baseOrArmy )
    {
        cs.playerId = playerId;
        cs.isTown = baseOrArmy;

        if ( cs.elevation < 0 )
            cs.elevation = 0;

        cs.setTroopsLevel( Consts.maxTroopsLevel );

        if ( cs.isTown )
        {
            cs.isTown = true;
            cs.townSize = Consts.maxTownSize;
            cs.townBuildStep = buildSteps;
        }
    }

    private static double angleDiff( double angle1, double angle2 )
    {
        double diff = angle2 - angle1;
        while ( diff < -Math.PI )
            diff += Math.PI * 2;
        while ( diff > Math.PI )
            diff -= Math.PI * 2;
        return Math.abs( diff );
    }

    private void circlePoint( int x, int y, double nearAngle, List<Dim2D> points )
    {
        Dim2D inDim = new Dim2D( x, y );

        if ( points.size() == 0 )
        {
            points.add( inDim );
        }
        else if ( !points.contains( inDim ) )
        {
            boolean inserted = false;

            double inAngle = Math.atan2( y, x );
            double diffIn = angleDiff( inAngle, nearAngle );

            int i = 0;
            for ( Dim2D p : points )
            {
                double pAngle = Math.atan2( p.y, p.x );
                double diffP = angleDiff( pAngle, nearAngle );

                if ( diffIn > diffP )
                {
                    points.add( i, inDim );
                    inserted = true;
                    break;
                }
                i++;
            }

            if ( !inserted )
            {
                points.add( inDim );
            }
        }
    }

    private void insertPoints( int x, int y, double nearAngle, List<Dim2D> points )
    {
        circlePoint( x, y, nearAngle, points );
        circlePoint( y, x, nearAngle, points );
        circlePoint( y, -x, nearAngle, points );
        circlePoint( x, -y, nearAngle, points );
        circlePoint( -x, -y, nearAngle, points );
        circlePoint( -y, -x, nearAngle, points );
        circlePoint( -y, x, nearAngle, points );
        circlePoint( -x, y, nearAngle, points );
    }

    // midpoint circle scan conversion using second order differences (Bresenham) 
    private void midpointCircle( int playerId, int xc, int yc, int radius, boolean baseOrArmy, double nearAngle, int count )
    {
        if ( count == 0 )
            return;

        int x = 0;
        int y = radius;
        int d = 1 - radius;
        int deltaE = 3;
        int deltaSE = -2 * radius + 5;

        List<Dim2D> points = new ArrayList<>();
        insertPoints( x, y, nearAngle, points );

        while ( y > x )
        {
            if ( d < 0 )
            {
                d += deltaE;
                deltaE += 2;
                deltaSE += 2;
                x++;
            }
            else
            {
                d += deltaSE;
                deltaE += 2;
                deltaSE += 4;
                x++;
                y--;
            }
            insertPoints( x, y, nearAngle, points );
        }

        int len = points.size();
        for ( int i = 0; i < count; i++ )
        {
            int ind = len - i - 1;
            if ( ind >= 0 )
            {
                Dim2D dim = points.get( ind );
                x = xc + dim.x;
                y = yc + dim.y;

                if ( x >= 0 && x < xCellCount && y >= 0 && y < yCellCount )
                {
                    Cell cell = getCell( x, y );
                    initBaseOrArmy( cell.cellState, playerId, baseOrArmy );
                }
            }
        }
    }

    private void initBasesAndArmies( IntParameter baseOrArmyCount, boolean basesOrArmies )
    {
        int count = baseOrArmyCount.getValue();
        if ( playerCount > 0 && count > 0 )
        {
            double angleStep;
            //            if ( playerCount == 3 )
            //                angleStep = Math.PI / 2.0;
            //            else
            angleStep = 2 * Math.PI / playerCount;

            double radius = (Math.min( xCellCount, yCellCount ) - 2) / 2;
            if ( !basesOrArmies )
                radius--;

            double xc = xCellCount / 2.0;
            double yc = yCellCount / 2.0;

            double angle = 0.0;
            for ( int player = 0; player < playerCount; player++ )
            {
                midpointCircle( player, (int)xc, (int)yc, (int)radius, basesOrArmies, angle, count );
                angle += angleStep;
            }
        }
    }

    private void randomMilitia( InitialisationParameters ip, Cell cell )
    {
        CellState cs = cell.cellState;

        IntParameter militia = ip.getMilitia();
        if ( randomGen.posRandomInt( militia.getMax() * Consts.militiaCorrection ) < militia.getValue() ) // occupied ?
        {
            // by who ?
            int pid = randomGen.posRandomInt( playerCount );

            if ( cs.elevation >= 0 && (cs.playerId == Consts.NullId || cs.playerId == pid) && !cs.isTown )
            {
                cs.playerId = pid;

                // militia strength ?
                cs.setTroopsLevel( randomGen.posRandomFloat( Consts.maxTroopsLevel ) );
            }
        }
    }

    private void randomTowns( InitialisationParameters ip, Cell cell )
    {
        CellState cs = cell.cellState;

        if ( cs.isFreeTerrain() )
        {
            IntParameter towns = ip.getTowns();
            if ( randomGen.posRandomInt( towns.getMax() * Consts.townsCorrection ) < towns.getValue() ) // town present ?
            {
                cs.isTown = true;
                cs.townSize = randomGen.posRandomInt( Consts.maxTownSize - Consts.minTownSize ) + Consts.minTownSize;
                cs.townBuildStep = buildSteps;
            }
        }
    }

    private Cell randomCell()
    {
        int x = randomCellX();
        int y = randomCellY();

        return getCell( x, y );
    }

    private void randomBases( InitialisationParameters ip, int playerId )
    {
        int rbaseCount = ip.getRandomBases().getValue();

        for ( int i = 0; i < rbaseCount; i++ )
        {
            boolean stop = false;
            while ( !stop )
            {
                Cell cell = randomCell();
                CellState cs = cell.cellState;

                if ( cs.elevation >= 0 && cs.playerId == Consts.NullId )
                {
                    cs.isTown = true;
                    cs.townSize = Consts.maxTownSize;
                    cs.townBuildStep = buildSteps;
                    cs.playerId = playerId;
                    cs.troopsLevel[ playerId ] = randomGen.posRandomInt( (int)Consts.maxTroopsLevel - 1 ) + 1;
                    stop = true;
                }
            }
        }
    }

    private void randomBoardSetup()
    {
        InitialisationParameters ip = gameParameters.getInitialisationParameters();

        randomBoardElevation( ip );
        initBasesAndArmies( ip.getBases(), true );
        initBasesAndArmies( ip.getArmies(), false );

        for ( Cell cell : this )
        {
            randomMilitia( ip, cell );
            randomTowns( ip, cell );
        }

        //for ( PlayerInfoModel pim : players.selectedPlayers() )
        SelectedPlayerIterator it = players.selectedPlayers();
        while ( it.hasNext() )
            randomBases( ip, it.next().getPlayerId() );
        //        it.close();
    }

    private void initVisibility()
    {
        boolean cellVisible;
        boolean enemyVisible;

        switch ( (InvisibilityMode)gameParameters.getVisibilityMode().getValue() )
        {
            case NONE:
                cellVisible = true;
                enemyVisible = true;
                break;

            case INVISIBLE_ENEMY:
                cellVisible = true;
                enemyVisible = false;
                break;

            case INVISIBLE_MAP:
                cellVisible = false;
                enemyVisible = true;
                break;

            default:
                throw new MVCModelError( "invalid visibility mode " + gameParameters.getVisibilityMode().getValue() );
        }

        //for ( Cell cell : gameModel.getBoardStateModel() )
        for ( Cell cell : this )
        {
            cell.cellState.cellVisible = cellVisible;
            cell.cellState.enemyVisible = enemyVisible;
        }
    }

    private class Dim2D
    {
        int x;

        int y;

        public Dim2D( int x1, int y1 )
        {
            x = x1;
            y = y1;
        }

        @Override
        public boolean equals( Object obj )
        {
            if ( this == obj )
                return true;
            if ( obj == null )
                return false;
            if ( getClass() != obj.getClass() )
                return false;
            Dim2D other = (Dim2D)obj;
            if ( !getOuterType().equals( other.getOuterType() ) )
                return false;
            if ( x != other.x )
                return false;
            if ( y != other.y )
                return false;
            return true;
        }

        private BoardStateModel getOuterType()
        {
            return BoardStateModel.this;
        }
    }

    // Iterable interface

    @Override
    public CellIterator iterator()
    {
        return boardState.iterator();
    }
}
