package org.generic.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TextLineIterator extends StringIterator
{
    public TextLineIterator( String t )
    {
        super( t, '\n' );
    }

    /*
        private byte[] bytes;

        private int byteLen;

        private int start = 0; // line start index

        private int end = 0; // line end index

        public TextLineIterator( String t )
        {
            bytes = t.getBytes();
            byteLen = bytes.length;
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

            while ( end < byteLen && bytes[ end ] != '\n' )
                end++;

            String res = new String( bytes, start, end - start );
            if ( end < byteLen && bytes[ end ] == '\n' )
                end++;
            start = end;
            return res;
        }

        @Override
        public void remove()
        {
        }
        */

    private static void tests() throws Exception
    {
        Map<String, List<String>> tests = new HashMap<>();

        tests.put( "", new ArrayList<String>() );
        tests.put( "\n", new ArrayList<>( Arrays.asList( "" ) ) );
        tests.put( "\n\n", new ArrayList<>( Arrays.asList( "", "" ) ) );

        tests.put( "aaaaa", new ArrayList<>( Arrays.asList( "aaaaa" ) ) );
        tests.put( "aaaaa\n", new ArrayList<>( Arrays.asList( "aaaaa" ) ) );
        tests.put( "\naaaaa", new ArrayList<>( Arrays.asList( "", "aaaaa" ) ) );
        tests.put( "\n\naaaaa", new ArrayList<>( Arrays.asList( "", "", "aaaaa" ) ) );
        tests.put( "\naaaaa\n", new ArrayList<>( Arrays.asList( "", "aaaaa" ) ) );
        tests.put( "\n\naaaaa\n", new ArrayList<>( Arrays.asList( "", "", "aaaaa" ) ) );
        tests.put( "aaaaa\n\n", new ArrayList<>( Arrays.asList( "aaaaa", "" ) ) );
        tests.put( "aaaaa\n\n\n", new ArrayList<>( Arrays.asList( "aaaaa", "", "" ) ) );

        tests.put( "aaaaa\nbbb", new ArrayList<>( Arrays.asList( "aaaaa", "bbb" ) ) );
        tests.put( "aaaaa\nbbb\n", new ArrayList<>( Arrays.asList( "aaaaa", "bbb" ) ) );
        tests.put( "\naaaaa\nbbb", new ArrayList<>( Arrays.asList( "", "aaaaa", "bbb" ) ) );
        tests.put( "\naaaaa\nbbb\n", new ArrayList<>( Arrays.asList( "", "aaaaa", "bbb" ) ) );

        tests.put( "aaaaa\tbbb", new ArrayList<>( Arrays.asList( "aaaaa\tbbb" ) ) );
        tests.put( "aaaaaébbb", new ArrayList<>( Arrays.asList( "aaaaaébbb" ) ) );

        //        tests.put( "XP_009440074.1\t>ref|XP_009440074.1| PREDICTED: LOW QUALITY PRO...\t39\tHOMO SA...,HOMO SA...\t27 ILIRSVAALLSTAALQSCCECYQSFHHRGKMQQSFTHHTH 66\t1", new ArrayList<>( Arrays.asList( "XP_009440074.1\t>ref|XP_009440074.1| PREDICTED: LOW QUALITY PRO...\t39\tHOMO SA...,HOMO SA...\t27 ILIRSVAALLSTAALQSCCECYQSFHHRGKMQQSFTHHTH 66\t1" ) ) );

        for ( Entry<String, List<String>> e : tests.entrySet() )
        {
            StringIterator it = new StringIterator( e.getKey(), '\n' );
            List<String> res = new ArrayList<>();

            while ( it.hasNext() )
                res.add( it.next() );

            if ( res.size() != e.getValue().size() )
                throw new Exception( "test fail on " + e );

            res.removeAll( e.getValue() );
            if ( res.size() != 0 )
                throw new Exception( "test fail on " + e );
        }

        System.out.println( "tests OK" );
    }

    public static void main( String[] args ) throws Exception
    {
        tests();
    }
}
