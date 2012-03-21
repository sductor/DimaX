package negotiation.negotiationframework.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.support.GimaObject;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractActionSpecification;
import negotiation.negotiationframework.contracts.AbstractContractTransition;

public class GreedySelectionModule<
ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends BasicAgentModule<SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract>>{
	private static final long serialVersionUID = 438513006322441185L;

	public enum GreedySelectionType { Greedy, Random, RooletteWheel};

	GreedySelectionType itType;

	public GreedySelectionModule(
			SimpleNegotiatingAgent<ActionSpec, PersonalState, Contract> ag,
			GreedySelectionType itType) {
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
		switch (itType){ 
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
			Contract currentContract = itContract.next();
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

		final List<Contract> contractsToExplore;
		int count=-1;

		public GreedyIterator(List<Contract> cs){
			contractsToExplore=cs;
			Collections.sort(contractsToExplore, Collections.reverseOrder(getMyAgent().getMyPreferenceComparator()));
		}


		@Override
		public boolean hasNext() {
			return count<contractsToExplore.size();
		}

		@Override
		public Contract next() {
			count++;
			return contractsToExplore.get(count);		
		}

		@Override
		public void remove() {
			contractsToExplore.remove(count);		
		}

	}


	public class RandomIterator  implements Iterator<Contract>, DimaComponentInterface{

		final List<Contract> contractsToExplore;
		int count=-1;

		public RandomIterator(List<Contract> cs){
			contractsToExplore=cs;
			Collections.shuffle(contractsToExplore);
		}


		@Override
		public boolean hasNext() {
			return count<contractsToExplore.size();
		}

		@Override
		public Contract next() {
			count++;
			return contractsToExplore.get(count);		
		}

		@Override
		public void remove() {
			contractsToExplore.remove(count);		
		}

	}


	public class RooletteWheelIterator implements Iterator<Contract>, DimaComponentInterface {

		List<Contract> contracts;
		List<Contract> initContract;

		Random rand = new Random();
		int currentContract=-1;
		int sumPref;

		public RooletteWheelIterator(final List<Contract> contracts) {
			super();
			this.contracts=new ArrayList<Contract>(contracts);
			initContract = contracts;
			this.currentContract=-1;
			
			for (Contract c : contracts)
				sumPref+=getMyAgent().evaluatePreference(c);
		}

		@Override
		public boolean hasNext() {
			return this.contracts.isEmpty();
		}

		@Override
		public Contract next() {

			currentContract = 0;
			final Double boule= sumPref * this.rand.nextDouble();
			Double wheel=getMyAgent().evaluatePreference(contracts.get(currentContract));

			while (boule>wheel) {
				this.currentContract++; 
				wheel+=getMyAgent().evaluatePreference(contracts.get(currentContract));
			}

			//Suppression du contrat
			sumPref-=getMyAgent().evaluatePreference(this.contracts.get(this.currentContract));
			this.contracts.remove(this.currentContract);

			return this.contracts.get(this.currentContract);
		}

		@Override
		public void remove() {
			initContract.remove(currentContract);	
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
