/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.runtime;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.myrmidon.api.AbstractTask;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.aspects.AspectHandler;
import org.apache.myrmidon.components.aspect.AspectManager;
import org.apache.myrmidon.components.type.TypeException;
import org.apache.myrmidon.components.type.TypeFactory;
import org.apache.myrmidon.components.type.TypeManager;
import org.apache.myrmidon.framework.AbstractContainerTask;
/**
 * Task that definesMethod to register a single converter.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Facility extends org.apache.myrmidon.framework.AbstractContainerTask implements org.apache.avalon.framework.component.Composable , org.apache.avalon.framework.configuration.Configurable {
    private java.lang.String m_namespace;

    private org.apache.myrmidon.aspects.AspectHandler m_aspectHandler;

    private org.apache.myrmidon.components.aspect.AspectManager m_aspectManager;

    private org.apache.myrmidon.components.type.TypeFactory m_factory;

    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        super.compose(componentManager);
        m_aspectManager = ((org.apache.myrmidon.components.aspect.AspectManager) (componentManager.lookup(AspectManager.ROLE)));
        final org.apache.myrmidon.components.type.TypeManager typeManager = ((org.apache.myrmidon.components.type.TypeManager) (componentManager.lookup(TypeManager.ROLE)));
        try {
            m_factory = typeManager.getFactory(AspectHandler.ROLE);
        } catch (final org.apache.myrmidon.components.type.TypeException te) {
            throw new org.apache.avalon.framework.component.ComponentException("Unable to retrieve factory from TypeManager", te);
        }
    }

    public void configure(final org.apache.avalon.framework.configuration.Configuration configuration) throws org.apache.avalon.framework.configuration.ConfigurationException {
        final java.lang.String[] attributes = configuration.getAttributeNames();
        for (int i = 0; i < attributes.length; i++) {
            final java.lang.String name = attributes[i];
            final java.lang.String value = configuration.getAttribute(name);
            configure(this, name, value);
        }
        final org.apache.avalon.framework.configuration.Configuration[] children = configuration.getChildren();
        if (1 == children.length) {
            try {
                m_aspectHandler = ((org.apache.myrmidon.aspects.AspectHandler) (m_factory.create(children[0].getName())));
            } catch (final java.lang.Exception e) {
                throw new org.apache.avalon.framework.configuration.ConfigurationException(("Failed to create aspect handler of type '" + children[0].getName()) + "'", e);
            }
            configure(m_aspectHandler, children[0]);
        } else {
            throw new org.apache.avalon.framework.configuration.ConfigurationException("Expected one sub-element to " + "configure facility");
        }
    }

    public void setNamespace(final java.lang.String namespace) {
        m_namespace = namespace;
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
        if (null == m_namespace) {
            throw new org.apache.myrmidon.api.TaskException("Must specify namespace parameter");
        }
        m_aspectManager.addAspectHandler(m_namespace, m_aspectHandler);
    }
}