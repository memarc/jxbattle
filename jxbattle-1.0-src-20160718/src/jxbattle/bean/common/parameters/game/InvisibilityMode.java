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
 * invisibility mode, ie. how enemy/map is visible 
 */
public enum InvisibilityMode implements EnumValue
{
    /**
     * all is visible
     */
    NONE,

    /**
     * map is visible but no enemy as long as you don't get close to it
     */
    INVISIBLE_ENEMY,

    /**
     * map/enemy is discovered as you move
     */
    INVISIBLE_MAP;

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public InvisibilityMode[] getValues()
    {
        return values();
    }

    @Override
    public InvisibilityMode getValueOf( int val )
    {
        for ( InvisibilityMode im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for InvisibilityMode enum " + val );
    }

    @Override
    public InvisibilityMode getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
