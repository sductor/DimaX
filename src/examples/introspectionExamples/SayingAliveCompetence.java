package examples.introspectionExamples;

import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;

public class SayingAliveCompetence extends BasicAgentCompetence<BasicCompetentAgent> {

//	public SayingAliveCompetence(BasicCompetentAgent ag) {
//		super(ag);
//	}

	@StepComposant(ticker=1000)
	public void sayAlive() {
		this.getMyAgent().logMonologue("I'M STILL ALIVE",LogService.onScreen);
	}

}
