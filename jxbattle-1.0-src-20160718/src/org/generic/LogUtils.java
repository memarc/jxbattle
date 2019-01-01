package org.generic;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.string.StringUtils;

public class LogUtils
{
    public static String getLastWords( String s, char sep, int count )
    {
        StringBuilder res = new StringBuilder();
        if ( s != null )
        {
            String[] words = s.split( "\\." );
            for ( int i = 0; i < count; i++ )
            {
                if ( i > 0 )
                    res.append( sep );
                res.insert( 0, words[ words.length - 1 - i ] );
            }
        }
        return res.toString();
    }

    //private static SimpleDateFormat simpleTimeFormat = new SimpleDateFormat( "[ HH:mm:ss SSS ] : " );

    //    public static String getTimeStamp()
    //    {
    //        return getTimeStamp( new Date() );
    //    }
    //
    //    public static String getTimeStamp( Date date )
    //    {
    //        return simpleTimeFormat.format( date );
    //    }

    private static SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat( "[ yyyy-MM-dd HH:mm:ss SSS ] : " );

    public static String getDateTimeStamp()
    {
        return simpleDateTimeFormat.format( new Date() );
    }

    public static String getDateTimeStamp( Date date )
    {
        return simpleDateTimeFormat.format( date );
    }

    public static String objectIdentity( Object obj )
    {
        if ( obj == null )
            return "<null>";
        return StringUtils.rightPadString( getLastWords( obj.getClass().getCanonicalName(), '.', 1 ) + " (objectid=" + java.lang.System.identityHashCode( obj ) + ")", ' ', 55 );
    }

    public static String objectIdentity( StackTraceElement where, Object who )
    {
        return StringUtils.rightPadString( getLastWords( where.getClassName(), '.', 1 ) + "." + where.getMethodName() + " (objectid=" + java.lang.System.identityHashCode( who ) + ")", ' ', 70 );
    }

    public static String durationString( long refTime )
    {
        long dur = System.currentTimeMillis() - refTime;
        int ms = (int)(dur % 1000);
        dur /= 1000;
        int s = (int)(dur % 1000);
        StringBuilder sb = new StringBuilder();
        if ( s > 0 )
        {
            sb.append( s );
            sb.append( "s " );
        }
        if ( ms > 0 )
        {
            sb.append( ms );
            sb.append( "ms" );
        }
        return sb.toString();
    }

    public static String getExceptionLabel( Throwable t )
    {
        return t.getMessage() == null ? t.getClass().getName() : t.getMessage();
    }

    //    public static String stackTraceToString( StackTraceElement[] stack )
    //    {
    //        StringBuilder sb = new StringBuilder();
    //        boolean first = true;
    //        for ( StackTraceElement ste : stack )
    //        {
    //            if ( !first )
    //                sb.append( '\n' );
    //            sb.append( ste.toString() );
    //            first = false;
    //        }
    //        return sb.toString();
    //    }

    public static String stackTraceToString( Thread thread )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( thread );

        for ( StackTraceElement ste : thread.getStackTrace() )
        {
            sb.append( '\n' );
            sb.append( ste.toString() );
        }
        return sb.toString();
    }

    public static String stackTraceToString( Thread thread, int minFrame, int maxFrame )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( thread );

        int f = 0;
        for ( StackTraceElement ste : thread.getStackTrace() )
        {
            if ( f >= minFrame && f <= maxFrame )
            {
                sb.append( '\n' );
                sb.append( ste.toString() );
            }
            f++;
        }

        return sb.toString();
    }

    public static void logModelChange( Object who, MVCModelChange change )
    {
        StackTraceElement e = Thread.currentThread().getStackTrace()[ 2 ];
        //logModelChange( Utils.getLastWords( e.getClassName(), '.', 1 ) + "." + e.getMethodName(), who, change );
        String warning = System.identityHashCode( who ) == System.identityHashCode( change.getSender() ) ? "!!!! " : "     ";
        String s = warning + LogUtils.objectIdentity( e, who ) + " : " + change.toString( System.identityHashCode( who ) != System.identityHashCode( change.getSourceModel() ) );
        System.out.println( LogUtils.getDateTimeStamp() + s );
    }
}
