package negotiation.negotiationframework.strategy.exploration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.agent.AgentState;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import negotiation.negotiationframework.interaction.MatchingCandidature;
import negotiation.negotiationframework.strategy.evaluation.AbstractStrategicEvaluationModule;
import negotiation.negotiationframework.strategy.evaluation.BasicPossibilistUtilitaristComparatorModule;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import dima.introspectionbasedagents.coreservices.information.NoInformationAvailableException;

public class BasicGradientSearch
<Action extends AbstractContractTransition<ActionSpec>,
ActionSpec extends AbstractActionSpecification> 
implements AbstractStrategicExplorationModule<AbstractContractTransition<ActionSpec>, ActionSpec>{
	private static final long serialVersionUID = -7844264592171808539L;

	final AbstractContractNeighborhood<Action, ActionSpec> neigborhood;
	
	int iterationNumber;
	int maxIterationNumber=50;
	boolean stop=false;

	public BasicGradientSearch(final AbstractContractNeighborhood<Action,ActionSpec> c) {
		super();
		this.neigborhood = c;
	}


	@Override
	public AllocationTransition<Action, ActionSpec> getNextContractToPropose(
			AbstractStrategicEvaluationModule<AbstractContractTransition<ActionSpec>, ActionSpec> myComparator,
			final Collection<AgentIdentifier> knownAgents, 
			final Collection<String> knownActions)
			throws NoInformationAvailableException {

		//final Set<Contract> tabus = new HashSet<Contract>();

//		ag.logMonologue("Entring getNextContractToPropose, knownActions :"+knownActions);
		Iterator<Action> neighbors;
		Action newMove;
		Action bestMove;
		final Collection<Action> lastBestMoves = new ArrayList<Action>();

		final AllocationTransition<Action, ActionSpec> current = this.neigborhood.getEmptyContract();
		final AllocationTransition<Action, ActionSpec> bestVoisin = this.neigborhood.getEmptyContract();
		final AllocationTransition<Action, ActionSpec> best = this.neigborhood.getEmptyContract();

		this.iterationNumber=0;

		/**
		 * Invariant : current a chaque entrée dans la boucle
		 * newMove est le mouvement courant entre current et neo
		 * bestVoisin est le meilleurs voisin de current
		 * bestMove est le mouvement vers le meilleur voisin de current
		 * best est le meilleur noeud visité jusqu'a maintenant 
		 */
		while (!this.stopCondition()){ //on explore les voisins de current
			//tabus.add(current); Pas besoin car on ne fait que faire grossir les contrats 
//			System.out.println("--------------------------> current explored contract :"+current);
			neighbors = this.neigborhood.getNeighbors(current, knownAgents, knownActions);
			if (neighbors.hasNext()){
				//Initialisation du premier voisin qui est le meilleur
				newMove = neighbors.next();
				bestMove = newMove;
				bestVoisin.add(bestMove);
				//				System.out.println("\nfirst neighbor :"+newMove);

				while (neighbors.hasNext()){ //Pour chaque voisin
					newMove = neighbors.next();
					//					System.out.println("\nnew neighbor :"+newMove);
					current.add(newMove);
					//current devient son voisin

					if (myComparator.strategiclyCompare(current,bestVoisin)>0){
						bestVoisin.remove(bestMove);
						bestMove = newMove;
						bestVoisin.add(bestMove);
					}

					current.remove(newMove);
					//current redevient current
				}
				
				lastBestMoves.add(bestMove);

//				System.out.println("**** best voisin found "+bestVoisin);
				if (myComparator.strategiclyCompare(best, bestVoisin)>0){
//					System.out.println("!!!!!!!!! Is the best node ever");
					//.myUtility.isRational : Je ne suis pas interesse par les contrat qui n'augment pas mon uilité
					for (final Action move : lastBestMoves)
						best.add(move);
					lastBestMoves.clear();
//					System.out.println("!!!!!!!!! Best = "+best);
				} else {
					//on stocke mle chemin dominé
//					System.out.println("!!!!!!!!! Not the best node, lastMove  : "+lastBestMoves);
				}

				current.add(bestMove);//current devient son meilleurs voisin : bestvoisin

				this.iterationNumber++;
			} else {
				System.out.println("No more neighbors! Best "+best);				
				this.stop = true;
			}
		}
		
		return best;
	}

	public boolean stopCondition(){
		return this.maxIterationNumber<this.iterationNumber || this.stop;
	}


}
