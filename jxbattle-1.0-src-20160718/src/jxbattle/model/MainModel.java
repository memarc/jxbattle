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

package jxbattle.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import jxbattle.bean.common.parameters.SystemParameters;
import jxbattle.gui.composite.mainframe.MainFrameController;
import jxbattle.gui.composite.mainframe.MainFrameModel;
import jxbattle.model.client.ClientConfigFile;
import jxbattle.model.client.ClientEngine;
import jxbattle.model.common.DebugLog;
import jxbattle.model.common.game.PlayerInfos;
import jxbattle.model.common.parameters.SystemParametersModel;
import jxbattle.model.server.GameParametersProfilesModel;
import jxbattle.model.server.ServerConfigFile;
import jxbattle.model.server.ServerEngine;
import jxbattle.model.server.ServerModel;

import org.generic.mvc.model.logmessage.ConsoleLogger;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.mvc.model.observer.MVCModelObserver;
import org.generic.net.NetEngine;
import org.generic.net.PeerInfo;
import org.generic.thread.ThreadUtils;

public class MainModel extends MVCModelImpl
{
    private static MainModel instance;

    private boolean isActive;

    private SystemParametersModel systemParametersModel;

    private GameParametersProfilesModel gameParametersProfilesModel;

    private ArrayBlockingQueue<PeerEngine> engines;

    public final static boolean debug = false; // TODO a virer

    public final static boolean fastPlay = false && debug; // TODO a virer

    public final static boolean debugLog = false && debug; // TODO a virer

    public final static boolean uniqueDebugLog = false;

    public final static boolean debugState = true && debug; // TODO a virer

    public final static boolean iconifyGameFrame = false && debug;

    public final static boolean gameFrameToBack = false && debug;

    public final static boolean debugDur = false && debug;

    //    private LockFile lockFile; // TODO a virer

    private int playerCount; // TODO a virer

    private static FileOutputStream fosDebug = null;

    private DebugLog globalDebugLog;

    /**
     * client state checking method
     * true : use full board cells state
     * false : use coverage % only
     */
    public static final boolean boardStateFullCheck = false;

    private MainModel()
    {
        engines = new ArrayBlockingQueue<>( 3 );

        isActive = true;

        systemParametersModel = new SystemParametersModel( new SystemParameters() );
        //        gameParametersProfilesModel = new GameParametersProfilesModel( new GameParametersProfileList() );
        gameParametersProfilesModel = new GameParametersProfilesModel();

        // load configs

        new ClientConfigFile( systemParametersModel ).read();
        new ServerConfigFile( systemParametersModel.getSystemParameters(), gameParametersProfilesModel ).read();

        //        if ( fastPlay )
        //            lockFile = new LockFile();

        new ConsoleLogger().setModel( NetEngine.logMessageModel );

        //        try
        //        {
        //            fosDebug = new FileOutputStream( new File( "/tmp/jx.log" ) );
        //        }
        //        catch( FileNotFoundException e )
        //        {
        //            e.printStackTrace();
        //            System.exit( 1 );
        //        }
    }

    public void setPlayerCount( int count )
    {
        if ( fastPlay )
            playerCount = count;
        else
            playerCount = 1;
    }

    public static MainModel getInstance()
    {
        if ( instance == null )
            instance = new MainModel();

        return instance;
    }

