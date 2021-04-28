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
package org.apache.ant.builder;
import java.io.IOException;
/**
 * A helper class which allows the build files which have been converted to
 * code to be built.
 *
 * @author Conor MacNeill
 * @created 16 February 2002
 */
public class BuildHelper {
    /**
     * Simple data class for storing info about a fileset.
     *
     * @author Conor MacNeill
     * @created 18 February 2002
     */
    private static class FileSetInfo {
        /**
         * The root directory of this fileset
         */
        private java.io.File root;

        /**
         * the list of files in the file set
         */
        private java.io.File[] files;
    }

    /**
     * The properties which have been defined in the build
     */
    private java.util.Map properties = new java.util.HashMap();

    /**
     * Path objects created in the build
     */
    private java.util.Map paths = new java.util.HashMap();

    /**
     * Filesets created in the build
     */
    private java.util.Map filesets = new java.util.HashMap();

    /**
     * Set a property for the build
     *
     * @param propertyName
     * 		the name of the property
     * @param propertyValue
     * 		the value of the property
     */
    protected void setProperty(java.lang.String propertyName, java.lang.String propertyValue) {
        if (!properties.containsKey(propertyName)) {
            java.lang.String value = resolve(propertyValue);
            properties.put(propertyName, value);
        }
    }

    /**
     * Create a Jar
     *
     * @param basedir
     * 		the base directpory from which files are added to the
     * 		jar
     * @param metaInfDir
     * 		the directory containing the META-INF for the jar
     * @param metaInfIncludes
     * 		the files to be included in the META-INF area of
     * 		the jar
     * @param jarFile
     * 		the file in which the Jar is created
     * @param classpath
     * 		Class-Path attribute in manifest
     * @param mainClass
     * 		Main-Class attribute in manifest
     */
    protected void jar(java.lang.String basedir, java.lang.String jarFile, java.lang.String metaInfDir, java.lang.String metaInfIncludes, java.lang.String classpath, java.lang.String mainClass) {
        try {
            java.io.File base = new java.io.File(resolve(basedir));
            java.io.File jar = new java.io.File(resolve(jarFile));
            java.util.jar.Manifest manifest = new java.util.jar.Manifest();
            java.util.jar.Attributes attributes = manifest.getMainAttributes();
            attributes.putValue("Manifest-Version", "1.0");
            attributes.putValue("Created-By", "Mutant Bootstrap");
            if (classpath != null) {
                attributes.putValue("Class-Path", classpath);
            }
            if (mainClass != null) {
                attributes.putValue("Main-Class", mainClass);
            }
            java.util.jar.JarOutputStream jos = new java.util.jar.JarOutputStream(new java.io.FileOutputStream(jar), manifest);
            addToJar(jos, base, null);
            if (metaInfDir != null) {
                java.io.File[] metaFileSet = buildFileSet(metaInfDir, metaInfIncludes);
                addFilesToJar(jos, new java.io.File(resolve(metaInfDir)), metaFileSet, "META-INF");
            }
            jos.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
            throw new java.lang.RuntimeException("Unable to Jar file");
        }
    }

