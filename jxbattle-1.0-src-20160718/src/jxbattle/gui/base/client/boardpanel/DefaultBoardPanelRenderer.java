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

package jxbattle.gui.base.client.boardpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.Random;

import jxbattle.bean.common.game.Cell;
import jxbattle.bean.common.game.CellState;
import jxbattle.bean.common.game.UserCommand;
import jxbattle.bean.common.game.UserCommand.UserCommandCode;
import jxbattle.bean.common.parameters.game.WrappingMode;
import jxbattle.bean.common.player.XBColor;
import jxbattle.common.Consts;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChange;

class DefaultBoardPanelRenderer implements BoardPanelRenderer
{
    private class Dim2D
    {
        double w;

        double h;
    }

    private BoardPanelModel model;

    private Color[] seaPalette; // color table for sea cells

    private Color[] hillPalette; // color table for hill cells

    /*
    <-----cellSize----->
    +------------------+
    |   cellInnerSize  |
    |   <---------->   |
    |   +----------+   |
    |   |          |   |
    |   |          |   |
    |   |    *     |   |   * = objects
    |   |          |   |
    |   |          |   |
    |   +----------+   |
    |                  |
    |                  |
    +------------------+
    <->
    cellMargin
    */

    private int cellSize;

    private int halfCellSize;

    private int cellInnerSize; // // size of objects rendering space

    private int cellMargin; // margin (in pixels) for rendering of objects in cell

    // precomputed values

    private int maxTownBuildStep;

    private XBColor playerColors[];

    //    private int cellXCount; // grid horizontal size
    //    private int cellYCount; // grid vertical size

    private WrappingMode wrapMode;

    // temporary values

    private Dim2D d1;

    private Dim2D d2;

    private Random randgen;

    public DefaultBoardPanelRenderer()
    {
        d1 = new Dim2D();
        d2 = new Dim2D();

        seaPalette = interpolatePalette( Consts.seaMin, Consts.seaMax, Consts.maxElevation );
        hillPalette = interpolatePalette( Consts.hillMin, Consts.hillMax, Consts.maxElevation );
        randgen = new Random( System.currentTimeMillis() );
    }

    private void setCellSize( int size )
    {
        cellSize = size;
        halfCellSize = cellSize >> 1;
        cellMargin = 3;
        cellInnerSize = cellSize - 2 * cellMargin;
    }

    /**
     * precompute some value to speed up further execution
     */
    private void preCompute()
    {
        maxTownBuildStep = model.getGameModel().getGameParametersModel().getGameParameters().getBaseBuildSteps().getValue();
        setCellSize( model.getCellSize() );

        int nb = model.getGameModel().getPlayersModel().getSelectedPlayerCount();
        playerColors = new XBColor[ nb ];
        for ( int id = 0; id < nb; id++ )
            playerColors[ id ] = model.getGameModel().getPlayersModel().getColorFromPlayerId( id );

        //        cellXCount = model.getGameModel().getGameParametersModel().getGameParameters().getBoardParameters().getXCellCount().getValue();
        //        cellYCount = model.getGameModel().getGameParametersModel().getGameParameters().getBoardParameters().getYCellCount().getValue();

        wrapMode = model.getGameModel().getGameParametersModel().getGameParameters().getBoardParameters().getWrappingMode().getValue();
    }

    private void renderCellBackground( Graphics g, Color col )
    {
        g.setColor( col );
        g.fillRect( 0, 0, cellSize, cellSize );
    }

    private void getObjectCornerDim( double k, Dim2D corner, Dim2D dim )
    {
        double w = (1.0 - k) * cellInnerSize / 2.0;
        double v1 = cellMargin + w;
        double v2 = cellSize - cellMargin - w;
        corner.w = v1;
        corner.h = v1;
        dim.w = v2 - v1;
        dim.h = v2 - v1;
    }

