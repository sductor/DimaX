//  Binary.java
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

import java.util.BitSet;

import dima.introspectionbasedagents.kernel.PseudoRandom;

import frameworks.faulttolerance.solver.jmetal.core.Variable;


/**
 * This class implements a generic binary string variable.It can be used as
 * a base class other binary string based classes (e.g., binary coded integer
 * or real variables).
 */
public class Binary extends Variable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3977638641616131231L;

	/**
	 * Stores the bits constituting the binary string. It is
	 * implemented using a BitSet object
	 */
	public BitSet bits_;
	private final PseudoRandom seed;

	/**
	 * Store the length of the binary string
	 */
	protected int numberOfBits_;

	/**
	 * Default constructor.
	 */
	public Binary(PseudoRandom seed) {
		this.seed=seed;
	} //Binary

	/**
	 *  Constructor
	 *  @param numberOfBits Length of the bit string
	 */
	public Binary(final int numberOfBits,PseudoRandom seed){
		this.numberOfBits_ = numberOfBits;
		this.seed=seed;

		this.bits_ = new BitSet(this.numberOfBits_);
		for (int i = 0; i < this.numberOfBits_; i++){
			if (seed.randDouble() < 0.5) {
				this.bits_.set(i,true);
			} else {
				this.bits_.set(i,false);
			}
		}
	} //Binary

	/**
	 * Copy constructor.
	 * @param variable The Binary variable to copy.
	 */
	public Binary(final Binary variable,PseudoRandom seed){
		this.numberOfBits_ = variable.numberOfBits_;
		this.seed=seed;

		this.bits_ = new BitSet(this.numberOfBits_);
		for (int i = 0; i < this.numberOfBits_; i++) {
			this.bits_.set(i,variable.bits_.get(i));
		}
	} //Binary

	/**
	 * This method is intended to be used in subclass of <code>Binary</code>,
	 * for examples the classes, <code>BinaryReal</code> and <code>BinaryInt<codes>.
	 * In this classes, the method allows to decode the
	 * value enconded in the binary string. As generic variables do not encode any
	 * value, this method do noting
	 */
	public void decode() {
		//do nothing
	} //decode

	/**
	 * Creates an exact copy of a Binary object
	 * @return An exact copy of the object.
	 **/
	@Override
	public Variable deepCopy() {
		return new Binary(this,seed);
	} //deepCopy

	/**
	 * Returns the length of the binary string.
	 * @return The length
	 */
	public int getNumberOfBits(){
		return this.numberOfBits_;
	} //getNumberOfBits

	/**
	 * Returns the value of the ith bit.
	 * @param bit The bit to retrieve
	 * @return The ith bit
	 */
	public boolean getIth(final int bit){
		return this.bits_.get(bit);
	} //getNumberOfBits

	/**
	 * Sets the value of the ith bit.
	 * @param bit The bit to set
	 */
	public void setIth(final int bit, final boolean value){
		this.bits_.set(bit, value) ;
	} //getNumberOfBits


	/**
	 * Obtain the hamming distance between two binary strings
	 * @param other The binary string to compare
	 * @return The hamming distance
	 */
	public int hammingDistance(final Binary other) {
		int distance = 0;
		int i = 0;
		while (i < this.bits_.size()) {
			if (this.bits_.get(i) != other.bits_.get(i)) {
				distance++;
			}
			i++;
		}
		return distance;
	} // hammingDistance

	@Override
	public boolean equals(final Object o){
		if (!(o instanceof Binary)){
			return false;
		}  else {
			return ((Binary)o).bits_.equals(this.bits_);
		}
	}

	/**
	 * 
	 */
	@Override
	public String toString() {
		String result ;

		result = "" ;
		for (int i = 0; i < this.numberOfBits_; i ++) {
			if (this.bits_.get(i)) {
				result = result + "1" ;
			} else {
				result = result + "0" ;
			}
		}

		return result ;
	} // toString
} // Binary
