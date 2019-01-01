package org.generic.gui.textpanel;

import org.generic.bean.cursor2d.Cursor2d;
import org.generic.bean.cursor2d.Interval2dImpl;

public class TextSelection extends Interval2dImpl
{
    private ColorInfo color;

    //    private boolean isDefined;

    private String tooltipText;

    private int layerLevel; // layered selection representation

    private Object userData1; // free field data

    private Object userData2; // free field data

    private Object userData3; // free field data

    public TextSelection()
    {
        //        isDefined = true;
        layerLevel = 0;
    }

    public TextSelection( TextSelection ts )
    {
        super( ts );

        startCursor = new Cursor2d( ts.startCursor );
        endCursor = new Cursor2d( ts.endCursor );
        color = new ColorInfo( ts.color );
        tooltipText = ts.tooltipText;
        layerLevel = ts.layerLevel;
        userData1 = ts.userData1;
        userData2 = ts.userData2;
        userData3 = ts.userData3;
    }

    public ColorInfo getColor()
    {
        return color;
    }

    public void setColor( ColorInfo c )
    {
        color = c;
    }

    public void startSelection( Cursor2d c )
    {
        //        isDefined = false;
        startCursor = c.clone();
        endCursor.undefine();

        //        System.out.println( "start sel " + startIndex + " -> " + endIndex );
    }

    public void editSelection( Cursor2d c )
    {
        //        endIndex = c.clone();

        //        if ( c.isLowerThan( startIndex ) )
        //        {
        //            if ( !endIndex.isDefined() )
        //                endIndex = startIndex;
        //            startIndex = c.clone();
        //        }
        //        else
        endCursor = c.clone();
        //        isDefined = true;

        //        System.out.println( "edit sel " + startIndex + " -> " + endIndex );
    }

    public void completeSelection()
    {
        //            if ( c.isLowerThan( startIndex ) )
        //                System.out.println( "compl sel c " + c + " < start " + startIndex );
        //            else
        //                System.out.println( "compl sel start " + startIndex + " < c " + c );
        //
        //            if ( isDefined )
        //            {
        //                if ( c.isLowerThan( endIndex ) )
        //                    startIndex = c.clone();
        //                else
        //                    endIndex = c.clone();
        //            }
        //            else
        //            {
        //                if ( c.isLowerThan( startIndex ) )
        //                {
        //                    //                    System.out.println( "compl sel c " + c );
        //                    //                    System.out.println( "compl sel start " + startIndex );
        //                    endIndex = startIndex;
        //                    startIndex = c.clone();
        //                    //                    System.out.println( "compl sel start2 " + startIndex + " end2 " + endIndex );
        //                }
        //                else
        //                    endIndex = c.clone();
        //            }
        //
        //            isDefined = true;

        //System.out.println( "compl sel 1 " + startIndex + " -> " + endIndex );
        sortIndices();
        //        System.out.println( "compl sel 2 " + startIndex + " -> " + endIndex );
    }

    public void endSelection( Cursor2d c )
    {
        //        endIndex = c.clone();
        //        isDefined = true;
        //        sortIndices();
        editSelection( c );
        completeSelection();
    }

    boolean hasTooltipText()
    {
        return tooltipText != null;
    }

    public String getTooltipText()
    {
        return tooltipText;
    }

    public void setTooltipText( String s )
    {
        tooltipText = s;
    }

    public int getLayerLevel()
    {
        return layerLevel;
    }

    public void setLayerLevel( int l )
    {
        layerLevel = l;
    }

    public Object getUserData1()
    {
        return userData1;
    }

    public void setUserData1( Object data )
    {
        userData1 = data;
    }

    public Object getUserData2()
    {
        return userData2;
    }

    public void setUserData2( Object data )
    {
        userData2 = data;
    }

    public Object getUserData3()
    {
        return userData3;
    }

    public void setUserData3( Object data )
    {
        userData3 = data;
    }

    // Object interface

    @Override
    public TextSelection clone()
    {
        //        TextSelection res = new TextSelection();
        //        //        res.isDefined = isDefined;
        //        res.startCursor = startCursor.clone();
        //        res.endCursor = endCursor.clone();
        //        res.color = color;
        //        res.tooltipText = tooltipText;
        //        res.layerLevel = layerLevel;
        //        return res;
        return new TextSelection( this );
    }

    @Override
    public String toString()
    {
        //        return tooltipText + " : " + startCursor.getX() + "->" + endCursor.getX() + " (" + layerLevel + ")";
        return tooltipText + " : " + startCursor.getX() + "->" + endCursor.getX();
    }
}