    private void getObjectCorners( double k, Dim2D corner1, Dim2D corner2 )
    {
        double w = (1.0 - k) * cellInnerSize / 2.0;
        double v1 = cellMargin + w;
        double v2 = cellSize - cellMargin - w;
        corner1.w = v1;
        corner1.h = v1;
        corner2.w = v2;
        corner2.h = v2;
    }

    private static Color[] interpolatePalette( XBColor mini, XBColor maxi, int n )
    {
        int count = n + 1;

        Color[] pal = new Color[ count ];

        Color min = mini.getAwtColor();
        Color max = maxi.getAwtColor();

        int redAmp = max.getRed() - min.getRed();
        int greenAmp = max.getGreen() - min.getGreen();
        int blueAmp = max.getBlue() - min.getBlue();

        for ( int i = 0; i < count; i++ )
        {
            float f = (float)i / (float)count;

            int r = (int)(min.getRed() + f * redAmp);
            int g = (int)(min.getGreen() + f * greenAmp);
            int b = (int)(min.getBlue() + f * blueAmp);

            pal[ i ] = new Color( r, g, b );
        }

        return pal;
    }

    // BoardPanelRenderer interface

    @Override
    public void setModel( BoardPanelModel m )
    {
        unsubscribeModel();
        model = m;
        preCompute();
        subscribeModel();
    }

    @Override
    public void renderGrid( Graphics g, int gridWidth, int gridHeight, int outerCellSize )
    {
        g.setColor( Consts.gridColor );

        for ( int x = 0; x <= gridWidth; x += outerCellSize )
            g.drawLine( x, 0, x, gridHeight );

        for ( int y = 0; y <= gridHeight; y += outerCellSize )
            g.drawLine( 0, y, gridWidth, y );
    }

    @Override
    public void renderSeaCell( Graphics g, CellState cellState )
    {
        renderCellBackground( g, seaPalette[ -cellState.elevation ] );
    }

    @Override
    public void renderLandBackground( Graphics g, CellState cellState )
    {
        renderCellBackground( g, hillPalette[ cellState.elevation ] );
    }

    @Override
    public void renderInvisibleCell( Graphics g, CellState cellState )
    {
        renderCellBackground( g, Consts.invisibleCell );
    }

    @Override
    public void renderBase( Graphics g, CellState cellState )
    {
        int playerId = cellState.playerId;

        if ( playerId == Consts.NullId )
            g.setColor( Color.BLACK );
        else
        {
            XBColor col = playerColors[ playerId ];
            g.setColor( col.getAwtInverse() );
        }

        getObjectCornerDim( (double)cellState.townSize / (double)Consts.maxTownSize, d1, d2 );

        g.drawArc( (int)d1.w, (int)d1.h, (int)d2.w, (int)d2.h, 0, cellState.townBuildStep * 360 / maxTownBuildStep );
        g.drawArc( (int)d1.w + 1, (int)d1.h + 1, (int)d2.w - 2, (int)d2.h - 2, 0, cellState.townBuildStep * 360 / maxTownBuildStep );
    }

    @Override
    public void renderPlayer( Graphics g, CellState cs, int playerId )
    {
        XBColor col = playerColors[ playerId ];
        Color c = col.getAwtColor();
        g.setColor( c );

        double k = cs.troopsLevel[ playerId ] / Consts.maxTroopsLevel;
        if ( k < 0.1f )
            k = 0.1f;

        getObjectCornerDim( k, d1, d2 );

        g.fillRect( (int)d1.w, (int)d1.h, (int)d2.w, (int)d2.h );
    }

