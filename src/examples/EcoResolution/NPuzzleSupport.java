package examples.EcoResolution;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

//************************************************************************
//************************************************************************
//La classe NPuzzleSupport
//************************************************************************
//************************************************************************

class NPuzzleSupport extends Canvas {

	/**
	 *
	 */
	private static final long serialVersionUID = 1325380462980543456L;
	EcoNPuzzle parent;
	int cote;
	int size;

	NPuzzleSupport( final EcoNPuzzle et) {
		this.parent = et;
		this.setBackground( Color.darkGray);
		final int hMax = Toolkit.getDefaultToolkit().getScreenSize().height;
		final int insets = this.parent.pzFrame.getInsets().top + this.parent.pzFrame.getInsets().bottom + 66;
		this.cote = this.parent.size;
		final int T = EcoNPuzzle.tile_size;
		final int aux = hMax  - insets;
		this.size = this.cote * T + insets <= hMax ? this.cote * T : aux - aux % this.cote;
		this.setSize(this.size, this.size);
	}
	@Override
	public void paint( final Graphics g) {
		for ( int i =0; i < this.cote; i++) {
			for ( int j = 0; j < this.cote; j++) {
				this.parent.places[i][j].refresh( g);
			}
		}
	}
	@Override
	public void  processMouseEvent(final MouseEvent evt) {
		final int aux = this.size / this.cote;
		final int j = evt.getX() / aux;
		final int i = evt.getY() / aux;
		if ( 0 <= i && 0 <= j && i < this.size && j < this.size) {
			this.parent.places[i][j].mouseDown();
		}
	}
}
