package negotiation.negotiationframework.interaction.selectioncores;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;

public class GreedyRouletteWheelSelectionCore<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec, 
Contract extends AbstractContractTransition<ActionSpec>>
extends GreedyBasicSelectionCore<ActionSpec, PersonalState, Contract> {


	LinkedList<Double> contractsValues;
	Random rand = new Random();
	int currentContract=-1;

	public GreedyRouletteWheelSelectionCore(boolean fuseInitiatorNparticipant,
			boolean considerOnWait) {
		super(fuseInitiatorNparticipant, considerOnWait);
	}

	@Override
	protected void sortContract(final List<Contract> contracts) {
		contractsValues = new LinkedList<Double>();
		currentContract=-1;

		Collections.sort(
				contracts, 
				Collections.reverseOrder(this.getMyAgent().getMyPreferenceComparator()));

		for (int i = 0; i < contracts.size(); i++){
			if (i==0)
				contractsValues.add(getMyAgent().evaluatePreference(contracts.get(i)));
			else
				contractsValues.add(contractsValues.get(i-1) + getMyAgent().evaluatePreference(contracts.get(i)));
		}
	}

	@Override
	protected Contract popNextContract(final List<Contract> contracts) {
		Contract result;
		if (currentContract==-1)
			result = getNextContract(contracts);
		else
			result = contracts.get(currentContract);		

		//Suppression du contrat
		Double cVal = getMyAgent().evaluatePreference(contracts.get(currentContract));
		contractsValues.remove(currentContract);
		contracts.remove(currentContract);
		for (int j=currentContract; j<contractsValues.size(); j++){
			Double contractj = contractsValues.get(j);
			contractj-=cVal;
		}	

		currentContract=-1;

		return result;
	}

	@Override
	protected Contract getNextContract(final List<Contract> contracts) {
		if (currentContract==-1){
			Double r= contractsValues.getLast() * rand.nextDouble();
			currentContract = 0;
			try {
			while (r>contractsValues.get(currentContract))// && currentContract<contracts.size()-1)
				currentContract++;
			} catch (Exception e) {
				logMonologue(r+" "+contractsValues);
			}
		}

		return contracts.get(currentContract);	
	}
}
