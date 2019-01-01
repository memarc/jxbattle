package org.generic.thread;

public class BoolBarrier
{
    private boolean condition;

    private Object monitor;

    public BoolBarrier()
    {
        monitor = new Object();
        resetCondition();
    }

    public boolean isConditionSet()
    {
        return condition;
    }

    public void resetCondition()
    {
        condition = false;
    }

    public void waitNotify()
    {
        while ( !condition )
        {
            synchronized( monitor )
            {
                try
                {
                    monitor.wait();
                }
                catch( InterruptedException e )
                {
                }
            }
        }
    }

    public void notifyWaiters()
    {
        condition = true;
        synchronized( monitor )
        {
            monitor.notifyAll();
        }
    }
}
