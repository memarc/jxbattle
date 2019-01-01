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

package jxbattle.gui.base.client.boardpanel;

import java.awt.Graphics;

import jxbattle.bean.common.game.Cell;
import jxbattle.bean.common.game.CellState;
import jxbattle.bean.common.game.UserCommand.UserCommandCode;
import jxbattle.bean.common.player.XBColor;

import org.generic.mvc.model.observer.MVCModelObserver;

public interface BoardPanelRenderer extends MVCModelObserver
{
    /**
     * set board model
     */
    public void setModel( BoardPanelModel model );

    /**
     * render board grid
     * @param g graphic context
     * @param gridWidth horizontal cell count
     * @param gridHeight vertical cell count
     * @param cellSize pixel cell size (width/height)
     */
    public void renderGrid( Graphics g, int gridWidth, int gridHeight, int cellSize );

    /**
     * render sea cell, ie. with elevation < 0 
     * @param g graphic context
     * @param cellState cell state information
     */
    public void renderSeaCell( Graphics g, CellState cellState );

    /**
     * render land cell background, ie. with elevation >= 0 
     * @param g graphic context
     * @param cellState cell state information
     */
    public void renderLandBackground( Graphics g, CellState cellState );

    /**
     * render invisible cell, ie. opponent cell not seen by player 
     * @param g graphic context
     * @param cellState cell state information
     */
    public void renderInvisibleCell( Graphics g, CellState cellState );

    /**
     * render base/town cell, ie. troop generator 
     * @param g graphic context
     * @param cellState cell state information
     */
    public void renderBase( Graphics g, CellState cellState );

    /**
     * render player
     * @param g graphic context
     * @param cellState cell state information
     * @param playerId player id
     */
    public void renderPlayer( Graphics g, CellState cellState, int playerId );

    /**
     * render normal player move, ie. not fighting player 
     * @param g graphic context
     * @param cellState cell state information
     * @param direction move direction. For square topology, 0=up, 1=right, 2=down, 3=left
     */
    public void renderPlayerMove( Graphics g, CellState cellState, int direction );

    /**
     * render fighting players
     * @param g graphic context
     * @param cellState cell state information
     */
    public void renderFight( Graphics g, CellState cellState );

    /**
     * render fighting player move 
     * @param g graphic context
     * @param direction move direction. For square topology, 0=up, 1=right, 2=down, 3=left
     * @param leadPlayerId id of biggest player amongst all fighting players in the cell
     */
    public void renderFightMove( Graphics g, int direction, int leadPlayerId );

    /**
     * render march command
     * @param g graphic contextS
     * @param cellState cell information
     */
    public void renderMarch( Graphics g, Cell cell );

    /**
     * render paratroops
     * @param g graphic context
     * @param paratroopsId player id of incoming parachute troops
     */
    public void renderParaTroops( Graphics g, CellState cellState );

    /**
     * render gun shell impact
     * @param g graphic context
     * @param guntroopsId player id of incoming shell
     */
    public void renderGunTroops( Graphics g, CellState cellState );

    /**
     * render guns/para trajectory
     * @param sourceCellX source cell grid X coordinate
     * @param sourceCellY source cell grid Y coordinate
     * @param destCellX destination cell grid X coordinate
     * @param destCellY destination cell grid Y coordinate
     * @param isValid true if path is valid
     */
    public void renderGunParaPath( Graphics g, int sourceCellX, int sourceCellY, int destCellX, int destCellY, boolean isValid );

    /**
     * render managed command
     * @param g graphic context
     * @param command managed command
     * @param xbColor 
     */
    public void renderManagedCommand( Graphics g, UserCommandCode command, XBColor playerColor );
}
