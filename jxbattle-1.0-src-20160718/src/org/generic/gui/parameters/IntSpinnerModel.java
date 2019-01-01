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

import org.generic.bean.parameter.IntParameter;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.mvc.model.parameter.IntParameterModel;

public class IntSpinnerModel extends MVCModelImpl
{
    private IntParameterModel intParameterModel;

    private int displayedMinValue;

    private int displayedMaxValue;

    private boolean doScale;

    public IntSpinnerModel( IntParameterModel ipm )
    {
        intParameterModel = ipm;
        displayedMinValue = ipm.getMin();
        displayedMaxValue = ipm.getMax();
        doScale = false;
    }

    public int getDisplayedMinValue()
    {
        return displayedMinValue;
    }

    public void setDisplayedMinValue( int min )
    {
        doScale = doScale || (displayedMinValue != min);
        displayedMinValue = min;
    }

    int getDisplayedMaxValue()
    {
        return displayedMaxValue;
    }

    public void setDisplayedMaxValue( int max )
    {
        doScale = doScale || (displayedMaxValue != max);
        displayedMaxValue = max;
    }

    public int getValue()
    {
        if ( doScale )
            return (int)intParameterModel.getIntParameter().scaleValue( displayedMinValue, displayedMaxValue );

        return intParameterModel.getValue();
    }

    private int convertFromDisplayed( int displayedValue )
    {
        return IntParameter.scaleValue( displayedValue, displayedMinValue, displayedMaxValue, intParameterModel.getMin(), intParameterModel.getMax() );
    }

    private int convertDeltaFromDisplayed( int displayedDelta )
    {
        return IntParameter.scaleDelta( displayedDelta, displayedMinValue, displayedMaxValue, intParameterModel.getMin(), intParameterModel.getMax() );
    }

    void setValue( Object sender, int displayedValue )
    {
        if ( doScale )
            intParameterModel.setValue( sender, convertFromDisplayed( displayedValue ) );
        else
            intParameterModel.setValue( sender, displayedValue );
    }

    void setDelta( Object sender, int delta )
    {
        if ( doScale )
            intParameterModel.setDelta( sender, convertDeltaFromDisplayed( delta ) );
        else
            intParameterModel.setDelta( sender, delta );
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        intParameterModel.addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        intParameterModel.removeObserver( obs );
    }
}
