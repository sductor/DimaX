package negotiation.faulttolerance.faulsimulation;

import java.io.Serializable;
import java.util.Collection;

import negotiation.faulttolerance.faulsimulation.FaultTriggeringService.FaultEvent;
import negotiation.faulttolerance.faulsimulation.FaultTriggeringService.RepairEvent;
import negotiation.negotiationframework.interaction.allocation.ResourceIdentifier;

public interface HostDisponibilityTrunk extends Serializable{

	Double getDisponibility(ResourceIdentifier host);

	Double getDisponibility(Collection<ResourceIdentifier> hosts);
	
	/*
	 * 
	 */

	boolean eventOccur(ResourceIdentifier host, boolean hostFailure);

	void beInformedOfFault(FaultEvent notification);

	void beInformedOfRepair(RepairEvent notification);
}



//
//Collection<ResourceIdentifier> getHosts();
//
//
//Double getLambda(ResourceIdentifier host);


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