package negotiation.interactionprotocols.socialNegotiation;

import java.util.HashMap;
import java.util.Map;

import negotiation.agentframework.rationalagent.AgentActionSpecification;
import negotiation.agentframework.rationalagent.AgentActionSpecification.ActionArguments;
import negotiation.interactionprotocols.SimpleContractAnswer;
import negotiation.interactionprotocols.contracts.AbstractSendableContract;
import dima.basicagentcomponents.AgentIdentifier;

public class SocialAnswer<
ActionSpec extends AgentActionSpecification,
ActionArgs extends ActionArguments,
Info extends Comparable<Info>> 
extends SimpleContractAnswer<ActionSpec,ActionArgs> implements SocialContract<Info>{
	private static final long serialVersionUID = 4790879401422007210L;
	

	private final Map<AgentIdentifier, Map<ActionSpec, ActionArgs>> args = new HashMap<AgentIdentifier, Map<ActionSpec,ActionArgs>>();
	Info info;	
	AgentIdentifier infoOwner;
	
	public SocialAnswer(final AbstractSendableContract id) {
		super(id);
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
	
	public void setRequiredArgument(final AgentIdentifier id, final Map<ActionSpec, ActionArgs> requiredArguments){
		this.args.put(id, requiredArguments);
	}
	
	public Map<ActionSpec, ActionArgs> getRequiredArgument(final AgentIdentifier id){
		return this.args.get(id);
	}
}
