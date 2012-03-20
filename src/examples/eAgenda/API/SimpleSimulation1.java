package examples.eAgenda.API;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.Message;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
import examples.eAgenda.data.Agenda;
import examples.eAgenda.data.Contact;
import examples.eAgenda.data.ContactList;
import examples.eAgenda.data.Day;
import examples.eAgenda.data.Meeting;



/** Use the eAgenda agents in simulation where everyone knows everyone */
public class SimpleSimulation1 extends BasicCommunicatingAgent{

	/**
	 *
	 */
	private static final long serialVersionUID = 8865922431826388151L;
	AgendaAgent0[] myAgents;
	static final int nbMax=100;
	int nbSucSim=0;
	int nbSim=0;
	int nbProcMeet=0;
	int nbMeetToBeProc=-1;
	boolean simSuc=false;
	boolean simFail=false;
	boolean obs;
	PrintWriter res=null;
	Vector futur= new Vector();
	Vector histFail=new Vector();
	Vector histInit=new Vector();
	int nbProc=0;
	int nbSuc=0;
	int expNum = 1;
	int expP=0;


	public static

	// String[]URL_TABLE={"leri-ma04.leri.univ-reims.fr","leri-ma24.leri.univ-reims.fr","leri-ma20.leri.univ-reims.fr","leri-ma21.leri.univ-reims.fr"};//,"leri-ma25.leri.univ-reims.fr"};//"leri-ma05.leri.univ-reims.fr","leri-ma10.leri.univ-reims.fr","leri-ma07.leri.univ-reims.fr","leri-ma03.leri.univ-reims.fr","leri-ma08.leri.univ-reims.fr"};
	String[]URL_TABLE={"leri-ma01.leri.univ-reims.fr"};


	public SimpleSimulation1(final String id, final Integer numExp,final Integer pExp,final boolean obs) {
		super(id);
		this.expNum= numExp.intValue();
		this.expP=pExp.intValue();
		// initialisation du fichier resultat
		this.obs=obs;

		/* */System.out.println("VALEUR NUM EXP..."+this.expNum+"..."+this.expP);
	}


	public void init(final int nb1,final int nb2, final int nb3){
		/* */ System.out.println("VALEUR DE RES_2202_ROB_ "+nb1);
		this.echange("TEMP.txt","RES_2504_ROB_"+nb1+"_"+nb3+".txt");

		this.generateRandomAction(nb2);

	}

	public void echange(final String fic1, final String fic2){


		try {
			final File newfile= new File(fic1);
			this.res= new PrintWriter(new BufferedWriter(new FileWriter(newfile)));
			final File somefile= new File(fic2);
			final BufferedReader lec= new BufferedReader(new FileReader(somefile));
			String line=lec.readLine();
			while (line!=null) {
				this.res.println(line);
				line=lec.readLine();
			}
			lec.close();


		} catch (final Exception e) {

			/* */ System.out.println("ERREUR OUVERTURE FICH RES");
		}

	}


	/** In order to have a long list of events, this method generate numerous user action event, ie plan meeting etc. in order to observe the events */

