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

public class TransitionRule<T extends AutomatonTransitionId, S extends StateId>
{
    private S activeState;

    private S nextState;

    //private ConditionId[] conditions;
    private T computationId;

    //    public TransitionRule( StateId active, StateId next, ConditionId[] conds )
    //    {
    //        activeState = active;
    //        nextState = next;
    //        conditions = conds;
    //    }

    public TransitionRule( S active, S next, T compId )
    {
        activeState = active;
        nextState = next;
        computationId = compId;
    }

    //    public StateId getActiveState()
    //    {
    //        return activeState;
    //    }

    boolean isValidated( S stateId )
    {
        return activeState == stateId;
    }

    public S getNextState()
    {
        return nextState;
    }

    public T getComputationId()
    {
        return computationId;
    }

    @Override
    public String toString()
    {
        return activeState + "-[" + computationId + "]->" + nextState;
    }

    //    public ConditionId[] getConditions()
    //    {
    //        return conditions;
    //    }

}
