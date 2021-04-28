/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.executor;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.log.Logger;
import org.apache.myrmidon.api.Task;
import org.apache.myrmidon.api.TaskContext;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.components.configurer.Configurer;
import org.apache.myrmidon.components.type.TypeException;
import org.apache.myrmidon.components.type.TypeFactory;
import org.apache.myrmidon.components.type.TypeManager;
public class DefaultExecutor extends org.apache.avalon.framework.logger.AbstractLoggable implements org.apache.myrmidon.components.executor.Executor , org.apache.avalon.framework.component.Composable {
    private org.apache.myrmidon.components.configurer.Configurer m_configurer;

    /**
     * Retrieve relevent services needed to deploy.
     *
     * @param componentManager
     * 		the ComponentManager
     * @exception ComponentException
     * 		if an error occurs
     */
    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        m_configurer = ((org.apache.myrmidon.components.configurer.Configurer) (componentManager.lookup(Configurer.ROLE)));
    }

    public void execute(final org.apache.avalon.framework.configuration.Configuration taskModel, final org.apache.myrmidon.components.executor.ExecutionFrame frame) throws org.apache.myrmidon.api.TaskException {
        getLogger().debug("Creating");
        final org.apache.myrmidon.api.Task task = createTask(taskModel.getName(), frame);
        doLoggable(task, taskModel, frame.getLogger());
        getLogger().debug("Contextualizing");
        doContextualize(task, taskModel, frame.getContext());
        getLogger().debug("Composing");
        doCompose(task, taskModel, frame.getComponentManager());
        getLogger().debug("Configuring");
        doConfigure(task, taskModel, frame.getContext());
        getLogger().debug("Initializing");
        doInitialize(task, taskModel);
        getLogger().debug("Running");
        task.execute();
        getLogger().debug("Disposing");
        doDispose(task, taskModel);
    }

    protected final org.apache.myrmidon.api.Task createTask(final java.lang.String name, final org.apache.myrmidon.components.executor.ExecutionFrame frame) throws org.apache.myrmidon.api.TaskException {
        try {
            final org.apache.myrmidon.components.type.TypeFactory factory = frame.getTypeManager().getFactory(Task.ROLE);
            return ((org.apache.myrmidon.api.Task) (factory.create(name)));
        } catch (final org.apache.myrmidon.components.type.TypeException te) {
            throw new org.apache.myrmidon.api.TaskException("Unable to create task " + name, te);
        }
    }

    protected final void doConfigure(final org.apache.myrmidon.api.Task task, final org.apache.avalon.framework.configuration.Configuration taskModel, final org.apache.myrmidon.api.TaskContext context) throws org.apache.myrmidon.api.TaskException {
        try {
            m_configurer.configure(task, taskModel, context);
        } catch (final java.lang.Throwable throwable) {
            throw new org.apache.myrmidon.api.TaskException(((((("Error configuring task " + taskModel.getName()) + " at ") + taskModel.getLocation()) + "(Reason: ") + throwable.getMessage()) + ")", throwable);
        }
    }

    protected final void doCompose(final org.apache.myrmidon.api.Task task, final org.apache.avalon.framework.configuration.Configuration taskModel, final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.myrmidon.api.TaskException {
        if (task instanceof org.apache.avalon.framework.component.Composable) {
            try {
                ((org.apache.avalon.framework.component.Composable) (task)).compose(componentManager);
            } catch (final java.lang.Throwable throwable) {
                throw new org.apache.myrmidon.api.TaskException(((((("Error composing task " + taskModel.getName()) + " at ") + taskModel.getLocation()) + "(Reason: ") + throwable.getMessage()) + ")", throwable);
            }
        }
    }

    protected final void doContextualize(final org.apache.myrmidon.api.Task task, final org.apache.avalon.framework.configuration.Configuration taskModel, final org.apache.myrmidon.api.TaskContext context) throws org.apache.myrmidon.api.TaskException {
        try {
            if (task instanceof org.apache.avalon.framework.context.Contextualizable) {
                ((org.apache.avalon.framework.context.Contextualizable) (task)).contextualize(context);
            }
        } catch (final java.lang.Throwable throwable) {
            throw new org.apache.myrmidon.api.TaskException(((((("Error contextualizing task " + taskModel.getName()) + " at ") + taskModel.getLocation()) + "(Reason: ") + throwable.getMessage()) + ")", throwable);
        }
    }

    protected final void doDispose(final org.apache.myrmidon.api.Task task, final org.apache.avalon.framework.configuration.Configuration taskModel) throws org.apache.myrmidon.api.TaskException {
        if (task instanceof org.apache.avalon.framework.activity.Disposable) {
            try {
                ((org.apache.avalon.framework.activity.Disposable) (task)).dispose();
            } catch (final java.lang.Throwable throwable) {
                throw new org.apache.myrmidon.api.TaskException(((((("Error disposing task " + taskModel.getName()) + " at ") + taskModel.getLocation()) + "(Reason: ") + throwable.getMessage()) + ")", throwable);
            }
        }
    }

    protected final void doLoggable(final org.apache.myrmidon.api.Task task, final org.apache.avalon.framework.configuration.Configuration taskModel, final org.apache.log.Logger logger) throws org.apache.myrmidon.api.TaskException {
        if (task instanceof org.apache.avalon.framework.logger.Loggable) {
            try {
                ((org.apache.avalon.framework.logger.Loggable) (task)).setLogger(logger);
            } catch (final java.lang.Throwable throwable) {
                throw new org.apache.myrmidon.api.TaskException(((((("Error setting logger for task " + taskModel.getName()) + " at ") + taskModel.getLocation()) + "(Reason: ") + throwable.getMessage()) + ")", throwable);
            }
        }
    }

    protected final void doInitialize(final org.apache.myrmidon.api.Task task, final org.apache.avalon.framework.configuration.Configuration taskModel) throws org.apache.myrmidon.api.TaskException {
        if (task instanceof org.apache.avalon.framework.activity.Initializable) {
            try {
                ((org.apache.avalon.framework.activity.Initializable) (task)).initialize();
            } catch (final java.lang.Throwable throwable) {
                throw new org.apache.myrmidon.api.TaskException(((((("Error initializing task " + taskModel.getName()) + " at ") + taskModel.getLocation()) + "(Reason: ") + throwable.getMessage()) + ")", throwable);
            }
        }
    }
}