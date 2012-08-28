package negotiation.negotiationframework.information;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import negotiation.negotiationframework.agent.AgentState;
import dima.basicagentcomponents.AgentIdentifier;

public class SimpleKnowledgeService<PersonalState extends AgentState> 
extends SimpleInformationService
implements KnowledgeService<PersonalState> {

	PersonalStateService<PersonalState> stateService;
	InformationService informationService;
	
	public SimpleKnowledgeService(PersonalState myInitialState) {
		stateService = new SimpleStateService<PersonalState>(myInitialState); 
		SimpleInformationService informNacquain = new SimpleInformationService();
		informationService = informNacquain;
	}

	//
	// Accessors
	//
	
	/*
	 * State
	 */


	public PersonalState getMyCurrentState() {
		return stateService.getMyCurrentState();
	}

	public void setNewState(PersonalState s) {
		stateService.setNewState(s);
		informationService.add(s);
	}
	
	/*
	 * Acquaintances
	 */

	public Collection<AgentIdentifier> getKnownAgents() {
		return informationService.getKnownAgents();
	}

	public void add(AgentIdentifier agentId) {
		informationService.add(agentId);
	}

	public void addAll(Collection<? extends AgentIdentifier> agents) {
		informationService.addAll(agents);
	}

	public void remove(AgentIdentifier agentId) {
		informationService.remove(agentId);
	}

	/*
	 * Information
	 */

	public <Info extends Information> Info get(Class<Info> informationType,
			AgentIdentifier agentId) {
		return informationService.get(informationType, agentId);
	}

	public <Info extends Information> void add(Info information) {
		informationService.add(information);
	}
//
//	public <Info extends Information> void remove(Info information) {
//		informationService.remove(information);
//	}
}
