package org.generic.gui.textpanel;

import java.awt.Color;

import org.generic.bean.cursor2d.Cursor2d;

public class TextCursor
{
    private Cursor2d position;

    private ColorInfo colorInfo;

    public TextCursor()
    {
        position = new Cursor2d();
        colorInfo = new ColorInfo();
    }

    public TextCursor( TextCursor tc )
    {
        position = new Cursor2d( tc.position );
        colorInfo = new ColorInfo( tc.colorInfo );
    }

    public Cursor2d getPosition()
    {
        return position;
    }

    public void setPosition( Cursor2d pos )
    {
        position = pos;
    }

    public boolean isPositionDefined()
    {
        return position.isDefined();
    }

    public ColorInfo getColorInfo()
    {
        return colorInfo;
    }

    public void setColorInfo( Color fg, Color bg )
    {
        colorInfo.setForegroundColor( fg );
        colorInfo.setBackgroundColor( bg );
    }

    // Object interface

    @Override
    public TextCursor clone()
    {
        //        TextCursor res = new TextCursor();
        //        res.position = position.clone();
        //        res.colorInfo = colorInfo.clone();
        //        return res;
        return new TextCursor( this );
    }

    @Override
    public String toString()
    {
        return position.toString();
    }
}
