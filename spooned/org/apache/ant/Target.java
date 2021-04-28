package org.apache.ant;
import java.util.*;
/**
 * In memory container for an Ant target.
 */
public class Target {
    // -----------------------------------------------------------------
    // PRIVATE DATA MEMBERS
    // -----------------------------------------------------------------
    /**
     * String containing the name of the target. This name must be
     * unique withing a project.
     */
    private java.lang.String name;

    /**
     * Vector containing the names of the targets that this target
     * depends on.
     */
    private java.util.Vector dependsList = new java.util.Vector();

    /**
     * Vector containing the tasks that are part of this target.
     */
    private java.util.Vector tasks = new java.util.Vector();

    // -----------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------
    /**
     * Constructs a new Target object with the given name.
     */
    public Target(java.lang.String name) {
        this.name = name;
    }

    // -----------------------------------------------------------------
    // PUBLIC ACCESSOR METHODS
    // -----------------------------------------------------------------
    /**
     * Adds a dependancy to this task.
     */
    public void addDependancy(java.lang.String targetName) {
        dependsList.addElement(targetName);
    }

    /**
     *
     */
    public void addTask(Task task) {
        tasks.addElement(task);
    }

    /**
     * Returns a String containing the name of this Target.
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     *
     */
    public java.lang.String toString() {
        return "TARGET: " + name;
    }

    /**
     * Returns a Vector of Tasks contained in this Target.
     * <p>
     * Please use caution when using this method. I am not happy
     * about exposing this data as something other than a
     * Collection, but don't want to use 1.1 collections. So,
     * this method may change in the future. You have been warned.
     */
    public java.util.Vector getTasks() {
        return tasks;
    }
}