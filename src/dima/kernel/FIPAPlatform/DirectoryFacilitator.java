package dima.kernel.FIPAPlatform;

/**
 * Insert the type's description here.
 * Creation date: (28/04/02 13:04:14)
 * @author:
 * This class defines one agents of the FIPA architectures
 * This agent has the role of yeloww pages
 * It provides the needed services to register a service
 * or a list of services.
 * And it provided the needed services to answer the request of the
 * agent looking for service providers.
 */

import java.util.HashMap;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.Message;




public class DirectoryFacilitator
extends dima.kernel.communicatingAgent.BasicCommunicatingAgent {
	/**
	 *
	 */
	private static final long serialVersionUID = 6587307498167697294L;
	public HashMap servicesList;
	public static DirectoryFacilitator DIMAdf;
	/**
	 * Broker constructor comment.
	 * @param newId Gdima.basicagentcomponents.AgentIdentifier
	 */
	public DirectoryFacilitator() {
		super(new AgentName("DF"));
		this.servicesList = new HashMap();
		DirectoryFacilitator.DIMAdf = this;
	}
	/**
	 * Broker constructor comment.
	 * @param param Gdima.basicagentcomponents.AgentAddress
	 * @param newId Gdima.basicagentcomponents.AgentIdentifier
	 */
	public DirectoryFacilitator(final AgentIdentifier newId) {
		super(newId);
		this.servicesList = new HashMap();
		DirectoryFacilitator.DIMAdf = this;
	}
	/**
	 * Broker constructor comment.
	 * @param param Gdima.basicagentcomponents.AgentAddress
	 * @param mp java.util.Map
	 */
	public DirectoryFacilitator(final java.util.Map mp) {
		super(mp);
		this.servicesList = new HashMap();
	}
	/**
	 * Broker constructor comment.
	 * @param param Gdima.basicagentcomponents.AgentAddress
	 * @param mp java.util.Map
	 * @param newId Gdima.basicagentcomponents.AgentIdentifier
	 */
	public DirectoryFacilitator(
			final java.util.Map mp,
			final AgentIdentifier newId) {
		super(mp, newId);
		this.servicesList = new HashMap();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (02/05/2002 11:38:39)
	 * @param prod Gdima.kernel.communicatingAgent.examples.marketplace.Product
	 * @param id Gdima.basicagentcomponents.AgentName
	 */
	public void addService(final AgentIdentifier providerId, final Service service) {
		System.out.println("DF registers the Service: " + service.getId() + " provided by: " + providerId);
		if (this.servicesList.containsKey(service.getId())) {
			((Vector) this.servicesList.get(service.getId())).add(providerId);
		} else {
			final Vector v = new Vector();
			v.add(providerId);
			this.servicesList.put(service.getId(), v);
		}
	}



	//System.out.println(" service ajout�");

	/**
	 * Insert the method's description here.
	 * Creation date: (07/07/2002 11:06:30)
	 * @return Gdima.kernel.aFIPAPlatform.DirectoryFacilitator
	 */
	public void addServices(final AgentIdentifier providerId, final Vector services) {
		System.out.println("DF registers the Services: " + services + " provided by: " + providerId);
		for (int i=0; i<services.size(); i++) {
			this.addService(providerId, (Service)services.elementAt(i));
		}
	}

	public static DirectoryFacilitator getDIMAdf() {
		return DirectoryFacilitator.DIMAdf;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28/04/02 18:11:18)
	 * @return java.util.Vector
	 */
	@Override
	public void proactivityInitialize() {
		System.out.println("DF has started its activity");
	}

	public void getServiceProviders(final Service service, final AgentIdentifier requesterId) {
		this.getServiceProviders(service.getId(), requesterId);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (28/04/02 18:11:18)
	 * @return java.util.Vector
	 */
	public void getServiceProviders(final String serviceId, final AgentIdentifier requesterId) {
		System.out.println("The DF is looking for service providers requested by :" + requesterId );
		final Vector providers = new Vector();
		//System.out.println("***** :" + servicesList.get(serviceId) );
		Vector s = new Vector();
		if (this.servicesList.containsKey(serviceId)) {
			s = (Vector) this.servicesList.get(serviceId);
		}
		providers.addAll(s);
		//System.out.println("***** suite:" + requesterId );
		//Message m = new Message("setServiceProviders", serviceId, providers);
		this.sendMessage(requesterId, new Message("setServiceProviders", serviceId, providers));
		//System.out.println("***** suite et suite:"  );
		System.out.println("The service providers :" + providers + "have been sent");

	}

	/**
	 * isActive method comment.
	 */
	@Override
	public boolean competenceIsActive() {
		return true;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/07/2002 11:06:30)
	 * @param newDIMAdf Gdima.kernel.aFIPAPlatform.DirectoryFacilitator
	 */
	static void setDIMAdf(final DirectoryFacilitator newDIMAdf) {
		DirectoryFacilitator.DIMAdf = newDIMAdf;
	}
	/**
	 * step method comment.
	 */


	@Override
	public void step() {
		if (this.hasMail()) {
			System.out.println("DF has a mesage");
		}
		{this.readMailBox();	 this.wwait(3000);
		}
	}

	public static void main(final String args[])
	{final DirectoryFacilitator df = new DirectoryFacilitator();
	df.activate();}

	public static void initDF()
	{final DirectoryFacilitator df = new DirectoryFacilitator();
	df.activate();}

}
