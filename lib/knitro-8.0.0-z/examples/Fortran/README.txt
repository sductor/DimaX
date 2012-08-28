Using KNITRO with Fortran
-------------------------

This directory contains example code illustrating one way to
call KNITRO from a Fortran language application.

README.txt:           This file.

makefile              Makefile that builds all examples on Unix machines.
                      To execute on Linux, type "gmake".
                      For Solaris, follow instructions in the makefile.

makefile.win          Makefile that builds all examples on Windows machines
                      using the Intel Visual Fortran 9.0 and Microsoft Visual
                      C++ compilers.
                      To execute, type "nmake -f makefile.win".

exampleProgram.f      An example driver that solves any problem implemented
                      with routines like those of "problemQCQP.f".  KNITRO is
                      invoked using the reverse communications API.  Reverse
                      communications means KNITRO returns to the calling
                      program whenever it needs problem information.

problemQCQP.f         Fortran routines that define a simple quadratically
                      constrained quadratic programming problem.  The same
                      problem is implemented for the C language API in
                      "examples/C/problemQCQP.c".

knitro_fortran.c      A set of C wrappers that export KNITRO function calls
                      to Fortran.  The most essential KNITRO calls are
                      provided; others may be added in a similar manner.

knitro.opt            A sample KNITRO input file of user options.  Contents
                      can be modified using any text-based editor.  The file
                      is read by "exampleProgram.f".
