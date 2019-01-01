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

package jxbattle.gui.base.server.gameplayerspanel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;

import jxbattle.bean.common.player.XBColor;
import jxbattle.model.common.observer.XBMVCModelChangeId;
import jxbattle.model.server.ClientInfoModel;
import jxbattle.model.server.ServerModel;

import org.generic.gui.GuiUtils;
import org.generic.gui.parameters.IntSpinnerController;
import org.generic.gui.parameters.IntSpinnerModel;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.list.ListModelChangeId;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class GamePlayersPanelController implements MVCController<ServerModel, GamePlayersPanelView>, MVCModelObserver, ListCellRenderer<ClientInfoModel>
{
    private GamePlayersPanelView view;

    private ServerModel model;

    private IntSpinnerController playerCountController;

    private DefaultListModel<ClientInfoModel> playersListModel;

    public GamePlayersPanelController( GamePlayersPanelView v )
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
        playerCountController = new IntSpinnerController( view.getSpinPlayerCount() );

        createHandlers();
    }

    private void createHandlers()
    {
    }

    private void updatePlayersList()
    {
        playersListModel.clear();

        if ( model != null )
        {
            //            SyncIterator<ClientInfoModel> it = model.getClientInfosModel().iterator();
            //            try
            //            {
            //                while ( it.hasNext() )
            //                    playersListModel.addElement( it.next() );
            //            }
            //            finally
            //            {
            //                it.close();
            //            }

            for ( ClientInfoModel cim : model.getClientInfosModel() )
                playersListModel.addElement( cim );
        }
    }

    private void updateUI()
    {
        if ( view != null )
        {
            view.revalidate();
            view.updateUI();
        }
    }

    private void modelToUI_edt()
    {
        updatePlayersList();
        updateUI();
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

    public void setEnabled( boolean en )
    {
        GuiUtils.setRecursiveEnable( view, en );
    }

    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof ListModelChangeId )
            switch ( (ListModelChangeId)change.getChangeId() )
            {
                case List_AddElement:
                case List_RemoveElement:
                case List_ListCleared:
                    modelToUI();
                    break;

                default:
                    break;
            }

        if ( change.getChangeId() instanceof XBMVCModelChangeId )
            switch ( (XBMVCModelChangeId)change.getChangeId() )
            {
                case ClientNameChanged:
                case PlayerStateChanged:
                    modelToUI();
                    break;

                default:
                    break;
            }
    }

    // ListCellRenderer interface

    @Override
    public Component getListCellRendererComponent( JList<? extends ClientInfoModel> list, ClientInfoModel value, int index, boolean isSelected, boolean cellHasFocus )
    {
        JLabel res = new JLabel();
        Color col = null;

        boolean allocated = value.isClientPlayerChosen();

        String label = value.getClientName();

        if ( allocated )
        {
            XBColor xc = value.getPlayerColor();
            label += " (" + xc.getName() + " player)";
            col = xc.getAwtColor();
        }
        else
        {
            col = Color.GRAY;
        }

        if ( value.isConnected() )
        {
            String clientIP = value.getClientPeer().getIPPort();
            String ip = " [" + clientIP + "]";
            label += ip;
        }

        res.setText( label );
        res.setForeground( col );

        if ( isSelected )
            res.setBackground( Color.GRAY );
        else
            res.setBackground( Color.LIGHT_GRAY );

        res.setOpaque( true );

        return res;
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        // called from code that is potentially not in the EDT...

        if ( SwingUtilities.isEventDispatchThread() )
            processModelChange( change );
        else
            //            try
            //            {
            //                                SwingUtilities.invokeAndWait( new Runnable()
            //                {
            //                    @Override
            //                    public void run()
            //                    {
            //                        processModelChange( change );
            //                    }
            //                } );
            //            }
            //            catch( Exception e )
            //            {
            //                e.printStackTrace();
            //            }
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
        model.getPlayerCountModel().addObserver( this );
        model.getClientInfosModel().addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        if ( model != null )
        {
            model.getPlayerCountModel().removeObserver( this );
            model.getClientInfosModel().removeObserver( this );
        }
    }

    // MVCController interface

    @Override
    public GamePlayersPanelView getView()
    {
        return view;
    }

    @Override
    public ServerModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( ServerModel m )
    {
        unsubscribeModel();
        model = null;

        //        if ( m instanceof ServerModel )
        //        {
        //            model = (ServerModel)m;
        model = m;
        playerCountController.setModel( new IntSpinnerModel( model.getPlayerCountModel() ) );
        subscribeModel();
        //        }

        modelToUI();
    }

    private void close_edt()
    {
        playerCountController.close();
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
