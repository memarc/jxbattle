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

package jxbattle.gui.composite.client.replayframe;

import java.io.IOException;

import jxbattle.gui.base.client.boardpanel.BoardPanelModel;
import jxbattle.gui.base.client.replaycursorpanel.ReplayCursorPanelModel;
import jxbattle.model.client.GameEngine;
import jxbattle.model.client.GameLogFile;
import jxbattle.model.common.game.GameModel;

import org.generic.mvc.model.observer.MVCModelObserver;

public class ReplayFrameModel extends ReplayCursorPanelModel
{
    private GameLogFile gameLogFile;

    private GameModel gameModel;

    private GameEngine gameEngine;

    private BoardPanelModel boardPanelModel;

    public ReplayFrameModel( String logFilename ) throws IOException
    {
        gameModel = new GameModel();
        //gameModel.getGameParametersModel().getGameParameters().getAllowKeyRepeat().setValue( true );

        gameLogFile = new GameLogFile( logFilename, gameModel );
        gameLogFile.setCurrentStep( 0 );
        gameLogFile.readCurrentStep();

        gameEngine = new GameEngine( gameModel );
        boardPanelModel = new BoardPanelModel( gameEngine, null );
        boardPanelModel.setAllowRepeatKey( true );

        int ns = gameLogFile.getStepCount() / 10;
        if ( ns < 1 )
            ns = 1;
        else if ( ns > 50 )
            ns = 50;

        setBigStep( this, ns );
    }

    GameModel getGameModel()
    {
        return gameModel;
    }

    BoardPanelModel getBoardPanelModel()
    {
        return boardPanelModel;
    }

    GameEngine getGameEngine()
    {
        return gameEngine;
    }

    void gameLogStart( Object sender ) throws IOException
    {
        gameLogFile.setCurrentStep( 0 );
        gameLogFile.readCurrentStep();
        notifyStepChanged( sender );
    }

    void gameLogBigPrevious( Object sender ) throws IOException
    {
        gameLogFile.setCurrentStep( gameLogFile.getCurrentStep() - bigStepModel.getValue() );
        gameLogFile.readCurrentStep();
        notifyStepChanged( sender );
    }

    void gameLogPrevious( Object sender ) throws IOException
    {
        gameLogFile.readPreviousStep();
        notifyStepChanged( sender );
    }

    void gameLogNext( Object sender ) throws IOException
    {
        gameLogFile.readNextStep();
        notifyStepChanged( sender );
    }

    void gameLogBigNext( Object sender ) throws IOException
    {
        gameLogFile.setCurrentStep( gameLogFile.getCurrentStep() + bigStepModel.getValue() );
        gameLogFile.readCurrentStep();
        notifyStepChanged( sender );
    }

    void gameLogEnd( Object sender ) throws IOException
    {
        gameLogFile.setCurrentStep( gameLogFile.getStepCount() - 1 );
        gameLogFile.readCurrentStep();
        notifyStepChanged( sender );
    }

    protected int getCurrentStep()
    {
        return gameLogFile.getCurrentStep();
    }

    protected int getStepCount()
    {
        return gameLogFile.getStepCount();
    }

    // MVCModel interface

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        super.addObserver( obs );
        boardPanelModel.addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        super.removeObserver( obs );
        boardPanelModel.removeObserver( obs );
    }
}
