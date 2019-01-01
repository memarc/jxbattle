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

package jxbattle.gui.base.client.keysetuppanel;

import javax.swing.SwingUtilities;

import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class KeySetupPanelController implements MVCController<KeySetupPanelModel, KeySetupPanelView>, MVCModelObserver
{
    private KeySetupPanelView view;

    private KeySetupPanelModel model;

    public KeySetupPanelController( KeySetupPanelView v )
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
    }

    private void updateUI()
    {
        view.revalidate();
        view.repaint();
    }

    private void modelToUI_edt()
    {
        view.getBtn().setText( model.getLabel() );
        view.getLabel().setText( model.getCode().toString() );
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

    private void processModelChange( MVCModelChange change )
    {
    }

    // MVCController interface

    @Override
    public KeySetupPanelView getView()
    {
        return view;
    }

    @Override
    public KeySetupPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( KeySetupPanelModel m )
    {
        unsubscribeModel();
        model = m;
        subscribeModel();

        modelToUI();
    }

    @Override
    public void close()
    {
        unsubscribeModel();
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        if ( SwingUtilities.isEventDispatchThread() )
            processModelChange( change );
        else
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
