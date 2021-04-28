/* The Apache Software License, Version 1.1

 Copyright (c) 1999 The Apache Software Foundation.  All rights
 reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in
 the documentation and/or other materials provided with the
 distribution.

 3. The end-user documentation included with the redistribution, if
 any, must include the following acknowlegement:
 "This product includes software developed by the
 Apache Software Foundation (http://www.apache.org/)."
 Alternately, this acknowlegement may appear in the software itself,
 if and wherever such third-party acknowlegements normally appear.

 4. The names "The Jakarta Project", "Ant", and "Apache Software
 Foundation" must not be used to endorse or promote products derived
 from this software without prior written permission. For written
 permission, please contact apache@apache.org.

 5. Products derived from this software may not be called "Apache"
 nor may "Apache" appear in their names without prior written
 permission of the Apache Group.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 SUCH DAMAGE.
 ====================================================================

 This software consists of voluntary contributions made by many
 individuals on behalf of the Apache Software Foundation.  For more
 information on the Apache Software Foundation, please see
 <http://www.apache.org/>.
 */
package org.apache.tools.ant.taskdefs;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import javax.xml.parsers.*;
import org.apache.tools.ant.types.*;
import org.xml.sax.*;
/**
 * Make available the tasks and types from an Ant library. <pre>
 * &lt;antlib library="libname.jar" &gt;
 *   &lt;alias name="nameOnLib" as="newName" /&gt;
 * &lt;/antlib&gt;
 *
 * &lt;antlib file="libname.jar" override="true" /&gt;
 * </pre>
 *
 * @author minor changes by steve loughran, steve_l@iseran.com
 * @author <a href="j_a_fernandez@yahoo.com">Jose Alberto Fernandez</a>
 * @since ant1.5
 */
// end class Antlib
public class Antlib extends Task {
    /**
     * Location of descriptor in library
     */
    public static final java.lang.String ANT_DESCRIPTOR = "META-INF/antlib.xml";

    /**
     * The named classloader to use.
     * Defaults to the default classLoader.
     */
    private java.lang.String loaderId = "";

    /**
     * file attribute
     */
    private java.io.File file = null;

    /**
     * override attribute
     */
    private boolean override = false;

    /**
     * attribute to control failure when loading
     */
    private org.apache.tools.ant.taskdefs.Antlib.FailureAction onerror = new org.apache.tools.ant.taskdefs.Antlib.FailureAction();

    /**
     * classpath to build up
     */
    private org.apache.tools.ant.taskdefs.Path classpath = null;

    /**
     * the manufacture set of classes to load
     */
    private org.apache.tools.ant.taskdefs.Path loaderPath = null;

    /**
     * our little xml parse
     */
    private javax.xml.parsers.SAXParserFactory saxFactory;

    /**
     * table of aliases
     */
    private java.util.Vector aliases = new java.util.Vector();

    private static final int FAIL = 0;

    private static final int REPORT = 1;

    /**
     * Some internal constants.
     */
    private static final int IGNORE = 2;

    /**
     * Posible actions when classes are not found
     */
    public static class FailureAction extends EnumeratedAttribute {
        public java.lang.String[] getValues() {
            return new java.lang.String[]{ "fail", "report", "ignore" };
        }
    }

    private static class DescriptorEnumeration implements java.util.Enumeration {
        /**
         * The name of the resource being searched for.
         */
        private java.lang.String resourceName;

        /**
         * The index of the next file to search.
         */
        private int index;

        /**
         * The list of files to search
         */
        private java.io.File[] files;

        /**
         * The URL of the next resource to return in the enumeration. If this
         * field is <code>null</code> then the enumeration has been completed,
         * i.e., there are no more elements to return.
         */
        private java.net.URL nextDescriptor;

        /**
         * Construct a new enumeration of resources of the given name found
         * within this class loader's classpath.
         *
         * @param name
         * 		the name of the resource to search for.
         */
        DescriptorEnumeration(java.lang.String[] fileNames, java.lang.String name) {
            this.resourceName = name;
            this.index = 0;
            this.files = new java.io.File[fileNames.length];
            for (int i = 0; i < files.length; i++) {
                files[i] = new java.io.File(fileNames[i]);
            }
            findNextDescriptor();
        }

