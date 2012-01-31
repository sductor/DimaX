package dima.tools.XCS;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Vector;

/**
 * This class handles the different sets of classifiers. It stores each set in an array. The array is initialized to
 * a sufficient large size so that no changes in the size of the array will be necessary.
 * The class provides constructors for constructing
 * <ul>
 * <li> the empty population,
 * <li> the match set, and
 * <li> the action set.
 * </ul>
 * It executes a GA in a set and updates classifier parameters of a set.
 * Moreover, it provides all necessary different sums and averages of parameters in the set.
 * Finally, it handles addition, deletion and subsumption of classifiers.
 *
 * @author    Martin V. Butz
 * @version   XCSJava 1.0
 * @since     JDK1.1
 */
public class XClassifierSet implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = -1702171692199090249L;

	/**
	 * The cons parameter is necessary for all kinds of calculations in the set. Note that it is static, so that
	 * it is not reconstructed each time a new set is created.
	 */
	private static XCSConstants cons=new XCSConstants();//private

	/**
	 * The Sum of the numerosity in one set is always kept up to date!
	 */
	private int numerositySum;// le 9 10 03

	/**
	 * Each set keeps a reference to the parent set out of which it was generated. In the population itself
	 * this pointer is set to zero.
	 */
	private final XClassifierSet parentSet; // chang�e le 9 10 03

	/**
	 * The classifier list (in form of an array)
	 */
	private final XClassifier clSet[];//9 10 03

	/**
	 * The actual number of macro-classifiers in the list (which is in fact equal to the number of entries in the array).
	 */
	private int cllSize; // Lilia e 9/10/03

	public int addedClassNumber;
	public int removedClassifiersNumber;
	/**
	 * Creates a new, empty population initializing the population array to the maximal population size
	 * plus the number of possible actions.
	 *
	 * @see XCSConstants#maxPopSize
	 * @param numberOfActions The number of actions possible in the problem.
	 */
	public XClassifierSet(final int numberOfActions)
	{
		this.numerositySum=0;
		this.cllSize=0;
		this.parentSet=null;
		this.clSet=new XClassifier[XCSConstants.maxPopSize+numberOfActions];
	}
	/**
	 * Constructs an action set out of the given match set.
	 *
	 * @param matchSet The current match set
	 * @param action The chosen action for the action set.
	 */
	public XClassifierSet(final XClassifierSet matchSet, final int action)
	{
		this.parentSet=matchSet;
		this.numerositySum=0;
		this.cllSize=0;
		this.clSet=new XClassifier[matchSet.cllSize];

		for(int i=0; i<matchSet.cllSize; i++)
			if( matchSet.clSet[i].getAction() == action)
				this.addClassifier(matchSet.clSet[i]);
	}
	/**
	 * Constructs a match set out of the population. After the creation, it is checked if the match set covers all possible actions
	 * in the environment. If one or more actions are not present, covering occurs, generating the missing action(s). If maximal
	 * population size is reached when covering, deletion occurs.
	 *
	 * @see XClassifier#XClassifier(double,int,String,int)
	 * @see XCSConstants#maxPopSize
	 * @see #deleteFromPopulation
	 * @param state The current situation/problem instance.
	 * @paramn pop The current population of classifiers.
	 * @param time  The actual number of instances the XCS learned from so far.
	 * @param numberOfActions The number of actions possible in the environment.
	 */
	public XClassifierSet(final String state, final XClassifierSet pop, final int time, final int numberOfActions)
	{
		this.parentSet=pop;
		this.numerositySum=0;
		this.cllSize=0;
		this.clSet=new XClassifier[pop.cllSize+numberOfActions];

		final boolean[] actionCovered =  new boolean[numberOfActions];
		for(int i=0; i<actionCovered.length; i++)
			actionCovered[i]=false;

		for(int i=0; i<pop.cllSize; i++){
			final XClassifier cl=pop.clSet[i];
			if( cl.match(state)){
				this.addClassifier(cl);
				actionCovered[cl.getAction()]=true;
			}
		}

		//Check if each action is covered. If not -> generate covering XClassifier and delete if the population is too big
		boolean again;
		do{
			again=false;
			for(int i=0; i<actionCovered.length; i++)
				if(!actionCovered[i]){
					final XClassifier newCl=new XClassifier(this.numerositySum+1, time, state, i);

					this.addClassifier(newCl);
					pop.addClassifier(newCl);
					pop.addedClassNumber++;//rajout� pour tenir compte de la compilation

				}
			while(pop.numerositySum > XCSConstants.maxPopSize){
				final XClassifier cdel=pop.deleteFromPopulation();
				// update the current match set in case a classifier was deleted out of that
				// and redo the loop if now another action is not covered in the match set anymore.
				int pos=0;
				if(cdel!=null && (pos=this.containsClassifier(cdel))!=-1) {
					this.numerositySum--;
					if(cdel.getNumerosity()==0){
						this.removeClassifier(pos);
						if( !this.isActionCovered(cdel.getAction())){
							again=true;
							actionCovered[cdel.getAction()]=false;
						}
					}
				}
			}
		}while(again);
	}
	/**
	 * Adds a classifier to the set and increases the numerositySum value accordingly.
	 *
	 * @param classifier The to be added classifier.
	 */
	private void addClassifier(final XClassifier classifier)     {
		this.clSet[this.cllSize]=classifier;
		this.addValues(classifier);
		this.cllSize++;
	}
	/**
	 * Increases the numerositySum value with the numerosity of the classifier.
	 */
	protected void addValues(final XClassifier cl)//
	{
		this.numerositySum+=cl.getNumerosity();
	}
	/**
	 * Adds the classifier to the population and checks if an identical classifier exists.
	 * If an identical classifier exists, its numerosity is increased.
	 *
	 * @see #getIdenticalClassifier
	 * @param cl The to be added classifier.
	 */
	public void addXClassifierToPopulation(final XClassifier cl) // chang�e en public
	{
		// set pop to the actual population
		XClassifierSet pop=this;
		while(pop.parentSet!=null)
			pop=pop.parentSet;

		XClassifier oldcl=null;
		if((oldcl=pop.getIdenticalClassifier(cl))!=null){
			oldcl.addNumerosity(1);//addedClassNumber++;// rajout�
			this.increaseNumerositySum(1);
		}else{
			pop.addClassifier(cl);
			pop.addedClassNumber++; // rajout� pour tenir compte de la convergence de la population

		}
	}
	/**
	 * Updates the numerositySum of the set and deletes all classifiers with numerosity 0.
	 */
	public void confirmClassifiersInSet()
	{
		int copyStep=0;
		this.numerositySum=0;
		int i;
		for(i=0; i<this.cllSize-copyStep; i++)
			if(this.clSet[i+copyStep].getNumerosity()==0){
				copyStep++;
				i--;
			}else{
				if(copyStep>0)
					this.clSet[i]=this.clSet[i+copyStep];
				this.numerositySum+=this.clSet[i].getNumerosity();
			}
		for( ; i<this.cllSize; i++)
			this.clSet[i]=null;
		this.cllSize -= copyStep;
	}
	/**
	 * Returns the position of the classifier in the set if it is present and -1 otherwise.
	 */
	private int containsClassifier(final XClassifier cl)
	{
		for(int i=0; i<this.cllSize; i++)
			if(this.clSet[i]==cl)
				return i;
		return -1;
	}
	/**
	 * Deletes one classifier in the population.
	 * The classifier that will be deleted is chosen by roulette wheel selection
	 * considering the deletion vote. Returns the macro-classifier which got decreased by one micro-classifier.
	 *
	 * @see XClassifier#getDelProp
	 */
	private XClassifier deleteFromPopulation()
	{
		final double meanFitness= this.getFitnessSum()/this.numerositySum;
		double sum=0.;
		for(int i=0; i<this.cllSize; i++)
			sum += this.clSet[i].getDelProp(meanFitness);

		final double choicePoint=sum*XCSConstants.drand();
		sum=0.;
		for(int i=0; i<this.cllSize; i++){
			sum += this.clSet[i].getDelProp(meanFitness);
			if(sum > choicePoint){
				final XClassifier cl=this.clSet[i];
				cl.addNumerosity(-1);
				this.numerositySum--;
				if(cl.getNumerosity()==0){
					this.removeClassifier(i); this.removedClassifiersNumber++;

				}
				return cl;
			}
		}
		return null;
	}
	/**
	 * Executes action set subsumption.
	 * The action set subsumption looks for the most general subsumer classifier in the action set
	 * and subsumes all classifiers that are more specific than the selected one.
	 *
	 * @see XClassifier#isSubsumer
	 * @see XClassifier#isMoreGeneral
	 */
	private void doActionSetSubsumption()
	{
		XClassifierSet pop=this;
		while(pop.parentSet!=null)
			pop=pop.parentSet;

		XClassifier subsumer=null;
		for(int i=0; i<this.cllSize; i++)
			if(this.clSet[i].isSubsumer())
				if(subsumer==null || this.clSet[i].isMoreGeneral(subsumer))
					subsumer=this.clSet[i];

		//If a subsumer was found, subsume all more specific classifiers in the action set
		if(subsumer!=null)
			for(int i=0; i<this.cllSize; i++)
				if(subsumer.isMoreGeneral(this.clSet[i])){
					final int num=this.clSet[i].getNumerosity();
					subsumer.addNumerosity(num);
					this.clSet[i].addNumerosity(-1*num);
					pop.removeClassifier(this.clSet[i]);
					pop.removedClassifiersNumber++;// ajout� pour rtenir compte d ela convergence de la firme
					this.removeClassifier(i);
					i--;
				}
	}
	/**
	 * Returns the classifier at the specified position.
	 */
	public XClassifier elementAt(final int i)
	{
		return this.clSet[i];
	}
	/**
	 * Returns the sum of the fitnesses of all classifiers in the set.
	 */
	private double getFitnessSum()
	{
		double sum=0.;

		for(int i=0; i<this.cllSize; i++)
			sum+=this.clSet[i].getFitness();
		return sum;
	}
	/**
	 * Looks for an identical classifier in the population.
	 *
	 * @param newCl The new classifier.
	 * @return Returns the identical classifier if found, null otherwise.
	 */
	private XClassifier getIdenticalClassifier(final XClassifier newCl)
	{
		for(int i=0; i<this.cllSize; i++)
			if(newCl.equals(this.clSet[i]))
				return this.clSet[i];
		return null;
	}
	/**
	 * Returns the number of micro-classifiers in the set.
	 */
	public int getNumerositySum()
	{
		return this.numerositySum;
	}
	/**
	 * Returns the sum of the prediction values of all classifiers in the set.
	 */
	private double getPredictionSum()
	{
		double sum=0.;

		for(int i=0; i<this.cllSize; i++)
			sum+=this.clSet[i].getPrediction() * this.clSet[i].getNumerosity();
		return sum;
	}
	/**
	 * Returns the number of macro-classifiers in the set.
	 */
	public int getSize()
	{
		return this.cllSize;
	}
	/**
	 * Returns the average of the time stamps in the set.
	 */
	private double getTimeStampAverage()
	{
		return this.getTimeStampSum()/this.numerositySum;
	}
	/**
	 * Returns the sum of the time stamps of all classifiers in the set.
	 */
	private double getTimeStampSum()
	{
		double sum=0.;

		for(int i=0; i<this.cllSize; i++)
			sum+=this.clSet[i].getTimeStamp() * this.clSet[i].getNumerosity();
		return sum;
	}
	/**
	 * Increases recursively all numerositySum values in the set and all parent sets.
	 * This function should be called when the numerosity of a classifier in some set is increased in
	 * order to keep the numerosity sums of all sets and essentially the population up to date.
	 */
	private void increaseNumerositySum(final int nr)//private
	{
		this.numerositySum+=nr;
		if(this.parentSet!=null)
			this.parentSet.increaseNumerositySum(nr);
	}
	/**
	 * Inserts both discovered classifiers keeping the maximal size of the population and possibly doing GA subsumption.
	 *
	 * @see XCSConstants#doGASubsumption
	 * @see #subsumeXClassifier
	 * @see #addXClassifierToPopulation
	 * @see XCSConstants#maxPopSize
	 * @see #deleteFromPopulation
	 * @param cl1 The first classifier generated by the GA.
	 * @param cl2 The second classifier generated by the GA.
	 * @param cl1P The first parent of the two new classifiers.
	 * @param cl2P The second classifier of the two new classifiers.
	 */
	private void insertDiscoveredXClassifiers(final XClassifier cl1, final XClassifier cl2, final XClassifier cl1P, final XClassifier cl2P)
	{
		XClassifierSet pop=this;
		while(pop.parentSet!=null)
			pop=pop.parentSet;

		if(XCSConstants.doGASubsumption){
			this.subsumeXClassifier(cl1, cl1P, cl2P);
			this.subsumeXClassifier(cl2, cl1P, cl2P);
		}else{
			pop.addXClassifierToPopulation(cl1);
			pop.addXClassifierToPopulation(cl2);
		}

		while(pop.numerositySum > XCSConstants.maxPopSize)
			pop.deleteFromPopulation();
	}
	/**
	 * Returns if the specified action is covered in this set.
	 */
	private boolean isActionCovered(final int action)
	{
		for(int i=0; i<this.cllSize; i++)
			if( this.clSet[i].getAction() == action)
				return true;
		return false;
	}
	/**
	 * Prints the classifier set to the control panel.
	 */
	public void printSet()
	{
		System.out.println("Averages:");
		System.out.println("Pre: "+this.getPredictionSum()/this.numerositySum+ " Fit: "+this.getFitnessSum()/this.numerositySum + " Tss: "+ this.getTimeStampSum()/this.numerositySum + " Num: " + this.numerositySum);
		for(int i=0; i<this.cllSize; i++)
			this.clSet[i].printXClassifier();
	}
	/**
	 * Prints the classifier set to the specified print writer (which usually refers to a file).
	 *
	 * @param pW The print writer that normally refers to a file writer.
	 */
	public void printSet(final PrintWriter pW)
	{
		pW.println("Averages:");
		pW.println("Pre: "+this.getPredictionSum()/this.numerositySum+ " Fit: "+this.getFitnessSum()/this.numerositySum + " Tss: "+ this.getTimeStampSum()/this.numerositySum + " Num: " + this.numerositySum);
		for(int i=0; i<this.cllSize; i++)
			this.clSet[i].printXClassifier(pW);
	}
	/**
	 * Removes the (possible macro-) classifier at the specified array position from the population.
	 * The function returns true when the classifier was found and removed and false
	 * otherwise. It does not update the numerosity of the set, neither
	 * recursively remove classifiers in the parent set. This must be done manually where required.
	 */
	private boolean removeClassifier(final int pos) // private
	{
		int i;
		for(i=pos ; i<this.cllSize-1; i++)
			this.clSet[i]=this.clSet[i+1];
		this.clSet[i]=null;
		this.cllSize--;

		return true;
	}
	/**
	 * Removes the specified (possible macro-) classifier from the population.
	 * The function returns true when the classifier was found and removed and false
	 * otherwise. It does not update the numerosity sum of the set, neither
	 * recursively remove classifiers in the parent set. This must be done manually where required.
	 */
	private boolean removeClassifier(final XClassifier classifier)// private
	{
		int i;
		for(i=0; i<this.cllSize; i++)
			if(this.clSet[i]==classifier)
				break;
		if(i==this.cllSize)
			return false;
		for( ; i<this.cllSize-1; i++)
			this.clSet[i]=this.clSet[i+1];
		this.clSet[i]=null;

		this.cllSize--;

		return true;
	}
	/**
	 * The Genetic Discovery in XCS takes place here. If a GA takes place, two classifiers are selected
	 * by roulette wheel selection, possibly crossed and mutated and then inserted.
	 *
	 * @see XCSConstants#theta_GA
	 * @see #selectXClassifierRW
	 * @see XClassifier#twoPointCrossover
	 * @see XClassifier#applyMutation
	 * @see XCSConstants#predictionErrorReduction
	 * @see XCSConstants#fitnessReduction
	 * @see #insertDiscoveredXClassifiers
	 * @param time  The actual number of instances the XCS learned from so far.
	 * @param state  The current situation/problem instance.
	 * @param numberOfActions The number of actions possible in the environment.
	 */
	public void runGA(final int time, final String state, final int numberOfActions)
	{
		// Don't do a GA if the theta_GA threshold is not reached, yet
		if( this.cllSize==0 || time-this.getTimeStampAverage() < XCSConstants.theta_GA )
			return;

		this.setTimeStamps(time);

		final double fitSum=this.getFitnessSum();
		// Select two XClassifiers with roulette Wheel Selection
		final XClassifier cl1P=this.selectXClassifierRW(fitSum);
		final XClassifier cl2P=this.selectXClassifierRW(fitSum);

		final XClassifier cl1=new XClassifier(cl1P);
		final XClassifier cl2=new XClassifier(cl2P);

		cl1.twoPointCrossover(cl2);

		cl1.applyMutation(state, numberOfActions);
		cl2.applyMutation(state, numberOfActions);

		cl1.setPrediction((cl1.getPrediction() + cl2.getPrediction())/2.);
		cl1.setPredictionError(XCSConstants.predictionErrorReduction * (cl1.getPredictionError() + cl2.getPredictionError())/2.);
		cl1.setFitness(XCSConstants.fitnessReduction * (cl1.getFitness() + cl2.getFitness())/2.);
		cl2.setPrediction(cl1.getPrediction());
		cl2.setPredictionError(cl1.getPredictionError());
		cl2.setFitness(cl1.getFitness());

		this.insertDiscoveredXClassifiers(cl1, cl2, cl1P, cl2P);
	}
	/**
	 * Selects one classifier using roulette wheel selection according to the fitnesses of the classifiers.
	 */
	private XClassifier selectXClassifierRW(final double fitSum)
	{
		final double choiceP=XCSConstants.drand()*fitSum;
		int i=0;
		double sum=this.clSet[i].getFitness();
		while(choiceP>sum){
			i++;
			sum+=this.clSet[i].getFitness();
		}

		return this.clSet[i];
	}
	/**
	 * Sets the time stamp of all classifiers in the set to the current time. The current time
	 * is the number of exploration steps executed so far.
	 *
	 * @param time The actual number of instances the XCS learned from so far.
	 */
	private void setTimeStamps(final int time)
	{
		for(int i=0; i<this.cllSize; i++)
			this.clSet[i].setTimeStamp(time);
	}
	/**
	 * Tries to subsume a classifier in the current set.
	 * This method is normally called in an action set.
	 * If no subsumption is possible the classifier is simply added to the population considering
	 * the possibility that there exists an identical classifier.
	 *
	 * @param cl The classifier that may be subsumed.
	 * @see #addXClassifierToPopulation
	 */
	private void subsumeXClassifier(final XClassifier cl)//private
	{
		//Open up a new Vector in order to chose the subsumer candidates randomly
		final Vector choices= new Vector();
		for(int i=0; i<this.cllSize; i++)
			if( this.clSet[i].subsumes(cl) )
				choices.addElement(this.clSet[i]);

		if(choices.size()>0){
			final int choice=(int)(XCSConstants.drand()*choices.size());
			((XClassifier)choices.elementAt(choice)).addNumerosity(1);
			this.increaseNumerositySum(1);
			return;
		}
		//If no subsumer was found, add the classifier to the population
		this.addXClassifierToPopulation(cl);
	}
	/**
	 * Tries to subsume a classifier in the parents.
	 * If no subsumption is possible it tries to subsume it in the current set.
	 *
	 * @see #subsumeXClassifier(XClassifier)
	 */
	private void subsumeXClassifier(final XClassifier cl, final XClassifier cl1P, final XClassifier cl2P)
	{
		if(cl1P!=null && cl1P.subsumes(cl)){
			this.increaseNumerositySum(1);
			cl1P.addNumerosity(1);
		}else if(cl2P!=null && cl2P.subsumes(cl)){
			this.increaseNumerositySum(1);
			cl2P.addNumerosity(1);
		} else
			this.subsumeXClassifier(cl); //calls second subsumeXClassifier fkt!
	}
	/**
	 * Special function for updating the fitnesses of the classifiers in the set.
	 *
	 * @see XClassifier#updateFitness
	 */
	private void updateFitnessSet()
	{
		double accuracySum=0.;
		final double []accuracies = new double[this.cllSize];

		//First, calculate the accuracies of the classifier and the accuracy sums
		for(int i=0; i<this.cllSize; i++){
			accuracies[i]= this.clSet[i].getAccuracy();
			accuracySum+=accuracies[i]*this.clSet[i].getNumerosity();
		}

		//Next, update the fitnesses accordingly
		for(int i=0; i<this.cllSize; i++)
			this.clSet[i].updateFitness(accuracySum, accuracies[i]);
	}
	/**
	 * Updates all parameters in the current set (should be the action set).
	 * Essentially, reinforcement Learning as well as the fitness evaluation takes place in this set.
	 * Moreover, the prediction error and the action set size estimate is updated. Also,
	 * action set subsumption takes place if selected. As in the algorithmic description, the fitness is updated
	 * after prediction and prediction error. However, in order to be more conservative the prediction error is
	 * updated before the prediction.
	 *
	 * @see XCSConstants#gamma
	 * @see XClassifier#increaseExperience
	 * @see XClassifier#updatePreError
	 * @see XClassifier#updatePrediction
	 * @see XClassifier#updateActionSetSize
	 * @see #updateFitnessSet
	 * @see XCSConstants#doActionSetSubsumption
	 * @see #doActionSetSubsumption
	 * @param maxPrediction The maximum prediction value in the successive prediction array
	 * (should be set to zero in single step environments).
	 * @param reward The actual resulting reward after the execution of an action.
	 */
	public void updateSet(final double maxPrediction, final double reward)
	{

		final double P=reward + XCSConstants.gamma*maxPrediction;

		for(int i=0; i<this.cllSize; i++){
			this.clSet[i].increaseExperience();
			this.clSet[i].updatePreError(P);
			this.clSet[i].updatePrediction(P);
			this.clSet[i].updateActionSetSize(this.numerositySum);
		}
		this.updateFitnessSet();

		if(XCSConstants.doActionSetSubsumption)
			this.doActionSetSubsumption();
	}
}
