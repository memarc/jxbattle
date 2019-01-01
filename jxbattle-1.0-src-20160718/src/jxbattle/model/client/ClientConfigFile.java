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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import jxbattle.bean.common.parameters.SystemParameters;
import jxbattle.common.Consts;
import jxbattle.common.config.ConfigFolder;
import jxbattle.model.common.parameters.SystemParametersModel;

import org.generic.net.PeerInfo;

public class ClientConfigFile
{
    private final String SERVER_PORT = "server.port";

    private final String SERVER_HOST = "server.host";

    // private final String SERVER_HISTORY = "server.host.history";
    // private final String PORT_HISTORY = "server.port.history";

    private final String SERVER_HISTORY = "server.history";

    private final String CLIENT_NAME = "client.name";

    private final String GAME_LOG_REPLAY = "game.logreplay";

    private final String CLIENT_SOCKET_TIMEOUT = "client.sockettimeout";

    private final String CLIENT_NETWORK_BIAS = "client.networkbias";

    private final String configFilename = "client.conf";

    private SystemParametersModel systemParametersModel;

    public ClientConfigFile( SystemParametersModel spm )
    {
        systemParametersModel = spm;
    }

    //    void read()
    //    {
    //        Properties props = new Properties( createDefaultProperties() );
    //
    //        FileInputStream in = null;
    //        try
    //        {
    //            in = new FileInputStream( ConfigFolder.getFilePath( configFilename ) );
    //            props.load( in );
    //        }
    //        catch( Exception e )
    //        {
    //        }
    //
    //        fromProperties( props );
    //
    //        if ( in != null )
    //            try
    //            {
    //                in.close();
    //            }
    //            catch( IOException e )
    //            {
    //            }
    //    }

    public void read()
    {
        Properties props = new Properties( createDefaultProperties() );

        try (FileInputStream in = new FileInputStream( ConfigFolder.getFilePath( configFilename ) ))
        {
            props.load( in );
        }
        catch( Exception e )
        {
        }

        fromProperties( props );
    }

    //    void write() throws IOException
    //    {
    //        ConfigFolder.makeConfigDir();
    //
    //        Properties props = createProperties();
    //
    //        FileOutputStream out = null;
    //        try
    //        {
    //            out = new FileOutputStream( ConfigFolder.getFilePath( configFilename ) );
    //            props.store( out, "" );
    //        }
    //        catch( FileNotFoundException e )
    //        {
    //        }
    //        finally
    //        {
    //            if ( out != null )
    //                out.close();
    //        }
    //    }

    public void write() throws IOException
    {
        ConfigFolder.makeConfigDir();

        Properties props = createProperties();

        try (FileOutputStream out = new FileOutputStream( ConfigFolder.getFilePath( configFilename ) ))
        {
            props.store( out, "" );
        }
        catch( FileNotFoundException e )
        {
        }
    }

    private Properties createDefaultProperties()
    {
        Properties props = new Properties();

        SystemParameters systemParameters = systemParametersModel.getSystemParameters();

        props.setProperty( SERVER_HOST, "localhost" );
        props.setProperty( SERVER_HISTORY, "" );
        // props.setProperty( PORT_HISTORY, "" );
        props.setProperty( SERVER_PORT, String.valueOf( Consts.defaultServerPort ) );
        props.setProperty( CLIENT_NAME, "anonymous" );
        props.setProperty( GAME_LOG_REPLAY, String.valueOf( systemParameters.getLogGameReplay().getDefaultValue() ) );
        props.setProperty( CLIENT_SOCKET_TIMEOUT, String.valueOf( systemParameters.getSocketTimeout().getDefaultValue() ) );
        props.setProperty( CLIENT_NETWORK_BIAS, String.valueOf( systemParameters.getNetworkBias().getDefaultValue() ) );

        return props;
    }

    // private String hostsToProperties()
    // {
    // StringBuilder sb = new StringBuilder();
    //
    // boolean first = true;
    // for ( PeerInfoModel gs : model.getServerHistoryModel() )
    // {
    // if ( !first )
    // sb.append( ':' );
    // first = false;
    // sb.append( gs.getHost() );
    // }
    //
    // return sb.toString();
    // }

    private String hostsToProperties()
    {
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        //        for ( PeerInfo gs : systemParameters.getServerHistory() )
        for ( PeerInfo gs : systemParametersModel.getServerHistoryModel().getPeerInfos() )
        {
            if ( !first )
                sb.append( ' ' );
            first = false;
            sb.append( gs.getHost() );
            sb.append( ':' );
            sb.append( gs.getPort() );
        }

        return sb.toString();
    }

