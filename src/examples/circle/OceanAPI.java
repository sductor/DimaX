package examples.circle;

/**
 * an example class : a Frame containing a panel (Sea) with Agents Moving on it.
 * The main method to see here is the bGo_ActionPerformed() method.
 * This exemple implements a set of agents with a very simple goal : goToaCircle. It aims to illustrate the proactiveComponents.
 *
 */

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Constructor;


public class OceanAPI extends Frame
implements ActionListener, WindowListener {

	/**
	 *
	 */
	private static final long serialVersionUID = 253083997500990483L;
	private Button ivjBGo;
	private Button ivjBExit;
	private Panel ivjContentsPane;
	private Label ivjLabel1;
	private static Sea ivjMer1;
	private TextField ivjTNumber;
	private int numbreOfSharks;


	public OceanAPI() {
		this.ivjBGo = null;
		this.ivjBExit = null;
		this.ivjContentsPane = null;
		this.ivjLabel1 = null;
		OceanAPI.ivjMer1 = this.getMer1();
		this.ivjTNumber = null;
		this.initialize();
	}
	public OceanAPI(final String title) {
		super(title);
		this.ivjBGo = null;
		this.ivjBExit = null;
		this.ivjContentsPane = null;
		this.ivjLabel1 = null;
		OceanAPI.ivjMer1 = this.getMer1();
		this.ivjTNumber = null;
	}
	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource() == this.getBGo()) {
			this.connEtoC2(e);
		}
		if(e.getSource() == this.getBExit()) {
			this.connEtoM1(e);
		}
	}
	public void bGo_ActionPerformed(final ActionEvent actionEvent) {
		this.numbreOfSharks = Integer.parseInt(this.getTNumber().getText());

		final java.util.Vector faune = new java.util.Vector();
		for (int i=0; i<this.numbreOfSharks;i++)
		{   final Shark a = new Shark();
		faune.addElement(a);
		a.activate();}

	}
	private void connEtoC1(final WindowEvent arg1) {
		try {
			this.dispose();
		}
		catch(final Throwable ivjExc) {
			this.handleException(ivjExc);
		}
	}
	private void connEtoC2(final ActionEvent arg1) {
		try {
			this.bGo_ActionPerformed(arg1);
		}
		catch(final Throwable ivjExc) {
			this.handleException(ivjExc);
		}
	}
	private void connEtoM1(final ActionEvent arg1) {
		try {
			this.dispose();
		}
		catch(final Throwable ivjExc) {
			this.handleException(ivjExc);
		}
	}
	private Button getBExit() {
		if(this.ivjBExit == null) {
			try {
				this.ivjBExit = new Button();
				this.ivjBExit.setName("BExit");
				this.ivjBExit.setBounds(37, 62, 56, 23);
				this.ivjBExit.setLabel("Exit");
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		}
		return this.ivjBExit;
	}
	private Button getBGo() {
		if(this.ivjBGo == null) {
			try {
				this.ivjBGo = new Button();
				this.ivjBGo.setName("BGo");
				this.ivjBGo.setBounds(35, 23, 56, 23);
				this.ivjBGo.setLabel("Go");
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		}
		return this.ivjBGo;
	}
	private Button getBQuitter() {
		if(this.ivjBExit == null) {
			try {
				this.ivjBExit = new Button();
				this.ivjBExit.setName("BExit");
				this.ivjBExit.setBounds(37, 62, 56, 23);
				this.ivjBExit.setLabel("Exit");
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		}
		return this.ivjBExit;
	}
	private Panel getContentsPane() {
		if(this.ivjContentsPane == null) {
			try {
				this.ivjContentsPane = new Panel();
				this.ivjContentsPane.setName("ContentsPane");
				this.ivjContentsPane.setLayout(null);
				this.getContentsPane().add(this.getMer1(), this.getMer1().getName());
				this.getContentsPane().add(this.getBGo(), this.getBGo().getName());
				this.getContentsPane().add(this.getLabel1(), this.getLabel1().getName());
				this.getContentsPane().add(this.getTNumber(), this.getTNumber().getName());
				this.getContentsPane().add(this.getBExit(), this.getBExit().getName());
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		}
		return this.ivjContentsPane;
	}
	private Label getLabel1() {
		if(this.ivjLabel1 == null) {
			try {
				this.ivjLabel1 = new Label();
				this.ivjLabel1.setName("Label1");
				this.ivjLabel1.setText("Number of Sharks");
				this.ivjLabel1.setBounds(108, 22, 125, 23);
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		}
		return this.ivjLabel1;
	}
	private Sea getMer1() {
		if(OceanAPI.ivjMer1 == null) {
			try {
				OceanAPI.ivjMer1 = new Sea();
				OceanAPI.ivjMer1.setName("Mer1");
				OceanAPI.ivjMer1.setLayout(null);
				OceanAPI.ivjMer1.setBackground(Color.cyan);
				OceanAPI.ivjMer1.setBounds(17, 110, 340, 276);
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		}
		return OceanAPI.ivjMer1;
	}
	public int getnumbreOfSharks() {
		return this.numbreOfSharks;
	}
	public static Sea getSea() {

		return OceanAPI.ivjMer1;
	}
	private TextField getTNumber() {
		if(this.ivjTNumber == null) {
			try {
				this.ivjTNumber = new TextField();
				this.ivjTNumber.setName("TNumber");
				this.ivjTNumber.setBounds(249, 23, 51, 23);
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		}
		return this.ivjTNumber;
	}
	private void handleException(final Throwable throwable) {
	}
	private void initConnections() {
		this.addWindowListener(this);
		this.getBGo().addActionListener(this);
		this.getBQuitter().addActionListener(this);
	}
	public void initialize() {
		this.setName("Ocean");
		this.setLayout(new BorderLayout());
		this.setSize(383, 440);
		this.setResizable(false);
		this.add(this.getContentsPane(), "Center");
		this.initConnections();
		this.getMer1().fixerRayon(100);
	}
	public static void main(final String args[]) {
		try {
			final OceanAPI aOcean = new OceanAPI();
			try {
				final Class aCloserClass = Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
				final Class parmTypes[] = {
						java.awt.Window.class
				};
				final Object parms[] = {
						aOcean
				};
				final Constructor aCtor = aCloserClass.getConstructor(parmTypes);
				aCtor.newInstance(parms);
			}
			catch(final Throwable _ex) { }
			aOcean.setVisible(true);
		}
		catch(final Throwable exception) {
			System.err.println("Exception occurred in main() of java.awt.Frame");
			exception.printStackTrace(System.out);
		}
	}
	@Override
	public void windowActivated(final WindowEvent windowevent) {
	}
	@Override
	public void windowClosed(final WindowEvent windowevent) {
	}
	@Override
	public void windowClosing(final WindowEvent e) {
		if(e.getSource() == this) {
			this.connEtoC1(e);
		}
	}
	@Override
	public void windowDeactivated(final WindowEvent windowevent) {
	}
	@Override
	public void windowDeiconified(final WindowEvent windowevent) {
	}
	@Override
	public void windowIconified(final WindowEvent windowevent) {
	}
	@Override
	public void windowOpened(final WindowEvent windowevent) {
	}
}
