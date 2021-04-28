/* The Apache Software License, Version 1.1

Copyright (c) 2002 The Apache Software Foundation.  All rights
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
package org.apache.ant.cli;
import org.apache.ant.antcore.config.AntConfig;
import org.apache.ant.antcore.execution.ExecutionManager;
import org.apache.ant.antcore.modelparser.XMLProjectParser;
import org.apache.ant.antcore.xml.XMLParseException;
import org.apache.ant.common.event.BuildEvent;
import org.apache.ant.common.event.BuildListener;
import org.apache.ant.common.event.MessageLevel;
import org.apache.ant.common.model.Project;
import org.apache.ant.common.util.ConfigException;
import org.apache.ant.common.util.DemuxOutputStream;
import org.apache.ant.frontend.FrontendUtils;
import org.apache.ant.init.InitConfig;
import org.apache.ant.init.InitUtils;
/**
 * This is the command line front end. It drives the core.
 *
 * @author Conor MacNeill
 * @created 9 January 2002
 */
public class Commandline {
    /**
     * The initialisation configuration for Ant
     */
    private org.apache.ant.init.InitConfig initConfig;

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
     * The command line properties
     */
    private java.util.Map definedProperties = new java.util.HashMap();

    /**
     * The Config files to use in this run
     */
    private java.util.List configFiles = new java.util.ArrayList();

    /**
     * This is the build file to run. By default it is a file: type URL but
     * other URL protocols can be used.
     */
    private java.net.URL buildFileURL;

    /**
     * The Ant logger class. There may be only one logger. It will have the
     * right to use the 'out' PrintStream. The class must implements the
     * BuildLogger interface
     */
    private java.lang.String loggerClassname = null;

    /**
     * Our current message output status. Follows MessageLevel values
     */
    private int messageOutputLevel = org.apache.ant.common.event.MessageLevel.MSG_INFO;

    /**
     * The logger that will be used for the build
     */
    private org.apache.ant.cli.BuildLogger logger = null;

    /**
     * Start the command line front end for mutant.
     *
     * @param args
     * 		the commandline arguments
     * @param config
     * 		the initialisation configuration
     */
    public static void start(java.lang.String[] args, org.apache.ant.init.InitConfig config) {
        // create a command line and use it to run ant
        org.apache.ant.cli.Commandline commandline = new org.apache.ant.cli.Commandline();
        commandline.process(args, config);
    }

    /**
     * Adds a feature to the BuildListeners attribute of the Commandline
     * object
     *
     * @param execManager
     * 		The feature to be added to the BuildListeners
     * 		attribute
     * @exception ConfigException
     * 		if the necessary listener instances could
     * 		not be created
     */
    protected void addBuildListeners(org.apache.ant.antcore.execution.ExecutionManager execManager) throws org.apache.ant.common.util.ConfigException {
        // Add the default listener
        execManager.addBuildListener(logger);
        for (java.util.Iterator i = listeners.iterator(); i.hasNext();) {
            java.lang.String className = ((java.lang.String) (i.next()));
            try {
                org.apache.ant.common.event.BuildListener listener = ((org.apache.ant.common.event.BuildListener) (java.lang.Class.forName(className).newInstance()));
                execManager.addBuildListener(listener);
            } catch (java.lang.ClassCastException e) {
                java.lang.System.err.println(("The specified listener class " + className) + " does not implement the Listener interface");
                throw new org.apache.ant.common.util.ConfigException("Unable to instantiate listener " + className, e);
            } catch (java.lang.Exception e) {
                java.lang.System.err.println(((("Unable to instantiate specified listener " + "class ") + className) + " : ") + e.getClass().getName());
                throw new org.apache.ant.common.util.ConfigException("Unable to instantiate listener " + className, e);
            }
        }
    }

    /**
     * Get an option value
     *
     * @param args
     * 		the full list of command line arguments
     * @param position
     * 		the position in the args array where the value shoudl
     * 		be
     * @param argType
     * 		the option type
     * @return the value of the option
     * @exception ConfigException
     * 		if the option cannot be read
     */
    private java.lang.String getOption(java.lang.String[] args, int position, java.lang.String argType) throws org.apache.ant.common.util.ConfigException {
        java.lang.String value = null;
        try {
            value = args[position];
        } catch (java.lang.IndexOutOfBoundsException e) {
            throw new org.apache.ant.common.util.ConfigException(("You must specify a value for the " + argType) + " argument");
        }
        return value;
    }

