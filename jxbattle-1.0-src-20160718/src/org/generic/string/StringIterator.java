package org.generic.string;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class StringIterator implements Iterator<String>
{
    /*
    int cursor;       // index of next element to return
     int lastRet = -1; // index of last element returned; -1 if no such

     public boolean hasNext() {
         return cursor != size;
     }

     @SuppressWarnings("unchecked")
     public E next() {
         int i = cursor;
         if (i >= size)
             throw new NoSuchElementException();
         Object[] elementData = ArrayList.this.elementData;
         cursor = i + 1;
         return (E) elementData[lastRet = i];
     }
    */

    //    private String text;

    private byte[] bytes;

    private int byteLen;

    private int start = 0; // line start index

    private int end = 0; // line end index

    private char separator;

    public StringIterator( String t, char sep )
    {
        //        text = t;
        bytes = t.getBytes();
        byteLen = bytes.length;
        //        byteLen = t.length();
        separator = sep;
    }

    @Override
    public boolean hasNext()
    {
        return start < byteLen;
    }

    @Override
    public String next()
    {
        if ( !hasNext() )
            throw new NoSuchElementException();

        while ( end < byteLen && bytes[ end ] != separator )
            end++;

        String res = new String( bytes, start, end - start );
        if ( end < byteLen && bytes[ end ] == separator )
            end++;
        start = end;
        return res;
    }

    @Override
    public void remove()
    {
    }
}
