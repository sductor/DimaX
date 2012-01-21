package negotiation.negotiationframework.interaction.selectioncores;

import java.util.Collections;
import java.util.List;

import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;

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