    @Override
    public void renderPlayerMove( Graphics g, CellState cellState, int direction )
    {
        int pid = cellState.playerId;

        XBColor col = playerColors[ pid ];
        g.setColor( col.getAwtColor() );

        double k = cellState.getTroopsLevel() / Consts.maxTroopsLevel;
        boolean renderHalf = cellState.getTroopsLevel() <= Consts.halfVectorTroopsLevel;

        if ( renderHalf )
        {
            getObjectCornerDim( 0.25, d1, d2 );

            switch ( direction )
            {
                case 0: // up
                    g.fillRect( halfCellSize - 1, halfCellSize - (int)d2.h, 3, (int)d2.h );
                    break;

                case 1: // right
                    g.fillRect( halfCellSize, halfCellSize - 1, (int)d2.w, 3 );
                    break;

                case 2: // down
                    g.fillRect( halfCellSize - 1, halfCellSize, 3, (int)d2.h + 1 );
                    break;

                case 3: // left
                    g.fillRect( halfCellSize - (int)d2.w, halfCellSize - 1, (int)d2.w, 3 );
                    break;

                default:
                    throw new MVCModelError( "invalid move direction" );
            }
        }
        else
        {
            getObjectCorners( k, d1, d2 );

            switch ( direction )
            {
                case 0: // up
                    g.fillRect( halfCellSize - 1, 0, 3, halfCellSize );
                    g.setColor( col.getAwtInverse() );
                    g.drawLine( halfCellSize, (int)d1.h, halfCellSize, halfCellSize );
                    break;

                case 1: // right
                    g.fillRect( halfCellSize, halfCellSize - 1, cellSize - halfCellSize, 3 );
                    g.setColor( col.getAwtInverse() );
                    g.drawLine( halfCellSize, halfCellSize, (int)d2.w, halfCellSize );
                    break;

                case 2: // down
                    g.fillRect( halfCellSize - 1, halfCellSize, 3, cellSize - halfCellSize );
                    g.setColor( col.getAwtInverse() );
                    g.drawLine( halfCellSize, halfCellSize, halfCellSize, (int)d2.h );
                    break;

                case 3: // left
                    g.fillRect( 0, halfCellSize - 1, cellSize - halfCellSize, 3 );
                    g.setColor( col.getAwtInverse() );
                    g.drawLine( (int)d1.w, halfCellSize, halfCellSize, halfCellSize );
                    break;

                default:
                    throw new MVCModelError( "invalid move direction" );
            }
        }
    }

    @Override
    public void renderFight( Graphics g, CellState cs )
    {
        //int[] sortedIds = sortFightingPlayers( cs );
        //int oneId = sortedIds[ 0 ];
        //int twoId = sortedIds[ 1 ];
        int oneId = cs.biggestFightingPlayers[ 0 ];
        int twoId = cs.biggestFightingPlayers[ 1 ];

        //if ( !debugMode )
        {
            // case where opponents are the same strength -> ramdomly pick player to render

            if ( cs.troopsLevel[ oneId ] == cs.troopsLevel[ twoId ] )
            {
                boolean rand = randgen.nextInt( 2 ) == 0;
                renderPlayer( g, cs, rand ? oneId : twoId );
            }
            else
            {
                // normal case, opponents with different strengths

                renderPlayer( g, cs, oneId );
                renderPlayer( g, cs, twoId );
            }
        }

        // draw "X" cross to indicate fight

        int max = cellSize - 1;
        g.setColor( playerColors[ oneId ].getAwtColor() );
        g.drawLine( 0, 0, max, max );
        g.setColor( playerColors[ twoId ].getAwtColor() );
        g.drawLine( max, 0, 0, max );
    }

