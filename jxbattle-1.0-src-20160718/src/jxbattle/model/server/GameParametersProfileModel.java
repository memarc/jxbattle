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
import jxbattle.model.common.parameters.game.GameParametersModel;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public class GameParametersProfileModel extends MVCModelImpl
{
    // observed beans

    private GameParametersProfile gameParametersProfile;

    // models

    private GameParametersModel gameParametersModel;

    GameParametersProfileModel()
    {
        setGameParametersProfile( new GameParametersProfile() );
    }

    GameParametersProfileModel( GameParametersProfile gpp )
    {
        setGameParametersProfile( gpp );
    }

    public GameParametersProfileModel( GameParametersProfileModel gppm )
    {
        setGameParametersProfile( gppm.getGameParametersProfile().clone() );
    }

    public GameParametersProfile getGameParametersProfile()
    {
        return gameParametersProfile;
    }

    public void setGameParametersProfile( GameParametersProfile gpp )
    {
        gameParametersProfile = gpp;
    }

    public GameParametersModel getGameParametersModel()
    {
        if ( gameParametersModel == null )
            gameParametersModel = new GameParametersModel( getGameParametersProfile().getGameParameters() );

        return gameParametersModel;
    }

    public String getProfileName()
    {
        return getGameParametersProfile().getProfileName();
    }

    public void setProfileName( Object sender, String profileName )
    {
        getGameParametersProfile().setProfileName( profileName );
        notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.GameParametersProfileNameChanged, getGameParametersProfile() ) );
    }

    public boolean getReadOnly()
    {
        return getGameParametersProfile().getReadOnly();
    }

    public void setReadOnly( boolean b )
    {
        getGameParametersProfile().setReadOnly( b );
    }

    @Override
    public GameParametersProfileModel clone()
    {
        // GameParametersProfileModel res = new GameParametersProfileModel(
        // getGameParametersProfile().clone() );
        // return res;
        return new GameParametersProfileModel( this );
    }

    @Override
    public String toString()
    {
        return getGameParametersProfile().toString() + " (" + (gameParametersProfile.getReadOnly() ? "RO" : "RW") + ")";
    }
}
