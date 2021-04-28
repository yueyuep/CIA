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
import org.apache.tools.ant.types.EnumeratedAttribute;
/**
 * This class is a foundational class for all the tasks which implement
 * http methods. To implement a subclass you *must* provide
 * an implementation of getRequestMethod(). Consider also
 * stating the parameter policy (areParamsAddedToUrl()) and
 * then, if needed, overriding doConnect, and the onConnected(),
 * OnDownloadFinished() methods.
 *
 * @since ant1.5
 * @author costin@dnt.ro
 * @author matth@pobox.com Matt Humphrey
 * @author steve_l@iseran.com Steve Loughran
 * @created March 17, 2001
 */
public abstract class HttpTask extends Task {
    /**
     * flag to control action on execution trouble
     */
    protected boolean failOnError = true;

    /**
     * this sets the size of the buffer and the hash for download
     */
    protected int blockSize = 64;

    /**
     * property to set on success
     */
    protected java.lang.String successProperty;

    /**
     * source URL. required
     */
    private java.lang.String source;

    /**
     * destination for download
     */
    private java.io.File dest;

    /**
     * verbose flag gives extra information
     */
    private boolean verbose = false;

    /**
     * timestamp based download flag. off by default
     */
    private boolean useTimestamp = false;

    /**
     * authorization mechanism in use.
     */
    private int authType = org.apache.tools.ant.taskdefs.optional.http.HttpTask.AUTH_NONE;

    /**
     * username for authentication
     */
    private java.lang.String username;

    /**
     * password for authentication
     */
    private java.lang.String password;

    /**
     * parameters to send on a request
     */
    private java.util.Vector params = new java.util.Vector();

    /**
     * headers to send on a request
     */
    private java.util.Vector headers = new java.util.Vector();

    /**
     * cache policy
     */
    private boolean usecaches = false;

    /**
     * the name of a destination property
     */
    private java.lang.String destinationPropname = null;

    /**
     * *
     * a flag to control whether or not response codes
     * are acted on
     */
    private boolean useResponseCode = true;

    /**
     * No authentication specified
     */
    public static final int AUTH_NONE = 0;

    /**
     * basic 'cleartext' authentication
     */
    public static final int AUTH_BASIC = 1;

    /**
     * digest auth. not actually supported but present for completeness
     */
    public static final int AUTH_DIGEST = 2;

    /**
     * turn caching on or off. only relevant for protocols and methods
     * which are cacheable (HEAD, GET) on http
     *
     * @param usecaches
     * 		The new UseCaches value
     */
    public void setUseCaches(boolean usecaches) {
        this.usecaches = usecaches;
    }

    /**
     * turn caching on or off. only relevant for protocols and methods
     * which are cacheable (HEAD, GET) on http
     *
     * @param usecaches
     * 		The new UseCaches value
     */
    public void setUseResponseCode(boolean useResponseCodes) {
        this.useResponseCode = useResponseCode;
    }

    /**
     * Set the URL.
     *
     * @param u
     * 		URL for the file.
     */
    public void setURL(java.lang.String u) {
        this.source = u;
    }

    /**
     * the local destination for any response. this can be null for 'dont
     * download'
     *
     * @param dest
     * 		Path to file.
     */
    public void setDest(java.io.File dest) {
        this.dest = dest;
    }

    /**
     * the local destination for any response. this can be null for 'dont
     * download'
     *
     * @param dest
     * 		Path to file.
     */
    public void setDestinationProperty(java.lang.String name) {
        this.destinationPropname = name;
    }

    /**
     * Be verbose, if set to " <CODE>true</CODE> ".
     *
     * @param verbose
     * 		The new Verbose value
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * set fail on error flag
     *
     * @param b
     * 		The new FailOnError value
     */
    public void setFailOnError(boolean b) {
        failOnError = b;
    }

