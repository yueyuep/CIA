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
import org.apache.ant.common.model.Target;
/**
 * Element handler for the target element
 *
 * @author Conor MacNeill
 * @created 9 January 2002
 */
public class TargetHandler extends org.apache.ant.antcore.modelparser.ModelElementHandler {
    /**
     * The name attribute
     */
    public static final java.lang.String NAME_ATTR = "name";

    /**
     * The depends attribute name
     */
    public static final java.lang.String DEPENDS_ATTR = "depends";

    /**
     * The depends attribute name
     */
    public static final java.lang.String DESC_ATTR = "description";

    /**
     * The if attribute name
     */
    public static final java.lang.String IF_ATTR = "if";

    /**
     * The unless attribute name
     */
    public static final java.lang.String UNLESS_ATTR = "unless";

    /**
     * The target being configured.
     */
    private org.apache.ant.common.model.Target target;

    /**
     * Get the target parsed by this handler.
     *
     * @return the Target model object parsed by this handler.
     */
    public org.apache.ant.common.model.Target getTarget() {
        return target;
    }

    /**
     * Process the target element.
     *
     * @param elementName
     * 		the name of the element
     * @exception SAXParseException
     * 		if there is a problem parsing the
     * 		element
     */
    public void processElement(java.lang.String elementName) throws org.xml.sax.SAXParseException {
        target = new org.apache.ant.common.model.Target(getLocation(), getAttribute(org.apache.ant.antcore.modelparser.TargetHandler.NAME_ATTR));
        setModelElement(target);
        target.setDescription(getAttribute(org.apache.ant.antcore.modelparser.TargetHandler.DESC_ATTR));
        target.setAspects(getAspects());
        java.lang.String depends = getAttribute(org.apache.ant.antcore.modelparser.TargetHandler.DEPENDS_ATTR);
        if (depends != null) {
            java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(depends, ",");
            while (tokenizer.hasMoreTokens()) {
                java.lang.String dependency = tokenizer.nextToken().trim();
                target.addDependency(dependency);
            } 
        }
        target.setIfCondition(getAttribute(org.apache.ant.antcore.modelparser.TargetHandler.IF_ATTR));
        target.setUnlessCondition(getAttribute(org.apache.ant.antcore.modelparser.TargetHandler.UNLESS_ATTR));
    }

    /**
     * Process an element within this target. All elements within the target
     * are treated as tasks.
     *
     * @param uri
     * 		The Namespace URI.
     * @param localName
     * 		The local name (without prefix).
     * @param qualifiedName
     * 		The qualified name (with prefix)
     * @param attributes
     * 		The attributes attached to the element.
     * @throws SAXParseException
     * 		if there is a parsing problem.
     */
    public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qualifiedName, org.xml.sax.Attributes attributes) throws org.xml.sax.SAXParseException {
        // everything is a task
        org.apache.ant.antcore.modelparser.BuildElementHandler taskHandler = new org.apache.ant.antcore.modelparser.BuildElementHandler();
        taskHandler.start(getParseContext(), getXMLReader(), this, getLocator(), attributes, getElementSource(), qualifiedName);
        target.addTask(taskHandler.getBuildElement());
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
        if (((((!attributeName.equals(org.apache.ant.antcore.modelparser.TargetHandler.NAME_ATTR)) && (!attributeName.equals(org.apache.ant.antcore.modelparser.TargetHandler.DEPENDS_ATTR))) && (!attributeName.equals(org.apache.ant.antcore.modelparser.TargetHandler.DESC_ATTR))) && (!attributeName.equals(org.apache.ant.antcore.modelparser.TargetHandler.IF_ATTR))) && (!attributeName.equals(org.apache.ant.antcore.modelparser.TargetHandler.UNLESS_ATTR))) {
            throwInvalidAttribute(attributeName);
        }
    }
}