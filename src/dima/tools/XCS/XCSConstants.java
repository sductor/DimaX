package dima.tools.XCS;

import java.io.Serializable;

/**
 * This class provides all relevant learning parameters for the XCS as well as
 * other experimental settings and flags. Most parameter-names are chosen similar to
 * the 'An Algorithmic Description of XCS' ( Butz&Wilson, IlliGAL report 2000017).
 *
 * @author    Martin V. Butz
 * @version   XCSJava 1.0
 * @since     JDK1.1
 */

public class XCSConstants implements Serializable
{
    /**
	 *
	 */
	private static final long serialVersionUID = -7800874285185939602L;

	/**
     * Specifies the maximal number of micro-classifiers in the population.
     * In the multiplexer problem this value is set to 400, 800, 2000 in the 6, 11, 20 multiplexer resp..
     * In the Woods1 and Woods2 environment the parameter was set to 800.
     */
    final public static int maxPopSize=800;

    /**
     * The fall of rate in the fitness evaluation.
     */
    final public static double alpha=0.1;

    /**
     * The learning rate for updating fitness, prediction, prediction error,
     * and action set size estimate in XCS's classifiers.
     */
    final public static double beta=0.001; //chang�e le 131103 �tait O.2

    /**
     * The discount rate in multi-step problems.
     */
    final public static double gamma=0.95;

    /**
     * The fraction of the mean fitness of the population below which the fitness of a classifier may be considered
     * in its vote for deletion.
     */
    final public static double delta=0.1;

    /**
     * Specifies the exponent in the power function for the fitness evaluation.
     */
    final public static double nu=5.;

    /**
     * The threshold for the GA application in an action set.
     */
    final public static double theta_GA=25;

    /**
     * The error threshold under which the accuracy of a classifier is set to one.
     */
    final public static double epsilon_0=10;

    /**
     * Specified the threshold over which the fitness of a classifier may be considered in its deletion probability.
     */
    final public static int theta_del=20;

    /**
     * The probability of applying crossover in an offspring classifier.
     */
    final public static double pX=0.8;

    /**
     * The probability of mutating one allele and the action in an offspring classifier.
     */
    final public static double pM=0.04;

    /**
     * The probability of using a don't care symbol in an allele when covering.
     */
    final public static double P_dontcare=0.5;

    /**
     * The reduction of the prediction error when generating an offspring classifier.
     */
    final public static double predictionErrorReduction=0.25;

    /**
     * The reduction of the fitness when generating an offspring classifier.
     */
    final public static double fitnessReduction=0.1;

    /**
     * The experience of a classifier required to be a subsumer.
     */
    final public static int theta_sub=20;

    /**
     * The maximal number of steps executed in one trial in a multi-step problem.
     */
    final public static int teletransportation=50;

    /**
     * Specifies if GA subsumption should be executed.
     */
    final public static boolean doGASubsumption=true;

    /**
     * Specifies if action set subsumption should be executed.
     */
    final public static boolean doActionSetSubsumption=true;

    /**
     * The initial prediction value when generating a new classifier (e.g in covering).
     */
    final public static double predictionIni=10.0;

    /**
     * The initial prediction error value when generating a new classifier (e.g in covering).
     */
    final public static double predictionErrorIni=0.0;

    /**
     * The initial prediction value when generating a new classifier (e.g in covering).
     */
    final public static double fitnessIni=0.01;

    /**
     * The don't care symbol (normally '#')
     */
    final public static char dontCare='#';

    /**
     * The initialization of the pseudo random generator. Must be at lest one and smaller than _M.
     */
    private static long seed=1;

    /**
     * Constant for the random number generator (modulus of PMMLCG = 2^31 -1).
     */
    final private static long _M = 2147483647;

    /**
     * Constant for the random number generator (default = 16807).
     */

    final private static long _A = 16807;
    /**
     * Constant for the random number generator (=_M/_A).
     */

    final private static long _Q = _M/_A;
    /**
     * Constant for the random number generator (=_M mod _A).
     */
    final private static long _R = _M%_A;

    /**
     * The default constructor.
     */
    public XCSConstants()//Default constructor
    {}
    /**
     * Returns a random number in between zero and one.
     */
    public static double drand()
    {
	final long hi   = seed / _Q;
	final long lo   = seed % _Q;
	final long test = _A*lo - _R*hi;

	if (test>0)
	    seed = test;
	else
	    seed = test+_M;

	return (double)seed/_M;
    }
    /**
     * Sets a random seed in order to randomize the pseudo random generator.
     */
    public static void setSeed(final long s)
    {
	seed=s;
    }
}
