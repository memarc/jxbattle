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
 * long integer parameter (with min/max/current and default value)
 */
public class LongParameter implements Cloneable
{
    private long min;

    private long max;

    private long value;

    private long defaultValue;

    public LongParameter( long minVal, long maxVal, long defaultVal )
    {
        min = minVal;
        max = maxVal;
        defaultValue = defaultVal;
        value = defaultValue;
    }

    public LongParameter( LongParameter lp )
    {
        min = lp.min;
        max = lp.max;
        value = lp.value;
        defaultValue = lp.defaultValue;
    }

    @Override
    public LongParameter clone()
    {
        //        try
        //        {
        //            LongParameter res = (LongParameter)super.clone();
        //            return res;
        //        }
        //        catch( CloneNotSupportedException e )
        //        {
        //            e.printStackTrace();
        //            return null;
        //        }
        return new LongParameter( this );
    }

    public long getMin()
    {
        return min;
    }

    public long getMax()
    {
        return max;
    }

    public long getValue()
    {
        return value;
    }

    public void setValue( long val )
    {
        value = val;

        if ( value < min )
            value = min;

        if ( value > max )
            value = max;
    }

    public void resetToDefault()
    {
        setValue( getDefaultValue() );
    }

    public long getDefaultValue()
    {
        return defaultValue;
    }

    @Override
    public String toString()
    {
        return String.valueOf( value );
    }
}
