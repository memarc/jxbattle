package org.generic.thread;

public class ThreadUtils
{
    //    private static Long startTime = new Long( System.currentTimeMillis() );
    private static long startTime = System.currentTimeMillis();

    public static long getApplicationStartTime()
    {
        return startTime;
    }

    public static void sleep( long ms )
    {
        try
        {
            Thread.sleep( ms );
        }
        catch( InterruptedException e )
        {
        }
    }

    public static void sleep( long ms, int ns )
    {
        try
        {
            Thread.sleep( ms, ns );
        }
        catch( InterruptedException e )
        {
        }
    }
}