    /**
     * Use timestamps, if set to " <CODE>true</CODE> ". <p>
     *
     * In this situation, the if-modified-since header is set so that
     * the file is only fetched if it is newer than the local file (or
     * there is no local file) This flag is only valid on HTTP connections,
     * it is ignored in other cases. When the flag is set, the local copy
     * of the downloaded file will also have its timestamp set to the
     * remote file time. <br>
     * Note that remote files of date 1/1/1970 (GMT) are treated as 'no
     * timestamp', and web servers often serve files with a timestamp
     * in the future by replacing their timestamp with that of the current
     * time. Also, inter-computer clock differences can cause no end of
     * grief.
     *
     * @param usetimestamp
     * 		The new UseTimestamp value
     */
    public void setUseTimestamp(boolean usetimestamp) {
        if (project.getJavaVersion() != Project.JAVA_1_1) {
            this.useTimestamp = usetimestamp;
        } else {
            log("usetimestamp is not supported on java 1.1", Project.MSG_WARN);
        }
    }

    /**
     * Sets the Authtype attribute of the HttpTask object REVISIT/REFACTOR
     *
     * @param type
     * 		The new Authtype value
     */
    public void setAuthtype(org.apache.tools.ant.taskdefs.optional.http.HttpTask.AuthMethodType type) {
        this.authType = type.mapValueToNumber();
    }

    /**
     * Sets the Username used for authentication. setting the username
     * implicitly turns authentication on.
     *
     * @param username
     * 		The new Username value
     */
    public void setUsername(java.lang.String username) {
        this.username = username;
        if (authType == org.apache.tools.ant.taskdefs.optional.http.HttpTask.AUTH_NONE) {
            authType = org.apache.tools.ant.taskdefs.optional.http.HttpTask.AUTH_BASIC;
        }
    }

    /**
     * Sets the Password attribute of the HttpTask object
     *
     * @param password
     * 		The new Password value
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }

    /**
     * set a variable to be set in the event of success
     *
     * @param successProperty
     * 		The new SuccessProperty value
     */
    public void setSuccessProperty(java.lang.String successProperty) {
        this.successProperty = successProperty;
    }

    /**
     * get block size (kb)
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * set the new block size for download
     *
     * @param the
     * 		new value (in kilobytes)
     */
    public void setBlockSize(int blocksize) {
        this.blockSize = blockSize;
    }

    /**
     * query cache policy
     *
     * @return The UseCaches value
     */
    public boolean getUseCaches() {
        return usecaches;
    }

    /**
     * query fail on error flag
     *
     * @return The FailFailOnError value
     */
    public boolean getFailOnError() {
        return failOnError;
    }

    /**
     * get the username
     *
     * @return current username or null for 'none'
     */
    public java.lang.String getUsername() {
        return username;
    }

    /**
     * get the password
     *
     * @return current password or null for 'none'
     */
    public java.lang.String getPassword() {
        return password;
    }

    /**
     *
     *
     * @return The RemoteURL value
     */
    public java.lang.String getURL() {
        return source;
    }

    /**
     * access parameters
     *
     * @return The RequestParameters value
     */
    public java.util.Vector getRequestParameters() {
        return params;
    }

    /**
     * accessor of success property name
     *
     * @return The SuccessProperty value
     */
    public java.lang.String getSuccessProperty() {
        return successProperty;
    }

    /**
     * accessor of destination property name
     *
     * @return The destination value
     */
    public java.lang.String getDestinationProperty() {
        return destinationPropname;
    }

    /**
     * accessor of destination
     *
     * @return Thedestination
     */
    public java.io.File getDest() {
        return dest;
    }

    /**
     * if the user wanted a success property, this is it. of course, it
     * is only relevant if failonerror=false
     *
     * @return Description of the Returned Value
     */
    public void noteSuccess() {
        if ((successProperty != null) && (successProperty.length() > 0)) {
            getProject().setProperty(successProperty, "true");
        }
    }

