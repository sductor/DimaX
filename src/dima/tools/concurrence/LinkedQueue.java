package dima.tools.concurrence;

/*
  File: LinkedQueue.java

  Originally written by Doug Lea and released into the public domain.
  This may be used for any purposes whatsoever without acknowledgment.
  Thanks for the assistance and support of Sun Microsystems Labs,
  and everyone contributing, testing, and using this code.

  History:
  Date       Who                What
  11Jun1998  dl               Create public version
  25aug1998  dl               added peek
  10dec1998  dl               added isEmpty
  10oct1999  dl               lock on node object to ensure visibility
*/

/**
 * A linked list based channel implementation.
 * The algorithm avoids contention between puts
 * and takes when the queue is not empty.
 * Normally a put and a take can proceed simultaneously.
 * (Although it does not allow multiple concurrent puts or takes.)
 * This class tends to perform more efficently than
 * other Channel implementations in producer/consumer
 * applications.
 * <p>[<a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html"> Introduction to this package. </a>]
 **/

public class LinkedQueue implements Channel {


  /**
   * Dummy header node of list. The first actual node, if it exists, is always
   * at head_.next. After each take, the old first node becomes the head.
   **/
  protected LinkedNode head_;

  /**
   * Helper monitor for managing access to last node.
   **/
  protected final Object putLock_ = new Object();

  /**
   * The last node of list. Put() appends to list, so modifies last_
   **/
  protected LinkedNode last_;

  /**
   * The number of threads waiting for a take.
   * Notifications are provided in put only if greater than zero.
   * The bookkeeping is worth it here since in reasonably balanced
   * usages, the notifications will hardly ever be necessary, so
   * the call overhead to notify can be eliminated.
   **/
  protected int waitingForTake_ = 0;

  public LinkedQueue() {
	this.head_ = new LinkedNode(null);
	this.last_ = this.head_;
  }
  /** Main mechanics for take/poll **/
  protected synchronized Object extract() {
	synchronized(this.head_) {
	  Object x = null;
	  final LinkedNode first = this.head_.next;
	  if (first != null) {
		x = first.value;
		first.value = null;
		this.head_ = first;
	  }
	  return x;
	}
  }
  /** Main mechanics for put/offer **/
  protected void insert(final Object x) {
	synchronized(this.putLock_) {
	  final LinkedNode p = new LinkedNode(x);
	  synchronized(this.last_) {
		this.last_.next = p;
		this.last_ = p;
	  }
	  if (this.waitingForTake_ > 0)
		this.putLock_.notify();
	}
  }
  public boolean isEmpty() {
	synchronized(this.head_) {
	  return this.head_.next == null;
	}
  }
  @Override
public boolean offer(final Object x, final long msecs) throws InterruptedException {
	if (x == null) throw new IllegalArgumentException();
	if (Thread.interrupted()) throw new InterruptedException();
	this.insert(x);
	return true;
  }
  @Override
public Object peek() {
	synchronized(this.head_) {
	  final LinkedNode first = this.head_.next;
	  if (first != null)
		return first.value;
	  else
		return null;
	}
  }
  @Override
public Object poll(final long msecs) throws InterruptedException {
	if (Thread.interrupted()) throw new InterruptedException();
	Object x = this.extract();
	if (x != null)
	  return x;
	else
		synchronized(this.putLock_) {
			try {
			  long waitTime = msecs;
			  final long start = msecs <= 0? 0 : System.currentTimeMillis();
			  ++this.waitingForTake_;
			  for (;;) {
				x = this.extract();
				if (x != null || waitTime <= 0) {
				  --this.waitingForTake_;
				  return x;
				}
				else {
				  this.putLock_.wait(waitTime);
				  waitTime = msecs - (System.currentTimeMillis() - start);
				}
			  }
			}
			catch(final InterruptedException ex) {
			  --this.waitingForTake_;
			  this.putLock_.notify();
			  throw ex;
			}
		  }
  }
  @Override
public void put(final Object x) throws InterruptedException {
	if (x == null) throw new IllegalArgumentException();
	if (Thread.interrupted()) throw new InterruptedException();
	this.insert(x);
  }
  @Override
public Object take() throws InterruptedException {
	if (Thread.interrupted()) throw new InterruptedException();
	// try to extract. If fail, then enter wait-based retry loop
	Object x = this.extract();
	if (x != null)
	  return x;
	else
		synchronized(this.putLock_) {
			try {
			  ++this.waitingForTake_;
			  for (;;) {
				x = this.extract();
				if (x != null) {
				  --this.waitingForTake_;
				  return x;
				} else
					this.putLock_.wait();
			  }
			}
			catch(final InterruptedException ex) {
			  --this.waitingForTake_;
			  this.putLock_.notify();
			  throw ex;
			}
		  }
  }
}
