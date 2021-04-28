/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.workspace;
import org.apache.myrmidon.listeners.ProjectListener;
/**
 * Support for the project listener event dispatching.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class ProjectListenerSupport implements org.apache.myrmidon.listeners.ProjectListener {
    private org.apache.myrmidon.listeners.ProjectListener[] m_listeners = new org.apache.myrmidon.listeners.ProjectListener[0];

    /**
     * Add an extra project listener that wants to receive notification of listener events.
     *
     * @param listener
     * 		the listener
     */
    public void addProjectListener(final org.apache.myrmidon.listeners.ProjectListener listener) {
        final org.apache.myrmidon.listeners.ProjectListener[] listeners = new org.apache.myrmidon.listeners.ProjectListener[m_listeners.length + 1];
        java.lang.System.arraycopy(m_listeners, 0, listeners, 0, m_listeners.length);
        listeners[m_listeners.length] = listener;
        m_listeners = listeners;
    }

    /**
     * Remove a project listener that wants to receive notification of listener events.
     *
     * @param listener
     * 		the listener
     */
    public void removeProjectListener(final org.apache.myrmidon.listeners.ProjectListener listener) {
        int found = -1;
        for (int i = 0; i < m_listeners.length; i++) {
            if (listener == m_listeners[i]) {
                found = i;
                break;
            }
        }
        if ((-1) == found)
            return;

        final org.apache.myrmidon.listeners.ProjectListener[] listeners = new org.apache.myrmidon.listeners.ProjectListener[m_listeners.length - 1];
        java.lang.System.arraycopy(m_listeners, 0, listeners, 0, found);
        final int count = (m_listeners.length - found) - 1;
        java.lang.System.arraycopy(m_listeners, found, listeners, found + 1, count);
        m_listeners = listeners;
    }

    /**
     * Fire a projectStarted event.
     */
    public void projectStarted() {
        for (int i = 0; i < m_listeners.length; i++) {
            m_listeners[i].projectStarted();
        }
    }

    /**
     * Fire a projectFinished event.
     */
    public void projectFinished() {
        for (int i = 0; i < m_listeners.length; i++) {
            m_listeners[i].projectFinished();
        }
    }

    /**
     * Fire a targetStarted event.
     *
     * @param targetName
     * 		the name of target
     */
    public void targetStarted(java.lang.String targetName) {
        for (int i = 0; i < m_listeners.length; i++) {
            m_listeners[i].targetStarted(targetName);
        }
    }

    /**
     * Fire a targetFinished event.
     */
    public void targetFinished() {
        for (int i = 0; i < m_listeners.length; i++) {
            m_listeners[i].targetFinished();
        }
    }

    /**
     * Fire a targetStarted event.
     *
     * @param targetName
     * 		the name of target
     */
    public void taskStarted(java.lang.String taskName) {
        for (int i = 0; i < m_listeners.length; i++) {
            m_listeners[i].taskStarted(taskName);
        }
    }

    /**
     * Fire a taskFinished event.
     */
    public void taskFinished() {
        for (int i = 0; i < m_listeners.length; i++) {
            m_listeners[i].taskFinished();
        }
    }

    /**
     * Fire a log event.
     *
     * @param message
     * 		the log message
     */
    public void log(java.lang.String message) {
        for (int i = 0; i < m_listeners.length; i++) {
            m_listeners[i].log(message);
        }
    }

    /**
     * Fire a log event.
     *
     * @param message
     * 		the log message
     * @param throwable
     * 		the throwable to be logged
     */
    public void log(java.lang.String message, java.lang.Throwable throwable) {
        for (int i = 0; i < m_listeners.length; i++) {
            m_listeners[i].log(message, throwable);
        }
    }
}