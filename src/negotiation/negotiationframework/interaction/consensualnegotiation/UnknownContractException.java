package negotiation.negotiationframework.interaction.consensualnegotiation;

import negotiation.negotiationframework.interaction.ContractIdentifier;

public class UnknownContractException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7034033947098338528L;
	final ContractIdentifier id;

	public UnknownContractException(ContractIdentifier id) {
		super(id.toString());
		this.id = id;
	}

	public ContractIdentifier getId() {
		return this.id;
	}
}