    /**
     * Does the work.
     *
     * @todo extract content length header and use it to verify
    completeness of download
     * @exception BuildException
     * 		Thrown in unrecoverable error.
     */
    public void execute() throws org.apache.tools.ant.taskdefs.optional.http.BuildException {
        // check arguments, will bail out if there
        // was trouble
        verifyArguments();
        // set up the URL connection
        java.net.URL url = buildURL();
        try {
            // now create a connection
            java.net.URLConnection connection = url.openConnection();
            // set caching option to whatever
            connection.setUseCaches(getUseCaches());
            // set the timestamp option if flag is set and
            // the local file actually exists.
            long localTimestamp = getTimestamp();
            if (localTimestamp != 0) {
                if (verbose) {
                    java.util.Date t = new java.util.Date(localTimestamp);
                    log("local file date : " + t.toString());
                }
                connection.setIfModifiedSince(localTimestamp);
            }
            // Set auth header, if specified
            // NB: verifyArguments will already have checked that you can't
            // have a null username with a non-null strategy.
            HttpAuthenticationStrategy authStrategy = getAuthStrategy();
            if (authStrategy != null) {
                authStrategy.setAuthenticationHeader(connection, null, username, password);
            }
            // Set explicitly specified request headers
            HttpRequestParameter header;
            for (int i = 0; i < headers.size(); i++) {
                header = ((HttpRequestParameter) (headers.get(i)));
                connection.setRequestProperty(header.getName(), header.getValue());
            }
            // cast to an http connection if we can,
            // then set the request method pulled from the subclass
            java.lang.String method = getRequestMethod();
            java.net.HttpURLConnection httpConnection = null;
            if (connection instanceof java.net.HttpURLConnection) {
                httpConnection = ((java.net.HttpURLConnection) (connection));
                httpConnection.setRequestMethod(method);
            }
            log((("making " + method) + " to ") + url);
            // call self or subclass for the connect.
            // the connection object may change identity at this point.
            connection = doConnect(connection);
            // then provide a bit of overridable post processing for the fun of it
            if (!onConnected(connection)) {
                return;
            }
            // repeat the cast.
            if (connection instanceof java.net.HttpURLConnection) {
                httpConnection = ((java.net.HttpURLConnection) (connection));
            }
            if (httpConnection != null) {
                // check for a 304 result (HTTP only) when we set the timestamp
                // earlier on (A fractional performance tweak)
                if (localTimestamp != 0) {
                    if (getResponseCode(httpConnection) == java.net.HttpURLConnection.HTTP_NOT_MODIFIED) {
                        // not modified so no file download. just return instead
                        // and trace out something so the user doesn't think that the
                        // download happened when it didn't
                        log("Local file is up to date - so nothing was downloaded");
                        noteSuccess();
                        return;
                    }
                }
            }
            // get the input stream
            java.io.InputStream is = getInputStream(connection);
            // bail out if the input stream isn't valid at this point
            // again, though we should have got to this point earlier.
            if (is == null) {
                log("Can't get " + url, Project.MSG_ERR);
                if (getFailOnError()) {
                    return;
                }
                throw new BuildException("Can't reach URL");
            }
            // pick a file or null stream for saving content
            java.io.OutputStream out = null;
            if (dest != null) {
                log("Saving output to " + dest, Project.MSG_DEBUG);
                out = new java.io.FileOutputStream(dest);
            } else if (destinationPropname != null) {
                // save contents to a property
                log("Saving output to property " + destinationPropname, Project.MSG_DEBUG);
                out = new java.io.ByteArrayOutputStream(blockSize * 1024);
            } else {
                // discard everything
                out = new NullOutputStream();
            }
            // get content length
            // do it this way instead of calling getContentLength() because
            // that way is sporadically unreliable (length is downgraded to
            // size of small packets)
            int contentLength = connection.getHeaderFieldInt("Content-Length", -1);
            int bytesRead = 0;
            // now start download.
            byte[] buffer = new byte[blockSize * 1024];
            int length;
            while (((length = is.read(buffer)) >= 0) && ((contentLength == (-1)) || (bytesRead < contentLength))) {
                bytesRead += length;
                out.write(buffer, 0, length);
                if (verbose) {
                    showProgressChar('.');
                }
            } 
            // finished successfully - clean up.
            if (verbose) {
                showProgressChar('\n');
            }
            // if it we were saving to a byte array, then
            // set the destination property with its contents
            if (out instanceof java.io.ByteArrayOutputStream) {
                getProject().setProperty(destinationPropname, out.toString());
            }
            // everything is downloaded; close files
            out.flush();
            out.close();
            is.close();
            is = null;
            out = null;
            // another overridable notification method
            if (!onDownloadFinished(connection)) {
                return;
            }
            // REFACTOR: move this down to HttpHead? What if a post wants
            // to set a date?
            // if (and only if) the use file time option is set, then the
            // saved file now has its timestamp set to that of the downloaded file
            if (useTimestamp) {
                long remoteTimestamp = connection.getLastModified();
                if (verbose) {
                    java.util.Date t = new java.util.Date(remoteTimestamp);
                    log(("last modified = " + t.toString()) + (remoteTimestamp == 0 ? " - using current time instead" : ""));
                }
                if (remoteTimestamp != 0) {
                    touchFile(dest, remoteTimestamp);
                }
            }
            java.lang.String failureString = null;
            if ((contentLength > (-1)) && (bytesRead != contentLength)) {
                failureString = ((("Incomplete download -Expected " + contentLength) + "received ") + bytesRead) + " bytes";
            } else // finally clean anything up.
            // http requests have their response code checked, and only
            // those in the success range are deemed successful.
            if ((httpConnection != null) && useResponseCode) {
                int statusCode = httpConnection.getResponseCode();
                if ((statusCode < 200) || (statusCode > 299)) {
                    failureString = ("Server error code " + statusCode) + " received";
                }
            }
            // check for an error message
            if (failureString == null) {
                noteSuccess();
            } else if (failOnError)
                throw new BuildException(failureString);
            else
                log(failureString, Project.MSG_ERR);

        } catch (java.io.IOException ioe) {
            log((((("Error performing " + getRequestMethod()) + " on ") + url) + " : ") + ioe.toString(), Project.MSG_ERR);
            if (failOnError) {
                throw new BuildException(ioe);
            }
        }
    }

