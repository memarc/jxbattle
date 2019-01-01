package org.generic.thread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.generic.LogUtils;

/**
 * @see http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/locks/AbstractQueuedSynchronizer.html
 */

public class Mutex extends AbstractQueuedSynchronizer implements Lock
{
    private boolean debug = false;

    //        private boolean debug = true;

    //private StackTraceElement[] ownerThreadStacktrace; // for debug purposes
    private String ownerThreadStacktrace;

    //private boolean dolog;

    //private static List<String> loglines = new ArrayList<String>();

    //        private static Logger logger = LoggerFactory.getLogger( "Mutex" );
    //
    //        private static void log( String s )
    //        {
    //            logger.debug( s );
    //        }

    //    private static void log( String s )
    //    {
    //        synchronized( loglines )
    //        {
    //            loglines.add( s );
    //        }
    //    }

    //    private static void flushLog()
    //    {
    //        synchronized( loglines )
    //        {
    //            for ( String s : loglines )
    //                System.err.println( s );
    //        }
    //    }

    // Reports whether in locked state
    @Override
    protected boolean isHeldExclusively()
    {
        return getState() == 1;
    }

    public String getOwnerThreadStacktrace()
    {
        return ownerThreadStacktrace;
    }

    //    private String formatLog( String s, Thread th )
    //    {
    //        return System.nanoTime() + " /" + (th == null ? "<nothread>" : th.toString()) + " - " + LogUtils.objectIdentity( this ) + " : " + s;
    //        //return LogUtils.objectIdentity( this ) + " : " + s;
    //    }

    //    private String formatLog( String s )
    //    {
    //        return formatLog( s, Thread.currentThread() );
    //    }

    //    private synchronized void printStackTrace( String header, Thread th, StackTraceElement[] es, int min )
    //    {
    //        if ( es != null )
    //        {
    //            int i = 0;
    //            for ( StackTraceElement e : es )
    //            {
    //                if ( i > min )
    //                    log( formatLog( header, th ) + " " + e );
    //                i++;
    //            }
    //        }
    //        else
    //            log( formatLog( header, th ) + " <none>" );
    //        //ps.flush();
    //    }

    //    private synchronized void printStackTrace( String header, Thread th, String stacktrace )
    //    {
    //        if ( stacktrace != null )
    //            log( formatLog( header, th ) + " " + stacktrace );
    //        else
    //            log( formatLog( header, th ) + " <none>" );
    //    }

    /**
     * @return true if current thread is lock owner
     */
    private boolean isOwner()
    {
        return Thread.currentThread() == getExclusiveOwnerThread();
    }

    //    public synchronized void logLock()
    //    {
    //        if ( debug ) // && dolog )
    //        //                if ( isOwner() ) // auto lock (re-lock) problem
    //        {
    //            log( formatLog( "mutex already locked ! " + getExclusiveOwnerThread() ) );
    //            //log( "s=" + s + " state=" + getState() );
    //            log( formatLog( "current thread : " + Thread.currentThread() ) );
    //            log( formatLog( "current method : " ) );
    //            //            printStackTrace( "acq", Thread.currentThread(), Thread.currentThread().getStackTrace(), 0 );
    //            printStackTrace( "acq", Thread.currentThread(), null );
    //
    //            //assert (getExclusiveOwnerThread() != null);
    //            log( formatLog( "owner thread : " + getExclusiveOwnerThread() ) );
    //            log( formatLog( "owner method : " ) );
    //            printStackTrace( "acq", getExclusiveOwnerThread(), ownerThreadStacktrace );
    //
    //            if ( getExclusiveOwnerThread() == null )
    //                flushLog();
    //            log( formatLog( "threads waiting : " + getQueuedThreads() ) );
    //        }
    //    }

    // Acquires the lock if state is zero
    @Override
    protected boolean tryAcquire( int acquires )
    {
        assert acquires == 1; // Otherwise unused
        if ( compareAndSetState( 0, 1 ) )
        {
            if ( debug )
                synchronized( this )
                {
                    assert (getExclusiveOwnerThread() == null);
                    setExclusiveOwnerThread( Thread.currentThread() );

                    if ( debug )
                        //                    if ( ownerThreadStacktrace != null )
                        //                        printStackTrace( Thread.currentThread().getStackTrace(), 0, System.err );
                        assert (ownerThreadStacktrace == null);
                    ownerThreadStacktrace = LogUtils.stackTraceToString( Thread.currentThread() );
                }
            else
            {
                setExclusiveOwnerThread( Thread.currentThread() );
                //                ownerThreadStacktrace = LogUtils.stackTraceToString( Thread.currentThread() );
            }
            return true;
        }

        //logLock();
        return false;
    }

