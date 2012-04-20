package examples.eAgenda.API;

/**
 * Insert the type's description here.
 * Creation date: (2004-03-02)
 * @author:
 */

import dima.ontologies.basicFIPAACLMessages.FIPAACLMessage;
import examples.eAgenda.data.Agenda;



public  class AgendaAgent4 extends AgendaAgent0{



	/**
	 *
	 */
	private static final long serialVersionUID = 5833215255900359391L;

	public AgendaAgent4(){
		super();

	}

	public AgendaAgent4(final Agenda data){
		super();
		this.myAgenda=data;

	}

	public void processAclMessage(final FIPAACLMessage m) {
		//to be implemented
		System.out.println("PROCESS ACL OBS AGENDAAGENT 4....");
		//  d�but partie notification
		System.out.println("REAC: NOTIFICATION MSG ACL..."+ m.getPerformative()+ "S..."+ m.getSender());//+ "  R...."+m.getReceiver());
		//    ReceiveMessageEvent e= new ReceiveMessageEvent(m);
		//    Message mesEvent = new Message("notifierEventOb", e);//,new  AgentName(((DARXCommunicationComponent)this.getCommunicationComponent()).getTask().getObsName()));
		//    sendMessage(new  AgentName(((DARXCommunicationComponent)getCommunicationComponent()).getTask().getObsName()),mesEvent);
		//    /* */ System.out.println("REAC: NOTIFICATION REUSSIE A OBS:..."+ ((DARXCommunicationComponent)getCommunicationComponent()).getTask().getObsName());

		//fin partie notification
		m.process(this);

	}


}
