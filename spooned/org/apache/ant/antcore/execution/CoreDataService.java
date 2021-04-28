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
package org.apache.ant.antcore.execution;
import org.apache.ant.common.service.DataService;
import org.apache.ant.common.util.ExecutionException;
import org.apache.ant.common.util.PropertyUtils;
/**
 * This is the core's implementation of the DataService service interface.
 * It gives Ant libraries access to property values maintained in the
 * Frame.
 *
 * @author Conor MacNeill
 * @created 31 January 2002
 */
public class CoreDataService implements org.apache.ant.common.service.DataService {
    /**
     * The Frame this service instance is working for
     */
    private org.apache.ant.antcore.execution.Frame frame;

    /**
     * all properties to be unset without throwing an exception
     */
    private boolean allowUnsetProperties;

    /**
     * Constructor
     *
     * @param frame
     * 		the frame containing this context
     * @param allowUnsetProperties
     * 		true if the reference to an unset
     * 		property should not throw an exception
     */
    protected CoreDataService(org.apache.ant.antcore.execution.Frame frame, boolean allowUnsetProperties) {
        this.frame = frame;
        this.allowUnsetProperties = allowUnsetProperties;
    }

    /**
     * Set a data value. If an existing data value exists, associated with
     * the given name, the value will not be changed
     *
     * @param valueName
     * 		the name of the data value
     * @param value
     * 		the value to be associated with the name
     * @exception ExecutionException
     * 		if the value cannot be set
     */
    public void setDataValue(java.lang.String valueName, java.lang.Object value) throws org.apache.ant.common.util.ExecutionException {
        frame.setDataValue(valueName, value, false);
    }

    /**
     * Set a data value which can be overwritten
     *
     * @param valueName
     * 		the name of the data value
     * @param value
     * 		the value to be associated with the name
     * @exception ExecutionException
     * 		if the value cannot be set
     */
    public void setMutableDataValue(java.lang.String valueName, java.lang.Object value) throws org.apache.ant.common.util.ExecutionException {
        frame.setDataValue(valueName, value, true);
    }

    /**
     * Get a data value
     *
     * @param valueName
     * 		the name of the data value
     * @return the current object associated with the name or null if no
    value is currently associated with the name
     * @exception ExecutionException
     * 		if the value cannot be retrieved.
     */
    public java.lang.Object getDataValue(java.lang.String valueName) throws org.apache.ant.common.util.ExecutionException {
        return frame.getDataValue(valueName);
    }

    /**
     * Indicate if a data value has been set
     *
     * @param name
     * 		the name of the data value - may contain reference
     * 		delimiters
     * @return true if the value exists
     * @exception ExecutionException
     * 		if the containing frame for the value
     * 		does not exist
     */
    public boolean isDataValueSet(java.lang.String name) throws org.apache.ant.common.util.ExecutionException {
        return frame.isDataValueSet(name);
    }

    /**
     * Get all the properties from the frame and any references frames. This
     * is an expensive operation since it must clone all of the property
     * stores in all frames
     *
     * @return a Map containing the frames properties indexed by their full
    name.
     */
    public java.util.Map getAllProperties() {
        return frame.getAllProperties();
    }

    /**
     * Replace ${} style constructions in the given value with the string
     * value of the corresponding data values in the frame
     *
     * @param value
     * 		the string to be scanned for property references.
     * @return the string with all property references replaced
     * @exception ExecutionException
     * 		if any of the properties do not exist
     */
    public java.lang.String replacePropertyRefs(java.lang.String value) throws org.apache.ant.common.util.ExecutionException {
        if (value == null) {
            return null;
        }
        java.util.List fragments = new java.util.ArrayList();
        java.util.List propertyRefs = new java.util.ArrayList();
        org.apache.ant.common.util.PropertyUtils.parsePropertyString(value, fragments, propertyRefs);
        java.lang.StringBuffer sb = new java.lang.StringBuffer();
        java.util.Iterator i = fragments.iterator();
        java.util.Iterator j = propertyRefs.iterator();
        while (i.hasNext()) {
            java.lang.String fragment = ((java.lang.String) (i.next()));
            if (fragment == null) {
                java.lang.String propertyName = ((java.lang.String) (j.next()));
                if (!isDataValueSet(propertyName)) {
                    if (!allowUnsetProperties) {
                        throw new org.apache.ant.common.util.ExecutionException(("Property \"" + propertyName) + "\" has not been set");
                    }
                    fragment = ("${" + propertyName) + "}";
                } else {
                    fragment = getDataValue(propertyName).toString();
                }
            }
            sb.append(fragment);
        } 
        return sb.toString();
    }

    /**
     * Replace ${} style constructions in the given value with the string
     * value of the objects in the given map. Any values which are not found
     * are left unchanged.
     *
     * @param value
     * 		the string to be scanned for property references.
     * @param replacementValues
     * 		the collection of replacement values
     * @return the string with all property references replaced
     * @exception ExecutionException
     * 		if any of the properties do not exist
     */
    public java.lang.String replacePropertyRefs(java.lang.String value, java.util.Map replacementValues) throws org.apache.ant.common.util.ExecutionException {
        if (value == null) {
            return null;
        }
        java.util.List fragments = new java.util.ArrayList();
        java.util.List propertyRefs = new java.util.ArrayList();
        org.apache.ant.common.util.PropertyUtils.parsePropertyString(value, fragments, propertyRefs);
        java.lang.StringBuffer sb = new java.lang.StringBuffer();
        java.util.Iterator i = fragments.iterator();
        java.util.Iterator j = propertyRefs.iterator();
        while (i.hasNext()) {
            java.lang.String fragment = ((java.lang.String) (i.next()));
            if (fragment == null) {
                java.lang.String propertyName = ((java.lang.String) (j.next()));
                if (!replacementValues.containsKey(propertyName)) {
                    fragment = ("${" + propertyName) + "}";
                } else {
                    fragment = replacementValues.get(propertyName).toString();
                }
            }
            sb.append(fragment);
        } 
        return sb.toString();
    }
}