// --------------------------------------------------------------------------
// $Id: Popup.java,v 1.1 1997/11/04 10:02:17 schreine Exp $
// a popup window
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// --------------------------------------------------------------------------
package frameworks.faulttolerance.dcop.daj.awt;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Window;
import java.util.StringTokenizer;

@SuppressWarnings("serial")
public class Popup extends Window {

	// the visualizer
	private Visualizer visualizer;
	// font characteristics
	private FontMetrics fontMetrics;
	private int fontHeight, fontAscent, fontAdvance;
	// window dimensions
	// private int width, height;
	// coordinates of frame
	private int frameX, frameY, frameWidth, frameHeight;
	// offset of text
	private int textX, textY;
	// current window text
	private String text;

	// --------------------------------------------------------------------------
	// create new popup window for `visual`
	// --------------------------------------------------------------------------
	public Popup(Visualizer visual) {
		super(visual);
		setVisible(false);
		visualizer = visual;
		// determine window font and color
		Font font = new Font("Arial", Font.PLAIN, 12);
		Color color = new Color(255, 255, 204);
		setFont(font);
		setBackground(color);
		// remember metrices
		fontMetrics = getFontMetrics(font);
		fontHeight = fontMetrics.getHeight();
		fontAscent = fontMetrics.getAscent();
		fontAdvance = 2;
	}

	// --------------------------------------------------------------------------
	// let window pop up at `xpos/ypos` showing `txt`
	// --------------------------------------------------------------------------
	public void popup(int xpos, int ypos, String txt) {
		// store text
		text = txt;
		// determine window dimensions
		StringTokenizer t = new StringTokenizer(text, "\n");
		int num = t.countTokens();
		int height = num * fontHeight + 2;
		int width = 0;
		for (int i = 0; i < num; i++) {
			String string = t.nextToken();
			int width0 = fontMetrics.stringWidth(string);
			if (width0 > width) width = width0;
		}
		width += 2 * (1 + fontAdvance);
		// determine frame coordinates
		frameX = 0;
		frameY = 0;
		frameWidth = width - 1;
		frameHeight = height - 1;
		// determine text coordinates
		textX = 1 + fontAdvance;
		textY = 1 + fontAscent;
		// compensate for the applet window warning string
		// a bloody hack to compensate for an apparent AWT 1.1 bug
		boolean isApplet = visualizer.getApplication().isApplet();
		if (isApplet) {
			int offset = getWarningString()==null ? 2 : fontMetrics.stringWidth(getWarningString());
			width += offset - 2;
			frameY += 17;
			textY += 17;
		}
		// position window and show it
		setLocation(xpos, ypos);
		setSize(width, height);
		setVisible(true);
	}

	// --------------------------------------------------------------------------
	// called to paint contents of window
	// --------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.setColor(Color.black);
		g.drawRect(frameX, frameY, frameWidth, frameHeight);
		StringTokenizer t = new StringTokenizer(text, "\n");
		int num = t.countTokens();
		for (int i = 0; i < num; i++) {
			String string = t.nextToken();
			g.drawString(string, textX, textY + i * fontHeight);
		}
	}
}
