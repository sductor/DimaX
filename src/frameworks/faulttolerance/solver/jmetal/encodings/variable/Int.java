//  Int.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
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

package frameworks.faulttolerance.solver.jmetal.encodings.variable;

import dima.introspectionbasedagents.kernel.PseudoRandom;
import frameworks.faulttolerance.solver.jmetal.core.Variable;
import frameworks.faulttolerance.solver.jmetal.util.Configuration;
import frameworks.faulttolerance.solver.jmetal.util.JMException;

/**
 * This class implements an integer decision variable
 */
public class Int extends Variable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5302560813889653652L;
	private int value_;       //Stores the value of the variable
	private int lowerBound_;  //Stores the lower limit of the variable
	private int upperBound_;  //Stores the upper limit of the variable

	/**
	 * Constructor
	 */
	public Int() {
		this.lowerBound_ = java.lang.Integer.MIN_VALUE ;
		this.upperBound_ = java.lang.Integer.MAX_VALUE ;
		this.value_      = 0                           ;
	} // Int

	/**
	 * Constructor
	 * @param lowerBound Variable lower bound
	 * @param upperBound Variable upper bound
	 */
	public Int(final int lowerBound, final int upperBound,PseudoRandom seed){
		this.lowerBound_ = lowerBound;
		this.upperBound_ = upperBound;
		this.value_ = seed.randInt(lowerBound, upperBound) ;
	} // Int

	/**
	 * Constructor
	 * @param value Value of the variable
	 * @param lowerBound Variable lower bound
	 * @param upperBound Variable upper bound
	 */
	public Int(final int value, final int lowerBound, final int upperBound) {
		super();

		this.value_      = value      ;
		this.lowerBound_ = lowerBound ;
		this.upperBound_ = upperBound ;
	} // Int

	/**
	 * Copy constructor.
	 * @param variable Variable to be copied.
	 * @throws JMException
	 */
	public Int(final Variable variable) throws JMException{
		this.lowerBound_ = (int)variable.getLowerBound();
		this.upperBound_ = (int)variable.getUpperBound();
		this.value_ = (int)variable.getValue();
	} // Int

	/**
	 * Returns the value of the variable.
	 * @return the value.
	 */
	@Override
	public double getValue() {
		return this.value_;
	} // getValue

	/**
	 * Assigns a value to the variable.
	 * @param value The value.
	 */
	@Override
	public void setValue(final double value) {
		this.value_ = (int)value;
	} // setValue

	/**
	 * Creates an exact copy of the <code>Int</code> object.
	 * @return the copy.
	 */
	@Override
	public Variable deepCopy(){
		try {
			return new Int(this);
		} catch (final JMException e) {
			Configuration.logger_.severe("Int.deepCopy.execute: JMException");
			return null ;
		}
	} // deepCopy

	/**
	 * Returns the lower bound of the variable.
	 * @return the lower bound.
	 */
	@Override
	public double getLowerBound() {
		return this.lowerBound_;
	} // getLowerBound

	/**
	 * Returns the upper bound of the variable.
	 * @return the upper bound.
	 */
	@Override
	public double getUpperBound() {
		return this.upperBound_;
	} // getUpperBound

	/**
	 * Sets the lower bound of the variable.
	 * @param lowerBound The lower bound value.
	 */
	@Override
	public void setLowerBound(final double lowerBound)  {
		this.lowerBound_ = (int)lowerBound;
	} // setLowerBound

	/**
	 * Sets the upper bound of the variable.
	 * @param upperBound The new upper bound value.
	 */
	@Override
	public void setUpperBound(final double upperBound) {
		this.upperBound_ = (int)upperBound;
	} // setUpperBound

	/**
	 * Returns a string representing the object
	 * @return The string
	 */
	@Override
	public String toString(){
		return this.value_+"";
	} // toString
} // Int
