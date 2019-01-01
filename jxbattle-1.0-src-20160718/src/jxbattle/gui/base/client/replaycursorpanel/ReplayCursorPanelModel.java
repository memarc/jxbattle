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

import org.generic.bean.parameter.IntParameter;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.parameter.IntParameterModel;

public abstract class ReplayCursorPanelModel extends MVCModelImpl
{
    protected IntParameterModel bigStepModel;

    public ReplayCursorPanelModel()
    {
        IntParameter bigStep = new IntParameter( 1, 10000, 50 );
        bigStepModel = new IntParameterModel( bigStep );
    }

    IntParameterModel getBigStepModel()
    {
        return bigStepModel;
    }

    void requestFirstStep( Object sender )
    {
        notifyObservers( new MVCModelChange( sender, this, ReplayCursorPanelModelChangeId.RequestFirstStep ) );
    }

    void requestBigPreviousStep( Object sender )
    {
        notifyObservers( new MVCModelChange( sender, this, ReplayCursorPanelModelChangeId.RequestPreviousBigStep ) );
    }

    void requestPreviousStep( Object sender )
    {
        notifyObservers( new MVCModelChange( sender, this, ReplayCursorPanelModelChangeId.RequestPreviousStep ) );
    }

    void requestNextStep( Object sender )
    {
        notifyObservers( new MVCModelChange( sender, this, ReplayCursorPanelModelChangeId.RequestNextStep ) );
    }

    void requestBigNextStep( Object sender )
    {
        notifyObservers( new MVCModelChange( sender, this, ReplayCursorPanelModelChangeId.RequestNextBigStep ) );
    }

    void requestLastStep( Object sender )
    {
        notifyObservers( new MVCModelChange( sender, this, ReplayCursorPanelModelChangeId.RequestLastStep ) );
    }

    protected void notifyStepChanged( Object sender )
    {
        notifyObservers( new MVCModelChange( sender, this, ReplayCursorPanelModelChangeId.StepChanged ) );
    }

    protected abstract int getCurrentStep();

    protected abstract int getStepCount();

    boolean isFirstStep()
    {
        return getCurrentStep() == 0;
    }

    boolean isLastStep()
    {
        return getCurrentStep() == getStepCount() - 1;
    }

    public void setBigStep( Object sender, int bs )
    {
        bigStepModel.setValue( sender, bs );
    }
}
