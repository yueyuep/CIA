/* Copyright (C) The Apache Software Foundation. All rights reserved.

This software is published under the terms of the Apache Software License
version 1.1, a copy of which has been included with this distribution in
the LICENSE file.
 */
package org.apache.myrmidon.components.builder;
import java.io.IOException;
import org.apache.avalon.framework.ExceptionUtil;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.SAXConfigurationHandler;
import org.apache.avalon.framework.logger.AbstractLoggable;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.log.Logger;
import org.apache.myrmidon.api.TaskContext;
import org.apache.myrmidon.components.model.DefaultProject;
import org.apache.myrmidon.components.model.Project;
import org.apache.myrmidon.components.model.Target;
import org.apache.myrmidon.components.model.TypeLib;
import org.apache.myrmidon.framework.Condition;
/**
 * Default implementation to construct project from a build file.
 *
 * @author <a href="mailto:donaldp@apache.org">Peter Donald</a>
 */
public class DefaultProjectBuilder extends org.apache.avalon.framework.logger.AbstractLoggable implements org.apache.myrmidon.components.builder.ProjectBuilder {
    private static final int PROJECT_REFERENCES = 0;

    private static final int LIBRARY_IMPORTS = 1;

    private static final int IMPLICIT_TASKS = 2;

    private static final int TARGETS = 3;

    /**
     * build a project from file.
     *
     * @param source
     * 		the source
     * @return the constructed Project
     * @exception IOException
     * 		if an error occurs
     * @exception Exception
     * 		if an error occurs
     */
    public org.apache.myrmidon.components.model.Project build(final java.lang.String source) throws java.lang.Exception {
        final java.io.File file = new java.io.File(source);
        return build(file, new java.util.HashMap());
    }

    private org.apache.myrmidon.components.model.Project build(final java.io.File file, final java.util.HashMap projects) throws java.lang.Exception {
        final java.net.URL systemID = file.toURL();
        final org.apache.myrmidon.components.model.Project result = ((org.apache.myrmidon.components.model.Project) (projects.get(systemID.toString())));
        if (null != result) {
            return result;
        }
        final org.apache.avalon.framework.configuration.SAXConfigurationHandler handler = new org.apache.avalon.framework.configuration.SAXConfigurationHandler();
        process(systemID, handler);
        final org.apache.avalon.framework.configuration.Configuration configuration = handler.getConfiguration();
        final org.apache.myrmidon.components.model.DefaultProject project = buildProject(file, configuration);
        projects.put(systemID.toString(), project);
        // build using all top-level attributes
        buildTopLevelProject(project, configuration, projects);
        return project;
    }

    protected void process(final java.net.URL systemID, final org.apache.avalon.framework.configuration.SAXConfigurationHandler handler) throws java.lang.Exception {
        final javax.xml.parsers.SAXParserFactory saxParserFactory = javax.xml.parsers.SAXParserFactory.newInstance();
        final javax.xml.parsers.SAXParser saxParser = saxParserFactory.newSAXParser();
        final org.xml.sax.XMLReader parser = saxParser.getXMLReader();
        parser.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        parser.setFeature("http://xml.org/sax/features/namespaces", false);
        // parser.setFeature( "http://xml.org/sax/features/validation", false );
        parser.setContentHandler(handler);
        parser.setErrorHandler(handler);
        parser.parse(systemID.toString());
    }

