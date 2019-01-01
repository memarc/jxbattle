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

package jxbattle.model.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jxbattle.bean.common.parameters.SystemParameters;
import jxbattle.bean.common.parameters.game.GameParameters;
import jxbattle.bean.server.GameParametersProfile;
import jxbattle.common.Consts;
import jxbattle.common.config.ConfigFolder;
import jxbattle.model.server.profilesettings.BoolProfileSetting;
import jxbattle.model.server.profilesettings.EnumProfileSetting;
import jxbattle.model.server.profilesettings.IntProfileSetting;
import jxbattle.model.server.profilesettings.LongProfileSetting;
import jxbattle.model.server.profilesettings.ProfileSetting;

import org.generic.bean.parameter.BoolParameter;

public class ServerConfigFile
{
    //private ServerEngine model;

    private SystemParameters systemParameters;

    private GameParametersProfilesModel gameParametersProfiles;

    private final String SERVER_LISTEN_PORT = "server.listenport";

    private final String SERVER_SOCKET_TIMEOUT = "server.sockettimeout";

    private final String SERVER_GAME_UPDATE_INTERVAL = "server.updatedelay";

    private final String SERVER_CHECK_CLIENT_STATE = "server.checkclientstate";

    private final String SERVER_FLUSH_FREQ = "server.flushfrequency";

    private final String BOARD_DIMX = "board.dimx";

    private final String BOARD_DIMY = "board.dimy";

    private final String BOARD_WRAP = "board.wrap";

    //private final String DRAW_GRID = "board.drawgrid";

    private final String INIT_GENERATE_SEED = "init.seed.generate";

    private final String INIT_RANDOM_SEED = "init.seed.value";

    private final String INIT_BASE_COUNT = "init.basecount";

    private final String INIT_RANDOMBASES_COUNT = "init.randombasecount";

    private final String INIT_TOWNS_DENSITY = "init.townsdensity";

    private final String INIT_ARMIES_COUNT = "init.armiescount";

    private final String INIT_MILITIA_DENSITY = "init.militiadensity";

    private final String INIT_SEA_DENSITY = "init.seadensity";

    private final String INIT_HILL_DENSITY = "init.hilldensity";

    private final String GAMEPLAY_MOVE_SPEED = "gameplay.movespeed";

    private final String GAMEPLAY_FIGHT_INTENSITY = "gameplay.fightintensity";

    private final String GAMEPLAY_ENABLE_BUILD = "gameplay.build.enable";

    private final String GAMEPLAY_BUILD_STEPS = "gameplay.build.steps";

    private final String GAMEPLAY_BUILD_COST = "gameplay.build.cost";

    private final String GAMEPLAY_ENABLE_SCUTTLE = "gameplay.scuttle.enable";

    private final String GAMEPLAY_SCUTTLE_COST = "gameplay.scuttle.cost";

    private final String GAMEPLAY_ENABLE_FILL = "gameplay.fill.enable";

    private final String GAMEPLAY_FILL_COST = "gameplay.fill.cost";

    private final String GAMEPLAY_ENABLE_DIG = "gameplay.dig.enable";

    private final String GAMEPLAY_DIG_COST = "gameplay.dig.cost";

    private final String GAMEPLAY_HILL_STEEPNESS = "gameplay.hillsteepness";

    private final String GAMEPLAY_ALLOW_REPEAT_KEY = "gameplay.allowrepeatkey";

    private final String GAMEPLAY_ENABLE_ATTACK = "gameplay.enableattack";

    private final String GAMEPLAY_ENABLE_MANAGED_COMMANDS = "gameplay.enablemanagedcommands";

    // para

    private final String GAMEPLAY_ENABLE_PARATROOPS = "gameplay.para.enable";

    private final String GAMEPLAY_PARATROOPS_RANGE = "gameplay.para.range";

    private final String GAMEPLAY_PARATROOPS_COST = "gameplay.para.cost";

    private final String GAMEPLAY_PARATROOPS_DAMAGE = "gameplay.para.damage";

    // gun
    private final String GAMEPLAY_ENABLE_GUNTROOPS = "gameplay.gun.enable";

    private final String GAMEPLAY_GUNTROOPS_RANGE = "gameplay.gun.range";

    private final String GAMEPLAY_GUNTROOPS_COST = "gameplay.gun.cost";

    private final String GAMEPLAY_GUNTROOPS_DAMAGE = "gameplay.gun.damage";

    // resupply

    private final String RESUPPLY_FARMS = "resupply.farms";

    private final String RESUPPLY_DECAY = "resupply.decay";

    private final String VISIBILITY_MODE = "visibility.mode";

