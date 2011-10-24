package examples.preypredators;

/**
 * Insert the type's description here.
 * Creation date: (04/11/00 11:27:23)
 * @author: Administrator
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

public class PredProieAPI extends Frame implements ActionListener, WindowListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 6501708321591111598L;
	private Button ivjBStart;
	private Button ivjBExit;
	private Button ivjBStop;

	private Panel ivjContentsPane;
	private Label ivjLabel1;
	private Label ivjLabel2;
	private Label ivjLabel3;

	private TextField ivjTNumberOfPredators;
	private TextField ivjTNumberOfPreys;
	private TextField ivjTGridSize;

	private static Grid ivjGrid1;
	private int numberOfPredators;
	private int numberOfPreys;
	private int GridSize;

	public World world;



	//private static int Tableau[][];
/**
 * FramePredProie constructor comment.
 */
public PredProieAPI() {
	this.ivjBStart = null;
	this.ivjBExit = null;
	this.ivjBStop = null;
	this.ivjContentsPane = null;
	this.ivjLabel1 = null;
	this.ivjLabel2 = null;
	this.ivjLabel3 = null;
	this.GridSize = 1;
	ivjGrid1 = getGrid1();
	this.ivjTNumberOfPredators = null;
	this.ivjTNumberOfPreys = null;
	this.ivjTGridSize = null;
	this.numberOfPredators = 10;
	this.numberOfPreys = 10;
	this.initialize();
}
	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource() == this.getBStart())
			this.connEtoStart(e);
		if(e.getSource() == this.getBQuitter())
			this.connEtoQuitter(e);
		if(e.getSource() == this.getBStop())
			this.connEtoStop(e);
	}
	public void bStart_ActionPerformed(final ActionEvent actionEvent) {

		final ActionEvent actionEvent1 = null;

		this.GridSize = Integer.parseInt(this.getTTailleGrille().getText());
		getGrid1().setTailleGrille(this.GridSize);
		getGrid1().repaint();

		this.numberOfPredators = Integer.parseInt(this.getTNombrePred().getText());
		this.numberOfPreys = Integer.parseInt(this.getTNombreProies().getText());
		this.world = new World(this.numberOfPredators, this.numberOfPreys, this.GridSize);
		this.world.initialise();
	}
	public void bStop_ActionPerformed(final ActionEvent actionEvent) {
		int i;
		for (i=0;i< this.world.animal.size();i++)
			if ( this.world.animal.elementAt(i) instanceof Predator )
				((Predator) this.world.animal.elementAt(i)).isAlive = false;
			else
				((Food) this.world.animal.elementAt(i)).isAlive = false;
		getGrille().setTailleGrille(1);
		this.GridSize = 1;
		getGrille().repaint();
		ivjGrid1.setBackground(Color.lightGray);
	}
private void connEtoQuitter(final ActionEvent arg1) {
		try {
			final ActionEvent actionEvent1 = null;
			this.bStop_ActionPerformed(actionEvent1);
			this.dispose();
		}
		catch(final Throwable ivjExc) {
			this.handleException(ivjExc);
		}
	}
	private void connEtoStart(final ActionEvent arg1) {
		try {
			this.bStart_ActionPerformed(arg1);
		}
		catch(final Throwable ivjExc) {
			this.handleException(ivjExc);
		}
	}
	private void connEtoStop(final ActionEvent arg1) {
		try {
			this.bStop_ActionPerformed(arg1);
		}
		catch(final Throwable ivjExc) {
			this.handleException(ivjExc);
		}
	}
