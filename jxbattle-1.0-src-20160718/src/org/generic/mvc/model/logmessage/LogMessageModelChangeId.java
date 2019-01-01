package org.generic.mvc.model.logmessage;

import org.generic.EnumValue;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChangeId;

public enum LogMessageModelChangeId implements MVCModelChangeId
{
    /**
     * normal logging message
     */
    InfoMessage,

    /**
     * error logging message
     */
    ErrorMessage,

    /**
     * message in a dialog ;-)
     */
    GuiMessage,

    /**
     * remove all messages
     */
    //    ClearMessages,

    ;

    @Override
    public int getOrdinal()
    {
        return super.ordinal();
    }

    @Override
    public LogMessageModelChangeId[] getValues()
    {
        return values();
    }

    @Override
    public LogMessageModelChangeId getValueOf( int val )
    {
        for ( LogMessageModelChangeId im : values() )
        {
            if ( im.ordinal() == val )
                return im;
        }

        throw new MVCModelError( "invalid value for LogMessageModelChangeId enum " + val );
    }

    @Override
    public LogMessageModelChangeId getValueOf( String s )
    {
        return valueOf( s );
    }

    @Override
    public boolean equals( EnumValue v )
    {
        return getOrdinal() == v.getOrdinal();
    }
}