    /**
     * Start the command line front end for mutant.
     *
     * @param args
     * 		the commandline arguments
     * @param initConfig
     * 		Ant's initialization configuration
     */
    private void process(java.lang.String[] args, org.apache.ant.init.InitConfig initConfig) {
        this.initConfig = initConfig;
        org.apache.ant.antcore.execution.ExecutionManager executionManager = null;
        org.apache.ant.common.model.Project project = null;
        try {
            parseArguments(args);
            createLogger();
            determineBuildFile();
            org.apache.ant.antcore.config.AntConfig config = new org.apache.ant.antcore.config.AntConfig();
            org.apache.ant.antcore.config.AntConfig userConfig = org.apache.ant.frontend.FrontendUtils.getAntConfig(initConfig.getUserConfigArea());
            org.apache.ant.antcore.config.AntConfig systemConfig = org.apache.ant.frontend.FrontendUtils.getAntConfig(initConfig.getSystemConfigArea());
            if (systemConfig != null) {
                config.merge(systemConfig);
            }
            if (userConfig != null) {
                config.merge(userConfig);
            }
            for (java.util.Iterator i = configFiles.iterator(); i.hasNext();) {
                java.io.File configFile = ((java.io.File) (i.next()));
                org.apache.ant.antcore.config.AntConfig runConfig = org.apache.ant.frontend.FrontendUtils.getAntConfigFile(configFile);
                config.merge(runConfig);
            }
            if ((!buildFileURL.getProtocol().equals("file")) && (!config.isRemoteProjectAllowed())) {
                throw new org.apache.ant.common.util.ConfigException("Remote Projects are not allowed: " + buildFileURL);
            }
            project = parseProject();
            // create the execution manager to execute the build
            executionManager = new org.apache.ant.antcore.execution.ExecutionManager(initConfig, config);
            java.io.OutputStream demuxOut = new org.apache.ant.common.util.DemuxOutputStream(executionManager, false);
            java.io.OutputStream demuxErr = new org.apache.ant.common.util.DemuxOutputStream(executionManager, true);
            java.lang.System.setOut(new java.io.PrintStream(demuxOut));
            java.lang.System.setErr(new java.io.PrintStream(demuxErr));
            addBuildListeners(executionManager);
        } catch (java.lang.Throwable e) {
            if (logger != null) {
                org.apache.ant.common.event.BuildEvent finishedEvent = new org.apache.ant.common.event.BuildEvent(this, org.apache.ant.common.event.BuildEvent.BUILD_FINISHED, e);
                logger.buildFinished(finishedEvent);
            } else {
                e.printStackTrace();
            }
            java.lang.System.exit(1);
        }
        try {
            executionManager.runBuild(project, targets, definedProperties);
            java.lang.System.exit(0);
        } catch (java.lang.Throwable t) {
            java.lang.System.exit(1);
        }
    }

    /**
     * Use the XML parser to parse the build file into a project model
     *
     * @return a project model representation of the project file
     * @exception XMLParseException
     * 		if the project cannot be parsed
     */
    private org.apache.ant.common.model.Project parseProject() throws org.apache.ant.antcore.xml.XMLParseException {
        org.apache.ant.antcore.modelparser.XMLProjectParser parser = new org.apache.ant.antcore.modelparser.XMLProjectParser();
        org.apache.ant.common.model.Project project = parser.parseBuildFile(buildFileURL);
        return project;
    }

    /**
     * Handle build file argument
     *
     * @param url
     * 		the build file's URL
     * @exception ConfigException
     * 		if the build file location is not valid
     */
    private void argBuildFile(java.lang.String url) throws org.apache.ant.common.util.ConfigException {
        try {
            if (url.indexOf(":") == (-1)) {
                // We convert any hash characters to their URL escape.
                buildFileURL = org.apache.ant.init.InitUtils.getFileURL(new java.io.File(url));
            } else {
                buildFileURL = new java.net.URL(url);
            }
        } catch (java.net.MalformedURLException e) {
            throw new org.apache.ant.common.util.ConfigException("Build file is not valid", e);
        }
    }

    /**
     * Handle the log file option
     *
     * @param arg
     * 		the value of the log file option
     * @exception ConfigException
     * 		if the log file is not writeable
     */
    private void argLogFile(java.lang.String arg) throws org.apache.ant.common.util.ConfigException {
        try {
            java.io.File logFile = new java.io.File(arg);
            out = new java.io.PrintStream(new java.io.FileOutputStream(logFile));
            err = out;
        } catch (java.io.IOException ioe) {
            throw new org.apache.ant.common.util.ConfigException("Cannot write on the specified log " + ("file. Make sure the path exists and " + "you have write permissions."), ioe);
        }
    }

