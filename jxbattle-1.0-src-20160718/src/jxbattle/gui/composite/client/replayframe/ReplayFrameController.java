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

package jxbattle.gui.composite.client.replayframe;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import jxbattle.bean.client.input.XBKeyEvent;
import jxbattle.bean.client.input.XBKeyInfo;
import jxbattle.bean.client.input.XBMouseEvent;
import jxbattle.bean.common.game.Cell;
import jxbattle.common.config.ConfigFolder;
import jxbattle.gui.base.client.boardpanel.BoardPanelController;
import jxbattle.gui.base.client.replaycursorpanel.ReplayCursorPanelController;
import jxbattle.gui.base.client.replaycursorpanel.ReplayCursorPanelModelChangeId;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.gui.GuiUtils;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class ReplayFrameController implements MVCController<ReplayFrameModel, ReplayFrameView>, MVCModelObserver
{
    private ReplayFrameView view;

    private ReplayFrameModel model;

    private boolean visualDebug;

    private BoardPanelController boardPanelController;

    private ReplayCursorPanelController replayCursorPanelController;

    private Component parentComponent;

    public ReplayFrameController( Component parent )
    {
        // clientModel = cm;
        // model = clientModel.getGameModel();
        parentComponent = parent;

        init();
    }

    private void init()
    {
        visualDebug = false;

        view = new ReplayFrameView();
        view.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );

        replayCursorPanelController = new ReplayCursorPanelController( view.getReplayCursorPanelView() );

        boardPanelController = new BoardPanelController( view.getBoardPanelView() );
        createHandlers();
    }

    //    private void initPlayerCount()
    //    {
    //        int playerCount = gameModel.getBoardState().cells[ 0 ][ 0 ].cellState.troopsLevel.length;
    //        boardPanelController.setDebugPlayerCount( playerCount );
    //    }

    //    private boolean initLogFile() throws IOException
    //    {
    //        if ( chooseFile() )
    //        {
    //            int playerCount = gameLogFile.readHeader();
    //            for ( int p = 0; p < playerCount; p++ )
    //            {
    //                PlayerParameters pp = new PlayerParameters();
    //                pp.setPlayerColor( this, Consts.colors[ p ] );
    //                pp.setPlayerId( this, p );
    //                gameModel.getPlayers().add( this, pp );
    //            }
    //
    //            return true;
    //        }
    //
    //        return false;
    //    }

    private void createHandlers()
    {
        view.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent w )
            {
                close();
            }
        } );

        //        view.getBtnStart().addActionListener( new ActionListener()
        //        {
        //            @SuppressWarnings("unused")
        //            @Override
        //            public void actionPerformed( ActionEvent e )
        //            {
        //                btnStart();
        //            }
        //        } );
        //
        //        view.getBtnBigPrevious().addActionListener( new ActionListener()
        //        {
        //            @SuppressWarnings("unused")
        //            @Override
        //            public void actionPerformed( ActionEvent e )
        //            {
        //                btnBigPrevious();
        //            }
        //        } );
        //        view.getBtnPrevious().addActionListener( new ActionListener()
        //        {
        //            @SuppressWarnings("unused")
        //            @Override
        //            public void actionPerformed( ActionEvent e )
        //            {
        //                btnPrevious();
        //            }
        //        } );
        //
        //        view.getBtnNext().addActionListener( new ActionListener()
        //        {
        //            @SuppressWarnings("unused")
        //            @Override
        //            public void actionPerformed( ActionEvent e )
        //            {
        //                btnNext();
        //            }
        //        } );
        //
        //        view.getBtnBigNext().addActionListener( new ActionListener()
        //        {
        //            @SuppressWarnings("unused")
        //            @Override
        //            public void actionPerformed( ActionEvent e )
        //            {
        //                btnBigNext();
        //            }
        //        } );
        //
        //        view.getBtnEnd().addActionListener( new ActionListener()
        //        {
        //            @SuppressWarnings("unused")
        //            @Override
        //            public void actionPerformed( ActionEvent e )
        //            {
        //                btnEnd();
        //            }
        //        } );

        view.getBtnEngineStep().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                btnEngineStep();
            }
        } );

        //        view.getBoardPanelView().addKeyListener( new KeyAdapter()
        //        {
        //            @Override
        //            public void keyPressed( KeyEvent e )
        //            {
        //                processKey( e.getKeyCode() );
        //            }
        //        } );

        view.getCbVisualDebug().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                setVisualDebug( view.getCbVisualDebug().isSelected() );
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
        //        try
        //        {
        //            unsubscribeModel();
        //
        //            String logFilename = chooseFile();
        //            if ( logFilename != null )
        //            {
        boardPanelController.startup();

        displayClientPlayer();

        //                subscribeModel();

        displayStatus();
        updateUI();

        view.pack();
        view.setVisible( true );
        //            }
        //        }
        //        catch( Exception e )
        //        {
        //            e.printStackTrace();
        //            JOptionPane.showMessageDialog( view, e.getStackTrace() );
        //        }
    }

    private void displayClientPlayer()
    {
        PlayerInfoModel clientPlayer = model.getGameModel().getClientPlayer();
        String name = clientPlayer.getPlayerName();
        Color color = clientPlayer.getPlayerColor().getAwtColor();
        view.getLblClientplayer().setText( GuiUtils.getColorLabel( name, color, Color.LIGHT_GRAY ) );
    }

    public String chooseFile()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory( new File( ConfigFolder.getConfigDir() ) );
        chooser.setFileFilter( new GameLogFileFilter() );
        chooser.setFileView( new GameLogFileView() );

        if ( chooser.showDialog( parentComponent, "Select game log file" ) == JFileChooser.APPROVE_OPTION )
            return chooser.getSelectedFile().getPath();

        return null;
    }

    private class GameLogFileFilter extends FileFilter
    {
        private Pattern pattern;

        public GameLogFileFilter()
        {
            pattern = Pattern.compile( "^game-[0-9]+-[0-9].log$" );
        }

        @Override
        public String getDescription()
        {
            return "Game log files";
        }

        @Override
        public boolean accept( File f )
        {
            if ( !f.isFile() )
                return false;

            return pattern.matcher( f.getName() ).matches();
        }
    }

    private class GameLogFileView extends FileView
    {
        private SimpleDateFormat df = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" );

        @Override
        public String getName( File f )
        {
            if ( f.isFile() )
            {
                int i = f.getName().indexOf( '-', 6 );
                String stamp = f.getName().substring( 5, i );
                long l = Long.valueOf( stamp ).longValue();
                return df.format( new Date( l ) ) + "  " + f.getName();
            }

            return f.getName();
        }
    }

    private void displayStatus()
    {
        int cellX = model.getBoardPanelModel().getCellGridXFromMouse();
        int cellY = model.getBoardPanelModel().getCellGridYFromMouse();

        String s = "step " + (model.getCurrentStep() + 1) + "/" + model.getStepCount();
        s += " x y " + cellX + " " + cellY;

        Cell cell = model.getGameModel().getBoardStateModel().getCell( cellX, cellY );
        s += " id " + cell.cellState.playerId;
        s += " troops " + cell.cellState.getTroopsLevel();

        view.getLblStatus().setText( s );
    }

    private void firstStep()
    {
        try
        {
            model.gameLogStart( ReplayFrameController.this );
            boardPanelController.redrawBoard();
            displayStatus();
            updateUI();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog( view, "Error reading step\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void previousBigStep()
    {
        try
        {
            model.gameLogBigPrevious( ReplayFrameController.this );
            boardPanelController.redrawBoard();
            displayStatus();
            updateUI();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog( view, "Error reading step\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void previousStep()
    {
        try
        {
            model.gameLogPrevious( ReplayFrameController.this );
            boardPanelController.redrawBoard();
            displayStatus();
            updateUI();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog( view, "Error reading step\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void nextStep()
    {
        try
        {
            model.gameLogNext( ReplayFrameController.this );
            boardPanelController.redrawBoard();
            displayStatus();
            updateUI();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog( view, "Error reading step\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void nextBigStep()
    {
        try
        {
            model.gameLogBigNext( ReplayFrameController.this );
            boardPanelController.redrawBoard();
            displayStatus();
            updateUI();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog( view, "Error reading step\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void lastStep()
    {
        try
        {
            model.gameLogEnd( ReplayFrameController.this );
            boardPanelController.redrawBoard();
            displayStatus();
            updateUI();
        }
        catch( Exception e )
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog( view, "Error reading step\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void btnEngineStep()
    {
        model.getGameEngine().computeStep();
    }

    private void setVisualDebug( boolean vd )
    {
        visualDebug = vd;
        view.getCbVisualDebug().setSelected( visualDebug );

        boardPanelController.setDebugMode( visualDebug );
        boardPanelController.redrawBoard();
    }

    private void processKey( XBKeyInfo keyInfo )
    {
        //        int keyCode = model.getBoardPanelModel().getKeyCode();

        switch ( keyInfo.getKeyCode() )
        {
            case KeyEvent.VK_RIGHT:
                nextStep();
                break;

            case KeyEvent.VK_LEFT:
                previousStep();
                break;

            case KeyEvent.VK_PAGE_UP:
                previousBigStep();
                break;

            case KeyEvent.VK_PAGE_DOWN:
                nextBigStep();
                break;

            case KeyEvent.VK_HOME:
                firstStep();
                break;

            case KeyEvent.VK_END:
                lastStep();
                break;

            case KeyEvent.VK_D:
                setVisualDebug( !visualDebug );
                break;

            case KeyEvent.VK_SPACE:
                btnEngineStep();
                break;

            default:
                break;

        //            default:
        //                System.out.println( "key code " + keyCode );
        }
    }

    private void updateUI()
    {
    }

    // MVCModelObserver interface

    private void processMessages( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof XBMVCModelChangeId )
            switch ( (XBMVCModelChangeId)change.getChangeId() )
            {
                case MouseMoved:
                    displayStatus();
                    break;

                case KeyPressed:
                    //                    processKey( (KeyInfo)change.getData() );
                    processKey( (XBKeyEvent)change.getData() );
                    break;

                case MouseWheel:
                    //                    Integer inc = (Integer)change.getData();
                    //                    MouseInfo mouseInfo = (MouseInfo)change.getData();
                    XBMouseEvent mouseInfo = (XBMouseEvent)change.getData();
                    //                    boolean ctrl = model.getBoardPanelModel().isCtrlModifier();
                    boolean ctrl = mouseInfo.isCtrl();
                    //                    boolean shift = model.getBoardPanelModel().isShiftModifier();
                    boolean shift = mouseInfo.isShift();
                    //                    if ( inc.intValue() > 0 )
                    if ( mouseInfo.getWheelInc() > 0 )
                    {
                        if ( ctrl && !shift )
                            model.getBoardPanelModel().modifyCellSize( this, 1 );

                        if ( !ctrl && shift )
                            nextBigStep();

                        if ( !ctrl && !shift )
                            nextStep();
                    }
                    else
                    {
                        if ( ctrl && !shift )
                            model.getBoardPanelModel().modifyCellSize( this, -1 );

                        if ( !ctrl && shift )
                            previousBigStep();

                        if ( !ctrl && !shift )
                            previousStep();
                    }
                    break;

                case GridCellSizeChanged:
                    view.pack();
                    break;

                default:
                    break;
            }
        else if ( change.getChangeId() instanceof ReplayCursorPanelModelChangeId )
            switch ( (ReplayCursorPanelModelChangeId)change.getChangeId() )
            {
                case RequestFirstStep:
                    firstStep();
                    break;

                case RequestLastStep:
                    lastStep();
                    break;

                case RequestPreviousStep:
                    previousStep();
                    break;

                case RequestNextStep:
                    nextStep();
                    break;

                case RequestNextBigStep:
                    nextBigStep();
                    break;

                case RequestPreviousBigStep:
                    previousBigStep();
                    break;

                default:
                    break;
            }
    }

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        //        if ( SwingUtilities.isEventDispatchThread() )
        //            processMessages( change );
        //        else
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
        if ( model != null )
            model.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        if ( model != null )
            model.removeObserver( this );
    }

    private void close_edt()
    {
        if ( view != null )
        {
            view.setVisible( false );
            view.dispose();
            view = null;
        }

        boardPanelController.close();
        replayCursorPanelController.close();

        unsubscribeModel();
    }

    private void modelToUI_edt()
    {
        view.getCbVisualDebug().setSelected( visualDebug );
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
    public ReplayFrameView getView()
    {
        return view;
    }

    @Override
    public ReplayFrameModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( ReplayFrameModel m )
    {
        unsubscribeModel();
        model = m;

        replayCursorPanelController.setModel( model );
        boardPanelController.setModel( model.getBoardPanelModel() );

        subscribeModel();
        modelToUI();
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
}
