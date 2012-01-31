package negotiation.negotiationframework.interaction.selectioncores;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import negotiation.negotiationframework.interaction.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.contracts.AbstractContractTransition;

public class GreedyRouletteWheelSelectionCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends GreedyBasicSelectionCore<ActionSpec, PersonalState, Contract> {


	/**
	 *
	 */
	private static final long serialVersionUID = 4014431405212667581L;
	LinkedList<Double> contractsValues;
	Random rand = new Random();
	int currentContract=-1;

	public GreedyRouletteWheelSelectionCore(final boolean fuseInitiatorNparticipant,
			final boolean considerOnWait) {
		super(fuseInitiatorNparticipant, considerOnWait);
	}

	@Override
	protected void sortContract(final List<Contract> contracts) {
		this.contractsValues = new LinkedList<Double>();
		this.currentContract=-1;

		Collections.sort(
				contracts,
				Collections.reverseOrder(this.getMyAgent().getMyPreferenceComparator()));

		for (int i = 0; i < contracts.size(); i++)
			if (i==0)
				this.contractsValues.add(this.getMyAgent().evaluatePreference(contracts.get(i)));
			else
				this.contractsValues.add(this.contractsValues.get(i-1) + this.getMyAgent().evaluatePreference(contracts.get(i)));
	}

	@Override
	protected Contract popNextContract(final List<Contract> contracts) {
		Contract result;
		if (this.currentContract==-1)
			result = this.getNextContract(contracts);
		else
			result = contracts.get(this.currentContract);

		//Suppression du contrat
		final Double cVal = this.getMyAgent().evaluatePreference(contracts.get(this.currentContract));
		this.contractsValues.remove(this.currentContract);
		contracts.remove(this.currentContract);
		for (int j=this.currentContract; j<this.contractsValues.size(); j++){
			Double contractj = this.contractsValues.get(j);
			contractj-=cVal;
		}

		this.currentContract=-1;

		return result;
	}

	@Override
	protected Contract getNextContract(final List<Contract> contracts) {
		if (this.currentContract==-1){
			final Double r= this.contractsValues.getLast() * this.rand.nextDouble();
			this.currentContract = 0;
			try {
				while (r>this.contractsValues.get(this.currentContract))// && currentContract<contracts.size()-1)
					this.currentContract++;
			} catch (final Exception e) {
				this.signalException(r+" "+this.contractsValues);
			}
		}

		return contracts.get(this.currentContract);
	}
}
