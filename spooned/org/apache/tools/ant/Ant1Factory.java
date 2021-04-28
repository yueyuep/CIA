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
package org.apache.tools.ant;
import org.apache.ant.common.antlib.AntContext;
import org.apache.ant.common.antlib.Converter;
import org.apache.ant.common.antlib.StandardLibFactory;
import org.apache.ant.common.service.EventService;
import org.apache.ant.common.util.ExecutionException;
import org.apache.ant.init.LoaderUtils;
/**
 * The factory object for the Ant1 compatability Ant library
 *
 * @author Conor MacNeill
 * @created 31 January 2002
 */
public class Ant1Factory extends org.apache.ant.common.antlib.StandardLibFactory {
    /**
     * A Project instance associated with the factory - used in the creation
     * of tasks and types
     */
    private org.apache.tools.ant.Project project;

    /**
     * The Ant context for this factory
     */
    private org.apache.ant.common.antlib.AntContext context;

    /**
     * Initialise the factory
     *
     * @param context
     * 		the context for this factory to use to access core
     * 		services.
     * @exception ExecutionException
     * 		if the factory cannot be initialised.
     */
    public void init(org.apache.ant.common.antlib.AntContext context) throws org.apache.ant.common.util.ExecutionException {
        if (project != null) {
            return;
        }
        this.context = context;
        // set the system classpath. In Ant2, the system classpath will not
        // in general, have any useful information. For Ant1 compatability
        // we set it now to include the Ant1 facade classes
        java.lang.System.setProperty("java.class.path", getAnt1Classpath());
        project = new org.apache.tools.ant.Project(this);
        project.init(context);
        org.apache.ant.common.service.EventService eventService = ((org.apache.ant.common.service.EventService) (context.getCoreService(org.apache.ant.common.service.EventService.class)));
        eventService.addBuildListener(project);
    }

    /**
     * Create an instance of the given component class
     *
     * @param componentClass
     * 		the class for which an instance is required
     * @param localName
     * 		the name within the library under which the task is
     * 		defined
     * @return an instance of the required class
     * @exception InstantiationException
     * 		if the class cannot be instantiated
     * @exception IllegalAccessException
     * 		if the instance cannot be accessed
     * @exception ExecutionException
     * 		if there is a problem creating the task
     */
    public java.lang.Object createComponent(java.lang.Class componentClass, java.lang.String localName) throws java.lang.InstantiationException, java.lang.IllegalAccessException, org.apache.ant.common.util.ExecutionException {
        try {
            java.lang.reflect.Constructor constructor = null;
            // DataType can have a "no arg" constructor or take a single
            // Project argument.
            java.lang.Object component = null;
            try {
                constructor = componentClass.getConstructor(new java.lang.Class[0]);
                component = constructor.newInstance(new java.lang.Object[0]);
            } catch (java.lang.NoSuchMethodException nse) {
                constructor = componentClass.getConstructor(new java.lang.Class[]{ org.apache.tools.ant.Project.class });
                component = constructor.newInstance(new java.lang.Object[]{ project });
            }
            if (component instanceof org.apache.tools.ant.ProjectComponent) {
                ((org.apache.tools.ant.ProjectComponent) (component)).setProject(project);
            }
            return component;
        } catch (java.lang.reflect.InvocationTargetException ite) {
            java.lang.Throwable t = ite.getTargetException();
            java.lang.String msg = (("Could not create component of type: " + componentClass.getName()) + " due to ") + t;
            throw new org.apache.ant.common.util.ExecutionException(msg, t);
        } catch (java.lang.NoSuchMethodException e) {
            throw new org.apache.ant.common.util.ExecutionException(("Unable to find an appropriate " + "constructor for component ") + componentClass.getName(), e);
        }
    }

    /**
     * Create a converter.
     *
     * @param converterClass
     * 		the class of the converter.
     * @return an instance of the requested converter class
     * @exception InstantiationException
     * 		if the converter cannot be
     * 		instantiated
     * @exception IllegalAccessException
     * 		if the converter cannot be accessed
     * @exception ExecutionException
     * 		if the converter cannot be created
     */
    public org.apache.ant.common.antlib.Converter createConverter(java.lang.Class converterClass) throws java.lang.InstantiationException, java.lang.IllegalAccessException, org.apache.ant.common.util.ExecutionException {
        java.lang.reflect.Constructor c = null;
        org.apache.ant.common.antlib.Converter converter = null;
        try {
            try {
                c = converterClass.getConstructor(new java.lang.Class[0]);
                converter = ((org.apache.ant.common.antlib.Converter) (c.newInstance(new java.lang.Object[0])));
            } catch (java.lang.NoSuchMethodException nse) {
                c = converterClass.getConstructor(new java.lang.Class[]{ org.apache.tools.ant.Project.class });
                converter = ((org.apache.ant.common.antlib.Converter) (c.newInstance(new java.lang.Object[]{ project })));
            }
            return converter;
        } catch (java.lang.reflect.InvocationTargetException ite) {
            java.lang.Throwable t = ite.getTargetException();
            java.lang.String msg = (("Could not create converter of type: " + converterClass.getName()) + " due to ") + t;
            throw new org.apache.ant.common.util.ExecutionException(msg, t);
        } catch (java.lang.NoSuchMethodException e) {
            throw new org.apache.ant.common.util.ExecutionException(("Unable to find an appropriate " + "constructor for converter ") + converterClass.getName(), e);
        }
    }

    /**
     * Register an element which has been created as the result of calling a
     * create method.
     *
     * @param createdElement
     * 		the element that the component created
     * @exception ExecutionException
     * 		if there is a problem registering the
     * 		element
     */
    public void registerCreatedElement(java.lang.Object createdElement) throws org.apache.ant.common.util.ExecutionException {
        if (createdElement instanceof org.apache.tools.ant.ProjectComponent) {
            org.apache.tools.ant.ProjectComponent component = ((org.apache.tools.ant.ProjectComponent) (createdElement));
            component.setProject(project);
        }
    }

    /**
     * Get an Ant1 equivalent classpath
     *
     * @return an Ant1 suitable classpath
     */
    java.lang.String getAnt1Classpath() {
        java.lang.ClassLoader classLoader = getClass().getClassLoader();
        java.lang.String path = org.apache.ant.init.LoaderUtils.getClasspath(classLoader);
        return path;
    }
}