package examples.circle;

/* *
* 	Applet demonstrating a ProactiveObject Example
*
*/


import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.SystemColor;

public class OceanApplet extends Applet
{
	/**
	 *
	 */
	private static final long serialVersionUID = 3396266671680261788L;
	Font font;
	String str;
	int xPos;
	private OceanAPI fenetreSurMer;

	public OceanApplet()
	{
		this.font = new Font("Dialog", 1, 24);
		this.str = "Go to your circle...";
		this.xPos = 10;
	}
	@Override
	public String getAppletInfo()
	{
		return "RequinApplet\n\nThis type was created in VisualAge.\n";
	}
	private void handleException(final Throwable exception)
	{
	}
/** Basicaly creates a new OceanAPI */

@Override
public void init()
	{
		super.init();
		try
		{
			this.setName("RequinApplet");
			this.setLayout(null);
			this.setBackground(SystemColor.textHighlightText);
			this.setSize(580, 460);
			this.fenetreSurMer = new OceanAPI();
			this.fenetreSurMer.setVisible(true);
		}
		catch (final Throwable ivjExc)
		{
			this.handleException(ivjExc);
		}
	}
	@Override
	public void paint(final Graphics g)
	{
		g.setFont(this.font);
		g.setColor(Color.black);
		g.drawString(this.str, this.xPos, 50);
	}
}
