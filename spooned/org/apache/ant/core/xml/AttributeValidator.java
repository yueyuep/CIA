/* The Apache Software License, Version 1.1

Copyright (c) 2001 The Apache Software Foundation.  All rights
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
package org.apache.ant.core.xml;
import java.util.*;
import org.xml.sax.*;
/**
 * Validates and extracts attribute values from a set of element attributes.
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 */
public abstract class AttributeValidator {
    public static java.util.Map validateAttributes(java.lang.String elementName, org.xml.sax.Attributes attributes, java.util.Set validAttributes, org.xml.sax.Locator locator) throws org.xml.sax.SAXParseException {
        return org.apache.ant.core.xml.AttributeValidator.validateAttributes(elementName, attributes, null, validAttributes, locator);
    }

    public static java.util.Map validateAttributes(java.lang.String elementName, org.xml.sax.Attributes attributes, java.util.Map aspects, java.util.Set validAttributes, org.xml.sax.Locator locator) throws org.xml.sax.SAXParseException {
        java.util.Map attributeValues = new java.util.HashMap();
        java.util.Set invalidAttributes = new java.util.HashSet();
        for (int i = 0; i < attributes.getLength(); ++i) {
            java.lang.String attributeName = attributes.getQName(i);
            java.lang.String attributeValue = attributes.getValue(i);
            if (validAttributes.contains(attributeName)) {
                attributeValues.put(attributeName, attributeValue);
            } else if ((aspects != null) && (attributeName.indexOf(":") != (-1))) {
                aspects.put(attributeName, attributeValue);
            } else {
                invalidAttributes.add(attributeName);
            }
        }
        if (invalidAttributes.size() != 0) {
            java.lang.StringBuffer message = new java.lang.StringBuffer();
            boolean justOne = invalidAttributes.size() == 1;
            message.append(justOne ? "The attribute " : "The attributes ");
            for (java.util.Iterator i = invalidAttributes.iterator(); i.hasNext();) {
                java.lang.String attributeName = ((java.lang.String) (i.next()));
                message.append(attributeName + " ");
            }
            message.append(justOne ? "is " : "are ");
            message.append(("not valid for the <" + elementName) + "> element.");
            throw new org.xml.sax.SAXParseException(message.toString(), locator);
        }
        return attributeValues;
    }
}