	protected void generateRandomAction(final int nb) {

		/* */ System.out.println("ENTRER DANS GENRANDACTION.......");
		final int nbDay=20;
		int temp=0;
		int d;
		int meet;
		final int[] duration = new int[2];
		int ag;
		Day startDay = Day.today();
		final File somefile=new File("FixedMeet100AgRedParticip7.txt");
		this.nbSim++;
		String line;
		int indexB=0;
		int indexE=0;

		try{
			final BufferedReader reader= new BufferedReader(new FileReader(somefile));
			line=reader.readLine();
			//while (line!=null) {

			for (int k=0;k<50;k++) {

				indexE=line.indexOf(";",indexB);
				/* */ System.out.println("VALEUR INDICE DU PROCHAIN ; "+indexE);
				/* */ System.out.println("VALEUR DE D EN CHAINE DE CAR...."+ line.substring(indexB,indexE));
				d= new Integer(line.substring(indexB,indexE)).intValue();
				/* */ System.out.println("AFFICHER VAL DU NUM DAY...."+d);
				indexB=indexE+1;
				indexE=line.indexOf(";",indexB);
				meet= new Integer(line.substring(indexB,indexE)).intValue();
				/* */ System.out.println("AFFICHER VAL DU NUM DAY...."+meet);
				indexB=indexE+1;
				indexE=line.indexOf(";",indexB);
				duration[0]= new Integer(line.substring(indexB,indexE)).intValue();
				indexB= indexE+1;
				indexE=line.indexOf(";",indexB);
				duration[1]= new Integer(line.substring(indexB,indexE)).intValue();
				indexB= indexE+1;
				indexE=line.indexOf(";",indexB);
				ag = new Integer(line.substring(indexB,indexE)).intValue();
				/* */ System.out.println("AFFICHER VAL DU NUM DAY...."+ag);
				int i=indexE+1;
				final Vector tab=new Vector();
				final ContactList necess = new ContactList("Necessary members");
				final ContactList optional = new ContactList("Optional members");
				while (i< line.length()-1) {
					if ( !line.substring(i,i+1).equals(";")) {
						if (!line.substring(i,i+1).equals("?") && !line.substring(i,i+1).equals("!")) {

							indexE=line.indexOf(";",i);
							/* */ System.out.println("VAL PARTICIP..."+ line.substring(i,indexE));
							tab.addElement(line.substring(i,indexE));
							i=indexE;


						}
						else {

							if (line.substring(i,i+1).equals("?")) {
								// mettre tab dans necess

								for (int j =0;j<tab.size();j++) {
									final String s= (String) tab.get(j);

									necess.addPeople(new Contact(""+new AgentName(s), new AgentName(s)));
								}
								tab.removeAllElements();


							}else
							{
								// mettre tab dans optional
								for (int j =0;j<tab.size();j++) {
									final String s= (String) tab.get(j);

									optional.addPeople(new Contact(""+new AgentName(s), new AgentName(s)));
								}
								tab.removeAllElements();
							}
							i++;

						}
					} else {
						i++;
					}
				}



				final Meeting m = new Meeting("Meeting "+d+"-"+meet,"",duration, Day.forwardedDay(Day.today(),nbDay*2), necess, optional, true, false);
				final Serializable [] args= new Serializable[1];
				args[0]=m;
				System.out.println("Agent "+ag+" plan a meeting on "+startDay+" et id "+ m.getStartLimit() +" for "+necess.getCanonicalList().size()+" necessary and "+optional.getCanonicalList().size()+" optionals");
				temp++;
				/* */ System.out.println("LES NECESSAIRES:  ....");
				final ArrayList li= necess.getMyPeople();
				for (int j=0; j< li.size();j++) {
					System.out.println(((Contact)li.get(j)).getAgentID().toString());
				}

				/* */ System.out.println("LES OPTIONNELS:  ....");
				final ArrayList li1= optional.getMyPeople();
				for (int j=0; j< li1.size();j++) {
					System.out.println(((Contact)li1.get(j)).getAgentID().toString());
				}

				final Message mes=new Message("planMeetingWhenAvailable", args);//, new AgentName(myAgents[ag].getId().toString()));
				this.wwait(2000);
				/* */ System.out.println("MESSAGE RECU PAR  AGENT.....");
				this.sendMessage(new AgentName(this.myAgents[ag].getId().toString()),mes);


				// Wait a little so that agents may have time to begin meeting negotiation
				// and won't start a new negotiation before receiving there invitation to another one
				try {
					//wwait(2000);
					// Thread.sleep(10000);
				}	catch (final Exception ex){ System.out.println("Exeption will trying to sleep: "+ex);}
				// Deadlock and weird situations may occurs if two beginTransaction message cross each others

				startDay = Day.forwardedDay(startDay, 1);
				indexB=0;
				indexE=0;
				line=reader.readLine();

			}
			reader.close();

		} catch (final Exception e) {
			/* */ System.out.println("ERREUR LORS LECTURE DANS GENRANDACT.....");

		}




		this.nbMeetToBeProc=temp;
		System.out.println("Finished automatic event creation...."+ temp+ "Meetings To Plan");
		System.out.println("AFFICHER NBSIM..."+this.nbSim);
	}




	/** Create a simulation with the given number of agents */


	protected void initAgents(final int nb) {

		// Create all agents
		if (this.obs) {
			this.myAgents = new AgendaAgent4[nb];
		} else {
			this.myAgents = new AgendaAgent0[nb];
		}
		for (int i=0;i<nb;i++)
		{
			final Agenda agentAgenda = new Agenda();

			if (this.obs) {
				this.myAgents[i] = new AgendaAgent4(agentAgenda);
			} else {
				this.myAgents[i] = new AgendaAgent0(agentAgenda);
			}

			final AgentName name = new AgentName("AgendaAgent"+i);
			this.myAgents[i].setId(name);

			agentAgenda.setResponsibleAgent(this.myAgents[i]);
		}

		// Tout le monde se connait dans Contact

		for (int i=0; i< this.myAgents.length; i++) {
			for (int it =0; it< this.myAgents.length; it++) {
				if (i!=it) {
					final String agentName = this.myAgents[it].getId().toString();
					this.myAgents[i].getAgenda().addContact(new	Contact(agentName, this.myAgents[it].getId()));
				}
			}
		}

		// Activate all agents

		for (int i=0;i<nb;i++)
		{

			final String adr=this.selectURL(i);
			// System.out.println("AGENT "+i+" A ACTIVER A "+adr+(7005+(i/4)%2));
			System.out.println("AGENT "+i+" A ACTIVER A "+adr+(7005+i%2));
			//		if (obs)
			//		myAgents[i].activateWithDarxObs(adr,(7005+(i)%2));
			//		else
			this.myAgents[i].activateWithDarx(adr,7005+i%2);
			this.wwait(100);
		}

	}


	public String selectURL(final int index)

