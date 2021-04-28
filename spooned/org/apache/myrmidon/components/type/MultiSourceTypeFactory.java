/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.type;
/**
 * This factory acts as a proxy to set of object factorys.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class MultiSourceTypeFactory implements org.apache.myrmidon.components.type.TypeFactory {
    // /Parent Selector
    private final org.apache.myrmidon.components.type.MultiSourceTypeFactory m_parent;

    // /Map of name->factory list
    private final java.util.HashMap m_factorys = new java.util.HashMap();

    // /Type expected to be created from factorys
    private final java.lang.Class m_type;

    public MultiSourceTypeFactory(final java.lang.Class type) {
        m_type = type;
        m_parent = null;
    }

    public MultiSourceTypeFactory(final org.apache.myrmidon.components.type.MultiSourceTypeFactory parent) {
        m_type = parent.getType();
        m_parent = parent;
    }

    /**
     * Populate the ComponentSelector.
     */
    public void register(final java.lang.String name, final org.apache.myrmidon.components.type.TypeFactory factory) {
        m_factorys.put(name, factory);
    }

    /**
     * Create a type instance based on name.
     *
     * @param name
     * 		the name
     * @return the type instance
     * @exception TypeException
     * 		if an error occurs
     */
    public java.lang.Object create(final java.lang.String name) throws org.apache.myrmidon.components.type.TypeException {
        org.apache.myrmidon.components.type.TypeFactory factory = getTypeFactory(name);
        if ((null == factory) && (null != m_parent)) {
            factory = m_parent.getTypeFactory(name);
        }
        if (null == factory) {
            throw new org.apache.myrmidon.components.type.TypeException(("Failed to locate factory for '" + name) + "'");
        } else {
            final java.lang.Object object = factory.create(name);
            if (!m_type.isInstance(object)) {
                throw new org.apache.myrmidon.components.type.TypeException((((("Object '" + name) + "' is not of ") + "correct Type (") + m_type.getName()) + ")");
            }
            return object;
        }
    }

    /**
     * Retrieve type managed by selector.
     * Used by other instances of TypedComponentSelector.
     *
     * @return the type class
     */
    protected final java.lang.Class getType() {
        return m_type;
    }

    protected final org.apache.myrmidon.components.type.TypeFactory getTypeFactory(final java.lang.String name) {
        return ((org.apache.myrmidon.components.type.TypeFactory) (m_factorys.get(name)));
    }
}