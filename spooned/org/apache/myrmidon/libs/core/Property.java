/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.core;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.myrmidon.api.TaskContext;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.components.type.TypeException;
import org.apache.myrmidon.components.type.TypeFactory;
import org.apache.myrmidon.components.type.TypeManager;
import org.apache.myrmidon.framework.AbstractContainerTask;
import org.apache.myrmidon.framework.DataType;
/**
 * This is the property "task" to declare a binding of a datatype to a name.
 *
 * TODO: Determine final format of property task.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Property extends org.apache.myrmidon.framework.AbstractContainerTask implements org.apache.avalon.framework.configuration.Configurable {
    private java.lang.String m_name;

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
        final java.lang.String[] attributes = configuration.getAttributeNames();
        for (int i = 0; i < attributes.length; i++) {
            final java.lang.String name = attributes[i];
            final java.lang.String value = configuration.getAttribute(name);
            configure(this, name, value);
        }
        final org.apache.avalon.framework.configuration.Configuration[] children = configuration.getChildren();
        for (int i = 0; i < children.length; i++) {
            try {
                final org.apache.myrmidon.framework.DataType value = ((org.apache.myrmidon.framework.DataType) (m_factory.create(children[i].getName())));
                configure(value, children[i]);
                setValue(value);
            } catch (final java.lang.Exception e) {
                throw new org.apache.avalon.framework.configuration.ConfigurationException("Unable to set datatype", e);
            }
        }
    }

    public void setName(final java.lang.String name) {
        m_name = name;
    }

    public void setValue(final java.lang.Object value) throws org.apache.myrmidon.api.TaskException {
        if (null != m_value) {
            throw new org.apache.myrmidon.api.TaskException("Value can not be set multiple times");
        }
        m_value = value;
    }

    public void setLocalScope(final boolean localScope) {
        m_localScope = localScope;
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
        if (null == m_name) {
            throw new org.apache.myrmidon.api.TaskException("Name must be specified");
        }
        if (null == m_value) {
            throw new org.apache.myrmidon.api.TaskException("Value must be specified");
        }
        if (m_localScope) {
            getContext().setProperty(m_name, m_value);
        } else {
            getContext().setProperty(m_name, m_value, TaskContext.PARENT);
        }
    }
}