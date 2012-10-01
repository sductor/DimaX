Using KNITRO with C
-------------------

This directory contains example code illustrating different ways to
call KNITRO from C language applications.

README.txt:           This file.

makefile              Makefile that builds all examples on Unix machines.
                      To execute on Linux type "gmake", on Mac OS X type
                      "gnumake", and on Solaris type "make".

makefile.win          Makefile that builds all examples on Windows machines
                      using the Microsoft Visual C++ compiler.
                      To execute, type "nmake -f makefile.win".

callbackExample1.c    An example driver that solves any problem implemented
                      with the "problemDef.h" interface using the callback API.
                      Callback means KNITRO is given function pointers that
                      it can invoke whenever it needs problem information.
                      The example defines 3 separate callback functions:
                      one for evaluating functions, one for first derivatives,
                      and one for second derivatives.  The example also shows
                      set user options by reading from the "knitro.opt" file.

callbackExample2.c    An example driver very similar to callbackExample1.
                      The difference is that only 2 callback functions are
                      defined.  The evaluation of functions and their
                      first derivatives is combined into a single callback.
                      The example also shows how to employ the "userParams"
                      argument to store ancillary data between callbacks.

callbackExampleMINLP.c  An example driver that solves a mixed integer
                      nonlinear programming (MINLP) model using the
                      callback API. 

checkDersExample.c    An example driver that uses KNITRO to check the accuracy
                      of derivatives.  The example provides separate routines
                      to evaluate functions and derivatives.  KNITRO uses
                      function evaluations to compute finite difference
                      estimates and compares these with derivative evaluations.
                      The example implements both the callback and reverse
                      communications APIs.

restartExample.c      An example driver similar to callbackExample1, but
                      using KTR_restart to solve the problem repeatedly,
                      varying a user option each time.  The example shows
                      how to restart without reloading the problem definition.

reverseCommExample.c  An example driver that solves any problem implemented
                      with the "problemDef.h" interface using the reverse
                      communications API.  Reverse communications means
                      KNITRO returns to the calling program whenever it
                      needs problem information.  The example also shows
                      how to set user options programmatically, and how to
                      retrieve solution information.

reverseCommExampleMINLP.c  An example driver that solves a mixed integer
                      nonlinear programming (MINLP) model using the
                      reverse communication API. 

blasAcmlExample.c     An example wrapper that makes the AMD Core Math Library
                      (ACML) available for use by KNITRO.  Comments in the
                      file describe the steps necessary to compile and link
                      the dynamic library ("makefile" does not create it).

knitro.opt            A sample KNITRO input file of user options.  Contents
                      can be modified using any text-based editor.  The file
                      is read by callbackExample1 and callbackExample2.
                      A similar file can be generated from any application
                      by calling KTR_save_param_file.


problemDef.h          An example interface for defining nonlinear optimization
                      problems in C.  The interface is designed to work
                      with the example drivers in this directory.

problemHS15.c         An implementation of "problemDef.h" for standard
                      Hock and Schittkowski problem number 15.

problemQCQP.c         An implementation of "problemDef.h" for a simple
                      quadratically constrained quadratic programming problem.

problemMINLP.c        An implementation of "problemDef.h" for a simple
                      mixed integer nonlinear programming example.
