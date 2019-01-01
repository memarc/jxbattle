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

package jxbattle.model.common.game;

import jxbattle.bean.common.game.UserCommand;
import jxbattle.bean.common.parameters.game.GameParameters;
import jxbattle.bean.common.player.ColorId;
import jxbattle.model.client.BoardStateModel;
import jxbattle.model.client.GameStatisticsModel;
import jxbattle.model.client.RandomGen;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;
import jxbattle.model.common.parameters.game.GameParametersModel;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;

public class GameModel extends MVCModelImpl
{
    /**
     * application wide random generator, initialised with same seed across all clients
     * so that generated sequences are the same for everybody
     */
    private RandomGen randomGen;

    /**
     * player played by client
     * (used on client side only)
     */
    private ColorId clientPlayerColorId;

    // observed beans

    // models

    private PlayerInfos playersModel;

    private GameStatisticsModel gameStatisticsModel;

    private BoardStateModel boardStateModel;

    private GameParametersModel gameParametersModel;

    public GameModel()
    {
        gameParametersModel = new GameParametersModel();
    }

    public PlayerInfoModel getClientPlayer()
    {
        return getPlayersModel().getPlayerInfoModel( clientPlayerColorId );
    }

    public boolean isPlayerSelected()
    {
        return clientPlayerColorId != null;
    }

    public boolean isClientColorId( ColorId id )
    {
        return clientPlayerColorId.value == id.value;
    }

    public void setClientPlayerColorId( ColorId id )
    {
        clientPlayerColorId = id;
    }

    public PlayerInfos getPlayersModel()
    {
        if ( playersModel == null )
        {
            playersModel = new PlayerInfos();

            //            int id = 0;
            //            for ( XBColor col : Consts.playerColors )
            //            {
            //                PlayerInfo pi = new PlayerInfo( col, id++ );
            //                PlayerInfoModel pim = new PlayerInfoModel( pi );
            //                playersModel.add( this, pim );
            //            }
        }

        return playersModel;
    }

    //    public void setGameParameters( GameParameters gp )
    //    {
    //        //game.setGameParameters( gp );
    //        gameParameters = gp;
    //        gameParametersModel = null; //  pb si gameParametersModel référencé ailleurs (cad getGameParametersModel() appelé avant cette méthode) ...
    //    }

    //    public void setGameParametersModel( GameParametersModel gpm )
    //    {
    //        gameParametersModel = gpm;
    //    }

    public BoardStateModel getBoardStateModel()
    {
        if ( boardStateModel == null )
            boardStateModel = new BoardStateModel( gameParametersModel.getGameParameters(), getPlayersModel(), getRandomGenerator() );

        return boardStateModel;
    }

    public GameStatisticsModel getGameStatisticsModel()
    {
        if ( gameStatisticsModel == null )
            gameStatisticsModel = new GameStatisticsModel();

        return gameStatisticsModel;
    }

    public GameParametersModel getGameParametersModel()
    {
        //        if ( gameParametersModel == null )
        //            gameParametersModel = new GameParametersModel();

        return gameParametersModel;
    }

    //    public void setGameParameters( GameParameters gp )
    //    {
    //        //game.setGameParameters( gp );
    //        gameParameters = gp;
    //        gameParametersModel = null; //  pb si gameParametersModel référencé ailleurs (cad getGameParametersModel() appelé avant cette méthode) ...
    //    }

    public void setGameParameters( Object sender, GameParameters gp )
    {
        //        if ( gameParametersModel == null )
        //            gameParametersModel = new GameParametersModel( gp );

        gameParametersModel.setGameParameters( sender, gp );
    }

    public boolean hasWinner()
    {
        return getPlayersModel().hasWinner();
    }

    public RandomGen getRandomGenerator()
    {
        if ( randomGen == null )
        {
            //long seed = game.getGameParameters().getInitialisationParameters().getRandomSeed().getValue();
            long seed = gameParametersModel.getGameParameters().getInitialisationParameters().getRandomSeed().getValue();
            randomGen = new RandomGen( seed );
        }

        return randomGen;
    }

    public void resetRandomGenerator()
    {
        randomGen = null;
    }

    public void newGame()
    {
        getBoardStateModel().reset( gameParametersModel.getGameParameters(), getPlayersModel(), getRandomGenerator() );
        //        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.NewGame ) );
    }

    public void setUserCommand( Object sender, UserCommand cmd )
    {
        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.UserCommand, cmd ) );
    }

    public void watchGame( Object sender )
    {
        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.WatchGame ) );
    }

    public void abortGame( Object sender )
    {
        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.AbortGame ) );
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        super.addObserver( obs );
        gameParametersModel.addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        super.removeObserver( obs );
        gameParametersModel.removeObserver( obs );
    }
}
