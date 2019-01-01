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

package jxbattle.gui.composite.server.serverpanel;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jxbattle.gui.base.common.gamesettingspanel.GameSettingsPanelModel;
import jxbattle.gui.base.server.gameplayerspanel.GamePlayersPanelController;
import jxbattle.gui.composite.client.gamesettingsdialog.GameSettingsDialogController;
import jxbattle.gui.composite.server.gameprofilesdialog.GameProfilesDialogController;
import jxbattle.gui.composite.server.serversettingsdialog.ServerSettingsDialogController;
import jxbattle.model.server.GameParametersProfileModel;

import org.generic.gui.logpanel.LogPanelController;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.automaton.AutomatonModelChangedId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class ServerPanelController implements MVCController<ServerPanelModel, ServerPanelView>, MVCModelObserver
{
    private ServerPanelView view;

    private Window parentWindow;

    private ServerPanelModel model;

    private GamePlayersPanelController playerParametersPanelController;

    private LogPanelController logPanelController;

    public ServerPanelController( ServerPanelView v, Window pw )
    {
        view = v;
        parentWindow = pw;
        init();
    }

    @Override
    protected void finalize() throws Throwable
    {
        close();
        super.finalize();
    }

    private void init()
    {
        playerParametersPanelController = new GamePlayersPanelController( view.getGamePlayersPanelView() );
        logPanelController = new LogPanelController( view.getLogPanelView() );
        createHandlers();
    }

    private void createHandlers()
    {
        view.getBtnRunGame().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ev )
            {
                onButtonRunGame();
            }
        } );

        view.getBtnStopGame().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ev )
            {
                onButtonStopGame();
            }
        } );

        view.getBtnGameSettings().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ev )
            {
                onButtonGameSettings();
            }
        } );

        view.getBtnServerSettings().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent ev )
            {
                onButtonServerSettings();
            }
        } );

        view.getCbStepMode().addItemListener( new ItemListener()
        {
            @Override
            public void itemStateChanged( ItemEvent e )
            {
                onButtonStepMode();
            }
        } );

        view.getBtnDoStep().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onButtonDoStep();
            }
        } );
    }

    private void onButtonStepMode()
    {
        boolean b = view.getCbStepMode().isSelected();
        model.getServeEngine().setStepMode( b );
        model.getServeEngine().setCanStep( true );
        updateCbStepModeState();
    }

    private void onButtonDoStep()
    {
        model.getServeEngine().setCanStep( true );
    }

    private void onButtonRunGame()
    {
        try
        {
            model.getServeEngine().checkGameParameters();
            model.getServeEngine().startListen();
        }
        catch( Exception e )
        {
            JOptionPane.showMessageDialog( view, e.getMessage(), "Error starting server", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void onButtonStopGame()
    {
        model.getServeEngine().stopGame();
    }

    private void onButtonGameSettings()
    {
        if ( model.getServeEngine().isSettingUp() )
        {
            GameProfilesDialogController gameProfilesDialogController = new GameProfilesDialogController( parentWindow );
            gameProfilesDialogController.setModel( model.getGameParametersProfilesModel() );
            gameProfilesDialogController.runDialog();
        }
        else
        {
            GameSettingsDialogController gameSettingsDialogController = new GameSettingsDialogController( parentWindow );
            //            gameSettingsDialogController.setModel( model.getGameParametersProfilesModel().getCurrentProfileModel().getGameParametersModel() );
            //            gameSettingsDialogController.setModel( model.getServerModel().getCurrentGameParametersProfileModel() );
            GameParametersProfileModel prof = model.getServerModel().getCurrentGameParametersProfileModel();
            //            gameSettingsDialogController.setModel( new GameSettingsPanelModel( prof.getGameParametersModel(), prof.getReadOnly() ) );
            gameSettingsDialogController.setModel( new GameSettingsPanelModel( prof.getGameParametersModel(), true ) );
            gameSettingsDialogController.runDialog();
        }
    }

    private void onButtonServerSettings()
    {
        ServerSettingsDialogController serverSettingsDialogController = new ServerSettingsDialogController( parentWindow );
        serverSettingsDialogController.setModel( model.getServeEngine() );
        serverSettingsDialogController.runDialog();
    }

    private void updateUIState()
    {
        if ( view != null )
        {
            boolean setup = model.getServeEngine().isSettingUp();

            view.getBtnRunGame().setEnabled( setup ); // && hasPlayableColors );
            view.getBtnStopGame().setEnabled( !setup );
            playerParametersPanelController.setEnabled( setup );
            updateCbStepModeState();

            view.getLblStatus().setText( model.getServeEngine().getCurrentState().toString() );
        }
    }

    private void updateCbStepModeState()
    {
        view.getBtnDoStep().setEnabled( model.getServeEngine().isGaming() && model.getServeEngine().getStepMode() );
    }

    private void modelToUI_edt()
    {
        updateUIState();
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

    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof AutomatonModelChangedId )
            switch ( (AutomatonModelChangedId)change.getChangeId() )
            {
                case AutomatonStateChanged:
                    updateUIState();
                    break;

                default:
                    break;
            }
    }

    // MVCController  interface

    @Override
    public ServerPanelView getView()
    {
        return view;
    }

    @Override
    public ServerPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( ServerPanelModel m )
    {
        unsubscribeModel();
        //        model = null;
        //
        //        if ( m instanceof ServerEngine )
        //        {
        //            model = (ServerEngine)m;
        model = m;
        playerParametersPanelController.setModel( model.getServerModel() );
        logPanelController.setModel( model.getLogPanelModel() );
        //        }

        subscribeModel();
        modelToUI();
    }

    private void close_edt()
    {
        playerParametersPanelController.close();
        logPanelController.close();
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
        // called from code that is potentially not in the EDT...

        //        if ( SwingUtilities.isEventDispatchThread() )
        //            processModelChange( change );
        //        else
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                processModelChange( change );
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
}
