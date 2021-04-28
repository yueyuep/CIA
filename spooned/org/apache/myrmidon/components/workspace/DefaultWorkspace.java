/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.workspace;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.parameters.ParameterException;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log.Hierarchy;
import org.apache.log.LogTarget;
import org.apache.log.Logger;
import org.apache.myrmidon.api.DefaultTaskContext;
import org.apache.myrmidon.api.TaskContext;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.components.deployer.DefaultDeployer;
import org.apache.myrmidon.components.deployer.Deployer;
import org.apache.myrmidon.components.deployer.DeploymentException;
import org.apache.myrmidon.components.executor.DefaultExecutionFrame;
import org.apache.myrmidon.components.executor.ExecutionFrame;
import org.apache.myrmidon.components.executor.Executor;
import org.apache.myrmidon.components.model.Project;
import org.apache.myrmidon.components.model.Target;
import org.apache.myrmidon.components.model.TypeLib;
import org.apache.myrmidon.components.type.TypeManager;
import org.apache.myrmidon.framework.Condition;
import org.apache.myrmidon.listeners.ProjectListener;
/**
 * This is the default implementation of Workspace.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultWorkspace extends org.apache.avalon.framework.logger.AbstractLoggable implements org.apache.myrmidon.components.workspace.Workspace , org.apache.avalon.framework.component.Composable , org.apache.avalon.framework.parameters.Parameterizable , org.apache.avalon.framework.activity.Initializable {
    private org.apache.myrmidon.components.executor.Executor m_executor;

    private org.apache.myrmidon.components.workspace.ProjectListenerSupport m_listenerSupport = new org.apache.myrmidon.components.workspace.ProjectListenerSupport();

    private org.apache.avalon.framework.component.ComponentManager m_componentManager;

    private org.apache.avalon.framework.parameters.Parameters m_parameters;

    private org.apache.myrmidon.api.TaskContext m_baseContext;

    private java.util.HashMap m_entrys = new java.util.HashMap();

    private org.apache.myrmidon.components.type.TypeManager m_typeManager;

    private org.apache.log.Hierarchy m_hierarchy;

    private int m_projectID;

    /**
     * Add a listener to project events.
     *
     * @param listener
     * 		the listener
     */
    public void addProjectListener(final org.apache.myrmidon.listeners.ProjectListener listener) {
        m_listenerSupport.addProjectListener(listener);
    }

    /**
     * Remove a listener from project events.
     *
     * @param listener
     * 		the listener
     */
    public void removeProjectListener(final org.apache.myrmidon.listeners.ProjectListener listener) {
        m_listenerSupport.removeProjectListener(listener);
    }

    /**
     * Retrieve relevent services needed for engine.
     *
     * @param componentManager
     * 		the ComponentManager
     * @exception ComponentException
     * 		if an error occurs
     */
    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        m_componentManager = componentManager;
        m_typeManager = ((org.apache.myrmidon.components.type.TypeManager) (componentManager.lookup(TypeManager.ROLE)));
        m_executor = ((org.apache.myrmidon.components.executor.Executor) (componentManager.lookup(Executor.ROLE)));
    }

    public void parameterize(final org.apache.avalon.framework.parameters.Parameters parameters) throws org.apache.avalon.framework.parameters.ParameterException {
        m_parameters = parameters;
    }

    public void initialize() throws java.lang.Exception {
        m_baseContext = createBaseContext();
        m_hierarchy = new org.apache.log.Hierarchy();
        final org.apache.log.LogTarget target = new org.apache.myrmidon.components.workspace.LogTargetToListenerAdapter(m_listenerSupport);
        m_hierarchy.setDefaultLogTarget(target);
    }

    /**
     * Execute a target in a particular project.
     * Execute in the project context.
     *
     * @param project
     * 		the Project
     * @param target
     * 		the name of the target
     * @exception TaskException
     * 		if an error occurs
     */
    public void executeProject(final org.apache.myrmidon.components.model.Project project, final java.lang.String target) throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.components.workspace.ProjectEntry entry = getProjectEntry(project);
        m_listenerSupport.projectStarted();
        executeTarget("<init>", project.getImplicitTarget(), entry.getFrame());
        execute(project, target, entry);
        m_listenerSupport.projectFinished();
    }

    private org.apache.myrmidon.api.TaskContext createBaseContext() throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.api.TaskContext context = new org.apache.myrmidon.api.DefaultTaskContext();
        final java.lang.String[] names = m_parameters.getNames();
        for (int i = 0; i < names.length; i++) {
            final java.lang.String value = m_parameters.getParameter(names[i], null);
            context.setProperty(names[i], value);
        }
        // Add system properties so that they overide user-defined properties
        addToContext(context, java.lang.System.getProperties());
        return context;
    }

    private java.io.File findTypeLib(final java.lang.String libraryName) throws org.apache.myrmidon.api.TaskException {
        // TODO: In future this will be expanded to allow
        // users to specify search path or automagically
        // add entries to lib path (like user specific or
        // workspace specific)
        final java.lang.String name = libraryName.replace('/', java.io.File.separatorChar) + ".atl";
        final java.lang.String home = java.lang.System.getProperty("myrmidon.home");
        final java.io.File homeDir = new java.io.File((home + java.io.File.separatorChar) + "ext");
        final java.io.File library = new java.io.File(homeDir, name);
        if (library.exists()) {
            if (!library.canRead()) {
                throw new org.apache.myrmidon.api.TaskException("Unable to read library at " + library);
            } else {
                return library;
            }
        }
        throw new org.apache.myrmidon.api.TaskException("Unable to locate Type Library " + libraryName);
    }

    private void deployTypeLib(final org.apache.myrmidon.components.deployer.Deployer deployer, final org.apache.myrmidon.components.model.Project project) throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.components.model.TypeLib[] typeLibs = project.getTypeLibs();
        for (int i = 0; i < typeLibs.length; i++) {
            final org.apache.myrmidon.components.model.TypeLib typeLib = typeLibs[i];
            final java.io.File file = findTypeLib(typeLib.getLibrary());
            try {
                if (null == typeLib.getRole()) {
                    deployer.deploy(file);
                } else {
                    deployer.deployType(typeLib.getRole(), typeLib.getName(), file);
                }
            } catch (final org.apache.myrmidon.components.deployer.DeploymentException de) {
                throw new org.apache.myrmidon.api.TaskException((("Error deploying type library " + typeLib) + " at ") + file, de);
            }
        }
    }

    private org.apache.myrmidon.components.executor.ExecutionFrame createExecutionFrame(final org.apache.myrmidon.components.model.Project project) throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.api.TaskContext context = new org.apache.myrmidon.api.DefaultTaskContext(m_baseContext);
        context.setProperty(TaskContext.BASE_DIRECTORY, project.getBaseDirectory());
        // Create per frame ComponentManager
        final org.apache.avalon.framework.component.DefaultComponentManager componentManager = new org.apache.avalon.framework.component.DefaultComponentManager(m_componentManager);
        // Add in child type manager so each frame can register different
        // sets of tasks etc
        final org.apache.myrmidon.components.type.TypeManager typeManager = m_typeManager.createChildTypeManager();
        componentManager.put(TypeManager.ROLE, typeManager);
        // We need to create a new deployer so that it deploys
        // to project specific TypeManager
        final org.apache.myrmidon.components.deployer.DefaultDeployer deployer = new org.apache.myrmidon.components.deployer.DefaultDeployer();
        deployer.setLogger(getLogger());
        try {
            deployer.compose(componentManager);
        } catch (final org.apache.avalon.framework.component.ComponentException ce) {
            throw new org.apache.myrmidon.api.TaskException("Error configuring deployer", ce);
        }
        // HACK: Didn't call initialize because Deployer contained in Embeddor
        // Already initialized and this would be reduendent
        // deployer.initialize();
        componentManager.put(Deployer.ROLE, deployer);
        deployTypeLib(deployer, project);
        // We need to place projects and ProjectManager
        // in ComponentManager so as to support project-local call()
        componentManager.put(Workspace.ROLE, this);
        componentManager.put(Project.ROLE, project);
        final java.lang.String[] names = project.getProjectNames();
        for (int i = 0; i < names.length; i++) {
            final java.lang.String name = names[i];
            final org.apache.myrmidon.components.model.Project other = project.getProject(name);
            componentManager.put((org.apache.myrmidon.components.model.Project.ROLE + "/") + name, other);
        }
        final org.apache.myrmidon.components.executor.DefaultExecutionFrame frame = new org.apache.myrmidon.components.executor.DefaultExecutionFrame();
        try {
            final org.apache.log.Logger logger = m_hierarchy.getLoggerFor("project" + m_projectID);
            m_projectID++;
            frame.setLogger(logger);
            frame.contextualize(context);
            frame.compose(componentManager);
        } catch (final java.lang.Exception e) {
            throw new org.apache.myrmidon.api.TaskException("Error setting up ExecutionFrame", e);
        }
        return frame;
    }

    private org.apache.myrmidon.components.workspace.ProjectEntry getProjectEntry(final org.apache.myrmidon.components.model.Project project) throws org.apache.myrmidon.api.TaskException {
        org.apache.myrmidon.components.workspace.ProjectEntry entry = ((org.apache.myrmidon.components.workspace.ProjectEntry) (m_entrys.get(project)));
        if (null == entry) {
            final org.apache.myrmidon.components.executor.ExecutionFrame frame = createExecutionFrame(project);
            entry = new org.apache.myrmidon.components.workspace.ProjectEntry(project, frame);
            m_entrys.put(project, entry);
        }
        return entry;
    }

    private org.apache.myrmidon.components.model.Project getProject(final java.lang.String name, final org.apache.myrmidon.components.model.Project project) throws org.apache.myrmidon.api.TaskException {
        final org.apache.myrmidon.components.model.Project other = project.getProject(name);
        if (null == other) {
            // TODO: Fix this so location information included in description
            throw new org.apache.myrmidon.api.TaskException(("Project '" + name) + "' not found.");
        }
        return other;
    }

    /**
     * Helper method to execute a target.
     *
     * @param project
     * 		the Project
     * @param target
     * 		the name of the target
     * @param context
     * 		the context
     * @param done
     * 		the list of targets already executed in current run
     * @exception TaskException
     * 		if an error occurs
     */
    private void execute(final org.apache.myrmidon.components.model.Project project, final java.lang.String targetName, final org.apache.myrmidon.components.workspace.ProjectEntry entry) throws org.apache.myrmidon.api.TaskException {
        final int index = targetName.indexOf("->");
        if ((-1) != index) {
            final java.lang.String name = targetName.substring(0, index);
            final java.lang.String otherTargetName = targetName.substring(index + 2);
            final org.apache.myrmidon.components.model.Project otherProject = getProject(name, project);
            final org.apache.myrmidon.components.workspace.ProjectEntry otherEntry = getProjectEntry(otherProject);
            // Execute target in referenced project
            execute(otherProject, otherTargetName, otherEntry);
            return;
        }
        final org.apache.myrmidon.components.model.Target target = project.getTarget(targetName);
        if (null == target) {
            throw new org.apache.myrmidon.api.TaskException("Unable to find target " + targetName);
        }
        // add target to list of targets executed
        entry.completeTarget(targetName);
        // execute all dependencies
        final java.lang.String[] dependencies = target.getDependencies();
        for (int i = 0; i < dependencies.length; i++) {
            if (!entry.isTargetCompleted(dependencies[i])) {
                execute(project, dependencies[i], entry);
            }
        }
        // notify listeners
        m_listenerSupport.targetStarted(targetName);
        executeTarget(targetName, target, entry.getFrame());
        // notify listeners
        m_listenerSupport.targetFinished();
    }

    /**
     * Method to execute a particular target instance.
     *
     * @param targetName
     * 		the name of target
     * @param target
     * 		the target
     * @param context
     * 		the context in which to execute
     * @exception TaskException
     * 		if an error occurs
     */
    private void executeTarget(final java.lang.String name, final org.apache.myrmidon.components.model.Target target, final org.apache.myrmidon.components.executor.ExecutionFrame frame) throws org.apache.myrmidon.api.TaskException {
        // check the condition associated with target.
        // if it is not satisfied then skip target
        final org.apache.myrmidon.framework.Condition condition = target.getCondition();
        if (null != condition) {
            try {
                if (false == condition.evaluate(frame.getContext())) {
                    getLogger().debug(("Skipping target " + name) + " as it does not satisfy condition");
                    return;
                }
            } catch (final org.apache.avalon.framework.context.ContextException ce) {
                throw new org.apache.myrmidon.api.TaskException("Error evaluating Condition for target " + name, ce);
            }
        }
        getLogger().debug("Executing target " + name);
        // frame.getContext().setProperty( Project.TARGET, target );
        // execute all tasks assciated with target
        final org.apache.avalon.framework.configuration.Configuration[] tasks = target.getTasks();
        for (int i = 0; i < tasks.length; i++) {
            executeTask(tasks[i], frame);
        }
    }

    /**
     * Execute a task.
     *
     * @param task
     * 		the task definition
     * @param context
     * 		the context
     * @exception TaskException
     * 		if an error occurs
     */
    private void executeTask(final org.apache.avalon.framework.configuration.Configuration task, final org.apache.myrmidon.components.executor.ExecutionFrame frame) throws org.apache.myrmidon.api.TaskException {
        final java.lang.String name = task.getName();
        getLogger().debug("Executing task " + name);
        // is setting name even necessary ???
        frame.getContext().setProperty(TaskContext.NAME, name);
        // notify listeners
        m_listenerSupport.taskStarted(name);
        // run task
        m_executor.execute(task, frame);
        // notify listeners task has ended
        m_listenerSupport.taskFinished();
    }

    /**
     * Helper method to add values to a context
     *
     * @param context
     * 		the context
     * @param map
     * 		the map of names->values
     */
    private void addToContext(final org.apache.myrmidon.api.TaskContext context, final java.util.Map map) throws org.apache.myrmidon.api.TaskException {
        final java.util.Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            final java.lang.String key = ((java.lang.String) (keys.next()));
            final java.lang.Object value = map.get(key);
            context.setProperty(key, value);
        } 
    }
}