/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.converter;
import org.apache.avalon.framework.context.Context;
/**
 * Instances of this interface are used to convert between different types.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface Converter {
    java.lang.String ROLE = "org.apache.myrmidon.converter.Converter";

    /**
     * Convert original to destination type.
     * Destination is passed so that one converter can potentiall
     * convert to multiple different types.
     *
     * @param destination
     * 		the destinaiton type
     * @param original
     * 		the original type
     * @param context
     * 		the context in which to convert
     * @return the converted object
     * @exception Exception
     * 		if an error occurs
     */
    java.lang.Object convert(java.lang.Class destination, java.lang.Object original, org.apache.avalon.framework.context.Context context) throws org.apache.myrmidon.converter.ConverterException;
}