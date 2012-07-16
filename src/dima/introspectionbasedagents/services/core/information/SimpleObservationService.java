package dima.introspectionbasedagents.services.core.information;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;

public class SimpleObservationService extends
BasicAgentCompetence<BasicCompetentAgent> implements
ObservationService {

	//
	// Fields
	//

	/**
	 *
	 */
	private static final long serialVersionUID = -1698180517104590922L;


	private final Set<AgentIdentifier> knownAgents = new HashSet<AgentIdentifier>();
	protected HashMap<Class<? extends Information>, InformationDataBase<? extends Information>> infos =
			new HashMap<Class<? extends Information>, InformationDataBase<? extends Information>>();


	//
	// Accessors
	//

	/*
	 * Acquaintances
	 */

	@Override
	public Set<AgentIdentifier> getKnownAgents() {
		return this.knownAgents;
	}

	@Override
	public void add(final AgentIdentifier agentId) {
		this.knownAgents.add(agentId);
	}

	@Override
	public void addAll(final Collection<? extends AgentIdentifier> agents) {
		this.knownAgents.addAll(agents);
	}

	@Override
	public void remove(final AgentIdentifier agentId) {
		this.knownAgents.remove(agentId);
		this.infos.remove(agentId);
	}

	/*
	 * Information
	 */

	@Override
	public <Info extends Information> boolean hasInformation(
			final Class<Info> informationType) {
		try {
			this.getInformation(informationType);
			return true;
		} catch (final NoInformationAvailableException e) {
			return false;
		}
	}

	@Override
	public <Info extends Information> boolean hasInformation(
			final Class<Info> informationType,
			final AgentIdentifier agentId) {
		try {
			this.getInformation(informationType,agentId);
			return true;
		} catch (final NoInformationAvailableException e) {
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public <Info extends Information> Info getInformation(
			final Class<Info> informationType, final AgentIdentifier agentId)
					throws NoInformationAvailableException {
		if (this.infos.get(informationType)==null) {
			//			System.err.println("classe : "+informationType+" "+infos);
			throw new NoInformationAvailableException();
		}

		final Info result = (Info) this.infos.get(informationType).get(agentId);
		if (result==null) {
			throw new NoInformationAvailableException();
		} else {
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Info extends Information> HashMap<AgentIdentifier, Info> getInformation(
			final Class<Info> informationType) throws NoInformationAvailableException{
		if (this.infos.get(informationType)!=null) {
			return (InformationDataBase<Info>) this.infos.get(informationType);
		} else {
			throw new NoInformationAvailableException();
		}
	}

	@Override
	public void add(final Information information) {
		this.add(information.getMyAgentIdentifier());
		if (!this.infos.containsKey(information.getClass())){//new information type
			this.infos.put(information.getClass(), new InformationDataBase<Information>());
			((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
		} else if (!this.infos.get(information.getClass()).containsKey(information.getMyAgentIdentifier())) {
			((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
		} else { //information replacement
			//			logMonologue("replacing !!!!!!!!!"+this.infos.get(information.getClass()).get(information.getMyAgentIdentifier())+" with "+information);
			final Information knownInfo=this.infos.get(information.getClass()).get(information.getMyAgentIdentifier());

			if (information.isNewerThan(knownInfo)>0) {
				((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
			} else if (information.isNewerThan(knownInfo)<0){
				//				do nothing
			} else if (information.isNewerThan(knownInfo)==0) {
				if (!information.equals(knownInfo)){
					this.logWarning(
							"remplacing an information with a different information of the same time :\n"+information+" and :\n "+
									this.infos.get(information.getClass()).get(information.getMyAgentIdentifier()),
									LogService.onBoth);
					((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
				}else{
					//do nothing
				}
			}
		}

	}



	@Override
	public  void remove(final Information information) {
		if (this.infos.containsKey(information.getClass())) {
			this.infos.get(information.getClass()).remove(
					information.getMyAgentIdentifier());
		}
	}

	@Override
	public <Info extends Information> Info getMyInformation(final Class<Info> informationType) {
		try {
			return this.getInformation(informationType, this.getIdentifier());
		} catch (final NoInformationAvailableException e) {
			LogService.writeException("impossibleddd", e);
			return null;
		}
	}

	@Override
	public <Info extends Information> boolean hasMyInformation(
			final Class<Info> informationType) {
		return this.hasInformation(informationType, this.getIdentifier());
	}

	@Override
	public String show(final Class<? extends Information> infotype){
		if (this.infos.get(infotype)!=null) {
			return this.infos.get(infotype).toString();
		} else {
			return "no info of type "+infotype;
		}
	}

	@Override
	public String toString(){
		return this.infos.toString();
		//		String s = "";
		//		for (Class<? extends Information> infotype : infos.keySet()){
		//			s += "\nINFO OF TYPE "+infotype +" : "+infos.get(infotype).toString();
		//		}
		//		return s;
	}


	//
	// Subclass
	//

	class InformationDataBase<Info extends Information> extends HashMap<AgentIdentifier, Info> {
		private static final long serialVersionUID = -1691723780496506679L;

		public Info add(final Info o) {
			return this.put(o.getMyAgentIdentifier(), o);
		}


		protected Collection<AgentIdentifier> getAgents(){
			return new ArrayList<AgentIdentifier>(this.keySet());
		}


	}

}


//@MessageHandler
//@NotificationEnvelope(SimpleObservationService.informationObservationKey)
//public <Info extends Information> void receiveInformation(
//		final NotificationMessage<Information> o) {
//	this.add(o.getNotification());
//
//}
//public static final String informationObservationKey="informationDiffusion";



//		if (getMyAgent() instanceof SimpleRationalAgent) {
//			SimpleRationalAgent new_name = (SimpleRationalAgent) getMyAgent();
//			if (information.
//					getClass().
//					equals(new_name.
//							myStateType) &&
//							information.getMyAgentIdentifier().
//							equals(getMyAgent().
//									getIdentifier())){
//				logException("yoooooooooooooooooo"+information.toString());
////				throw new RuntimeException();
//				return;
//			}
//
//		}

//		if (!this.infos.containsKey(information.getClass())){//new information type
//			this.infos.put(information.getClass(), new InformationDataBase<Information>());
//			((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
//			this.add(information.getMyAgentIdentifier());
//		} else if (!this.infos.get(information.getClass()).containsKey(information.getMyAgentIdentifier())){
//			((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
//		}else if (information.getCreationTime()>this.infos.get(information.getClass()).get(information.getMyAgentIdentifier()).getCreationTime())
//		 ((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
