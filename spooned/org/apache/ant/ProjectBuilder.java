package org.apache.ant;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
/**
 * Helper class to build Project object trees.
 *
 * XXX right now this class only deals with the primary levels (project/target/task)
 * and nothing else. Also, it only supports attributes....
 *
 * @author James Duncan Davidson (duncan@apache.org)
 */
public class ProjectBuilder {
    // -----------------------------------------------------------------
    // PRIVATE MEMBERS
    // -----------------------------------------------------------------
    /**
     *
     */
    private org.apache.ant.AntFrontEnd frontEnd;

    /**
     *
     */
    private javax.xml.parsers.SAXParserFactory parserFactory;

    /**
     *
     */
    private org.apache.ant.TaskManager taskManager;

    // -----------------------------------------------------------------
    // CONSTRUCTORS
    // -----------------------------------------------------------------
    /**
     * Creates a new project builder that will build projects for the given
     * Ant.
     */
    public ProjectBuilder(AntFrontEnd frontEnd) {
        this.frontEnd = frontEnd;
        taskManager = new TaskManager(frontEnd);
        parserFactory = javax.xml.parsers.SAXParserFactory.newInstance();
        parserFactory.setValidating(false);
    }

    // -----------------------------------------------------------------
    // PUBLIC METHODS
    // -----------------------------------------------------------------
    /**
     * Builds a project from the given file.
     */
    public org.apache.ant.Project buildFromFile(java.io.File file) throws org.apache.ant.AntException {
        try {
            javax.xml.parsers.SAXParser parser = parserFactory.newSAXParser();
            org.apache.ant.ProjectBuilder.BuilderHandlerBase bhb = new org.apache.ant.ProjectBuilder.BuilderHandlerBase();
            bhb.setProjectFileLocation(file);
            parser.parse(file, bhb);
            Project project = bhb.getProject();
            project.setFrontEnd(frontEnd);
            return project;
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            throw new AntException(pce);
        } catch (org.xml.sax.SAXException se) {
            java.lang.Exception e = se.getException();
            if ((e != null) && (e instanceof AntException)) {
                // it's one of our own thrown from inside the parser to stop it
                throw ((AntException) (e));
            }
            throw new AntException(se);
        } catch (java.io.IOException ioe) {
            throw new AntException(ioe);
        }
    }

    /**
     * Returns the TaskManager associated with this ProjectBuilder and
     * the projects that it builds
     */
    public org.apache.ant.TaskManager getTaskManager() {
        return taskManager;
    }

    // -----------------------------------------------------------------
    // INNER CLASSES
    // -----------------------------------------------------------------
    /**
     * Inner class that implements the needed SAX methods to get all the
     * data needed out of a build file.
     */
    class BuilderHandlerBase extends org.xml.sax.HandlerBase {
        private static final int STATE_START = 0;

        private static final int STATE_PROJECT = 1;

        private static final int STATE_TARGET = 2;

        private static final int STATE_TASK = 3;

        private static final int STATE_DESCRIPTION = 4;

        private static final int STATE_PROPERTY = 5;

        private static final int STATE_FINISHED = 99;

        private int state = org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_START;

        private java.util.Vector tagCharDataStack = new java.util.Vector();

        private org.apache.ant.Target currentTarget;

        private org.apache.ant.Task currentTask;

        org.apache.ant.Project project = new Project(frontEnd, taskManager);

        org.apache.ant.Project getProject() {
            return project;
        }

        void setProjectFileLocation(java.io.File file) {
            project.setBaseDir(file.getParentFile());
        }

