package examples.Factorial2.masapplication;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;


/**
 * Agent qui va s'occuper d'une partie de calcul
 */
public class AgentFact extends BasicCommunicatingAgent {
	private static final long serialVersionUID = -6545091805135720139L;

	int n, nbMes;

	LinkedList<Integer> liste;
	LinkedList<AgentIdentifier> agentsMult;

	public AgentFact(final AgentIdentifier id, final int m, final AgentIdentifier... agentsMult) {
		super(id);
		this.agentsMult=new LinkedList<AgentIdentifier>(Arrays.asList(agentsMult));
		this.n = m;
	}

	public AgentFact(final AgentIdentifier id, final int m,
			final Collection<AgentIdentifier> mult) {
		super(id);
		this.agentsMult=new LinkedList<AgentIdentifier>(mult);
		this.n = m;
	}


	//
	// Proactivity
	//
	@Override
	public boolean isActive() {
		return !(this.liste.size() < 2 && this.nbMes == 0);
	}

	/**
	 * On initialise la liste des elements dans le vecteur ici ce sont les
	 * nombres 1 à n
	 */
	@Override
	public void proactivityInitialize() {
		this.nbMes = 0;
		this.liste = new LinkedList<Integer>();

		for (int i = this.n; i > 1; i--) {
			this.liste.add(new Integer(i));
		}
		this.wwait(1000);
	}

	@Override
	public void step() {
		this.readAllMessages();
		while (this.liste.size() > 1) {
			final AgentIdentifier mult = this.agentsMult.pop();
			final Message m = new Message("multiply", this.liste.pop(), this.liste.pop());
			this.sendMessage(mult, m);
			this.nbMes++;
			this.agentsMult.addLast(mult);
		}
	}

	@Override
	public void proactivityTerminate(){
		LogService.write(this.getId(),"La factoriel de "+this.n+" vaut "+this.liste.pop());
		this.desactivateWithFipa();
		//		System.exit(1);
	}

	@Override
	public void finalize(){
		System.out.println("so long and thanks for all the fish");
	}

	//
	// MessageHandling
	//

	public void result(final Integer a) {
		this.liste.add(a);
		this.nbMes--;
	}
}
