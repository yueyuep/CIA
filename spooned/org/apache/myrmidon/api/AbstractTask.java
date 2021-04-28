/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.api;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLoggable;
/**
 * This is the class that Task writers should extend to provide custom tasks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractTask extends org.apache.avalon.framework.logger.AbstractLoggable implements org.apache.myrmidon.api.Task , org.apache.avalon.framework.context.Contextualizable , org.apache.avalon.framework.activity.Initializable , org.apache.avalon.framework.activity.Disposable {
    // /Variable to hold context for use by sub-classes
    private org.apache.myrmidon.api.TaskContext m_context;

    /**
     * Retrieve context from container.
     *
     * @param context
     * 		the context
     */
    public void contextualize(final org.apache.avalon.framework.context.Context context) {
        m_context = ((org.apache.myrmidon.api.TaskContext) (context));
    }

    /**
     * This will be called before execute() method and checks any preconditions.
     *
     * @exception Exception
     * 		if an error occurs
     */
    public void initialize() throws java.lang.Exception {
    }

    /**
     * Execute task.
     * This method is called to perform actual work associated with task.
     * It is called after Task has been Configured and Initialized and before
     * beig Disposed (If task implements appropriate interfaces).
     *
     * @exception Exception
     * 		if an error occurs
     */
    public abstract void execute() throws org.apache.myrmidon.api.TaskException;

    /**
     * This will be called after execute() method.
     * Use this to clean up any resources associated with task.
     *
     * @exception Exception
     * 		if an error occurs
     */
    public void dispose() {
    }

    /**
     * Convenience method for sub-class to retrieve context.
     *
     * @return the context
     */
    protected final org.apache.myrmidon.api.TaskContext getContext() {
        return m_context;
    }
}