/* The Apache Software License, Version 1.1

Copyright (c) 2002 The Apache Software Foundation.  All rights
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
package org.apache.ant.antcore.antlib;
import java.net.MalformedURLException;
import org.apache.ant.antcore.xml.ParseContext;
import org.apache.ant.antcore.xml.XMLParseException;
import org.apache.ant.common.util.CircularDependencyChecker;
import org.apache.ant.common.util.CircularDependencyException;
import org.apache.ant.common.util.ExecutionException;
import org.apache.ant.init.InitConfig;
import org.apache.ant.init.InitUtils;
import org.apache.ant.init.LoaderUtils;
/**
 * This class manages the configuration of Ant Libraries
 *
 * @author Conor MacNeill
 * @created 29 January 2002
 */
public class AntLibManager {
    /**
     * The list of extensions which are examined for ant libraries
     */
    public static final java.lang.String[] ANTLIB_EXTENSIONS = new java.lang.String[]{ ".tsk", ".jar", ".zip" };

    /**
     * Flag which indicates whether non-file URLS are used
     */
    private boolean remoteAllowed;

    /**
     * Constructor for the AntLibManager object
     *
     * @param remoteAllowed
     * 		true if remote libraries can be used and
     * 		configured
     */
    public AntLibManager(boolean remoteAllowed) {
        this.remoteAllowed = remoteAllowed;
    }

    /**
     * Add all the Ant libraries that can be found at the given URL
     *
     * @param librarySpecs
     * 		A map to which additional library specifications
     * 		are added.
     * @param libURL
     * 		the URL from which Ant libraries are to be loaded
     * @exception MalformedURLException
     * 		if the URL for the individual
     * 		library components cannot be formed
     * @exception ExecutionException
     * 		if the library specs cannot be parsed
     */
    public void addAntLibraries(java.util.Map librarySpecs, java.net.URL libURL) throws java.net.MalformedURLException, org.apache.ant.common.util.ExecutionException {
        java.net.URL[] libURLs = org.apache.ant.init.LoaderUtils.getLocationURLs(libURL, libURL.toString(), org.apache.ant.antcore.antlib.AntLibManager.ANTLIB_EXTENSIONS);
        if (libURLs == null) {
            return;
        }
        // parse each task library to get its library definition
        for (int i = 0; i < libURLs.length; ++i) {
            java.net.URL antLibraryURL = new java.net.URL(("jar:" + libURLs[i]) + "!/META-INF/antlib.xml");
            try {
                org.apache.ant.antcore.antlib.AntLibrarySpec antLibrarySpec = parseLibraryDef(antLibraryURL);
                if (antLibrarySpec != null) {
                    java.lang.String libraryId = antLibrarySpec.getLibraryId();
                    if (librarySpecs.containsKey(libraryId)) {
                        org.apache.ant.antcore.antlib.AntLibrarySpec currentSpec = ((org.apache.ant.antcore.antlib.AntLibrarySpec) (librarySpecs.get(libraryId)));
                        throw new org.apache.ant.common.util.ExecutionException((((((("Found more than one " + "copy of library with id = ") + libraryId) + " (") + libURLs[i]) + ") + existing library at (") + currentSpec.getLibraryURL()) + ")");
                    }
                    antLibrarySpec.setLibraryURL(libURLs[i]);
                    librarySpecs.put(libraryId, antLibrarySpec);
                }
            } catch (org.apache.ant.antcore.xml.XMLParseException e) {
                java.lang.Throwable t = e.getCause();
                // ignore file not found exceptions - means the
                // jar does not provide META-INF/antlib.xml
                if (!(t instanceof java.io.FileNotFoundException)) {
                    throw new org.apache.ant.common.util.ExecutionException("Unable to parse Ant library " + libURLs[i], e);
                }
            }
        }
    }

