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

package jxbattle.bean.server;

import jxbattle.bean.common.parameters.game.GameParameters;

public class GameParametersProfile implements Cloneable
{
    private GameParameters gameParameters;

    private String profileName;

    private boolean readOnly;

    public GameParametersProfile()
    {
        gameParameters = new GameParameters();
    }

    public GameParametersProfile( GameParametersProfile gpp )
    {
        gameParameters = new GameParameters( gpp.gameParameters );
        profileName = gpp.profileName;
        readOnly = gpp.readOnly;
    }

    @Override
    public GameParametersProfile clone()
    {
        //        try
        //        {
        //            GameParametersProfile res = (GameParametersProfile)super.clone();
        //
        //            res.gameParameters = gameParameters.clone();
        //
        //            return res;
        //        }
        //        catch( CloneNotSupportedException e )
        //        {
        //            e.printStackTrace();
        //            return null;
        //        }
        return new GameParametersProfile( this );
    }

    public boolean getReadOnly()
    {
        return readOnly;
    }

    public void setReadOnly( boolean ro )
    {
        readOnly = ro;
    }

    public GameParameters getGameParameters()
    {
        return gameParameters;
    }

    public void setGameParameters( GameParameters parameters )
    {
        gameParameters = parameters;
    }

    public String getProfileName()
    {
        return profileName;
    }

    public void setProfileName( String name )
    {
        profileName = name;
    }

    @Override
    public String toString() // for combobox items, etc..
    {
        return getProfileName();
    }
}
