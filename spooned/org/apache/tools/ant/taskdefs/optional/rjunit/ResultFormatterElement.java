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
import org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter;
import org.apache.tools.ant.types.EnumeratedAttribute;
/**
 * An element representing a <tt>Formatter</tt>
 *
 * <pre>
 * <!ELEMENT formatter (filter)*>
 * <!ATTLIST formatter type (plain|xml|brief) #REQUIRED>
 * <!ATTLIST formatter classname CDATA #REQUIRED>
 * <!ATTLIST formatter extension CDATA #IMPLIED>
 * <!ATTLIST formatter usefile (yes|no) no>
 * </pre>
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 * @see RJUnitTask
 * @see Formatter
 */
public class ResultFormatterElement {
    /**
     * output stream for the formatter
     */
    private java.io.OutputStream out = new org.apache.tools.ant.taskdefs.optional.rjunit.KeepAliveOutputStream(java.lang.System.out);

    /**
     * formatter classname
     */
    private java.lang.String classname;

    /**
     * the filters to apply to this formatter
     */
    private java.util.Vector filters = new java.util.Vector();

    /**
     * the parameters set for configuration purposes
     */
    private java.util.Vector params = new java.util.Vector();

    /**
     * set an existing type of formatter.
     *
     * @see TypeAttribute
     * @see #setClassname(String)
     */
    public void setType(org.apache.tools.ant.taskdefs.optional.rjunit.ResultFormatterElement.TypeAttribute type) {
        setClassname(type.getClassName());
    }

    /**
     * <p> Set name of class to be used as the formatter.
     *
     * <p> This class must implement <code>Formatter</code>
     */
    public void setClassname(java.lang.String classname) {
        this.classname = classname;
    }

    /**
     * Setting a comma separated list of filters in the specified order.
     *
     * @see #addFilter(FilterElement)
     */
    public void setFilters(java.lang.String filters) {
        java.util.StringTokenizer st = new java.util.StringTokenizer(filters, ",");
        while (st.hasMoreTokens()) {
            org.apache.tools.ant.taskdefs.optional.rjunit.FilterElement fe = new org.apache.tools.ant.taskdefs.optional.rjunit.FilterElement();
            org.apache.tools.ant.taskdefs.optional.rjunit.FilterElement.FilterAttribute fa = new org.apache.tools.ant.taskdefs.optional.rjunit.FilterElement.FilterAttribute();
            fa.setValue(st.nextToken());
            fe.setType(fa);
            addFilter(fe);
        } 
    }

    /**
     * Add a filter to this formatter.
     */
    public void addFilter(org.apache.tools.ant.taskdefs.optional.rjunit.FilterElement fe) {
        filters.addElement(fe);
    }

    /**
     * Add a parameter that can be used for configuration.
     */
    public void addParam(org.apache.tools.ant.taskdefs.optional.rjunit.ResultFormatterElement.Parameter param) {
        params.addElement(param);
    }

    /**
     * Set whether the formatter should log to file.
     */
    public void setOutput(org.apache.tools.ant.taskdefs.optional.rjunit.OutputAttribute output) {
        this.out = output.getOutputStream();
    }

    /**
     * create the Formatter corresponding to this element.
     */
    protected org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter createFormatter() throws org.apache.tools.ant.BuildException {
        if (classname == null) {
            throw new org.apache.tools.ant.BuildException("you must specify type or classname");
        }
        org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter f = null;
        try {
            java.lang.Class clazz = java.lang.Class.forName(classname);
            if (!org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter.class.isAssignableFrom(clazz)) {
                throw new org.apache.tools.ant.BuildException(clazz + " is not a Formatter");
            }
            f = ((org.apache.tools.ant.taskdefs.optional.rjunit.formatter.Formatter) (clazz.newInstance()));
        } catch (org.apache.tools.ant.BuildException e) {
            throw e;
        } catch (java.lang.Exception e) {
            throw new org.apache.tools.ant.BuildException(e);
        }
        // wrap filters in the reverse order: first = top, last = bottom.
        for (int i = filters.size() - 1; i >= 0; i--) {
            org.apache.tools.ant.taskdefs.optional.rjunit.FilterElement fe = ((org.apache.tools.ant.taskdefs.optional.rjunit.FilterElement) (filters.elementAt(i)));
            f = fe.createFilterFormatter(f);
        }
        // create properties from parameters
        java.util.Properties props = new java.util.Properties();
        for (int i = 0; i < params.size(); i++) {
            org.apache.tools.ant.taskdefs.optional.rjunit.ResultFormatterElement.Parameter param = ((org.apache.tools.ant.taskdefs.optional.rjunit.ResultFormatterElement.Parameter) (params.elementAt(i)));
            props.put(param.getName(), param.getValue());
        }
        // it is assumed here that the filters are chaining til the
        // wrapped formatter.
        f.init(props);
        return f;
    }

    /**
     * <p> Enumerated attribute with the values "plain", "xml" and "brief".
     * <p> Use to enumerate options for <tt>type</tt> attribute.
     */
    public static final class TypeAttribute extends org.apache.tools.ant.types.EnumeratedAttribute {
        private static final java.lang.String[] VALUES = new java.lang.String[]{ "plain", "xml", "brief" };

        private static final java.lang.String[] CLASSNAMES = new java.lang.String[]{ "org.apache.tools.ant.taskdefs.optional.rjunit.formatter.PlainFormatter", "org.apache.tools.ant.taskdefs.optional.rjunit.formatter.XMLFormatter", "org.apache.tools.ant.taskdefs.optional.rjunit.formatter.BriefFormatter" };

        public java.lang.String[] getValues() {
            return org.apache.tools.ant.taskdefs.optional.rjunit.ResultFormatterElement.TypeAttribute.VALUES;
        }

        public java.lang.String getClassName() {
            return org.apache.tools.ant.taskdefs.optional.rjunit.ResultFormatterElement.TypeAttribute.CLASSNAMES[getIndex()];
        }
    }

    /**
     * a parameter that be used to configure a formatter
     */
    public static final class Parameter {
        private java.lang.String name;

        private java.lang.String value;

        public void setName(java.lang.String name) {
            this.name = name;
        }

        public void setLocation(java.io.File file) {
            setValue(file.getAbsolutePath());
        }

        public void setValue(java.lang.String value) {
            this.value = value;
        }

        public java.lang.String getName() {
            return this.name;
        }

        public java.lang.String getValue() {
            return this.value;
        }
    }
}