package negotiation.negotiationframework.strategy;


public class PossibilistSocialGradientSearch
<Charge extends ResourcePart,
Info extends Comparable<Info>> 
extends 
BasicGradientSearch<ResourceIdentifier, SimpleSocialContract<Charge,Info>, ReplicaState>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1612851746563496580L;

//	public PossibilistSocialGradientSearch() {
//		super(
//				new BasicPossibilistStrategicComparatorModule<SocialAllocationContract<Charge,Info>, ReplicaState>(), 
//				new AllocationNeighborhood<SocialAllocationContract<Charge,Info>>(){
//					private static final long serialVersionUID = 5184433818526037415L;
//					@Override
//					public SocialAllocationContract<Charge,Info> getEmptyContract() {
//						return new SocialAllocationContract<Charge,Info>(AllocationNeighborhood.dummyManager);
//					}					
//				});
//	}

	public PossibilistSocialGradientSearch() {
	super(
			
			new AllocationNeighborhood<SimpleSocialContract<Charge,Info>>(){
				private static final long serialVersionUID = 5184433818526037415L;
				@Override
				public SimpleSocialContract<Charge,Info> getEmptyContract() {
					return new SimpleSocialContract<Charge,Info>(AllocationNeighborhood.dummyManager);
				}					
			});
	}
}