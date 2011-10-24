package examples.eAgenda.gui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import examples.eAgenda.data.Agenda;

public class AgendaInterface extends JFrame {

	/**
	 *
	 */
	private static final long serialVersionUID = 4703349357937082559L;
	public AgendaInterface(final Agenda agenda) {
		super("My E-Agenda, ready to serve");
		this.setSize(600,400);

		/*addWindowListener(new java.awt.event.WindowAdapter () {
		      public void windowClosing(java.awt.event.WindowEvent evt) { close();}    }
		);*/
		//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		final JTabbedPane mainPane = new JTabbedPane();

		final Component agendaPanel = new AgendaViewer(agenda);
		mainPane.addTab("Agenda", null, agendaPanel, "Consult agenda");

		final Component availabilityPanel = new StandardAvailabilityViewer(agenda);
		mainPane.addTab("Default Availability", null, availabilityPanel, "View and edit usual weekly availability");

		this.getContentPane().add(mainPane, BorderLayout.CENTER);

		System.out.println("Finished to create this interface");
	}
	public void close() {
		this.hide();
		this.dispose();
	}
}
