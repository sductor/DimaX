package examples.Facorial;

import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.Message;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;

/**
 * Agent qui va s'occuper d'une partie de calcul
 */
public class AgentFact extends BasicCommunicatingAgent {
	/**
	 *
	 */
	private static final long serialVersionUID = 706856212746826502L;

	int n, nbMes;

	Vector liste;

	public int reusltPartiel;

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:06:37)
	 */
	public AgentFact() {
		this.liste = new Vector();
	}

	AgentFact(final int m, final AgentIdentifier id) {
		super(id);
		this.nbMes = 0;
		this.n = m;
		this.reusltPartiel = 1;

	}

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:06:37)
	 */
	public AgentFact(final AgentIdentifier newId) {
		super(newId);
		this.liste = new Vector();
	}

	/**
	 * Insert the method's description here. Creation date: (08/11/2002
	 * 07:06:37)
	 */
	public AgentFact(final AgentIdentifier newId, final int nn) {
		super(newId);
		this.reusltPartiel = 1;
		this.n = nn;
		this.liste = new Vector();
	}

	@Override
	public boolean isActive() {
		return !(this.liste.size() < 2 && this.nbMes == 0);
	}

	/**
	 * On initialise la liste des elements dans le vecteur ici ce sont les
	 * nombres 1 � n
	 */
	@Override
	public void proactivityInitialize() {
		for (int i = 1; i < this.n; i++) {
			this.liste.addElement(new Integer(i));
		}
		this.wwait(1000);
	}

	/**
	 * On initialise la liste des elements dans le vecteur ici ce sont les
	 * nombres 1 � n
	 */
	public void result(final Integer a) {

		this.liste.addElement(a);

	}

	void resultatPartiel(final int a) {
		this.liste.addElement(new Integer(a));
		this.nbMes--;
	}

	@Override
	public void step() {
		this.readAllMessages();
		while (this.liste.size() > 1) {
			final Message m = new Message("multiply", this.liste.elementAt(0), this.liste
					.elementAt(1));
			this.sendMessage(new AgentName("M"), m);
			this.nbMes++;
			this.liste.removeElementAt(0);
			this.liste.removeElementAt(0);
			//this.sendAll("resultPartiel", null);
		}
	}
}
