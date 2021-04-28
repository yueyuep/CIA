/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.embeddor;
import org.apache.avalon.excalibur.io.ExtensionFileFilter;
import org.apache.avalon.excalibur.io.FileUtil;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.component.DefaultComponentManager;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.parameters.Parameterizable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.myrmidon.api.JavaVersion;
import org.apache.myrmidon.components.aspect.AspectManager;
import org.apache.myrmidon.components.builder.ProjectBuilder;
import org.apache.myrmidon.components.configurer.Configurer;
import org.apache.myrmidon.components.converter.ConverterRegistry;
import org.apache.myrmidon.components.converter.MasterConverter;
import org.apache.myrmidon.components.deployer.Deployer;
import org.apache.myrmidon.components.deployer.DeploymentException;
import org.apache.myrmidon.components.executor.Executor;
import org.apache.myrmidon.components.model.Project;
import org.apache.myrmidon.components.role.RoleManager;
import org.apache.myrmidon.components.type.TypeFactory;
import org.apache.myrmidon.components.type.TypeManager;
import org.apache.myrmidon.components.workspace.Workspace;
/**
 * Default implementation of Embeddor.
 * Instantiate this to embed inside other applications.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultEmbeddor extends org.apache.avalon.framework.logger.AbstractLoggable implements org.apache.myrmidon.components.embeddor.Embeddor {
    private org.apache.myrmidon.components.deployer.Deployer m_deployer;

    private org.apache.myrmidon.components.role.RoleManager m_roleManager;

    private org.apache.myrmidon.components.aspect.AspectManager m_aspectManager;

    private org.apache.myrmidon.components.type.TypeManager m_typeManager;

    private org.apache.myrmidon.components.converter.MasterConverter m_converter;

    private org.apache.myrmidon.components.converter.ConverterRegistry m_converterRegistry;

    private org.apache.myrmidon.components.executor.Executor m_executor;

    private org.apache.myrmidon.components.configurer.Configurer m_configurer;

    private org.apache.avalon.framework.component.DefaultComponentManager m_componentManager;

    private org.apache.avalon.framework.parameters.Parameters m_parameters;

    private org.apache.avalon.framework.parameters.Parameters m_defaults;

    private java.io.File m_homeDir;

    private java.io.File m_binDir;

    private java.io.File m_libDir;

    private java.io.File m_taskLibDir;

    /**
     * Setup basic properties of engine.
     * Called before init() and can be used to specify alternate components in system.
     *
     * @param properties
     * 		the properties
     */
    public void parameterize(final org.apache.avalon.framework.parameters.Parameters parameters) {
        m_parameters = parameters;
    }

    public org.apache.myrmidon.components.model.Project createProject(final java.lang.String location, java.lang.String type, final org.apache.avalon.framework.parameters.Parameters parameters) throws java.lang.Exception {
        if (null == type) {
            type = guessTypeFor(location);
        }
        final org.apache.myrmidon.components.builder.ProjectBuilder builder = getProjectBuilder(type, parameters);
        return builder.build(location);
    }

    private java.lang.String guessTypeFor(final java.lang.String location) {
        return org.apache.avalon.excalibur.io.FileUtil.getExtension(location);
    }

    private org.apache.myrmidon.components.builder.ProjectBuilder getProjectBuilder(final java.lang.String type, final org.apache.avalon.framework.parameters.Parameters parameters) throws java.lang.Exception {
        final org.apache.myrmidon.components.type.TypeFactory factory = m_typeManager.getFactory(ProjectBuilder.ROLE);
        final org.apache.myrmidon.components.builder.ProjectBuilder builder = ((org.apache.myrmidon.components.builder.ProjectBuilder) (factory.create(type)));
        setupLogger(builder);
        if (builder instanceof org.apache.avalon.framework.component.Composable) {
            ((org.apache.avalon.framework.component.Composable) (builder)).compose(m_componentManager);
        }
        if (builder instanceof org.apache.avalon.framework.parameters.Parameterizable) {
            ((org.apache.avalon.framework.parameters.Parameterizable) (builder)).parameterize(parameters);
        }
        if (builder instanceof org.apache.avalon.framework.activity.Initializable) {
            ((org.apache.avalon.framework.activity.Initializable) (builder)).initialize();
        }
        return builder;
    }

    public org.apache.myrmidon.components.workspace.Workspace createWorkspace(final org.apache.avalon.framework.parameters.Parameters parameters) throws java.lang.Exception {
        final java.lang.String component = getParameter(Workspace.ROLE);
        final org.apache.myrmidon.components.workspace.Workspace workspace = ((org.apache.myrmidon.components.workspace.Workspace) (createComponent(component, org.apache.myrmidon.components.workspace.Workspace.class)));
        setupLogger(workspace);
        if (workspace instanceof org.apache.avalon.framework.component.Composable) {
            ((org.apache.avalon.framework.component.Composable) (workspace)).compose(m_componentManager);
        }
        if (workspace instanceof org.apache.avalon.framework.parameters.Parameterizable) {
            ((org.apache.avalon.framework.parameters.Parameterizable) (workspace)).parameterize(parameters);
        }
        if (workspace instanceof org.apache.avalon.framework.activity.Initializable) {
            ((org.apache.avalon.framework.activity.Initializable) (workspace)).initialize();
        }
        return workspace;
    }

    /**
     * Initialize the system.
     *
     * @exception Exception
     * 		if an error occurs
     */
    public void initialize() throws java.lang.Exception {
        // setup default properties
        m_defaults = createDefaultParameters();
        // create all the components
        createComponents();
        // setup the component manager
        m_componentManager = createComponentManager();
        setupComponents();
        setupFiles();
    }

    public void start() throws java.lang.Exception {
        final org.apache.avalon.excalibur.io.ExtensionFileFilter filter = new org.apache.avalon.excalibur.io.ExtensionFileFilter(".atl");
        deployFromDirectory(m_deployer, m_taskLibDir, filter);
    }

    public void stop() {
        // Undeploy all the tasks by killing ExecutionFrame???
    }

    /**
     * Dispose engine.
     *
     * @exception Exception
     * 		if an error occurs
     */
    public void dispose() {
        m_aspectManager = null;
        m_roleManager = null;
        m_converterRegistry = null;
        m_converter = null;
        m_executor = null;
        m_deployer = null;
        m_configurer = null;
        m_componentManager = null;
        m_parameters = null;
        m_defaults = null;
        m_homeDir = null;
        m_binDir = null;
        m_libDir = null;
        m_taskLibDir = null;
    }

    /**
     * Create default properties which includes default names of all components.
     * Overide this in sub-classes to change values.
     *
     * @return the Parameters
     */
    private org.apache.avalon.framework.parameters.Parameters createDefaultParameters() {
        final org.apache.avalon.framework.parameters.Parameters defaults = new org.apache.avalon.framework.parameters.Parameters();
        // create all the default properties for files/directories
        defaults.setParameter("myrmidon.bin.path", "bin");
        defaults.setParameter("myrmidon.lib.path", "lib");
        // create all the default properties for components
        defaults.setParameter(AspectManager.ROLE, "org.apache.myrmidon.components.aspect.DefaultAspectManager");
        defaults.setParameter(RoleManager.ROLE, "org.apache.myrmidon.components.role.DefaultRoleManager");
        defaults.setParameter(MasterConverter.ROLE, "org.apache.myrmidon.components.converter.DefaultMasterConverter");
        defaults.setParameter(ConverterRegistry.ROLE, "org.apache.myrmidon.components.converter.DefaultConverterRegistry");
        defaults.setParameter(TypeManager.ROLE, "org.apache.myrmidon.components.type.DefaultTypeManager");
        // "org.apache.myrmidon.components.executor.DefaultExecutor" );
        // "org.apache.myrmidon.components.executor.PrintingExecutor" );
        defaults.setParameter(Executor.ROLE, "org.apache.myrmidon.components.executor.AspectAwareExecutor");
        defaults.setParameter(Workspace.ROLE, "org.apache.myrmidon.components.workspace.DefaultWorkspace");
        defaults.setParameter(Deployer.ROLE, "org.apache.myrmidon.components.deployer.DefaultDeployer");
        defaults.setParameter(Configurer.ROLE, "org.apache.myrmidon.components.configurer.DefaultConfigurer");
        return defaults;
    }

    /**
     * Create a ComponentManager containing all components in engine.
     *
     * @return the ComponentManager
     */
    private org.apache.avalon.framework.component.DefaultComponentManager createComponentManager() {
        final org.apache.avalon.framework.component.DefaultComponentManager componentManager = new org.apache.avalon.framework.component.DefaultComponentManager();
        componentManager.put(MasterConverter.ROLE, m_converter);
        // Following components required when Myrmidon is used as build tool
        componentManager.put(Embeddor.ROLE, this);
        // Following components required when Myrmidon allows user deployment of tasks etal.
        componentManager.put(RoleManager.ROLE, m_roleManager);
        componentManager.put(Deployer.ROLE, m_deployer);
        // Following components used when want to types (ie tasks/mappers etc)
        componentManager.put(TypeManager.ROLE, m_typeManager);
        componentManager.put(ConverterRegistry.ROLE, m_converterRegistry);
        componentManager.put(AspectManager.ROLE, m_aspectManager);
        // Following components required when allowing Container tasks
        componentManager.put(Configurer.ROLE, m_configurer);
        componentManager.put(Executor.ROLE, m_executor);
        return componentManager;
    }

    /**
     * Create all required components.
     *
     * @exception Exception
     * 		if an error occurs
     */
    private void createComponents() throws java.lang.Exception {
        java.lang.String component = null;
        component = getParameter(ConverterRegistry.ROLE);
        m_converterRegistry = ((org.apache.myrmidon.components.converter.ConverterRegistry) (createComponent(component, org.apache.myrmidon.components.converter.ConverterRegistry.class)));
        component = getParameter(MasterConverter.ROLE);
        m_converter = ((org.apache.myrmidon.components.converter.MasterConverter) (createComponent(component, org.apache.myrmidon.components.converter.MasterConverter.class)));
        component = getParameter(Configurer.ROLE);
        m_configurer = ((org.apache.myrmidon.components.configurer.Configurer) (createComponent(component, org.apache.myrmidon.components.configurer.Configurer.class)));
        component = getParameter(TypeManager.ROLE);
        m_typeManager = ((org.apache.myrmidon.components.type.TypeManager) (createComponent(component, org.apache.myrmidon.components.type.TypeManager.class)));
        component = getParameter(RoleManager.ROLE);
        m_roleManager = ((org.apache.myrmidon.components.role.RoleManager) (createComponent(component, org.apache.myrmidon.components.role.RoleManager.class)));
        component = getParameter(AspectManager.ROLE);
        m_aspectManager = ((org.apache.myrmidon.components.aspect.AspectManager) (createComponent(component, org.apache.myrmidon.components.aspect.AspectManager.class)));
        component = getParameter(Deployer.ROLE);
        m_deployer = ((org.apache.myrmidon.components.deployer.Deployer) (createComponent(component, org.apache.myrmidon.components.deployer.Deployer.class)));
        component = getParameter(Executor.ROLE);
        m_executor = ((org.apache.myrmidon.components.executor.Executor) (createComponent(component, org.apache.myrmidon.components.executor.Executor.class)));
    }

    /**
     * Setup all the components. (ir run all required lifecycle methods).
     *
     * @exception Exception
     * 		if an error occurs
     */
    private void setupComponents() throws java.lang.Exception {
        setupComponent(m_roleManager);
        setupComponent(m_aspectManager);
        setupComponent(m_converterRegistry);
        setupComponent(m_converter);
        setupComponent(m_executor);
        setupComponent(m_deployer);
        setupComponent(m_configurer);
    }

    /**
     * Setup an individual component.
     *
     * @param component
     * 		the component
     * @exception Exception
     * 		if an error occurs
     */
    private void setupComponent(final org.apache.avalon.framework.component.Component component) throws java.lang.Exception {
        setupLogger(component);
        if (component instanceof org.apache.avalon.framework.component.Composable) {
            ((org.apache.avalon.framework.component.Composable) (component)).compose(m_componentManager);
        }
        if (component instanceof org.apache.avalon.framework.activity.Initializable) {
            ((org.apache.avalon.framework.activity.Initializable) (component)).initialize();
        }
    }

    /**
     * Setup all the files attributes.
     */
    private void setupFiles() throws java.lang.Exception {
        java.lang.String filepath = null;
        filepath = getParameter("myrmidon.home");
        m_homeDir = new java.io.File(filepath).getAbsoluteFile();
        checkDirectory(m_homeDir, "home");
        filepath = getParameter("myrmidon.bin.path");
        m_binDir = resolveDirectory(filepath, "bin-dir");
        filepath = getParameter("myrmidon.lib.path");
        m_taskLibDir = resolveDirectory(filepath, "task-lib-dir");
    }

    /**
     * Retrieve value of named property.
     * First access passed in properties and then the default properties.
     *
     * @param name
     * 		the name of property
     * @return the value of property or null
     */
    private java.lang.String getParameter(final java.lang.String name) {
        java.lang.String value = m_parameters.getParameter(name, null);
        if (null == value) {
            value = m_defaults.getParameter(name, null);
        }
        return value;
    }

    /**
     * Resolve a directory relative to another base directory.
     *
     * @param dir
     * 		the base directory
     * @param name
     * 		the relative directory
     * @return the created File
     * @exception Exception
     * 		if an error occurs
     */
    private java.io.File resolveDirectory(final java.lang.String dir, final java.lang.String name) throws java.lang.Exception {
        final java.io.File file = org.apache.avalon.excalibur.io.FileUtil.resolveFile(m_homeDir, dir);
        checkDirectory(file, name);
        return file;
    }

    /**
     * Verify file is a directory else throw an exception.
     *
     * @param file
     * 		the File
     * @param name
     * 		the name of file type (used in error messages)
     */
    private void checkDirectory(final java.io.File file, final java.lang.String name) throws java.lang.Exception {
        if (!file.exists()) {
            throw new java.lang.Exception(((name + " (") + file) + ") does not exist");
        } else if (!file.isDirectory()) {
            throw new java.lang.Exception(((name + " (") + file) + ") is not a directory");
        }
    }

    /**
     * Helper method to retrieve current JVM version.
     *
     * @return the current JVM version
     */
    private org.apache.myrmidon.api.JavaVersion getJavaVersion() {
        org.apache.myrmidon.api.JavaVersion version = org.apache.myrmidon.api.JavaVersion.JAVA1_0;
        try {
            java.lang.Class.forName("java.lang.Void");
            version = org.apache.myrmidon.api.JavaVersion.JAVA1_1;
            java.lang.Class.forName("java.lang.ThreadLocal");
            version = org.apache.myrmidon.api.JavaVersion.JAVA1_2;
            java.lang.Class.forName("java.lang.StrictMath");
            version = org.apache.myrmidon.api.JavaVersion.JAVA1_3;
        } catch (final java.lang.ClassNotFoundException cnfe) {
        }
        return version;
    }

    /**
     * Create a component that implements an interface.
     *
     * @param component
     * 		the name of the component
     * @param clazz
     * 		the name of interface/type
     * @return the created object
     * @exception Exception
     * 		if an error occurs
     */
    private java.lang.Object createComponent(final java.lang.String component, final java.lang.Class clazz) throws java.lang.Exception {
        try {
            final java.lang.Object object = java.lang.Class.forName(component).newInstance();
            if (!clazz.isInstance(object)) {
                throw new java.lang.Exception((("Object " + component) + " is not an instance of ") + clazz);
            }
            return object;
        } catch (final java.lang.IllegalAccessException iae) {
            throw new java.lang.Exception((("Non-public constructor for " + clazz) + " ") + component);
        } catch (final java.lang.InstantiationException ie) {
            throw new java.lang.Exception((("Error instantiating class for " + clazz) + " ") + component);
        } catch (final java.lang.ClassNotFoundException cnfe) {
            throw new java.lang.Exception(((("Could not find the class for " + clazz) + " (") + component) + ")");
        }
    }

    private void deployFromDirectory(final org.apache.myrmidon.components.deployer.Deployer deployer, final java.io.File directory, final java.io.FilenameFilter filter) throws org.apache.myrmidon.components.deployer.DeploymentException {
        final java.io.File[] files = directory.listFiles(filter);
        if (null != files) {
            deployFiles(deployer, files);
        }
    }

    private void deployFiles(final org.apache.myrmidon.components.deployer.Deployer deployer, final java.io.File[] files) throws org.apache.myrmidon.components.deployer.DeploymentException {
        for (int i = 0; i < files.length; i++) {
            final java.lang.String filename = files[i].getName();
            int index = filename.lastIndexOf('.');
            if ((-1) == index)
                index = filename.length();

            final java.lang.String name = filename.substring(0, index);
            try {
                final java.io.File file = files[i].getCanonicalFile();
                deployer.deploy(file);
            } catch (final org.apache.myrmidon.components.deployer.DeploymentException de) {
                throw de;
            } catch (final java.lang.Exception e) {
                throw new org.apache.myrmidon.components.deployer.DeploymentException("Unable to retrieve filename for file " + files[i], e);
            }
        }
    }
}