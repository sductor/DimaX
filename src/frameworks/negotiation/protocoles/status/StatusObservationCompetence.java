package frameworks.negotiation.protocoles.status;

import java.util.ArrayList;
import java.util.Random;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.PreStepComposant;
import dima.introspectionbasedagents.modules.faults.Assert;
import dima.introspectionbasedagents.services.BasicCommunicatingCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.NoInformationAvailableException;
import frameworks.negotiation.NegotiatingAgent;
import frameworks.negotiation.NegotiationParameters;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.opinion.NoOpinionHandlerException;
import frameworks.negotiation.opinion.OpinionHandler;
import frameworks.negotiation.opinion.OpinionService;
import frameworks.negotiation.opinion.OpinionService.Opinion;
import frameworks.negotiation.opinion.SimpleOpinionService;
import frameworks.negotiation.rationality.AgentState;

public class StatusObservationCompetence<PersonalState extends AgentState>
extends BasicCommunicatingCompetence<NegotiatingAgent<PersonalState,?>>{

	//
	// Subclass
	//

	/**
	 * 
	 */
	private static final long serialVersionUID = 5570880806727642949L;

	public enum AgentStateStatus {
		Fragile, /**/Empty, Thrifty, Full, /**/Wastefull,
	}

	//
	// Fields
	//
	private final boolean centralised;

	//si centralised
	private final AgentIdentifier myLaborantin;

	//sinon
	private final Integer numberTodiffuse;
	private final Random rand;

	private final boolean iDiffuseOriginalState;
	private final Class<? extends AgentState> stateTypeToDiffuse;

	private Float lowerThreshold = Float.NaN;
	private Float higherThreshold = Float.NaN;

	public final double alpha_low;
	public final double alpha_high;
	//
	// Constructor
	//

	public StatusObservationCompetence(
			final int numberTodiffuse,
			final boolean iDiffuseOriginalState,
			final Class<? extends AgentState> stateTypeToDiffuse,
			final double alpha_low, final double alpha_high)
					throws UnrespectedCompetenceSyntaxException {
		this.rand = new Random();
		this.centralised = false;
		this.myLaborantin = null;
		this.numberTodiffuse = numberTodiffuse;
		this.iDiffuseOriginalState=iDiffuseOriginalState;
		this.stateTypeToDiffuse=stateTypeToDiffuse;
		this.alpha_low=alpha_low;
		this.alpha_high=alpha_high;
		//		assert iDiffuseOriginalState?stateTypeToDiffuse.equals(this.getMyAgent().getMyStateType()):true;
	}

	public StatusObservationCompetence(
			final AgentIdentifier myLaborantin,
			final boolean iDiffuseOriginalState,
			final Class<? extends AgentState> stateTypeToDiffuse,
			final double alpha_low, final double alpha_high)
					throws UnrespectedCompetenceSyntaxException {
		this.rand = new Random();
		this.centralised = true;
		this.myLaborantin = myLaborantin;
		this.numberTodiffuse = null;
		this.iDiffuseOriginalState=iDiffuseOriginalState;
		this.stateTypeToDiffuse=stateTypeToDiffuse;
		this.alpha_low=alpha_low;
		this.alpha_high=alpha_high;
	}
	//
	// Accessors
	//

	public OpinionHandler<PersonalState> getMyOpinionHandler(){
		return ((SimpleOpinionService) this.getMyAgent().getMyInformation())
				.getHandler(this.getMyAgent().getMyStateType());
	}

	public boolean stateStatusIs(
			final PersonalState state,
			final AgentStateStatus status) {
		return this.getStatus(state).equals(status);
	}

	public AgentStateStatus getMyStatus() {
		return this.getStatus(this.getMyAgent().getMyCurrentState());
	}

	public AgentStateStatus getStatus(final PersonalState s) {
		this.updateThreshold();
		final boolean empty = this.getMyAgent().getMyCurrentState()
				.getMyResourceIdentifiers().size() <= 1;
		final boolean full = this.getMyAgent().getMyCurrentState()
				.getMyResourceIdentifiers().size() == this.getMyAgent().getMyInformation()
				.getKnownAgents().size();
		final boolean fragile = this.getMyOpinionHandler()
				.getNumericValue(this.getMyAgent().getMyCurrentState()) <= this.getLowerThreshold();
		final boolean wastefull = this.getMyOpinionHandler()
				.getNumericValue(this.getMyAgent().getMyCurrentState()) > this.getHigherThreshold();

				if (wastefull && fragile) {
					throw new RuntimeException(
							"impossible! : " +
									"me: "+this.getMyOpinionHandler().getNumericValue(this.getMyAgent().getMyCurrentState())+
									" low : "+this.getLowerThreshold()
									+" high "+this.getHigherThreshold()+
									(this.getMyOpinionHandler().getNumericValue(this.getMyAgent().getMyCurrentState()) <= this.getLowerThreshold())
									+" "+(this.getMyOpinionHandler().getNumericValue(this.getMyAgent().getMyCurrentState()) > this.getHigherThreshold())
									+wastefull+" "+fragile+" "+(wastefull && fragile));
				} else if (full && !wastefull) {
					return AgentStateStatus.Full;
				} else if (empty && !fragile) {
					return AgentStateStatus.Empty;
				} else if (!wastefull && !fragile) {
					return AgentStateStatus.Thrifty;
				} else if (wastefull && !empty) {
					return AgentStateStatus.Wastefull;
				} else if (fragile && !full) {
					return AgentStateStatus.Fragile;
				} else {
					throw new RuntimeException("impossible!");
				}
	}

	//
	// Behavior
	//

	@PreStepComposant(ticker=NegotiationParameters._timeToCollect)
	void diffuseInfo(){
		assert this.isActive();
		if (this.centralised){
			this.sendInfo(this.myLaborantin);
		} else {
			int numberdiffused=0;
			final ArrayList<AgentIdentifier> allAgents = new ArrayList<AgentIdentifier>(this.getMyAgent().getMyInformation().getKnownAgents());
			allAgents.remove(this.getMyAgent().getIdentifier());
			while (numberdiffused < this.numberTodiffuse && !allAgents.isEmpty()){
				final AgentIdentifier id = allAgents.remove(this.rand.nextInt(allAgents.size()));
				assert Assert.Imply(!(this.getMyAgent().getIdentifier() instanceof ResourceIdentifier),id instanceof ResourceIdentifier):id;
				assert Assert.Imply(this.getMyAgent().getIdentifier() instanceof ResourceIdentifier, id instanceof AgentIdentifier):id;
				this.sendInfo(id);
				numberdiffused++;
			}
		}
	}

	@MessageHandler
	void updateAgent4StatusObservation(final StatusMessage n) {
		this.getMyAgent().getMyInformation().add(n.getTransmittedState());
	}

	//
	// Methods
	//

	public void updateThreshold(){
		try {
			assert this.getMyAgent().getMyInformation().getInformation(this.getMyAgent().getMyStateType()).size()==1;
			assert this.stateTypeToDiffuse.equals(this.getMyAgent().getMyStateType());
			//			if (this.getMyAgent().getMyInformation().getInformation(this.getMyAgent().getMyStateType()).size()==1){
			//				this.lowerThreshold=getMyOpinionHandler()
			//						.getNumericValue(this.getMyAgent().getMyCurrentState());
			//				this.higherThreshold = getMyOpinionHandler()
			//						.getNumericValue(this.getMyAgent().getMyCurrentState());
			//			} else {

			final Opinion<PersonalState> o =
					(Opinion<PersonalState>) ((OpinionService) this.getMyAgent().getMyInformation()).getGlobalOpinion(this.getMyAgent().getMyStateType());

			Double mean, min, max;
			mean = this.getMyOpinionHandler().getNumericValue(o.getMeanInfo());
			min = this.getMyOpinionHandler().getNumericValue(o.getMinInfo());
			max = this.getMyOpinionHandler().getNumericValue(o.getMaxInfo());

			this.lowerThreshold = new Float(this.alpha_low * mean + (1-this.alpha_low) * min);
			this.higherThreshold = new Float(this.alpha_high * mean + (1-this.alpha_high) * max);
			//			}

			assert this.lowerThreshold - this.higherThreshold < 0.000001:"arg";
			if (this.lowerThreshold> this.higherThreshold){
				assert min<=mean && max>=mean:min+" "+mean+" "+max;
				this.lowerThreshold=this.higherThreshold;
			}
		} catch (final Exception e) {
			this.getMyAgent().signalException(
					"impossible on raisonne sur son propre ��tat il doit etre au moins pr��sent!\n"
							+this.getMyAgent().getMyInformation(), e);
			throw new RuntimeException();
		}
	}

	//
	// Primitives
	//

	private void sendInfo(final AgentIdentifier id){
		assert this.iDiffuseOriginalState?this.stateTypeToDiffuse.equals(this.getMyAgent().getMyStateType()):true;
		if (this.iDiffuseOriginalState){
			this.sendMessage(id, new StatusMessage(this.getMyAgent().getMyCurrentState()));
		} else {
			try {
				this.sendMessage(id,
						new StatusMessage(((OpinionService) this.getMyAgent().getMyInformation()).getGlobalOpinion(this.stateTypeToDiffuse)));
			} catch (final NoInformationAvailableException e) {}
			catch (final NoOpinionHandlerException e) {
				throw new RuntimeException("impossible!!!!");
			}
		}
	}

	private Double getLowerThreshold() {
		if (this.lowerThreshold.equals(Double.NaN)) {
			return Double.POSITIVE_INFINITY;
		} else {
			return new Double(this.lowerThreshold);
		}
	}

	private Double getHigherThreshold() {
		if (this.higherThreshold.equals(Double.NaN)) {
			return Double.POSITIVE_INFINITY;
		} else {
			return new Double(this.higherThreshold);
		}
	}

}

