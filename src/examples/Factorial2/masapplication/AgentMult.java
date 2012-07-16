package examples.Factorial2.masapplication;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;

/**
 * Agent qui va s'occuper d'une partie de calcul
 */
public class AgentMult extends BasicCommunicatingAgent {

	/**
	 *
	 */
	private static final long serialVersionUID = 4623313855732490917L;
	AgentIdentifier myFact;

	public AgentMult(final AgentIdentifier newId, final AgentIdentifier myFact) {
		super(newId);
		this.myFact = myFact;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	public void multiply(final Integer a, final Integer b) {
		final Integer c = new Integer(a.intValue() * b.intValue());
		LogService.write(this.getId(),""+a+" * "+b+" = " + c);
		final Message m = new Message("result", c);
		this.sendMessage(this.myFact, m);
	}


	@Override
	public void proactivityTerminate(){
		this.desactivateWithFipa();
		//		System.exit(1);
	}

	@Override
	public void finalize(){
		System.out.println("so long and thanks for all the fish");
	}

	@Override
	public void step() {
		this.readAllMessages();
		this.wwait();
	}
}