    private final String VISIBILITY_RANGE = "visibility.range";

    private final String VISIBILITY_REMEMBER = "visibility.remember";

    private final String VISIBILITY_HIDE_VECTOR = "visibility.hidevectors";

    // marching

    private final String GAMEPLAY_ENABLE_MARCH = "gameplay.march.enable";

    private final String GAMEPLAY_MARCH_SPEED = "gameplay.march.speed";

    private final String PROFILE_COUNT = "profile.count";

    private final String PROFILE_CURRENT = "profile.current";

    private final String configFilename = "server.conf";

    public ServerConfigFile( SystemParameters sp, GameParametersProfilesModel gppm )
    {
        systemParameters = sp;
        gameParametersProfiles = gppm;
    }

    private String getFilePath()
    {
        return ConfigFolder.getFilePath( configFilename );
    }

    //    void read()
    //    {
    //        Properties props = new Properties( createDefaultProperties() );
    //
    //        FileInputStream in = null;
    //        try
    //        {
    //            in = new FileInputStream( getFilePath() );
    //            props.load( in );
    //        }
    //        catch( Exception e )
    //        {
    //            model.logMessage( "no config file found, using default values" );
    //        }
    //
    //        profilesFromProperties( props );
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

        try (FileInputStream in = new FileInputStream( getFilePath() ))
        {
            props.load( in );
        }
        catch( Exception e )
        {
            //model.logMessage( "no config file found, using default values" );
            ServerModel.logMessageModel.infoMessage( this, "no config file found, using default values" );
        }

