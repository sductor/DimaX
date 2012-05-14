package negotiation.negotiationframework.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.rationality.AgentState;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.BasicAgentModule;

public class GreedySelectionModule<
ActionSpec extends AbstractActionSpecif,
PersonalState extends AgentState,
Contract extends AbstractContractTransition<ActionSpec>>
extends BasicAgentModule<SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>>{
	private static final long serialVersionUID = 438513006322441185L;

	public enum GreedySelectionType { Greedy, Random, RooletteWheel};

	GreedySelectionType itType;

	public GreedySelectionModule(
			final SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract> ag,
			final GreedySelectionType itType) {
		super(ag);
		this.itType=itType;
	}

	//
	// Methods
	//

	protected Collection<Contract> greedySelection(
			PersonalState currentState,
			final List<Contract> contractsToExplore) {
		// logMonologue("!GreedySelection! : myState"+getMyAgent().getMyCurrentState());

		final Collection<Contract> toValidate = new ArrayList<Contract>();
		Iterator<Contract> itContract;
		switch (this.itType){
		case Greedy :
			itContract = new GreedyIterator(contractsToExplore);
			break;
		case Random :
			itContract = new RandomIterator(contractsToExplore);
			break;
		case RooletteWheel :
			itContract = new RooletteWheelIterator(contractsToExplore);
			break;
		default :
			itContract=null;
		}

		// logMonologue("analysed contract (1): -->\n"+currentContract+"\n SO? : "
		// +(this.getMyAgent().respectMyRights(currentState) &&
		// this.getMyAgent().Iaccept(currentState, currentContract)));



		while (itContract.hasNext()) {// this.getMyAgent().respectMyRights(currentState)
			// &&
			final Contract currentContract = itContract.next();
			if (this.getMyAgent().Iaccept(currentState,currentContract)){
				toValidate.add(currentContract);
				currentState =
						this.getMyAgent().getMyResultingState(
								currentState,
								currentContract);
			}

			// logMonologue("!GreedySelection! : myState"+getMyAgent().getMyCurrentState()+"\n analysed contract (2): "+currentContract+"\n SO? : "+(this.getMyAgent().respectMyRights(currentState)
			// &&
			// this.getMyAgent().Iaccept(currentState, currentContract)));

			// Verification de la consistance
			if (!currentState.isValid()) {
				throw new RuntimeException(
						"what the  (3)!!!!!!\n accepted state : "
								+ currentState);
			}

		}
		return toValidate;
	}

	//
	// Subclass
	//

	public class GreedyIterator implements Iterator<Contract>, DimaComponentInterface{

		/**
		 *
		 */
		private static final long serialVersionUID = 4199330742934099050L;
		final List<Contract> contractsToExplore;
		int count=-1;

		public GreedyIterator(final List<Contract> cs){
			this.contractsToExplore=cs;
			Collections.sort(this.contractsToExplore, Collections.reverseOrder(GreedySelectionModule.this.getMyAgent().getMyPreferenceComparator()));
		}


		@Override
		public boolean hasNext() {
			return this.count<this.contractsToExplore.size();
		}

		@Override
		public Contract next() {
			this.count++;
			return this.contractsToExplore.get(this.count);
		}

		@Override
		public void remove() {
			this.contractsToExplore.remove(this.count);
		}

	}


	public class RandomIterator  implements Iterator<Contract>, DimaComponentInterface{

		/**
		 *
		 */
		private static final long serialVersionUID = -194332049051755246L;
		final List<Contract> contractsToExplore;
		int count=-1;

		public RandomIterator(final List<Contract> cs){
			this.contractsToExplore=cs;
			Collections.shuffle(this.contractsToExplore);
		}


		@Override
		public boolean hasNext() {
			return this.count<this.contractsToExplore.size();
		}

		@Override
		public Contract next() {
			this.count++;
			return this.contractsToExplore.get(this.count);
		}

		@Override
		public void remove() {
			this.contractsToExplore.remove(this.count);
		}

	}


	public class RooletteWheelIterator implements Iterator<Contract>, DimaComponentInterface {

		/**
		 *
		 */
		private static final long serialVersionUID = -6677689550030424329L;
		List<Contract> contracts;
		List<Contract> initContract;

		Random rand = new Random();
		int currentContract=-1;
		int sumPref;

		public RooletteWheelIterator(final List<Contract> contracts) {
			super();
			this.contracts=new ArrayList<Contract>(contracts);
			this.initContract = contracts;
			this.currentContract=-1;

			for (final Contract c : contracts) {
				this.sumPref+=GreedySelectionModule.this.getMyAgent().evaluatePreference(c);
			}
		}

		@Override
		public boolean hasNext() {
			return this.contracts.isEmpty();
		}

		@Override
		public Contract next() {

			this.currentContract = 0;
			final Double boule= this.sumPref * this.rand.nextDouble();
			Double wheel=GreedySelectionModule.this.getMyAgent().evaluatePreference(this.contracts.get(this.currentContract));

			while (boule>wheel) {
				this.currentContract++;
				wheel+=GreedySelectionModule.this.getMyAgent().evaluatePreference(this.contracts.get(this.currentContract));
			}

			//Suppression du contrat
			this.sumPref-=GreedySelectionModule.this.getMyAgent().evaluatePreference(this.contracts.get(this.currentContract));
			this.contracts.remove(this.currentContract);

			return this.contracts.get(this.currentContract);
		}

		@Override
		public void remove() {
			this.initContract.remove(this.currentContract);
		}
	}
}


//	protected void sortContract(final List<Contract> contracts) {
//		Collections.sort(contracts, this.getMyAgent()
//				.getMyPreferenceComparator());
//	}
//
//	protected Contract popNextContract(final List<Contract> contracts) {
//		return contracts.remove(contracts.size() - 1);
//	}
//
//	protected Contract getNextContract(final List<Contract> contracts) {
//		return contracts.get(contracts.size() - 1);
//	}
// myAgent.logMonologue(
// "my State : "+currentState+
// "\n** i accept "+currentContract+"? "+
// myAgent.Iaccept(currentState, currentContract)
// +"\n my result state  : "+myAgent.getMyResultingState(
// currentState, currentContract));

// if (!this.myAgent.respectMyRights(
// this.myAgent.getMyResultingState(
// this.myAgent.getMyCurrentState(), result.getAcceptedContracts())))
// this.myAgent.logException("ahah!!"+this.myAgent.getMyResultingState(
// this.myAgent.getMyCurrentState(), result.getAcceptedContracts()));
// return result;

// if (!cs.getUnlabelledContracts().isEmpty() &&
// result.getAcceptedContracts().isEmpty() &&
// cs.getAcceptedContracts().isEmpty())
// myAgent.logMonologue("i do not accept any more contract!"+myAgent.getMyCurrentState());

// //Rejection or wl?
// if (cs.getAcceptedContractsIdentifier().isEmpty())
// //Aucune confirmation en attente on annule tout ce qu l'on ne peut supporter
// for (final Contract c : contracts)
// result.addRejected(c);
// else
// //Des confirmations en attente on met les autres en waiting list :
// for (final Contract c : contracts)
// result.addOnWaitingList(c);
// }
