package org.generic.bean.definedvalue;

public interface IDefinedValue<T>
{
    public boolean isDefined();

    public void undefine() throws Exception;

    public T getValue();
}
