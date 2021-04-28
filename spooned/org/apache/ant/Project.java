package org.apache.ant;
import java.io.*;
import java.util.*;
/**
 * In memory container for an Ant project.
 *
 * @author James Duncan Davidson (duncan@apache.org)
 */
public class Project {
    // -----------------------------------------------------------------
    // PRIVATE DATA MEMBERS
    // -----------------------------------------------------------------
    /**
     *
     */
    // private Ant ant;
    /**
     * Base directory of this project. Usually this value is the directory
     * where the project file was found, but can be different.
     */
    private java.io.File baseDir;

    /**
     *
     */
    private java.lang.String defaultTargetName;

    /**
     * Short description of the project.
     */
    private java.lang.String description;

    /**
     * Front end that this project communicates to.
     */
    private org.apache.ant.AntFrontEnd frontEnd;

    /**
     * Properties of this project.
     */
    private java.util.Properties properties = new java.util.Properties();

    /**
     * Parent project to this project, if one exists.
     */
    private org.apache.ant.Project parentProject = null;

    /**
     *
     */
    private java.lang.String name;

    /**
     * Hashtable containing all of the targets that are part of this
     * project. Targets are stored in this hashtable using the name
     * of the target as the key and the Target object for the target
     * as the value.
     */
    private java.util.Hashtable targets = new java.util.Hashtable();

    /**
     * TaskManager for this project.
     */
    private org.apache.ant.TaskManager taskManager;

    // -----------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------
    /**
     * Creates a new Project object with the given FrontEnd and TaskManager
     */
    public Project(AntFrontEnd frontEnd, TaskManager taskManager) {
        this.frontEnd = frontEnd;
        this.taskManager = taskManager;
    }

    // -----------------------------------------------------------------
    // PUBLIC  METHODS
    // -----------------------------------------------------------------
    /**
     * Adds a target to this project.
     */
    public void addTarget(Target target) {
        // XXX check out for name, if null, reject!
        targets.put(target.getName(), target);
    }

    /**
     * Returns the base directory of this project.
     */
    public java.io.File getBaseDir() {
        return baseDir;
    }

    /**
     * Returns the default target for this project, if there is one. Otherwise
     * it returns null.
     */
    public java.lang.String getDefaultTargetName() {
        return defaultTargetName;
    }

    /**
     * Returns a short description of this project, if any. If not, returns
     * null.
     */
    public java.lang.String getDescription() {
        return description;
    }

    /**
     * Gets the front end that is running this project.
     */
    public org.apache.ant.AntFrontEnd getFrontEnd() {
        return frontEnd;
    }

    /**
     * Returns the parent Project object to this Project if a parent
     * project exists. If there is not a parent Project object, null
     * is returned.
     */
    public org.apache.ant.Project getParent() {
        return parentProject;
    }

    /**
     * Returns the target identified with the given name. If no target
     * is known by the given name, then null is returned.
     */
    public org.apache.ant.Target getTarget(java.lang.String name) {
        return ((Target) (targets.get(name)));
    }

    /**
     * Gets an exumeration of all the targets that are part of this project.
     */
    public java.util.Enumeration getTargets() {
        return targets.elements();
    }

    /**
     * Gets the name of this project.
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Returns the value of a property. Returns null if the property does
     * not exist.
     */
    public java.lang.String getProperty(java.lang.String propertyName) {
        return properties.getProperty(propertyName);
    }

    /**
     *
     */
    // public void setAnt(Ant ant) {
    // this.ant = ant;
    // }
    /**
     * Sets the base dir for this project.
     */
    public void setBaseDir(java.io.File dir) {
        // XXX should check this to make sure it's a dir!
        baseDir = dir;
    }

    /**
     * Sets the default target for this project.
     */
    public void setDefaultTargetName(java.lang.String targetName) {
        defaultTargetName = targetName;
    }

    /**
     * Sets the description for this project.
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    /**
     * Sets the front end for this project.
     */
    public void setFrontEnd(AntFrontEnd frontEnd) {
        this.frontEnd = frontEnd;
    }

    /**
     * Sets the name of this project.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * Sets a property on this project. If the property is already
     * set, this method will override it.
     */
    public void setProperty(java.lang.String propertyName, java.lang.String propertyValue) {
        properties.put(propertyName, propertyValue);
    }

    /**
     * Starts a build of this project using the default target if one
     * is set.
     */
    public void startBuild() throws org.apache.ant.AntException {
        // XXX need to do something if the default target isn't set..
        // maybe look for target name 'default', then bail?
        startBuild(defaultTargetName);
    }

    /**
     * Starts a build of this project with the entry point at the given
     * target.
     */
    public void startBuild(java.lang.String targetName) throws org.apache.ant.AntException {
        // notify AntFrontEnd that we are starting a build on a project
        frontEnd.notifyProjectStart(this);
        Target target = getTarget(targetName);
        frontEnd.notifyTargetStart(target);
        // XXX don't forget to execute dependancies first!
        Enumeration = target.getTasks().elements();
        // notify frontEnd that we are done
    }

    /**
     * Givens a string representation of this object. Useful for debugging.
     */
    public java.lang.String toString() {
        return "Project name=" + name;
    }
}