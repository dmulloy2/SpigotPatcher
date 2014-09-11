package org.spigotmc.patcher;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextAreaOutputStream extends OutputStream
{

    private final StringBuilder sb = new StringBuilder();
    private final JTextArea textArea;
    private final String prefix;

    public TextAreaOutputStream(JTextArea textArea, String prefix)
    {
        this.textArea = textArea;
        this.prefix = prefix;
    }

    @Override
    public void write(int b) throws IOException
    {
        if ( b == '\r' )
            return;

        if ( b == '\n' )
        {
            final String text = sb.toString() + "\n";
            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    textArea.append( prefix + " " + text );
                }
            } );
            sb.setLength( 0 );
            return;
        }

        sb.append( (char) b );
    }
}