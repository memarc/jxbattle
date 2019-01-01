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

import java.util.ArrayList;
import java.util.List;

public class TimerManager
{
    /**
     * registred timers
     */
    private List<EngineTimer> timers;

    public TimerManager()
    {
        timers = new ArrayList<>();
    }

    public void processTimers()
    {
        List<EngineTimer> canTrigger = new ArrayList<>();

        // get timers that can trigger

        for ( EngineTimer timer : timers )
        {
            //if ( timer.isActive() && timer.periodCompleted() )
            if ( timer.canRunHandler() )
                canTrigger.add( timer );
        }

        // get most prioritary

        int p = Integer.MIN_VALUE;
        EngineTimer winner = null;
        for ( EngineTimer timer : canTrigger )
        {
            if ( timer.getPriority() > p )
            {
                p = timer.getPriority();
                winner = timer;
            }
        }

        // run it !

        if ( winner != null )
            winner.runHandler();
        //winner.run();
    }

    public void clearAll()
    {
        timers.clear();
    }

    public void addTimer( EngineTimer timer )
    {
        timers.add( timer );
    }
}
