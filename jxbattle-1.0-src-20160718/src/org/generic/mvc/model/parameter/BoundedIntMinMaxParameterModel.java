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

import org.generic.bean.parameter.BoundedIntMinMaxParameter;
import org.generic.mvc.model.observer.MVCModelChangeId;
import org.generic.mvc.model.observer.MVCModelImpl;

public class BoundedIntMinMaxParameterModel extends MVCModelImpl
{
    private BoundedIntMinMaxParameter minMaxParameter;

    private IntParameterModel minParameterModel;

    private IntParameterModel maxParameterModel;

    //    public BoundedIntMinMaxParameterModel( int min, int max )
    //    {
    //        minMaxParameter = new BoundedIntMinMaxParameter( min, max );
    //    }

    public BoundedIntMinMaxParameterModel( BoundedIntMinMaxParameter mms )
    {
        minMaxParameter = mms;
        initRelatedModels();
    }

    private void initRelatedModels()
    {
        addRelatedModel( getMinIntParameterModel() );
        addRelatedModel( getMaxIntParameterModel() );
    }

    //    public int getMinValue()
    //    {
    //        return minMaxParameter.getMinValue();
    //    }
    //
    //    public int getMaxValue()
    //    {
    //        return minMaxParameter.getMaxValue();
    //    }
    //
    //    public void setMinValue( Object sender, int v )
    //    {
    //        boolean changed = v != minMaxParameter.getMinValue();
    //        if ( changed )
    //        {
    //            minMaxParameter.setMinValue( v );
    //            notifyMinMaxChanged( sender );
    //        }
    //    }
    //
    //    public void setMaxValue( Object sender, int v )
    //    {
    //        boolean changed = v != minMaxParameter.getMaxValue();
    //        if ( changed )
    //        {
    //            minMaxParameter.setMaxValue( v );
    //            notifyMinMaxChanged( sender );
    //        }
    //    }
    //
    //    public void setMinMaxValue( Object sender, int min, int max )
    //    {
    //        boolean changed = min != minMaxParameter.getMinValue() || max != minMaxParameter.getMaxValue();
    //        if ( changed )
    //        {
    //            minMaxParameter.setMinValue( min );
    //            minMaxParameter.setMaxValue( max );
    //            notifyMinMaxChanged( sender );
    //        }
    //    }
    //
    //    private void notifyMinMaxChanged( Object sender )
    //    {
    //        MVCModelChange change = new MVCModelChange( sender, this, ParameterModelChangeId.IntMinMaxParameterChanged );
    //        notifyObservers( change );
    //    }

    public IntParameterModel getMinIntParameterModel()
    {
        if ( minParameterModel == null )
            minParameterModel = new IntParameterModel( minMaxParameter.getMinParameter() )
            {
                @Override
                protected MVCModelChangeId getValueChangeId()
                {
                    return ParameterModelChangeId.BoundedIntMinMaxParameterChanged;
                }
            };
        return minParameterModel;
    }

    public IntParameterModel getMaxIntParameterModel()
    {
        if ( maxParameterModel == null )
            maxParameterModel = new IntParameterModel( minMaxParameter.getMaxParameter() )
            {
                @Override
                protected MVCModelChangeId getValueChangeId()
                {
                    return ParameterModelChangeId.BoundedIntMinMaxParameterChanged;
                }

            };
        return maxParameterModel;
    }
}
