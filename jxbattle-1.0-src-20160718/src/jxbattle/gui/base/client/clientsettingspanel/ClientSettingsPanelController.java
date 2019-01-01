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

package jxbattle.gui.base.client.clientsettingspanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import jxbattle.model.MainModel;

import org.generic.gui.GuiUtils;
import org.generic.gui.parameters.IntSpinnerController;
import org.generic.gui.parameters.IntSpinnerModel;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.automaton.AutomatonModelChangedId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.mvc.model.parameter.ParameterModelChangeId;

public class ClientSettingsPanelController implements MVCController<ClientSettingsPanelModel, ClientSettingsPanelView>, MVCModelObserver
{
    private ClientSettingsPanelModel model;

    private ClientSettingsPanelView view;

    private IntSpinnerController socketTimeoutController;

    private IntSpinnerController networkBiasController;

    public ClientSettingsPanelController( ClientSettingsPanelView v )
    {
        view = v;
        init();
    }

    private void init()
    {
        socketTimeoutController = new IntSpinnerController( view.getSpinSocketTimeout() );
        networkBiasController = new IntSpinnerController( view.getSpinNetworkBias() );

        createHandlers();
    }

    private void createHandlers()
    {
        view.getCbLogGame().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                boolean s = view.getCbLogGame().isSelected();
                //                model.getSystemParametersModel().getLogGameReplayModel().setValue( ClientSettingsPanelController.this, s );
                model.setLogGameReplay( ClientSettingsPanelController.this, s );
            }
        } );
    }

    private void modelToUI_edt()
    {
        //        view.getCbLogGame().setSelected( model.getSystemParametersModel().getLogGameReplayModel().getValue() );
        view.getCbLogGame().setSelected( model.getLogGameReplay() );
        //        GuiUtils.setRecursiveEnable( view, model.getCurrentState() == ClientAutomatonStateId.NotConnected );
        GuiUtils.setRecursiveEnable( view, !model.isReadOnly() );
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
                    modelToUI();
                    break;

                default:
                    break;
            }
        else if ( change.getChangeId() instanceof ParameterModelChangeId )
            switch ( (ParameterModelChangeId)change.getChangeId() )
            {
                case BoolParameterChanged:
                    modelToUI();
                    break;

                default:
                    break;
            }
    }

    // MVCController interface

    @Override
    public ClientSettingsPanelView getView()
    {
        return view;
    }

    @Override
    public ClientSettingsPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( ClientSettingsPanelModel m )
    {
        unsubscribeModel();
        //        model = null;
        //
        //        if ( m instanceof ClientSettingsPanelModel )
        //        {
        //            model = (ClientSettingsPanelModel)m;
        model = m;
        //            socketTimeoutController.setModel( model.getSystemParametersModel().getSocketTimeoutModel() );
        socketTimeoutController.setModel( new IntSpinnerModel( model.getSocketTimeoutModel() ) );
        //            networkBiasController.setModel( model.getSystemParametersModel().getNetworkBiasModel() );
        networkBiasController.setModel( new IntSpinnerModel( model.getNetworkBiasModel() ) );
        //        }

        subscribeModel();
        modelToUI();
    }

    private void close_edt()
    {
        //model.saveConfig();
        if ( !model.isReadOnly() )
            MainModel.getInstance().saveClientConfig();
        unsubscribeModel();
        socketTimeoutController.close();
        networkBiasController.close();
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

    // MVCModelObserver

    @Override
    public void modelChanged( final MVCModelChange change )
    {
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
