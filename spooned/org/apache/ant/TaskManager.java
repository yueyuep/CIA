package org.apache.ant;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
/**
 * Manager of tasks and all things related to tasks. Tasks can be found in a
 * wide number of locations -- and most of these locations require class loading
 * help. As well, new nodes on the task search path may be added at any time.
 * When these are added, new tasks should be scanned for.
 *
 * @author James Duncan Davidson (duncan@apache.org)
 */
public class TaskManager {
    // -----------------------------------------------------------------
    // PRIVATE DATA MEMBERS
    // -----------------------------------------------------------------
    /**
     * FrontEnd that this TaskManager can communicate through.
     */
    private org.apache.ant.AntFrontEnd frontEnd;

    /**
     * Data structure where all the Class definition for all known tasks are
     * held.
     */
    private java.util.Hashtable taskClasses = new java.util.Hashtable();

    /**
     * Data structure that holds all the nodes where tasks are picked up from.
     */
    private java.util.Vector taskPathNodes = new java.util.Vector();

    // -----------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------
    /**
     * Creates a new TaskManager.
     */
    TaskManager(AntFrontEnd frontEnd) {
        this.frontEnd = frontEnd;
    }

    // -----------------------------------------------------------------
    // PUBLIC METHODS
    // -----------------------------------------------------------------
    /**
     * Adds a node to the task path
     */
    public void addTaskPathNode(java.io.File file) throws org.apache.ant.AntException {
        taskPathNodes.addElement(file);
        processTaskPathNode(file);
    }

    // -----------------------------------------------------------------
    // PACKAGE METHODS
    // -----------------------------------------------------------------
    /**
     *
     */
    org.apache.ant.AbstractTask getTaskInstance(java.lang.String taskName) throws org.apache.ant.AntException {
        java.lang.Class clazz = ((java.lang.Class) (taskClasses.get(taskName)));
        try {
            return ((AbstractTask) (clazz.newInstance()));
        } catch (java.lang.Exception e) {
            java.lang.String msg = "Can't instantiate task: " + taskName;
            AntException ae = new AntException(msg, e);
            throw ae;
        }
    }

    // -----------------------------------------------------------------
    // PRIVATE METHODS
    // -----------------------------------------------------------------
    /**
     * Returns an enum of the task names that are defined in a given
     * properties file.
     */
    private java.util.Enumeration getTaskNames(java.util.Properties props) {
        java.util.Vector v = new java.util.Vector();
        java.lang.String s = props.getProperty("tasks");
        java.util.StringTokenizer tok = new java.util.StringTokenizer(s, ",", false);
        while (tok.hasMoreTokens()) {
            java.lang.String taskName = tok.nextToken().trim();
            v.addElement(taskName);
        } 
        return v.elements();
    }

    /**
     * Processes a directory to get class defintions from it
     */
    private void processDir(java.io.File dir) {
        frontEnd.writeMessage(("Scanning " + dir) + " for tasks", AntFrontEnd.MSG_LEVEL_LOW);
        java.io.File file = new java.io.File(dir, "taskdef.properties");
        java.io.InputStream in = new java.io.FileInputStream(file);
        java.util.Properties props = new java.util.Properties();
        props.load(in);
        in.close();
        Enumeration = getTaskNames(props);
        // XXX error out and stop....
        // XXX error out and stop the build
    }

    /**
     * Processes a jar file to get class definitions from it
     */
    private void processJar(java.io.File file) throws org.apache.ant.AntException {
        frontEnd.writeMessage(("Scanning " + file) + " for tasks", AntFrontEnd.MSG_LEVEL_LOW);
        java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(file);
        java.util.zip.ZipEntry zipEntry = zipFile.getEntry("taskdef.properties");
        java.io.InputStream in = zipFile.getInputStream(zipEntry);
        java.util.Properties props = new java.util.Properties();
        props.load(in);
        in.close();
        Enumeration = getTaskNames(props);
        // XXX error out and stop....
        // make sure to not leave resources hanging
        // XXX need to exception out of here properly to stop things
    }

    /**
     * Processes a node of the task path searching for task definitions there
     * and adding them to the list of known tasks
     */
    private void processTaskPathNode(java.io.File file) throws org.apache.ant.AntException {
        // task path nodes can be any of the following:
        // * jar file
        // * directory of jar files
        // * directory holding class files
        if (file.isDirectory()) {
            // first look for all jar files here
            // second look for a taskdefs.properties here to see if we should
            // treat the directory as a classpath
            java.lang.String[] files = file.list();
            for (int i = 0; i < files.length; i++) {
                if (files[i].endsWith(".jar")) {
                    processJar(new java.io.File(file, files[i]));
                } else if (files[i].equals("taskdef.properties")) {
                    processDir(file);
                }
            }
        } else if (file.getName().endsWith(".jar")) {
            processJar(file);
        }
    }

    /**
     * Sets up the taskpath based on the currently running operating
     * system. In general, the ordering of the taskpath is: user directory,
     * system directory, and then installation. This allows users or
     * system admins to override or add tasks.
     */
    private void setUpTaskPath() throws org.apache.ant.AntException {
        // 1st, add user's home dir.
        java.io.File f;
        java.lang.String userHome = java.lang.System.getProperty("user.home");
        // generic unix
        f = new java.io.File(userHome + ".ant", "tasks");
        if (f.exists() && f.isDirectory()) {
            addTaskPathNode(f);
        }
        // macos x
        f = new java.io.File(userHome + "/Library/Ant", "Tasks");
        if (f.exists() && f.isDirectory()) {
            addTaskPathNode(f);
        }
        // windows -- todo
        // 2nd, add system local dir.
        // generic unix
        f = new java.io.File("/usr/local/ant/tasks");
        if (f.exists() && f.isDirectory()) {
            addTaskPathNode(f);
        }
        // macos x
        f = new java.io.File("/Library/Ant/Tasks");
        if (f.exists() && f.isDirectory()) {
            addTaskPathNode(f);
        }
        // windows -- todo
        // 3rd, add installation local dir.
        // System.out.println("BASE: " + this.getClass().getResource("/"));
        // XXX ---- not really sure how the best way of getting this info is...
        // hafta think about it.
    }
}