/* The Apache Software License, Version 1.1

Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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
package org.apache.tools.ant.taskdefs.optional.http;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 * this class does post of form content or raw files. you can have one
 * or the other -as soon as a file is specified all the other properties
 * are dropped on the floor.
 * a file post will have content type determined from the extension, you can
 * override it
 *
 * @since ant1.5
 * @author matth@pobox.com Matt Humphrey
 * @author steve_l@iseran.com Steve Loughran
 * @created March 17, 2001
 */
public class HttpPost extends HttpTask {
    /**
     * file to upload. Null is ok
     */
    protected java.io.File postFile = null;

    /**
     * set the file to post
     */
    public void setUploadFile(java.io.File postFile) {
        this.postFile = postFile;
    }

    /**
     * query the post file
     *
     * @return the file or null for 'not defined'
     */
    public java.io.File getUploadFile() {
        return postFile;
    }

    /**
     * content type. ignored when the file is null,
     * and even then we guess it if aint specified
     */
    private java.lang.String contentType;

    /**
     * set the content type. Recommended if a file is being uploaded
     */
    public void setContentType(java.lang.String contentType) {
        this.contentType = contentType;
    }

    /**
     * query the content type
     *
     * @return the content type or null for 'not defined'
     */
    public java.lang.String getContentType() {
        return contentType;
    }

    /**
     * override of test
     *
     * @return false always
     */
    protected boolean areParamsAddedToUrl() {
        return false;
    }

    /**
     * this override of the base connect pumps
     * up the parameter vector as form data
     *
     * @param connection
     * 		where to connect to
     * @exception BuildException
     * 		build trouble
     * @exception IOException
     * 		IO trouble
     */
    protected java.net.URLConnection doConnect(java.net.URLConnection connection) throws org.apache.tools.ant.taskdefs.optional.http.BuildException, java.io.IOException {
        if (postFile == null) {
            return doConnectFormPost(connection);
        } else {
            return doConnectFilePost(connection);
        }
    }

    /**
     * feed up the parameter vector as form data
     *
     * @param connection
     * 		where to connect to
     * @exception BuildException
     * 		build trouble
     * @exception IOException
     * 		IO trouble
     */
    protected java.net.URLConnection doConnectFormPost(java.net.URLConnection connection) throws org.apache.tools.ant.taskdefs.optional.http.BuildException, java.io.IOException {
        log("Posting data as a form", Project.MSG_VERBOSE);
        // Create the output payload
        java.io.ByteArrayOutputStream byteStream = new java.io.ByteArrayOutputStream(256);
        java.io.PrintWriter out = new java.io.PrintWriter(byteStream);
        writePostData(out);
        out.flush();
        out.close();
        byte[] data = byteStream.toByteArray();
        // send it
        return doConnectWithUpload(connection, "application/x-www-form-urlencoded", byteStream.size(), new java.io.ByteArrayInputStream(data));
    }

    /**
     * feed up the data file
     *
     * @param connection
     * 		where to connect to
     * @exception BuildException
     * 		build trouble
     * @exception IOException
     * 		IO trouble
     */
    protected java.net.URLConnection doConnectFilePost(java.net.URLConnection connection) throws org.apache.tools.ant.taskdefs.optional.http.BuildException, java.io.IOException {
        int size = ((int) (postFile.length()));
        log("Posting file " + postFile, Project.MSG_VERBOSE);
        java.io.InputStream instream = new java.io.FileInputStream(postFile);
        java.lang.String type = contentType;
        if (type == null) {
            type = ContentGuesser.guessContentType(postFile.getName());
        }
        return doConnectWithUpload(connection, type, size, instream);
    }

    /**
     * write out post data in form mode
     *
     * @param out
     * 		Description of Parameter
     */
    protected void writePostData(java.io.PrintWriter out) {
        HttpRequestParameter param;
        java.util.Vector params = getRequestParameters();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                out.print('&');
            }
            param = ((HttpRequestParameter) (params.get(i)));
            out.print(param.toString());
            log("parameter : " + param.toString(), Project.MSG_DEBUG);
        }
    }

    /**
     * this must be overridden by implementations
     * to set the request method to GET, POST, whatever
     *
     * @return the method string
     */
    public java.lang.String getRequestMethod() {
        return "POST";
    }
}