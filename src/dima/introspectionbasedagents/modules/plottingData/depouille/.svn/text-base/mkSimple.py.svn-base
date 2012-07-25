# Simplify display -- display only 1 line out of N from results.log file
# 2008 01 09 - niko(at)lri.fr
#
# Syntax: simply execute the script without any command line parameters
# "results.log" input file is hard coded as well as N parameter
#
# -n.


import glob
import os
from array import array


max = 4 # number of line to ignore --- THIS IS THE PARAMETER (default is 0)

k = -1 # mandatory to get first line

f = open("rawresults.log")
try:
    for line in f:
        if k<0: # first line of file is legend (exception)
            print line,
            k=0
        else:
            if k==0:
                print line,
                k = max
            else:
                k = k - 1
finally:
    f.close()
