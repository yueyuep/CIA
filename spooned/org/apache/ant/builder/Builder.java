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
/**
 * The Builder object builds the code for bootstrap purposes. It invokes the
 * mathods of the required targets in the converted build files.
 *
 * @author Conor MacNeill
 * @created 18 February 2002
 */
public class Builder {
    /**
     * The root of the Ant1 source tree
     */
    private static final java.io.File ANT1_SRC_ROOT = new java.io.File("../../src/main");

    /**
     * the root of the Ant package in the Ant1 source tree
     */
    private static final java.io.File PACKAGE_ROOT = new java.io.File(org.apache.ant.builder.Builder.ANT1_SRC_ROOT, "org/apache/tools/ant");

    /**
     * The zip utilities root
     */
    private static final java.io.File ZIP_ROOT = new java.io.File(org.apache.ant.builder.Builder.ANT1_SRC_ROOT, "org/apache/tools/zip");

    /**
     * the taskdefs root
     */
    private static final java.io.File TASKDEFS_ROOT = new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "taskdefs");

    /**
     * the types root
     */
    private static final java.io.File TYPES_ROOT = new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "types");

    /**
     * the filters root
     */
    private static final java.io.File FILTERS_ROOT = new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "filters");

    /**
     * the util root
     */
    private static final java.io.File UTIL_ROOT = new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "util");

    /**
     * the input root
     */
    private static final java.io.File INPUT_ROOT = new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "input");

    /**
     * the root forthe depend task's support classes
     */
    private static final java.io.File DEPEND_ROOT = new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "optional/depend");

    /**
     * The main program - create a builder and run the build
     *
     * @param args
     * 		the command line arguments - not currently used
     */
    public static void main(java.lang.String[] args) {
        org.apache.ant.builder.Builder builder = new org.apache.ant.builder.Builder();
        builder.runBuild(args);
    }

    /**
     * Add all the java files fro, a given directory.
     *
     * @param files
     * 		the list to which the files are to be added.
     * @param dir
     * 		the directory from which the Java files are added.
     */
    private void addJavaFiles(java.util.List files, java.io.File dir) {
        java.io.File[] javaFiles = dir.listFiles(new java.io.FilenameFilter() {
            public boolean accept(java.io.File dir, java.lang.String name) {
                return name.endsWith(".java");
            }
        });
        if (javaFiles != null) {
            for (int i = 0; i < javaFiles.length; ++i) {
                files.add(javaFiles[i]);
            }
        }
    }

    /**
     * Get the Ant1 files currently required to build a bootstrap build.
     *
     * @return an array of files which need to be copied into the bootstrap
    build.
     */
    private java.io.File[] getAnt1Files() {
        java.util.List files = new java.util.ArrayList();
        addJavaFiles(files, org.apache.ant.builder.Builder.TASKDEFS_ROOT);
        addJavaFiles(files, new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "compilers"));
        addJavaFiles(files, new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "condition"));
        addJavaFiles(files, org.apache.ant.builder.Builder.DEPEND_ROOT);
        addJavaFiles(files, new java.io.File(org.apache.ant.builder.Builder.DEPEND_ROOT, "constantpool"));
        addJavaFiles(files, org.apache.ant.builder.Builder.TYPES_ROOT);
        addJavaFiles(files, org.apache.ant.builder.Builder.FILTERS_ROOT);
        addJavaFiles(files, org.apache.ant.builder.Builder.UTIL_ROOT);
        addJavaFiles(files, new java.io.File(org.apache.ant.builder.Builder.UTIL_ROOT, "depend"));
        addJavaFiles(files, org.apache.ant.builder.Builder.ZIP_ROOT);
        addJavaFiles(files, new java.io.File(org.apache.ant.builder.Builder.UTIL_ROOT, "facade"));
        addJavaFiles(files, org.apache.ant.builder.Builder.INPUT_ROOT);
        files.add(new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "BuildException.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "Location.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "AntClassLoader.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "BuildListener.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "BuildEvent.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "DirectoryScanner.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "FileScanner.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "PathTokenizer.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "TaskAdapter.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "MatchingTask.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.PACKAGE_ROOT, "defaultManifest.mf"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "defaults.properties"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.TYPES_ROOT, "defaults.properties"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.UTIL_ROOT, "regexp/Regexp.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.UTIL_ROOT, "regexp/RegexpMatcher.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.UTIL_ROOT, "regexp/RegexpFactory.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.UTIL_ROOT, "regexp/RegexpMatcherFactory.java"));
        files.add(new java.io.File(org.apache.ant.builder.Builder.FILTERS_ROOT, "util/ChainReaderHelper.java"));
        // these should not be included
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TYPES_ROOT, "DataType.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "Ant.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "CallTarget.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "AntStructure.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "Recorder.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "RecorderEntry.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "SendEmail.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "Do.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.INPUT_ROOT, "InputRequest.java"));
        // not needed for bootstrap
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "Java.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "Tar.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "Untar.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "BZip2.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "BUnzip2.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "Rmic.java"));
        files.remove(new java.io.File(org.apache.ant.builder.Builder.TASKDEFS_ROOT, "SendEmail.java"));
        return ((java.io.File[]) (files.toArray(new java.io.File[0])));
    }

    /**
     * Run the build
     *
     * @param args
     * 		the command line arguments for the build - currently not
     * 		used.
     */
    private void runBuild(java.lang.String[] args) {
        org.apache.ant.builder.BuildHelper mainBuild = new org.apache.ant.builder.BuildHelper();
        mainBuild.setProperty("dist.dir", "bootstrap");
        org.apache.ant.builder.MutantBuilder mutantBuilder = new org.apache.ant.builder.MutantBuilder();
        mutantBuilder._init(mainBuild);
        mutantBuilder.buildsetup(mainBuild);
        mutantBuilder.init(mainBuild);
        mutantBuilder.common(mainBuild);
        mutantBuilder.antcore(mainBuild);
        mutantBuilder.start(mainBuild);
        mutantBuilder.frontend(mainBuild);
        mutantBuilder.systemlib(mainBuild);
        org.apache.ant.builder.Ant1CompatBuilder ant1Builder = new org.apache.ant.builder.Ant1CompatBuilder();
        org.apache.ant.builder.BuildHelper ant1Build = new org.apache.ant.builder.BuildHelper();
        ant1Build.setProperty("dist.dir", "bootstrap");
        ant1Build.addFileSet("ant1src_tocopy", org.apache.ant.builder.Builder.ANT1_SRC_ROOT, getAnt1Files());
        ant1Builder._init(ant1Build);
        ant1Builder.ant1compat(ant1Build);
    }
}