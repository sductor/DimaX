package examples.eAgenda.API;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;
import java.util.Vector;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.Message;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
import dima.ontologies.basicFIPAACLMessages.FIPAACLMessage;
import examples.eAgenda.data.Agenda;
import examples.eAgenda.data.Contact;
import examples.eAgenda.data.Meeting;
import examples.eAgenda.data.TimeSlot;
import examples.eAgenda.mas.PlannedAction;
import examples.eAgenda.mas.TransactionData;
import examples.eAgenda.mas.TransactionID;

/** Classe d agent principal en charge de l agenda
 */
public class AgendaAgent0 extends BasicCommunicatingAgent implements java.awt.event.ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -5695564448306000650L;
	Agenda myAgenda;
	Vector list_time=new Vector();
	Vector list_timer=new Vector();
	Vector list_participants=new Vector();
	Vector buf=new Vector();

	///////////// This part corresponds to initiator

	public Stack commencedPanification = new Stack();
	ArrayList meetingToBePlanned = new ArrayList();
	ArrayList actionToDo = new ArrayList();
	ArrayList history= new ArrayList();
	boolean commencedPlanification=false;
	boolean premier=true;
	byte[][] awaitingAvailability;
	Integer splitNecessary=new Integer(0);
	Integer nbParticipant=new Integer(0); // Nb of meeting participants for the currently planned meeting
	ArrayList possibleSlot = new ArrayList();
	Integer offset= new Integer(0);
	long debutM=0;
	protected Boolean allNecessaryHere = new Boolean(false);
	public static final int WAIT_OTHER_AGENT_TIMEOUT = 100000; // avant 10000	Bon 09 01 On attends 10 seconds au plus les agents pas necessaire une fois qu on a tout les autres

	///////////// La partie donneur d information (ceux qui participue au meeting)

	public Stack commencedTransaction = new Stack();
	public ArrayList waitingTransaction = new ArrayList();

	// Test de communication point a point: question - reponse

	protected Object[] theAnswer;
	protected final int timeOutForAnswer = 2000; // 0602 50000 0402 10000

	// partie pour n�go sur le plan � planfier


	public AgendaAgent0(final Agenda data) {
		super();
		this.myAgenda = data;
	}

	public AgendaAgent0(){
		super();
	}
	/** An agent is ready for a transaction and acknowledge this way to the initiator agent (this method owner) */
	public synchronized void acceptTransaction(final TransactionID tid, final byte[] availabilities, final Integer identifierInTransaction, final String sender,final Meeting m) {
		/* */ System.out.println("ENTRER DANS ACCEPT TRANSACTION..."+this.getId().toString()+ "..."+tid. getMyValue());
		/* */ System.out.println("VALEUR DE ALLNECESSARY HERE....."+this.allNecessaryHere.booleanValue());
		// contr�ler la redondance mais comment

		if (this.isCurrentPlanification(tid))
		{
			this.awaitingAvailability[identifierInTransaction.intValue()] = availabilities;
			/* */ System.out.println("STEP 1");

			if ( !this.allNecessaryHere.booleanValue()) {
				/* */ System.out.println("JE SUIS L'INITIATEUR DU MEETING...."+tid.getMyValue());
				/* */ System.out.println("FIXER UN TIME OUT NECESS ET OPT");
				this.allNecessaryHere=new Boolean(true);
				System.out.println("Adding a planned action for PlanMeetingPart2");
				this.actionToDo.add(new PlannedAction(System.currentTimeMillis()+AgendaAgent0.WAIT_OTHER_AGENT_TIMEOUT, PlannedAction.planMeetingPart2, tid.getMyValue()));

			}


		}

		/* */ System.out.println("VALEUR DE ALLNECESSARY HERE....."+this.allNecessaryHere.booleanValue());
		/* */ System.out.println("SORTIE DE ACCEPT TRANSACTION.."+this.getId().toString()+"..."+tid. getMyValue());
	}
	private synchronized void acknowledgeTransaction(final TransactionData data) {

		boolean b=false;
		// reply to the caller
		if (!this.commencedTransaction.empty())
			b=((TransactionData)this.commencedTransaction.peek()).TID.getMyValue()==data.TID.getMyValue();

		if (b)	/* */ System.out.println("DUPLICAT ACKNOWLEDGE  "+data.TID.getMyValue());


		else {

			final Serializable[] args = new Serializable[5];
			args[0] = data.TID;
			args[1] = this.myAgenda.getNonImpossibleTimeFromNowUntil(data.goal.getLimitDay(), data.goal.getStartLimit());
			args[2] = data.identifierWithinTransaction;
			args[3] = this.getId().toString();
			args[4]= data.goal;

			final FIPAACLMessage mes = new FIPAACLMessage("acceptTransaction",args, data.initiator.toString());
			mes.setPerformative("Propose");
			mes.setConversationId("conv"+ new Long(data.TID.getMyValue()).toString());
			this.sendMessage(data.initiator, mes);
			this.commencedTransaction.push(data);
			/* */ this.afficherContComTrans();
			System.out.println(" APRES PUSH ACKNOWLEDGE DE...."+data.TID.getMyValue());

		}

	}
	/** Funtion called by the timer when the timout occurs */
	@Override
	public synchronized void actionPerformed(final java.awt.event.ActionEvent evt) {
		System.out.println("TIMEOUT "+evt+" "+evt.getActionCommand());

	}
	public void answer(final Serializable[] answers) {
		this.theAnswer = answers;
	}
	protected Object ask(final AgentIdentifier to, final FIPAACLMessage m) {
		// Send the request
		this.sendMessage(to, m);

		// Wait until it comes
		this.theAnswer = null;
		final long limit = System.currentTimeMillis()+this.timeOutForAnswer;
		while (System.currentTimeMillis()<limit && this.theAnswer==null)
		{
			try
			{
				this.readMailBox(); // This read mail box can cause errors : Does it try to read only if there is a message in the the box (cause, NoSuchElementExeception occurs when trying to removing it) : Check in DIMA
			}
			catch (final Exception ex)
			{
				System.err.println("ReadMail in AgendaAgent.ask creates exception ");
				ex.printStackTrace(System.err);
				return null;
			}

			try
			{
				// Pitier, qu on est le temps de faire autre chose
				// Thread.currentThread().sleep(50);
				Thread.sleep(50);
			}
			catch (final Exception ex) { }
		}

		// Return the result
		if (this.theAnswer == null)
			return null;
		else
			return this.theAnswer[0];
	}
	private double calculateCostFor(final TimeSlot t) {
		// Y a du boulot a faire la
		// ...
		return 0.0;
	}

	public void noAction(final TransactionID TID){
		/* */ System.out.println(" ENTRER NOACTION FIN NEGOCIATION..."+ TID.getMyValue()+"..."+this.getId().toString());
		if (!this.commencedTransaction.empty()&& !this.commencedPanification.empty()){
			final TransactionData data=(TransactionData)this.commencedTransaction.peek();

			/* */ System.out.println("NOACTION: COURANTE ET A TESTER  "+ data.TID.getMyValue() + "..."+ TID.getMyValue());
			if (data.TID.egale(TID)){

				/* */ System.out.println("ENVOI MSG REUSSI A SIMULATEUR....");
				final Serializable []args= new Serializable[2];
				args[0]=this.getId().toString();
				if (this.debutM!=0)
					args[1]= new Long(System.currentTimeMillis() - this.debutM);
				final Message mes= new Message("inc",args);//,new AgentName("AgentSimulateur"));
				this.sendMessage(new AgentName("AgentSimulateur"),mes);


				this.commencedPanification.pop();
				this.commencedTransaction.pop();
				boolean t=false;
				while (!this.commencedTransaction.empty() && !t) {

					t= ((TransactionData)this.commencedTransaction.peek()).TID.getMyValue()!=TID.getMyValue();
					if (!t) this.commencedTransaction.pop();

				}


				/* */ this.afficherContComTrans();
				System.out.println(" APRES POP NO ACTION..."+ TID.getMyValue()+"..."+this.getId().toString());

				// sauvegarde historique

				this.history.add(new Long(TID.getMyValue()));

			}// else ignore redondance
		}

		if (this.commencedTransaction.empty())
			if (this.waitingTransaction.size() != 0)
			{
				/* */ System.out.println("DEPILER LES TRANSACTIONS EN ATTENTE COTE INITIATEUR"+ ((TransactionData)this.waitingTransaction.get(0)).TID.getMyValue());
				final TransactionData d= (TransactionData)this.waitingTransaction.get(0);
				this.startTransaction((TransactionData)this.waitingTransaction.get(0));

			} else /* */ System.out.println("AUCUNE TRANSACTION EN ATTENTE");
		/* */ System.out.println("SORTIE NOACTION..."+TID.getMyValue()+"..."+this.getId().toString());


	}

	public void noActionFailure(final TransactionID TID){
		/* */ System.out.println(" ENTRER NOACTIONFAILURE..."+TID.getMyValue()+"..."+this.getId().toString());
		if (!this.commencedTransaction.empty()&& !this.commencedPanification.empty()){
			final TransactionData data=(TransactionData)this.commencedTransaction.peek();

			/* */ System.out.println("NOACTIONFAILURE: COURANTE ET A TESTER  "+ data.TID.getMyValue() + "..."+ TID.getMyValue());
			if (data.TID.egale(TID)){

				/* */ System.out.println("ENVOI MSG NEGO ECHEC A SIMULATEUR....");

				final Serializable []args=new Serializable[2];
				args[0]=this.getId().toString();
				if (this.debutM!=0)
					args[1]= new Long(System.currentTimeMillis() - this.debutM);
				final Message mes= new Message("inc",args);//,new  AgentName("AgentSimulateur"));
				this.sendMessage(new AgentName("AgentSimulateur"),mes);

				this.commencedPanification.pop();
				this.commencedTransaction.pop();
				boolean t=false;
				while (!this.commencedTransaction.empty() && !t) {

					t= ((TransactionData)this.commencedTransaction.peek()).TID.getMyValue()!=TID.getMyValue();
					if (!t) this.commencedTransaction.pop();

				}


				/* */ this.afficherContComTrans();
				System.out.println(" APRES POP NO ACTIONFAILURE..."+ TID.getMyValue()+"..."+this.getId().toString());
				// sauvegarde historique

				this.history.add(new Long(TID.getMyValue()));

			}

			// else ignore redondance
		}

		if (this.commencedTransaction.empty())
			if (this.waitingTransaction.size() != 0)
			{
				/* */ System.out.println("DEPILER LES TRANSACTIONS EN ATTENTE COTE INITIATEUR"+ ((TransactionData)this.waitingTransaction.get(0)).TID.getMyValue());
				final TransactionData d= (TransactionData)this.waitingTransaction.get(0);
				this.startTransaction((TransactionData)this.waitingTransaction.get(0));

			} else /* */ System.out.println("AUCUNE TRANSACTION EN ATTENTE");

		/* */ System.out.println("SORTIE NOACTIONFAILURE..."+TID.getMyValue()+"..."+this.getId().toString());
	}

	public synchronized void endFailureTransaction(final TransactionID TID){

		/* */ System.out.println("MEETING NON PLANIF TIME OUT"+	this.getId().toString()+ "TID..."+TID.getMyValue());


		if (!this.commencedTransaction.empty()) {

			final TransactionData data= (TransactionData)this.commencedTransaction.peek();
			/* */ System.out.println("JE PARTICIPE A......"+ data.TID.getMyValue()+".."+this.getId().toString());


			if (data.TID.egale(TID)) {

				/* */ System.out.println("ENTRER DANS FAILURE END A CAUSE TIME OUT");
				this.commencedTransaction.pop();

				boolean t=false;
				while (!this.commencedTransaction.empty() && !t) {

					t= ((TransactionData)this.commencedTransaction.peek()).TID.getMyValue()!=TID.getMyValue();
					if (!t) this.commencedTransaction.pop();

				}
				/* */ this.afficherContComTrans();
				System.out.println(" APRES POP ENDFAILURE..."+ TID.getMyValue());
				if (this.waitingTransaction.size() != 0)
				{
					/* */ System.out.println("DEPILER LES TRANSACTIONS EN ATTENTE  COTE PARTICIPANT "+ ((TransactionData)this.waitingTransaction.get(0)).TID.getMyValue());
					final TransactionData d= (TransactionData)this.waitingTransaction.get(0);
					this.startTransaction((TransactionData)this.waitingTransaction.get(0));

				} else /* */ System.out.println("AUCUNE TRANSACTION EN ATTENTE");


			}
			else {
				/* */ System.out.println("ECHEC D'UNE TRANSACTION ALORS QUE JE PARTICIPE A UNE AUTRE"+ TID.getMyValue());
				this.traiter1(TID);

			}

		}
		else
		{

			/* */ System.out.println("ECHEC D'UNE TRANSACTION ALORS QUE JE NE PARTICIPE A AUCUNE");
			this.traiter1(TID);

		}
	}



	public void traiter1(final TransactionID TID){

		final Stack temp= new Stack();
		boolean found=false;
		while (!this.commencedTransaction.isEmpty()&& !found){
			final TransactionData d= (TransactionData) this.commencedTransaction.pop();
			this.afficherContComTrans();
			temp.push(d);
			found=d.TID.getMyValue()== TID.getMyValue();

		}
		if (found) {
			/* */ System.out.println("CAS TRANSACTION COMMENCEE....");
			final TransactionData d=(TransactionData)temp.pop();
		}

		while(!temp.isEmpty()) {
			final TransactionData d= (TransactionData) temp.pop();
			this.commencedTransaction.push(d);

		}
		this.afficherContComTrans();


		// chercher dans waitingTransaction
		found=false;
		int j=-1;
		for (int i=0;i<this.waitingTransaction.size()&& !found;i++){
			found=TID.getMyValue()== ((TransactionData)this.waitingTransaction.get(i)).TID.getMyValue();
			if (found) j=i;
		}

		if (found) {
			/* */ System.out.println("OUF J'AI TROUVE......");
			this.waitingTransaction.remove(j);

		} else
			/* */ System.out.println("ERROR.DUPLICATION.....");



	}


	public synchronized void endTransaction(final TransactionID TID, final TimeSlot ts) {



		/* */ System.out.println("ENTRER DANS ENDTRANSACTION......."+this.getId().toString()+ "TID "+TID.getMyValue());

		if (! this.commencedTransaction.empty()) {
			final TransactionData data = (TransactionData)this.commencedTransaction.peek();
			/* */ System.out.println(" VOICI VALEUR DE DATA " + data.TID.getMyValue());
			if (data.TID.egale(TID))
			{
				// Depiler la transaction (seulement si c est la courante)
				/* */ System.out.println("STEP 10");


				this.commencedTransaction.pop();
				boolean t=false;
				while (!this.commencedTransaction.empty() && !t) {

					t= ((TransactionData)this.commencedTransaction.peek()).TID.getMyValue()!=TID.getMyValue();
					if (!t) this.commencedTransaction.pop();

				}

				/* */ this.afficherContComTrans();
				System.out.println(" APRES POP ENDTRANS..."+ TID.getMyValue());

				// Tient compte de la conclusion
				/* */ System.out.println("FIN REUSSI AVEC ENVOI....");
				System.out.println("Adding a meeting to "+this.getId()+" at "+ts);
				this.myAgenda.addActivity(data.goal, ts);
				/* */  final Serializable[] args = new Serializable[1];
				args[0]= TID;
				final FIPAACLMessage mes = new FIPAACLMessage("noAction",args,data.initiator.toString());
				mes.setPerformative("InformDone");
				mes.setConversationId("conv"+ new Long(TID.getMyValue()).toString());
				this.sendMessage(data.initiator, mes);
				/* */ System.out.println("FIN REUSSIE RECEPTION DU MESSAGE PAR LE DESTINATAIRE");



				// Vide le buffer des transactions qui se sont accumuler pendant cette transaction
				if (this.waitingTransaction.size() != 0)
				{
					/* */ System.out.println("DEPILER LES TRANSACTIONS EN ATTENTE  COTE PARTICIPANT "+ ((TransactionData)this.waitingTransaction.get(0)).TID.getMyValue());
					final TransactionData d= (TransactionData)this.waitingTransaction.get(0);
					this.startTransaction((TransactionData)this.waitingTransaction.get(0));

				} else /* */ System.out.println("AUCUNE TRANSACTION EN ATTENTE");
			}
			else {
				//
			}
		}
		else System.out.println("DUPLICATION DU END MSG.....");
		/* */ System.out.println("SORTIE DE ENDTRANSACTION....."+this.getId().toString());
	}
	public Agenda getAgenda() {
		return this.myAgenda;
	}



	private synchronized boolean isCurrentPlanification(final TransactionID tid) {

		if (!this.commencedPanification.empty()) {
			/* */ System.out.println("CONTENU DE "+ this.commencedPanification.peek());
			/* */ System.out.println("VOICI ID CURRENT PLANIF"+((TransactionID)this.commencedPanification.peek()).getMyValue()+"..ET CELLE QUI DOIT �TRE TESTE"+tid.getMyValue());

			return ((TransactionID)this.commencedPanification.peek()).egale(tid);
		}

		else 	return false;
	}
	/** return if the planning has started for the specified meeting */
	private synchronized boolean planMeetingPart1(final Meeting m) {

		if (this.commencedPanification.empty() && this.commencedTransaction.empty())
		{
			// This agent acquire the role of initiator
			// ... ne le fait plus dans cette version
			//ObservationAgent.getLocalInstance().notifiyEvent(new DirectMessageEvent(this.getId(), "Acquired Initiator role"), this.getId());

			final TransactionID TID = new TransactionID(m.getStartLimit());
			this.commencedPanification.push(TID);

			// Prepare la structure d arrivee avant l appel
			final ArrayList all = m.getAllParticipants();

			final int size = all.size()+1; // +1 for self
			this.nbParticipant = new Integer(size);
			this.splitNecessary = new Integer(m.getNecessaryParticipants().getCanonicalList().size());
			this.awaitingAvailability = new byte[size][];
			for (int i=0;i<size;i++) this.awaitingAvailability[i] = null;

			// Tout le monde na pas encore repondu
			this.allNecessaryHere = new Boolean(false);

			// Pour chaque membre, on les appels et on attends leur reponse (traitement different pour les necessary et les autres)
			// Soi meme
			int myPos;
			if (m.isSelfNecessary()) {
				myPos = 0; this.offset = new Integer(1);
			}	else {
				myPos = size-1; this.offset = new Integer(0);
			}

			final int n =this.splitNecessary.intValue() +this.offset.intValue();
			this.splitNecessary = new Integer(n);
			this.allNecessaryHere=new Boolean(false);
			this.acceptTransaction(TID, this.myAgenda.getNonImpossibleTimeFromNowUntil(m.getLimitDay(), m.getStartLimit()), new Integer(myPos), this.getId().toString(),m);
			final TransactionData dataAsParticipant = new TransactionData(TID, m, TransactionID.transactionRoot, this.myAgenda.getActivities().cloneActivities(), this.getId(), new Integer(myPos), new Boolean(m.isSelfNecessary()));
			this.commencedTransaction.push(dataAsParticipant);
			/* */ this.afficherContComTrans();
			System.out.println(" APRES PUSH PLAN MEET PART 1 DE...."+dataAsParticipant.TID.getMyValue());

			// 2912 allNecessaryHere = new Boolean(splitNecessary.intValue()== offset.intValue());

			// tous les autres
			synchronized (all) {for (int i=0;i<size;i++)
				if (i!= myPos)
				{
					final Serializable[] args = new Serializable[6];
					args[0] = TID;
					args[1] = m;
					args[2] = TransactionID.transactionRoot; // No parent transaction
					args[3] = this.getId();
					args[4] = new Integer(i); // Identificateur au sein de la transaction
					args[5] = new Boolean(i<this.splitNecessary.intValue()); // Identificateur au sein de la transaction

					final FIPAACLMessage mes = new FIPAACLMessage("startTransaction", args, ((Contact)all.get(i-this.offset.intValue())).getAgentID().toString());
					mes.setPerformative("CallForProposal");
					mes.setConversationId("conv"+ new Long(TID.getMyValue()).toString());
					System.out.println("DOMAIN: ENVOI D'UN 	CFP....."+ mes.getType() +"IDTRANS"+TID.getMyValue() + "  AVEC RECEPTEUR..."+ ((Contact)all.get(i-this.offset.intValue())).getAgentID().toString());
					//wwait(1000);

					this.sendMessage(((Contact)all.get(i-this.offset.intValue())).getAgentID(), mes); // Le dernier champs n est pas utilise par l API actuel
					this.debutM= System.currentTimeMillis();
				}
			}

			return true;} else
				//Sinon, on est deja en train de plannifier (initiateur ou pas), repasser plutard !
				return false;

	}
	private synchronized void planMeetingPart2() {

		/* */ System.out.println("ENTRER DANS PLAN PART 2....");

		// Utilise le faite, cet fois a notre avantage, qu on participe aussi a la negociation
		final TransactionData data = (TransactionData)this.commencedTransaction.peek();
		final Meeting m = data.goal;
		final TransactionID TID = data.TID;

		// Avec toute ces informations, on initialise les propositions
		// On fait la somme des availabilities pour detecter les impossibles



		final int[] everyoneAvailability = new int[this.awaitingAvailability[0].length];
		for (int i=0;i<everyoneAvailability.length;i++)
		{
			everyoneAvailability[i] = 0;
			for (int j=0;j<this.nbParticipant.intValue();j++)
				if (this.awaitingAvailability[j] != null)
					everyoneAvailability[i] += this.awaitingAvailability[j][i];
		}

		// Now all 0 values are possible place to start
		// Init with the first possible shot ! [...]
		int index = 0;
		while (index < everyoneAvailability.length && everyoneAvailability[index] > 0)
			index++;

		if (index == everyoneAvailability.length)
			// Pas de meeting possible avant la date d expiration
			System.out.println("Meeting impossible avant la date d expiration");
		final Calendar start = Agenda.getNextDepartureTimeAfter(System.currentTimeMillis());
		start.add(Calendar.MINUTE, index);

		// In more advanced agent, check for the duration to fit
		// ...

		this.possibleSlot.add(new TimeSlot(start, m.getDurationSet()[0])); // Use only the minimum duration

		// Not used yet
		// testSlot();

		// Get the best time slot stored
		// To be simple : use the first one
		final TimeSlot bestTimeSlot = new TimeSlot(start, m.getDurationSet()[0]);


		final Serializable[] args = new Serializable[2];
		args[0] = TID;
		args[1] = bestTimeSlot;
		final FIPAACLMessage mes = new FIPAACLMessage("endTransaction", args, null);
		mes.setPerformative("AcceptProposal");
		mes.setConversationId("conv"+ new Long(TID.getMyValue()).toString());


		final Serializable [] args1= new Serializable[1];
		args1[0]= TID;
		final FIPAACLMessage mes1= new FIPAACLMessage("endFailureTransaction",args1,null);
		mes1.setPerformative("RejectProposal");
		mes1.setConversationId("conv"+ new Long(TID.getMyValue()).toString());

		if (this.sendToAllParticipants(m, TID, mes,mes1)!=0) {

			System.out.println("Adding a meeting to "+this.getId()+" at "+bestTimeSlot);
			this.myAgenda.addActivity(m, bestTimeSlot);

		} else
			/* */ System.out.println("No Possible Meeting....."+m.getStartLimit());

		/* */ System.out.println("SORTIR DE PLAN PART 2");
	}


	/** Call by the agenda in order to plan a meeting with the others (So that call to agent plan meeting function will not stop caller (ex GUI)) */


	public synchronized void planMeetingWhenAvailable(final Meeting m) {

		/* */ System.out.println("ENTREE DANS PLAN MEETING WHEN	AVAIL...."+ this.getId());
		this.meetingToBePlanned.add(m);

		/* */ System.out.println("CONTENU DES MEETING TO PLAN PAR..."+this.getId()+ "EST...");
		for (int i=0; i<this.meetingToBePlanned.size(); i++) {
			final Meeting m1= (Meeting) this.meetingToBePlanned.get(i);
			/* */ System.out.println(m1.getStartLimit());
		}
	}





	@Override
	public synchronized void readMailBox () {
		if (this.hasMail())
			this.processNextMessage();
		else {
			//System.out.println(""+getId()+" has no mail");
		}
	}
	/** Is considered as participants every agents that has returned its original disponibilites (event if negociation had started) */


	private int sendToAllParticipants(final Meeting m, final TransactionID tid, final FIPAACLMessage message, final FIPAACLMessage message1) {
		final ArrayList all = m.getAllParticipants();
		int nbAccept=0;
		for (int i=0;i<all.size();i++)
			synchronized (message) {

				// System.out.println("CONTENU DES DISPONIBILITES..."+ awaitingAvailability[i+offset.intValue()].toString());

				// ???if (!(awaitingAvailability[i+offset.intValue()] == null))

				if (!(this.awaitingAvailability[i+this.offset.intValue()] == null))
				{
					message.setReceiver(((Contact)all.get(i)).getAgentID());
					this.sendMessage( ((Contact)all.get(i)).getAgentID(), message);
					nbAccept++;
				}

				else
				{
					message1.setReceiver(((Contact)all.get(i)).getAgentID());
					this.sendMessage( ((Contact)all.get(i)).getAgentID(), message1);
				}
			}

		if (nbAccept==0)
			// aucun acceptProposal envoy� dans ce cas noAction
			this.noActionFailure(tid);

		return nbAccept;
	}
	public void setVisualInterfaceOn(final boolean on) {
		this.myAgenda.setVisualInterfaceOn(on);
	}
	private void startTransaction(final TransactionData data) {

		/* */ System.out.println("ENTRER START TRANSACTION "+this.getId().toString()+ ".."+data.getTransactionID().getMyValue());

		final boolean bol1= data.parentTID.egale(TransactionID.transactionRoot) &&
				this.commencedTransaction.empty();


		boolean bol2=false;
		if (!this.commencedTransaction.empty()) {

			bol2= data.parentTID.egale(((TransactionData) this.commencedTransaction.peek()).getTransactionID()) ;
			/* */ System.out.println("VALEUR DE TRANSACTION EN COURS  "+((TransactionData)this.commencedTransaction.peek()).getTransactionID().getMyValue());
		}


		boolean bol3=false;
		if (!this.commencedTransaction.empty())
			bol3= data.TID.egale(((TransactionData) this.commencedTransaction.peek()).getTransactionID());

		if (!bol3) {
			if (bol1 || bol2) {


				if (this.waitingTransaction.size()!=0)
					if(((TransactionData)this.waitingTransaction.get(0)).TID.getMyValue()==data.TID.getMyValue())

					{
						/* */ System.out.println("START TRANSAC: TRT TRANS EN ATTENTE "+data.TID.getMyValue());
						this.waitingTransaction.remove(data);


					}
				/* */ System.out.println("ON EST LIBRE TRAITER LA REQUETE...."+	this.getId().toString()+ "..."+ data.TID.getMyValue());


				// Je suis libre ou cette transaction s incscrit dans une que j ai commencer
				//System.out.println("Puff toto 2"); System.out.flush();
				// Mettre a jour l emploi du temps tel qu il est actuellement
				data.activities = this.myAgenda.getActivities().cloneActivities();
				//System.out.println("Puff toto 3"); System.out.flush();
				// Daccord, on prends cette transaction
				this.acknowledgeTransaction(data);

				/* */ System.out.println("ENVOI POUR PROPOSE REUSSI..."+this.getId().toString());
				//System.out.println("Puff toto 4"); System.out.flush();

			}

			else {
				// On fait rien car on est pas libre, faudra qu il passiante.
				//System.out.println("Puff toto 5"); System.out.flush();

				/* */ System.out.println("MISE ATTENTE TRANSACTION..."+data.TID.getMyValue());
				if (this.waitingTransaction.size()!=0)
				{
					if (((TransactionData)this.waitingTransaction.get(0)).TID.getMyValue()!=data.TID.getMyValue())
					{
						/* */ System.out.println("START TRANS: CAS NON VIDE PREMIERE FOIS EN	ATTENTE  "+ data.TID.getMyValue());
						this.waitingTransaction.add(data);

					}
				}
				else
				{
					/* */ System.out.println("START TRANS: CAS VIDE PREMIERE FOIS EN	ATTENTE  "+ data.TID.getMyValue());
					this.waitingTransaction.add(data);
				}
				//System.out.println("Puff toto 6"); System.out.flush();
			}
		} else
			/* */  System.out.println("DUPLICATA DE MESSAGE......");

		/* */ System.out.println("SORTIE START TRANSACTION "+this.getId().toString()+data.getTransactionID().getMyValue());
	}


	public boolean contientAgent(final String agId, final ArrayList necessParticip) {
		boolean found=false;
		int i=0;

		while (i<necessParticip.size() && !found){

			found=((Contact)necessParticip.get(i)).getAgentID().toString().equals(agId);
			i++;
		}

		return found;

	}


	public void afficherContComTrans(){

		/* */ System.out.println("CONTENU DE COMMENCED TRANS  TAILLE "+ this.commencedTransaction.size()+ "..."+this.getId().toString());

		for(int i=0; i<this.commencedTransaction.size();i++)
			/* */ System.out.println(((TransactionData) this.commencedTransaction.get(i)).TID.getMyValue());

	}

	public void afficherContComPlan(){

		/* */ System.out.println("CONTENU DE COMMENCED PLAN  TAILLE "+ this.commencedPanification.size()+ "..."+this.getId().toString());

		for(int i=0; i<this.commencedPanification.size();i++)
			/* */ System.out.println(((TransactionID) this.commencedPanification.get(i)).getMyValue());

	}



	public boolean contientSender(final ArrayList al, final String emet) {
		boolean tr=false;
		/* */ System.out.println("CONTENU NBRESP ARRAYLIST..."+emet );

		for (int i=0; i< al.size() && !tr;i++){
			/* */ System.out.println((String) al.get(i));
			tr= ((String) al.get(i)).equals(emet);

		}
		return tr;
	}

	public int indexMeeting(final Meeting m,final ArrayList al){

		boolean found=false;
		int i=0;

		while (i<al.size() && !found){

			found= ((Meeting)al.get(i)).getStartLimit()==m.getStartLimit();
			i++;
		}

		if(found) return i-1;
		else return -1;

	}




	public void startTransaction(final TransactionID TID, final Meeting m, final TransactionID parentTID, final AgentName whoAsk, final Integer identifierWithinTransaction, final Boolean necessary) {
		this.startTransaction(new TransactionData(TID, m, parentTID, this.myAgenda.getActivities().cloneActivities(), whoAsk, identifierWithinTransaction, necessary));
	}

	@Override
	public void proactivityInitialize() {

		this.wwait(100000);

	}



	@Override
	public void step() {


		try{
			// wwait(3000); 0403
			this.wwait(1000);
		}catch (final Exception e ) {
		}

		while (this.hasMail())
			this.readMailBox();



		// Plan a meeting if needed


		if (!this.meetingToBePlanned.isEmpty())
		{
			/* */ System.out.println("CONTENU DES MEETING TO PLAN PAR..."+this.getId()+ "EST...");
			for (int i=0; i<this.meetingToBePlanned.size(); i++) {
				final Meeting m1= (Meeting) this.meetingToBePlanned.get(i);
				/* */ System.out.println(m1.getStartLimit());
			}



			if (this.planMeetingPart1((Meeting)this.meetingToBePlanned.get(0)))
			{
				// It was succesfully started : remove it from the list


				/* */ System.out.println("Meeting to REMOVE..."+((Meeting)this.meetingToBePlanned.get(0)).getStartLimit());
				final Meeting met= (Meeting) this.meetingToBePlanned.get(0);

				for (int i=0; i<this.meetingToBePlanned.size();i++)
					if (met.getStartLimit()== ((Meeting)this.meetingToBePlanned.get(i)).getStartLimit())
					{
						/* */ System.out.println("SUPPRESSION MEETING ..."+met.getStartLimit());
						this.meetingToBePlanned.remove(i);
					}


			}


		}

		// Check if suspended job must be restarted
		if (this.actionToDo.size()>0)
			//System.out.println("Action to do in reserve");
			for (int i=0;i<this.actionToDo.size();i++)
			{
				final PlannedAction act = (PlannedAction)this.actionToDo.get(i);
				if (act.shouldBeExecuted())
				{
					//System.out.println("action "+act+" should do "+act.getAction());
					switch (act.getAction())
					{
					case PlannedAction.planMeetingPart2 :
						/* */System.out.println(" Planned action part2");
						if (!this.commencedTransaction.empty())
						{
							if (act.getStartMeeting()== ((TransactionData) this.commencedTransaction.peek()).TID.getMyValue()) {

								this.planMeetingPart2();

								break;
							}
							else /* */ System.out.println("DUPLICAT 1 AU NIVEAU DU ADD ACTION PLAN...."+ act.getStartMeeting()+"..."+((TransactionData) this.commencedTransaction.peek()).TID.getMyValue());
						} else
							/* */ System.out.println("DUPLICAT 2 AU NIVEAU DU ADD ACTION PLAN...."+ act.getStartMeeting());
					}
					this.actionToDo.remove(i);

					i--;
				}
			}
	}




}
