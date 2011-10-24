package examples.eAgenda.data;


import java.util.ArrayList;

import dima.basicagentcomponents.AgentIdentifier;

public class Contact extends People {

	/**
	 *
	 */
	private static final long serialVersionUID = 7898410956897062042L;
	AgentIdentifier myAgent;

	public Contact(final String myName, final AgentIdentifier id) {
		super(myName);
		this.myAgent = id;
	}
	/** The could should know what is the ID of the associated agent */
	public AgentIdentifier getAgentID() {
		return this.myAgent;
	}
	@Override
	public int getSize(){
		return 1;
	}
	@Override
	public ArrayList getCanonicalList() {
		final ArrayList list = new ArrayList(1);
		list.add(this);
		return list;
	}
}
