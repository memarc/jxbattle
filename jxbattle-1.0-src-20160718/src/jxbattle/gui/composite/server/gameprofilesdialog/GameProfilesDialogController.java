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

package jxbattle.gui.composite.server.gameprofilesdialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jxbattle.gui.composite.server.gameprofilespanel.GameProfilesPanelController;
import jxbattle.model.MainModel;
import jxbattle.model.server.GameParametersProfilesModel;

import org.generic.gui.GuiUtils;
import org.generic.mvc.gui.MVCController;

public class GameProfilesDialogController implements MVCController<GameParametersProfilesModel, GameProfilesDialogView>
{
    private GameProfilesDialogView view;

    //    private ServerEngine model;

    private GameProfilesPanelController gameProfilesPanelController;

    public GameProfilesDialogController( Window parentWindow )
    {
        init( parentWindow );
    }

    private void init( Window parentWindow )
    {
        view = new GameProfilesDialogView( parentWindow );

        view.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
        GuiUtils.centerWindow( view, parentWindow );

        gameProfilesPanelController = new GameProfilesPanelController( view.getGameProfilesPanelView() );
        createHandlers();
    }

    private void createHandlers()
    {
        view.getBtnClose().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                close();
            }
        } );

        view.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent e )
            {
                close();
            }
        } );
    }

    public void runDialog()
    {
        if ( SwingUtilities.isEventDispatchThread() )
            view.setVisible( true );
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    view.setVisible( true );
                }
            } );
    }

    @Override
    public GameProfilesDialogView getView()
    {
        return view;
    }

    @Override
    public GameParametersProfilesModel getModel()
    {
        return null;
    }

    @Override
    public void setModel( GameParametersProfilesModel m )
    {
        //        model = null;
        //        if ( m instanceof ServerEngine )
        //        {
        //            model = (ServerEngine)m;
        //            gameProfilesPanelController.setModel( model.getGameParametersProfilesModel() );
        //        }

        gameProfilesPanelController.setModel( m );
    }

    private void close_edt()
    {
        gameProfilesPanelController.close();
        if ( view != null )
        {
            view.dispose();
            view = null;
        }
    }

    @Override
    public void close()
    {
        //model.saveConfig();
        MainModel.getInstance().saveServerConfig();

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
