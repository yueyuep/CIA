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
 * this class implements basic auth, the one that shouldn't be used
 * except over an encrypted link or trusted network.
 *
 * @author matth@pobox.com Matt Humphrey
 * @author steve_l@iseran.com Steve Loughran
 * @created 20 March 2001
 */
public class HttpDigestAuth implements HttpAuthenticationStrategy {
    /**
     * Sets the AuthenticationHeader attribute of the HttpAuthStrategy
     * object
     *
     * @param requestConnection
     * 		The current request
     * @param responseConnection
     * 		any previous request, which can contain a
     * 		challenge for the next round. Will often be null
     * @param user
     * 		the current user name
     * @param password
     * 		the current password
     */
    public void setAuthenticationHeader(java.net.URLConnection requestConnection, java.net.URLConnection responseConnection, java.lang.String username, java.lang.String password) throws org.apache.tools.ant.taskdefs.optional.http.BuildException {
        if (username != null) {
            password = (username == null) ? "" : password;
            java.lang.String encodeStr = (username + ":") + password;
            Base64Encode encoder = new Base64Encode();
            char[] encodedPass = encoder.encodeBase64(encodeStr.getBytes());
            java.lang.String authStr = "BASIC " + new java.lang.String(encodedPass);
            requestConnection.setRequestProperty("Authorization", authStr);
        }
    }
}