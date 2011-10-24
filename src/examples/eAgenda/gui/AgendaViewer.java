package examples.eAgenda.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import examples.eAgenda.data.Agenda;

/** Show the activity of the agenda owner */
public class AgendaViewer extends JPanel implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -6210960855352544327L;

	ActivityViewer actualActivityViewer;

	// Popup interface on demand
	PersonalActivityInterface newPersonalActivityInterface;
	MeetingInterface newMeetingInteface;

	public AgendaViewer(final Agenda agenda) {
		super();
		this.setLayout(new BorderLayout());

		// Create the button bar at the bottom;
		final JPanel buttonBar = new JPanel();
		buttonBar.setLayout(new GridLayout(1,3));

		final JButton newPersonalActivityButton = new JButton("New Personal Activity");
		newPersonalActivityButton.setActionCommand("new personal activity");
		newPersonalActivityButton.addActionListener(this);
		buttonBar.add(newPersonalActivityButton);

		final JButton newMeetingButton = new JButton("New Meeting");
		newMeetingButton.setActionCommand("new meeting");
		newMeetingButton.addActionListener(this);
		buttonBar.add(newMeetingButton);

		this.add(buttonBar, BorderLayout.SOUTH);

		this.actualActivityViewer = new WeekActivityViewer(agenda);
		this.add(this.actualActivityViewer, BorderLayout.CENTER);

		// Create other frame for later use
		this.newPersonalActivityInterface = new PersonalActivityInterface(agenda);
		this.newMeetingInteface = new MeetingInterface(agenda);
	}
	@Override
	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();

		if (command.equals("new personal activity"))
			this.newPersonalActivityInterface.newActivity();
		else if (command.equals("new meeting"))
			this.newMeetingInteface.newMeeting();
	}
}
