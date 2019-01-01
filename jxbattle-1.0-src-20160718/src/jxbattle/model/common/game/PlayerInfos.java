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

import java.util.Comparator;

import jxbattle.bean.common.player.ColorId;
import jxbattle.bean.common.player.PlayerInfo;
import jxbattle.bean.common.player.PlayerState;
import jxbattle.bean.common.player.XBColor;
import jxbattle.common.Consts;
import jxbattle.model.common.PlayerInfoModel;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.list.AbstractSyncList;
import org.generic.list.SyncIterator;
import org.generic.mvc.model.MVCModelError;
import org.generic.mvc.model.list.ListModelChangeId;
import org.generic.mvc.model.list.SyncListModel;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelObserver;

/**
 * player list, sorted by player id
 * players status :
 * - available (no id)
 * - selected (id set)
 * - playing (state = PlayingGame)
 */
public class PlayerInfos extends SyncListModel<PlayerInfoModel> implements MVCModelObserver
{
    private int selectedPlayerCount;

    private int ingamePlayerCount;

    private int activePlayerCount;

    public PlayerInfos()
    {
        resetCounts();

        for ( XBColor col : Consts.playerColors )
        {
            PlayerInfo pi = new PlayerInfo( col );
            PlayerInfoModel pim = new PlayerInfoModel( pi );
            add( this, pim );
        }

        // observe list in order to automatically subscribe to added elements (see ListModel.add())
        // -> be warned when player id changes and re-sort list
        addObserver( this );

        // provide sort comparator
        keepSorted( new PlayerIdComparator() );
    }

    //    public void addPlayer( Object sender, PlayerInfo pi )
    //    {
    //        PlayerInfoModel pim = new PlayerInfoModel( pi );
    //        add( sender, pim ); // notification sent by this method
    //    }

    public PlayerInfoModel getPlayerInfoModel( ColorId colorId )
    {
        for ( PlayerInfoModel pim : this )
            if ( pim.getColorId().value == colorId.value )
                return pim;

        throw new MVCModelError( "no player with color id " + colorId );
    }

    public PlayerInfoModel getPlayerInfoModel( int playerId )
    {
        for ( PlayerInfoModel pim : this )
            if ( pim.getPlayerId() == playerId )
                return pim;

        throw new MVCModelError( "no player with id " + playerId );
    }

    //    private int getAvailablePlayerId()
    //    {
    //        return get( size() - 1 ).getPlayerId() + 1;
    //        //return get( 0 ).getPlayerId() + 1;
    //    }

    //    public PlayerInfoModel isPlayerAvailable( int playerId )
    //    {
    //        PlayerInfoModel pim = getPlayerInfoModelFromPlayerId( playerId );
    //        if ( !pim.isPlayerSelected() )
    //            return pim;
    //
    //        return null;
    //    }

    public XBColor getColorFromPlayerId( int playerId )
    {
        for ( PlayerInfoModel pim : this )
        //        SyncIterator<PlayerInfoModel> it = iterator();
        //        try
        //        {
        //            while ( it.hasNext() )
        {
            //                PlayerInfoModel pim = it.next();
            if ( pim.getPlayerId() == playerId )
                return pim.getPlayerColor();
        }
        //        }
        //        finally
        //        {
        //            it.close();
        //        }

        throw new MVCModelError( "no player with id " + playerId );
    }

    private void resetCounts()
    {
        selectedPlayerCount = -1;
        ingamePlayerCount = -1;
        activePlayerCount = -1;
    }

    /**
     * @return number of players chosen to be in game session (includes watching/lost/...)
     */
    public int getSelectedPlayerCount()
    {
        if ( selectedPlayerCount == -1 )
        {
            selectedPlayerCount = 0;

            for ( PlayerInfoModel pim : this )
                //            SyncIterator<PlayerInfoModel> it = iterator();
                //            try
                //            {
                //                while ( it.hasNext() )
                //                    if ( it.next().isPlayerSelected() )
                if ( pim.isPlayerSelected() )
                    selectedPlayerCount++;
            //            }
            //            finally
            //            {
            //                it.close();
            //            }
        }

        return selectedPlayerCount;
    }

    /**
     * @return number of players in game session, ie. watching/lost/playing (excluding ones which left game) 
     */
    public int getInGamePlayerCount()
    {
        if ( ingamePlayerCount == -1 )
        {
            ingamePlayerCount = 0;

            for ( PlayerInfoModel pim : this )
                if ( pim.isPlayerSelected() )
                {
                    switch ( pim.getPlayerState() )
                    {
                        case PlayingGame:
                        case WatchingGame:
                        case LostGame:
                            ingamePlayerCount++;
                            break;

                        default:
                            break;
                    }
                }
        }

        return ingamePlayerCount;
    }

