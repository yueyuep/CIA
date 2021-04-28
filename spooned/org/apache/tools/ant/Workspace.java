/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant;
import java.util.*;
/**
 * The main class in the Ant class hierarchy. A workspace contains
 *  multiple projects, which in turn contain multiple targets, which
 *  in turn contain multiple task proxies. The workspace also handles
 *  the sorting and execution of targets during a build.
 *
 * @author <a href="mailto:mpfoemme@thoughtworks.com">Matthew Foemmel</a>
 */
public class Workspace {
    public static final char SCOPE_SEPARATOR = ':';

    private org.apache.tools.ant.Importer importer;

    private java.util.Map projects;

    private java.util.Map tasks;

    private java.util.List listeners;

    private org.apache.tools.ant.Task currentTask = null;

    /**
     * Constructs new Ant workspace with no projects. The only
     *  task that will be registered is the "load" task.
     *
     *  The importer is used to handle the actual reading of build files.
     *  In theory, different importers could be used to read project info from
     *  DOM trees, serialized objects, databases, etc.
     */
    public Workspace(Importer importer) {
        this.importer = importer;
        this.projects = new java.util.HashMap();
        this.tasks = new java.util.HashMap();
        this.listeners = new java.util.ArrayList();
        registerTask("load", org.apache.tools.ant.Load.class);
    }

    /**
     * Assigns a task class to a name.
     */
    public void registerTask(java.lang.String name, java.lang.Class type) {
        tasks.put(name, type);
    }

    /**
     * Returns the class for a task with the specified name.
     */
    public java.lang.Class getTaskClass(java.lang.String name) throws org.apache.tools.ant.BuildException {
        java.lang.Class type = ((java.lang.Class) (tasks.get(name)));
        if (type == null) {
            throw new BuildException(("No task named \"" + name) + "\" has been loaded");
        }
        return type;
    }

    /**
     * Creates a project with the specified name. The project initially
     *  contains no targets.
     */
    public org.apache.tools.ant.Project createProject(java.lang.String name) {
        Project project = new Project(this, name);
        projects.put(name, project);
        return project;
    }

    /**
     * Returns the project with the specified name, or throws
     *  an exception if no project exists with that name.
     */
    public org.apache.tools.ant.Project getProject(java.lang.String name) throws org.apache.tools.ant.BuildException {
        Project project = ((Project) (projects.get(name)));
        if (project == null) {
            throw new BuildException(("Project \"" + name) + "\" not found");
        }
        return project;
    }

    /**
     * Builds all of the targets in the list. Target names must
     *  be of the form projectname:targetname.
     */
    public boolean build(java.util.List fullNames) throws org.apache.tools.ant.BuildException {
        // This lets the tasks intercept System.exit() calls
        java.lang.SecurityManager sm = java.lang.System.getSecurityManager();
        java.lang.System.setSecurityManager(new AntSecurityManager());
        fireBuildStarted();
        try {
            // Parse the project files...
            importTargets(fullNames);
            // ...figure out the build order...
            java.util.List toDoList = sortTargets(fullNames);
            // ...and build the targets
            java.util.Iterator itr = toDoList.iterator();
            while (itr.hasNext()) {
                Target target = ((Target) (itr.next()));
                buildTarget(target);
            } 
            fireBuildFinished(null);
            return true;
        } catch (BuildException exc) {
            fireBuildFinished(exc);
            return false;
        } finally {
            java.lang.System.setSecurityManager(sm);
        }
    }