private Button getBQuitter() {
		if(this.ivjBExit == null)
			try {
				this.ivjBExit = new Button();
				this.ivjBExit.setName("BExit");
				this.ivjBExit.setBounds(35, 77, 56, 23);
				this.ivjBExit.setLabel("Exit");
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		return this.ivjBExit;
	}
	private Button getBStart() {
		if(this.ivjBStart == null)
			try {
				this.ivjBStart = new Button();
				this.ivjBStart.setName("BStart");
				this.ivjBStart.setBounds(35, 23, 56, 23);
				this.ivjBStart.setLabel("Start");
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		return this.ivjBStart;
	}
	private Button getBStop() {
		if(this.ivjBStop == null)
			try {
				this.ivjBStop = new Button();
				this.ivjBStop.setName("BStop");
				this.ivjBStop.setBounds(35, 50, 56, 23);
				this.ivjBStop.setLabel("Stop");
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		return this.ivjBStop;
	}
	private Panel getContentsPane() {
		if(this.ivjContentsPane == null)
			try {
				this.ivjContentsPane = new Panel();
				this.ivjContentsPane.setName("ContentsPane");
				this.ivjContentsPane.setLayout(null);
				this.getContentsPane().add(this.getGrille1(), this.getGrille1().getName());
				this.getContentsPane().add(this.getBStart(), this.getBStart().getName());
				this.getContentsPane().add(this.getBStop(), this.getBStop().getName());
				this.getContentsPane().add(this.getBQuitter(), this.getBQuitter().getName());
				this.getContentsPane().add(this.getLabel1(), this.getLabel1().getName());
				this.getContentsPane().add(this.getLabel2(), this.getLabel2().getName());
				this.getContentsPane().add(this.getLabel3(), this.getLabel3().getName());
				this.getContentsPane().add(this.getTNombrePred(), this.getTNombrePred().getName());
				this.getContentsPane().add(this.getTNombreProies(), this.getTNombreProies().getName());
				this.getContentsPane().add(this.getTTailleGrille(), this.getTTailleGrille().getName());

			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		return this.ivjContentsPane;
	}
	public static Grid getGrid1() {
		return ivjGrid1;
	}
	public static Grid getGrille() {
		return ivjGrid1;
	}
	private Grid getGrille1() {
		if(ivjGrid1 == null)
			try {
				ivjGrid1 = new Grid(this.GridSize);
				ivjGrid1.setName("Grid1");
				ivjGrid1.setLayout(null);
				ivjGrid1.setBackground(Color.lightGray);
				ivjGrid1.setBounds(30, 110, 600, 600);
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		return ivjGrid1;
	}
	private Label getLabel1() {
		if(this.ivjLabel1 == null)
			try {
				this.ivjLabel1 = new Label();
				this.ivjLabel1.setName("Label1");
				this.ivjLabel1.setText("Number of Predators");
				this.ivjLabel1.setBounds(120, 22, 125, 23);
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		return this.ivjLabel1;
	}
	private Label getLabel2() {
		if(this.ivjLabel2 == null)
			try {
				this.ivjLabel2 = new Label();
				this.ivjLabel2.setName("Label1");
				this.ivjLabel2.setText("Number of Preys");
				this.ivjLabel2.setBounds(120, 52, 125, 23);
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		return this.ivjLabel2;
	}
	private Label getLabel3() {
		if(this.ivjLabel3 == null)
			try {
				this.ivjLabel3 = new Label();
				this.ivjLabel3.setName("Label3");
				this.ivjLabel3.setText("Grid Size");
				this.ivjLabel3.setBounds(120, 82, 125, 23);
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		return this.ivjLabel3;
	}
	private TextField getTNombrePred() {
		if(this.ivjTNumberOfPredators == null)
			try {
				this.ivjTNumberOfPredators = new TextField();
				this.ivjTNumberOfPredators.setName("TNumberOfPredators");
				this.ivjTNumberOfPredators.setBounds(249, 23, 51, 23);
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		return this.ivjTNumberOfPredators;
	}
	private TextField getTNombreProies() {
		if(this.ivjTNumberOfPreys == null)
			try {
				this.ivjTNumberOfPreys = new TextField();
				this.ivjTNumberOfPreys.setName("TNumberOfPreys");
				this.ivjTNumberOfPreys.setBounds(249, 53, 51, 23);
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		return this.ivjTNumberOfPreys;
	}
	private TextField getTTailleGrille() {
		if(this.ivjTGridSize == null)
			try {
				this.ivjTGridSize = new TextField();
				this.ivjTGridSize.setName("TGridSize");
				this.ivjTGridSize.setBounds(249, 83, 51, 23);
			}
			catch(final Throwable ivjExc) {
				this.handleException(ivjExc);
			}
		return this.ivjTGridSize;
	}
	private void handleException(final Throwable throwable) {
	}
	private void initConnections() {
		this.addWindowListener(this);
		this.getBStart().addActionListener(this);
		this.getBStop().addActionListener(this);
		this.getBQuitter().addActionListener(this);
	}
	public void initialize() {
		this.setName("Frame Preys Predators");
		this.setLayout(new BorderLayout());
		this.setSize(683, 740);
		this.setResizable(false);
		this.add(this.getContentsPane(), "Center");
		this.initConnections();
	}
	public static void main(final String args[]) {
		try {
			final PredProieAPI aFramePredProie = new PredProieAPI();
			try {
				final Class aCloserClass = Class.forName("com.ibm.uvm.abt.edit.WindowCloser");
				final Class parmTypes[] = {
					java.awt.Window.class
				};
				final Object parms[] = {
					aFramePredProie
				};
				final Constructor aCtor = aCloserClass.getConstructor(parmTypes);
				aCtor.newInstance(parms);
			}
			catch(final Throwable _ex) { }
			aFramePredProie.setVisible(true);
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
	public void windowClosing(final WindowEvent windowevent) {
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
