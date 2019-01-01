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

package jxbattle.gui.base.common.menupanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jxbattle.common.Consts;
import jxbattle.gui.composite.client.replayframe.ReplayFrameController;
import jxbattle.gui.composite.client.replayframe.ReplayFrameModel;
import jxbattle.model.MainModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.gui.Desktop;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class MenuPanelController implements MVCController<MenuPanelModel, MenuPanelView>, MVCModelObserver
{
    private MenuPanelModel model;

    private MenuPanelView view;

    private ReplayFrameController replayFrameController;

    public MenuPanelController( MenuPanelView v )
    {
        view = v;
        init();
    }

    private void init()
    {
        createHandlers();
    }

    private void createHandlers()
    {
        view.getBtnPlayGame().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onPlayGameButton( MenuPanelController.this );
            }
        } );

        view.getBtnCreateServer().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onCreateServerButton( MenuPanelController.this );
            }
        } );

        view.getBtnReplayGame().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onReplayGameButton();
            }
        } );

        view.getBtnWebSite().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                onBrowseWebSite();
            }
        } );
    }

    private void onPlayGameButton( Object sender )
    {
        model.openClient( sender );
    }

    private void onCreateServerButton( Object sender )
    {
        model.openServer( sender );
    }

    private void onReplayGameButton()
    {
        replayFrameController = new ReplayFrameController( view );
        String filename = replayFrameController.chooseFile();
        if ( filename != null )
        {
            try
            {
                replayFrameController.setModel( new ReplayFrameModel( filename ) );
                replayFrameController.run();
            }
            catch( Exception e )
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog( view, e.getStackTrace() );
            }
        }
    }

    private void onBrowseWebSite()
    {
        model.browseWebSite( this );
    }

    //    private void doBrowseWebSite()
    //    {
    //        try
    //        {
    //            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
    //
    //            if ( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) )
    //                throw new Exception( "Desktop doesn't support the browse action.\nPlease open a web browser at " + Consts.webSiteUrl );
    //
    //            java.net.URI uri = new java.net.URI( Consts.webSiteUrl );
    //            desktop.browse( uri );
    //        }
    //        catch( Exception e )
    //        {
    //            JOptionPane.showMessageDialog( view, "Error opening url\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
    //        }
    //    }

    private void doBrowseWebSite()
    {
        try
        {
            java.net.URI uri = new java.net.URI( Consts.webSiteUrl );
            if ( !Desktop.browse( uri ) )
                throw new Exception( "Desktop doesn't support the browse action.\nPlease open a web browser at " + Consts.webSiteUrl );
        }
        catch( Exception e )
        {
            JOptionPane.showMessageDialog( view, "Error opening url\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE );
        }
    }

    private void processModelChange( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof XBMVCModelChangeId )
            switch ( (XBMVCModelChangeId)change.getChangeId() )
            {
                case ClientEngineStart:
                    if ( !MainModel.fastPlay )
                        view.getBtnPlayGame().setEnabled( false );
                    break;

                case ServerEngineStart:
                    view.getBtnCreateServer().setEnabled( false );
                    break;

                case BrowseWebSite:
                    doBrowseWebSite();
                    break;

                default:
                    break;
            }
    }

    // MVCController interface

    @Override
    public MenuPanelView getView()
    {
        return view;
    }

    @Override
    public MenuPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( MenuPanelModel m )
    {
        unsubscribeModel();
        //        model = null;
        //
        //        if ( m instanceof MenuPanelModel )
        //        {
        //            model = (MenuPanelModel)m;
        model = m;
        subscribeModel();
        //        }
    }

    private void close_edt()
    {
        unsubscribeModel();

        if ( replayFrameController != null )
        {
            replayFrameController.close();
            replayFrameController = null;
        }

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
