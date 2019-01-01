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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import jxbattle.bean.client.input.XBKeyEvent;
import jxbattle.bean.common.game.BoardState.CellIterator;
import jxbattle.bean.common.game.Cell;
import jxbattle.bean.common.game.CellMove;
import jxbattle.bean.common.game.CellState;
import jxbattle.bean.common.game.UserCommand.UserCommandCode;
import jxbattle.bean.common.parameters.game.InvisibilityMode;
import jxbattle.common.Consts;
import jxbattle.gui.base.client.boardpanel.BoardPanelModel.GunsParaPath;
import jxbattle.gui.base.client.boardpanel.BoardPanelModel.GunsParaPathIterator;
import jxbattle.gui.base.common.KeyListenerWrapper;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class BoardPanelController implements MVCController<BoardPanelModel, BoardPanelView>, MVCModelObserver
{
    private BoardPanelModel model;

    private BoardPanelView view;

    private JComponent drawAera;

    // private int cellSize;

    // private int gridWidth;

    //private int gridHeight;

    private boolean active;

    // precomputed values

    //private boolean allowRepeat;

    // temporary values

    private boolean debugMode; // display in debug mode : moves always rendered, level for all players in each cell

    private int dbgPlayerCount;

    private BoardPanelRenderer boardRenderer;

    //    private boolean doRender;

    //private Runnable redrawRunnable;

    public BoardPanelController( BoardPanelView v )
    {
        boardRenderer = new DefaultBoardPanelRenderer();
        debugMode = false;
        active = true;

        view = v;
        init();
    }

    private void init()
    {
        view.setFocusable( true );

        //        doRender = false;
        //        new Thread( new Runnable()
        //        {
        //            public void run()
        //            {
        //                while ( active )
        //                {
        //                    if ( doRender )
        //                    {
        //                        doRender = false;
        //                        view.repaint();
        //                    }
        //
        //                    Thread.yield();
        //                }
        //            }
        //        } ).start();

        //        redrawRunnable = new Runnable()
        //        {
        //
        //            @Override
        //            public void run()
        //            {
        //                redrawBoard();
        //            }
        //        };

        createHandlers();
    }

    private void createHandlers()
    {
        drawAera = new JComponent()
        {
            @Override
            public void paint( Graphics g )
            {
                paintBoard( g );
            }
        };

        view.add( drawAera );

        //        view.addFocusListener( new FocusAdapter() // TODO verifier si vraiment nécessaire (car cause peut être un bug)
        //        {
        //            @Override
        //            public void focusLost( FocusEvent e )
        //            {
        //                if ( view != null )
        //                    view.requestFocus();
        //            }
        //        } );

        view.addMouseListener( new MouseAdapter()
        {
            @Override
            public void mouseClicked( MouseEvent e )
            {
                onMouseClicked( e );
            }
        } );

        view.addMouseMotionListener( new MouseMotionAdapter()
        {
            @Override
            public void mouseMoved( MouseEvent e )
            {
                //                model.setShiftModifier( (e.getModifiers() & 1) != 0 );
                //                model.setCtrlModifier( (e.getModifiers() & 2) != 0 );
                onMouseMove( e );
            }
        } );

        view.addMouseWheelListener( new MouseWheelListener()
        {
            @Override
            public void mouseWheelMoved( MouseWheelEvent e )
            {
                //                model.setShiftModifier( (e.getModifiers() & 1) != 0 );
                //                model.setCtrlModifier( (e.getModifiers() & 2) != 0 );
                onMouseWheel( e );
            }
        } );

        view.addKeyListener( KeyListenerWrapper.init( new KeyListener()
        {
            private boolean released = true;

            @Override
            public void keyPressed( KeyEvent e )
            {
                //                model.setFirstKey( released );
                if ( released || model.isAllowRepeatKey() )
                {
                    int kc = e.getKeyCode();
                    if ( kc != KeyEvent.VK_SHIFT && kc != KeyEvent.VK_CONTROL )
                    {
                        //                        model.setShiftModifier( (e.getModifiers() & 1) != 0 );
                        //                        model.setCtrlModifier( (e.getModifiers() & 2) != 0 );
                        //                        model.keyPressed( this, kc );
                        model.keyPressed( this, kc, e.getModifiers() );
                        released = false;
                    }
                }
            }

            @Override
            public void keyReleased( KeyEvent e )
            {
                int kc = e.getKeyCode();
                if ( kc != KeyEvent.VK_SHIFT && kc != KeyEvent.VK_CONTROL )
                {
                    //                    model.setShiftModifier( (e.getModifiers() & 1) != 0 ); // TODO à décommenter ?
                    //                    model.setCtrlModifier( (e.getModifiers() & 2) != 0 );
                    model.keyReleased( this );
                }
                released = true;
            }

            @Override
            public void keyTyped( KeyEvent e )
            {
            }
        }, false ) );
    }

    //    private void preCompute()
    //    {
    //        allowRepeat = model.getGameModel().getGameParametersModel().getGameParameters().getAllowKeyRepeat().getValue();
    //        // onCellSizeChange();
    //    }

    public void setDebugMode( boolean dbMode )
    {
        debugMode = dbMode;
        dbgPlayerCount = model.getGameModel().getPlayersModel().getSelectedPlayerCount();
    }

    private void onMouseMove( MouseEvent e )
    {
        int gridWidth = model.getGridPixelWidth();
        int gridHeight = model.getGridPixelHeight();

        int mouseX = e.getX();
        if ( mouseX < 0 )
            mouseX = 0;
        if ( mouseX >= gridWidth )
            mouseX = gridWidth;

        int mouseY = e.getY();
        if ( mouseY < 0 )
            mouseY = 0;
        if ( mouseY >= gridHeight )
            mouseY = gridHeight;

        model.mouseMoved( this, mouseX, mouseY, e.getModifiers() );
    }

    private void onMouseWheel( MouseWheelEvent e )
    {
        model.mouseWheel( this, e.getX(), e.getY(), e.getModifiers(), -e.getWheelRotation() );
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

    private void onMouseClicked( MouseEvent e )
    {
        if ( (e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK ) // lmb
        {
            model.leftMouse( this, e.getX(), e.getY(), e.getModifiers() );
        }
        else if ( (e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK ) // mmb
        {
            model.middleMouse( this, e.getX(), e.getY(), e.getModifiers() );
        }
        else if ( (e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK ) // rmb
        {
            model.rightMouse( this, e.getX(), e.getY(), e.getModifiers() );
        }

        //        model.mouseClick( BoardPanelController.this, new XBMouseEvent( e.getX(), e.getY(), e.getModifiers() ) );
    }

    public void startup()
    {
        //        if ( SwingUtilities.isEventDispatchThread() )
        //            view.requestFocus();
        //        else
        //            SwingUtilities.invokeLater( new Runnable()
        //            {
        //                @Override
        //                public void run()
        //                {
        //                    view.requestFocus();
        //                }
        //            } );
    }

    private void renderFightDebug( Graphics g, CellState cs )
    {
        int oneId = cs.biggestFightingPlayers[ 0 ];
        int twoId = cs.biggestFightingPlayers[ 1 ];

        //        if ( !debugMode )
        //        {
        //            // case where opponents are the same strength -> ramdomly pick player to render
        //
        //            if ( cs.troopsLevel[ oneId ] == cs.troopsLevel[ twoId ] )
        //            {
        //                boolean rand = model.getRandomGen().posRandomInt( 2 ) == 0;
        //                renderPlayer( g, cs, rand ? oneId : twoId );
        //            }
        //            else
        //            {
        //                // normal case, opponents with different strengths
        //
        //                renderPlayer( g, cs, oneId );
        //                renderPlayer( g, cs, twoId );
        //            }
        //        }

        // draw "X" cross to indicate fight

        int max = model.getCellSize() - 1;
        g.setColor( model.getGameModel().getPlayersModel().getColorFromPlayerId( oneId ).getAwtColor() );
        g.drawLine( 0, 0, max, max );
        g.setColor( model.getGameModel().getPlayersModel().getColorFromPlayerId( twoId ).getAwtColor() );
        g.drawLine( max, 0, 0, max );
    }

    private void renderDebug( Graphics g, CellState cs )
    {
        int cellSize = model.getCellSize();

        // render cell owner

        if ( cs.playerId != Consts.NullId )
        {
            Color c = model.getGameModel().getPlayersModel().getColorFromPlayerId( cs.playerId ).getAwtColor();
            g.setColor( c );
            g.drawRect( 0, 0, cellSize - 1, cellSize - 1 );
        }

        // players level

        int dbgPlayerWidth = cellSize / dbgPlayerCount - 1;
        for ( int i = 0; i < dbgPlayerCount; i++ )
        {
            float p = cs.troopsLevel[ i ] / Consts.maxTroopsLevel;
            int h = (int)((cellSize - 2) * p);
            int h1 = cellSize - 2 - h;

            Color c = model.getGameModel().getPlayersModel().getColorFromPlayerId( i ).getAwtColor();
            g.setColor( c );

            g.fillRect( dbgPlayerWidth * i + 1, h1 + 2, dbgPlayerWidth, h - 1 );
        }

        // always render moves even if in fight

        renderMoves( g, cs );

        // render fight

        if ( cs.fight )
            renderFightDebug( g, cs );
    }

    // moves
    private void renderMoves( Graphics g, CellState cs )
    {
        if ( active )
        {
            if ( model.getGameModel().getGameParametersModel().getGameParameters().getVisibilityHideVectors().getValue() && (cs.playerId != model.getClientPlayerId()) )
                return;

            int biggestFightingPlayerId = Consts.NullId;

            CellMove[] cm = cs.moves;
            for ( int i = 0; i < cm.length; i++ )
            {
                if ( cm[ i ].isSet )
                {
                    if ( cs.fight )
                    {
                        if ( biggestFightingPlayerId == Consts.NullId )
                            //biggestFightingPlayerId = sortFightingPlayers( cs )[ 1 ];
                            biggestFightingPlayerId = cs.biggestFightingPlayers[ 1 ];

                        boardRenderer.renderFightMove( g, i, biggestFightingPlayerId );
                    }
                    else
                        boardRenderer.renderPlayerMove( g, cs, i );
                }
            }
        }
    }

    private void renderCell( Graphics g, Cell cell )
    {
        if ( active )
        {
            CellState cs = cell.cellState;

            if ( cs.cellVisible )
            {
                if ( cs.isSeaCell() )
                    boardRenderer.renderSeaCell( g, cs ); // render sea
                else
                {
                    // render land background
                    boardRenderer.renderLandBackground( g, cs );

                    if ( model.getGameModel().getGameParametersModel().getGameParameters().getVisibilityMode().getValue() == InvisibilityMode.INVISIBLE_ENEMY && cs.playerId != model.getClientPlayerId() && !cs.enemyVisible )
                        return;

                    cs.sortFightingPlayers();

                    if ( debugMode )
                        renderDebug( g, cs );
                    else
                    {
                        if ( cs.playerId != Consts.NullId )
                        {
                            if ( cs.fight )
                            {
                                boardRenderer.renderFight( g, cs );
                                renderMoves( g, cs );
                            }
                            else
                            {
                                boardRenderer.renderPlayer( g, cs, cs.playerId );
                                renderMoves( g, cs );
                            }
                        }

                        boardRenderer.renderMarch( g, cell );
                    }

                    if ( cs.isTown )
                        boardRenderer.renderBase( g, cs );

                    //                    // pending commands
                    //
                    //                    for ( UserCommand uc : cell.commands )
                    //                        switch ( uc.code )
                    //                        {
                    //                            case TROOPS_PARA:
                    //                                boardRenderer.renderParaTroops( g, uc.playerId );
                    //                                break;
                    //
                    //                            case TROOPS_GUN:
                    //                                boardRenderer.renderGunTroops( g, uc.playerId );
                    //                                break;
                    //
                    //                            default:
                    //                                break;
                    //                        }

                    if ( cs.paraTroopsId != Consts.NullId ) // player id of incoming parachute troops
                        boardRenderer.renderParaTroops( g, cs );

                    if ( cs.gunTroopsId != Consts.NullId ) // player id of incoming player id of incoming shell
                        boardRenderer.renderGunTroops( g, cs );

                    UserCommandCode mc = cell.hasManagedCommand();

                    if ( mc != null && model.isMyCell( cell ) )
                        boardRenderer.renderManagedCommand( g, mc, model.getGameModel().getPlayersModel().getPlayerInfoModel( cs.playerId ).getPlayerColor() );
                }
            }
            else
                // render invisible cell
                boardRenderer.renderInvisibleCell( g, cs );

            //        if ( model.getCellGridXFromMouse() == cell.gridX && model.getCellGridYFromMouse() == cell.gridY )
            //        {
            //            g.setColor( new Color( 0.5f, 0.5f, 0.5f, 0.5f ) );
            //            g.fillRect( 0, 0, model.getCellSize(), model.getCellSize() );
            //        }
        }
    }

    private void paintBoard( Graphics g )
    {
        if ( view == null )
            return;

        int gridWidth = model.getGridPixelWidth();
        int gridHeight = model.getGridPixelHeight();
        int outerSize = model.getOuterCellSize();

        if ( model.getDrawGrid() )
        {
            boardRenderer.renderGrid( g, gridWidth, gridHeight, outerSize );
            g.translate( 1, 1 );
        }

        CellIterator it = model.getGameModel().getBoardStateModel().iterator();
        while ( it.hasNext() )
        {
            Cell cell = it.next();

            renderCell( g, cell );
            g.translate( 0, outerSize );

            if ( it.isLastLine() )
                g.translate( outerSize, -gridHeight );
        }

        g.translate( -gridWidth, 0 );

        if ( model.getDrawGrid() )
            g.translate( -1, -1 );

        GunsParaPathIterator pit = model.getGunsParaPathIterator();
        while ( pit.hasNext() )
        {
            //Rectangle r = pit.nextRectangle();
            GunsParaPath p = pit.next();
            //            boardRenderer.renderGunParaPath( g, r.x, r.y, r.width, r.height );
            Point dst = model.getDestinationCell( p.sourceCellGridX, p.sourceCellGridY, p.distFromCenter, p.angleFromCenter, p.range );
            if ( dst != null )
                boardRenderer.renderGunParaPath( g, p.sourceCellGridX, p.sourceCellGridY, dst.x, dst.y, model.isCellValid( dst.x, dst.y ) );
        }
    }

    public void disableView()
    {
        if ( SwingUtilities.isEventDispatchThread() )
            view.setEnabled( false );
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    view.setEnabled( false );
                }
            } );
    }

    public void redrawBoard()
    {
        //        doRender = true;

        if ( view != null )
        {
            //            long start = System.nanoTime();
            view.repaint();
            //            MainModel.printDur( "boardPanelcontroller view.repaint ms=", start, 1.0f );
        }
    }

    private void redrawAll()
    {
        Dimension d = new Dimension( model.getGridPixelWidth() + 1, model.getGridPixelHeight() + 1 );
        view.setSize( d );
        view.setPreferredSize( d );

        view.requestFocus();
    }

    //    private void onCellSizeChange()
    //    {
    //        //cellSize = model.getCellSize();
    //        gridWidth = model.getGridWidth();
    //        gridHeight = model.getGridHeight();
    //        //        cellInnerSize = model.getCellInnerSize();
    //        //        cellMargin = model.getCellMargin();
    //    }

    private void modelToUI_edt()
    {
        redrawAll();
    }

    private void modelToUI()
    {
        if ( SwingUtilities.isEventDispatchThread() )
            modelToUI_edt();
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    modelToUI_edt();
                }
            } );
    }

    private void processMessages( MVCModelChange change )
    {
        switch ( (XBMVCModelChangeId)change.getChangeId() )
        {
            case GridCellSizeChanged:
                //onCellSizeChange();
                redrawAll();
                break;

            case MouseMoved:
                model.deleteTemporaryGunsParaPaths();
                view.requestFocusInWindow();
                break;

            case GameBoardUpdated:
                redrawBoard();
                break;

            case WatchGame:
                redrawBoard();
                disableView();
                break;

            case KeyPressed:
                //                KeyInfo keyInfo = (KeyInfo)change.getData();
                //                switch ( keyInfo.getKeyCode() )
                //                {
                //                    case KeyEvent.VK_G:
                //                        model.deleteTemporaryGunsParaPaths();
                //                        model.addGunsPath( true );
                //                        break;
                //
                //                    case KeyEvent.VK_P:
                //                        model.deleteTemporaryGunsParaPaths();
                //                        model.addParaPath( true );
                //                        break;
                //
                //                    case KeyEvent.VK_C:
                //                        model.deleteGunsParaPathUnderMouse();
                //                        break;
                //
                //                    default:
                //                        break;
                //                }

                XBKeyEvent keyEvent = (XBKeyEvent)change.getData();
                UserCommandCode ucc = model.mapInputInfo( keyEvent );
                if ( ucc != null )
                    switch ( ucc )
                    {
                        case TROOPS_GUN:
                        case MANAGED_TROOPS_GUN:
                            model.deleteTemporaryGunsParaPaths();
                            model.addGunsPath( true );
                            break;

                        case TROOPS_PARA:
                        case MANAGED_TROOPS_PARA:
                            model.deleteTemporaryGunsParaPaths();
                            model.addParaPath( true );
                            break;

                        case CANCEL_MANAGE:
                            model.deleteGunsParaPathUnderMouse();
                            break;

                        default:
                            break;
                    }
                break;

            case KeyReleased:
                //                keyInfo = (KeyInfo)change.getData();
                //                switch ( keyInfo.getKeyCode() )
                //                keyEvent = (XBKeyEvent)change.getData();
                //                switch ( keyEvent.getKeyCode() )
                keyEvent = (XBKeyEvent)change.getData();
                ucc = model.mapInputInfo( keyEvent );
                if ( ucc != null )
                    switch ( ucc )
                    {
                    //                    case KeyEvent.VK_P:
                        case TROOPS_PARA:
                            model.deleteTemporaryGunsParaPaths();
                            break;

                        case MANAGED_TROOPS_PARA:
                            model.deleteTemporaryGunsParaPaths();
                            //                        if ( keyInfo.isShift() )
                            if ( keyEvent.isShift() )
                                model.addParaPath( false );
                            break;

                        //                    case KeyEvent.VK_G:
                        case TROOPS_GUN:
                            model.deleteTemporaryGunsParaPaths();
                            break;

                        case MANAGED_TROOPS_GUN:
                            model.deleteTemporaryGunsParaPaths();
                            //                        if ( keyInfo.isShift() )
                            //                            if ( keyEvent.isShift() )
                            model.addGunsPath( false );
                            break;

                        default:
                            break;
                    }
                break;

            //            case GameParametersChanged:
            //                active = change.getData() != null;
            //                break;

            default:
                break;
        }
    }

    // MVCController interface

    @Override
    public BoardPanelView getView()
    {
        return view;
    }

    @Override
    public BoardPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( BoardPanelModel m )
    {
        unsubscribeModel();
        model = null;

        //        if ( m instanceof BoardPanelModel )
        //        {
        //            model = (BoardPanelModel)m;
        model = m;
        boardRenderer.setModel( model );
        subscribeModel();
        //        preCompute();
        //        }

        modelToUI();
    }

    private void close_edt()
    {
        active = false;
        boardRenderer.close();
        unsubscribeModel();
        view = null;
    }

    @Override
    public void close()
    {
        if ( SwingUtilities.isEventDispatchThread() )
            close_edt();
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    close_edt();
                }
            } );
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        //        switch ( (XBMVCModelChangeId)change.getChangeId() )
        //        {
        //        //            case GameParametersChanged:
        //        //                active = change.getData() != null;
        //        //                break;
        //
        //            default:
        if ( SwingUtilities.isEventDispatchThread() )
            processMessages( change );
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    processMessages( change );
                }
            } );
        //        break;
        //        }
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
}
