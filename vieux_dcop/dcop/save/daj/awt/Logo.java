// ----------------------------------------------------------------------------
// $Id: Logo.java,v 1.1 1997/10/25 15:50:01 ws Exp $
// a converter from XPM to a Java image
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package vieux.dcop.save.daj.awt;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.image.MemoryImageSource;
import java.util.StringTokenizer;

import vieux.dcop.save.daj.Assertion;


@SuppressWarnings("serial")
public class Logo extends Panel {

	Image image;
	int width, height;

	// --------------------------------------------------------------------------
	// create image from XPM format (array of strings)
	// --------------------------------------------------------------------------
	protected Logo() {
		try {
			String base = strings[0];
			StringTokenizer t = new StringTokenizer(base, " ");
			width = Integer.parseInt(t.nextToken());
			height = Integer.parseInt(t.nextToken());
			int ncolors = Integer.parseInt(t.nextToken());
			int chars_per_pixel = Integer.parseInt(t.nextToken());
			String colorName[] = new String[ncolors];
			int colorValue[] = new int[ncolors];
			for (int i = 0; i < ncolors; i++) {
				String string = strings[i + 1];
				StringTokenizer t0 = new StringTokenizer(string, " ");
				colorName[i] = t0.nextToken();
				// String dummy = t0.nextToken();
				colorValue[i] = Integer.parseInt(t0.nextToken().substring(1), 16);
			}
			int start = ncolors + 1;
			int pix[] = new int[width * height];
			for (int i = 0; i < height; i++) {
				String line = strings[i + start];
				for (int j = 0; j < width; j++) {
					// int value = 0;
					String color =
						line.substring(j * chars_per_pixel, (j + 1) * chars_per_pixel);
					boolean found = false;
					for (int k = 0; k < ncolors; k++) {
						if (color.equals(colorName[k])) {
							int c = colorValue[k];
							// make white transparent
							if (c == 0xffffff) pix[i * width + j] = 0;
							else pix[i * width + j] = (255 << 24) | c;
							found = true;
							break;
						}
					}
					Assertion.test(found, "color not found");
				}
			}
			MemoryImageSource source =
				new MemoryImageSource(width, height, pix, 0, width);
			image = createImage(source);
		}
		catch (NumberFormatException e) {
			Assertion.fail("NumberFormatException");
		}
	}

