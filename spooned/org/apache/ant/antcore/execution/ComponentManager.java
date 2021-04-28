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
import org.apache.ant.antcore.antlib.AntLibDefinition;
import org.apache.ant.antcore.antlib.AntLibManager;
import org.apache.ant.antcore.antlib.AntLibrary;
import org.apache.ant.antcore.antlib.ComponentLibrary;
import org.apache.ant.antcore.antlib.DynamicLibrary;
import org.apache.ant.antcore.config.AntConfig;
import org.apache.ant.common.antlib.AntLibFactory;
import org.apache.ant.common.antlib.Converter;
import org.apache.ant.common.antlib.DeferredTask;
import org.apache.ant.common.antlib.ExecutionComponent;
import org.apache.ant.common.antlib.StandardLibFactory;
import org.apache.ant.common.antlib.Task;
import org.apache.ant.common.antlib.TaskContainer;
import org.apache.ant.common.event.MessageLevel;
import org.apache.ant.common.model.BuildElement;
import org.apache.ant.common.service.ComponentService;
import org.apache.ant.common.util.ExecutionException;
import org.apache.ant.common.util.Location;
import org.apache.ant.init.LoaderUtils;
/**
 * The instance of the ComponentServices made available by the core to the
 * ant libraries.
 *
 * @author Conor MacNeill
 * @created 27 January 2002
 */
public class ComponentManager implements org.apache.ant.common.service.ComponentService {
    /**
     * Type converters for this frame. Converters are used when configuring
     * Tasks to handle special type conversions.
     */
    private java.util.Map converters = new java.util.HashMap();

    /**
     * This is the set of libraries whose converters have been loaded
     */
    private java.util.Set loadedConverters = new java.util.HashSet();

    /**
     * The factory objects for each library, indexed by the library Id
     */
    private java.util.Map libFactories = new java.util.HashMap();

    /**
     * The Frame this service instance is working for
     */
    private org.apache.ant.antcore.execution.Frame frame;

    /**
     * The library manager instance used to configure libraries.
     */
    private org.apache.ant.antcore.antlib.AntLibManager libManager;

    /**
     * These are AntLibraries which have been loaded into this component
     * manager
     */
    private java.util.Map antLibraries = new java.util.HashMap();

    /**
     * dynamic libraries which have been defined
     */
    private java.util.Map dynamicLibraries;

    /**
     * The definitions which have been imported into this frame.
     */
    private java.util.Map definitions = new java.util.HashMap();

    /**
     * This map stores a list of additional paths for each library indexed
     * by the libraryId
     */
    private java.util.Map libPathsMap = new java.util.HashMap();

    /**
     * Reflector objects used to configure Tasks from the Task models.
     */
    private java.util.Map setters = new java.util.HashMap();

    /**
     * Constructor
     *
     * @param frame
     * 		the frame containing this context
     */
    protected ComponentManager(org.apache.ant.antcore.execution.Frame frame) {
        this.frame = frame;
        org.apache.ant.antcore.config.AntConfig config = frame.getConfig();
        libManager = new org.apache.ant.antcore.antlib.AntLibManager(config.isRemoteLibAllowed());
        dynamicLibraries = new java.util.HashMap();
        libPathsMap = new java.util.HashMap();
    }

    /**
     * Load a library or set of libraries from a location making them
     * available for use
     *
     * @param libLocation
     * 		the file or URL of the library location
     * @param importAll
     * 		if true all tasks are imported as the library is
     * 		loaded
     * @exception ExecutionException
     * 		if the library cannot be loaded
     */
    public void loadLib(java.lang.String libLocation, boolean importAll) throws org.apache.ant.common.util.ExecutionException {
        try {
            java.util.Map librarySpecs = new java.util.HashMap();
            libManager.loadLibs(librarySpecs, libLocation);
            libManager.configLibraries(frame.getInitConfig(), librarySpecs, antLibraries, libPathsMap);
            java.util.Iterator i = librarySpecs.keySet().iterator();
            while (i.hasNext()) {
                java.lang.String libraryId = ((java.lang.String) (i.next()));
                if (importAll || libraryId.startsWith(Constants.ANT_LIB_PREFIX)) {
                    importLibrary(libraryId);
                }
            } 
        } catch (java.net.MalformedURLException e) {
            throw new org.apache.ant.common.util.ExecutionException("Unable to load libraries from " + libLocation, e);
        }
    }

