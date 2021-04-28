/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.builder;
/**
 * Handler that reacts to PIs before first element.
 * Have to do it this way as there doesn't seem to be a *safe* way
 * of redirecting content handlers at runtime while using transformers.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ReactorPIHandler extends org.xml.sax.helpers.DefaultHandler {
    private java.util.ArrayList m_targets = new java.util.ArrayList();

    private java.util.ArrayList m_data = new java.util.ArrayList();

    public int getPICount() {
        return m_targets.size();
    }

    public java.lang.String getTarget(final int index) {
        return ((java.lang.String) (m_targets.get(index)));
    }

    public java.lang.String getData(final int index) {
        return ((java.lang.String) (m_data.get(index)));
    }

    public void processingInstruction(final java.lang.String target, final java.lang.String data) throws org.xml.sax.SAXException {
        m_targets.add(target);
        m_data.add(data);
    }

    public void startElement(final java.lang.String uri, final java.lang.String localName, final java.lang.String qName, final org.xml.sax.Attributes atts) throws org.xml.sax.SAXException {
        // Workaround to stop SAX pipeline
        throw new org.apache.myrmidon.components.builder.StopParsingException();
    }
}