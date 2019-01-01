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

package jxbattle.gui.base.client.replaycursorpanel;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum ReplayCursorPanelModelChangeId implements MVCModelChangeId
{
    RequestPreviousStep, RequestNextStep, RequestPreviousBigStep, RequestNextBigStep, RequestFirstStep, RequestLastStep, StepChanged,

    ;

    // MVCModelChangeId interface

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public ReplayCursorPanelModelChangeId[] getValues()
    {
        return values();
    }

    @Override
    public ReplayCursorPanelModelChangeId getValueOf( int val )
    {
        for ( ReplayCursorPanelModelChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for ReplayCursorPanelModelChangeId enum " + val );
    }

    @Override
    public ReplayCursorPanelModelChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}