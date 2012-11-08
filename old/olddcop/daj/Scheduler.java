// ----------------------------------------------------------------------------
// $Id: Scheduler.java,v 1.5 1997/11/14 16:38:59 schreine Exp schreine $
// scheduler for network execution
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package frameworks.faulttolerance.olddcop.daj;

import frameworks.faulttolerance.olddcop.daj.awt.NodeVisual;
import frameworks.faulttolerance.olddcop.daj.awt.Visualizer;

abstract public class Scheduler {

	// maximum number of threads
	private final int nodeMax = 2000;
	// actual number of threads
	private int nodeNum = 0;
	// index of currently executing thread; -1 denotes main thread
	private int threadExec = -1;
	// main thread
	private Thread mainThread = null;
	// node threads to schedule and their status
	private Node node[] = new Node[nodeMax];
	private boolean active[] = new boolean[nodeMax];
	// set if thread is to be terminated after being descheduled
	private boolean terminateThread = false;
	// set if thread continues execution after being descheduled
	private boolean continueThread = false;
	// set if execution is to be interrupted
	private boolean interruptThread = false;
	// set if execution has been interrupted
	private boolean interrupted = false;
	// set if execution takes place slowly
	private boolean walking = false;
	// interrupted thread
	private Thread currentThread;
	// scheduling time and counter
	private int time = 0;
	private int counter = 0;

	//--------------------------------------------------------------------------
	// register main thread `t`
	//--------------------------------------------------------------------------
	public void main(Thread t) {
		Assertion.test(mainThread == null, "main thread already set");
		mainThread = t;
	}

	//--------------------------------------------------------------------------
	// register node `t` to schedule; called before scheduler is activated
	//--------------------------------------------------------------------------
	public void register(Node t) {
		Assertion.test(nodeNum < nodeMax, "too many nodes to schedule");
		node[nodeNum] = t;
		active[nodeNum] = true;
		nodeNum++;
	}

	//--------------------------------------------------------------------------
	// return number of threads to schedule
	//--------------------------------------------------------------------------
	public int getNumber() {
		return nodeNum;
	}

	//--------------------------------------------------------------------------
	// returns true iff thread `i` is active
	//--------------------------------------------------------------------------
	public boolean isReady(int i) {
		Assertion.test(0 <= i && i < nodeNum, "invalid thread");
		return active[i];
	}

	//--------------------------------------------------------------------------
	// terminate current thread (never returns)
	//--------------------------------------------------------------------------
	public void terminate() {
		active[threadExec] = false;
		terminateThread = true;
		schedule();
		Assertion.fail("thread not terminated");
	}

	//--------------------------------------------------------------------------
	// exempt current thread from scheduling and schedule other thread
	// return index of current thread
	//--------------------------------------------------------------------------
	public int sleep() {
		int index = threadExec;
		active[index] = false;
		continueThread = true;
		schedule();
		return index;
	}

	//--------------------------------------------------------------------------
	// interrupt scheduling
	//--------------------------------------------------------------------------
	public void interrupt() {
		interruptThread = true;
	}

	//--------------------------------------------------------------------------
	// interrupt execution and wait until acknowledged
	//--------------------------------------------------------------------------
	public void interruptWait() {
		interruptThread = true;
		while (!interrupted)
			Thread.yield();
	}

	//--------------------------------------------------------------------------
	// continue execution after interruption
	//--------------------------------------------------------------------------
	public void cont() {
		interruptWait();
		interruptThread = false;
		interrupted = false;
		walking = false;
		synchronized (currentThread) {
			currentThread.notify();
		}
	}

	//--------------------------------------------------------------------------
	// continue execution slowly after interruption
	//--------------------------------------------------------------------------
	public void walk() {
		interruptWait();
		interruptThread = false;
		interrupted = false;
		walking = true;
		synchronized (currentThread) {
			currentThread.notify();
		}
	}

	//--------------------------------------------------------------------------
	// continue execution for one step
	//--------------------------------------------------------------------------
	public void step() {
		synchronized (currentThread) {
			currentThread.notify();
		}
	}

	//--------------------------------------------------------------------------
	// bring thread numbered `index` back to scheduling
	//--------------------------------------------------------------------------
	public void awake(int index) {
		active[index] = true;
	}

	//--------------------------------------------------------------------------
	// terminate execution of all threads
	//--------------------------------------------------------------------------
	public void exit() {
		for (int i = 0; i < nodeNum; i++)
			active[i] = false;
		interruptThread = false;
		interrupted = false;
		walking = false;
		terminateThread = true;
		schedule();
		Assertion.fail("return from scheduler");
	}

	//--------------------------------------------------------------------------
	// give control to thread `i`, (-1) signals no thread
	//--------------------------------------------------------------------------
	@SuppressWarnings("deprecation")
	public void schedule(int i) {
		Assertion.test(i == -1 || (0 <= i && i < nodeNum && active[i]),
				"invalid node");
		Thread myself = Thread.currentThread();
		if (threadExec != -1) {
			Node currentNode = (Node) Thread.currentThread();
			currentNode.incSwitches();
			NodeVisual visual = currentNode.getVisual();
			if (visual != null)
				visual.draw();
		}
		if (interruptThread) {
			currentThread = myself;
			interrupted = true;
			walking = false;
			synchronized (currentThread) {
				try {
					currentThread.wait();
				} catch (InterruptedException e) {
					Assertion.fail("InterruptedException");
				}
			}
		} else if (walking) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Assertion.fail("InterruptedException");
			}
		}
		boolean terminate = terminateThread;
		boolean cont = continueThread;
		terminateThread = false;
		continueThread = false;
		if (threadExec == i)
			return;
		threadExec = i;
		synchronized (myself) {
			if (i == -1) {
				synchronized (mainThread) {
					mainThread.notify();
				}
			} else {
				Node n = node[i];
				synchronized (n) {
					n.notify();
				}
			}
			if (terminate) {
				myself.stop();
			}
			if (!cont) {
				try {
					myself.wait();
				} catch (InterruptedException e) {
					Assertion.fail("InterruptedException");
				}
			}
		}
	}

	//--------------------------------------------------------------------------
	// schedule next program for execution
	//--------------------------------------------------------------------------
	public void schedule() {
		schedule(nextProgram());
	}

	//--------------------------------------------------------------------------
	// increase network time
	//--------------------------------------------------------------------------
	public void incTime() {
		counter++;
		if (counter == nodeNum) {
			counter = 0;
			time++;
			Node currentNode = (Node) Thread.currentThread();
			Visualizer visualizer = currentNode.getNetwork().getVisualizer();
			if (visualizer != null)
				visualizer.getScreen().drawTime(time);
		}
	}

	public int getTime() {
		return time;
	}

	//--------------------------------------------------------------------------
	// return index of next program for execution (-1, if none)
	//--------------------------------------------------------------------------
	abstract public int nextProgram();
}
