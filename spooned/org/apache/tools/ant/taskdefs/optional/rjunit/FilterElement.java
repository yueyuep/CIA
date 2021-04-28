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
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.rjunit.formatter.FilterFormatter;
import org.apache.tools.ant.taskdefs.optional.rjunit.formatter.FilterStackFormatter;
import org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter;
import org.apache.tools.ant.types.EnumeratedAttribute;
/**
 * A filter element that can be used inside a ResultFormatterElement to denote
 * a filtering. Note that the filtering order correspond to the element
 * order. The first element being the top filter, the last element
 * being the bottom filter.
 *
 * <pre>
 * <!ELEMENT filter>
 * <!ATTLIST filter type (stack) required>
 * <!ATTLIST filter classname CDATA required>
 * </pre>
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public class FilterElement {
    /**
     * filter classname, is should inherit from FilterFormatter
     */
    private java.lang.String classname;

    /**
     * Called by introspection on <tt>type</tt> attribute.
     *
     * @see FilterAttribute
     */
    public void setType(org.apache.tools.ant.taskdefs.optional.rjunit.FilterElement.FilterAttribute fa) {
        setClassName(fa.getClassName());
    }

    /**
     * Called by introspection on <tt>classname</tt> attribute.
     * It must inherit from <tt>FilterFormatter</tt>
     *
     * @see FilterFormatter
     */
    public void setClassName(java.lang.String name) {
        classname = name;
    }

    /**
     * Wrap this filter around a given formatter.
     *
     * @throws BuildException
     * 		if any error happens when creating this filter.
     */
    public org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter createFilterFormatter(org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter f) throws org.apache.tools.ant.BuildException {
        try {
            java.lang.Class clazz = java.lang.Class.forName(classname);
            if (!org.apache.tools.ant.taskdefs.optional.rjunit.formatter.FilterFormatter.class.isAssignableFrom(clazz)) {
                throw new org.apache.tools.ant.BuildException(clazz + " must be a FilterFormatter.");
            }
            java.lang.reflect.Constructor ctor = clazz.getConstructor(new java.lang.Class[]{ org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter.class });
            return ((org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter) (ctor.newInstance(new java.lang.Object[]{ f })));
        } catch (org.apache.tools.ant.BuildException e) {
            throw e;
        } catch (java.lang.Exception e) {
            throw new org.apache.tools.ant.BuildException(e);
        }
    }

    /**
     * a predefined set of filters w/ their class mapping
     */
    public static class FilterAttribute extends org.apache.tools.ant.types.EnumeratedAttribute {
        /**
         * the predefined alias for filters
         */
        private static final java.lang.String[] VALUES = new java.lang.String[]{ "stack" };

        /**
         * the class corresponding to the alias (in the same order)
         */
        private static final java.lang.String[] CLASSNAMES = new java.lang.String[]{ org.apache.tools.ant.taskdefs.optional.rjunit.formatter.FilterStackFormatter.class.getName() };

        public java.lang.String[] getValues() {
            return org.apache.tools.ant.taskdefs.optional.rjunit.FilterElement.FilterAttribute.VALUES;
        }

        /**
         * get the classname matching the alias
         */
        public java.lang.String getClassName() {
            return org.apache.tools.ant.taskdefs.optional.rjunit.FilterElement.FilterAttribute.CLASSNAMES[getIndex()];
        }
    }
}