    /**
     * @return number of active (indeed playing) players in game session, ie. not lost/left/watching/quit
     */
    public int getActivePlayerCount()
    {
        if ( activePlayerCount == -1 )
        {
            activePlayerCount = 0;

            for ( PlayerInfoModel pim : this )
            //            SyncIterator<PlayerInfoModel> it = iterator();
            //            try
            {
                //                while ( it.hasNext() )
                //                {
                //                    PlayerInfoModel pim = it.next();
                if ( pim.isPlayerSelected() && pim.getPlayerState() == PlayerState.PlayingGame )
                    activePlayerCount++;
            }
            //            }
            //            finally
            //            {
            //                it.close();
            //            }
        }

        return activePlayerCount;
    }

    /**
     * set player id to a color 
     * @param colorId color to modify
     * @param playerId player id to set
     * @return modified player
     */
    public PlayerInfoModel setPlayerId( Object sender, ColorId colorId, int playerId )
    {
        PlayerInfoModel res = getPlayerInfoModel( colorId );
        res.setPlayerId( sender, playerId );
        return res;
    }

    public void setPlayerState( Object sender, ColorId colorId, PlayerState state )
    {
        PlayerInfoModel pim = getPlayerInfoModel( colorId );
        pim.setPlayerState( sender, state );
    }

    public void setAllPlayersStateId( Object sender, PlayerState state )
    {
        //        SyncIterator<PlayerInfoModel> it = iterator();
        //        try
        //        {
        //            while ( it.hasNext() )
        //          it.next().setPlayerState( sender, state );
        //        }
        //        finally
        //        {
        //            it.close();
        //        }

        for ( PlayerInfoModel pim : this )
            pim.setPlayerState( sender, state );
    }

    public void resetAllPlayers( Object sender )
    {
        for ( PlayerInfoModel pim : this )
            pim.resetPlayer( sender );
    }

    public void setPlayerName( Object sender, ColorId colorId, String name )
    {
        PlayerInfoModel pim = getPlayerInfoModel( colorId );
        pim.setPlayerName( sender, name );
    }

    /**
     * determine if a player is present in player list
     */
    public boolean hasWinner()
    {
        for ( PlayerInfoModel pim : this )
        //        SyncIterator<PlayerInfoModel> it = iterator();
        //        try
        {
            //            while ( it.hasNext() )
            //            {
            //                PlayerInfoModel pim = it.next();
            if ( pim.getPlayerState() == PlayerState.WonGame )
                return true;
        }
        //        }
        //        finally
        //        {
        //            it.close();
        //        }

        return false;
    }

    private class PlayerIdComparator implements Comparator<PlayerInfoModel>
    {
        @Override
        public int compare( PlayerInfoModel o1, PlayerInfoModel o2 )
        {
            int id1 = o1.getPlayerId();
            int id2 = o2.getPlayerId();

            if ( id1 == id2 )
                return 0;

            return id1 < id2 ? -1 : 1;
        }
    }

    public SelectedPlayerIterator selectedPlayers()
    {
        return new SelectedPlayerIterator( this );
    }

    public class SelectedPlayerIterator extends SyncIterator<PlayerInfoModel>
    {
        private SelectedPlayerIterator( AbstractSyncList<PlayerInfoModel> l )
        {
            super( l );
        }

        @Override
        protected boolean isElementIterable( int i )
        {
            return get( i ).isPlayerSelected();
        }
    }

    // MVCModelObserver interface

    @Override
    public void modelChanged( MVCModelChange change )
    {
        if ( change.getChangeId() instanceof ListModelChangeId )
            switch ( (ListModelChangeId)change.getChangeId() )
            {
                case List_AddElement:
                case List_ListCleared:
                case List_RemoveElement:
                    resetCounts();
                    break;

                default:
                    break;
            }
        else if ( change.getChangeId() instanceof XBMVCModelChangeId )
            switch ( (XBMVCModelChangeId)change.getChangeId() )
            {
                case PlayerStateChanged:
                case PlayerIdChanged: // because of getSelectedPlayerCount() / isPlayerSelected relying on id
                    resetCounts();
                    break;

                default:
                    break;
            }
    }

    @Override
    public void subscribeModel()
    {
    }

    @Override
    public void unsubscribeModel()
    {
    }

    @Override
    public void close()
    {
    }
}
