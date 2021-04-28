/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.selftest;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.myrmidon.api.AbstractTask;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.framework.AbstractContainerTask;
/**
 * This is to test self interpretation of configuration.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ConfigurationTest extends org.apache.myrmidon.framework.AbstractContainerTask implements org.apache.avalon.framework.configuration.Configurable {
    private java.lang.String m_message;

    public void configure(final org.apache.avalon.framework.configuration.Configuration configuration) throws org.apache.avalon.framework.configuration.ConfigurationException {
        final java.lang.String message = configuration.getAttribute("message");
        final java.lang.Object object = resolve(message);
        if (object instanceof java.lang.String) {
            m_message = ((java.lang.String) (object));
        } else {
            m_message = object.toString();
        }
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
        getLogger().warn(m_message);
    }
}