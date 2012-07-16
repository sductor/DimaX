# My own "depouille" script
# 2008 01 09 - niko(at)lri.fr
#
# Syntax: simply execute the script without any command line parameters
#
# takes the same column from each file, for each line compute median, best, worst, upper and lower quartiles
# display also no of iterations
# 
# assumes that:
# - all files that end with the ".dat" suffix are to be taken into account
# - data file is a standard cmaes "*_fit.dat" log file
# - hard-coded targetIndex value targets the correct column (hint: use either best or bestever column)
#
# note that:
# - different data files may not be of the same length
# - hint: dont use the ".dat" extension for the output file (avoid messing up when relaunching script -- remember, all "*.dat" are considered as input! ) :-)
# - in the same directory, you can find a gnuplot script that can be used to display the resulting compiled datafile (whisker-box display)
# 
# -n.


import glob
import os
from array import array

# takes a float, tells if integer cast value is same value
def isInteger(value=0.0):
    if value == float(int(value)):
        return True
    else:
        return False

path=r'./'
i = 0
filenames = glob.glob(os.path.join(path,'*.dat'))
dataFile = []

# load files
for filename in filenames:
    dataFile.append(open(filename, 'r').readlines())
    #print i
    #print ''.join(dataFile[i])
    i = i + 1

i = 0
targetIndex = 2 # 4: best, 3: bestever

allData = []
maxLen = 0
maxIndex = 0

# prepare data
for i in range(len(filenames)):
    #for texte in ''.join(dataFile[i]).split('\n'):
    texte = ''.join(dataFile[i]).split('\n')
    allData.append(texte);
    if len(texte) > maxLen :
        maxLen = len(texte)
        maxIndex = i
    #print str(i) + ': ' + allData[0][2].split(' ')[1] + ' , ' + allData[0][2].split(' ')[targetIndex] 

# parse data and compile new data
print '# nbEval, min, lowerQuartile, median, upperQuartile, max'
for i in range(2,maxLen-1):  # ignore 2 first lines

    # display stats (1/2) (iterations)
    print allData[maxIndex][i].split(' ')[1],
    
    # get raw data
    
    values = []
    for j in range(len(filenames)):
        if i < len(allData[j])-1: # some files may be shorter than other
            values.append( float(allData[j][i].split(' ')[targetIndex]) )
    values.sort()
    #print ' '
    #print values
    
    # compute stats
    
    medianIndex = (len(values) - 1.) / 2.
    lowerQuartileIndex = medianIndex / 2.
    upperQuartileIndex = medianIndex / 2. * 3.
    
    #print str(lowerQuartileIndex) + ' ; ' + str(medianIndex) + ' ; ' + str(upperQuartileIndex)

    min    = values[0]
    max    = values[-1]

    if isInteger(medianIndex) is True:
        median = values[int(medianIndex)]
    else:
        median = ( values[int(medianIndex)] + values[int(medianIndex)+1] ) / 2.
    
    if isInteger(lowerQuartileIndex) is True:
        lowerQuartileValue = values[int(lowerQuartileIndex)]
    else:
        lowerQuartileValue = ( values[int(lowerQuartileIndex)] + values[int(lowerQuartileIndex)+1] ) / 2.
    
    if isInteger(upperQuartileIndex) is True:
        upperQuartileValue = values[int(upperQuartileIndex)]
    else:
        upperQuartileValue = ( values[int(upperQuartileIndex)] + values[int(upperQuartileIndex)+1] ) / 2.

    # display stats (2/2) (whisker+box values)
    print ' ' + str(min) + ' ' + str(lowerQuartileValue) + ' ' + str(median) + ' ' + str(upperQuartileValue) + ' ' + str(max)

            




