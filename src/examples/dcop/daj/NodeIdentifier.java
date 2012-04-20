package examples.dcop.daj;

import dima.basicagentcomponents.AgentName;

public class NodeIdentifier extends AgentName {
	/**
	 *
	 */
	private static final long serialVersionUID = 4492707587024208428L;
	Integer id;

	public NodeIdentifier(final Integer id) {
		super(id.toString());
		this.id=id;
	}

	public int asInt() {
		return this.id;
	}


}
