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
package org.apache.ant.core.execution;
import java.net.*;
import java.util.*;
import org.apache.ant.core.model.*;
import org.apache.ant.core.support.*;
import org.apache.ant.core.types.*;
/**
 * An ExecutionFrame is the state of a project during an execution.
 * The ExecutionFrame contains the data values set by Ant tasks as
 * they are executed, including task definitions, property values, etc.
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 */
public class ExecutionFrame {
    /**
     * State used in dependency analysis when a target's dependencies are being
     * examined.
     */
    private static final java.lang.String VISITING = "VISITING";

    /**
     * State used in dependency analysis to indicate a target has been examined
     */
    private static final java.lang.String VISITED = "VISITED";

    /**
     * The Project that this execiton frame is processing
     */
    private org.apache.ant.core.execution.Project project = null;

    /**
     * The base URL for this frame. This is derived from the
     * Project's source URL and it's base attribute.
     */
    private java.net.URL baseURL = null;

    /**
     * The imported frames of this frame. For each project imported by this frame's
     * project, a corresponding ExecutionFrame is created.
     */
    private java.util.Map importedFrames = new java.util.HashMap();

    /**
     * BuildEvent support used to fire events and manage listeners
     */
    private org.apache.ant.core.execution.BuildEventSupport eventSupport = new BuildEventSupport();

    /**
     * The context of this execution. This contains all data object's
     * created by tasks that have been executed
     */
    private java.util.Map dataValues = new java.util.HashMap();

    /**
     * Introspector objects used to configure Tasks from the Task models.
     */
    private java.util.Map introspectors = new java.util.HashMap();

    /**
     * The task defs that this frame will use to process tasks
     */
    private java.util.Map taskDefs = new java.util.HashMap();

    /**
     * Type converters for this executionFrame. Converters are used when configuring
     * Tasks to handle special type conversions.
     */
    private java.util.Map converters = new java.util.HashMap();

    /**
     * The aspect handler active in this frame
     */
    private java.util.Map aspectHandlers = new java.util.HashMap();

    /**
     * The namespace under which this execution frame lives in the hierarchical
     * project namespace - null for the root namespace
     */
    private java.lang.String namespace;

    /**
     * Construct an execution frame to process the given project model with
     * the configuration represented by the libraries.
     *
     * @param project
     * 		the model of the project to be built.
     * @param libraries
     * 		an Array of AntLibrary objects containing the
     * 		configuration of Ant for this build.
     * @throws ConfigException
     * 		when the project cannot be setup with the
     * 		given configuration
     */
    public ExecutionFrame(Project project, AntLibrary[] libraries) throws org.apache.ant.core.execution.ConfigException {
        this.namespace = null;
        setupFrame(project, libraries);
    }

    /**
     * Construct a subframe for managing a project imported into the main project.
     *
     * @param project
     * 		the model of the project to be built.
     * @param libraries
     * 		an Array of AntLibrary objects containing the
     * 		configuration of Ant for this build.
     * @param namespace
     * 		the location of this project within the overall import
     * 		namespace.
     * @throws ConfigException
     * 		when the project cannot be setup with the
     * 		given configuration
     */
    private ExecutionFrame(Project project, AntLibrary[] libraries, java.lang.String namespace) throws org.apache.ant.core.execution.ConfigException {
        this.namespace = namespace;
        setupFrame(project, libraries);
    }

