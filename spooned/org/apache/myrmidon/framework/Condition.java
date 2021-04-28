/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.framework;
import org.apache.avalon.excalibur.property.PropertyException;
import org.apache.avalon.excalibur.property.PropertyUtil;
import org.apache.avalon.framework.component.Component;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.myrmidon.api.TaskException;
/**
 * Class representing a condition.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Condition implements org.apache.avalon.framework.component.Component {
    private java.lang.String m_condition;

    private boolean m_isIfCondition;

    public Condition(final boolean isIfCondition, final java.lang.String condition) {
        m_isIfCondition = isIfCondition;
        m_condition = condition;
    }

    public java.lang.String getCondition() {
        return m_condition;
    }

    public boolean isIfCondition() {
        return m_isIfCondition;
    }

    public boolean evaluate(final org.apache.avalon.framework.context.Context context) throws org.apache.avalon.framework.context.ContextException {
        boolean result = false;
        try {
            final java.lang.Object resolved = org.apache.avalon.excalibur.property.PropertyUtil.resolveProperty(getCondition(), context, false);
            if (null != resolved) {
                final java.lang.Object object = context.get(resolved);
                // TODO: Do more than just check for presence????????????
                // true as object present
                result = true;
            }
        } catch (final org.apache.avalon.framework.context.ContextException ce) {
            result = false;
        } catch (final org.apache.avalon.excalibur.property.PropertyException pe) {
            throw new org.apache.avalon.framework.context.ContextException("Error resolving " + m_condition, pe);
        }
        if (!m_isIfCondition) {
            result = !result;
        }
        return result;
    }

    public java.lang.String toString() {
        if (isIfCondition()) {
            return ("if='" + getCondition()) + "'";
        } else {
            return ("unless='" + getCondition()) + "'";
        }
    }
}