        /**
         * Indicates whether there are more elements in the enumeration to
         * return.
         *
         * @return <code>true</code> if there are more elements in the
        enumeration; <code>false</code> otherwise.
         */
        public boolean hasMoreElements() {
            return this.nextDescriptor != null;
        }

        /**
         * Returns the next resource in the enumeration.
         *
         * @return the next resource in the enumeration.
         */
        public java.lang.Object nextElement() {
            java.net.URL ret = this.nextDescriptor;
            findNextDescriptor();
            return ret;
        }

        /**
         * Locates the next descriptor of the correct name in the files and
         * sets <code>nextDescriptor</code> to the URL of that resource. If no
         * more resources can be found, <code>nextDescriptor</code> is set to
         * <code>null</code>.
         */
        private void findNextDescriptor() {
            java.net.URL url = null;
            while ((index < files.length) && (url == null)) {
                try {
                    url = getDescriptorURL(files[index], this.resourceName);
                    index++;
                } catch (BuildException e) {
                    // ignore path elements which are not valid relative to the
                    // project
                }
            } 
            this.nextDescriptor = url;
        }

        /**
         * Get an URL to a given resource in the given file which may
         * either be a directory or a zip file.
         *
         * @param file
         * 		the file (directory or jar) in which to search for
         * 		the resource. Must not be <code>null</code>.
         * @param resourceName
         * 		the name of the resource for which a URL
         * 		is required. Must not be <code>null</code>.
         * @return a URL to the required resource or <code>null</code> if the
        resource cannot be found in the given file object
         * @todo This code is extracted from AntClassLoader.getResourceURL
        I hate when that happens but the code there is too tied to
        the ClassLoader internals. Maybe we can find a nice place
        to put it where both can use it.
         */
        private java.net.URL getDescriptorURL(java.io.File file, java.lang.String resourceName) {
            try {
                if (!file.exists()) {
                    return null;
                }
                if (file.isDirectory()) {
                    java.io.File resource = new java.io.File(file, resourceName);
                    if (resource.exists()) {
                        try {
                            return new java.net.URL("file:" + resource.toString());
                        } catch (java.net.MalformedURLException ex) {
                            return null;
                        }
                    }
                } else {
                    java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(file);
                    try {
                        java.util.zip.ZipEntry entry = zipFile.getEntry(resourceName);
                        if (entry != null) {
                            try {
                                return new java.net.URL((("jar:file:" + file.toString()) + "!/") + entry);
                            } catch (java.net.MalformedURLException ex) {
                                return null;
                            }
                        }
                    } finally {
                        zipFile.close();
                    }
                }
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * constructor creates a validating sax parser
     */
    public Antlib() {
        super();
        // Default error action
        onerror.setValue("report");
        saxFactory = javax.xml.parsers.SAXParserFactory.newInstance();
        saxFactory.setValidating(false);
    }

    /**
     * constructor binds to a project and sets ignore mode on errors
     *
     * @param p
     * 		Description of Parameter
     */
    public Antlib(Project p) {
        this();
        setProject(p);
    }

    /**
     * Set name of library to load. The library is located in $ANT_HOME/antlib.
     *
     * @param lib
     * 		the name of library relative to $ANT_HOME/antlib.
     */
    public void setLibrary(java.lang.String lib) {
        setFile(libraryFile("antlib", lib));
    }

    /**
     * Set file location of library to load.
     *
     * @param file
     * 		the jar file for the library.
     */
    public void setFile(java.io.File file) {
        this.file = file;
    }

    /**
     * Set the ID of the ClassLoader to use for this library.
     *
     * @param id
     * 		the id for the ClassLoader to use,
     * 		<code>null</code> means use ANT's core classloader.
     */
    public void setLoaderid(java.lang.String id) {
        this.loaderId = id;
    }

    /**
     * Set whether to override any existing definitions.
     *
     * @param override
     * 		if true new definitions will replace existing ones.
     */
    public void setOverride(boolean override) {
        this.override = override;
    }

    /**
     * Get what to do if a definition cannot be loaded
     * This method is mostly used by the core when loading core tasks.
     *
     * @return what to do if a definition cannot be loaded
     */
    protected final org.apache.tools.ant.taskdefs.Antlib.FailureAction getOnerror() {
        return this.onerror;
    }

    /**
     * Set whether to fail if a definition cannot be loaded
     * Default is <code>true</code>.
     * This property is mostly used by the core when loading core tasks.
     *
     * @param failedonerror
     * 		if true loading will stop if classes
     * 		cannot be instantiated
     */
    public void setOnerror(org.apache.tools.ant.taskdefs.Antlib.FailureAction onerror) {
        this.onerror = onerror;
    }

    /**
     * Create new Alias element.
     *
     * @return Description of the Returned Value
     */
    public org.apache.tools.ant.taskdefs.Antlib.Alias createAlias() {
        org.apache.tools.ant.taskdefs.Antlib.Alias als = new org.apache.tools.ant.taskdefs.Antlib.Alias();
        aliases.add(als);
        return als;
    }

    /**
     * Set the classpath to be used for this compilation
     *
     * @param cp
     * 		The new Classpath value
     */
    public void setClasspath(Path cp) {
        if (classpath == null) {
            classpath = cp;
        } else {
            classpath.append(cp);
        }
    }

    /**
     * create a nested classpath element.
     *
     * @return classpath to use
     */
    public org.apache.tools.ant.taskdefs.Path createClasspath() {
        if (classpath == null) {
            classpath = new Path(project);
        }
        return classpath.createPath();
    }

    /**
     * Adds a reference to a CLASSPATH defined elsewhere
     *
     * @param r
     * 		The new ClasspathRef value
     */
    public void setClasspathRef(Reference r) {
        createClasspath().setRefid(r);
    }

    /**
     * Obtain library file from ANT_HOME directory.
     *
     * @param lib
     * 		the library name.
     * @return the File instance of the library
     */
    private java.io.File libraryFile(java.lang.String homeSubDir, java.lang.String lib) {
        // For the time being libraries live in $ANT_HOME/antlib.
        // The idea being that not to load all the jars there anymore
        java.lang.String home = project.getProperty("ant.home");
        if (home == null) {
            throw new BuildException("ANT_HOME not set as required.");
        }
        return new java.io.File(new java.io.File(home, homeSubDir), lib);
    }

    /**
     * actually do the work of loading the library
     *
     * @exception BuildException
     * 		Description of Exception
     */
    public void execute() throws org.apache.tools.ant.taskdefs.BuildException {
        if ((file == null) && (classpath == null)) {
            java.lang.String msg = "Must specify either library or file attribute or classpath.";
            throw new BuildException(msg, location);
        }
        if ((file != null) && (!file.exists())) {
            java.lang.String msg = "Cannot find library: " + file;
            throw new BuildException(msg, location);
        }
        loadDefinitions();
    }

    /**
     * Load definitions in library and classpath
     *
     * @exception BuildException
     * 		failure to access the resource
     */
    public boolean loadDefinitions() throws org.apache.tools.ant.taskdefs.BuildException {
        return loadDefinitions(org.apache.tools.ant.taskdefs.Antlib.ANT_DESCRIPTOR);
    }

    /**
     * Load definitions from resource name in library and classpath
     *
     * @param res
     * 		the name of the resources to load
     * @exception BuildException
     * 		failure to access the resource
     */
    protected final boolean loadDefinitions(java.lang.String res) throws org.apache.tools.ant.taskdefs.BuildException {
        Path path = makeLoaderClasspath();
        java.lang.ClassLoader cl = makeClassLoader(path);
        boolean found = false;
        try {
            for (java.util.Enumeration e = getDescriptors(path, res); e.hasMoreElements();) {
                java.net.URL resURL = ((java.net.URL) (e.nextElement()));
                java.io.InputStream is = resURL.openStream();
                loadDefinitions(cl, is);
                found = true;
            }
            if ((!found) && (onerror.getIndex() != org.apache.tools.ant.taskdefs.Antlib.IGNORE)) {
                java.lang.String sPath = path.toString();
                if ("".equals(sPath.trim())) {
                    sPath = java.lang.System.getProperty("java.classpath");
                }
                java.lang.String msg = (("Cannot find any " + res) + " antlib descriptors in: ") + sPath;
                switch (onerror.getIndex()) {
                    case org.apache.tools.ant.taskdefs.Antlib.FAIL :
                        throw new BuildException(msg);
                    case org.apache.tools.ant.taskdefs.Antlib.REPORT :
                        log(msg, project.MSG_WARN);
                }
            }
        } catch (java.io.IOException io) {
            java.lang.String msg = "Cannot load definitions from: " + res;
            switch (onerror.getIndex()) {
                case org.apache.tools.ant.taskdefs.Antlib.FAIL :
                    throw new BuildException(msg, io);
                case org.apache.tools.ant.taskdefs.Antlib.REPORT :
                    log(io.getMessage(), project.MSG_WARN);
            }
        }
        return found;
    }

    /**
     * Load definitions directly from InputStream.
     *
     * @param is
     * 		InputStream for the Antlib descriptor.
     * @exception BuildException
     * 		trouble
     */
    private void loadDefinitions(java.lang.ClassLoader cl, java.io.InputStream is) throws org.apache.tools.ant.taskdefs.BuildException {
        evaluateDescriptor(cl, processAliases(), is);
    }

    /**
     * get an Enumeration of URLs for all resouces corresponding to the
     * descriptor name.
     *
     * @param res
     * 		the name of the resource to collect
     * @return input stream to the Descriptor or null if none existent
     * @exception BuildException
     * 		io trouble, or it isnt a zipfile
     */
    private java.util.Enumeration getDescriptors(Path path, final java.lang.String res) throws org.apache.tools.ant.taskdefs.BuildException, java.io.IOException {
        if (loaderId == null) {
            // Path cannot be added to the CoreLoader so simply
            // ask for all instances of the resource descriptors
            return project.getCoreLoader().getResources(res);
        }
        return new org.apache.tools.ant.taskdefs.Antlib.DescriptorEnumeration(path.list(), res);
    }

    /**
     * turn the alias list to a property hashtable
     *
     * @return generated property hashtable
     */
    private java.util.Properties processAliases() {
        java.util.Properties p = new java.util.Properties();
        for (java.util.Enumeration e = aliases.elements(); e.hasMoreElements();) {
            org.apache.tools.ant.taskdefs.Antlib.Alias a = ((org.apache.tools.ant.taskdefs.Antlib.Alias) (e.nextElement()));
            p.put(a.name, a.as);
        }
        return p;
    }

    /**
     * create the classpath for this library from the file passed in and
     * any classpath parameters
     *
     * @param file
     * 		library file to use
     * @return classloader using te
     * @exception BuildException
     * 		trouble creating the classloader
     */
    protected java.lang.ClassLoader makeClassLoader(Path clspath) throws org.apache.tools.ant.taskdefs.BuildException {
        if (loaderId == null) {
            log("Loading definitions from CORE, <classpath> ignored", project.MSG_VERBOSE);
            return project.getCoreLoader();
        }
        log((("Using ClassLoader '" + loaderId) + "' to load path: ") + clspath, project.MSG_VERBOSE);
        return project.addToLoader(loaderId, clspath);
    }

    /**
     * Constructs the Path to add to the ClassLoader
     */
    private org.apache.tools.ant.taskdefs.Path makeLoaderClasspath() {
        Path clspath = new Path(project);
        if (file != null)
            clspath.setLocation(file);

        // append any build supplied classpath
        if (classpath != null) {
            clspath.append(classpath);
        }
        return clspath;
    }

    /**
     * parse the antlib descriptor
     *
     * @param cl
     * 		optional classloader
     * @param als
     * 		alias list as property hashtable
     * @param is
     * 		input stream to descriptor
     * @exception BuildException
     * 		trouble
     */
    protected void evaluateDescriptor(java.lang.ClassLoader cl, java.util.Properties als, java.io.InputStream is) throws org.apache.tools.ant.taskdefs.BuildException {
        try {
            javax.xml.parsers.SAXParser saxParser = saxFactory.newSAXParser();
            org.xml.sax.Parser parser = saxParser.getParser();
            org.xml.sax.InputSource inputSource = new org.xml.sax.InputSource(is);
            // inputSource.setSystemId(uri); //URI is nasty for jar entries
            project.log("parsing descriptor for library: " + file, Project.MSG_VERBOSE);
            saxParser.parse(inputSource, new org.apache.tools.ant.taskdefs.Antlib.AntLibraryHandler(cl, als));
        } catch (javax.xml.parsers.ParserConfigurationException exc) {
            throw new BuildException("Parser has not been configured correctly", exc);
        } catch (org.xml.sax.SAXParseException exc) {
            Location location = new Location(org.apache.tools.ant.taskdefs.Antlib.ANT_DESCRIPTOR, exc.getLineNumber(), exc.getColumnNumber());
            java.lang.Throwable t = exc.getException();
            if (t instanceof BuildException) {
                BuildException be = ((BuildException) (t));
                if (be.getLocation() == Location.UNKNOWN_LOCATION) {
                    be.setLocation(location);
                }
                throw be;
            }
            throw new BuildException(exc.getMessage(), t, location);
        } catch (org.xml.sax.SAXException exc) {
            java.lang.Throwable t = exc.getException();
            if (t instanceof BuildException) {
                throw ((BuildException) (t));
            }
            throw new BuildException(exc.getMessage(), t);
        } catch (java.io.IOException exc) {
            throw new BuildException("Error reading library descriptor", exc);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (java.io.IOException ioe) {
                    // ignore this
                }
            }
        }
    }

    /**
     * Parses the document describing the content of the
     * library. An inner class for access to Project.log
     */
    // end inner class AntLibraryHandler
    private class AntLibraryHandler extends org.xml.sax.HandlerBase {
        /**
         * our classloader
         */
        private final java.lang.ClassLoader classloader;

        /**
         * the aliases
         */
        private final java.util.Properties aliasMap;

        /**
         * doc locator
         */
        private org.xml.sax.Locator locator = null;

        private int level = 0;

        private java.lang.String name = null;

        private java.lang.String className = null;

        private java.lang.String adapter = null;

        /**
         * Constructor for the AntLibraryHandler object
         *
         * @param cl
         * 		optional classloader
         * @param als
         * 		alias list
         */
        AntLibraryHandler(java.lang.ClassLoader classloader, java.util.Properties als) {
            this.classloader = classloader;
            this.aliasMap = als;
        }

        /**
         * Sets the DocumentLocator attribute of the AntLibraryHandler
         * object
         *
         * @param locator
         * 		The new DocumentLocator value
         */
        public void setDocumentLocator(org.xml.sax.Locator locator) {
            this.locator = locator;
        }

        private void parseAttributes(java.lang.String tag, org.xml.sax.AttributeList attrs) throws org.xml.sax.SAXParseException {
            name = null;
            className = null;
            adapter = null;
            for (int i = 0, last = attrs.getLength(); i < last; i++) {
                java.lang.String key = attrs.getName(i);
                java.lang.String value = attrs.getValue(i);
                if (key.equals("name")) {
                    name = value;
                } else if (key.equals("class")) {
                    className = value;
                } else if ("role".equals(tag) && key.equals("adapter")) {
                    adapter = value;
                } else {
                    throw new org.xml.sax.SAXParseException(("Unexpected attribute \"" + key) + "\"", locator);
                }
            }
            if ((name == null) || (className == null)) {
                java.lang.String msg = ("Underspecified " + tag) + " declaration.";
                throw new org.xml.sax.SAXParseException(msg, locator);
            }
        }

        /**
         * SAX callback handler
         *
         * @param tag
         * 		XML tag
         * @param attrs
         * 		attributes
         * @exception SAXParseException
         * 		parse trouble
         */
        public void startElement(java.lang.String tag, org.xml.sax.AttributeList attrs) throws org.xml.sax.SAXParseException {
            level++;
            if ("antlib".equals(tag)) {
                if (level > 1) {
                    throw new org.xml.sax.SAXParseException("Unexpected element: " + tag, locator);
                }
                // No attributes to worry about
                return;
            }
            if (level == 1) {
                throw new org.xml.sax.SAXParseException("Missing antlib root element", locator);
            }
            // Must have the two attributes declared
            parseAttributes(tag, attrs);
            try {
                if ("role".equals(tag)) {
                    if (project.isRoleDefined(name)) {
                        java.lang.String msg = "Cannot override role: " + name;
                        log(msg, Project.MSG_WARN);
                        return;
                    }
                    // Defining a new role
                    java.lang.Class clz = loadClass(className);
                    if (clz != null) {
                        project.addRoleDefinition(name, clz, adapter == null ? null : loadClass(adapter));
                    }
                    return;
                }
                // Defining a new element kind
                // check for name alias
                java.lang.String alias = aliasMap.getProperty(name);
                if (alias != null) {
                    name = alias;
                }
                // catch an attempted override of an existing name
                if ((!override) && project.isDefinedOnRole(tag, name)) {
                    java.lang.String msg = (("Cannot override " + tag) + ": ") + name;
                    log(msg, Project.MSG_WARN);
                    return;
                }
                java.lang.Class clz = loadClass(className);
                if (clz != null)
                    project.addDefinitionOnRole(tag, name, clz);

            } catch (BuildException be) {
                switch (onerror.getIndex()) {
                    case org.apache.tools.ant.taskdefs.Antlib.FAIL :
                        throw new org.xml.sax.SAXParseException(be.getMessage(), locator, be);
                    case org.apache.tools.ant.taskdefs.Antlib.REPORT :
                        project.log(be.getMessage(), project.MSG_WARN);
                        break;
                    default :
                        project.log(be.getMessage(), project.MSG_DEBUG);
                }
            }
        }

        public void endElement(java.lang.String tag) {
            level--;
        }

        private java.lang.Class loadClass(java.lang.String className) throws org.xml.sax.SAXParseException {
            java.lang.String msg = null;
            try {
                // load the named class
                java.lang.Class cls;
                if (classloader == null) {
                    cls = java.lang.Class.forName(className);
                } else {
                    cls = classloader.loadClass(className);
                }
                return cls;
            } catch (java.lang.ClassNotFoundException cnfe) {
                msg = ("Class " + className) + " cannot be found";
                if (onerror.getIndex() == org.apache.tools.ant.taskdefs.Antlib.FAIL)
                    throw new org.xml.sax.SAXParseException(msg, locator, cnfe);

            } catch (java.lang.NoClassDefFoundError ncdfe) {
                msg = ("Class " + className) + " cannot be loaded";
                if (onerror.getIndex() == org.apache.tools.ant.taskdefs.Antlib.FAIL)
                    throw new org.xml.sax.SAXParseException(msg, locator);

            }
            if (onerror.getIndex() == org.apache.tools.ant.taskdefs.Antlib.REPORT) {
                project.log(msg, project.MSG_WARN);
            } else {
                project.log(msg, project.MSG_DEBUG);
            }
            return null;
        }
    }

    /**
     * this class is used for alias elements
     *
     * @author slo
     * @created 11 November 2001
     */
    // end inner class alias
    public static class Alias {
        /**
         * Description of the Field
         */
        private java.lang.String name;

        /**
         * Description of the Field
         */
        private java.lang.String as;

        /**
         * Sets the Name attribute of the Alias object
         *
         * @param name
         * 		The new Name value
         */
        public void setName(java.lang.String name) {
            this.name = name;
        }

        /**
         * Sets the As attribute of the Alias object
         *
         * @param as
         * 		The new As value
         */
        public void setAs(java.lang.String as) {
            this.as = as;
        }
    }
}