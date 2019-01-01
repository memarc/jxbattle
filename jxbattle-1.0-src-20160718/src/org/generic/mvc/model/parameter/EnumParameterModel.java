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

import org.generic.EnumValue;
import org.generic.bean.parameter.EnumParameter;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public class EnumParameterModel<T extends EnumValue> extends MVCModelImpl
{
    // observed beans

    private EnumParameter<T> enumParameter;

    // models

    public EnumParameterModel( EnumParameter<T> v )
    {
        enumParameter = v;
    }

    public EnumParameter<T> getEnumParameter()
    {
        return enumParameter;
    }

    public void setEnumParameter( Object sender, EnumParameter<T> ep )
    {
        boolean changed = (getEnumParameter() == null && ep != null) || (getEnumParameter().getValue() != ep.getValue());
        enumParameter = ep;
        if ( changed )
            notifyObservers( new MVCModelChange( sender, this, ParameterModelChangeId.EnumParameterChanged ) );
    }

    public T getValue()
    {
        return getEnumParameter().getValue();
    }

    public void setValue( Object sender, T v )
    {
        boolean changed = getEnumParameter().getValue() != v;
        if ( changed )
        {
            getEnumParameter().setValue( v );
            notifyObservers( new MVCModelChange( sender, this, ParameterModelChangeId.EnumParameterChanged ) );
        }
    }

    public void resetToDefault( Object sender )
    {
        setValue( sender, getDefaultValue() );
    }

    public T getDefaultValue()
    {
        return getEnumParameter().getDefaultValue();
    }

    public T getValueOf( String s )
    {
        return getEnumParameter().getValueOf( s );
    }
}
