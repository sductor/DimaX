######################################################################
#
#  Copyright (c) 2006-2011 by Ziena Optimization LLC
#
#  Makefile for Unix platforms:
#    To build on Linux:    gmake
#    To build on MacOSX:   gnumake
#    To build on Solaris:  gmake
#
#  This makefile builds a single driver program and all test problems.
#  Add your own test problem similar to the example.
#
#  The driver dynamically loads a test problem at runtime.  To run,
#  the $LD_LIBRARY_PATH environment variable ($DYLD_LIBRARY_PATH on
#  MacOSX) must include .:../../lib.
#    bash shells:  export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:.:../../lib
#    tcsh shells:  setenv LD_LIBRARY_PATH ${LD_LIBRARY_PATH}:.:../../lib
#
######################################################################

# Set the location of KNITRO.
KNDIR = ../..

# Set up platform-specific make parameters.
UNAME = $(shell uname -s)

# These are parameters for Linux platforms.
ifeq ($(findstring Linux,${UNAME}), Linux)
  CC = g++
  LD = g++
  CFPIC = -fpic
  LDFLAGS = -shared
  LIBS = -ldl
  DYN_TARGET = so
  KNLIB_DYNAMIC = $(KNDIR)/lib/libknitro.so

  CFLAGS = -c -O

  # Try to detect if this is a 64-bit platform.
  MNAME = $(shell uname -m)
  ifeq ($(findstring x86_64,${MNAME}), x86_64)
    CFLAGS = -c -O -m64
  endif
endif

# These are parameters for MacOSX platforms.
ifeq ($(findstring Darwin,${UNAME}), Darwin)
  # Use -arch x86_64 for 64-bit MacOSX
  #CC = g++
  #LD = g++
  CC = g++ -arch x86_64
  LD = g++ -arch x86_64
  CFPIC = -fPIC
  LDFLAGS = -dynamiclib
  LIBS = -ldl
  DYN_TARGET = dylib
  KNLIB_DYNAMIC = $(KNDIR)/lib/libknitro.dylib
  CFLAGS = -c -O
endif

# These are parameters for Solaris platforms.
ifeq ($(findstring SunOS,${UNAME}), SunOS)
  CC = gcc
  LD = g++ -m64
  CFPIC = -fPIC
  LDFLAGS = -shared
  # Use these instead to compile for 64-bit SPARC.
  #CC = CC -xarch=v9
  #LD = CC -xarch=v9
  #CFPIC = -KPIC
  #LDFLAGS = -G
  LIBS = -ldl -lsocket -lnsl -lm
  DYN_TARGET = so
  KNLIB_DYNAMIC = $(KNDIR)/lib/libknitro.so
  CFLAGS = -c -O -m64
endif

######################################################################

TGTS = \
driverExample \
ExampleHS35.$(DYN_TARGET)

default: $(TGTS)



driverExample.o : driverExample.cpp nlpProblemDef.h
	$(CC) $(CFLAGS) -I$(KNDIR)/include $<
driverExample : driverExample.o $(KNLIB_DYNAMIC)
	$(LD) -o $@ $^ $(LIBS)


ExampleHS35.o : ExampleHS35.cpp ExampleHS35.h nlpProblemDef.h
	$(CC) $(CFLAGS) $(CFPIC) -I$(KNDIR)/include $<
ExampleHS35.so : ExampleHS35.o
	$(LD) $(LDFLAGS) -o $@ $^
ExampleHS35.dylib : ExampleHS35.o $(KNLIB_DYNAMIC)
	$(LD) $(LDFLAGS) -o $@ $^


clean:
	rm -f *.o
	rm -f $(TGTS)
