######################################################################
#
#  Copyright (c) 2006-2011 by Ziena Optimization LLC
#
#  Makefile for Unix platforms.
#    To build on Linux:    gmake
#    To build on MacOSX:   gnumake
#    To build on Solaris:  gmake
#
#  Assume the javac compiler is present in $PATH.
#
#  The examples built in this makefile cannot execute unless ../../lib
#  is added to $LD_LIBRARY_PATH (on MacOSX use the environment variable
#  $DYLD_LIBRARY_PATH).
#    bash shells:  export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:../../lib
#    tcsh shells:  setenv LD_LIBRARY_PATH ${LD_LIBRARY_PATH}:../../lib
#  Then to run the example:
#    java -cp .:knitro800java.jar exampleHS15
#    java -cp .:knitro800java.jar exampleMINLP
#  or for 64-bit use:
#    java -d64 -cp .:knitro800java.jar exampleHS15
#    java -d64 -cp .:knitro800java.jar exampleMINLP
#
######################################################################

# Set the location of the KNITRO jar file.
KNRELEASE = 800
KNJAR = knitro$(KNRELEASE)java.jar

TGT_CLASSES=\
  exampleHS15.class \
  exampleMINLP.class

default: $(TGT_CLASSES)


exampleHS15.class : exampleHS15.java $(KNJAR)
	javac -classpath .:$(KNJAR) $<

exampleMINLP.class : exampleMINLP.java $(KNJAR)
	javac -classpath .:$(KNJAR) $<



clean:
	rm -f $(TGT_CLASSES)
