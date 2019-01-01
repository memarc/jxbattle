package org.generic.bean.parameter2;

import org.generic.mvc.model.MVCModelError;

public class IntLessThanConstraint extends LessThanConstraint<Integer>
{
    public IntLessThanConstraint( IntParameter v1, IntParameter v2 )
    {
        super( v1, v2 );
    }

    @Override
    public void check()
    {
        if ( value1.getValue() >= value2.getValue() )
            throw new MVCModelError( "error : v1 >= v2" );
    }

}
