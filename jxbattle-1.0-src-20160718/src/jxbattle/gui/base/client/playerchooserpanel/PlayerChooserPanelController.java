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

package jxbattle.gui.base.client.playerchooserpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.automaton.AutomatonModelChangedId;
import org.generic.mvc.model.list.ListModelChangeId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.net.NetModelChangeId;

public class PlayerChooserPanelController implements MVCController<PlayerChooserPanelModel, PlayerChooserPanelView>, MVCModelObserver, ListCellRenderer<PlayerInfoModel>
{
    private PlayerChooserPanelView view;

    private DefaultListModel<PlayerInfoModel> playersListModel;

    private PlayerChooserPanelModel model;

    public PlayerChooserPanelController( PlayerChooserPanelView v )
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
        playersListModel = new DefaultListModel<>();
        view.getListPlayers().setModel( playersListModel );
        view.getListPlayers().setCellRenderer( this );

        createHandlers();
    }

    private void createHandlers()
    {
        view.getBtnChoosePlayer().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                requestPlayer();
            }
        } );

        view.getBtnCancel().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                cancelPlayer();
            }
        } );

        view.getListPlayers().addMouseListener( new MouseAdapter()
        {
            @Override
            public void mouseClicked( MouseEvent e )
            {
                if ( view.getListPlayers().isEnabled() )
                {
                    switch ( e.getClickCount() )
                    {
                        case 2:
                            requestPlayer();
                            break;

                        default:
                            break;
                    }
                }
            }
        } );

        view.getListPlayers().addKeyListener( new KeyAdapter()
        {
            @Override
            public void keyPressed( KeyEvent e )
            {
                if ( e.getKeyCode() == KeyEvent.VK_ENTER )
                    requestPlayer();
            }
        } );

        view.getListPlayers().getSelectionModel().addListSelectionListener( new ListSelectionListener()
        {
            @Override
            public void valueChanged( ListSelectionEvent e )
            {
                updateChoosePlayerButtonState();
            }
        } );

        view.getTfPlayerName().addKeyListener( new KeyAdapter()
        {
            @Override
            public void keyReleased( KeyEvent e )
            {
                onPlayerNameKeyTyped();
            }
        } );
    }

    private void ui2mClientName()
    {
        String pn = view.getTfPlayerName().getText();
        model.getClientEngine().getSystemParametersModel().setClientName( this, pn );
    }

    private void onPlayerNameKeyTyped()
    {
        ui2mClientName();
        updatePlayerNameLabelState();
        updateChoosePlayerButtonState();
    }

    private PlayerInfoModel getSelectedPlayer()
    {
        return view.getListPlayers().getSelectedValue();
    }

    private boolean canRequestPlayer()
    {
        boolean selected = getSelectedPlayer() != null;
        return selected && model.getClientEngine().canRequestPlayer( getSelectedPlayer() );
    }

    private void requestPlayer()
    {
        if ( canRequestPlayer() )
        {
            view.getListPlayers().setEnabled( false ); // inhibit events (avoid double triggering)

            PlayerInfoModel pim = getSelectedPlayer();
            if ( pim != null )
                model.getClientEngine().requestPlayer( pim );

            view.getListPlayers().setEnabled( true );
        }
    }

    private void cancelPlayer()
    {
        model.getClientEngine().cancelPlayer();
    }

    private void updatePlayerNameLabelState()
    {
        boolean connected = model.getClientEngine().getClientSideConnectionModel().isConnected();
        boolean nameOk = model.getClientEngine().isClientNameOk();
        view.getLblPleaseEnterAName().setVisible( !connected && !nameOk );
    }

    private void updateChoosePlayerButtonState()
    {
        view.getBtnChoosePlayer().setEnabled( canRequestPlayer() );
    }

    private void m2uiPlayerName()
    {
        // use invokeLater since this method can be called during a notification sent by
        // the JTextField being modified (the Document backing the JTextField throws an
        // IllegalState exception if modified during a notification)

        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                if ( view != null )
                    view.getTfPlayerName().setText( model.getClientEngine().getSystemParametersModel().getClientName() );
            }
        } );
    }

    private void m2uiPlayerList()
    {
        playersListModel.clear();

        if ( model.getClientEngine().getClientSideConnectionModel().isConnected() )
        {
            int ind = 0;

            //            SyncIterator<PlayerInfoModel> it = model.getGameModel().getPlayersModel().iterator();
            //            while ( it.hasNext() )
            //            {
            //                PlayerInfoModel pim = it.next();
            //                if ( pim.isPlayerSelected() )
            //                    playersListModel.insertElementAt( pim, ind++ );
            //                else
            //                    playersListModel.addElement( pim );
            //            }
            //            it.close();

            for ( PlayerInfoModel pim : model.getClientEngine().getGameModel().getPlayersModel() )
                if ( pim.isPlayerSelected() )
                    playersListModel.insertElementAt( pim, ind++ );
                else
                    playersListModel.addElement( pim );
        }

        view.getListPlayers().revalidate();
        view.getListPlayers().repaint();
        view.getListPlayers().updateUI();
    }

    private void updateGuiState()
    {
        boolean connected = model.getClientEngine().getClientSideConnectionModel().isConnected();

        boolean setup = model.getClientEngine().isSettingUp();
        boolean setupCompleted = model.getClientEngine().isSetupCompleted();

        view.getTfPlayerName().setEnabled( !connected );
        view.getBtnCancel().setEnabled( setupCompleted );

        view.getListPlayers().setEnabled( setup );

        updatePlayerNameLabelState();
        updateChoosePlayerButtonState();
        m2uiClientPlayerColor();
    }

    private void m2uiClientPlayerColor()
    {
        Color panelCol;
        if ( model.getClientEngine().isClientPlayerChosen() )
            panelCol = model.getClientEngine().getClientPlayerColor().getAwtColor();
        else
            panelCol = new Color( 238, 238, 238 );

        view.getPnlColor().setBackground( panelCol );
    }

    private void modelToUI_edt()
    {
        m2uiPlayerName();
        m2uiPlayerList();
        updateGuiState();
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

    private void playerParametersChanged()
    {
        m2uiPlayerList();
    }

    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof AutomatonModelChangedId )
            switch ( (AutomatonModelChangedId)change.getChangeId() )
            {
                case AutomatonStateChanged:
                    updateGuiState();
                    break;

                default:
                    break;
            }
        else if ( change.getChangeId() instanceof XBMVCModelChangeId )
            switch ( (XBMVCModelChangeId)change.getChangeId() )
            {
                case PlayerStateChanged:
                    m2uiPlayerList();
                    m2uiClientPlayerColor();
                    break;

                case ClientNameChanged:
                    m2uiPlayerName();
                    break;

                default:
                    break;
            }
        else if ( change.getChangeId() instanceof NetModelChangeId )
            switch ( (NetModelChangeId)change.getChangeId() )
            {
                case PeerConnectionSucceeded:
                case ConnectionShutdown:
                case PeerDisconnected:
                    if ( view != null )
                    {
                        m2uiPlayerList();
                        m2uiClientPlayerColor();
                    }
                    break;

                default:
                    break;
            }

        if ( change.getChangeId() instanceof ListModelChangeId )
            switch ( (ListModelChangeId)change.getChangeId() )
            {
                case List_AddElement:
                case List_ListCleared:
                case List_RemoveElement:
                    if ( change.getData() == model.getClientEngine().getGameModel().getPlayersModel() )
                        playerParametersChanged();
                    break;

                default:
                    break;
            }

        //        else if ( change instanceof NetworkSpeedChange )
        //            updateNetworkSpeed();
    }

    // MVCController interface

    @Override
    public PlayerChooserPanelView getView()
    {
        return view;
    }

    @Override
    public PlayerChooserPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( PlayerChooserPanelModel m )
    {
        unsubscribeModel();
        model = m;
        subscribeModel();

        modelToUI();
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

    // ListCellRenderer interface

    @Override
    public Component getListCellRendererComponent( JList<? extends PlayerInfoModel> list, PlayerInfoModel value, int index, boolean isSelected, boolean cellHasFocus )
    {
        JLabel res = new JLabel();

        res.setForeground( value.getPlayerColor().getAwtColor() );

        boolean allocated = value.isPlayerSelected();

        if ( allocated )
            res.setText( value.getPlayerName() + " [in use]" );
        else
            res.setText( value.getPlayerColor().getName() + " player" );

        if ( isSelected )
            res.setBackground( Color.GRAY );
        else
            res.setBackground( Color.LIGHT_GRAY );

        res.setOpaque( true );

        return res;
    }
}
