package pdftrans.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.icepdf.core.pobjects.Document;
import org.icepdf.ri.common.SwingController;

import pdftrans.ViewerCtrl;

public class NewSwingController extends SwingController implements ActionListener {

	Field tabbedPane = null;
	private JTabbedPane utilityTabbed;
	private JButton openFileButton;
	private File file = null;
	private static ViewerCtrl viewerCtrl;
	{
		try {
			tabbedPane = SwingController.class.getDeclaredField("utilityTabbedPane");
			tabbedPane.setAccessible(true);
			utilityTabbed = (JTabbedPane) tabbedPane.get(this);

		} catch (NoSuchFieldException nsfe) {
			throw new RuntimeException(nsfe);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																		
	private NewSearchPanel searchBar;
	private Document document;

	public void setSearchPanel(NewSearchPanel sp) {
		searchBar = sp;
	}

	@Override
	public void showSearchPanel() {

		if (utilityTabbed != null && searchBar != null) {
			toggleUtilityPaneVisibility();

			if (isUtilityPaneVisible()) {
				if (utilityTabbed.getSelectedComponent() != searchBar) {
					safelySelectUtilityPanel(searchBar);
				}

				searchBar.requestFocus();
			}
		}

	}

	public void openFile() {

		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		fileChooser.setDialogTitle("Open Dialog");
		int returnval = fileChooser.showOpenDialog(getViewerFrame());

		if (returnval == fileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			fileChooser.setVisible(false);
			openDocument(file.getPath());

			JScrollPane jScrollPane = (JScrollPane) getDocumentViewController().getViewContainer();
			JComponent vue = (JComponent) jScrollPane.getViewport().getComponent(0);
			String printKey = "imprimer";
			KeyStroke raccourciImprimer = KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK);
			InputMap inputMap = vue.getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW);
			inputMap.put(raccourciImprimer, printKey);
			ActionMap actionMap = vue.getActionMap();
			actionMap.put(printKey, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {

					Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
					String paste;
					try {
						paste = (String) c.getContents(null).getTransferData(DataFlavor.stringFlavor);
						ViewerCtrl view = getView();
						NewSwingViewBuilder factory = view.getBuilder();
						NewSearchPanel sp = factory.getSearchPanel();
						JTextField res = sp.getResultField();
						JButton search = sp.getSearchButton();
						paste = paste.toLowerCase();
						paste = paste.replaceAll("\\s+$", "");
						res.setText(paste);
						// TimeUnit.SECONDS.sleep(1);
						search.doClick();

					} catch (UnsupportedFlavorException | IOException e1) {
						e1.printStackTrace();
					}
				}
			});

		}
	}

	public static void main(String args[]) throws IOException {
		SwingUtilities.invokeLater(() -> {
			ViewerCtrl view = new ViewerCtrl();
			setView(view);

		});
	}

	public static void setView(ViewerCtrl view) {
		viewerCtrl = view;
	}

	public ViewerCtrl getView() {
		return viewerCtrl;
	}

	public File getFile() {
		return file;
	}

	public boolean isSuccess() {
		if (file == null)
			return false;
		else
			return true;
	}

}
