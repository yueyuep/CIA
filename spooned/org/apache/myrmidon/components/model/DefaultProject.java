/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.model;
/**
 * Default project implementation.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultProject implements org.apache.myrmidon.components.model.Project {
    // /The imports
    private final java.util.ArrayList m_imports = new java.util.ArrayList();

    // /The projects refferred to by this project
    private final java.util.HashMap m_projects = new java.util.HashMap();

    // /The targets contained by this project
    private final java.util.HashMap m_targets = new java.util.HashMap();

    // /The implicit target (not present in m_targets)
    private org.apache.myrmidon.components.model.Target m_implicitTarget;

    // /The name of the default target
    private java.lang.String m_defaultTarget;

    // /The base directory of project
    private java.io.File m_baseDirectory;

    /**
     * Get the imports for project.
     *
     * @return the imports
     */
    public org.apache.myrmidon.components.model.TypeLib[] getTypeLibs() {
        return ((org.apache.myrmidon.components.model.TypeLib[]) (m_imports.toArray(new org.apache.myrmidon.components.model.TypeLib[0])));
    }

    /**
     * Get names of projects referred to by this project.
     *
     * @return the names
     */
    public java.lang.String[] getProjectNames() {
        return ((java.lang.String[]) (m_projects.keySet().toArray(new java.lang.String[0])));
    }

    /**
     * Retrieve project reffered to by this project.
     *
     * @param name
     * 		the project name
     * @return the Project or null if none by that name
     */
    public org.apache.myrmidon.components.model.Project getProject(final java.lang.String name) {
        return ((org.apache.myrmidon.components.model.Project) (m_projects.get(name)));
    }

    /**
     * Retrieve base directory of project.
     *
     * @return the projects base directory
     */
    public final java.io.File getBaseDirectory() {
        return m_baseDirectory;
    }

    /**
     * Retrieve implicit target.
     * The implicit target contains all the top level tasks.
     *
     * @return the Target
     */
    public final org.apache.myrmidon.components.model.Target getImplicitTarget() {
        return m_implicitTarget;
    }

    /**
     * Set ImplicitTarget.
     *
     * @param target
     * 		the implicit target
     */
    public final void setImplicitTarget(final org.apache.myrmidon.components.model.Target target) {
        m_implicitTarget = target;
    }

    /**
     * Retrieve a target by name.
     *
     * @param name
     * 		the name of target
     * @return the Target or null if no target exists with name
     */
    public final org.apache.myrmidon.components.model.Target getTarget(final java.lang.String targetName) {
        return ((org.apache.myrmidon.components.model.Target) (m_targets.get(targetName)));
    }

    /**
     * Get name of default target.
     *
     * @return the default target name
     */
    public final java.lang.String getDefaultTargetName() {
        return m_defaultTarget;
    }

    /**
     * Retrieve names of all targets in project.
     *
     * @return an array target names
     */
    public final java.lang.String[] getTargetNames() {
        return ((java.lang.String[]) (m_targets.keySet().toArray(new java.lang.String[0])));
    }

    /**
     * Set DefaultTargetName.
     *
     * @param defaultTarget
     * 		the default target name
     */
    public final void setDefaultTargetName(final java.lang.String defaultTarget) {
        m_defaultTarget = defaultTarget;
    }

    /**
     * Retrieve base directory of project.
     *
     * @return the projects base directory
     */
    public final void setBaseDirectory(final java.io.File baseDirectory) {
        m_baseDirectory = baseDirectory;
    }

    public final void addTypeLib(final org.apache.myrmidon.components.model.TypeLib typeLib) {
        m_imports.add(typeLib);
    }

    /**
     * Add a target.
     *
     * @param name
     * 		the name of target
     * @param target
     * 		the Target
     * @exception IllegalArgumentException
     * 		if target already exists with same name
     */
    public final void addTarget(final java.lang.String name, final org.apache.myrmidon.components.model.Target target) {
        if (null != m_targets.get(name)) {
            throw new java.lang.IllegalArgumentException(("Can not have two targets in a " + "file with the name ") + name);
        } else {
            m_targets.put(name, target);
        }
    }

    /**
     * Add a project reference.
     *
     * @param name
     * 		the name of target
     * @param project
     * 		the Project
     * @exception IllegalArgumentException
     * 		if project already exists with same name
     */
    public final void addProject(final java.lang.String name, final org.apache.myrmidon.components.model.Project project) {
        if (null != m_projects.get(name)) {
            throw new java.lang.IllegalArgumentException(("Can not have two projects referenced in a " + "file with the name ") + name);
        } else {
            m_projects.put(name, project);
        }
    }
}