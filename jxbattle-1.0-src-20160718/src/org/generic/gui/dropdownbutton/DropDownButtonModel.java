package org.generic.gui.dropdownbutton;

import org.generic.mvc.model.list.SyncListModel;
import org.generic.mvc.model.observer.MVCModelChange;

public class DropDownButtonModel extends SyncListModel<String>
{
    private String buttonText;

    public DropDownButtonModel( String bt )
    {
        buttonText = bt;
    }

    public void addItem( String i )
    {
        super.add( i );
    }

    String getButtonText()
    {
        return buttonText;
    }

    public int getItemIndex( String item )
    {
        return super.indexOf( item );
    }

    void notifyItemSelection( Object sender, String item )
    {
        MVCModelChange change = new MVCModelChange( sender, this, DropDownButtonModelChangeId.ItemSelected, item );
        notifyObservers( change );
    }
}