	// --------------------------------------------------------------------------
	// draw image
	// --------------------------------------------------------------------------
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}

	// --------------------------------------------------------------------------
	// return preferred component size
	// --------------------------------------------------------------------------
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	// --------------------------------------------------------------------------
	// return minimum component size
	// --------------------------------------------------------------------------
	public Dimension getMinimumSize() {
		return new Dimension(width, height);
	}
	// --------------------------------------------------------------------------
	// the XPM data
	// --------------------------------------------------------------------------
	private final static String strings[] =
		{
			// width height ncolors chars_per_pixel
			"96 97 3 1",
			// colors
			"` c #000000",
			"a c #6688E6",
			"b c #FFFFFF",
			// pixels
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb`bbb````````bbbbb`bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbb``````bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb`````bbb````bbbbb`````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```b```bbbbbbbbbb```b```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbb```bbbbbbbb```bbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```bbbbbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```bbbbbbbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```bbbbbbbbbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```bbbbbbbbbbbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```bbbbbbbbbbbbbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```bbbbbbbbbbbbbbbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbb```bbbbbbbbb```bbbbb```bbbbbbbbbbbbbbbbbb```bbbbb```bbbbbbbb``bbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbb`````bbbbbbbb```bbbbb```bbbbbbbbbbbbbbbbbbbb```bbbbb```bbbbbbb````bbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbb````b``bbbbbbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbb```bbbbb```bbbbbb``````bbbbbbbbbbbbbb",
			"bbbbbbbbbb````bbb``bbbbbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```bbbbb``bb````bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbb``bbbbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```bbbb``bbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbb``bbbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```bbb``bbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbb``bbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```bb``bbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbb``bb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```b``bbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbb``b```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb`````bbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbb`````bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb````bbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbb````bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb```bbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbb```bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb``bbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbb``bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbb`bbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbb`bbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbbbbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbbbbbbb```````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb```````````bbbbbbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbbbbbb````````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb````````````bbbbbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbbbbbbbbbbbbbbb``bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``bbbbbbbbbbbbbbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbbbbbbbbbbbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``bbbbbbbbbbbbbbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbbbbbbbbbbbbbb``bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``bbbbbbbbbbbbbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb``bbbbbbbbbbbbbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``bbbbbbbbbbbbbbb``bbbbbbbbbbbb",
			"bbbbbbbbbb````````````````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````````````bbbbbbbbbbbb",
			"bbbbbbbbbb````````````````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````````````bbbbbbbbbbbb",
			"bbbb````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb````bbbbb",
			"bbb``````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````bbbb",
			"bb````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb````````bbb",
			"bb`````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bb",
			"b``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bb",
			"b``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bb",
			"b``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bb",
			"b``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bb",
			"bb````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb````````bbb",
			"bbb``````bbbbb``bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbabbbb``````bbbb",
			"bbbb````bbbbb````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaabbbb````bbbbb",
			"bbbbbbbbbbbb``````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaabbbbbbbbbbbb",
			"bbbbbbbbbbb```bb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaaaabbbbbbbbbbb",
			"bbbbbbbbbb```bbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaaaaaabbbbbbbbbb",
			"bbbbbbbbb```bbbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaaaaaaaabbbbbbbbb",
			"bbbbbbbbb```bbbbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaaaaaaaabbbbbbbbbb",
			"bbbbbbbbbb```bbbbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaaaaaaaabbbbbbbbbbb",
			"bbbbbbbbbbb```bbbbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaaaaaaaabbbbbbbbbbbb",
			"bbbbbbbbbbbb```bbbbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaaaaaaaabbbbbbbbbbbbb",
			"bbbbbbbbbbbbb```bbbbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaaaaaaaabbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbb```bbbbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaaaaaaaabbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbb```bbbbbbb```bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbaaaaaaaaaaabbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbb```bbbbbbb```bbbbbb``bbbbbbbbbbbbbbbbbbbbbaabbbbbbbbaaaaaaaaaaabbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbb```bbbbbbb```bbbbb````bbbbbbbbbbbbbbbbbaaaabbbbbbbaaaaaaaaaaabbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbb```bbbbbbb```bbbb``````bbbbbbbbbbbbbaaaaaabbbbbbaaaaaaaaaaabbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbb```bbbbbbb```bbb``bb````bbbbbbbbbaaaaaaaabbbbbaaaaaaaaaaabbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbb```bbbbbbb```bb``bbbb``bbbbbbbbbaaaaaaaabbbbaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbb```bbbbbbb```b``bbbb``bbbbbbbbbaaaaaaaabbbaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbb```bbbbbbb`````bbbb``bbbbbbbbbaaaaaaaabbaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbb```bbbbbbb````bbbb``bbbbbbbbbaaaaaaaabaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbb```bbbbbbb```bbbb``bbbbbbbbbaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbb```bbbbbbb``bbbb``bbbbbbbbbaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbbbb`bbbb``bbbbbbbbbaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbbbbbbbb``bbbbbbbbbaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbb```bbbbbbbbbb``bbbbbbbbbaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbb``````````bbbbbbbbb``bbbbbbbbbaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbb```````````bbbbbbbb``bbbbbbbbbaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbb``bbbbbbbbbbbbbbbb``bbbbbbbbbaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbb``bbbbbbbbbbbbbbbb``bbbbbbbbbaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbb``bbbbbbbbbbbbbbb``bbbbbbbbbaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbb``bbbbbbbbbbbbbbb``bbbbbbbbbaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbb``````````````````bbbbbbbbbaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbb``````````````````bb````bbbaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb````````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb``````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb````bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
			"bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb" };
}
