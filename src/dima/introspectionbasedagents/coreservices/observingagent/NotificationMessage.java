package dima.introspectionbasedagents.coreservices.observingagent;


import java.io.Serializable;

import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.ontologies.Envelope;
import dima.introspectionbasedagents.ontologies.MessageInEnvelope;

/**
 * Notifications send to the pattern observer must implements this interface.
 * And thus provide a key that will be used when registering observer
 * If no key is seted, the class name is used
 *
 * @author Ductor Sylvain
 */
public class NotificationMessage<Notification extends Serializable>
extends Message implements MessageInEnvelope {

	/**
	 *
	 */
	private static final long serialVersionUID = 5992434425900202153L;
	private final Notification notif;
	private final String key;


	//
	// Constructors
	//

	 NotificationMessage(final String key, final Notification n) {
		super("notification of "+key+", "+n);
		this.notif=n;
		this.key = key;
	}

	//
	// Accessor
	//

	public Notification getNotification(){
		return this.notif;
	}

	public String getKey(){
		return this.key;
	}

	@Override
	public Envelope getMyEnvelope() {
		return new NotificationEnvelopeClass(this.getKey());
	}
}