        profilesFromProperties( props );
    }

    //    void write() throws IOException
    //    {
    //        ConfigFolder.makeConfigDir();
    //
    //        Properties props = profilesToProperties();
    //
    //        FileOutputStream out = null;
    //        try
    //        {
    //            out = new FileOutputStream( getFilePath() );
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

        Properties props = profilesToProperties();

        try (FileOutputStream out = new FileOutputStream( getFilePath() ))
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

        // server

        props.setProperty( SERVER_LISTEN_PORT, String.valueOf( Consts.defaultServerPort ) );
        props.setProperty( SERVER_SOCKET_TIMEOUT, String.valueOf( systemParameters.getSocketTimeout().getDefaultValue() ) );
        props.setProperty( SERVER_GAME_UPDATE_INTERVAL, String.valueOf( systemParameters.getGameTickInterval().getDefaultValue() ) );
        props.setProperty( SERVER_FLUSH_FREQ, String.valueOf( systemParameters.getFlushFrequency().getDefaultValue() ) );
        props.setProperty( SERVER_CHECK_CLIENT_STATE, "false" );

        return props;
    }

    private static String profileNameProperty( int profileNumber )
    {
        return "profile" + profileNumber + ".name";
    }

    private List<ProfileSetting> createSettingsFromModel( Properties props, GameParameters params, boolean genSeed )
    {
        List<ProfileSetting> settings = new ArrayList<>();

        // board

        settings.add( new IntProfileSetting( props, params.getBoardParameters().getXCellCount(), BOARD_DIMX ) );
        settings.add( new IntProfileSetting( props, params.getBoardParameters().getYCellCount(), BOARD_DIMY ) );
        settings.add( new EnumProfileSetting<>( props, params.getBoardParameters().getWrappingMode(), BOARD_WRAP ) );

        // init

        settings.add( new BoolProfileSetting( props, params.getInitialisationParameters().getGenerateRandomSeed(), INIT_GENERATE_SEED ) );
        if ( !genSeed )
            settings.add( new LongProfileSetting( props, params.getInitialisationParameters().getRandomSeed(), INIT_RANDOM_SEED ) );
        settings.add( new IntProfileSetting( props, params.getInitialisationParameters().getBases(), INIT_BASE_COUNT ) );
        settings.add( new IntProfileSetting( props, params.getInitialisationParameters().getRandomBases(), INIT_RANDOMBASES_COUNT ) );
        settings.add( new IntProfileSetting( props, params.getInitialisationParameters().getTowns(), INIT_TOWNS_DENSITY ) );
        settings.add( new IntProfileSetting( props, params.getInitialisationParameters().getArmies(), INIT_ARMIES_COUNT ) );
        settings.add( new IntProfileSetting( props, params.getInitialisationParameters().getMilitia(), INIT_MILITIA_DENSITY ) );
        settings.add( new IntProfileSetting( props, params.getInitialisationParameters().getSeaDensity(), INIT_SEA_DENSITY ) );
        settings.add( new IntProfileSetting( props, params.getInitialisationParameters().getHillDensity(), INIT_HILL_DENSITY ) );

        // gameplay

        settings.add( new IntProfileSetting( props, params.getTroopsMove(), GAMEPLAY_MOVE_SPEED ) );
        settings.add( new IntProfileSetting( props, params.getFightIntensity(), GAMEPLAY_FIGHT_INTENSITY ) );

        settings.add( new BoolProfileSetting( props, params.getEnableBaseBuild(), GAMEPLAY_ENABLE_BUILD ) );
        settings.add( new IntProfileSetting( props, params.getBaseBuildSteps(), GAMEPLAY_BUILD_STEPS ) );
        settings.add( new IntProfileSetting( props, params.getBaseBuildCost(), GAMEPLAY_BUILD_COST ) );

        settings.add( new BoolProfileSetting( props, params.getEnableBaseScuttle(), GAMEPLAY_ENABLE_SCUTTLE ) );
        settings.add( new IntProfileSetting( props, params.getBaseScuttleCost(), GAMEPLAY_SCUTTLE_COST ) );

        settings.add( new BoolProfileSetting( props, params.getEnableFill(), GAMEPLAY_ENABLE_FILL ) );
        settings.add( new IntProfileSetting( props, params.getFillCost(), GAMEPLAY_FILL_COST ) );

        settings.add( new BoolProfileSetting( props, params.getEnableDig(), GAMEPLAY_ENABLE_DIG ) );
        settings.add( new IntProfileSetting( props, params.getDigCost(), GAMEPLAY_DIG_COST ) );

        settings.add( new IntProfileSetting( props, params.getHillSteepness(), GAMEPLAY_HILL_STEEPNESS ) );

        settings.add( new BoolProfileSetting( props, params.getAllowKeyRepeat(), GAMEPLAY_ALLOW_REPEAT_KEY ) );
        settings.add( new BoolProfileSetting( props, params.getEnableAttack(), GAMEPLAY_ENABLE_ATTACK ) );
        settings.add( new BoolProfileSetting( props, params.getEnableManagedCommands(), GAMEPLAY_ENABLE_MANAGED_COMMANDS ) );

        // para

        settings.add( new BoolProfileSetting( props, params.getEnableParaTroops(), GAMEPLAY_ENABLE_PARATROOPS ) );
        settings.add( new IntProfileSetting( props, params.getParaTroopsRange(), GAMEPLAY_PARATROOPS_RANGE ) );
        settings.add( new IntProfileSetting( props, params.getParaTroopsCost(), GAMEPLAY_PARATROOPS_COST ) );
        settings.add( new IntProfileSetting( props, params.getParaTroopsDamage(), GAMEPLAY_PARATROOPS_DAMAGE ) );

        // gun

        settings.add( new BoolProfileSetting( props, params.getEnableGunTroops(), GAMEPLAY_ENABLE_GUNTROOPS ) );
        settings.add( new IntProfileSetting( props, params.getGunTroopsRange(), GAMEPLAY_GUNTROOPS_RANGE ) );
        settings.add( new IntProfileSetting( props, params.getGunTroopsCost(), GAMEPLAY_GUNTROOPS_COST ) );
        settings.add( new IntProfileSetting( props, params.getGunTroopsDamage(), GAMEPLAY_GUNTROOPS_DAMAGE ) );

        // resupply

        settings.add( new IntProfileSetting( props, params.getSupplyFarms(), RESUPPLY_FARMS ) );
        settings.add( new IntProfileSetting( props, params.getSupplyDecay(), RESUPPLY_DECAY ) );

        // visibility

        settings.add( new EnumProfileSetting<>( props, params.getVisibilityMode(), VISIBILITY_MODE ) );
        settings.add( new IntProfileSetting( props, params.getVisibilityRange(), VISIBILITY_RANGE ) );
        settings.add( new BoolProfileSetting( props, params.getVisibilityRememberCells(), VISIBILITY_REMEMBER ) );
        settings.add( new BoolProfileSetting( props, params.getVisibilityHideVectors(), VISIBILITY_HIDE_VECTOR ) );

        // marching

        settings.add( new BoolProfileSetting( props, params.getEnableMarching(), GAMEPLAY_ENABLE_MARCH ) );
        settings.add( new IntProfileSetting( props, params.getMarchSpeed(), GAMEPLAY_MARCH_SPEED ) );

        return settings;
    }

    private void profileToProperties( GameParameters params, Properties props, int profileNumber ) //, boolean defaultValues )
    {
        // create settings list

        List<ProfileSetting> settings = createSettingsFromModel( props, params, params.getInitialisationParameters().getGenerateRandomSeed().getValue() );

        // read settings

        for ( ProfileSetting ps : settings )
            ps.toProperties( profileNumber );
    }

    private Properties profilesToProperties()
    {
        Properties props = new Properties();

        // server

        props.setProperty( SERVER_LISTEN_PORT, String.valueOf( systemParameters.getServerListenPort().getValue() ) );
        props.setProperty( SERVER_SOCKET_TIMEOUT, String.valueOf( systemParameters.getSocketTimeout().getValue() ) );
        props.setProperty( SERVER_GAME_UPDATE_INTERVAL, String.valueOf( systemParameters.getGameTickInterval().getValue() ) );
        props.setProperty( SERVER_FLUSH_FREQ, String.valueOf( systemParameters.getFlushFrequency().getValue() ) );
        props.setProperty( SERVER_CHECK_CLIENT_STATE, String.valueOf( systemParameters.getCheckClientsState().getValue() ) );

        int i = 1;
        for ( GameParametersProfileModel gpfm : gameParametersProfiles )
        {
            GameParametersProfile gpf = gpfm.getGameParametersProfile();
            if ( !gpf.getReadOnly() )
            {
                props.setProperty( profileNameProperty( i ), gpf.getProfileName() );
                profileToProperties( gpf.getGameParameters(), props, i ); //, false );
                i++;
            }
        }

        // profile count

        props.setProperty( PROFILE_COUNT, String.valueOf( i - 1 ) );

        // current profile name

        //        props.setProperty( PROFILE_CURRENT, gameParametersProfiles.getCurrentProfileModel().getProfileName() );
        props.setProperty( PROFILE_CURRENT, gameParametersProfiles.getCurrent().getProfileName() );

        return props;
    }

    private void profileFromProperties( GameParameters params, Properties props, int profileNumber )
    {
        // get "generate seed" property value

        BoolParameter genSeedBP = params.getInitialisationParameters().getGenerateRandomSeed();
        BoolProfileSetting genSeedPS = new BoolProfileSetting( props, genSeedBP, INIT_GENERATE_SEED );
        genSeedPS.fromProperties( profileNumber );

        // create settings list

        List<ProfileSetting> settings = createSettingsFromModel( props, params, genSeedBP.getValue() );

        // read settings

        for ( ProfileSetting ps : settings )
            ps.fromProperties( profileNumber );
    }

    private void profilesFromProperties( Properties props )
    {
        // server

        //systemParametersModel.getServerListenPortModel().setValue( sender, Integer.valueOf( props.getProperty( SERVER_LISTEN_PORT ) ).intValue() );
        systemParameters.getServerListenPort().setValue( Integer.valueOf( props.getProperty( SERVER_LISTEN_PORT ) ).intValue() );
        systemParameters.getSocketTimeout().setValue( Integer.valueOf( props.getProperty( SERVER_SOCKET_TIMEOUT ) ).intValue() );
        systemParameters.getGameTickInterval().setValue( Integer.valueOf( props.getProperty( SERVER_GAME_UPDATE_INTERVAL ) ).intValue() );
        systemParameters.getFlushFrequency().setValue( Integer.valueOf( props.getProperty( SERVER_FLUSH_FREQ ) ).intValue() );
        systemParameters.getCheckClientsState().setValue( Boolean.valueOf( props.getProperty( SERVER_CHECK_CLIENT_STATE ) ).booleanValue() );

        // profile count

        String profCount = props.getProperty( PROFILE_COUNT );
        int profileCount = Integer.valueOf( profCount == null ? "0" : profCount ).intValue();

        // default profile

        String currentProfile = props.getProperty( PROFILE_CURRENT );
        if ( currentProfile == null )
            currentProfile = "default";
        GameParametersProfile defaultProfile = null;

        // read profiles

        for ( int i = 1; i <= profileCount; i++ )
        {
            GameParametersProfile gpf = new GameParametersProfile();
            gpf.setReadOnly( false );
            gpf.setProfileName( props.getProperty( profileNameProperty( i ) ) );
            profileFromProperties( gpf.getGameParameters(), props, i );
            gameParametersProfiles.add( this, gpf );

            if ( gpf.getProfileName().equals( currentProfile ) )
                defaultProfile = gpf;
        }

        if ( defaultProfile == null )
        {
            defaultProfile = new GameParametersProfile();
            defaultProfile.setProfileName( currentProfile );
            defaultProfile.setReadOnly( false );
            gameParametersProfiles.add( this, defaultProfile );
        }

        //        gameParametersProfiles.setCurrentProfile( defaultProfile );
        gameParametersProfiles.setCurrentProfileModel( this, defaultProfile.getProfileName() );
    }
}
