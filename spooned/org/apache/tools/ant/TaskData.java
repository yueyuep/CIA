/* Copyright (c) 2000 The Apache Software Foundation */
package org.apache.tools.ant;
import java.beans.*;
import java.lang.reflect.*;
import java.util.*;
/**
 * This class stores info about a bean's properties so that
 *  the actual bean can be instantiated at a later time. This data
 *  is used to store info about a task, since the actual
 *  task class might not be loaded until after parsing is completed.
 *
 * @see TaskProxy
 * @author <a href="mailto:mpfoemme@thoughtworks.com">Matthew Foemmel</a>
 */
public class TaskData {
    private org.apache.tools.ant.TaskProxy proxy;

    private java.lang.String location;

    private java.lang.String text;

    private java.util.Map properties;

    /**
     * Constructs a new TaskData under the specified task.
     */
    public TaskData(TaskProxy proxy) {
        this.proxy = proxy;
        this.location = null;
        this.properties = new java.util.HashMap();
    }

    /**
     * Returns the task proxy that this data is associated with.
     */
    public org.apache.tools.ant.TaskProxy getTaskProxy() {
        return proxy;
    }

    /**
     * Returns the location in the build fiole where this data was defined.
     */
    public java.lang.String getLocation() {
        return location;
    }

    /**
     * Returns the location in the build fiole where this data was defined.
     */
    public void setLocation(java.lang.String location) {
        this.location = location;
    }

    /**
     * Sets the text for this bean data, for cases where the bean is a simple
     *  type like String or int.
     */
    public void setText(java.lang.String text) {
        this.text = text;
    }

    /**
     * Sets the value of a property on the bean. Multiple properties can be
     *  added with the same name only if the property on the bean is an array.
     */
    public org.apache.tools.ant.TaskData addProperty(java.lang.String name) {
        org.apache.tools.ant.TaskData data = new org.apache.tools.ant.TaskData(proxy);
        getProperties(name).add(data);
        return data;
    }

    /**
     * Returns the list of property values for the specified name.
     */
    private java.util.List getProperties(java.lang.String name) {
        java.util.List result = ((java.util.List) (properties.get(name)));
        if (result == null) {
            result = new java.util.ArrayList();
            properties.put(name, result);
        }
        return result;
    }

    /**
     * Creates a new bean instance and initializes its properties.
     */
    public java.lang.Object createBean(java.lang.Class type) throws org.apache.tools.ant.BuildException {
        java.lang.Object bean = null;
        // See if an editor exists for this type
        java.beans.PropertyEditor editor = java.beans.PropertyEditorManager.findEditor(type);
        if (editor == null) {
            // We don't know how to handle text for types without editors
            if (text != null) {
                throw new BuildException(("Unexpected text \"" + text) + "\"", location);
            }
            try {
                bean = type.newInstance();
            } catch (java.lang.InstantiationException exc) {
                throw new AntException("Unable to instantiate " + type.getName(), exc);
            } catch (java.lang.IllegalAccessException exc) {
                throw new AntException("Unable to access constructor for " + type.getName(), exc);
            }
        } else {
            try {
                // Let the editor parse the text
                editor.setAsText(parseVariables(text));
            } catch (java.lang.NumberFormatException exc) {
                throw new BuildException(("\"" + text) + "\" is not a valid number", location);
            }
            bean = editor.getValue();
        }
        // Update the fields on the bean
        updateProperties(bean);
        return bean;
    }

    /**
     * Sets all of the property values on the bean.
     */
    private void updateProperties(java.lang.Object bean) throws org.apache.tools.ant.BuildException {
        // Call setProperty for each property that's been defined
        java.util.Iterator itr = properties.entrySet().iterator();
        while (itr.hasNext()) {
            java.util.Map.Entry entry = ((java.util.Map.Entry) (itr.next()));
            java.lang.String name = ((java.lang.String) (entry.getKey()));
            java.util.List values = ((java.util.List) (entry.getValue()));
            setProperty(bean, name, values);
        } 
    }

    /**
     * Finds the PropertyDescriptor for the specifed property and sets it.
     */
    private void setProperty(java.lang.Object bean, java.lang.String name, java.util.List value) throws org.apache.tools.ant.BuildException {
        java.beans.PropertyDescriptor[] descriptors = org.apache.tools.ant.TaskData.getPropertyDescriptors(bean.getClass());
        // Search for the property with the matching name
        for (int i = 0; i < descriptors.length; i++) {
            if (descriptors[i].getName().equals(name)) {
                org.apache.tools.ant.TaskData.setProperty(bean, descriptors[i], value);
                return;
            }
        }
        throw new BuildException(("Unexpected attribute \"" + name) + "\"", location);
    }

    /**
     * Sets a single property on a bean.
     */
    private static void setProperty(java.lang.Object obj, java.beans.PropertyDescriptor descriptor, java.util.List values) throws org.apache.tools.ant.BuildException {
        java.lang.Object value = null;
        java.lang.Class type = descriptor.getPropertyType();
        if (type.isArray()) {
            value = org.apache.tools.ant.TaskData.createBeans(type.getComponentType(), values);
        } else if (values.size() == 1) {
            org.apache.tools.ant.TaskData data = ((org.apache.tools.ant.TaskData) (values.get(0)));
            value = data.createBean(type);
        }
        try {
            descriptor.getWriteMethod().invoke(obj, new java.lang.Object[]{ value });
        } catch (java.lang.IllegalAccessException exc) {
            throw new AntException(("Unable to access write method for \"" + descriptor.getName()) + "\"", exc);
        } catch (java.lang.reflect.InvocationTargetException exc) {
            throw new AntException(("Unable to set property \"" + descriptor.getName()) + "\"", exc.getTargetException());
        }
    }

    /**
     * Creates a number of beans with the same type using the list of TaskData's
     */
    private static java.lang.Object[] createBeans(java.lang.Class type, java.util.List values) throws org.apache.tools.ant.BuildException {
        java.lang.Object[] beans = ((java.lang.Object[]) (java.lang.reflect.Array.newInstance(type, values.size())));
        int i = 0;
        java.util.Iterator itr = values.iterator();
        while (itr.hasNext()) {
            org.apache.tools.ant.TaskData data = ((org.apache.tools.ant.TaskData) (itr.next()));
            beans[i++] = data.createBean(type);
        } 
        return beans;
    }

    /**
     * Uses the Introspector class to lookup the property descriptors for the class.
     */
    private static java.beans.PropertyDescriptor[] getPropertyDescriptors(java.lang.Class type) {
        try {
            return java.beans.Introspector.getBeanInfo(type, java.lang.Object.class).getPropertyDescriptors();
        } catch (java.beans.IntrospectionException exc) {
            throw new AntException("Unable to get bean info for " + type.getName());
        }
    }

    /**
     * Replaces any variables in the input string with their values.
     */
    private java.lang.String parseVariables(java.lang.String input) throws org.apache.tools.ant.BuildException {
        java.lang.StringBuffer output = new java.lang.StringBuffer();
        int start = 0;
        int end = 0;
        while ((start = input.indexOf('{', end)) != (-1)) {
            output.append(input.substring(end, start));
            end = input.indexOf('}', start);
            if (end != (-1)) {
                java.lang.String name = input.substring(++start, end++);
                java.lang.String value = proxy.getTarget().getProject().getVariable(name);
                if (value == null) {
                    throw new BuildException(("The variable \"" + name) + "\" has not been defined");
                }
                output.append(value);
            }
        } 
        output.append(input.substring(end));
        return output.toString();
    }
}