package negotiation.negotiationframework.strategy.evaluation;

import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.coreservices.information.NoInformationAvailableException;

public class BasicPossibilistUtilitaristComparatorModule
<Contract extends AbstractContractTransition<ActionSpec>, 
ActionSpec extends AbstractActionSpecification> 
implements AbstractStrategicEvaluationModule<Contract, ActionSpec>{
	private static final long serialVersionUID = 3571150755873496299L;
	
	//
	// Fields
	//

	final AbstractUtilitaristStrategicCore<Contract,?>  utilitaristCore;
	
	//
	// Constructor
	//
	
	public BasicPossibilistUtilitaristComparatorModule(
			AbstractUtilitaristStrategicCore<Contract, ?> utilitaristCore) {
		super();
		this.utilitaristCore = utilitaristCore;
	}
	
	//
	//  Methods
	//

	@Override
	public int strategiclyCompare(final Contract c1, final Contract c2)  throws NoInformationAvailableException{
		return utilitaristCore.iMRiskAdverse()?Double.compare(this.uMoins(c1), this.uMoins(c2)):Double.compare(this.uPlus(c1), this.uPlus(c2));
	}

	//
	// Primitives
	//

	protected boolean societyWillAccept(final Contract c) throws NoInformationAvailableException{
		for (final AgentIdentifier id : c.getAllParticipants())
			if (!utilitaristCore.iThinkItwillAccept(id, c))
				return false;
		return true;
	}

	/** societyWillAccept?
	 * 		piOK = 1
	 * 		piNon = confiance dans les infos des agents ET gain pour chaque agent (l'agent le selectionnera)
	 * :
	 * 		piOK = confiance dans les infos des agents ET gain pour chaque agent (l'agent le selectionnera)
	 * 		piNon = 1 		
	 **/
	protected Double getAcceptationConfidence(final Contract c) throws NoInformationAvailableException{
		final Double result = utilitaristCore.getConfidenceOfInformationAbout(c.getAllParticipants());
		for (final AgentIdentifier id : c.getAllParticipants())
			Math.min(result, utilitaristCore.evaluateContractUtility(id, c));
		return result;
	}


	/*
	 * 
	 */

	/**
	 * uOK = utilité du contrat pour l'agent
	 * uNon= 0
	 *  societyWillAccept?
	 * 		piOK = 1
	 * 		piNon = getAcceptationConfidence
	 * :
	 * 		piOK = getAcceptationConfidence
	 * 		piNon = 1 
	 * return Math.max(Math.min(piOK, uOK), Math.min(piNon, uNon));
	 **/
	protected Double uPlus(final Contract c) throws NoInformationAvailableException{

		final Double uOK = utilitaristCore.evaluateContractPersonalUtility(c);
		final Double uNon =  new Double(0);

		final Double piOK, piNon;

		if (this.societyWillAccept(c)){
			piOK =  new Double(1);
			piNon = this.getAcceptationConfidence(c);
		} else {
			piOK = this.getAcceptationConfidence(c);
			piNon = new Double(1);	
		}	

		return Math.max(Math.min(piOK, uOK), Math.min(piNon, uNon));		
	}

	/**
	 * uOK = utilité du contrat pour l'agent
	 * uNon= 0
	 *  societyWillAccept?
	 * 		piOK = 1
	 * 		piNon = getAcceptationConfidence
	 * :
	 * 		piOK = getAcceptationConfidence
	 * 		piNon = 1 
	 * return Math.min(Math.max(1 - piOK, uOK), Math.max(1 - piNon, uNon));
	 **/
	protected Double uMoins(final Contract c) throws NoInformationAvailableException{

		final Double uOK = utilitaristCore.evaluateContractPersonalUtility(c);
		final Double uNon = new Double(0);

		final Double piOK, piNon;

		if (this.societyWillAccept(c)){
			piOK = new Double(1);
			piNon = this.getAcceptationConfidence(c);
		} else {
			piOK = this.getAcceptationConfidence(c);
			piNon =new Double(1);		
		}	

		return Math.min(Math.max(1 - piOK, uOK), Math.max(1 - piNon, uNon));	
	}

	protected Double uPlusOpt(final Contract c) throws NoInformationAvailableException{
		Double piOK;
		final Double uOK = utilitaristCore.evaluateContractPersonalUtility(c);

		if (this.societyWillAccept(c))
			piOK = new Double(1);
		else
			piOK = this.getAcceptationConfidence(c);

		return Math.min(piOK, uOK); //équivalent car uNON = 0;
	}

	protected Double uMoinsOpt(final Contract c) throws NoInformationAvailableException{
		Double piNon;
		final Double uOK = utilitaristCore.evaluateContractPersonalUtility(c);
		new Double(0);

		if (this.societyWillAccept(c)){
			piNon = this.getAcceptationConfidence(c);
			return Math.min(uOK,1 - piNon);//équivalent car 1 - piOK = 0 et uNon = O			
		} else 			
			return new Double(0);//équivalent car 1 - piNon = 0 et uNon = O
	}
}





