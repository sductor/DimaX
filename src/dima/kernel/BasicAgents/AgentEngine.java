package dima.kernel.BasicAgents;

/**
 * Insert the type's description here.
 * Creation date: (27/04/00 11:31:04)
 * @author: Zahia Guessoum
 */
  import dima.basicinterfaces.ProactiveComponentInterface;
import dima.kernel.ProactiveComponents.ProactiveComponent;
import dima.kernel.ProactiveComponents.ProactiveComponentEngine;


@Deprecated/** 17 mai 2010 , Sylvain Ductor */
public  class AgentEngine extends ProactiveComponentEngine {
/**
	 *
	 */
	private static final long serialVersionUID = 5501339121403250570L;
/**
 * ATNInterpreter constructor comment.
 */
public AgentEngine(final ProactiveComponent p) {
		super(p);
		//proactivity = p;
		this.initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (27/04/01 18:23:44)
 * @return Gdima.proactive.component.ProactiveComponent
 */
public ProactiveComponentInterface getProactivity() {
	return this.proactivity;
}
}
