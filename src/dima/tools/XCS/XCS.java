package dima.tools.XCS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Date;

/**
 * This class is the XCS itself. It stores the population and the posed problem.
 * The class provides methods for the main learning cycles in XCS distinguishing between
 * single-step and multi-step problems as well as exploration vs. exploitation trials.
 * Moreover, it handles the performance evaluation.
 *
 * @author    Martin V. Butz
 * @version   XCSJava 1.0
 * @since     JDK1.1
 */
public class XCS implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 4828519261412759644L;

	/**
	 * Stores the posed problem.
	 */
	protected Environment env; //environnement a �t� chang�

	/**
	 * Stores the current population of XCS.
	 */
	protected XClassifierSet pop; // pop a �t� chang�e

	/**
	 * Stores the specified output File, where the performance will be written.
	 */
	private final File outFile;

	/**
	 * Specifies the number of exploration problems/trials to solve in one experiment.
	 */
	protected int maxProblems=20000; //chang�

	/**
	 * Specifies the number of investigated experiments.
	 */
	private int nrExps=10;


	/**
	 * Stores the relevant constants in XCS.
	 *
	 * @see XCSConstants
	 */
	private static XCSConstants cons;//private

	/**
	 * Constructs the XCS system.
	 */
	public XCS(final Environment e, final String outFileString)
	{
		this.env=e;

		//specify output file
		this.outFile=new File(outFileString);

		//initialize XCS
		this.pop=null;
		XCS.cons = new XCSConstants();
	}
	/************************** Multi-step Experiments *******************************/

	/**
	 * Executes one multi step experiment and monitors the performance.
	 *
	 * @see #doOneMultiStepProblemExplore
	 * @see #doOneMultiStepProblemExploit
	 * @see #writePerformance
	 */
	void doOneMultiStepExperiment(final PrintWriter pW)
	{
		int explore=0, exploreStepCounter=0;
		final int[] stepsToFood = new int[50];
		final double[] sysError = new double[50];

		for (int exploreTrialC=0; exploreTrialC < this.maxProblems; exploreTrialC+=explore){
			explore = (explore+1)%2;

			final String state = this.env.resetState();
			if(explore==1)
				exploreStepCounter=this.doOneMultiStepProblemExplore(state, exploreStepCounter);
			else
				this.doOneMultiStepProblemExploit(state, stepsToFood, sysError, exploreTrialC, exploreStepCounter);
			if(exploreTrialC%50==0 && explore==0 && exploreTrialC>0)
				this.writePerformance(pW, stepsToFood, sysError, exploreTrialC);
		}
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
	private void doOneMultiStepProblemExploit(String state, final int[] stepsToGoal, final double[] sysError, final int trialCounter, final int stepCounter)
	{
		XClassifierSet prevActionSet=null;
		double prevReward=0., prevPrediction=0.;
		int steps;

		sysError[trialCounter%50]=0.;
		for( steps=0; steps<XCSConstants.teletransportation; steps++){
			final XClassifierSet matchSet = new XClassifierSet(state, this.pop, stepCounter, this.env.getNrActions());

			final PredictionArray predictionArray = new PredictionArray(matchSet, this.env.getNrActions());

			final int actionWinner = predictionArray.bestActionWinner();

			final XClassifierSet actionSet = new XClassifierSet(matchSet, actionWinner);

			final double reward = this.env.executeAction( actionWinner );

			if(prevActionSet!=null){
				prevActionSet.confirmClassifiersInSet();
				prevActionSet.updateSet(predictionArray.getBestValue(), prevReward);
				sysError[trialCounter%50]+=Math.abs(XCSConstants.gamma * predictionArray.getValue(actionWinner) + prevReward
						- prevPrediction) / this.env.getMaxPayoff();
			}

			if(this.env.doReset()){
				actionSet.confirmClassifiersInSet();
				actionSet.updateSet(0., reward);
				sysError[trialCounter%50]+=Math.abs(reward - predictionArray.getValue(actionWinner))
						/ this.env.getMaxPayoff();
				steps++;
				break;
			}
			prevActionSet=actionSet;
			prevPrediction=predictionArray.getValue(actionWinner);
			prevReward=reward;
			state=this.env.getCurrentState();
		}
		sysError[trialCounter%50]/=steps;
		stepsToGoal[trialCounter%50]=steps;
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
	private int doOneMultiStepProblemExplore(String state, final int stepCounter)
	{
		XClassifierSet prevActionSet=null;
		double prevReward=0.;
		int steps;
		String prevState=null;

		for(steps=0; steps<XCSConstants.teletransportation; steps++){
			final XClassifierSet matchSet = new XClassifierSet(state, this.pop, stepCounter+steps, this.env.getNrActions());

			final PredictionArray predictionArray = new PredictionArray(matchSet, this.env.getNrActions());

			final int actionWinner = predictionArray.randomActionWinner();

			final XClassifierSet actionSet = new XClassifierSet(matchSet, actionWinner);

			final double reward = this.env.executeAction( actionWinner );

			if(prevActionSet!=null){
				prevActionSet.confirmClassifiersInSet();
				prevActionSet.updateSet(predictionArray.getBestValue(), prevReward);
				prevActionSet.runGA(stepCounter+steps, prevState, this.env.getNrActions());
			}

			if(this.env.doReset()){
				actionSet.confirmClassifiersInSet();
				actionSet.updateSet(0., reward);
				actionSet.runGA(stepCounter+steps, state, this.env.getNrActions());
				break;
			}
			prevActionSet=actionSet;
			prevReward=reward;
			prevState=state;
			state=this.env.getCurrentState();
		}
		return stepCounter+steps;
	}
	/**************************** Single Step Experiments ***************************/

	/**
	 * Executes one single-step experiment monitoring the performance.
	 *
	 * @see #doOneSingleStepProblemExplore
	 * @see #doOneSingleStepProblemExploit
	 * @see #writePerformance
	 */
	void doOneSingleStepExperiment(final PrintWriter pW)
	{
		int explore=0;
		final int[] correct = new int[50];
		final double[] sysError = new double[50];

		for (int exploreProbC=0; exploreProbC < this.maxProblems; exploreProbC+=explore){
			explore = (explore+1)%2;

			final String state = this.env.resetState();

			if(explore==1)
				this.doOneSingleStepProblemExplore(state, exploreProbC);
			else
				this.doOneSingleStepProblemExploit(state, exploreProbC, correct, sysError);
			if(exploreProbC%50==0 && explore==0 && exploreProbC>0)
				this.writePerformance(pW, correct, sysError, exploreProbC);
		}
	}
	/**
	 * Executes one main performance evaluation loop for a single step problem.
	 *
	 * @see XClassifierSet#XClassifierSet(String,XClassifierSet,int,int)
	 * @see PredictionArray#PredictionArray
	 * @see PredictionArray#bestActionWinner
	 * @see Environment#executeAction
	 * @param state The actual problem instance.
	 * @param counter The number of problems observed so far in exploration.
	 * @param correct The array stores the last fifty correct/wrong exploitation classifications.
	 * @param sysError The array stores the last fifty predicted-received reward differences.
	 */
	private void doOneSingleStepProblemExploit(final String state, final int counter, final int[] correct, final double[] sysError)
	{
		final XClassifierSet matchSet = new XClassifierSet(state, this.pop, counter, this.env.getNrActions());

		final PredictionArray predictionArray = new PredictionArray (matchSet, this.env.getNrActions());

		final int actionWinner = predictionArray.bestActionWinner();

		final double reward = this.env.executeAction( actionWinner );

		if(this.env.wasCorrect())
			correct[counter%50]=1;
		else
			correct[counter%50]=0;

		sysError[counter%50] = Math.abs(reward - predictionArray.getBestValue());
	}
	/**
	 * Executes one main learning loop for a single step problem.
	 *
	 * @see XClassifierSet#XClassifierSet(String,XClassifierSet,int,int)
	 * @see PredictionArray#PredictionArray
	 * @see PredictionArray#randomActionWinner
	 * @see XClassifierSet#XClassifierSet(XClassifierSet,int)
	 * @see Environment#executeAction
	 * @see XClassifierSet#updateSet
	 * @see XClassifierSet#runGA
	 * @param state The actual problem instance.
	 * @param counter The number of problems observed so far in exploration.
	 */
	private void doOneSingleStepProblemExplore(final String state, final int counter)
	{
		final XClassifierSet matchSet = new XClassifierSet(state, this.pop, counter, this.env.getNrActions());

		final PredictionArray predictionArray = new PredictionArray(matchSet, this.env.getNrActions());

		final int actionWinner = predictionArray.randomActionWinner();

		final XClassifierSet actionSet = new XClassifierSet(matchSet, actionWinner);

		final double reward = this.env.executeAction( actionWinner );

		actionSet.updateSet(0., reward);

		actionSet.runGA(counter, state, this.env.getNrActions());
	}
	public static void main(final String args[])
	{
		final String envFileString=null;
		Environment e = null;

		XCSConstants.setSeed(1+ new Date().getTime() % 10000);

		if(args.length >= 4 && (args[0].equals("maze") || args[0].equals("mp"))){
			if(args[0].equals("maze")){
				System.out.println("Construct maze environment with maze coded in "+args[2]+" coding each feature with "+args[3]+" bits");
				e=new MazeEnvironment(args[2],new Integer(args[3]).intValue());
			}else{
				System.out.println("Construct Multiplexer problem of length "+args[2]+" and payoff type "+args[3]);
				e=new MPEnvironment(new Integer(args[2]).intValue(), new Integer(args[3]).intValue());
			}
		}else{
			// In case of a wrong number of arguments print out the usage.
			System.out.println("Usage: java XCS problemType(maze,mp) outputFile {problemLength(1-), mazeEnvironment} {payoffLandscape(0,1), codingLength(2,3)} [MaxNumberOfTrials] [NumberOfExperiments]");
			return;
		}

		final XCS xcs=new XCS(e, args[1]);
		if(args.length > 4)
			xcs.setNumberOfTrials(new Integer(args[4]).intValue());
		if(args.length > 5)
			xcs.setNumberOfExperiments(new Integer(args[5]).intValue());
		xcs.runXCS();
		return;
	}
	/**
	 * Runs the posed problem with XCS.
	 * The function essentially initializes the output File and then runs the experiments.
	 *
	 * @see #startExperiments
	 */
	public void runXCS()
	{
		FileWriter fW=null;
		BufferedWriter bW=null;
		PrintWriter pW = null;
		try{
			fW = new FileWriter(this.outFile);
			bW = new BufferedWriter(fW);
			pW = new PrintWriter(bW);
		}catch(final Exception e){System.out.println("Mistake in create file Writers"+e);}

		this.startExperiments(pW);

		try{
			pW.flush();
			bW.flush();
			fW.flush();
			fW.close();
		}catch(final Exception e){System.out.println("Mistake in closing the file writer!"+e);}
	}
	/**
	 * Resets the number of experiments. (The default is set to ten.)
	 */
	public void setNumberOfExperiments(final int exps)
	{
		this.nrExps=exps;
	}
	/**
	 * Resets the maximal number of trials in one experiment. (The default is set to 20000)
	 */
	public void setNumberOfTrials(final int trials)
	{
		this.maxProblems=trials;
	}
	/**
	 * This function runs the number of experiments specified. After the initialization of the empty
	 * population, either one single- or one multi-step problem is executed.
	 *
	 * @param pW The print writer refers to the output file for the performance evaluation. The experiments are separated by
	 * the text 'Next Experiment'.
	 * @see XClassifierSet#XClassifierSet(int)
	 * @see Environment#isMultiStepProblem
	 * @see #doOneSingleStepExperiment
	 * @see #doOneMultiStepExperiment
	 */
	private void startExperiments(final PrintWriter pW)
	{
		for(int expCounter=0; expCounter < this.nrExps; expCounter++){
			pW.println("Next Experiment");
			System.out.println("Experiment Nr."+(expCounter+1));

			//Initialize Population
			this.pop=new XClassifierSet(this.env.getNrActions());

			if(!this.env.isMultiStepProblem())
				this.doOneSingleStepExperiment(pW);
			else
				this.doOneMultiStepExperiment(pW);
			this.pop=null;
		}
	}
	/*##########---- Output ----##########*/

	/**
	 * Writes the performance of the XCS to the specified file.
	 * The function writes time performance systemError actualPopulationSizeInMacroClassifiers.
	 * Performance and system error are averaged over the last fifty trials.
	 *
	 * @param pW The reference where to write the performance.
	 * @param performance The performance in the last fifty exploration trials.
	 * @param sysError The system error in the last fifty exploration trials.
	 * @param exploreProbC The number of exploration trials executed so far.
	 */
	private void writePerformance(final PrintWriter pW, final int[] performance, final double[] sysError, final int exploreProbC)
	{
		double perf=0.;
		double serr=0.;
		for(int i=0; i<50; i++){
			perf+=performance[i];
			serr+=sysError[i];
		}
		perf/=50.;
		serr/=50.;
		pW.println("" + exploreProbC + " " + (float)perf + " " + (float)serr + " " + this.pop.getSize());
		System.out.println("" + exploreProbC + " " + (float)perf + " " + (float)serr + " " + this.pop.getSize());
	}
}
