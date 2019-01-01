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

package jxbattle.gui.composite.server.serversettingsdialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jxbattle.gui.base.server.serversettingspanel.ServerSettingsPanelController;
import jxbattle.gui.base.server.serversettingspanel.ServerSettingsPanelModel;
import jxbattle.model.server.ServerEngine;

import org.generic.gui.GuiUtils;
import org.generic.mvc.gui.MVCController;

public class ServerSettingsDialogController implements MVCController<ServerEngine, ServerSettingsDialogView>
{
    private ServerSettingsDialogView view;

    private ServerEngine model;

    private ServerSettingsPanelController serverSettingsPanelController;

    public ServerSettingsDialogController( Window parentWindow )
    {
        init( parentWindow );
    }

    private void init( Window parentWindow )
    {
        view = new ServerSettingsDialogView( parentWindow );

        GuiUtils.centerWindow( view, parentWindow );

        view.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
        serverSettingsPanelController = new ServerSettingsPanelController( view.getServerSettingsPanelView() );

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
    public ServerSettingsDialogView getView()
    {
        return view;
    }

    @Override
    public ServerEngine getModel()
    {
        return model;
    }

    @Override
    public void setModel( ServerEngine m )
    {
        //        model = null;
        //
        //        if ( m instanceof ServerEngine )
        //        {
        //            model = (ServerEngine)m;
        model = m;
        serverSettingsPanelController.setModel( new ServerSettingsPanelModel( model.getSystemParametersModel(), model ) );
        //        }
    }

    private void close_edt()
    {
        serverSettingsPanelController.close();
        if ( view != null )
        {
            view.dispose();
            view = null;
        }
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
