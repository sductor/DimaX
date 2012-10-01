######################################################################
#
#  Copyright (c) 2006-2008 by Ziena Optimization, Inc.
#
#  Makefile for KNITRO-AMPL solver on Unix platforms.
#    To build on Linux:    gmake   <debug>
#    To build on MacOSX:   gnumake <debug>
#    To build on Solaris:  gmake   <debug>
#
#  Users must supply the AMPL SDK.
#
#  Builds a statically linked executable.
#  See examples/C/makefile for dynamic linking with libknitro.so.
#
######################################################################

#---- Set locations.
AMPL_DIR = netlib/ampl/solvers
KN_DIR = ..

#---- Set up platform specific parameters.
UNAME = $(shell uname -s)
ifeq ($(findstring Linux,${UNAME}),Linux)
  CC = gcc
  CFLAGS = -O -Wall -DNON_STDIO -Dxargv=f__xargv
  PLT_CFLAGS =
  LD = g++
  SYSLIBS = -lm -ldl

  #---- Try to detect AMD64 processor.
  MNAME = $(shell uname -m)
  ifeq ($(findstring x86_64,${MNAME}),x86_64)
    PLT_CFLAGS = -m64
  endif
endif
ifeq ($(findstring SunOS,${UNAME}),SunOS)
  CC = cc
  CFLAGS = -O -DMAIN__=main_ -Dxargv=__xargv
  PLT_CFLAGS =
  LD = CC
  SYSLIBS = -lm -ldl -lsocket -lnsl
endif
ifeq ($(findstring Darwin,${UNAME}),Darwin)
  CC = gcc
  CFLAGS = -O -Wall -DNON_STDIO -Dxargv=f__xargv
  PLT_CFLAGS =
  LD = g++
  SYSLIBS = -lm -ldl -lpthread
endif


######################################################################

defaut: knitroampl

debug: DBG_CFLAGS = -g
debug: knitroampl

######################################################################

knitroampl.o: knitroampl.c
	$(CC) -c $(CFLAGS) $(PLT_CFLAGS) $(DBG_CFLAGS) -I$(AMPL_DIR) -I$(KN_DIR)/include $*.c


K_LIB    = $(KN_DIR)/lib/libknitro.a
AMPL_LIB = $(AMPL_DIR)/amplsolver.a

knitroampl: knitroampl.o $(K_LIB) $(AMPL_LIB)
	$(LD) -o knitroampl $^ $(SYSLIBS)

clean:
	rm -f knitroampl knitroampl.o
