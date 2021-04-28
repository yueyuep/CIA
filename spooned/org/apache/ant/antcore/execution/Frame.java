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
package org.apache.ant.antcore.execution;
import org.apache.ant.antcore.config.AntConfig;
import org.apache.ant.common.antlib.Task;
import org.apache.ant.common.event.BuildListener;
import org.apache.ant.common.event.MessageLevel;
import org.apache.ant.common.model.BuildElement;
import org.apache.ant.common.model.Project;
import org.apache.ant.common.model.Target;
import org.apache.ant.common.service.ComponentService;
import org.apache.ant.common.service.DataService;
import org.apache.ant.common.service.EventService;
import org.apache.ant.common.service.ExecService;
import org.apache.ant.common.service.FileService;
import org.apache.ant.common.service.InputService;
import org.apache.ant.common.service.MagicProperties;
import org.apache.ant.common.util.DemuxOutputReceiver;
import org.apache.ant.common.util.ExecutionException;
import org.apache.ant.common.util.FileUtils;
import org.apache.ant.init.InitConfig;
/**
 * An Frame maintains the state of a project during an execution. The Frame
 * contains the data values set by Ant tasks as they are executed, including
 * task definitions, property values, etc.
 *
 * @author Conor MacNeill
 * @created 14 January 2002
 */
public class Frame implements org.apache.ant.common.util.DemuxOutputReceiver {
    /**
     * the base dir of the project
     */
    private java.io.File baseDir;

    /**
     * The Project that this execution frame is processing
     */
    private org.apache.ant.common.model.Project project = null;

    /**
     * The referenced frames corresponding to the referenced projects
     */
    private java.util.Map referencedFrames = new java.util.HashMap();

    /**
     * The property overrides for the referenced frames. This map is indexed
     * by the reference names of the frame. Each entry is another Map of
     * property values indexed by their relative name.
     */
    private java.util.Map overrides = new java.util.HashMap();

    /**
     * The context of this execution. This contains all data object's created
     * by tasks that have been executed
     */
    private java.util.Map dataValues = new java.util.HashMap();

    /**
     * Ant's initialization configuration with information on the location of
     * Ant and its libraries.
     */
    private org.apache.ant.init.InitConfig initConfig;

    /**
     * BuildEvent support used to fire events and manage listeners
     */
    private org.apache.ant.antcore.execution.BuildEventSupport eventSupport = new org.apache.ant.antcore.execution.BuildEventSupport();

    /**
     * The services map is a map of service interface classes to instances
     * which provide the service.
     */
    private java.util.Map services = new java.util.HashMap();

    /**
     * The configuration to be used in this execution of Ant. It is formed
     * from the system, user and any runtime configs.
     */
    private org.apache.ant.antcore.config.AntConfig config;

    /**
     * The Data Service instance used by the frame for data management
     */
    private org.apache.ant.common.service.DataService dataService;

    /**
     * The execution file service instance
     */
    private org.apache.ant.common.service.FileService fileService;

    /**
     * the Component Manager used to manage the importing of library
     * components from the Ant libraries
     */
    private org.apache.ant.antcore.execution.ComponentManager componentManager;

    /**
     * The core's execution Service
     */
    private org.apache.ant.antcore.execution.CoreExecService execService;

    /**
     * Create an Execution Frame for the given project
     *
     * @param standardLibs
     * 		The libraries of tasks and types available to this
     * 		frame
     * @param config
     * 		the user config to use for this execution of Ant
     * @param initConfig
     * 		Ant's initialisation config
     * @exception ExecutionException
     * 		if a component of the library cannot be
     * 		imported
     */
    protected Frame(org.apache.ant.init.InitConfig initConfig, org.apache.ant.antcore.config.AntConfig config) throws org.apache.ant.common.util.ExecutionException {
        this.config = config;
        this.initConfig = initConfig;
    }

