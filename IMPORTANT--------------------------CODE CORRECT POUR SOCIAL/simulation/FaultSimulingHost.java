package negotiation.simulation;

import java.util.Date;
import java.util.Random;

import negotiation.ressourcenegotiation.ResourceIdentifier;
import dima.basicinterfaces.CommunicationComponentInterface;
import dima.introspectionBasedAgent.annotations.StepComposant;
import dima.introspectionBasedAgent.competences.DuplicateCompetenceException;
import dima.introspectionBasedAgent.competences.UnInstanciableCompetenceException;
import dima.introspectionBasedAgent.ontologies.Protocol;
import dima.introspectionBasedAgent.ontologies.FIPAACLOntologie.FipaACLMessage;
import dima.introspectionBasedAgent.ontologies.FIPAACLOntologie.Performative;

public class FaultSimulingHost extends SocialHost{

	public class FaultProtocol extends Protocol {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3482401751712375815L;

		public FaultProtocol(final CommunicationComponentInterface com) {
			super(com);
		}

		final static String hostStatusChangement="I changed my status (faulty/repaired)";
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -7956089145618925273L;

	private final static long maxFaultFrequency = 500;

	private final Double lambdaFault;
	private final Double lambdaRepair;
	private final Date creation = new Date();

	private boolean iMFaulty = false;


	public FaultSimulingHost(final ResourceIdentifier id, final Double procChargeMax,
			final Double memChargeMax, final Double lambdaFault,
			final Double lambdaRepair) throws UnInstanciableCompetenceException, DuplicateCompetenceException {
		super(id, procChargeMax, memChargeMax);
		this.lambdaFault = lambdaFault;
		this.lambdaRepair = lambdaRepair;
	}

	@StepComposant(ticker=FaultSimulingHost.maxFaultFrequency)
	public void handleFault(){
		final Random rand = new Random();
		if (!this.iMFaulty){
			if (rand.nextDouble()>getFaultLaw()){
				this.iMFaulty=true;
				getMyCurrentState().reset();
				final FipaACLMessage information = 
					new FipaACLMessage(Performative.Inform, FaultProtocol.hostStatusChangement, FaultProtocol.class);
				information.setAttachement(this.iMFaulty);
				this.notify(information);
			}
		} else if (rand.nextDouble()>getRepairLaw()){
			this.iMFaulty=false;
			final FipaACLMessage information = 
				new FipaACLMessage(Performative.Inform, FaultProtocol.hostStatusChangement, FaultProtocol.class);
			information.setAttachement(this.iMFaulty);
			this.notify(information);
		}
	}

	/*
	 * 
	 */


}
//Densité:
//	private Double getWeibullLaw(Double t,Double lambda, Double k){
//		return  k/lambda * Math.pow((t-teta)/lambda, k-1) * Math.exp(- Math.pow((t-teta)/lambda, k));
//	}
//
//	private Double getWeibullLaw(Double t,Double lambda, Double k, Double teta){
//		return  k/lambda * Math.pow((t-teta)/lambda, k-1) * Math.exp(- Math.pow((t-teta)/lambda, k));
//	}