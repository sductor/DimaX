package negotiation.interactionprotocols.socialNegotiation;

import dima.basicagentcomponents.AgentIdentifier;
import negotiation.interactionprotocols.contracts.AbstractSendableContract;


public interface SocialContract<
Info extends Comparable<Info>> 
extends AbstractSendableContract, Comparable<SocialContract<Info>>{

	public void attachInfo(AgentIdentifier id, Info i);	
	
	public Info getInfo();

	public AgentIdentifier getInfoOwner();
}
