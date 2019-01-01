package org.generic.bean.cursor2d;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.generic.bean.cursor1d.Interval1d;

public class Interval2dHelper //implements Comparator<Interval2d>
{
    private List<? extends Interval2d> list;

    private Comparator<Interval2d> comp;

    public Interval2dHelper( List<? extends Interval2d> l )
    {
        list = l;
        comp = new Comparator<Interval2d>()
        {
            @Override
            public int compare( Interval2d ts1, Interval2d ts2 )
            {
                if ( ts1.getEndCursor().isLowerOrEqualThan( ts2.getStartCursor() ) )
                    return -1;
                return 1;
            }
        };

    }

    public void sort()
    {
        //        Collections.sort( list, this );
        Collections.sort( list, comp );
    }

    //    public int compare( Interval2d ts1, Interval2d ts2 )
    //    {
    //        if ( ts1.getEndCursor().isLowerOrEqualThan( ts2.getStartCursor() ) )
    //            return -1;
    //        return 1;
    //    }

    public Interval2d getIntervalIncluding( int position )
    {
        for ( Interval2d i : list )
            if ( i.includesCoordinates( position, 0 ) )
                return i;

        return null;
    }

    //    public Interval2d hasInterval( Cursor2d start, Cursor2d end )
    //    {
    //        for ( Interval2d i : list )
    //            if ( i.getStartCursor().equals( start ) && i.getEndCursor().equals( end ) )
    //                return i;
    //        return null;
    //    }
    //
    //    public Interval2d hasInterval( int start, int end )
    //    {
    //        for ( Interval2d i : list )
    //        {
    //            if ( i.getInterval1d() != null )
    //                if ( i.getInterval1d().equals( start, end ) )
    //                    return i;
    //        }
    //        return null;
    //    }

    public Interval1d hasInterval1d( int start, int end )
    {
        for ( Interval2d i : list )
            if ( i.getInterval1d().equals( start, end ) )
                return i.getInterval1d();
        return null;
    }

    public Interval2d getLeftInterval( Cursor2d c )
    {
        Interval2d closest = null;

        if ( c.isDefined() )
        {
            for ( Interval2d i : list )
            {
                if ( i.getEndCursor().isLowerThan( c ) )
                {
                    if ( closest == null )
                        closest = i;
                    else
                    {
                        Cursor2d c2 = i.getEndCursor();
                        if ( c.getClosest( closest.getEndCursor(), c2 ) == c2 )
                            closest = i;
                    }
                }
            }
        }

        return closest;
    }

    //    public Interval2d getRightInterval( Cursor2d c )
    //    {
    //        Interval2d res = null;
    //
    //        if ( c.isDefined() )
    //            for ( Interval2d i : list )
    //            {
    //                if ( i.getStartCursor().isGreaterThan( c ) )
    //                    return res;
    //                res = i;
    //            }
    //
    //        return res;
    //    }

    public Interval2d getRightInterval( Cursor2d c )
    {
        Interval2d closest = null;

        if ( c.isDefined() )
        {
            for ( Interval2d i : list )
            {
                if ( i.getStartCursor().isGreaterThan( c ) )
                {
                    if ( closest == null )
                        closest = i;
                    else
                    {
                        Cursor2d c2 = i.getStartCursor();
                        if ( c.getClosest( closest.getStartCursor(), c2 ) == c2 )
                            closest = i;
                    }
                }
            }
        }

        return closest;
    }

    public Interval2d getLeftInterval( Interval2d i )
    {
        //        Interval2d res = null;
        //        for ( Interval2d ts : list )
        //        {
        //            if ( compare( i, ts ) == -1 )
        //                return res;
        //            res = ts;
        //        }
        //
        //        return res;

        return getLeftInterval( i.getStartCursor() );
    }

    public Interval2d getRightInterval( Interval2d i )
    {
        return getRightInterval( i.getEndCursor() );
    }
}