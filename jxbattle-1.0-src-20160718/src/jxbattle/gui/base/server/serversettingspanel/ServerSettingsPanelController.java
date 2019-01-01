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

package jxbattle.gui.base.server.serversettingspanel;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import jxbattle.model.common.parameters.SystemParametersModel;

import org.generic.gui.GuiUtils;
import org.generic.gui.parameters.BoolCheckController;
import org.generic.gui.parameters.IntSpinnerController;
import org.generic.gui.parameters.IntSpinnerModel;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.automaton.AutomatonModelChangedId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class ServerSettingsPanelController implements MVCController<ServerSettingsPanelModel, ServerSettingsPanelView>, MVCModelObserver
{
    private ServerSettingsPanelView view;

    private ServerSettingsPanelModel model;

    private IntSpinnerController listenPortController;

    private IntSpinnerController socketTimeoutController;

    private IntSpinnerController gameTickController;

    private IntSpinnerController flushFrequencyController;

    private IntSpinnerController networkBiasController;

    private BoolCheckController checkClientStateController;

    public ServerSettingsPanelController( ServerSettingsPanelView v )
    {
        view = v;
        init();
    }

    private void init()
    {
        ToolTipManager.sharedInstance().setInitialDelay( 500 );
        ToolTipManager.sharedInstance().setReshowDelay( 0 );

        listenPortController = new IntSpinnerController( view.getSpinListenPort() );
        socketTimeoutController = new IntSpinnerController( view.getSpinSocketTimeout() );
        gameTickController = new IntSpinnerController( view.getSpinGameTick() );
        flushFrequencyController = new IntSpinnerController( view.getSpinFlushFrequency() );
        networkBiasController = new IntSpinnerController( view.getSpinNetworkBias() );
        checkClientStateController = new BoolCheckController( view.getCbCheckClientsState() );
    }

    private void modelToUI_edt()
    {
        if ( model != null )
        {
            //            boolean isSettingUp = model.isSettingUp();
            GuiUtils.setRecursiveEnable( view, !model.isReadOnly() );
        }
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
    public ServerSettingsPanelView getView()
    {
        return view;
    }

    @Override
    public ServerSettingsPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( ServerSettingsPanelModel m )
    {
        unsubscribeModel();
        //        model = null;
        //
        //        if ( m instanceof ServerSettingsPanelModel )
        //        {
        //            model = (ServerSettingsPanelModel)m;
        model = m;
        SystemParametersModel spm = model.getSystemParametersModel();
        listenPortController.setModel( new IntSpinnerModel( spm.getServerListenPortModel() ) );
        socketTimeoutController.setModel( new IntSpinnerModel( spm.getSocketTimeoutModel() ) );
        gameTickController.setModel( new IntSpinnerModel( spm.getGameTickIntervalModel() ) );
        flushFrequencyController.setModel( new IntSpinnerModel( spm.getFlushFrequencyModel() ) );
        networkBiasController.setModel( new IntSpinnerModel( spm.getNetworkBiasModel() ) );
        checkClientStateController.setModel( spm.getCheckClientsStateModel() );
        //        }

        subscribeModel();
        modelToUI();
    }

    private void close_edt()
    {
        //        if ( model.isSettingUp() )
        if ( !model.isReadOnly() )
            model.saveConfig();

        listenPortController.close();
        socketTimeoutController.close();
        gameTickController.close();
        flushFrequencyController.close();
        networkBiasController.close();
        checkClientStateController.close();

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
    public void modelChanged( MVCModelChange change )
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
