package org.generic.thread;

public class SyncBool
{
    private Mutex mutex;

    private boolean value;

    public SyncBool( boolean v )
    {
        mutex = new Mutex();
        value = v;
    }

    public boolean get()
    {
        try
        {
            mutex.lock();
            return value;
        }
        finally
        {
            mutex.unlock();
        }
    }

    public boolean getAndReset()
    {
        try
        {
            mutex.lock();
            boolean res = value;
            value = false;
            return res;
        }
        finally
        {
            mutex.unlock();
        }
    }

    public void set( boolean b )
    {
        mutex.lock();
        value = b;
        mutex.unlock();
    }

    @Override
    public String toString()
    {
        return Boolean.valueOf( value ).toString();
    }
}
