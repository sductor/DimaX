package frameworks.negotiation.negotiationframework.protocoles.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.shells.NotReadyException;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition;
import frameworks.negotiation.negotiationframework.contracts.ContractTrunk;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;
import frameworks.negotiation.negotiationframework.protocoles.AtMostKCandidaturesProposer;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import frameworks.negotiation.negotiationframework.protocoles.status.StatusObservationCompetence.AgentStateStatus;
import frameworks.negotiation.negotiationframework.rationality.AgentState;

public   abstract
class StatusProposerCore<
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends AtMostKCandidaturesProposer<StatusAgent<PersonalState, Contract>, PersonalState, Contract>
implements
ProposerCore<
StatusAgent<PersonalState, Contract>,
PersonalState, Contract> {
	private static final long serialVersionUID = -5315491050460219982L;


	public StatusProposerCore(final int k)
			throws UnrespectedCompetenceSyntaxException {
		super(k);
	}


	@Override
	public Set<Contract> getNextContractsToPropose()
			throws NotReadyException {

		final Set<Contract> candidatures = new HashSet<Contract>();

		if (this.getMyAgent().stateStatusIs(this.getMyAgent().getMyCurrentState(),
				AgentStateStatus.Wastefull)) {
			final List<AgentState> replicas = new ArrayList<AgentState>();
			replicas.addAll(this.getMyAgent().getMyResources());
			Collections.shuffle(replicas);


			PersonalState nextState = this.getMyAgent().getMyCurrentState();


			while (this.getMyAgent().stateStatusIs(nextState, AgentStateStatus.Wastefull)
					&& !replicas.isEmpty()){

				final Contract destructionCandidature =this.constructDestructionCandidature((ResourceIdentifier) replicas.remove(0).getMyAgentIdentifier());

				if (this.getMyAgent().getMyResultingState(nextState,
						destructionCandidature).isValid()
						|| this.getMyAgent().stateStatusIs(
								this.getMyAgent().getMyResultingState(nextState,
										destructionCandidature),
										AgentStateStatus.Fragile)){
					//on ne fait rien et on fait sauter cette candidature de destruction
				} else {

					candidatures.add(destructionCandidature);

					nextState = this.getMyAgent().getMyResultingState(nextState,
							destructionCandidature);


				}
			}

			//			logMonologue("Wastefull!!! Proposing :\n"+candidatures);
			// Application direct des demandes de destruction qui seront
			// directement ex��cuter par les h��tes
			//			for (final ReplicationCandidature c : candidatures)
			//				this.getMyAgent().execute(c);

		} else if (this.getMyAgent().stateStatusIs(this.getMyAgent().getMyCurrentState(),
				AgentStateStatus.Fragile)) {
			candidatures.addAll(super.getNextContractsToPropose());
		}

		return candidatures;
	}


	//	@Override
	//	public Contract constructCandidature(final ResourceIdentifier id) {
	//		return new ReplicationCandidature((ResourceIdentifier) id,this.getMyAgent().getIdentifier(),true,true);
	//
	//	}

	public abstract Contract constructDestructionCandidature(final ResourceIdentifier id);
	//	new DestructionOrder(
	//			(ResourceIdentifier) replicas.remove(0).getMyAgentIdentifier(), this.getMyAgent().getIdentifier(),true);
	//final AgentState host = this.getMyAgent().getMyInformation()
	//	.getInformation(
	//			resourceState,
	//			destructionCandidature.getResource());
	//destructionCandidature.setInitialState(host);


	@Override
	public boolean IWantToNegotiate(
			final ContractTrunk<Contract> contracts) {
		this.getMyAgent().updateThreshold();
		//		System.out.println(super.IWantToNegotiate(s)+" "+this.getStatus(s)+(super.IWantToNegotiate(s)
		//				&& (this.getStatus(s).equals(AgentStateStatus.Fragile) || this
		//						.getStatus(s).equals(AgentStateStatus.Wastefull))));
		final PersonalState s = this.getMyAgent().getMyCurrentState();
		return super.IWantToNegotiate(contracts)
				&& (this.getMyAgent().getStatus(s).equals(AgentStateStatus.Fragile) ||
						this.getMyAgent().getStatus(s).equals(AgentStateStatus.Wastefull));
	}

}