package examples.eAgenda.data;

import java.io.Serializable;
import java.util.ArrayList;

/** Structure for a meeting within the agenda */
public class Meeting extends Activity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -593275503402727257L;
	int[] durationSet;
	boolean selfNecessary;
	Day limitDay;
	People necessaryMembers;
	People otherMembers;
	/** Time in millisecond before wich the meeting should not take place */
	long startLimit;

	public Meeting() {
		this("","", new int[2], null, null, null, true, false, System.currentTimeMillis());
	}
	public Meeting(final String titl, final int mini, final int maxi, final Day valid, final People necessary, final People wished, final boolean movable) {
		this(titl, "",  null, valid, necessary, wished, true, movable, System.currentTimeMillis());
		this.setDuration(mini, maxi);
	}
	/** Create a meeting that may start from now */
	public Meeting(final String titl, final String descript, final int[] duration, final Day valid, final People necessary, final People wished, final boolean selfNecess, final boolean movable) {
		this(titl, descript, duration, valid, necessary, wished, selfNecess, movable, System.currentTimeMillis());
	}
	public Meeting(final String titl, final String descript, final int[] duration, final Day valid, final People necessary, final People wished, final boolean selfNecess, final boolean movable, final long sLimit) {
		super(titl, descript, movable);
		this.durationSet = duration;
		this.limitDay = valid;
		this.necessaryMembers = necessary;
		this.otherMembers = wished;
		this.selfNecessary = selfNecess;
		this.startLimit = sLimit;
	}
	public ArrayList getAllParticipants() {
		final ArrayList res = this.necessaryMembers.getCanonicalList();
		res.addAll(this.otherMembers.getCanonicalList());
		return res;
	}
	public ArrayList getNecessParticipants() {
	    if (this.necessaryMembers.getSize()!=0) {
		final ArrayList res = this.necessaryMembers.getCanonicalList();
		return res;}
	    else return new ArrayList();
	}

	public int[] getDurationSet() {
		return this.durationSet;
	}
	public Day getLimitDay() {
		return this.limitDay;
	}
	public People getNecessaryParticipants() {
		return this.necessaryMembers;
	}
	public People getOtherParticipants() {
		return this.otherMembers;
	}
	/** Time in millisecond before wich the meeting should not take place */
	public long getStartLimit() {
		return this.startLimit;
	}
	public boolean isSelfNecessary() {
		return this.selfNecessary;
	}
	public void setDuration(final int mini, final int maxi) {
		this.durationSet[0] = mini;
		this.durationSet[1] = maxi;
	}
	public void setLimitDay(final Day d) {
		this.limitDay = d;
	}
	public void setSelfNecessary(final boolean b) {
		this.selfNecessary = b;
	}
}
