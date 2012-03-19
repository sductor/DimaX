package negotiation.negotiationframework.selection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import dima.introspectionbasedagents.services.BasicAgentModule;

public class RooletteWheel<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends BasicAgentModule<SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>> {

	LinkedList<Double> contractsValues;
	Random rand = new Random();
	int currentContract=-1;
	List<Contract> contracts;




	public RooletteWheel(final List<Contract> contracts) {
		super();
		this.contracts=contracts;
		this.sortContract();
	}

	public RooletteWheel(
			final SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract> ag,
			final List<Contract> contracts) {
		super(ag);
		this.contracts=contracts;
		this.sortContract();
	}

	private void sortContract() {
		this.contractsValues = new LinkedList<Double>();
		this.currentContract=-1;

		Collections.sort(
				this.contracts,
				Collections.reverseOrder(this.getMyAgent().getMyPreferenceComparator()));

		for (int i = 0; i < this.contracts.size(); i++)
			if (i==0)
				this.contractsValues.add(this.getMyAgent().evaluatePreference(this.contracts.get(i)));
			else
				this.contractsValues.add(this.contractsValues.get(i-1) + this.getMyAgent().evaluatePreference(this.contracts.get(i)));
	}

	public boolean hasNext(){
		return this.contracts.isEmpty();
	}
	public Contract popNextContract() {
		Contract result;
		if (this.currentContract==-1)
			result = this.getNextContract();
		else
			result = this.contracts.get(this.currentContract);

		//Suppression du contrat
		final Double cVal = this.getMyAgent().evaluatePreference(this.contracts.get(this.currentContract));
		this.contractsValues.remove(this.currentContract);
		this.contracts.remove(this.currentContract);
		for (int j=this.currentContract; j<this.contractsValues.size(); j++){
			Double contractj = this.contractsValues.get(j);
			contractj-=cVal;
		}

		this.currentContract=-1;

		return result;
	}

	public Contract getNextContract() {
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

		return this.contracts.get(this.currentContract);
	}
}
