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
package org.apache.ant.antcore.modelparser;
import org.xml.sax.SAXParseException;
import org.apache.ant.antcore.xml.ElementHandler;
import org.apache.ant.antcore.xml.XMLParseException;
import org.apache.ant.common.model.Project;
/**
 * The include handler is used to read in included projects or fragments
 * into a project.
 *
 * @author Conor MacNeill
 * @created 11 January 2002
 */
public class IncludeHandler extends org.apache.ant.antcore.xml.ElementHandler {
    /**
     * The attribute name which identifies the fragment to be included
     */
    public static final java.lang.String SYSTEMID_ATTR = "fragment";

    /**
     * The including project
     */
    private org.apache.ant.common.model.Project project;

    /**
     * Create an IncludeHandler.
     *
     * @param project
     * 		the project into which the include fragment is to be
     * 		placed
     */
    public IncludeHandler(org.apache.ant.common.model.Project project) {
        this.project = project;
    }

    /**
     * Process the element.
     *
     * @param elementName
     * 		the name of the element
     * @exception SAXParseException
     * 		if there is a problem parsing the
     * 		element
     */
    public void processElement(java.lang.String elementName) throws org.xml.sax.SAXParseException {
        java.lang.String includeSystemId = getAttribute(org.apache.ant.antcore.modelparser.IncludeHandler.SYSTEMID_ATTR);
        if (includeSystemId == null) {
            throw new org.xml.sax.SAXParseException(("Attribute " + org.apache.ant.antcore.modelparser.IncludeHandler.SYSTEMID_ATTR) + " is required in an <include> element", getLocator());
        }
        // create a new parser to read this project relative to the
        // project's URI
        try {
            java.net.URL includeURL = new java.net.URL(getElementSource(), includeSystemId);
            org.apache.ant.antcore.modelparser.ProjectHandler includedProjectHandler = new org.apache.ant.antcore.modelparser.ProjectHandler(project);
            getParseContext().parse(includeURL, new java.lang.String[]{ "project", "fragment" }, includedProjectHandler);
        } catch (java.net.MalformedURLException e) {
            throw new org.xml.sax.SAXParseException((("Unable to include " + includeSystemId) + ": ") + e.getMessage(), getLocator());
        } catch (org.apache.ant.antcore.xml.XMLParseException e) {
            throw new org.xml.sax.SAXParseException((("Error parsing included project " + includeSystemId) + ": ") + e.getMessage(), getLocator());
        }
    }

    /**
     * Validate that the given attribute and value are valid.
     *
     * @param attributeName
     * 		The name of the attributes
     * @param attributeValue
     * 		The value of the attributes
     * @exception SAXParseException
     * 		if the attribute is not allowed on the
     * 		element.
     */
    protected void validateAttribute(java.lang.String attributeName, java.lang.String attributeValue) throws org.xml.sax.SAXParseException {
        if (!attributeName.equals(org.apache.ant.antcore.modelparser.IncludeHandler.SYSTEMID_ATTR)) {
            throwInvalidAttribute(attributeName);
        }
    }
}