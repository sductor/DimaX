package dima.tools.XCS;

import java.io.PrintWriter;
import java.io.Serializable;

/**
 * Each instance of this class represents one classifier. The class provides different constructors for generating
 * <ul>
 * <li> copies of existing classifiers,
 * <li> new matching classifiers with random action,
 * <li> new matching classifiers with specified action, and
 * <li> new completely random classifier.
 * </ul>
 * It handles classifier mutation and crossover and provides, sets, and updates parameters.
 * Moreover, it handles all types of comparisons between different classifiers.
 *
 * @author    Martin V. Butz
 * @version   XCSJava 1.0
 * @since     JDK1.1
 */
public class XClassifier implements Serializable
{

	/**
	 *
	 */
	private static final long serialVersionUID = -6783520811007257472L;

	/**
	 * The condition of this classifier.
	 */
	private String condition;

	/**
	 * The action of this classifier.
	 */
	private int action;


	/**
	 * The reward prediction value of this classifier.
	 */
	private double prediction;

	/**
	 * The reward prediction error of this classifier.
	 */
	private double predictionError;

	/**
	 * The fitness of the classifier in terms of the macro-classifier.
	 */
	private double fitness;


	/**
	 * The numerosity of the classifier. This is the number of micro-classifier this macro-classifier represents.
	 */
	private int numerosity;

	/**
	 * The experience of the classifier. This is the number of problems the classifier learned from so far.
	 */
	private int experience;

	/**
	 * The action set size estimate of the classifier.
	 */
	private double actionSetSize;

	/**
	 * The time the last GA application took place in this classifier.
	 */
	private int timeStamp;

	/**
	 * An instance of the learning parameters in XCSJava.
	 * Static assures that the Constants are not generated for each classifier separately.
	 */
	private static XCSConstants cons=new XCSConstants();




