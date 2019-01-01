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

package jxbattle.bean.client.input;

import java.awt.event.InputEvent;

public class XBKeyEvent extends XBKeyInfo
{
    private boolean pressedOrReleased;

    public XBKeyEvent( int code, int modifier, boolean pressed )
    {
        super( code, (modifier & InputEvent.CTRL_MASK) != 0, (modifier & InputEvent.SHIFT_MASK) != 0 );
        pressedOrReleased = pressed;
    }

    public boolean isPressedOrReleased()
    {
        return pressedOrReleased;
    }

    public void setPressedOrReleased( boolean b )
    {
        pressedOrReleased = b;
    }

    //    @Override
    //    public boolean equals( Object obj )
    //    {
    //        if ( this == obj )
    //            return true;
    //        if ( obj == null )
    //            return false;
    //        //        if ( getClass() != obj.getClass() )
    //        if ( !(obj instanceof XBKeyInfo) )
    //            return false;
    //        XBKeyInfo other = (XBKeyInfo)obj;
    //        if ( ctrl != other.ctrl )
    //            return false;
    //        if ( keyCode != other.keyCode )
    //            return false;
    //        if ( shift != other.shift )
    //            return false;
    //        return true;
    //    }
}
