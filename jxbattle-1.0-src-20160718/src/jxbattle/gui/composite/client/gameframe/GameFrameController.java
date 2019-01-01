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

package jxbattle.gui.composite.client.gameframe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

import jxbattle.bean.client.input.XBKeyEvent;
import jxbattle.bean.client.input.XBMouseEvent;
import jxbattle.bean.common.game.CellState;
import jxbattle.bean.common.parameters.game.InvisibilityMode;
import jxbattle.bean.common.player.XBColor;
import jxbattle.gui.base.client.boardpanel.BoardPanelController;
import jxbattle.gui.base.client.boardpanel.BoardPanelModel;
import jxbattle.gui.base.client.clientstatuspanel.ClientStatusPanelController;
import jxbattle.gui.base.client.clientstatuspanel.ClientStatusPanelModel;
import jxbattle.gui.base.client.quitdialog.QuitDialogController;
import jxbattle.gui.base.client.quitdialog.QuitDialogController.QuitDialogCode;
import jxbattle.gui.base.common.JTranslucentLabel;
import jxbattle.model.MainModel;
import jxbattle.model.client.PlayerStatisticsModel;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.game.PlayerInfos.SelectedPlayerIterator;
import jxbattle.model.common.observer.XBMVCModelChangeId;
import net.miginfocom.swing.MigLayout;

import org.generic.gui.parameters.BoolCheckController;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.thread.ThreadUtils;

public class GameFrameController implements MVCController<GameFrameModel, GameFrameView>, MVCModelObserver
{
    private GameFrameModel model;

    private GameFrameView view;

    private BoardPanelController boardPanelController;

    private BoardPanelModel boardPanelModel;

    private QuitDialogController quitDialogController;

    private BoolCheckController drawGridBoolController;

    private SimpleDateFormat timeFormat;

    private long startTime;

    private JTranslucentLabel glassPanelabel;

    private List<ClientStatusPanelController> clientStatusControllers;

    private Timer statusTimer;

    public GameFrameController()
    {
        timeFormat = new SimpleDateFormat( "HH:mm:ss" );

        clientStatusControllers = new ArrayList<>();
        init();
    }

    private void init()
    {
        view = new GameFrameView();

        view.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );

        boardPanelController = new BoardPanelController( view.getBoardPanelView() );
        drawGridBoolController = new BoolCheckController( view.getCbDrawGrid() );

        createGlassPaneLabel();

        createHandlers();

