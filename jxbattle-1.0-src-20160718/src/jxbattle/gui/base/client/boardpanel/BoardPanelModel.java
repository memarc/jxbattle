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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jxbattle.bean.client.input.XBInputInfo;
import jxbattle.bean.client.input.XBInputInfoMapping;
import jxbattle.bean.client.input.XBKeyEvent;
import jxbattle.bean.client.input.XBMouseEvent;
import jxbattle.bean.common.game.Cell;
import jxbattle.bean.common.game.UserCommand.UserCommandCode;
import jxbattle.bean.common.parameters.game.WrappingMode;
import jxbattle.common.Consts;
import jxbattle.model.client.GameEngine;
import jxbattle.model.client.RandomGen;
import jxbattle.model.common.game.GameModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.list.IndexedIterator;
import org.generic.mvc.model.BoolModel;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;

public class BoardPanelModel extends MVCModelImpl implements MVCModelObserver
{
    private GameEngine gameEngine;

    private GameModel gameModel;

    /**
     * this client's player id
     */
    private int clientPlayerId;

    // private MouseInfo mouseInfo;

    private XBInputInfoMapping inputInfoMapping;

    private XBMouseEvent mouseEvent;

    private XBKeyEvent keyEvent;

    private int cellGridX;

    private int cellGridY;

    private int inCellMouseX; // mouse X coordinate in cell (with center as reference)

    private int inCellMouseY; // mouse Y coordinate in cell (with center as reference)

    private int cellZone;

    private int cellSize; // cell size without grid (if specified)

    private int cellSize_2; // half cell size

    private int outerCellSize; // cell size with grid (if no grid present, equals cellSize)

    private int gridPixelWidth;

    private int gridPixelHeight;

    private int cellXCount;

    private int cellYCount;

    private RandomGen randomGen;

    private boolean allowRepeatKey;

    //    private KeyInfo keyInfo;

    private BoolModel drawGrid;

    private List<GunsParaPath> gunParaPaths;

    private WrappingMode wrapMode;

    private boolean isValidMouseposition;

    public BoardPanelModel( GameEngine ge, XBInputInfoMapping iim )
    {
        gameEngine = ge;
        inputInfoMapping = iim;
        gameModel = gameEngine.getGameModel();
        clientPlayerId = gameEngine.getClientPlayerId();
        randomGen = gameEngine.getRandomGenerator();
        cellGridX = -1;
        cellGridY = -1;
        drawGrid = new BoolModel( true, XBMVCModelChangeId.DrawGridChanged );
        gunParaPaths = new ArrayList<>();
        setCellSize( this, Consts.defaultCellSize );
        cellXCount = gameModel.getGameParametersModel().getGameParameters().getBoardParameters().getXCellCount().getValue();
        cellYCount = gameModel.getGameParametersModel().getGameParameters().getBoardParameters().getYCellCount().getValue();
        wrapMode = gameModel.getGameParametersModel().getGameParameters().getBoardParameters().getWrappingMode().getValue();

        setAllowRepeatKey( gameModel.getGameParametersModel().getAllowKeyRepeatModel().getValue() );

        subscribeModel();
    }

    GameModel getGameModel()
    {
        return gameModel;
    }

    int getClientPlayerId()
    {
        return clientPlayerId;
    }

    RandomGen getRandomGen()
    {
        return randomGen;
    }

    boolean isAllowRepeatKey()
    {
        return allowRepeatKey;
    }

    public void setAllowRepeatKey( boolean b )
    {
        allowRepeatKey = b;
    }

    //    private boolean postUserCommand( Object sender, XBInputInfo me )
    //    {
    //        UserCommandCode uc = inputInfoMapping.get( me );
    //        if ( uc != null )
    //        {
    //            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.UserCommand, uc ) );
    //            return true;
    //        }
    //
    //        return false;
    //    }

    UserCommandCode mapInputInfo( XBInputInfo ii )
    {
        return inputInfoMapping.get( ii );
    }

    //    void mouseClick( Object sender, XBMouseEvent me )
    //    {
    //        if ( isValidMouseposition )
    //            postUserCommand( sender, me );
    //    }

