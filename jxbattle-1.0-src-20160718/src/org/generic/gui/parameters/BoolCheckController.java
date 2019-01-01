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

package org.generic.gui.parameters;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.SwingUtilities;

import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.IBoolModel;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class BoolCheckController implements MVCController<IBoolModel, JCheckBox>, MVCModelObserver
{
    private JCheckBox view;

    //private BoolParameterModel model;
    private IBoolModel model;

    private ToggleButtonModel uiModel;

    public BoolCheckController( JCheckBox v )
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
        uiModel = new ToggleButtonModel();
        view.setModel( uiModel );

        // update model when value changes

        //        uiModel.addChangeListener( new ChangeListener()
        //        {
        //            @SuppressWarnings("unused")
        //            @Override
        //            public void stateChanged( ChangeEvent e )
        //            {
        //                uiToModel();
        //            }
        //        } );

        uiModel.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                uiToModel( BoolCheckController.this );
            }
        } );
    }

    private void updateUI()
    {
        view.revalidate();
        view.updateUI();
    }

    private void modelToUI_edt()
    {
        uiModel.setSelected( model.getValue() );
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

    private void uiToModel( Object sender )
    {
        boolean b = view.isSelected();
        model.setValue( sender, b );
    }

    // MVCController

    @Override
    public JCheckBox getView()
    {
        return view;
    }

    @Override
    public IBoolModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( IBoolModel m )
    {
        unsubscribeModel();
        //        model = null;
        //
        //        if ( m instanceof IBoolModel )
        //        {
        //            model = (IBoolModel)m;
        model = m;
        subscribeModel();
        //        }

        modelToUI();
    }

    private void close_edt()
    {
        unsubscribeModel();
        uiModel = null;
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

    // ModelObserver

    //    @SuppressWarnings("unused")
    @Override
    public void modelChanged( MVCModelChange change )
    {
        if ( change.getSender() != this )
            modelToUI();
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