    /**
     * Set up the execution frame.
     *
     * This method examines the project model and constructs the required
     * subframes to handle imported projects.
     *
     * @param project
     * 		the model of the project to be built.
     * @param libraries
     * 		an Array of AntLibrary objects containing the
     * 		configuration of Ant for this build.
     * @throws ConfigException
     * 		when the project cannot be setup with the
     * 		given configuration
     */
    private void setupFrame(Project project, AntLibrary[] libraries) throws org.apache.ant.core.execution.ConfigException {
        this.project = project;
        for (int i = 0; i < libraries.length; ++i) {
            addLibrary(libraries[i]);
        }
        try {
            java.lang.String base = project.getBase();
            if (base == null) {
                baseURL = project.getSourceURL();
            } else {
                base = base.trim();
                if (!base.endsWith("/")) {
                    base += "/";
                }
                baseURL = new java.net.URL(project.getSourceURL(), base);
            }
        } catch (java.net.MalformedURLException e) {
            throw new ConfigException(("Project\'s base value \"" + project.getBase()) + "\" is not valid", e, project.getLocation());
        }
        for (java.util.Iterator i = project.getImportedProjectNames(); i.hasNext();) {
            java.lang.String importName = ((java.lang.String) (i.next()));
            Project importedProject = project.getImportedProject(importName);
            java.lang.String importNamespace = (namespace == null) ? importName : (namespace + ":") + importName;
            org.apache.ant.core.execution.ExecutionFrame importedFrame = new org.apache.ant.core.execution.ExecutionFrame(importedProject, libraries, importNamespace);
            importedFrames.put(importName, importedFrame);
        }
    }

    /**
     * Add a configuration library to this execution frame. The library
     * will contain task definitions, converters, apsect handler definitions,
     * etc.
     *
     * @param library
     * 		the configuration library to add to this frame.
     * @throws ConfigException
     * 		if the items in the library cannot be configured.
     */
    public void addLibrary(AntLibrary library) throws org.apache.ant.core.execution.ConfigException {
        for (java.util.Iterator i = library.getTaskDefinitions(); i.hasNext();) {
            TaskDefinition taskDefinition = ((TaskDefinition) (i.next()));
            addTaskDefinition(taskDefinition);
        }
        for (java.util.Iterator i = library.getConverterDefinitions(); i.hasNext();) {
            ConverterDefinition converterDef = ((ConverterDefinition) (i.next()));
            addConverterDefinition(converterDef);
        }
        for (java.util.Iterator i = library.getAspectDefinitions(); i.hasNext();) {
            AspectDefinition aspectDef = ((AspectDefinition) (i.next()));
            addAspectHandler(aspectDef);
        }
    }

    /**
     * Add a task definition to this execution frame
     *
     * @param taskDefinition
     * 		the TaskDefinition to be added to the project.
     */
    public void addTaskDefinition(TaskDefinition taskDefinition) {
        java.lang.String taskName = taskDefinition.getName();
        taskDefs.put(taskName, taskDefinition);
    }

    /**
     * Add a aspect handler definition to this execution frame
     *
     * @param taskDefinition
     * 		the TaskDefinition to be added to the project.
     * @throws ConfigException
     * 		if the aspect handler cannot be created or configured.
     */
    public void addAspectHandler(AspectDefinition aspectDefinition) throws org.apache.ant.core.execution.ConfigException {
        java.lang.String aspectPrefix = aspectDefinition.getAspectPrefix();
        try {
            java.lang.Class aspectHandlerClass = aspectDefinition.getAspectHandlerClass();
            aspectHandlers.put(aspectPrefix, aspectHandlerClass);
        } catch (java.lang.ClassNotFoundException e) {
            throw new ConfigException((("Unable to load aspect handler class for " + aspectDefinition.getAspectHandlerClassName()) + " in converter from ") + aspectDefinition.getLibraryURL(), e);
        }
    }

