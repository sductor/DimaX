// --------------------------------------------------------------------------
// $Id: Visualizer.java,v 1.5 1997/11/14 16:39:04 schreine Exp schreine $
// graphical user interface
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package frameworks.faulttolerance.olddcop.daj.awt;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import frameworks.faulttolerance.olddcop.daj.Application;
import frameworks.faulttolerance.olddcop.daj.Network;

@SuppressWarnings("serial")
public class Visualizer extends Frame implements ActionListener, ComponentListener,
		WindowListener {

	// current application
	private Application application;
	// network that is visualized
	private Network network;
	// buttons arranged in a panel
	private Button cont, walk, stop, step, redraw, restart, quit;
	private Panel buttons;
	// the menu bar
	private MenuBar menubar;
	private Menu helpMenu;
	private MenuItem alg, about, home, copying, help;
	// a screen
	private Screen screen;
	// a text label in a panel
	private Label message;
	private Panel status;
	// popup windows for messages
	private InfoDialog alginfo = null;
	private InfoDialog copyright = null;
	private InfoDialog warning = null;
	// the layout manager for all components
	private GridBagLayout layout;
	// current window size position to be remembered after restart
	private Point pos;
	private Dimension dim;
	// text of copyright message
	private final static String infoText =
		"daj -- Simulation of Distributed Algorithms in Java\n"
			+ "http://www.risc.uni-linz.ac.at/software/daj\n"
			+ "Version 1.01 for JDK 1.1.x\n \n"
			+ "Copyright (c) 1997, Wolfgang Schreiner\n"
			+ "Wolfgang.Schreiner@risc.uni-linz.ac.at\n"
			+ "See menu item \"Copying\"\n \n"
			+ "Research Institute for Symbolic Computation (RISC-Linz)\n"
			+ "Johannes Kepler University, A-4040 Linz, Austria";
	// base url of home page (including trailing /)
	private final static String baseURL = "http://www.risc.uni-linz.ac.at/software/daj/";
	// determine initial windows move event
	boolean initMoved = true;
	// determine whether execution is interrupted
	boolean interrupted = true;

	// --------------------------------------------------------------------------
	// create visualizer for Network `net` with `title` and screen size `x/y`
	// `appl` is the current application
	// move visualizer to `px/py`, if `setsize`, then resize to `w/h`
	// --------------------------------------------------------------------------
	public Visualizer(Application appl, Network net, String title, int x, int y, int px,
			int py, boolean setsize, int w, int h) {
		// set title, store network and application
		super(title);
		network = net;
		application = appl;
		// create layout manager; options are
		// BorderLayout (center or along edges, i.e., north, south, east, west)
		// CardLayout (one visible at a time)
		// FlowLayout (left to right in rows)
		// GridBagLayout (grid of row and columns using constraints)
		// GridLayout (grid using left-to-right and top-to-bottom)
		layout = new GridBagLayout();
		this.setLayout(layout);
		// create buttons
		cont = new Button("Run");
		walk = new Button("Walk");
		stop = new Button("Interrupt");
		step = new Button("Step");
		redraw = new Button("Redraw");
		restart = new Button("Reset");
		quit = new Button("Quit");
		// set button states
		stop.setEnabled(false);
		restart.setEnabled(false);
		// set button fonts
		Font font = new Font("Helvetica", Font.BOLD, 14);
		stop.setFont(font);
		cont.setFont(font);
		walk.setFont(font);
		step.setFont(font);
		redraw.setFont(font);
		restart.setFont(font);
		quit.setFont(font);
		// set button colors
		cont.setForeground(Color.green.darker().darker().darker());
		walk.setForeground(Color.green.darker().darker().darker());
		step.setForeground(Color.green.darker().darker().darker());
		stop.setForeground(Color.red.darker());
		// create button panel
		buttons = new Panel();
		buttons.setLayout(layout);
		// font for window descriptions
		Font windowFont = new Font("Helvetica", Font.PLAIN, 12);
		// create menubar
		// setHelpMenu() does not work on some systems
		menubar = new MenuBar();
		setMenuBar(menubar);
		helpMenu = new Menu("Help");
		alg = new MenuItem("About Algorithm");
		about = new MenuItem("About Toolkit");
		helpMenu.add(alg);
		helpMenu.add(about);
		helpMenu.addSeparator();
		home = new MenuItem("Home");
		helpMenu.add(home);
		copying = new MenuItem("Copying");
		helpMenu.add(copying);
		help = new MenuItem("Help");
		helpMenu.add(help);
		menubar.setHelpMenu(helpMenu);
		menubar.setFont(windowFont);
		// add elements to panel, top, down
		add(buttons, cont, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.CENTER, 1.0, 0.0, 0, 0, 0, 0);
		add(buttons, walk, 0, 1, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.CENTER, 1.0, 0.0, 0, 0, 0, 0);
		add(buttons, step, 0, 2, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.CENTER, 1.0, 0.0, 0, 0, 0, 0);
		add(buttons, stop, 0, 3, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.CENTER, 1.0, 0.0, 0, 0, 0, 0);
		add(buttons, redraw, 0, 4, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.SOUTH, 1.0, 1.0, 0, 0, 0, 0);
		add(buttons, restart, 0, 5, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.SOUTH, 1.0, 0.0, 0, 0, 0, 0);
		add(buttons, quit, 0, 6, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.SOUTH, 1.0, 0.0, 0, 0, 0, 0);
		// create label
		message = new Label("Network is in initial state.", Label.LEFT);
		status = new Panel();
		status.setLayout(layout);
		add(status, message, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.CENTER, 1.0, 0.0, 0, 0, 0, 0);
		message.setFont(windowFont);
		// create screen with two scrollbars
		screen = new Screen(this, x, y);
		// arrange panels in window
		// screen may resize in both directions
		// panel only in vertical direction
		// status only in horizontal direction
		add(this, screen, 0, 0, 1, 1, GridBagConstraints.BOTH, GridBagConstraints.CENTER,
				1.0, 1.0, 0, 0, 0, 0);
		add(this, buttons, 1, 0, 1, 1, GridBagConstraints.VERTICAL,
				GridBagConstraints.CENTER, 0.0, 1.0, 5, 5, 0, 5);
		add(this, status, 0, 1, 2, 1, GridBagConstraints.HORIZONTAL,
				GridBagConstraints.CENTER, 0.0, 0.0, 5, 5, 5, 5);
		// pack window to fit components and move it to last recalled position
		pack();
		setVisible(true);
		pos = new Point(px, py);
		if (setsize) dim = new Dimension(w, h);
		else dim = getSize();
		setSize(dim);
		setLocation(pos.x, pos.y);
		// setResizable(false);
		// register event handlers
		cont.addActionListener(this);
		walk.addActionListener(this);
		step.addActionListener(this);
		stop.addActionListener(this);
		redraw.addActionListener(this);
		restart.addActionListener(this);
		quit.addActionListener(this);
		alg.addActionListener(this);
		about.addActionListener(this);
		home.addActionListener(this);
		copying.addActionListener(this);
		help.addActionListener(this);
		addComponentListener(this);
		addWindowListener(this);
	}

	// --------------------------------------------------------------------------
	// add `component` to `container` using the GridBagConstraints layout
	// manager
	//
	// `gridx/gridy` specify the position of the component in the grid
	// `gridwidth/gridy` specify the extension of the component in the grid
	//
	// `fill` says whether the component should grow
	// - to fill horizontal space (GridBagConstraints.HORIZONTAL)
	// - to fill vertical space (GridBagConstraints.VERTICAL)
	// - in both directions (GridBagConstraints.BOTH)
	// - not at all (GridBagConstraints.NONE)
	//
	// `anchor` specifieds how component should be positioned within
	// grid cells if there is extra spece and the component does not grow
	// in both directions (GridBagConstraints.CENTER,NORTH,NORTHWEST,...)
	//
	// `weightx/weighty` specify how rows/colums in grid should resize when
	// container is resized (in proportion of their relative weights)
	//
	// `top/left/bottom/right` specify margins for each side of the component
	// --------------------------------------------------------------------------
	public void add(Container container, Component component, int gridx, int gridy,
			int gridwidth, int gridheight, int fill, int anchor, double weightx,
			double weighty, int top, int left, int bottom, int right) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = gridwidth;
		c.gridheight = gridheight;
		c.fill = fill;
		c.anchor = anchor;
		c.weightx = weightx;
		c.weighty = weighty;
		if (top + left + bottom + right > 0)
			c.insets = new Insets(top, left, bottom, right);
		((GridBagLayout) container.getLayout()).setConstraints(component, c);
		container.add(component);
	}

	// --------------------------------------------------------------------------
	// called when action event was triggered
	// --------------------------------------------------------------------------
	public void actionPerformed(ActionEvent event) {
		Object target = event.getSource();
		if (target == cont) {
			interrupted = false;
			quit.setEnabled(false);
			cont.setEnabled(false);
			walk.setEnabled(false);
			step.setEnabled(false);
			stop.setEnabled(true);
			stop.requestFocus();
			restart.setEnabled(false);
			setText("Network is running");
			network.getScheduler().cont();
		}
		else if (target == walk) {
			interrupted = false;
			quit.setEnabled(false);
			cont.setEnabled(false);
			walk.setEnabled(false);
			step.setEnabled(false);
			stop.setEnabled(true);
			stop.requestFocus();
			restart.setEnabled(false);
			setText("Network is walking");
			network.getScheduler().walk();
		}
		else if (target == step) {
			quit.setEnabled(true);
			cont.setEnabled(true);
			walk.setEnabled(true);
			step.setEnabled(true);
			step.requestFocus();
			stop.setEnabled(false);
			restart.setEnabled(true);
			setText("Network is interrupted");
			network.getScheduler().step();
		}
		else if (target == stop) {
			stop();
		}
		else if (target == redraw) {
			screen.clear();
			screen.redraw();
		}
		else if (target == restart) {
			application.restart();
		}
		else if (target == quit) {
			if (application.isApplet()) application.terminate();
			else System.exit(0);
		}
		else if (target == alg) {
			if (alginfo == null) {
				alginfo =
					new InfoDialog(this, "About this Algorithm", application.getText(),
							false);
				alginfo.setLocation(getLocationOnScreen());
			}
			alginfo.setVisible(true);
		}
		else if (target == about) {
			if (copyright == null) {
				copyright = new InfoDialog(this, "About daj", infoText, true);
				copyright.setLocation(getLocationOnScreen());
			}
			copyright.setVisible(true);
		}
		else if (target instanceof MenuItem) {
			String label = event.getActionCommand();
			String url;
			if (label.equals("Home")) url = baseURL;
			else url = baseURL + label;
			/*if (application.isApplet()) {
				try {
					URL url0 = new URL(url);
					AppletContext context = application.getAppletContext();
					context.showDocument(url0, "daj");
				}
				catch (MalformedURLException e) {
					Assertion.fail("MalformedURLException");
				}
			}
			else {*/
				Runtime runtime = Runtime.getRuntime();
				String command = application.browserCommand + " " + url;
				setText("Executing " + command);
				try {
					runtime.exec(command);
				}
				catch (IOException e) {
					// setText("Could not execute " + command);
					setText(command);
				}
			//}
		}
	}

	// --------------------------------------------------------------------------
	// called when frame has been hidden
	// --------------------------------------------------------------------------
	public void componentHidden(ComponentEvent e) {
	}

	// --------------------------------------------------------------------------
	// called when frame has been shown
	// --------------------------------------------------------------------------
	public void componentShown(ComponentEvent e) {
		this.getScreen().redraw();
	}

	// --------------------------------------------------------------------------
	// called when frame has been moved
	// --------------------------------------------------------------------------
	public void componentMoved(ComponentEvent e) {
		if (initMoved) {
			initMoved = false;
			cont.requestFocus();
		}
		if (this.isShowing()) pos = getLocationOnScreen();
	}

	// --------------------------------------------------------------------------
	// called when frame has been resized
	// --------------------------------------------------------------------------
	public void componentResized(ComponentEvent e) {
		pos = getLocationOnScreen();
		dim = getSize();
		// getScreen().clear();
		// this.getScreen().redraw();
		// network.redraw();
	}

	// --------------------------------------------------------------------------
	// called when window is closed
	// --------------------------------------------------------------------------
	public void windowClosed(WindowEvent e) {
		screen.close();
	}

	// --------------------------------------------------------------------------
	// called when window is iconified
	// --------------------------------------------------------------------------
	public void windowIconified(WindowEvent e) {
		screen.close();
	}

	// --------------------------------------------------------------------------
	// called on various window events
	// --------------------------------------------------------------------------
	public void windowDeactivated(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	// --------------------------------------------------------------------------
	// called when user closes window
	// --------------------------------------------------------------------------
	public void windowClosing(WindowEvent e) {
		if (!interrupted) {
			if (warning == null) {
				warning =
					new InfoDialog(this, "Closing Window", "Please interrupt execution\n"
						+ "before closing the window.", false);
				warning.setLocation(getLocationOnScreen());
			}
			warning.setVisible(true);
			return;
		}
		if (application.isApplet()) application.terminate();
		else System.exit(0);
	}

	// --------------------------------------------------------------------------
	// called when execution is to be interrupted
	// --------------------------------------------------------------------------
	public void stop() {
		interrupted = true;
		network.getScheduler().interrupt();
		quit.setEnabled(true);
		stop.setEnabled(false);
		cont.setEnabled(true);
		walk.setEnabled(true);
		step.setEnabled(true);
		restart.setEnabled(true);
		cont.requestFocus();
		setText("Network is interrupted");
	}

	// --------------------------------------------------------------------------
	// disable further scheduler activities
	// --------------------------------------------------------------------------
	public void inactivate() {
		interrupted = true;
		stop.setEnabled(false);
		cont.setEnabled(false);
		walk.setEnabled(false);
		step.setEnabled(false);
		restart.setEnabled(true);
		quit.setEnabled(true);
		restart.requestFocus();
	}

	// --------------------------------------------------------------------------
	// terminate visualizer
	// --------------------------------------------------------------------------
	public void terminate() {
		if (alginfo != null) alginfo.setVisible(false);
		if (copyright != null) copyright.setVisible(false);
		setVisible(false);
	}

	// --------------------------------------------------------------------------
	// called when component is painted
	// --------------------------------------------------------------------------
	public void paint(Graphics g) {
		screen.paint(g);
	}

	// --------------------------------------------------------------------------
	// return screen
	// --------------------------------------------------------------------------
	public Screen getScreen() {
		return screen;
	}

	// --------------------------------------------------------------------------
	// set message text
	// --------------------------------------------------------------------------
	public void setText(String text) {
		message.setText(text);
	}

	// --------------------------------------------------------------------------
	// return application
	// --------------------------------------------------------------------------
	public Application getApplication() {
		return application;
	}

	// --------------------------------------------------------------------------
	// return position
	// --------------------------------------------------------------------------
	public int getXPos() {
		return pos.x;
	}

	// --------------------------------------------------------------------------
	// return position
	// --------------------------------------------------------------------------
	public int getYPos() {
		return pos.y;
	}

	// --------------------------------------------------------------------------
	// return size
	// --------------------------------------------------------------------------
	public int getWidth() {
		return dim.width;
	}

	// --------------------------------------------------------------------------
	// return height
	// --------------------------------------------------------------------------
	public int getHeight() {
		return dim.height;
	}
}
