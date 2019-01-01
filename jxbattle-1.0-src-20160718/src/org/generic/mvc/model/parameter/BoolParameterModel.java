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

package org.generic.mvc.model.parameter;

import org.generic.bean.parameter.BoolParameter;
import org.generic.mvc.model.IBoolModel;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public class BoolParameterModel extends MVCModelImpl implements IBoolModel
{
    // observed beans

    private BoolParameter boolParameter;

    // models

    public BoolParameterModel( BoolParameter v )
    {
        boolParameter = v;
    }

    public BoolParameter getBoolParameter()
    {
        return boolParameter;
    }

    public void setBoolParameter( Object sender, BoolParameter bp )
    {
        boolean changed = (getBoolParameter() == null && bp != null) || (getBoolParameter().getValue() != bp.getValue());
        boolParameter = bp;
        if ( changed )
            notifyObservers( new MVCModelChange( sender, this, ParameterModelChangeId.BoolParameterChanged ) );
    }

    @Override
    public boolean getValue()
    {
        return getBoolParameter().getValue();
    }

    @Override
    public void setValue( Object sender, boolean v )
    {
        boolean changed = getBoolParameter().getValue() != v;
        if ( changed )
        {
            getBoolParameter().setValue( v );
            notifyObservers( new MVCModelChange( sender, this, ParameterModelChangeId.BoolParameterChanged ) );
        }
    }

    public void resetToDefault( Object sender )
    {
        setValue( sender, getDefaultValue() );
    }

    public boolean getDefaultValue()
    {
        return getBoolParameter().getDefaultValue();
    }
}
