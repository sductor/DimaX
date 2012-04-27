package negotiation.faulttolerance.experimentation;

import java.lang.reflect.Field;
import java.util.Date;

import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dimaxx.experimentation.ExperimentationResults;

public class ReplicationResultHost implements ExperimentationResults {

	/**
	 *
	 */
	private static final long serialVersionUID = 4602509671903952286L;

	private final long creation;
	
	private long firstModifTime=-1;
	private long lastModifTime;

	final ResourceIdentifier id;
	final Double charge;
	int nbOfModif;
	// final Double lambda;

	final boolean isFaulty;
	boolean lastInfo;

	public ReplicationResultHost(final HostState s, long firstModifTime, long lastModifTime,
			final Date agentCreationTime, int initialStateCounter) {
		super();
		this.creation = new Date().getTime() - agentCreationTime.getTime();
		this.charge = s.getMyCharge();
		// this.lambda = s.lambda;
		this.isFaulty = s.isFaulty();
		this.id = s.getMyAgentIdentifier();
		this.lastInfo = false;
		nbOfModif = s.getStateCounter()-initialStateCounter;
		assert !(this.firstModifTime==1) || nbOfModif==0;
		assert !(nbOfModif==0) || (firstModifTime==-1 && lastModifTime==-1):nbOfModif+" "+s.getStateCounter()+" "+initialStateCounter+" "+firstModifTime+" "+lastModifTime;
		assert (nbOfModif==0) || (firstModifTime!=-1 && lastModifTime!=-1):nbOfModif+" "+s.getStateCounter()+" "+initialStateCounter+" "+firstModifTime+" "+lastModifTime;
		this.firstModifTime=firstModifTime;
		this.lastModifTime=lastModifTime;
		
	}

	@Override
	public long getUptime() {
		return this.creation;
	}

	@Override
	public AgentIdentifier getId() {
		return this.id;
	}

	public long getFirstModifTime() {
		return firstModifTime;
	}

	public long getLastModifTime() {
		return lastModifTime;
	}

	public boolean isFaulty() {
		return isFaulty;
	}

	@Override
	public boolean isLastInfo() {
		return this.lastInfo;
	}


	public boolean isHost() {
		return true;
	}

	@Override
	public void setLastInfo() {
		this.lastInfo=true;

	}
	
	@Override
	public String toString(){
		String result ="****************************************************************************************************************************\n";
		result+= "Host result :\n";
		for (final Field f : this.getClass().getDeclaredFields()) {
			try {
				result += f.getName()+" : "+f.get(this)+"\n";
			} catch (final Exception e) {
				LogService.writeException("immmmmmmmpppppppppoooooooossssssssiiiiiiiiiibbbbllllllllllle",e);
			}
		}
		result+="**************";
		return result;
	}
}
