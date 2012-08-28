Usage:

java -classpath bin exec.DCOPApplication <DCOP File> <Algorithm> <k/t Parameter> <Maximum Cycles>

E.g.,
To run topt with t=1 on 1.dcop with a maximum cycle of 500:
java -classpath bin exec.DCOPApplication 1.dcop TOPT 1 500

Other settings can be adjusted in the DCOPApplication.java, including lock window size, partial locking or not, and showing gui or not.