    /**
     * Compile a set of files
     *
     * @param srcDir
     * 		the source directory
     * @param destDir
     * 		where the compiled classes will go
     * @param classpathRef
     * 		the id of a path object with the classpath for the
     * 		build
     */
    protected void javac(java.lang.String srcDir, java.lang.String destDir, java.lang.String classpathRef) {
        java.util.List javaFiles = new java.util.ArrayList();
        java.lang.String src = resolve(srcDir);
        java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(src, ":");
        while (tokenizer.hasMoreTokens()) {
            java.io.File srcLocation = new java.io.File(tokenizer.nextToken());
            getJavaFiles(srcLocation, javaFiles);
        } 
        java.io.File dest = new java.io.File(resolve(destDir));
        int numArgs = javaFiles.size() + 2;
        if (classpathRef != null) {
            numArgs += 2;
        }
        java.lang.String[] args = new java.lang.String[numArgs];
        int index = 0;
        args[index++] = "-d";
        args[index++] = dest.getPath();
        if (classpathRef != null) {
            java.lang.String path = ((java.lang.String) (paths.get(resolve(classpathRef))));
            args[index++] = "-classpath";
            args[index++] = path;
        }
        for (java.util.Iterator i = javaFiles.iterator(); i.hasNext();) {
            args[index++] = ((java.io.File) (i.next())).getPath();
        }
        try {
            java.lang.Class c = java.lang.Class.forName("com.sun.tools.javac.Main");
            java.lang.Object compiler = c.newInstance();
            java.lang.reflect.Method compile = c.getMethod("compile", new java.lang.Class[]{ new java.lang.String[]{  }.getClass() });
            compile.invoke(compiler, new java.lang.Object[]{ args });
        } catch (java.lang.Exception e) {
            e.printStackTrace();
            throw new java.lang.RuntimeException("Compile failed");
        }
    }

    /**
     * Copy a directory
     *
     * @param fromDir
     * 		the source directory name
     * @param toDir
     * 		the destination directory name
     */
    protected void copyFileset(java.lang.String fromDir, java.lang.String toDir) {
        java.io.File from = new java.io.File(resolve(fromDir));
        java.io.File to = new java.io.File(resolve(toDir));
        copyDir(from, to);
    }

    /**
     * Add a fileset to this build helper
     *
     * @param name
     * 		the name of the fileset (its id)
     * @param root
     * 		the root directory of the fileset
     * @param files
     * 		the files in the fileset
     */
    protected void addFileSet(java.lang.String name, java.io.File root, java.io.File[] files) {
        org.apache.ant.builder.BuildHelper.FileSetInfo info = new org.apache.ant.builder.BuildHelper.FileSetInfo();
        info.root = root;
        info.files = files;
        filesets.put(name, info);
    }

    /**
     * Copy a fileset given a reference to the source fileset
     *
     * @param toDir
     * 		the name of the destination directory
     * @param fileSetRef
     * 		the fileset to be copied
     */
    protected void copyFilesetRef(java.lang.String fileSetRef, java.lang.String toDir) {
        org.apache.ant.builder.BuildHelper.FileSetInfo fileset = ((org.apache.ant.builder.BuildHelper.FileSetInfo) (filesets.get(resolve(fileSetRef))));
        if (fileset != null) {
            java.io.File to = new java.io.File(resolve(toDir));
            copyFileList(fileset.root, fileset.files, to);
        }
    }

    /**
     * Make a directory
     *
     * @param dirName
     * 		the name of the directory path to be created.
     */
    protected void mkdir(java.lang.String dirName) {
        java.io.File dir = new java.io.File(resolve(dirName));
        dir.mkdirs();
    }

    /**
     * Create a path object
     *
     * @param pathName
     * 		the name of the path object in the build
     */
    protected void createPath(java.lang.String pathName) {
        java.lang.String path = "";
        paths.put(pathName, path);
    }

    /**
     * Add a fileset to a path
     *
     * @param pathName
     * 		the name of the path
     * @param filesetDir
     * 		the base directory of the fileset
     * @param filesetIncludes
     * 		the files to be included in the fileset
     */
    protected void addFileSetToPath(java.lang.String pathName, java.lang.String filesetDir, java.lang.String filesetIncludes) {
        java.io.File[] files = buildFileSet(filesetDir, filesetIncludes);
        java.lang.String currentPath = ((java.lang.String) (paths.get(pathName)));
        for (int i = 0; i < files.length; ++i) {
            if ((currentPath == null) || (currentPath.length() == 0)) {
                currentPath = files[i].getPath();
            } else {
                currentPath = (currentPath + java.io.File.pathSeparator) + files[i].getPath();
            }
        }
        paths.put(pathName, currentPath);
    }

