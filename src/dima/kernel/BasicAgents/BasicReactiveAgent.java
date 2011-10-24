package dima.kernel.BasicAgents;

 /* this class describe simple agents.
 The main activity of these agents is described by the method proactivityLoop
  * @author: Zahia Guessoum
 */

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.kernel.ProactiveComponents.ProactiveComponent;
public  abstract class BasicReactiveAgent extends ProactiveComponent implements IdentifiedComponentInterface {
	/**
	 *
	 */
	private static final long serialVersionUID = 2861608007927031135L;
	private AgentIdentifier id;
	protected static int nbInstances=0;
/**
 * AbstractAgent constructor comment.
 */
public BasicReactiveAgent() {
	super();
	nbInstances ++  ;
	this.id = new AgentName("Agent" + String.valueOf(nbInstances));
}
/**
 * AbstractAgent constructor comment.
 */
public BasicReactiveAgent(final AgentIdentifier newId) {
	super();
	this.id = newId;

}
/**
 * AbstractAgent constructor comment.
 */
public BasicReactiveAgent(final String newId) {
	super();
	this.id = new AgentName(newId);

}
/**
 * Insert the method's description here.
 * Creation date: (19/07/00 16:34:07)
 */
@Override
public void activate() {
	final AgentEngine engine = new AgentEngine (this);
	engine.startUp();}
/**
 * Insert the method's description here.
 * Creation date: (27/04/00 11:37:10)
 * @return Gdima.competences.communication.AgentIdentifier
 */
public AgentIdentifier getId() {
	return this.id;
}
/**
 * Insert the method's description here.
 * Creation date: (27/04/00 11:37:10)
 * @return Gdima.competences.communication.AgentIdentifier
 */
@Override
public AgentIdentifier getIdentifier() {
    //System.out.println("getIdentifier");
	return this.id;
}
/**
 * Insert the method's description here.
 * Creation date: (08/07/2002 17:08:42)
 * @param s java.lang.String
 */
public void print(final String s)
   {System.out.println(s);}
/**
 * Insert the method's description here.
 * Creation date: (27/04/00 11:37:10)
 * @param newId Gdima.competences.communication.AgentIdentifier
 */
public void setId(final AgentIdentifier newId) {
	this.id = newId;
}
}
