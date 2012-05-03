package negotiation.negotiationframework.protocoles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;

public class AtMostCContractSelectioner<
Agent extends SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>,
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends
BasicAgentCompetence<Agent>
implements SelectionCore<Agent,ActionSpec, PersonalState, Contract> {

	final int c;
	final SelectionCore<Agent,ActionSpec, PersonalState, Contract>  myCore;

	Random rand = new Random();

	public AtMostCContractSelectioner(int c,
			SelectionCore myCore)
					throws UnrespectedCompetenceSyntaxException {
		super();
		this.c = c;
		this.myCore = myCore;
	}

	public void setMyAgent(Agent ag){
		super.setMyAgent(ag);
		myCore.setMyAgent(ag);
	}

	@Override
	public void select(ContractTrunk<Contract, ActionSpec, PersonalState> cs,
			Collection<Contract> toAccept, Collection<Contract> toReject,
			Collection<Contract> toPutOnWait) {
		List<Contract> all = cs.getParticipantOnWaitContracts();
		all.remove(cs.getLockedContracts());
		int nbContracts = all.size()+this.getMyAgent().getMyCurrentState().getMyResourceIdentifiers().size();
		if (nbContracts>c && !all.isEmpty()){			
			Collection<Contract> notAnalysed = new ArrayList<Contract>();
			for (int i = 0; i < nbContracts-c; i++){
				if (all.isEmpty()) break;
				int toRemove =rand.nextInt(all.size());
				notAnalysed.add(all.get(toRemove));
				all.remove(toRemove);
			}
		 assert !all.isEmpty() || getMyAgent().getMyCurrentState().getMyResourceIdentifiers().size()>=c;
			getMyAgent().getMyProtocol().answerRejected(notAnalysed);
		}

		myCore.select(cs, toAccept, toReject, toPutOnWait);

	}





}
