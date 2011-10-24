package dima.introspectionbasedagents.services.core;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class AcquaintanceHandlerCompetence extends HashedHashSet<String, AgentIdentifier>{

	/**
	 *
	 */
	private static final long serialVersionUID = 1358373432236288117L;
	//
	//	// public interface NetworkKey {}
	//
	//	//
	//	// Fields
	//	//
	//
	//	/**
	//	 *
	//	 */
	//	private static final long serialVersionUID = -7563259248613466608L;
	//
	//	//
	//	// Constructor
	//	//
	//
	//	public AcquaintanceHandlerCompetence(final AgentIdentifier identifier) {
	//
	//	}
	//
	//	//
	//	// Accessor
	//	//
	//
	//	//
	//	// Methods
	//	//
	//
	//	/*
	//	 * Network & Broadcast Creation
	//	 */
	//
	//	/**
	//	 * Create a network named name between hosts of hosts. In a network every
	//	 * hosts know each other
	//	 *
	//	 * @param hosts
	//	 * @param name
	//	 */
	//	public void updateNetwork(final Collection<HostIdentifier> hosts,
	//			final String name) {
	//		// for (HostIdentifier manager : hosts){
	//		// Message m = new NetworkCreationMessage(hosts, name);
	//		// m.setReceiver(manager);
	//		// sendMessage(manager,m);
	//		// }
	//	}
	//
	//	/**
	//	 * Send m to all the host of hosts
	//	 *
	//	 * @param hosts
	//	 * @param m
	//	 */
	//	public void diffuse(final String k, final Message m) {
	//		for (final AgentIdentifier id : this.myAcquaintances.get(k)) {
	//			m.setReceiver(id);
	//			this.sendMessage(id, m);
	//		}
	//	}
	//
	//
	//	/******************
	//	 * PROTOCOL
	//	 */
	//	public class AcquaintanceProtocol extends Protocol {
	//
	//		public AcquaintanceProtocol(CommunicationComponentInterface com) {
	//			super(com);
	//			// TODO Auto-generated constructor stub
	//		}
	//
	//		/**
	//		 *
	//		 */
	//		private static final long serialVersionUID = -7263285171666632823L;
	//		public static final String Activate = "Activate Acquaintance";
	//		public static final String UpdateNetwork = "Add/Remove agents to acquaintance network";
	//		public static final String InstanciateNetwork = "Make all the agents of this network knowing each other";
	//		public static final String DiffuseToNetwork = "Diffuse message to acquaintance network";
	//		public static final String GetNetworkMembers = "Get members of acquaintance network";
	//
	//		/**
	//		 * Used to switch on or off the acquaintance service
	//		 *
	//		 * @param activate
	//		 * @return true if success
	//		 */
	//		public Boolean activateAcquaintance(final Boolean activate) {
	//			final MessageForService<Boolean> m = new MessageForService<Boolean>(
	//					Performative.Request, AcquaintanceProtocol.Activate,
	//					AcquaintanceProtocol.class, new Object[] { activate },
	//					new Class[] { Boolean.class });
	//			return this.com.sendMessageToService(m);
	//		}
	//
	//		/**
	//		 * Used to add a new agent in an acquaintance network
	//		 *
	//		 * @param activate
	//		 * @return true if success
	//		 */
	//		public Boolean addNewAcquaintance(final String network,
	//				final AgentIdentifier agent) {
	//			final MessageForService<Boolean> m = new MessageForService<Boolean>(
	//					Performative.Request, AcquaintanceProtocol.UpdateNetwork,
	//					AcquaintanceProtocol.class, new Object[] { network, agent },
	//					new Class[] { String.class, AgentIdentifier.class });
	//			return this.com.sendMessageToService(m);
	//		}
	//
	//		/**
	//		 * Used to add a new agent in an acquaintance network
	//		 *
	//		 * @param activate
	//		 * @return true if success
	//		 */
	//		public Boolean broadcastToNetwork(final String network,
	//				final Message message) {
	//			final MessageForService<Boolean> m = new MessageForService<Boolean>(
	//					Performative.Request, AcquaintanceProtocol.DiffuseToNetwork,
	//					AcquaintanceProtocol.class, new Object[] { network, message },
	//					new Class[] { String.class, Message.class });
	//			return this.com.sendMessageToService(m);
	//		}
	//
	//		/**
	//		 * Used to add a new agent in an acquaintance network
	//		 *
	//		 * @param activate
	//		 * @return true if success
	//		 */
	//		public Collection<AgentIdentifier> getNetworkMember(final String network) {
	//			final MessageForService<Collection<AgentIdentifier>> m = new MessageForService<Collection<AgentIdentifier>>(
	//					Performative.Request, AcquaintanceProtocol.GetNetworkMembers,
	//					AcquaintanceProtocol.class, new Object[] { network },
	//					new Class[] { String.class });
	//			return this.com.sendMessageToService(m);
	//		}
	//
	//		/**
	//		 * Make all the agents of this network knowing each other
	//		 *
	//		 * @param activate
	//		 * @return true if success
	//		 */
	//		public Collection<AgentIdentifier> instanciateNewAcquaintancesNetwork(
	//				final String network,
	//				final Collection<? extends AgentIdentifier> acquaintances) {
	//			final MessageForService<Collection<AgentIdentifier>> m = new MessageForService<Collection<AgentIdentifier>>(
	//					Performative.Request, AcquaintanceProtocol.InstanciateNetwork,
	//					AcquaintanceProtocol.class, new Object[] { network,
	//							acquaintances }, new Class[] { String.class,
	//							Collection.class });
	//			return this.com.sendMessageToService(m);
	//		}
	//	}
	//
	//	@Override
	//	public Class<? extends Annotation> annotationType() {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
}

//
//
// /*
// * Network & Broadcast Use : Messages Handling
// */
//
// @ClassMessageParser()
// public void registratingToNewNetwork(NetworkCreationMessage rm){
// myKnownHostManagers.addAll(rm.getNetwork());
// for (HostIdentifier h : rm.getNetwork())
// myKnownNetwork.add(rm.getNetworkName(), h);
// }
//
//
// @ClassMessageParser()
// public void diffusingToObservedMonitors(AskForDiffusionToHostedMonitorMessage
// rm){
// for (MonitorIdentifier observer : getHostedMonitorIdentifiers())
// sendMessage(observer, rm.getMessage());
// }
//
// /**
// * Send m to all the host of m.hosts
// *
// * @param hosts
// * @param m
// */
// @ClassMessageParser()
// public void diffusingToAllKnownManager(AskForDiffusionToKnownManagerMessage
// m){
// for (HostIdentifier observer : getKnownManagers())
// sendMessage(observer, m.getMessage());
// }
//
// /**
// * Send m to all the host of network named m.name
// *
// * @param hosts
// * @param m
// */
// @ClassMessageParser()
// public void diffusingToNetworkManager(AskForDiffusionToKnownNetworkMessage
// m){
// for (HostIdentifier observer : myKnownNetwork.get(m.getNetworkName()))
// sendMessage(observer, m.getMessage());
// }

//
// Methodes
//