    /**
     * Add a converter definition to this library.
     *
     * The converter is created immediately to handle conversions
     * when items are being configured. If the converter is an instance of
     * an AntConverter, the converter is configured with this execution
     * frame giving it the context it needs to resolve items relative to the
     * project's base, etc.
     *
     * @param converterDef
     * 		the converter definition to load
     * @throws ConfigException
     * 		if the converter cannot be created or configured.
     */
    public void addConverterDefinition(ConverterDefinition converterDef) throws org.apache.ant.core.execution.ConfigException {
        boolean targetLoaded = false;
        try {
            java.lang.Class targetClass = converterDef.getTargetClass();
            targetLoaded = false;
            java.lang.Class converterClass = converterDef.getConverterClass();
            Converter converter = ((AntConverter) (converterClass.newInstance()));
            if (converter instanceof AntConverter) {
                ((AntConverter) (converter)).init(this);
            }
            converters.put(targetClass, converter);
        } catch (java.lang.ClassNotFoundException e) {
            if (targetLoaded) {
                throw new ConfigException((("Unable to load converter class for " + converterDef.getConverterClassName()) + " in converter from ") + converterDef.getLibraryURL(), e);
            } else {
                throw new ConfigException((("Unable to load target class " + converterDef.getTargetClassName()) + " in converter from ") + converterDef.getLibraryURL(), e);
            }
        } catch (java.lang.InstantiationException e) {
            throw new ConfigException((("Unable to instantiate converter class " + converterDef.getTargetClassName()) + " in converter from ") + converterDef.getLibraryURL(), e);
        } catch (java.lang.IllegalAccessException e) {
            throw new ConfigException((("Unable to access converter class " + converterDef.getTargetClassName()) + " in converter from ") + converterDef.getLibraryURL(), e);
        }
    }

    /**
     * Get the bae URL of this frame. This will either be specified by the project's
     * base attribute or be derived implicitly from the project's location.
     */
    public java.net.URL getBaseURL() {
        return baseURL;
    }

    public void addBuildListener(BuildListener listener) {
        for (java.util.Iterator i = getImportedFrames(); i.hasNext();) {
            org.apache.ant.core.execution.ExecutionFrame subFrame = ((org.apache.ant.core.execution.ExecutionFrame) (i.next()));
            subFrame.addBuildListener(listener);
        }
        eventSupport.addBuildListener(listener);
    }

    public void removeBuildListener(BuildListener listener) {
        for (java.util.Iterator i = getImportedFrames(); i.hasNext();) {
            org.apache.ant.core.execution.ExecutionFrame subFrame = ((org.apache.ant.core.execution.ExecutionFrame) (i.next()));
            subFrame.removeBuildListener(listener);
        }
        eventSupport.removeBuildListener(listener);
    }

    /**
     * Get the project associated with this execution frame.
     *
     * @return the project associated iwth this execution frame.
     */
    public org.apache.ant.core.execution.Project getProject() {
        return project;
    }

    /**
     * Get the names of the frames representing imported projects.
     *
     * @return an iterator which returns the names of the imported frames.
     */
    public java.util.Iterator getImportedFrameNames() {
        return importedFrames.keySet().iterator();
    }

    /**
     * Get the frames representing imported projects.
     *
     * @return an iterator which returns the imported ExeuctionFrames..
     */
    public java.util.Iterator getImportedFrames() {
        return importedFrames.values().iterator();
    }

    /**
     * Get an imported frame by name
     *
     * @param importName
     * 		the name under which the frame was imported.
     * @return the ExecutionFrame asscociated with the given import name or null
    if there is no such project.
     */
    public org.apache.ant.core.execution.ExecutionFrame getImportedFrame(java.lang.String importName) {
        return ((org.apache.ant.core.execution.ExecutionFrame) (importedFrames.get(importName)));
    }

    /**
     * Get the location of this frame in the namespace hierarchy
     *
     * @return the location of this frame within the project import
    namespace hierarchy.
     */
    public java.lang.String getNamespace() {
        return namespace;
    }

    /**
     * Get the fully qualified name of something with respect to this
     * execution frame.
     *
     * @param name
     * 		the unqualified name.
     * @return the fully qualified version of the given name
     */
    public java.lang.String getQualifiedName(java.lang.String name) {
        return namespace == null ? name : (namespace + ":") + name;
    }

