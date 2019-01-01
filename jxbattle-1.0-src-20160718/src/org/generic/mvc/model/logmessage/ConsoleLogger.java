package org.generic.mvc.model.logmessage;

import org.generic.bean.TextMessage;
import org.generic.mvc.model.observer.MVCModel;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

public class ConsoleLogger implements MVCModelObserver
{
    private LogMessageModel model;

    public ConsoleLogger()
    {
    }

    public void setModel( MVCModel m )
    {
        unsubscribeModel();
        model = null;

        if ( m instanceof LogMessageModel )
        {
            model = (LogMessageModel)m;
            subscribeModel();
        }
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof LogMessageModelChangeId )
        {
            TextMessage tm = (TextMessage)change.getData();
            String m = tm.toString();
            switch ( (LogMessageModelChangeId)change.getChangeId() )
            {
                case InfoMessage:
                case GuiMessage:
                    System.out.println( m );
                    break;

                default:
                    System.err.println( m );
                    if ( tm.getException() != null )
                        tm.getException().printStackTrace();
                    break;
            }
        }
    }

    @Override
    public void subscribeModel()
    {
        if ( model != null )
            model.addObserver( this );
    }

    @Override
    public void unsubscribeModel()
    {
        if ( model != null )
            model.removeObserver( this );
    }

    @Override
    public void close()
    {
        unsubscribeModel();
    }
}
