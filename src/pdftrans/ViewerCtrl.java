package pdftrans;

import java.awt.Container;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.awt.Robot;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.icepdf.core.pobjects.Document;
import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.MyAnnotationCallback;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.common.views.DocumentViewController;
import org.icepdf.ri.util.PropertiesManager;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.itextpdf.text.pdf.parser.RegionTextRenderFilter;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import pdfExtraction.CustomGlassPane;
import pdftrans.ui.NewSearchPanel;
import pdftrans.ui.NewSwingController;
import pdftrans.ui.NewSwingViewBuilder;

public class ViewerCtrl implements KeyListener {

	private static NewSwingController controller;
	private NewSwingViewBuilder factory;
	private static String filePath;
	private static ViewerCtrl viewctrl;
	int[] arr;
	int i = 0;

	public ViewerCtrl() {

		arr = new int[3];
		controller = new NewSwingController();

		factory = new NewSwingViewBuilder(controller);
		JPanel viewerComponentPanel = factory.buildViewerPanel();
		ComponentKeyBinding.install(controller, viewerComponentPanel);
		controller.getDocumentViewController()
				.setAnnotationCallback(new MyAnnotationCallback(controller.getDocumentViewController()));

		final JFrame applicationFrame = new JFrame("My PDF Viewer");
		applicationFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		final Container contentPane = applicationFrame.getContentPane();
		final JScrollPane scrollPane = new JScrollPane(viewerComponentPanel);
		final int horizontalPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
		final int verticalPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
		scrollPane.setHorizontalScrollBarPolicy(horizontalPolicy);
		scrollPane.setVerticalScrollBarPolicy(verticalPolicy);
		applicationFrame.add(viewerComponentPanel);
		viewerComponentPanel.addKeyListener(this);
		applicationFrame.pack();
		applicationFrame.setVisible(true);

	}

	public NewSwingViewBuilder getBuilder() {
		return factory;
	}

	public void handleSelection(Point topLeftPoint, Point bottomRightPoint) {

		final int width = bottomRightPoint.x - topLeftPoint.x;
		final int height = bottomRightPoint.y - topLeftPoint.y;

		String text = parsePdf(topLeftPoint.x, topLeftPoint.y, width, height, filePath);
		System.out.println("text:" + text);

	}

	private String parsePdf(int x, int y, int width, int height, String filePath) {

		String text = null;

		try {

			PdfReader pdfReader = new PdfReader(filePath);
			int pageNumber = controller.getCurrentPageNumber();
			System.out.println("Page Number:" + (pageNumber + 1));
			Rectangle selection = new Rectangle(x, y, width, height);
			RenderFilter renderFilter = new RegionTextRenderFilter(selection);
			LocationTextExtractionStrategy delegate = new LocationTextExtractionStrategy();
			TextExtractionStrategy extractionStrategy = new FilteredTextRenderListener(delegate, renderFilter);
			text = PdfTextExtractor.getTextFromPage(pdfReader, pageNumber + 1, extractionStrategy);

			pdfReader.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}

	public void keyPressed(KeyEvent e) {

		arr[i] = e.getKeyCode();
		i++;

		if ((arr[0] == e.VK_C || arr[1] == e.VK_C) && (arr[0] == e.VK_CONTROL || arr[1] == e.VK_CONTROL)) {
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			String paste;
			try {
				paste = (String) c.getContents(null).getTransferData(DataFlavor.stringFlavor);
				System.out.println("text:" + paste);
			} catch (UnsupportedFlavorException | IOException e1) {
				e1.printStackTrace();
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		arr[i] = (Integer) null;

		Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
		String paste;
		try {
			paste = (String) c.getContents(null).getTransferData(DataFlavor.stringFlavor);
			System.out.println("text:" + paste);
		} catch (UnsupportedFlavorException | IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
