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

public class XBKeyInfo implements XBInputInfo
{
    protected int keyCode;

    protected boolean shift;

    protected boolean ctrl;

    public XBKeyInfo( String s )
    {
        String f[] = s.split( "-" );

        keyCode = Integer.valueOf( f[ 0 ] );
        //        pressedOrReleased = f[ 1 ].equals( "p" );
        ctrl = f[ 1 ].equals( "c" );
        shift = f[ 2 ].equals( "s" );
    }

    //    public XBKeyInfo( int code, boolean c, boolean s, boolean pressed )
    public XBKeyInfo( int code, boolean c, boolean s )
    {
        keyCode = code;
        ctrl = c;
        shift = s;
        //        pressedOrReleased = pressed;
    }

    public int getKeyCode()
    {
        return keyCode;
    }

    public boolean isShift()
    {
        return shift;
    }

    public boolean isCtrl()
    {
        return ctrl;
    }

    //
    //    public boolean isPressedOrReleased()
    //    {
    //        return pressedOrReleased;
    //    }
    //
    //    public void setPressedOrReleased( boolean b )
    //    {
    //        pressedOrReleased = b;
    //    }

    // Object interface

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        //        if ( getClass() != obj.getClass() )
        if ( !(obj instanceof XBKeyInfo) )
            return false;
        XBKeyInfo other = (XBKeyInfo)obj;
        if ( ctrl != other.ctrl )
            return false;
        if ( keyCode != other.keyCode )
            return false;
        if ( shift != other.shift )
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (ctrl ? 1231 : 1237);
        result = prime * result + keyCode;
        result = prime * result + (shift ? 1231 : 1237);
        return result;
    }

    @Override
    public String toString()
    {
        //        return keyCode + "-" + (pressedOrReleased ? "p" : "r") + "-" + (ctrl ? "c" : "") + "-" + (shift ? "s" : "");
        return keyCode + "-" + (ctrl ? "c" : "") + "-" + (shift ? "s" : "");
    }
}
