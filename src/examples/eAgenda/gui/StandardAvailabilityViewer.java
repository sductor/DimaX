package examples.eAgenda.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import examples.eAgenda.data.Agenda;

public class StandardAvailabilityViewer extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = 3644211220680284597L;
	Agenda myAgenda;

	public StandardAvailabilityViewer(final Agenda agenda) {
		super();
		this.setLayout(new BorderLayout());
		this.myAgenda = agenda;
	}
}
