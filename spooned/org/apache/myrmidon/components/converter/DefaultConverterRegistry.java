/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.converter;
/**
 * Default implementation of Converter registry.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultConverterRegistry implements org.apache.myrmidon.components.converter.ConverterRegistry {
    private final java.util.HashMap m_mapping = new java.util.HashMap();

    public java.lang.String getConverterName(final java.lang.String source, final java.lang.String destination) {
        final java.util.HashMap map = ((java.util.HashMap) (m_mapping.get(source)));
        if (null == map)
            return null;

        return ((java.lang.String) (map.get(destination)));
    }

    public void registerConverter(final java.lang.String className, final java.lang.String source, final java.lang.String destination) {
        java.util.HashMap map = ((java.util.HashMap) (m_mapping.get(source)));
        if (null == map) {
            map = new java.util.HashMap();
            m_mapping.put(source, map);
        }
        map.put(destination, className);
    }
}