package examples.EcoResolution;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;

//************************************************************************
//************************************************************************
//PuzzleFrame :c'est la fenetre graphique dans laquelle s'execute
//l'application ( sous-classe de Frame)
//************************************************************************
//************************************************************************

public class PuzzleFrame extends Frame {

	/**
	 *
	 */
	private static final long serialVersionUID = -1827756192251051198L;
	N_Puzzle_Applet parent;//l'applet depuis laquelle est lanc'ee l'application
	EcoNPuzzle taquin;

	Panel panCentral;//support graphique de l'EcoNPuzzle
	CheckboxMenuItem mi;

	Execution active;
	int nbDeplacements = 0;
	float nbMoyenDeplacements = 0;
	public static  int TAILLE_DEFAUT ;

	//{{DECLARE_CONTROLS
	java.awt.Panel puzzlePanel;
	java.awt.Panel panel1;
	java.awt.Button button2;
	java.awt.Button button1;
	java.awt.TextField textField1;
	java.awt.Label label4;
	java.awt.Label avgMovesText;
	java.awt.Label movesText;
	java.awt.Label label3;
	java.awt.Label label2;
	java.awt.Label label1;
	java.awt.Choice speedChoice;
	java.awt.Button stopButton;
	java.awt.Button runButton;
	//}}
	//{{DECLARE_MENUS
	//}}

	class SymAction implements java.awt.event.ActionListener
	{
		@Override
		public void actionPerformed(final java.awt.event.ActionEvent event)
		{
			final Object object = event.getSource();
			if (object == PuzzleFrame.this.runButton)
				PuzzleFrame.this.runButton_ActionPerformed(event);
			if (object == PuzzleFrame.this.stopButton)
				PuzzleFrame.this.stopButton_ActionPerformed(event);
			else if (object == PuzzleFrame.this.button1)
				PuzzleFrame.this.button1_ActionPerformed(event);
			else if (object == PuzzleFrame.this.button2)
				PuzzleFrame.this.button2_ActionPerformed(event);


		}
	}

	class SymWindow extends java.awt.event.WindowAdapter
	{
		@Override
		public void windowDeiconified(final java.awt.event.WindowEvent event)
		{
			final Object object = event.getSource();
			if (object == PuzzleFrame.this)
				PuzzleFrame.this.PuzzleFrame_WindowDeiconified(event);
		}

		@Override
		public void windowIconified(final java.awt.event.WindowEvent event)
		{
			final Object object = event.getSource();
			if (object == PuzzleFrame.this)
				PuzzleFrame.this.PuzzleFrame_WindowIconified(event);
		}

		@Override
		public void windowClosing(final java.awt.event.WindowEvent event)
		{
			final Object object = event.getSource();
			if (object == PuzzleFrame.this)
				PuzzleFrame.this.PuzzleFrame_WindowClosing(event);
		}
	}

