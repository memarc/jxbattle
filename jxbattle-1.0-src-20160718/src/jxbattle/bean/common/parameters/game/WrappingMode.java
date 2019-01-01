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

package jxbattle.bean.common.parameters.game;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;

/**
 * wrapping mode, ie. how left/right/top/down cells are connected 
 */
public enum WrappingMode implements EnumValue
{
    /**
     * no wrapping        
     */
    NONE,

    /**
     * horizontal wrapping
     */
    LEFT_RIGHT,

    /**
     * vertical wrapping
     */
    TOP_DOWN,

    /**
     * full (horizontal+vertical) wrapping
     */
    FULL;

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public WrappingMode[] getValues()
    {
        return values();
    }

    @Override
    public WrappingMode getValueOf( int val )
    {
        for ( WrappingMode im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for WrappingMode enum " + val );
    }

    @Override
    public EnumValue getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
