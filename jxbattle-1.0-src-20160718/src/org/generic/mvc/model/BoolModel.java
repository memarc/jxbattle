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

package org.generic.mvc.model;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelChangeId;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.parameter.ParameterModelChangeId;

public class BoolModel extends MVCModelImpl implements IBoolModel
{
    private MVCModelChangeId changeId;

    // observed value

    private boolean value;

    public BoolModel( boolean v )
    {
        this( v, ParameterModelChangeId.BoolParameterChanged );
    }

    public BoolModel( boolean v, MVCModelChangeId id )
    {
        value = v;
        changeId = id;
    }

    @Override
    public boolean getValue()
    {
        return value;
    }

    @Override
    public void setValue( Object sender, boolean v )
    {
        boolean changed = value != v;
        if ( changed )
        {
            value = v;
            notifyObservers( new MVCModelChange( sender, this, changeId ) );
        }
    }
}
