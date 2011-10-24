package negotiation.interactionprotocols.socialNegotiation;

import negotiation.interactionprotocols.candidatureNegotiation.ResourceIdentifier;
import negotiation.interactionprotocols.contracts.ActionNegotiationContract;
import dima.basicagentcomponents.AgentIdentifier;

public class SimpleSocialContract
<Charge extends ResourcePart,
Info extends Comparable<Info>>
extends ResourceReallocationContract<Charge>
implements SocialContract<Info>, ActionNegotiationContract<ResourceIdentifier, Charge>{
	private static final long serialVersionUID = -4139352654489090460L;
		
	Info info;	
	AgentIdentifier infoOwner;
	
	public SimpleSocialContract(final AgentIdentifier manager) {
		super(manager);
	}
	
	@Override
	public void attachInfo(AgentIdentifier id, final Info i) {
		this.info = i;		
		infoOwner = id;
	}

	@Override
	public Info getInfo() {
		return this.info;
	}

	@Override
	public AgentIdentifier getInfoOwner() {
		return this.infoOwner;
	}
	
	@Override
	public int compareTo(final SocialContract<Info> o) {
		return this.getInfo().compareTo(o.getInfo());
	}
}