        // setGlassPaneText( "...", 0.5f );
    }

    private void createGlassPaneLabel()
    {
        final JPanel glassPane = (JPanel)view.getGlassPane();

        glassPane.setVisible( false );
        glassPane.setLayout( new MigLayout( "", "[grow]", "[grow]" ) );
        glassPanelabel = new JTranslucentLabel( 0.3f );
        glassPanelabel.setBackground( Color.GRAY );
        glassPanelabel.setForeground( Color.WHITE );
        glassPanelabel.setFont( new Font( "Dialog", Font.BOLD, 35 ) );
        glassPanelabel.setBorder( new LineBorder( new Color( 0, 0, 0 ) ) );

        glassPane.add( glassPanelabel, "cell 0 0,alignx center,aligny center" );
    }

    private void createHandlers()
    {
        view.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent w )
            {
                closeWindow();
            }
        } );

        view.getBtnZoomMinus().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                boardPanelModel.modifyCellSize( GameFrameController.this, -1 );
            }
        } );

        view.getBtnZoomPlus().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                boardPanelModel.modifyCellSize( GameFrameController.this, 1 );
            }
        } );
    }

    public void run()
    {
        if ( SwingUtilities.isEventDispatchThread() )
            startup();
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    startup();
                }
            } );
    }

    private void startup()
    {
        startTime = System.currentTimeMillis();
        boardPanelController.startup();
        view.setVisible( true );

        if ( MainModel.iconifyGameFrame )
            view.setState( Frame.ICONIFIED );
        if ( MainModel.gameFrameToBack )
            view.toBack();

        statusTimer = new Timer();
        statusTimer.schedule( new TimerTask()
        {
            @Override
            public void run()
            {
                setStatusLabels();
            }
        }, 0, 200 );
    }

    //    public BoardPanelController getBoardPanelController()
    //    {
    //        return boardPanelController;
    //    }

    public void countDown()
    {
        if ( !MainModel.fastPlay )
        {
            setGlassPaneText( "3...", 0.2f );
            ThreadUtils.sleep( 1000 );
            setGlassPaneText( "2...", 0.4f );
            ThreadUtils.sleep( 1000 );
            setGlassPaneText( "1...", 0.6f );
            ThreadUtils.sleep( 1000 );
        }

        setGlassPaneText( "GO !", 1.0f );
        ThreadUtils.sleep( 1000 );

        unsetGlassPaneText();
    }

    private void disposeFrame()
    {
        boardPanelController.close();

        if ( view != null )
        {
            view.setVisible( false );
            view.dispose();
            view = null;
        }
    }

    //    private void disposeFrame()
    //    {
    //        if ( SwingUtilities.isEventDispatchThread() )
    //        {
    //            disposeFrame_edt();
    //        }
    //        else
    //        {
    //            SwingUtilities.invokeLater( new Runnable()
    //            {
    //                @Override
    //                public void run()
    //                {
    //                    disposeFrame_edt();
    //                }
    //            } );
    //        }
    //    }

    public void setGlassPaneText( String m, float a )
    {
        final JPanel glassPane = (JPanel)view.getGlassPane();
        glassPane.setVisible( true );
        glassPanelabel.setAlpha( a );
        glassPanelabel.setText( " " + m + " " );
    }

    private void unsetGlassPaneText()
    {
        final JPanel glassPane = (JPanel)view.getGlassPane();
        glassPane.setVisible( false );
    }

    private void setStatusLabels()
    {
        // time

        StringBuilder sb = new StringBuilder();
        sb.append( timeFormat.format( new Date( System.currentTimeMillis() - startTime - 3600 * 1000 ) ) );
        view.getLblTime().setText( sb.toString() );

        // cell coordinates

        sb.setLength( 0 );
        //        if ( boardPanelModel.validMouseposition() )
        //        {
        int cellX = boardPanelModel.getCellGridXFromMouse();
        int cellY = boardPanelModel.getCellGridYFromMouse();
        if ( cellX != -1 && cellY != -1 )
        {
            //            CellState cs = model.getGameModel().getBoardStateModel().getCell( cellX, cellY ).cellState;
            CellState cs = model.getCellStateAt( cellX, cellY );

            // cell coordinates

            sb.append( String.valueOf( cellX ) );
            sb.append( " " );
            sb.append( String.valueOf( cellY ) );
            //            sb.append( " icm " );
            //            sb.append( String.valueOf( boardPanelModel.getInCellMouseX() ) );
            //            sb.append( " " );
            //            sb.append( String.valueOf( boardPanelModel.getInCellMouseY() ) );
            //            sb.append( " da " );
            //            sb.append( String.valueOf( boardPanelModel.getMouseDistanceFromCellCenter() ) );
            //            sb.append( " " );
            //            sb.append( String.valueOf( boardPanelModel.getMouseAngleFromCellCenter() ) );

            // elevation

            //            InvisibilityMode vm = model.getGameModel().getGameParametersModel().getGameParameters().getVisibilityMode().getValue();
            InvisibilityMode vm = model.getVisibilityMode();
            boolean mapMode = vm == InvisibilityMode.INVISIBLE_MAP;
            boolean mapVisible = mapMode && cs.cellVisible;
            boolean elevation = mapVisible || !mapMode;

            if ( elevation )
                view.getLblElevation().setText( String.valueOf( cs.elevation ) );

            // enemy info

            boolean cellVisible = vm == InvisibilityMode.NONE;
            boolean enemyMode = vm == InvisibilityMode.INVISIBLE_ENEMY;
            boolean enemyVisible = enemyMode && cs.enemyVisible;
            cellVisible = cellVisible || mapVisible;
            cellVisible = cellVisible || enemyVisible;

            if ( cellVisible )
            {
                view.getLblLevel().setText( String.valueOf( cs.getTroopsLevel() ) );
                view.getLblReserve().setText( String.valueOf( cs.reserveLevel ) );
                view.getLblOwner().setText( String.valueOf( cs.playerId ) );
                view.getLblFight().setVisible( cs.fight );
            }
        }
        else
        {
            view.getLblLevel().setText( "" );
            view.getLblReserve().setText( "" );
            view.getLblOwner().setText( "" );
            view.getLblFight().setVisible( false );
        }

        view.getLblCellXY().setText( sb.toString() );
    }

    private void clearStatusControllers()
    {
        for ( ClientStatusPanelController cspc : clientStatusControllers )
            cspc.close();
        clientStatusControllers.clear();
    }

    private void setPlayersStatus()
    {
        JPanel pnl = view.getPnlPlayersStatus();
        pnl.removeAll();

        clearStatusControllers();

        pnl.setLayout( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );

        //        SelectedPlayerIterator it = model.getGameModel().getPlayersModel().selectedPlayers();
        SelectedPlayerIterator it = model.getSelectedPlayerIterator();
        while ( it.hasNext() )
        {
            PlayerInfoModel pim = it.next();
            ClientStatusPanelController psc = new ClientStatusPanelController();
            clientStatusControllers.add( psc );

            //            PlayerStatisticsModel psm = model.getGameModel().getGameStatisticsModel().getPlayerStatisticsModel( pim.getPlayerId() );
            PlayerStatisticsModel psm = model.getPlayerStatisticsModel( pim );
            //            psc.setModel( new ClientStatusPanelModel( pim, psm, pim.getPlayerId() == model.getClientPlayerId() ) );
            psc.setModel( new ClientStatusPanelModel( pim, psm, model.isMe( pim ) ) );

            pnl.add( psc.getView() );
        }
        //        it.close();
    }

    private void closeWindow()
    {
        // if alone, game won or already watching, enabling watch button is pointless
        //boolean allowWatch = !model.getGameModel().hasWinner() && !model.isWatchMode() && model.getGameModel().getPlayersModel().getActivePlayerCount() > 1;

        quitDialogController = new QuitDialogController( view );
        QuitDialogCode qdc = quitDialogController.run( model.allowWatch() );
        if ( qdc != null ) // may be null if QuitDialogController closed by game abort
            switch ( qdc )
            {
                case Cancel:
                    break;

                case Watch:
                    //                    model.getGameModel().watchGame( this );
                    model.watchGame( GameFrameController.this );
                    setGlassPaneText( "watching", 0.5f );
                    break;

                case Abort:
                    //                    model.getGameModel().abortGame( this );
                    model.abortGame( GameFrameController.this );
                    close();
                    break;

                default:
                    break;
            }

        closeQuitDialog();
    }

    //    private void processUserCommand( UserCommand uc )
    //    {
    //        switch ( uc.code )
    //        {
    //            case REPEAT_COMMAND:
    //                model.repeatLastCommand();
    //                break;
    //
    //            default:
    //                break;
    //        }
    //    }

    //    private void processKey( KeyInfo keyInfo )
    //    {
    //        if ( keyInfo.isPressedOrReleased() )
    //            switch ( keyInfo.getKeyCode() )
    //            {
    //                case KeyEvent.VK_UP:
    //                    swapVectors( 3 );
    //                    break;
    //                    
    //                case KeyEvent.VK_RIGHT:
    //                    swapVectors( 5 );
    //                    break;
    //                    
    //                case KeyEvent.VK_DOWN:
    //                    swapVectors( 7 );
    //                    break;
    //                    
    //                case KeyEvent.VK_LEFT:
    //                    swapVectors( 1 );
    //                    break;
    //                    
    //                case KeyEvent.VK_NUMPAD5:
    //                case KeyEvent.VK_J:
    //                    cancelAllVectors();
    //                    break;
    //                    
    //                case KeyEvent.VK_NUMPAD7:
    //                case KeyEvent.VK_Y:
    //                    setVectors( 2 );
    //                    break;
    //                    
    //                case KeyEvent.VK_NUMPAD8:
    //                case KeyEvent.VK_U:
    //                    setVectors( 3 );
    //                    break;
    //                    
    //                case KeyEvent.VK_NUMPAD9:
    //                case KeyEvent.VK_I:
    //                    setVectors( 4 );
    //                    break;
    //                    
    //                case KeyEvent.VK_NUMPAD6:
    //                case KeyEvent.VK_K:
    //                    setVectors( 5 );
    //                    break;
    //                    
    //                case KeyEvent.VK_NUMPAD3:
    //                case KeyEvent.VK_SEMICOLON:
    //                    setVectors( 6 );
    //                    break;
    //                    
    //                case KeyEvent.VK_NUMPAD2:
    //                case KeyEvent.VK_COMMA:
    //                    setVectors( 7 );
    //                    break;
    //                    
    //                case KeyEvent.VK_NUMPAD1:
    //                case KeyEvent.VK_N:
    //                    setVectors( 8 );
    //                    break;
    //                    
    //                case KeyEvent.VK_NUMPAD4:
    //                case KeyEvent.VK_H:
    //                    setVectors( 1 );
    //                    break;
    //                    
    //                case KeyEvent.VK_ENTER:
    //                    repeatLastCommand();
    //                    break;
    //                    
    //                case KeyEvent.VK_F:
    //                    fillGround( keyInfo.isShift() );
    //                    break;
    //                    
    //                case KeyEvent.VK_D:
    //                    digGround( keyInfo.isShift() );
    //                    break;
    //                    
    //                case KeyEvent.VK_B:
    //                    buildBase( keyInfo.isShift() );
    //                    break;
    //                    
    //                case KeyEvent.VK_S:
    //                    scuttleBase( keyInfo.isShift() );
    //                    break;
    //                    
    //                case KeyEvent.VK_F1:
    //                    troopsReserve( 0 );
    //                    break;
    //                    
    //                case KeyEvent.VK_F2:
    //                    troopsReserve( 1 );
    //                    break;
    //                    
    //                case KeyEvent.VK_F3:
    //                    troopsReserve( 2 );
    //                    break;
    //                    
    //                case KeyEvent.VK_F4:
    //                    troopsReserve( 3 );
    //                    break;
    //                    
    //                case KeyEvent.VK_F5:
    //                    troopsReserve( 4 );
    //                    break;
    //                    
    //                case KeyEvent.VK_F6:
    //                    troopsReserve( 5 );
    //                    break;
    //                    
    //                case KeyEvent.VK_F7:
    //                    troopsReserve( 6 );
    //                    break;
    //                    
    //                case KeyEvent.VK_F8:
    //                    troopsReserve( 7 );
    //                    break;
    //                    
    //                case KeyEvent.VK_F9:
    //                    troopsReserve( 8 );
    //                    break;
    //                    
    //                case KeyEvent.VK_F10:
    //                    troopsReserve( 9 );
    //                    break;
    //                    
    //                case KeyEvent.VK_A:
    //                    cellAttack();
    //                    break;
    //                    
    //                case KeyEvent.VK_C:
    //                    cancelManagement();
    //                    break;
    //                    
    //                default:
    //                    break;
    //            }
    //        else
    //            switch ( keyInfo.getKeyCode() )
    //            {
    //                case KeyEvent.VK_P:
    //                    paraTroops( keyInfo.isShift() );
    //                    break;
    //                    
    //                case KeyEvent.VK_G:
    //                    gunTroops( keyInfo.isShift() );
    //                    break;
    //                    
    //                default:
    //                    break;
    //            }
    //    }

    /*
            With the option "-march <n>", troops may be  commanded to  march in a  particular direction and  to continue in that  direction  without  further  com‐
       mands.   March commands are activated with  shift left  or shift  middle mouse button.  For example,  if you click near the  top edge of  a  cell  with
       "shift left mouse",  the troops will begin to march up, and on arrival  in the next cell they will transfer the  march  command  to  that cell so  that
       they   will continue  marching upwards  to the  next   cell,  and so forth. If a marching  column  encounters   hostile forces  the   march command  is
       canceled and  the   column stops.   To prevent marching  columns from traveling  much  faster than  manually  commanded troops,   the   march  argument
       <n>  defines  the number of game update  cycles that the troops must wait in each new cell before marching on to the next cell, so that "-march 1" will
       result in a fast  march, whereas "-march 10" will be slow. 

       The  "march command" is  indicated on the  game board  by a double command  vector  (looks
       like   an  "="   sign)  in the appropriate direction, and  the march command  is always passed on  to the head
       of the  column.  March   commands may be  set  in   cells that  are  NOT occupied by your troops, and will be
       activated  when a marching column arrives in that cell.  This allows you  to define turns   in  the  path  
       of  the marching column to avoid obstacles.

       A "stop march" command may also be set to program the  marching column  to stop  in
       that cell.  This is achieved by clicking "shift left  mouse" in the center of that cell,
       and will be  displayed as an  empty box in  that  cell.
       
       When set  with  the left   mouse, the  march vector   is  overwritten on to existing command
       vectors encountered in the march  path, whereas when set  with
       the  middle mouse   the march  vector  removes and replaces existing command vectors. 
       
       March commands are canceled by clicking  on  the   cell  without the  shift   key.
       
       March  commands  may be  set in cells that are  beyond the visible  horizon in the  normal way , and will appear as a double vector
       in  that cell so long  as that cell is not a "sea" cell.  If the target  cell contains invisible enemy troops,  then the march   command  vectors  will
       appear   initially,   but disappear again as soon as the enemy  is approached close enough to be visible.  March commands are specific to the team that
       sets  them, and different march  commands may be  set by different   teams in the same game cell.  The double command vectors are visible  only to  the
       team that sets  them.
     */
    private void leftMouse( boolean shift )
    {
        int z = boardPanelModel.getCellZone();
        switch ( z )
        {
            case -1:
                break;

            case 0:
                if ( shift )
                    model.stopMarchCommand();
                else
                {
                    //                    cancelMarchCommands();
                    model.cancelAllVectors();
                }
                break;

            default:
                if ( shift )
                    switch ( z )
                    {
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                            model.swapMarchCommand( z );
                            break;

                        default:
                            break;
                    }
                else
                {
                    //                    cancelMarchCommands();
                    model.swapVectors( z );
                }
                break;
        }
    }

    private void middleMouse( boolean shift )
    {
        int z = boardPanelModel.getCellZone();
        switch ( z )
        {
            case -1:
                break;

            case 0:
                if ( shift )
                    model.cancelMarchCommands();
                else
                    model.cancelAllVectors();
                break;

            default:
                if ( shift )
                    switch ( z )
                    {
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                            model.setMarchCommand( z );
                            break;

                        default:
                            break;
                    }
                else
                {
                    //                    cancelMarchCommands();
                    model.setVectors( z );
                }
                break;
        }
    }

    private void rightMouse()
    {
        model.repeatLastCommand();
    }

    private void processMessages( MVCModelChange change )
    {
        switch ( (XBMVCModelChangeId)change.getChangeId() )
        {
            case LeftMouseClicked:
                //                MouseInfo mi = (MouseInfo)change.getData();
                XBMouseEvent me = (XBMouseEvent)change.getData();
                leftMouse( me.isShift() );
                break;

            case MiddleMouseClicked:
                //                mi = (MouseInfo)change.getData();
                me = (XBMouseEvent)change.getData();
                middleMouse( me.isShift() );
                break;

            case RightMouseClicked:
                rightMouse();
                break;

            case KeyReleased:
            case KeyPressed:
                //                processKey( (KeyInfo)change.getData() );
                model.processKey( (XBKeyEvent)change.getData() );
                break;

            case GridCellSizeChanged:
                view.pack();
                break;

            case MouseWheel:
                //                mi = (MouseInfo)change.getData();
                //                boardPanelModel.modifyCellSize( this, mi.getWheelInc() > 0 ? 1 : -1 );
                me = (XBMouseEvent)change.getData();
                boardPanelModel.modifyCellSize( this, me.getWheelInc() > 0 ? 1 : -1 );
                break;

            //            case UserCommand:
            //                processUserCommand( (UserCommand)change.getChangeId() );
            //                break;

            default:
                break;
        }
    }

    //    public void redrawBoard()
    //    {
    //        boardPanelController.redrawBoard();
    //    }

    //    public BoardPanelModel getBoardPanelModel() // TODO a virer
    //    {
    //        return boardPanelModel;
    //    }

    /**
     * compute board panel zoom to fit frame to screen size if too big
     */
    private void setWindowSizeAndPos()
    {
        // Get the current screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        // board panel size
        Dimension boardSize = view.getBoardPanelView().getSize();

        int extraWidth = boardSize.width - screenSize.width;
        int extraHeight = boardSize.height - screenSize.height;
        boolean b1 = extraWidth > 0;
        boolean b2 = extraHeight > 0;
        if ( b1 || b2 ) // too large ?
        {
            float zoomCorrection;
            boolean correctWidth = true; // correction based on width
            if ( b1 && b2 ) // if both width and height are too large
                correctWidth = extraWidth > extraHeight;
            else
                correctWidth = b1;

            if ( correctWidth )
            {
                zoomCorrection = (float)screenSize.width / boardSize.width;
                zoomCorrection *= 0.98; // factor to integrate size of components/frame border/... around board panel
            }
            else
            {
                zoomCorrection = (float)screenSize.height / boardSize.height;
                zoomCorrection *= 0.85; // factor to integrate size of components/frame border/... around board panel
            }

            int cellSize = boardPanelModel.getCellSize();
            cellSize *= zoomCorrection;
            if ( cellSize < 10 )
                cellSize = 10;

            boardPanelModel.setCellSize( this, cellSize );
        }

        view.setLocationRelativeTo( null ); // center frame 
    }

    private void modelToUI_edt()
    {
        XBColor clientColor = model.getClientPlayerColor();
        view.setTitle( clientColor.getName() + " side" );

        setPlayersStatus();

        view.pack();

        setWindowSizeAndPos();
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

    // MVCController interface

    @Override
    public GameFrameView getView()
    {
        return this.view;
    }

    @Override
    public GameFrameModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( GameFrameModel m )
    {
        unsubscribeModel();
        model = m;
        boardPanelModel = model.getBoardPanelModel();
        boardPanelController.setModel( boardPanelModel );
        drawGridBoolController.setModel( boardPanelModel.getDrawGridModel() );
        subscribeModel();
        modelToUI();
    }

    private void closeQuitDialog()
    {
        if ( quitDialogController != null )
        {
            quitDialogController.close();
            quitDialogController = null;
        }
    }

    private void close_edt()
    {
        statusTimer.cancel();

        boardPanelController.close();
        drawGridBoolController.close();

        closeQuitDialog();
        disposeFrame();
        clearStatusControllers();
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
    }

    @Override
    public void subscribeModel()
    {
        if ( boardPanelModel != null )
            boardPanelModel.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        if ( boardPanelModel != null )
            boardPanelModel.removeObserver( this );
    }
}
