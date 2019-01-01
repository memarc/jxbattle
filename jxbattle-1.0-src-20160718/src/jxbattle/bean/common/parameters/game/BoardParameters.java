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

package jxbattle.bean.common.parameters.game;

import org.generic.bean.parameter.EnumParameter;
import org.generic.bean.parameter.IntParameter;

/**
 * parameters of game board
 */
public class BoardParameters implements Cloneable
{
    /**
     * cell topology (square, hexa, ...)
     * currently only square shape is supported
     */
    private CellTopology cellTopology = CellTopology.Square;

    /**
     * number of cells in X direction
     */
    private IntParameter XCellCount;

    /**
     * number of cells in Y direction
     */
    private IntParameter YCellCount;

    /**
     * "pacman" mode
     */
    private EnumParameter<WrappingMode> wrappingMode;

    /**
     * draw grid
     */
    //private BoolParameter drawGrid;

    public BoardParameters()
    {
        XCellCount = new IntParameter( 1, 150, 20 );
        YCellCount = new IntParameter( 1, 100, 10 );
        wrappingMode = new EnumParameter<WrappingMode>( WrappingMode.NONE );
        //drawGrid = new BoolParameter( true, true );
    }

    public BoardParameters( BoardParameters bp )
    {
        cellTopology = bp.cellTopology;
        XCellCount = new IntParameter( bp.XCellCount );
        YCellCount = new IntParameter( bp.YCellCount );
        wrappingMode = new EnumParameter<WrappingMode>( bp.wrappingMode );
    }

    public IntParameter getXCellCount()
    {
        return XCellCount;
    }

    public IntParameter getYCellCount()
    {
        return YCellCount;
    }

    public CellTopology getCellTopology()
    {
        return cellTopology;
    }

    public void setCellTopology( CellTopology topology )
    {
        cellTopology = topology;
    }

    public EnumParameter<WrappingMode> getWrappingMode()
    {
        return wrappingMode;
    }

    //    public BoolParameter getDrawGrid()
    //    {
    //        return drawGrid;
    //    }

    @Override
    public BoardParameters clone()
    {
        //        try
        //        {
        //            BoardParameters res = (BoardParameters)super.clone();
        //
        //            res.cellTopology = cellTopology;
        //            res.XCellCount = XCellCount.clone();
        //            res.YCellCount = YCellCount.clone();
        //            //res.drawGrid = drawGrid.clone();
        //
        //            return res;
        //        }
        //        catch( CloneNotSupportedException e )
        //        {
        //            e.printStackTrace();
        //            return null;
        //        }
        return new BoardParameters( this );
    }
}
