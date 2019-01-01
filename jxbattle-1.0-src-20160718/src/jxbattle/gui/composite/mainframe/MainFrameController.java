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

package jxbattle.gui.composite.mainframe;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jxbattle.gui.composite.mainpanel.MainPanelController;
import jxbattle.model.MainModel;

import org.generic.mvc.gui.MVCController;

public class MainFrameController implements MVCController<MainFrameModel, MainFrameView>
{
    private MainFrameView view;

    private MainFrameModel model;

    private MainPanelController mainPanelController;

    public MainFrameController()
    {
        init();
    }

    private void init()
    {
        view = new MainFrameView();

        view.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
        createHandlers();

        mainPanelController = new MainPanelController( view.getMainPanelView(), view );
    }

    private void createHandlers()
    {
        view.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent w )
            {
                //close();
                if ( confirmQuit() )
                    model.shutdown();
            }
        } );
    }

    public void run()
    {
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                if ( MainModel.iconifyGameFrame )
                    view.setState( Frame.ICONIFIED );
                view.setVisible( true );
                if ( MainModel.gameFrameToBack )
                    view.toBack();
            }
        } );
    }

    // MVCController interface

    @Override
    public MainFrameView getView()
    {
        return view;
    }

    @Override
    public MainFrameModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( MainFrameModel m )
    {
        //        if ( m instanceof MainFrameModel )
        //        {
        //            model = (MainFrameModel)m;
        model = m;
        mainPanelController.setModel( model.getMainPanelModel() );
        //        }
    }

    private boolean confirmQuit()
    {
        if ( MainModel.fastPlay )
            return true;

        Object[] options = { " Yes ", "  No  " };
        return JOptionPane.showOptionDialog( view, "Quit jXBattle ?", "Confirm close", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[ 1 ] ) == JOptionPane.YES_OPTION;
    }

    private void close_edt()
    {
        //if ( confirmQuit() )
        {
            mainPanelController.close();
            //model.shutdown();
            if ( view != null )
            {
                view.dispose();
                view = null;
            }
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
