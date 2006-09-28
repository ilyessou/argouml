#!/bin/sh
# $Id$

BUILD=PACKRELEASE:

echo $BUILD The purpose of this shellscript is to take the contents
echo $BUILD of the build directory, tar/zip/whatever it and upload it.

# Check that JAVA_HOME is set.
if test ! -x $JAVA_HOME/bin/javac
then
    echo JAVA_HOME is not set correctly.
    exit 1;
fi

if test ! -d ../svn/argouml-downloads/trunk/www
then
    echo The output directory ../svn/argouml-downloads/trunk/www does not exist.
    exit 1;
fi

echo "Give the name of the release (like X.Y.Z)."
read releasename

directory=VERSION_`echo $releasename | sed 's/\./_/g'`_F

if test ! -d $directory
then
    echo The directory $directory does not exist.
    exit 1;
fi
cd $directory

echo "$BUILD Create the zip and tar files."
mkdir DIST
(
  cd argouml/build;
  $JAVA_HOME/bin/jar cvf ../../DIST/ArgoUML-$releasename.zip *.jar README.txt *.sh *.bat
  tar cvf ../../DIST/ArgoUML-$releasename.tar *.jar README.txt *.sh *.bat
)
(
  cd argouml/lib;
  $JAVA_HOME/bin/jar cvf ../../DIST/ArgoUML-$releasename-libs.zip *.txt *.jar
  tar cvf ../../DIST/ArgoUML-$releasename-libs.tar *.txt *.jar
)
(
  cd argouml/build;
  $JAVA_HOME/bin/jar cvf ../../DIST/ArgoUML-$releasename-modules.zip ext/*.jar
  tar cvf ../../DIST/ArgoUML-$releasename-modules.tar ext/*.jar
)
(
  SRCDIRS="argouml/src_new argouml/src/*/src argouml/src/i18n/*/src argouml/modules/cpp/src argouml/modules/classfile/src argouml/modules/idl/src argouml/modules/php/src argouml-*/src"
  $JAVA_HOME/bin/jar cvf DIST/ArgoUML-$releasename-src.zip $SRCDIRS
  tar cvf DIST/ArgoUML-$releasename-src.tar --exclude="CVS" $SRCDIRS
)
( cd DIST && gzip -v *.tar )
cp argouml/build/*.pdf DIST

# Create the Appbund for MacOS
(
  cd argouml/build;
  mkdir ArgoUML.app
  mkdir ArgoUML.app/Contents
  mkdir ArgoUML.app/Contents/Resources
  mkdir ArgoUML.app/Contents/Resources/Java
  mkdir ArgoUML.app/Contents/MacOS

  # Copy the specific things
  (
    cd ../src_new/bin/appbund/
    cp PkgInfo ../../../build/ArgoUML.app/Contents
    cp GenericJavaApp.icns ../../../build/ArgoUML.app/Contents/Resources
    cp JavaApplicationStub ../../../build/ArgoUML.app/Contents/MacOS
  )
  # Format the Info.plist file
  ls *.jar > ArgoUML.app/temp.list
  cat < ../src_new/bin/appbund/Info.plist |
    sed 's/@VERSION_NUMBER@/'$releasename'/' |
    awk '$0 == "@FILE_LIST@" {
            while ((getline line < "ArgoUML.app/temp.list" > 0))
                printf "<string>$JAVAROOT/%s</string>\n", line
            next
         }
         { print }' |
    cat > ArgoUML.app/Contents/Info.plist
  rm ArgoUML.app/temp.list

  cp *.jar ArgoUML.app/Contents/Resources/Java
  tar cvf - ArgoUML.app |
  gzip > ../../DIST/ArgoUML-$releasename.app.tgz
)
    
# Copy the Java Web Start stuff
mkdir DIST/jws
cp argouml/build/*.jar DIST/jws
mkdir DIST/jws/ext
cp argouml/build/ext/*.jar DIST/jws/ext
for jnlpfile in argouml/src_new/templates/jnlp/*.jnlp
do
  sed "s,@URLROOT@,http://argouml-downloads.tigris.org/argouml-$releasename,g;s,@VERSION@,$releasename,g" < $jnlpfile > DIST/jws/`basename $jnlpfile`
done

sed "s,@URLROOT@,http://argouml-downloads.tigris.org/nonav/argouml-$releasename,g;s,@VERSION@,$releasename,g" < argouml/src_new/templates/release_html.template > DIST/index.html

echo $BUILD copying to the svn directory
mv DIST ../../svn/argouml-downloads/trunk/www/argouml-$releasename

echo Add and commit the newly created directory
echo ../svn/argouml-downloads/trunk/www/$directoryname

echo Update the index.html in the argouml-downloads project.

echo "Copy the index file to the download directory (argouml/www/download)"
echo and add and commit it there. This is not in the tagged version but
echo in the echo original version.
