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

package jxbattle.gui.composite.client.gameframe;

import java.awt.event.KeyEvent;

import jxbattle.bean.client.input.XBInputInfo;
import jxbattle.bean.client.input.XBInputInfoMapping;
import jxbattle.bean.client.input.XBKeyEvent;
import jxbattle.bean.client.input.XBKeyInfo;
import jxbattle.bean.common.game.CellState;
import jxbattle.bean.common.game.UserCommand.UserCommandCode;
import jxbattle.bean.common.parameters.game.InvisibilityMode;
import jxbattle.bean.common.player.XBColor;
import jxbattle.gui.base.client.boardpanel.BoardPanelModel;
import jxbattle.model.client.GameEngine;
import jxbattle.model.client.PlayerStatisticsModel;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.game.PlayerInfos.SelectedPlayerIterator;

import org.generic.mvc.model.observer.MVCModelImpl;

public class GameFrameModel extends MVCModelImpl
{
    private GameEngine gameEngine;

    private BoardPanelModel boardPanelModel;

    private XBInputInfoMapping inputInfoMapping;

    public GameFrameModel( GameEngine ge )
    {
        gameEngine = ge;
    }

    GameEngine getGameEngine()
    {
        return gameEngine;
    }

    private XBInputInfoMapping getInputInfoMapping()
    {
        if ( inputInfoMapping == null )
        {
            inputInfoMapping = new XBInputInfoMapping();
            XBKeyInfo ki;

            // directions arrows

            ki = new XBKeyInfo( KeyEvent.VK_UP, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SWAP_VECTOR_U );

            ki = new XBKeyInfo( KeyEvent.VK_DOWN, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SWAP_VECTOR_D );

            ki = new XBKeyInfo( KeyEvent.VK_LEFT, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SWAP_VECTOR_L );

            ki = new XBKeyInfo( KeyEvent.VK_RIGHT, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SWAP_VECTOR_R );

            // directions yiuhjkn,;

            ki = new XBKeyInfo( KeyEvent.VK_Y, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_UL );

            ki = new XBKeyInfo( KeyEvent.VK_U, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_U );

            ki = new XBKeyInfo( KeyEvent.VK_I, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_UR );

            ki = new XBKeyInfo( KeyEvent.VK_K, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_R );

            ki = new XBKeyInfo( KeyEvent.VK_SEMICOLON, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_DR );

            ki = new XBKeyInfo( KeyEvent.VK_COMMA, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_D );

            ki = new XBKeyInfo( KeyEvent.VK_N, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_DL );

            ki = new XBKeyInfo( KeyEvent.VK_H, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_L );

            ki = new XBKeyInfo( KeyEvent.VK_J, false, false );
            inputInfoMapping.add( ki, UserCommandCode.CANCEL_VECTORS );

            // directions numeric pad

            ki = new XBKeyInfo( KeyEvent.VK_NUMPAD7, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_UL );

            ki = new XBKeyInfo( KeyEvent.VK_NUMPAD8, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_U );

            ki = new XBKeyInfo( KeyEvent.VK_NUMPAD9, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_UR );

            ki = new XBKeyInfo( KeyEvent.VK_NUMPAD6, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_R );

            ki = new XBKeyInfo( KeyEvent.VK_NUMPAD3, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_DR );

            ki = new XBKeyInfo( KeyEvent.VK_NUMPAD2, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_D );

            ki = new XBKeyInfo( KeyEvent.VK_NUMPAD1, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_DL );

            ki = new XBKeyInfo( KeyEvent.VK_NUMPAD4, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_L );

            ki = new XBKeyInfo( KeyEvent.VK_NUMPAD5, false, false );
            inputInfoMapping.add( ki, UserCommandCode.CANCEL_VECTORS );

            // directions zqsd 

            ki = new XBKeyInfo( KeyEvent.VK_Z, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_U );

            ki = new XBKeyInfo( KeyEvent.VK_Q, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_L );

            ki = new XBKeyInfo( KeyEvent.VK_S, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_D );

            ki = new XBKeyInfo( KeyEvent.VK_D, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SET_VECTOR_R );

            ki = new XBKeyInfo( KeyEvent.VK_ENTER, false, false );
            inputInfoMapping.add( ki, UserCommandCode.REPEAT_LAST_COMMAND );

            // ground fill/dig, build/destroy bases...

            ki = new XBKeyInfo( KeyEvent.VK_F, false, false );
            inputInfoMapping.add( ki, UserCommandCode.FILL_GROUND );

            ki = new XBKeyInfo( KeyEvent.VK_F, false, true );
            inputInfoMapping.add( ki, UserCommandCode.MANAGED_FILL_GROUND );

            ki = new XBKeyInfo( KeyEvent.VK_V, false, false );
            inputInfoMapping.add( ki, UserCommandCode.DIG_GROUND );

            ki = new XBKeyInfo( KeyEvent.VK_V, false, true );
            inputInfoMapping.add( ki, UserCommandCode.MANAGED_DIG_GROUND );

            ki = new XBKeyInfo( KeyEvent.VK_B, false, false );
            inputInfoMapping.add( ki, UserCommandCode.BUILD_BASE );

            ki = new XBKeyInfo( KeyEvent.VK_B, false, true );
            inputInfoMapping.add( ki, UserCommandCode.MANAGED_BUILD_BASE );

            ki = new XBKeyInfo( KeyEvent.VK_X, false, false );
            inputInfoMapping.add( ki, UserCommandCode.SCUTTLE_BASE );

            ki = new XBKeyInfo( KeyEvent.VK_X, false, true );
            inputInfoMapping.add( ki, UserCommandCode.MANAGED_SCUTTLE_BASE );

            // reserve

            ki = new XBKeyInfo( KeyEvent.VK_F1, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_RESERVE_L1 );

            ki = new XBKeyInfo( KeyEvent.VK_F2, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_RESERVE_L2 );

            ki = new XBKeyInfo( KeyEvent.VK_F3, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_RESERVE_L3 );

            ki = new XBKeyInfo( KeyEvent.VK_F4, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_RESERVE_L4 );

            ki = new XBKeyInfo( KeyEvent.VK_F5, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_RESERVE_L5 );

            ki = new XBKeyInfo( KeyEvent.VK_F6, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_RESERVE_L6 );

            ki = new XBKeyInfo( KeyEvent.VK_F7, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_RESERVE_L7 );

            ki = new XBKeyInfo( KeyEvent.VK_F8, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_RESERVE_L8 );

            ki = new XBKeyInfo( KeyEvent.VK_F9, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_RESERVE_L9 );

            ki = new XBKeyInfo( KeyEvent.VK_F10, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_RESERVE_L10 );

            // attack, troops, ...

            ki = new XBKeyInfo( KeyEvent.VK_A, false, false );
            inputInfoMapping.add( ki, UserCommandCode.ATTACK_CELL );

            ki = new XBKeyInfo( KeyEvent.VK_G, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_GUN );

            ki = new XBKeyInfo( KeyEvent.VK_G, false, true );
            inputInfoMapping.add( ki, UserCommandCode.MANAGED_TROOPS_GUN );

            ki = new XBKeyInfo( KeyEvent.VK_P, false, false );
            inputInfoMapping.add( ki, UserCommandCode.TROOPS_PARA );

            ki = new XBKeyInfo( KeyEvent.VK_P, false, true );
            inputInfoMapping.add( ki, UserCommandCode.MANAGED_TROOPS_PARA );

            ki = new XBKeyInfo( KeyEvent.VK_C, false, false );
            inputInfoMapping.add( ki, UserCommandCode.CANCEL_MANAGE );

            // reserve
        }
        return inputInfoMapping;
    }

