/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.runtime;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.myrmidon.api.AbstractTask;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.components.converter.ConverterRegistry;
import org.apache.myrmidon.components.type.DefaultTypeFactory;
import org.apache.myrmidon.components.type.TypeManager;
import org.apache.myrmidon.converter.Converter;
/**
 * Task to define a converter.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ConverterDef extends org.apache.myrmidon.api.AbstractTask implements org.apache.avalon.framework.component.Composable {
    private java.lang.String m_sourceType;

    private java.lang.String m_destinationType;

    private java.io.File m_lib;

    private java.lang.String m_classname;

    private org.apache.myrmidon.components.converter.ConverterRegistry m_converterRegistry;

    private org.apache.myrmidon.components.type.TypeManager m_typeManager;

    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        m_converterRegistry = ((org.apache.myrmidon.components.converter.ConverterRegistry) (componentManager.lookup(ConverterRegistry.ROLE)));
        m_typeManager = ((org.apache.myrmidon.components.type.TypeManager) (componentManager.lookup(TypeManager.ROLE)));
    }

    public void setLib(final java.io.File lib) {
        m_lib = lib;
    }

    public void setClassname(final java.lang.String classname) {
        m_classname = classname;
    }

    public void setSourceType(final java.lang.String sourceType) {
        m_sourceType = sourceType;
    }

    public void setDestinationType(final java.lang.String destinationType) {
        m_destinationType = destinationType;
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
        if (null == m_classname) {
            throw new org.apache.myrmidon.api.TaskException("Must specify classname parameter");
        } else if (null == m_sourceType) {
            throw new org.apache.myrmidon.api.TaskException("Must specify the source-type parameter");
        } else if (null == m_destinationType) {
            throw new org.apache.myrmidon.api.TaskException("Must specify the destination-type parameter");
        } else if (null == m_lib) {
            throw new org.apache.myrmidon.api.TaskException("Must specify the lib parameter");
        }
        try {
            m_converterRegistry.registerConverter(m_classname, m_sourceType, m_destinationType);
            final java.net.URL url = m_lib.toURL();
            final org.apache.myrmidon.components.type.DefaultTypeFactory factory = new org.apache.myrmidon.components.type.DefaultTypeFactory(new java.net.URL[]{ url });
            factory.addNameClassMapping(m_classname, m_classname);
            m_typeManager.registerType(Converter.ROLE, m_classname, factory);
        } catch (final java.lang.Exception e) {
            throw new org.apache.myrmidon.api.TaskException("Failed to register converter " + m_classname, e);
        }
    }
}