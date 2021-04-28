/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.frontends;
import org.apache.avalon.excalibur.cli.CLArgsParser;
import org.apache.avalon.excalibur.cli.CLOption;
import org.apache.avalon.excalibur.cli.CLOptionDescriptor;
import org.apache.avalon.excalibur.cli.CLUtil;
import org.apache.avalon.excalibur.io.ExtensionFileFilter;
import org.apache.avalon.framework.ExceptionUtil;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.log.Priority;
import org.apache.log.output.DefaultOutputLogTarget;
import org.apache.myrmidon.Constants;
import org.apache.myrmidon.api.DefaultTaskContext;
import org.apache.myrmidon.api.TaskContext;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.components.builder.ProjectBuilder;
import org.apache.myrmidon.components.embeddor.DefaultEmbeddor;
import org.apache.myrmidon.components.embeddor.Embeddor;
import org.apache.myrmidon.components.executor.Executor;
import org.apache.myrmidon.components.model.Project;
import org.apache.myrmidon.components.workspace.Workspace;
import org.apache.myrmidon.listeners.ProjectListener;
/**
 * The class to kick the tires and light the fires.
 * Starts myrmidon, loads ProjectBuilder, builds project then uses ProjectManager
 * to run project.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class CLIMain extends org.apache.avalon.framework.logger.AbstractLoggable {
    // defines for the Command Line options
    private static final int HELP_OPT = 'h';

    private static final int QUIET_OPT = 'q';

    private static final int VERBOSE_OPT = 'v';

    private static final int FILE_OPT = 'f';

    private static final int LOG_LEVEL_OPT = 'l';

    private static final int DEFINE_OPT = 'D';

    private static final int BUILDER_PARAM_OPT = 'B';

    private static final int VERSION_OPT = 1;

    private static final int LISTENER_OPT = 2;

    private static final int TASKLIB_DIR_OPT = 5;

    private static final int INCREMENTAL_OPT = 6;

    private static final int HOME_DIR_OPT = 7;

    private static final int DRY_RUN_OPT = 8;

    // incompatable options for info options
    private static final int[] INFO_OPT_INCOMPAT = new int[]{ org.apache.myrmidon.frontends.CLIMain.HELP_OPT, org.apache.myrmidon.frontends.CLIMain.QUIET_OPT, org.apache.myrmidon.frontends.CLIMain.VERBOSE_OPT, org.apache.myrmidon.frontends.CLIMain.FILE_OPT, org.apache.myrmidon.frontends.CLIMain.LOG_LEVEL_OPT, org.apache.myrmidon.frontends.CLIMain.VERSION_OPT, org.apache.myrmidon.frontends.CLIMain.LISTENER_OPT, org.apache.myrmidon.frontends.CLIMain.DEFINE_OPT, org.apache.myrmidon.frontends.CLIMain.DRY_RUN_OPT// TASKLIB_DIR_OPT, HOME_DIR_OPT
     };

    // incompatable options for other logging options
    private static final int[] LOG_OPT_INCOMPAT = new int[]{ org.apache.myrmidon.frontends.CLIMain.QUIET_OPT, org.apache.myrmidon.frontends.CLIMain.VERBOSE_OPT, org.apache.myrmidon.frontends.CLIMain.LOG_LEVEL_OPT };

    private org.apache.myrmidon.listeners.ProjectListener m_listener;

    // /Parameters for run of myrmidon
    private org.apache.avalon.framework.parameters.Parameters m_parameters = new org.apache.avalon.framework.parameters.Parameters();

    // /List of targets supplied on command line to execute
    private java.util.ArrayList m_targets = new java.util.ArrayList();

    // /List of user supplied defines
    private org.apache.avalon.framework.parameters.Parameters m_defines = new org.apache.avalon.framework.parameters.Parameters();

    // /List of user supplied parameters for builder
    private org.apache.avalon.framework.parameters.Parameters m_builderParameters = new org.apache.avalon.framework.parameters.Parameters();

    // /Determine whether tasks are actually executed
    private boolean m_dryRun = false;

    /**
     * Main entry point called to run standard Myrmidon.
     *
     * @param args
     * 		the args
     */
    public static void main(final java.lang.String[] args) {
        final org.apache.myrmidon.frontends.CLIMain main = new org.apache.myrmidon.frontends.CLIMain();
        try {
            main.execute(args);
        } catch (final java.lang.Throwable throwable) {
            java.lang.System.err.println("Error: " + org.apache.avalon.framework.ExceptionUtil.printStackTrace(throwable));
            java.lang.System.exit(-1);
        }
        java.lang.System.exit(0);
    }

    /**
     * Display usage report.
     */
    private void usage(final org.apache.avalon.excalibur.cli.CLOptionDescriptor[] options) {
        java.lang.System.out.println(("java " + getClass().getName()) + " [options]");
        java.lang.System.out.println("\tAvailable options:");
        java.lang.System.out.println(org.apache.avalon.excalibur.cli.CLUtil.describeOptions(options));
    }

    /**
     * Initialise the options for command line parser.
     */
    private org.apache.avalon.excalibur.cli.CLOptionDescriptor[] createCLOptions() {
        // TODO: localise
        final org.apache.avalon.excalibur.cli.CLOptionDescriptor[] options = new org.apache.avalon.excalibur.cli.CLOptionDescriptor[13];
        options[0] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("help", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENT_DISALLOWED, org.apache.myrmidon.frontends.CLIMain.HELP_OPT, "display this help message", org.apache.myrmidon.frontends.CLIMain.INFO_OPT_INCOMPAT);
        options[1] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("file", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENT_REQUIRED, org.apache.myrmidon.frontends.CLIMain.FILE_OPT, "the build file.");
        options[2] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("log-level", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENT_REQUIRED, org.apache.myrmidon.frontends.CLIMain.LOG_LEVEL_OPT, "the verbosity level at which to log messages. " + "(DEBUG|INFO|WARN|ERROR|FATAL_ERROR)", org.apache.myrmidon.frontends.CLIMain.LOG_OPT_INCOMPAT);
        options[3] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("quiet", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENT_DISALLOWED, org.apache.myrmidon.frontends.CLIMain.QUIET_OPT, "equivelent to --log-level=FATAL_ERROR", org.apache.myrmidon.frontends.CLIMain.LOG_OPT_INCOMPAT);
        options[4] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("verbose", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENT_DISALLOWED, org.apache.myrmidon.frontends.CLIMain.VERBOSE_OPT, "equivelent to --log-level=INFO", org.apache.myrmidon.frontends.CLIMain.LOG_OPT_INCOMPAT);
        options[5] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("listener", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENT_REQUIRED, org.apache.myrmidon.frontends.CLIMain.LISTENER_OPT, "the listener for log events.");
        options[6] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("version", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENT_DISALLOWED, org.apache.myrmidon.frontends.CLIMain.VERSION_OPT, "display version", org.apache.myrmidon.frontends.CLIMain.INFO_OPT_INCOMPAT);
        options[7] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("task-lib-dir", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENT_REQUIRED, org.apache.myrmidon.frontends.CLIMain.TASKLIB_DIR_OPT, "the task lib directory to scan for .tsk files.");
        options[8] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("incremental", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENT_DISALLOWED, org.apache.myrmidon.frontends.CLIMain.INCREMENTAL_OPT, "Run in incremental mode");
        options[9] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("myrmidon-home", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENT_REQUIRED, org.apache.myrmidon.frontends.CLIMain.HOME_DIR_OPT, "Specify myrmidon home directory");
        options[10] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("define", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENTS_REQUIRED_2, org.apache.myrmidon.frontends.CLIMain.DEFINE_OPT, "Define a variable (ie -Dfoo=var)", new int[0]);
        options[11] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("builder-parameter", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENTS_REQUIRED_2, org.apache.myrmidon.frontends.CLIMain.BUILDER_PARAM_OPT, "Define a builder parameter (ie -Bfoo=var)");
        options[12] = new org.apache.avalon.excalibur.cli.CLOptionDescriptor("dry-run", org.apache.avalon.excalibur.cli.CLOptionDescriptor.ARGUMENT_DISALLOWED, org.apache.myrmidon.frontends.CLIMain.DRY_RUN_OPT, "Do not execute tasks - just print them out");
        return options;
    }

    private boolean parseCommandLineOptions(final java.lang.String[] args) {
        final org.apache.avalon.excalibur.cli.CLOptionDescriptor[] options = createCLOptions();
        final org.apache.avalon.excalibur.cli.CLArgsParser parser = new org.apache.avalon.excalibur.cli.CLArgsParser(args, options);
        if (null != parser.getErrorString()) {
            java.lang.System.err.println("Error: " + parser.getErrorString());
            return false;
        }
        final java.util.List clOptions = parser.getArguments();
        final int size = clOptions.size();
        for (int i = 0; i < size; i++) {
            final org.apache.avalon.excalibur.cli.CLOption option = ((org.apache.avalon.excalibur.cli.CLOption) (clOptions.get(i)));
            switch (option.getId()) {
                case org.apache.myrmidon.frontends.CLIMain.HELP_OPT :
                    usage(options);
                    return false;
                case org.apache.myrmidon.frontends.CLIMain.VERSION_OPT :
                    java.lang.System.out.println(Constants.BUILD_DESCRIPTION);
                    return false;
                case org.apache.myrmidon.frontends.CLIMain.HOME_DIR_OPT :
                    m_parameters.setParameter("myrmidon.home", option.getArgument());
                    break;
                case org.apache.myrmidon.frontends.CLIMain.TASKLIB_DIR_OPT :
                    m_parameters.setParameter("myrmidon.lib.path", option.getArgument());
                    break;
                case org.apache.myrmidon.frontends.CLIMain.LOG_LEVEL_OPT :
                    m_parameters.setParameter("log.level", option.getArgument());
                    break;
                case org.apache.myrmidon.frontends.CLIMain.VERBOSE_OPT :
                    m_parameters.setParameter("log.level", "INFO");
                    break;
                case org.apache.myrmidon.frontends.CLIMain.QUIET_OPT :
                    m_parameters.setParameter("log.level", "ERROR");
                    break;
                case org.apache.myrmidon.frontends.CLIMain.INCREMENTAL_OPT :
                    m_parameters.setParameter("incremental", "true");
                    break;
                case org.apache.myrmidon.frontends.CLIMain.FILE_OPT :
                    m_parameters.setParameter("filename", option.getArgument());
                    break;
                case org.apache.myrmidon.frontends.CLIMain.LISTENER_OPT :
                    m_parameters.setParameter("listener", option.getArgument());
                    break;
                case org.apache.myrmidon.frontends.CLIMain.DEFINE_OPT :
                    m_defines.setParameter(option.getArgument(0), option.getArgument(1));
                    break;
                case org.apache.myrmidon.frontends.CLIMain.BUILDER_PARAM_OPT :
                    m_builderParameters.setParameter(option.getArgument(0), option.getArgument(1));
                    break;
                case org.apache.myrmidon.frontends.CLIMain.DRY_RUN_OPT :
                    m_dryRun = true;
                    break;
                case 0 :
                    m_targets.add(option.getArgument());
                    break;
            }
        }
        return true;
    }

    private void setupDefaultParameters() {
        // System property set up by launcher
        m_parameters.setParameter("myrmidon.home", java.lang.System.getProperty("myrmidon.home", "."));
        m_parameters.setParameter("filename", "build.ant");
        m_parameters.setParameter("log.level", "WARN");
        m_parameters.setParameter("listener", "org.apache.myrmidon.listeners.DefaultProjectListener");
        m_parameters.setParameter("incremental", "false");
    }

    private void execute(final java.lang.String[] args) throws java.lang.Exception {
        setupDefaultParameters();
        if (!parseCommandLineOptions(args)) {
            return;
        }
        // handle logging...
        final java.lang.String logLevel = m_parameters.getParameter("log.level", null);
        setLogger(createLogger(logLevel));
        final java.lang.String home = m_parameters.getParameter("myrmidon.home", null);
        final java.io.File homeDir = new java.io.File(home).getAbsoluteFile();
        if (!homeDir.isDirectory()) {
            throw new java.lang.Exception(("myrmidon-home (" + homeDir) + ") is not a directory");
        }
        final java.lang.String filename = m_parameters.getParameter("filename", null);
        final java.io.File buildFile = new java.io.File(filename).getCanonicalFile();
        if (!buildFile.isFile()) {
            throw new java.lang.Exception(("File " + buildFile) + " is not a file or doesn't exist");
        }
        // handle listener..
        final java.lang.String listenerName = m_parameters.getParameter("listener", null);
        final org.apache.myrmidon.listeners.ProjectListener listener = createListener(listenerName);
        getLogger().warn("Ant Build File: " + buildFile);
        getLogger().info("Ant Home Directory: " + homeDir);
        // getLogger().info( "Ant Bin Directory: " + m_binDir );
        // getLogger().debug( "Ant Lib Directory: " + m_libDir );
        // getLogger().debug( "Ant Task Lib Directory: " + m_taskLibDir );
        if (m_dryRun) {
            m_parameters.setParameter(Executor.ROLE, "org.apache.myrmidon.components.executor.PrintingExecutor");
        }
        final org.apache.myrmidon.components.embeddor.Embeddor embeddor = new org.apache.myrmidon.components.embeddor.DefaultEmbeddor();
        setupLogger(embeddor);
        embeddor.parameterize(m_parameters);
        embeddor.initialize();
        embeddor.start();
        // create the project
        final org.apache.myrmidon.components.model.Project project = embeddor.createProject(buildFile.toString(), null, m_builderParameters);
        java.io.BufferedReader reader = null;
        // loop over build if we are in incremental mode..
        final boolean incremental = m_parameters.getParameterAsBoolean("incremental", false);
        while (true) {
            // actually do the build ...
            final org.apache.myrmidon.components.workspace.Workspace workspace = embeddor.createWorkspace(m_defines);
            workspace.addProjectListener(listener);
            doBuild(workspace, project, m_targets);
            if (!incremental)
                break;

            java.lang.System.out.println("Continue ? (Enter no to stop)");
            if (null == reader) {
                reader = new java.io.BufferedReader(new java.io.InputStreamReader(java.lang.System.in));
            }
            java.lang.String line = reader.readLine();
            if (line.equalsIgnoreCase("no"))
                break;

        } 
        embeddor.stop();
        embeddor.dispose();
    }

    /**
     * Actually do the build.
     *
     * @param manager
     * 		the manager
     * @param project
     * 		the project
     * @param targets
     * 		the targets to build as passed by CLI
     */
    private void doBuild(final org.apache.myrmidon.components.workspace.Workspace workspace, final org.apache.myrmidon.components.model.Project project, final java.util.ArrayList targets) {
        try {
            final int targetCount = targets.size();
            // if we didn't specify a target on CLI then choose default
            if (0 == targetCount) {
                workspace.executeProject(project, project.getDefaultTargetName());
            } else {
                for (int i = 0; i < targetCount; i++) {
                    workspace.executeProject(project, ((java.lang.String) (targets.get(i))));
                }
            }
        } catch (final org.apache.myrmidon.api.TaskException ae) {
            getLogger().error("BUILD FAILED");
            getLogger().error("Reason:\n" + org.apache.avalon.framework.ExceptionUtil.printStackTrace(ae, 5, true));
        }
    }

    /**
     * Create Logger of appropriate log-level.
     *
     * @param logLevel
     * 		the log-level
     * @return the logger
     * @exception Exception
     * 		if an error occurs
     */
    private org.apache.log.Logger createLogger(final java.lang.String logLevel) throws java.lang.Exception {
        final java.lang.String logLevelCapitalized = logLevel.toUpperCase();
        final org.apache.log.Priority priority = org.apache.log.Priority.getPriorityForName(logLevelCapitalized);
        if (!priority.getName().equals(logLevelCapitalized)) {
            throw new java.lang.Exception("Unknown log level - " + logLevel);
        }
        final org.apache.log.Logger logger = org.apache.log.Hierarchy.getDefaultHierarchy().getLoggerFor("myrmidon");
        final org.apache.log.output.DefaultOutputLogTarget target = new org.apache.log.output.DefaultOutputLogTarget();
        target.setFormat("[%8.8{category}] %{message}\\n%{throwable}");
        logger.setLogTargets(new org.apache.log.LogTarget[]{ target });
        logger.setPriority(priority);
        return logger;
    }

    /**
     * Setup project listener.
     *
     * @param listener
     * 		the classname of project listener
     */
    private org.apache.myrmidon.listeners.ProjectListener createListener(final java.lang.String listener) throws java.lang.Exception {
        try {
            return ((org.apache.myrmidon.listeners.ProjectListener) (java.lang.Class.forName(listener).newInstance()));
        } catch (final java.lang.Throwable t) {
            throw new java.lang.Exception((("Error creating the listener " + listener) + " due to ") + org.apache.avalon.framework.ExceptionUtil.printStackTrace(t, 5, true));
        }
    }

    /**
     * Helper method to add values to a context
     *
     * @param context
     * 		the context
     * @param map
     * 		the map of names->values
     */
    private void addToContext(final org.apache.myrmidon.api.TaskContext context, final java.util.Map map) throws java.lang.Exception {
        final java.util.Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            final java.lang.String key = ((java.lang.String) (keys.next()));
            final java.lang.Object value = map.get(key);
            context.setProperty(key, value);
        } 
    }
}