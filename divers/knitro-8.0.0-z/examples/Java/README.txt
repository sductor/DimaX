Using KNITRO with Java
----------------------

This directory contains example code illustrating one way to
call KNITRO from a Java language application.

After compiling, run the examples by typing:
  java -cp .:knitrojava.jar exampleHS15
  java -cp .:knitrojava.jar exampleMINLP


README.txt:           This file.

makefile              Makefile that builds all examples on Unix machines.
                      To execute on Linux, type "gmake", on Mac OS X type
                      "gnumake", and on Solaris type "make".

makefile.win          Makefile that builds all examples on Windows machines.
                      To execute, type "nmake -f makefile.win".

knitrojava.jar        The jar file that interfaces with KNITRO through JNI.
                      The class inside makes KNITRO calls available in Java
                      while maintaining thread-safety of the code.
                      Users may extract the Java source code and examine
                      it for better understanding of the KNITRO Java API
                      (to extract:  jar xf knitrojava.jar).

exampleHS15.java      An example driver that solves a small nonlinear
                      optimization problem.  KNITRO is invoked using the
                      reverse communications API.  Reverse communications
                      means KNITRO returns to the calling program whenever
                      it needs problem information.  The example must load
                      the KNITRO JNI binary at runtime.  See the makefile
                      for instructions on how to make the JNI binary
                      available to the Java executable.

exampleMINLP.java     Another example driver that solves a small nonlinear
                      mixed integer optimization problem.  KNITRO is invoked
                      using the reverse communications API, very similar
                      to exampleHS15.
