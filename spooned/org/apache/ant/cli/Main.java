package org.apache.ant.cli;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 * Entry point for Ant on the Command Line Interface. This class sets
 * up the basic environment that Ant will execute in and then hands
 * off the the CLIFrontEnd class.
 *
 * @author James Duncan Davidson (duncan@apache.org)
 */
public class Main {
    /**
     * Command line entry point. Here we set up the environment via
     * a URLCLassLoader and then execute in the scope of that class loader
     * so that the user doesnt have to set things up themselves.
     */
    public static void main(java.lang.String[] args) throws java.lang.Exception {
        CLIFrontEnd frontEnd;
        // check a few things out and make sure we have the right things
        // that we need in our classpath -- set those up in a custom class
        // loader and execute from there...
        java.util.Vector classpathNodes = new java.util.Vector();
        // check to see if we have a compiler on the classpath. Right now
        // we're just checking for the old compiler, but that does tell us
        // if we have tools.jar or not
        try {
            java.lang.Class clazz = java.lang.Class.forName("sun.tools.javac.Main");
        } catch (java.lang.ClassNotFoundException cnfe) {
            java.lang.String javaHome = java.lang.System.getProperty("java.home");
            if (javaHome.endsWith("jre")) {
                javaHome = javaHome.substring(0, javaHome.length() - 4);
            }
            // XXX should check if this exists and bail out if it doesn't
            java.lang.String classpath = javaHome + "/lib/tools.jar";
            java.net.URL url = new java.io.File(classpath).toURL();
            classpathNodes.addElement(url);
        }
        // XXX add handling for -cp [classpath] argument to set up more classpath
        // nodes
        // FUTURE -- At this point we'll want to take a sneak peack at
        // the args, find the build file, and check it to see if we need
        // to do any CJAN requests. This will be done once we have a CJAN
        // system in place.
        // Set up URLs that classloader should use to find classes
        java.net.URL[] urls = new java.net.URL[classpathNodes.size()];
        Enumeration = classpathNodes.elements();
        int i = 0;
        // Set up classloader
        // Run, Run Forrest, Run
    }
}