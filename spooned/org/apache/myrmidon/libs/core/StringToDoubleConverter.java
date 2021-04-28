/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.core;
import org.apache.avalon.framework.context.Context;
import org.apache.myrmidon.converter.AbstractConverter;
import org.apache.myrmidon.converter.ConverterException;
/**
 * String to double converter
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class StringToDoubleConverter extends org.apache.myrmidon.converter.AbstractConverter {
    public StringToDoubleConverter() {
        super(java.lang.String.class, java.lang.Double.class);
    }

    public java.lang.Object convert(final java.lang.Object original, final org.apache.avalon.framework.context.Context context) throws org.apache.myrmidon.converter.ConverterException {
        try {
            return new java.lang.Double(((java.lang.String) (original)));
        } catch (final java.lang.NumberFormatException nfe) {
            throw new org.apache.myrmidon.converter.ConverterException("Error formatting object", nfe);
        }
    }
}