    private static void drawDottedLine( Graphics g, int x1, int y1, int x2, int y2, float width, float stroke )
    {
        Graphics2D g2d = (Graphics2D)g;
        Stroke oldStroke = g2d.getStroke();
        Object oldRH = g2d.getRenderingHint( RenderingHints.KEY_ANTIALIASING );

        Stroke drawingStroke = new BasicStroke( width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { stroke }, 0 );
        g2d.setStroke( drawingStroke );

        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        g2d.drawLine( x1, y1, x2, y2 );

        g2d.setStroke( oldStroke );
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, oldRH );
    }

    @Override
    public void renderFightMove( Graphics g, int direction, int leadPlayerId )
    {
        getObjectCorners( 1.0, d1, d2 );

        XBColor col = playerColors[ leadPlayerId ];

        g.setColor( col.getAwtInverse() );

        switch ( direction )
        {
            case 0: // up
                drawDottedLine( g, halfCellSize, (int)d1.h, halfCellSize, halfCellSize, 1.0f, 2.0f );
                break;

            case 1: // right
                drawDottedLine( g, halfCellSize, halfCellSize, (int)d2.w, halfCellSize, 1.0f, 2.0f );
                break;

            case 2: // down
                drawDottedLine( g, halfCellSize, halfCellSize, halfCellSize, (int)d2.h, 1.0f, 2.0f );
                break;

            case 3: // left
                drawDottedLine( g, (int)d1.w, halfCellSize, halfCellSize, halfCellSize, 1.0f, 2.0f );
                break;

            default:
                throw new MVCModelError( "invalid move direction" );
        }

    }

    @Override
    public void renderMarch( Graphics g, Cell cell )
    {
        for ( UserCommand cmd : cell.commands )
            if ( model.getClientPlayerId() == cmd.playerId )
            {
                int pid = cmd.playerId;
                XBColor col = playerColors[ pid ];

                switch ( cmd.code )
                {
                    case SET_MARCH:
                    case SWAP_MARCH:
                        g.setColor( col.getAwtColor() );
                        switch ( cmd.arg1 )
                        {
                            case 1:
                                g.fillRect( 0, halfCellSize - 5, cellSize - halfCellSize, 3 );
                                g.fillRect( 0, halfCellSize + 3, cellSize - halfCellSize, 3 );
                                break;

                            case 3: // up
                                g.fillRect( halfCellSize - 5, 0, 3, halfCellSize );
                                g.fillRect( halfCellSize + 3, 0, 3, halfCellSize );
                                break;

                            case 5:
                                g.fillRect( halfCellSize, halfCellSize - 5, cellSize - halfCellSize, 3 );
                                g.fillRect( halfCellSize, halfCellSize + 3, cellSize - halfCellSize, 3 );
                                break;

                            case 7:
                                g.fillRect( halfCellSize - 5, halfCellSize, 3, cellSize - halfCellSize );
                                g.fillRect( halfCellSize + 3, halfCellSize, 3, cellSize - halfCellSize );
                                break;

                            default:
                                break;
                        }
                        break;

                    case STOP_MARCH:
                        if ( cell.cellState.getTroopsLevel() > Consts.maxTroopsLevel * 0.1 )
                            g.setColor( col.getAwtInverse() );
                        else
                            g.setColor( col.getAwtColor() );
                        getObjectCornerDim( 0.3, d1, d2 );
                        g.drawRect( (int)d1.w, (int)d1.h, (int)d2.w, (int)d2.h );
                        break;

                    default:
                        break;
                }
            }
    }

    //    @Override
    //    public void renderParaTroops( Graphics g, int paraTroopsId )
    //    {
    //        XBColor col = playerColors[ paraTroopsId ];
    //        g.setColor( col.getAwtColor() );
    //
    //        int x = cellMargin; //cellSize / 2;
    //        int y = cellMargin;
    //        int dim = cellSize / 3;
    //        int dim_d2 = dim >> 1;
    //        g.fillArc( x, y, dim, dim, 0, 180 );
    //        g.drawLine( x, y + dim_d2, x + dim_d2, y + dim );
    //        g.drawLine( x + dim_d2, y + dim_d2, x + dim_d2, y + dim );
    //        g.drawLine( x + dim, y + dim_d2, x + dim_d2, y + dim );
    //    }

    @Override
    public void renderParaTroops( Graphics g, CellState cs )
    {
        XBColor col = playerColors[ cs.paraTroopsId ];
        g.setColor( col.getAwtColor() );

        int x = cellMargin; //cellSize / 2;
        int y = cellMargin;
        int dim = cellSize / 3;
        int dim_d2 = dim >> 1;
        g.fillArc( x, y, dim, dim, 0, 180 );
        g.drawLine( x, y + dim_d2, x + dim_d2, y + dim );
        g.drawLine( x + dim_d2, y + dim_d2, x + dim_d2, y + dim );
        g.drawLine( x + dim, y + dim_d2, x + dim_d2, y + dim );
    }

    @Override
    public void renderGunTroops( Graphics g, CellState cs )
    {
        XBColor col = playerColors[ cs.gunTroopsId ];
        g.setColor( col.getAwtColor() );

        int x = cellMargin; //cellSize / 2;
        int y = cellMargin;
        int dim = cellSize / 4;

        g.fillArc( x, y, x + dim, y + dim, 0, 360 );
    }

    @Override
    public void renderGunParaPath( Graphics g, int sourceCellX, int sourceCellY, int destCellX, int destCellY, boolean isValid )
    {
        g.setColor( isValid ? Color.BLUE : Color.RED );
        //drawDottedLine( g, sourceCellX, sourceCellY, destCellX, destCellY );

        int x1 = model.cellToPixel( sourceCellX ) + halfCellSize;
        int y1 = model.cellToPixel( sourceCellY ) + halfCellSize;
        int x2 = model.cellToPixel( destCellX ) + halfCellSize;
        int y2 = model.cellToPixel( destCellY ) + halfCellSize;
        drawDottedLine( g, x1, y1, x2, y2, 2.0f, 5.0f );

        int gw = model.getGridXCellCount();
        int gh = model.getGridYCellCount();
        int sx = sourceCellX;
        int sy = sourceCellY;
        int dx = destCellX;
        int dy = destCellY;

        if ( isValid )
        {
            if ( wrapMode == WrappingMode.LEFT_RIGHT || wrapMode == WrappingMode.FULL )
            {
                if ( dx < 0 )
                {
                    sx += gw;
                    dx += gw;
                }
                else if ( dx >= gw )
                {
                    sx -= gw;
                    dx -= gw;
                }
            }
            if ( wrapMode == WrappingMode.TOP_DOWN || wrapMode == WrappingMode.FULL )
            {
                if ( dy < 0 )
                {
                    sy += gh;
                    dy += gh;
                }
                else if ( dy >= gh )
                {
                    sy -= gh;
                    dy -= gh;
                }
            }

            x1 = model.cellToPixel( sx ) + halfCellSize;
            y1 = model.cellToPixel( sy ) + halfCellSize;
            x2 = model.cellToPixel( dx ) + halfCellSize;
            y2 = model.cellToPixel( dy ) + halfCellSize;
            drawDottedLine( g, x1, y1, x2, y2, 2.0f, 5.0f );
        }
    }

    @Override
    public void renderManagedCommand( Graphics g, UserCommandCode command, XBColor playerColor )
    {
        //        g.setColor( Consts.white.getAwtInverse() );
        g.setColor( playerColor.getAwtInverse() );
        String m = null;
        switch ( command )
        {
            case MANAGED_FILL_GROUND:
                m = "F";
                break;

            case MANAGED_DIG_GROUND:
                m = "D";
                break;

            case MANAGED_BUILD_BASE:
                m = "B";
                break;

            case MANAGED_SCUTTLE_BASE:
                m = "S";
                break;

            case MANAGED_TROOPS_PARA:
                m = "P";
                break;

            case MANAGED_TROOPS_GUN:
                m = "G";
                break;

            default:
                break;
        }
        g.drawString( m, cellMargin, cellSize - cellMargin );
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        switch ( (XBMVCModelChangeId)change.getChangeId() )
        {
            case GridCellSizeChanged:
                setCellSize( model.getCellSize() );
                break;

            default:
                break;
        }
    }

    @Override
    public void subscribeModel()
    {
        if ( model != null )
            model.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        if ( model != null )
            model.removeObserver( this );
    }

    @Override
    public void close()
    {
        unsubscribeModel();
    }
}
