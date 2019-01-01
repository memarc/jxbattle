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

public class XBMouseInfo implements XBInputInfo
{
    private boolean leftButton;

    private boolean middleButton;

    private boolean rightButton;

    private boolean shift;

    private boolean ctrl;

    public XBMouseInfo( int modifier )
    {
        leftButton = (modifier & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK;
        middleButton = (modifier & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK;
        rightButton = (modifier & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK;
        ctrl = (modifier & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK;
        shift = (modifier & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK;
    }

    public XBMouseInfo( boolean lb, boolean mb, boolean rb, boolean c, boolean s )
    {
        leftButton = lb;
        middleButton = mb;
        rightButton = rb;
        //        ctrl = (modif & 2) != 0;
        //        shift = (modif & 1) != 0;
        ctrl = c;
        shift = s;
    }

    public boolean isLeftButton()
    {
        return leftButton;
    }

    public boolean isMiddleButton()
    {
        return middleButton;
    }

    public boolean isRightButton()
    {
        return rightButton;
    }

    public boolean isShift()
    {
        return shift;
    }

    public boolean isCtrl()
    {
        return ctrl;
    }

    // Object interface

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (ctrl ? 1231 : 1237);
        result = prime * result + (leftButton ? 1231 : 1237);
        result = prime * result + (middleButton ? 1231 : 1237);
        result = prime * result + (rightButton ? 1231 : 1237);
        result = prime * result + (shift ? 1231 : 1237);
        return result;
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
        XBMouseInfo other = (XBMouseInfo)obj;
        if ( ctrl != other.ctrl )
            return false;
        if ( leftButton != other.leftButton )
            return false;
        if ( middleButton != other.middleButton )
            return false;
        if ( rightButton != other.rightButton )
            return false;
        if ( shift != other.shift )
            return false;
        return true;
    }
}
