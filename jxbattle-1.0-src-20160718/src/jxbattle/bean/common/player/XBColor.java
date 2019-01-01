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

package jxbattle.bean.common.player;

import java.awt.Color;

/**
 * player color information
 */
public class XBColor
{
    /**
     * color id.
     * used to speed up network communications and identify players
     */
    private ColorId colorId;

    /**
     * AWT color value
     */
    private Color awtColor;

    /**
     * AWT inverse color value.
     * used for fighting cells
     */
    private Color awtInverse;

    /**
     * color name
     */
    private String name;

    private static int idGen = 0;

    public XBColor( String n, int r, int g, int b, int ri, int gi, int bi )
    {
        colorId = new ColorId( idGen++ );
        name = n;
        awtColor = new Color( r, g, b );
        awtInverse = new Color( ri, gi, bi );
    }

    public XBColor( String n, int r, int g, int b )
    {
        this( n, r, g, b, 0, 0, 0 );
    }

    //    static XBColor valueOf( int id )
    //    {
    //        for ( XBColor col : Consts.playerColors )
    //        {
    //            if ( col.colorId == id )
    //                return col;
    //        }
    //
    //        throw new MVCModelError( "no XBColor with value " + id );
    //    }

    public ColorId getColorId()
    {
        return colorId;
    }

    public String getName()
    {
        return name;
    }

    public Color getAwtColor()
    {
        return awtColor;
    }

    public Color getAwtInverse()
    {
        return awtInverse;
    }

    @Override
    public String toString()
    {
        return name + '(' + colorId + ')';
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

        XBColor other = (XBColor)obj;

        return awtColor.equals( other.awtColor );
    }
}
