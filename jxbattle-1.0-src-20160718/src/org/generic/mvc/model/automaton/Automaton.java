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

interface Automaton<S extends StateId, C extends ConditionId>
{
    public S getCurrentState();

    /**
     * set temporary condition (evaluated at next state transition and then removed)
     */
    public void setTemporaryCondition( C id );

    /**
     * set temporary condition with data
     */
    public void setTemporaryCondition( ConditionId id, Object data );

    /**
     * set permanent condition (evaluated at next state transition and NOT removed)
     */
    public void setPermanentCondition( C id );

    /**
     * remove a permanent condition
     */
    public void removePermanentCondition( ConditionId id );

    public void automatonStep( Object sender );

    //  protected abstract boolean evalTransition( TransitionComputationId computationId );

    //public void removeAllConditions();
}