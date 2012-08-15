package negotiation.negotiationframework.exploration.strategic.evaluation;

import java.util.Collection;

import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import negotiation.negotiationframework.contracts.AbstractContractTransition;
import negotiation.negotiationframework.protocoles.strategic.StrategicNegotiatingAgent;
import negotiation.negotiationframework.rationality.AgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dima.introspectionbasedagents.services.information.OpinionService.Opinion;

public class UtilitaristStrategicModule
<ActionSpec extends AbstractActionSpecif,
PersonalState extends ActionSpec,
Contract extends AbstractContractTransition<ActionSpec>>
extends BasicAgentModule<StrategicNegotiatingAgent<ActionSpec, PersonalState, Contract>>
implements AbstractUtilitaristStrategicCore<Contract, ActionSpec>{

	/**
	 *
	 */
	private static final long serialVersionUID = 7253845916258288308L;

	public UtilitaristStrategicModule(
			final StrategicNegotiatingAgent<ActionSpec, PersonalState, Contract> ag) {
		super(ag);
	}

	@Override
	public  Float getAgentBelievedStateConfidence(final AgentIdentifier id){
		try {
			final Opinion<? extends Information> state = this.getMyAgent().getMyInformation().getOpinion(this.getMyInfoType(), id);
			if (!state.isCertain()) {
				return new Float(1 - state.getOpinionDispersion());
			} else {
				final Opinion<? extends Information> global = this.getMyAgent().getMyInformation().getGlobalOpinion(this.getMyInfoType());
				if (state.getUptime() <= global.getMinInformationDynamicity()) {
					return new Float(1);
				}
				if (state.getUptime() >= global.getMaxInformationDynamicity()) {
					//cas jamais utilisé pcq isKnown fait le update
					return new Float(0);
				} else {
					return new Float(
							(state.getUptime() -
									global.getMaxInformationDynamicity())/
									(global.getMinInformationDynamicity() -
											global.getMaxInformationDynamicity()));
				}
			}
		} catch (final Exception e) {
			this.getMyAgent().signalException("impossible on raisonne sur son propre état il doit etre au moins présent!", e);
			throw new RuntimeException();
		}
	}

	@Override
	public Double getConfidenceOfInformationAbout(final Collection<? extends AgentIdentifier> ids){
		try {
			int nbknown=0;
			Float confTotal=new Float(0);
			for (final AgentIdentifier id : ids){
				confTotal+=1 - this.getAgentBelievedStateConfidence(id);
				if (this.getMyAgent().getMyInformation().getOpinion(this.getMyInfoType(), id).isCertain()) {
					nbknown++;
				}
			}
			return new Double(confTotal / (ids.size() * nbknown) );
			//		new Double(nbUnknown * getMyAgent().getMyInformation().getSystemDispersion() * ageMoyen)  / (ids.size() * (ids.size() - nbUnknown));

		} catch (final Exception e) {
			this.getMyAgent().signalException("impossible on raisonne sur son propre état il doit etre au moins présent!", e);
			throw new RuntimeException();
		}
	}

	public Double getStateUtility(final PersonalState s1){
		return this.getMyAgent().evaluatePreference(s1);
	}

	@Override
	public Double evaluateContractPersonalUtility(final Contract c){
		return this.getStateUtility(this.getMyAgent().getMyResultingState(this.getMyAgent().getMyCurrentState(), c));
	}

	@Override
	public boolean iThinkItwillAccept(final AgentIdentifier id, final Contract c){
		try {
			return this.getMyAgent().Iaccept((PersonalState)this.getMyAgent().getMyInformation().getOpinion(this.getMyInfoType(), id).getRepresentativeElement(),c) ;

		} catch (final Exception e) {
			this.getMyAgent().signalException("impossible on raisonne sur son propre état il doit etre au moins présent!", e);
			throw new RuntimeException();
		}
	}

	@Override
	public boolean iMRiskAdverse() {
		return false;
	}

	@Override
	public Double evaluateContractUtility(final AgentIdentifier id, final Contract c){
		try {
			return this.getStateUtility(
					this.getMyAgent().getMyResultingState((PersonalState)this.getMyAgent().getMyInformation().getOpinion(this.getMyInfoType(), id).getRepresentativeElement(), c));

		} catch (final Exception e) {
			this.getMyAgent().signalException("impossible on raisonne sur son propre état il doit etre au moins présent!", e);
			throw new RuntimeException();
		}
	}

	private Class<? extends AgentState> getMyInfoType(){
		return this.getMyAgent().getMyCurrentState().getClass();
	}
}
