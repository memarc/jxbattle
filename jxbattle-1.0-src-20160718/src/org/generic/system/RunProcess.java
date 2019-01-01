package org.generic.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * classe de creation/lancement de process externe
 * (cf egalement ProcessBuilder)
 * @author francois
 */
public class RunProcess
{
    //    private String command;
    private List<String> args;

    protected Process process;

    private long timeout;

    private String workingDir;

    private boolean finished;

    private boolean log = false;

    private String stdOutput;

    private String errOutput;

    private String interpreter; // not null if command is a script is to be run with this interpreter

    private int exitCode;

    public RunProcess( String cmd )
    {
        initialize();
        args.add( cmd );
    }

    private void initialize()
    {
        args = new ArrayList<String>();
        process = null;
        workingDir = null;
        stdOutput = new String();
        errOutput = new String();
        interpreter = null;
        timeout = 0L;
        finished = true;
    }

    @Override
    protected void finalize() throws Throwable
    {
        destroy();
    }

    public void destroy()
    {
        args = null;
        process = null;
        workingDir = null;
        stdOutput = null;
        errOutput = null;
    }

    public boolean isFinished()
    {
        return finished;
    }

    public String getStandardOutput()
    {
        return stdOutput;
    }

    public String getErrorOutput()
    {
        return errOutput;
    }

    public void addArgument( String arg )
    {
        args.add( arg );
    }

    public void setTimeOut( long t )
    {
        if ( t >= 0L )
            timeout = t;
        else
            timeout = 0L;
    }

    public void setWorkingDir( String wd )
    {
        workingDir = wd;
    }

    public void setInterpreter( String cmd )
    {
        interpreter = cmd;
    }

    public void setLog( boolean b )
    {
        log = b;
    }