    /**
     * Configures the Ant Libraries. Configuration of an Ant Library
     * involves resolving any dependencies between libraries and then
     * creating the class loaders for the library
     *
     * @param librarySpecs
     * 		the loaded specifications of the Ant libraries
     * @param initConfig
     * 		the Ant initialization configuration
     * @param libraries
     * 		the collection of libraries already configured
     * @param libPathsMap
     * 		a map of lists of library paths for each library
     * @exception ExecutionException
     * 		if a library cannot be configured from
     * 		the given specification
     */
    public void configLibraries(org.apache.ant.init.InitConfig initConfig, java.util.Map librarySpecs, java.util.Map libraries, java.util.Map libPathsMap) throws org.apache.ant.common.util.ExecutionException {
        // check if any already defined
        for (java.util.Iterator i = librarySpecs.keySet().iterator(); i.hasNext();) {
            java.lang.String libraryId = ((java.lang.String) (i.next()));
            if (libraries.containsKey(libraryId)) {
                org.apache.ant.antcore.antlib.AntLibrary currentVersion = ((org.apache.ant.antcore.antlib.AntLibrary) (libraries.get(libraryId)));
                throw new org.apache.ant.common.util.ExecutionException((("Ant Library \"" + libraryId) + "\" is already loaded from ") + currentVersion.getDefinitionURL());
            }
        }
        org.apache.ant.common.util.CircularDependencyChecker configuring = new org.apache.ant.common.util.CircularDependencyChecker("configuring Ant libraries");
        for (java.util.Iterator i = librarySpecs.keySet().iterator(); i.hasNext();) {
            java.lang.String libraryId = ((java.lang.String) (i.next()));
            if (!libraries.containsKey(libraryId)) {
                configLibrary(initConfig, librarySpecs, libraryId, configuring, libraries, libPathsMap);
            }
        }
    }

    /**
     * Load either a set of libraries or a single library.
     *
     * @param libLocationURL
     * 		URL where libraries can be found
     * @param librarySpecs
     * 		A collection of library specs which will be
     * 		populated with the libraries found
     * @exception ExecutionException
     * 		if the libraries cannot be loaded
     * @exception MalformedURLException
     * 		if the library's location cannot be
     * 		formed
     */
    public void loadLibs(java.util.Map librarySpecs, java.net.URL libLocationURL) throws org.apache.ant.common.util.ExecutionException, java.net.MalformedURLException {
        if ((!libLocationURL.getProtocol().equals("file")) && (!remoteAllowed)) {
            throw new org.apache.ant.common.util.ExecutionException(((("The config library " + "location \"") + libLocationURL) + "\" cannot be used because config does ") + "not allow remote libraries");
        }
        addAntLibraries(librarySpecs, libLocationURL);
    }

    /**
     * Load either a set of libraries or a single library.
     *
     * @param libLocationString
     * 		URL or file where libraries can be found
     * @param librarySpecs
     * 		A collection of library specs which will be
     * 		populated with the libraries found
     * @exception ExecutionException
     * 		if the libraries cannot be loaded
     * @exception MalformedURLException
     * 		if the library's location cannot be
     * 		formed
     */
    public void loadLibs(java.util.Map librarySpecs, java.lang.String libLocationString) throws org.apache.ant.common.util.ExecutionException, java.net.MalformedURLException {
        java.io.File libLocation = new java.io.File(libLocationString);
        if (!libLocation.exists()) {
            try {
                loadLibs(librarySpecs, new java.net.URL(libLocationString));
            } catch (java.net.MalformedURLException e) {
                // XXX
            }
        } else {
            addAntLibraries(librarySpecs, org.apache.ant.init.InitUtils.getFileURL(libLocation));
        }
    }

    /**
     * Add a library path to the given library
     *
     * @param antLibrary
     * 		the library to which the path is to be added
     * @param path
     * 		the path to be added
     * @exception ExecutionException
     * 		if remote paths are not allowed by
     * 		configuration
     */
    public void addLibPath(org.apache.ant.antcore.antlib.AntLibrary antLibrary, java.net.URL path) throws org.apache.ant.common.util.ExecutionException {
        if ((!path.getProtocol().equals("file")) && (!remoteAllowed)) {
            throw new org.apache.ant.common.util.ExecutionException(("Remote libpaths are not" + " allowed: ") + path);
        }
        antLibrary.addLibraryURL(path);
    }

