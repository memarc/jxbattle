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

import jxbattle.bean.server.GameParametersProfile;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.model.list.SyncListModel;
import org.generic.mvc.model.observer.MVCModelChangeId;

public class GameParametersProfilesModel extends SyncListModel<GameParametersProfileModel>
{
    // observed beans

    //    private GameParametersProfileList gameParametersProfileList;

    // models

    //    private GameParametersProfileModel currentGameParametersProfileModel;

    //    public GameParametersProfilesModel( GameParametersProfileList gppl )
    public GameParametersProfilesModel()
    {
        //        gameParametersProfileList = gppl;
        setAutoCurrent( true );
    }

    //    public GameParametersProfileModel getCurrentProfileModel()
    //    {
    //        return currentGameParametersProfileModel;
    //    }
    //
    //    public void setCurrentProfileModel( Object sender, GameParametersProfileModel gpfm )
    //    {
    //        currentGameParametersProfileModel = gpfm;
    //        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.GameParametersProfileChanged, currentGameParametersProfileModel ) );
    //    }

    @Override
    protected MVCModelChangeId getCurrentElementChangeId()
    {
        return XBMVCModelChangeId.GameParametersProfileChanged;
    }

    void setCurrentProfileModel( Object sender, String profileName )
    {
        for ( GameParametersProfileModel gpfm : this )
            if ( gpfm.getProfileName().equals( profileName ) )
            {
                setCurrent( sender, gpfm );
                break;
            }
    }

    private GameParametersProfileModel getProfileFromName( String profileName )
    {
        for ( GameParametersProfileModel gpf : this )
            //        SyncIterator<GameParametersProfileModel> it = iterator();
            //        try
            //        {
            //            while ( it.hasNext() )
            //            {
            //                GameParametersProfileModel gpf = it.next();
            if ( gpf.getProfileName().equals( profileName ) )
                return gpf;
        //            }
        //        }
        //        finally
        //        {
        //            it.close();
        //        }

        return null;
    }

    public GameParametersProfileModel cloneCurrentProfileModel( Object sender )
    {
        //        GameParametersProfileModel newProfile = new GameParametersProfileModel( currentGameParametersProfileModel );
        GameParametersProfileModel newProfile = new GameParametersProfileModel( getCurrent() );
        newProfile.setReadOnly( false );

        //        StringBuilder sb = new StringBuilder( currentGameParametersProfileModel.getProfileName() );
        StringBuilder sb = new StringBuilder( getCurrent().getProfileName() );
        while ( getProfileFromName( sb.toString() ) != null )
            sb.append( " (copy)" );
        newProfile.setProfileName( sender, sb.toString() );

        return newProfile;
    }

    public boolean isDuplicateProfileName( String profileName )
    {
        GameParametersProfileModel gpf = getProfileFromName( profileName );
        //        boolean isDuplicate = gpf != null && gpf != currentGameParametersProfileModel;
        boolean isDuplicate = gpf != null && gpf != getCurrent();
        return isDuplicate;
    }

    //    @Override
    //    public boolean add( Object sender, GameParametersProfileModel pm )
    //    {
    //        if ( currentGameParametersProfileModel == null )
    //            currentGameParametersProfileModel = pm;
    //
    //        return super.add( sender, pm );
    //    }

    public boolean add( Object sender, GameParametersProfile gpf )
    {
        //        gameParametersProfileList.add( gpf );
        //
        //        GameParametersProfileModel gpfm = new GameParametersProfileModel( gpf );
        //        boolean res = super.add( sender, gpfm );
        //        if ( currentGameParametersProfileModel == null )
        //            currentGameParametersProfileModel = gpfm;
        //        return res;
        return super.add( sender, new GameParametersProfileModel( gpf ) );
    }

    //    @Override
    //    public boolean remove( Object sender, GameParametersProfileModel pm )
    //    {
    //        boolean res = super.remove( sender, pm );
    //
    //        if ( getCurrentProfileModel() == pm )
    //            setCurrentProfileModel( sender, get( size() - 1 ) );
    //
    //        return res;
    //    }

    //    public GameParametersProfileList getProfileList()
    //    {
    //        return gameParametersProfileList;
    //    }
}
