/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.builder;
/**
 * Dummy exception to stop parsing "safely".
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
class StopParsingException extends org.xml.sax.SAXException {
    public StopParsingException() {
        super("");
    }
}