    /**
     * Experimental - define a new task
     *
     * @param taskName
     * 		the name by which this task will be referred
     * @param factory
     * 		the library factory object to create the task
     * 		instances
     * @param loader
     * 		the class loader to use to create the particular tasks
     * @param className
     * 		the name of the class implementing the task
     * @exception ExecutionException
     * 		if the task cannot be defined
     */
    public void taskdef(org.apache.ant.common.antlib.AntLibFactory factory, java.lang.ClassLoader loader, java.lang.String taskName, java.lang.String className) throws org.apache.ant.common.util.ExecutionException {
        defineComponent(factory, loader, ComponentLibrary.TASKDEF, taskName, className);
    }

    /**
     * Experimental - define a new type
     *
     * @param typeName
     * 		the name by which this type will be referred
     * @param factory
     * 		the library factory object to create the type
     * 		instances
     * @param loader
     * 		the class loader to use to create the particular types
     * @param className
     * 		the name of the class implementing the type
     * @exception ExecutionException
     * 		if the type cannot be defined
     */
    public void typedef(org.apache.ant.common.antlib.AntLibFactory factory, java.lang.ClassLoader loader, java.lang.String typeName, java.lang.String className) throws org.apache.ant.common.util.ExecutionException {
        defineComponent(factory, loader, ComponentLibrary.TYPEDEF, typeName, className);
    }

    /**
     * Add a library path for the given library
     *
     * @param libraryId
     * 		the unique id of the library for which an additional
     * 		path is being defined
     * @param libPath
     * 		the library path (usually a jar)
     * @exception ExecutionException
     * 		if the path cannot be specified
     */
    public void addLibPath(java.lang.String libraryId, java.net.URL libPath) throws org.apache.ant.common.util.ExecutionException {
        java.util.List libPaths = ((java.util.List) (libPathsMap.get(libraryId)));
        if (libPaths == null) {
            libPaths = new java.util.ArrayList();
            libPathsMap.put(libraryId, libPaths);
        }
        libPaths.add(libPath);
        // If this library already exists give it the new path now
        org.apache.ant.antcore.antlib.AntLibrary library = ((org.apache.ant.antcore.antlib.AntLibrary) (antLibraries.get(libraryId)));
        if (library != null) {
            libManager.addLibPath(library, libPath);
        }
    }

