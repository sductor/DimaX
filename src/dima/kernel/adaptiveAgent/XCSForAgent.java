package dima.kernel.adaptiveAgent;

import java.io.RandomAccessFile;

import dima.tools.XCS.Environment;
import dima.tools.XCS.PredictionArray;
import dima.tools.XCS.XCSConstants;
import dima.tools.XCS.XClassifierSet;




/**
 * Insert the type's description here.
 * Creation date: (29/04/2003 06:23:31)
 * @author:
 */
public class XCSForAgent extends dima.tools.XCS.XCS {
	/**
	 *
	 */
	private static final long serialVersionUID = 3584854795209315057L;
	/**
	 * @param e
	 * @param outFileString
	 */

	XClassifierSet prevActionSet=null;
	double prevReward=0., prevPrediction=0.;
	int steps=1;
	String prevState=null;
	protected int explore;

	/**
	 * Insert the method's description here.
	 * Creation date: (29/04/2003 06:25:49)
	 * @param env aMoveco.Firm.XCS.XCSBasedFirmXCSA
	 */
	public XCSForAgent(final XCSAgentEnvironment en) {
		super(en,"");
		this.env=en;

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (29/04/2003 06:25:49)
	 * @param env aMoveco.Firm.XCS.XCSBasedFirmXCSA
	 */
	public XCSForAgent(final XCSAgentEnvironment en, final int explo) {
		super(en,"");
		this.env=en;
		this.explore = explo;

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/09/03 10:28:58)
	 * @param en aMoveco.Firm.XCS.XCSFirmEnvironment
	 * @param pop Gdima.tools.XCS.XClassifierSet
	 */
	public XCSForAgent(final XCSAgentEnvironment en, final RandomAccessFile po) {
		super(en,"");
		this.env=en;}


	public XCSForAgent(final XCSAgentEnvironment en,final String outFileString, final int explo) {
		super(en, outFileString);
		this.env=en;
		this.explore = explo;


	}
	/************************** Multi-step Experiments *******************************/

	/**
	 * Executes one multi step experiment and monitors the performance.
	 *
	 * @see #doOneMultiStepProblemExplore
	 * @see #doOneMultiStepProblemExploit
	 * @see #writePerformance
	 */
	void doOneMultiStepExperiment(int explore)
	{
		int  exploreStepCounter=0;
		final int[] stepsToFood = new int[50];

		final double[] sysError = new double[50];
		if (explore == 3)
		{

			// determins randomly the value of explore to execute an excplore or exploit step
			// explore = (explore+1)%2;
			final java.util.Random hasard=new java.util.Random();
			final int exp=hasard.nextInt(10);
			if (exp>5) explore =0;
			else explore = 1;}


		final int exploreTrialC=1;

		final String state = this.env.resetState();
		if(explore==1)
			exploreStepCounter=this.doOneMultiStepProblemExplore(state, exploreStepCounter);
		else
			this.doOneMultiStepProblemExploit(state, stepsToFood, sysError,exploreTrialC, exploreStepCounter);

	}
	/**
	 * Executes one performance evaluation trial a multi-step problem.
	 * Similar to Wilson's exploitation the function updates the parameters of the classifiers
	 * but it does not execute the genetic algorithm.
	 * A trial normally ends when the food is reached or the teletransportation
	 * threshold is reached.
	 *
	 * @see XCSConstants#teletransportation
	 * @see XClassifierSet#XClassifierSet(String,XClassifierSet,int,int)
	 * @see PredictionArray#PredictionArray
	 * @see PredictionArray#bestActionWinner
	 * @see XClassifierSet#XClassifierSet(XClassifierSet,int)
	 * @see Environment#executeAction
	 * @see XClassifierSet#confirmClassifiersInSet
	 * @see XClassifierSet#updateSet
	 * @param state The reseted perception of the problem.
	 * @param stepsToGoal The last fifty numbers of steps to the goal during exploitation.
	 * @param sysError The averaged prediction errors of the last fifty trials.
	 * @param trialCounter The number of exploration trials executed so far.
	 * @param stepCounter The number of exploration steps executed so far.
	 */
	private void doOneMultiStepProblemExploit(final String state, final int[] stepsToGoal, final double[] sysError, final int trialCounter, final int stepCounter)
	{
		//FirmXClassifierSet prevActionSet=null;
		//XClassifierSet prevActionSet=null;
		//double prevReward=0., prevPrediction=0.;
		//int steps;

		// sysError[trialCounter%50]=0.
		// the step of the XCS. The matching set is then determined, the prediction array is defined and the best action
		//(having the greater prediction value is the choosen; One action set is the determined and the reward is calculated
		// by the applicaton of the choosen action or strategy

		final XClassifierSet matchSet = new XClassifierSet(state, this.pop, stepCounter, this.env.getNrActions());

		final PredictionArray predictionArray = new PredictionArray(matchSet, this.env.getNrActions());

		final int actionWinner = predictionArray.bestActionWinner();

		final XClassifierSet actionSet = new XClassifierSet(matchSet, actionWinner);

		final double reward = this.env.executeAction( actionWinner );


		if(this.prevActionSet!=null){
			this.prevActionSet.confirmClassifiersInSet();
			this.prevActionSet.updateSet(predictionArray.getBestValue(), this.prevReward);
			sysError[trialCounter%50]+=Math.abs(dima.tools.XCS.XCSConstants.gamma * predictionArray.getValue(actionWinner) + this.prevReward
					- this.prevPrediction) / this.env.getMaxPayoff();
		}


		if(this.env.doReset()){
			actionSet.confirmClassifiersInSet();
			actionSet.updateSet(0., reward);
			sysError[trialCounter%50]+=Math.abs(reward - predictionArray.getValue(actionWinner))
					/ this.env.getMaxPayoff();

		}


		this.prevActionSet=actionSet;
		this.prevPrediction=predictionArray.getValue(actionWinner);
		this.prevReward=reward;



	}
	/**
	 * Executes one learning trial in a multi-step problem.
	 * A trial normally ends when the food is reached or the teletransportation
	 * threshold is reached.
	 *
	 * @see XCSConstants#teletransportation
	 * @see XClassifierSet#XClassifierSet(String,XClassifierSet,int,int)
	 * @see PredictionArray#PredictionArray
	 * @see PredictionArray#randomActionWinner
	 * @see XClassifierSet#XClassifierSet(XClassifierSet,int)
	 * @see Environment#executeAction
	 * @see XClassifierSet#confirmClassifiersInSet
	 * @see XClassifierSet#updateSet
	 * @see XClassifierSet#runGA
	 * @param state The reseted perception of the problem.
	 * @param stepCounter The number of exploration steps executed so far.
	 * @return The updated number of exploration setps.
	 */
	private int doOneMultiStepProblemExplore(final String state, final int stepCounter)
	{

		this.steps =0;

		final XClassifierSet matchSet = new XClassifierSet(state, this.pop, stepCounter+this.steps, this.env.getNrActions());

		final PredictionArray predictionArray = new PredictionArray(matchSet, this.env.getNrActions());

		final int actionWinner = predictionArray.randomActionWinner();

		final XClassifierSet actionSet = new XClassifierSet(matchSet, actionWinner);

		final double reward = this.env.executeAction( actionWinner );

		if(this.prevActionSet!=null){
			this.prevActionSet.confirmClassifiersInSet();
			this.prevActionSet.updateSet(predictionArray.getBestValue(), this.prevReward);
			this.prevActionSet.runGA(stepCounter+this.steps, this.prevState, this.env.getNrActions());
		}

		if(this.env.doReset()){
			actionSet.confirmClassifiersInSet();
			actionSet.updateSet(0., reward);
			actionSet.runGA(stepCounter+this.steps, state, this.env.getNrActions());

		}
		this.prevActionSet=actionSet;
		this.prevReward=reward;
		this.prevState=state;


		return stepCounter+this.steps;
	}
	public void initXCSAgent() {


		this.pop=new XClassifierSet(this.env.getNrActions());

	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/05/03 13:43:29)
	 * @param newEnv aMoveco.Firm.XCS.XCSBasedFirmXCSA
	 */
	public void setEnv(final Environment newEnv) {
		this.env = newEnv;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/05/03 15:19:03)
	 * @param newPop aMoveco.Firm.XCS.FirmXClassifierSet
	 */
	public void setPop(final XClassifierSet newPop) {
		this.pop = newPop;
	}
}
