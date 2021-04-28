/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.core;
import org.apache.myrmidon.api.AbstractTask;
import org.apache.myrmidon.api.TaskException;
/**
 * This is the echo task to display a message.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Echo extends org.apache.myrmidon.api.AbstractTask {
    private java.lang.String m_message;

    public void setMessage(final java.lang.String message) {
        m_message = message;
    }

    public void execute() throws org.apache.myrmidon.api.TaskException {
        getLogger().warn(m_message);
    }
}