package dima.tools.concurrence;

/*
  File: SynchronizedInt.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  19Jun1998  dl               Create public version
 */

/**
 * A class useful for offloading synch for int instance variables.
 *
 * <p>[<a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html"> Introduction to this package. </a>]
 **/

public class SynchronizedInt extends SynchronizedVariable implements Comparable, Cloneable {

	protected int value_;

	/**
	 * Make a new SynchronizedInt with the given initial value,
	 * and using its own internal lock.
	 **/
	public SynchronizedInt(final int initialValue) {
		super();
		this.value_ = initialValue;
	}
	/**
	 * Make a new SynchronizedInt with the given initial value,
	 * and using the supplied lock.
	 **/
	public SynchronizedInt(final int initialValue, final Object lock) {
		super(lock);
		this.value_ = initialValue;
	}
	/**
	 * Add amount to value (i.e., set value += amount)
	 * @return the new value
	 **/
	public int add(final int amount) {
		synchronized (this.lock_) {
			return this.value_ += amount;
		}
	}
	/**
	 * Set value to value &amp; b.
	 * @return the new value
	 **/
	public  int and(final int b) {
		synchronized (this.lock_) {
			this.value_ = this.value_ & b;
			return this.value_;
		}
	}
	/**
	 * Set value to newValue only if it is currently assumedValue.
	 * @return true if successful
	 **/
	public boolean commit(final int assumedValue, final int newValue) {
		synchronized(this.lock_) {
			final boolean success = assumedValue == this.value_;
			if (success) this.value_ = newValue;
			return success;
		}
	}
	public int compareTo(final int other) {
		final int val = this.get();
		return val < other? -1 : val == other? 0 : 1;
	}
	public int compareTo(final SynchronizedInt other) {
		return this.compareTo(other.get());
	}
	@Override
	public int compareTo(final Object other) {
		return this.compareTo((SynchronizedInt)other);
	}
	/**
	 * Set the value to its complement
	 * @return the new value
	 **/
	public  int complement() {
		synchronized (this.lock_) {
			this.value_ = ~this.value_;
			return this.value_;
		}
	}
	/**
	 * Decrement the value.
	 * @return the new value
	 **/
	public int decrement() {
		synchronized (this.lock_) {
			return --this.value_;
		}
	}
	/**
	 * Divide value by factor (i.e., set value /= factor)
	 * @return the new value
	 **/
	public int divide(final int factor) {
		synchronized (this.lock_) {
			return this.value_ /= factor;
		}
	}
	@Override
	public boolean equals(final Object other) {
		if (other != null &&
				other instanceof SynchronizedInt)
			return this.get() == ((SynchronizedInt)other).get();
		else
			return false;
	}
	/**
	 * Return the current value
	 **/
	public final int get() { synchronized(this.lock_) { return this.value_; } }
	@Override
	public int hashCode() { return this.get(); }
	/**
	 * Increment the value.
	 * @return the new value
	 **/
	public int increment() {
		synchronized (this.lock_) {
			return ++this.value_;
		}
	}
	/**
	 * Multiply value by factor (i.e., set value *= factor)
	 * @return the new value
	 **/
	public synchronized int multiply(final int factor) {
		synchronized (this.lock_) {
			return this.value_ *= factor;
		}
	}
	/**
	 * Set the value to the negative of its old value
	 * @return the new value
	 **/
	public  int negate() {
		synchronized (this.lock_) {
			this.value_ = -this.value_;
			return this.value_;
		}
	}
	/**
	 * Set value to value | b.
	 * @return the new value
	 **/
	public  int or(final int b) {
		synchronized (this.lock_) {
			this.value_ = this.value_ | b;
			return this.value_;
		}
	}
	/**
	 * Set to newValue.
	 * @return the old value
	 **/

	public int set(final int newValue) {
		synchronized (this.lock_) {
			final int old = this.value_;
			this.value_ = newValue;
			return old;
		}
	}
	/**
	 * Subtract amount from value (i.e., set value -= amount)
	 * @return the new value
	 **/
	public int subtract(final int amount) {
		synchronized (this.lock_) {
			return this.value_ -= amount;
		}
	}
	/**
	 * Atomically swap values with another SynchronizedInt.
	 * Uses identityHashCode to avoid deadlock when
	 * two SynchronizedInts attempt to simultaneously swap with each other.
	 * (Note: Ordering via identyHashCode is not strictly guaranteed
	 * by the language specification to return unique, orderable
	 * values, but in practice JVMs rely on them being unique.)
	 * @return the new value
	 **/

	public int swap(final SynchronizedInt other) {
		if (other == this) return this.get();
		SynchronizedInt fst = this;
		SynchronizedInt snd = other;
		if (System.identityHashCode(fst) > System.identityHashCode(snd)) {
			fst = other;
			snd = this;
		}
		synchronized(fst.lock_) {
			synchronized(snd.lock_) {
				fst.set(snd.set(fst.get()));
				return this.get();
			}
		}
	}
	@Override
	public String toString() { return String.valueOf(this.get()); }
	/**
	 * Set value to value ^ b.
	 * @return the new value
	 **/
	public  int xor(final int b) {
		synchronized (this.lock_) {
			this.value_ = this.value_ ^ b;
			return this.value_;
		}
	}
}