    /**
     * show a progress character
     *
     * @todo this doesn't work in shell wrappers
     */
    protected void showProgressChar(char c) {
        java.lang.System.out.write(c);
    }

    /**
     * Adds a form / request parameter.
     *
     * @param param
     * 		The feature to be added to the HttpRequestParameter
     * 		attribute
     */
    public void addParam(HttpRequestParameter param) {
        params.add(param);
    }

    /**
     * Adds an HTTP request header.
     *
     * @param header
     * 		The feature to be added to the Header attribute
     */
    public void addHeader(HttpRequestParameter header) {
        headers.add(header);
    }

    /**
     * this must be overridden by implementations to set the request method
     * to GET, POST, whatever NB: this method only gets called for an
     * http request
     *
     * @return the method string
     */
    protected abstract java.lang.String getRequestMethod();

    /**
     * determine the timestamp to use if the flag is set and the local
     * file actually exists.
     *
     * @return 0 for 'no timestamp', a number otherwhise
     */
    protected long getTimestamp() {
        long timestamp = 0;
        if ((useTimestamp && (dest != null)) && dest.exists()) {
            timestamp = dest.lastModified();
        } else {
            timestamp = 0;
        }
        return timestamp;
    }

    /**
     * ask for authentication details. An empty string means 'no auth'
     *
     * @return an RFC2617 auth string
     */
    protected java.lang.String getAuthenticationString() {
        // Set authorization eader, if specified
        if ((authType == org.apache.tools.ant.taskdefs.optional.http.HttpTask.AUTH_BASIC) && (username != null)) {
            password = (password == null) ? "" : password;
            java.lang.String encodeStr = (username + ":") + password;
            Base64Encode encoder = new Base64Encode();
            char[] encodedPass = encoder.encodeBase64(encodeStr.getBytes());
            java.lang.String authStr = "BASIC " + new java.lang.String(encodedPass);
            return authStr;
        } else {
            return null;
        }
    }

    /**
     * this overridable method verifies that all the params are valid
     * the base implementation checks for remote url validity and if the
     * destination is not null, write access to what mustnt be a directory.
     * sublcasses can call the base class as well as check their own data
     *
     * @return true if everything is fine. false if we have encountered
    problems but arent allowed to fail on an error,
     * @exception BuildException
     * 		only throw this when the failonerror
     * 		flag is true
     */
    protected void verifyArguments() throws org.apache.tools.ant.taskdefs.optional.http.BuildException {
        BuildException trouble = null;
        // check remote params -but only create an exception, not throw it
        if (getURL() == null) {
            throw new BuildException("target URL missing");
        }
        // check destination parameters  -but only create an exception, not throw it
        if ((dest != null) && dest.exists()) {
            if (dest.isDirectory()) {
                throw new BuildException("The specified destination is a directory");
            } else if (!dest.canWrite()) {
                throw new BuildException("Can't write to " + dest.getAbsolutePath());
            }
        }
        // check auth policy
        if ((authType != org.apache.tools.ant.taskdefs.optional.http.HttpTask.AUTH_NONE) && (username == null)) {
            throw new BuildException("no username defined to use with authorisation");
        }
    }

