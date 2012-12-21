package frameworks.negotiation.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.modules.faults.Assert;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.NegotiationParameters;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.contracts.ContractTrunk;
import frameworks.negotiation.contracts.MatchingCandidature;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.Receivers;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.protocoles.collaborative.InformedCandidature;
import frameworks.negotiation.rationality.AgentState;
import frameworks.negotiation.selection.GreedySelectionModule.GreedySelectionType;

public class MixedCandidatureTypecontractSelectionCore <
Agent extends NegotiatingAgent<PersonalState, Contract>,
PersonalState extends AgentState,
Contract extends MatchingCandidature>
extends
BasicAgentCompetence<Agent>
implements SelectionModule<
Agent,
PersonalState,
Contract>  {


	final SelectionModule<Agent,PersonalState,Contract> mainSelectionModule;
	final long maxComputingTime;

	public MixedCandidatureTypecontractSelectionCore(
			SelectionModule<Agent, PersonalState, Contract> mainSelectionModule,
			final long maxComputingTime)
					throws UnrespectedCompetenceSyntaxException {
		super();
		this.mainSelectionModule = mainSelectionModule;
		this.maxComputingTime=maxComputingTime;
	}

	public void setMyAgent(Agent ag){
		super.setMyAgent(ag);
		mainSelectionModule.setMyAgent(ag);
	}

	@Override
	public Collection<Contract> selection(PersonalState currentState,
			Collection<Contract> contractsToExplore) {
		Collection<Contract> toAccept = new ArrayList<Contract>();
		Date begining = new Date();

		//
		assert currentState.isValid();
		assert verification(getMyAgent().getMyCurrentState(), contractsToExplore);

		//Extraction et conversion des contrats de destruction 
		final Collection<Contract> destructionContracts = new ArrayList<Contract>();
		for (final Contract c:contractsToExplore){
			try {
				assert c.getInitialState(c.getResource()).equals(getMyAgent().getMyCurrentState());
			} catch (IncompleteContractException e) {
				throw new RuntimeException();
			}
			if (!c.isMatchingCreation())
				destructionContracts.add(c);
			assert Assert.IIF(destructionContracts.contains(c), !c.isMatchingCreation()):"destruction inco";
		}
		PersonalState freedState = getMyAgent().getMyResultingState(currentState, destructionContracts);
		
		//
		setNewStateToAll(freedState,contractsToExplore);
		assert verification(freedState, contractsToExplore);

		//execution  de ma sélection
		for (int i = 0; i < NegotiationParameters.MixedSelectionHeuristicNumberOfTry; i++){
			toAccept = solve(freedState,contractsToExplore,destructionContracts);
			if (new Date().getTime()-begining.getTime()>this.maxComputingTime)
				break;
			if (!toAccept.isEmpty())
				break;
		}
		
		//
		setNewStateToAll(getMyAgent().getMyCurrentState(),contractsToExplore);
		assert verification(getMyAgent().getMyCurrentState(), contractsToExplore);
		assert verification(getMyAgent().getMyCurrentState(), toAccept);	

		/*rturn*/
		assert getMyAgent().getMyResultingState(currentState, toAccept).isValid();
		assert getMyAgent().IdontCare(currentState,toAccept);
		return toAccept;
	}

	private Collection<Contract> solve(
			PersonalState freedState,
			Collection<Contract> contractsToExplore, 
			Collection<Contract> destructionContracts) {

		//selection des contrats
		Collection<Contract> selected = mainSelectionModule.selection(freedState, contractsToExplore);
		boolean better = getMyAgent().getMyAllocationPreferenceComparator().compare(selected, destructionContracts)>0;


		//reorga des contrats
		final Collection<Contract> toAccept = new ArrayList<Contract>();
		if (better){
			for (Contract c : contractsToExplore){
				if ((selected.contains(c) && !destructionContracts.contains(c)) ||
						(!selected.contains(c) && destructionContracts.contains(c)) )
					toAccept.add(c);
			}
		} else {
			//on accepte rien on fait pas mieu!
		}

		return toAccept;
	}

	private void setNewStateToAll(
			PersonalState myState,
			Collection<Contract> cs){
		for (Contract c : cs){
			assert myState instanceof HostState;
			assert c instanceof MatchingCandidature;
			//
			c.setCreation(!myState.hasResource(c.getAgent()));
			c.setInitialState(myState);
			try {
				c.setInitialState(((ReplicaState) c.getInitialState(c.getAgent())).allocate(
						(HostState) myState,
						myState.hasResource(c.getAgent())));
			} catch (IncompleteContractException e) {
				throw new RuntimeException("impossible");
			}
		}
	}

	private boolean verification(
			PersonalState currentState,
			Collection<Contract> contractsToExplore){
		for (Contract c : contractsToExplore){
			try {
				assert currentState instanceof HostState;
				assert c.getInitialState(c.getResource()).equals(currentState):"wrong inti state";
				assert Assert.IIF(c.isMatchingCreation(), 
						!c.getInitialState(c.getAgent()).hasResource(currentState.getMyAgentIdentifier())):"agent inco";
				assert Assert.IIF(c.isMatchingCreation(), 
						!currentState.hasResource(c.getAgent())):"host inco";
			} catch (IncompleteContractException e) {
				throw new RuntimeException();
			}
		}
		return true;

	}

}
