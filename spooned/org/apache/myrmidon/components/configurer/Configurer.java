/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.configurer;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.Context;
/**
 * Class used to configure tasks.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Configurer extends org.apache.avalon.framework.component.Component {
    java.lang.String ROLE = "org.apache.myrmidon.components.configurer.Configurer";

    /**
     * Configure an object based on a configuration in a particular context.
     * This configuring can be done in different ways for different
     * configurers.
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
    void configure(java.lang.Object object, org.apache.avalon.framework.configuration.Configuration configuration, org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException;

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
    void configure(java.lang.Object object, java.lang.String name, java.lang.String value, org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.configuration.ConfigurationException;
}