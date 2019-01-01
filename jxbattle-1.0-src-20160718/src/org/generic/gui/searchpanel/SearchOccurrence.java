package org.generic.gui.searchpanel;

import org.generic.bean.cursor2d.Cursor2d;

public class SearchOccurrence
{
    private int xPosition;

    private int yPosition;

    private int length;

    private Cursor2d pos;

    public SearchOccurrence( int x, int y, int l )
    {
        xPosition = x;
        yPosition = y;
        length = l;
    }

    public int getXPosition()
    {
        return xPosition;
    }

    public int getYPosition()
    {
        return yPosition;
    }

    public int getLength()
    {
        return length;
    }

    public Cursor2d toCursor2d()
    {
        if ( pos == null )
            pos = new Cursor2d( xPosition, yPosition );
        return pos;
    }
}
