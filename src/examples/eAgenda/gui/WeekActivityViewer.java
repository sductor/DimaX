package examples.eAgenda.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import examples.eAgenda.data.Agenda;
import examples.eAgenda.data.Day;

public class WeekActivityViewer extends ActivityViewer implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 3120608329152772228L;
	DayActivityViewer[] days;
	Day startDay;

	public WeekActivityViewer(final Agenda agenda) {
		this.setLayout(new BorderLayout());

		this.days = new DayActivityViewer[7];

		// Should become the first sunday from the current date
		final Day today = Day.today();
		// Start day should be a sunday
		this.startDay = Day.forwardedDay(today, -today.getWeekDayValue());

		for (int i=0;i<7;i++)
			this.days[i] = new DayActivityViewer(agenda, Day.forwardedDay(this.startDay,i));

		this.setupGUI();
	}
	@Override
	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();

		if (command.equals("reward"))
		{
			this.startDay = Day.forwardedDay(this.startDay, -7);
			for (int i=0;i<7;i++)
				this.days[i].setDay(Day.forwardedDay(this.startDay, i));
			this.repaint();
		}
		else if (command.equals("forward"))
		{
			this.startDay = Day.forwardedDay(this.startDay, 7);
			for (int i=0;i<7;i++)
				this.days[i].setDay(Day.forwardedDay(this.startDay, i));
			this.repaint();
		}
		else if (command.equals("up"))
		{
		}
		else if (command.equals("down"))
		{
		}

	}
	public void setupGUI() {

		// This component should assure srolling (vertical and horizontale)
		final JPanel scrollHButton = new JPanel();
		scrollHButton.setLayout(new GridLayout(1,2));

		final JButton scrollBeforeButton = new JButton("- 1 Week");
		scrollBeforeButton.setActionCommand("reward");
		scrollBeforeButton.addActionListener(this);
		scrollHButton.add(scrollBeforeButton);

		final JButton scrollAfterButton = new JButton("+ 1 Week");
		scrollAfterButton.setActionCommand("forward");
		scrollAfterButton.addActionListener(this);
		scrollHButton.add(scrollAfterButton);

		this.add(scrollHButton, BorderLayout.SOUTH);

		final JPanel weekPanel = new JPanel();
		weekPanel.setLayout(new BorderLayout());

		// Add up and down button here
		/*JButton scrollUpButton = new JButton("Up");
		scrollUpButton.setActionCommand("up");
		scrollUpButton.addActionListener(this);
		weekPanel.add(scrollUpButton, BorderLayout.NORTH);

		JButton scrollDownButton = new JButton("Down");
		scrollDownButton.setActionCommand("down");
		scrollDownButton.addActionListener(this);
		weekPanel.add(scrollDownButton, BorderLayout.SOUTH);
		*/

		final JPanel daysPanel = new JPanel();
		daysPanel.setLayout(new GridLayout(1,7,5,5));
		for (int i=0;i<7;i++)
			daysPanel.add(this.days[i]);

		weekPanel.add(daysPanel, BorderLayout.CENTER);

		this.add(weekPanel, BorderLayout.CENTER);
	}
}
