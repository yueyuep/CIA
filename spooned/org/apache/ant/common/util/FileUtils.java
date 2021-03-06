/* The Apache Software License, Version 1.1

Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
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
package org.apache.ant.common.util;
/**
 * This class encapsulates methods which allow Files to be refered to using
 * abstract path names which are translated to native system file paths at
 * runtime as well as copying files or setting there last modification time.
 *
 * @author duncan@x180.com
 * @author Conor MacNeill
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 * @created 21 January 2002
 */
public class FileUtils {
    /**
     * Empty constructor.
     */
    protected FileUtils() {
    }

    /**
     * Factory method.
     *
     * @return The FileUtils instance to actually use. May be a subclass of
    this class.
     */
    public static org.apache.ant.common.util.FileUtils newFileUtils() {
        return new org.apache.ant.common.util.FileUtils();
    }

    /**
     * Interpret the filename as a file relative to the given file - unless
     * the filename already represents an absolute filename.
     *
     * @param file
     * 		the "reference" file for relative paths. This instance
     * 		must be an absolute file and must not contain &quot;./&quot; or
     * 		&quot;../&quot; sequences (same for \ instead of /). If it is
     * 		null, this call is equivalent to
     * 		<code>new java.io.File(filename)</code>.
     * @param filename
     * 		the filename to be resolved
     * @return an absolute file that doesn't contain &quot;./&quot; or
    &quot;../&quot; sequences and uses the correct separator for the
    current platform.
     * @exception ExecutionException
     * 		if the file cannot be resolved
     */
    public java.io.File resolveFile(java.io.File file, java.lang.String filename) throws org.apache.ant.common.util.ExecutionException {
        java.lang.String platformFilename = filename.replace('/', java.io.File.separatorChar).replace('\\', java.io.File.separatorChar);
        // deal with absolute files
        if (platformFilename.startsWith(java.io.File.separator) || (((platformFilename.length() >= 2) && java.lang.Character.isLetter(platformFilename.charAt(0))) && (platformFilename.charAt(1) == ':'))) {
            return normalize(platformFilename);
        }
        if (file == null) {
            return new java.io.File(platformFilename);
        }
        java.io.File helpFile = new java.io.File(file.getAbsolutePath());
        java.util.StringTokenizer tok = new java.util.StringTokenizer(platformFilename, java.io.File.separator);
        while (tok.hasMoreTokens()) {
            java.lang.String part = tok.nextToken();
            if (part.equals("..")) {
                helpFile = helpFile.getParentFile();
                if (helpFile == null) {
                    java.lang.String msg = (("The file or path you specified (" + filename) + ") is invalid relative to ") + file.getPath();
                    throw new org.apache.ant.common.util.ExecutionException(msg);
                }
            } else if (part.equals(".")) {
                // Do nothing here
            } else {
                helpFile = new java.io.File(helpFile, part);
            }
        } 
        return new java.io.File(helpFile.getAbsolutePath());
    }

    /**
     * &quot;normalize&quot; the given absolute path. <p>
     *
     * This includes:
     * <ul>
     *   <li> Uppercase the drive letter if there is one.</li>
     *   <li> Remove redundant slashes after the drive spec.</li>
     *   <li> resolve all ./, .\, ../ and ..\ sequences.</li>
     *   <li> DOS style paths that start with a drive letter will have \ as
     *   the separator.</li>
     * </ul>
     *
     * @param path
     * 		the path to be normalized
     * @return the normalized path
     * @exception ExecutionException
     * 		if there is a problem with the path
     * @throws NullPointerException
     * 		if the file path is equal to null.
     */
    public java.io.File normalize(java.lang.String path) throws java.lang.NullPointerException, org.apache.ant.common.util.ExecutionException {
        java.lang.String platformPath = path.replace('/', java.io.File.separatorChar).replace('\\', java.io.File.separatorChar);
        // make sure we are dealing with an absolute path
        if ((!platformPath.startsWith(java.io.File.separator)) && (!(((platformPath.length() >= 2) && java.lang.Character.isLetter(platformPath.charAt(0))) && (platformPath.charAt(1) == ':')))) {
            java.lang.String msg = path + " is not an absolute path";
            throw new org.apache.ant.common.util.ExecutionException(msg);
        }
        boolean dosWithDrive = false;
        java.lang.String root = null;
        // Eliminate consecutive slashes after the drive spec
        if (((platformPath.length() >= 2) && java.lang.Character.isLetter(platformPath.charAt(0))) && (platformPath.charAt(1) == ':')) {
            dosWithDrive = true;
            char[] ca = platformPath.replace('/', '\\').toCharArray();
            java.lang.StringBuffer sb = new java.lang.StringBuffer();
            sb.append(java.lang.Character.toUpperCase(ca[0])).append(':');
            for (int i = 2; i < ca.length; i++) {
                if ((ca[i] != '\\') || ((ca[i] == '\\') && (ca[i - 1] != '\\'))) {
                    sb.append(ca[i]);
                }
            }
            platformPath = sb.toString().replace('\\', java.io.File.separatorChar);
            if (platformPath.length() == 2) {
                root = platformPath;
                platformPath = "";
            } else {
                root = platformPath.substring(0, 3);
                platformPath = platformPath.substring(3);
            }
        } else if (platformPath.length() == 1) {
            root = java.io.File.separator;
            platformPath = "";
        } else if (platformPath.charAt(1) == java.io.File.separatorChar) {
            // UNC drive
            root = java.io.File.separator + java.io.File.separator;
            platformPath = platformPath.substring(2);
        } else {
            root = java.io.File.separator;
            platformPath = platformPath.substring(1);
        }
        java.util.Stack s = new java.util.Stack();
        s.push(root);
        java.util.StringTokenizer tok = new java.util.StringTokenizer(platformPath, java.io.File.separator);
        while (tok.hasMoreTokens()) {
            java.lang.String thisToken = tok.nextToken();
            if (".".equals(thisToken)) {
                continue;
            } else if ("..".equals(thisToken)) {
                if (s.size() < 2) {
                    throw new org.apache.ant.common.util.ExecutionException("Cannot resolve path " + path);
                } else {
                    s.pop();
                }
            } else {
                // plain component
                s.push(thisToken);
            }
        } 
        java.lang.StringBuffer sb = new java.lang.StringBuffer();
        int size = s.size();
        for (int i = 0; i < size; i++) {
            if (i > 1) {
                // not before the filesystem root and not after it, since root
                // already contains one
                sb.append(java.io.File.separatorChar);
            }
            sb.append(s.elementAt(i));
        }
        platformPath = sb.toString();
        if (dosWithDrive) {
            platformPath = platformPath.replace('/', '\\');
        }
        return new java.io.File(platformPath);
    }
}