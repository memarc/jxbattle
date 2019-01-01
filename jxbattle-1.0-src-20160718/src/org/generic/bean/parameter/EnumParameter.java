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

package org.generic.bean.parameter;

import org.generic.EnumValue;

/**
 * enumeration parameter (with current and default value)
 */
public class EnumParameter<T extends EnumValue> implements Cloneable
{
    private T value;

    private T defaultValue;

    public EnumParameter( T defaultVal )
    {
        value = defaultVal;
        defaultValue = defaultVal;
    }

    public EnumParameter( EnumParameter<T> ep )
    {
        value = ep.value;
        defaultValue = ep.defaultValue;
    }

    @Override
    public EnumParameter<T> clone()
    {
        //        try
        //        {
        //            EnumParameter res = (EnumParameter)super.clone();
        //            return res;
        //        }
        //        catch( CloneNotSupportedException e )
        //        {
        //            e.printStackTrace();
        //            return null;
        //        }
        return new EnumParameter<T>( this );
    }

    public T getDefaultValue()
    {
        return defaultValue;
    }

    public T getValue()
    {
        return value;
    }

    public void setValue( T val )
    {
        value = val;
    }

    public void setValue( int val )
    {
        value = (T)value.getValueOf( val ); // bit weird but...
    }

    public T getValueOf( String s )
    {
        return (T)value.getValueOf( s ); // bit weird but...
    }

    public void resetToDefault()
    {
        setValue( getDefaultValue() );
    }

    @Override
    public String toString()
    {
        return String.valueOf( value );
    }
}