	{
		return SimpleSimulation1.URL_TABLE[index % 1];
	}

	public void echec(final String s) {
		/* */ System.out.println("AGENT..."+s+"A CAUSE LE CRASH.....");
		this.wwait();
		this.histFail.addElement(new Integer(this.futur.size()));
		this.histInit.addElement(s);

	}

	public void chercher (final String s,final int deb){
		/* */System.out.println("ENTRER DANS CHERCHER AVEC TAILLE HIST .."+this.futur.size());
		/* */ System.out.println("ET DEB DE LA COMP..."+ deb);
		for (int i=deb;i<this.futur.size();i++){
			final String st=(String) this.futur.get(i);
			if (st.equals(s))
			{
				if (!this.simFail) {
					this.nbProc=this.nbProcMeet-(i-deb);
				}
				this.simFail=true;
				this.res.print(s);this.res.print("; ");
			}
		}
	}

	public void reussi(){
		this.simSuc=true;
		this.nbSucSim++;
	}


	public void inc(final String s, final Long tps) {
		this.nbProcMeet++;
		/* */ System.out.println("NBRE DE MEETINGS TRAITES  "+this.nbProcMeet);
		if (this.nbProcMeet >= this.nbMeetToBeProc-10)
		{
			this.reussi(); // -10 au lieu de -20 pour RI
		}
		/* */ System.out.println("ID DE L'INIT...."+s);
		this.futur.addElement(s);
	}



	@Override
	public void proactivityInitialize() {
		System.out.println("ENTRER AVANT INITAGENT.....");

		this.initAgents(100);
		/* */ System.out.println("VALEUR DE expNum.."+this.expNum+"..."+this.expP);
		this.init(this.expNum,1,this.expP);
	}

	@Override
	public void step() {

		while (this.hasMail()) {
			this.readMailBox();
		}

		this.wwait(1600);
		if (this.simFail || this.simSuc) {
			this.wwait(2000);
			/* */ System.out.println("APPUYER SUR ENTRER POUR ARRETER LA SIM....");

			try {
				for (int i=0;i<this.histFail.size();i++){
					final String iniAg=(String)this.histInit.get(i);
					final Integer deb1=(Integer)this.histFail.get(i);

					this.chercher(iniAg,deb1.intValue());

				}
				if (this.simSuc) {
					this.nbSuc=1;
				} else {
					this.nbSuc=0;
				}
				this.res.print(this.nbSuc); this.res.print(" ;");
				this.res.print(this.nbProc);this.res.println();
				this.res.close();
				this.echange("RES_2504_ROB_"+this.expNum+"_"+this.expP+".txt","TEMP.txt");
				this.res.close();
			} catch (final Exception e) {
				/* */ System.out.println("ERREUR LORS MAJ RES......");

			}
			this.simFail=false;
			this.simSuc=false;
		}

	}

	public static void main(final String[] args) {

		int num;
		int p=0;
		int rm=0;
		// boolean obs=false;
		boolean obs=true;
		boolean activeF=false;
		// System.out.println("TAILLE DES ARGUMENTS ....."+args.length+"..."+ args[0]);
		try{
			java.lang.Thread.sleep(1000);} catch (final Exception e) {
			}
		if (args.length> 0) {
			num=new Integer(args[0]).intValue();
			/* */ System.out.println("VALEUR de NUM..."+num);
			if (args.length>=2)
			{
				obs=true;
				if (args.length>=3) {
					p=new Integer(args[2]).intValue();
					/* */ System.out.println("VALEUR de P..."+p);
					if (args.length>=4)
					{
						rm=new Integer(args[3]).intValue();
						/* */ System.out.println("VALEUR de RM..."+rm);
						if (args.length==5)
						{
							/* */ System.out.println("ACTIF MIS A VRAI ......");
							activeF=true;
						}
					}

				}
			}
		} else {
			num=1;
		}
		final SimpleSimulation1 sim = new SimpleSimulation1("AgentSimulateur",new Integer(num),new Integer(p),obs);

		System.out.println("ACTIVATION DU SIMULATEUR");
		// sim.activateWithDarx("leri-ma01.leri.univ-reims.fr",7002);
		sim.activateWithDarx("leri-ma01.leri.univ-reims.fr",7020);
		System.out.println("APRES ACTIVATION DU SIMULATEUR");
		/* FaultySimulator2 fault = new FaultySimulator2("FaultySimul",300);
	System.out.println("ACTIVATION DU FAULTY....");
	fault.activateWithDarx("leri-ma01.leri.univ-reims.fr",7003);
	System.out.println("APRES ACTIVATION DU FAULTY.....");
	// partie random replication

	 RandomReplicator randRep = new	RandomReplicator("FaultySimulR",rm,activeF);
	System.out.println("ACTIVATION DU RANDOM REPL....");
	randRep.activateWithDarx("leri-ma01.leri.univ-reims.fr",7004);
	System.out.println("APRES ACTIVATION DU RANDOM REPLICATOR.....");*/

	}




}
