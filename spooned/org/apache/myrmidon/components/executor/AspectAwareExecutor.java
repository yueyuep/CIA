/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.executor;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log.Logger;
import org.apache.myrmidon.api.Task;
import org.apache.myrmidon.api.TaskContext;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.aspects.AspectHandler;
import org.apache.myrmidon.components.aspect.AspectManager;
public class AspectAwareExecutor extends org.apache.myrmidon.components.executor.DefaultExecutor {
    private static final org.apache.avalon.framework.parameters.Parameters EMPTY_PARAMETERS;

    private static final org.apache.avalon.framework.configuration.Configuration[] EMPTY_ELEMENTS = new org.apache.avalon.framework.configuration.Configuration[0];

    static {
        EMPTY_PARAMETERS = new org.apache.avalon.framework.parameters.Parameters();
        EMPTY_PARAMETERS.makeReadOnly();
    }

    private org.apache.myrmidon.components.aspect.AspectManager m_aspectManager;

    /**
     * Retrieve relevent services.
     *
     * @param componentManager
     * 		the ComponentManager
     * @exception ComponentException
     * 		if an error occurs
     */
    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        super.compose(componentManager);
        m_aspectManager = ((org.apache.myrmidon.components.aspect.AspectManager) (componentManager.lookup(AspectManager.ROLE)));
    }

    public void execute(final org.apache.avalon.framework.configuration.Configuration taskModel, final org.apache.myrmidon.components.executor.ExecutionFrame frame) throws org.apache.myrmidon.api.TaskException {
        try {
            executeTask(taskModel, frame);
        } catch (final org.apache.myrmidon.api.TaskException te) {
            if (false == getAspectManager().error(te)) {
                throw te;
            }
        }
    }

    private void executeTask(org.apache.avalon.framework.configuration.Configuration taskModel, final org.apache.myrmidon.components.executor.ExecutionFrame frame) throws org.apache.myrmidon.api.TaskException {
        taskModel = getAspectManager().preCreate(taskModel);
        taskModel = prepareAspects(taskModel);
        getLogger().debug("Pre-Create");
        final org.apache.myrmidon.api.Task task = createTask(taskModel.getName(), frame);
        getAspectManager().postCreate(task);
        getLogger().debug("Pre-Loggable");
        final org.apache.log.Logger logger = frame.getLogger();
        getAspectManager().preLoggable(logger);
        doLoggable(task, taskModel, logger);
        getLogger().debug("Contextualizing");
        doContextualize(task, taskModel, frame.getContext());
        getLogger().debug("Composing");
        doCompose(task, taskModel, frame.getComponentManager());
        getLogger().debug("Configuring");
        getAspectManager().preConfigure(taskModel);
        doConfigure(task, taskModel, frame.getContext());
        getLogger().debug("Initializing");
        doInitialize(task, taskModel);
        getLogger().debug("Executing");
        getAspectManager().preExecute();
        doExecute(taskModel, task);
        getLogger().debug("Disposing");
        getAspectManager().preDestroy();
        doDispose(task, taskModel);
    }

    protected void doExecute(final org.apache.avalon.framework.configuration.Configuration taskModel, final org.apache.myrmidon.api.Task task) throws org.apache.myrmidon.api.TaskException {
        task.execute();
    }

    // TODO: Extract and clean taskModel here.
    // Get all parameters from model and provide to appropriate aspect.
    // aspect( final Parameters parameters, final Configuration[] elements )
    private final org.apache.avalon.framework.configuration.Configuration prepareAspects(final org.apache.avalon.framework.configuration.Configuration taskModel) throws org.apache.myrmidon.api.TaskException {
        final org.apache.avalon.framework.configuration.DefaultConfiguration newTaskModel = new org.apache.avalon.framework.configuration.DefaultConfiguration(taskModel.getName(), taskModel.getLocation());
        final java.util.HashMap parameterMap = new java.util.HashMap();
        final java.util.HashMap elementMap = new java.util.HashMap();
        processAttributes(taskModel, newTaskModel, parameterMap);
        processElements(taskModel, newTaskModel, elementMap);
        dispatchAspectsSettings(parameterMap, elementMap);
        checkForUnusedSettings(parameterMap, elementMap);
        return newTaskModel;
    }

    private final void dispatchAspectsSettings(final java.util.HashMap parameterMap, final java.util.HashMap elementMap) throws org.apache.myrmidon.api.TaskException {
        final java.lang.String[] names = getAspectManager().getNames();
        for (int i = 0; i < names.length; i++) {
            final java.util.ArrayList elementList = ((java.util.ArrayList) (elementMap.remove(names[i])));
            org.apache.avalon.framework.parameters.Parameters parameters = ((org.apache.avalon.framework.parameters.Parameters) (parameterMap.remove(names[i])));
            if (null == parameters)
                parameters = org.apache.myrmidon.components.executor.AspectAwareExecutor.EMPTY_PARAMETERS;

            org.apache.avalon.framework.configuration.Configuration[] elements = null;
            if (null == elementList)
                elements = org.apache.myrmidon.components.executor.AspectAwareExecutor.EMPTY_ELEMENTS;
            else {
                elements = ((org.apache.avalon.framework.configuration.Configuration[]) (elementList.toArray(org.apache.myrmidon.components.executor.AspectAwareExecutor.EMPTY_ELEMENTS)));
            }
            dispatch(names[i], parameters, elements);
        }
    }

    private final void checkForUnusedSettings(final java.util.HashMap parameterMap, final java.util.HashMap elementMap) throws org.apache.myrmidon.api.TaskException {
        if (0 != parameterMap.size()) {
            final java.lang.String[] namespaces = ((java.lang.String[]) (parameterMap.keySet().toArray(new java.lang.String[0])));
            for (int i = 0; i < namespaces.length; i++) {
                final java.lang.String namespace = namespaces[i];
                final org.apache.avalon.framework.parameters.Parameters parameters = ((org.apache.avalon.framework.parameters.Parameters) (parameterMap.get(namespace)));
                final java.util.ArrayList elementList = ((java.util.ArrayList) (elementMap.remove(namespace)));
                org.apache.avalon.framework.configuration.Configuration[] elements = null;
                if (null == elementList)
                    elements = org.apache.myrmidon.components.executor.AspectAwareExecutor.EMPTY_ELEMENTS;
                else {
                    elements = ((org.apache.avalon.framework.configuration.Configuration[]) (elementList.toArray(org.apache.myrmidon.components.executor.AspectAwareExecutor.EMPTY_ELEMENTS)));
                }
                unusedSetting(namespace, parameters, elements);
            }
        }
        if (0 != elementMap.size()) {
            final java.lang.String[] namespaces = ((java.lang.String[]) (elementMap.keySet().toArray(new java.lang.String[0])));
            for (int i = 0; i < namespaces.length; i++) {
                final java.lang.String namespace = namespaces[i];
                final java.util.ArrayList elementList = ((java.util.ArrayList) (elementMap.remove(namespace)));
                final org.apache.avalon.framework.configuration.Configuration[] elements = ((org.apache.avalon.framework.configuration.Configuration[]) (elementList.toArray(org.apache.myrmidon.components.executor.AspectAwareExecutor.EMPTY_ELEMENTS)));
                unusedSetting(namespace, org.apache.myrmidon.components.executor.AspectAwareExecutor.EMPTY_PARAMETERS, elements);
            }
        }
    }

    private void unusedSetting(final java.lang.String namespace, final org.apache.avalon.framework.parameters.Parameters parameters, final org.apache.avalon.framework.configuration.Configuration[] elements) throws org.apache.myrmidon.api.TaskException {
        throw new org.apache.myrmidon.api.TaskException(((((("Unused aspect settings for namespace " + namespace) + " (parameterCount=") + parameters.getNames().length) + " elementCount=") + elements.length) + ")");
    }

    private void dispatch(final java.lang.String namespace, final org.apache.avalon.framework.parameters.Parameters parameters, final org.apache.avalon.framework.configuration.Configuration[] elements) throws org.apache.myrmidon.api.TaskException {
        getAspectManager().dispatchAspectSettings(namespace, parameters, elements);
        if (getLogger().isDebugEnabled()) {
            getLogger().debug((((("Dispatching Aspect Settings to: " + namespace) + " parameterCount=") + parameters.getNames().length) + " elementCount=") + elements.length);
        }
    }

    private final void processElements(final org.apache.avalon.framework.configuration.Configuration taskModel, final org.apache.avalon.framework.configuration.DefaultConfiguration newTaskModel, final java.util.HashMap map) {
        final org.apache.avalon.framework.configuration.Configuration[] elements = taskModel.getChildren();
        for (int i = 0; i < elements.length; i++) {
            final java.lang.String name = elements[i].getName();
            final int index = name.indexOf(':');
            if ((-1) == index) {
                newTaskModel.addChild(elements[i]);
            } else {
                final java.lang.String namespace = name.substring(0, index);
                final java.lang.String localName = name.substring(index + 1);
                final java.util.ArrayList elementSet = getElements(namespace, map);
                elementSet.add(elements[i]);
            }
        }
    }

    private final void processAttributes(final org.apache.avalon.framework.configuration.Configuration taskModel, final org.apache.avalon.framework.configuration.DefaultConfiguration newTaskModel, final java.util.HashMap map) {
        final java.lang.String[] attributes = taskModel.getAttributeNames();
        for (int i = 0; i < attributes.length; i++) {
            final java.lang.String name = attributes[i];
            final java.lang.String value = taskModel.getAttribute(name, null);
            final int index = name.indexOf(':');
            if ((-1) == index) {
                newTaskModel.setAttribute(name, value);
            } else {
                final java.lang.String namespace = name.substring(0, index);
                final java.lang.String localName = name.substring(index + 1);
                final org.apache.avalon.framework.parameters.Parameters parameters = getParameters(namespace, map);
                parameters.setParameter(localName, value);
            }
        }
    }

    private final java.util.ArrayList getElements(final java.lang.String namespace, final java.util.HashMap map) {
        java.util.ArrayList elements = ((java.util.ArrayList) (map.get(namespace)));
        if (null == elements) {
            elements = new java.util.ArrayList();
            map.put(namespace, elements);
        }
        return elements;
    }

    private final org.apache.avalon.framework.parameters.Parameters getParameters(final java.lang.String namespace, final java.util.HashMap map) {
        org.apache.avalon.framework.parameters.Parameters parameters = ((org.apache.avalon.framework.parameters.Parameters) (map.get(namespace)));
        if (null == parameters) {
            parameters = new org.apache.avalon.framework.parameters.Parameters();
            map.put(namespace, parameters);
        }
        return parameters;
    }

    protected final org.apache.myrmidon.components.aspect.AspectManager getAspectManager() {
        return m_aspectManager;
    }
}