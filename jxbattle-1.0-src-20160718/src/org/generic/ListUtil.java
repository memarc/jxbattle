package org.generic;

import java.util.AbstractList;
import java.util.List;

public class ListUtil
{
    /*
      cf.http://stackoverflow.com/questions/933447/how-do-you-cast-a-list-of-supertypes-to-a-list-of-subtypes

      void test(List<TestA> listA, List<TestB> listB)
      {
        List<TestB> castedB = ListUtil.cast(listA); // all items are blindly casted
        List<TestB> convertedB = ListUtil.<TestB, TestA>convert(listA); // wrong cause TestA does not extend TestB
        List<TestA> convertedA = ListUtil.<TestA, TestB>convert(listB); // OK all items are safely casted
      }
     */

    public static <TCastTo, TCastFrom extends TCastTo> List<TCastTo> convert( final List<TCastFrom> list )
    {
        return new AbstractList<TCastTo>()
        {
            @Override
            public TCastTo get( int i )
            {
                return list.get( i );
            }

            @Override
            public int size()
            {
                return list.size();
            }
        };
    }

    public static <TCastTo, TCastFrom> List<TCastTo> cast( final List<TCastFrom> list )
    {
        return new AbstractList<TCastTo>()
        {
            @SuppressWarnings("unchecked")
            @Override
            public TCastTo get( int i )
            {
                return (TCastTo)list.get( i );
            }

            @Override
            public int size()
            {
                return list.size();
            }
        };
    }
}