    /**
     * Add a new element to a path
     *
     * @param pathName
     * 		the name of the path object to be updated
     * @param location
     * 		the location to be added to the path
     */
    protected void addPathElementToPath(java.lang.String pathName, java.lang.String location) {
        java.lang.String pathElement = resolve(location).replace('/', java.io.File.separatorChar);
        java.lang.String currentPath = ((java.lang.String) (paths.get(pathName)));
        if ((currentPath == null) || (currentPath.length() == 0)) {
            currentPath = pathElement;
        } else {
            currentPath = (currentPath + java.io.File.pathSeparator) + pathElement;
        }
        paths.put(pathName, currentPath);
    }

    /**
     * Add an existing path to another path
     *
     * @param pathName
     * 		the name of the path to which the path is to be added
     * @param pathNameToAdd
     * 		the name of the path to be added.
     */
    protected void addPathToPath(java.lang.String pathName, java.lang.String pathNameToAdd) {
        java.lang.String pathToAdd = ((java.lang.String) (paths.get(pathNameToAdd)));
        if ((pathToAdd == null) || (pathToAdd.length() == 0)) {
            return;
        }
        java.lang.String currentPath = ((java.lang.String) (paths.get(pathName)));
        if ((currentPath == null) || (currentPath.length() == 0)) {
            currentPath = pathToAdd;
        } else {
            currentPath = (currentPath + java.io.File.pathSeparator) + pathToAdd;
        }
        paths.put(pathName, currentPath);
    }