    /**
     * Execute the given target's tasks
     *
     * @param the
     * 		name of the target within this frame that is to be executed.
     */
    public void executeTargetTasks(java.lang.String targetName) throws org.apache.ant.core.execution.ExecutionException, org.apache.ant.core.execution.ConfigException {
        Target target = project.getTarget(targetName);
        try {
            java.util.Iterator taskIterator = target.getTasks();
            eventSupport.fireTargetStarted(this, target);
            executeTasks(taskIterator);
            eventSupport.fireTargetFinished(this, target, null);
        } catch (java.lang.RuntimeException e) {
            eventSupport.fireTargetFinished(this, target, e);
            throw e;
        }
    }

    /**
     * Initialize the frame by executing the project level tasks if any
     */
    public void initialize() throws org.apache.ant.core.execution.ExecutionException, org.apache.ant.core.execution.ConfigException {
        for (java.util.Iterator i = getImportedFrames(); i.hasNext();) {
            org.apache.ant.core.execution.ExecutionFrame subFrame = ((org.apache.ant.core.execution.ExecutionFrame) (i.next()));
            subFrame.initialize();
        }
        java.util.Iterator taskIterator = project.getTasks();
        executeTasks(taskIterator);
    }

    public void fillinDependencyOrder(java.lang.String targetName, java.util.List dependencyOrder, java.util.Map state, java.util.Stack visiting) throws org.apache.ant.core.execution.ConfigException {
        java.lang.String fullTargetName = getQualifiedName(targetName);
        if (state.get(fullTargetName) == org.apache.ant.core.execution.ExecutionFrame.VISITED) {
            return;
        }
        Target target = getProject().getTarget(targetName);
        if (target == null) {
            java.lang.StringBuffer sb = new java.lang.StringBuffer("Target `");
            sb.append(targetName);
            sb.append("' does not exist in this project. ");
            if (!visiting.empty()) {
                java.lang.String parent = ((java.lang.String) (visiting.peek()));
                sb.append("It is used from target `");
                sb.append(parent);
                sb.append("'.");
            }
            throw new ConfigException(new java.lang.String(sb), getProject().getLocation());
        }
        state.put(fullTargetName, org.apache.ant.core.execution.ExecutionFrame.VISITING);
        visiting.push(fullTargetName);
        for (java.util.Iterator i = target.getDependencies(); i.hasNext();) {
            java.lang.String dependency = ((java.lang.String) (i.next()));
            try {
                org.apache.ant.core.execution.ExecutionFrame dependencyFrame = getRelativeFrame(dependency);
                if (dependencyFrame == null) {
                    java.lang.StringBuffer sb = new java.lang.StringBuffer("Target `");
                    sb.append(dependency);
                    sb.append("' does not exist in this project. ");
                    throw new ConfigException(new java.lang.String(sb), target.getLocation());
                }
                java.lang.String fullyQualifiedName = getQualifiedName(dependency);
                java.lang.String dependencyState = ((java.lang.String) (state.get(fullyQualifiedName)));
                if (dependencyState == null) {
                    dependencyFrame.fillinDependencyOrder(getNameInFrame(dependency), dependencyOrder, state, visiting);
                } else if (dependencyState == org.apache.ant.core.execution.ExecutionFrame.VISITING) {
                    java.lang.String circleDescription = getCircularDesc(dependency, visiting);
                    throw new ConfigException(circleDescription, target.getLocation());
                }
            } catch (ExecutionException e) {
                throw new ConfigException(e.getMessage(), e, target.getLocation());
            }
        }
        state.put(fullTargetName, org.apache.ant.core.execution.ExecutionFrame.VISITED);
        java.lang.String poppedNode = ((java.lang.String) (visiting.pop()));
        if (poppedNode != fullTargetName) {
            throw new ConfigException((((("Problem determining dependencies " + " - expecting '") + fullTargetName) + "' but got '") + poppedNode) + "'");
        }
        dependencyOrder.add(fullTargetName);
    }

