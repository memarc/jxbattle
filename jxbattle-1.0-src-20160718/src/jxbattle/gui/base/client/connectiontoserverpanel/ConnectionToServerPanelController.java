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

package jxbattle.gui.base.client.connectiontoserverpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import jxbattle.model.common.PeerInfoModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.gui.parameters.IntSpinnerController;
import org.generic.gui.parameters.IntSpinnerModel;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.automaton.AutomatonModelChangedId;
import org.generic.mvc.model.list.ListModelChangeId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.net.NetModelChangeId;
import org.generic.net.PeerInfo;

public class ConnectionToServerPanelController implements MVCController<ConnectionToServerPanelModel, ConnectionToServerPanelView>, MVCModelObserver
{
    private ConnectionToServerPanelView view;

    private ConnectionToServerPanelModel model;

    private IntSpinnerController serverPortController;

    //    private AutocompleteComboBoxController<ServerComboBoxItem> serverComboBoxController;

    public ConnectionToServerPanelController( ConnectionToServerPanelView v )
    {
        view = v;
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
        serverPortController = new IntSpinnerController( view.getSpinServerPort() );
        //        serverComboBoxController = new AutocompleteComboBoxController<ServerComboBoxItem>( view.getCmbServers() );

        view.getCmbServers().setRenderer( new ComboItemRenderer() );

        createHandlers();
    }

