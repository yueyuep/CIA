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
package org.apache.tools.ant.taskdefs.optional.clearcase;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.FileSet;
/**
 * Creates a file or directory element.
 *
 * @see http://clearcase.rational.com/doc/latest/ccase_ux/ccref/mkelem.html
 * @author <a href="mailto:sbailliez@apache.org">Stephane Bailliez</a>
 */
public class CCMkelem extends org.apache.tools.ant.taskdefs.optional.clearcase.CCMatchingTask {
    private java.lang.String type;

    private boolean nocheckout;

    private boolean checkin;

    private boolean preserveTime;

    private java.util.Hashtable codirs = new java.util.Hashtable();

    public void execute(java.lang.String[] args, org.apache.tools.ant.taskdefs.optional.clearcase.CCFile file) throws org.apache.tools.ant.BuildException {
        org.apache.tools.ant.taskdefs.optional.clearcase.CCFile parent = ((org.apache.tools.ant.taskdefs.optional.clearcase.CCFile) (codirs.get(file.getParent())));
        if (parent == null) {
            parent = new org.apache.tools.ant.taskdefs.optional.clearcase.CCFile(file.getParent());
            if (!parent.isVersioned()) {
                // ensure versioned dir
            } else if (parent.isCheckedIn()) {
                utils.checkout(parent);
            }
            codirs.put(parent.getPath(), parent);
        }
        args[args.length - 1] = file.getAbsolutePath();
        org.apache.tools.ant.taskdefs.optional.clearcase.CmdResult res = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.cleartool(args);
        if (res.getStatus() != 0) {
            throw new org.apache.tools.ant.BuildException(res.getStdErr());
        }
    }

    protected void postExecute() {
        // checkin back all co directories
        java.util.Enumeration dirs = codirs.elements();
        while (dirs.hasMoreElements()) {
            java.io.File dir = ((java.io.File) (dirs.nextElement()));
            utils.checkin(dir);
        } 
        super.postExecute();
    }

    /**
     * create the command line options based on user input
     */
    protected java.util.Vector getOptions() {
        java.util.Vector v = new java.util.Vector();
        v.addElement("mkelem");
        if (type != null) {
            v.addElement("-eltype");
            v.addElement(type);
        }
        if (comment == null) {
            v.addElement("-nc");
        } else {
            commentFile = org.apache.tools.ant.taskdefs.optional.clearcase.CCUtils.createCommentFile(comment);
            v.addElement("-cfi");
            v.addElement(commentFile.getAbsolutePath());
        }
        if (nocheckout) {
            v.addElement("-nco");
        } else if (checkin) {
            v.addElement("-ci");
            if (preserveTime) {
                v.addElement("-ptime");
            }
        }
        v.addElement("<pname>");// dummy arg for file

        return v;
    }

    // bean setters
    public void setType(java.lang.String value) {
        type = value;
    }

    public void setNoCheckout(boolean value) {
        nocheckout = value;
    }

    public void setCheckin(boolean value) {
        checkin = value;
    }

    public void setPreserveTime(boolean value) {
        preserveTime = value;
    }
}