/* The Apache Software License, Version 1.1

Copyright (c) 2002 The Apache Software Foundation.  All rights
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
package org.apache.ant.common.model;
import org.apache.ant.common.util.CircularDependencyChecker;
import org.apache.ant.common.util.CircularDependencyException;
import org.apache.ant.common.util.Location;
/**
 * A project is a collection of targets and global tasks. A project may
 * reference objects in other projects using named references of the form
 * refname:object
 *
 * @author Conor MacNeill
 * @created 9 January 2002
 */
public class Project extends org.apache.ant.common.model.ModelElement {
    /**
     * The delimiter used to separate reference names in target names, data
     * values, etc
     */
    public static final java.lang.String REF_DELIMITER = ":";

    /**
     * The default target in this project.
     */
    private java.lang.String defaultTarget = null;

    /**
     * The base URL of this project. Relative locations are relative to this
     * base.
     */
    private java.lang.String base;

    /**
     * The name of this project when referenced by a script within this
     * project.
     */
    private java.lang.String name;

    /**
     * These are the targets which belong to the project. They will have
     * interdependencies which are used to determine which targets need to be
     * executed before a given target.
     */
    private java.util.Map targets = new java.util.HashMap();

    /**
     * The global tasks for this project. These are the tasks that will get
     * executed whenever an execution context is associated with this project.
     */
    private java.util.List tasks = new java.util.ArrayList();

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
    public Project(java.net.URL sourceURL, org.apache.ant.common.util.Location location) {
        super(location);
        this.sourceURL = sourceURL;
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
     * Set the base URL for this project.
     *
     * @param base
     * 		the baseURL for this project.
     */
    public void setBase(java.lang.String base) {
        this.base = base;
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
     * Get the URL where this project is defined
     *
     * @return the project source URL
     */
    public java.net.URL getSourceURL() {
        return sourceURL;
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
     * Get the base URL for this project.
     *
     * @return the baseURL for this project as a string.
     */
    public java.lang.String getBase() {
        return base;
    }

    /**
     * Get the name of the project element
     *
     * @return the project's name
     */
    public java.lang.String getName() {
        return name;
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
     * @return the target with the given name or null if there is no such
    target.
     */
    public org.apache.ant.common.model.Target getTarget(java.lang.String targetName) {
        return ((org.apache.ant.common.model.Target) (targets.get(targetName)));
    }

    /**
     * Get the initialisation tasks for this project
     *
     * @return an iterator over the set of tasks for this project.
     */
    public java.util.Iterator getTasks() {
        return tasks.iterator();
    }

    /**
     * Add a target to the project.
     *
     * @param target
     * 		the Target to be added
     * @throws ModelException
     * 		if a target with the same name already exists.
     */
    public void addTarget(org.apache.ant.common.model.Target target) throws org.apache.ant.common.model.ModelException {
        if (targets.containsKey(target.getName())) {
            throw new org.apache.ant.common.model.ModelException(("A target with name '" + target.getName()) + "' has already been defined in this project", target.getLocation());
        }
        targets.put(target.getName(), target);
    }

    /**
     * Add a task to the list of global tasks for this project.
     *
     * @param task
     * 		a task to be executed when an execution context is
     * 		associated with the Project (a non-target task)
     */
    public void addTask(org.apache.ant.common.model.BuildElement task) {
        tasks.add(task);
    }

    /**
     * Validate this project
     *
     * @exception ModelException
     * 		if the project is not valid
     */
    public void validate() throws org.apache.ant.common.model.ModelException {
        // check whether all of dependencies for our targets
        // exist in the model
        // visited contains the targets we have already visited and verified
        java.util.Set visited = new java.util.HashSet();
        // checker records the targets we are currently visiting
        org.apache.ant.common.util.CircularDependencyChecker checker = new org.apache.ant.common.util.CircularDependencyChecker("checking target dependencies");
        // dependency order is purely recorded for debug purposes
        java.util.List dependencyOrder = new java.util.ArrayList();
        for (java.util.Iterator i = getTargets(); i.hasNext();) {
            org.apache.ant.common.model.Target target = ((org.apache.ant.common.model.Target) (i.next()));
            target.validate();
            fillinDependencyOrder(target, dependencyOrder, visited, checker);
        }
    }

    /**
     * Determine target dependency order within this project.
     *
     * @param target
     * 		The target being examined
     * @param dependencyOrder
     * 		The dependency order of targets
     * @param visited
     * 		Set of targets in this project already visited.
     * @param checker
     * 		A circular dependency checker
     * @exception ModelException
     * 		if the dependencies of the project's targets
     * 		are not valid.
     */
    public void fillinDependencyOrder(org.apache.ant.common.model.Target target, java.util.List dependencyOrder, java.util.Set visited, org.apache.ant.common.util.CircularDependencyChecker checker) throws org.apache.ant.common.model.ModelException {
        if (visited.contains(target.getName())) {
            return;
        }
        try {
            java.lang.String targetName = target.getName();
            checker.visitNode(targetName);
            for (java.util.Iterator i = target.getDependencies(); i.hasNext();) {
                java.lang.String dependency = ((java.lang.String) (i.next()));
                boolean localTarget = dependency.indexOf(org.apache.ant.common.model.Project.REF_DELIMITER) == (-1);
                if (localTarget) {
                    org.apache.ant.common.model.Target dependencyTarget = getTarget(dependency);
                    if (dependencyTarget == null) {
                        java.lang.StringBuffer sb = new java.lang.StringBuffer("Target '");
                        sb.append(dependency);
                        sb.append("' does not exist in this project. ");
                        throw new org.apache.ant.common.model.ModelException(new java.lang.String(sb), target.getLocation());
                    }
                    // need to check the targets we depend on
                    fillinDependencyOrder(dependencyTarget, dependencyOrder, visited, checker);
                }
            }
            visited.add(targetName);
            checker.leaveNode(targetName);
            dependencyOrder.add(targetName);
        } catch (org.apache.ant.common.util.CircularDependencyException e) {
            throw new org.apache.ant.common.model.ModelException(e.getMessage(), target.getLocation());
        }
    }
}