    private java.lang.String getCircularDesc(java.lang.String end, java.util.Stack visitingNodes) {
        java.lang.StringBuffer sb = new java.lang.StringBuffer("Circular dependency: ");
        sb.append(end);
        java.lang.String c;
        do {
            c = ((java.lang.String) (visitingNodes.pop()));
            sb.append(" <- ");
            sb.append(c);
        } while (!c.equals(end) );
        return new java.lang.String(sb);
    }

    /**
     * Check whether the targets in this frame and its subframes are OK
     */
    public void checkTargets(java.util.List dependencyOrder, java.util.Map state, java.util.Stack visiting) throws org.apache.ant.core.execution.ConfigException {
        // get the targets and just iterate through them.
        for (java.util.Iterator i = getProject().getTargets(); i.hasNext();) {
            Target target = ((Target) (i.next()));
            fillinDependencyOrder(target.getName(), dependencyOrder, state, visiting);
        }
        // Now do the subframes.
        for (java.util.Iterator i = getImportedFrames(); i.hasNext();) {
            org.apache.ant.core.execution.ExecutionFrame importedFrame = ((org.apache.ant.core.execution.ExecutionFrame) (i.next()));
            importedFrame.checkTargets(dependencyOrder, state, visiting);
        }
    }

    /**
     * Create a Task and configure it according to the given model.
     */
    private org.apache.ant.core.execution.Task configureTask(TaskElement model) throws org.apache.ant.core.execution.ConfigException, org.apache.ant.core.execution.ExecutionException {
        java.lang.String taskType = model.getType();
        TaskDefinition taskDefinition = ((TaskDefinition) (taskDefs.get(taskType)));
        if (taskDefinition == null) {
            throw new ConfigException(("There is no defintion for tasks of type <" + taskType) + ">", model.getLocation());
        }
        try {
            java.lang.Class elementClass = taskDefinition.getExecutionTaskClass();
            java.lang.Object element = elementClass.newInstance();
            Task task = null;
            if (element instanceof Task) {
                // create a Task context for the Task
                task = ((Task) (element));
            } else {
                task = new TaskAdapter(taskType, element);
            }
            configureElement(element, model);
            return task;
        } catch (java.lang.ClassNotFoundException e) {
            throw new ConfigException(("Execution class " + taskDefinition.getTaskClassName()) + " was not found", e, model.getLocation());
        } catch (java.lang.InstantiationException e) {
            throw new ConfigException("Unable to instantiate execution class " + taskDefinition.getTaskClassName(), e, model.getLocation());
        } catch (java.lang.IllegalAccessException e) {
            throw new ConfigException("Unable to access execution class " + taskDefinition.getTaskClassName(), e, model.getLocation());
        }
    }

    private java.util.List getActiveAspects(BuildElement model) throws org.apache.ant.core.execution.ConfigException, org.apache.ant.core.execution.ExecutionException, org.apache.ant.core.execution.ClassIntrospectionException, org.apache.ant.core.execution.ConversionException {
        java.util.List activeAspects = new java.util.ArrayList();
        for (java.util.Iterator i = model.getAspectNames(); i.hasNext();) {
            java.lang.String aspectPrefix = ((java.lang.String) (i.next()));
            java.lang.Class aspectHandlerClass = ((java.lang.Class) (aspectHandlers.get(aspectPrefix)));
            if (aspectHandlerClass != null) {
                try {
                    AspectHandler aspectHandler = ((AspectHandler) (aspectHandlerClass.newInstance()));
                    ClassIntrospector introspector = getIntrospector(aspectHandlerClass);
                    ExecutionContext context = new ExecutionContext(this, eventSupport, model);
                    aspectHandler.setAspectContext(context);
                    java.util.Map aspectAttributes = model.getAspectAttributes(aspectPrefix);
                    for (java.util.Iterator j = aspectAttributes.keySet().iterator(); j.hasNext();) {
                        java.lang.String attributeName = ((java.lang.String) (j.next()));
                        java.lang.String attributeValue = ((java.lang.String) (aspectAttributes.get(attributeName)));
                        introspector.setAttribute(aspectHandler, attributeName, replacePropertyRefs(attributeValue));
                    }
                    activeAspects.add(aspectHandler);
                } catch (java.lang.InstantiationException e) {
                    throw new ConfigException("Unable to instantiate aspect handler class " + aspectHandlerClass, e);
                } catch (java.lang.IllegalAccessException e) {
                    throw new ConfigException("Unable to access aspect handler class " + aspectHandlerClass, e);
                }
            }
        }
        return activeAspects;
    }

