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

package jxbattle.model.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import jxbattle.bean.common.game.Cell;
import jxbattle.bean.common.game.CellState;
import jxbattle.bean.common.parameters.game.GameParameters;
import jxbattle.bean.common.player.ColorId;
import jxbattle.common.config.ConfigFolder;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.game.GameModel;
import jxbattle.model.common.game.PlayerInfos.SelectedPlayerIterator;
import jxbattle.model.common.parameters.game.GameParametersModel;

import org.generic.thread.ThreadUtils;

public class GameLogFile
{
    private RandomAccessFile is;

    private DataOutputStream os;

    private String systemCharset;

    private String filePath;

    private GameModel gameModel;

    // private PlayerInfoModel clientPlayerInfoModel;

    private int headerSize;

    private int stepSize; // size of a step in game replay

    private int stepCount;

    private int currentStep;

    private int playerCount;

    private static int counter = 1;

    //GameLogFile( GameModel gm, PlayerInfoModel cp ) throws IOException
    GameLogFile( GameModel gm ) throws IOException
    {
        filePath = getDefaultFilePath();
        gameModel = gm;
        //clientPlayerInfoModel = cp;
        stepCount = -1;
        systemCharset = System.getProperty( "file.encoding" );

        writeHeader();
    }

    public GameLogFile( String path, GameModel gm ) throws IOException
    {
        filePath = path;
        gameModel = gm;
        stepCount = -1;

        readHeader();
    }

    private static String getDefaultFilePath()
    {
        String name = "game-" + ThreadUtils.getApplicationStartTime() + "-" + counter + ".log";
        counter++;
        return ConfigFolder.getFilePath( name );
    }

    private RandomAccessFile getInStream() throws FileNotFoundException
    {
        if ( is == null )
        {
            File f = new File( filePath );
            is = new RandomAccessFile( f, "r" );
        }

        return is;
    }

    private DataOutputStream getOutStream() throws FileNotFoundException
    {
        if ( os == null )
        {
            File f = new File( filePath );
            os = new DataOutputStream( new FileOutputStream( f ) );
        }

        return os;
    }

    void close() throws IOException
    {
        try
        {
            if ( is != null )
                is.close();
            is = null;
        }
        finally
        {
            if ( os != null )
                os.close();
            os = null;
        }
    }

    private String readString() throws IOException
    {
        int len = getInStream().readInt();

        byte[] b = new byte[ len ];
        getInStream().read( b );

        if ( systemCharset == null )
            return new String( b );

        return new String( b, systemCharset );
    }

    private ColorId readColorId() throws IOException
    {
        return new ColorId( getInStream().readByte() );
    }

    private void writeString( String s ) throws IOException
    {
        getOutStream().writeInt( s.getBytes().length );
        getOutStream().writeBytes( s );
    }

    private PlayerInfoModel readPlayer() throws IOException
    {
        ColorId cid = readColorId();
        int playerId = getInStream().readByte();

        //PlayerInfoModel pi = gameModel.getPlayersModel().getPlayerInfoModelFromPlayerId( playerId );
        PlayerInfoModel pi = gameModel.getPlayersModel().getPlayerInfoModel( cid );
        pi.setPlayerName( this, readString() );
        pi.setPlayerId( this, playerId );

        return pi;
    }

    private void writePlayer( PlayerInfoModel pim ) throws IOException
    {
        getOutStream().writeByte( pim.getColorId().value );
        getOutStream().writeByte( pim.getPlayerId() );
        writeString( pim.getPlayerName() );
    }

    private void readHeader() throws IOException
    {
        getInStream().seek( 0 );

        // system charset

        systemCharset = readString();

        // random seed

        long randomSeed = getInStream().readLong();
        GameParameters gp = new GameParameters();
        gp.getInitialisationParameters().getRandomSeed().setValue( randomSeed );
        gameModel.setGameParameters( this, gp );
        GameParametersModel gpm = gameModel.getGameParametersModel();

        // build steps

        int bs = getInStream().readByte();
        gp.getBaseBuildSteps().setValue( bs );

        // grid size

        int xcount = getInStream().readByte();
        int ycount = getInStream().readByte();
        gpm.getBoardParametersModel().getXCellCountModel().setValue( this, xcount );
        gpm.getBoardParametersModel().getYCellCountModel().setValue( this, ycount );

        // read players

        playerCount = getInStream().readByte();
        for ( int p = 0; p < playerCount; p++ )
            readPlayer();

        // client player

        //        int clientPlayerId = getInStream().readByte();
        //        clientPlayerInfoModel = gameModel.getPlayersModel().getPlayerInfoModelFromPlayerId( clientPlayerId );
        ColorId cid = readColorId();
        //clientPlayerInfoModel = gameModel.getPlayersModel().getPlayerInfoModel( cid );
        gameModel.setClientPlayerColorId( cid );

        // header size

        headerSize = (int)getInStream().getFilePointer();

        // cell size

        CellState cs = new CellState( 4 );
        cs.setPlayerCount( playerCount );
        cs.read( getInStream() );
        int cellSize = (int)(getInStream().getFilePointer() - headerSize);

        // step size

        stepSize = cellSize * xcount * ycount;
    }