    // Releases the lock by setting state to zero
    @Override
    protected boolean tryRelease( int releases )
    {
        assert releases == 1; // Otherwise unused
        if ( getState() == 0 )
            throw new IllegalMonitorStateException();
        if ( debug )
            synchronized( this )
            {
                if ( !isOwner() )
                    throw new IllegalMonitorStateException();

                assert (getExclusiveOwnerThread() != null);
                if ( debug )
                {
                    //                    log( formatLog( "tryRelease", Thread.currentThread() ) + " owner was " + getExclusiveOwnerThread() );
                    //                    //                    printStackTrace( "tryRelease", Thread.currentThread(), Thread.currentThread().getStackTrace(), 0 );
                    //                    printStackTrace( "tryRelease", Thread.currentThread(), null );
                    ownerThreadStacktrace = null;
                }

                setExclusiveOwnerThread( null );
            }
        else
        {
            setExclusiveOwnerThread( null );
            ownerThreadStacktrace = null;
        }

        setState( 0 );
        return true;
    }

    // Provides a Condition
    @Override
    public Condition newCondition()
    {
        return new ConditionObject();
    }

    // Deserializes properly
    private void readObject( ObjectInputStream s ) throws IOException, ClassNotFoundException
    {
        s.defaultReadObject();
        setState( 0 ); // reset to unlocked state
    }

    private Thread getOwnerThread()
    {
        return super.getExclusiveOwnerThread();
    }

    @Override
    public void lock()
    {
        //        if ( debug )
        //        {
        //            boolean logged = false;
        //            {

        //        while ( !tryAcquire( 1 ) )
        //        {
        //            //            if ( !logged )
        //            //            {
        //            //                logLock();
        //            //                logged = true;
        //            //            }
        //
        //            try
        //            {
        //                Thread.sleep( 0 );
        //            }
        //            catch( InterruptedException e )
        //            {
        //                e.printStackTrace();
        //            }
        //        }

        //            }
        //        }
        //        else
        acquire( 1 );
    }

    //    /**
    //     * @return true if lock could be acquired, ie. was not locked before, or not already locked by current thread 
    //     */
    //    public boolean lockSelf()
    //    {
    //        if ( isOwner() )
    //            return false;
    //
    //        if ( debug )
    //        {
    //            boolean logged = false;
    //            while ( !tryAcquire( 1 ) )
    //            {
    //                if ( !logged )
    //                {
    //                    logLock();
    //                    logged = true;
    //                }
    //
    //                try
    //                {
    //                    Thread.sleep( 0 );
    //                }
    //                catch( InterruptedException e )
    //                {
    //                    e.printStackTrace();
    //                }
    //            }
    //        }
    //        else
    //            acquire( 1 );
    //
    //        return true;
    //    }

    /**
     * Acquire lock and block only if held by another thread ie.
     * does not block if was not locked before, or locked by current thread.
     * @return true if unlock must be called  
     */
    public boolean lockSelf()
    {
        if ( tryAcquire( 1 ) )
            return true; // lock was free

        // lock was not free before tryAcquire()
        if ( getExclusiveOwnerThread() == Thread.currentThread() )
            return false; // lock is held by current thread

        acquire( 1 );
        return true;
    }

    /**
     * @return true if lock could be acquired (was not locked before) 
     */
    @Override
    public boolean tryLock()
    {
        return tryAcquire( 1 );
    }

    @Override
    public void unlock()
    {
        release( 1 );
    }

    public boolean isLocked()
    {
        return isHeldExclusively();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException
    {
        acquireInterruptibly( 1 );
    }

    @Override
    public boolean tryLock( long timeout, TimeUnit unit ) throws InterruptedException
    {
        return tryAcquireNanos( 1, unit.toNanos( timeout ) );
    }

    //    public Collection<Thread> getQueuedThreads()
    //    {
    //        return getExclusiveQueuedThreads();
    //    }

    //    public Thread getOwnerThread()
    //    {
    //        return getOwnerThread();
    //    }

    //    public StackTraceElement[] getOwnerThreadStacktrace()
    //    {
    //        return ownerThreadStacktrace;
    //    }

    public void setDebug()
    {
        debug = true;
    }

    @Override
    public String toString()
    {
        if ( isLocked() )
        {
            StringBuilder sb = new StringBuilder();
            sb.append( "locked by " );
            sb.append( getOwnerThread().toString() );
            //if ( debug )
            {
                sb.append( '\n' );
                //                sb.append( stackTraceToString( ownerThreadStacktrace ) );
                sb.append( ownerThreadStacktrace );
            }
            return sb.toString();
        }

        return "unlocked";
    }
}