    /**
     * Configure an element according to the given model.
     */
    private void configureElement(java.lang.Object element, TaskElement model) throws org.apache.ant.core.execution.ExecutionException, org.apache.ant.core.execution.ConfigException {
        if (element instanceof Task) {
            Task task = ((Task) (element));
            ExecutionContext context = new ExecutionContext(this, eventSupport, model);
            task.setTaskContext(context);
        }
        try {
            ClassIntrospector introspector = getIntrospector(element.getClass());
            java.util.List aspects = getActiveAspects(model);
            for (java.util.Iterator i = aspects.iterator(); i.hasNext();) {
                AspectHandler aspectHandler = ((AspectHandler) (i.next()));
                aspectHandler.beforeConfigElement(element);
            }
            // start by setting the attributes of this element
            for (java.util.Iterator i = model.getAttributeNames(); i.hasNext();) {
                java.lang.String attributeName = ((java.lang.String) (i.next()));
                java.lang.String attributeValue = model.getAttributeValue(attributeName);
                introspector.setAttribute(element, attributeName, replacePropertyRefs(attributeValue));
            }
            java.lang.String modelText = model.getText().trim();
            if (modelText.length() != 0) {
                introspector.addText(element, replacePropertyRefs(modelText));
            }
            // now do the nested elements
            for (java.util.Iterator i = model.getNestedElements(); i.hasNext();) {
                TaskElement nestedElementModel = ((TaskElement) (i.next()));
                if ((element instanceof TaskContainer) && (!introspector.supportsNestedElement(nestedElementModel.getType()))) {
                    Task nestedTask = configureTask(nestedElementModel);
                    TaskContainer container = ((TaskContainer) (element));
                    container.addTask(nestedTask);
                } else {
                    java.lang.Object nestedElement = introspector.createElement(element, nestedElementModel.getType());
                    configureElement(nestedElement, nestedElementModel);
                }
            }
            for (java.util.Iterator i = aspects.iterator(); i.hasNext();) {
                AspectHandler aspectHandler = ((AspectHandler) (i.next()));
                aspectHandler.afterConfigElement(element);
            }
        } catch (ClassIntrospectionException e) {
            throw new ExecutionException(e, model.getLocation());
        } catch (ConversionException e) {
            throw new ExecutionException(e, model.getLocation());
        }
    }

    /**
     * Run the tasks returned by the give iterator
     *
     * @param taskIterator
     * 		the iterator giving the tasks to execute
     */
    public void executeTasks(java.util.Iterator taskIterator) throws org.apache.ant.core.execution.ExecutionException, org.apache.ant.core.execution.ConfigException {
        TaskElement task = null;
        try {
            while (taskIterator.hasNext()) {
                task = ((TaskElement) (taskIterator.next()));
                try {
                    Task configuredTask = configureTask(task);
                    eventSupport.fireTaskStarted(this, task);
                    configuredTask.execute();
                } catch (ExecutionException e) {
                    if ((e.getLocation() == null) || (e.getLocation() == Location.UNKNOWN_LOCATION)) {
                        e.setLocation(task.getLocation());
                    }
                    throw e;
                } catch (ConfigException e) {
                    if ((e.getLocation() == null) || (e.getLocation() == Location.UNKNOWN_LOCATION)) {
                        e.setLocation(task.getLocation());
                    }
                    throw e;
                }
                eventSupport.fireTaskFinished(this, task, null);
            } 
        } catch (java.lang.RuntimeException e) {
            eventSupport.fireTaskFinished(this, task, e);
            throw e;
        }
    }

