/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.ant1;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.myrmidon.api.TaskContext;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.components.type.TypeException;
import org.apache.myrmidon.components.type.TypeFactory;
import org.apache.myrmidon.components.type.TypeManager;
import org.apache.myrmidon.framework.AbstractContainerTask;
import org.apache.tools.ant.Task;
/**
 * Adapter of Ant1 tasks to myrmidon.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class TaskAdapter extends org.apache.myrmidon.framework.AbstractContainerTask implements org.apache.avalon.framework.configuration.Configurable {
    private org.apache.tools.ant.Task m_task;

    private org.apache.myrmidon.libs.ant1.Ant1Project m_project = new org.apache.myrmidon.libs.ant1.Ant1Project();

    public TaskAdapter(final org.apache.tools.ant.Task task) {
        m_task = task;
    }

    protected final org.apache.tools.ant.Task getTask() {
        return m_task;
    }

    protected final org.apache.myrmidon.libs.ant1.Ant1Project getProject() {
        return m_project;
    }

    public void configure(final org.apache.avalon.framework.configuration.Configuration configuration) throws org.apache.avalon.framework.configuration.ConfigurationException {
        getTask().setTaskName(configuration.getName());
        // do configuration
        configure(getTask(), configuration);
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
        try {
            getProject().setLogger(getLogger());
            getProject().contextualize(getContext());
            getProject().init();
            getTask().setProject(getProject());
            getTask().init();
            getTask().execute();
        } catch (final java.lang.Exception e) {
            throw new org.apache.myrmidon.api.TaskException(e.getMessage(), e);
        }
    }
}