    private void writeHeader() throws IOException
    {
        ConfigFolder.makeConfigDir();

        GameParameters gp = gameModel.getGameParametersModel().getGameParameters();

        // system charset 

        writeString( systemCharset );

        // random seed

        getOutStream().writeLong( gp.getInitialisationParameters().getRandomSeed().getValue() );

        // build steps

        getOutStream().writeByte( gp.getBaseBuildSteps().getValue() );

        // grid size

        getOutStream().writeByte( gp.getBoardParameters().getXCellCount().getValue() );
        getOutStream().writeByte( gp.getBoardParameters().getYCellCount().getValue() );

        // players

        getOutStream().writeByte( gameModel.getPlayersModel().getSelectedPlayerCount() );
        //for ( PlayerInfoModel pim : gameModel.getPlayersModel().selectedPlayers() )
        SelectedPlayerIterator it = gameModel.getPlayersModel().selectedPlayers();
        while ( it.hasNext() )
            writePlayer( it.next() );
        //        it.close();

        // client player

        //getOutStream().writeByte( clientPlayerInfoModel.getColorId().value );
        getOutStream().writeByte( gameModel.getClientPlayer().getColorId().value );

        getOutStream().flush();
    }

    public void readNextStep() throws IOException
    {
        if ( currentStep < getStepCount() - 1 )
            currentStep++;
        readCurrentStep();
    }

    public void readPreviousStep() throws IOException
    {
        if ( currentStep > 0 )
            currentStep--;
        readCurrentStep();
    }

    //    private long getStepSize() throws IOException
    //    {
    //        if ( stepSize == 0 )
    //        {
    //            long p1 = getInStream().getFilePointer();
    //
    //            int xcount = gameModel.getGameParametersModel().getGameParameters().getBoardParameters().getXCellCount().getValue();
    //            int ycount = gameModel.getGameParametersModel().getGameParameters().getBoardParameters().getYCellCount().getValue();
    //
    //            CellState cs = new CellState( 4 );
    //            cs.setPlayerCount( playerCount );
    //            cs.read( getInStream() );
    //
    //            stepSize = (getInStream().getFilePointer() - p1) * xcount * ycount;
    //        }
    //
    //        return stepSize;
    //    }

    private void readStep() throws IOException
    {
        BoardStateModel bs = gameModel.getBoardStateModel();
        //        try
        //        {
        for ( Cell cell : bs )
            cell.cellState.read( getInStream() );
        //        }
        //        catch( EOFException e ) // TODO ???
        //        {
        //        }
    }

    public void readCurrentStep() throws IOException
    {
        long p = headerSize + stepSize * currentStep;
        getInStream().seek( p );
        readStep();
    }

    void writeStep() throws IOException
    {
        BoardStateModel bs = gameModel.getBoardStateModel();

        for ( Cell cell : bs )
            cell.cellState.write( getOutStream() );

        getOutStream().flush();
    }

    public int getStepCount()// throws IOException
    {
        if ( stepCount == -1 )
        {
            File f = new File( filePath );
            long l = f.length();
            stepCount = (int)(l / stepSize);
        }

        return stepCount;
    }

    public int getCurrentStep()
    {
        return currentStep;
    }

    public void setCurrentStep( int step )// throws IOException
    {
        currentStep = step;

        if ( currentStep < 0 )
            currentStep = 0;

        if ( currentStep >= getStepCount() )
            currentStep = getStepCount() - 1;
    }

    public String getFilename()
    {
        return filePath;
    }

    //    public PlayerInfoModel getClientPlayerInfoModel()
    //    {
    //        return clientPlayerInfoModel;
    //    }
}
