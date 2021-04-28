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
import org.xml.sax.*;
/**
 * Handle the root of a XML parse
 */
public abstract class RootHandler extends org.xml.sax.helpers.DefaultHandler {
    /**
     * Locator used to identify where in the build source particular elements
     * occur.
     */
    private org.xml.sax.Locator locator;

    /**
     * The actual XML parser used to parse the build source
     */
    private org.xml.sax.XMLReader reader;

    /**
     * The URL from which the XML source is being read.
     */
    private java.net.URL sourceURL;

    /**
     * Create a Root Handler.
     *
     * @param sourceURL
     * 		the URL containing the XML source
     * @param reader
     * 		the XML parser.
     */
    public RootHandler(java.net.URL sourceURL, org.xml.sax.XMLReader reader) {
        this.sourceURL = sourceURL;
        this.reader = reader;
    }

    /**
     * Set the locator to use when parsing elements. This is passed onto
     * child elements.
     *
     * @param locator
     * 		the locator for locating elements in the build source.
     */
    public void setDocumentLocator(org.xml.sax.Locator locator) {
        this.locator = locator;
    }

    /**
     * Get the XML Reader being used to parse the XML.
     *
     * @return the XML Reader.
     */
    protected org.xml.sax.XMLReader getXMLReader() {
        return reader;
    }

    /**
     * Get the locator used to locate elements in the XML source as
     * they are parsed.
     *
     * @return the locator object which can be used to determine an elements location
    within the XML source
     */
    protected org.xml.sax.Locator getLocator() {
        return locator;
    }

    /**
     * Get the source URL
     *
     * @return a URL identifiying from where the XML is being read.
     */
    public java.net.URL getSourceURL() {
        return sourceURL;
    }
}