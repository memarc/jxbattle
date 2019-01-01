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

package org.generic.mvc.model.logmessage;

import org.generic.bean.TextMessage;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public class LogMessageModel extends MVCModelImpl
{
    //    private static LogMessageModel instance;

    //    private LogMessageModel()
    //    {
    //    }

    //    public static LogMessageModel getInstance()
    //    {
    //        if ( instance == null )
    //            instance = new LogMessageModel();
    //
    //        return instance;
    //    }

    public void errorMessage( Object sender, String s )
    {
        notifyObservers( new MVCModelChange( sender, this, LogMessageModelChangeId.ErrorMessage, new TextMessage( s ) ) );
    }

    public void errorMessage( Object sender, String s, Throwable e )
    {
        notifyObservers( new MVCModelChange( sender, this, LogMessageModelChangeId.ErrorMessage, new TextMessage( s, e ) ) );
    }

    public void infoMessage( Object sender, String s )
    {
        notifyObservers( new MVCModelChange( sender, this, LogMessageModelChangeId.InfoMessage, new TextMessage( s ) ) );
    }

    //    public void infoMessage( Object sender, String s, Exception e )
    //    {
    //        notifyObservers( new MVCModelChange( sender, this, LogMessageModelChangeId.InfoMessage, new TextMessage( s, e ) ) );
    //    }

    public void guiMessage( Object sender, String s )
    {
        notifyObservers( new MVCModelChange( sender, this, LogMessageModelChangeId.GuiMessage, new TextMessage( s ) ) );
    }

    public void guiMessage( Object sender, String s, Throwable e )
    {
        notifyObservers( new MVCModelChange( sender, this, LogMessageModelChangeId.GuiMessage, new TextMessage( s, e ) ) );
    }
}
