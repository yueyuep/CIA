/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.workspace;
import org.apache.myrmidon.components.executor.ExecutionFrame;
import org.apache.myrmidon.components.model.Project;
/**
 * This contains detaisl for each project that is managed by ProjectManager.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public final class ProjectEntry {
    private final org.apache.myrmidon.components.model.Project m_project;

    private final org.apache.myrmidon.components.executor.ExecutionFrame m_frame;

    private final java.util.ArrayList m_targetsCompleted = new java.util.ArrayList();

    public ProjectEntry(final org.apache.myrmidon.components.model.Project project, final org.apache.myrmidon.components.executor.ExecutionFrame frame) {
        m_project = project;
        m_frame = frame;
    }

    public org.apache.myrmidon.components.model.Project getProject() {
        return m_project;
    }

    public org.apache.myrmidon.components.executor.ExecutionFrame getFrame() {
        return m_frame;
    }

    public boolean isTargetCompleted(final java.lang.String target) {
        return m_targetsCompleted.contains(target);
    }

    public void completeTarget(final java.lang.String target) {
        m_targetsCompleted.add(target);
    }
}