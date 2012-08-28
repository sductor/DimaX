######################################################################
#
#  Copyright (c) 2006-2011 by Ziena Optimization LLC
#
#  Makefile for Unix platforms.
#
#  Edit the macro for OPTPROB to solve one of the example problems.
#    To build on Linux:    gmake
#    To build on MacOSX:   gnumake
#    To build on Solaris:  gmake
#
#  This makefile builds both statically linked and dynamically linked
#  versions of each example.  The dynamic versions cannot execute unless
#  ../../lib is added to $LD_LIBRARY_PATH (on MacOSX use the environment
#  variable $DYLD_LIBRARY_PATH).
#    bash shells:  export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:../../lib
#    tcsh shells:  setenv LD_LIBRARY_PATH ${LD_LIBRARY_PATH}:../../lib
#
######################################################################

# Choose a problem to solve by uncommenting one of the lines below.
OPTPROB = problemHS15
#OPTPROB = problemQCQP
OPTPROB_MIP = problemMINLP

# Set the location of KNITRO.
KNDIR = ../..
KNRELEASE = 800

# Set up platform-specific make parameters.
UNAME = $(shell uname -s)

# These are parameters for Linux platforms.
ifeq ($(findstring Linux,${UNAME}), Linux)
  CC = gcc
  LD = g++
  LIBS = -ldl
  KNLIB_STATIC  = $(KNDIR)/lib/libknitro$(KNRELEASE).a
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
  CC = gcc -arch x86_64 -fopenmp
  LD = g++ -arch x86_64 -fopenmp
  LIBS = -ldl
  KNLIB_STATIC  = $(KNDIR)/lib/libknitro$(KNRELEASE).a
  KNLIB_DYNAMIC = $(KNDIR)/lib/libknitro.dylib
  CFLAGS = -c -O
endif

# These are parameters for Solaris platforms.
ifeq ($(findstring SunOS,${UNAME}), SunOS)
  CC = gcc
  LD = g++ -m64
  LIBS = -ldl -lsocket -lnsl -lm
  KNLIB_STATIC  = $(KNDIR)/lib/libknitro$(KNRELEASE).a
  KNLIB_DYNAMIC = $(KNDIR)/lib/libknitro.so
  CFLAGS = -c -O -m64
endif

######################################################################

TGTS = \
callback1_static \
callback1_dynamic \
callback2_static \
callback2_dynamic \
callbackMINLP_static \
callbackMINLP_dynamic \
checkDers_static \
checkDers_dynamic \
restart_static \
restart_dynamic \
reverseComm_static \
reverseComm_dynamic \
reverseCommMINLP_static \
reverseCommMINLP_dynamic

default: $(TGTS)


problemHS15.o : problemHS15.c problemDef.h
	$(CC) $(CFLAGS) -I$(KNDIR)/include $<

problemQCQP.o : problemQCQP.c problemDef.h
	$(CC) $(CFLAGS) -I$(KNDIR)/include $<

problemMINLP.o : problemMINLP.c problemDef.h
	$(CC) $(CFLAGS) -I$(KNDIR)/include $<

callbackExample1.o : callbackExample1.c problemDef.h
	$(CC) $(CFLAGS) -I$(KNDIR)/include $<

callbackExample2.o : callbackExample2.c problemDef.h
	$(CC) $(CFLAGS) -I$(KNDIR)/include $<

callbackExampleMINLP.o : callbackExampleMINLP.c problemDef.h
	$(CC) $(CFLAGS) -I$(KNDIR)/include $<

checkDersExample.o : checkDersExample.c problemDef.h
	$(CC) $(CFLAGS) -I$(KNDIR)/include $<

restartExample.o : restartExample.c problemDef.h
	$(CC) $(CFLAGS) -I$(KNDIR)/include $<

reverseCommExample.o : reverseCommExample.c problemDef.h
	$(CC) $(CFLAGS) -I$(KNDIR)/include $<

reverseCommExampleMINLP.o : reverseCommExampleMINLP.c problemDef.h
	$(CC) $(CFLAGS) -I$(KNDIR)/include $<


callback1_static: $(OPTPROB).o callbackExample1.o $(KNLIB_STATIC)
	$(LD) -o $@ $^ $(LIBS)
callback1_dynamic: $(OPTPROB).o callbackExample1.o $(KNLIB_DYNAMIC)
	$(LD) -o $@ $^ $(LIBS)

callback2_static: $(OPTPROB).o callbackExample2.o $(KNLIB_STATIC)
	$(LD) -o $@ $^ $(LIBS)
callback2_dynamic: $(OPTPROB).o callbackExample2.o $(KNLIB_DYNAMIC)
	$(LD) -o $@ $^ $(LIBS)

callbackMINLP_static: $(OPTPROB_MIP).o callbackExampleMINLP.o $(KNLIB_STATIC)
	$(LD) -o $@ $^ $(LIBS)
callbackMINLP_dynamic: $(OPTPROB_MIP).o callbackExampleMINLP.o $(KNLIB_DYNAMIC)
	$(LD) -o $@ $^ $(LIBS)

checkDers_static: $(OPTPROB).o checkDersExample.o $(KNLIB_STATIC)
	$(LD) -o $@ $^ $(LIBS)
checkDers_dynamic: $(OPTPROB).o checkDersExample.o $(KNLIB_DYNAMIC)
	$(LD) -o $@ $^ $(LIBS)

restart_static: $(OPTPROB).o restartExample.o $(KNLIB_STATIC)
	$(LD) -o $@ $^ $(LIBS)
restart_dynamic: $(OPTPROB).o restartExample.o $(KNLIB_DYNAMIC)
	$(LD) -o $@ $^ $(LIBS)

reverseComm_static: $(OPTPROB).o reverseCommExample.o $(KNLIB_STATIC)
	$(LD) -o $@ $^ $(LIBS)
reverseComm_dynamic: $(OPTPROB).o reverseCommExample.o $(KNLIB_DYNAMIC)
	$(LD) -o $@ $^ $(LIBS)

reverseCommMINLP_static: $(OPTPROB_MIP).o reverseCommExampleMINLP.o $(KNLIB_STATIC)
	$(LD) -o $@ $^ $(LIBS)
reverseCommMINLP_dynamic: $(OPTPROB_MIP).o reverseCommExampleMINLP.o $(KNLIB_DYNAMIC)
	$(LD) -o $@ $^ $(LIBS)


clean:
	rm -f *.o
	rm -f $(TGTS)
