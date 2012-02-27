package negotiation.negotiationframework.selectioncores;

import java.util.Collections;
import java.util.List;

import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;

public class GreedyRandomSelectionCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends GreedyBasicSelectionCore<ActionSpec, PersonalState, Contract>  {



	public GreedyRandomSelectionCore(final boolean fuseInitiatorNparticipant,
			final boolean considerOnWait) {
		super(fuseInitiatorNparticipant, considerOnWait);
	}

	private static final long serialVersionUID = 5087844893878488264L;

	@Override
	protected void sortContract(final List<Contract> contracts) {
		Collections.shuffle(contracts);
	}
}
