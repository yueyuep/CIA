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
 * String to integer converter.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class StringToIntegerConverter extends org.apache.myrmidon.converter.AbstractConverter {
    public StringToIntegerConverter() {
        super(java.lang.String.class, java.lang.Integer.class);
    }

    public java.lang.Object convert(final java.lang.Object original, final org.apache.avalon.framework.context.Context context) throws org.apache.myrmidon.converter.ConverterException {
        try {
            return new java.lang.Integer(((java.lang.String) (original)));
        } catch (final java.lang.NumberFormatException nfe) {
            throw new org.apache.myrmidon.converter.ConverterException("Error formatting object", nfe);
        }
    }
}