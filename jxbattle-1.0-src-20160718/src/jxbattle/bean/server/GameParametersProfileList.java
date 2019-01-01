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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameParametersProfileList implements Iterable<GameParametersProfile>
{
    private List<GameParametersProfile> gameProfiles;

    private GameParametersProfile currentProfile;

    public GameParametersProfileList()
    {
        gameProfiles = new ArrayList<GameParametersProfile>();
    }

    @Override
    public Iterator<GameParametersProfile> iterator()
    {
        return gameProfiles.iterator();
    }

    public GameParametersProfile getCurrentProfile()
    {
        return currentProfile;
    }

    public void setCurrentProfile( GameParametersProfile gpf )
    {
        currentProfile = gpf;
    }

    public void add( GameParametersProfile gpf )
    {
        gameProfiles.add( gpf );
    }
}