    /**
     * Configure a library from a specification and the Ant init config.
     *
     * @param initConfig
     * 		Ant's init config passed in from the front end.
     * @param librarySpecs
     * 		the library specs from which this library is to
     * 		be configured.
     * @param libraryId
     * 		the global identifier for the library
     * @param configuring
     * 		A circualr dependency chcker for library
     * 		dependencies.
     * @param libraries
     * 		the collection of libraries which have already been
     * 		configured
     * @param libPathsMap
     * 		a map of lists of library patsh fro each library
     * @exception ExecutionException
     * 		if the library cannot be configured.
     */
    private void configLibrary(org.apache.ant.init.InitConfig initConfig, java.util.Map librarySpecs, java.lang.String libraryId, org.apache.ant.common.util.CircularDependencyChecker configuring, java.util.Map libraries, java.util.Map libPathsMap) throws org.apache.ant.common.util.ExecutionException {
        try {
            configuring.visitNode(libraryId);
            org.apache.ant.antcore.antlib.AntLibrarySpec librarySpec = ((org.apache.ant.antcore.antlib.AntLibrarySpec) (librarySpecs.get(libraryId)));
            java.lang.String extendsId = librarySpec.getExtendsLibraryId();
            if (extendsId != null) {
                if (!libraries.containsKey(extendsId)) {
                    if (!librarySpecs.containsKey(extendsId)) {
                        throw new org.apache.ant.common.util.ExecutionException(((("Could not find library, " + extendsId) + ", upon which library ") + libraryId) + " depends");
                    }
                    configLibrary(initConfig, librarySpecs, extendsId, configuring, libraries, libPathsMap);
                }
            }
            // now create the library for the specification
            org.apache.ant.antcore.antlib.AntLibrary antLibrary = new org.apache.ant.antcore.antlib.AntLibrary(librarySpec);
            // determine the URLs required for this task. These are the
            // task URL itself, the XML parser URLs if required, the
            // tools jar URL if required
            java.util.List urlsList = new java.util.ArrayList();
            if (librarySpec.getLibraryURL() != null) {
                urlsList.add(librarySpec.getLibraryURL());
            }
            if (librarySpec.isToolsJarRequired() && (initConfig.getToolsJarURL() != null)) {
                urlsList.add(initConfig.getToolsJarURL());
            }
            if (librarySpec.usesAntXML()) {
                java.net.URL[] parserURLs = initConfig.getParserURLs();
                for (int i = 0; i < parserURLs.length; ++i) {
                    urlsList.add(parserURLs[i]);
                }
            }
            for (java.util.Iterator i = urlsList.iterator(); i.hasNext();) {
                antLibrary.addLibraryURL(((java.net.URL) (i.next())));
            }
            if (extendsId != null) {
                org.apache.ant.antcore.antlib.AntLibrary extendsLibrary = ((org.apache.ant.antcore.antlib.AntLibrary) (libraries.get(extendsId)));
                antLibrary.setExtendsLibrary(extendsLibrary);
            }
            antLibrary.setParentLoader(initConfig.getCommonLoader());
            libraries.put(libraryId, antLibrary);
            if (libPathsMap != null) {
                java.util.List libPaths = ((java.util.List) (libPathsMap.get(libraryId)));
                if (libPaths != null) {
                    for (java.util.Iterator j = libPaths.iterator(); j.hasNext();) {
                        java.net.URL pathURL = ((java.net.URL) (j.next()));
                        addLibPath(antLibrary, pathURL);
                    }
                }
            }
            configuring.leaveNode(libraryId);
        } catch (org.apache.ant.common.util.CircularDependencyException e) {
            throw new org.apache.ant.common.util.ExecutionException(e);
        }
    }

    /**
     * Read an Ant library definition from a URL
     *
     * @param antlibURL
     * 		the URL of the library definition
     * @return the AntLibrary specification read from the library XML
    definition
     * @exception XMLParseException
     * 		if the library cannot be parsed
     */
    private org.apache.ant.antcore.antlib.AntLibrarySpec parseLibraryDef(java.net.URL antlibURL) throws org.apache.ant.antcore.xml.XMLParseException {
        org.apache.ant.antcore.xml.ParseContext context = new org.apache.ant.antcore.xml.ParseContext();
        org.apache.ant.antcore.antlib.AntLibHandler libHandler = new org.apache.ant.antcore.antlib.AntLibHandler();
        context.parse(antlibURL, "antlib", libHandler);
        return libHandler.getAntLibrarySpec();
    }
}