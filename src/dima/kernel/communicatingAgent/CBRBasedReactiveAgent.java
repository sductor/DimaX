package dima.kernel.communicatingAgent;

import dima.basicagentcomponents.AgentIdentifier;
import dima.tools.caseBasedReasoning.Case;
import dima.tools.caseBasedReasoning.CaseBase;

public abstract class CBRBasedReactiveAgent extends  BasicCommunicatingAgent{
	/**
	 *
	 */
	private static final long serialVersionUID = 6847983342100129437L;
	public CaseBase cb;
	public Case currentCase;
	/**
	 * Insert the method's description here.
	 * Creation date: (19/04/02 14:13:25)
	 */
	public CBRBasedReactiveAgent() {super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/04/02 14:13:25)
	 */
	public CBRBasedReactiveAgent(final AgentIdentifier newId) {
		super(newId);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/04/02 14:13:25)
	 */
	public CBRBasedReactiveAgent (final CaseBase a) {
		super();
		this.cb = a;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/04/02 14:13:25)
	 */
	public CBRBasedReactiveAgent (final CaseBase a, final AgentIdentifier newId) {
		super(newId);
		this.cb = a;
	}
	/**
	 * Tests wheter a proactive object is active or no ie whether the ProactiveComponent.
	 */
	@Override
	public boolean isActive()
	{return this.cb.isActive();
	}
	/**
	 * This is the main method for a proactive component :
	 * what to do while in activity.
	 *
	 */

	public abstract Case newId();
	/**
	 * This is the main method for a proactive component :
	 * what to do while in activity.
	 *
	 */

	@Override
	public void step(){
		//update the current case;
		this.currentCase= this.updateCurrentCase();
		this.cb.step(this.currentCase);
	}
	/**
	 * This is the main method for a proactive component :
	 * what to do while in activity.
	 *
	 */

	public abstract Case updateCurrentCase();
}
