package examples.eAgenda.data;

import java.io.Serializable;

/** A couple Activity and a TimeSlot (when the activity should occurs) */
public class TimedActivity implements Cloneable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1579053953815688631L;
	Activity myActivity;
	TimeSlot myTime;

	public TimedActivity(final Activity act, final TimeSlot time) {
		this.myActivity = act;
		this.myTime = time;
	}
	public TimedActivity cloneTimedActivity() {
		try
		{
			return (TimedActivity)this.clone();
		}
		catch (final Exception ex)
		{
			return null;
		}
	}
	public Activity getActivity() {
		return this.myActivity;
	}
	public TimeSlot getTimeSlot() {
		return this.myTime;
	}
}
