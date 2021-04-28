/* The Apache Software License, Version 1.1

Copyright (c) 2000 The Apache Software Foundation.  All rights
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
package org.apache.ant.core.types;
import org.apache.ant.core.execution.*;
/**
 * Helper class for attributes that can only take one of a fixed list
 * of values.
 *
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 */
public abstract class EnumeratedAttribute {
    /**
     * The value of this attribute.
     */
    private java.lang.String value;

    /**
     * This is the only method a subclass needs to implement.
     *
     * @return an array holding all possible values of the enumeration.
     */
    public abstract java.lang.String[] getValues();

    /**
     * Set the value of the enumeration.
     *
     * Invoked by {@link org.apache.ant.core.execution.IntrospectionHelper IntrospectionHelper}.
     *
     * @param value
     * 		the value of the enumeration
     * @throws ExecutionException
     * 		if the value is not value
     */
    public final void setValue(java.lang.String value) throws org.apache.ant.core.types.ExecutionException {
        if (!containsValue(value)) {
            throw new ExecutionException(value + " is not a legal value for this attribute");
        }
        this.value = value;
    }

    /**
     * Is this value included in the enumeration?
     */
    public final boolean containsValue(java.lang.String value) {
        java.lang.String[] values = getValues();
        if ((values == null) || (value == null)) {
            return false;
        }
        for (int i = 0; i < values.length; i++) {
            if (value.equals(values[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the value.
     */
    public final java.lang.String getValue() {
        return value;
    }
}