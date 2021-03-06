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
 * String to class converter
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class StringToClassConverter extends org.apache.myrmidon.converter.AbstractConverter {
    public StringToClassConverter() {
        super(java.lang.String.class, java.lang.Class.class);
    }

    public java.lang.Object convert(final java.lang.Object original, final org.apache.avalon.framework.context.Context context) throws org.apache.myrmidon.converter.ConverterException {
        // TODO: Should we use ContextClassLoader here???
        try {
            return java.lang.Class.forName(((java.lang.String) (original)));
        } catch (final java.lang.Exception e) {
            throw new org.apache.myrmidon.converter.ConverterException("Error converting to class type", e);
        }
    }
}