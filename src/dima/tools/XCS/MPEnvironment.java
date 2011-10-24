package dima.tools.XCS;

import java.io.Serializable;

/**
 * This class implements the Multiplexer problem.
 * It provides randomly generated strings (the problem instances), determines if a classification
 * was correct, and which payoff is provided. The problem is implemented in a way that any problem length can be chosen.
 * The class simply constructs the maximal multiplexer problem that fits in the specified problem length. The remaining bits are
 * (as the other bits) generated randomly in each problem instance but are irrelevant for the problem itself.
 *
 * @author    Martin V. Butz
 * @version   XCSJava 1.0
 * @since     JDK1.1
 */
public class MPEnvironment implements Environment, Serializable
{
    /**
	 *
	 */
	private static final long serialVersionUID = -8027358065308634501L;


	/**
     * Specifies the length of each presented problem.
     * The constructor selects the largest multiplexer problem that fits the condition length.
     * Note that any length can be selected. The additional bits are randomly generated and have
     * no influence on the result.
     */
    public static int conLength;


    /**
     * Defines if either a payoff landscape or a 1000/0 payoff is provided after the execution of a classification.
     * The payoff Landscape is constructed as in Wilson's
     * Classifier fitness based on accuracy paper (Evolutionary Computation Journal, 1995).
     */
    public static boolean payoffLandscape;


    /*#####---- The following parameters are set internally dependent on the value of conLength and payoffLandscape ----#####*/

    /**
     * Specifies the maximal payoff possible in this environment.
     */
    private int maxPayoff;

    /**
     * Specifies the number of position bits in the multiplexer problem.
     */
    private final int posBits;

    /**
     * Stores the current problem.
     */
    private final char[] currentState;


    /*#####---- The following parameters are used internally ----#####*/

    /**
     * Stores if the last classification was correct.
     */
    private boolean correct;

    /**
     * Is set to true after a classification was executed
     */
    private boolean reset;

    /**
     * In the Multiplexer problem there are two classifications possible!
     */
    private final int nrActions=2;







    /**
     * Constructs the Multiplexer environment according to the specified problem length and chosen payoff type.
     * Essentially the relevant constants for the environment are calculated here.
     * Moreover, the problem array is generated
     *
     * @param length Specifies the problem length. The biggest Multiplexer problem is chosen that fits this length.
     * @param payoffMap Specifies if a payoff map should be provided or a 1000/0 payoff.
     */
    public MPEnvironment(final int length, final int payoffMap)
    {
	conLength=length;
	double i;
	for(i=1.; i+Math.pow(2.,i)<=conLength; i++);//calculates the position bits in this problem
	this.posBits=(int)(i-1);

	if(this.posBits+Math.pow(2.,this.posBits) != conLength)
	    System.out.println("There are additonally "+(int)(conLength-(this.posBits+Math.pow(2.,this.posBits)))+" irrelevant Bits!");

	this.currentState = new char[conLength];

	if(payoffMap==0)
	    payoffLandscape=false;
	else
	    payoffLandscape=true;
	if(payoffLandscape)
		this.maxPayoff=(int)(200 + 200 * Math.pow(2.,this.posBits));
	else
		this.maxPayoff=1000;

	this.correct=false;
	this.reset=false;
    }
    /**
     * Returns true after the current problem was classified
     */
    @Override
	public boolean doReset()
    {
	return this.reset;
    }
    /**
     * Executes the action and determines the reward.
     * Distinguishes between the payoff landscape and the 0/1000 reward.
     *
     * @param action Specifies the classification.
     */
    @Override
	public double executeAction(final int action)
    {
	int place=this.posBits;
	for(int i=0; i<this.posBits; i++)
		if(this.currentState[i]=='1')
			place+=Math.pow(2., (this.posBits-1-i));
	int ret=0;
	if(action == Character.digit(this.currentState[place],10)){
	    this.correct=true;
	    if(payoffLandscape){
		ret = 300 + (place-this.posBits)*200 ;
		if(this.currentState[place]=='1')
		    ret+=100;
	    } else
			ret = this.maxPayoff;
	}else{
	    this.correct=false;
	    if(payoffLandscape){
		ret = (place-this.posBits)*200;
		if(this.currentState[place] == '1')
		    ret+=100;
	    } else
			ret = 0;
	}
	this.reset = true;
	return ret;
    }
    /**
     * Returns the problem length
     */
    @Override
	public int getConditionLength()
    {
	return conLength;
    }
    /**
     * Returns the current problem
     */
    @Override
	public String getCurrentState()
    {
	return new String(this.currentState);
    }
    /**
     * Returns the maximal payoff possible in the current multiplexer problem.
     * The maximal payoff is determined out of the payoff type. If the payoff type 1000/0 is selected
     * this function will return 1000, otherwise the maximal value depends on the problem size.
     */
    @Override
	public int getMaxPayoff()
    {
	return this.maxPayoff;
    }
    /**
     * Returns the number of possible actions.
     * in the Multiplexer problem there are two classifications possible
     */
    @Override
	public int getNrActions()
    {
	return this.nrActions;
    }
    /**
     * Returns false since the Multiplexer problem is a single step problem
     */
    @Override
	public boolean isMultiStepProblem()
    {
	return false;
    }
    /**
     * Generates a new random problem instance.
     */
    @Override
	public String resetState()
    {
	for(int i=0; i<conLength; i++)
		if(XCSConstants.drand()<0.5)
			this.currentState[i]='0';
		else
			this.currentState[i]='1';
	this.reset=false;
	return new String(this.currentState);
    }
    /**
     * Returns true if the last executed action was a correct classification
     */
    @Override
	public boolean wasCorrect()
    {
	return this.correct;
    }
}
