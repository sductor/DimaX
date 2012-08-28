// ----------------------------------------------------------------------------
// $Id: MultiLineLabel.java,v 1.1 1997/10/25 15:50:01 ws Exp $
// a simple multi-line label derived from "Java in a Nutshell"
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package frameworks.dcop.daj.awt;

import java.awt.*;
import java.util.*;

@SuppressWarnings("serial")
public class MultiLineLabel extends Canvas {

	private int numLines;
	private String lines[];
	private int widths[];
	int height;
	int ascent;
	int width;
	int margin;

	// --------------------------------------------------------------------------
	// create new label with `text`
	// --------------------------------------------------------------------------
	protected MultiLineLabel(String text) {
		StringTokenizer t = new StringTokenizer(text, "\n");
		numLines = t.countTokens();
		lines = new String[numLines];
		widths = new int[numLines];
		for (int i = 0; i < numLines; i++) {
			lines[i] = t.nextToken();
		}
	}

	// --------------------------------------------------------------------------
	// compute all measures
	// --------------------------------------------------------------------------
	public void measure() {
		FontMetrics metrics = getFontMetrics(getFont());
		height = metrics.getHeight();
		ascent = metrics.getAscent();
		width = 0;
		for (int i = 0; i < numLines; i++) {
			widths[i] = metrics.stringWidth(lines[i]);
			if (widths[i] > width) width = widths[i];
		}
		margin = metrics.getMaxAdvance();
	}

	// --------------------------------------------------------------------------
	// return preferred size for layout manager
	// --------------------------------------------------------------------------
	public Dimension getPreferredSize() {
		measure();
		return new Dimension(width + 2 * margin, numLines * height);
	}

	// --------------------------------------------------------------------------
	// return minimum size for layout manager
	// --------------------------------------------------------------------------
	public Dimension getMinimumSize() {
		measure();
		return new Dimension(width + 2 * margin, numLines * height);
	}

	// --------------------------------------------------------------------------
	// paint label
	// --------------------------------------------------------------------------
	public void paint(Graphics g) {
		measure();
		Dimension d = getSize();
		for (int i = 0; i < numLines; i++) {
			int x = (d.width - widths[i]) / 2;
			int y = ascent + (d.height - numLines * height) / 2 + i * height;
			g.drawString(lines[i], x, y);
		}
	}
}
