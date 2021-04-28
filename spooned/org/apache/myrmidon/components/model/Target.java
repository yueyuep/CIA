/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.model;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.myrmidon.framework.Condition;
/**
 * Targets in build file.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class Target {
    private final java.util.ArrayList m_dependencies = new java.util.ArrayList();

    private final java.util.ArrayList m_tasks = new java.util.ArrayList();

    private final org.apache.myrmidon.framework.Condition m_condition;

    /**
     * Constructor taking condition for target.
     *
     * @param condition
     * 		the condition
     */
    public Target(final org.apache.myrmidon.framework.Condition condition, final org.apache.avalon.framework.configuration.Configuration[] tasks, final java.lang.String[] dependencies) {
        m_condition = condition;
        for (int i = 0; i < tasks.length; i++) {
            m_tasks.add(tasks[i]);
        }
        if (null != dependencies) {
            for (int i = 0; i < dependencies.length; i++) {
                m_dependencies.add(dependencies[i]);
            }
        }
    }

    /**
     * Get condition under which target is executed.
     *
     * @return the condition for target or null
     */
    public final org.apache.myrmidon.framework.Condition getCondition() {
        return m_condition;
    }

    /**
     * Get dependencies of target
     *
     * @return the dependency list
     */
    public final java.lang.String[] getDependencies() {
        return ((java.lang.String[]) (m_dependencies.toArray(new java.lang.String[0])));
    }

    /**
     * Get tasks in target
     *
     * @return the target list
     */
    public final org.apache.avalon.framework.configuration.Configuration[] getTasks() {
        return ((org.apache.avalon.framework.configuration.Configuration[]) (m_tasks.toArray(new org.apache.avalon.framework.configuration.Configuration[0])));
    }
}