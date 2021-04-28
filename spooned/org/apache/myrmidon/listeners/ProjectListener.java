/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.listeners;
/**
 * The interface to implement if you want to receive
 * notification of project status.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public interface ProjectListener {
    /**
     * Notify listener of projectStarted event.
     */
    void projectStarted();

    /**
     * Notify listener of projectFinished event.
     */
    void projectFinished();

    /**
     * Notify listener of targetStarted event.
     *
     * @param targetName
     * 		the name of target
     */
    void targetStarted(java.lang.String targetName);

    /**
     * Notify listener of targetFinished event.
     */
    void targetFinished();

    /**
     * Notify listener of taskStarted event.
     *
     * @param taskName
     * 		the name of task
     */
    void taskStarted(java.lang.String taskName);

    /**
     * Notify listener of taskFinished event.
     */
    void taskFinished();

    /**
     * Notify listener of log message event.
     *
     * @param message
     * 		the message
     */
    void log(java.lang.String message);

    /**
     * Notify listener of log message event.
     *
     * @param message
     * 		the message
     * @param throwable
     * 		the throwable
     */
    void log(java.lang.String message, java.lang.Throwable throwable);
}