/* The Apache Software License, Version 1.1

Copyright (c) 2001 The Apache Software Foundation.  All rights
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
package org.apache.ant.bootstrap;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.zip.*;
/**
 * Bootstrap class to build the rest of ant with a minimum of user intervention
 *
 * The bootstrap class is able to act as a class loader to load new classes/jars
 * into the VM in which it is running.
 *
 * @author <a href="mailto:conor@apache.org">Conor MacNeill</a>
 */
public class BootstrapLoader extends java.lang.ClassLoader {
    public static final java.lang.String RECURSION_GUARD = "ant.bootstrap.recursionGuard";

    private static final int BUFFER_SIZE = 1024;

    private java.lang.String[] classpathElements;

    public BootstrapLoader(java.lang.String classpath) {
        java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(classpath, java.io.File.pathSeparator);
        classpathElements = new java.lang.String[tokenizer.countTokens()];
        for (int i = 0; tokenizer.hasMoreTokens(); ++i) {
            classpathElements[i] = tokenizer.nextToken();
        }
    }

    protected java.lang.Class findClass(java.lang.String name) throws java.lang.ClassNotFoundException {
        java.lang.String resourceName = name.replace('.', '/') + ".class";
        java.io.InputStream classStream = getResourceStream(resourceName);
        if (classStream == null) {
            throw new java.lang.ClassNotFoundException();
        }
        try {
            return getClassFromStream(classStream, name);
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
            throw new java.lang.ClassNotFoundException();
        }
    }

    /**
     * Get a stream to read the requested resource name.
     *
     * @param name
     * 		the name of the resource for which a stream is required.
     * @return a stream to the required resource or null if the resource cannot be
    found on the loader's classpath.
     */
    private java.io.InputStream getResourceStream(java.lang.String name) {
        // we need to search the components of the path to see if we can find the
        // class we want.
        java.io.InputStream stream = null;
        for (int i = 0; (i < classpathElements.length) && (stream == null); ++i) {
            java.io.File pathComponent = new java.io.File(classpathElements[i]);
            stream = getResourceStream(pathComponent, name);
        }
        return stream;
    }

    /**
     * Get a stream to read the requested resource name.
     *
     * @param name
     * 		the name of the resource for which a stream is required.
     * @return a stream to the required resource or null if the resource cannot be
    found on the loader's classpath.
     */
    public java.io.InputStream getResourceAsStream(java.lang.String name) {
        return getResourceStream(name);
    }

    protected java.lang.Class loadClass(java.lang.String name, boolean resolve) throws java.lang.ClassNotFoundException {
        java.lang.Class requestedClass = findLoadedClass(name);
        try {
            if (requestedClass == null) {
                requestedClass = findClass(name);
                if (resolve) {
                    resolveClass(requestedClass);
                }
            }
            return requestedClass;
        } catch (java.lang.ClassNotFoundException cnfe) {
            return super.loadClass(name, resolve);
        }
    }

