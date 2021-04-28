/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.type;
/**
 * The interface that is used to manage types.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultTypeManager implements org.apache.myrmidon.components.type.TypeManager {
    // /Parent type manager to inherit values from.
    private final org.apache.myrmidon.components.type.DefaultTypeManager m_parent;

    // /Maps role to MultiSourceTypeFactory.
    private final java.util.HashMap m_roleMap = new java.util.HashMap();

    public DefaultTypeManager() {
        this(null);
    }

    public DefaultTypeManager(final org.apache.myrmidon.components.type.DefaultTypeManager parent) {
        m_parent = parent;
    }

    public void registerType(final java.lang.String role, final java.lang.String shorthandName, final org.apache.myrmidon.components.type.TypeFactory factory) throws org.apache.myrmidon.components.type.TypeException {
        final org.apache.myrmidon.components.type.MultiSourceTypeFactory msFactory = createFactory(role);
        msFactory.register(shorthandName, factory);
    }

    public org.apache.myrmidon.components.type.TypeFactory getFactory(final java.lang.String role) throws org.apache.myrmidon.components.type.TypeException {
        return createFactory(role);
    }

    public org.apache.myrmidon.components.type.TypeManager createChildTypeManager() {
        return new org.apache.myrmidon.components.type.DefaultTypeManager(this);
    }

    protected final org.apache.myrmidon.components.type.MultiSourceTypeFactory lookupFactory(final java.lang.String role) {
        return ((org.apache.myrmidon.components.type.MultiSourceTypeFactory) (m_roleMap.get(role)));
    }

    /**
     * Get a factory of appropriate role.
     * Create a Factory if none exists with same name.
     *
     * @param role
     * 		the role name(must be name of work interface)
     * @return the Factory for interface
     * @exception TypeException
     * 		role does not specify accessible work interface
     */
    private org.apache.myrmidon.components.type.MultiSourceTypeFactory createFactory(final java.lang.String role) throws org.apache.myrmidon.components.type.TypeException {
        org.apache.myrmidon.components.type.MultiSourceTypeFactory factory = ((org.apache.myrmidon.components.type.MultiSourceTypeFactory) (m_roleMap.get(role)));
        if (null != factory) {
            return factory;
        }
        final org.apache.myrmidon.components.type.MultiSourceTypeFactory parentFactory = getParentTypedFactory(role);
        if (null != parentFactory) {
            factory = new org.apache.myrmidon.components.type.MultiSourceTypeFactory(parentFactory);
        }
        // /If we haven't got factory try to create a new one
        if (null == factory) {
            try {
                // TODO: Should we use ContextClassLoader here ??? Or perhaps try that on failure??
                final java.lang.Class clazz = java.lang.Class.forName(role);
                factory = new org.apache.myrmidon.components.type.MultiSourceTypeFactory(clazz);
            } catch (final java.lang.Exception e) {
                throw new org.apache.myrmidon.components.type.TypeException((("Role '" + role) + "' does not specify ") + "accessible work interface");
            }
        }
        m_roleMap.put(role, factory);
        return factory;
    }

    private org.apache.myrmidon.components.type.MultiSourceTypeFactory getParentTypedFactory(final java.lang.String role) {
        if (null != m_parent) {
            return m_parent.lookupFactory(role);
        } else {
            return null;
        }
    }
}