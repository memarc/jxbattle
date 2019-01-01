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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public abstract class AutomatonModel2<S extends StateId, C extends ConditionId, T extends AutomatonTransitionId> extends MVCModelImpl implements Automaton<S, C>
{
    private boolean initialState; // initial state processing

    private S currentState;

    private List<TransitionRule<T, S>> transitionRules;

    //private List<Condition> currentConditions;
    //    private ArrayBlockingQueue<Condition> currentTemporaryConditions;
    private List<Condition> currentTemporaryConditions;

    private ArrayBlockingQueue<Condition> nextTemporaryConditions;

    private ArrayBlockingQueue<Condition> permanentConditions;

    //    private final boolean debugLog = true;
    private final boolean debugLog = false;

    public AutomatonModel2( S initState )
    {
        currentState = initState;
        initialState = true;
        transitionRules = new ArrayList<>();
        //        currentConditions = Collections.synchronizedList( new ArrayList<Condition>() );
        //        currentTemporaryConditions = new ArrayBlockingQueue<>( 100 );
        currentTemporaryConditions = new ArrayList<>();
        nextTemporaryConditions = new ArrayBlockingQueue<>( 100 );
        permanentConditions = new ArrayBlockingQueue<>( 100 );
    }

    /**
     * list transition rules applicable to current state
     */
    private List<TransitionRule<T, S>> getApplicableTransitions()
    {
        List<TransitionRule<T, S>> res = new ArrayList<>();

        for ( TransitionRule<T, S> tr : transitionRules )
        {
            //if ( tr.getActiveState() == currentState )
            if ( tr.isValidated( currentState ) )
                res.add( tr );
        }

        return res;
    }

    protected void addTransitionRule( TransitionRule<T, S> rule )
    {
        transitionRules.add( rule );
    }

    /**
     * determine if a temporary/permanent condition exists
     */
    protected boolean hasCondition( ConditionId id )
    {
        return getCondition( id ) != null;
    }

    private Condition getCondition( ConditionId id )
    {
        for ( Condition c : currentTemporaryConditions )
            if ( c.equals( id ) )
                return c;

        for ( Condition c : permanentConditions )
            if ( c.equals( id ) )
                return c;

        return null;
    }

    protected void removeAllConditions()
    {
        nextTemporaryConditions.clear();
        permanentConditions.clear();
    }

    //   @SuppressWarnings("null")
    private synchronized S computeNextState()
    {
        List<TransitionRule<T, S>> ats = getApplicableTransitions();
        List<S> next = new ArrayList<>();

        //    TransitionRule<T, S> foundRule = null;
        for ( TransitionRule<T, S> tr : ats )
        {
            //doDebugLog( "règle applicable " + currentState + "->" + tr.getNextState() + " (compId " + tr.getComputationId() + ")" );

            if ( evalTransition( tr.getComputationId() ) )
                if ( !next.contains( tr.getNextState() ) )
                {
                    //    foundRule = tr;
                    //doDebugLog( "transition validée compId=" + tr.getComputationId() );
                    next.add( tr.getNextState() );

                    //                    if ( next.size() > 1 )
                    //                        doDebugLog( "non deterministic automaton detected" ); // TODO a virer
                }
        }

        switch ( next.size() )
        {
            case 0:
                //                model.removeAllConditions();
                return currentState;

            case 1:
                //                if ( !foundRule.getComputationId().isTrue() ) // TODO
                //                    removeAllConditions();

                //                for ( TransitionRule tr : ars )
                //                    for ( StateId stateId : next )
                //                    {
                //                        if ( tr.getNextState() == stateId )
                //                            doDebugLog( "application regle " + currentState + "->" + tr.getNextState() + " (compId " + tr.getComputationId() + ")" );
                //                    }
                return next.get( 0 );

            default:
                String n = "next states : " + next.toString();
                doDebugLog( n );
                doDebugLog( "current conds : " + condsToString() );
                for ( TransitionRule<T, S> tr : ats )
                    for ( StateId stateId : next )
                    {
                        if ( tr.getNextState() == stateId )
                            doDebugLog( "regle " + currentState + "->" + tr.getNextState() + " (compId " + tr.getComputationId() + ")" );
                    }
                throw new MVCModelError( "non deterministic automaton detected !" );
        }
    }

    private String condsToString()
    {
        return "ct " + currentTemporaryConditions + " nt " + nextTemporaryConditions + " p" + permanentConditions;
    }

    private void setCurrentState( Object sender, S state )
    {
        boolean changed = currentState != state;
        if ( changed )
        {
            doDebugLog( "state change : " + currentState + " -> " + state ); // + "  conds " + condsToString() );
            currentState = state;
            notifyObservers( new MVCModelChange( sender, this, AutomatonModelChangedId.AutomatonStateChanged, currentState ) );
        }
    }

    private synchronized void copyTemporaryConditions()
    {
        doDebugLog( "copy conds    " + condsToString() );
        nextTemporaryConditions.drainTo( currentTemporaryConditions );
        doDebugLog( "copy conds -> " + condsToString() );
    }

    @Override
    public void automatonStep( Object sender )
    {
        //copyTemporaryConditions();

        //        doDebugLog( "automatonStep()" );
        if ( initialState )
        {
            processState();
            initialState = false;
        }
        else
        {
            //            long start = System.nanoTime();
            prepareConditions();
            //            MainModel.printDur( "autom prep ms=", start, 0.3f );

            copyTemporaryConditions();

            //            start = System.nanoTime();
            //            StateId nextState = getNextState();
            S nextState = computeNextState();
            doDebugLog( "compute next state with conditions " + condsToString() + " -> " + nextState );

            setCurrentState( sender, nextState );
            //            MainModel.printDur( "autom nextstate ms=", start, 0.2f );

            //            long start = System.nanoTime();
            //doDebugLog( "processState()" );
            processState();
            //            MainModel.printDur( "autom procstate ms=", start, 0.2f );
        }

        // remove temporary conditions
        currentTemporaryConditions.clear();
    }

    private Condition getNextTemporaryCondition( ConditionId id )
    {
        for ( Condition c : nextTemporaryConditions )
            if ( c.equals( id ) )
                return c;
        return null;
    }

    @Override
    public synchronized void setTemporaryCondition( ConditionId id )
    {
        Condition c = getNextTemporaryCondition( id );
        if ( c == null )
        {
            c = new Condition( id, true, null );
            nextTemporaryConditions.add( c );
            doDebugLog( "setTemporaryCondition() : " + id + " -> " + condsToString() );
        }
    }

    @Override
    public synchronized void setTemporaryCondition( ConditionId id, Object data )
    {
        Condition c = getNextTemporaryCondition( id );
        if ( c == null )
        {
            c = new Condition( id, true, data );
            nextTemporaryConditions.add( c );
            doDebugLog( "setTemporaryCondition(d) : " + id + " -> " + condsToString() );
        }
    }

    private Condition getPermanentCondition( ConditionId id )
    {
        for ( Condition c : permanentConditions )
            if ( c.equals( id ) )
                return c;
        return null;
    }

    public boolean hasPermanentCondition( ConditionId id )
    {
        return getPermanentCondition( id ) != null;
    }

    @Override
    public synchronized void setPermanentCondition( ConditionId id )
    {
        Condition c = getPermanentCondition( id );
        if ( c == null )
        {
            c = new Condition( id, false, null );
            permanentConditions.add( c );
            doDebugLog( "setPermanentCondition : " + id + " -> " + condsToString() );
        }
    }

    @Override
    public synchronized void removePermanentCondition( ConditionId id )
    {
        Iterator<Condition> it = permanentConditions.iterator();
        while ( it.hasNext() )
        {
            Condition c = it.next();
            if ( c.getConditionId() == id && !c.isTemporary() )
            {
                it.remove();
                break;
            }
        }
    }

    @Override
    public S getCurrentState()
    {
        return currentState;
    }

    private void doDebugLog( String m )
    {
        if ( debugLog )
            debugLog( m );
    }

    //    private void doDebugLog( String m )
    //    {
    //        if ( debugLog != null )
    //            debugLog.log( "[Automaton/" + model.getCurrentState() + "] " + m );
    //    }

    protected abstract void debugLog( String m );

    //    protected void setDebugLog( DebugLog dl )
    //    {
    //        debugLog = dl;
    //        model.setDebugLog( dl );
    //    }

    protected abstract void prepareConditions();

    protected abstract void processState();

    protected abstract boolean evalTransition( T computationId );
}
