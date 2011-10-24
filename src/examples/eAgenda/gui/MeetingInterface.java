package examples.eAgenda.gui;

import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import examples.eAgenda.data.Agenda;
import examples.eAgenda.data.Day;
import examples.eAgenda.data.Meeting;

public class MeetingInterface extends JFrame implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -8226544179247664836L;

	Agenda myAgenda;

	JTextField titleField, durationField;
	TextArea descriptionArea;
	JCheckBox movableCheck;
	final boolean defaultSelection = true;
	JComboBox yearCombo, monthCombo, dayCombo;
	ContactListManager contacterNecessary, contacterOptional;

	public MeetingInterface(final Agenda agenda) {
		super("Specify new meeting");
		this.myAgenda = agenda;

		this.setupGUI();
	}
	@Override
	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		if (command.equals("ok"))
		{
			final int[] duration = new int[2];
			duration[0] = Integer.parseInt(this.durationField.getText());
			duration[1] = duration[0];
			final Day valid = new Day(Integer.parseInt((String)this.yearCombo.getSelectedItem()), this.monthCombo.getSelectedIndex()+1, Integer.parseInt((String)this.dayCombo.getSelectedItem()));

			final Meeting act = new Meeting(this.titleField.getText(), this.descriptionArea.getText(), duration, valid, this.contacterNecessary.getSelectedPeople(), this.contacterOptional.getSelectedPeople(), this.movableCheck.isSelected(), true);

			this.myAgenda.findTimeSlotFor(act);

			this.hide();
		}
	}
	public void newMeeting() {
		// Set all field by default

		// Show up if necessary
		this.show();
	}
	public void setupGUI() {

		this.setResizable(false);
		this.setSize(325,460);
		this.getContentPane().setLayout(null);
		//setDefaultCloseOperation(HIDE_ON_CLOSE);

		final JLabel titleLabel = new JLabel("Title:");
		titleLabel.setSize(70,20);
		titleLabel.setLocation(45, 5);
		this.getContentPane().add(titleLabel);

		this.titleField = new JTextField();
		this.titleField.setSize(200,20);
		this.titleField.setLocation(80, 5);
		this.getContentPane().add(this.titleField);

		final JLabel descriptionLabel = new JLabel("Description:");
		descriptionLabel.setSize(70,20);
		descriptionLabel.setLocation(5,30);
		this.getContentPane().add(descriptionLabel);

		this.descriptionArea = new TextArea();
		this.descriptionArea.setSize(230,80);
		this.descriptionArea.setLocation(80,30);
		this.getContentPane().add(this.descriptionArea);

		this.movableCheck = new JCheckBox("Is self necessary ?");
		this.movableCheck.setSize(140,20);
		this.movableCheck.setSelected(this.defaultSelection);
		this.movableCheck.setLocation(80,115);
		this.getContentPane().add(this.movableCheck);

		final JLabel necessaryLabel = new JLabel("Necessary participants(s)");
		necessaryLabel.setSize(150,20);
		necessaryLabel.setLocation(8, 140);
		this.getContentPane().add(necessaryLabel);

		this.contacterNecessary = new ContactListManager(this.myAgenda.getContactList());
		this.contacterNecessary.setSize(310,80);
		this.contacterNecessary.setLocation(5,160);
		this.getContentPane().add(this.contacterNecessary);

		final JLabel optionalLabel = new JLabel("Optional participant(s)");
		optionalLabel.setSize(150,20);
		optionalLabel.setLocation(8, 250);
		this.getContentPane().add(optionalLabel);

		this.contacterOptional = new ContactListManager(this.myAgenda.getContactList());
		this.contacterOptional.setSize(310,80);
		this.contacterOptional.setLocation(5,270);
		this.getContentPane().add(this.contacterOptional);

		final JLabel durationLabel = new JLabel("Duration:");
		durationLabel.setSize(80,20);
		durationLabel.setLocation(8, 355);
		this.getContentPane().add(durationLabel);

		this.durationField = new JTextField();
		this.durationField.setText("60");
		this.durationField.setSize(200,20);
		this.durationField.setLocation(80, 355);
		this.getContentPane().add(this.durationField);

		final JLabel limiteDateLabel = new JLabel("Limit Date:");
		limiteDateLabel.setSize(80,20);
		limiteDateLabel.setLocation(8, 380);
		this.getContentPane().add(limiteDateLabel);

		final Day proposedLimit = Day.forwardedDay(Day.today(), 30);

		final String[] values1 = {"2001", "2002", "2003", "2004", "2005"};
		this.yearCombo = new JComboBox(values1);
		this.yearCombo.setSelectedIndex(proposedLimit.getYear()-2001);
		this.yearCombo.setSize(57,20);
		this.yearCombo.setLocation(195,380);
		this.getContentPane().add(this.yearCombo);

		final String[] values2 = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		this.monthCombo = new JComboBox(values2);
		this.monthCombo.setSelectedIndex(proposedLimit.getMonth());
		this.monthCombo.setSize(60,20);
		this.monthCombo.setLocation(135,380);
		this.getContentPane().add(this.monthCombo);

		final String[] values3 = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
		this.dayCombo = new JComboBox(values3);
		this.dayCombo.setSelectedIndex(proposedLimit.getDayInMonth()-1);
		this.dayCombo.setSize(50,20);
		this.dayCombo.setLocation(85,380);
		this.getContentPane().add(this.dayCombo);

		final JButton okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		okButton.setSize(300,25);
		okButton.setLocation(10,405);
		this.getContentPane().add(okButton);

	}
}
