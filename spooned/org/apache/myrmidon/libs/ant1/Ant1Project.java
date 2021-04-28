/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.ant1;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.log.Logger;
import org.apache.myrmidon.api.TaskContext;
import org.apache.tools.ant.*;
import org.apache.tools.ant.Project;
public class Ant1Project extends org.apache.tools.ant.Project implements org.apache.avalon.framework.logger.Loggable , org.apache.avalon.framework.context.Contextualizable {
    private org.apache.log.Logger m_logger;

    // /Variable to hold context for use by sub-classes
    private org.apache.myrmidon.api.TaskContext m_context;

    public void setLogger(final org.apache.log.Logger logger) {
        m_logger = logger;
    }

    protected final org.apache.log.Logger getLogger() {
        return m_logger;
    }

    /**
     * Retrieve context from container.
     *
     * @param context
     * 		the context
     */
    public void contextualize(final org.apache.avalon.framework.context.Context context) {
        m_context = ((org.apache.myrmidon.api.TaskContext) (context));
    }

    protected final org.apache.myrmidon.api.TaskContext getContext() {
        return m_context;
    }

    /**
     * Initialise the project.
     */
    public void init() throws org.apache.myrmidon.libs.ant1.BuildException {
        setJavaVersionProperty();
    }

    public void setProperty(final java.lang.String name, final java.lang.String value) {
        try {
            getContext().setProperty(name, value);
        } catch (final java.lang.Exception e) {
            getLogger().warn((("Failed to set property " + name) + " to ") + value, e);
        }
    }

    public void setUserProperty(final java.lang.String name, final java.lang.String value) {
        setProperty(name, value);
    }

    public java.lang.String getProperty(final java.lang.String name) {
        return "" + getContext().getProperty(name);
    }

    public java.lang.String getUserProperty(final java.lang.String name) {
        return getProperty(name);
    }

    public java.lang.String getName() {
        return "Ant1 Project";
    }

    public org.apache.myrmidon.libs.ant1.Task createTask(final java.lang.String taskType) throws org.apache.myrmidon.libs.ant1.BuildException {
        throw new java.lang.UnsupportedOperationException();
    }

    public java.lang.Object createDataType(final java.lang.String typeName) throws org.apache.myrmidon.libs.ant1.BuildException {
        throw new java.lang.UnsupportedOperationException();
    }

    public java.io.File resolveFile(final java.lang.String fileName) {
        try {
            return getContext().resolveFile(fileName);
        } catch (final java.lang.Exception e) {
            return null;
        }
    }

    protected void fireBuildStarted() {
    }

    protected void fireBuildFinished(java.lang.Throwable exception) {
    }

    protected void fireTargetStarted(Target target) {
    }

    protected void fireTargetFinished(Target target, java.lang.Throwable exception) {
    }

    protected void fireTaskStarted(Task task) {
    }

    protected void fireTaskFinished(Task task, java.lang.Throwable exception) {
    }

    private void fireMessageLoggedEvent(BuildEvent event, java.lang.String message, int priority) {
        messageLogged(message, priority);
    }

    protected void fireMessageLogged(org.apache.tools.ant.Project project, java.lang.String message, int priority) {
        messageLogged(message, priority);
    }

    protected void fireMessageLogged(Target target, java.lang.String message, int priority) {
        messageLogged(message, priority);
    }

    protected void fireMessageLogged(Task task, java.lang.String message, int priority) {
        messageLogged(message, priority);
    }

    private void messageLogged(java.lang.String message, int priority) {
        switch (priority) {
            case MSG_ERR :
                getLogger().error(message);
                break;
            case MSG_WARN :
                getLogger().warn(message);
                break;
            case MSG_INFO :
                getLogger().info(message);
                break;
            case MSG_VERBOSE :
                getLogger().debug(message);
                break;
            case MSG_DEBUG :
                getLogger().debug(message);
                break;
            default :
                getLogger().debug(message);
        }
    }
}