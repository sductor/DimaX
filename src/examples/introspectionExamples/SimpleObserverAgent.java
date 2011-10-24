package examples.introspectionExamples;


import dima.introspectionbasedagents.BasicCompetentAgent;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.core.observingagent.NotificationEnvelopeClass.NotificationEnvelope;


public class SimpleObserverAgent extends BasicCompetentAgent {

	private static final long serialVersionUID = 450370160682829982L;

	int nbAgent;

	public SimpleObserverAgent(final int nbAgent) throws CompetenceException {
		super("OBSERVER");
		this.nbAgent=nbAgent;
	}

	@StepComposant
	@Transient
	public Boolean register(){
		for (int i=0; i<this.nbAgent; i++){
			this.observe(SimpleAgent.getsimpleId(i), SimpleMessage.class);
			this.logMonologue("observing "+SimpleAgent.getsimpleId(i));
		}return true;
	}

	@MessageHandler
	@NotificationEnvelope()
	public void simpleObserverStep(final NotificationMessage<SimpleMessage> m) {
		this.logMonologue(
				"YOOOO!!(1) =) I've received :\n" + m);
	}

}
