/* The Apache Software License, Version 1.1

Copyright (c) 2001 The Apache Software Foundation.  All rights
reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in
   the documentation and/or other materials provided with the
   distribution.

3. The end-user documentation included with the redistribution, if
   any, must include the following acknowlegement:
      "This product includes software developed by the
       Apache Software Foundation (http://www.apache.org/)."
   Alternately, this acknowlegement may appear in the software itself,
   if and wherever such third-party acknowlegements normally appear.

4. The names "The Jakarta Project", "Ant", and "Apache Software
   Foundation" must not be used to endorse or promote products derived
   from this software without prior written permission. For written
   permission, please contact apache@apache.org.

5. Products derived from this software may not be called "Apache"
   nor may "Apache" appear in their names without prior written
   permission of the Apache Group.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
====================================================================

This software consists of voluntary contributions made by many
individuals on behalf of the Apache Software Foundation.  For more
information on the Apache Software Foundation, please see
<http://www.apache.org/>.
 */
package org.apache.ant.frontend;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.apache.ant.core.config.*;
import org.apache.ant.core.execution.*;
import org.apache.ant.core.model.*;
import org.apache.ant.core.support.*;
import org.apache.ant.core.xml.*;
/**
 * This is the command line front end to end. It drives the core
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 */
public class Commandline {
    /**
     * The default build file name
     */
    public static final java.lang.String DEFAULT_BUILD_FILENAME = "build.ant";

    /**
     * Stream that we are using for logging
     */
    private java.io.PrintStream out = java.lang.System.out;

    /**
     * Stream that we are using for logging error messages
     */
    private java.io.PrintStream err = java.lang.System.err;

    /**
     * Names of classes to add as listeners to project
     */
    private java.util.List listeners = new java.util.ArrayList(2);

    /**
     * The list of targets to be evaluated in this invocation
     */
    private java.util.List targets = new java.util.ArrayList(4);

    /**
     * Our current message output status. Follows Project.MSG_XXX
     */
    private int messageOutputLevel = BuildEvent.MSG_VERBOSE;

    /**
     * This is the build file to run. By default it is a file: type URL
     * but other URL protocols can be used.
     */
    private java.net.URL buildFileURL;

    /**
     * The Ant logger class. There may be only one logger. It will have the
     * right to use the 'out' PrintStream. The class must implements the BuildLogger
     * interface
     */
    private java.lang.String loggerClassname = null;

    public static void start(java.lang.String[] args) {
        // create a command line and use it to run ant
        org.apache.ant.frontend.Commandline commandline = new org.apache.ant.frontend.Commandline();
        commandline.runAnt(args);
    }

    public void runAnt(java.lang.String[] args) {
        ExecutionFrame mainFrame = null;
        try {
            parseArguments(args);
            Project project = getProject();
            for (java.util.Iterator i = project.getTargets(); i.hasNext();) {
                Target target = ((Target) (i.next()));
            }
            // Get the list of library components
            AntLibrary[] libraries = ComponentManager.getComponents();
            mainFrame = new ExecutionFrame(project, libraries);
            // We iterate through all nodes of all projects and make sure every node is OK
            java.util.Map state = new java.util.HashMap();
            java.util.Stack visiting = new java.util.Stack();
            java.util.List dependencyOrder = new java.util.ArrayList();
            mainFrame.checkTargets(dependencyOrder, state, visiting);
            addBuildListeners(mainFrame);
        } catch (AntException e) {
            Location location = e.getLocation();
            java.lang.Throwable cause = e.getCause();
            if ((location != null) && (location != Location.UNKNOWN_LOCATION)) {
                java.lang.System.out.print(location);
            }
            java.lang.System.out.println(e.getMessage());
            if (cause != null) {
                java.lang.System.out.println((("Root cause: " + cause.getClass().getName()) + ": ") + cause.getMessage());
            }
            java.lang.System.exit(1);
        }
        try {
            mainFrame.runBuild(targets);
            java.lang.System.exit(0);
        } catch (java.lang.Exception e) {
            java.lang.System.exit(1);
        }
    }

    protected void addBuildListeners(ExecutionFrame frame) throws org.apache.ant.frontend.ConfigException {
        // Add the default listener
        frame.addBuildListener(createLogger());
        for (java.util.Iterator i = listeners.iterator(); i.hasNext();) {
            java.lang.String className = ((java.lang.String) (i.next()));
            try {
                BuildListener listener = ((BuildListener) (java.lang.Class.forName(className).newInstance()));
                frame.addBuildListener(listener);
            } catch (java.lang.Exception exc) {
                throw new ConfigException("Unable to instantiate listener " + className, exc);
            }
        }
    }

