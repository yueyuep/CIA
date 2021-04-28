/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.aspects;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log.Logger;
import org.apache.myrmidon.api.Task;
import org.apache.myrmidon.api.TaskException;
/**
 * AspectHandler is the interface through which aspects are handled.
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractAspectHandler implements org.apache.myrmidon.aspects.AspectHandler {
    private org.apache.avalon.framework.parameters.Parameters m_aspectParameters;

    private org.apache.avalon.framework.configuration.Configuration[] m_aspectElements;

    private org.apache.myrmidon.api.Task m_task;

    private org.apache.log.Logger m_logger;

    private org.apache.avalon.framework.configuration.Configuration m_taskModel;

    public org.apache.avalon.framework.configuration.Configuration preCreate(final org.apache.avalon.framework.configuration.Configuration taskModel) throws org.apache.myrmidon.api.TaskException {
        return taskModel;
    }

    public void aspectSettings(final org.apache.avalon.framework.parameters.Parameters parameters, final org.apache.avalon.framework.configuration.Configuration[] elements) throws org.apache.myrmidon.api.TaskException {
        m_aspectParameters = parameters;
        m_aspectElements = elements;
    }

    public void postCreate(final org.apache.myrmidon.api.Task task) throws org.apache.myrmidon.api.TaskException {
        m_task = task;
    }

    public void preLoggable(final org.apache.log.Logger logger) throws org.apache.myrmidon.api.TaskException {
        m_logger = logger;
    }

    public void preConfigure(final org.apache.avalon.framework.configuration.Configuration taskModel) throws org.apache.myrmidon.api.TaskException {
        m_taskModel = taskModel;
    }

    public void preExecute() throws org.apache.myrmidon.api.TaskException {
    }

    public void preDestroy() throws org.apache.myrmidon.api.TaskException {
        reset();
    }

    public boolean error(final org.apache.myrmidon.api.TaskException te) throws org.apache.myrmidon.api.TaskException {
        reset();
        return false;
    }

    protected void reset() {
        m_aspectParameters = null;
        m_aspectElements = null;
        m_task = null;
        m_logger = null;
        m_taskModel = null;
    }

    protected final org.apache.avalon.framework.configuration.Configuration getTaskModel() {
        return m_taskModel;
    }

    protected final org.apache.myrmidon.api.Task getTask() {
        return m_task;
    }

    protected final org.apache.log.Logger getLogger() {
        return m_logger;
    }

    protected final org.apache.avalon.framework.configuration.Configuration[] getAspectElements() {
        return m_aspectElements;
    }

    protected final org.apache.avalon.framework.parameters.Parameters getAspectParameters() {
        return m_aspectParameters;
    }
}