    /**
     * Get an inputstream to a given resource in the given file which may
     * either be a directory or a zip file.
     *
     * @param file
     * 		the file (directory or jar) in which to search for the resource.
     * @param resourceName
     * 		the name of the resource for which a stream is required.
     * @return a stream to the required resource or null if the resource cannot be
    found in the given file object
     */
    private java.io.InputStream getResourceStream(java.io.File file, java.lang.String resourceName) {
        try {
            if (!file.exists()) {
                return null;
            }
            if (file.isDirectory()) {
                java.io.File resource = new java.io.File(file, resourceName);
                if (resource.exists()) {
                    return new java.io.FileInputStream(resource);
                }
            } else {
                java.util.zip.ZipFile zipFile = null;
                try {
                    zipFile = new java.util.zip.ZipFile(file);
                    java.util.zip.ZipEntry entry = zipFile.getEntry(resourceName);
                    if (entry != null) {
                        // we need to read the entry out of the zip file into
                        // a baos and then
                        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                        byte[] buffer = new byte[org.apache.ant.bootstrap.BootstrapLoader.BUFFER_SIZE];
                        int bytesRead;
                        java.io.InputStream stream = zipFile.getInputStream(entry);
                        while ((bytesRead = stream.read(buffer, 0, org.apache.ant.bootstrap.BootstrapLoader.BUFFER_SIZE)) != (-1)) {
                            baos.write(buffer, 0, bytesRead);
                        } 
                        return new java.io.ByteArrayInputStream(baos.toByteArray());
                    }
                } finally {
                    if (zipFile != null) {
                        zipFile.close();
                    }
                }
            }
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Read a class definition from a stream.
     *
     * @param stream
     * 		the stream from which the class is to be read.
     * @param classname
     * 		the class name of the class in the stream.
     * @return the Class object read from the stream.
     * @throws IOException
     * 		if there is a problem reading the class from the
     * 		stream.
     */
    private java.lang.Class getClassFromStream(java.io.InputStream stream, java.lang.String classname) throws java.io.IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        int bytesRead = -1;
        byte[] buffer = new byte[1024];
        while ((bytesRead = stream.read(buffer, 0, 1024)) != (-1)) {
            baos.write(buffer, 0, bytesRead);
        } 
        byte[] classData = baos.toByteArray();
        return defineClass(classname, classData, 0, classData.length);
    }

    private static void buildAnt() {
        java.lang.System.out.println("Bootstrapping Ant ...");
    }

    private static void runWithToolsJar(java.lang.String[] args) {
        try {
            java.lang.String javaHome = java.lang.System.getProperty("java.home");
            if (javaHome.endsWith("jre")) {
                javaHome = javaHome.substring(0, javaHome.length() - 4);
            }
            java.io.File toolsjar = new java.io.File(javaHome + "/lib/tools.jar");
            if (!toolsjar.exists()) {
                java.lang.System.out.println("Unable to locate tools.jar. expected it to be in " + toolsjar.getPath());
                return;
            }
            java.lang.String newclasspath = (toolsjar.getPath() + java.io.File.pathSeparator) + java.lang.System.getProperty("java.class.path");
            java.lang.System.out.println("New Classpath is " + newclasspath);
            org.apache.ant.bootstrap.BootstrapLoader loader = new org.apache.ant.bootstrap.BootstrapLoader(newclasspath);
            java.lang.Class newBootClass = loader.loadClass("org.apache.ant.bootstrap.BootstrapLoader", true);
            final java.lang.Class[] param = new java.lang.Class[]{ java.lang.Class.forName("[Ljava.lang.String;") };
            final java.lang.reflect.Method main = newBootClass.getMethod("main", param);
            final java.lang.Object[] argument = new java.lang.Object[]{ args };
            main.invoke(null, argument);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            throw new java.lang.RuntimeException("Unable to run boot with tools.jar");
        }
    }

    public static void main(java.lang.String[] args) {
        // check whether the tools.jar is already in the classpath.
        try {
            java.lang.Class compilerClass = java.lang.Class.forName("sun.tools.javac.Main");
            java.lang.System.out.println("Compiler is available");
        } catch (java.lang.ClassNotFoundException cnfe) {
            if (java.lang.System.getProperty(org.apache.ant.bootstrap.BootstrapLoader.RECURSION_GUARD) != null) {
                cnfe.printStackTrace();
                java.lang.System.out.println("Unable to load compiler");
                return;
            }
            java.lang.System.setProperty(org.apache.ant.bootstrap.BootstrapLoader.RECURSION_GUARD, "yes");
            java.lang.System.out.println("Compiler is not on classpath - locating ...");
            org.apache.ant.bootstrap.BootstrapLoader.runWithToolsJar(args);
            return;
        }
        org.apache.ant.bootstrap.BootstrapLoader.buildAnt();
    }
}