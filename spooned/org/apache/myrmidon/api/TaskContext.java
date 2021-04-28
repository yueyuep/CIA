/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.api;
import org.apache.avalon.framework.Enum;
import org.apache.avalon.framework.context.Context;
/**
 * This interface represents the <em>Context</em> in which Task is executed.
 * Like other Component APIs the TaskContext represents the communication
 * path between the container and the Task.
 * Unlike other APIs the Logging is provided through another interface (Loggable)
 * as is access to Peer components (via Composable).
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface TaskContext extends org.apache.avalon.framework.context.Context {
    // these values are used when setting properties to indicate the scope at
    // which properties are set
    org.apache.myrmidon.api.TaskContext.ScopeEnum CURRENT = new org.apache.myrmidon.api.TaskContext.ScopeEnum("Current");

    org.apache.myrmidon.api.TaskContext.ScopeEnum PARENT = new org.apache.myrmidon.api.TaskContext.ScopeEnum("Parent");

    org.apache.myrmidon.api.TaskContext.ScopeEnum TOP_LEVEL = new org.apache.myrmidon.api.TaskContext.ScopeEnum("TopLevel");

    // these are the names of properties that every TaskContext must contain
    java.lang.String JAVA_VERSION = "myrmidon.java.version";

    java.lang.String BASE_DIRECTORY = "myrmidon.base.directory";

    java.lang.String NAME = "myrmidon.task.name";

    /**
     * Retrieve JavaVersion running under.
     *
     * @return the version of JVM
     */
    org.apache.myrmidon.api.JavaVersion getJavaVersion();

    /**
     * Retrieve Name of tasklet.
     *
     * @return the name
     */
    java.lang.String getName();

    /**
     * Retrieve base directory.
     *
     * @return the base directory
     */
    java.io.File getBaseDirectory();

    /**
     * Resolve filename.
     * This involves resolving it against baseDirectory and
     * removing ../ and ./ references. It also means formatting
     * it appropriately for the particular OS (ie different OS have
     * different volumes, file conventions etc)
     *
     * @param filename
     * 		the filename to resolve
     * @return the resolved file
     */
    java.io.File resolveFile(java.lang.String filename) throws org.apache.myrmidon.api.TaskException;

    /**
     * Retrieve property for name.
     *
     * @param name
     * 		the name of property
     * @return the value of property
     */
    java.lang.Object getProperty(java.lang.String name);

    /**
     * Set property value in current context.
     *
     * @param name
     * 		the name of property
     * @param value
     * 		the value of property
     */
    void setProperty(java.lang.String name, java.lang.Object value) throws org.apache.myrmidon.api.TaskException;

    /**
     * Set property value.
     *
     * @param name
     * 		the name of property
     * @param value
     * 		the value of property
     * @param scope
     * 		the scope at which to set property
     */
    void setProperty(java.lang.String name, java.lang.Object value, org.apache.myrmidon.api.TaskContext.ScopeEnum scope) throws org.apache.myrmidon.api.TaskException;

    /**
     * Create a Child Context.
     * This allows separate hierarchly contexts to be easily constructed.
     *
     * @param name
     * 		the name of sub-context
     * @return the created TaskContext
     * @exception TaskException
     * 		if an error occurs
     */
    // TaskContext createSubContext( String name )
    // throws TaskException;
    /**
     * Safe wrapper class for Scope enums.
     */
    final class ScopeEnum extends java.lang.Enum {
        ScopeEnum(final java.lang.String name) {
            super(name);
        }
    }
}