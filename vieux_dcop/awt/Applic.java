// --------------------------------------------------------------------------
// $Id: Applic.java,v 1.3 1997/11/03 10:25:05 schreine Exp $
// execution framework for application
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package frameworks.dcop.daj.awt;

import java.awt.Button;
import java.awt.Font;

import vieux.Network;
import vieux.Scheduler;
import vieux.dcop_old.agent.AgentNode;
import vieux.dcop_old.daj.Application;
import vieux.dcop_old.daj.Assertion;
import vieux.dcop_old.daj.Channel;
import vieux.dcop_old.daj.Program;
import vieux.dcop_old.daj.Selector;
import vieux.dcop_old.daj.SelectorDefault;


public abstract class Applic /*extends Applet implements ActionListener*/ {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1165911650759353403L;
	// the network being executed
	protected Network network;
	// visualization on or off?
	private boolean visualized;
	// visualization information
	private String title;
	private int width;
	private int height;
	// executing as applet?
	private boolean isAppl;
	/*
	 * paths to browser
	 */
	// public String browserCommand = "/usr/local/bin/netscape";
	public String browserCommand = "";
	// global variables that may be customized by programmer
	public int nodeRadius = 19;
	public Font nodeNormalFont = new Font("Helvetica", Font.BOLD, 12);
	public Font nodeSmallFont = new Font("Helvetica", Font.PLAIN, 10);
	public int channelWidth = 3;
	public int channelRadius = 6;
	public Selector defaultSelector = new SelectorDefault();
	// button for starting applet
	private Button button;

	// --------------------------------------------------------------------------
	// run application without visualization
	// --------------------------------------------------------------------------
	public Applic() {
		visualized = false;
		network = new Network((Application) this);
	}

	// --------------------------------------------------------------------------
	// run application with visualization titled 't' and screen of size 'x/y'
	// --------------------------------------------------------------------------
	public Applic(String t, int x, int y) {
		visualized = true;
		title = t;
		width = x;
		height = y;
	}

	// --------------------------------------------------------------------------
	//
	// the following methods are used internally to control the application
	//
	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	// executed when run in standalone mode; execute network in current thread
	// --------------------------------------------------------------------------
	public void run() {
		isAppl = false;
		if(visualized){
			network =
				new Network((Application) this, title, width, height, 100, 100, false, 0, 0);
		}
		else{
			network = new Network((Application) this);
		}
		construct();
		if (visualized) start();
		network.run();
	}

	// --------------------------------------------------------------------------
	// executed when run in applet mode; create button to start application
	// --------------------------------------------------------------------------
	/*public void init() {
		// create button; use parameters to determine its label
		button = new Button(title);
		String bname = getParameter("buttonLabel");
		if (bname != null) {
			button.setLabel(bname);
		}
		// use parameters to determine button font
		Font font = getFont();
		String name = font.getName();
		String fname = getParameter("fontName");
		if (fname != null) {
			name = fname;
		}
		int style = font.getStyle();
		String fstyle = getParameter("fontStyle");
		if (fstyle != null) {
			if (fstyle.equalsIgnoreCase("bold")) style = Font.BOLD;
			else if (fstyle.equalsIgnoreCase("italic")) style = Font.ITALIC;
			else if (fstyle.equalsIgnoreCase("plain")) style = Font.PLAIN;
			else Assertion.fail("invalid font style");
		}
		int size = font.getSize();
		String fsize = getParameter("fontSize");
		if (fsize != null) {
			try {
				size = Integer.parseInt(fsize);
			}
			catch (NumberFormatException e) {
				Assertion.fail("invalid font size");
			}
		}
		font = new Font(name, style, size);
		button.setFont(font);
		// layout button to cover all the applet space
		GridBagLayout gridbag = new GridBagLayout();
		setLayout(gridbag);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		gridbag.setConstraints(button, c);
		add(button);
		// register event handler for button
		button.addActionListener(this);
	}

	// --------------------------------------------------------------------------
	// called when button is pressed
	// --------------------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == button) {
			startApplet();
			button.setEnabled(false);
		}
	}

	// --------------------------------------------------------------------------
	// start execution in applet mode; execute network in new thread
	// --------------------------------------------------------------------------
	private void startApplet() {
		Assertion.test(visualized, "visualization off in applet mode");
		isAppl = true;
		Point loc = getLocationOnScreen();
		network =
			new Network((Application) this, title, width, height, loc.x + 100,
					loc.y + 100, false, 0, 0);
		construct();
		network.start();
		start();
	}*/

	// --------------------------------------------------------------------------
	// applet continues, show window but let user manually continue execution
	// --------------------------------------------------------------------------
	public void start() {
		if (network != null) network.getVisualizer().setVisible(true);
	}

