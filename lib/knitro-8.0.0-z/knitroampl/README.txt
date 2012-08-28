
Using KNITRO with AMPL
----------------------

NOTE: This directory contains two versions of the KNITRO-AMPL binary
file for 64-bit Linux.  The default binary "knitroampl" implements
parallel features using OpenMP.  This binary is built using
gcc 4.4 and glibc 2.5. In order to use this binary you must have a
newer installation of Linux that provides the required OpenMP
dependent libraries.  If you have an older installation of Linux that
is not compatible with the default "knitoampl" binary,  then you can
use the older binary "knitroampl_s" that is provided.  This binary
DOES NOT use OpenMP and thus do not implement any of the parallel
features in KNITRO. The sequential binary "knitroampl_s" is built
using gcc 3.4.2 with glibc 2.3.4.

In order to use KNITRO with AMPL, you will need to puchase a version
of AMPL (available from Ziena), or download the free student/evaluation
version (which is constrained to 300 variables) from 
  http://www.ampl.com/DOWNLOADS/details.html#StudentEd

Other locations for the free version of AMPL:
  http://netlib.bell-labs.com/netlib/ampl/student/mswin/ampl.exe.gz
  http://netlib.bell-labs.com/netlib/ampl/student/linux/ampl.gz
  http://netlib.bell-labs.com/netlib/ampl/student/solaris/ampl.gz


To choose the knitro solver, at the AMPL command prompt type

	option solver knitroampl;

  or if the knitro executable was not in your path

	option solver "/path/to/your/knitroampl";
  for example
	option solver "../bin/knitroampl";                (Unix)
        option solver "c:\Program Files\...\knitroampl";  (Windows)

User definable KNITRO parameters can be set as follows

	option knitro_options "maxit=100 alg=1";

  which makes the maximum number of allowable iterations 100,
  and chooses the interior-point direct algorithm.

To load and solve the test problem, type

        model "testproblem.mod";
	option solver knitroampl;
        solve;

For additional help please see the knitro documentation in
the doc directory of this distribution.  It is also available at
   http://www.ziena.com/documentation.html

Also of assistance is the AMPL tutorial and reference book
   http://isbn.nu/0534388094
with first chapter available online at
   http://www.ampl.com/BOOK/ch1-2.pdf


To build the KNITRO interface for AMPL
--------------------------------------

This directory contains code to build the KNITRO solver interface
for AMPL.  A precompiled version is provided in this directory for
your convenience.  You might want to compile your own version to
use special compiler flags, or to use a different version of the
AMPL solver library.

To create the AMPL executable for KNITRO you need the following AMPL
library source code files which are available from

   http://netlib.bell-labs.com/netlib/ampl/solvers/
   ftp://netlib.bell-labs.com/netlib/ampl/solvers.tar


amplsolver.a :  AMPL library created from makefile
                in AMPL solvers/ directory
arith.h
asl.h
asl_pfgh.h
funcadd.h
getstub.h
nlp2.h
psinfo.h
stdio1.h

Once you have gotten all these files, place them in this directory
(or modify the AMPL_SOLVERS path in the makefile) and type 'make' to
create the KNITRO AMPL executable file "knitroampl".
