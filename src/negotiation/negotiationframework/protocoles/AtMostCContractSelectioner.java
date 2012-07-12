package negotiation.negotiationframework.protocoles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import negotiation.negotiationframework.NegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.AgentState;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;

public class AtMostCContractSelectioner<
Agent extends NegotiatingAgent<PersonalState, Contract>,
ActionSpec extends AbstractActionSpecif,
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends
BasicAgentCompetence<Agent>
implements SelectionCore<Agent,PersonalState, Contract> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7536780686332687059L;
	final int c;
	final SelectionCore<Agent,PersonalState, Contract>  myCore;

	Random rand = new Random();

	public AtMostCContractSelectioner(final int c,
			final SelectionCore myCore)
					throws UnrespectedCompetenceSyntaxException {
		super();
		this.c = c;
		this.myCore = myCore;
	}

	@Override
	public void setMyAgent(final Agent ag){
		super.setMyAgent(ag);
		this.myCore.setMyAgent(ag);
	}

	@Override
	public void select(final ContractTrunk<Contract> cs,
			final Collection<Contract> toAccept, final Collection<Contract> toReject,
			final Collection<Contract> toPutOnWait) {
		final List<Contract> all = cs.getParticipantOnWaitContracts();
		all.remove(cs.getLockedContracts());
		final int nbContracts = all.size()+this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers().size();
		if (nbContracts>this.c && !all.isEmpty()){
			final Collection<Contract> notAnalysed = new ArrayList<Contract>();
			for (int i = 0; i < nbContracts-this.c; i++){
				if (all.isEmpty()) {
					break;
				}
				final int toRemove =this.rand.nextInt(all.size());
				notAnalysed.add(all.get(toRemove));
				all.remove(toRemove);
			}
			assert !all.isEmpty() || this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers().size()>=this.c;
			this.getMyAgent().getMyProtocol().answerRejected(notAnalysed);
		}

		this.myCore.select(cs, toAccept, toReject, toPutOnWait);

	}





}
