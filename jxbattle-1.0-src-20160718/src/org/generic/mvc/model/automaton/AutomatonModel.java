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

package org.generic.mvc.model.automaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public class AutomatonModel<S extends StateId, T extends AutomatonTransitionId> extends MVCModelImpl
{
    private S currentState;

    private List<Condition> currentConditions;

    private List<TransitionRule<T, S>> transitionRules;

    //private DebugLog debugLog;

    public AutomatonModel( S initState )
    {
        currentState = initState;
        currentConditions = Collections.synchronizedList( new ArrayList<Condition>() );
        transitionRules = new ArrayList<>();
    }

    //        public void setDebugLog( DebugLog dl )
    //        {
    //            debugLog = dl;
    //        }

    private void debugLog( String m )
    {
        //        if ( debugLog != null )
        //            debugLog.log( "{" + getCurrentState() + "} " + m );
    }

    public List<TransitionRule<T, S>> getTransitionRules()
    {
        return transitionRules;
    }

    public S getCurrentState()
    {
        return currentState;
    }

    public void setCurrentState( Object sender, S state )
    {
        boolean changed = currentState != state;
        if ( changed )
        {
            debugLog( "state change : " + currentState + " -> " + state + "  " + currentConditions );
            currentState = state;
            notifyObservers( new MVCModelChange( sender, this, AutomatonModelChangedId.AutomatonStateChanged, currentState ) );
        }
    }

    public List<Condition> getCurrentConditions()
    {
        return currentConditions;
    }

    public synchronized void setCondition( ConditionId id )
    {
        if ( !hasCondition( id ) )
        {
            Condition c = new Condition( id, true, null );
            currentConditions.add( c );
            debugLog( "setCondition : " + id + " -> " + currentConditions.toString() );
        }
    }

    public boolean hasCondition( ConditionId id )
    {
        for ( Condition c : currentConditions )
        {
            if ( c.equals( id ) )
                return true;
        }

        return false;
    }

    public void removeAllConditions()
    {
        currentConditions.clear();
    }
}
