# create a graph from an "exp.data" file
# example for plotting data from a neural network learning
#
# syntaxe:  gnuplot plotNN.gp
#
# no_iteration learning_error generalization_error
# an output file "exp.eps" is created

set xlabel 'iteration (n)'
set ylabel 'errors'
set title 'Neural network learning results'
set key left bottom
set key box 
set datafile separator "," 

set yrange[0:*]
set xrange[0:*]

plot 'expNN.dat' using 1:($2) title "error on examples used for learning" with lines, 'expNN.dat' using 1:($3) title "error on examples used for validation" with lines

# Decommenter ce qui suit pour generer un fichier EPS en sortie

#set term post eps "Times-Roman" 8
#set size 5./10., 3./7.
#set output 'exp.eps'
#replot
#set term X11

# OU ALORS: 
set term postscript eps enhanced monochrome
set output 'exp.eps'
replot

pause -1 
