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
 * integer parameter (with min/max/current and default value)
 */
public class IntParameter extends IntMinMax
{
    private int value;

    private int defaultValue;

    public IntParameter( int minVal, int maxVal, int defaultVal )
    {
        super( minVal, maxVal );
        value = defaultVal;
        defaultValue = defaultVal;
    }

    public IntParameter( int minVal, int maxVal )
    {
        super( minVal, maxVal );
        value = min;
        defaultValue = min;
    }

    public IntParameter( IntParameter src )
    {
        super( src.min, src.max );
        value = src.value;
        defaultValue = src.defaultValue;
    }

    public int getDefaultValue()
    {
        return defaultValue;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue( int val )
    {
        value = val;

        if ( value < min )
            value = min;

        if ( value > max )
            value = max;
    }

    public void setValue( Integer val )
    {
        setValue( val.intValue() );
    }

    public void resetToDefault()
    {
        setValue( getDefaultValue() );
    }

    public float scaleValue( int newMin, int newMax )
    {
        return scaleValue( value, min, max, newMin, newMax );
    }

    public float scaleValue( float newMin, float newMax )
    {
        return scaleValue( value, min, max, newMin, newMax );
    }

    // Object interface

    @Override
    public IntParameter clone()
    {
        return new IntParameter( this );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + defaultValue;
        result = prime * result + value;
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( !super.equals( obj ) )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        IntParameter other = (IntParameter)obj;
        if ( defaultValue != other.defaultValue )
            return false;
        if ( value != other.value )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return String.valueOf( value ) + " " + super.toString();
    }
}
