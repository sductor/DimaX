package negotiation.negotiationframework.information.belief;

import negotiation.negotiationframework.information.Information;
import negotiation.negotiationframework.information.InformationService;
import negotiation.negotiationframework.information.NoInformationAvailableException;
import dima.basicagentcomponents.AgentIdentifier;

public interface OpinionService
extends InformationService{

	public <Info extends Information> Info getBelief(Class<Info> informationType, AgentIdentifier id)
	throws NoInformationAvailableException;

	public <Info extends Information> Float getBeliefConfidence(Info information)
	throws NoInformationAvailableException;

	public <Info extends Information> Info getMyOpinion(Class<Info> informationType);
	
	public <Info extends Information> void collectInformation(Class<Info> informationType);

}