    /**
     * Adds a listener to the workspace.
     */
    public void addBuildListener(BuildListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener to the workspace.
     */
    public void removeBuildListener(BuildListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fires a messageLogged event with DEBUG priority
     */
    public void debug(java.lang.String message) {
        fireMessageLogged(message, BuildEvent.DEBUG);
    }

    /**
     * Fires a messageLogged event with INFO priority
     */
    public void info(java.lang.String message) {
        fireMessageLogged(message, BuildEvent.INFO);
    }

    /**
     * Fires a messageLogged event with WARN priority
     */
    public void warn(java.lang.String message) {
        fireMessageLogged(message, BuildEvent.WARN);
    }

    /**
     * Fires a messageLogged event with ERROR priority
     */
    public void error(java.lang.String message) {
        fireMessageLogged(message, BuildEvent.ERROR);
    }

    /**
     * Imports into the workspace all of the projects required to
     *  build a set of targets.
     */
    private void importTargets(java.util.List fullNames) throws org.apache.tools.ant.BuildException {
        java.util.Iterator itr = fullNames.iterator();
        while (itr.hasNext()) {
            java.lang.String fullName = ((java.lang.String) (itr.next()));
            java.lang.String projectName = org.apache.tools.ant.Workspace.getProjectName(fullName);
            importProject(projectName);
        } 
    }

    /**
     * Imports the project into the workspace, as well as any others
     *  that the project depends on.
     */
    public org.apache.tools.ant.Project importProject(java.lang.String projectName) throws org.apache.tools.ant.BuildException {
        Project project = ((Project) (projects.get(projectName)));
        // Don't parse a project file more than once
        if (project == null) {
            // Parse the project file
            project = createProject(projectName);
            fireImportStarted(project);
            try {
                importer.importProject(project);
                fireImportFinished(project, null);
            } catch (BuildException exc) {
                fireImportFinished(project, exc);
                throw exc;
            }
            // Parse any imported projects as well
            java.util.Iterator itr = project.getImports().iterator();
            while (itr.hasNext()) {
                Import imp = ((Import) (itr.next()));
                importProject(imp.getName());
            } 
        }
        return project;
    }

    /**
     * Builds a specific target. This assumes that the targets it depends
     *  on have already been built.
     */
    private void buildTarget(Target target) throws org.apache.tools.ant.BuildException {
        fireTargetStarted(target);
        try {
            java.util.List tasks = target.getTasks();
            java.util.Iterator itr = tasks.iterator();
            while (itr.hasNext()) {
                TaskProxy proxy = ((TaskProxy) (itr.next()));
                executeTask(target, proxy);
            } 
            fireTargetFinished(target, null);
        } catch (BuildException exc) {
            fireTargetFinished(target, null);
            throw exc;
        }
    }

    /**
     * Instantiates the task from the proxy and executes.
     */
    private void executeTask(Target target, TaskProxy proxy) throws org.apache.tools.ant.BuildException {
        Task task = proxy.createTask();
        task.setWorkspace(this);
        task.setProject(target.getProject());
        task.setTarget(target);
        fireTaskStarted(task);
        currentTask = task;
        try {
            task.execute();
            fireTaskFinished(task, null);
        } catch (BuildException exc) {
            exc.setLocation(proxy.getLocation());
            fireTaskFinished(task, exc);
            throw exc;
        } finally {
            currentTask = null;
        }
    }

    /**
     * Does a topological sort on a list of target names. Returns
     *  a list of Target objects in the order to be executed.
     */
    private java.util.List sortTargets(java.util.List fullNames) throws org.apache.tools.ant.BuildException {
        java.util.List results = new java.util.ArrayList();
        sortTargets(results, new java.util.Stack(), fullNames);
        return results;
    }

    private void sortTargets(java.util.List results, java.util.Stack visited, java.util.List fullNames) throws org.apache.tools.ant.BuildException {
        java.util.Iterator itr = fullNames.iterator();
        while (itr.hasNext()) {
            java.lang.String fullName = ((java.lang.String) (itr.next()));
            // Check for cycles
            if (visited.contains(fullName)) {
                throwCyclicDependency(visited, fullName);
            }
            // Check if we're already added this target to the list
            Target target = getTarget(fullName);
            if (results.contains(target)) {
                continue;
            }
            visited.push(fullName);
            sortTargets(results, visited, target.getDepends());
            results.add(target);
            visited.pop();
        } 
    }

    /**
     * Creates and throws an exception indicating a cyclic dependency.
     */
    private void throwCyclicDependency(java.util.Stack visited, java.lang.String fullName) throws org.apache.tools.ant.BuildException {
        java.lang.StringBuffer msg = new java.lang.StringBuffer("Cyclic dependency: ");
        for (int i = 0; i < visited.size(); i++) {
            msg.append(((java.lang.String) (visited.get(i))));
            msg.append(" -> ");
        }
        msg.append(fullName);
        throw new BuildException(msg.toString());
    }

    /**
     * Parses the full target name into is project and target components,
     *  then locates the Target object.
     */
    private org.apache.tools.ant.Target getTarget(java.lang.String fullName) throws org.apache.tools.ant.BuildException {
        java.lang.String projectName = org.apache.tools.ant.Workspace.getProjectName(fullName);
        java.lang.String targetName = org.apache.tools.ant.Workspace.getTargetName(fullName);
        Project project = ((Project) (projects.get(projectName)));
        if (project == null) {
            throw new BuildException(("Project \"" + projectName) + "\" not found");
        }
        Target target = project.getTarget(targetName);
        if (target == null) {
            throw new BuildException(("Target \"" + fullName) + "\" not found");
        }
        return target;
    }

    /**
     * Returns the project portion of a full target name.
     */
    public static java.lang.String getProjectName(java.lang.String fullName) throws org.apache.tools.ant.BuildException {
        int pos = fullName.indexOf(org.apache.tools.ant.Workspace.SCOPE_SEPARATOR);
        if ((pos == (-1)) || (pos == 0)) {
            throw new BuildException(("\"" + fullName) + "\" is not a valid target name");
        }
        return fullName.substring(0, pos);
    }

    /**
     * Returns the target portion of a full target name.
     */
    public static java.lang.String getTargetName(java.lang.String fullName) throws org.apache.tools.ant.BuildException {
        int pos = fullName.indexOf(org.apache.tools.ant.Workspace.SCOPE_SEPARATOR);
        if ((pos == (-1)) || (pos == 0)) {
            throw new BuildException(("\"" + fullName) + "\" is not a valid target name");
        }
        return fullName.substring(pos + 1);
    }

    private void fireMessageLogged(java.lang.String message, int priority) {
        BuildEvent event;
        if (currentTask == null) {
            event = new BuildEvent(this);
        } else {
            event = new BuildEvent(currentTask);
        }
        event.setMessage(message, priority);
        java.util.Iterator itr = listeners.iterator();
        while (itr.hasNext()) {
            BuildListener listener = ((BuildListener) (itr.next()));
            listener.messageLogged(event);
        } 
    }

    private void fireBuildStarted() {
        java.util.Iterator itr = listeners.iterator();
        while (itr.hasNext()) {
            BuildListener listener = ((BuildListener) (itr.next()));
            BuildEvent event = new BuildEvent(this);
            listener.buildStarted(event);
        } 
    }

    private void fireBuildFinished(BuildException exc) {
        BuildEvent event = new BuildEvent(this);
        event.setException(exc);
        java.util.Iterator itr = listeners.iterator();
        while (itr.hasNext()) {
            BuildListener listener = ((BuildListener) (itr.next()));
            listener.buildFinished(event);
        } 
    }

    private void fireImportStarted(Project project) {
        BuildEvent event = new BuildEvent(project);
        java.util.Iterator itr = listeners.iterator();
        while (itr.hasNext()) {
            BuildListener listener = ((BuildListener) (itr.next()));
            listener.importStarted(event);
        } 
    }

    private void fireImportFinished(Project project, BuildException exc) {
        BuildEvent event = new BuildEvent(project);
        event.setException(exc);
        java.util.Iterator itr = listeners.iterator();
        while (itr.hasNext()) {
            BuildListener listener = ((BuildListener) (itr.next()));
            listener.importFinished(event);
        } 
    }

    private void fireTargetStarted(Target target) {
        BuildEvent event = new BuildEvent(target);
        java.util.Iterator itr = listeners.iterator();
        while (itr.hasNext()) {
            BuildListener listener = ((BuildListener) (itr.next()));
            listener.targetStarted(event);
        } 
    }

    private void fireTargetFinished(Target target, BuildException exc) {
        BuildEvent event = new BuildEvent(target);
        event.setException(exc);
        java.util.Iterator itr = listeners.iterator();
        while (itr.hasNext()) {
            BuildListener listener = ((BuildListener) (itr.next()));
            listener.targetFinished(event);
        } 
    }

    private void fireTaskStarted(Task task) {
        BuildEvent event = new BuildEvent(task);
        java.util.Iterator itr = listeners.iterator();
        while (itr.hasNext()) {
            BuildListener listener = ((BuildListener) (itr.next()));
            listener.taskStarted(event);
        } 
    }

    private void fireTaskFinished(Task task, BuildException exc) {
        BuildEvent event = new BuildEvent(task);
        event.setException(exc);
        java.util.Iterator itr = listeners.iterator();
        while (itr.hasNext()) {
            BuildListener listener = ((BuildListener) (itr.next()));
            listener.taskFinished(event);
        } 
    }
}