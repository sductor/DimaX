package dima.tools.concurrence;

/*
  File: SynchronizedLong.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  19Jun1998  dl               Create public version
 */

/**
 * A class useful for offloading synch for long instance variables.
 *
 * <p>[<a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html"> Introduction to this package. </a>]
 **/

public class SynchronizedLong extends SynchronizedVariable implements Comparable, Cloneable {

	protected long value_;

	/**
	 * Make a new SynchronizedLong with the given initial value,
	 * and using its own internal lock.
	 **/
	public SynchronizedLong(final long initialValue) {
		super();
		this.value_ = initialValue;
	}
	/**
	 * Make a new SynchronizedLong with the given initial value,
	 * and using the supplied lock.
	 **/
	public SynchronizedLong(final long initialValue, final Object lock) {
		super(lock);
		this.value_ = initialValue;
	}
	/**
	 * Add amount to value (i.e., set value += amount)
	 * @return the new value
	 **/
	public long add(final long amount) {
		synchronized (this.lock_) {
			return this.value_ += amount;
		}
	}
	/**
	 * Set value to value &amp; b.
	 * @return the new value
	 **/
	public  long and(final long b) {
		synchronized (this.lock_) {
			this.value_ = this.value_ & b;
			return this.value_;
		}
	}
	/**
	 * Set value to newValue only if it is currently assumedValue.
	 * @return true if successful
	 **/
	public boolean commit(final long assumedValue, final long newValue) {
		synchronized(this.lock_) {
			final boolean success = assumedValue == this.value_;
			if (success) this.value_ = newValue;
			return success;
		}
	}
	public int compareTo(final long other) {
		final long val = this.get();
		return val < other? -1 : val == other? 0 : 1;
	}
	public int compareTo(final SynchronizedLong other) {
		return this.compareTo(other.get());
	}
	@Override
	public int compareTo(final Object other) {
		return this.compareTo((SynchronizedLong)other);
	}
	/**
	 * Set the value to its complement
	 * @return the new value
	 **/
	public  long complement() {
		synchronized (this.lock_) {
			this.value_ = ~this.value_;
			return this.value_;
		}
	}
	/**
	 * Decrement the value.
	 * @return the new value
	 **/
	public long decrement() {
		synchronized (this.lock_) {
			return --this.value_;
		}
	}
	/**
	 * Divide value by factor (i.e., set value /= factor)
	 * @return the new value
	 **/
	public long divide(final long factor) {
		synchronized (this.lock_) {
			return this.value_ /= factor;
		}
	}
	@Override
	public boolean equals(final Object other) {
		if (other != null &&
				other instanceof SynchronizedLong)
			return this.get() == ((SynchronizedLong)other).get();
		else
			return false;
	}
	/**
	 * Return the current value
	 **/
	public final long get() { synchronized(this.lock_) { return this.value_; } }
	@Override
	public int hashCode() { // same expression as java.lang.Long
		final long v = this.get();
		return (int)(v ^ v >> 32);
	}
	/**
	 * Increment the value.
	 * @return the new value
	 **/
	public long increment() {
		synchronized (this.lock_) {
			return ++this.value_;
		}
	}
	/**
	 * Multiply value by factor (i.e., set value *= factor)
	 * @return the new value
	 **/
	public synchronized long multiply(final long factor) {
		synchronized (this.lock_) {
			return this.value_ *= factor;
		}
	}
	/**
	 * Set the value to the negative of its old value
	 * @return the new value
	 **/
	public  long negate() {
		synchronized (this.lock_) {
			this.value_ = -this.value_;
			return this.value_;
		}
	}
	/**
	 * Set value to value | b.
	 * @return the new value
	 **/
	public  long or(final long b) {
		synchronized (this.lock_) {
			this.value_ = this.value_ | b;
			return this.value_;
		}
	}
	/**
	 * Set to newValue.
	 * @return the old value
	 **/

	public long set(final long newValue) {
		synchronized (this.lock_) {
			final long old = this.value_;
			this.value_ = newValue;
			return old;
		}
	}
	/**
	 * Subtract amount from value (i.e., set value -= amount)
	 * @return the new value
	 **/
	public long subtract(final long amount) {
		synchronized (this.lock_) {
			return this.value_ -= amount;
		}
	}
	/**
	 * Atomically swap values with another SynchronizedLong.
	 * Uses identityHashCode to avoid deadlock when
	 * two SynchronizedLongs attempt to simultaneously swap with each other.
	 * @return the new value
	 **/

	public long swap(final SynchronizedLong other) {
		if (other != this) {
			SynchronizedLong fst = this;
			SynchronizedLong snd = other;
			if (System.identityHashCode(fst) > System.identityHashCode(snd)) {
				fst = other;
				snd = this;
			}
			synchronized(fst.lock_) {
				synchronized(snd.lock_) {
					fst.set(snd.set(fst.get()));
				}
			}
		}
		return this.value_;
	}
	@Override
	public String toString() { return String.valueOf(this.get()); }
	/**
	 * Set value to value ^ b.
	 * @return the new value
	 **/
	public  long xor(final long b) {
		synchronized (this.lock_) {
			this.value_ = this.value_ ^ b;
			return this.value_;
		}
	}
}
