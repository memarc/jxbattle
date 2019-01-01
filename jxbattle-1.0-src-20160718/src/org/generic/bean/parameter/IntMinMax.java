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

import org.generic.mvc.model.MVCModelError;

public class IntMinMax implements Cloneable
{
    protected int min;

    protected int max;

    public IntMinMax( int minVal, int maxVal )
    {
        checkMinMax( minVal, maxVal );
        min = minVal;
        max = maxVal;
    }

    public IntMinMax( IntMinMax src )
    {
        min = src.min;
        max = src.max;
    }

    public int getMin()
    {
        return min;
    }

    public int getMax()
    {
        return max;
    }

    /**
     * make sure min always <= max
     */
    private static void checkMinMax( int mi, int ma )
    {
        if ( mi > ma )
            throw new MVCModelError( "error : min > max" );
    }

    public void checkValue( int v )
    {
        if ( v < min )
            throw new MVCModelError( "error : value < min" );

        if ( v > max )
            throw new MVCModelError( "error : value > max" );
    }

    public void setMin( int v )
    {
        checkMinMax( v, max );
        min = v;
    }

    public void setMax( int v )
    {
        checkMinMax( min, v );
        max = v;
    }

    public static int scaleValue( int value, int oldMin, int oldMax, int newMin, int newMax )
    {
        double d = (double)(value - oldMin) / (oldMax - oldMin);
        return (int)(newMin + d * (newMax - newMin));
    }

    public static float scaleValue( int value, int oldMin, int oldMax, float newMin, float newMax )
    {
        double d = (double)(value - oldMin) / (oldMax - oldMin);
        return (float)(newMin + d * (newMax - newMin));
    }

    public static int scaleDelta( int delta, int oldMin, int oldMax, int newMin, int newMax )
    {
        double d = (double)delta / (oldMax - oldMin);
        return (int)(d * (newMax - newMin));
    }

    // Object interface

    @Override
    public IntMinMax clone()
    {
        return new IntMinMax( this );
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
        IntMinMax other = (IntMinMax)obj;
        if ( max != other.max )
            return false;
        if ( min != other.min )
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + max;
        result = prime * result + min;
        return result;
    }

    @Override
    public String toString()
    {
        return "IntMinMax [min=" + min + ", max=" + max + "]";
    }
}