        public void startElement(java.lang.String name, org.xml.sax.AttributeList atts) throws org.xml.sax.SAXException {
            java.lang.StringBuffer tagCharData = new java.lang.StringBuffer();
            tagCharDataStack.insertElementAt(tagCharData, 0);
            switch (state) {
                case org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_START :
                    if (name.equals("project")) {
                        state = org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_PROJECT;
                        java.lang.String projectName = atts.getValue("name");
                        if (projectName != null) {
                            project.setName(projectName);
                        } else {
                            java.lang.String msg = "Project element doesn't contain a name attribute";
                            AntException ae = new AntException(msg);
                            throw new org.xml.sax.SAXException(ae);
                        }
                        java.lang.String defaultTarget = atts.getValue("default");
                        if (defaultTarget != null) {
                            project.setDefaultTargetName(defaultTarget);
                        }
                        java.lang.String baseDirName = atts.getValue("basedir");
                        if (baseDirName != null) {
                            // XXX need to check to see if base dir exists
                            project.setBaseDir(new java.io.File(baseDirName));
                        }
                    } else {
                        java.lang.String msg = "Project file doesn't contain a project element as " + "its root node";
                        AntException ae = new AntException(msg);
                        throw new org.xml.sax.SAXException(ae);
                    }
                    break;
                case org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_PROJECT :
                    // valid tags in a project object are: description, property, and target
                    if (name.equals("description")) {
                        state = org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_DESCRIPTION;
                    } else if (name.equals("property")) {
                        state = org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_PROPERTY;
                        java.lang.String propertyName = atts.getValue("name");
                        java.lang.String propertyValue = atts.getValue("value");
                        if (propertyName == null) {
                            java.lang.String msg = "Name attribute must be present on property";
                            AntException ae = new AntException(msg);
                            throw new org.xml.sax.SAXException(ae);
                        } else if (propertyValue == null) {
                            java.lang.String msg = "Value attribute must be present on property";
                            AntException ae = new AntException(msg);
                            throw new org.xml.sax.SAXException(ae);
                        } else {
                            project.setProperty(propertyName, propertyValue);
                        }
                    } else if (name.equals("target")) {
                        state = org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_TARGET;
                        java.lang.String targetName = atts.getValue("name");
                        if (targetName != null) {
                            currentTarget = new Target(targetName);
                            project.addTarget(currentTarget);
                        } else {
                            // XXX figure out which target we're talking about!
                            // Like a location
                            java.lang.String msg = "Target element doesn't contain a name attribute";
                            AntException ae = new AntException(msg);
                            throw new org.xml.sax.SAXException(ae);
                        }
                        java.lang.String depends = atts.getValue("depends");
                        if (depends != null) {
                            java.util.StringTokenizer tok = new java.util.StringTokenizer(depends, ",", false);
                            while (tok.hasMoreTokens()) {
                                currentTarget.addDependancy(tok.nextToken().trim());
                            } 
                        }
                        // XXX add dependency checks
                    } else {
                        java.lang.System.out.println("Expecting target, got: " + name);
                        // XXX exception out
                    }
                    break;
                case org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_TARGET :
                    // Valid tags inside target: task
                    state = org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_TASK;
                    // System.out.println("Getting task: " + name + " for target " +
                    // currentTarget);
                    // XXX need to validate that task type (name) exists in system
                    // else exception out.
                    currentTask = new Task(name);
                    currentTarget.addTask(currentTask);
                    for (int i = 0; i < atts.getLength(); i++) {
                        java.lang.String atName = atts.getName(i);
                        java.lang.String atValue = atts.getValue(i);
                        currentTask.addAttribute(atName, atValue);
                    }
                    break;
                case org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_TASK :
                    // data in here needs to be reflected into tasks
                    java.lang.System.out.println("Not yet supporting tags inside of tasks!");
                    java.lang.System.out.println("The project build will probably bust right here");
                    break;
                default :
                    java.lang.System.out.println("I'm not sure, but we're off base here: " + name);
                    // XXX exception out
            }
        }

        public void characters(char[] ch, int start, int length) throws org.xml.sax.SAXException {
            java.lang.StringBuffer buf = ((java.lang.StringBuffer) (tagCharDataStack.elementAt(0)));
            buf.append(ch, start, length);
        }

        public void endElement(java.lang.String name) throws org.xml.sax.SAXException {
            java.lang.StringBuffer elementData = ((java.lang.StringBuffer) (tagCharDataStack.elementAt(0)));
            tagCharDataStack.removeElementAt(0);
            switch (state) {
                case org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_TASK :
                    state = org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_TARGET;
                    break;
                case org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_TARGET :
                    if (name.equals("target")) {
                        state = org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_PROJECT;
                    } else {
                        java.lang.System.out.println("Expecting to get an end of target, got: " + name);
                        // XXX exception out.
                    }
                    break;
                case org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_DESCRIPTION :
                    if (name.equals("description")) {
                        state = org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_PROJECT;
                        project.setDescription(elementData.toString().trim());
                    } else {
                        java.lang.System.out.println("Expecting to get an end of description, got: " + name);
                        // XXX exception out.
                    }
                    break;
                case org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_PROPERTY :
                    if (name.equals("property")) {
                        state = org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_PROJECT;
                    } else {
                        java.lang.System.out.println("Expecting to get end of property, got: " + name);
                        // XXX exception out
                    }
                    break;
                case org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_PROJECT :
                    if (name.equals("project")) {
                        state = org.apache.ant.ProjectBuilder.BuilderHandlerBase.STATE_FINISHED;
                    } else {
                        java.lang.System.out.println("Expecting to get end of project, got: " + name);
                        // XXX exception out;
                    }
                    break;
                default :
                    java.lang.System.out.println("I'm not sure what we are ending here: " + name);
                    // XXX exception out;
            }
        }
    }
}