    public void run( Object sender )
    {
        // open main frame

        MainFrameController mainFrameController = new MainFrameController();
        mainFrameController.setModel( new MainFrameModel( this ) );
        mainFrameController.run();

        int i = 0;
        while ( isActive() )
        {
            //            try
            //            {
            //                fosDebug.write( "\n".getBytes() );
            //            }
            //            catch( IOException e )
            //            {
            //                System.exit( 1 );
            //            }

            long startTotal = System.nanoTime();

            for ( PeerEngine pe : engines )
            {
                long startLoop = System.nanoTime();
                pe.doLoop( sender );
                printDur( "engine loop ms=", startLoop, 0.1f );
            }
            //            printDur( "loops ms=", startTotal, 1.5f );

            //            long startFast = System.nanoTime();
            if ( fastPlay )
            {
                switch ( i++ )
                {
                    case 5:
                        //                        if ( lockFile.acquire() )
                        //                            getServerEngine().setEngineActive( sender, true );
                        if ( uniqueDebugLog )
                            globalDebugLog = new DebugLog();
                        createServerEngine( sender );
                        createClients();
                        break;

                    case 10:
                        getServerEngine().startListen();
                        break;

                    //                    case 20:
                    //                        clientEngine.getModel().getSystemParametersModel().getServerHistoryModel().getPeerInfoModel().setPort( this, 5000 );
                    //                        break;

                    case 50:
                        startClients( sender );
                        break;

                    case 100:
                        connectClients( sender );
                        break;

                    case 200:
                        selectPlayers();
                        break;

                    //                    case 300:
                    //                        debugDur = debug;
                    //                        break;

                    //                    case 310:
                    //                        for ( PeerEngine pe : engines )
                    //                            if ( pe instanceof ClientEngine )
                    //                            {
                    //                                ClientEngine ce = (ClientEngine)pe;
                    //                                ce.getGameEngine().cellParaUserCommand( 21, 9, 100, 0, true );
                    //                                ce.getGameFrameController().getBoardPanelModel().toto();
                    //                                //                                                    Cell c = ce.getGameModel().getBoardStateModel().getBoardState().getCell( 0, 0 );
                    //                                //                                                    c.cellState.playerId = 0;
                    //                                //                                                    c.cellState.troopsLevel[ 0 ] = Consts.maxTroopsLevel;
                    //                                //                                                    ce.ge
                    //                                //                                                    GameEngine.startManageBuildBase( c );
                    //                            }
                    //                        break;

                    //                    case 300:
                    //                        if ( true )
                    //                            for ( PeerEngine pe : engines )
                    //                                if ( pe instanceof ClientEngine )
                    //                                {
                    //                                    ClientEngine ce = (ClientEngine)pe;
                    //                                    ce.watchGame();
                    //                                    break;
                    //                                }
                    //                        break;

                    default:
                        break;
                }
            }
            //            printDur( "fast play ms=", startFast, 0.1f );

            long startSleep = System.nanoTime();
            //            ThreadUtils.sleep( 0, 100 );
            ThreadUtils.sleep( 1 );
            printDur( "sleep ms=", startSleep, 2.0f );

            printDur( "total ms=", startTotal, 3.0f );
        }

        for ( PeerEngine pe : engines )
            pe.close();
        mainFrameController.close();

        try
        {
            if ( fosDebug != null )
                fosDebug.close();
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }

    public static void printDur( String msg, long start, float max )
    {
        if ( debugDur )
        {
            float dur = (System.nanoTime() - start) / 1000000.0f;
            String m = dur >= max ? "  " + msg.toUpperCase() : msg;
            String s = m + dur + '\n';

            if ( fosDebug != null )
                try
                {
                    fosDebug.write( s.getBytes() );
                    fosDebug.flush();
                }
                catch( IOException e )
                {
                    e.printStackTrace();
                    if ( fosDebug != null )
                        try
                        {
                            fosDebug.close();
                        }
                        catch( IOException e1 )
                        {
                            e1.printStackTrace();
                            fosDebug = null;
                        }
                }
        }
    }

    private boolean isActive()
    {
        if ( isActive )
            return true;

        for ( PeerEngine pe : engines )
            if ( pe.isEngineActive() && !pe.isShutdown() )
                return true;

        return false;
    }

    public void shutdown()
    {
        saveClientConfig();
        isActive = false;

        for ( PeerEngine pe : engines )
            pe.shutdown();
    }

    public ClientEngine createClientEngine()
    {
        int n = engines.size();
        ClientEngine clientEngine = new ClientEngine( systemParametersModel );

        if ( debugLog )
        {
            DebugLog clientLog = uniqueDebugLog ? globalDebugLog : new DebugLog(); // "/home/francois/.jxbattle-1.0/client.log" );
            clientLog.setHeader( "[ClientEngine" + n + "] " );
            clientLog.setEnabled( true );
            clientEngine.setDebugLog( clientLog );
            //            clientEngine.setAutomatonDebugLog( clientLog );

            DebugLog clientConnectionLog = uniqueDebugLog ? globalDebugLog : new DebugLog(); // "/home/francois/.jxbattle-1.0/clientnet.log" );
            clientConnectionLog.setHeader( "[ClientSideConnection" + n + "] " );
            clientConnectionLog.setEnabled( true );
            clientEngine.getClientSideConnectionModel().setDebugLog( clientConnectionLog );
        }

        engines.add( clientEngine );
        clientEngine.addObservers( getObservers() );

        //clientEngine.setEngineActive( sender, true );

        return clientEngine;
    }

    public void createServerEngine( Object sender )
    {
        ServerEngine serverEngine = new ServerEngine( systemParametersModel, gameParametersProfilesModel );
        if ( debugLog )
        {
            DebugLog serverLog = uniqueDebugLog ? globalDebugLog : new DebugLog(); // "/home/francois/.jxbattle-1.0/server.log" );
            serverLog.setHeader( "[ServerEngine] " );
            serverLog.setEnabled( true );
            serverEngine.setDebugLog( serverLog );
            //            serverEngine.setAutomatonDebugLog( serverLog );

            DebugLog serverConnectionLog = uniqueDebugLog ? globalDebugLog : new DebugLog(); // "/home/francois/.jxbattle-1.0/servernet.log" );
            serverConnectionLog.setHeader( "[ServerSideConnection] " );
            serverConnectionLog.setEnabled( true );
            serverEngine.getServerSideConnectionModel().setDebugLog( serverConnectionLog );
        }

        serverEngine.setPlayerCount( sender, playerCount );

        serverEngine.addObservers( getObservers() );
        engines.add( serverEngine );

        //        serverEngine.setEngineActive( sender, true );
        serverEngine.setEngineActive( sender );

        //        return serverEngine;
    }

    private ServerEngine getServerEngine()
    {
        for ( PeerEngine pe : engines )
            if ( pe instanceof ServerEngine )
                return (ServerEngine)pe;

        return null;
    }

    private void createClients()
    {
        for ( int n = 0; n < playerCount; n++ )
            createClientEngine();

        int i = 1;
        for ( PeerEngine pe : engines )
            if ( pe instanceof ClientEngine )
            {
                ClientEngine ce = (ClientEngine)pe;
                ce.getSystemParametersModel().setClientName( this, "fanfan" + i );
                i++;
            }
    }

    private void startClients( Object sender )
    {
        for ( PeerEngine pe : engines )
            if ( pe instanceof ClientEngine )
            {
                ClientEngine ce = (ClientEngine)pe;
                //                ce.setEngineActive( sender, true );
                ce.setEngineActive( sender );
            }
    }

    private void connectClients( Object sender )
    {
        PeerInfo pi = new PeerInfo( "localhost", 10000 );
        for ( PeerEngine pe : engines )
            if ( pe instanceof ClientEngine )
            {
                ClientEngine ce = (ClientEngine)pe;
                ce.connectServer( sender, pi );
            }
    }

    private void selectPlayers()
    {
        int i = 0;
        for ( PeerEngine pe : engines )
            if ( pe instanceof ClientEngine )
            {
                ClientEngine ce = (ClientEngine)pe;
                PlayerInfos players = ce.getGameModel().getPlayersModel();
                ce.requestPlayer( players.get( i++ ) );
            }
    }

    public void saveClientConfig()
    {
        try
        {
            new ClientConfigFile( systemParametersModel ).write();
        }
        catch( IOException e )
        {
            ServerModel.logMessageModel.errorMessage( this, "error writing client configuration file", e );
        }
    }

    public void saveServerConfig()
    {
        try
        {
            new ServerConfigFile( systemParametersModel.getSystemParameters(), gameParametersProfilesModel ).write();
        }
        catch( IOException e )
        {
            ServerModel.logMessageModel.errorMessage( this, "error writing server configuration file", e );
        }
    }

    //    public LockFile getLockFile()
    //    {
    //        return lockFile;
    //    }

    @Override
    public void addObserver( MVCModelObserver obs )
    {
        super.addObserver( obs );
        for ( PeerEngine pe : engines )
            pe.addObserver( obs );
    }

    @Override
    public void removeObserver( MVCModelObserver obs )
    {
        super.removeObserver( obs );
        for ( PeerEngine pe : engines )
            pe.removeObserver( obs );
    }
}