    private org.apache.ant.core.execution.ClassIntrospector getIntrospector(java.lang.Class c) {
        if (introspectors.containsKey(c)) {
            return ((ClassIntrospector) (introspectors.get(c)));
        }
        ClassIntrospector introspector = new ClassIntrospector(c, converters);
        introspectors.put(c, introspector);
        return introspector;
    }

    /**
     * Replace ${} style constructions in the given value with the string value of
     * the corresponding data types.
     *
     * @param value
     * 		the string to be scanned for property references.
     */
    public java.lang.String replacePropertyRefs(java.lang.String value) throws org.apache.ant.core.execution.ExecutionException {
        if (value == null) {
            return null;
        }
        java.util.List fragments = new java.util.ArrayList();
        java.util.List propertyRefs = new java.util.ArrayList();
        org.apache.ant.core.execution.ExecutionFrame.parsePropertyString(value, fragments, propertyRefs);
        java.lang.StringBuffer sb = new java.lang.StringBuffer();
        java.util.Iterator i = fragments.iterator();
        java.util.Iterator j = propertyRefs.iterator();
        while (i.hasNext()) {
            java.lang.String fragment = ((java.lang.String) (i.next()));
            if (fragment == null) {
                java.lang.String propertyName = ((java.lang.String) (j.next()));
                if (!isDataValueSet(propertyName)) {
                    throw new ExecutionException(("Property " + propertyName) + " has not been set");
                }
                fragment = getDataValue(propertyName).toString();
            }
            sb.append(fragment);
        } 
        return sb.toString();
    }

    /**
     * This method will parse a string containing ${value} style
     * property values into two list. The first list is a collection
     * of text fragments, while the other is a set of string property names
     * null entries in the first list indicate a property reference from the
     * second list.
     */
    public static void parsePropertyString(java.lang.String value, java.util.List fragments, java.util.List propertyRefs) throws org.apache.ant.core.execution.ExecutionException {
        int prev = 0;
        int pos;
        while ((pos = value.indexOf("$", prev)) >= 0) {
            if (pos > 0) {
                fragments.add(value.substring(prev, pos));
            }
            if (pos == (value.length() - 1)) {
                fragments.add("$");
                prev = pos + 1;
            } else if (value.charAt(pos + 1) != '{') {
                fragments.add(value.substring(pos + 1, pos + 2));
                prev = pos + 2;
            } else {
                int endName = value.indexOf('}', pos);
                if (endName < 0) {
                    throw new ExecutionException("Syntax error in property: " + value);
                }
                java.lang.String propertyName = value.substring(pos + 2, endName);
                fragments.add(null);
                propertyRefs.add(propertyName);
                prev = endName + 1;
            }
        } 
        if (prev < value.length()) {
            fragments.add(value.substring(prev));
        }
    }

    /**
     * Given a name of an object, get the frame relative from this frame that
     * contains that object.
     */
    public org.apache.ant.core.execution.ExecutionFrame getRelativeFrame(java.lang.String name) throws org.apache.ant.core.execution.ExecutionException {
        int index = name.lastIndexOf(":");
        if (index == (-1)) {
            return this;
        }
        org.apache.ant.core.execution.ExecutionFrame currentFrame = this;
        java.lang.String relativeFrameName = name.substring(0, index);
        java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(relativeFrameName, ":");
        while (tokenizer.hasMoreTokens()) {
            java.lang.String frameName = tokenizer.nextToken();
            currentFrame = currentFrame.getImportedFrame(frameName);
            if (currentFrame == null) {
                throw new ExecutionException(((("The project " + frameName) + " in ") + name) + " was not found");
            }
        } 
        return currentFrame;
    }

