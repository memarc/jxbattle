package org.generic.bean.cursor1d;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Interval1dHelper implements Comparator<Interval1d>
{
    private List<? extends Interval1d> list;

    public Interval1dHelper( List<? extends Interval1d> l )
    {
        list = l;
    }

    public void sort()
    {
        Collections.sort( list, this );
    }

    @Override
    public int compare( Interval1d ts1, Interval1d ts2 )
    {
        if ( ts1.getEndPos() <= ts2.getStartPos() )
            return -1;
        return 1;
    }

    public Interval1d getIntervalIncluding( int position )
    {
        for ( Interval1d i : list )
            if ( i.includesPos( position ) )
                return i;

        return null;
    }

    private static Interval1d getClosestByEnd( Interval1d c1, Interval1d c2, int p )
    {
        int dx1 = Math.abs( p - c1.getEndPos() );
        int dx2 = Math.abs( p - c2.getEndPos() );
        if ( dx1 <= dx2 )
            return c1;
        return c2;
    }

    public Interval1d getLeftInterval( Interval1d c )
    {
        Interval1d closest = null;

        for ( Interval1d i : list )
        {
            int c2 = i.getEndPos();
            if ( c2 < c.getStartPos() )
            {
                if ( closest == null )
                    closest = i;
                else
                {
                    if ( getClosestByEnd( closest, i, c.getStartPos() ) == i )
                        closest = i;
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

    private static Interval1d getClosestByStart( Interval1d c1, Interval1d c2, int p )
    {
        int dx1 = Math.abs( p - c1.getStartPos() );
        int dx2 = Math.abs( p - c2.getStartPos() );
        if ( dx1 <= dx2 )
            return c1;
        return c2;
    }

    public Interval1d getRightInterval( Interval1d c )
    {
        Interval1d closest = null;

        if ( c.isDefined() )
        {
            for ( Interval1d i : list )
            {
                int c2 = i.getStartPos();
                if ( c2 > c.getEndPos() )
                {
                    if ( closest == null )
                        closest = i;
                    else
                    {
                        if ( getClosestByStart( closest, i, c.getStartPos() ) == i )
                            closest = i;
                    }
                }
            }
        }

        return closest;
    }

    //    public Interval2d getLeftInterval( Interval2d i )
    //    {
    //        //        Interval2d res = null;
    //        //        for ( Interval2d ts : list )
    //        //        {
    //        //            if ( compare( i, ts ) == -1 )
    //        //                return res;
    //        //            res = ts;
    //        //        }
    //        //
    //        //        return res;
    //
    //        return getLeftInterval( i.getStartCursor() );
    //    }
    //
    //    public Interval2d getRightInterval( Interval2d i )
    //    {
    //        return getRightInterval( i.getEndCursor() );
    //    }
}