    private void createHandlers()
    {
        view.getBtnConnect().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onBtnConnect();
            }
        } );

        view.getBtnDisconnect().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onBtnDisconnect();
            }
        } );

        //        view.getCmbServers().addActionListener( new ActionListener()
        //        {
        //            @SuppressWarnings("unused")
        //            @Override
        //            public void actionPerformed( ActionEvent e )
        //            {
        //                int si = view.getCmbServers().getSelectedIndex();
        //                if ( si != -1 )
        //                    //                    EntrySelection es = (EntrySelection)view.getCbGroups().getSelectedItem();
        //                    onComboItemSelect( ConnectionToServerPanelController.this );
        //            }
        //        } );

        view.getCmbServers().getEditor().getEditorComponent().addKeyListener( new KeyAdapter()
        {
            @Override
            public void keyReleased( KeyEvent e )
            {
                onKeyReleased( e );
            }
        } );

        view.getBtnRemoveServer().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onBtnRemoveServer( ConnectionToServerPanelController.this );
            }
        } );
    }

    private void onBtnConnect()
    {
        model.connectServer( ConnectionToServerPanelController.this, getServerInfo() );
    }

    private void onBtnDisconnect()
    {
        model.disconnectServer();
    }

    private void onBtnRemoveServer( Object sender )
    {
        model.getServerHistoryModel().removeFirstFromHistory( sender );
    }

    //    private void onComboItemSelect( Object sender )
    //    {
    //        PeerInfoModel pim = getServerInfoFromCombo();
    //        if ( pim != null )
    //        {
    //            model.setHost( sender, pim.getHost() );
    //            model.setPort( sender, pim.getPort() );
    //        }
    //        else
    //        {
    //            model.setHost( sender, getHostFromUI() );
    //            model.setPort( sender, serverPortController.getModel().getValue() );
    //        }
    //    }

    //    private void onKeyReleased( Object sender )
    //    {
    //        //updateUIState();
    //        model.setHost( sender, getHostFromUI() );
    //    }

    private void onKeyReleased( KeyEvent e )
    {
        if ( e.getKeyCode() == KeyEvent.VK_ENTER )
            onBtnConnect();
        //        else
        //        {
        //            ComboBoxModel<ServerComboBoxItem> cm = view.getCmbServers().getModel();
        //            String ed = getHostFromComboEditor();
        //            System.out.println( "\nin " + ed );
        //            boolean found = false;
        //            if ( ed != null && ed.length() > 0 )
        //            {
        //                ServerComboBoxItem item = null;
        //                for ( int i = 0; i < cm.getSize(); i++ )
        //                {
        //                    item = cm.getElementAt( i );
        //                    String host = item.getServerInfo().getHost();
        //                    found = host.indexOf( ed ) != -1;
        //                    if ( found )
        //                        break;
        //                }
        //                if ( found )
        //                {
        //                    System.out.println( "found " + item );
        //                    cm.setSelectedItem( item );
        //                }
        //
        //            }
        //            view.getCmbServers().setPopupVisible( found );
        //            //            updateUI();
        //        }
    }

    private PeerInfo getServerInfo()
    {
        Object o = view.getCmbServers().getEditor().getItem();

        if ( o instanceof ServerComboBoxItem )
        {
            ServerComboBoxItem item = (ServerComboBoxItem)o;
            return item.getServerInfo().getPeerInfo();
        }
        else if ( o instanceof String )
            return new PeerInfo( (String)o, serverPortController.getModel().getValue() );

        return null;
    }

    //    private String getHostFromComboEditor()
    //    {
    //        Object o = view.getCmbServers().getEditor().getItem();
    //        if ( o instanceof String )
    //            return (String)o;
    //
    //        return null;
    //    }

    private void hostToUI()
    {
        if ( model != null )
            view.getCmbServers().getEditor().setItem( model.getHost() );
    }

    private void historyToUI()
    {
        DefaultComboBoxModel<ServerComboBoxItem> cmbModel = new DefaultComboBoxModel<>();

        if ( model != null )
        {
            // add history

            //            SyncIterator<PeerInfoModel> it = model.getServerHistoryModel().iterator();
            //            while ( it.hasNext() )
            //            {
            //                ServerComboBoxItem item = new ServerComboBoxItem( it.next() );
            //                cmbModel.addElement( item );
            //            }
            //            it.close();

            for ( PeerInfoModel pim : model.getServerHistoryModel() )
            {
                ServerComboBoxItem item = new ServerComboBoxItem( pim );
                cmbModel.addElement( item );
            }
        }

        view.getCmbServers().setModel( cmbModel );
    }

    private void updateUIState()
    {
        boolean connected = false;
        boolean serverDefined = false;
        boolean clientNameOk = false;
        boolean gaming = false;
        boolean gameEnd = false;

        if ( model != null )
        {
            serverDefined = model.getHost().length() > 0;
            connected = model.isConnected();
            clientNameOk = model.isClientNameOk();
            gaming = model.isGaming();
            gameEnd = model.isGameEnded();
        }

        view.getCmbServers().setEnabled( !connected );
        view.getSpinServerPort().setEnabled( !connected );
        view.getBtnConnect().setEnabled( !connected && serverDefined && clientNameOk && !gameEnd );
        view.getBtnDisconnect().setEnabled( connected && !gaming );
        view.getBtnRemoveServer().setEnabled( !connected );
    }

    //    private void updateUI()
    //    {
    //        view.revalidate();
    //        view.repaint();
    //    }

    private void modelToUI_edt()
    {
        historyToUI();
        hostToUI();
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
        else if ( change.getChangeId() instanceof NetModelChangeId )
            switch ( (NetModelChangeId)change.getChangeId() )
            {
                case PeerConnectionSucceeded:
                    model.addCurrentToHistory( ConnectionToServerPanelController.this );
                    updateUIState();
                    break;

                case ConnectionShutdown:
                    updateUIState();
                    break;

                default:
                    break;
            }
        else if ( change.getChangeId() instanceof XBMVCModelChangeId )
            switch ( (XBMVCModelChangeId)change.getChangeId() )
            {
            //                case PeerHostChanged:
            //                    hostToUI();
            //                    updateUIState();
            //                    break;

                case ClientNameChanged:
                    updateUIState();
                    break;

                default:
                    break;
            }
        else if ( change.getChangeId() instanceof ListModelChangeId )
            switch ( (ListModelChangeId)change.getChangeId() )
            {
                case List_AddElement:
                case List_RemoveElement:
                    historyToUI();
                    break;

                default:
                    break;
            }
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

    private void close_edt()
    {
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

    // MVCController interface

    @Override
    public ConnectionToServerPanelView getView()
    {
        return view;
    }

    @Override
    public ConnectionToServerPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( ConnectionToServerPanelModel m )
    {
        unsubscribeModel();
        model = m;
        serverPortController.setModel( new IntSpinnerModel( model.getPortModel() ) );
        //            serverComboBoxController.setModel( new AutocompleteComboBoxModel<>( model.getServerHistoryModel().getPeerInfos() ) );
        subscribeModel();

        modelToUI();
    }

    private class ComboItemRenderer implements ListCellRenderer<ServerComboBoxItem>
    {
        @Override
        public Component getListCellRendererComponent( JList<? extends ServerComboBoxItem> list, ServerComboBoxItem value, int index, boolean isSelected, boolean cellHasFocus )
        {
            String s = value.getServerInfo().toString();

            JLabel res = new JLabel( s );
            if ( isSelected )
                res.setBackground( Color.GRAY );
            return res;
        }
    }
}