    /**
     * build project from configuration.
     *
     * @param file
     * 		the file from which configuration was loaded
     * @param configuration
     * 		the configuration loaded
     * @return the created Project
     * @exception IOException
     * 		if an error occurs
     * @exception Exception
     * 		if an error occurs
     * @exception ConfigurationException
     * 		if an error occurs
     */
    private org.apache.myrmidon.components.model.DefaultProject buildProject(final java.io.File file, final org.apache.avalon.framework.configuration.Configuration configuration) throws java.lang.Exception {
        if (!configuration.getName().equals("project")) {
            throw new java.lang.Exception("Project file must be enclosed in project element");
        }
        // get project-level attributes
        final java.lang.String baseDirectoryName = configuration.getAttribute("basedir");
        final java.lang.String defaultTarget = configuration.getAttribute("default");
        // final String name = configuration.getAttribute( "name" );
        // determine base directory for project
        final java.io.File baseDirectory = new java.io.File(file.getParentFile(), baseDirectoryName).getAbsoluteFile();
        getLogger().debug((("Project " + file) + " base directory: ") + baseDirectory);
        // create project and ...
        final org.apache.myrmidon.components.model.DefaultProject project = new org.apache.myrmidon.components.model.DefaultProject();
        project.setDefaultTargetName(defaultTarget);
        project.setBaseDirectory(baseDirectory);
        // project.setName( name );
        return project;
    }

    /**
     * Handle all top level elements in configuration.
     *
     * @param project
     * 		the project
     * @param configuration
     * 		the Configuration
     * @exception Exception
     * 		if an error occurs
     */
    private void buildTopLevelProject(final org.apache.myrmidon.components.model.DefaultProject project, final org.apache.avalon.framework.configuration.Configuration configuration, final java.util.HashMap projects) throws java.lang.Exception {
        final java.util.ArrayList implicitTaskList = new java.util.ArrayList();
        final org.apache.avalon.framework.configuration.Configuration[] children = configuration.getChildren();
        int state = org.apache.myrmidon.components.builder.DefaultProjectBuilder.PROJECT_REFERENCES;
        for (int i = 0; i < children.length; i++) {
            final org.apache.avalon.framework.configuration.Configuration element = children[i];
            final java.lang.String name = element.getName();
            if (org.apache.myrmidon.components.builder.DefaultProjectBuilder.PROJECT_REFERENCES == state) {
                if (name.equals("projectref")) {
                    buildProjectRef(project, element, projects);
                    continue;
                } else {
                    state = org.apache.myrmidon.components.builder.DefaultProjectBuilder.LIBRARY_IMPORTS;
                }
            }
            if (org.apache.myrmidon.components.builder.DefaultProjectBuilder.LIBRARY_IMPORTS == state) {
                if (name.equals("import")) {
                    buildTypeLib(project, element);
                    continue;
                } else {
                    state = org.apache.myrmidon.components.builder.DefaultProjectBuilder.IMPLICIT_TASKS;
                }
            }
            if (org.apache.myrmidon.components.builder.DefaultProjectBuilder.IMPLICIT_TASKS == state) {
                // Check for any implicit tasks here
                if (!name.equals("target")) {
                    implicitTaskList.add(element);
                    continue;
                } else {
                    state = org.apache.myrmidon.components.builder.DefaultProjectBuilder.TARGETS;
                }
            }
            if (name.equals("target"))
                buildTarget(project, element);
            else {
                throw new java.lang.Exception(((("Unknown top-level element " + name) + " at ") + element.getLocation()) + ". Expecting target");
            }
        }
        final org.apache.avalon.framework.configuration.Configuration[] implicitTasks = ((org.apache.avalon.framework.configuration.Configuration[]) (implicitTaskList.toArray(new org.apache.avalon.framework.configuration.Configuration[0])));
        final org.apache.myrmidon.components.model.Target implicitTarget = new org.apache.myrmidon.components.model.Target(null, implicitTasks, null);
        project.setImplicitTarget(implicitTarget);
    }

    private void buildProjectRef(final org.apache.myrmidon.components.model.DefaultProject project, final org.apache.avalon.framework.configuration.Configuration element, final java.util.HashMap projects) throws java.lang.Exception {
        final java.lang.String name = element.getAttribute("name", null);
        final java.lang.String location = element.getAttribute("location", null);
        if (null == name) {
            throw new java.lang.Exception("Malformed projectref without a name attribute at " + element.getLocation());
        }
        if (!validName(name)) {
            throw new java.lang.Exception("Projectref with an invalid name attribute at " + element.getLocation());
        }
        if (null == location) {
            throw new java.lang.Exception("Malformed projectref without a location attribute at " + element.getLocation());
        }
        final java.io.File baseDirectory = project.getBaseDirectory();
        // TODO: standardize and migrate to Avalon-Excalibur.io
        final java.io.File file = new java.io.File(baseDirectory, location);
        final java.lang.String systemID = file.toURL().toString();
        org.apache.myrmidon.components.model.Project other = ((org.apache.myrmidon.components.model.Project) (projects.get(systemID)));
        if (null == other) {
            other = build(file, projects);
        }
        project.addProject(name, other);
    }