    /**
     * Get the set of Java files to be compiled
     *
     * @param srcDir
     * 		the directory to search (recursively searched)
     * @param javaFiles
     * 		the list of files to which Java files are added
     */
    private void getJavaFiles(java.io.File srcDir, java.util.List javaFiles) {
        java.io.File[] files = srcDir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                getJavaFiles(files[i], javaFiles);
            } else if (files[i].getPath().endsWith(".java")) {
                javaFiles.add(files[i]);
            }
        }
    }

    /**
     * Copy a file
     *
     * @param from
     * 		the source location
     * @param dest
     * 		the destination location
     */
    private void copyFile(java.io.File from, java.io.File dest) {
        if (from.exists()) {
            dest.getParentFile().mkdirs();
            try {
                java.io.FileInputStream in = new java.io.FileInputStream(from);
                java.io.FileOutputStream out = new java.io.FileOutputStream(dest);
                byte[] buf = new byte[1024 * 16];
                int count = 0;
                count = in.read(buf, 0, buf.length);
                while (count != (-1)) {
                    out.write(buf, 0, count);
                    count = in.read(buf, 0, buf.length);
                } 
                in.close();
                out.close();
            } catch (java.io.IOException ioe) {
                ioe.printStackTrace();
                throw new java.lang.RuntimeException("Unable to copy files");
            }
        }
    }

    /**
     * Copy a list of files from one directory to another, preserving the
     * relative paths
     *
     * @param root
     * 		the root of the source directory
     * @param files
     * 		the files to be copied
     * @param to
     * 		the destination directory
     */
    private void copyFileList(java.io.File root, java.io.File[] files, java.io.File to) {
        for (int i = 0; i < files.length; ++i) {
            if (files[i].getName().equals("CVS")) {
                continue;
            }
            java.lang.String name = files[i].getPath().substring(root.getPath().length() + 1);
            java.io.File dest = new java.io.File(to, name);
            if (files[i].isDirectory()) {
                copyDir(files[i], dest);
            } else {
                copyFile(files[i], dest);
            }
        }
    }

    /**
     * Copy a directory
     *
     * @param from
     * 		the source directory
     * @param to
     * 		the destination directory
     */
    private void copyDir(java.io.File from, java.io.File to) {
        to.mkdirs();
        java.io.File[] files = from.listFiles();
        copyFileList(from, files, to);
    }

    /**
     * Add a directory to a Jar
     *
     * @param jos
     * 		the JarOutputStream representing the Jar being created
     * @param dir
     * 		the directory to be added to the jar
     * @param prefix
     * 		the prefix in the jar at which the directory is to be
     * 		added
     * @exception IOException
     * 		if the files cannot be added to the jar
     */
    private void addToJar(java.util.jar.JarOutputStream jos, java.io.File dir, java.lang.String prefix) throws java.io.IOException {
        java.io.File[] files = dir.listFiles();
        addFilesToJar(jos, dir, files, prefix);
    }

    /**
     * Add a set of files to a jar
     *
     * @param jos
     * 		the JarOutputStream representing the Jar being created
     * @param dir
     * 		the directory fro which the files are taken
     * @param prefix
     * 		the prefix in the jar at which the directory is to be
     * 		added
     * @param files
     * 		the list of files to be added to the jar
     * @exception IOException
     * 		if the files cannot be added to the jar
     */
    private void addFilesToJar(java.util.jar.JarOutputStream jos, java.io.File dir, java.io.File[] files, java.lang.String prefix) throws java.io.IOException {
        for (int i = 0; i < files.length; i++) {
            java.lang.String name = files[i].getPath().replace('\\', '/');
            name = name.substring(dir.getPath().length() + 1);
            if (prefix != null) {
                name = (prefix + "/") + name;
            }
            java.util.zip.ZipEntry ze = new java.util.zip.ZipEntry(name);
            jos.putNextEntry(ze);
            if (files[i].isDirectory()) {
                addToJar(jos, files[i], name);
            } else {
                java.io.FileInputStream fis = new java.io.FileInputStream(files[i]);
                int count = 0;
                byte[] buf = new byte[8 * 1024];
                count = fis.read(buf, 0, buf.length);
                while (count != (-1)) {
                    jos.write(buf, 0, count);
                    count = fis.read(buf, 0, buf.length);
                } 
                fis.close();
            }
        }
    }

    /**
     * Build a simple fileset. Only simple inclusion filtering is supported -
     * no complicated patterns.
     *
     * @param filesetDir
     * 		the base directory of the fileset
     * @param filesetIncludes
     * 		the simple includes spec for the fileset
     * @return the fileset expressed as an array of File instances.
     */
    private java.io.File[] buildFileSet(java.lang.String filesetDir, java.lang.String filesetIncludes) {
        if (filesetDir == null) {
            return new java.io.File[0];
        }
        final java.lang.String includes = resolve(filesetIncludes);
        if (includes.indexOf("**") != (-1)) {
            throw new java.lang.RuntimeException("Simple fileset cannot handle ** " + "style includes");
        }
        int index = 0;
        if (includes.charAt(0) == '*') {
            index = 1;
        }
        if (includes.indexOf("*", index) != (-1)) {
            throw new java.lang.RuntimeException("Simple fileset cannot handle * " + "style includes except at start");
        }
        java.io.File base = new java.io.File(resolve(filesetDir));
        return base.listFiles(new java.io.FilenameFilter() {
            public boolean accept(java.io.File dir, java.lang.String name) {
                if (includes.startsWith("*")) {
                    return name.endsWith(includes.substring(1));
                } else {
                    return name.equals(includes);
                }
            }
        });
    }

    /**
     * Resolve the property references in a string
     *
     * @param propertyValue
     * 		the string to be resolved
     * @return the string with property references replaced by their current
    value.
     */
    private java.lang.String resolve(java.lang.String propertyValue) {
        java.lang.String newValue = propertyValue;
        while (newValue.indexOf("${") != (-1)) {
            int index = newValue.indexOf("${");
            int endIndex = newValue.indexOf("}", index);
            java.lang.String propertyName = newValue.substring(index + 2, endIndex);
            java.lang.String repValue = ((java.lang.String) (properties.get(propertyName)));
            newValue = (newValue.substring(0, index) + repValue) + newValue.substring(endIndex + 1);
        } 
        return newValue;
    }
}