    public void run()
    {
        exitCode = -1;
        Thread thOut = null;
        Thread thErr = null;
        ProcessStreamReader psrStdOut = null;
        ProcessStreamReader psrStdErr = null;

        finished = false;
        process = null;

        try
        {
            if ( interpreter != null )
                args.add( 0, interpreter );

            if ( log )
            {
                System.out.println( "RunProcess.run() : " + args.toString() );
                if ( workingDir != null )
                    System.out.println( "working dir : " + workingDir );
            }

            ProcessBuilder pb = new ProcessBuilder( args );

            // working dir

            if ( workingDir != null )
                pb.directory( new File( workingDir ) );

            // run !!!

            process = pb.start();

            // consommation des flux de sortie standard et d'erreur dans des threads séparés

            psrStdOut = new ProcessStreamReader( process, true );
            psrStdErr = new ProcessStreamReader( process, false );

            thOut = new Thread( psrStdOut );
            thErr = new Thread( psrStdErr );
            thOut.start();
            thErr.start();

            ProcessWaiter pwProcess = new ProcessWaiter( process );
            Thread processThread = new Thread( pwProcess );
            processThread.start();
            processThread.join( timeout );

            //            process.waitFor();

            exitCode = process.exitValue();

            thOut.join();
            thErr.join();

            //process.destroy();

            stdOutput = psrStdOut.getOutput();
            errOutput = psrStdErr.getOutput();

            finished = true;

            if ( log )
            {
                System.out.println( "sortie standard : " + stdOutput );
                System.out.println( "sortie erreur : " + errOutput );
                System.out.println( "code retour : " + exitCode );
            }

            //****************************
            //            if ( timeout > 0L )
            //            {
            //                ProcessRunnable prProcess = new ProcessRunnable();
            //                Thread processThread = new Thread( prProcess );
            //                processThread.start();
            //
            //                try
            //                {
            //                    processThread.join( timeout );
            //                    try
            //                    {
            //                        status = process.exitValue();
            //                    }
            //                    catch( IllegalThreadStateException itse )
            //                    {
            //                        process.destroy();
            //                        status = process.exitValue();
            //                    }
            //                }
            //                catch( InterruptedException ie )
            //                {
            //                    ie.printStackTrace();
            //                }
            //            }
            //            else if ( timeout == 0L )
            //            {
            //                try
            //                {
            //                    status = process.waitFor();
            //                }
            //                catch( InterruptedException ie )
            //                {
            //                    ie.printStackTrace();
            //                }
            //            }
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
        catch( InterruptedException ie )
        {
            ie.printStackTrace();
        }
        finally
        {
            finished = true;
        }

        //        finished = true;
        //        try
        //        {
        //            thOut.join();
        //            thErr.join();
        //        }
        //        catch( InterruptedException e )
        //        {
        //            e.printStackTrace();
        //        }
    }

    public int getExitCode()
    {
        return exitCode;
    }

    private class ProcessStreamReader implements Runnable
    {
        private Process proc;

        private String output;

        private boolean isStdOut;

        public ProcessStreamReader( Process p, boolean stdOut )
        {
            proc = p;
            output = new String();
            isStdOut = stdOut;
        }

        @Override
        protected void finalize() throws Throwable
        {
            destroy();
        }

        public void destroy()
        {
            output = null;
        }

        @Override
        public void run()
        {
            BufferedReader reader = null;
            try
            {
                if ( isStdOut )
                    reader = new BufferedReader( new InputStreamReader( proc.getInputStream() ) );
                else
                    reader = new BufferedReader( new InputStreamReader( proc.getErrorStream() ) );

                boolean first = true;
                String line;
                do
                {
                    line = reader.readLine();
                    if ( line != null )
                    {
                        if ( output == null )
                            System.out.println( line );
                        else
                        {
                            if ( first )
                            {
                                output = line;
                                first = false;
                            }
                            else
                                output = output.concat( "\n" ).concat( line );
                        }
                    }
                }
                while ( line != null );
            }
            catch( IOException e )
            {
            }
            finally
            {
                try
                {
                    if ( reader != null )
                        reader.close();
                    reader = null;
                }
                catch( IOException e )
                {
                }
            }
        }

        public String getOutput()
        {
            return output;
        }
    }

    protected class ProcessWaiter implements Runnable
    {
        private Process proc;

        public ProcessWaiter( Process p )
        {
            proc = p;
        }

        @Override
        public void run()
        {
            try
            {
                proc.waitFor();
            }
            catch( InterruptedException ie )
            {
                ie.printStackTrace();
            }
        }
    }
}

///**
// * ProcessLauncher permet de lancer une application externe en consommant 
// * les divers fluxs dans des threads separes.
// */
//public class RunProcess2
//{
//    private OutputStream out = null;
//
//    private OutputStream err = null;
//
//    private InputStream in = null;
//
//    private Process process;
//
//    private long timeout = 0L;
//
//    private boolean finished = false;
//
//    public RunProcess2()
//    {
//        this( null, null, null, 0L );
//    }
//
//    /**
//     * @param out Outputstream vers lequel sera redirige la sortie standard de 
//     *    l'application externe (null pour ne pas rediriger).
//     * @param err   Outputstream vers lequel sera redirige la sortie d'erreur de 
//     *    l'application externe (null pour ne pas rediriger). 
//     */
//    public RunProcess2( OutputStream out, OutputStream err )
//    {
//        this( out, err, null, 0L );
//    }
//
//    /**
//     * @param out Outputstream vers lequel sera redirige la sortie standard de 
//     *    l'application externe (null pour ne pas rediriger).
//     * @param err   Outputstream vers lequel sera redirige la sortie d'erreur de 
//     *    l'application externe (null pour ne pas rediriger). 
//     * @param in InputStream vers lequel sera redirige l'entree standard de 
//     *    l'application externe (null pour ne pas rediriger).
//     */
//    public RunProcess2( OutputStream out, OutputStream err, InputStream in )
//    {
//        this( out, err, in, 0L );
//    }
//
//    /**
//     * @param out Outputstream vers lequel sera redirige la sortie standard de 
//     *    l'application externe (null pour ne pas rediriger).
//     * @param err   Outputstream vers lequel sera redirige la sortie d'erreur de 
//     *    l'application externe (null pour ne pas rediriger). 
//     * @param timeout   temps en millisecondes avant de forcer l'arret de 
//     *    l'application externe (0 pour ne jamais forcer l'arret).
//     */
//    public RunProcess2( OutputStream out, OutputStream err, long timeout )
//    {
//        this( out, err, null, timeout );
//    }
//
//    /**
//     * @param out Outputstream vers lequel sera redirige la sortie standard de 
//     *    l'application externe (null pour ne pas rediriger).
//     * @param err   Outputstream vers lequel sera redirige la sortie d'erreur de 
//     *    l'application externe (null pour ne pas rediriger). 
//     * @param in InputStream vers lequel sera redirige l'entree standard de 
//     *    l'application externe (null pour ne pas rediriger).
//     * @param timeout   temps en millisecondes avant de forcer l'arret de 
//     *    l'application externe (0 pour ne jamais forcer l'arret).
//     */
//    public RunProcess2( OutputStream out, OutputStream err, InputStream in, long timeout )
//    {
//        this.out = out;
//        this.err = err;
//        this.in = in;
//        this.timeout = timeout < 0 ? 0L : timeout;
//    }
//
//    /**
//     * Execute une ligne de commande dans un processus separe.
//     *   
//     * @param command ligne de commande a executer
//     * @return valeur de retour du processus     
//     */
//    public int exec( String command ) throws IOException
//    {
//        process = Runtime.getRuntime().exec( command );
//        return execute();
//    }
//
//    /**
//     * Execute une commande avec ses parametres dans un processus separe.
//     *   
//     * @param cmdarray tableau de String contenant la commande et ses parametres
//     * @return valeur de retour du processus
//     */
//    public int exec( String[] cmdarray ) throws IOException
//    {
//        process = Runtime.getRuntime().exec( cmdarray );
//        return execute();
//    }
//
//    /**
//     * Execute une commande avec ses parametres dans un processus separe en 
//     specifiant des variables d'environnement.
//     
//     * @param cmdarray tableau de String contenant la commande et ses parametres
//     * @param envp variables d'environnement     
//     * @return valeur de retour du processus
//     */
//    public int exec( String[] cmdarray, String[] envp ) throws IOException
//    {
//        process = Runtime.getRuntime().exec( cmdarray, envp );
//        return execute();
//    }
//
//    /**
//     * Execute une commande avec ses parametres dans un processus separe en 
//     specifiant des variables d'environnement et le repertoire de travail.
//     
//     * @param cmdarray tableau de String contenant la commande et ses parametres
//     * @param envp variables d'environnement     
//     * @param dir repertoire de travail  
//     * @return valeur de retour du processus
//     */
//    public int exec( String[] cmdarray, String[] envp, File dir ) throws IOException
//    {
//        process = Runtime.getRuntime().exec( cmdarray, envp, dir );
//        return execute();
//    }
//
//    /**
//     * Execute une ligne de commande dans un processus separe en specifiant des 
//     *    variables d'environnement.
//     *       
//     * @param command ligne de commande
//     * @param envp variables d'environnement     
//     * @return valeur de retour du processus
//     */
//    public int exec( String command, String[] envp ) throws IOException
//    {
//        process = Runtime.getRuntime().exec( command, envp );
//        return execute();
//    }
//
//    /**
//     * Execute une ligne de commande dans un processus separe en specifiant des 
//     *    variables d'environnement et le repertoire de travail.
//     *       
//     * @param command ligne de commande
//     * @param envp variables d'environnement     
//     * @param dir repertoire de travail  
//     * @return valeur de retour du processus
//     */
//    public int exec( String command, String[] envp, File dir ) throws IOException
//    {
//        process = Runtime.getRuntime().exec( command, envp, dir );
//        return execute();
//    }
//
//    private int execute()
//    {
//        int status = -1;
//
//        // Consommation des fluxs de sortie standard et d'erreur dans des threads separes. 
//        createStreamThread( process.getErrorStream(), err );
//        createStreamThread( process.getInputStream(), out );
//
//        // Mapping de l'entree standard de l'application si besoin est.
//        if ( in != null )
//        {
//            createStreamThread( in, process.getOutputStream() );
//        }
//
//        if ( timeout > 0L )
//        {
//            Thread processThread = createProcessThread( process );
//            processThread.start();
//            try
//            {
//                processThread.join( timeout );
//                try
//                {
//                    status = process.exitValue();
//                }
//                catch( IllegalThreadStateException itse )
//                {
//                    process.destroy();
//                    status = process.exitValue();
//                }
//            }
//            catch( InterruptedException ie )
//            {
//                ie.printStackTrace();
//            }
//        }
//        else if ( timeout == 0L )
//        {
//            try
//            {
//                status = process.waitFor();
//            }
//            catch( InterruptedException ie )
//            {
//                ie.printStackTrace();
//            }
//        }
//        finished = true;
//        return status;
//    }
//
//    private void createStreamThread( final InputStream is, final OutputStream os )
//    {
//        new Thread( new Runnable()
//        {
//            public void run()
//            {
//                BufferedInputStream bis = new BufferedInputStream( is );
//                BufferedOutputStream bos = null;
//                if ( os != null )
//                {
//                    bos = new BufferedOutputStream( os );
//                }
//                byte[] data = new byte[ 2048 ];
//                int nbRead = 0;
//                try
//                {
//                    while ( (nbRead = bis.read( data )) > 0 )
//                    {
//                        if ( bos != null )
//                        {
//                            if ( finished )
//                            {
//                                break;
//                            }
//                            bos.write( data, 0, nbRead );
//                            bos.flush();
//                        }
//                    }
//                }
//                catch( IOException ioe )
//                {
//                    ioe.printStackTrace();
//                }
//            }
//        } ).start();
//    }
//
//    private Thread createProcessThread( final Process process )
//    {
//        return new Thread()
//        {
//            public void run()
//            {
//                try
//                {
//                    process.waitFor();
//                }
//                catch( InterruptedException ie )
//                {
//                    ie.printStackTrace();
//                }
//            }
//        };
//    }
//
//    /**
//     * Renvoie l'OutputStream vers lequel a ete redirige le flux d'erreur de 
//     *    l'application externe.
//     * 
//     *@return l'OutputStream vers lequel a ete redirige le flux d'erreur de 
//     *   l'application externe
//     */
//    public OutputStream getErrorStream()
//    {
//        return err;
//    }
//
//    /**
//     * Renvoie l'InputStream duquel les donnees sont envoyees au flux d'entree 
//     *    standard de l'application externe.
//     * 
//     *@return l'InputStream duquel les donnees sont envoyees au flux d'entree 
//     *    standard de l'application externe
//     */
//    public InputStream getInputStream()
//    {
//        return in;
//    }
//
//    /**
//     * Renvoie l'OutputStream vers lequel a ete redirige le flux de sortie 
//     *    standard de l'application externe
//     * 
//     *@return l'OutputStream vers lequel a ete redirige le flux de sortie 
//     *    standard de l'application externe
//     */
//    public OutputStream getOutputStream()
//    {
//        return out;
//    }
//
//    /**
//     * Renvoie le timeout.
//     * 
//     *@return le timeout
//     */
//    public long getTimeout()
//    {
//        return timeout;
//    }
//
//    /**
//     * Specifie l'Outputstream vers lequel sera redirige la sortie d'erreur de 
//     *    l'application externe.
//     *       
//     * @param err   Outputstream vers lequel sera redirige la sortie d'erreur de 
//     *    l'application externe (null pour ne pas rediriger)
//     */
//    public void setErrorStream( OutputStream err )
//    {
//        this.err = err;
//    }
//
//    /** 
//     * Specifie l'InputStream vers lequel sera redirige l'entree standard de 
//     *    l'application externe.
//     *       
//     * @param in InputStream vers lequel sera redirige l'entree standard de 
//     *    l'application externe (null pour ne pas rediriger)
//     */
//    public void setInputStream( InputStream in )
//    {
//        this.in = in;
//    }
//
//    /**
//     * Specifie l'Outputstream vers lequel sera redirige la sortie standard de 
//     *    l'application externe.
//     *       
//     * @param out Outputstream vers lequel sera redirige la sortie standard de 
//     *    l'application externe (null pour ne pas rediriger)
//     */
//    public void setOutputStream( OutputStream out )
//    {
//        this.out = out;
//    }
//
//    /**
//     *  Specifie le timeout temps en millisecondes avant de forcer l'arret de 
//     *    l'application externe.
//     *       
//     * @param timeout   temps en millisecondes avant de forcer l'arret de 
//     *    l'application externe (0 pour ne jamais forcer l'arret)
//     */
//    public void setTimeout( long timeout )
//    {
//        this.timeout = timeout;
//    }

