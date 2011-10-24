package examples.preypredators;

/**
 * Insert the type's description here.
 * Creation date: (04/11/00 10:41:37)
 * @author: Administrator
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;

public class Grid extends Panel {
	/**
	 *
	 */
	private static final long serialVersionUID = 8335151786839176093L;
	private int TailleGrille;
/**
 * Grille constructor comment.
 */
public Grid() {
	super();
	this.TailleGrille = 1;
}
	 public Grid(final int valTailleGrille) {
	//super();
	this.TailleGrille = valTailleGrille;
	}
/**
 * Insert the method's description here.
 * Creation date: (04/11/00 11:23:30)
 * @return int
 */
public int getTailleGrille() {
	return this.TailleGrille;
}
@Override
public void paint(final Graphics g) {
	this.setBackground(Color.lightGray);
	g.setColor(Color.black);
	final Dimension d = this.getSize();
	final int xoff = d.width / this.TailleGrille;
	final int yoff = d.height / this.TailleGrille;
	for (int i = 1; i < this.TailleGrille; i++) {
		g.drawLine(xoff * i, 0, xoff * i, d.height);
		g.drawLine(0, yoff * i, d.width, yoff * i);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (04/11/00 11:24:10)
 * @param val int
 */
public void setTailleGrille(final int valTailleGrille) {
	this.TailleGrille = valTailleGrille;
}
}
