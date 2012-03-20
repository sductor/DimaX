package examples.eAgenda.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/** List all the activities that are planned into an agenda */
public class ActivityList implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -6986335247932399348L;
	/** List of timed activities */
	ArrayList act;

	/** Create an empty activity list */
	public ActivityList() {
		this.act = new ArrayList();
	}
	public void addActivity(final Activity activity, final TimeSlot slot) {
		this.act.add(new TimedActivity(activity, slot));
	}

	// me
	public void removeActivity(final Activity activity) {
		this.act.remove(activity);
	}

	/** Return an exact copy of this activity list */
	public ActivityList cloneActivities() {
		final ActivityList res = new ActivityList();
		for (final Iterator it = this.act.iterator();it.hasNext(); ) {
			res.act.add( ((TimedActivity)it.next()).cloneTimedActivity() );
		}
		return res;
	}
	public Iterator getAllActivities() {
		return this.act.iterator();
	}
	public ArrayList getDayActivities(final Day day) {
		final ArrayList res = new ArrayList();
		for(int i=0;i<this.act.size();i++) {
			if ( ((TimedActivity)this.act.get(i)).getTimeSlot().contained(day) ) {
				res.add(this.act.get(i));
			}
		}
		return res;
	}
}
