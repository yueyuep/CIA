/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.framework;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.myrmidon.api.AbstractTask;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.components.role.RoleManager;
import org.apache.myrmidon.components.type.DefaultTypeFactory;
import org.apache.myrmidon.components.type.TypeException;
import org.apache.myrmidon.components.type.TypeManager;
/**
 * Abstract task to extend to define a type.
 *
 * TODO: Make this support classpath sub-element in future
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractTypeDef extends org.apache.myrmidon.api.AbstractTask implements org.apache.avalon.framework.component.Composable {
    private java.io.File m_lib;

    private java.lang.String m_name;

    private java.lang.String m_className;

    private org.apache.myrmidon.components.type.TypeManager m_typeManager;

    private org.apache.myrmidon.components.role.RoleManager m_roleManager;

    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        m_typeManager = ((org.apache.myrmidon.components.type.TypeManager) (componentManager.lookup(TypeManager.ROLE)));
        m_roleManager = ((org.apache.myrmidon.components.role.RoleManager) (componentManager.lookup(RoleManager.ROLE)));
    }

    public void setLib(final java.io.File lib) {
        // In the future this would be replaced by ClassPath sub-element
        m_lib = lib;
    }

    public void setName(final java.lang.String name) {
        m_name = name;
    }

    public void setClassname(final java.lang.String className) {
        m_className = className;
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
        if (null == m_name) {
            throw new org.apache.myrmidon.api.TaskException("Must specify name parameter");
        } else if (null == m_className) {
            throw new org.apache.myrmidon.api.TaskException("Must specify classname parameter");
        }
        final java.lang.String typeName = getTypeName();
        final java.lang.String role = m_roleManager.getRoleForName(typeName);
        final java.lang.ClassLoader classLoader = createClassLoader();
        final org.apache.myrmidon.components.type.DefaultTypeFactory factory = new org.apache.myrmidon.components.type.DefaultTypeFactory(classLoader);
        factory.addNameClassMapping(m_name, m_className);
        try {
            m_typeManager.registerType(role, m_name, factory);
        } catch (final org.apache.myrmidon.components.type.TypeException te) {
            throw new org.apache.myrmidon.api.TaskException("Failed to register type", te);
        }
    }

    protected java.lang.ClassLoader createClassLoader() throws org.apache.myrmidon.api.TaskException {
        // TODO: Make this support classpath sub-element in future
        try {
            final java.net.URL url = m_lib.toURL();
            final java.lang.ClassLoader classLoader = java.lang.Thread.currentThread().getContextClassLoader();
            return new java.net.URLClassLoader(new java.net.URL[]{ url }, classLoader);
        } catch (final java.lang.Exception e) {
            throw new org.apache.myrmidon.api.TaskException("Failed to build classLoader due to: " + e, e);
        }
    }

    protected final org.apache.myrmidon.components.type.TypeManager getTypeManager() {
        return m_typeManager;
    }

    protected abstract java.lang.String getTypeName();
}