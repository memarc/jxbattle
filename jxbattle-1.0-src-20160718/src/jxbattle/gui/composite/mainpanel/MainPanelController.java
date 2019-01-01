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

package jxbattle.gui.composite.mainpanel;

import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import jxbattle.gui.base.common.menupanel.MenuPanelController;
import jxbattle.gui.composite.client.clientpanel.ClientPanelController;
import jxbattle.gui.composite.client.clientpanel.ClientPanelModel;
import jxbattle.gui.composite.client.clientpanel.ClientPanelView;
import jxbattle.gui.composite.server.serverpanel.ServerPanelController;
import jxbattle.gui.composite.server.serverpanel.ServerPanelModel;
import jxbattle.gui.composite.server.serverpanel.ServerPanelView;
import jxbattle.model.client.ClientEngine;
import jxbattle.model.common.observer.XBMVCModelChangeId;
import jxbattle.model.server.ServerEngine;

import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class MainPanelController implements MVCController<MainPanelModel, MainPanelView>, MVCModelObserver
{
    private MainPanelView view;

    private MainPanelModel model;

    private Window parentWindow;

    private MenuPanelController menuPanelController;

    private List<MVCController<?, ?>> panelControllers;

    private final String ClientTabTitle = "Client";

    private final String ServerTabTitle = "Server";

    public MainPanelController( MainPanelView v, Window pw )
    {
        view = v;
        parentWindow = pw;
        init();
    }

    private void init()
    {
        panelControllers = new ArrayList<>();
        menuPanelController = new MenuPanelController( view.getMenuPanelView() );
    }

    private void createClientPanel( ClientEngine ce )
    {
        ClientPanelView clientPanelView = new ClientPanelView();
        view.getTabbedPane().addTab( ClientTabTitle, null, clientPanelView, null );

        ClientPanelController clientPanelController = new ClientPanelController( clientPanelView, parentWindow );
        clientPanelController.setModel( new ClientPanelModel( ce ) );
        panelControllers.add( clientPanelController );

        int ind = view.getTabbedPane().getTabCount() - 1;
        view.getTabbedPane().setSelectedIndex( ind );
    }

    private void createServerPanel( ServerEngine se )
    {
        ServerPanelView serverPanelView = new ServerPanelView();
        view.getTabbedPane().addTab( ServerTabTitle, null, serverPanelView, null );

        ServerPanelController serverPanelController = new ServerPanelController( serverPanelView, parentWindow );
        serverPanelController.setModel( new ServerPanelModel( se ) );
        panelControllers.add( serverPanelController );

        int ind = view.getTabbedPane().indexOfTab( ServerTabTitle );
        view.getTabbedPane().setSelectedIndex( ind );
    }

    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof XBMVCModelChangeId )
            switch ( (XBMVCModelChangeId)change.getChangeId() )
            {
                case ClientEngineStart:
                    createClientPanel( (ClientEngine)change.getData() );
                    break;

                case ServerEngineStart:
                    createServerPanel( (ServerEngine)change.getData() );
                    break;

                default:
                    break;
            }
    }

    // MVCController interface

    @Override
    public MainPanelView getView()
    {
        return view;
    }

    @Override
    public MainPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( MainPanelModel m )
    {
        unsubscribeModel();
        model = m;

        menuPanelController.setModel( model.getMenuPanelModel() );

        subscribeModel();
    }

    private void close_edt()
    {
        menuPanelController.close();

        for ( MVCController<?, ?> mvcc : panelControllers )
            mvcc.close();

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
}
