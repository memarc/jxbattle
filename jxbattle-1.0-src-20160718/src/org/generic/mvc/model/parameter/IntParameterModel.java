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

import org.generic.bean.parameter.IntParameter;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelChangeId;
import org.generic.mvc.model.observer.MVCModelImpl;

public class IntParameterModel extends MVCModelImpl
{
    protected IntParameter intParameter;

    public IntParameterModel( IntParameter v )
    {
        intParameter = v;
    }

    public IntParameter getIntParameter()
    {
        return intParameter;
    }

    public void setIntParameter( Object sender, IntParameter ip )
    {
        boolean changed = (getIntParameter() == null && ip != null) || (getIntParameter().getValue() != ip.getValue());
        intParameter = ip;
        if ( changed )
            notifyObservers( new MVCModelChange( sender, this, getValueChangeId() ) );
    }

    public int getValue()
    {
        return getIntParameter().getValue();
    }

    public int getMin()
    {
        return getIntParameter().getMin();
    }

    public int getMax()
    {
        return getIntParameter().getMax();
    }

    public void setValue( Object sender, int v )
    {
        boolean changed = getIntParameter().getValue() != v;
        if ( changed )
        {
            getIntParameter().setValue( v );
            notifyObservers( new MVCModelChange( sender, this, getValueChangeId() ) );
        }
    }

    public void setDelta( Object sender, int delta )
    {
        setValue( sender, getIntParameter().getValue() + delta );
    }

    public void resetToDefault( Object sender )
    {
        setValue( sender, getDefaultValue() );
    }

    public int getDefaultValue()
    {
        return getIntParameter().getDefaultValue();
    }

    public boolean equals( IntParameterModel ipm )
    {
        return intParameter.equals( ipm.intParameter );
    }

    protected MVCModelChangeId getValueChangeId()
    {
        return ParameterModelChangeId.IntParameterChanged;
    }

    // Object interface

    @Override
    public String toString()
    {
        return getIntParameter().toString();
    }
}
