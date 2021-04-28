/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.ant1;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.myrmidon.api.AbstractTask;
import org.apache.myrmidon.api.Task;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.components.type.TypeManager;
/**
 * Method to register a tasklib.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Ant1Tasklib extends org.apache.myrmidon.api.AbstractTask implements org.apache.avalon.framework.component.Composable {
    private java.lang.String m_prefix = "";

    private java.io.File m_lib;

    private org.apache.myrmidon.components.type.TypeManager m_typeManager;

    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        m_typeManager = ((org.apache.myrmidon.components.type.TypeManager) (componentManager.lookup(TypeManager.ROLE)));
    }

    public void setLib(final java.io.File lib) {
        m_lib = lib;
    }

    public void setPrefix(final java.lang.String prefix) {
        m_prefix = prefix;
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
        if (null == m_lib) {
            throw new org.apache.myrmidon.api.TaskException("Must specify lib parameter");
        }
        final java.lang.String location = ("jar:" + m_lib.toURL()) + "!/org/apache/tools/ant/taskdefs/defaults.properties";
        final java.net.URL url = new java.net.URL(location);
        final java.io.InputStream input = url.openStream();
        final java.util.Properties tasks = new java.util.Properties();
        tasks.load(input);
        input.close();
        final org.apache.myrmidon.libs.ant1.Ant1TypeFactory factory = new org.apache.myrmidon.libs.ant1.Ant1TypeFactory(m_lib.toURL());
        final java.util.Enumeration $missing$ = tasks.propertyNames();
    }
}