package examples.Facorial;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.Message;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;

/**
 * Agent qui va s'occuper d'une partie de calcul
 */
public class AgentMult extends BasicCommunicatingAgent {

	/**
	 *
	 */
	private static final long serialVersionUID = -7584556203543396026L;

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:05:24)
	 */
	public AgentMult() {
	}

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:05:24)
	 */
	public AgentMult(final AgentIdentifier newId) {
		super(newId);
	}

	@Override
	public boolean competenceIsActive() {
		return true;
	}

	public static void main(final String args[]) {

		final AgentFact F = new AgentFact(new AgentName("Fact"), 10);
		final AgentMult M = new AgentMult(new AgentName("M"));
		F.addAquaintance(M.getAddress());
		M.addAquaintance(F.getAddress());

		F.activate();
		M.activate();

	}

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:09:41)
	 *
	 * @return java.lang.Integer
	 * @param a
	 *            java.lang.Integer
	 * @param b
	 *            java.lang.Integer
	 */
	public void multiply(final Integer a, final Integer b) {
		final Integer c = new Integer(a.intValue() * b.intValue());
		System.out.println("Result :" + c);
		final Message m = new Message("result", c);
		this.sendMessage(new AgentName("Fact"), m);

	}

	@Override
	public void step() {
		this.readAllMessages();
		this.wwait();

	}
}
