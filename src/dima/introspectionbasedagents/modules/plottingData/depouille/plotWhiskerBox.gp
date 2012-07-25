# source: http://www.gnuplot.info/demo/candlesticks.html
# box-and-whisker plot adding median value as bar

set terminal png
set output 'results.png'

#set boxwidth 0.8 absolute
#set boxwidth 0.2 absolute
#set boxwidth 40 absolute

#set title "box-and-whisker plot adding median value as bar"
#set title "results"

set title "Affichage du fichier results.log"

#set xrange [ 0.00000 : 5000.0000 ] noreverse nowriteback
#set yrange [ 0.00000 : 5.00 ] noreverse nowriteback

#plot 'results.log' using 1:3:2:6:5 with candlesticks lt 3 lw 2 title 'Quartiles', '' using 1:4:4:4:4 with candlesticks lt -1 lw 2 notitle
plot [0:100] [0:1] 'results.log' using 2:3 title 'Essais affichage'

#plot [0:100] [0:1] "/users/nfs/Etu3/2765353/workspace/BaldwinEffect/Stat/results.log" using 2:3 title 'Essais affichage'
#results.log doit être à la hauteur ou on ce trouve au niveau du terminal. pour que la commande
#/usr/bin/gnuplot /users/nfs/Etu3/2765353/workspace/BaldwinEffect/Stat/plotWhiskerBox.gp le trouve


# notes on the experiment format file
# nbEval, min, min_quartile, median, max_quartile, max
# 1       1.5     2       2.4     4       6.
# 2       1.5     3       3.5     4       5.

# notes on results compiled from cmaes
# - file used is compiled from N experiments cmaes file with respect to previous format
# - nbEval is considered (and not iterations) - nbEval is not reset after restart
# - ALL DATA IN RESULT FILE ARE BASED ON BEST VALUES (or bestever -- but not median of each run -- take it as blackbox)
