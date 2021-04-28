/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.core;
import org.apache.avalon.framework.context.Context;
import org.apache.myrmidon.api.TaskContext;
import org.apache.myrmidon.api.TaskException;
import org.apache.myrmidon.converter.AbstractConverter;
import org.apache.myrmidon.converter.ConverterException;
/**
 * String to file converter
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class StringToFileConverter extends org.apache.myrmidon.converter.AbstractConverter {
    public StringToFileConverter() {
        super(java.lang.String.class, java.io.File.class);
    }

    public java.lang.Object convert(final java.lang.Object original, final org.apache.avalon.framework.context.Context context) throws org.apache.myrmidon.converter.ConverterException {
        try {
            final org.apache.myrmidon.api.TaskContext taskContext = ((org.apache.myrmidon.api.TaskContext) (context));
            return taskContext.resolveFile(((java.lang.String) (original)));
        } catch (final org.apache.myrmidon.api.TaskException te) {
            throw new org.apache.myrmidon.converter.ConverterException("Error resolving file during conversion", te);
        }
    }
}