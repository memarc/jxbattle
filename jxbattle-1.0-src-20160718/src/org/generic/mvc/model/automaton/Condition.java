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

public class Condition
{
    /**
     * condition id 
     */
    private ConditionId conditionId;

    /**
     * condition is temporary, ie. removed from condition list after state change
     */
    private boolean temporary;

    /**
     * user data
     */
    private Object data;

    /**
     * creation time 
     */
    private long creationTime;

    //    Condition( ConditionId id )
    //    {
    //        conditionId = id;
    //        temporary = true;
    //        data = null;
    //        creationTime = System.currentTimeMillis();
    //    }

    Condition( ConditionId id, boolean temp, Object d )
    {
        conditionId = id;
        temporary = temp;
        data = d;
        creationTime = System.currentTimeMillis();
    }

    public ConditionId getConditionId()
    {
        return conditionId;
    }

    public boolean isTemporary()
    {
        return temporary;
    }

    public Object getData()
    {
        return data;
    }

    public long getAge()
    {
        return System.currentTimeMillis() - creationTime;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((conditionId == null) ? 0 : conditionId.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;

        if ( getClass() != obj.getClass() )
            return false;

        Condition other = (Condition)obj;
        if ( conditionId == null )
        {
            if ( other.conditionId != null )
                return false;
        }
        else if ( !conditionId.equals( other.conditionId ) )
            return false;

        return true;
    }

    public boolean equals( ConditionId cid )
    {
        return conditionId.equals( cid );
    }

    @Override
    public String toString()
    {
        return conditionId.toString();
    }
}
