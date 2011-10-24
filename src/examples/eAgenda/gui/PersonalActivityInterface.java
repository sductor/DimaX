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

import examples.eAgenda.data.Activity;
import examples.eAgenda.data.Agenda;
import examples.eAgenda.data.Day;
import examples.eAgenda.data.PersonalActivity;
import examples.eAgenda.data.TimeSlot;

public class PersonalActivityInterface extends JFrame implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 9081536066997314804L;

	Agenda myAgenda;

	JTextField titleField;
	TextArea descriptionArea;
	JCheckBox movableCheck;
	final boolean defaultSelection = false;
	JComboBox yearCombo, monthCombo, dayCombo, hourCombo, minuteCombo;
	JComboBox yearCombo2, monthCombo2, dayCombo2, hourCombo2, minuteCombo2;

	public PersonalActivityInterface(final Agenda agenda) {
		super("Specify new activity");
		this.myAgenda = agenda;

		this.setupGUI();
	}
	@Override
	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();
		if (command.equals("ok"))
		{
			final Activity act = new PersonalActivity(this.titleField.getText(), this.descriptionArea.getText(), this.movableCheck.isSelected());

			final Day start = new Day(Integer.parseInt((String)this.yearCombo.getSelectedItem()), this.monthCombo.getSelectedIndex()+1, Integer.parseInt((String)this.dayCombo.getSelectedItem()));
			final Day stop = new Day(Integer.parseInt((String)this.yearCombo2.getSelectedItem()), this.monthCombo2.getSelectedIndex()+1, Integer.parseInt((String)this.dayCombo2.getSelectedItem()));
			final TimeSlot slot = new TimeSlot(start,Integer.parseInt((String)this.hourCombo.getSelectedItem()), Integer.parseInt((String)this.minuteCombo.getSelectedItem()), stop,Integer.parseInt((String)this.hourCombo2.getSelectedItem()), Integer.parseInt((String)this.minuteCombo2.getSelectedItem()));

			this.myAgenda.addActivity(act, slot);

			this.hide();
		}
	}
	public void newActivity() {
		// Set all field by default
		this.titleField.setText("");
		this.descriptionArea.setText("");
		this.movableCheck.setSelected(this.defaultSelection);

		// Show up if necessary
		this.show();
	}
	public void setupGUI() {
		this.setResizable(false);
		this.setSize(300,250);
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
		this.descriptionArea.setSize(200,80);
		this.descriptionArea.setLocation(80,30);
		this.getContentPane().add(this.descriptionArea);

		this.movableCheck = new JCheckBox("Moveable");
		this.movableCheck.setSize(100,20);
		this.movableCheck.setSelected(this.defaultSelection);
		this.movableCheck.setLocation(80,115);
		this.getContentPane().add(this.movableCheck);

		final String[] values1 = {"2001", "2002", "2003", "2004", "2005"};
		this.yearCombo = new JComboBox(values1);
		this.yearCombo.setSelectedIndex(Day.today().getYear()-2001);
		this.yearCombo.setSize(57,20);
		this.yearCombo.setLocation(115,140);
		this.getContentPane().add(this.yearCombo);

		final String[] values2 = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		this.monthCombo = new JComboBox(values2);
		this.monthCombo.setSelectedIndex(Day.today().getMonth()-1);
		this.monthCombo.setSize(60,20);
		this.monthCombo.setLocation(55,140);
		this.getContentPane().add(this.monthCombo);

		final String[] values3 = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
		this.dayCombo = new JComboBox(values3);
		this.dayCombo.setSelectedIndex(Day.today().getDayInMonth()-1);
		this.dayCombo.setSize(50,20);
		this.dayCombo.setLocation(5,140);
		this.getContentPane().add(this.dayCombo);

		final String[] values4 = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
		this.hourCombo = new JComboBox(values4);
		this.hourCombo.setSelectedIndex(12);
		this.hourCombo.setSize(50,20);
		this.hourCombo.setLocation(180,140);
		this.getContentPane().add(this.hourCombo);

		final String[] values5 = {"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};
		this.minuteCombo = new JComboBox(values5);
		this.minuteCombo.setSelectedIndex(0);
		this.minuteCombo.setSize(50,20);
		this.minuteCombo.setLocation(230,140);
		this.getContentPane().add(this.minuteCombo);

		this.yearCombo2 = new JComboBox(values1);
		this.yearCombo2.setSelectedIndex(Day.today().getYear()-2001);
		this.yearCombo2.setSize(57,20);
		this.yearCombo2.setLocation(115,165);
		this.getContentPane().add(this.yearCombo2);

		this.monthCombo2 = new JComboBox(values2);
		this.monthCombo2.setSelectedIndex(Day.today().getMonth()-1);
		this.monthCombo2.setSize(60,20);
		this.monthCombo2.setLocation(55,165);
		this.getContentPane().add(this.monthCombo2);

		this.dayCombo2 = new JComboBox(values3);
		this.dayCombo2.setSelectedIndex(Day.today().getDayInMonth()-1);
		this.dayCombo2.setSize(50,20);
		this.dayCombo2.setLocation(5,165);
		this.getContentPane().add(this.dayCombo2);

		this.hourCombo2 = new JComboBox(values4);
		this.hourCombo2.setSelectedIndex(14);
		this.hourCombo2.setSize(50,20);
		this.hourCombo2.setLocation(180,165);
		this.getContentPane().add(this.hourCombo2);

		this.minuteCombo2 = new JComboBox(values5);
		this.minuteCombo2.setSelectedIndex(0);
		this.minuteCombo2.setSize(50,20);
		this.minuteCombo2.setLocation(230,165);
		this.getContentPane().add(this.minuteCombo2);

		final JButton okButton = new JButton("OK");
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		okButton.setSize(220,25);
		okButton.setLocation(40,195);
		this.getContentPane().add(okButton);
	}
}
