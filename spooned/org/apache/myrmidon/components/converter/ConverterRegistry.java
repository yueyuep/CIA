/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.converter;
import org.apache.avalon.framework.component.Component;
/**
 * Interface for registry for ConverterInfos.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ConverterRegistry extends org.apache.avalon.framework.component.Component {
    java.lang.String ROLE = "org.apache.myrmidon.components.converter.ConverterRegistry";

    /**
     * Retrieve name of ConverterInfo that describes converter that converts
     * from source to destination.
     *
     * @param source
     * 		the source classname
     * @param destination
     * 		the destination classname
     * @return the className of converter or null if none available
     */
    java.lang.String getConverterName(java.lang.String source, java.lang.String destination);

    /**
     * Register a converter
     *
     * @param className
     * 		the className of converter
     * @param source
     * 		the source classname
     * @param destination
     * 		the destination classname
     */
    void registerConverter(java.lang.String className, java.lang.String source, java.lang.String destination);
}