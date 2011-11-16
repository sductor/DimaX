package examples.introspectionExamples;

import java.util.ArrayList;
import java.util.Collection;

import dima.introspectionbasedagents.APILauncherModule;
import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.services.CompetenceException;

public class SimpleExampleApi extends APILauncherModule{
	private static final long serialVersionUID = 2197507938900786081L;

	int nbAgent = 5;
	int nbTour = 50;
	final Collection<BasicCompetentAgent> agents = new ArrayList<BasicCompetentAgent>();


	public SimpleExampleApi()
	throws CompetenceException {
		super("simple example agent launcher");
		for (int i = 0; i < this.nbAgent; i++)
			agents.add(new SimpleAgent(i, this.nbAgent, this.nbTour));
		agents.add(new SimpleObserverAgent(this.nbAgent));
	}



	@Override
	public Collection<BasicCompetentAgent> getAgents() {		
		return agents;
	}


	/**
	 * @param args
	 * @throws CompetenceException 
	 * @throws MissingCompetenceException
	 */
	public static void main(final String[] args) throws CompetenceException {
		new SimpleExampleApi().launchWithFipa();
		//		new SimpleExampleApi().launchWithoutThreads(20);
		//		new SimpleExampleApi().launchWithDarx();
	}
}