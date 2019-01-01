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

package jxbattle.model.common.parameters;

import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.bean.parameter.LongParameter;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public class LongParameterModel extends MVCModelImpl
{
    // observed beans

    private LongParameter longParameter;

    // models

    public LongParameterModel( LongParameter v )
    {
        longParameter = v;
    }

    public LongParameter getLongParameter()
    {
        return longParameter;
    }

    public void setLongParameter( Object sender, LongParameter lp )
    {
        boolean changed = (getLongParameter() == null && lp != null) || (getLongParameter().getValue() != lp.getValue());
        longParameter = lp;
        if ( changed )
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.LongParameterChanged ) );
    }

    public long getValue()
    {
        return getLongParameter().getValue();
    }

    public long getMin()
    {
        return getLongParameter().getMin();
    }

    public long getMax()
    {
        return getLongParameter().getMax();
    }

    public void setValue( Object sender, long v )
    {
        boolean changed = getLongParameter().getValue() != v;
        if ( changed )
        {
            getLongParameter().setValue( v );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.LongParameterChanged ) );
        }
    }

    public void resetToDefault( Object sender )
    {
        setValue( sender, getDefaultValue() );
    }

    public long getDefaultValue()
    {
        return getLongParameter().getDefaultValue();
    }
}
