package examples.eAgenda.data;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Day implements Comparable, Cloneable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8187952233933117027L;
	int year, month, day;
	Calendar draft = Calendar.getInstance();
	static Calendar draftS = Calendar.getInstance();

	/** Create a mew day structure
	 *
	 * @param y year like 1996 or 2001
	 * @param m month from 1 to 12 (like Calendar.get(Calendar.MONTH) + 1)
	 * @param d day in month from 1 to 31
	 */
	public Day(final int y, final int m, final int d) {
		this.year = y;
		this.month = m;
		this.day = d;
	}
	public Day(final long timeMillis) {
		this.draft.setTime(new Date(timeMillis));
		this.year = this.draft.get(Calendar.YEAR);
		this.month = this.draft.get(Calendar.MONTH)+1;
		this.day = this.draft.get(Calendar.DAY_OF_MONTH);
	}
	@Override
	public int compareTo(final Object o) {
		final Day d = (Day)o;

		if (this.year<d.year)
			return -1;
		else if (this.year>d.year)
			return 1;
		else if (this.month<d.month)
			return -1;
		else if (this.month>d.month)
			return 1;
		else if (this.day<d.day)
			return -1;
		else if (this.day>d.day)
			return 1;
		else return 0;
	}
	public boolean equals(final Day d) {
		return this.year == d.getYear() && this.month == d.getMonth() && this.day == d.getDayInMonth();
	}
	public static Day forwardedDay(final Day d, final int days) {
		Day.draftS.set(d.year, d.month-1, d.day);
		Day.draftS.add(Calendar.DAY_OF_MONTH, days);
		return new Day(Day.draftS.get(Calendar.YEAR), Day.draftS.get(Calendar.MONTH)+1, Day.draftS.get(Calendar.DAY_OF_MONTH));
	}
	public int getDayInMonth() {
		return this.day;
	}
	public int getMonth() {
		return this.month;
	}
	public static long getTimeMillis(final Day d, final int hour, final int min, final int offset) {
		Day.draftS.set(d.year, d.month-1, d.day, hour, min+offset, 0);
		return Day.draftS.getTime().getTime();
	}
	public String getWeekDay() {
		this.draft.set(this.year, this.month-1, this.day);

		switch (this.draft.get(Calendar.DAY_OF_WEEK))
		{
			case Calendar.SUNDAY:
				return "Sunday";
			case Calendar.MONDAY:
				return "Monday";
			case Calendar.TUESDAY:
				return "Tuesday";
			case Calendar.WEDNESDAY:
				return "Wednesday";
			case Calendar.THURSDAY:
				return "Thursday";
			case Calendar.FRIDAY:
				return "Friday";
			case Calendar.SATURDAY:
				return "Saturday";
			default:
				return "Unknown";
		}
	}
	public int getWeekDayValue() {
		this.draft.set(this.year, this.month-1, this.day);

		switch (this.draft.get(Calendar.DAY_OF_WEEK))
		{
			case Calendar.SUNDAY:
				return 0;
			case Calendar.MONDAY:
				return 1;
			case Calendar.TUESDAY:
				return 2;
			case Calendar.WEDNESDAY:
				return 3;
			case Calendar.THURSDAY:
				return 4;
			case Calendar.FRIDAY:
				return 5;
			case Calendar.SATURDAY:
				return 6;
			default:
				return -1;
		}
	}
	public int getYear() {
		return this.year;
	}
	public static Day today() {
		final Calendar c = Calendar.getInstance();
		return new Day(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH));
	}
	@Override
	public String toString() {
		return ""+this.day+"/"+this.month+"/"+this.year;
	}
}