    private void buildTypeLib(final org.apache.myrmidon.components.model.DefaultProject project, final org.apache.avalon.framework.configuration.Configuration element) throws java.lang.Exception {
        final java.lang.String library = element.getAttribute("library", null);
        final java.lang.String name = element.getAttribute("name", null);
        final java.lang.String type = element.getAttribute("type", null);
        if (null == library) {
            throw new java.lang.Exception("Malformed import without a library attribute at " + element.getLocation());
        }
        if ((null == name) || (null == type)) {
            if ((null != name) || (null != type)) {
                throw new java.lang.Exception((("Malformed import at " + element.getLocation()) + ". If name or type attribute is specified, both ") + "attributes must be specified.");
            }
        }
        project.addTypeLib(new org.apache.myrmidon.components.model.TypeLib(library, type, name));
    }

    /**
     * Build a target from configuration.
     *
     * @param project
     * 		the project
     * @param task
     * 		the Configuration
     */
    private void buildTarget(final org.apache.myrmidon.components.model.DefaultProject project, final org.apache.avalon.framework.configuration.Configuration target) throws java.lang.Exception {
        final java.lang.String name = target.getAttribute("name", null);
        final java.lang.String depends = target.getAttribute("depends", null);
        final java.lang.String ifCondition = target.getAttribute("if", null);
        final java.lang.String unlessCondition = target.getAttribute("unless", null);
        if (null == name) {
            throw new java.lang.Exception("Discovered un-named target at " + target.getLocation());
        }
        if (!validName(name)) {
            throw new java.lang.Exception("Target with an invalid name at " + target.getLocation());
        }
        getLogger().debug("Parsing target: " + name);
        if ((null != ifCondition) && (null != unlessCondition)) {
            throw new java.lang.Exception(("Discovered invalid target that has both a if and " + "unless condition at ") + target.getLocation());
        }
        org.apache.myrmidon.framework.Condition condition = null;
        if (null != ifCondition) {
            getLogger().debug("Target if condition: " + ifCondition);
            condition = new org.apache.myrmidon.framework.Condition(true, ifCondition);
        } else if (null != unlessCondition) {
            getLogger().debug("Target unless condition: " + unlessCondition);
            condition = new org.apache.myrmidon.framework.Condition(false, unlessCondition);
        }
        java.lang.String[] dependencies = null;
        // apply depends attribute
        if (null != depends) {
            final java.lang.String[] elements = org.apache.avalon.framework.ExceptionUtil.splitString(depends, ",");
            final java.util.ArrayList dependsList = new java.util.ArrayList();
            for (int i = 0; i < elements.length; i++) {
                final java.lang.String dependency = elements[i].trim();
                if (0 == dependency.length()) {
                    throw new java.lang.Exception((("Discovered empty dependency in target " + target.getName()) + " at ") + target.getLocation());
                }
                getLogger().debug("Target dependency: " + dependency);
                dependsList.add(dependency);
            }
            dependencies = ((java.lang.String[]) (dependsList.toArray(new java.lang.String[0])));
        }
        final org.apache.myrmidon.components.model.Target defaultTarget = new org.apache.myrmidon.components.model.Target(condition, target.getChildren(), dependencies);
        // add target to project
        project.addTarget(name, defaultTarget);
    }

    protected boolean validName(final java.lang.String name) {
        if ((-1) != name.indexOf("->"))
            return false;
        else
            return true;

    }
}