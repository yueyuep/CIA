/* The Apache Software License, Version 1.1

Copyright (c) 2001 The Apache Software Foundation.  All rights
reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in
   the documentation and/or other materials provided with the
   distribution.

3. The end-user documentation included with the redistribution, if
   any, must include the following acknowlegement:
      "This product includes software developed by the
       Apache Software Foundation (http://www.apache.org/)."
   Alternately, this acknowlegement may appear in the software itself,
   if and wherever such third-party acknowlegements normally appear.

4. The names "The Jakarta Project", "Ant", and "Apache Software
   Foundation" must not be used to endorse or promote products derived
   from this software without prior written permission. For written
   permission, please contact apache@apache.org.

5. Products derived from this software may not be called "Apache"
   nor may "Apache" appear in their names without prior written
   permission of the Apache Group.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
====================================================================

This software consists of voluntary contributions made by many
individuals on behalf of the Apache Software Foundation.  For more
information on the Apache Software Foundation, please see
<http://www.apache.org/>.
 */
package org.apache.ant.core.model;
import java.util.*;
import org.apache.ant.core.support.*;
/**
 * A project is a collection of targets and global tasks. A project
 * may reference objects in other projects using named references of
 * the form project:object
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 */
public class Project extends BuildElement {
    /**
     * The default target in this project.
     */
    private java.lang.String defaultTarget = null;

    /**
     * The base URL of this project. Relative locations are relative to this base.
     */
    private java.lang.String base;

    /**
     * The name of this project when referenced by a script within this project.
     */
    private java.lang.String name;

    /**
     * These are the targets which belong to the project. They
     * will have interdependencies which are used to determine
     * which targets need to be executed before a given target.
     */
    private java.util.Map targets = new java.util.HashMap();

    /**
     * The global tasks for this project. These are the tasks that will get executed
     * whenever an execution context is associated with this project.
     */
    private java.util.List tasks = new java.util.ArrayList();

    /**
     * The projects imported into this project. Each imported project is
     * given a name which is used to identify access to that project's
     * elements.
     */
    private java.util.Map importedProjects = new java.util.HashMap();

    /**
     * The URL where the project is defined.
     */
    private java.net.URL sourceURL;

    /**
     * Create a Project
     *
     * @param sourceURL
     * 		the URL where the project is defined.
     * @param location
     * 		the location of this element within the source.
     */
    public Project(java.net.URL sourceURL, Location location) {
        super(location);
        this.sourceURL = sourceURL;
    }

    /**
     * Get the URL where this project is defined
     *
     * @return the project source URL
     */
    public java.net.URL getSourceURL() {
        return sourceURL;
    }

    /**
     * Add a target to the project.
     *
     * @param target
     * 		the Target to be added
     * @throws ProjectModelException
     * 		if a target with the same name already exists.
     */
    public void addTarget(Target target) throws org.apache.ant.core.model.ProjectModelException {
        if (targets.containsKey(target.getName())) {
            throw new ProjectModelException(("A target with name '" + target.getName()) + "' has already been defined in this project", target.getLocation());
        }
        targets.put(target.getName(), target);
    }

    /**
     * Set the defautl target of this project.
     *
     * @param defaultTarget
     * 		the name of the defaultTarget of this project.
     */
    public void setDefaultTarget(java.lang.String defaultTarget) {
        this.defaultTarget = defaultTarget;
    }

    /**
     * Get the Project's default Target, if any
     *
     * @return the project's defautl target or null if there is no default.
     */
    public java.lang.String getDefaultTarget() {
        return defaultTarget;
    }

    /**
     * Set the base URL for this project.
     *
     * @param base
     * 		the baseURL for this project.
     */
    public void setBase(java.lang.String base) {
        this.base = base;
    }

    /**
     * Get the base URL for this project.
     *
     * @return the baseURL for this project as a string.
     */
    public java.lang.String getBase() {
        return base;
    }

    /**
     * Set the name of this project.
     *
     * @param name
     * 		the name for this project.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * Add a task to the list of global tasks for this project.
     *
     * @param task
     * 		a task to be executed when an execution context
     * 		is associated with the Project (a non-target task)
     */
    public void addTask(TaskElement task) {
        tasks.add(task);
    }

    /**
     * Import a project to be referenced using the given name.
     *
     * @param importName
     * 		the name under which the project will be referenced.
     * @param project
     * 		the imported project.
     * @throws ProjectModelException
     * 		if an existing project has already
     * 		been imported with that name.
     */
    public void importProject(java.lang.String importName, org.apache.ant.core.model.Project project) throws org.apache.ant.core.model.ProjectModelException {
        if (importedProjects.containsKey(importName)) {
            throw new ProjectModelException(("A project has already been imported with name '" + importName) + "'");
        }
        importedProjects.put(importName, project);
    }

    /**
     * Get the targets in this project.
     *
     * @return an iterator returning Target objects.
     */
    public java.util.Iterator getTargets() {
        return targets.values().iterator();
    }

    /**
     * Get the target with the given name
     *
     * @param targetName
     * 		the name of the desired target.
     * @return the target with the given name or null if there is no
    such target.
     */
    public org.apache.ant.core.model.Target getTarget(java.lang.String targetName) {
        return ((Target) (targets.get(targetName)));
    }

    /**
     * Get the name sof the imported projects.
     *
     * @return an iterator which returns the name sof the imported projects.
     */
    public java.util.Iterator getImportedProjectNames() {
        return importedProjects.keySet().iterator();
    }

    /**
     * Get an imported project by name
     *
     * @param importName
     * 		the name under which the project was imported.
     * @return the project asscociated with the given import name or null
    if there is no such project.
     */
    public org.apache.ant.core.model.Project getImportedProject(java.lang.String importName) {
        return ((org.apache.ant.core.model.Project) (importedProjects.get(importName)));
    }

    /**
     * Get the initialisation tasks for this project
     *
     * @return an iterator over the set of tasks for this project.
     */
    public java.util.Iterator getTasks() {
        return tasks.iterator();
    }
}