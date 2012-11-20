package frameworks.negotiation.protocoles.status;

import java.util.ArrayList;
import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.BasicCommunicatingCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import frameworks.negotiation.NegotiationParameters;
import frameworks.negotiation.opinion.Believer;
import frameworks.negotiation.opinion.NoOpinionHandlerException;
import frameworks.negotiation.opinion.OpinionService.Opinion;
import frameworks.negotiation.rationality.AgentState;

public class CentralisedStatusCompterCompetence extends BasicCommunicatingCompetence<Believer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6277641207102827627L;
	private final Collection<AgentIdentifier> acquaintances = new ArrayList<AgentIdentifier>();
	private final Class<? extends AgentState> stateTypeToDiffuse;
	private final Long simulationTime;

	public CentralisedStatusCompterCompetence(
			final Class<? extends AgentState> stateTypeToDiffuse,
			final Long maxSimulationTime)
					throws UnrespectedCompetenceSyntaxException {
		super();
		this.stateTypeToDiffuse = stateTypeToDiffuse;
		this.simulationTime=maxSimulationTime;

	}


	@MessageHandler
	void updateAgent4StatusObservation(final StatusMessage n) {
		this.getMyAgent().getMyOpinion().add(n.getTransmittedState());
	}

	@Transient@StepComposant(ticker=NegotiationParameters.opinionDiffusionFrequency)
	boolean diffuseInfo(){
		try {
			final Opinion o = this.getMyAgent().getMyOpinion().getGlobalOpinion(this.stateTypeToDiffuse);
			this.sendMessage(this.acquaintances,
					new StatusMessage(o));
			//		logMonologue(o.getNumberOfAggregatedElements()+" "
			//		+o.getAggregatedAgents()+"\n"+o.getMaxElement()+"\n"+o.getMinElement()+"\n"+o.getRepresentativeElement());
		} catch (final NoInformationAvailableException e) {}
		catch (final NoOpinionHandlerException e) {
			throw new RuntimeException("impossible!!!!");
		}
		return this.getMyAgent().getUptime()>this.simulationTime;
	}

	public Collection<AgentIdentifier> getAcquaintances() {
		return this.acquaintances;
	}

	public boolean addAcquaintance(final AgentIdentifier id) {
		return this.acquaintances.add(id);
	}

	public boolean removeAcquaintance(final AgentIdentifier id) {
		return this.acquaintances.remove(id);
	}
}
