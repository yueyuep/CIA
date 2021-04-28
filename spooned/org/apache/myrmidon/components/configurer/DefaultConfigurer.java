/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.configurer;
import org.apache.avalon.excalibur.property.PropertyException;
import org.apache.avalon.excalibur.property.PropertyUtil;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.logger.Loggable;
import org.apache.log.Logger;
import org.apache.myrmidon.components.converter.MasterConverter;
import org.apache.myrmidon.converter.Converter;
import org.apache.myrmidon.converter.ConverterException;
/**
 * Class used to configure tasks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultConfigurer extends org.apache.avalon.framework.logger.AbstractLoggable implements org.apache.myrmidon.components.configurer.Configurer , org.apache.avalon.framework.component.Composable , org.apache.avalon.framework.logger.Loggable {
    // /Compile time constant to turn on extreme debugging
    private static final boolean DEBUG = false;

    /* TODO: Should reserved names be "configurable" ? */
    // /Attribute names that are reserved
    private static final java.lang.String[] RESERVED_ATTRIBUTES = new java.lang.String[]{ "logger" };

    // /Element names that are reserved
    private static final java.lang.String[] RESERVED_ELEMENTS = new java.lang.String[]{ "content" };

    // /Converter to use for converting between values
    private org.apache.myrmidon.components.converter.MasterConverter m_converter;

    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        m_converter = ((org.apache.myrmidon.components.converter.MasterConverter) (componentManager.lookup(MasterConverter.ROLE)));
    }

    /**
     * Configure a task based on a configuration in a particular context.
     * This configuring can be done in different ways for different
     * configurers.
     * This one does it by first checking if object implements Configurable
     * and if it does will pass the task the configuration - else it will use
     * mapping rules to map configuration to types
     *
     * @param object
     * 		the object
     * @param configuration
     * 		the configuration
     * @param context
     * 		the Context
     * @exception ConfigurationException
     * 		if an error occurs
     */
    public void configure(final java.lang.Object object, final org.apache.avalon.framework.configuration.Configuration configuration, final org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException {
        if (org.apache.myrmidon.components.configurer.DefaultConfigurer.DEBUG) {
            getLogger().debug("Configuring " + object);
        }
        if (object instanceof org.apache.avalon.framework.configuration.Configurable) {
            if (org.apache.myrmidon.components.configurer.DefaultConfigurer.DEBUG) {
                getLogger().debug("Configuring object via Configurable interface");
            }
            ((org.apache.avalon.framework.configuration.Configurable) (object)).configure(configuration);
        } else {
            if (org.apache.myrmidon.components.configurer.DefaultConfigurer.DEBUG) {
                getLogger().debug("Configuring object via Configurable reflection");
            }
            final java.lang.String[] attributes = configuration.getAttributeNames();
            for (int i = 0; i < attributes.length; i++) {
                final java.lang.String name = attributes[i];
                final java.lang.String value = configuration.getAttribute(name);
                if (org.apache.myrmidon.components.configurer.DefaultConfigurer.DEBUG) {
                    getLogger().debug((("Configuring attribute name=" + name) + " value=") + value);
                }
                configureAttribute(object, name, value, context);
            }
            final org.apache.avalon.framework.configuration.Configuration[] children = configuration.getChildren();
            for (int i = 0; i < children.length; i++) {
                final org.apache.avalon.framework.configuration.Configuration child = children[i];
                if (org.apache.myrmidon.components.configurer.DefaultConfigurer.DEBUG) {
                    getLogger().debug("Configuring subelement name=" + child.getName());
                }
                configureElement(object, child, context);
            }
            final java.lang.String content = configuration.getValue(null);
            if (null != content) {
                if (!content.trim().equals("")) {
                    if (org.apache.myrmidon.components.configurer.DefaultConfigurer.DEBUG) {
                        getLogger().debug("Configuring content " + content);
                    }
                    configureContent(object, content, context);
                }
            }
        }
    }

    /**
     * Configure named attribute of object in a particular context.
     * This configuring can be done in different ways for different
     * configurers.
     *
     * @param object
     * 		the object
     * @param name
     * 		the attribute name
     * @param value
     * 		the attribute value
     * @param context
     * 		the Context
     * @exception ConfigurationException
     * 		if an error occurs
     */
    public void configure(final java.lang.Object object, final java.lang.String name, final java.lang.String value, final org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException {
        configureAttribute(object, name, value, context);
    }

    /**
     * Try to configure content of an object.
     *
     * @param object
     * 		the object
     * @param content
     * 		the content value to be set
     * @param context
     * 		the Context
     * @exception ConfigurationException
     * 		if an error occurs
     */
    private void configureContent(final java.lang.Object object, final java.lang.String content, final org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException {
        setValue(object, "addContent", content, context);
    }

    private void configureAttribute(final java.lang.Object object, final java.lang.String name, final java.lang.String value, final org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException {
        for (int i = 0; i < org.apache.myrmidon.components.configurer.DefaultConfigurer.RESERVED_ATTRIBUTES.length; i++) {
            if (org.apache.myrmidon.components.configurer.DefaultConfigurer.RESERVED_ATTRIBUTES[i].equals(name)) {
                throw new org.apache.avalon.framework.configuration.ConfigurationException("Can not specify reserved attribute " + name);
            }
        }
        final java.lang.String methodName = getMethodNameFor(name);
        setValue(object, methodName, value, context);
    }

    private void setValue(final java.lang.Object object, final java.lang.String methodName, final java.lang.String value, final org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException {
        // OMFG the rest of this is soooooooooooooooooooooooooooooooo
        // slow. Need to cache results per class etc.
        final java.lang.Class clazz = object.getClass();
        final java.lang.reflect.Method[] methods = getMethodsFor(clazz, methodName);
        if (0 == methods.length) {
            throw new org.apache.avalon.framework.configuration.ConfigurationException((("Unable to set attribute via " + methodName) + " due to not finding any appropriate ") + "accessor method");
        }
        setValue(object, value, context, methods);
    }

    private void setValue(final java.lang.Object object, final java.lang.String value, final org.apache.avalon.framework.context.Context context, final java.lang.reflect.Method[] methods) throws org.apache.avalon.framework.configuration.ConfigurationException {
        try {
            final java.lang.Object objectValue = org.apache.avalon.excalibur.property.PropertyUtil.resolveProperty(value, context, false);
            setValue(object, objectValue, methods, context);
        } catch (final org.apache.avalon.excalibur.property.PropertyException pe) {
            throw new org.apache.avalon.framework.configuration.ConfigurationException("Error resolving property " + value, pe);
        }
    }

    private void setValue(final java.lang.Object object, java.lang.Object value, final java.lang.reflect.Method[] methods, final org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException {
        final java.lang.Class sourceClass = value.getClass();
        final java.lang.String source = sourceClass.getName();
        for (int i = 0; i < methods.length; i++) {
            if (setValue(object, value, methods[i], sourceClass, source, context)) {
                return;
            }
        }
        throw new org.apache.avalon.framework.configuration.ConfigurationException(((("Unable to set attribute via " + methods[0].getName()) + " as could not convert ") + source) + " to a matching type");
    }

    private boolean setValue(final java.lang.Object object, java.lang.Object value, final java.lang.reflect.Method method, final java.lang.Class sourceClass, final java.lang.String source, final org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException {
        java.lang.Class parameterType = method.getParameterTypes()[0];
        if (parameterType.isPrimitive()) {
            parameterType = getComplexTypeFor(parameterType);
        }
        try {
            value = m_converter.convert(parameterType, value, context);
        } catch (final org.apache.myrmidon.converter.ConverterException ce) {
            if (org.apache.myrmidon.components.configurer.DefaultConfigurer.DEBUG) {
                getLogger().debug("Failed to find converter ", ce);
            }
            return false;
        } catch (final java.lang.Exception e) {
            throw new org.apache.avalon.framework.configuration.ConfigurationException("Error converting attribute for " + method.getName(), e);
        }
        try {
            method.invoke(object, new java.lang.Object[]{ value });
        } catch (final java.lang.IllegalAccessException iae) {
            // should never happen ....
            throw new org.apache.avalon.framework.configuration.ConfigurationException("Error retrieving methods with " + "correct access specifiers", iae);
        } catch (final java.lang.reflect.InvocationTargetException ite) {
            throw new org.apache.avalon.framework.configuration.ConfigurationException("Error calling method attribute " + method.getName(), ite);
        }
        return true;
    }

    private java.lang.Class getComplexTypeFor(final java.lang.Class clazz) {
        if (java.lang.String.class == clazz)
            return java.lang.String.class;
        else if (java.lang.Integer.TYPE.equals(clazz))
            return java.lang.Integer.class;
        else if (java.lang.Long.TYPE.equals(clazz))
            return java.lang.Long.class;
        else if (java.lang.Short.TYPE.equals(clazz))
            return java.lang.Short.class;
        else if (java.lang.Byte.TYPE.equals(clazz))
            return java.lang.Byte.class;
        else if (java.lang.Boolean.TYPE.equals(clazz))
            return java.lang.Boolean.class;
        else if (java.lang.Float.TYPE.equals(clazz))
            return java.lang.Float.class;
        else if (java.lang.Double.TYPE.equals(clazz))
            return java.lang.Double.class;
        else {
            throw new java.lang.IllegalArgumentException(("Can not get complex type for non-primitive " + "type ") + clazz.getName());
        }
    }

    private java.lang.reflect.Method[] getMethodsFor(final java.lang.Class clazz, final java.lang.String methodName) {
        final java.lang.reflect.Method[] methods = clazz.getMethods();
        final java.util.ArrayList matches = new java.util.ArrayList();
        for (int i = 0; i < methods.length; i++) {
            final java.lang.reflect.Method method = methods[i];
            if (methodName.equals(method.getName()) && (java.lang.reflect.Method.PUBLIC == (method.getModifiers() & java.lang.reflect.Method.PUBLIC))) {
                if (method.getReturnType().equals(java.lang.Void.TYPE)) {
                    final java.lang.Class[] parameters = method.getParameterTypes();
                    if (1 == parameters.length) {
                        matches.add(method);
                    }
                }
            }
        }
        return ((java.lang.reflect.Method[]) (matches.toArray(new java.lang.reflect.Method[0])));
    }

    private java.lang.reflect.Method[] getCreateMethodsFor(final java.lang.Class clazz, final java.lang.String methodName) {
        final java.lang.reflect.Method[] methods = clazz.getMethods();
        final java.util.ArrayList matches = new java.util.ArrayList();
        for (int i = 0; i < methods.length; i++) {
            final java.lang.reflect.Method method = methods[i];
            if (methodName.equals(method.getName()) && (java.lang.reflect.Method.PUBLIC == (method.getModifiers() & java.lang.reflect.Method.PUBLIC))) {
                final java.lang.Class returnType = method.getReturnType();
                if ((!returnType.equals(java.lang.Void.TYPE)) && (!returnType.isPrimitive())) {
                    final java.lang.Class[] parameters = method.getParameterTypes();
                    if (0 == parameters.length) {
                        matches.add(method);
                    }
                }
            }
        }
        return ((java.lang.reflect.Method[]) (matches.toArray(new java.lang.reflect.Method[0])));
    }

    private java.lang.String getMethodNameFor(final java.lang.String attribute) {
        return "set" + getJavaNameFor(attribute.toLowerCase());
    }

    private java.lang.String getJavaNameFor(final java.lang.String name) {
        final java.lang.StringBuffer sb = new java.lang.StringBuffer();
        int index = name.indexOf('-');
        int last = 0;
        while ((-1) != index) {
            final java.lang.String word = name.substring(last, index).toLowerCase();
            sb.append(java.lang.Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1, word.length()));
            last = index + 1;
            index = name.indexOf('-', last);
        } 
        index = name.length();
        final java.lang.String word = name.substring(last, index).toLowerCase();
        sb.append(java.lang.Character.toUpperCase(word.charAt(0)));
        sb.append(word.substring(1, word.length()));
        return sb.toString();
    }

    private void configureElement(final java.lang.Object object, final org.apache.avalon.framework.configuration.Configuration configuration, final org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException {
        final java.lang.String name = configuration.getName();
        for (int i = 0; i < org.apache.myrmidon.components.configurer.DefaultConfigurer.RESERVED_ELEMENTS.length; i++) {
            if (org.apache.myrmidon.components.configurer.DefaultConfigurer.RESERVED_ATTRIBUTES[i].equals(name))
                return;

        }
        final java.lang.String javaName = getJavaNameFor(name);
        // OMFG the rest of this is soooooooooooooooooooooooooooooooo
        // slow. Need to cache results per class etc.
        final java.lang.Class clazz = object.getClass();
        java.lang.reflect.Method[] methods = getMethodsFor(clazz, "add" + javaName);
        if (0 != methods.length) {
            // guess it is first method ????
            addElement(object, methods[0], configuration, context);
        } else {
            methods = getCreateMethodsFor(clazz, "create" + javaName);
            if (0 == methods.length) {
                throw new org.apache.avalon.framework.configuration.ConfigurationException((("Unable to set attribute " + javaName) + " due to not finding any appropriate ") + "accessor method");
            }
            // guess it is first method ????
            createElement(object, methods[0], configuration, context);
        }
    }

    private void createElement(final java.lang.Object object, final java.lang.reflect.Method method, final org.apache.avalon.framework.configuration.Configuration configuration, final org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException {
        try {
            final java.lang.Object created = method.invoke(object, new java.lang.Object[0]);
            configure(created, configuration, context);
        } catch (final org.apache.avalon.framework.configuration.ConfigurationException ce) {
            throw ce;
        } catch (final java.lang.Exception e) {
            throw new org.apache.avalon.framework.configuration.ConfigurationException("Error creating sub-element", e);
        }
    }

    private void addElement(final java.lang.Object object, final java.lang.reflect.Method method, final org.apache.avalon.framework.configuration.Configuration configuration, final org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException {
        try {
            final java.lang.Class clazz = method.getParameterTypes()[0];
            final java.lang.Object created = clazz.newInstance();
            configure(created, configuration, context);
            method.invoke(object, new java.lang.Object[]{ created });
        } catch (final org.apache.avalon.framework.configuration.ConfigurationException ce) {
            throw ce;
        } catch (final java.lang.Exception e) {
            throw new org.apache.avalon.framework.configuration.ConfigurationException("Error creating sub-element", e);
        }
    }
}