// --------------------------------------------------------------------------
// $Id: Network.java,v 1.3 1997/10/25 15:50:00 ws Exp $
// a system of nodes linked by channels
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package negotiation.dcopframework.daj;

import negotiation.dcopframework.daj.awt.*;

public class Network extends Thread {

	// set of nodes, its size, current number of nodes in set, nr of active nodes
	private int setSize = 100;
	private Node[] set = new Node[setSize];
	private int setNum = 0;
	private int activeNum = 0;
	// current application
	private Application application;
	// scheduler for network nodes
	private Scheduler scheduler = new SchedulerDefault();
	// visualizer for network
	private Visualizer visualizer = null;
	// programs of current network
	private Program[] programs;
	// set if network has prematurely exited with corresponding code
	private boolean exited = false;
	private int exitCode;

	// --------------------------------------------------------------------------
	// create new Network for application `appl`
	// --------------------------------------------------------------------------
	public Network(Application appl) {
		application = appl;
	}

	// --------------------------------------------------------------------------
	// create new Network for application `appl`
	// switch visualization on with title `label` and size `x/y`
	// `appl` is the current application
	// move visualizer to `px/py`, if `resize`, then resize to `w/h`
	// --------------------------------------------------------------------------
	public Network(Application appl, String label, int x, int y, int px, int py,
			boolean resize, int w, int h) {
		this(appl);
		visualizer = new Visualizer(appl, this, label, x, y, px, py, resize, w, h);
		redraw();
	}

	// --------------------------------------------------------------------------
	// set network scheduler
	// --------------------------------------------------------------------------
	public void setScheduler(Scheduler sched) {
		scheduler = sched;
	}

	// --------------------------------------------------------------------------
	// switch visualization off
	// --------------------------------------------------------------------------
	public void devisualize() {
		Assertion.test(visualizer != null, "no visualization to switch off");
		visualizer.terminate();
		visualizer = null;
	}

	// --------------------------------------------------------------------------
	// redraw network
	// --------------------------------------------------------------------------
	public void redraw() {
		if (visualizer != null) {
			visualizer.getScreen().redraw();
		}
	}

	// --------------------------------------------------------------------------
	// print `message`
	// --------------------------------------------------------------------------
	public void print(String message) {
		if (visualizer == null) System.out.println(message);
		else visualizer.setText(message);
	}

	// --------------------------------------------------------------------------
	// add `node` to network
	// --------------------------------------------------------------------------
	public void add(Node node) {
		if (setNum == setSize) {
			Node oldSet[] = set;
			setSize *= 2;
			set = new Node[setSize];
			for (int i = 0; i < setNum; i++)
				set[i] = oldSet[i];
		}
		if (visualizer != null) {
			NodeVisual v0 = node.getVisual();
			for (int i = 0; i < setNum; i++) {
				NodeVisual v1 = set[i].getVisual();
				Assertion.test(v0.x() != v1.x() || v0.y() != v1.y(),
						"Two nodes positioned at same place");
			}
		}
		set[setNum] = node;
		setNum = setNum + 1;
		scheduler.register(node);
	}

	// --------------------------------------------------------------------------
	// exit with `code` if no visualizer available
	// --------------------------------------------------------------------------
	public void systemExit(int code) {
		if (visualizer == null) System.exit(code);
	}

	// --------------------------------------------------------------------------
	// terminate network execution with `code`
	// --------------------------------------------------------------------------
	public void exit(int code) {
		exited = true;
		exitCode = code;
		scheduler.exit();
	}

	// --------------------------------------------------------------------------
	// execute network
	// --------------------------------------------------------------------------
	synchronized public void run() {
		scheduler.main(Thread.currentThread());
		for (int i = 0; i < setNum; i++) {
			set[i].start();
			Thread.yield();
		}
		programs = new Program[setNum];
		for (int i = 0; i < setNum; i++) {
			programs[i] = set[i].getProgram();
		}
		
		activeNum = setNum;
		if (visualizer != null) scheduler.interrupt();
		// decrease priority to make sure applet thread gets updates
		setPriority(getPriority() - 1);
		scheduler.schedule();
		if (visualizer != null) visualizer.inactivate();
		/*if (exited) {
			systemExit(exitCode);
		}
		else if (activeNum == 0) {
			if (visualizer != null) print("Execution has terminated");
			systemExit(0);
		}
		else {
			print("Execution is deadlocked");
			systemExit(-1);
		}*/
	}

	// --------------------------------------------------------------------------
	// signal termination of `node`
	// --------------------------------------------------------------------------
	public void terminate(Node node) {
		activeNum--;
		scheduler.terminate();
	}

	// --------------------------------------------------------------------------
	// return number of nodes in set
	// --------------------------------------------------------------------------
	public int getSize() {
		return setNum;
	}

	// --------------------------------------------------------------------------
	// return node numbered `i` in set
	// --------------------------------------------------------------------------
	public Node getNode(int i) {
		Assertion.test(0 <= i && i < setNum, "invalid node index");
		return set[i];
	}

	// --------------------------------------------------------------------------
	// return scheduler
	// --------------------------------------------------------------------------
	public Scheduler getScheduler() {
		return scheduler;
	}

	// --------------------------------------------------------------------------
	// return visualizer
	// --------------------------------------------------------------------------
	public Visualizer getVisualizer() {
		return visualizer;
	}

	// --------------------------------------------------------------------------
	// return vector of programs executed by network nodes
	// --------------------------------------------------------------------------
	public Program[] getPrograms() {
		return programs;
	}

	// --------------------------------------------------------------------------
	// return network application
	// --------------------------------------------------------------------------
	public Application getApplication() {
		return application;
	}
}
