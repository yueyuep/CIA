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
package org.apache.tools.ant.taskdefs.optional.rjunit;
import java.util.NoSuchElementException;
/**
 * Convenient enumeration over an array of enumeration. For example:
 * <pre>
 * Enumeration e1 = v1.elements();
 * while (e1.hasMoreElements()){
 *    // do something
 * }
 * Enumeration e2 = v2.elements();
 * while (e2.hasMoreElements()){
 *    // do the same thing
 * }
 * </pre>
 * can be written as:
 * <pre>
 * Enumeration[] enums = { v1.elements(), v2.elements() };
 * Enumeration e = Enumerations.fromCompound(enums);
 * while (e.hasMoreElements()){
 *    // do something
 * }
 * </pre>
 * Note that the enumeration will skip null elements in the array. The following is
 * thus possible:
 * <pre>
 * Enumeration[] enums = { v1.elements(), null, v2.elements() }; // a null enumeration in the array
 * Enumeration e = Enumerations.fromCompound(enums);
 * while (e.hasMoreElements()){
 *    // do something
 * }
 * </pre>
 *
 * @author <a href="mailto:sbailliez@imediation.com">Stephane Bailliez</a>
 */
public class CompoundEnumeration implements java.util.Enumeration {
    /**
     * enumeration array
     */
    private java.util.Enumeration[] enumArray;

    /**
     * index in the enums array
     */
    private int index = 0;

    public CompoundEnumeration(java.util.Enumeration[] enumarray) {
        this.enumArray = enumarray;
    }

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return <code>true</code> if and only if this enumeration object
    contains at least one more element to provide;
    <code>false</code> otherwise.
     */
    public boolean hasMoreElements() {
        while (index < enumArray.length) {
            if ((enumArray[index] != null) && enumArray[index].hasMoreElements()) {
                return true;
            }
            index++;
        } 
        return false;
    }

    /**
     * Returns the next element of this enumeration if this enumeration
     * object has at least one more element to provide.
     *
     * @return the next element of this enumeration.
     * @throws NoSuchElementException
     * 		if no more elements exist.
     */
    public java.lang.Object nextElement() throws java.util.NoSuchElementException {
        if (hasMoreElements()) {
            return enumArray[index].nextElement();
        }
        throw new java.util.NoSuchElementException();
    }
}