    /**
     * Import a complete library into the current execution frame
     *
     * @param libraryId
     * 		The id of the library to be imported
     * @exception ExecutionException
     * 		if the library cannot be imported
     */
    public void importLibrary(java.lang.String libraryId) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.antlib.AntLibrary library = ((org.apache.ant.antcore.antlib.AntLibrary) (antLibraries.get(libraryId)));
        if (library == null) {
            throw new org.apache.ant.common.util.ExecutionException(("Unable to import library " + libraryId) + " as it has not been loaded");
        }
        for (java.util.Iterator i = library.getDefinitionNames(); i.hasNext();) {
            java.lang.String defName = ((java.lang.String) (i.next()));
            importLibraryDef(library, defName, null);
        }
        addLibraryConverters(library);
    }

    /**
     * Import a single component from a library, optionally aliasing it to a
     * new name
     *
     * @param libraryId
     * 		the unique id of the library from which the
     * 		component is being imported
     * @param defName
     * 		the name of the component within its library
     * @param alias
     * 		the name under which this component will be used in the
     * 		build scripts. If this is null, the components default name is
     * 		used.
     * @exception ExecutionException
     * 		if the component cannot be imported
     */
    public void importComponent(java.lang.String libraryId, java.lang.String defName, java.lang.String alias) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.antlib.AntLibrary library = ((org.apache.ant.antcore.antlib.AntLibrary) (antLibraries.get(libraryId)));
        if (library == null) {
            throw new org.apache.ant.common.util.ExecutionException((("Unable to import component from " + "library \"") + libraryId) + "\" as it has not been loaded");
        }
        importLibraryDef(library, defName, alias);
        addLibraryConverters(library);
    }

    /**
     * Imports a component defined in a nother frame.
     *
     * @param relativeName
     * 		the qualified name of the component relative to
     * 		this execution frame
     * @param alias
     * 		the name under which this component will be used in the
     * 		build scripts. If this is null, the components default name is
     * 		used.
     * @exception ExecutionException
     * 		if the component cannot be imported
     */
    public void importFrameComponent(java.lang.String relativeName, java.lang.String alias) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.execution.ImportInfo definition = frame.getReferencedDefinition(relativeName);
        if (definition == null) {
            throw new org.apache.ant.common.util.ExecutionException("The reference \"relativeName\" does" + " not refer to a defined component");
        }
        java.lang.String label = alias;
        if (label == null) {
            label = frame.getNameInFrame(relativeName);
        }
        frame.log((((((("Adding referenced component <" + definition.getLocalName()) + "> as <") + label) + "> from library \"") + definition.getComponentLibrary().getLibraryId()) + "\", class: ") + definition.getClassName(), MessageLevel.MSG_DEBUG);
        definitions.put(label, definition);
    }

    /**
     * Create a component. The component will have a context but will not be
     * configured. It should be configured using the appropriate set methods
     * and then validated before being used.
     *
     * @param componentName
     * 		the name of the component
     * @return the created component. The return type of this method depends
    on the component type.
     * @exception ExecutionException
     * 		if the component cannot be created
     */
    public java.lang.Object createComponent(java.lang.String componentName) throws org.apache.ant.common.util.ExecutionException {
        return createComponent(componentName, null);
    }

    /**
     * Create a component given its class. The component will have a context
     * but will not be configured. It should be configured using the
     * appropriate set methods and then validated before being used.
     *
     * @param componentClass
     * 		the component's class
     * @param factory
     * 		the factory to create the component
     * @param loader
     * 		the classloader associated with the component
     * @param addTaskAdapter
     * 		whenther the returned component should be a
     * 		task, potentially being wrapped in an adapter
     * @param componentName
     * 		the name of the component type
     * @return the created component. The return type of this method depends
    on the component type.
     * @exception ExecutionException
     * 		if the component cannot be created
     */
    public java.lang.Object createComponent(org.apache.ant.common.antlib.AntLibFactory factory, java.lang.ClassLoader loader, java.lang.Class componentClass, boolean addTaskAdapter, java.lang.String componentName) throws org.apache.ant.common.util.ExecutionException {
        return createComponent(loader, factory, componentClass, componentName, componentName, addTaskAdapter, null);
    }

    /**
     * Get the collection ov converters currently configured
     *
     * @return A map of converter instances indexed on the class they can
    convert
     */
    protected java.util.Map getConverters() {
        return converters;
    }

    /**
     * Get the collection of Ant Libraries defined for this frame Gets the
     * factory object for the given library
     *
     * @param componentLibrary
     * 		the compnent library for which a factory
     * 		objetc is required
     * @return the library's factory object
     * @exception ExecutionException
     * 		if the factory cannot be created
     */
    protected org.apache.ant.common.antlib.AntLibFactory getLibFactory(org.apache.ant.antcore.antlib.ComponentLibrary componentLibrary) throws org.apache.ant.common.util.ExecutionException {
        java.lang.String libraryId = componentLibrary.getLibraryId();
        if (libFactories.containsKey(libraryId)) {
            return ((org.apache.ant.common.antlib.AntLibFactory) (libFactories.get(libraryId)));
        }
        org.apache.ant.antcore.execution.ExecutionContext context = new org.apache.ant.antcore.execution.ExecutionContext(frame, null, org.apache.ant.common.util.Location.UNKNOWN_LOCATION);
        org.apache.ant.common.antlib.AntLibFactory libFactory = componentLibrary.getFactory(context);
        if (libFactory == null) {
            libFactory = new org.apache.ant.common.antlib.StandardLibFactory();
        }
        libFactories.put(libraryId, libFactory);
        return libFactory;
    }

    /**
     * Get an imported definition from the component manager
     *
     * @param name
     * 		the name under which the component has been imported
     * @return the ImportInfo object detailing the import's library and
    other details
     */
    protected org.apache.ant.antcore.execution.ImportInfo getDefinition(java.lang.String name) {
        return ((org.apache.ant.antcore.execution.ImportInfo) (definitions.get(name)));
    }

    /**
     * Create a component from a build model
     *
     * @param model
     * 		the build model representing the component and its
     * 		configuration
     * @return the configured component
     * @exception ExecutionException
     * 		if there is a problem creating or
     * 		configuring the component
     */
    protected java.lang.Object createComponent(org.apache.ant.common.model.BuildElement model) throws org.apache.ant.common.util.ExecutionException {
        java.lang.String componentName = model.getType();
        return createComponent(componentName, model);
    }

    /**
     * Create a component.
     *
     * @param componentName
     * 		the name of the component which is used to
     * 		select the object type to be created
     * @param model
     * 		the build model of the component. If this is null, the
     * 		component is created but not configured.
     * @return the configured component
     * @exception ExecutionException
     * 		if there is a problem creating or
     * 		configuring the component
     */
    protected java.lang.Object createComponent(java.lang.String componentName, org.apache.ant.common.model.BuildElement model) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.common.util.Location location = org.apache.ant.common.util.Location.UNKNOWN_LOCATION;
        if (model != null) {
            location = model.getLocation();
        }
        org.apache.ant.antcore.execution.ImportInfo definition = getDefinition(componentName);
        if (definition == null) {
            throw new org.apache.ant.common.util.ExecutionException(("There is no definition of the <" + componentName) + "> component");
        }
        java.lang.String className = definition.getClassName();
        org.apache.ant.antcore.antlib.ComponentLibrary componentLibrary = definition.getComponentLibrary();
        boolean isTask = definition.getDefinitionType() == org.apache.ant.antcore.antlib.AntLibrary.TASKDEF;
        java.lang.String localName = definition.getLocalName();
        try {
            java.lang.ClassLoader componentLoader = componentLibrary.getClassLoader();
            java.lang.Class componentClass = java.lang.Class.forName(className, true, componentLoader);
            org.apache.ant.common.antlib.AntLibFactory libFactory = getLibFactory(componentLibrary);
            return createComponent(componentLoader, libFactory, componentClass, componentName, localName, isTask, model);
        } catch (java.lang.ClassNotFoundException e) {
            throw new org.apache.ant.common.util.ExecutionException(((("Class " + className) + " for component <") + componentName) + "> was not found", e, location);
        } catch (java.lang.NoClassDefFoundError e) {
            throw new org.apache.ant.common.util.ExecutionException((("Could not load a dependent class (" + e.getMessage()) + ") for component ") + componentName, e, location);
        } catch (org.apache.ant.common.util.ExecutionException e) {
            e.setLocation(model.getLocation(), false);
            throw e;
        }
    }

    /**
     * Import a single component from the given library
     *
     * @param library
     * 		the library which provides the component
     * @param defName
     * 		the name of the component in the library
     * @param alias
     * 		the name to be used for the component in build files. If
     * 		this is null, the component's name within its library is used.
     */
    protected void importLibraryDef(org.apache.ant.antcore.antlib.ComponentLibrary library, java.lang.String defName, java.lang.String alias) {
        java.lang.String label = alias;
        if (label == null) {
            label = defName;
        }
        org.apache.ant.antcore.antlib.AntLibDefinition libDef = library.getDefinition(defName);
        frame.log((((((("Adding component <" + defName) + "> as <") + label) + "> from library \"") + library.getLibraryId()) + "\", class: ") + libDef.getClassName(), MessageLevel.MSG_DEBUG);
        definitions.put(label, new org.apache.ant.antcore.execution.ImportInfo(library, libDef));
    }

    /**
     * Gets the setter for the given class
     *
     * @param c
     * 		the class for which the reflector is desired
     * @return the reflector
     */
    private org.apache.ant.antcore.execution.Setter getSetter(java.lang.Class c) {
        if (setters.containsKey(c)) {
            return ((org.apache.ant.antcore.execution.Setter) (setters.get(c)));
        }
        org.apache.ant.antcore.execution.Setter setter = null;
        if (org.apache.ant.common.antlib.DeferredTask.class.isAssignableFrom(c)) {
            setter = new org.apache.ant.antcore.execution.DeferredSetter();
        } else {
            org.apache.ant.antcore.execution.ClassIntrospector introspector = new org.apache.ant.antcore.execution.ClassIntrospector(c, getConverters());
            setter = introspector.getReflector();
        }
        setters.put(c, setter);
        return setter;
    }

    /**
     * Create a component - handles all the variations
     *
     * @param loader
     * 		the component's classloader
     * @param componentClass
     * 		The class of the component.
     * @param componentName
     * 		The component's name in the global context
     * @param addTaskAdapter
     * 		whether the component should add a Task adapter
     * 		to make this component a Task.
     * @param localName
     * 		The name of the component within its library
     * @param model
     * 		the BuildElement model of the component's configuration
     * @param factory
     * 		the facrtory object used to create the component
     * @return the required component potentially wrapped in a wrapper
    object.
     * @exception ExecutionException
     * 		if the component cannot be created
     */
    private java.lang.Object createComponent(java.lang.ClassLoader loader, org.apache.ant.common.antlib.AntLibFactory factory, java.lang.Class componentClass, java.lang.String componentName, java.lang.String localName, boolean addTaskAdapter, org.apache.ant.common.model.BuildElement model) throws org.apache.ant.common.util.ExecutionException {
        // set the location to unknown unless we have a build model to use
        org.apache.ant.common.util.Location location = org.apache.ant.common.util.Location.UNKNOWN_LOCATION;
        if (model != null) {
            location = model.getLocation();
        }
        try {
            // create the component using the factory
            java.lang.Object component = factory.createComponent(componentClass, localName);
            // wrap the component in an adapter if required.
            org.apache.ant.common.antlib.ExecutionComponent execComponent = null;
            if (addTaskAdapter) {
                if (component instanceof org.apache.ant.common.antlib.Task) {
                    execComponent = ((org.apache.ant.common.antlib.Task) (component));
                } else {
                    execComponent = new org.apache.ant.antcore.execution.TaskAdapter(componentName, component);
                }
            } else if (component instanceof org.apache.ant.common.antlib.ExecutionComponent) {
                execComponent = ((org.apache.ant.common.antlib.ExecutionComponent) (component));
            }
            // set the context loader to that for the component
            java.lang.ClassLoader currentLoader = org.apache.ant.init.LoaderUtils.setContextLoader(loader);
            // if the component is an execution component create a context and
            // initialise the component with it.
            if (execComponent != null) {
                org.apache.ant.antcore.execution.ExecutionContext context = new org.apache.ant.antcore.execution.ExecutionContext(frame, execComponent, location);
                context.setClassLoader(loader);
                execComponent.init(context, componentName);
            }
            // if we have a model, use it to configure the component. Otherwise
            // the caller is expected to configure thre object
            if (model != null) {
                configureElement(factory, component, model);
                // if the component is an execution component and we have a
                // model, validate it
                if (execComponent != null) {
                    execComponent.validateComponent();
                }
            }
            // reset the loader
            org.apache.ant.init.LoaderUtils.setContextLoader(currentLoader);
            // if we have an execution component, potentially a wrapper,
            // return it otherwise the component directly
            if (execComponent != null) {
                return execComponent;
            } else {
                return component;
            }
        } catch (java.lang.InstantiationException e) {
            throw new org.apache.ant.common.util.ExecutionException((((("Unable to instantiate component " + "class ") + componentClass.getName()) + " for component <") + componentName) + ">", e, location);
        } catch (java.lang.IllegalAccessException e) {
            throw new org.apache.ant.common.util.ExecutionException(((("Unable to access task class " + componentClass.getName()) + " for component <") + componentName) + ">", e, location);
        } catch (org.apache.ant.common.util.ExecutionException e) {
            e.setLocation(location, false);
            throw e;
        } catch (java.lang.RuntimeException e) {
            throw new org.apache.ant.common.util.ExecutionException(e, location);
        }
    }

    /**
     * Create an instance of a type given its required class
     *
     * @param typeClass
     * 		the class from which the instance should be created
     * @param model
     * 		the model describing the required configuration of the
     * 		instance
     * @param libFactory
     * 		the factory object of the typeClass's Ant library
     * @param localName
     * 		the name of the type within its Ant library
     * @return an instance of the given class appropriately configured
     * @exception ExecutionException
     * 		if there is a problem creating the type
     * 		instance
     */
    private java.lang.Object createTypeInstance(java.lang.Class typeClass, org.apache.ant.common.antlib.AntLibFactory libFactory, org.apache.ant.common.model.BuildElement model, java.lang.String localName) throws org.apache.ant.common.util.ExecutionException {
        try {
            java.lang.Object typeInstance = libFactory.createComponent(typeClass, localName);
            if (typeInstance instanceof org.apache.ant.common.antlib.ExecutionComponent) {
                org.apache.ant.common.antlib.ExecutionComponent component = ((org.apache.ant.common.antlib.ExecutionComponent) (typeInstance));
                org.apache.ant.antcore.execution.ExecutionContext context = new org.apache.ant.antcore.execution.ExecutionContext(frame, component, model.getLocation());
                component.init(context, localName);
                configureElement(libFactory, typeInstance, model);
                component.validateComponent();
            } else {
                configureElement(libFactory, typeInstance, model);
            }
            return typeInstance;
        } catch (java.lang.InstantiationException e) {
            throw new org.apache.ant.common.util.ExecutionException(((("Unable to instantiate type class " + typeClass.getName()) + " for type <") + model.getType()) + ">", e, model.getLocation());
        } catch (java.lang.IllegalAccessException e) {
            throw new org.apache.ant.common.util.ExecutionException(((("Unable to access type class " + typeClass.getName()) + " for type <") + model.getType()) + ">", e, model.getLocation());
        } catch (org.apache.ant.common.util.ExecutionException e) {
            e.setLocation(model.getLocation(), false);
            throw e;
        } catch (java.lang.RuntimeException e) {
            throw new org.apache.ant.common.util.ExecutionException(e, model.getLocation());
        }
    }

    /**
     * Create and add a nested element
     *
     * @param setter
     * 		The Setter instance for the container element
     * @param element
     * 		the container element in which the nested element will
     * 		be created
     * @param model
     * 		the model of the nested element
     * @param factory
     * 		Ant Library factory associated with the element to
     * 		which the attribute is to be added.
     * @exception ExecutionException
     * 		if the nested element cannot be created
     */
    private void addNestedElement(org.apache.ant.common.antlib.AntLibFactory factory, org.apache.ant.antcore.execution.Setter setter, java.lang.Object element, org.apache.ant.common.model.BuildElement model) throws org.apache.ant.common.util.ExecutionException {
        java.lang.String nestedElementName = model.getType();
        java.lang.Class nestedType = setter.getType(nestedElementName);
        // is there a polymorph indicator - look in Ant aspects
        java.lang.String typeName = model.getAspectValue(Constants.ANT_ASPECT, "type");
        java.lang.String refId = model.getAspectValue(Constants.ANT_ASPECT, "refid");
        if ((refId != null) && (typeName != null)) {
            throw new org.apache.ant.common.util.ExecutionException(((("Only one of " + Constants.ANT_ASPECT) + ":type and ") + Constants.ANT_ASPECT) + ":refid may be specified at a time", model.getLocation());
        }
        java.lang.Object typeInstance = null;
        if (typeName != null) {
            // the build file has specified the actual type of the element.
            // we need to look up that type and use it
            typeInstance = createComponent(typeName, model);
        } else if (refId != null) {
            // We have a reference to an existing instance. Need to check if
            // it is compatible with the type expected by the nested element's
            // adder method
            typeInstance = frame.getDataValue(refId);
            if ((model.getAttributeNames().hasNext() || model.getNestedElements().hasNext()) || (model.getText().length() != 0)) {
                throw new org.apache.ant.common.util.ExecutionException((("Element <" + nestedElementName) + "> is defined by reference and hence may not specify ") + "any attributes, nested elements or content", model.getLocation());
            }
            if (typeInstance == null) {
                throw new org.apache.ant.common.util.ExecutionException(("The given ant:refid value '" + refId) + "' is not defined", model.getLocation());
            }
        } else if (nestedType != null) {
            // We need to create an instance of the class expected by the nested
            // element's adder method if that is possible
            if (nestedType.isInterface()) {
                throw new org.apache.ant.common.util.ExecutionException(((("No element can be created for " + "nested element <") + nestedElementName) + ">. Please ") + "provide a value by reference or specify the value type", model.getLocation());
            }
            typeInstance = createTypeInstance(nestedType, factory, model, null);
        } else {
            throw new org.apache.ant.common.util.ExecutionException((((("The type of the <" + nestedElementName) + "> nested element is not known. ") + "Please specify by the type using the \"ant:type\" ") + "attribute or provide a reference to an instance with ") + "the \"ant:id\" attribute");
        }
        // is the typeInstance compatible with the type expected
        // by the element's add method
        if (!nestedType.isInstance(typeInstance)) {
            if (refId != null) {
                throw new org.apache.ant.common.util.ExecutionException(((("The value specified by refId " + refId) + " is not compatible with the <") + nestedElementName) + "> nested element", model.getLocation());
            } else if (typeName != null) {
                throw new org.apache.ant.common.util.ExecutionException(((("The type " + typeName) + " is not compatible with the <") + nestedElementName) + "> nested element", model.getLocation());
            }
        }
        setter.addElement(element, nestedElementName, typeInstance);
    }

    /**
     * Create a nested element for the given object according to the model.
     *
     * @param setter
     * 		the Setter instance of the container object
     * @param element
     * 		the container object for which a nested element is
     * 		required.
     * @param model
     * 		the build model for the nestd element
     * @param factory
     * 		Ant Library factory associated with the element
     * 		creating the nested element
     * @exception ExecutionException
     * 		if the nested element cannot be
     * 		created.
     */
    private void createNestedElement(org.apache.ant.common.antlib.AntLibFactory factory, org.apache.ant.antcore.execution.Setter setter, java.lang.Object element, org.apache.ant.common.model.BuildElement model) throws org.apache.ant.common.util.ExecutionException {
        java.lang.String nestedElementName = model.getType();
        try {
            java.lang.Object nestedElement = setter.createElement(element, nestedElementName);
            factory.registerCreatedElement(nestedElement);
            if (nestedElement instanceof org.apache.ant.common.antlib.ExecutionComponent) {
                org.apache.ant.common.antlib.ExecutionComponent component = ((org.apache.ant.common.antlib.ExecutionComponent) (nestedElement));
                org.apache.ant.antcore.execution.ExecutionContext context = new org.apache.ant.antcore.execution.ExecutionContext(frame, component, model.getLocation());
                component.init(context, nestedElementName);
                configureElement(factory, nestedElement, model);
                component.validateComponent();
            } else {
                configureElement(factory, nestedElement, model);
            }
        } catch (org.apache.ant.common.util.ExecutionException e) {
            e.setLocation(model.getLocation(), false);
            throw e;
        } catch (java.lang.RuntimeException e) {
            throw new org.apache.ant.common.util.ExecutionException(e, model.getLocation());
        }
    }

    /**
     * Configure an element according to the given model.
     *
     * @param element
     * 		the object to be configured
     * @param model
     * 		the BuildElement describing the object in the build file
     * @param factory
     * 		Ant Library factory associated with the element being
     * 		configured
     * @exception ExecutionException
     * 		if the element cannot be configured
     */
    private void configureElement(org.apache.ant.common.antlib.AntLibFactory factory, java.lang.Object element, org.apache.ant.common.model.BuildElement model) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.execution.Setter setter = getSetter(element.getClass());
        // start by setting the attributes of this element
        for (java.util.Iterator i = model.getAttributeNames(); i.hasNext();) {
            java.lang.String attributeName = ((java.lang.String) (i.next()));
            java.lang.String attributeValue = model.getAttributeValue(attributeName);
            if (!setter.supportsAttribute(attributeName)) {
                throw new org.apache.ant.common.util.ExecutionException(((model.getType() + " does not support the \"") + attributeName) + "\" attribute", model.getLocation());
            }
            setter.setAttribute(element, attributeName, frame.replacePropertyRefs(attributeValue));
        }
        java.lang.String modelText = model.getText().trim();
        if (modelText.length() != 0) {
            if (!setter.supportsText()) {
                throw new org.apache.ant.common.util.ExecutionException(model.getType() + " does not support content", model.getLocation());
            }
            setter.addText(element, frame.replacePropertyRefs(modelText));
        }
        // now do the nested elements
        for (java.util.Iterator i = model.getNestedElements(); i.hasNext();) {
            org.apache.ant.common.model.BuildElement nestedElementModel = ((org.apache.ant.common.model.BuildElement) (i.next()));
            java.lang.String nestedElementName = nestedElementModel.getType();
            org.apache.ant.antcore.execution.ImportInfo info = getDefinition(nestedElementName);
            if ((((element instanceof org.apache.ant.common.antlib.TaskContainer) && (info != null)) && (info.getDefinitionType() == org.apache.ant.antcore.antlib.AntLibrary.TASKDEF)) && (!setter.supportsNestedElement(nestedElementName))) {
                // it is a nested task
                org.apache.ant.common.antlib.Task nestedTask = ((org.apache.ant.common.antlib.Task) (createComponent(nestedElementModel)));
                org.apache.ant.common.antlib.TaskContainer container = ((org.apache.ant.common.antlib.TaskContainer) (element));
                container.addNestedTask(nestedTask);
            } else if (setter.supportsNestedAdder(nestedElementName)) {
                addNestedElement(factory, setter, element, nestedElementModel);
            } else if (setter.supportsNestedCreator(nestedElementName)) {
                createNestedElement(factory, setter, element, nestedElementModel);
            } else {
                throw new org.apache.ant.common.util.ExecutionException(((model.getType() + " does not support the \"") + nestedElementName) + "\" nested element", nestedElementModel.getLocation());
            }
        }
    }

    /**
     * Define a new component
     *
     * @param componentName
     * 		the name this component will take
     * @param defType
     * 		the type of component being defined
     * @param factory
     * 		the library factory object to create the component
     * 		instances
     * @param loader
     * 		the class loader to use to create the particular
     * 		components
     * @param className
     * 		the name of the class implementing the component
     * @exception ExecutionException
     * 		if the component cannot be defined
     */
    private void defineComponent(org.apache.ant.common.antlib.AntLibFactory factory, java.lang.ClassLoader loader, int defType, java.lang.String componentName, java.lang.String className) throws org.apache.ant.common.util.ExecutionException {
        org.apache.ant.antcore.antlib.DynamicLibrary dynamicLibrary = new org.apache.ant.antcore.antlib.DynamicLibrary(factory, loader);
        dynamicLibrary.addComponent(defType, componentName, className);
        dynamicLibraries.put(dynamicLibrary.getLibraryId(), dynamicLibrary);
        importLibraryDef(dynamicLibrary, componentName, null);
    }

    /**
     * Add the converters from the given library to those managed by this
     * frame.
     *
     * @param library
     * 		the library from which the converters are required
     * @exception ExecutionException
     * 		if a converter defined in the library
     * 		cannot be instantiated
     */
    private void addLibraryConverters(org.apache.ant.antcore.antlib.AntLibrary library) throws org.apache.ant.common.util.ExecutionException {
        if ((!library.hasConverters()) || loadedConverters.contains(library.getLibraryId())) {
            return;
        }
        java.lang.String className = null;
        try {
            org.apache.ant.common.antlib.AntLibFactory libFactory = getLibFactory(library);
            java.lang.ClassLoader converterLoader = library.getClassLoader();
            for (java.util.Iterator i = library.getConverterClassNames(); i.hasNext();) {
                className = ((java.lang.String) (i.next()));
                java.lang.Class converterClass = java.lang.Class.forName(className, true, converterLoader);
                if (!org.apache.ant.common.antlib.Converter.class.isAssignableFrom(converterClass)) {
                    throw new org.apache.ant.common.util.ExecutionException(((("In Ant library \"" + library.getLibraryId()) + "\" the converter class ") + converterClass.getName()) + " does not implement the Converter interface");
                }
                org.apache.ant.common.antlib.Converter converter = libFactory.createConverter(converterClass);
                org.apache.ant.antcore.execution.ExecutionContext context = new org.apache.ant.antcore.execution.ExecutionContext(frame, null, org.apache.ant.common.util.Location.UNKNOWN_LOCATION);
                converter.init(context);
                java.lang.Class[] converterTypes = converter.getTypes();
                for (int j = 0; j < converterTypes.length; ++j) {
                    converters.put(converterTypes[j], converter);
                }
            }
            loadedConverters.add(library.getLibraryId());
        } catch (java.lang.ClassNotFoundException e) {
            throw new org.apache.ant.common.util.ExecutionException(((("In Ant library \"" + library.getLibraryId()) + "\" converter class ") + className) + " was not found", e);
        } catch (java.lang.NoClassDefFoundError e) {
            throw new org.apache.ant.common.util.ExecutionException((((("In Ant library \"" + library.getLibraryId()) + "\" could not load a dependent class (") + e.getMessage()) + ") for converter ") + className);
        } catch (java.lang.InstantiationException e) {
            throw new org.apache.ant.common.util.ExecutionException((("In Ant library \"" + library.getLibraryId()) + "\" unable to instantiate converter class ") + className, e);
        } catch (java.lang.IllegalAccessException e) {
            throw new org.apache.ant.common.util.ExecutionException((("In Ant library \"" + library.getLibraryId()) + "\" unable to access converter class ") + className, e);
        }
    }
}