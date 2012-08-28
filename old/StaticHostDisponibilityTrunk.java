package negotiation.faulttolerance.faulsimulation;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import negotiation.faulttolerance.faulsimulation.FaultTriggeringService.FaultEvent;
import negotiation.faulttolerance.faulsimulation.FaultTriggeringService.RepairEvent;
import negotiation.negotiationframework.interaction.allocation.ResourceIdentifier;

public class StaticHostDisponibilityTrunk extends PersonalHostDisponibilityTrunk{

	/**
	 *
	 */
	private static final long serialVersionUID = 1444858209007788890L;
	private static final  HashMap<ResourceIdentifier, Double> lambdaValues =
		new HashMap<ResourceIdentifier, Double>();
	private static final  HashMap<ResourceIdentifier, Long> creationTimes =
		new HashMap<ResourceIdentifier, Long>();

	/*
	 *
	 */

	@Override
	public void add(final ResourceIdentifier h, final Double lambda, final Long creationTime){
		StaticHostDisponibilityTrunk.lambdaValues.put(h, lambda);
		StaticHostDisponibilityTrunk.creationTimes.put(h, creationTime);
	}

	@Override
	protected void resetUptime(final ResourceIdentifier h){
		StaticHostDisponibilityTrunk.creationTimes.put(h, new Date().getTime());
	}

	@Override
	public Collection<ResourceIdentifier> getHosts() {
		return StaticHostDisponibilityTrunk.lambdaValues.keySet();
	}

	@Override
	public Double getLambda(final ResourceIdentifier h) {
		return StaticHostDisponibilityTrunk.lambdaValues.get(h);
	}

	@Override
	protected Long getCreationtime(final ResourceIdentifier h) {
		return StaticHostDisponibilityTrunk.creationTimes.get(h);
	}

	/*
	 *
	 */


	@Override
	public void beInformedOfFault(final FaultEvent fault) {
		//do nothing
	}

	@Override
	public void beInformedOfRepair(final RepairEvent repair) {
		//do nothing
	}

	@Override
	public String toString(){
		return lambdaValues.toString();
	}
}





//public void set(final ResourceIdentifier h, final Double lambda){
//	this.lambdaValues.put(h, lambda);
//}

//public void remove(final ResourceIdentifier h){
//	creationTimes.remove(h);
//	lambdaValues.remove(h);
//}
//
//
//
	//
	//
	//

	//
	//
	//
//
//
//	public  void setMyDisponibility(final ResourceIdentifier host,final double dispo) {
//		lambdaValues.put(host, 1 - dispo);
//	}