package org.apache.ant;
/**
 * Signals a problem while setting up or executing a build.
 *
 * @author James Duncan Davidson (duncan@apache.org)
 */
public class AntException extends java.lang.Exception {
    // -----------------------------------------------------------------
    // PRIVATE MEMBERS
    // -----------------------------------------------------------------
    /**
     * The cause of this exception.
     */
    private java.lang.Throwable cause;

    /**
     * Project within which this exception occured, if applicable.
     */
    private org.apache.ant.Project project;

    /**
     * Target within which this exception occurred, if applicable.
     */
    private org.apache.ant.Target target;

    /**
     * Task within which this exception occurred, if applicable.
     */
    private org.apache.ant.Task task;

    // -----------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------
    /**
     * Constructs a new AntException with no message.
     */
    public AntException() {
        super();
    }

    /**
     * Constructs a new AntException with the given message.
     */
    public AntException(java.lang.String msg) {
        super(msg);
    }

    /**
     * Constructs a new AntException with the given message and cause.
     */
    public AntException(java.lang.String msg, java.lang.Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    /**
     * Constructs a new AntException with the given cause and a
     * detailed message of (cause==null ? null : cause.toString())
     */
    public AntException(java.lang.Throwable cause) {
        super(cause == null ? null : cause.toString());
        this.cause = cause;
    }

    // -----------------------------------------------------------------
    // PUBLIC METHODS
    // -----------------------------------------------------------------
    /**
     * Returns the cause of this exception.
     */
    public java.lang.Throwable getCause() {
        return cause;
    }

    /**
     * Returns the Project within the scope of which this exception occurred,
     * if applicable. Otherwise null.
     */
    public org.apache.ant.Project getProject() {
        return project;
    }

    /**
     * Returns the Target within the scope of which this exception occurred,
     * if applicable. Otherwise null.
     */
    public org.apache.ant.Target getTarget() {
        return target;
    }

    /**
     * Returns the Task wihtin the scope of which this exception occurred,
     * if applicable. Otherwise null.
     */
    public org.apache.ant.Task getTask() {
        return task;
    }

    // -----------------------------------------------------------------
    // PACKAGE METHODS
    // -----------------------------------------------------------------
    /**
     * Sets the project within the scope of which this exception occurred.
     * This method is called by the internal error handling mechanism of
     * Ant before it is propogated out.
     */
    void setProject(org.apache.ant.Project project) {
        this.project = project;
    }

    /**
     * Sets the target within the scope of which this exception occurred.
     * This method is called by the internal error handling mechansim of
     * Ant before it is propogated out.
     */
    void setTarget(org.apache.ant.Target target) {
        this.target = target;
    }

    /**
     * Sets the task within the scope of which this exception occurred.
     * This method is called by the internal error handling mechanism of
     * Ant before it is propogated out.
     */
    void setTask(org.apache.ant.Task task) {
        this.task = task;
    }
}