//package negotiation.framework.strategicproposer.preferencesunderrisk;
//
//import java.util.Iterator;
//
//import negotiation.framework.information.BeliefHandler;
//import negotiation.framework.interactionprotocols.NegotiatedContract;
//import negotiation.framework.rationalagent.AgentState;
//import dima.basicagentcomponents.AgentIdentifier;
//
//public abstract class OrderedStrategicComparator<State extends AgentState, Contract extends NegotiatedContract<?,?>> 
//extends BeliefHandler<State, Contract, State> implements StrategicComparator<State, Contract> {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = -4464395794997957937L;
//	PreferenceBasedAgent<State> myAgent;
//
//	public <Agent extends BasicMyopicAgent<State, Contract> & PreferenceBasedAgent<State> > OrderedStrategicComparator(final Agent ag) {
//		super(ag);
//		this.myAgent = ag;
//	}
//
//	//
//	//
//	//
//
//	@Override
//	public BasicMyopicAgent<State, Contract> getMyAgent(){
//		return (BasicMyopicAgent<State, Contract>) super.getMyAgent();
//	}
//
//	@Override
//	public int compareAcceptationConfidence(final Contract c1, final Contract c2) {
//		getSystemAverageState();
//		//		if (willAccept(gb.getMyAgentIdentifier(), gb c))
//		int nbGlobInfo1=0, nbGlobInfo2=0;
//		for (final AgentIdentifier id : c1.getAgentsToContact())
//			if (!isKnown(id))
//				nbGlobInfo1++;
//		for (final AgentIdentifier id : c2.getAgentsToContact())
//			if (!isKnown(id))
//				nbGlobInfo2++;
//		nbGlobInfo1/=c1.getAgentsToContact().size();
//		nbGlobInfo1/=c2.getAgentsToContact().size();
//		//On compare par pourcentage d'information globale
//		if (nbGlobInfo1<nbGlobInfo2)
//			return 1;
//		else if  (nbGlobInfo2<nbGlobInfo1)
//			return -1;
//		else {
//			//En cas d'égalité on compare l'info la moins sure (lexicographique):
//			Float minInfo1=new Float(0), minInfo2=new Float(0);
//			final Iterator<? extends AgentIdentifier> infos1It = c1.getAgentsToContact().iterator();
//			if (infos1It.hasNext())
//				minInfo1=getAgentStateConfidence(infos1It.next());
//			while (infos1It.hasNext())
//				minInfo1=Math.max(minInfo1, getAgentStateConfidence(infos1It.next()));
//			final Iterator<? extends AgentIdentifier> infos2It = c2.getAgentsToContact().iterator();
//			if (infos2It.hasNext())
//				minInfo2=getAgentStateConfidence(infos2It.next());
//			while (infos2It.hasNext())
//				minInfo2=Math.max(minInfo2, getAgentStateConfidence(infos2It.next()));
//			return minInfo1.compareTo(minInfo2);
//
//		}
//	}
//
//	@Override
//	public int compareContractUtility(final Contract c1, final Contract c2) {
//		return this.myAgent.getMyPreference(
//				getMyAgent().predictState(getMyAgent().getMyCurrentState(), c1),
//				getMyAgent().predictState(getMyAgent().getMyCurrentState(), c2));
//	}
//}
