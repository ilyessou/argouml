Welcome to ArgoUML!

In case you just downloaded and unzipped your ArgoUML distribution, 
here's how to proceed:

First, you need Java2 installed, get it from 
  http://java.sun.com 
for your platform. 

Any Java2 JDK or JRE should work. Our preferred version is JDK 1.4.x, 
but JDK 1.2.x and JDK 1.3.x should also work. Please let us know if you 
experience any JDK related problems. Try typing "java" in a console window 
to see if Java is successfully installed. 
You should get a list of your options. If not, try reinstalling Java.

Now (after unpacking the distro, which you obviously have done, because this 
file was in it) you should have a bunch of .jar files 
(argouml.jar, nsuml.jar and others...) in your directory.
Please do NOT unpack the .jars! Just leave them as jar files.

Now you can just start the argouml.jar, on a Windows platforms by 
double-clicking it. 

And you can as well type the following at a command line console: 
  java -jar argouml.jar
which is better because you can see all the messages being generated 
in ArgoUML and that makes it easier for you to understand
what's going on and you can copy some of the message (though not all) to the 
clipboard if ArgoUML behaves funny...

The same effect can be obtained by creating a batch-file (in the same dir as 
argouml.jar), with the following contents:
  java -jar argouml.jar
which can then be started via a shortcut on the desktop.

Detailed instructions in running ArgoUML from a command line console on Windows:

Suppose that you have downloaded ArgoUML to the C: drive,  so your directory 
looks like this:
  c:\ArgoUML
Open your console (for M$ users this is the DOS prompt) and type
  cd \
to set the current directory to the root. Then type:
  c\:>cd ArgoUML
which will give you the prompt:
  c:\ArgoUML>
Then type: 
  dir 
to see everything in the ArgoUML folder. You should have the argouml.jar file 
present in this folder. Note that you need all downloaded jar files together 
in this directory.
Then type
  java -jar argouml.jar
That's it, ArgoUML should start up now. 

If not, please consider asking on the users mailing list at 
  users@argouml.tigris.org
or you can subscribe by sending a mail to 
  users-subscribe@argouml.tigris.org

Have fun!