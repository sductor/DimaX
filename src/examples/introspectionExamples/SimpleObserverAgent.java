package examples.introspectionExamples;


import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;


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
			this.logMonologue("observing "+SimpleAgent.getsimpleId(i),LogService.onScreen);
		}return true;
	}

	@MessageHandler
	@NotificationEnvelope()
	public void simpleObserverStep(final NotificationMessage<SimpleMessage> m) {
		SimpleMessage m2 = m.getNotification();
		this.logMonologue(
				"YOOOO!!(1) =) I've received :\n" + m2,LogService.onScreen);
	}

}
