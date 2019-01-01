package org.generic.bean.cursor2d;

import org.generic.bean.cursor1d.Interval1d;
import org.generic.bean.cursor1d.Interval1dImpl;

public class Interval2dImpl implements Interval2d
{
    protected Cursor2d startCursor;

    protected Cursor2d endCursor;

    private Interval1d interval1d;

    public Interval2dImpl()
    {
        startCursor = new Cursor2d();
        endCursor = new Cursor2d();
        interval1d = new Interval1dImpl();
    }

    public Interval2dImpl( Interval2dImpl i )
    {
        startCursor = new Cursor2d( i.getStartCursor() );
        endCursor = new Cursor2d( i.getEndCursor() );
        interval1d = new Interval1dImpl( i.interval1d );
    }

    public Interval2dImpl( int start, int end )
    {
        startCursor = new Cursor2d( start, 0 );
        endCursor = new Cursor2d( end, 0 );
        interval1d = new Interval1dImpl( start, end );
    }

    @Override
    public Cursor2d getStartCursor()
    {
        return startCursor;
    }

    @Override
    public Cursor2d getEndCursor()
    {
        return endCursor;
    }

    private void swapIndices()
    {
        Cursor2d tmp = endCursor;
        endCursor = startCursor;
        startCursor = tmp;
    }

    protected void sortIndices()
    {
        if ( endCursor.isLowerThan( startCursor ) )
            swapIndices();
    }

    private Cursor2d getLowerBound()
    {
        if ( endCursor.isLowerThan( startCursor ) )
            return endCursor;
        return startCursor;
    }

    private Cursor2d getUpperBound()
    {
        if ( startCursor.isLowerThan( endCursor ) )
            return endCursor;
        return startCursor;
    }

    @Override
    public boolean includesCoordinates( int x, int y )
    {
        if ( isDefined() )
        {
            Cursor2d low = getLowerBound();
            //            Cursor2d low = startIndex;
            Cursor2d high = getUpperBound();
            //            Cursor2d high = endIndex;
            Cursor2d tmp = new Cursor2d( x, y );

            boolean res = low.isLowerOrEqualThan( tmp ) && tmp.isLowerOrEqualThan( high );
            //System.out.println( "incl " + low + " <= " + tmp + " <= " + high + " : " + res );
            return res;
        }

        return false;
    }

    //    @Override
    //    public Interval2d intersection( Interval2d i )
    //    {
    //        Interval2d res = new Interval2dImpl();
    //        //        if ( isDefined() && i.getStartCursor().isDefined() && i.getEndCursor().isDefined() )
    //        if ( isDefined() && i.isDefined() )
    //        {
    //            Cursor2d s1 = getStartCursor();
    //            Cursor2d e1 = getEndCursor();
    //
    //            Cursor2d s2 = i.getStartCursor();
    //            Cursor2d e2 = i.getEndCursor();
    //
    //            boolean empty = s1.isGreaterThan( e2 ) || s2.isGreaterThan( e1 );
    //            if ( !empty )
    //            {
    //                boolean b1 = s2.isLowerOrEqualThan( s1 );
    //                Cursor2d is = b1 ? s1 : s2;
    //
    //                boolean b2 = e2.isLowerOrEqualThan( e1 );
    //                Cursor2d ie = b2 ? e2 : e1;
    //
    //                res.getStartCursor().set( is );
    //                res.getEndCursor().set( ie );
    //            }
    //        }
    //        return res;
    //    }

    private void updateInterval1d()
    {
        if ( isDefined() && startCursor.getY().getValue() == endCursor.getY().getValue() )
        {
            if ( interval1d == null )
                interval1d = new Interval1dImpl();
            interval1d.setStartPos( getStartIndex() );
            interval1d.setEndPos( getEndIndex() );
        }
        else
            interval1d.undefine();
    }

    public boolean hasInterval1d( int start, int end )
    {
        return interval1d.equals( start, end );
    }

    @Override
    public Interval1d getInterval1d()
    {
        return interval1d;
    }

    // 1D start position
    public int getStartIndex()
    {
        return startCursor.getX().getValue();
    }

    // 1D start position
    public void setStartIndex( int i )
    {
        startCursor.set( i, 0 );
        updateInterval1d();
    }

    // 1D end position
    public int getEndIndex()
    {
        return endCursor.getX().getValue();
    }

    // 1D end position
    public void setEndIndex( int i )
    {
        endCursor.set( i, 0 );
        updateInterval1d();
    }

    @Override
    public boolean isDefined()
    {
        return startCursor.isDefined() && endCursor.isDefined();
    }

    public void undefine()
    {
        startCursor.undefine();
        endCursor.undefine();
        interval1d.undefine();
    }

    public boolean equals( Interval2d i )
    {
        return startCursor.equals( i.getStartCursor() ) && endCursor.equals( i.getEndCursor() );
    }

    // Object interface

    @Override
    public Interval2dImpl clone()
    {
        //        Interval2dImpl res = new Interval2dImpl();
        //        res.startCursor = startCursor.clone();
        //        res.endCursor = endCursor.clone();
        //        return res;
        return new Interval2dImpl( this );
    }

    @Override
    public String toString()
    {
        return startCursor.toString() + "->" + endCursor.toString();
    }
}
