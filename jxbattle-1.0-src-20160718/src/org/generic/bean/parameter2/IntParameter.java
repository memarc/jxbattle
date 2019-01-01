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

package org.generic.bean.parameter2;

public class IntParameter extends NumericParameter<Integer>
{
    public IntParameter( int defaultVal )
    {
        super( defaultVal );
    }

    public IntParameter( IntParameter ip )
    {
        super( ip );
    }

    public void resetToDefault()
    {
        setValue( getDefaultValue() );
    }

    //    public float scaleValue( int newMin, int newMax )
    //    {
    //        return scaleValue( value, min, max, newMin, newMax );
    //    }
    //    public float scaleValue( float newMin, float newMax )
    //    {
    //        return scaleValue( value, min, max, newMin, newMax );
    //    }

    // Object interface

    @Override
    public IntParameter clone()
    {
        return new IntParameter( this );
    }
}
