package org.generic.gui.tristatebutton;

import java.util.Arrays;
import java.util.List;

import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;

public class TristateButtonModel extends MVCModelImpl
{
    private int state;

    private List<String> statesText;

    public TristateButtonModel()
    {
        state = 0;
    }

    private void notifyStateChanged( Object sender )
    {
        MVCModelChange change = new MVCModelChange( sender, this, TristateButtonModelChangeId.TristateButtonStateChanged, Integer.valueOf( state ) );
        notifyObservers( change );
    }

    public String getButtonText()
    {
        return statesText.get( state );
    }

    public void setStatesText( Object sender, List<String> st )
    {
        statesText = st;
        //        notifyStateChanged( sender );
    }

    public void setStatesText( Object sender, String[] strings )
    {
        setStatesText( sender, Arrays.asList( strings ) );
    }

    public int getState()
    {
        return state;
    }

    public void setState( Object sender, int s )
    {
        boolean changed = state != s;
        if ( changed )
        {
            if ( s < 0 )
                state = 0;
            else if ( s > 2 )
                state = 2;
            else
                state = s;
            notifyStateChanged( sender );
        }
    }

    public void nextState( Object sender )
    {
        state = (state + 1) % 3;
        notifyStateChanged( sender );
    }
}