    UserCommandCode mapInputInfo( XBInputInfo ii )
    {
        return inputInfoMapping.get( ii );
    }

    BoardPanelModel getBoardPanelModel()
    {
        if ( boardPanelModel == null )
        {
            boardPanelModel = new BoardPanelModel( gameEngine, getInputInfoMapping() );
        }
        return boardPanelModel;
    }

    private void issueCellUserCommand( UserCommandCode ucc )
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        gameEngine.issueCellUserCommand( ucc, cx, cy );
    }

    void processKey( XBKeyEvent keyEvent )
    {
        UserCommandCode ucc = mapInputInfo( keyEvent );

        if ( ucc != null )
            if ( keyEvent.isPressedOrReleased() )
                switch ( ucc )
                {
                /**
                 * swap one vector, keep others
                 */
                    case SWAP_VECTOR_U:
                    case SWAP_VECTOR_R:
                    case SWAP_VECTOR_D:
                    case SWAP_VECTOR_L:

                    case CANCEL_VECTORS:

                        /**
                         * set one or more vectors and cancel others
                         */
                    case SET_VECTOR_UL:
                    case SET_VECTOR_U:
                    case SET_VECTOR_UR:
                    case SET_VECTOR_R:
                    case SET_VECTOR_DR:
                    case SET_VECTOR_D:
                    case SET_VECTOR_DL:
                    case SET_VECTOR_L:

                    case TROOPS_RESERVE_L1:
                    case TROOPS_RESERVE_L2:
                    case TROOPS_RESERVE_L3:
                    case TROOPS_RESERVE_L4:
                    case TROOPS_RESERVE_L5:
                    case TROOPS_RESERVE_L6:
                    case TROOPS_RESERVE_L7:
                    case TROOPS_RESERVE_L8:
                    case TROOPS_RESERVE_L9:
                    case TROOPS_RESERVE_L10:

                    case ATTACK_CELL: // attack cell with surrounding ones

                    case CANCEL_MANAGE:
                        issueCellUserCommand( ucc );
                        break;

                    case REPEAT_LAST_COMMAND:
                        repeatLastCommand();
                        break;

                    case FILL_GROUND:
                        fillGround( false );
                        break;

                    case MANAGED_FILL_GROUND:
                        fillGround( true );
                        break;

                    case DIG_GROUND:
                        digGround( false );
                        break;

                    case MANAGED_DIG_GROUND:
                        digGround( true );
                        break;

                    case BUILD_BASE:
                        buildBase( false );
                        break;

                    case MANAGED_BUILD_BASE:
                        buildBase( true );
                        break;

                    case SCUTTLE_BASE:
                        scuttleBase( false );
                        break;

                    case MANAGED_SCUTTLE_BASE:
                        scuttleBase( true );
                        break;

                    //                case TROOPS_RESERVE_L1:
                    //                    troopsReserve( 0 );
                    //                    break;
                    //
                    //                case TROOPS_RESERVE_L2:
                    //                    troopsReserve( 1 );
                    //                    break;
                    //
                    //                case TROOPS_RESERVE_L3:
                    //                    troopsReserve( 2 );
                    //                    break;
                    //
                    //                case TROOPS_RESERVE_L4:
                    //                    troopsReserve( 3 );
                    //                    break;
                    //
                    //                case TROOPS_RESERVE_L5:
                    //                    troopsReserve( 4 );
                    //                    break;
                    //
                    //                case TROOPS_RESERVE_L6:
                    //                    troopsReserve( 5 );
                    //                    break;
                    //
                    //                case TROOPS_RESERVE_L7:
                    //                    troopsReserve( 6 );
                    //                    break;
                    //
                    //                case TROOPS_RESERVE_L8:
                    //                    troopsReserve( 7 );
                    //                    break;
                    //
                    //                case TROOPS_RESERVE_L9:
                    //                    troopsReserve( 8 );
                    //                    break;
                    //
                    //                case TROOPS_RESERVE_L10:
                    //                    troopsReserve( 9 );
                    //                    break;

                    //                case ATTACK_CELL:
                    //                    cellAttack();
                    //                    break;

                    //                case CANCEL_MANAGE:
                    //                    cancelManagement();
                    //                    break;

                    default:
                        break;
                }
            else
                // key released
                switch ( ucc )
                {
                    case TROOPS_PARA:
                        paraTroops( false );
                        break;

                    case MANAGED_TROOPS_PARA:
                        paraTroops( true );
                        break;

                    case TROOPS_GUN:
                        gunTroops( false );
                        break;

                    case MANAGED_TROOPS_GUN:
                        gunTroops( true );
                        break;

                    default:
                        break;
                }
    }

    void repeatLastCommand()
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        gameEngine.repeatLastUserCommand( cx, cy );
    }

    void watchGame( Object sender )
    {
        gameEngine.getGameModel().watchGame( sender );
    }

    void abortGame( Object sender )
    {
        gameEngine.getGameModel().abortGame( sender );
    }

    boolean allowWatch()
    {
        return gameEngine.allowWatch();
    }

    /*
    cell zones
    
    +-------------------+
    |     \   3   /     |
    |   2  \     /  4   |
    |\__    \___/    __/|
    |   \__ /   \ __/   |
    | 1    |  0  |   5  |
    |    __|     |__    |
    | __/   \___/   \__ |
    |/      /   \      \|
    |  8   /     \  6   |
    |     /   7   \     |
    +-------------------+
    */

    void swapVectors( int zone )
    {
        //        int cx = boardPanelModel.getCellGridXFromMouse();
        //        int cy = boardPanelModel.getCellGridYFromMouse();
        //gameEngine.swapVectorsUserCommand( cx, cy, zone );

        switch ( zone )
        {
            case 1:
                issueCellUserCommand( UserCommandCode.SWAP_VECTOR_L );
                break;

            case 2:
                issueCellUserCommand( UserCommandCode.SWAP_VECTOR_UL );
                break;

            case 3:
                issueCellUserCommand( UserCommandCode.SWAP_VECTOR_U );
                break;

            case 4:
                issueCellUserCommand( UserCommandCode.SWAP_VECTOR_UR );
                break;

            case 5:
                issueCellUserCommand( UserCommandCode.SWAP_VECTOR_R );
                break;

            case 6:
                issueCellUserCommand( UserCommandCode.SWAP_VECTOR_DR );
                break;

            case 7:
                issueCellUserCommand( UserCommandCode.SWAP_VECTOR_D );
                break;

            case 8:
                issueCellUserCommand( UserCommandCode.SWAP_VECTOR_DL );
                break;
        }
    }

    void cancelAllVectors()
    {
        //        int cx = boardPanelModel.getCellGridXFromMouse();
        //        int cy = boardPanelModel.getCellGridYFromMouse();
        //        gameEngine.cancelAllVectorsUserCommand( cx, cy );

        issueCellUserCommand( UserCommandCode.CANCEL_VECTORS );
    }

    /**
     * set one or more vectors and cancel others
     */
    void setVectors( int zone )
    {
        //        int cx = boardPanelModel.getCellGridXFromMouse();
        //        int cy = boardPanelModel.getCellGridYFromMouse();
        //        gameEngine.setVectorsUserCommand( cx, cy, zone );

        switch ( zone )
        {
            case 1:
                issueCellUserCommand( UserCommandCode.SET_VECTOR_L );
                break;

            case 2:
                issueCellUserCommand( UserCommandCode.SET_VECTOR_UL );
                break;

            case 3:
                issueCellUserCommand( UserCommandCode.SET_VECTOR_U );
                break;

            case 4:
                issueCellUserCommand( UserCommandCode.SET_VECTOR_UR );
                break;

            case 5:
                issueCellUserCommand( UserCommandCode.SET_VECTOR_R );
                break;

            case 6:
                issueCellUserCommand( UserCommandCode.SET_VECTOR_DR );
                break;

            case 7:
                issueCellUserCommand( UserCommandCode.SET_VECTOR_D );
                break;

            case 8:
                issueCellUserCommand( UserCommandCode.SET_VECTOR_DL );
                break;
        }
    }

    void cancelMarchCommands()
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        gameEngine.cancelAllMarchUserCommand( cx, cy );
    }

    void stopMarchCommand()
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        gameEngine.stopMarchUserCommand( cx, cy );
    }

    void setMarchCommand( int zone )
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        gameEngine.setMarchUserCommand( cx, cy, zone );
    }

    void swapMarchCommand( int zone )
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        gameEngine.swapMarchUserCommand( cx, cy, zone );
    }

    void fillGround( boolean managed )
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        gameEngine.fillGroundUserCommand( cx, cy, managed );
    }

    void digGround( boolean managed )
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        gameEngine.digGroundUserCommand( cx, cy, managed );
    }

    void buildBase( boolean managed )
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        gameEngine.buildBaseUserCommand( cx, cy, managed );
    }

    void scuttleBase( boolean managed )
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        gameEngine.scuttleBaseUserCommand( cx, cy, managed );
    }

    //    void troopsReserve( int level )
    //    {
    //        int cx = boardPanelModel.getCellGridXFromMouse();
    //        int cy = boardPanelModel.getCellGridYFromMouse();
    //        gameEngine.troopsReserveUserCommand( cx, cy, level );
    //    }

    //    void cellAttack()
    //    {
    //        int cx = boardPanelModel.getCellGridXFromMouse();
    //        int cy = boardPanelModel.getCellGridYFromMouse();
    //        gameEngine.cellAttackUserCommand( cx, cy );
    //    }

    /**
     * send para troops command
     */
    void paraTroops( boolean managed )
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        int md = boardPanelModel.getMouseDistanceFromCellCenter();
        int ma = boardPanelModel.getMouseAngleFromCellCenter();
        gameEngine.cellParaUserCommand( cx, cy, md, ma, managed );
    }

    /**
     * send gun troops command
     */
    void gunTroops( boolean managed )
    {
        int cx = boardPanelModel.getCellGridXFromMouse();
        int cy = boardPanelModel.getCellGridYFromMouse();
        int md = boardPanelModel.getMouseDistanceFromCellCenter();
        int ma = boardPanelModel.getMouseAngleFromCellCenter();
        gameEngine.cellGunUserCommand( cx, cy, md, ma, managed );
    }

    //    void cancelManagement()
    //    {
    //        int cx = boardPanelModel.getCellGridXFromMouse();
    //        int cy = boardPanelModel.getCellGridYFromMouse();
    //        gameEngine.cancelManagementUserCommand( cx, cy );
    //    }

    SelectedPlayerIterator getSelectedPlayerIterator()
    {
        return gameEngine.getGameModel().getPlayersModel().selectedPlayers();
    }

    PlayerStatisticsModel getPlayerStatisticsModel( PlayerInfoModel pim )
    {
        return gameEngine.getGameModel().getGameStatisticsModel().getPlayerStatisticsModel( pim.getPlayerId() );
    }

    boolean isMe( PlayerInfoModel pim )
    {
        return pim.getPlayerId() == gameEngine.getClientPlayerId();
    }

    InvisibilityMode getVisibilityMode()
    {
        return gameEngine.getGameModel().getGameParametersModel().getGameParameters().getVisibilityMode().getValue();
    }

    CellState getCellStateAt( int cellX, int cellY )
    {
        return gameEngine.getGameModel().getBoardStateModel().getCell( cellX, cellY ).cellState;
    }

    XBColor getClientPlayerColor()
    {
        return gameEngine.getClientPlayerColor();
    }
}
