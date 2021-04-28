/* The Apache Software License, Version 1.1

Copyright (c) 2002 The Apache Software Foundation.  All rights
reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in
   the documentation and/or other materials provided with the
   distribution.

3. The end-user documentation included with the redistribution, if
   any, must include the following acknowlegement:
      "This product includes software developed by the
       Apache Software Foundation (http://www.apache.org/)."
   Alternately, this acknowlegement may appear in the software itself,
   if and wherever such third-party acknowlegements normally appear.

4. The names "The Jakarta Project", "Ant", and "Apache Software
   Foundation" must not be used to endorse or promote products derived
   from this software without prior written permission. For written
   permission, please contact apache@apache.org.

5. Products derived from this software may not be called "Apache"
   nor may "Apache" appear in their names without prior written
   permission of the Apache Group.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
====================================================================

This software consists of voluntary contributions made by many
individuals on behalf of the Apache Software Foundation.  For more
information on the Apache Software Foundation, please see
<http://www.apache.org/>.
 */
package org.apache.ant.antlib.system;
import org.apache.ant.common.antlib.AbstractConverter;
import org.apache.ant.common.util.ExecutionException;
import org.apache.ant.common.util.PropertyUtils;
/**
 * A converter to convert to Java's primitie types
 *
 * @author Conor MacNeill
 */
public class PrimitiveConverter extends org.apache.ant.common.antlib.AbstractConverter {
    /**
     * Get the list of classes this converter is able to convert to.
     *
     * @return an array of Class objects representing the classes this
    converter handles.
     */
    public java.lang.Class[] getTypes() {
        return new java.lang.Class[]{ java.lang.Character.class, java.lang.Character.TYPE, java.lang.Byte.TYPE, java.lang.Short.TYPE, java.lang.Integer.TYPE, java.lang.Long.TYPE, java.lang.Float.TYPE, java.lang.Double.TYPE, java.lang.Boolean.class, java.lang.Boolean.TYPE };
    }

    /**
     * Convert a string from the value given to an instance of the given
     * type.
     *
     * @param value
     * 		The value to be converted
     * @param type
     * 		the desired type of the converted object
     * @return the value of the converted object
     * @exception ExecutionException
     * 		if the conversion cannot be made
     */
    public java.lang.Object convert(java.lang.String value, java.lang.Class type) throws org.apache.ant.common.util.ExecutionException {
        if (type.equals(java.lang.Character.class) || type.equals(java.lang.Character.TYPE)) {
            return new java.lang.Character(value.charAt(0));
        } else if (type.equals(java.lang.Byte.TYPE)) {
            return new java.lang.Byte(value);
        } else if (type.equals(java.lang.Short.TYPE)) {
            return new java.lang.Short(value);
        } else if (type.equals(java.lang.Integer.TYPE)) {
            return new java.lang.Integer(value);
        } else if (type.equals(java.lang.Long.TYPE)) {
            return new java.lang.Long(value);
        } else if (type.equals(java.lang.Float.TYPE)) {
            return new java.lang.Float(value);
        } else if (type.equals(java.lang.Double.TYPE)) {
            return new java.lang.Double(value);
        } else if (type.equals(java.lang.Boolean.class) || type.equals(java.lang.Boolean.TYPE)) {
            return new java.lang.Boolean(org.apache.ant.common.util.PropertyUtils.toBoolean(value));
        }
        throw new org.apache.ant.common.util.ExecutionException("This converter does not handle " + type.getName());
    }
}