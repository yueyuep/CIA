/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.aspect;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log.Logger;
import org.apache.myrmidon.api.Task;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.aspects.AspectHandler;
import org.apache.myrmidon.aspects.NoopAspectHandler;
/**
 * Manage and propogate Aspects.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultAspectManager implements org.apache.myrmidon.components.aspect.AspectManager , org.apache.avalon.framework.activity.Initializable {
    private java.util.HashMap m_aspectMap = new java.util.HashMap();

    private org.apache.myrmidon.aspects.AspectHandler[] m_aspects = new org.apache.myrmidon.aspects.AspectHandler[0];

    private java.lang.String[] m_names = new java.lang.String[0];

    public void initialize() throws java.lang.Exception {
        // /UGLY HACK!!!!
        addAspectHandler("ant", new org.apache.myrmidon.aspects.NoopAspectHandler());
        addAspectHandler("doc", new org.apache.myrmidon.aspects.NoopAspectHandler());
    }

    public synchronized void addAspectHandler(final java.lang.String name, final org.apache.myrmidon.aspects.AspectHandler handler) throws org.apache.myrmidon.api.TaskException {
        m_aspectMap.put(name, handler);
        rebuildArrays();
    }

    public synchronized void removeAspectHandler(final java.lang.String name, final org.apache.myrmidon.aspects.AspectHandler handler) throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.aspects.AspectHandler entry = ((org.apache.myrmidon.aspects.AspectHandler) (m_aspectMap.remove(name)));
        if (null == entry) {
            throw new org.apache.myrmidon.api.TaskException(("No such aspect with name '" + name) + "'");
        }
        rebuildArrays();
    }

    private void rebuildArrays() {
        m_aspects = ((org.apache.myrmidon.aspects.AspectHandler[]) (m_aspectMap.values().toArray(m_aspects)));
        m_names = ((java.lang.String[]) (m_aspectMap.keySet().toArray(m_names)));
    }

    public java.lang.String[] getNames() {
        return m_names;
    }

    public void dispatchAspectSettings(final java.lang.String name, final org.apache.avalon.framework.parameters.Parameters parameters, final org.apache.avalon.framework.configuration.Configuration[] elements) throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.aspects.AspectHandler handler = ((org.apache.myrmidon.aspects.AspectHandler) (m_aspectMap.get(name)));
        if (null == handler) {
            throw new org.apache.myrmidon.api.TaskException(("No such aspect with name '" + name) + "'");
        }
        handler.aspectSettings(parameters, elements);
    }

    public org.apache.avalon.framework.configuration.Configuration preCreate(final org.apache.avalon.framework.configuration.Configuration configuration) throws org.apache.myrmidon.api.TaskException {
        org.apache.avalon.framework.configuration.Configuration model = configuration;
        final org.apache.myrmidon.aspects.AspectHandler[] aspects = m_aspects;
        for (int i = 0; i < aspects.length; i++) {
            model = aspects[i].preCreate(model);
        }
        return model;
    }

    public void aspectSettings(final org.apache.avalon.framework.parameters.Parameters parameters, final org.apache.avalon.framework.configuration.Configuration[] elements) throws org.apache.myrmidon.api.TaskException {
        throw new java.lang.UnsupportedOperationException("Can not provide Settings to AspectManager");
    }

    public void postCreate(final org.apache.myrmidon.api.Task task) throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.aspects.AspectHandler[] aspects = m_aspects;
        for (int i = 0; i < aspects.length; i++) {
            aspects[i].postCreate(task);
        }
    }

    public void preLoggable(final org.apache.log.Logger logger) throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.aspects.AspectHandler[] aspects = m_aspects;
        for (int i = 0; i < aspects.length; i++) {
            aspects[i].preLoggable(logger);
        }
    }

    public void preConfigure(final org.apache.avalon.framework.configuration.Configuration taskModel) throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.aspects.AspectHandler[] aspects = m_aspects;
        for (int i = 0; i < aspects.length; i++) {
            aspects[i].preConfigure(taskModel);
        }
    }

    public void preExecute() throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.aspects.AspectHandler[] aspects = m_aspects;
        for (int i = 0; i < aspects.length; i++) {
            aspects[i].preExecute();
        }
    }

    public void preDestroy() throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.aspects.AspectHandler[] aspects = m_aspects;
        for (int i = 0; i < aspects.length; i++) {
            aspects[i].preDestroy();
        }
    }

    public boolean error(final org.apache.myrmidon.api.TaskException te) throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.aspects.AspectHandler[] aspects = m_aspects;
        for (int i = 0; i < aspects.length; i++) {
            if (true == aspects[i].error(te)) {
                return true;
            }
        }
        return false;
    }
}