    // private String portsToProperties()
    // {
    // StringBuilder sb = new StringBuilder();
    //
    // boolean first = true;
    // for ( PeerInfoModel gs : model.getServerHistoryModel() )
    // {
    // if ( !first )
    // sb.append( ':' );
    // first = false;
    // sb.append( String.valueOf( gs.getPort() ) );
    // }
    //
    // return sb.toString();
    // }

    private Properties createProperties()
    {
        Properties props = new Properties();

        SystemParameters systemParameters = systemParametersModel.getSystemParameters();

        props.setProperty( SERVER_HOST, systemParameters.getServerPeerInfo().getHost() );
        props.setProperty( SERVER_PORT, String.valueOf( systemParameters.getServerPeerInfo().getPort() ) );
        props.setProperty( SERVER_HISTORY, hostsToProperties() );
        // props.setProperty( PORT_HISTORY, portsToProperties() );
        props.setProperty( CLIENT_NAME, systemParameters.getClientName() );
        props.setProperty( GAME_LOG_REPLAY, String.valueOf( systemParameters.getLogGameReplay().getValue() ) );
        props.setProperty( CLIENT_SOCKET_TIMEOUT, String.valueOf( systemParameters.getSocketTimeout().getValue() ) );
        props.setProperty( CLIENT_NETWORK_BIAS, String.valueOf( systemParameters.getNetworkBias().getValue() ) );

        return props;
    }

    // private void historyFromProperties( Properties props )
    // {
    // String serverHistory = props.getProperty( SERVER_HISTORY );
    // String portHistory = props.getProperty( PORT_HISTORY );
    //
    // if ( serverHistory.length() > 0 )
    // {
    // String[] servs = serverHistory.split( ":" );
    // String[] ports = portHistory.split( ":" );
    //
    // for ( int i = 0; i < servs.length; i++ )
    // {
    // PeerInfo pi = new PeerInfo();
    // pi.setHost( servs[ i ] );
    //
    // if ( i < ports.length )
    // pi.setPort( Integer.valueOf( ports[ i ] ).intValue() );
    //
    // model.getServerHistoryModel().add( this, new PeerInfoModel( pi ) );
    // }
    // }
    // }

    private void historyFromProperties( Properties props )
    {
        String serverHistory = props.getProperty( SERVER_HISTORY );

        if ( serverHistory.length() > 0 )
        {
            String[] servs = serverHistory.split( " " );

            for ( int i = 0; i < servs.length; i++ )
            {
                // PeerInfo pi = new PeerInfo();

                String[] serv_port = servs[ i ].split( ":" );
                // pi.setHost( serv_port[ 0 ] );
                String host = serv_port[ 0 ];

                // if ( i < ports.length )
                // pi.setPort( Integer.valueOf( ports[ i ] ).intValue() );

                // pi.setPort( Integer.valueOf( serv_port[ 1 ] ).intValue() );
                int port = Integer.valueOf( serv_port[ 1 ] ).intValue();

                PeerInfo pi = new PeerInfo( host, port );
                //                systemParameters.getServerHistory().addUnique( pi );
                systemParametersModel.getServerHistoryModel().addUnique( this, pi );
            }
        }
    }

    private void fromProperties( Properties props )
    {
        historyFromProperties( props );

        SystemParameters systemParameters = systemParametersModel.getSystemParameters();

        String serverHost = props.getProperty( SERVER_HOST );
        int serverPort = Integer.valueOf( props.getProperty( SERVER_PORT ) ).intValue();
        systemParameters.getServerPeerInfo().setHost( serverHost );
        systemParameters.getServerPeerInfo().setPort( serverPort );

        systemParameters.setClientName( props.getProperty( CLIENT_NAME ) );
        systemParameters.getLogGameReplay().setValue( Boolean.valueOf( props.getProperty( GAME_LOG_REPLAY ) ).booleanValue() );

        systemParameters.getSocketTimeout().setValue( Integer.valueOf( props.getProperty( CLIENT_SOCKET_TIMEOUT ) ).intValue() );
        systemParameters.getNetworkBias().setValue( Integer.valueOf( props.getProperty( CLIENT_NETWORK_BIAS ) ).intValue() );
    }
}
