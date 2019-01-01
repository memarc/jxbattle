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

package jxbattle.gui.base.client.clientstatuspanel;

import java.awt.Color;

import javax.swing.SwingUtilities;

import jxbattle.model.client.PlayerStatisticsModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.gui.GuiUtils;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class ClientStatusPanelController implements MVCController<ClientStatusPanelModel, ClientStatusPanelView>, MVCModelObserver
{
    private ClientStatusPanelView view;

    private ClientStatusPanelModel model;

    private Color backgroundColor = Color.LIGHT_GRAY;

    public ClientStatusPanelController()
    {
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
        view = new ClientStatusPanelView();
    }

    private void m2uiPlayerName()
    {
        if ( view != null )
        {
            // name

            String name = model.getPlayerInfoModel().getPlayerName();
            Color color = model.getPlayerInfoModel().getPlayerColor().getAwtColor();
            String s = GuiUtils.getColorLabel( name, color, backgroundColor );

            view.getLblPlayerName().setText( s );
        }
    }

    private void m2uiPlayerStatus()
    {
        if ( view != null )
        {
            switch ( model.getPlayerInfoModel().getPlayerState() )
            {
                case PlayingGame:
                    view.getLblPlayerStatus().setText( "Playing" );
                    break;

                case WatchingGame:
                    view.getLblPlayerStatus().setText( "Watching" );
                    break;

                case LeftGame:
                    view.getLblPlayerStatus().setText( "Quit" );
                    break;

                case LostGame:
                    view.getLblPlayerStatus().setText( "Lost" );
                    break;

                case WonGame:
                    view.getLblPlayerStatus().setText( "Wins" );
                    break;

                default:
                    view.getLblPlayerStatus().setText( "---" );
                    break;
            }
        }
    }

    private void m2uiPlayerCoverage()
    {
        if ( view != null )
        {
            PlayerStatisticsModel psm = model.getPlayerStatisticsModel();
            if ( psm != null )
                view.getLblOccupyPercent().setText( " " + String.valueOf( (int)psm.getCoveragePercent() ) + "%" );
            else
                view.getLblOccupyPercent().setText( " --" );
        }
    }

    private void processModelChange( MVCModelChange change )
    {
        switch ( (XBMVCModelChangeId)change.getChangeId() )
        {
            case PlayerStateChanged:
                m2uiPlayerStatus();
                break;

            case PlayerNameChanged:
                m2uiPlayerName();
                break;

            case CoveragePercentChanged:
                m2uiPlayerCoverage();
                break;

            default:
                break;
        }
    }

    private void modelToUI_edt()
    {
        m2uiPlayerStatus();
        m2uiPlayerName();
        m2uiPlayerCoverage();
        if ( model.isClientPlayer() )
            view.setClientBorder( model.getPlayerInfoModel().getPlayerColor().getAwtColor() );
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

    // MVCModelObserver interface

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

    private void close_edt()
    {
        // this method has to be executed in the EDT
        // since others methods access 'view' in the EDT
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
    public ClientStatusPanelView getView()
    {
        return view;
    }

    @Override
    public ClientStatusPanelModel getModel()
    {
        return null;
    }

    @Override
    public void setModel( ClientStatusPanelModel m )
    {
        unsubscribeModel();
        //        model = null;
        //
        //        if ( m instanceof ClientStatusPanelModel )
        //        {
        //            model = (ClientStatusPanelModel)m;
        model = m;
        subscribeModel();
        //        }

        modelToUI();
    }
}
