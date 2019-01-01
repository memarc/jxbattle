package org.generic.bean;

import java.util.Date;

import org.generic.LogUtils;

/**
 * text message
 */
public class TextMessage extends Message
{
    /**
     * date stamp
     */
    private Date date;

    /**
     * message text
     */
    //private String message;

    /**
     * linked exception
     */
    private Throwable exception;

    public TextMessage( String m )
    {
        set( m );
        //System.out.println( toString() );
    }

    public TextMessage( String s, Throwable e )
    {
        exception = e;
        set( s );
        //System.err.println( toString() );
        //        if ( e != null )
        //            e.printStackTrace();
    }

    private void set( String m )
    {
        date = new Date();
        setData( m );
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( LogUtils.getDateTimeStamp( date ) );

        sb.append( getMessage() );
        if ( exception != null )
        {
            sb.append( " : " );

            //            String em = exception.getMessage();
            //            if ( em == null )
            //                em = exception.getClass().getName();
            //            sb.append( em );
            sb.append( LogUtils.getExceptionLabel( exception ) );
        }

        return sb.toString();
    }

    public String getMessage()
    {
        return (String)getData();
    }

    public Throwable getException()
    {
        return exception;
    }
}
