package examples.eAgenda.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;

import examples.eAgenda.data.ContactList;
import examples.eAgenda.data.People;

/** A component that present two side of contact that can swap sides */
public class ContactListManager extends JPanel implements ActionListener {

	/**
	 *
	 */
	private static final long serialVersionUID = -1558708986440165962L;
	ContactList myReferenceList;
	List refList, choiceList;

	public ContactListManager(final ContactList ref) {
		this.myReferenceList = ref;

		this.setLayout(new BorderLayout());

		this.choiceList = new List();
		this.choiceList.setMultipleMode(true);
		this.refList = new List();
		this.refList.setMultipleMode(true);
		for(final Iterator it = this.myReferenceList.getAllPeople();it.hasNext();)
			this.refList.add(((People)it.next()).getName());

		this.add(this.choiceList, BorderLayout.WEST);
		this.add(this.refList, BorderLayout.EAST);

		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(5,1));

		final JButton addButton = new JButton("Add");
		addButton.setSize(100,30);
		addButton.setActionCommand("add");
		addButton.addActionListener(this);

		final JButton removeButton = new JButton("Del");
		removeButton.setSize(100,30);
		removeButton.setActionCommand("del");
		removeButton.addActionListener(this);

		buttonPanel.add(new JPanel());
		buttonPanel.add(addButton);
		buttonPanel.add(new JPanel());
		buttonPanel.add(removeButton);
		buttonPanel.add(new JPanel());

		this.add(buttonPanel, BorderLayout.CENTER);
	}
	@Override
	public void actionPerformed(final ActionEvent e) {
		final String command = e.getActionCommand();

		if (command.equals("add"))
		{
			final String[] selection = this.refList.getSelectedItems();
			for (final String element : selection)
				this.choiceList.add(element);
		}
		else if (command.equals("del"))
		{
			final String[] selection = this.choiceList.getSelectedItems();
			for (final String element : selection)
				this.choiceList.remove(element);
		}
	}
	public ContactList getSelectedPeople() {
		final ContactList res = new ContactList("Some Participants");

		final String[] selection = this.choiceList.getItems();

		for(final Iterator it = this.myReferenceList.getAllPeople();it.hasNext();) {
			final People possiblySelected = (People)it.next();
			final String possibleName = possiblySelected.getName();
			for (final String element : selection)
				if (possibleName.equals(element))
					res.addPeople(possiblySelected);
		}

		return res;
	}
}
