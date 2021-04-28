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
package org.apache.ant.antcore.xml;
import org.apache.ant.common.util.AntException;
import org.apache.ant.common.util.CircularDependencyChecker;
import org.apache.ant.common.util.CircularDependencyException;
import org.apache.ant.common.util.Location;
/**
 * Holds the current parsing context.
 *
 * @author Conor MacNeill
 * @created 9 January 2002
 */
public class ParseContext {
    /**
     * Used to check if we are trying to parse a build file within its own
     * context.
     */
    private org.apache.ant.common.util.CircularDependencyChecker checker = new org.apache.ant.common.util.CircularDependencyChecker("parsing XML");

    /**
     * The factory used to create SAX parsers.
     */
    private javax.xml.parsers.SAXParserFactory parserFactory = javax.xml.parsers.SAXParserFactory.newInstance();

    /**
     * Parse a URL using the given root handler
     *
     * @param source
     * 		The URL to the source to be parsed
     * @param rootElementName
     * 		The required root element name
     * @param rootElementHandler
     * 		The handler for the root element
     * @exception XMLParseException
     * 		if the element cannot be parsed
     */
    public void parse(java.net.URL source, java.lang.String rootElementName, org.apache.ant.antcore.xml.ElementHandler rootElementHandler) throws org.apache.ant.antcore.xml.XMLParseException {
        parse(source, new java.lang.String[]{ rootElementName }, rootElementHandler);
    }

    /**
     * Parse a URL using the given root handler
     *
     * @param source
     * 		The URL to the source to be parsed
     * @param rootElementNames
     * 		The allowable root element names
     * @param rootElementHandler
     * 		The handler for the root element
     * @exception XMLParseException
     * 		if the element cannot be parsed
     */
    public void parse(java.net.URL source, java.lang.String[] rootElementNames, org.apache.ant.antcore.xml.ElementHandler rootElementHandler) throws org.apache.ant.antcore.xml.XMLParseException {
        try {
            checker.visitNode(source);
            // create a parser for this source
            javax.xml.parsers.SAXParser saxParser = parserFactory.newSAXParser();
            org.xml.sax.XMLReader xmlReader = saxParser.getXMLReader();
            // create a root handler for this
            org.apache.ant.antcore.xml.RootHandler rootHandler = new org.apache.ant.antcore.xml.RootHandler(this, source, xmlReader, rootElementNames, rootElementHandler);
            saxParser.parse(source.toString(), rootHandler);
            checker.leaveNode(source);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new org.apache.ant.antcore.xml.XMLParseException(e);
        } catch (org.xml.sax.SAXParseException e) {
            org.apache.ant.common.util.Location location = new org.apache.ant.common.util.Location(e.getSystemId(), e.getLineNumber(), e.getColumnNumber());
            if (e.getException() != null) {
                java.lang.Throwable nestedException = e.getException();
                if (nestedException instanceof org.apache.ant.common.util.AntException) {
                    location = ((org.apache.ant.common.util.AntException) (nestedException)).getLocation();
                }
                throw new org.apache.ant.antcore.xml.XMLParseException(nestedException, location);
            } else {
                throw new org.apache.ant.antcore.xml.XMLParseException(e, location);
            }
        } catch (org.xml.sax.SAXException e) {
            throw new org.apache.ant.antcore.xml.XMLParseException(e);
        } catch (java.io.IOException e) {
            throw new org.apache.ant.antcore.xml.XMLParseException(e);
        } catch (org.apache.ant.common.util.CircularDependencyException e) {
            throw new org.apache.ant.antcore.xml.XMLParseException(e);
        }
    }
}