/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.converter;
import org.apache.avalon.framework.component.ComponentException;
import org.apache.avalon.framework.component.ComponentManager;
import org.apache.avalon.framework.component.Composable;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.myrmidon.components.converter.MasterConverter;
import org.apache.myrmidon.components.type.TypeException;
import org.apache.myrmidon.components.type.TypeFactory;
import org.apache.myrmidon.components.type.TypeManager;
import org.apache.myrmidon.converter.Converter;
import org.apache.myrmidon.converter.ConverterException;
/**
 * Converter engine to handle converting between types.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultMasterConverter extends org.apache.avalon.framework.logger.AbstractLoggable implements org.apache.myrmidon.components.converter.MasterConverter , org.apache.avalon.framework.component.Composable {
    private static final boolean DEBUG = false;

    private org.apache.myrmidon.components.converter.ConverterRegistry m_registry;

    private org.apache.myrmidon.components.type.TypeFactory m_factory;

    /**
     * Retrieve relevent services needed to deploy.
     *
     * @param componentManager
     * 		the ComponentManager
     * @exception ComponentException
     * 		if an error occurs
     */
    public void compose(final org.apache.avalon.framework.component.ComponentManager componentManager) throws org.apache.avalon.framework.component.ComponentException {
        m_registry = ((org.apache.myrmidon.components.converter.ConverterRegistry) (componentManager.lookup(ConverterRegistry.ROLE)));
        final org.apache.myrmidon.components.type.TypeManager typeManager = ((org.apache.myrmidon.components.type.TypeManager) (componentManager.lookup(TypeManager.ROLE)));
        try {
            m_factory = typeManager.getFactory(Converter.ROLE);
        } catch (final org.apache.myrmidon.components.type.TypeException te) {
            throw new org.apache.avalon.framework.component.ComponentException("Unable to retrieve factory from TypeManager", te);
        }
    }

    /**
     * Convert object to destination type.
     *
     * @param destination
     * 		the destination type
     * @param original
     * 		the original object
     * @param context
     * 		the context in which to convert
     * @return the converted object
     * @exception Exception
     * 		if an error occurs
     */
    public java.lang.Object convert(java.lang.Class destination, final java.lang.Object original, final org.apache.avalon.framework.context.Context context) throws org.apache.myrmidon.converter.ConverterException {
        final java.lang.Class originalClass = original.getClass();
        if (destination.isAssignableFrom(originalClass)) {
            return original;
        }
        if (org.apache.myrmidon.components.converter.DefaultMasterConverter.DEBUG) {
            getLogger().debug((("Looking for converter from " + originalClass.getName()) + " to ") + destination.getName());
        }
        // TODO: Start searching inheritance hierarchy for converter
        final java.lang.String name = m_registry.getConverterName(originalClass.getName(), destination.getName());
        if (null == name) {
            throw new org.apache.myrmidon.converter.ConverterException(((("Unable to find converter for " + originalClass.getName()) + " to ") + destination.getName()) + " conversion");
        }
        try {
            // TODO: Start caching converters instead of repeatedly instantiating em.
            final org.apache.myrmidon.converter.Converter converter = ((org.apache.myrmidon.converter.Converter) (m_factory.create(name)));
            if (org.apache.myrmidon.components.converter.DefaultMasterConverter.DEBUG) {
                getLogger().debug("Found Converter: " + converter);
            }
            return converter.convert(destination, original, context);
        } catch (final org.apache.myrmidon.components.type.TypeException te) {
            throw new org.apache.myrmidon.converter.ConverterException("Badly configured TypeManager missing " + "converter definition", te);
        }
    }
}