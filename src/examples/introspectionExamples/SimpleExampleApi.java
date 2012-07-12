package examples.introspectionExamples;

import java.util.ArrayList;
import java.util.Collection;

import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.shells.APIAgent;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;

public class SimpleExampleApi extends APIAgent{
	private static final long serialVersionUID = 2197507938900786081L;

	int nbAgent = 2;
	int nbTour = 50;
	Collection<BasicCompetentAgent> agents = new ArrayList<BasicCompetentAgent>();


	public SimpleExampleApi()
			throws CompetenceException {
		super("simple example agent launcher");
		for (int i = 0; i < this.nbAgent; i++) {
			this.agents.add(new SimpleAgent(i, this.nbAgent, this.nbTour));
		}
		this.agents.add(new SimpleObserverAgent(this.nbAgent));
	}

	@ProactivityInitialisation
	public void startAppli() {
		this.launch(this.agents);
		this.startApplication();
	}


	/**
	 * @param args
	 * @throws CompetenceException
	 * @throws MissingCompetenceException
	 */
	public static void main(final String[] args) throws CompetenceException {
		final SimpleExampleApi apiAgent = new SimpleExampleApi();
		apiAgent.initAPI(false);//SCHEDULED
		//		apiAgent.initAPI(true);//FIPA
		//		apiAgent.initAPI(7777,7776);//DARX LOCAL
		//		apiAgent.initAPI("lip6.xml");//DARX Deployed
		apiAgent.launchMySelf();
	}
}