//public StatusObservationCompetence(AgentIdentifier myLaborantin)  {
//	this.centralised = true;
//	this.myLaborantin = myLaborantin;
//	this.numberTodiffuse = null;
//}
//
//public StatusObservationCompetence(int numberTodiffuse)
//		throws UnrespectedCompetenceSyntaxException {
//	rand = new Random();
//	this.centralised = false;
//	this.myLaborantin = null;
//	this.numberTodiffuse = numberTodiffuse;
//}
//private final boolean centralised;
//private final AgentIdentifier myLaborantin;//si centralised
//public void diffuse(AgentState s) {
//
//	if (centralised){
//		getMyAgent().sendMessage(myLaborantin, new NotificationMessage(StatusObservationCompetence.statusObservationKey,s));
//	} else {
//		int numberdiffused=0;
//		ArrayList<AgentIdentifier> allAgents = new ArrayList<AgentIdentifier>(getMyAgent().getMyInformation().getKnownAgents());
//		while (numberdiffused < numberTodiffuse && !allAgents.isEmpty()){
//			AgentIdentifier id = allAgents.remove(rand.nextInt(allAgents.size()));
//			assert !(getMyAgent().getIdentifier() instanceof AgentIdentifier) || id instanceof ResourceIdentifier;
//			assert !(getMyAgent().getIdentifier() instanceof ResourceIdentifier) || id instanceof AgentIdentifier;
//
//			getMyAgent().sendMessage(id, new NotificationMessage(StatusObservationCompetence.statusObservationKey,s));
//			numberdiffused++;
//		}
//	}
//}