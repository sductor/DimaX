package dima.kernel.SimulationTools;

/**
 * Multi-agent simultions includes often a large number of agents
 * and do no need to be distributed. So, this class provides basic scheduler
 * to run agents without threads.
 **/

 import java.util.Vector;

import dima.kernel.ProactiveComponents.ProactiveComponent;
import dima.kernel.ProactiveComponents.ProactiveComponentsManager;



public class Simulator extends ProactiveComponentsManager {
	/**
	 *
	 */
	private static final long serialVersionUID = 9096846136533655237L;
	public int stepNumber = 10;


public Simulator() {
	super();
}


public Simulator(final java.util.Vector pao) {
	super(pao);
}


public Simulator(final java.util.Vector pao, final int nbPas) {
	super(pao);
	this.stepNumber = nbPas;
}
/**
 * Insert the method's description here.
 * Creation date: (09/07/2002 11:20:17)
 * @return int
 */
public int getStepNumber() {
	return this.stepNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (09/07/2002 11:20:17)
 * @param newStepNumber int
 */
public void setStepNumber(final int newStepNumber) {
	this.stepNumber = newStepNumber;
}
/**
 * Execute
 * all known ProactiveObjects.
 *
 */

@Override
public void startAll() {
	final Vector pv = this.getProactiveObjects();
	for (int i=0; i<pv.size();i++) {
		final ProactiveComponent pao =(ProactiveComponent)pv.elementAt(i);
		pao.activate();
	}
}
/**
 * Execute
 * all known ProactiveObjects.
 *
 */

public void startAllWithSimulation() {
	final Vector pv = this.getProactiveObjects();
	for (int j=0; j<this.stepNumber; j++)
		for (int i=0; i<pv.size();i++) {
			final ProactiveComponent pao =(ProactiveComponent)pv.elementAt(i);
			pao.step();
			}
}
}
