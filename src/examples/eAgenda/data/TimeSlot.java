package examples.eAgenda.data;

import java.io.Serializable;
import java.util.Calendar;

/** Contains a time instant and a duration */
public class TimeSlot implements Cloneable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5372967361016894425L;
	int hour, minute, duration;
	Day startDay, stopDay;

	/** Cf Day for y,m and d parameters */
	public TimeSlot(final int y, final int m, final int d, final int hr, final int min, final int durations) {
		this(new Day(y,m,d), hr, min, durations);
	}
	/** Create a mew time slot from the specified moment and with the specified duration
	 *
	 * @param hr hour from 0 to 23
	 * @param min minutes from 0 to 59
	 * @param durations duration of the slot in number of mintute
	 */
	public TimeSlot(final Day d, final int hr, final int min, final int durations) {
		this.startDay = d;
		this.hour = hr;
		this.minute = min;
		this.duration = durations;
		this.stopDay = new Day(Day.getTimeMillis(this.startDay, this.hour, this.minute, this.duration));
	}
	public TimeSlot(final Day start, final int hr1, final int min1, final Day stop, final int hr2, final int min2) {
		this.startDay = start;
		this.hour = hr1;
		this.minute = min1;
		this.duration = (int)((Day.getTimeMillis(stop, hr2, min2, 0) - Day.getTimeMillis(start,hr1,min1, 0))/60000);
		this.stopDay = new Day(Day.getTimeMillis(this.startDay, this.hour, this.minute, this.duration));
	}
	public TimeSlot(final Calendar c, final int durations) {
		this(new Day(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH)), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), durations);
	}
	/** Does the day and the time slot intersec ? */
	public boolean contained(final Day d) {
		if (d.compareTo(this.startDay)<0 || d.compareTo(this.stopDay)>0) {
			return false;
		} else {
			return true;
		}
	}
	/** Get duration in minute */
	public int getDuration() {
		return this.duration;
	}
	/** How many minute this slot is used on this day and after */
	public int getDurationRemaining(final Day day) {
		if (day.compareTo(this.startDay)==0) {
			return this.duration;
		} else {
			return (int)((Day.getTimeMillis(this.startDay,this.hour,this.minute,0) - Day.getTimeMillis(day, 0, 0, 0))/60000)+this.duration;
		}
	}
	/** Return the number of minute passed within this day */
	public int getMinuteInDay() {
		return this.hour*60+this.minute;
	}
	public Day getStartDay() {
		return this.startDay;
	}
	public Day getStopDay() {
		return this.stopDay;
	}
	@Override
	public String toString() {
		return this.startDay.toString()+" at "+this.hour+":"+this.minute+" for "+this.duration+" min";
	}
}
