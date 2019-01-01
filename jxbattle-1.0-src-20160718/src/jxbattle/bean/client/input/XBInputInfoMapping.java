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

package jxbattle.bean.client.input;

import java.util.HashMap;
import java.util.Map;

import jxbattle.bean.common.game.UserCommand.UserCommandCode;

public class XBInputInfoMapping
{
    private Map<XBInputInfo, UserCommandCode> map;

    public XBInputInfoMapping()
    {
        map = new HashMap<>();
        //        map = new HashashMap<>();
    }

    public void add( XBInputInfo ii, UserCommandCode ucc )
    {
        assert (get( ii ) == null);
        map.put( ii, ucc );
    }

    public UserCommandCode get( XBInputInfo ii )
    {
        return map.get( ii );
    }
}
