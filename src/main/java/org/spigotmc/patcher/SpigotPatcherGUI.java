package org.spigotmc.patcher;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;

public class SpigotPatcherGUI extends JFrame
{

    private static final long serialVersionUID = -2502141083903859399L;

    public SpigotPatcherGUI()
    {
        super( "SpigotPatcher v" + SpigotPatcher.VERSION );

        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setLookAndFeel();
        initialize();

        EventQueue.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                setVisible( true );
            }
        } );
    }

    // Components
    private JTextField originalFilePath;
    private JTextField patchFilePath;
    private JTextField outputFilePath;
    private JTextArea outputTextArea;

    private final void initialize()
    {
        originalFilePath = new JTextField();
        originalFilePath.setColumns( 10 );

        JLabel lblOriginal = new JLabel( "Original Jar - MUST be Spigot #1649" );

        JButton btnOriginalBrowse = new JButton( "Browse" );
        btnOriginalBrowse.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                JFileChooser fc = new JFileChooser();
                if ( fc.showOpenDialog( SpigotPatcherGUI.this ) == 0 )
                {
                    File originalFile = fc.getSelectedFile();
                    originalFilePath.setText( originalFile.getPath() );
                }
            }
        } );

        patchFilePath = new JTextField();
        patchFilePath.setColumns( 10 );

        JLabel lblPatchFile = new JLabel( "Patch File - Leave blank to automatically download" );

        JButton btnPatchBrowse = new JButton( "Browse" );
        btnPatchBrowse.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                JFileChooser fc = new JFileChooser();
                if ( fc.showOpenDialog( SpigotPatcherGUI.this ) == 0 )
                {
                    File patchFile = fc.getSelectedFile();
                    patchFilePath.setText( patchFile.getPath() );
                }
            }
        } );

        JLabel lblOutputFile = new JLabel( "Output File" );

        outputFilePath = new JTextField();
        outputFilePath.setColumns( 10 );

        JButton btnOutputBrowse = new JButton( "Browse" );
        btnOutputBrowse.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                JFileChooser fc = new JFileChooser();
                if ( fc.showOpenDialog( SpigotPatcherGUI.this ) == 0 )
                {
                    File outputFile = fc.getSelectedFile();
                    outputFilePath.setText( outputFile.getPath() );
                }
            }
        } );

        JScrollPane scrollPane = new JScrollPane();

        JButton btnRun = new JButton( "Run" );
        btnRun.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                if ( originalFilePath.getText().isEmpty() )
                {
                    System.err.println( "You must select the original file!" );
                    return;
                }

                File originalFile = new File( originalFilePath.getText() );

                File patchFile = null;
                if ( !patchFilePath.getText().isEmpty() )
                {
                    patchFile = new File( patchFilePath.getText() );
                } else
                {
                    System.out.println( "Patch file unspecified, attempting to download automatcally." );

                    try
                    {
                        patchFile = downloadAutomatcally();
                    } catch ( IOException ex )
                    {
                        System.err.println( "***** Encountered an exception whilst downloading patch automatcially." );
                        ex.printStackTrace();
                    }

                    if ( patchFile == null )
                    {
                        System.out.println( "Failed to automatcally download patch file!" );
                        System.out.println( "Please download it manually from " + DOWNLOAD_URL );
                        return;
                    }
                }

                if ( outputFilePath.getText().isEmpty() )
                {
                    System.err.println( "You must select the output file!" );
                    return;
                }

                File outputFile = new File( outputFilePath.getText() );

                SpigotPatcher.patch( originalFile, patchFile, outputFile );
            }
        } );

        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblOriginal)
                                .addComponent(lblPatchFile)
                                .addComponent(lblOutputFile)
                                .addGroup(groupLayout.createSequentialGroup()
                                    .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                                        .addComponent(outputFilePath, Alignment.LEADING)
                                        .addComponent(patchFilePath, Alignment.LEADING)
                                        .addComponent(originalFilePath, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE))
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(btnOutputBrowse)
                                        .addComponent(btnPatchBrowse)
                                        .addComponent(btnOriginalBrowse))))
                            .addGap(50))
                        .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE))
                    .addContainerGap())
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap(294, Short.MAX_VALUE)
                    .addComponent(btnRun)
                    .addGap(283))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblOriginal)
                    .addGap(3)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(originalFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnOriginalBrowse))
                    .addGap(4)
                    .addComponent(lblPatchFile)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(patchFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPatchBrowse))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(lblOutputFile)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(outputFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnOutputBrowse))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 270, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(btnRun)
                    .addContainerGap(5, Short.MAX_VALUE))
        );

        // Output Area
        outputTextArea = new JTextArea();
        scrollPane.setViewportView( outputTextArea );
        outputTextArea.setEditable( false );
        outputTextArea.setColumns( 20 );
        outputTextArea.setLineWrap( true );

        // Replace the default out and err streams w/ our text area
        System.setOut( new PrintStream( new TextAreaOutputStream( outputTextArea, "[INFO]" ) ) );
        System.setErr( new PrintStream( new TextAreaOutputStream( outputTextArea, "[ERROR]" ) ) );

        getContentPane().setLayout( groupLayout );
        setSize( 650, 525 );

        System.out.println( "Welcome to the Spigot patch applicator." );
        setIconImage();
    }

    private static final String DOWNLOAD_URL = "http://www.spigotmc.org/spigot-updates/";
    private static final Pattern PATCH_PATTERN = Pattern.compile( "spigot([-+]\\d+)([a-z])\\.bps" );

    private final File downloadAutomatcally() throws IOException
    {
        URL url = new URL( DOWNLOAD_URL );
        System.out.println( "Checking for available patches from " + url.getPath() );

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod( "GET" );
        connection.setRequestProperty( "User-Agent", "SpigotPatcher/" + SpigotPatcher.VERSION );

        List<String> availablePatches = new ArrayList<String>();

        InputStreamReader isr = new InputStreamReader( connection.getInputStream() );
        BufferedReader in = new BufferedReader( isr );
        String line = null;
        while ( ( line = in.readLine() ) != null )
        {
            Matcher matcher = PATCH_PATTERN.matcher( line );
            while ( matcher.find() )
                availablePatches.add( matcher.group() );
        }
        in.close();
        isr.close();
        connection.disconnect();

        if ( availablePatches.isEmpty() )
            throw new IOException( "Did not find any patches to download!" );

        String latest = availablePatches.get( availablePatches.size() - 1 );
        url = new URL( DOWNLOAD_URL + latest );
        System.out.println( "Downloading latest patch from " + url.getPath() );

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod( "GET" );
        connection.setRequestProperty( "User-Agent", "SpigotPatcher/" + SpigotPatcher.VERSION );

        File file = new File( latest );
        if ( file.exists() )
        {
            System.out.println( "Using existing patch file: " + file );
            return file;
        }

        BufferedInputStream bis = new BufferedInputStream( connection.getInputStream() );
        FileOutputStream fos = new FileOutputStream( file );
        BufferedOutputStream bos = new BufferedOutputStream( fos );

        int i = 0;
        while ( ( i = bis.read() ) != -1 )
            bos.write( i );
        bos.flush();
        bos.close();
        fos.close();
        bis.close();
        return file;
    }

    // TODO: This scales poorly
    private final void setIconImage()
    {
        try
        {
            InputStream stream = SpigotPatcher.class.getResourceAsStream( "/spigot.png" );
            if ( stream != null )
            {
                Image image = ImageIO.read( stream );
                setIconImage( image );
            }
        } catch ( Exception ex ) { }
    }

    private final void setLookAndFeel()
    {
        try
        {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        } catch ( Exception ex ) { }
    }
}