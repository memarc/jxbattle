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

package org.generic.mvc.model.observer;

import org.generic.LogUtils;
import org.generic.bean.Message;

/**
 * informations sent when model notifies a change
 * 
 */
public class MVCModelChange extends Message
{
    private Object sender;

    private MVCModelChangeId changeId;

    private MVCModel sourceModel;

    public MVCModelChange( Object sndr, MVCModel mdl, MVCModelChangeId id )
    {
        sender = sndr;
        sourceModel = mdl;
        changeId = id;
    }

    public MVCModelChange( Object sndr, MVCModel mdl, MVCModelChangeId id, Object data )
    {
        sender = sndr;
        sourceModel = mdl;
        changeId = id;
        setData( data );
    }

    public MVCModel getSourceModel()
    {
        return sourceModel;
    }

    public Object getSender()
    {
        return sender;
    }

    public MVCModelChangeId getChangeId()
    {
        return changeId;
    }

    @Override
    public String toString()
    {
        //return "sender=" + System.identityHashCode( sender ) + " id=" + getChangeId() + (getChangedObject() == null ? "null" : " " + getChangedObject());
        return "sender=" + LogUtils.objectIdentity( sender ) + (sender == sourceModel ? "" : ", model=" + LogUtils.objectIdentity( sourceModel )) + ", changeid=" + getChangeId() + ", val=" + (getData() == null ? "null" : getData());
    }

    public String toString( boolean printModel )
    {
        return "sender=" + LogUtils.objectIdentity( sender ) + ", changeid=" + getChangeId() + ", val=" + (getData() == null ? "null" : getData()) + (printModel ? ", model=" + LogUtils.objectIdentity( sourceModel ) : "");
    }
}
