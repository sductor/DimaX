package examples.eAgenda.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JComponent;

import examples.eAgenda.data.Activity;
import examples.eAgenda.data.Agenda;
import examples.eAgenda.data.Day;
import examples.eAgenda.data.PersonalActivity;
import examples.eAgenda.data.TimeSlot;
import examples.eAgenda.data.TimedActivity;

public class DayActivityViewer extends JComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 4760198637319415761L;
	Day myDay;
	Agenda myAgenda;
	int startTime, stopTime; // in hours
	String title;
	int myWeekDay;

	private final int upTitleHeight = 20;

	public DayActivityViewer(final Agenda agenda, final Day day) {
		super();
		this.setBackground(Color.white);
		this.setOpaque(true);

		this.myAgenda = agenda;
		this.setDay(day);

		// From the agenda, get the first usually available time of the day : THIS SHOULD be constant for all day through the week
		this.startTime = 7;// (int)(myAgenda.getStandardAvailability(myWeekDay).getFirstAvailableTime()/60);
		this.stopTime = 19;//(int)(myAgenda.getStandardAvailability(myWeekDay).getLastAvailableTime()/60)+1;
	}
	@Override
	public void paintComponent(final Graphics g) {

		// Clean everything up
		g.setColor(Color.white);
		g.fillRect(0,this.upTitleHeight,this.getWidth(), this.getHeight());

		final double oneHourPixel = (double)(this.getHeight()-this.upTitleHeight)/(this.stopTime-this.startTime);

		// Draw activities
		final ArrayList list = this.myAgenda.getActivities().getDayActivities(this.myDay);
		TimedActivity tact;
		TimeSlot time;
		Activity act;
		int yStart, yHeight;
		for (int i=0;i<list.size();i++)
		{
			tact = (TimedActivity)list.get(i);
			time = tact.getTimeSlot();

			if (time.getStartDay().compareTo(this.myDay) == 0) {
				yStart = (int)((time.getMinuteInDay()-this.startTime*60)*oneHourPixel/60)+this.upTitleHeight;
			} else {
				yStart = (int)(-this.startTime*oneHourPixel)+this.upTitleHeight;
			}

			yHeight = (int)(Math.min(1440, time.getDurationRemaining(this.myDay))*oneHourPixel/60);

			act = tact.getActivity();
			if (act instanceof PersonalActivity) {
				g.setColor(Color.yellow);
			} else {
				g.setColor(Color.green);
			}
			g.fillRect(3,yStart,this.getWidth()-3,yHeight);
			g.setColor(Color.lightGray);
			g.drawLine(3,yStart,this.getWidth(), yStart);
			g.drawLine(3,yStart+yHeight,this.getWidth(), yStart+yHeight);

			g.setColor(Color.black);
			g.setFont(g.getFont().deriveFont(10.0f));
			g.drawString(tact.getActivity().getTitle(), 7, yStart + 10);
		}

		// The upper part is for drawing the day
		g.setColor(Color.black);
		g.fillRect(0,0,this.getWidth(), this.upTitleHeight);
		g.setColor(Color.white);
		g.setFont(g.getFont().deriveFont(12.0f));
		g.drawString(this.title, 3, this.upTitleHeight - 4);

		// Draw on the left the layer
		g.setColor(Color.black);
		g.drawLine(1,0,1,this.getHeight());

		final int cycles = this.stopTime-this.startTime;
		g.setFont(g.getFont().deriveFont(9.0f));
		for (int i=0; i<cycles; i++)
		{
			final int y = (int)(this.upTitleHeight+i*oneHourPixel);
			if ((this.startTime+i)%2 == 0) {
				g.drawString(""+(this.startTime+i), 2, y+11);
			}
			g.drawLine(1,y, 5, y);
		}
	}
	public void setDay(final Day day) {
		this.myDay = day;
		this.myWeekDay = day.getWeekDayValue();
		this.title = this.myDay.getWeekDay()+" "+this.myDay.getDayInMonth();
	}
}
