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

package jxbattle.gui.base.client.replaycursorpanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

import org.generic.gui.parameters.IntSpinnerController;
import org.generic.gui.parameters.IntSpinnerModel;
import org.generic.mvc.gui.MVCController;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class ReplayCursorPanelController implements MVCController<ReplayCursorPanelModel, ReplayCursorPanelView>, MVCModelObserver
{
    private ReplayCursorPanelView view;

    private ReplayCursorPanelModel model;

    private IntSpinnerController stepSizeController;

    public ReplayCursorPanelController( ReplayCursorPanelView v )
    {
        view = v;
        init();
    }

    private void init()
    {
        stepSizeController = new IntSpinnerController( view.getSpinStepSize() );
        createHandlers();
    }

    private void createHandlers()
    {
        view.getBtnStart().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                btnStart();
            }
        } );

        view.getBtnBigPrevious().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                btnBigPrevious();
            }
        } );
        view.getBtnPrevious().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                btnPrevious();
            }
        } );

        view.getBtnNext().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                btnNext();
            }
        } );

        view.getBtnBigNext().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                btnBigNext();
            }
        } );

        view.getBtnEnd().addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                btnEnd();
            }
        } );
    }

    private void btnStart()
    {
        model.requestFirstStep( ReplayCursorPanelController.this );
    }

    private void btnBigPrevious()
    {
        model.requestBigPreviousStep( ReplayCursorPanelController.this );
    }

    private void btnPrevious()
    {
        model.requestPreviousStep( ReplayCursorPanelController.this );
    }

    private void btnNext()
    {
        model.requestNextStep( ReplayCursorPanelController.this );
    }

    private void btnBigNext()
    {
        model.requestBigNextStep( ReplayCursorPanelController.this );
    }

    private void btnEnd()
    {
        model.requestLastStep( ReplayCursorPanelController.this );
    }

    private void updateUI()
    {
        boolean start = model.isFirstStep();
        view.getBtnStart().setEnabled( !start );
        view.getBtnPrevious().setEnabled( !start );
        view.getBtnBigPrevious().setEnabled( !start );

        boolean end = model.isLastStep();
        view.getBtnEnd().setEnabled( !end );
        view.getBtnNext().setEnabled( !end );
        view.getBtnBigNext().setEnabled( !end );
    }

    // MVCModelObserver interface

    private void processMessages( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof ReplayCursorPanelModelChangeId )
            switch ( (ReplayCursorPanelModelChangeId)change.getChangeId() )
            {
                case StepChanged:
                    updateUI();
                    break;

                default:
                    break;
            }
    }

    @Override
    public void modelChanged( final MVCModelChange change )
    {
        if ( SwingUtilities.isEventDispatchThread() )
            processMessages( change );
        else
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    processMessages( change );
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
        stepSizeController.close();
        unsubscribeModel();
    }

    private void modelToUI_edt()
    {
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

    // MVCController interface

    @Override
    public ReplayCursorPanelView getView()
    {
        return view;
    }

    @Override
    public ReplayCursorPanelModel getModel()
    {
        return model;
    }

    @Override
    public void setModel( ReplayCursorPanelModel m )
    {
        unsubscribeModel();
        model = m;

        stepSizeController.setModel( new IntSpinnerModel( model.getBigStepModel() ) );

        subscribeModel();
        modelToUI();
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
