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

package org.generic.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * mother class for network/MVC messages 
 */
public abstract class Message
{
    //    /**
    //     * user data
    //     */
    //    private Object data;
    //
    //    private List<Object> extraData;
    //
    //    private List<Object> getExtraData()
    //    {
    //        if ( extraData == null )
    //            extraData = new ArrayList<Object>();
    //        return extraData;
    //    }
    //
    //    public Object getData()
    //    {
    //        return data;
    //    }
    //
    //    public void setData( Object d )
    //    {
    //        data = d;
    //    }
    //
    //    public Object getExtraData( int index )
    //    {
    //        return getExtraData().get( index );
    //    }
    //
    //    public void addExtraData( Object d )
    //    {
    //        getExtraData().add( d );
    //    }

    private List<Object> data;

    public List<Object> getDataArray()
    {
        if ( data == null )
            data = new ArrayList<>();
        return data;
    }

    public void setDataArray( List<Object> d )
    {
        data = d;
    }

    public Object getData()
    {
        if ( getDataArray().size() == 0 )
            return null;

        return getDataArray().get( 0 );
    }

    public void setData( Object d )
    {
        if ( getDataArray().size() != 0 )
            throw new Error( "cannot re-set data" );
        data.add( d );
    }

    public void addData( Object d )
    {
        getDataArray().add( d );
    }
}
