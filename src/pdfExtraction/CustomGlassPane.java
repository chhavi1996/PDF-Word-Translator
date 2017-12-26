package pdfExtraction;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;

import pdftrans.ViewerCtrl;

public class CustomGlassPane extends JComponent{
	
	private static final long serialVersionUID = 1L;
	private Point topLeftPoint;
	private Point bottomRightPoint;

	
	public CustomGlassPane(ViewerCtrl viewerCtrl, Container contentPane) {
		// TODO Auto-generated constructor stub
		final MouseEventsListener listener=new MouseEventsListener(viewerCtrl,this,contentPane);
		
		addMouseListener(listener);
		addMouseMotionListener(listener);
	}

	public void setSelection(final Point topLeftPoint,final Point bottomRightPoint)
	{
		this.topLeftPoint=topLeftPoint;
		this.bottomRightPoint=bottomRightPoint;
	}
	
	protected void paintComponent(final Graphics graphics)
	{
		if(topLeftPoint!=null && bottomRightPoint!=null)
		{
			graphics.setColor(Color.BLACK);
			graphics.drawRect(topLeftPoint.x,topLeftPoint.y,
					bottomRightPoint.x-topLeftPoint.x, bottomRightPoint.y-topLeftPoint.y);
			
		}
	}
}