	PuzzleFrame( final N_Puzzle_Applet a,final String titre, final int t) {
		super( titre);
		this.parent = a;
		PuzzleFrame.TAILLE_DEFAUT = t;
			//{{INIT_CONTROLS
		this.setLayout(new BorderLayout(1,2));
		this.setVisible(false);
		this.setSize(609,576);
		this.puzzlePanel = new java.awt.Panel();
		this.puzzlePanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
		this.puzzlePanel.setBounds(0,0,609,494);
		this.puzzlePanel.setBackground(new Color(16777215));
		this.add("Center", this.puzzlePanel);
		this.panel1 = new java.awt.Panel();
		this.panel1.setLayout(null);
		this.panel1.setBounds(0,496,609,80);
		this.panel1.setBackground(new Color(12632256));
		this.add("South", this.panel1);
		this.button2 = new java.awt.Button();
		this.button2.setLabel("New game");
		this.button2.setBounds(500,12,92,20);
		this.panel1.add(this.button2);
		this.button1 = new java.awt.Button();
		this.button1.setLabel("Set");
		this.button1.setBounds(323,42,60,20);
		this.panel1.add(this.button1);
		this.textField1 = new java.awt.TextField();
		this.textField1.setText("4");
		this.textField1.setBounds(273,43,44,19);
		this.panel1.add(this.textField1);
		this.label4 = new java.awt.Label("Size : ");
		this.label4.setBounds(223,42,49,21);
		this.label4.setFont(new Font("Arial", Font.BOLD, 12));
		this.panel1.add(this.label4);
		this.avgMovesText = new java.awt.Label("0");
		this.avgMovesText.setBounds(143,42,40,18);
		this.avgMovesText.setBackground(new Color(16777215));
		this.panel1.add(this.avgMovesText);
		this.movesText = new java.awt.Label("0");
		this.movesText.setBounds(143,12,40,18);
		this.movesText.setBackground(new Color(16777215));
		this.panel1.add(this.movesText);
		this.label3 = new java.awt.Label("avg. moves per tile :");
		this.label3.setBounds(13,42,134,18);
		this.panel1.add(this.label3);
		this.label2 = new java.awt.Label("number of moves :");
		this.label2.setBounds(13,12,125,17);
		this.panel1.add(this.label2);
		this.label1 = new java.awt.Label("Speed : ");
		this.label1.setBounds(223,12,60,21);
		this.label1.setFont(new Font("Arial", Font.BOLD, 12));
		this.panel1.add(this.label1);
		this.speedChoice = new java.awt.Choice();
		this.speedChoice.addItem("Min");
		this.speedChoice.addItem("Medium");
		this.speedChoice.addItem("Max");
		try {
			this.speedChoice.select(2);
		}
		catch (final IllegalArgumentException e) { }
		this.panel1.add(this.speedChoice);
		this.speedChoice.setBounds(291,12,91,21);
		this.speedChoice.setFont(new Font("Arial", Font.PLAIN, 12));
		this.stopButton = new java.awt.Button();
		this.stopButton.setLabel("Stop");
		this.stopButton.setBounds(423,42,60,20);
		this.stopButton.setFont(new Font("Dialog", Font.PLAIN, 12));
		this.panel1.add(this.stopButton);
		this.runButton = new java.awt.Button();
		this.runButton.setLabel("Run");
		this.runButton.setBounds(423,12,60,20);
		this.runButton.setFont(new Font("Dialog", Font.BOLD, 12));
		this.panel1.add(this.runButton);
		this.setTitle("N-Puzzle");
		this.setResizable(false);
		//}}
		//{{INIT_MENUS
		//}}
		 this.taquin = new EcoNPuzzle( this,  PuzzleFrame.TAILLE_DEFAUT);//creation d'une instance d'EcoNPuzzle
		//liee a la fenetre de l'application
		this.puzzlePanel.add( this.taquin.support);
		//les boutons et la boite a cocher
		this.pack();//donne a la fenetre une size adaptee aux composants qui s'y trouvent
		//move( 10, 2);
		//resize( size().width, size().height + 5);
		this.show();//affichage a l'ecran de la fenetre

		this.parent.repaint();
		this.toFront();




		//{{REGISTER_LISTENERS
		final SymAction lSymAction = new SymAction();
		this.runButton.addActionListener(lSymAction);
		this.stopButton.addActionListener(lSymAction);
		this.button1.addActionListener(lSymAction);
		this.button2.addActionListener(lSymAction);
		final SymWindow aSymWindow = new SymWindow();
		this.addWindowListener(aSymWindow);
		//}}
	}
	void button1_ActionPerformed(final java.awt.event.ActionEvent event)
	{
		 final String str = this.textField1.getText();
		 try {
			final int i = Integer.parseInt( str);
			if ( 2 < i ) this.resize( i);
		 } catch ( final NumberFormatException exc) {}
		 this.runButton.setEnabled(true);
		 this.stopButton.setEnabled(false);

	}
	void button2_ActionPerformed(final java.awt.event.ActionEvent event)
	{
		this.newGame(false);
	}
	public void newGame( final boolean ordonne) {
		if ( this.active != null) {
			this.active.stop();
			this.active = null;
		}
		this.taquin.reInit( ordonne);
		this.taquin.support.setEnabled(true);
		this.nbDeplacements = 0;
		this.movesText.setText( "" + this.nbDeplacements);
		this.stopButton.setEnabled(false);
		this.runButton.setEnabled(true);
		this.toFront();
	}
	void PuzzleFrame_WindowClosing(final java.awt.event.WindowEvent event)
	{
			if ( this.active != null) {
				this.active.stop();
				this.active = null;
			}
			this.dispose();
			this.parent.repaint();
	}
	void PuzzleFrame_WindowDeiconified(final java.awt.event.WindowEvent event)
	{
		            if ( this.active != null)
				this.active.resume();

	}
	void PuzzleFrame_WindowIconified(final java.awt.event.WindowEvent event)
	{
			if ( this.active != null)
				this.active.suspend();
			this.parent.repaint();
	}
   public void refresh( final EcoPlace e1, final EcoPlace e2) {
		final Graphics g = this.taquin.support.getGraphics();
		e1.refresh( g);
		e2.refresh( g);
		this.movesText.setText( "" + this.nbDeplacements);
		this.avgMovesText.setText(new Double((float)this.nbDeplacements / (this.taquin.size * this.taquin.size - 1)).toString());
	}
	public void resize( final int t) {//redimensionnement du jeu
		if ( this.active != null) {
			this.active.stop();
			this.active = null;
		}
		//hide();
		try {
			Thread.sleep(100);
		} catch (final InterruptedException e) { }

		this.puzzlePanel.removeAll();
		this.taquin = new EcoNPuzzle( this, t);
		this.puzzlePanel.add( this.taquin.support);
		this.nbDeplacements = 0;
		this.movesText.setText( "" + this.nbDeplacements);
		this.pack();//donne a la fenetre une size adaptee aux composants qui s'y trouvent
		//move( 10,  2);
		this.show();//affichage a l'ecran de la fenetre
		this.parent.repaint();
		this.toFront();
	}
	public void run() {
		if ( this.active == null) {
			this.active = new Execution( this.taquin);
			this.active.start();
			this.taquin.support.disable();
		} else
			this.active.resume();
	}
	void runButton_ActionPerformed(final java.awt.event.ActionEvent event)
	{
		this.run();

		//{{CONNECTION
		// Disable the Button
		this.runButton.setEnabled(false);
		this.stopButton.setEnabled(true);
		//}}
	}
	public void stop() {
		if ( this.active != null) {
			this.active.stop();
			this.active = null;
		}
		this.stopButton.setEnabled(false);
		this.taquin.interrupt();
		this.taquin.support.enable();
	}
	void stopButton_ActionPerformed(final java.awt.event.ActionEvent event)
	{
		this.stop();

		//{{CONNECTION
		// Enable the Button
		this.runButton.setEnabled(true);
		//}}

	}
}