    void mouseMoved( Object sender, int mx, int my, int modif )
    {
        mouseEvent = new XBMouseEvent( mx, my, modif );

        isValidMouseposition = computeValidMousePosition( mouseEvent );
        cellGridX = computeCellGridXFromMouse( mouseEvent );
        cellGridY = computeCellGridYFromMouse( mouseEvent );
        inCellMouseX = -1;
        inCellMouseY = -1;
        //        mouseInfo = new XBMouseInfo( mx, my, modif );
        //        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.MouseMoved, mouseInfo ) );

        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.MouseMoved, mouseEvent ) );
    }

    void mouseWheel( Object sender, int mx, int my, int modif, int inc )
    {
        //        mouseInfo = new XBMouseInfo( mx, my, modif, inc );
        //        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.MouseWheel, mouseInfo ) );
        mouseEvent = new XBMouseEvent( mx, my, modif, inc );
        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.MouseWheel, mouseEvent ) );
    }

    void keyPressed( Object sender, int kc, int modif )
    {
        //        if ( validMouseposition() )
        //        {
        //            keyInfo = new KeyInfo( kc, modif, true );
        //            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.KeyPressed, keyInfo ) );
        //        }

        if ( isValidMouseposition )
        {
            keyEvent = new XBKeyEvent( kc, modif, true );
            //            postUserCommand( sender, keyEvent );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.KeyPressed, keyEvent ) );
        }
    }

    void keyReleased( Object sender )
    {
        //        if ( keyInfo != null )
        //        {
        //            keyInfo.setPressedOrReleased( false );
        //            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.KeyReleased, keyInfo ) );
        //        }

        if ( isValidMouseposition && keyEvent != null )
        {
            keyEvent.setPressedOrReleased( false );
            //            postUserCommand( sender, keyEvent );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.KeyReleased, keyEvent ) );
        }
    }

    private void resetCellZone()
    {
        cellZone = -1;
    }

    void leftMouse( Object sender, int mx, int my, int modif )
    {
        //        if ( validMouseposition() )
        //        {
        //            mouseInfo = new MouseInfo( mx, my, modif );
        //            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.LeftMouseClicked, mouseInfo ) );
        //        }
        if ( isValidMouseposition )
        {
            resetCellZone();
            XBMouseEvent me = new XBMouseEvent( mx, my, modif );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.LeftMouseClicked, me ) );
        }
    }

    void middleMouse( Object sender, int mx, int my, int modif )
    {
        //            if ( validMouseposition() )
        //            {
        //                mouseInfo = new MouseInfo( mx, my, modif );
        //                notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.MiddleMouseClicked, mouseInfo ) );
        //            }
        if ( isValidMouseposition )
        {
            resetCellZone();
            XBMouseEvent me = new XBMouseEvent( mx, my, modif );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.MiddleMouseClicked, me ) );
        }
    }

    void rightMouse( Object sender, int mx, int my, int modif )
    {
        //            if ( validMouseposition() )
        //            {
        //                mouseInfo = new MouseInfo( mx, my, modif );
        //                notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.RightMouseClicked, mouseInfo ) );
        //            }
        if ( isValidMouseposition )
        {
            resetCellZone();
            XBMouseEvent me = new XBMouseEvent( mx, my, modif );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.RightMouseClicked, me ) );
        }
    }

    int getGridXCellCount()
    {
        return cellXCount;
    }

    int getGridYCellCount()
    {
        return cellYCount;
    }

    public int getOuterCellSize()
    {
        return outerCellSize;
    }

    /*
    <----outerCellSize--->
     <-----cellSize----->
    gggggggggggggggggggggg
    g+------------------+g
    g|   cellInnerSize  |g
    g|   <---------->   |g
    g|   +----------+   |g
    g|   |          |   |g
    g|   |          |   |g
    g|   |    *     |   |g   * = objects
    g|   |          |   |g   g = grid pixels (if grid present)
    g|   |          |   |g
    g|   +----------+   |g
    g|                  |g
    g|                  |g
    g+------------------+g
    gggggggggggggggggggggg
      <->
      cellMargin
    */

    public int getCellSize()
    {
        return cellSize;
    }

    public void setCellSize( Object sender, int i )
    {
        if ( i > 0 )
        {
            cellSize = i;

            // outer cell size (optionnally include grid)

            outerCellSize = cellSize;
            if ( drawGrid.getValue() )
                outerCellSize++;

            // cells x,y pixel coordinates

            //            int x, y;
            //            if ( drawGrid )
            //                x = y = 1;
            //            else
            //                x = y = 0;
            //
            //            CellIterator it = gameModel.getBoardStateModel().iterator();
            //            while ( it.hasNext() )
            //            {
            //                Cell cell = it.next();
            //
            //                cell.pixelX = x;
            //                cell.pixelY = y;
            //
            //                y += outerCellSize;
            //                if ( it.isLastLine() )
            //                {
            //                    x += outerCellSize;
            //                    y = 1;
            //                }
            //            }

            //        cellMargin = 3;
            //        cellInnerSize = cellSize - 2 * cellMargin;

            cellSize_2 = cellSize >> 1;

            gridPixelWidth = 0;
            gridPixelHeight = 0;
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.GridCellSizeChanged ) );
        }
    }

    public void modifyCellSize( Object sender, int inc )
    {
        setCellSize( sender, cellSize + inc );
    }

    public BoolModel getDrawGridModel()
    {
        return drawGrid;
    }

    boolean getDrawGrid()
    {
        return drawGrid.getValue();
    }

    int getGridPixelWidth()
    {
        if ( gridPixelWidth == 0 )
        {
            //            int cellxCount = gameModel.getGameParametersModel().getGameParameters().getBoardParameters().getXCellCount().getValue();
            //            gridPixelWidth = cellxCount * outerCellSize;
            gridPixelWidth = cellToPixel( cellXCount );
        }

        return gridPixelWidth;
    }

    int getGridPixelHeight()
    {
        if ( gridPixelHeight == 0 )
        {
            //            int cellyCount = gameModel.getGameParametersModel().getGameParameters().getBoardParameters().getYCellCount().getValue();
            //            gridPixelHeight = cellyCount * outerCellSize;
            gridPixelHeight = cellToPixel( cellYCount );
        }

        return gridPixelHeight;
    }

    private boolean pixelInsideBoard( int x, int y )
    {
        return x >= 0 && y >= 0 && x < getGridPixelWidth() && y < getGridPixelHeight();
    }

    private boolean pixelOnGrid( int x, int y )
    {
        return (drawGrid.getValue()) && ((x % outerCellSize == 0) || (y % outerCellSize == 0));
    }

    private int pixelToCell( int p )
    {
        if ( drawGrid.getValue() )
            return (p - 1) / outerCellSize;
        return p - 1 / outerCellSize;
    }

    public int cellToPixel( int c )
    {
        return c * outerCellSize;
    }

    //    private boolean validMousePosition()
    //    {
    //        if ( mouseInfo == null )
    //            return false;
    //        
    //        int mx = mouseInfo.getX();
    //        int my = mouseInfo.getY();
    //        return pixelInsideBoard( mx, my ) && !pixelOnGrid( mx, my );
    //    }

    private boolean computeValidMousePosition( XBMouseEvent me )
    {
        int mx = me.getX();
        int my = me.getY();
        return pixelInsideBoard( mx, my ) && !pixelOnGrid( mx, my );
    }

    boolean isCellValid( int cx, int cy )
    {
        return gameEngine.isCellValid( cx, cy, wrapMode, cellXCount, cellYCount );
    }

    boolean isMyCell( Cell cell )
    {
        return gameEngine.isMyCell( cell.gridX, cell.gridY );
    }

    Point getDestinationCell( int sourceCellGridX, int sourceCellGridY, int distFromCenter, int angleFromCenter, int range )
    {
        return gameEngine.getDestinationCell( sourceCellGridX, sourceCellGridY, distFromCenter, angleFromCenter, range );
    }

    private int computeCellGridXFromMouse( XBMouseEvent me )
    {
        if ( isValidMouseposition )
            return pixelToCell( me.getX() );
        return -1;
    }

    public int getCellGridXFromMouse()
    {
        return cellGridX;
    }

    private int computeCellGridYFromMouse( XBMouseEvent me )
    {
        if ( isValidMouseposition )
            return pixelToCell( me.getY() );
        return -1;
    }

    public int getCellGridYFromMouse()
    {
        return cellGridY;
    }

    private int computeInCellMouse( int pixelPos )
    {
        if ( isValidMouseposition )
        {
            if ( drawGrid.getValue() )
                return (pixelPos % outerCellSize - 1) - cellSize_2;

            return (pixelPos % outerCellSize) - cellSize_2;
        }
        return -1;
    }

    /** 
     * @return mouse X coordinate in cell (with center as reference)
     */
    //    public int getInCellMouseX()
    //    {
    //        if ( inCellMouseX == -1 )
    //        {
    //            if ( drawGrid.getValue() )
    //                inCellMouseX = (mouseInfo.getX() % outerCellSize - 1) - cellSize_2;
    //            else
    //                inCellMouseX = (mouseInfo.getX() % outerCellSize) - cellSize_2;
    //        }
    //        return inCellMouseX;
    //    }

    private int getInCellMouseX()
    {
        if ( inCellMouseX == -1 )
            inCellMouseX = computeInCellMouse( mouseEvent.getX() );
        return inCellMouseX;
    }

    /** 
     * @return mouse Y coordinate in cell (with center as reference)
     */
    //    public int getInCellMouseY()
    //    {
    //        if ( inCellMouseY == -1 )
    //        {
    //            if ( drawGrid.getValue() )
    //                inCellMouseY = (mouseInfo.getY() % outerCellSize - 1) - cellSize_2;
    //            else
    //                inCellMouseY = (mouseInfo.getY() % outerCellSize) - cellSize_2;
    //        }
    //        return inCellMouseY;
    //    }

    private int getInCellMouseY()
    {
        if ( inCellMouseY == -1 )
            inCellMouseY = computeInCellMouse( mouseEvent.getY() );
        return inCellMouseY;
    }

    /**
     * @return mouse distance from cell center, normalised (100 = center to side)
     */
    public int getMouseDistanceFromCellCenter()
    {
        int x = getInCellMouseX();
        int y = getInCellMouseY();
        return (x * x + y * y) * 100 / cellSize_2 / cellSize_2;
    }

    /**
     * @return angle (-180 to 180 degrees) between (cell horizontal axis through center) and (mouse position-center)
     */
    public int getMouseAngleFromCellCenter()
    {
        //        int x = getInCellMouseX();
        //        int y = getInCellMouseY();
        //        double a = Math.atan2( y, x );
        double a = Math.atan2( getInCellMouseY(), getInCellMouseX() );
        return (int)(a * 180 / Math.PI);
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
     * compute cell zone on mouse click
     */
    //    int getCellZone()
    //    {
    //        //if ( mouseOnGrid() )
    //        if ( !validMouseposition() )
    //            return -1;
    //
    //        //        int xInCell = mouseX % outerCellSize - 1;
    //        //        int yInCell = mouseY % outerCellSize - 1;
    //        //        xInCell -= cellSize / 2; // center
    //        //        yInCell -= cellSize / 2;
    //        int xInCell = getInCellMouseX();
    //        int yInCell = getInCellMouseY();
    //
    //        if ( xInCell * xInCell + yInCell * yInCell < cellSize * cellSize / 20 )
    //            return 0;
    //
    //        double a = Math.atan2( yInCell, xInCell ) + Math.PI;
    //        a /= Math.PI / 8.0;
    //        int an = (int)a;
    //        int z = 1;
    //        if ( an != 0 && an < 15 )
    //            z = (an + 1) / 2 + 1;
    //
    //        return z;
    //    }

    //    int getCellZone()
    //    {
    //        if ( !validMouseposition() )
    //            return -1;
    //
    //        int xInCell = getInCellMouseX();
    //        int yInCell = getInCellMouseY();
    //
    //        if ( xInCell * xInCell + yInCell * yInCell < cellSize * cellSize / 20 )
    //            return 0;
    //
    //        double a = Math.atan2( yInCell, xInCell ) + Math.PI;
    //        a /= Math.PI / 8.0;
    //        int an = (int)a;
    //        int z = 1;
    //        if ( an != 0 && an < 15 )
    //            z = (an + 1) / 2 + 1;
    //
    //        return z;
    //    }

    private int computeCellZone()
    {
        if ( isValidMouseposition )
        {
            int xInCell = getInCellMouseX();
            int yInCell = getInCellMouseY();

            if ( xInCell * xInCell + yInCell * yInCell < cellSize * cellSize / 20 )
                return 0;

            double a = Math.atan2( yInCell, xInCell ) + Math.PI;
            a /= Math.PI / 8.0;
            int an = (int)a;
            int z = 1;
            if ( an != 0 && an < 15 )
                z = (an + 1) / 2 + 1;

            return z;
        }
        return -1;
    }

    public int getCellZone()
    {
        if ( cellZone == -1 )
            cellZone = computeCellZone();
        return cellZone;
    }

    //    public void toto() // TODO a virer
    //    {
    //        gunParaPaths.add( new GunsParaPath( 21, 9, 100, 20, 3, false ) );
    //    }

    private void addGunsParaPath( int range, boolean isTemporary )
    {
        //        int cx = getCellGridXFromMouse();
        //        int cy = getCellGridYFromMouse();
        //        if ( gameEngine.isMyCell( cx, cy ) )
        //            gunParaPaths.add( new GunsParaPath( cx, cy, getMouseDistanceFromCellCenter(), getMouseAngleFromCellCenter(), range, isTemporary ) );
        if ( gameEngine.isMyCell( cellGridX, cellGridY ) )
            gunParaPaths.add( new GunsParaPath( cellGridX, cellGridY, getMouseDistanceFromCellCenter(), getMouseAngleFromCellCenter(), range, isTemporary ) );
    }

    void addParaPath( boolean isTemporary )
    {
        addGunsParaPath( gameModel.getGameParametersModel().getGameParameters().getParaTroopsRange().getValue(), isTemporary );
    }

    void addGunsPath( boolean isTemporary )
    {
        addGunsParaPath( gameModel.getGameParametersModel().getGameParameters().getGunTroopsRange().getValue(), isTemporary );
    }

    void deleteTemporaryGunsParaPaths()
    {
        Iterator<GunsParaPath> it = gunParaPaths.iterator();
        while ( it.hasNext() )
        {
            GunsParaPath p = it.next();
            //            if ( p.sourceCellX == cx && p.sourceCellY == cy )
            if ( p.editing )
            {
                it.remove();
                break;
            }
        }
    }

    void deleteGunsParaPathUnderMouse()
    {
        //        int cx = getCellGridXFromMouse();
        //        int cy = getCellGridYFromMouse();

        Iterator<GunsParaPath> it = gunParaPaths.iterator();
        while ( it.hasNext() )
        {
            GunsParaPath p = it.next();
            //            if ( p.sourceCellGridX == cx && p.sourceCellGridY == cy )
            if ( p.sourceCellGridX == cellGridX && p.sourceCellGridY == cellGridY )
            {
                it.remove();
                break;
            }
        }
    }

    GunsParaPathIterator getGunsParaPathIterator()
    {
        return new GunsParaPathIterator();
    }

    class GunsParaPathIterator extends IndexedIterator<GunsParaPath>
    {
        //        private Iterator<GunsParaPath> iter;

        //        private int halfCellSize;

        public GunsParaPathIterator()
        {
            //            iter = gunParaPaths.iterator();
            super( gunParaPaths.iterator() );
            //            halfCellSize = outerCellSize >> 1;
        }

        private Point getDestinationPoint( GunsParaPath p )
        {
            return getDestinationCell( p.sourceCellGridX, p.sourceCellGridY, p.distFromCenter, p.angleFromCenter, p.range );
        }

        //        public Rectangle nextRectangle()
        //        {
        //            GunsParaPath p = next();
        //
        //            int sx = p.sourceCellGridX * outerCellSize + halfCellSize;
        //            int sy = p.sourceCellGridY * outerCellSize + halfCellSize;
        //
        //            Point dst = getDestinationPoint( p );
        //
        //            int dx = dst.x * outerCellSize + halfCellSize;
        //            int dy = dst.y * outerCellSize + halfCellSize;
        //
        //            return new Rectangle( sx, sy, dx, dy );
        //        }

        @Override
        protected boolean isElementIterable( int i )
        {
            return getDestinationPoint( gunParaPaths.get( i ) ) != null;
        }
    }

    class GunsParaPath
    {
        int sourceCellGridX;

        int sourceCellGridY;

        int distFromCenter;

        int angleFromCenter;

        int range;

        boolean editing;

        public GunsParaPath( int sx, int sy, int d, int a, int r, boolean edit )
        {
            sourceCellGridX = sx;
            sourceCellGridY = sy;
            distFromCenter = d;
            angleFromCenter = a;
            range = r;
            editing = edit;
        }
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        gameEngine.addObserver( obs );
        gameModel.addObserver( obs );
        super.addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        super.removeObserver( obs );
        gameEngine.removeObserver( obs );
        gameModel.removeObserver( obs );
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( MVCModelChange change )
    {
        switch ( (XBMVCModelChangeId)change.getChangeId() )
        {
            case DrawGridChanged:
                setCellSize( this, cellSize ); // to update precomputed value
                break;

            default:
                break;
        }
    }

    @Override
    public void subscribeModel()
    {
        drawGrid.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        drawGrid.removeObserver( this );
    }

    @Override
    public void close()
    {
        unsubscribeModel();
    }
}
