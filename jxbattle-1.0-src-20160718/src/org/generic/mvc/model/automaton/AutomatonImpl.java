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
import java.util.List;

import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;

public abstract class AutomatonImpl<S extends StateId, C extends ConditionId, T extends AutomatonTransitionId> extends MVCModelImpl implements Automaton<S, C>
{
    private AutomatonModel<S, T> model;

    /**
     * console log facility
     */
    //sprivate DebugLog debugLog;

    private boolean initialState; // initial state processing

    public AutomatonImpl( AutomatonModel<S, T> m )
    {
        model = m;
        initialState = true;
    }

    /**
     * list applicable rules to current state
     */
    private List<TransitionRule<T, S>> getApplicableRules()
    {
        List<TransitionRule<T, S>> res = new ArrayList<>();

        for ( TransitionRule<T, S> tr : model.getTransitionRules() )
        {
            //if ( tr.getActiveState() == currentState )
            if ( tr.isValidated( model.getCurrentState() ) )
                res.add( tr );
        }

        return res;
    }

    protected void addTransitionRule( TransitionRule<T, S> rule )
    {
        model.getTransitionRules().add( rule );
    }

    protected boolean hasCondition( ConditionId id )
    {
        return model.hasCondition( id );
    }

    @SuppressWarnings("null")
    private synchronized S getNextState()
    {
        List<TransitionRule<T, S>> ars = getApplicableRules();
        List<S> next = new ArrayList<>();

        TransitionRule<T, S> foundRule = null;
        for ( TransitionRule<T, S> tr : ars )
        {
            //debugLog( "regle applicable " + currentState + "->" + tr.getNextState() + " (compId " + tr.getComputationId() + ")" );

            if ( evalTransition( tr.getComputationId() ) )
                if ( !next.contains( tr.getNextState() ) )
                {
                    foundRule = tr;
                    //debugLog( "transition validée compId=" + tr.getComputationId() );
                    next.add( tr.getNextState() );

                    if ( next.size() > 1 )
                        debugLog( "non deterministic automaton detected" ); // TODO a virer
                }
        }

        switch ( next.size() )
        {
            case 0:
                //                model.removeAllConditions();
                return model.getCurrentState();

            case 1:
                if ( !foundRule.getComputationId().isTrue() )
                    model.removeAllConditions();
                //                for ( TransitionRule tr : ars )
                //                    for ( StateId stateId : next )
                //                    {
                //                        if ( tr.getNextState() == stateId )
                //                            debugLog( "application regle " + currentState + "->" + tr.getNextState() + " (compId " + tr.getComputationId() + ")" );
                //                    }
                return next.get( 0 );

            default:
                String n = "next states : " + next.toString();
                debugLog( n );
                debugLog( "current conds : " + model.getCurrentConditions().toString() );
                for ( TransitionRule<T, S> tr : ars )
                    for ( StateId stateId : next )
                    {
                        if ( tr.getNextState() == stateId )
                            debugLog( "regle " + model.getCurrentState() + "->" + tr.getNextState() + " (compId " + tr.getComputationId() + ")" );
                    }
                throw new MVCModelError( "non deterministic automaton detected !" );
        }
    }

    @Override
    public void automatonStep( Object sender )
    {
        debugLog( "automatonStep()" );
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

            //            start = System.nanoTime();
            debugLog( "compute next state with conditions " + model.getCurrentConditions() );
            //            StateId nextState = getNextState();
            S nextState = getNextState();

            model.setCurrentState( sender, nextState );
            //            MainModel.printDur( "autom nextstate ms=", start, 0.2f );

            //            long start = System.nanoTime();
            //debugLog( "processState()" );
            processState();
            //            MainModel.printDur( "autom procstate ms=", start, 0.2f );
        }
    }

    @Override
    public void setTemporaryCondition( ConditionId cond )
    {
        model.setCondition( cond );
    }

    protected void removeAllConditions()
    {
        model.removeAllConditions();
    }

    @Override
    public S getCurrentState()
    {
        return model.getCurrentState();
    }

    //    private void debugLog( String m )
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

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        super.addObserver( obs );
        model.addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        super.removeObserver( obs );
        model.removeObserver( obs );
    }
}
