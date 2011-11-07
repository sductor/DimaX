package negotiation.negotiationframework.strategy.evaluation;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.library.information.ObservationService.Information;
import dima.introspectionbasedagents.services.library.information.OpinionService.Opinion;
import negotiation.negotiationframework.StrategicNegotiatingAgent;
import negotiation.negotiationframework.agent.AgentState;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.AbstractContractTransition;

public class UtilitaristStrategicModule
<ActionSpec extends AbstractActionSpecification,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends BasicAgentModule<StrategicNegotiatingAgent<ActionSpec, PersonalState, Contract>>
implements AbstractUtilitaristStrategicCore<Contract, ActionSpec>{

	public UtilitaristStrategicModule(
			StrategicNegotiatingAgent<ActionSpec, PersonalState, Contract> ag) {
		super(ag);
	}

	@Override
	public  Float getAgentBelievedStateConfidence(final AgentIdentifier id){
		try {
			Opinion<? extends Information> state = getMyAgent().getMyInformation().getOpinion(getMyInfoType(), id);
			if (!state.isCertain())
				return new Float(1 - state.getOpinionDispersion());
			else {
				Opinion<? extends Information> global = getMyAgent().getMyInformation().getGlobalOpinion(getMyInfoType());
				if (state.getUptime() <= global.getMinInformationDynamicity())
					return new Float(1);
				if (state.getUptime() >= global.getMaxInformationDynamicity())
					//cas jamais utilisé pcq isKnown fait le update
					return new Float(0);
				else
					return new Float(
							(state.getUptime() -
									global.getMaxInformationDynamicity())/
									(global.getMinInformationDynamicity() -
											global.getMaxInformationDynamicity()));
			}
		} catch (Exception e) {
			getMyAgent().signalException("impossible on raisonne sur son propre état il doit etre au moins présent!", e);
			throw new RuntimeException();
		}
	}

	@Override
	public Double getConfidenceOfInformationAbout(final Collection<? extends AgentIdentifier> ids){
		try {
			int nbknown=0;
			Float confTotal=new Float(0);
			for (final AgentIdentifier id : ids){
				confTotal+=1 - getAgentBelievedStateConfidence(id);
				if (getMyAgent().getMyInformation().getOpinion(getMyInfoType(), id).isCertain())
					nbknown++;
			}
			return new Double(confTotal / (ids.size() * nbknown) );
			//		new Double(nbUnknown * getMyAgent().getMyInformation().getSystemDispersion() * ageMoyen)  / (ids.size() * (ids.size() - nbUnknown));

		} catch (Exception e) {
			getMyAgent().signalException("impossible on raisonne sur son propre état il doit etre au moins présent!", e);
			throw new RuntimeException();
		}
	}

	public Double getStateUtility(final PersonalState s1){
		return getMyAgent().evaluatePreference(s1);
	}

	@Override
	public Double evaluateContractPersonalUtility(final Contract c){
		return getStateUtility(getMyAgent().getMyResultingState(getMyAgent().getMyCurrentState(), c));
	}

	@Override
	public boolean iThinkItwillAccept(AgentIdentifier id, Contract c){
		try {
			return getMyAgent().Iaccept((PersonalState)getMyAgent().getMyInformation().getOpinion(getMyInfoType(), id).getRepresentativeElement(),c) ;

		} catch (Exception e) {
			getMyAgent().signalException("impossible on raisonne sur son propre état il doit etre au moins présent!", e);
			throw new RuntimeException();
		}
	}

	@Override
	public boolean iMRiskAdverse() {
		return false;
	}

	@Override
	public Double evaluateContractUtility(AgentIdentifier id, Contract c){
		try {
			return getStateUtility(
					getMyAgent().getMyResultingState((PersonalState)getMyAgent().getMyInformation().getOpinion(getMyInfoType(), id).getRepresentativeElement(), c));

		} catch (Exception e) {
			getMyAgent().signalException("impossible on raisonne sur son propre état il doit etre au moins présent!", e);
			throw new RuntimeException();
		}
	}

	private Class<? extends AgentState> getMyInfoType(){
		return getMyAgent().getMyCurrentState().getClass();
	}
}
