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

package jxbattle.model.common;

import jxbattle.bean.common.net.PortParameter;
import jxbattle.model.common.observer.XBMVCModelChangeId;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.parameter.IntParameterModel;

public class PortParameterModel extends IntParameterModel
{
    PortParameterModel( PortParameter v )
    {
        super( v );
    }

    @Override
    public void setValue( Object sender, int port )
    {
        boolean changed = intParameter.getValue() != port;
        if ( changed )
        {
            intParameter.setValue( port );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.PeerPortChanged ) );
        }
    }
}
