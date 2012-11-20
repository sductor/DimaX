//  Solution.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Description:
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package frameworks.faulttolerance.solver.jmetal.core;

import java.io.Serializable;
import java.util.Arrays;

import frameworks.faulttolerance.solver.jmetal.encodings.variable.Binary;



/**
 * Class representing a solution for a problem.
 */
public class Solution implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1226207866091909864L;

	/**
	 * Stores the problem
	 */
	Problem problem_ ;

	/**
	 * Stores the type of the variable
	 */
	private SolutionType type_ ;

	/**
	 * Stores the decision variables of the solution.
	 */
	public Variable[] variable_ ;

	/**
	 * Stores the objectives values of the solution.
	 */
	private final double [] objective_ ;

	/**
	 * Stores the number of objective values of the solution
	 */
	private int numberOfObjectives_ ;

	/**
	 * Stores the so called fitness value. Used in some metaheuristics
	 */
	private double fitness_ ;

	/**
	 * Used in algorithm AbYSS, this field is intended to be used to know
	 * when a <code>Solution</code> is marked.
	 */
	private boolean marked_ ;

	/**
	 * Stores the so called rank of the solution. Used in NSGA-II
	 */
	private int rank_ ;

	/**
	 * Stores the overall constraint violation of the solution.
	 */
	private double  overallConstraintViolation_ ;

	/**
	 * Stores the number of constraints violated by the solution.
	 */
	private int  numberOfViolatedConstraints_ ;

	/**
	 * This field is intended to be used to know the location of
	 * a solution into a <code>SolutionSet</code>. Used in MOCell
	 */
	private int location_ ;

	/**
	 * Stores the distance to his k-nearest neighbor into a
	 * <code>SolutionSet</code>. Used in SPEA2.
	 */
	private double kDistance_ ;

	/**
	 * Stores the crowding distance of the the solution in a
	 * <code>SolutionSet</code>. Used in NSGA-II.
	 */
	private double crowdingDistance_ ;

	/**
	 * Stores the distance between this solution and a <code>SolutionSet</code>.
	 * Used in AbySS.
	 */
	private double distanceToSolutionSet_ ;

	/**
	 * Constructor.
	 */
	public Solution() {
		this.problem_                      = null  ;
		this.marked_                       = false ;
		this.overallConstraintViolation_   = 0.0   ;
		this.numberOfViolatedConstraints_  = 0     ;
		this.type_                         = null ;
		this.variable_                     = null ;
		this.objective_                    = null ;
	} // Solution

	/**
	 * Constructor
	 * @param numberOfObjectives Number of objectives of the solution
	 * 
	 * This constructor is used mainly to read objective values from a file to
	 * variables of a SolutionSet to apply quality indicators
	 */
	public Solution(final int numberOfObjectives) {
		this.numberOfObjectives_ = numberOfObjectives;
		this.objective_          = new double[numberOfObjectives];
	}

	/**
	 * Constructor.
	 * @param problem The problem to solve
	 * @throws ClassNotFoundException
	 */
	public Solution(final Problem problem) throws ClassNotFoundException{
		this.problem_ = problem ;
		this.type_ = problem.getSolutionType() ;
		this.numberOfObjectives_ = problem.getNumberOfObjectives() ;
		this.objective_          = new double[this.numberOfObjectives_] ;

		// Setting initial values
		this.fitness_              = 0.0 ;
		this.kDistance_            = 0.0 ;
		this.crowdingDistance_     = 0.0 ;
		this.distanceToSolutionSet_ = Double.POSITIVE_INFINITY ;
		//<-

		//variable_ = problem.solutionType_.createVariables() ;
		this.variable_ = this.type_.createVariables() ;
	} // Solution

	static public Solution getNewSolution(final Problem problem) throws ClassNotFoundException {
		return new Solution(problem) ;
	}

	/**
	 * Constructor
	 * @param problem The problem to solve
	 */
	public Solution(final Problem problem, final Variable [] variables){
		this.problem_ = problem ;
		this.type_ = problem.getSolutionType() ;
		this.numberOfObjectives_ = problem.getNumberOfObjectives() ;
		this.objective_          = new double[this.numberOfObjectives_] ;

		// Setting initial values
		this.fitness_              = 0.0 ;
		this.kDistance_            = 0.0 ;
		this.crowdingDistance_     = 0.0 ;
		this.distanceToSolutionSet_ = Double.POSITIVE_INFINITY ;
		//<-

		this.variable_ = variables ;
	} // Constructor

	/**
	 * Copy constructor.
	 * @param solution Solution to copy.
	 */
	public Solution(final Solution solution) {
		this.problem_ = solution.problem_ ;
		this.type_ = solution.type_;

		this.numberOfObjectives_ = solution.numberOfObjectives();
		this.objective_ = new double[this.numberOfObjectives_];
		for (int i = 0; i < this.objective_.length;i++) {
			this.objective_[i] = solution.getObjective(i);
		} // for
		//<-

		this.variable_ = this.type_.copyVariables(solution.variable_) ;
		this.overallConstraintViolation_  = solution.getOverallConstraintViolation();
		this.numberOfViolatedConstraints_ = solution.getNumberOfViolatedConstraint();
		this.distanceToSolutionSet_ = solution.getDistanceToSolutionSet();
		this.crowdingDistance_     = solution.getCrowdingDistance();
		this.kDistance_            = solution.getKDistance();
		this.fitness_              = solution.getFitness();
		this.marked_               = solution.isMarked();
		this.rank_                 = solution.getRank();
		this.location_             = solution.getLocation();
	} // Solution

	/**
	 * Sets the distance between this solution and a <code>SolutionSet</code>.
	 * The value is stored in <code>distanceToSolutionSet_</code>.
	 * @param distance The distance to a solutionSet.
	 */
	public void setDistanceToSolutionSet(final double distance){
		this.distanceToSolutionSet_ = distance;
	} // SetDistanceToSolutionSet

	/**
	 * Gets the distance from the solution to a <code>SolutionSet</code>.
	 * <b> REQUIRE </b>: this method has to be invoked after calling
	 * <code>setDistanceToPopulation</code>.
	 * @return the distance to a specific solutionSet.
	 */
	public double getDistanceToSolutionSet(){
		return this.distanceToSolutionSet_;
	} // getDistanceToSolutionSet


	/**
	 * Sets the distance between the solution and its k-nearest neighbor in
	 * a <code>SolutionSet</code>. The value is stored in <code>kDistance_</code>.
	 * @param distance The distance to the k-nearest neighbor.
	 */
	public void setKDistance(final double distance){
		this.kDistance_ = distance;
	} // setKDistance

	/**
	 * Gets the distance from the solution to his k-nearest nighbor in a
	 * <code>SolutionSet</code>. Returns the value stored in
	 * <code>kDistance_</code>. <b> REQUIRE </b>: this method has to be invoked
	 * after calling <code>setKDistance</code>.
	 * @return the distance to k-nearest neighbor.
	 */
	public double getKDistance(){
		return this.kDistance_;
	} // getKDistance

	/**
	 * Sets the crowding distance of a solution in a <code>SolutionSet</code>.
	 * The value is stored in <code>crowdingDistance_</code>.
	 * @param distance The crowding distance of the solution.
	 */
	public void setCrowdingDistance(final double distance){
		this.crowdingDistance_ = distance;
	} // setCrowdingDistance


	/**
	 * Gets the crowding distance of the solution into a <code>SolutionSet</code>.
	 * Returns the value stored in <code>crowdingDistance_</code>.
	 * <b> REQUIRE </b>: this method has to be invoked after calling
	 * <code>setCrowdingDistance</code>.
	 * @return the distance crowding distance of the solution.
	 */
	public double getCrowdingDistance(){
		return this.crowdingDistance_;
	} // getCrowdingDistance

	/**
	 * Sets the fitness of a solution.
	 * The value is stored in <code>fitness_</code>.
	 * @param fitness The fitness of the solution.
	 */
	public void setFitness(final double fitness) {
		this.fitness_ = fitness;
	} // setFitness

	/**
	 * Gets the fitness of the solution.
	 * Returns the value of stored in the variable <code>fitness_</code>.
	 * <b> REQUIRE </b>: This method has to be invoked after calling
	 * <code>setFitness()</code>.
	 * @return the fitness.
	 */
	public double getFitness() {
		return this.fitness_;
	} // getFitness

	/**
	 * Sets the value of the i-th objective.
	 * @param i The number identifying the objective.
	 * @param value The value to be stored.
	 */
	public void setObjective(final int i, final double value) {
		this.objective_[i] = value;
	} // setObjective

	/**
	 * Returns the value of the i-th objective.
	 * @param i The value of the objective.
	 */
	public double getObjective(final int i) {
		return this.objective_[i];
	} // getObjective

	/**
	 * Returns the number of objectives.
	 * @return The number of objectives.
	 */
	public int numberOfObjectives() {
		if (this.objective_ == null) {
			return 0 ;
		} else {
			return this.numberOfObjectives_;
		}
	} // numberOfObjectives

	/**
	 * Returns the number of decision variables of the solution.
	 * @return The number of decision variables.
	 */
	public int numberOfVariables() {
		return this.problem_.getNumberOfVariables() ;
	} // numberOfVariables

	/**
	 * Returns a string representing the solution.
	 * @return The string.
	 */
	@Override
	public String toString() {
		String aux="{";
		aux+=Arrays.asList(this.variable_)+", value :";
		for (int i = 0; i < this.numberOfObjectives_; i++) {
			aux = aux + this.getObjective(i) + " ";
		}
		aux+="}";
		return aux;
	} // toString

	/**
	 * Returns the decision variables of the solution.
	 * @return the <code>DecisionVariables</code> object representing the decision
	 * variables of the solution.
	 */
	public Variable[] getDecisionVariables() {
		return this.variable_ ;
	} // getDecisionVariables

	/**
	 * Sets the decision variables for the solution.
	 * @param decisionVariables The <code>DecisionVariables</code> object
	 * representing the decision variables of the solution.
	 */
	public void setDecisionVariables(final Variable [] variables) {
		this.variable_ = variables ;
	} // setDecisionVariables

	/**
	 * Indicates if the solution is marked.
	 * @return true if the method <code>marked</code> has been called and, after
	 * that, the method <code>unmarked</code> hasn't been called. False in other
	 * case.
	 */
	public boolean isMarked() {
		return this.marked_;
	} // isMarked

	/**
	 * Establishes the solution as marked.
	 */
	public void marked() {
		this.marked_ = true;
	} // marked

	/**
	 * Established the solution as unmarked.
	 */
	public void unMarked() {
		this.marked_ = false;
	} // unMarked

	/**
	 * Sets the rank of a solution.
	 * @param value The rank of the solution.
	 */
	public void setRank(final int value){
		this.rank_ = value;
	} // setRank

	/**
	 * Gets the rank of the solution.
	 * <b> REQUIRE </b>: This method has to be invoked after calling
	 * <code>setRank()</code>.
	 * @return the rank of the solution.
	 */
	public int getRank(){
		return this.rank_;
	} // getRank

	/**
	 * Sets the overall constraints violated by the solution.
	 * @param value The overall constraints violated by the solution.
	 */
	public void setOverallConstraintViolation(final double value) {
		this.overallConstraintViolation_ = value;
	} // setOverallConstraintViolation

	/**
	 * Gets the overall constraint violated by the solution.
	 * <b> REQUIRE </b>: This method has to be invoked after calling
	 * <code>overallConstraintViolation</code>.
	 * @return the overall constraint violation by the solution.
	 */
	public double getOverallConstraintViolation() {
		return this.overallConstraintViolation_;
	}  //getOverallConstraintViolation


	/**
	 * Sets the number of constraints violated by the solution.
	 * @param value The number of constraints violated by the solution.
	 */
	public void setNumberOfViolatedConstraint(final int value) {
		this.numberOfViolatedConstraints_ = value;
	} //setNumberOfViolatedConstraint

	/**
	 * Gets the number of constraint violated by the solution.
	 * <b> REQUIRE </b>: This method has to be invoked after calling
	 * <code>setNumberOfViolatedConstraint</code>.
	 * @return the number of constraints violated by the solution.
	 */
	public int getNumberOfViolatedConstraint() {
		return this.numberOfViolatedConstraints_;
	} // getNumberOfViolatedConstraint

	/**
	 * Sets the location of the solution into a solutionSet.
	 * @param location The location of the solution.
	 */
	public void setLocation(final int location) {
		this.location_ = location;
	} // setLocation

	/**
	 * Gets the location of this solution in a <code>SolutionSet</code>.
	 * <b> REQUIRE </b>: This method has to be invoked after calling
	 * <code>setLocation</code>.
	 * @return the location of the solution into a solutionSet
	 */
	public int getLocation() {
		return this.location_;
	} // getLocation

	/**
	 * Sets the type of the variable.
	 * @param type The type of the variable.
	 */
	//public void setType(String type) {
	// type_ = Class.forName("") ;
	//} // setType

	/**
	 * Sets the type of the variable.
	 * @param type The type of the variable.
	 */
	public void setType(final SolutionType type) {
		this.type_ = type ;
	} // setType

	/**
	 * Gets the type of the variable
	 * @return the type of the variable
	 */
	public SolutionType getType() {
		return this.type_;
	} // getType

	/**
	 * Returns the aggregative value of the solution
	 * @return The aggregative value.
	 */
	public double getAggregativeValue() {
		double value = 0.0;
		for (int i = 0; i < this.numberOfObjectives(); i++){
			value += this.getObjective(i);
		}
		return value;
	} // getAggregativeValue

	/**
	 * Returns the number of bits of the chromosome in case of using a binary
	 * representation
	 * @return The number of bits if the case of binary variables, 0 otherwise
	 */
	public int getNumberOfBits() {
		int bits = 0 ;

		for (final Variable element : this.variable_) {
			try {
				if (element.getVariableType() == Class.forName("jmetal.base.variable.Binary") ||
						element.getVariableType() == Class.forName("jmetal.base.variable.BinaryReal")) {
					bits += ((Binary)element).getNumberOfBits() ;
				}
			} catch (final ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return bits ;
	} // getNumberOfBits

	@Override
	public boolean equals(final Object o){
		//		System.out.println(o+"  equals  "+this+"?");
		if (!(o instanceof Solution)) {
			return false;
		} else {
			final Solution that = (Solution) o;
			for (int i = 0; i < this.variable_.length; i++){
				assert this.type_.equals(that.type_):this.type_+" "+that.type_;
				if (!this.variable_[i].equals(that.variable_[i])) {
					return false;
				}
			}
			assert this.getObjective(0)==that.getObjective(0):this+" "+that;
			//			System.out.println("oui!!!!");
			return true;
		}
	}

	@Override
	public int hashCode(){
		int code=0;
		for (int i = 0; i < this.variable_.length; i++){
			code+=Math.pow(2,i)*this.variable_[i].hashCode();
		}
		return code;
	}
} // Solution
