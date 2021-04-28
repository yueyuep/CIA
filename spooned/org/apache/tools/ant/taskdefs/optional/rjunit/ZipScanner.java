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
package org.apache.tools.ant.taskdefs.optional.rjunit;
import org.apache.tools.ant.DirectoryScanner;
/**
 * Provide a way to scan entries in a zip file. Note that it extends
 * DirectoryScanner to make use of protected methods but implementation
 * may not be valid for some methods.
 * <p>
 * the setBaseDir() must be called to set the reference to the archive
 * file (.jar or .zip).
 * </p>
 *
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public class ZipScanner extends org.apache.tools.ant.DirectoryScanner {
    public ZipScanner() {
    }

    public void setExcludes(java.lang.String[] excludes) {
        super.setExcludes(excludes);
        normalize(this.excludes);
    }

    public void setIncludes(java.lang.String[] includes) {
        super.setIncludes(includes);
        normalize(this.includes);
    }

    /**
     * normalize a set of paths so that it uses / otherwise matching will
     * fail beautifully since archives use / to denote a path.
     */
    protected void normalize(java.lang.String[] files) {
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                files[i] = files[i].replace('\\', '/');
            }
        }
    }

    /**
     * Scans the archive for files that match at least one include
     * pattern, and don't match any exclude patterns.
     *
     * @exception IllegalStateException
     * 		when the zip file was set incorrecly
     */
    public void scan() {
        if (basedir == null) {
            throw new java.lang.IllegalStateException("No zipfile set");
        }
        if (!basedir.exists()) {
            throw new java.lang.IllegalStateException(("zipfile " + basedir) + " does not exist");
        }
        if (basedir.isDirectory()) {
            throw new java.lang.IllegalStateException(("zipfile " + basedir) + " is not a file");
        }
        if (includes == null) {
            // No includes supplied, so set it to 'matches all'
            includes = new java.lang.String[1];
            includes[0] = "**";
        }
        if (excludes == null) {
            excludes = new java.lang.String[0];
        }
        filesIncluded = new java.util.Vector();
        filesNotIncluded = new java.util.Vector();
        filesExcluded = new java.util.Vector();
        dirsIncluded = new java.util.Vector();
        dirsNotIncluded = new java.util.Vector();
        dirsExcluded = new java.util.Vector();
        if (isIncluded("")) {
            if (!isExcluded("")) {
                dirsIncluded.addElement("");
            } else {
                dirsExcluded.addElement("");
            }
        } else {
            dirsNotIncluded.addElement("");
        }
        scandir(basedir, "", true);
    }

    protected void scandir(java.io.File file, java.lang.String vpath, boolean fast) {
        java.util.zip.ZipFile zip = null;
        try {
            zip = new java.util.zip.ZipFile(file);
        } catch (java.io.IOException e) {
            throw new java.lang.IllegalStateException(e.getMessage());
        }
        java.util.Enumeration entries = zip.entries();
        while (entries.hasMoreElements()) {
            java.util.zip.ZipEntry entry = ((java.util.zip.ZipEntry) (entries.nextElement()));
            java.lang.String name = entry.getName();
            // @fixme do we need to strip out entries that starts
            // with . or ./ ?
            if (entry.isDirectory()) {
                if (isIncluded(name)) {
                    if (!isExcluded(name)) {
                        dirsIncluded.addElement(name);
                    } else {
                        everythingIncluded = false;
                        dirsExcluded.addElement(name);
                    }
                } else {
                    everythingIncluded = false;
                    dirsNotIncluded.addElement(name);
                }
            } else if (isIncluded(name)) {
                if (!isExcluded(name)) {
                    filesIncluded.addElement(name);
                } else {
                    everythingIncluded = false;
                    filesExcluded.addElement(name);
                }
            } else {
                everythingIncluded = false;
                filesNotIncluded.addElement(name);
            }
        } 
    }
}