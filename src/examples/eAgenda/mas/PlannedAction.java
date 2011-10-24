package examples.eAgenda.mas;

import java.io.Serializable;

/** Use to encapsulate a function call that will occurs after a specified time */
public class PlannedAction implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7909693190145289132L;
	public static final int unspecifiedAction = -1;
	public static final int planMeetingPart2 = 0;

	long occuringTime;
	int action;
	long startMeeting;

	public PlannedAction(final long time, final int act, final long startMeet) {
		this.occuringTime = time;
		this.action = act;
		this.startMeeting=startMeet;
	}
	public int getAction() {
		return this.action;
	}

	public long getStartMeeting(){
	return this.startMeeting;
	}
	public boolean shouldBeExecuted() {
		return System.currentTimeMillis()>this.occuringTime;
	}
}
