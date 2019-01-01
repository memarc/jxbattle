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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import jxbattle.common.Consts;

import org.generic.mvc.model.MVCModelError;

/**
 * current state of a cell
 */
public class CellState
{
    /**
     * id of player owning cell
     */
    public int playerId;

    /**
     * level of troops in cell for each player (array needed to manage fights)
     */
    public float troopsLevel[];

    /**
     * level of troops in cell for next update cycle
     */
    public float nextTroopsLevel[];

    /**
     * minimum cell level to check before moving
     */
    public float reserveLevel;

    /**
     * sea/hill level
     */
    public int elevation;

    /**
     * active/unactive moves to the neighbours
     */
    public CellMove[] moves;

    /**
     * "cell is under fight" flag
     */
    public boolean fight;

    /**
     * cell has received paratroops
     */
    //public int paratroopsId;

    /**
     * ids of the 2 biggest players during a fight
     */
    public int biggestFightingPlayers[];

    // visibility 

    /**
     * "everything in cell is visible" flag
     */
    public boolean cellVisible;

    /**
     * "enemy in cell is visible" flag
     */
    public boolean enemyVisible;

    // town data

    /**
     * cell holds a town
     */
    public boolean isTown;

    /**
     * town size (1-10)
     */
    public int townSize;

    /**
     * town current building step (1-10)
     */
    public int townBuildStep;

    // update cycle

    public static final float[] zeroes = new float[ Consts.playerColors.length ];

    /**
     * possible amount of troops that can move out of the cell in each direction during update step
     */
    public float[] possibleMoveAmount;

    /**
     * render parachute symbol in cell
     */
    public int paraTroopsId;

    public long paraTroopsStart; // time of parachute symbol rendering start time

    /**
     * render guns symbol in cell
     */
    public int gunTroopsId;

    public long gunTroopsStart; // time of guns symbol rendering start time

    /**
     * rendered managed command
     */
    //    public UserCommandCode managedCommand;

    public CellState( int maxNeighbours )
    {
        moves = new CellMove[ maxNeighbours ];
        possibleMoveAmount = new float[ maxNeighbours ];
        for ( int i = 0; i < maxNeighbours; i++ )
            moves[ i ] = new CellMove();

        biggestFightingPlayers = new int[ 2 ];

        init();
        cancelMoves();

        cellVisible = false;
        enemyVisible = false;
        paraTroopsId = Consts.NullId;
        gunTroopsId = Consts.NullId;
        //        managedCommand = null;
    }

    private void init()
    {
        playerId = Consts.NullId;
        fight = false;
        reserveLevel = 0.0f;
    }

    public void setPlayerCount( int playerCount )
    {
        troopsLevel = new float[ playerCount ];
        nextTroopsLevel = new float[ playerCount ];
    }

    public void copyLevel()
    {
        System.arraycopy( troopsLevel, 0, nextTroopsLevel, 0, troopsLevel.length );
    }

    public void copyBackLevel()
    {
        System.arraycopy( nextTroopsLevel, 0, troopsLevel, 0, nextTroopsLevel.length );
    }

    public float getTroopsLevel()
    {
        if ( playerId != Consts.NullId )
            return troopsLevel[ playerId ];
        return 0.0f;
    }

    private static float limitTroopLevel( float l )
    {
        if ( l < 0 )
            l = 0;
        if ( l > Consts.maxTroopsLevel )
            l = Consts.maxTroopsLevel;
        return l;
    }

    public void setTroopsLevel( float l )
    {
        if ( playerId != Consts.NullId )
            troopsLevel[ playerId ] = limitTroopLevel( l );
        else
            throw new MVCModelError( "cannot set level of unoccupied cell !" );
    }

    public void subTroopsLevel( float dl )
    {
        if ( playerId != Consts.NullId )
            troopsLevel[ playerId ] = limitTroopLevel( troopsLevel[ playerId ] - dl );
        else
            throw new MVCModelError( "cannot set level of unoccupied cell !" );
    }

    public void addTroopsLevel( float dl )
    {
        if ( playerId != Consts.NullId )
            troopsLevel[ playerId ] = limitTroopLevel( troopsLevel[ playerId ] + dl );
        else
            throw new MVCModelError( "cannot set level of unoccupied cell !" );
    }

    public void addTroopsLevel( int pid, float dl )
    {
        troopsLevel[ pid ] = limitTroopLevel( troopsLevel[ pid ] + dl );
    }

    private void clearTroops()
    {
        System.arraycopy( zeroes, 0, troopsLevel, 0, troopsLevel.length );
        System.arraycopy( zeroes, 0, nextTroopsLevel, 0, nextTroopsLevel.length );
    }

    public void cancelMoves()
    {
        for ( CellMove cm : moves )
            cm.isSet = false;
    }

