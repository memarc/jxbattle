package org.generic.gui.logpanel;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LogPanelView extends JPanel
{
    private JScrollPane scrollPane;

    private JTextArea textArea;

    public LogPanelView()
    {
        setLayout( new BorderLayout( 0, 0 ) );
        add( getScrollPane(), BorderLayout.CENTER );
    }

    JScrollPane getScrollPane()
    {
        if ( scrollPane == null )
        {
            scrollPane = new JScrollPane();
            scrollPane.setViewportView( getTextArea() );
        }
        return scrollPane;
    }

    JTextArea getTextArea()
    {
        if ( textArea == null )
        {
            textArea = new JTextArea();
            textArea.setEditable( false ); // to avoid edition and selection events 
            textArea.setEnabled( false );
            textArea.setLineWrap( true );
            textArea.setDisabledTextColor( Color.BLACK );
            textArea.setDocument( new SafePlainDocument() );

        }
        return textArea;
    }

    class SafePlainDocument extends PlainDocument
    {
        public void append( String s )
        {
            writeLock();
            try
            {
                insertString( getLength(), s, null );
            }
            catch( BadLocationException e )
            {
                e.printStackTrace();
            }
            finally
            {
                writeUnlock();
            }
        }
    }
}
