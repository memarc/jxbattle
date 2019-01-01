package org.generic.gui.textpanel;

import java.awt.Color;

import org.generic.bean.cursor2d.Cursor2d;

public class ColorInfo
{
    private Color backgroundColor;

    private Color foregroundColor;

    private Cursor2d pos;

    public ColorInfo()
    {
        pos = new Cursor2d();
    }

    public ColorInfo( Color fg, Color bg )
    {
        this();

        foregroundColor = fg;
        backgroundColor = bg;
    }

    public ColorInfo( ColorInfo ci )
    {
        pos = new Cursor2d( ci.pos );
        foregroundColor = ci.foregroundColor;
        backgroundColor = ci.backgroundColor;
    }

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor( Color col )
    {
        backgroundColor = col;
    }

    public Color getForegroundColor()
    {
        return foregroundColor;
    }

    public void setForegroundColor( Color col )
    {
        foregroundColor = col;
    }

    public Cursor2d getPosition()
    {
        return pos;
    }

    @Override
    protected ColorInfo clone()
    {
        //        ColorInfo res = new ColorInfo();
        //        res.foreGroundColor = foreGroundColor;
        //        res.backgroundColor = backgroundColor;
        //        return res;
        return new ColorInfo( this );
    }

    @Override
    public String toString()
    {
        return "fg " + foregroundColor + " bg " + backgroundColor;
    }
}
