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

package jxbattle.model.common.parameters.game;

import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.parameter.EnumParameterModel;
import org.generic.mvc.model.parameter.IntParameterModel;

import jxbattle.bean.common.parameters.game.BoardParameters;
import jxbattle.bean.common.parameters.game.WrappingMode;

public class BoardParametersModel extends MVCModelImpl
{
    // observed beans

    private BoardParameters boardParameters;

    // models

    private IntParameterModel xCellCountModel;

    private IntParameterModel yCellCountModel;

    private EnumParameterModel<WrappingMode> wrappingModeModel;

    //private BoolParameterModel drawGridModel;

    BoardParametersModel( BoardParameters bp )
    {
        boardParameters = bp;
    }

    private BoardParameters getBoardParameters()
    {
        return boardParameters;
    }

    public void setBoardParameters( Object sender, BoardParameters bp )
    {
        boardParameters = bp;

        getXCellCountModel().setIntParameter( sender, boardParameters.getXCellCount() );
        getYCellCountModel().setIntParameter( sender, boardParameters.getYCellCount() );
        getWrappingModeModel().setEnumParameter( sender, boardParameters.getWrappingMode() );
    }

    public IntParameterModel getXCellCountModel()
    {
        if ( xCellCountModel == null )
            xCellCountModel = new IntParameterModel( getBoardParameters().getXCellCount() );
        return xCellCountModel;
    }

    public IntParameterModel getYCellCountModel()
    {
        if ( yCellCountModel == null )
            yCellCountModel = new IntParameterModel( getBoardParameters().getYCellCount() );
        return yCellCountModel;
    }

    public EnumParameterModel<WrappingMode> getWrappingModeModel()
    {
        if ( wrappingModeModel == null )
            wrappingModeModel = new EnumParameterModel<>( getBoardParameters().getWrappingMode() );
        return wrappingModeModel;
    }
}
