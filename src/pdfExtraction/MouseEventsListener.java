package pdfExtraction;

import java.awt.Container;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import pdftrans.ViewerCtrl;
public class MouseEventsListener extends MouseInputAdapter{
	
	private ViewerCtrl viewer;;
	private CustomGlassPane customGlassPane;
	private Container contentPane;
	private Point topLeftPoint;
	private Point bottomRightPoint;
	
	public MouseEventsListener(ViewerCtrl viewerCtrl, CustomGlassPane customGlassPane,
			Container contentPane) {
		
		this.viewer = viewerCtrl;
		this.customGlassPane = customGlassPane;
		this.contentPane = contentPane;
	}
	
	
	public void mousePressed(final MouseEvent mouseEvent)
	{
		topLeftPoint=mouseEvent.getPoint();
		redispatchMouseEvent(mouseEvent);
	}
	
	public void mouseDragged(final MouseEvent mouseEvent)
	{
		bottomRightPoint=mouseEvent.getPoint();
		
		redispatchMouseEvent(mouseEvent,topLeftPoint!=null,false);
	}
	
	public void mouseReleased(final MouseEvent mouseEvent)
	{
		bottomRightPoint=mouseEvent.getPoint();
		
		redispatchMouseEvent(mouseEvent,true,true);
	}
	
	public void mouseMoved(final MouseEvent mouseEvent)
	{
		redispatchMouseEvent(mouseEvent);
	}
	
	public void mouseClicked(final MouseEvent mouseEvent)
	{
		redispatchMouseEvent(mouseEvent);
	}

	public void mouseEntered(final MouseEvent mouseEvent)
	{
		redispatchMouseEvent(mouseEvent);
	}
	
	public void mouseExited(final MouseEvent mouseEvent)
	{
		redispatchMouseEvent(mouseEvent);
	}

	private void redispatchMouseEvent(MouseEvent mouseEvent) {
		
		redispatchMouseEvent(mouseEvent,false,false);
		
	}


	private void redispatchMouseEvent(MouseEvent mouseEvent, boolean repaint, boolean extract) {
		
		final Point glassPanePoint=mouseEvent.getPoint();
		final Point containerPoint=SwingUtilities.convertPoint(customGlassPane, glassPanePoint,contentPane);
		
		if(containerPoint.y>=0)
		{
			final java.awt.Component component=SwingUtilities.getDeepestComponentAt(contentPane,containerPoint.x, containerPoint.y);
			
			if(component!=null)
			{
				final Point componentPoint=SwingUtilities.convertPoint(contentPane, containerPoint, (java.awt.Component) component);
				
				component.dispatchEvent(new MouseEvent(component,mouseEvent.getID(),mouseEvent.getWhen(), mouseEvent.getModifiers()
						, componentPoint.x, componentPoint.y, mouseEvent.getClickCount(),mouseEvent.isPopupTrigger()));
			}
		}
			
		if(repaint)
		{
			if(extract)
			{
				viewer.handleSelection(topLeftPoint,bottomRightPoint);
				
				topLeftPoint=null;
				bottomRightPoint=null;
			}
			
			customGlassPane.setSelection(topLeftPoint, bottomRightPoint);
			customGlassPane.repaint();
		}
	}
	
	
	
	

}