    // find 2 largest players
    public void sortFightingPlayers()
    {
        float bigOne = -1.0f, bigTwo = -1.0f;
        int oneId = Consts.NullId, twoId = Consts.NullId;

        int id = 0;
        for ( float fl : troopsLevel )
        {
            if ( fl > bigOne )
            {
                bigTwo = bigOne;
                bigOne = fl;
                twoId = oneId;
                oneId = id;
            }
            else if ( fl > bigTwo )
            {
                bigTwo = fl;
                twoId = id;
            }
            id++;
        }

        biggestFightingPlayers[ 0 ] = oneId;
        biggestFightingPlayers[ 1 ] = twoId;
    }

    public void setFightWinner( int plyrId )
    {
        boolean cellOwnerWins = plyrId == playerId && plyrId != Consts.NullId;

        init();
        playerId = plyrId;

        // erase all troops levels except winner's

        //        for ( int i = 0; i < troopsLevel.length; i++ )
        for ( int i = troopsLevel.length - 1; i >= 0; i-- )
            if ( i != plyrId )
                troopsLevel[ i ] = 0.0f;

        if ( !cellOwnerWins )
            cancelMoves();

        /*
                float l = 0.0f;

                if ( plyrId != Consts.NoPlayerId )
                    l = troopsLevel[ plyrId ];

                clear();
                playerId = plyrId;

                if ( playerId != Consts.NoPlayerId )
                    troopsLevel[ playerId ] = l;
                    */
    }

    public boolean isSeaCell()
    {
        return elevation < 0;
    }

    public void initSeaCell()
    {
        clearTroops();
        init();
        cancelMoves();

        isTown = false;
    }

    public boolean isFreeTerrain()
    {
        // is there anybody out there ?
        return !isSeaCell() && playerId == Consts.NullId && !isTown;
    }

    public void cellLimitUp( boolean updateNext )
    {
        if ( updateNext )
        {
            //            for ( int i = 0; i < nextTroopsLevel.length; i++ )
            for ( int i = nextTroopsLevel.length - 1; i >= 0; i-- )
                if ( nextTroopsLevel[ i ] > Consts.maxTroopsLevel )
                    nextTroopsLevel[ i ] = Consts.maxTroopsLevel;
        }
        else
        {
            //            for ( int i = 0; i < nextTroopsLevel.length; i++ )
            for ( int i = troopsLevel.length - 1; i >= 0; i-- )
                if ( troopsLevel[ i ] > Consts.maxTroopsLevel )
                    troopsLevel[ i ] = Consts.maxTroopsLevel;
        }
    }

    public void cellLimitDown( boolean updateNext )
    {
        if ( updateNext )
        {
            //            for ( int i = 0; i < nextTroopsLevel.length; i++ )
            for ( int i = nextTroopsLevel.length - 1; i >= 0; i-- )
                if ( nextTroopsLevel[ i ] < 0.0f )
                    nextTroopsLevel[ i ] = 0.0f;
        }
        else
        {
            //            for ( int i = 0; i < nextTroopsLevel.length; i++ )
            for ( int i = troopsLevel.length - 1; i >= 0; i-- )
                if ( troopsLevel[ i ] < 0.0f )
                    troopsLevel[ i ] = 0.0f;
        }
    }

    public void read( RandomAccessFile is ) throws IOException
    {
        playerId = is.readByte();

        for ( int i = 0; i < troopsLevel.length; i++ )
            troopsLevel[ i ] = is.readFloat();

        for ( CellMove cm : moves )
            cm.isSet = is.readBoolean();

        fight = is.readBoolean();
        isTown = is.readBoolean();
        townSize = is.readByte();
        townBuildStep = is.readByte();
        cellVisible = is.readBoolean();
        elevation = is.readByte();
    }

    public void write( DataOutputStream os ) throws IOException
    {
        os.writeByte( playerId );

        for ( float fl : troopsLevel )
            os.writeFloat( fl );

        for ( CellMove cm : moves )
            os.writeBoolean( cm.isSet );

        os.writeBoolean( fight );
        os.writeBoolean( isTown );
        os.writeByte( townSize );
        os.writeByte( townBuildStep );
        os.writeBoolean( cellVisible );
        os.writeByte( elevation );
    }

    private static String arrayToString( float[] a )
    {
        StringBuilder sb = new StringBuilder();
        sb.append( '[' );
        boolean first = true;
        for ( float i : a )
        {
            if ( !first )
                sb.append( ", " );
            first = false;
            sb.append( i );
        }
        sb.append( ']' );
        return sb.toString();
    }

    // Object interface

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof CellState )
        {
            CellState cs = (CellState)obj;
            if ( playerId == cs.playerId )
            {
                for ( int i = 0; i < troopsLevel.length; i++ )
                    if ( troopsLevel[ i ] != cs.troopsLevel[ i ] )
                        return false;
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString()
    {
        return (isTown ? "<town>" : "<troops>") + " troops=" + arrayToString( troopsLevel ) + " next=" + arrayToString( nextTroopsLevel ) + " poss.moves=" + arrayToString( possibleMoveAmount );
    }
}
