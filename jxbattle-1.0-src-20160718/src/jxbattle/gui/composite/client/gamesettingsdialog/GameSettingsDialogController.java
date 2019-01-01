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

package jxbattle.gui.composite.client.gamesettingsdialog;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jxbattle.gui.base.common.gamesettingspanel.GameSettingsPanelController;
import jxbattle.gui.base.common.gamesettingspanel.GameSettingsPanelModel;

import org.generic.gui.GuiUtils;
import org.generic.mvc.gui.MVCController;

//public class GameSettingsDialogController implements MVCController<GameParametersProfileModel, GameSettingsDialogView>
public class GameSettingsDialogController implements MVCController<GameSettingsPanelModel, GameSettingsDialogView>
{
    private GameSettingsDialogView view;

    //    private GameParametersProfileModel model;

    private GameSettingsPanelController gameSettingsPanelController;

    public GameSettingsDialogController( Window parentWindow )
    {
        init( parentWindow );
    }

    private void init( Window parentWindow )
    {
        view = new GameSettingsDialogView( parentWindow );

        view.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
        GuiUtils.centerWindow( view, parentWindow );

        gameSettingsPanelController = new GameSettingsPanelController( view.getGameSettingsPanelView() );
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
    public GameSettingsDialogView getView()
    {
        return view;
    }

    @Override
    public GameSettingsPanelModel getModel()
    {
        return null;
    }

    @Override
    public void setModel( GameSettingsPanelModel m )
    {
        //        model = null;
        //        if ( m instanceof GameParametersProfileModel )
        //        {
        //            model = (GameParametersProfileModel)m;
        //        model = m;
        //        gameSettingsPanelController.setModel( new GameSettingsPanelModel( m, true ) );
        gameSettingsPanelController.setModel( m );
        //        }
    }

    private void close_edt()
    {
        gameSettingsPanelController.close();
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