    /**
     * Creates the default build logger for sending build events to the ant log.
     */
    private org.apache.ant.frontend.BuildLogger createLogger() throws org.apache.ant.frontend.ConfigException {
        BuildLogger logger = null;
        if (loggerClassname != null) {
            try {
                logger = ((BuildLogger) (java.lang.Class.forName(loggerClassname).newInstance()));
            } catch (java.lang.ClassCastException e) {
                java.lang.System.err.println(("The specified logger class " + loggerClassname) + " does not implement the BuildLogger interface");
                throw new ConfigException("Unable to instantiate logger " + loggerClassname, e);
            } catch (java.lang.Exception e) {
                java.lang.System.err.println((("Unable to instantiate specified logger class " + loggerClassname) + " : ") + e.getClass().getName());
                throw new ConfigException("Unable to instantiate logger " + loggerClassname, e);
            }
        } else {
            logger = new DefaultLogger();
        }
        logger.setMessageOutputLevel(messageOutputLevel);
        logger.setOutputPrintStream(out);
        logger.setErrorPrintStream(err);
        return logger;
    }

    /**
     * Parse the command line arguments.
     */
    private void parseArguments(java.lang.String[] args) throws org.apache.ant.frontend.ConfigException {
        for (int i = 0; i < args.length; i++) {
            java.lang.String arg = args[i];
            if ((arg.equals("-buildfile") || arg.equals("-file")) || arg.equals("-f")) {
                try {
                    java.lang.String url = args[i + 1];
                    if (url.indexOf(":") == (-1)) {
                        java.io.File file = new java.io.File(url);
                        java.lang.String uri = "file:" + file.getAbsolutePath().replace('\\', '/');
                        for (int index = uri.indexOf('#'); index != (-1); index = uri.indexOf('#')) {
                            uri = (uri.substring(0, index) + "%23") + uri.substring(index + 1);
                        }
                        buildFileURL = new java.net.URL(uri);
                        // We convert any hash characters to their URL escape.
                    } else {
                        buildFileURL = new java.net.URL(url);
                    }
                    i++;
                } catch (java.net.MalformedURLException e) {
                    java.lang.System.err.println("Buildfile is not valid: " + e.getMessage());
                    throw new ConfigException("Build file is not valid", e);
                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    java.lang.System.err.println("You must specify a buildfile when " + "using the -buildfile argument");
                    return;
                }
            } else if (arg.equals("-logfile") || arg.equals("-l")) {
                try {
                    java.io.File logFile = new java.io.File(args[i + 1]);
                    i++;
                    out = new java.io.PrintStream(new java.io.FileOutputStream(logFile));
                    err = out;
                } catch (java.io.IOException ioe) {
                    java.lang.System.err.println("Cannot write on the specified log file. " + "Make sure the path exists and you have write permissions.");
                    return;
                } catch (java.lang.ArrayIndexOutOfBoundsException aioobe) {
                    java.lang.System.err.println("You must specify a log file when " + "using the -log argument");
                    return;
                }
            } else if (arg.equals("-listener")) {
                try {
                    listeners.add(args[i + 1]);
                    i++;
                } catch (java.lang.ArrayIndexOutOfBoundsException aioobe) {
                    java.lang.System.err.println("You must specify a classname when " + "using the -listener argument");
                    return;
                }
            } else if (arg.equals("-logger")) {
                if (loggerClassname != null) {
                    java.lang.System.err.println("Only one logger class may be specified.");
                    return;
                }
                try {
                    loggerClassname = args[++i];
                } catch (java.lang.ArrayIndexOutOfBoundsException aioobe) {
                    java.lang.System.err.println("You must specify a classname when " + "using the -logger argument");
                    return;
                }
            } else if (arg.startsWith("-")) {
                // we don't have any more args to recognize!
                java.lang.System.out.println("Unknown argument: " + arg);
                return;
            } else {
                // if it's no other arg, it must be a target
                targets.add(arg);
            }
        }
        if (buildFileURL == null) {
            java.io.File defaultBuildFile = new java.io.File(org.apache.ant.frontend.Commandline.DEFAULT_BUILD_FILENAME);
            try {
                buildFileURL = defaultBuildFile.toURL();
            } catch (java.net.MalformedURLException e) {
                java.lang.System.err.println("Buildfile is not valid: " + e.getMessage());
                throw new ConfigException("Build file is not valid", e);
            }
        }
    }

    private org.apache.ant.frontend.Project getProject() throws org.apache.ant.frontend.ConfigException {
        XMLProjectParser parser = new XMLProjectParser();
        Project project = parser.parseBuildFile(buildFileURL);
        return project;
    }
}