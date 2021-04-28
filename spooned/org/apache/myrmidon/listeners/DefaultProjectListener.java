/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.listeners;
import org.apache.avalon.framework.ExceptionUtil;
/**
 * Default listener that emulates the old ant listener notifications.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultProjectListener implements org.apache.myrmidon.listeners.ProjectListener {
    private java.lang.String m_prefix;

    /**
     * Notify listener of projectStarted event.
     */
    public void projectStarted() {
    }

    /**
     * Notify listener of projectFinished event.
     */
    public void projectFinished() {
    }

    /**
     * Notify listener of targetStarted event.
     *
     * @param targetName
     * 		the name of target
     */
    public void targetStarted(final java.lang.String targetName) {
        output(targetName + ":\n");
    }

    /**
     * Notify listener of targetFinished event.
     */
    public void targetFinished() {
    }

    /**
     * Notify listener of taskStarted event.
     *
     * @param taskName
     * 		the name of task
     */
    public void taskStarted(final java.lang.String taskName) {
        setPrefix(taskName);
    }

    /**
     * Notify listener of taskFinished event.
     */
    public void taskFinished() {
        setPrefix(null);
    }

    /**
     * Notify listener of log message event.
     *
     * @param message
     * 		the message
     */
    public void log(java.lang.String message) {
        output(message);
    }

    /**
     * Notify listener of log message event.
     *
     * @param message
     * 		the message
     * @param throwable
     * 		the throwable
     */
    public void log(java.lang.String message, java.lang.Throwable throwable) {
        output((message + "\n") + org.apache.avalon.framework.ExceptionUtil.printStackTrace(throwable, 5, true));
    }

    /**
     * Utility class to output data.
     * Overide in sub-classes to direct to a different destination.
     *
     * @param data
     * 		the data
     */
    protected void output(final java.lang.String data) {
        if (null != getPrefix())
            java.lang.System.out.println((("\t[" + getPrefix()) + "] ") + data);
        else
            java.lang.System.out.println(data);

    }

    protected final java.lang.String getPrefix() {
        return m_prefix;
    }

    protected final void setPrefix(final java.lang.String prefix) {
        m_prefix = prefix;
    }
}