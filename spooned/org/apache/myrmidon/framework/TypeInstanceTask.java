/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.framework;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.myrmidon.api.TaskContext;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.components.type.TypeException;
import org.apache.myrmidon.components.type.TypeFactory;
import org.apache.myrmidon.components.type.TypeManager;
/**
 * This is the property "task" to declare a binding of a datatype to a name.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class TypeInstanceTask extends org.apache.myrmidon.framework.AbstractContainerTask implements org.apache.avalon.framework.configuration.Configurable {
    private java.lang.String m_id;

    private java.lang.Object m_value;

    private boolean m_localScope = true;

    private org.apache.myrmidon.components.type.TypeFactory m_factory;

    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        super.compose(componentManager);
        final org.apache.myrmidon.components.type.TypeManager typeManager = ((org.apache.myrmidon.components.type.TypeManager) (componentManager.lookup(TypeManager.ROLE)));
        try {
            m_factory = typeManager.getFactory(DataType.ROLE);
        } catch (final org.apache.myrmidon.components.type.TypeException te) {
            throw new org.apache.avalon.framework.component.ComponentException("Unable to retrieve factory from TypeManager", te);
        }
    }

    public void configure(final org.apache.avalon.framework.configuration.Configuration configuration) throws org.apache.avalon.framework.configuration.ConfigurationException {
        final org.apache.avalon.framework.configuration.DefaultConfiguration newConfiguration = new org.apache.avalon.framework.configuration.DefaultConfiguration(configuration.getName(), configuration.getLocation());
        final java.lang.String[] attributes = configuration.getAttributeNames();
        for (int i = 0; i < attributes.length; i++) {
            final java.lang.String name = attributes[i];
            final java.lang.String value = configuration.getAttribute(name);
            if (name.equals("id") || name.equals("local-scope")) {
                configure(this, name, value);
            } else {
                newConfiguration.setAttribute(name, value);
            }
        }
        final org.apache.avalon.framework.configuration.Configuration[] children = configuration.getChildren();
        for (int i = 0; i < children.length; i++) {
            newConfiguration.addChild(children[i]);
        }
        try {
            m_value = m_factory.create(configuration.getName());
        } catch (final java.lang.Exception e) {
            throw new org.apache.avalon.framework.configuration.ConfigurationException("Unable to create datatype", e);
        }
        configure(m_value, newConfiguration);
    }

    public void setId(final java.lang.String id) {
        m_id = id;
    }

    public void setLocalScope(final boolean localScope) {
        m_localScope = localScope;
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
        if (null == m_id) {
            throw new org.apache.myrmidon.api.TaskException("Id must be specified");
        }
        if (m_localScope) {
            getContext().setProperty(m_id, m_value);
        } else {
            getContext().setProperty(m_id, m_value, TaskContext.PARENT);
        }
    }
}