    /**
     * Handle the logger attribute
     *
     * @param arg
     * 		the logger classname
     * @exception ConfigException
     * 		if a logger has already been defined
     */
    private void argLogger(java.lang.String arg) throws org.apache.ant.common.util.ConfigException {
        if (loggerClassname != null) {
            throw new org.apache.ant.common.util.ConfigException("Only one logger class may be " + "specified.");
        }
        loggerClassname = arg;
    }

    /**
     * Determine the build file to use
     *
     * @exception ConfigException
     * 		if the build file cannot be found
     */
    private void determineBuildFile() throws org.apache.ant.common.util.ConfigException {
        if (buildFileURL == null) {
            java.io.File defaultBuildFile = new java.io.File(org.apache.ant.frontend.FrontendUtils.DEFAULT_BUILD_FILENAME);
            if (!defaultBuildFile.exists()) {
                java.io.File ant1BuildFile = new java.io.File(org.apache.ant.frontend.FrontendUtils.DEFAULT_ANT1_FILENAME);
                if (ant1BuildFile.exists()) {
                    defaultBuildFile = ant1BuildFile;
                }
            }
            try {
                buildFileURL = org.apache.ant.init.InitUtils.getFileURL(defaultBuildFile);
            } catch (java.net.MalformedURLException e) {
                throw new org.apache.ant.common.util.ConfigException("Build file is not valid", e);
            }
        }
    }

    /**
     * Parse the command line arguments.
     *
     * @param args
     * 		the command line arguments
     * @exception ConfigException
     * 		thrown when the command line contains some
     * 		sort of error.
     */
    private void parseArguments(java.lang.String[] args) throws org.apache.ant.common.util.ConfigException {
        int i = 0;
        while (i < args.length) {
            java.lang.String arg = args[i++];
            if ((arg.equals("-buildfile") || arg.equals("-file")) || arg.equals("-f")) {
                argBuildFile(getOption(args, i++, arg));
            } else if (arg.equals("-logfile") || arg.equals("-l")) {
                argLogFile(getOption(args, i++, arg));
            } else if (arg.equals("-quiet") || arg.equals("-q")) {
                messageOutputLevel = org.apache.ant.common.event.MessageLevel.MSG_WARN;
            } else if (arg.equals("-verbose") || arg.equals("-v")) {
                // printVersion();
                messageOutputLevel = org.apache.ant.common.event.MessageLevel.MSG_VERBOSE;
            } else if (arg.equals("-debug")) {
                // printVersion();
                messageOutputLevel = org.apache.ant.common.event.MessageLevel.MSG_DEBUG;
            } else if (arg.equals("-config") || arg.equals("-c")) {
                configFiles.add(new java.io.File(getOption(args, i++, arg)));
            } else if (arg.equals("-listener")) {
                listeners.add(getOption(args, i++, arg));
            } else if (arg.equals("-logger")) {
                argLogger(getOption(args, i++, arg));
            } else if (arg.startsWith("-D")) {
                java.lang.String name = arg.substring(2, arg.length());
                java.lang.String value = null;
                int posEq = name.indexOf("=");
                if (posEq > 0) {
                    value = name.substring(posEq + 1);
                    name = name.substring(0, posEq);
                } else {
                    value = getOption(args, i++, arg);
                }
                definedProperties.put(name, value);
            } else if (arg.startsWith("-")) {
                // we don't have any more args to recognize!
                java.lang.System.out.println("Unknown option: " + arg);
                return;
            } else {
                // if it's no other arg, it must be a target
                targets.add(arg);
            }
        } 
    }

    /**
     * Creates the default build logger for sending build events to the ant
     * log.
     *
     * @exception ConfigException
     * 		if the logger cannot be instantiatd
     */
    private void createLogger() throws org.apache.ant.common.util.ConfigException {
        if (loggerClassname != null) {
            try {
                java.lang.Class loggerClass = java.lang.Class.forName(loggerClassname);
                logger = ((org.apache.ant.cli.BuildLogger) (loggerClass.newInstance()));
            } catch (java.lang.ClassCastException e) {
                java.lang.System.err.println(("The specified logger class " + loggerClassname) + " does not implement the BuildLogger interface");
                throw new org.apache.ant.common.util.ConfigException("Unable to instantiate logger " + loggerClassname, e);
            } catch (java.lang.Exception e) {
                java.lang.System.err.println(((("Unable to instantiate specified logger " + "class ") + loggerClassname) + " : ") + e.getClass().getName());
                throw new org.apache.ant.common.util.ConfigException("Unable to instantiate logger " + loggerClassname, e);
            }
        } else {
            logger = new org.apache.ant.cli.DefaultLogger();
        }
        logger.setMessageOutputLevel(messageOutputLevel);
        logger.setOutputPrintStream(out);
        logger.setErrorPrintStream(err);
    }
}