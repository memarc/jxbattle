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

package jxbattle.gui.composite.client.clientpanel;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import jxbattle.gui.base.client.connectiontoserverpanel.ConnectionToServerPanelController;
import jxbattle.gui.base.client.connectiontoserverpanel.ConnectionToServerPanelModel;
import jxbattle.gui.base.client.playerchooserpanel.PlayerChooserPanelController;
import jxbattle.gui.base.client.playerchooserpanel.PlayerChooserPanelModel;
import jxbattle.gui.base.common.gamesettingspanel.GameSettingsPanelModel;
import jxbattle.gui.composite.client.clientsettingsdialog.ClientSettingsDialogController;
import jxbattle.gui.composite.client.clientsettingsdialog.ClientSettingsDialogModel;
import jxbattle.gui.composite.client.gamesettingsdialog.GameSettingsDialogController;
import jxbattle.model.client.automaton.ClientAutomatonStateId;

import org.generic.gui.logpanel.LogPanelController;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.automaton.AutomatonModelChangedId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class ClientPanelController implements MVCController<ClientPanelModel, ClientPanelView>, MVCModelObserver
{
    private ClientPanelView view;

    private Window parentWindow;

    private ClientPanelModel model;

    private ConnectionToServerPanelController serverConnectionController;

    private PlayerChooserPanelController playerChooserPanelController;

    private LogPanelController logPanelController;

    public ClientPanelController( ClientPanelView v, Window pw )
    {
        view = v;
        parentWindow = pw;
        init();
    }

    private void init()
    {
        serverConnectionController = new ConnectionToServerPanelController( view.getServerConnectionView() );
        playerChooserPanelController = new PlayerChooserPanelController( view.getPlayerChooserPanelView() );
        logPanelController = new LogPanelController( view.getLogPanelView() );

        createHandlers();
    }

    private void createHandlers()
    {
        view.getBtnClientSettings().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onButtonClientSettings();
            }
        } );

        view.getBtnGameSettings().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onButtonGameSettings();
            }
        } );
    }

    private void onButtonClientSettings()
    {
        ClientSettingsDialogController clientSettingsDialogController = new ClientSettingsDialogController( parentWindow );
        clientSettingsDialogController.setModel( new ClientSettingsDialogModel( model.getClientEngine() ) );
        clientSettingsDialogController.runDialog();
    }

    private void onButtonGameSettings()
    {
        GameSettingsDialogController gameSettingsDialogController = new GameSettingsDialogController( parentWindow );
        //        gameSettingsDialogController.setModel( model.getGameModel().getGameParametersModel() );
        gameSettingsDialogController.setModel( new GameSettingsPanelModel( model.getClientEngine().getGameModel().getGameParametersModel(), true ) );
        gameSettingsDialogController.runDialog();
    }

    private void updateUIState()
    {
        boolean b = false;

        if ( model != null )
            //            b = model.hasGameParameters();
            b = model.getClientEngine().isGaming();

        view.getBtnGameSettings().setEnabled( b );
    }

    private void setStatus()
    {
        ClientAutomatonStateId state = model.getClientEngine().getCurrentState();
        view.getLblStatus().setText( state.toString() );
    }

    private void modelToUI_edt()
    {
        setStatus();
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
                    //view.getLblStatus().setText( change.getChangedObject().toString() );
                    modelToUI();
                    break;

                default:
                    break;
            }
    }

    // MVCController interface

    @Override
    public ClientPanelView getView()
    {
        return view;
    }

    @Override
    public ClientPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( ClientPanelModel m )
    {
        unsubscribeModel();
        model = m;
        serverConnectionController.setModel( new ConnectionToServerPanelModel( model.getClientEngine() ) );
        //        logPanelController.setModel( ClientEngine.logMessageModel );
        logPanelController.setModel( model.getLogPanelModel() );
        playerChooserPanelController.setModel( new PlayerChooserPanelModel( model.getClientEngine() ) );

        subscribeModel();

        modelToUI();
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        // called from code that is potentially not in the EDT...

        if ( SwingUtilities.isEventDispatchThread() )
            processModelChange( change );
        else
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

    private void close_edt()
    {
        playerChooserPanelController.close();
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
}
