import java.io.*;
import java.util.*;
import java.util.zip.*;
/**
 * Quick and dirty single class bootstrap utility for getting Ant off
 * the ground when in need. To use, compile this file in the directory
 * where the source code is in the repository, then execute it. That's
 * it.<p>
 *
 * No pretense is made that this is an elegant peice of code. This code
 * only exists to do a ground zero build of Ant. Any other building of
 * Ant should be done with itself whenever possible.
 *
 * @author James Duncan Davidson (duncan@apache.org)
 * @author Conor MacNeill (conor@m64.com)
 */
public class Bootstrap {
    /**
     * Command line entry point. This is the first part of the bootstrap
     * where we go and set up the environment and generally do what is
     * necessary to set up for Bootstrapping.
     */
    public static void main(java.lang.String[] args) throws java.lang.Exception {
        java.lang.String[] command;
        java.lang.String classpath = null;
        // check to see if we have a compiler on the classpath. Right now
        // we're just checking for the old compiler, but will want to check
        // for the new compiler and use it if it exists. Later.
        try {
            java.lang.Class clazz = java.lang.Class.forName("sun.tools.javac.Main");
        } catch (java.lang.ClassNotFoundException cnfe) {
            java.lang.String javaHome = java.lang.System.getProperty("java.home");
            if (javaHome.endsWith("jre")) {
                javaHome = javaHome.substring(0, javaHome.length() - 4);
            }
            // XXX should check if this exists and bail out if it doesn't
            classpath = ((javaHome + "/lib/tools.jar") + java.io.File.pathSeparator) + ".";
        }
        // XXX really should check to see if compiling the bootstrap is necessary. :)
        java.lang.System.out.println("Compiling Bootstrap2");
        if (classpath == null) {
            command = new java.lang.String[]{ "javac", "./Bootstrap2.java" };
        } else {
            command = new java.lang.String[]{ "javac", "-classpath", classpath, "./Bootstrap2.java" };
        }
        Bootstrap.runCommand(command);
        java.lang.System.out.println("Running Bootstrap2");
        if (classpath == null) {
            command = new java.lang.String[]{ "java", "Bootstrap2" };
        } else {
            command = new java.lang.String[]{ "java", "-cp", classpath, "Bootstrap2" };
        }
        Bootstrap.runCommand(command, args);
    }

    /**
     * Utility method for execing processes
     */
    static void runCommand(java.lang.String[] command) throws java.io.IOException {
        java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
        java.lang.Process process = runtime.exec(command);
        // echo output from process
        java.io.InputStream in = process.getInputStream();
        byte[] buf = new byte[80];
        int count = 0;
        count = in.read(buf, 0, buf.length);
        while (count != (-1)) {
            java.lang.System.out.write(buf, 0, count);
            count = in.read(buf, 0, buf.length);
        } 
        in = process.getErrorStream();
        count = in.read(buf, 0, buf.length);
        if (count > 0) {
            java.lang.System.out.println();
            java.lang.System.out.println("Error Stream Output:");
            while (count != (-1)) {
                java.lang.System.out.write(buf, 0, count);
                count = in.read(buf, 0, buf.length);
            } 
        }
    }

    /**
     * Utility method for running processes that let some additional args
     * be specified.
     */
    static void runCommand(java.lang.String[] command, java.lang.String[] addtlArgs) throws java.io.IOException {
        java.lang.String[] newCommand = new java.lang.String[command.length + addtlArgs.length];
        for (int i = 0; i < command.length; i++) {
            newCommand[i] = command[i];
        }
        for (int i = 0; i < addtlArgs.length; i++) {
            newCommand[command.length + i] = addtlArgs[i];
        }
        Bootstrap.runCommand(newCommand);
    }
}