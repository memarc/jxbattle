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

package jxbattle.bean.common.game;

import java.util.Iterator;
import java.util.Random;

import jxbattle.bean.common.parameters.game.WrappingMode;
import jxbattle.common.Consts;

/**
 * game board information
 */
public class BoardState implements Iterable<Cell>
{
    /**
     * cell array
     */
    private Cell[][] cells;

    /**
     * X dimension (cell count)
     */
    private int xCellCount;

    /**
     * Y dimension (cell count)
     */
    private int yCellCount;

    public BoardState( int dimx, int dimy )
    {
        xCellCount = dimx;
        yCellCount = dimy;
        allocateCells();
    }

    private void allocateCells()
    {
        cells = new Cell[ xCellCount ][ yCellCount ];

        for ( int y = 0; y < yCellCount; y++ )
            for ( int x = 0; x < xCellCount; x++ )
                cells[ x ][ y ] = new Cell( Consts.maxCellSideCount );
    }

    /**
     * cell neighbours initialisation
     * @param cellSideCount number of side of a cell. 4 = square, 6=hexagon (not supported)
     * @param wrap map is wrapped (right of right is first left, up of first line is last line, etc...) 
     */
    public void initNeighbours( int cellSideCount, WrappingMode wrap )
    {
        int x = 0, y;
        for ( Cell[] cols : cells )
        {
            y = 0;
            for ( Cell cell : cols )
            {
                cell.gridX = x;
                cell.gridY = y;
                CellMove[] cm = cell.cellState.moves;

                // neighbours

                for ( int dir = 0; dir < cellSideCount; dir++ )
                {
                    cm[ dir ].dir = dir; // lots of "dir" :-)
                    cm[ dir ].revDir = (dir + 2) % 4;

                    switch ( dir )
                    {
                        case 0: // up
                            if ( y > 0 )
                                cm[ dir ].targetCell = cells[ x ][ y - 1 ];
                            else
                            {
                                switch ( wrap )
                                {
                                    case FULL:
                                    case TOP_DOWN:
                                        cm[ dir ].targetCell = cells[ x ][ yCellCount - 1 ];
                                        break;

                                    default:
                                        break;
                                }
                            }
                            break;

                        case 1: // right
                            if ( x < xCellCount - 1 )
                                cm[ dir ].targetCell = cells[ x + 1 ][ y ];
                            else
                            {
                                switch ( wrap )
                                {
                                    case FULL:
                                    case LEFT_RIGHT:
                                        cm[ dir ].targetCell = cells[ 0 ][ y ];
                                        break;

                                    default:
                                        break;
                                }
                            }

                            break;

                        case 2: // down
                            if ( y < yCellCount - 1 )
                                cm[ dir ].targetCell = cells[ x ][ y + 1 ];
                            else
                            {
                                switch ( wrap )
                                {
                                    case FULL:
                                    case TOP_DOWN:
                                        cm[ dir ].targetCell = cells[ x ][ 0 ];
                                        break;

                                    default:
                                        break;
                                }
                            }
                            break;

                        case 3: // left
                            if ( x > 0 )
                                cm[ dir ].targetCell = cells[ x - 1 ][ y ];
                            else
                            {
                                switch ( wrap )
                                {
                                    case FULL:
                                    case LEFT_RIGHT:
                                        cm[ dir ].targetCell = cells[ xCellCount - 1 ][ y ];
                                        break;

                                    default:
                                        break;
                                }
                            }
                            break;
                    }
                }
                y++;
            }
            x++;
        }
    }

    /**
     * get cell from its coordinates
     */
    public Cell getCell( int cx, int cy )
    {
        return cells[ cx ][ cy ];
    }

    public int getBoardXDim()
    {
        return xCellCount;
    }

    public int getBoardYDim()
    {
        return yCellCount;
    }

    public void test()
    {
        Random rnd = new Random( 6516513981L );

        for ( int y = 0; y < yCellCount; y++ )
            for ( int x = 0; x < xCellCount; x++ )
            {
                CellState cs = cells[ x ][ y ].cellState;
                if ( (x % 3) == 0 )
                {
                    cs.playerId = 0;
                    cs.troopsLevel[ 0 ] = Consts.maxTroopsLevel / 100 + rnd.nextFloat() * 10.0f;
                    cs.moves[ 1 ].dir = 1;
                    cs.moves[ 1 ].isSet = true;
                }
                else if ( (x % 3) == 1 )
                {
                    cs.playerId = 1;
                    cs.troopsLevel[ 1 ] = Consts.maxTroopsLevel / 100 + rnd.nextFloat();
                }
                else if ( (x % 3) == 2 )
                {
                    cs.playerId = 1;
                    cs.troopsLevel[ 1 ] = Consts.maxTroopsLevel / 100 + rnd.nextFloat() * 10.0f;
                    cs.moves[ 3 ].dir = 3;
                    cs.moves[ 3 ].isSet = true;
                }
            }
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof BoardState )
        {
            int dimx = cells.length;
            int dimy = cells[ 0 ].length;

            BoardState bs = (BoardState)obj;

            for ( int y = 0; y < dimy; y++ )
                for ( int x = 0; x < dimx; x++ )
                {
                    Cell c1 = cells[ x ][ y ];
                    Cell c2 = bs.cells[ x ][ y ];

                    if ( !c1.cellState.equals( c2.cellState ) )
                    {
                        System.out.println( "BoardState.equals false at " + x + " " + y );
                        return false;
                    }
                }

            return true;
        }

        return false;
    }

    public class CellIterator implements Iterator<Cell>
    {
        private int x;

        private int y;

        private final int lastCol = xCellCount - 1;

        private final int lastLine = yCellCount - 1;

        private Cell[] currentCol;

        public CellIterator()
        {
            x = 0;
            y = -1;
            currentCol = cells[ 0 ];
        }

        @Override
        public boolean hasNext()
        {
            if ( x < lastCol )
                return true;

            return y < lastLine;
        }

        @Override
        public Cell next()
        {
            y++;
            if ( y > lastLine )
            {
                y = 0;
                x++;
                currentCol = cells[ x ];
            }

            Cell res = currentCol[ y ];

            return res;
        }

        @Override
        public void remove()
        {
        }

        public boolean isLastLine()
        {
            return y == lastLine;
        }
    }

    // Iterable interface

    @Override
    public CellIterator iterator()
    {
        return new CellIterator();
    }
}