	// --------------------------------------------------------------------------
	// applet is stopped; interrupt network execution and hide window
	// --------------------------------------------------------------------------
	public void stop() {
		if (network != null) {
			Visualizer visualizer = network.getVisualizer();
			visualizer.stop();
			visualizer.setVisible(false);
		}
	}

	// --------------------------------------------------------------------------
	// restart application
	// --------------------------------------------------------------------------
	public void restart() {
		Assertion.test(visualized, "no visualization");
		Visualizer visualizer = network.getVisualizer();
		int px = visualizer.getXPos();
		int py = visualizer.getYPos();
		int w = visualizer.getWidth();
		int h = visualizer.getHeight();
		resetStatistics();
		network.devisualize();
		network =
			new Network((Application) this, title, width, height, px, py, true, w, h);
		construct();
		start();
		network.start();
	}

	/**
	 * additional functionality. resets counters in "Main" when "Restart" button pushed
	 */
	public abstract void resetStatistics();

	// --------------------------------------------------------------------------
	// terminate visualized application
	// --------------------------------------------------------------------------
	public void terminate() {
		//network.devisualize();
		network = null;
		System.gc();
		//if (button != null) button.setEnabled(true);
	}

	// --------------------------------------------------------------------------
	// informative text shown as applet info
	// --------------------------------------------------------------------------
	public String getAppletInfo() {
		return getText();
	}

	// --------------------------------------------------------------------------
	// return true iff in applet mode
	// --------------------------------------------------------------------------
	public boolean isApplet() {
		return isAppl;
	}

	// --------------------------------------------------------------------------
	// informative text shown as applet info
	// --------------------------------------------------------------------------
	public String[][] getParameterInfo() {
		String info[][] =
			{ { "buttonLabel", "String", "text on button" },
				{ "fontName", "String", "name of button font" },
				{ "fontStyle", "plain, bold, italic", "style of button font" },
				{ "fontSize", "Integer >= 1", "size of button font" } };
		return info;
	}

	// --------------------------------------------------------------------------
	// 
	// the following functions may/must be overridden by the user
	//
	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	// informative text
	// --------------------------------------------------------------------------
	public String getText() {
		return "(no information)";
	}

	// --------------------------------------------------------------------------
	// network construction provided by user
	// --------------------------------------------------------------------------
	public abstract void construct();

	// --------------------------------------------------------------------------
	// 
	// the following functions may be used for network construction
	//
	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	// create new node in network
	// --------------------------------------------------------------------------
	public AgentNode node(Program prog, String text, int xPos, int yPos) {
		Assertion.test(network != null, "network not initialized");
		return new AgentNode(network, prog, text, xPos, yPos);
	}

	// --------------------------------------------------------------------------
	// create new node in network (without visualization information)
	// --------------------------------------------------------------------------
	public AgentNode node(Program prog) {
		Assertion.test(network != null, "network not initialized");
		return new AgentNode(network, prog);
	}

	// --------------------------------------------------------------------------
	// link two nodes by channel
	// --------------------------------------------------------------------------
	public void link(AgentNode sender, AgentNode receiver) {
		Assertion.test(network != null, "network not initialized");
		Channel.link(sender, receiver);
	}

	// --------------------------------------------------------------------------
	// link two nodes by channel with selector `s`
	// --------------------------------------------------------------------------
	public void link(AgentNode sender, AgentNode receiver, Selector s) {
		Assertion.test(network != null, "network not initialized");
		Channel.link(sender, receiver, s);
	}

	// --------------------------------------------------------------------------
	//
	// the following methods may be used to customize the application
	//
	// --------------------------------------------------------------------------
	// --------------------------------------------------------------------------
	// set scheduler for network
	// --------------------------------------------------------------------------
	public void setScheduler(Scheduler sched) {
		network.setScheduler(sched);
	}

	// --------------------------------------------------------------------------
	// set message selector for network
	// --------------------------------------------------------------------------
	public void setSelector(Selector sel) {
		defaultSelector = sel;
	}

	// --------------------------------------------------------------------------
	// set node radius
	// --------------------------------------------------------------------------
	public void setNodeRadius(int r) {
		nodeRadius = r;
	}

	// --------------------------------------------------------------------------
	// sets fonts for display of node label and time subscript
	// --------------------------------------------------------------------------
	public void setNodeFonts(Font normal, Font small) {
		nodeNormalFont = normal;
		nodeSmallFont = small;
	}

	// --------------------------------------------------------------------------
	// set channel width
	// --------------------------------------------------------------------------
	public void setChannelWidth(int w) {
		channelWidth = w;
		channelRadius = 2 * w;
	}

	// --------------------------------------------------------------------------
	// set browser to invoke in non-applet mode for help pages
	// --------------------------------------------------------------------------
	public void setBrowser(String command) {
		browserCommand = command;
	}
}
