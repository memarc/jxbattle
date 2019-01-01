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

package org.generic.mvc.model.parameter;

import org.generic.bean.parameter.FloatParameter;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public class FloatParameterModel extends MVCModelImpl
{
    protected FloatParameter floatParameter;

    public FloatParameterModel( FloatParameter v )
    {
        floatParameter = v;
    }

    public FloatParameter getFloatParameter()
    {
        return floatParameter;
    }

    public void setFloatParameter( Object sender, FloatParameter ip )
    {
        boolean changed = (getFloatParameter() == null && ip != null) || (getFloatParameter().getValue() != ip.getValue());
        floatParameter = ip;
        if ( changed )
            notifyObservers( new MVCModelChange( sender, this, ParameterModelChangeId.FloatParameterChanged ) );
    }

    public float getValue()
    {
        return getFloatParameter().getValue();
    }

    public float getMin()
    {
        return getFloatParameter().getMin();
    }

    public float getMax()
    {
        return getFloatParameter().getMax();
    }

    public void setValue( Object sender, float v )
    {
        boolean changed = getFloatParameter().getValue() != v;
        if ( changed )
        {
            getFloatParameter().setValue( v );
            notifyObservers( new MVCModelChange( sender, this, ParameterModelChangeId.FloatParameterChanged ) );
        }
    }

    public void setDelta( Object sender, float delta )
    {
        setValue( sender, getFloatParameter().getValue() + delta );
    }

    public void resetToDefault( Object sender )
    {
        setValue( sender, getDefaultValue() );
    }

    public float getDefaultValue()
    {
        return getFloatParameter().getDefaultValue();
    }

    public boolean equals( FloatParameterModel ipm )
    {
        return floatParameter.equals( ipm.floatParameter );
    }

    @Override
    public String toString()
    {
        return getFloatParameter().toString();
    }
}
