package org.generic.mvc.model;

import org.generic.mvc.model.observer.MVCModel;

public interface IBoolModel extends MVCModel
{
    public boolean getValue();

    public void setValue( Object sender, boolean v );
}
