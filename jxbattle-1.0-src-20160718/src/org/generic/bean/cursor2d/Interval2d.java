package org.generic.bean.cursor2d;

import org.generic.bean.cursor1d.Interval1d;

public interface Interval2d
{
    public Cursor2d getStartCursor();

    public Cursor2d getEndCursor();

    public boolean includesCoordinates( int x, int y );

    //    public Interval2d intersection( Interval2d i );

    public Interval1d getInterval1d();

    public boolean isDefined();
}
