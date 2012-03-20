package examples.eAgenda.data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import examples.eAgenda.API.AgendaAgent0;
import examples.eAgenda.gui.AgendaInterface;

/** Main class for gathering all the information usefull in this application */
public class Agenda implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 3282467759853283143L;
	protected ActivityList activities;
	protected StandardWeekAvailability availability;
	protected ContactList contacts;

	protected AgendaAgent0 myAgent;
	protected transient AgendaInterface myInterface;
	protected boolean interfaceVisible = false;

	int diffGMT = (Calendar.getInstance().get(Calendar.ZONE_OFFSET) + Calendar.getInstance().get(Calendar.DST_OFFSET))/60000;

	/** Create a new empty agenda */
	public Agenda() {

		//myAgent = agent;
		this.myInterface = null;

		// Load an empty list of activity
		this.activities = new ActivityList();

		// Set the default availabilty
		this.availability = new StandardWeekAvailability();
		this.availability.setStandard();

		this.contacts = new ContactList("WholeList");
	}
	public void addActivity(final Activity activity, final TimeSlot slot) {
		this.activities.addActivity(activity, slot);
		if (this.interfaceVisible) {
			this.myInterface.repaint();
		}
	}
	/** Shortcut */

	//me
	public void removeActivity(final Activity activity) {
		this.activities.removeActivity(activity);
	}


	public void addContact(final Contact c) {
		this.contacts.addPeople(c);
	}
	/** Ask the agent to plan this meeting (and the agenda won't worry anymore about this issue) */
	public void findTimeSlotFor(final Meeting m) {
		// Use the agent to find a proper date !
		this.myAgent.planMeetingWhenAvailable(m);
	}
	public ActivityList getActivities() {
		return this.activities;
	}
	public ContactList getContactList() {
		return this.contacts;
	}
	/** When is the next time that an action can try to be planned */
	public static Calendar getNextDepartureTimeAfter(final long startTime) {
		// On doit se mettre tous d accord (implicitement) sur la date de debut.
		// ATTENTION : On suppose evidement ici que tout le monde a la meme heure
		// On prends tout de meme de marge, mais attention quand meme !!!

		final Calendar now = Calendar.getInstance();
		now.setTime(new Date(startTime));
		final int minute = now.get(Calendar.MINUTE);
		if ( minute <50 ) {
			now.add(Calendar.MINUTE, 60-minute);
		} else {
			// On commence a la demi-heure de l heure suivante
			now.add(Calendar.MINUTE, 60-minute + 30);
		}

		return now;
	}
	/** Return an array for each minutes from now with 0 if an activities can take place, 1 otherwise */
	public byte[] getNonImpossibleTimeFromNowUntil(final Day dayLimit, final long startTime) {

		final int departureTime = (int)(Agenda.getNextDepartureTimeAfter(startTime).getTime().getTime() / 60000);
		final int size = (int)(Day.getTimeMillis(dayLimit, 0, 0, 0)/60000) - departureTime + this.diffGMT + 24*60*60; // Le +24*60*60 c est pour inclure la date limite dans les pour parler ;)
		final byte[] possible = new byte[size];
		for (int i=0;i<size;i++) {
			possible[i] = 0;
		}

		// Mettre une couche pour Standard availability

		// Parcour l integralite de la liste juste qu a la date limite en utilisant l availability
		Day timeCursor = Day.today();
		StandardAvailability av = this.availability.getAvailabilityForDay(timeCursor.getWeekDayValue());
		// Parcour pour aujourd hui
		int start = (departureTime+this.diffGMT)%(24*60);
		final int top = 24*60 - start;
		for (int i=0;i<top;i++) {
			if (av.getAvailability(i+start) > 0.9) {
				// Lock this position
				possible[i] = 1;
			}
		}
		if (timeCursor.compareTo(dayLimit)>=0)
		{
			System.err.println("Pas bon "+timeCursor+" "+dayLimit+" "+start+" "+top+" "+Day.today());
			System.exit(-1);
		}
		start = top;
		// Parcour tout les autres jour jusqu a limite
		timeCursor = Day.forwardedDay(timeCursor,1);
		while (timeCursor.compareTo(dayLimit)<0)
		{
			av = this.availability.getAvailabilityForDay(timeCursor.getWeekDayValue());
			// Parcourir toute les minutes dans cette journee
			for (int i=0;i<24*60;i++) {
				if (av.getAvailability(i) > 0.9) {
					try
					{
						// Lock this position
						possible[i+start] = 1;
					}
					catch (final ArrayIndexOutOfBoundsException ex)
					{
						System.err.println("Array out of bound with "+i+" "+start+" "+size+" "+possible.length+" "+timeCursor+" "+dayLimit);
						System.exit(-1);
					}
				}
			}
			start += 24*60;
			timeCursor = Day.forwardedDay(timeCursor,1);
		}

		// Mettre une couche pour les activitees deja prise inamovible
		for (final Iterator it = this.activities.getAllActivities();it.hasNext(); )
		{
			final TimedActivity act = (TimedActivity)it.next();

			// Comme actuellement, on ne peut rien deplacer (l agent ne sais pas faire), on dit que tout est statique
			// !act.getActivity().isMoveable())
			if (true)
			{
				final TimeSlot slot = act.getTimeSlot();
				final int beginActivityTime = (int)(Day.getTimeMillis(slot.getStartDay(), 0, 0, slot.getMinuteInDay())/60000)- departureTime;
				if (beginActivityTime > 0)
				{
					final int stopTime = slot.getDuration()+beginActivityTime;
					for (int i=beginActivityTime;i<stopTime;i++) {
						if (i>=0 && i<size) {
							possible[i] = 1;
						}
					}
				}
			}
		}

		// Mettre une couche si on veut forcer certaine impossibilite (exple pas de changement (inclu rajout) dans les 2h a venir)
		for (int i=0;i<60*2;i++) {
			possible[i] =1;
		}

		return possible;
	}
	/** return the availability for a spefic day of the week (0 is sunday) */
	public StandardAvailability getStandardAvailability(final int day) {
		return this.availability.getAvailabilityForDay(day);
	}
	public StandardWeekAvailability getStandardWeekAvailability() {
		return this.availability;
	}
	public static void main(final String[] args) {
		final Agenda ag = new Agenda();
		ag.setVisualInterfaceOn(true);
	}
	public void setResponsibleAgent(final AgendaAgent0 agent) {
		this.myAgent = agent;
	}
	public void setVisualInterfaceOn(final boolean on) {
		this.interfaceVisible = on;
		if (on)
		{
			if (this.myInterface == null) {
				this.myInterface = new AgendaInterface(this);
			}
			this.myInterface.show();
		} else {
			this.myInterface.close();
		}
	}
}
