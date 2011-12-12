package dima.introspectionbasedagents.services.library.information;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.core.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.core.observingagent.NotificationEnvelopeClass.NotificationEnvelope;

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

	public static final String informationObservationKey="informationDiffusion";

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
	public void add(AgentIdentifier agentId) {
		this.knownAgents.add(agentId);
	}

	@Override
	public void addAll(Collection<? extends AgentIdentifier> agents) {
		this.knownAgents.addAll(agents);
	}

	@Override
	public void remove(AgentIdentifier agentId) {
		this.knownAgents.remove(agentId);
		this.infos.remove(agentId);
	}

	/*
	 * Information
	 */

	@SuppressWarnings("unchecked")
	@Override
	public <Info extends Information> Info getInformation(
			Class<Info> informationType, AgentIdentifier agentId)
					throws NoInformationAvailableException {
		if (this.infos.get(informationType)==null){
			//			System.err.println("classe : "+informationType+" "+infos);
			throw new NoInformationAvailableException();
		}

		Info result = (Info) this.infos.get(informationType).get(agentId);
		if (result==null)
			throw new NoInformationAvailableException();
		else 
			return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Info extends Information> HashMap<AgentIdentifier, Info> getInformation(
			Class<Info> informationType) throws NoInformationAvailableException{
		if (this.infos.get(informationType)!=null)
			return (InformationDataBase<Info>) this.infos.get(informationType);
		else
			throw new NoInformationAvailableException();
	}

	@Override
	public void add(Information information) {

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

		if (!this.infos.containsKey(information.getClass())){//new information type
			this.infos.put(information.getClass(), new InformationDataBase<Information>());
			((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
			this.add(information.getMyAgentIdentifier());
		} else if (!this.infos.get(information.getClass()).containsKey(information.getMyAgentIdentifier())) { //first info for the agent
			((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
		} else if (information.getCreationTime()>this.infos.get(information.getClass()).get(information.getMyAgentIdentifier()).getCreationTime()){ //information replacement//la nouvelle est plus récente que l'ancienne
			((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information); 
		}
		//			else if (this.infos.get(information.getClass()).containsKey(information.getMyAgentIdentifier())) {//information replacement
		//				//			logMonologue("replacing !!!!!!!!!"+this.infos.get(information.getClass()).get(information.getMyAgentIdentifier())+" with "+information);
		//				if (information.getCreationTime()>this.infos.get(information.getClass()).get(information.getMyAgentIdentifier()).getCreationTime())
		//					((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
		//				else
		//					((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
		//			} else //first info for the agent
		//				((InformationDataBase<Information>)this.infos.get(information.getClass())).add(information);
	}


	@Override
	public  void remove(Information information) {
		if (this.infos.containsKey(information.getClass()))
			this.infos.get(information.getClass()).remove(
					information.getMyAgentIdentifier());
	}

	@Override
	public <Info extends Information> Info getMyInformation(Class<Info> informationType) {
		try {
			return this.getInformation(informationType, this.getIdentifier());
		} catch (final NoInformationAvailableException e) {
			LogService.writeException("impossibleddd", e);
			return null;
		}
	}

	@Override
	public String show(Class<? extends Information> infotype){
		if (infos.get(infotype)!=null)
			return infos.get(infotype).toString();
		else 
			return "no info of type "+infotype;
	}

	@Override
	public String toString(){
		return infos.toString();
		//		String s = "";
		//		for (Class<? extends Information> infotype : infos.keySet()){
		//			s += "\nINFO OF TYPE "+infotype +" : "+infos.get(infotype).toString();
		//		}
		//		return s;
	}	


	@MessageHandler
	@NotificationEnvelope(informationObservationKey)
	public <Info extends Information> void receiveInformation(
			NotificationMessage<Information> o) {
		this.add(o.getNotification());
	}

	//
	// Subclass
	//

	class InformationDataBase<Info extends Information> extends HashMap<AgentIdentifier, Info> {
		private static final long serialVersionUID = -1691723780496506679L;

		public Info add(Info o) {
			return this.put(o.getMyAgentIdentifier(), o);
		}	


		protected Collection<AgentIdentifier> getAgents(){
			return new ArrayList<AgentIdentifier>(this.keySet());
		}


	}
}
