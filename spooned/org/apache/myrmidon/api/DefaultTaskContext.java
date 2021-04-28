/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.api;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.excalibur.property.PropertyException;
import org.apache.avalon.excalibur.property.PropertyUtil;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
/**
 * Default implementation of TaskContext.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultTaskContext extends org.apache.avalon.framework.context.DefaultContext implements org.apache.myrmidon.api.TaskContext {
    /**
     * Constructor for Context with no parent contexts.
     */
    public DefaultTaskContext(final java.util.Map contextData) {
        super(contextData);
    }

    /**
     * Constructor for Context with no parent contexts.
     */
    public DefaultTaskContext() {
        this(((org.apache.myrmidon.api.TaskContext) (null)));
    }

    /**
     * Constructor.
     */
    public DefaultTaskContext(final org.apache.myrmidon.api.TaskContext parent) {
        super(parent);
    }

    /**
     * Retrieve JavaVersion running under.
     *
     * @return the version of JVM
     */
    public org.apache.myrmidon.api.JavaVersion getJavaVersion() {
        try {
            return ((org.apache.myrmidon.api.JavaVersion) (get(org.apache.myrmidon.api.JAVA_VERSION)));
        } catch (final org.apache.avalon.framework.context.ContextException ce) {
            throw new java.lang.IllegalStateException("No JavaVersion in Context");
        }
    }

    /**
     * Retrieve Name of tasklet.
     *
     * @return the name
     */
    public java.lang.String getName() {
        try {
            return ((java.lang.String) (get(org.apache.myrmidon.api.NAME)));
        } catch (final org.apache.avalon.framework.context.ContextException ce) {
            throw new java.lang.IllegalStateException("No Name in Context");
        }
    }

    /**
     * Retrieve base directory.
     *
     * @return the base directory
     */
    public java.io.File getBaseDirectory() {
        try {
            return ((java.io.File) (get(org.apache.myrmidon.api.BASE_DIRECTORY)));
        } catch (final org.apache.avalon.framework.context.ContextException ce) {
            throw new java.lang.IllegalStateException("No Base Directory in Context");
        }
    }

    /**
     * Resolve filename.
     * This involves resolving it against baseDirectory and
     * removing ../ and ./ references. It also means formatting
     * it appropriately for the particular OS (ie different OS have
     * different volumes, file conventions etc)
     *
     * @param filename
     * 		the filename to resolve
     * @return the resolved filename
     */
    public java.io.File resolveFile(final java.lang.String filename) {
        return org.apache.avalon.excalibur.io.FileUtil.resolveFile(getBaseDirectory(), filename);
    }

    /**
     * Retrieve property for name.
     *
     * @param name
     * 		the name of property
     * @return the value of the property
     */
    public java.lang.Object getProperty(final java.lang.String name) {
        try {
            return get(name);
        } catch (final org.apache.avalon.framework.context.ContextException ce) {
            return null;
        }
    }

    /**
     * Set property value in current context.
     *
     * @param name
     * 		the name of property
     * @param value
     * 		the value of property
     */
    public void setProperty(final java.lang.String name, final java.lang.Object value) throws org.apache.myrmidon.api.TaskException {
        setProperty(name, value, org.apache.myrmidon.api.CURRENT);
    }

    /**
     * Set property value.
     *
     * @param property
     * 		the property
     */
    public void setProperty(final java.lang.String name, final java.lang.Object value, final org.apache.myrmidon.api.ScopeEnum scope) throws org.apache.myrmidon.api.TaskException {
        checkPropertyValid(name, value);
        if (CURRENT == scope)
            put(name, value);
        else if (PARENT == scope) {
            if (null == getParent()) {
                throw new org.apache.myrmidon.api.TaskException("Can't set a property with parent scope when context " + " has no parent");
            } else {
                ((org.apache.myrmidon.api.TaskContext) (getParent())).setProperty(name, value);
            }
        } else if (TOP_LEVEL == scope) {
            org.apache.myrmidon.api.DefaultTaskContext context = this;
            while (null != context.getParent()) {
                context = ((org.apache.myrmidon.api.DefaultTaskContext) (context.getParent()));
            } 
            context.put(name, value);
        } else {
            throw new java.lang.IllegalStateException(("Unknown property scope! (" + scope) + ")");
        }
    }

    /**
     * Make sure property is valid if it is one of the "magic" properties.
     *
     * @param name
     * 		the name of property
     * @param value
     * 		the value of proeprty
     * @exception TaskException
     * 		if an error occurs
     */
    protected void checkPropertyValid(final java.lang.String name, final java.lang.Object value) throws org.apache.myrmidon.api.TaskException {
        if (org.apache.myrmidon.api.BASE_DIRECTORY.equals(name) && (!(value instanceof java.io.File))) {
            throw new org.apache.myrmidon.api.TaskException((("Property " + BASE_DIRECTORY) + " must have a value of type ") + java.io.File.class.getName());
        } else if (org.apache.myrmidon.api.NAME.equals(name) && (!(value instanceof java.lang.String))) {
            throw new org.apache.myrmidon.api.TaskException((("Property " + NAME) + " must have a value of type ") + java.lang.String.class.getName());
        } else if (org.apache.myrmidon.api.JAVA_VERSION.equals(name) && (!(value instanceof org.apache.myrmidon.api.JavaVersion))) {
            throw new org.apache.myrmidon.api.TaskException((("Property " + JAVA_VERSION) + " must have a value of type ") + org.apache.myrmidon.api.JavaVersion.class.getName());
        }
    }
}