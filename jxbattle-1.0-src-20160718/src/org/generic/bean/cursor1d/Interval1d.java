package org.generic.bean.cursor1d;

public interface Interval1d
{
    public int getStartPos();

    public void setStartPos( int p );

    public int getEndPos();

    public void setEndPos( int p );

    public int length();

    public boolean includesPos( int p );

    public Interval1d intersection( Interval1d i );

    boolean hasIntersection( Interval1d i );

    public boolean equals( int start, int end );

    public boolean equals( Interval1d i );

    public Interval1d shift( int i );

    public boolean isDefined();

    public Interval1d clone();

    public void undefine();

}
