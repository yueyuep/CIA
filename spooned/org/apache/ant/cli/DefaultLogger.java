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
import org.apache.ant.common.antlib.ExecutionComponent;
import org.apache.ant.common.antlib.Task;
import org.apache.ant.common.event.BuildEvent;
import org.apache.ant.common.event.MessageLevel;
import org.apache.ant.common.model.Target;
import org.apache.ant.common.util.AntException;
import org.apache.ant.common.util.Location;
/**
 * Writes build event to a PrintStream. Currently, it only writes which
 * targets are being executed, and any messages that get logged.
 *
 * @author Conor MacNeill
 * @created 15 January 2002
 */
public class DefaultLogger implements org.apache.ant.cli.BuildLogger {
    /**
     * Standard field separator
     */
    private static java.lang.String lSep = java.lang.System.getProperty("line.separator");

    /**
     * spacing to allow for task tags
     */
    private static final int LEFT_COLUMN_SIZE = 12;

    /**
     * The stream where output should be written
     */
    private java.io.PrintStream out;

    /**
     * The stream to where errors should be written
     */
    private java.io.PrintStream err;

    /**
     * The level of messages which should be let through
     */
    private int messageOutputLevel = org.apache.ant.common.event.MessageLevel.MSG_ERR;

    /**
     * Controls whether adornments are added
     */
    private boolean emacsMode = false;

    /**
     * The time at which the build started
     */
    private long startTime = java.lang.System.currentTimeMillis();

    /**
     * Format the time into something readable
     *
     * @param millis
     * 		Java millis value
     * @return the formatted time
     */
    protected static java.lang.String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        if (minutes > 0) {
            return ((((java.lang.Long.toString(minutes) + " minute") + (minutes == 1 ? " " : "s ")) + java.lang.Long.toString(seconds % 60)) + " second") + ((seconds % 60) == 1 ? "" : "s");
        } else {
            return (java.lang.Long.toString(seconds) + " second") + ((seconds % 60) == 1 ? "" : "s");
        }
    }

    /**
     * Set the messageOutputLevel this logger is to respond to. Only
     * messages with a message level lower than or equal to the given level
     * are output to the log. <P>
     *
     * Constants for the message levels are in Project.java. The order of
     * the levels, from least to most verbose, is MSG_ERR, MSG_WARN,
     * MSG_INFO, MSG_VERBOSE, MSG_DEBUG. The default message level for
     * DefaultLogger is Project.MSG_ERR.
     *
     * @param level
     * 		the logging level for the logger.
     */
    public void setMessageOutputLevel(int level) {
        this.messageOutputLevel = level;
    }

    /**
     * Set the output stream to which this logger is to send its output.
     *
     * @param output
     * 		the output stream for the logger.
     */
    public void setOutputPrintStream(java.io.PrintStream output) {
        this.out = output;
    }

    /**
     * Set the output stream to which this logger is to send error messages.
     *
     * @param err
     * 		the error stream for the logger.
     */
    public void setErrorPrintStream(java.io.PrintStream err) {
        this.err = err;
    }

    /**
     * Set this logger to produce emacs (and other editor) friendly output.
     *
     * @param emacsMode
     * 		true if output is to be unadorned so that emacs and
     * 		other editors can parse files names, etc.
     */
    public void setEmacsMode(boolean emacsMode) {
        this.emacsMode = emacsMode;
    }

    /**
     * Report an exception
     *
     * @param t
     * 		The exception to be reported.
     */
    public void reportException(java.lang.Throwable t) {
        if (t instanceof org.apache.ant.common.util.AntException) {
            org.apache.ant.common.util.AntException e = ((org.apache.ant.common.util.AntException) (t));
            org.apache.ant.common.util.Location location = e.getLocation();
            java.lang.Throwable cause = e.getCause();
            if ((location != null) && (location != org.apache.ant.common.util.Location.UNKNOWN_LOCATION)) {
                out.print(location);
            }
            out.println(e.getMessage());
            if (messageOutputLevel >= org.apache.ant.common.event.MessageLevel.MSG_VERBOSE) {
                t.printStackTrace(out);
            }
            if (!cause.getMessage().equals(t.getMessage())) {
                out.println("Root cause: " + cause.toString());
            }
        } else {
            t.printStackTrace(err);
        }
    }

    /**
     * Description of the Method
     *
     * @param event
     * 		Description of Parameter
     */
    public void buildStarted(org.apache.ant.common.event.BuildEvent event) {
        startTime = java.lang.System.currentTimeMillis();
    }

    /**
     * Description of the Method
     *
     * @param event
     * 		Description of Parameter
     */
    public void buildFinished(org.apache.ant.common.event.BuildEvent event) {
        java.lang.Throwable cause = event.getCause();
        if (cause == null) {
            out.println(org.apache.ant.cli.DefaultLogger.lSep + "BUILD SUCCESSFUL");
        } else {
            err.println((org.apache.ant.cli.DefaultLogger.lSep + "BUILD FAILED") + org.apache.ant.cli.DefaultLogger.lSep);
            reportException(cause);
        }
        out.println((org.apache.ant.cli.DefaultLogger.lSep + "Total time: ") + org.apache.ant.cli.DefaultLogger.formatTime(java.lang.System.currentTimeMillis() - startTime));
    }

    /**
     * Description of the Method
     *
     * @param event
     * 		Description of Parameter
     */
    public void targetStarted(org.apache.ant.common.event.BuildEvent event) {
        if (org.apache.ant.common.event.MessageLevel.MSG_INFO <= messageOutputLevel) {
            org.apache.ant.common.model.Target target = ((org.apache.ant.common.model.Target) (event.getSource()));
            out.println((org.apache.ant.cli.DefaultLogger.lSep + target.getName()) + ":");
        }
    }

    /**
     * Description of the Method
     *
     * @param event
     * 		Description of Parameter
     */
    public void targetFinished(org.apache.ant.common.event.BuildEvent event) {
    }

    /**
     * Description of the Method
     *
     * @param event
     * 		Description of Parameter
     */
    public void taskStarted(org.apache.ant.common.event.BuildEvent event) {
    }

    /**
     * Description of the Method
     *
     * @param event
     * 		Description of Parameter
     */
    public void taskFinished(org.apache.ant.common.event.BuildEvent event) {
    }

    /**
     * Description of the Method
     *
     * @param event
     * 		Description of Parameter
     */
    public void messageLogged(org.apache.ant.common.event.BuildEvent event) {
        java.io.PrintStream logTo = (event.getPriority() == org.apache.ant.common.event.MessageLevel.MSG_ERR) ? err : out;
        // Filter out messages based on priority
        if (event.getPriority() <= messageOutputLevel) {
            java.lang.String name = null;
            java.lang.Object source = event.getSource();
            if (source instanceof org.apache.ant.common.antlib.Task) {
                name = ((org.apache.ant.common.antlib.Task) (source)).getTaskName();
            }
            if ((name == null) && (source instanceof org.apache.ant.common.antlib.ExecutionComponent)) {
                name = ((org.apache.ant.common.antlib.ExecutionComponent) (source)).getComponentType();
            }
            if (name != null) {
                // Print out the name of the task if we're in one
                if (!emacsMode) {
                    java.lang.String tag = ("[" + name) + "] ";
                    int indentSize = org.apache.ant.cli.DefaultLogger.LEFT_COLUMN_SIZE - tag.length();
                    for (int i = 0; i < indentSize; i++) {
                        logTo.print(" ");
                    }
                    logTo.print(tag);
                }
            }
            // Print the message
            logTo.println(event.getMessage());
        }
    }
}