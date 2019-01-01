package org.generic.gui.expandablepanel;

import org.generic.NumericUtils;
import org.generic.mvc.model.observer.MVCModelChange;
import org.generic.mvc.model.observer.MVCModelImpl;
import org.generic.string.StringUtils;

public class ExpandablePanelModel extends MVCModelImpl
{
    private String buttonText;

    private boolean expanded;

    public ExpandablePanelModel()
    {
        expanded = false;
    }

    public String getButtonText()
    {
        return buttonText;
    }

    public void setButtonText( Object sender, String t )
    {
        boolean changed = !StringUtils.equalsNotNull( buttonText, t );
        if ( changed )
        {
            buttonText = t;
            MVCModelChange change = new MVCModelChange( sender, this, ExpandablePanelModelChangeId.ButtonTextChanged );
            notifyObservers( change );
        }
    }

    public boolean isExpanded()
    {
        return expanded;
    }

    public void setExpanded( Object sender, boolean b )
    {
        boolean changed = !NumericUtils.booleanIdentity( expanded, b );
        if ( changed )
        {
            expanded = b;
            MVCModelChange change = new MVCModelChange( sender, this, ExpandablePanelModelChangeId.ExpansionChanged );
            notifyObservers( change );
        }
    }
}
