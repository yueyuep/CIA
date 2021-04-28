/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.framework;
import org.apache.myrmidon.api.TaskException;
/**
 * Basic data type for holding patterns.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Pattern implements org.apache.myrmidon.framework.DataType {
    private java.lang.String m_value;

    private org.apache.myrmidon.framework.Condition m_condition;

    /**
     * Retrieve value of pattern.
     *
     * @return the value of pattern
     */
    public java.lang.String getValue() {
        return m_value;
    }

    /**
     * Get condition associated with pattern if any.
     *
     * @return the Condition
     */
    public org.apache.myrmidon.framework.Condition getCondition() {
        return m_condition;
    }

    /**
     * Setter method for value of pattern.
     * Conforms to setter patterns
     *
     * @param value
     * 		the value
     */
    public void setValue(final java.lang.String value) {
        m_value = value;
    }

    /**
     * Set if clause on pattern.
     *
     * @param condition
     * 		the condition
     * @exception TaskException
     * 		if an error occurs
     */
    public void setIf(final java.lang.String condition) throws org.apache.myrmidon.api.TaskException {
        verifyConditionNull();
        m_condition = new org.apache.myrmidon.framework.Condition(true, condition);
    }

    /**
     * Set unless clause of pattern.
     *
     * @param condition
     * 		the unless clause
     * @exception TaskException
     * 		if an error occurs
     */
    public void setUnless(final java.lang.String condition) throws org.apache.myrmidon.api.TaskException {
        verifyConditionNull();
        m_condition = new org.apache.myrmidon.framework.Condition(false, condition);
    }

    public java.lang.String toString() {
        java.lang.String result = ("Pattern['" + m_value) + "',";
        if (null != m_condition)
            result = result + m_condition;

        return result + "]";
    }

    /**
     * Utility method to make sure condition unset.
     * Made so that it is not possible for both if and unless to be set.
     *
     * @exception TaskException
     * 		if an error occurs
     */
    private void verifyConditionNull() throws org.apache.myrmidon.api.TaskException {
        if (null != m_condition) {
            throw new org.apache.myrmidon.api.TaskException("Can only set one of if/else for pattern data type");
        }
    }
}