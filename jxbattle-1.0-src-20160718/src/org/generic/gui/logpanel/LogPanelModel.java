package org.generic.gui.logpanel;

import java.util.concurrent.ArrayBlockingQueue;

import org.generic.mvc.model.logmessage.LogMessageModel;
import org.generic.mvc.model.observer.MVCModelImpl;

public class LogPanelModel extends MVCModelImpl
{
    private LogMessageModel logMessageModel;

    private ArrayBlockingQueue<String> messageBuffer;

    public LogPanelModel( LogMessageModel lmm, boolean bufferise )
    {
        logMessageModel = lmm;
        if ( bufferise )
            messageBuffer = new ArrayBlockingQueue<>( 1000 );

        addRelatedModel( logMessageModel );
    }

    //    ArrayBlockingQueue<String> getMessageBuffer()
    //    {
    //        return messageBuffer;
    //    }

    boolean hasMessageBuffer()
    {
        return messageBuffer != null;
    }

    String getBufferisedMessage()
    {
        return messageBuffer.poll();
    }

    boolean bufferiseMessage( String m )
    {
        return messageBuffer.offer( m );
    }
}
