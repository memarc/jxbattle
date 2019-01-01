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

/**
 * boolean parameter (with current and default value)
 */
public class BoolParameter implements Cloneable
{
    private boolean value;

    private boolean defaultValue;

    public BoolParameter( boolean val, boolean defaultVal )
    {
        value = val;
        defaultValue = defaultVal;
    }

    public BoolParameter( BoolParameter bp )
    {
        value = bp.value;
        defaultValue = bp.defaultValue;
    }

    public boolean getValue()
    {
        return value;
    }

    public void setValue( boolean val )
    {
        value = val;
    }

    public void resetToDefault()
    {
        setValue( getDefaultValue() );
    }

    public boolean getDefaultValue()
    {
        return defaultValue;
    }

    // Object interface

    @Override
    public String toString()
    {
        return String.valueOf( getValue() );
    }

    @Override
    public BoolParameter clone()
    {
        //        try
        //        {
        //            BoolParameter res = (BoolParameter)super.clone();
        //            return res;
        //        }
        //        catch( CloneNotSupportedException e )
        //        {
        //            e.printStackTrace();
        //            return null;
        //        }
        return new BoolParameter( this );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (defaultValue ? 1231 : 1237);
        result = prime * result + (value ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        BoolParameter other = (BoolParameter)obj;
        if ( defaultValue != other.defaultValue )
            return false;
        if ( value != other.value )
            return false;
        return true;
    }
}
