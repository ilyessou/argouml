This document shortly describes how to compile the ArgoUML sources 
after you downloaded them. There is more information in the file build.xml.

At the bottom of this page you will find how to compile under Netbeans 3.x

First, you need to get Java installed. The easiest way to check that is to get
a command line and type java. A list of options should appear.

At the moment JDK1.4 is best for Argo, but 1.3 works also. Please do
set the JAVA_HOME environment variable to where Java is installed on your disk.
For example under windoze:
  set JAVA_HOME=C:\jdk1.3.1

Then, you need at least ANT 1.2 (http://jakarta.apache.org) installed. 
Ant 1.4.1 works as well. 

This includes setting the ANT_HOME environment variable.
For example for Windoze:
  path C:\jdk1.3.1\bin;C:\jakarta-ant-1.4.1\bin
  set ANT_HOME=C:\jakarta-ant-1.4.1

Here's a sample autoexec.bat that works under Win98:

  path C:\jdk1.3.1\bin;C:\jakarta-ant-1.4.1\bin
  set JAVA_HOME=C:\jdk1.3.1
  set ANT_HOME=C:\jakarta-ant-1.4.1

Please note that the build.xml sets its own CLASSPATH, there is no need 
for you to set one up.

Most problems with building Argo will stem from not having set 
the Java environment correctly.

Next, you need to have 
gef.jar, xercesImpl.jar, xml-apis.jar, nsuml.jar, antlrall.jar and ocl-argo.jar in argouml/lib
which must be next to argouml/src_new in your directory structure.
If you've downloaded the CVS then everything is set to run:
type "build" to test the Ant environment.  You should see a
list of options appear.
Type (run) "build package"
and ArgoUML will be compiled. 

<<<<<Netbeans 3.3.x>>>>>>

You must set the src_new folder as a relative mount!

Load the following jars into the project:
antlrall.jar (this is used for Java Re)
gef.jar (this is the diagram handler)
log4j (used for logging, see resource folder for existing log sets)
W3C xml etc
nsuml
ocl-argo (Object constraint language)
xerces
xml-apis
tools (from the jdk library, without this you will probably get SAX errors)

Be sure to set the main class under the Project and then compile. 


Toby 
with modifications by 
Curt Arnold
Dennis Daniels

problems? questions? comments?
users@argouml.org
dev@argouml.org 