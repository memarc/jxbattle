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
 * float parameter (with min/max/current and default value)
 */
public class FloatParameter implements Cloneable
{
    private float min;

    private float max;

    private float value;

    private float defaultValue;

    public FloatParameter( float minVal, float maxVal, float defaultVal )
    {
        min = minVal;
        max = maxVal;
        value = defaultVal;
        defaultValue = defaultVal;
    }

    public FloatParameter( float minVal, float maxVal )
    {
        min = minVal;
        max = maxVal;
        value = min;
        defaultValue = min;
    }

    public FloatParameter( FloatParameter fp )
    {
        min = fp.min;
        max = fp.max;
        value = fp.value;
        defaultValue = fp.defaultValue;
    }

    public float getDefaultValue()
    {
        return defaultValue;
    }

    public float getMin()
    {
        return min;
    }

    public float getMax()
    {
        return max;
    }

    public float getValue()
    {
        return value;
    }

    public void setValue( float val )
    {
        value = val;

        if ( value < min )
            value = min;

        if ( value > max )
            value = max;
    }

    public void setValue( Float val )
    {
        setValue( val.floatValue() );
    }

    public void resetToDefault()
    {
        setValue( getDefaultValue() );
    }

    public float scaleValue( float newMin, float newMax )
    {
        return scaleValue( value, min, max, newMin, newMax );
    }

    public static float scaleValue( float value, float oldMin, float oldMax, float newMin, float newMax )
    {
        double d = (double)(value - oldMin) / (oldMax - oldMin);
        return (float)(newMin + d * (newMax - newMin));
    }

    public static float scaleDelta( float delta, float oldMin, float oldMax, float newMin, float newMax )
    {
        double d = (double)delta / (oldMax - oldMin);
        return (float)(d * (newMax - newMin));
    }

    // Object interface

    @Override
    public FloatParameter clone()
    {
        //        try
        //        {
        //            FloatParameter res = (FloatParameter)super.clone();
        //            return res;
        //        }
        //        catch( CloneNotSupportedException e )
        //        {
        //            e.printStackTrace();
        //            return null;
        //        }
        return new FloatParameter( this );
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
        FloatParameter other = (FloatParameter)obj;
        if ( value != other.value )
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return String.valueOf( value );
    }
}
