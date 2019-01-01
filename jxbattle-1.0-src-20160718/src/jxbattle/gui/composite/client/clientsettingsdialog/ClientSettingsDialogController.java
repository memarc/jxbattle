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

package jxbattle.gui.composite.client.clientsettingsdialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jxbattle.gui.base.client.clientsettingspanel.ClientSettingsPanelController;
import jxbattle.gui.base.client.clientsettingspanel.ClientSettingsPanelModel;

import org.generic.gui.GuiUtils;
import org.generic.mvc.gui.MVCController;

public class ClientSettingsDialogController implements MVCController<ClientSettingsDialogModel, ClientSettingsDialogView>
{
    private ClientSettingsDialogModel model;

    private ClientSettingsDialogView view;

    private ClientSettingsPanelController clientSettingsPanelController;

    public ClientSettingsDialogController( Window parentWindow )
    {
        init( parentWindow );
    }

    private void init( Window parentWindow )
    {
        view = new ClientSettingsDialogView( parentWindow );

        GuiUtils.centerWindow( view, parentWindow );

        view.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
        clientSettingsPanelController = new ClientSettingsPanelController( view.getClientSettingsPanelView() );

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

    private void close_edt()
    {
        clientSettingsPanelController.close();
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

    @Override
    public ClientSettingsDialogView getView()
    {
        return view;
    }

    @Override
    public ClientSettingsDialogModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( ClientSettingsDialogModel m )
    {
        model = null;
        model = m;
        clientSettingsPanelController.setModel( new ClientSettingsPanelModel( model.getClientEngine() ) );
    }
}
