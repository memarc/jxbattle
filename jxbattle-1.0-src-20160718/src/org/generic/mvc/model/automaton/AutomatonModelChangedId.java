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

package org.generic.mvc.model.automaton;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum AutomatonModelChangedId implements MVCModelChangeId
{
    /**
     * state of automaton changed
     */
    AutomatonStateChanged,

    //
    ;

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public AutomatonModelChangedId[] getValues()
    {
        return values();
    }

    @Override
    public AutomatonModelChangedId getValueOf( int val )
    {
        for ( AutomatonModelChangedId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for AutomatonModelChangedId enum " + val );
    }

    @Override
    public AutomatonModelChangedId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }

}
