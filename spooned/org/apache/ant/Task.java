package org.apache.ant;
import java.util.*;
/**
 * In memory container for an Ant target.
 *
 * XXX need a way to query which attributes are valid for this particular
 * task type... Like into Ant object to do this?
 */
public class Task {
    // -----------------------------------------------------------------
    // PRIVATE DATA MEMBERS
    // -----------------------------------------------------------------
    /**
     *
     */
    private java.util.Hashtable attributes = new java.util.Hashtable();

    /**
     * String containing the type of the task.
     */
    private java.lang.String type;

    // -----------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------
    /**
     * Constructs a new Target object with the given name.
     */
    public Task(java.lang.String type) {
        this.type = type;
    }

    // -----------------------------------------------------------------
    // PUBLIC ACCESSOR METHODS
    // -----------------------------------------------------------------
    /**
     *
     */
    public void addAttribute(java.lang.String name, java.lang.String value) {
        attributes.put(name, value);
    }

    public java.lang.String getAttribute(java.lang.String name) {
        return ((java.lang.String) (attributes.get(name)));
    }

    /**
     *
     */
    public java.util.Hashtable getAttributes() {
        return attributes;
    }

    /**
     *
     */
    public java.util.Enumeration getAttributeNames() {
        return attributes.keys();
    }

    /**
     * Returns a String containing the name of this Target.
     */
    public java.lang.String getType() {
        return type;
    }

    /**
     *
     */
    public java.lang.String toString() {
        return "TASK: " + type;
    }
}