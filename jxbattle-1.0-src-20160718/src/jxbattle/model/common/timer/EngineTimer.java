/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package jxbattle.model.common.timer;

public class EngineTimer
{
    /**
     * is timer run() method called ?
     */
    private boolean active;

    /**
     * timer period (ms)
     */
    private long period;

    /**
     * last time timer triggered 
     */
    private long lastTrig;

    /**
     * timer priority
     */
    private int priority;

    /**
     * has timer already triggered ?
     */
    //private boolean ticked;

    private boolean executedHandler;

    public EngineTimer( long p, int prio )
    {
        active = false;
        period = p;
        priority = prio;
        setActive( false );
    }

    public void setPeriod( int p )
    {
        period = p;
    }

    //    public boolean isActive()
    //    {
    //        return active;
    //    }

    public void setActive( boolean b )
    {
        active = b;
        //lastTrig = System.currentTimeMillis();
        if ( active )
            start();
        else
        {
            executedHandler = true;
            lastTrig = Long.MAX_VALUE;
        }
    }

    public void start()
    {
        lastTrig = System.currentTimeMillis();
        //ticked = false;
        executedHandler = false;
    }

    //    public boolean testAndReset()
    //    {
    //        //        boolean res = ticked;
    //        //        ticked = false;
    //        //        return res;
    //        boolean res = periodCompleted();
    //        reset();
    //        return res;
    //    }

    public boolean periodCompleted()
    {
        return System.currentTimeMillis() - lastTrig > period;
    }

    int getPriority()
    {
        return priority;
    }

    boolean canRunHandler()
    {
        return active && !executedHandler && periodCompleted();
    }

    void runHandler()
    {
        // if ( active )
        {
            //lastTrig = System.currentTimeMillis();
            //ticked = true;
            //reset();
            handler();
            executedHandler = true;
        }
    }

    protected void handler()
    {
    }
}
