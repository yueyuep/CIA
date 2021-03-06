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
package org.apache.ant.bootstrap;
import org.apache.ant.init.ClassLocator;
/**
 * The Bootstrap class initailses the boot strap build, then loads the
 * Builder class to perform the bootstrap build.
 *
 * @author Conor MacNeill
 * @created 18 February 2002
 */
public class Bootstrap {
    /**
     * The main program - adds tools.jar and runs build
     *
     * @param args
     * 		The command line arguments
     * @exception Exception
     * 		if there is a bootstrap problem
     */
    public static void main(java.lang.String[] args) throws java.lang.Exception {
        java.lang.System.out.println("Bootstrapping mutant");
        java.net.URL bootstrapURL = org.apache.ant.init.ClassLocator.getClassLocationURL(org.apache.ant.bootstrap.Bootstrap.class);
        java.net.URL builderURL = new java.net.URL(bootstrapURL, "../builder/");
        java.net.URL toolsJarURL = org.apache.ant.init.ClassLocator.getToolsJarURL();
        java.net.URL[] urls = new java.net.URL[]{ builderURL, toolsJarURL };
        java.lang.ClassLoader builderLoader = new java.net.URLClassLoader(urls);
        // org.apache.ant.init.LoaderUtils.dumpLoader(System.out,
        // builderLoader);
        java.lang.Class builderClass = java.lang.Class.forName("org.apache.ant.builder.Builder", true, builderLoader);
        final java.lang.Class[] param = new java.lang.Class[]{ java.lang.Class.forName("[Ljava.lang.String;") };
        final java.lang.reflect.Method main = builderClass.getMethod("main", param);
        final java.lang.Object[] argument = new java.lang.Object[]{ args };
        main.invoke(null, argument);
    }
}