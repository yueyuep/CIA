/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.libs.runtime;
import org.apache.myrmidon.api.Task;
import org.apache.myrmidon.framework.AbstractTypeDef;
/**
 * Task to define a type.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class TypeDef extends org.apache.myrmidon.framework.AbstractTypeDef {
    private java.lang.String m_type;

    public void setType(final java.lang.String type) {
        m_type = type;
    }

    protected java.lang.String getTypeName() {
        return m_type;
    }
}