    /**
     * set the timestamp of a named file to a specified time. prints a
     * warning on java1.1
     *
     * @param file
     * 		Description of Parameter
     * @param timemillis
     * 		Description of Parameter
     * @exception BuildException
     * 		Thrown in unrecoverable error. Likely
     * 		this comes from file access failures.
     */
    protected void touchFile(java.io.File file, long timemillis) throws org.apache.tools.ant.taskdefs.optional.http.BuildException {
        getProject().setFileLastModified(file, timemillis);
    }

    /**
     * build a URL from the source url, maybe with parameters attached
     *
     * @return Description of the Returned Value
     * @exception BuildException
     * 		Description of Exception
     */
    protected java.net.URL buildURL() throws org.apache.tools.ant.taskdefs.optional.http.BuildException {
        java.lang.String urlbase = getURL();
        try {
            if (areParamsAddedToUrl()) {
                urlbase = parameterizeURL();
            }
            return new java.net.URL(urlbase);
        } catch (java.net.MalformedURLException e) {
            throw new BuildException("Invalid URL");
        }
    }

    /**
     * take a url and add parameters to it. if there are no parameters
     * the base url string is returned
     *
     * @return a string to be used for URL creation
     * @exception BuildException
     * 		Description of Exception
     */
    protected java.lang.String parameterizeURL() throws org.apache.tools.ant.taskdefs.optional.http.BuildException {
        // return immediately if there are no parameters
        if (params.size() == 0) {
            return getURL();
        }
        java.lang.StringBuffer buf = new java.lang.StringBuffer(getURL());
        // this devious little line code recognises a parameter string already
        // in the source url, and if so doesnt add a new one
        buf.append(source.indexOf('?') == (-1) ? '?' : '&');
        HttpRequestParameter param;
        // run through the parameter list, encode the name/value pairs and
        // append them to the list
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                buf.append('&');
            }
            param = ((HttpRequestParameter) (params.get(i)));
            buf.append(param.toString());
        }
        return buf.toString();
    }

    /**
     * query for the request wanting parameters on the url default is
     * true, subclasses may want to change
     *
     * @return true if a url should have params attached.
     */
    protected boolean areParamsAddedToUrl() {
        return true;
    }

    /**
     * get the auth policy
     * a null return value means 'no policy chosen'
     *
     * @return current authorisation strategy or null
     */
    protected org.apache.tools.ant.taskdefs.optional.http.HttpAuthenticationStrategy getAuthStrategy() {
        HttpAuthenticationStrategy strategy = null;
        switch (authType) {
            case org.apache.tools.ant.taskdefs.optional.http.HttpTask.AUTH_BASIC :
                strategy = new HttpBasicAuth();
                break;
            case org.apache.tools.ant.taskdefs.optional.http.HttpTask.AUTH_DIGEST :
                // TODO
                break;
            case org.apache.tools.ant.taskdefs.optional.http.HttpTask.AUTH_NONE :
            default :
                break;
        }
        return strategy;
    }

    /**
     * this method opens the connection. It can recognise a 401 error code
     * and in digest auth will then open a new connection with the
     * supplied nonce encoded. That is why it can return a new
     * connection object.
     *
     * @todo handle digest auth
     * @param connection
     * 		where to connect to
     * @exception BuildException
     * 		build trouble
     * @exception IOException
     * 		IO trouble
     * @return a new connection. This may be different than the old one
     */
    protected java.net.URLConnection makeConnectionWithAuthHandling(java.net.URLConnection connection) throws org.apache.tools.ant.taskdefs.optional.http.BuildException, java.io.IOException {
        log("Connecting to " + connection.toString(), Project.MSG_DEBUG);
        connection.connect();
        java.net.URLConnection returnConnection = connection;
        log("connected", Project.MSG_DEBUG);
        if (connection instanceof java.net.HttpURLConnection) {
            java.net.HttpURLConnection httpConnection = ((java.net.HttpURLConnection) (connection));
            if ((getResponseCode(httpConnection) == java.net.HttpURLConnection.HTTP_UNAUTHORIZED) && (authType == org.apache.tools.ant.taskdefs.optional.http.HttpTask.AUTH_DIGEST)) {
                // TODO auth failure. in digest mode we can make a new auth
                // duplicating all the settings then reconnect
                // and return it
                log("Digest authentication needed but not yet supported", Project.MSG_DEBUG);
            }
        }
        return returnConnection;
    }

    /**
     * by making a query for a value from the connection, we force the
     * client code to actually do the http request and go into input mode.
     * so next we can check for trouble.
     */
    void probeConnection(java.net.HttpURLConnection connection) {
        java.lang.String probe = connection.getHeaderFieldKey(0);
    }

    /**
     * get a response from a connection request.
     * This code fixes a problem found in HttpURLConnection, that
     * any attempt to get the response code would trigger a FileNotFound
     *
     * @see <a href="http://developer.java.sun.com/developer/bugParade/bugs/4160499.html">
    BugParade details </a>
    "If the requested file does not exist, and ends in .html, .htm, .txt or /, you
    will get the error stream with no exception thrown. If the file does not end
    like any of these you can catch the exception and immediately request it again
    to get the error stream. The response code can be obtained with
    getResponseCode()."
    which means, to really get the response code you need to ask twice.
     * @param connection
     * 		the current http link
     * @return whatever we get back
     * @throws IOException
     * 		if anything other than file not found gets thrown,
     * 		and even a FileNotFound exception if that gets thrown too many times.
     */
    protected int getResponseCode(java.net.HttpURLConnection connection) throws java.io.IOException {
        // force the creation of the input stream
        // (which is what HttpURLConnection.getResponseCode() does internally
        // that way the bug handler code is only needed once.
        // probeConnection(connection);
        java.io.IOException swallowed = null;
        boolean caught = false;
        int response = 0;
        for (int attempts = 0; attempts < 5; attempts++) {
            try {
                response = connection.getResponseCode();
                caught = true;
                break;
            } catch (java.io.FileNotFoundException ex) {
                log("Swallowed FileNotFoundException in getResponseCode", Project.MSG_VERBOSE);
                log(ex.toString(), Project.MSG_DEBUG);
                swallowed = ex;
            }
        }
        if ((!caught) && (swallowed != null)) {
            throw swallowed;
        }
        return response;
    }

    /**
     * get an input stream from a connection
     * This code tries to fix a problem found in HttpURLConnection, that
     * any attempt to get the response code would trigger a FileNotFound
     * BugParade ID 4160499 :
     * <blockquote>
     * "If the requested file does not exist, and ends in .html, .htm, .txt or /, you
     *  will get the error stream with no exception thrown. If the file does not end
     *  like any of these you can catch the exception and immediately request it again
     *  to get the error stream. The response code can be obtained with
     *  getResponseCode()."
     * <blockquote>
     * which means, to really get the response code you need to ask twice. More to the point
     * this handling is not consistent across JVMs: on java 1.3 you can ask as often as you like
     * but you are not going to get the input stream on a JSP page when it has some 500 class error.
     *
     * @param connection
     * 		the current link
     * @return the input stream.
     * @throws IOException
     * 		if anything other than file not found gets thrown,
     * 		and even a FileNotFound exception if that gets thrown too many times.
     */
    protected java.io.InputStream getInputStream(java.net.URLConnection connection) throws java.io.IOException {
        java.io.IOException swallowed = null;
        java.io.InputStream instream = null;
        for (int attempts = 0; attempts < 5; attempts++) {
            try {
                instream = connection.getInputStream();
                break;
            } catch (java.io.FileNotFoundException ex) {
                log("Swallowed IO exception in getInputStream", Project.MSG_VERBOSE);
                log(ex.toString(), Project.MSG_DEBUG);
                swallowed = ex;
            }
        }
        if ((instream == null) && (swallowed != null)) {
            throw swallowed;
        }
        return instream;
    }

    /**
     * this method is inteded for overriding. it is called when connecting
     * to a URL, and the base implementation just calls connect() on the
     * parameter. any subclass that wants to pump its own datastream up
     * (like post) must override this
     *
     * @param connection
     * 		where to connect to
     * @exception BuildException
     * 		build trouble
     * @exception IOException
     * 		IO trouble
     */
    protected java.net.URLConnection doConnect(java.net.URLConnection connection) throws org.apache.tools.ant.taskdefs.optional.http.BuildException, java.io.IOException {
        return makeConnectionWithAuthHandling(connection);
    }

    /**
     * this is a method for upload centric post-like requests
     *
     * @param connection
     * 		who we talk to
     * @param contentType
     * 		Description of Parameter
     * @param contentLength
     * 		Description of Parameter
     * @param content
     * 		Description of Parameter
     * @exception IOException
     * 		something went wrong with the IO
     */
    protected java.net.URLConnection doConnectWithUpload(java.net.URLConnection connection, java.lang.String contentType, int contentLength, java.io.InputStream content) throws java.io.IOException {
        log((("uploading " + contentLength) + " bytes of type ") + contentType, Project.MSG_VERBOSE);
        // tell the connection we are in output mode
        connection.setDoOutput(true);
        // Set content length and type headers
        connection.setRequestProperty("Content-Length", java.lang.String.valueOf(contentLength));
        connection.setRequestProperty("Content-Type", contentType);
        // todo: add auth handling
        // connection=makeConnectionWithAuthHandling(connection);
        connection.connect();
        java.io.OutputStream toServer = connection.getOutputStream();
        // create a buffer which is the smaller of
        // the content length and the block size (in KB)
        int buffersize = blockSize * 1024;
        if (contentLength < buffersize)
            buffersize = contentLength;

        byte[] buffer = new byte[buffersize];
        int remaining = contentLength;
        while (remaining > 0) {
            int read = content.read(buffer);
            log("block of " + read, Project.MSG_DEBUG);
            toServer.write(buffer, 0, read);
            remaining -= read;
            if (verbose) {
                showProgressChar('^');
            }
        } 
        if (verbose) {
            showProgressChar('\n');
        }
        log("upload completed", Project.MSG_DEBUG);
        return connection;
    }

    /**
     * internal event handler called after a connect can throw an exception
     * or return false for an immediate exit from the process
     *
     * @param connection
     * 		the now open connection
     * @return true if the execution is to continue
     * @exception BuildException
     * 		Description of Exception
     */
    protected boolean onConnected(java.net.URLConnection connection) throws org.apache.tools.ant.taskdefs.optional.http.BuildException {
        return true;
    }

    /**
     * internal event handler called after the download is complete the
     * code can still bail out at this point, and the connection may contain
     * headers of interest. can throw an exception or return false for
     * an immediate exit from the process
     *
     * @param connection
     * 		the now open connection
     * @return true if the execution is to continue
     * @exception BuildException
     * 		Description of Exception
     */
    protected boolean onDownloadFinished(java.net.URLConnection connection) throws org.apache.tools.ant.taskdefs.optional.http.BuildException {
        return true;
    }

    /**
     * Enumerated attribute for "authType" with the value "basic" (note,
     * eventually we can add "digest" authentication)
     *
     * @author matt_h@pobox.com;
     * @created March 17, 2001
     */
    public static class AuthMethodType extends org.apache.tools.ant.types.EnumeratedAttribute {
        /**
         * Gets the possible values of authorisation supported
         *
         * @return The Values value
         */
        public java.lang.String[] getValues() {
            return new java.lang.String[]{ "none", "basic", "digest" };
        }

        /**
         * lookup from value to a numeric value. defaults to 0, basic-auth
         *
         * @param choice
         * 		string selection
         * @return selected value
         */
        public int mapValueToNumber() {
            java.lang.String choice = getValue();
            int value = 0;
            java.lang.String[] values = getValues();
            for (int i = 0; i < values.length; i++) {
                if (values[i].equalsIgnoreCase(choice))
                    value = i;

            }
            return value;
        }
    }
}