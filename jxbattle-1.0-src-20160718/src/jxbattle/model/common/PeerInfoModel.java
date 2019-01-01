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

import org.generic.bean.definedvalue.DefinedString;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.net.PeerInfo;

public class PeerInfoModel extends MVCModelImpl
{
    private DefinedString host;

    private PortParameterModel portParameterModel;

    public PeerInfoModel( PeerInfo pi )
    {
        host = new DefinedString();
        if ( pi != null )
        {
            host.setValue( pi.getHost() );
            getPortParameterModel().setValue( this, pi.getPort() );
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        if ( portParameterModel != null )
        {
            portParameterModel.removeAllObservers();
            portParameterModel = null;
        }
        super.finalize();
    }

    public int getPort()
    {
        return getPortParameterModel().getValue();
    }

    public void setPort( Object sender, int port )
    {
        getPortParameterModel().setValue( sender, port );
    }

    public String getHost()
    {
        return host.getValue();
    }

    public PeerInfo getPeerInfo()
    {
        return new PeerInfo( getHost(), getPort() );
    }

    public void setHost( Object sender, String h )
    {
        boolean changed = !host.equals( h );
        if ( changed )
        {
            host.setValue( h );
            notifyObservers( new MVCModelChange( sender, this, XBMVCModelChangeId.PeerHostChanged ) );
        }
    }

    public void set( Object sender, PeerInfo pi )
    {
        setHost( sender, pi.getHost() );
        setPort( sender, pi.getPort() );
    }

    public PortParameterModel getPortParameterModel()
    {
        if ( portParameterModel == null )
        {
            portParameterModel = new PortParameterModel( new PortParameter() );
            portParameterModel.addObservers( getObservers() );
        }

        return portParameterModel;
    }

    @Override
    public String toString()
    {
        return host + ":" + getPortParameterModel().getValue();
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof PeerInfoModel )
        {
            PeerInfoModel pim = (PeerInfoModel)obj;
            return host.equals( pim.getHost() ) && getPortParameterModel().equals( pim.getPortParameterModel() );
        }

        return false;
    }
}
