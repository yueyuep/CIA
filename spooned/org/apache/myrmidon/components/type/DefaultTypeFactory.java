/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.type;
/**
 * Create a type instance based on name.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 * @version CVS $Revision$ $Date$
 */
public class DefaultTypeFactory implements org.apache.myrmidon.components.type.TypeFactory {
    // /A Map of shortnames to classnames
    private final java.util.HashMap m_classNames = new java.util.HashMap();

    // /A list of URLs from which classLoader is constructed
    private final java.net.URL[] m_urls;

    // /The parent classLoader (if any)
    private final java.lang.ClassLoader m_parent;

    // /The parent classLoader (if any)
    private java.lang.ClassLoader m_classLoader;

    public DefaultTypeFactory(final java.net.URL url) {
        this(new java.net.URL[]{ url });
    }

    public DefaultTypeFactory(final java.net.URL[] urls) {
        this(urls, java.lang.Thread.currentThread().getContextClassLoader());
    }

    public DefaultTypeFactory(final java.net.URL[] urls, final java.lang.ClassLoader parent) {
        m_urls = urls;
        m_parent = parent;
    }

    public DefaultTypeFactory(final java.lang.ClassLoader classLoader) {
        this(null, null);
        m_classLoader = classLoader;
    }

    public void addNameClassMapping(final java.lang.String name, final java.lang.String className) {
        m_classNames.put(name, className);
    }

    /**
     * Create a type instance with appropriate name.
     *
     * @param name
     * 		the name
     * @return the created instance
     * @exception TypeException
     * 		if an error occurs
     */
    public java.lang.Object create(final java.lang.String name) throws org.apache.myrmidon.components.type.TypeException {
        final java.lang.String className = getClassName(name);
        try {
            return getClassLoader().loadClass(className).newInstance();
        } catch (final java.lang.Exception e) {
            throw new org.apache.myrmidon.components.type.TypeException(("Unable to instantiate '" + name) + "'", e);
        }
    }

    private java.lang.String getClassName(final java.lang.String name) throws org.apache.myrmidon.components.type.TypeException {
        final java.lang.String className = ((java.lang.String) (m_classNames.get(name)));
        if (null == className) {
            throw new org.apache.myrmidon.components.type.TypeException(("Malconfigured factory, no clasname for '" + name) + "'");
        }
        return className;
    }

    private java.lang.ClassLoader getClassLoader() {
        if (null == m_classLoader) {
            m_classLoader = new java.net.URLClassLoader(m_urls, m_parent);
        }
        return m_classLoader;
    }
}