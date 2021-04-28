/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.framework;
import org.apache.avalon.excalibur.property.PropertyException;
import org.apache.avalon.excalibur.property.PropertyUtil;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.myrmidon.api.AbstractTask;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.components.configurer.Configurer;
import org.apache.myrmidon.components.converter.MasterConverter;
import org.apache.myrmidon.components.executor.Executor;
import org.apache.myrmidon.converter.Converter;
import org.apache.myrmidon.converter.ConverterException;
/**
 * This is the class that Task writers should extend to provide custom tasks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public abstract class AbstractContainerTask extends org.apache.myrmidon.api.AbstractTask implements org.apache.avalon.framework.component.Composable {
    // /For converting own attributes
    private org.apache.myrmidon.components.converter.MasterConverter m_converter;

    // /For configuring own sub-elements
    private org.apache.myrmidon.components.configurer.Configurer m_configurer;

    // /For executing sub-elements as tasks
    private org.apache.myrmidon.components.executor.Executor m_executor;

    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        m_configurer = ((org.apache.myrmidon.components.configurer.Configurer) (componentManager.lookup(Configurer.ROLE)));
        m_converter = ((org.apache.myrmidon.components.converter.MasterConverter) (componentManager.lookup(MasterConverter.ROLE)));
        m_executor = ((org.apache.myrmidon.components.executor.Executor) (componentManager.lookup(Executor.ROLE)));
    }

    /**
     * Helper method to resolve a string.
     *
     * @param value
     * 		the value to resolve
     * @return the resolved value
     * @exception ConfigurationException
     * 		if an error occurs
     */
    protected final java.lang.Object resolve(final java.lang.String value) throws org.apache.avalon.framework.configuration.ConfigurationException {
        try {
            final java.lang.Object object = org.apache.avalon.excalibur.property.PropertyUtil.resolveProperty(value, getContext(), false);
            if (null == object) {
                throw new org.apache.avalon.framework.configuration.ConfigurationException(("Value (" + value) + ") resolved to null");
            }
            return object;
        } catch (final org.apache.avalon.excalibur.property.PropertyException pe) {
            throw new org.apache.avalon.framework.configuration.ConfigurationException("Error resolving value: " + value, pe);
        }
    }

    /**
     * Helper method to convert an object to a specific type.
     *
     * @param to
     * 		type to convert object to
     * @param object
     * 		the object to convert
     * @return the converted object
     * @exception ConfigurationException
     * 		if an error occurs
     */
    protected final java.lang.Object convert(final java.lang.Class to, final java.lang.Object object) throws org.apache.avalon.framework.configuration.ConfigurationException {
        try {
            return getConverter().convert(to, object, getContext());
        } catch (final org.apache.myrmidon.converter.ConverterException ce) {
            throw new org.apache.avalon.framework.configuration.ConfigurationException("Error converting value", ce);
        }
    }

    /**
     * Configure an object using specific configuration element.
     *
     * @param object
     * 		the object
     * @param element
     * 		the configuration element
     * @exception ConfigurationException
     * 		if an error occurs
     */
    protected final void configure(final java.lang.Object object, final org.apache.avalon.framework.configuration.Configuration element) throws org.apache.avalon.framework.configuration.ConfigurationException {
        getConfigurer().configure(object, element, getContext());
    }

    /**
     * Configure an objects attribute using parameters.
     *
     * @param object
     * 		the object
     * @param name
     * 		the attibute name
     * @param value
     * 		the attibute value
     * @exception ConfigurationException
     * 		if an error occurs
     */
    protected final void configure(final java.lang.Object object, final java.lang.String name, final java.lang.String value) throws org.apache.avalon.framework.configuration.ConfigurationException {
        getConfigurer().configure(object, name, value, getContext());
    }

    /**
     * Convenience method for sub-class to retrieve Configurer.
     *
     * @return the configurer
     */
    protected final org.apache.myrmidon.components.configurer.Configurer getConfigurer() {
        return m_configurer;
    }

    /**
     * Convenience method for sub-class to retrieve Converter.
     *
     * @return the converter
     */
    protected final org.apache.myrmidon.converter.Converter getConverter() {
        return m_converter;
    }

    /**
     * Convenience method for sub-class to retrieve Executor.
     *
     * @return the executor
     */
    protected final org.apache.myrmidon.components.executor.Executor getExecutor() {
        return m_executor;
    }
}