    /**
     * Get the name of an object in its frame
     */
    public java.lang.String getNameInFrame(java.lang.String name) {
        int index = name.lastIndexOf(":");
        if (index == (-1)) {
            return name;
        }
        return name.substring(index + 1);
    }

    /**
     * Set a value in this frame or any of its imported frames
     */
    public void setDataValue(java.lang.String name, java.lang.Object value) throws org.apache.ant.core.execution.ExecutionException {
        org.apache.ant.core.execution.ExecutionFrame frame = getRelativeFrame(name);
        frame.setDirectDataValue(getNameInFrame(name), value);
    }

    /**
     * Get a value from this frame or any imported frame
     */
    public java.lang.Object getDataValue(java.lang.String name) throws org.apache.ant.core.execution.ExecutionException {
        org.apache.ant.core.execution.ExecutionFrame frame = getRelativeFrame(name);
        return frame.getDirectDataValue(getNameInFrame(name));
    }

    /**
     * Set a value in this frame only
     */
    private void setDirectDataValue(java.lang.String name, java.lang.Object value) {
        dataValues.put(name, value);
    }

    /**
     * Get a value from this frame
     */
    private java.lang.Object getDirectDataValue(java.lang.String name) {
        return dataValues.get(name);
    }

    /**
     * Indicate if a data value has been set
     */
    public boolean isDataValueSet(java.lang.String name) throws org.apache.ant.core.execution.ExecutionException {
        org.apache.ant.core.execution.ExecutionFrame frame = getRelativeFrame(name);
        return frame.isDirectDataValueSet(getNameInFrame(name));
    }

    /**
     * Indicate if a data value has been set in this frame
     */
    private boolean isDirectDataValueSet(java.lang.String name) {
        return dataValues.containsKey(name);
    }

    public void runBuild(java.util.List targetNames) throws org.apache.ant.core.execution.AntException {
        java.lang.Throwable buildFailureCause = null;
        try {
            eventSupport.fireBuildStarted(this, project);
            initialize();
            if (targetNames.isEmpty()) {
                // we just execute the default target if any
                java.lang.String defaultTarget = project.getDefaultTarget();
                if (defaultTarget != null) {
                    executeTarget(defaultTarget);
                }
            } else {
                for (java.util.Iterator i = targetNames.iterator(); i.hasNext();) {
                    executeTarget(((java.lang.String) (i.next())));
                }
            }
            eventSupport.fireBuildFinished(this, project, null);
        } catch (java.lang.RuntimeException e) {
            buildFailureCause = e;
            throw e;
        } catch (AntException e) {
            buildFailureCause = e;
            throw e;
        } finally {
            eventSupport.fireBuildFinished(this, project, buildFailureCause);
        }
    }

    public void executeTarget(java.lang.String targetName) throws org.apache.ant.core.execution.ExecutionException, org.apache.ant.core.execution.ConfigException {
        // to execute a target we must determine its dependencies and
        // execute them in order.
        java.util.Map state = new java.util.HashMap();
        java.util.Stack visiting = new java.util.Stack();
        java.util.List dependencyOrder = new java.util.ArrayList();
        org.apache.ant.core.execution.ExecutionFrame startingFrame = getRelativeFrame(targetName);
        startingFrame.fillinDependencyOrder(getNameInFrame(targetName), dependencyOrder, state, visiting);
        // Now tell each frame to execute the targets required
        for (java.util.Iterator i = dependencyOrder.iterator(); i.hasNext();) {
            java.lang.String fullTargetName = ((java.lang.String) (i.next()));
            org.apache.ant.core.execution.ExecutionFrame frame = getRelativeFrame(fullTargetName);
            frame.executeTargetTasks(getNameInFrame(fullTargetName));
        }
    }
}