    /**
     * Replace ${} style constructions in the given value with the string
     * value of the corresponding data values in the frame
     *
     * @param value
     * 		the string to be scanned for property references.
     * @return the string with all property references replaced
     * @exception ExecutionException
     * 		if any of the properties do not exist
     */
    protected java.lang.String replacePropertyRefs(java.lang.String value) throws org.apache.ant.common.util.ExecutionException {
        return dataService.replacePropertyRefs(value);
    }

    /**
     * Sets the Project of the Frame
     *
     * @param project
     * 		The new Project value
     * @exception ExecutionException
     * 		if any required sub-frames cannot be
     * 		created and configured
     */
    protected void setProject(org.apache.ant.common.model.Project project) throws org.apache.ant.common.util.ExecutionException {
        this.project = project;
        referencedFrames.clear();
    }

    /**
     * get the name of the project associated with this frame.
     *
     * @return the project's name
     */
    protected java.lang.String getProjectName() {
        if (project != null) {
            return project.getName();
        }
        return null;
    }

    /**
     * Set a value in this frame or any of its imported frames.
     *
     * @param name
     * 		the name of the value
     * @param value
     * 		the actual value
     * @param mutable
     * 		if true, existing values can be changed
     * @exception ExecutionException
     * 		if the value name is invalid
     */
    protected void setDataValue(java.lang.String name, java.lang.Object value, boolean mutable) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.execution.Frame frame = getContainingFrame(name);
        if (frame == null) {
            setOverrideProperty(name, value, mutable);
            return;
        }
        if (frame == this) {
            if (dataValues.containsKey(name) && (!mutable)) {
                log("Ignoring oveeride for data value " + name, MessageLevel.MSG_VERBOSE);
            } else {
                dataValues.put(name, value);
            }
        } else {
            frame.setDataValue(getNameInFrame(name), value, mutable);
        }
    }

    /**
     * When a frame has not yet been referenced, this method is used
     * to set the initial properties for the frame when it is introduced.
     *
     * @param name
     * 		the name of the value
     * @param value
     * 		the actual value
     * @param mutable
     * 		if true, existing values can be changed
     * @exception ExecutionException
     * 		if attempting to override a property in
     * 		the current frame.
     */
    private void setOverrideProperty(java.lang.String name, java.lang.Object value, boolean mutable) throws org.apache.ant.common.util.ExecutionException {
        int refIndex = name.indexOf(Project.REF_DELIMITER);
        if (refIndex == (-1)) {
            throw new org.apache.ant.common.util.ExecutionException(("Property overrides can only be set" + " for properties in referenced projects - not ") + name);
        }
        java.lang.String firstFrameName = name.substring(0, refIndex);
        java.lang.String relativeName = name.substring(refIndex + Project.REF_DELIMITER.length());
        java.util.Map frameOverrides = ((java.util.Map) (overrides.get(firstFrameName)));
        if (frameOverrides == null) {
            frameOverrides = new java.util.HashMap();
            overrides.put(firstFrameName, frameOverrides);
        }
        if (mutable || (!frameOverrides.containsKey(relativeName))) {
            frameOverrides.put(relativeName, value);
        }
    }

    /**
     * Get a value which exists in the frame property overrides awaiting
     * the frame to be introduced.
     *
     * @param name
     * 		the name of the value
     * @return the value of the property or null if the property does not
    exist.
     * @exception ExecutionException
     * 		if attempting to get an override in
     * 		the current frame.
     */
    private java.lang.Object getOverrideProperty(java.lang.String name) throws org.apache.ant.common.util.ExecutionException {
        int refIndex = name.indexOf(Project.REF_DELIMITER);
        if (refIndex == (-1)) {
            throw new org.apache.ant.common.util.ExecutionException(("Property overrides can only be" + " returned for properties in referenced projects - not ") + name);
        }
        java.lang.String firstFrameName = name.substring(0, refIndex);
        java.lang.String relativeName = name.substring(refIndex + Project.REF_DELIMITER.length());
        java.util.Map frameOverrides = ((java.util.Map) (overrides.get(firstFrameName)));
        if (frameOverrides == null) {
            return null;
        }
        return frameOverrides.get(relativeName);
    }

    /**
     * Get a value which exists in the frame property overrides awaiting
     * the frame to be introduced.
     *
     * @param name
     * 		the name of the value
     * @return the value of the property or null if the property does not
    exist.
     * @exception ExecutionException
     * 		if attempting to check an override in
     * 		the current frame.
     */
    private boolean isOverrideSet(java.lang.String name) throws org.apache.ant.common.util.ExecutionException {
        int refIndex = name.indexOf(Project.REF_DELIMITER);
        if (refIndex == (-1)) {
            throw new org.apache.ant.common.util.ExecutionException(("Property overrides can only be" + " returned for properties in referenced projects - not ") + name);
        }
        java.lang.String firstFrameName = name.substring(0, refIndex);
        java.lang.String relativeName = name.substring(refIndex + Project.REF_DELIMITER.length());
        java.util.Map frameOverrides = ((java.util.Map) (overrides.get(firstFrameName)));
        if (frameOverrides == null) {
            return false;
        }
        return frameOverrides.containsKey(relativeName);
    }

    /**
     * Set the initial properties to be used when the frame starts execution
     *
     * @param properties
     * 		a Map of named properties which may in fact be any
     * 		object
     * @exception ExecutionException
     * 		if the properties cannot be set
     */
    protected void setInitialProperties(java.util.Map properties) throws org.apache.ant.common.util.ExecutionException {
        if (properties != null) {
            addProperties(properties);
        }
        // add in system properties
        addProperties(java.lang.System.getProperties());
    }

    /**
     * Set the values of various magic properties
     *
     * @exception ExecutionException
     * 		if the properties cannot be set
     */
    protected void setMagicProperties() throws org.apache.ant.common.util.ExecutionException {
        java.net.URL antHomeURL = initConfig.getAntHome();
        java.lang.String antHomeString = null;
        if (antHomeURL.getProtocol().equals("file")) {
            java.io.File antHome = new java.io.File(antHomeURL.getFile());
            antHomeString = antHome.getAbsolutePath();
        } else {
            antHomeString = antHomeURL.toString();
        }
        setDataValue(MagicProperties.ANT_HOME, antHomeString, true);
    }

    /**
     * Get a definition from a referenced frame
     *
     * @param definitionName
     * 		the name of the definition relative to this frame
     * @return the appropriate import info object from the referenced frame's
    imports
     * @exception ExecutionException
     * 		if the referenced definition cannot be
     * 		found
     */
    protected org.apache.ant.antcore.execution.ImportInfo getReferencedDefinition(java.lang.String definitionName) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.execution.Frame containingFrame = getContainingFrame(definitionName);
        java.lang.String localName = getNameInFrame(definitionName);
        if (containingFrame == null) {
            throw new org.apache.ant.common.util.ExecutionException((("There is no project corresponding " + "to the name \"") + definitionName) + "\"");
        }
        if (containingFrame == this) {
            return componentManager.getDefinition(localName);
        } else {
            return containingFrame.getReferencedDefinition(localName);
        }
    }

    /**
     * Gets the project model this frame is working with
     *
     * @return the project model
     */
    protected org.apache.ant.common.model.Project getProject() {
        return project;
    }

    /**
     * Get all the properties from the frame and any references frames. This
     * is an expensive operation since it must clone all of the property
     * stores in all frames
     *
     * @return a Map containing the frames properties indexed by their full
    name.
     */
    protected java.util.Map getAllProperties() {
        java.util.Map allProperties = new java.util.HashMap(dataValues);
        java.util.Iterator i = referencedFrames.keySet().iterator();
        while (i.hasNext()) {
            java.lang.String refName = ((java.lang.String) (i.next()));
            org.apache.ant.antcore.execution.Frame refFrame = getReferencedFrame(refName);
            java.util.Map refProperties = refFrame.getAllProperties();
            java.util.Iterator j = refProperties.keySet().iterator();
            while (j.hasNext()) {
                java.lang.String name = ((java.lang.String) (j.next()));
                java.lang.Object value = refProperties.get(name);
                allProperties.put((refName + org.apache.ant.common.model.Project.REF_DELIMITER) + name, value);
            } 
        } 
        return allProperties;
    }

    /**
     * Get the Ant initialization configuration for this frame.
     *
     * @return Ant's initialization configuration
     */
    protected org.apache.ant.init.InitConfig getInitConfig() {
        return initConfig;
    }

    /**
     * Get the config instance being used by this frame.
     *
     * @return the config associated with this frame.
     */
    protected org.apache.ant.antcore.config.AntConfig getConfig() {
        return config;
    }

    /**
     * Get the core's implementation of the given service interface.
     *
     * @param serviceInterfaceClass
     * 		the service interface for which an
     * 		implementation is require
     * @return the core's implementation of the service interface
     * @exception ExecutionException
     * 		if the core does not provide an
     * 		implementatin of the requested interface
     */
    protected java.lang.Object getCoreService(java.lang.Class serviceInterfaceClass) throws org.apache.ant.common.util.ExecutionException {
        java.lang.Object service = services.get(serviceInterfaceClass);
        if (service == null) {
            throw new org.apache.ant.common.util.ExecutionException("No service of interface class " + serviceInterfaceClass);
        }
        return service;
    }

    /**
     * Get the EventSupport instance for this frame. This tracks the build
     * listeners on this frame
     *
     * @return the EventSupport instance
     */
    protected org.apache.ant.antcore.execution.BuildEventSupport getEventSupport() {
        return eventSupport;
    }

    /**
     * Gets the baseDir of the Frame
     *
     * @return the baseDir value
     */
    protected java.io.File getBaseDir() {
        return baseDir;
    }

    /**
     * Get a referenced frame by its reference name
     *
     * @param referenceName
     * 		the name under which the frame was imported.
     * @return the Frame asscociated with the given reference name or null if
    there is no such project.
     */
    protected org.apache.ant.antcore.execution.Frame getReferencedFrame(java.lang.String referenceName) {
        return ((org.apache.ant.antcore.execution.Frame) (referencedFrames.get(referenceName)));
    }

    /**
     * Get the frames representing referenced projects.
     *
     * @return an iterator which returns the referenced ExeuctionFrames..
     */
    protected java.util.Iterator getReferencedFrames() {
        return referencedFrames.values().iterator();
    }

    /**
     * Get the name of an object in its frame
     *
     * @param fullname
     * 		The name of the object
     * @return the name of the object within its containing frame
     */
    protected java.lang.String getNameInFrame(java.lang.String fullname) {
        int index = fullname.lastIndexOf(Project.REF_DELIMITER);
        if (index == (-1)) {
            return fullname;
        }
        return fullname.substring(index + Project.REF_DELIMITER.length());
    }

    /**
     * Get a value from this frame or any imported frame
     *
     * @param name
     * 		the name of the data value - may contain reference
     * 		delimiters
     * @return the data value fetched from the appropriate frame
     * @exception ExecutionException
     * 		if the value is not defined
     */
    protected java.lang.Object getDataValue(java.lang.String name) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.execution.Frame frame = getContainingFrame(name);
        if (frame == null) {
            return getOverrideProperty(name);
        }
        if (frame == this) {
            return dataValues.get(name);
        } else {
            return frame.getDataValue(getNameInFrame(name));
        }
    }

    /**
     * Indicate if a data value has been set
     *
     * @param name
     * 		the name of the data value - may contain reference
     * 		delimiters
     * @return true if the value exists
     * @exception ExecutionException
     * 		if the containing frame for the value
     * 		does not exist
     */
    protected boolean isDataValueSet(java.lang.String name) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.execution.Frame frame = getContainingFrame(name);
        if (frame == null) {
            return isOverrideSet(name);
        }
        if (frame == this) {
            return dataValues.containsKey(name);
        } else {
            return frame.isDataValueSet(getNameInFrame(name));
        }
    }

    /**
     * Get the execution frame which contains, directly, the named element
     * where the name is relative to this frame
     *
     * @param elementName
     * 		The name of the element
     * @return the execution frame for the project that contains the given
    target
     */
    protected org.apache.ant.antcore.execution.Frame getContainingFrame(java.lang.String elementName) {
        int index = elementName.lastIndexOf(Project.REF_DELIMITER);
        if (index == (-1)) {
            return this;
        }
        org.apache.ant.antcore.execution.Frame currentFrame = this;
        java.lang.String relativeName = elementName.substring(0, index);
        java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(relativeName, org.apache.ant.common.model.Project.REF_DELIMITER);
        while (tokenizer.hasMoreTokens()) {
            java.lang.String refName = tokenizer.nextToken();
            currentFrame = currentFrame.getReferencedFrame(refName);
            if (currentFrame == null) {
                return null;
            }
        } 
        return currentFrame;
    }

    /**
     * Add a collection of properties to this frame
     *
     * @param properties
     * 		the collection of property values, indexed by their
     * 		names
     * @exception ExecutionException
     * 		if the frame cannot be created.
     */
    protected void addProperties(java.util.Map properties) throws org.apache.ant.common.util.ExecutionException {
        for (java.util.Iterator i = properties.keySet().iterator(); i.hasNext();) {
            java.lang.String name = ((java.lang.String) (i.next()));
            java.lang.Object value = properties.get(name);
            setDataValue(name, value, false);
        }
    }

    /**
     * Create a project reference.
     *
     * @param name
     * 		the name under which the project will be
     * 		referenced.
     * @param project
     * 		the project model.
     * @exception ExecutionException
     * 		if the project cannot be referenced.
     */
    protected void createProjectReference(java.lang.String name, org.apache.ant.common.model.Project project) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.execution.Frame referencedFrame = createFrame(project);
        // does the frame have any overrides?
        java.util.Map initialProperties = ((java.util.Map) (overrides.get(name)));
        if (initialProperties != null) {
            referencedFrame.setInitialProperties(initialProperties);
            overrides.remove(name);
        }
        referencedFrames.put(name, referencedFrame);
        referencedFrame.initialize();
    }

    /**
     * Create a new frame for a given project
     *
     * @param project
     * 		the project model the frame will deal with
     * @return an Frame ready to build the project
     * @exception ExecutionException
     * 		if the frame cannot be created.
     */
    protected org.apache.ant.antcore.execution.Frame createFrame(org.apache.ant.common.model.Project project) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.execution.Frame newFrame = new org.apache.ant.antcore.execution.Frame(initConfig, config);
        newFrame.setProject(project);
        for (java.util.Iterator j = eventSupport.getListeners(); j.hasNext();) {
            org.apache.ant.common.event.BuildListener listener = ((org.apache.ant.common.event.BuildListener) (j.next()));
            newFrame.addBuildListener(listener);
        }
        return newFrame;
    }

    /**
     * Log a message as a build event
     *
     * @param message
     * 		the message to be logged
     * @param level
     * 		the priority level of the message
     */
    protected void log(java.lang.String message, int level) {
        eventSupport.fireMessageLogged(project, message, level);
    }

    /**
     * Add a build listener to this execution frame
     *
     * @param listener
     * 		the listener to be added to the frame
     */
    protected void addBuildListener(org.apache.ant.common.event.BuildListener listener) {
        for (java.util.Iterator i = getReferencedFrames(); i.hasNext();) {
            org.apache.ant.antcore.execution.Frame referencedFrame = ((org.apache.ant.antcore.execution.Frame) (i.next()));
            referencedFrame.addBuildListener(listener);
        }
        eventSupport.addBuildListener(listener);
    }

    /**
     * Remove a build listener from the execution
     *
     * @param listener
     * 		the listener to be removed
     */
    protected void removeBuildListener(org.apache.ant.common.event.BuildListener listener) {
        for (java.util.Iterator i = getReferencedFrames(); i.hasNext();) {
            org.apache.ant.antcore.execution.Frame subFrame = ((org.apache.ant.antcore.execution.Frame) (i.next()));
            subFrame.removeBuildListener(listener);
        }
        eventSupport.removeBuildListener(listener);
    }

    /**
     * Run the given list of targets
     *
     * @param targets
     * 		a list of target names which are to be evaluated
     * @exception ExecutionException
     * 		if there is a problem in the build
     */
    protected void runBuild(java.util.List targets) throws org.apache.ant.common.util.ExecutionException {
        initialize();
        if (targets.isEmpty()) {
            // we just execute the default target if any
            java.lang.String defaultTarget = project.getDefaultTarget();
            if (defaultTarget != null) {
                log("Executing default target: " + defaultTarget, MessageLevel.MSG_DEBUG);
                executeTarget(defaultTarget);
            }
        } else {
            for (java.util.Iterator i = targets.iterator(); i.hasNext();) {
                java.lang.String targetName = ((java.lang.String) (i.next()));
                log("Executing target: " + targetName, MessageLevel.MSG_DEBUG);
                executeTarget(targetName);
            }
        }
    }

    /**
     * Given a fully qualified target name, this method returns the fully
     * qualified name of the project
     *
     * @param fullTargetName
     * 		the full qualified target name
     * @return the full name of the containing project
     */
    private java.lang.String getFullProjectName(java.lang.String fullTargetName) {
        int index = fullTargetName.lastIndexOf(Project.REF_DELIMITER);
        if (index == (-1)) {
            return null;
        }
        return fullTargetName.substring(0, index);
    }

    /**
     * Flatten the dependencies to the given target
     *
     * @param flattenedList
     * 		the List of targets that must be executed before
     * 		the given target
     * @param fullTargetName
     * 		the fully qualified name of the target
     * @exception ExecutionException
     * 		if the given target does not exist in the
     * 		project hierarchy
     */
    private void flattenDependency(java.util.List flattenedList, java.lang.String fullTargetName) throws org.apache.ant.common.util.ExecutionException {
        if (flattenedList.contains(fullTargetName)) {
            return;
        }
        java.lang.String fullProjectName = getFullProjectName(fullTargetName);
        org.apache.ant.antcore.execution.Frame frame = getContainingFrame(fullTargetName);
        java.lang.String localTargetName = getNameInFrame(fullTargetName);
        org.apache.ant.common.model.Target target = frame.getProject().getTarget(localTargetName);
        if (target == null) {
            throw new org.apache.ant.common.util.ExecutionException(("Target " + fullTargetName) + " does not exist");
        }
        for (java.util.Iterator i = target.getDependencies(); i.hasNext();) {
            java.lang.String localDependencyName = ((java.lang.String) (i.next()));
            java.lang.String fullDependencyName = localDependencyName;
            if (fullProjectName != null) {
                fullDependencyName = (fullProjectName + org.apache.ant.common.model.Project.REF_DELIMITER) + localDependencyName;
            }
            flattenDependency(flattenedList, fullDependencyName);
            if (!flattenedList.contains(fullDependencyName)) {
                flattenedList.add(fullDependencyName);
            }
        }
    }

    /**
     * get the list of dependent targets which must be evaluated for the
     * given target.
     *
     * @param fullTargetName
     * 		the full name (in reference space) of the
     * 		target
     * @return the flattened list of targets
     * @exception ExecutionException
     * 		if the given target could not be found
     */
    protected java.util.List getTargetDependencies(java.lang.String fullTargetName) throws org.apache.ant.common.util.ExecutionException {
        java.util.List flattenedList = new java.util.ArrayList();
        flattenDependency(flattenedList, fullTargetName);
        flattenedList.add(fullTargetName);
        return flattenedList;
    }

    /**
     * Execute the tasks of a target in this frame with the given name
     *
     * @param targetName
     * 		the name of the target whose tasks will be evaluated
     * @exception ExecutionException
     * 		if there is a problem executing the tasks
     * 		of the target
     */
    protected void executeTarget(java.lang.String targetName) throws org.apache.ant.common.util.ExecutionException {
        // to execute a target we must determine its dependencies and
        // execute them in order.
        // firstly build a list of fully qualified target names to execute.
        java.util.List dependencyOrder = getTargetDependencies(targetName);
        for (java.util.Iterator i = dependencyOrder.iterator(); i.hasNext();) {
            java.lang.String fullTargetName = ((java.lang.String) (i.next()));
            org.apache.ant.antcore.execution.Frame frame = getContainingFrame(fullTargetName);
            java.lang.String localTargetName = getNameInFrame(fullTargetName);
            frame.executeTargetTasks(localTargetName);
        }
    }

    /**
     * Run the tasks returned by the given iterator
     *
     * @param taskIterator
     * 		the iterator giving the tasks to execute
     * @exception ExecutionException
     * 		if there is execution problem while
     * 		executing tasks
     */
    protected void executeTasks(java.util.Iterator taskIterator) throws org.apache.ant.common.util.ExecutionException {
        while (taskIterator.hasNext()) {
            org.apache.ant.common.model.BuildElement model = ((org.apache.ant.common.model.BuildElement) (taskIterator.next()));
            // what sort of element is this.
            try {
                java.lang.Object component = componentManager.createComponent(model);
                if (component instanceof org.apache.ant.common.antlib.Task) {
                    execService.executeTask(((org.apache.ant.common.antlib.Task) (component)));
                } else {
                    java.lang.String typeId = model.getAspectValue(Constants.ANT_ASPECT, "id");
                    if (typeId != null) {
                        setDataValue(typeId, component, true);
                    }
                }
            } catch (org.apache.ant.common.util.ExecutionException e) {
                e.setLocation(model.getLocation(), false);
                throw e;
            } catch (java.lang.RuntimeException e) {
                org.apache.ant.common.util.ExecutionException ee = new org.apache.ant.common.util.ExecutionException(e, model.getLocation());
                throw ee;
            }
        } 
    }

    /**
     * Execute the given target's tasks. The target must be local to this
     * frame's project
     *
     * @param targetName
     * 		the name of the target within this frame that is to
     * 		be executed.
     * @exception ExecutionException
     * 		if there is a problem executing tasks
     */
    protected void executeTargetTasks(java.lang.String targetName) throws org.apache.ant.common.util.ExecutionException {
        java.lang.Throwable failureCause = null;
        org.apache.ant.common.model.Target target = project.getTarget(targetName);
        java.lang.String ifCondition = target.getIfCondition();
        java.lang.String unlessCondition = target.getUnlessCondition();
        if (ifCondition != null) {
            ifCondition = dataService.replacePropertyRefs(ifCondition.trim());
            if (!isDataValueSet(ifCondition)) {
                return;
            }
        }
        if (unlessCondition != null) {
            unlessCondition = dataService.replacePropertyRefs(unlessCondition.trim());
            if (isDataValueSet(unlessCondition)) {
                return;
            }
        }
        try {
            java.util.Iterator taskIterator = target.getTasks();
            eventSupport.fireTargetStarted(target);
            executeTasks(taskIterator);
        } catch (org.apache.ant.common.util.ExecutionException e) {
            e.setLocation(target.getLocation(), false);
            failureCause = e;
            throw e;
        } catch (java.lang.RuntimeException e) {
            org.apache.ant.common.util.ExecutionException ee = new org.apache.ant.common.util.ExecutionException(e, target.getLocation());
            failureCause = ee;
            throw ee;
        } finally {
            eventSupport.fireTargetFinished(target, failureCause);
        }
    }

    /**
     * Initialize the frame by executing the project level tasks if any
     *
     * @exception ExecutionException
     * 		if the top level tasks of the frame
     * 		failed
     */
    protected void initialize() throws org.apache.ant.common.util.ExecutionException {
        configureServices();
        setMagicProperties();
        determineBaseDir();
        try {
            // load system ant lib
            java.net.URL systemLibs = new java.net.URL(initConfig.getLibraryURL(), "syslibs/");
            componentManager.loadLib(systemLibs.toString(), true);
            // execute any config tasks
            executeTasks(config.getTasks());
            // now load other system libraries
            java.net.URL antLibs = new java.net.URL(initConfig.getLibraryURL(), "antlibs/");
            componentManager.loadLib(antLibs.toString(), false);
            executeTasks(project.getTasks());
        } catch (java.net.MalformedURLException e) {
            throw new org.apache.ant.common.util.ExecutionException("Unable to initialize antlibs", e);
        }
    }

    /**
     * Determine the base directory for each frame in the frame hierarchy
     *
     * @exception ExecutionException
     * 		if the base directories cannot be
     * 		determined
     */
    private void determineBaseDir() throws org.apache.ant.common.util.ExecutionException {
        if (isDataValueSet(MagicProperties.BASEDIR)) {
            baseDir = new java.io.File(getDataValue(MagicProperties.BASEDIR).toString());
        } else {
            java.net.URL projectURL = project.getSourceURL();
            if (projectURL.getProtocol().equals("file")) {
                java.io.File projectFile = new java.io.File(projectURL.getFile());
                java.io.File projectFileParent = projectFile.getParentFile();
                java.lang.String base = project.getBase();
                if (base == null) {
                    baseDir = projectFileParent;
                } else {
                    org.apache.ant.common.util.FileUtils fileUtils = org.apache.ant.common.util.FileUtils.newFileUtils();
                    baseDir = fileUtils.resolveFile(projectFileParent, base);
                }
            } else {
                baseDir = new java.io.File(".");
            }
        }
        setDataValue(MagicProperties.BASEDIR, baseDir.getAbsolutePath(), true);
    }

    /**
     * Configure the services that the frame makes available to its library
     * components
     */
    private void configureServices() {
        // create services and make them available in our services map
        fileService = new org.apache.ant.antcore.execution.CoreFileService(this);
        componentManager = new org.apache.ant.antcore.execution.ComponentManager(this);
        dataService = new org.apache.ant.antcore.execution.CoreDataService(this, config.isUnsetPropertiesAllowed());
        execService = new org.apache.ant.antcore.execution.CoreExecService(this);
        services.put(org.apache.ant.common.service.FileService.class, fileService);
        services.put(org.apache.ant.common.service.ComponentService.class, componentManager);
        services.put(org.apache.ant.common.service.DataService.class, dataService);
        services.put(org.apache.ant.common.service.EventService.class, new org.apache.ant.antcore.execution.CoreEventService(this));
        services.put(org.apache.ant.common.service.ExecService.class, execService);
        services.put(org.apache.ant.common.service.InputService.class, new org.apache.ant.antcore.execution.CoreInputService(this));
    }

    /**
     * Handle the content from a single thread. This method will be called by
     * the thread producing the content. The content is broken up into
     * separate lines
     *
     * @param line
     * 		the content produce by the current thread.
     * @param isErr
     * 		true if this content is from the thread's error stream.
     */
    public void threadOutput(java.lang.String line, boolean isErr) {
        eventSupport.threadOutput(line, isErr);
    }
}