	/**
	 * Construct a classifier with random condition and random action.
	 *
	 * @param setSize The size of the current set which the new classifier matches.
	 * @param time  The actual number of instances the XCS learned from so far.
	 * @param condLength The length of the condition of the new classifier.
	 * @param numberOfActions The number of different actions to chose from
	 */
	public XClassifier(final double setSize, final int  time, final int condLength, final int numberOfActions)
	{
		this.createRandomCondition(condLength);
		this.createRandomAction(numberOfActions);
		this.classifierSetVariables(setSize, time);
	}
	/**
	 * Construct matching classifier with random action.
	 *
	 * @param setSize The size of the current set which the new classifier matches.
	 * @param time The actual number of instances the XCS learned from so far.
	 * @param numberOfActions The number of different actions to chose from
	 * (This should be set to the number of actions possible in the problem).
	 * @param situation The current problem instance/perception.
	 */
	public XClassifier(final double setSize, final int time, final int numberOfActions, final String situation)
	{
		this.createMatchingCondition(situation);
		this.createRandomAction(numberOfActions);
		this.classifierSetVariables(setSize, time);
	}
	/**
	 * Constructs a classifier with matching condition and specified action.
	 *
	 * @param setSize The size of the current set which the new classifier matches.
	 * @param time  The actual number of instances the XCS learned from so far.
	 * @param situation The current problem instance/perception.
	 * @param act The action of the new classifier.
	 */
	public XClassifier(final double setSize, final int time, final String situation, final int act)
	{
		this.createMatchingCondition(situation);
		this.action=act;
		this.classifierSetVariables(setSize, time);
	}
	/**
	 * Constructs an identical XClassifier.
	 * However, the experience of the copy is set to 0 and the numerosity is set to 1 since this is indeed
	 * a new individual in a population.
	 *
	 * @param clOld The to be copied classifier.
	 */
	public XClassifier(final XClassifier clOld)
	{
		this.condition=new String(clOld.condition);
		this.action=clOld.action;
		this.prediction=clOld.prediction;
		this.predictionError=clOld.predictionError;
		// Here we should divide the fitness by the numerosity to get a accurate value for the new one!
		this.fitness=clOld.fitness/clOld.numerosity;
		this.numerosity=1;
		this.experience=0;
		this.actionSetSize=clOld.actionSetSize;
		this.timeStamp=clOld.timeStamp;
	}
	/**
	 * Adds to the numerosity of the classifier.
	 *
	 * @param num The added numerosity (can be negative!).
	 */
	public void addNumerosity(final int num)
	{
		this.numerosity+=num;
	}
	/**
	 * Applies a niche mutation to the classifier.
	 * This method calls mutateCondition(state) and mutateAction(numberOfActions) and returns
	 * if at least one bit or the action was mutated.
	 *
	 * @param state The current situation/problem instance
	 * @param numberOfActions The maximal number of actions possible in the environment.
	 */
	public boolean applyMutation(final String state, final int numberOfActions)
	{
		boolean changed=this.mutateCondition(state);
		if(this.mutateAction(numberOfActions)) {
			changed=true;
		}
		return changed;
	}
	/**
	 * Sets the initial variables of a new classifier.
	 *
	 * @see XCSConstants#predictionIni
	 * @see XCSConstants#predictionErrorIni
	 * @see XCSConstants#fitnessIni
	 * @param setSize The size of the set the classifier is created in.
	 * @param time The actual number of instances the XCS learned from so far.
	 */
	public void classifierSetVariables(final double setSize, final int time) // chang�e en public
	{
		this.prediction=XCSConstants.predictionIni;
		this.predictionError=XCSConstants.predictionErrorIni;
		this.fitness=XCSConstants.fitnessIni;

		this.numerosity=1;
		this.experience=0;
		this.actionSetSize=setSize;
		this.timeStamp=time;
	}
	/**
	 * Creates a matching condition considering the constant <code>P_dontcare<\code>.
	 *
	 * @see XCSConstants#P_dontcare
	 */
	private void createMatchingCondition(final String cond)
	{
		final int condLength=cond.length();
		final char condArray[]= new char[condLength];

		for(int i=0; i<condLength; i++) {
			if(XCSConstants.drand()<XCSConstants.P_dontcare) {
				condArray[i]=XCSConstants.dontCare;
			} else {
				condArray[i]=cond.charAt(i);
			}
		}
		this.condition=new String(condArray);
	}
	/**
	 * Creates a random action.
	 *
	 * @param numberOfActions The number of actions to chose from.
	 */
	private void createRandomAction(final int numberOfActions)
	{
		this.action=(int)(XCSConstants.drand()*numberOfActions);
	}
	/**
	 * Creates a condition randomly considering the constant <code>P_dontcare<\code>.
	 *
	 * @see XCSConstants#P_dontcare
	 */
	private void createRandomCondition(final int condLength)
	{
		final char condArray[]=new char[condLength];
		for(int i=0; i<condLength; i++) {
			if(XCSConstants.drand()<XCSConstants.P_dontcare) {
				condArray[i]=XCSConstants.dontCare;
			} else
				if(XCSConstants.drand()<0.5) {
					condArray[i]='0';
				} else {
					condArray[i]='1';
				}
		}
		this.condition=new String(condArray);
	}
	/**
	 * Returns if the two classifiers are identical in condition and action.
	 *
	 * @param cl The classifier to be compared.
	 */
	public boolean equals(final XClassifier cl)
	{
		if(cl.condition.equals( this.condition )) {
			if(cl.action ==  this.action) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Returns the accuracy of the classifier.
	 * The accuracy is determined from the prediction error of the classifier using Wilson's
	 * power function as published in 'Get Real! XCS with continuous-valued inputs' (1999)
	 *
	 * @see XCSConstants#epsilon_0
	 * @see XCSConstants#alpha
	 * @see XCSConstants#nu
	 */
	public double getAccuracy()
	{
		double accuracy;

		if(this.predictionError <= XCSConstants.epsilon_0) {
			accuracy = 1.;
		} else {
			accuracy = XCSConstants.alpha * Math.pow( this.predictionError / XCSConstants.epsilon_0 , -XCSConstants.nu);
		}
		return accuracy;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/09/03 10:26:23)
	 * @return int
	 */
	public int getAction() {
		return this.action;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/03 10:30:16)
	 * @return double
	 */
	public double getActionSetSize() {
		return this.actionSetSize;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/09/03 10:24:42)
	 * @return java.lang.String
	 */
	public java.lang.String getCondition() {
		return this.condition;
	}
	/**
	 * Returns the vote for deletion of the classifier.
	 *
	 * @see XCSConstants#delta
	 * @see XCSConstants#theta_del
	 * @param meanFitness The mean fitness in the population.
	 */
	public double getDelProp(final double meanFitness)
	{
		if(this.fitness/this.numerosity >= XCSConstants.delta*meanFitness || this.experience < XCSConstants.theta_del) {
			return this.actionSetSize*this.numerosity;
		}
		return this.actionSetSize*this.numerosity*meanFitness / ( this.fitness/this.numerosity);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/03 10:29:37)
	 * @return int
	 */
	public int getExperience() {
		return this.experience;
	}
	/**
	 * Returns the fitness of the classifier.
	 */
	public double getFitness()
	{
		return this.fitness;
	}
	/**
	 * Returns the numerosity of the classifier.
	 */
	public int getNumerosity()
	{
		return this.numerosity;
	}
	/**
	 * Returns the prediction of the classifier.
	 */
	public double getPrediction()
	{
		return this.prediction;
	}
	/**
	 * Returns the prediction error of the classifier.
	 */
	public double getPredictionError()
	{
		return this.predictionError;
	}
	/**
	 * Returns the time stamp of the classifier.
	 */
	public int getTimeStamp()
	{
		return this.timeStamp;
	}
	/**
	 * Increases the Experience of the classifier by one.
	 */
	public void increaseExperience()
	{
		this.experience++;
	}
	/**
	 * Returns if the classifier is more general than cl. It is made sure that the classifier is indeed more general and
	 * not equally general as well as that the more specific classifier is completely included in the more general one
	 * (do not specify overlapping regions)
	 *
	 * @param The classifier that is tested to be more specific.
	 */
	public boolean isMoreGeneral(final XClassifier cl)
	{
		boolean ret=false;
		final int length=this.condition.length();
		for(int i=0; i<length; i++) {
			if(this.condition.charAt(i) != XCSConstants.dontCare && this.condition.charAt(i) != cl.condition.charAt(i)) {
				return false;
			} else if(this.condition.charAt(i) !=  cl.condition.charAt(i)) {
				ret = true;
			}
		}
		return ret;
	}
	/**
	 * Returns if the classifier is a possible subsumer. It is affirmed if the classifier
	 * has a sufficient experience and if its reward prediction error is sufficiently low.
	 *
	 * @see XCSConstants#theta_sub
	 * @see XCSConstants#epsilon_0
	 */
	public boolean isSubsumer()
	{
		if(this.experience>XCSConstants.theta_sub && this.predictionError < XCSConstants.epsilon_0) {
			return true;
		}
		return false;
	}
	/**
	 * Returns if the classifier matches in the current situation.
	 *
	 * @param state The current situation which can be the current state or problem instance.
	 */
	public boolean match(final String state)
	{
		if(this.condition.length()!=state.length()) {
			return false;
		}
		for(int i=0; i<this.condition.length(); i++) {
			if(this.condition.charAt(i)!=XCSConstants.dontCare && this.condition.charAt(i)!=state.charAt(i)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Mutates the action of the classifier.
	 *
	 * @see XCSConstants#pM
	 * @param numberOfActions The number of actions/classifications possible in the environment.
	 */
	private boolean mutateAction(final int numberOfActions)
	{
		boolean changed=false;

		if(XCSConstants.drand()<XCSConstants.pM){
			int act=0;
			do {
				act=(int)(XCSConstants.drand()*numberOfActions);
			} while(act==this.action);
			this.action=act;
			changed=true;
		}
		return changed;
	}
	/**
	 * Mutates the condition of the classifier. If one allele is mutated depends on the constant pM.
	 * This mutation is a niche mutation. It assures that the resulting classifier
	 * still matches the current situation.
	 *
	 * @see XCSConstants#pM
	 * @param state The current situation/problem instance.
	 */
	private boolean mutateCondition(final String state)
	{
		boolean changed=false;
		final int condLength=this.condition.length();

		for(int i=0; i<condLength; i++) {
			if(XCSConstants.drand()<XCSConstants.pM){
				final char[] cond=this.condition.toCharArray();
				final char[] stateC=state.toCharArray();
				changed=true;
				if(cond[i]==XCSConstants.dontCare) {
					cond[i]=stateC[i];
				} else {
					cond[i]=XCSConstants.dontCare;
				}
				this.condition=new String(cond);
			}
		}
		return changed;
	}
	/**
	 * Prints the classifier to the control panel.
	 * The method prints condition action prediction predictionError fitness numerosity experience actionSetSize timeStamp.
	 */
	public void printXClassifier()
	{
		System.out.println(this.condition+" "+this.action+"\t"+(float)this.prediction+"\t"+(float)this.predictionError+"\t"+(float)this.fitness+
				"\t"+this.numerosity+"\t"+this.experience+"\t"+(float)this.actionSetSize+"\t"+this.timeStamp);
	}
	/**
	 * Prints the classifier to the print writer (normally referencing a file).
	 * The method prints condition action prediction predictionError fitness numerosity experience actionSetSize timeStamp.
	 *
	 * @param pW The writer to which the classifier is written.
	 */
	public void printXClassifier(final PrintWriter pW)
	{
		pW.println(this.condition+"-"+this.action+" "+(float)this.prediction+" "+(float)this.predictionError+" " +(float)this.fitness+
				" "+this.numerosity+" "+this.experience+" "+(float)this.actionSetSize+" "+this.timeStamp);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/09/03 10:26:23)
	 * @param newAction int
	 */
	public void setAction(final int newAction) {
		this.action = newAction;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/03 10:30:16)
	 * @param newActionSetSize double
	 */
	public void setActionSetSize(final double newActionSetSize) {
		this.actionSetSize = newActionSetSize;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (04/09/03 10:24:42)
	 * @param newCondition java.lang.String
	 */
	public void setCondition(final java.lang.String newCondition) {
		this.condition = newCondition;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/09/03 10:29:37)
	 * @param newExperience int
	 */
	public void setExperience(final int newExperience) {
		this.experience = newExperience;
	}
	/**
	 * Sets the fitness of the classifier.
	 *
	 * @param fit The new fitness of the classifier.
	 */
	public void setFitness(final double fit)
	{
		this.fitness=fit;
	}
	/**
	 * Sets the prediction of the classifier.
	 *
	 * @param pre The new prediction of the classifier.
	 */
	public void setPrediction(final double pre)
	{
		this.prediction=pre;
	}
	/**
	 * Sets the prediction error of the classifier.
	 *
	 * @param preE The new prediction error of the classifier.
	 */
	public void setPredictionError(final double preE)
	{
		this.predictionError=preE;
	}
	/**
	 * Sets the time stamp of the classifier.
	 *
	 * @param ts The new time stamp of the classifier.
	 */
	public void setTimeStamp(final int ts)
	{
		this.timeStamp=ts;
	}
	/**
	 * Returns if the classifier subsumes cl.
	 *
	 * @param The new classifier that possibly is subsumed.
	 */
	public boolean subsumes(final XClassifier cl)
	{
		if(cl.action == this.action) {
			if(this.isSubsumer()) {
				if(this.isMoreGeneral(cl)) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Applies two point crossover and returns if the classifiers changed.
	 *
	 * @see XCSConstants#pX
	 * @param cl The second classifier for the crossover application.
	 */
	public boolean twoPointCrossover(final XClassifier cl)
	{
		boolean changed=false;
		if(XCSConstants.drand()<XCSConstants.pX){
			final int length=this.condition.length();
			int sep1=(int)(XCSConstants.drand()*length);
			int sep2=(int)(XCSConstants.drand()*length)+1;
			if(sep1>sep2){
				final int help=sep1;
				sep1=sep2;
				sep2=help;
			}else if(sep1==sep2) {
				sep2++;
			}
			final char[] cond1=this.condition.toCharArray();
			final char[] cond2=cl.condition.toCharArray();
			for(int i=sep1; i<sep2; i++) {
				if(cond1[i]!=cond2[i]){
					changed=true;
					final char help=cond1[i];
					cond1[i]=cond2[i];
					cond2[i]=help;
				}
			}
			if(changed){
				this.condition=new String(cond1);
				cl.condition=new String(cond2);
			}
		}
		return changed;
	}
	/**
	 * Updates the action set size.
	 *
	 * @see XCSConstants#beta
	 * @param numeriositySum The number of micro-classifiers in the population
	 */
	public double updateActionSetSize(final double numerositySum)
	{
		if(this.experience < 1./XCSConstants.beta) {
			this.actionSetSize= (this.actionSetSize * (this.experience-1)+ numerositySum) / this.experience;
		} else {
			this.actionSetSize+= XCSConstants.beta * (numerositySum - this.actionSetSize);
		}
		return this.actionSetSize*this.numerosity;
	}
	/**
	 * Updates the fitness of the classifier according to the relative accuracy.
	 *
	 * @see XCSConstants#beta
	 * @param accSum The sum of all the accuracies in the action set
	 * @param accuracy The accuracy of the classifier.
	 */
	public double updateFitness(final double accSum, final double accuracy)
	{
		this.fitness += XCSConstants.beta * (accuracy * this.numerosity / accSum - this.fitness);
		return this.fitness;//fitness already considers numerosity
	}
	/**
	 * Updates the prediction of the classifier according to P.
	 *
	 * @see XCSConstants#beta
	 * @param P The actual Q-payoff value (actual reward + max of predicted reward in the following situation).
	 */
	public double updatePrediction(final double P)
	{
		if( this.experience < 1./XCSConstants.beta) {
			this.prediction = (this.prediction * (this.experience - 1.) + P) / this.experience;
		} else {
			this.prediction += XCSConstants.beta * (P-this.prediction);
		}
		return this.prediction*this.numerosity;
	}
	/**
	 * Updates the prediction error of the classifier according to P.
	 *
	 * @see XCSConstants#beta
	 * @param P The actual Q-payoff value (actual reward + max of predicted reward in the following situation).
	 */
	public double updatePreError(final double P)
	{
		if( this.experience < 1./XCSConstants.beta) {
			this.predictionError = (this.predictionError*(this.experience - 1.) + Math.abs(P - this.prediction)) / this.experience;
		} else {
			this.predictionError += XCSConstants.beta * (Math.abs(P - this.prediction) - this.predictionError);
		}
		return this.predictionError*this.numerosity;
	}
}
