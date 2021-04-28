/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.executor;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.log.Logger;
import org.apache.myrmidon.api.TaskContext;
import org.apache.myrmidon.components.aspect.AspectManager;
import org.apache.myrmidon.components.builder.ProjectBuilder;
import org.apache.myrmidon.components.configurer.Configurer;
import org.apache.myrmidon.components.converter.ConverterRegistry;
import org.apache.myrmidon.components.converter.MasterConverter;
import org.apache.myrmidon.components.deployer.Deployer;
import org.apache.myrmidon.components.executor.Executor;
import org.apache.myrmidon.components.role.RoleManager;
import org.apache.myrmidon.components.type.TypeManager;
/**
 * Frames in which tasks are executed.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultExecutionFrame implements org.apache.myrmidon.components.executor.ExecutionFrame , org.apache.avalon.framework.logger.Loggable , org.apache.avalon.framework.context.Contextualizable , org.apache.avalon.framework.component.Composable {
    private org.apache.myrmidon.components.type.TypeManager m_typeManager;

    private org.apache.log.Logger m_logger;

    private org.apache.myrmidon.api.TaskContext m_context;

    private org.apache.avalon.framework.component.ComponentManager m_componentManager;

    public void setLogger(final org.apache.log.Logger logger) {
        m_logger = logger;
    }

    public void contextualize(final org.apache.avalon.framework.context.Context context) {
        m_context = ((org.apache.myrmidon.api.TaskContext) (context));
    }

    /**
     * Retrieve relevent services needed to deploy.
     *
     * @param componentManager
     * 		the ComponentManager
     * @exception ComponentException
     * 		if an error occurs
     */
    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        m_componentManager = componentManager;
        m_typeManager = ((org.apache.myrmidon.components.type.TypeManager) (componentManager.lookup(TypeManager.ROLE)));
    }

    public org.apache.myrmidon.components.type.TypeManager getTypeManager() {
        return m_typeManager;
    }

    public org.apache.log.Logger getLogger() {
        return m_logger;
    }

    public org.apache.myrmidon.api.TaskContext getContext() {
        return m_context;
    }

    public org.apache.avalon.framework.component.ComponentManager getComponentManager() {
        return m_componentManager;
    }
}