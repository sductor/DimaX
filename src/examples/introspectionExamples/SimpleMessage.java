package examples.introspectionExamples;

import dima.basiccommunicationcomponents.Message;

public class SimpleMessage extends  Message {

	private static final long serialVersionUID = -821093427752977351L;

	int id;
	int tourNb;

	public SimpleMessage() {
		super("simpleMessage");
		this.id = 0;
		this.setType(SimpleMessage.getMyType());
	}

	static String getMyType() {
		return "Simple message type";
	}


	public void incremente() {
		this.id++;
	}
	public void incrementeTour() {
		this.tourNb++;
	}

	@Override
	public String toString(){
		return super.toString()+" "+this.id+" tour: "+this.tourNb;
	}
}