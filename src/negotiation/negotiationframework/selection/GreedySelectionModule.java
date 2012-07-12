package negotiation.negotiationframework.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import negotiation.negotiationframework.NegotiatingAgent;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.rationality.AgentState;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.services.BasicAgentModule;

public class GreedySelectionModule<
PersonalState extends AgentState,
Contract extends AbstractContractTransition>
extends BasicAgentModule<NegotiatingAgent<PersonalState, Contract>>{
	private static final long serialVersionUID = 438513006322441185L;

	public enum GreedySelectionType { Greedy, Random, RooletteWheel};

	GreedySelectionType itType;

	public GreedySelectionModule(
			final GreedySelectionType itType) {
		super();
		this.itType=itType;
	}

	//
	// Methods
	//

	public Collection<Contract> greedySelection(
			PersonalState currentState,
			final Collection<Contract> contractsToExplore) {
		// logMonologue("!GreedySelection! : myState"+getMyAgent().getMyCurrentState());

		assert this.getMyAgent()!=null;
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
			throw new RuntimeException();
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

		public GreedyIterator(final Collection<Contract> cs){
			assert GreedySelectionModule.this.getMyAgent()!=null;
			this.contractsToExplore=new ArrayList<Contract>(cs);
			Collections.sort(this.contractsToExplore,
					Collections.reverseOrder(
							GreedySelectionModule.this.
							getMyAgent().
							getMyPreferenceComparator()));
		}


		@Override
		public boolean hasNext() {
			return this.count<this.contractsToExplore.size()-1;
		}

		@Override
		public Contract next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			this.count++;
			return this.contractsToExplore.get(this.count);
		}

		@Override
		public void remove() {
			//			this.contractsToExplore.remove(this.count);
			throw new UnsupportedOperationException();
		}

	}


	public class RandomIterator  implements Iterator<Contract>, DimaComponentInterface{

		/**
		 *
		 */
		private static final long serialVersionUID = -194332049051755246L;
		final List<Contract> contractsToExplore;
		int count=-1;

		public RandomIterator(final Collection<Contract> cs){
			this.contractsToExplore=new ArrayList<Contract>(cs);
			Collections.shuffle(this.contractsToExplore);
		}


		@Override
		public boolean hasNext() {
			return this.count<this.contractsToExplore.size()-1;
		}

		@Override
		public Contract next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			this.count++;
			return this.contractsToExplore.get(this.count);
		}

		@Override
		public void remove() {
			//			this.contractsToExplore.remove(this.count);
			throw new UnsupportedOperationException();
		}

	}


	public class RooletteWheelIterator implements Iterator<Contract>, DimaComponentInterface {
		private static final long serialVersionUID = -6677689550030424329L;
		List<Contract> contracts;
		//		Collection<Contract> initContract;//for remove

		Random rand = new Random();
		//		int currentContract=-1;
		double sumPref;

		public RooletteWheelIterator(final Collection<Contract> contracts) {
			super();
			this.contracts=new ArrayList<Contract>(contracts);
			//			this.initContract = contracts;//for remove
			//			this.currentContract=-1;

			for (final Contract c : contracts) {
				this.sumPref+=GreedySelectionModule.this.getMyAgent().evaluatePreference(c);
			}
		}

		@Override
		public boolean hasNext() {
			return !this.contracts.isEmpty();
		}

		@Override
		public Contract next() {

			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}

			Contract c;
			final Double boule= this.sumPref * this.rand.nextDouble();
			assert boule<=this.sumPref;

			//détection du contrat correspondant à boule
			c = this.contracts.get(this.foundContract(boule));

			//Suppression du contrat
			this.sumPref-=GreedySelectionModule.this.getMyAgent().evaluatePreference(c);
			this.contracts.remove(c);

			//return
			return c;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
			//			if (currentContract==-1)
			//				throw new IllegalStateException();
			//			this.initContract.remove(this.currentContract);
			//			currentContract=-1;
		}

		//
		// Primitive
		//

		private int foundContract(final double boule){
			return this.foundContract(0,GreedySelectionModule.this.getMyAgent().evaluatePreference(this.contracts.get(0)), boule);
		}

		private int foundContract(final int currentPos, final double currentSum, final double boule){
			//			assert currentSum<=sumPref:currentPos+" "+currentSum+" "+boule+" "+sumPref+" \n --->"+contracts;
			if (this.contracts.size()==0){//probleme d'arrondi??
				assert currentPos==0;
				return 0;
			}
			if (currentSum>=boule) {
				return currentPos;
			} else if (currentPos==this.contracts.size()-1){//probleme d'arrondi??
				//				logWarning("arrgh "+currentPos+" "+currentSum+" "+sumPref+" "+boule+" \n --->"+contracts);
				return currentPos;
			} else {
				return this.foundContract(
						currentPos+1,
						currentSum+GreedySelectionModule.this.getMyAgent().evaluatePreference(this.contracts.get(currentPos)),
						boule);
			}

		}
	}
}
//Double wheel=GreedySelectionModule.this.getMyAgent().evaluatePreference(this.contracts.get(this.currentContract));
//while (boule>wheel) {
//				this.currentContract++;
//				wheel+=GreedySelectionModule.this.getMyAgent().evaluatePreference(this.contracts.get(this.currentContract));
//				assert wheel<=sumPref;
//			}

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
