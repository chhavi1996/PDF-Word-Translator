package pdftrans.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.icepdf.core.util.Defs;
import org.icepdf.ri.common.views.DocumentViewController;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;
import org.icepdf.ri.images.Images;
import org.icepdf.ri.util.PropertiesManager;

public class NewSwingViewBuilder {

	protected NewSwingController viewerController;
	public NewSearchPanel searchPanel;

	protected static final float[] DEFAULT_ZOOM_LEVELS = { 0.05f, 0.10f, 0.25f, 0.50f, 1.0f, 1.5f, 2.0f, 3.0f, 4.0f,
			8.0f, 16.0f, 24.0f, 32.0f, 64.0f };

	public static final int TOOLBAR_STYLE_FLOATING = 1;
	public static final int TOOLBAR_STYLE_FIXED = 2;

	protected int documentViewType;
	protected int documentPageFitMode;
	protected float[] zoomLevels;
	protected ResourceBundle messageBundle;
	protected Font buttonFont;
	protected boolean showButtonText;
	protected int toolbarStyle;
	protected boolean haveMadeAToolBar = false;

	private PropertiesManager propertiesManager;

	public NewSwingViewBuilder(NewSwingController c) {
		this(c, DocumentViewControllerImpl.ONE_PAGE_VIEW, DocumentViewController.PAGE_FIT_WINDOW_HEIGHT, null, null,
				false, NewSwingViewBuilder.TOOLBAR_STYLE_FIXED);
	}

	public NewSwingViewBuilder(NewSwingController c, PropertiesManager properties) {
		this(c, properties, null, false, NewSwingViewBuilder.TOOLBAR_STYLE_FIXED, null,
				DocumentViewControllerImpl.ONE_PAGE_VIEW, DocumentViewController.PAGE_FIT_WINDOW_HEIGHT);
	}

	public NewSwingViewBuilder(NewSwingController c, PropertiesManager properties, Font bf, boolean bt, int ts,
			float[] zl, final int documentViewType, final int documentPageFitMode) {
		viewerController = c;

		messageBundle = viewerController.getMessageBundle();

		if (properties != null) {
			viewerController.setPropertiesManager(properties);
			this.propertiesManager = properties;
		}

		// Attempt to override the highlight color from the properties file
		overrideHighlightColor();

		// update View Controller with previewer document page fit and view type
		// info
		DocumentViewControllerImpl documentViewController = (DocumentViewControllerImpl) viewerController
				.getDocumentViewController();
		documentViewController.setDocumentViewType(documentViewType, documentPageFitMode);

		buttonFont = bf;
		if (buttonFont == null)
			buttonFont = buildButtonFont();
		showButtonText = bt;
		toolbarStyle = ts;
		zoomLevels = zl;
		if (zoomLevels == null)
			zoomLevels = DEFAULT_ZOOM_LEVELS;
		// set default doc view type, single page, facing page, etc.
		this.documentViewType = documentViewType;
		// set default view mode type, fit page, fit width, no-fit.
		this.documentPageFitMode = documentPageFitMode;
	}

	protected void overrideHighlightColor() {
		// Attempt to override the default highlight color
		// We will only attempt this if a -D system parameter was not passed
		if (Defs.sysProperty(PropertiesManager.SYSPROPERTY_HIGHLIGHT_COLOR) == null) {
			doubleCheckPropertiesManager();

			// Try to pull the color from our local properties file
			// If we can find a value, then set it as the system property
			if (propertiesManager != null) {
				String newColor = propertiesManager.getString(PropertiesManager.SYSPROPERTY_HIGHLIGHT_COLOR, null);
				if (newColor != null) {
					Defs.setSystemProperty(PropertiesManager.SYSPROPERTY_HIGHLIGHT_COLOR, newColor);
				}
			}
		}
	}

	protected void doubleCheckPropertiesManager() {
		if ((propertiesManager == null) && (viewerController != null)
				&& (viewerController.getWindowManagementCallback() != null)) {
			propertiesManager = viewerController.getWindowManagementCallback().getProperties();
		}
	}

	public NewSwingViewBuilder(NewSwingController viewerController, int documentViewType, int documentPageFitMode,
			float[] zoomLevels, Font bt, boolean showButtonText, int toolbarStyle) {

		this.viewerController = viewerController;

		messageBundle = viewerController.getMessageBundle();
		DocumentViewControllerImpl documentViewController = (DocumentViewControllerImpl) viewerController
				.getDocumentViewController();
		documentViewController.setDocumentViewType(documentViewType, documentPageFitMode);

		buttonFont = bt;
		if (buttonFont == null)
			buttonFont = buildButtonFont();
		this.showButtonText = showButtonText;
		this.zoomLevels = zoomLevels;
		if (zoomLevels == null)
			this.zoomLevels = DEFAULT_ZOOM_LEVELS;
		this.toolbarStyle = toolbarStyle;
		this.documentViewType = documentViewType;
		this.documentPageFitMode = documentPageFitMode;

	}

	public JFrame buildViewerFrame() {
		JFrame viewer = new JFrame("My PDF Viewer");

		JMenuBar menuBar = buildMenuBar();

		if (menuBar != null)
			viewer.setJMenuBar(menuBar);

		Container contentPane = viewer.getContentPane();
		buildContents(contentPane, false);

		if (viewerController != null)
			viewerController.setViewerFrame(viewer);

		return viewer;
	}

	public JPanel buildViewerPanel() {
		JPanel panel = new JPanel();
		buildContents(panel, true);

		return panel;
	}

	private JMenuBar buildMenuBar() {
		// TODO Auto-generated method stub
		return null;
	}

	private void buildContents(Container cp, boolean embeddableComponent) {

		cp.setLayout(new BorderLayout());
		JToolBar toolbar = buildCompleteToolBar(embeddableComponent);

		if (toolbar != null)
			cp.add(toolbar, BorderLayout.NORTH);

		JSplitPane utilAndDocSplit = buildUtilityAndDocumentSplitPane(embeddableComponent);

		if (utilAndDocSplit != null)
			cp.add(utilAndDocSplit, BorderLayout.CENTER);

	}

	
	private JSplitPane buildUtilityAndDocumentSplitPane(boolean embeddableComponent) {

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setOneTouchExpandable(false);
		splitPane.setDividerSize(8);
		splitPane.setContinuousLayout(true);

		splitPane.setLeftComponent(buildUtilityTabbedPane());

		DocumentViewController viewController = viewerController.getDocumentViewController();
		viewerController.setIsEmbeddedComponent(embeddableComponent);
		splitPane.setRightComponent(viewController.getViewContainer());

		if (viewerController != null)
			viewerController.setUtilityAndDocumentSplitPane(splitPane);

		return splitPane;
	}

	private JTabbedPane buildUtilityTabbedPane() {

		searchPanel = buildSearchPanel();

		if (searchPanel == null)
			return null;

		JTabbedPane utilityTabbedPane = new JTabbedPane();
		utilityTabbedPane.setPreferredSize(new Dimension(400, 400));

		if (searchPanel != null)
			utilityTabbedPane.add(messageBundle.getString("viewer.utilityPane.search.tab.title"), searchPanel);

		if (viewerController != null)
			viewerController.setUtilityTabbedPane(utilityTabbedPane);

		return utilityTabbedPane;

	}

	public NewSearchPanel getSearchPanel() {
		return searchPanel;
	}

	private NewSearchPanel buildSearchPanel() {

		NewSearchPanel searchPanel = new NewSearchPanel(viewerController);

		if (viewerController != null)
			viewerController.setSearchPanel(searchPanel);

		return searchPanel;
	}

	private JToolBar buildCompleteToolBar(boolean embeddableComponent) {

		JToolBar toolbar = new JToolBar();
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));
		commonToolBarSetup(toolbar, true);

		addToToolBar(toolbar, buildUtilityToolBar(embeddableComponent));
		addToToolBar(toolbar, buildZoomToolBar());
		addToToolBar(toolbar, buildToolToolBar());

		if (viewerController != null)
			viewerController.setCompleteToolBar(toolbar);
		return toolbar;
	}

	public JToolBar buildToolToolBar() {

		JToolBar toolbar = new JToolBar();
		commonToolBarSetup(toolbar, false);

		addToToolBar(toolbar, buildTextSelectToolButton());

		return toolbar;

	}

	private JToggleButton buildTextSelectToolButton() {

		JToggleButton btn = makeToolBarToggleButton("SelectTool",
				messageBundle.getString("viewer.toolbar.tool.text.tooltip"), "selection_text", buttonFont);

		if (viewerController != null && btn != null)
			viewerController.setTextSelectToolButton(btn);
		return btn;
	}

	public JToolBar buildZoomToolBar() {

		JToolBar toolbar = new JToolBar();
		commonToolBarSetup(toolbar, false);

		addToToolBar(toolbar, buildZoomOutButton());
		addToToolBar(toolbar, buildZoomCombBox());
		addToToolBar(toolbar, buildZoomInButton());

		return toolbar;
	}

	private JButton buildZoomInButton() {
		JButton btn = makeToolBarButton(messageBundle.getString("viewer.toolbar.zoom.out.label"),
				messageBundle.getString("viewer.toolbar.zoom.out.tooltip"), "zoom_out", buttonFont);

		if (viewerController != null && btn != null)
			viewerController.setZoomOutButton(btn);

		return btn;
	}

	private JComboBox buildZoomCombBox() {

		JComboBox tmp = new JComboBox();
		tmp.setToolTipText(messageBundle.getString("viewer.toolbar.zoom.tooltip"));
		tmp.setPreferredSize(new Dimension(75, tmp.getHeight()));

		for (float zoomLevel : zoomLevels) {
			tmp.addItem(NumberFormat.getPercentInstance().format(zoomLevel));
		}

		tmp.setEditable(true);

		if (viewerController != null)
			viewerController.setZoomComboBox(tmp, zoomLevels);

		return tmp;
	}

	private JButton buildZoomOutButton() {
		JButton btn = makeToolBarButton(messageBundle.getString("viewer.toolbar.zoom.in.label"),
				messageBundle.getString("viewer.toolbar.zoom.in.tooltip"), "zoom_in", buttonFont);

		if (viewerController != null && btn != null)
			viewerController.setZoomInButton(btn);

		return btn;

	}

	public JToolBar buildUtilityToolBar(boolean embeddableComponent) {

		JToolBar toolbar = new JToolBar();
		commonToolBarSetup(toolbar, false);

		addToToolBar(toolbar, buildOpenFileButton());

		addToToolBar(toolbar, buildSaveAsFileButton());
		addToToolBar(toolbar, buildSearchButton());
		addToToolBar(toolbar, buildShowHideUtilityPaneButton());
		return toolbar;
	}

	private JToggleButton buildShowHideUtilityPaneButton() {

		JToggleButton btn = makeToolBarToggleButton(messageBundle.getString("viewer.toolbar.utilityPane.label"),
				messageBundle.getString("viewer.toolbar.utilityPane.tooltip"), "utility_pane", buttonFont);

		if (viewerController != null && btn != null)
			viewerController.setShowHideUtilityPaneButton(btn);

		return btn;
	}

	private JToggleButton makeToolBarToggleButton(String title, String toolTip, String imgName, Font font) {

		JToggleButton tmp = new JToggleButton();
		tmp.setFont(font);

		tmp.setToolTipText(toolTip);
		tmp.setPreferredSize(new Dimension(24, 24));
		tmp.setRolloverEnabled(true);

		tmp.setIcon(new ImageIcon(Images.get(imgName + "_a_32.png")));
		tmp.setPressedIcon(new ImageIcon(Images.get(imgName + "_i_32.png")));
		tmp.setRolloverIcon(new ImageIcon(Images.get(imgName + "_r_32.png")));
		tmp.setDisabledIcon(new ImageIcon(Images.get(imgName + "_i_32.png")));

		tmp.setBorder(BorderFactory.createEmptyBorder());
		tmp.setContentAreaFilled(true);
		tmp.setFocusPainted(true);

		return tmp;

	}

	private JButton buildSaveAsFileButton() {
		JButton btn = makeToolBarButton(messageBundle.getString("viewer.toolbar.saveAs.label"),
				messageBundle.getString("viewer.toolbar.saveAs.tooltip"), "save", buttonFont);

		if (viewerController != null && btn != null)
			viewerController.setSaveAsFileButton(btn);

		return btn;

	}

	private JButton buildOpenFileButton() {
		JButton btn = makeToolBarButton("Open", messageBundle.getString("viewer.toolbar.open.tooltip"), "open",
				buttonFont);

		if (viewerController != null && btn != null)
			viewerController.setOpenFileButton(btn);

		return btn;
	}

	private void addToToolBar(JToolBar toolbar, JComponent comp) {

		if (comp != null)
			toolbar.add(comp);
	}

	private JButton buildSearchButton() {

		JButton btn = makeToolBarButton(messageBundle.getString("viewer.toolbar.search.label"),
				messageBundle.getString("viewer.toolbar.search.tooltip"), "search", buttonFont);

		if (viewerController != null && btn != null)
			viewerController.setSearchButton(btn);

		return btn;
	}

	private JButton makeToolBarButton(String title, String toolTip, String imgName, Font font) {

		JButton tmp = new JButton();
		tmp.setFont(font);

		tmp.setToolTipText(toolTip);
		tmp.setPreferredSize(new Dimension(24, 24));
		tmp.setIcon(new ImageIcon(Images.get(imgName + "_a_32.png")));
		tmp.setPressedIcon(new ImageIcon(Images.get(imgName + "_i_32.png")));
		tmp.setRolloverIcon(new ImageIcon(Images.get(imgName + "_r_32.png")));
		tmp.setDisabledIcon(new ImageIcon(Images.get(imgName + "_i_32.png")));

		tmp.setRolloverEnabled(true);
		tmp.setBorderPainted(false);
		tmp.setContentAreaFilled(true);
		tmp.setFocusPainted(true);

		return tmp;

	}

	private void commonToolBarSetup(JToolBar toolbar, boolean isMainToolBar) {

		if (!isMainToolBar) {
			reflectComponentSetFocusable(toolbar, true);
			reflectJToolBarSetRollover(toolbar, true);
		}

		if (toolbarStyle == TOOLBAR_STYLE_FIXED) {
			toolbar.setFloatable(false);
			if (!isMainToolBar) {
				if (haveMadeAToolBar)
					toolbar.addSeparator();
				haveMadeAToolBar = true;
			}
		}

	}

	protected void reflectJToolBarSetRollover(JToolBar jtoolbar, boolean val) {
		try {
			Class<? extends JToolBar> toolBar = jtoolbar.getClass();
			Method rolloverMethod = toolBar.getMethod("setRollover", Boolean.TYPE);
			if (rolloverMethod != null) {
				rolloverMethod.invoke(jtoolbar, (val ? Boolean.TRUE : Boolean.FALSE));
			}
		} catch (Throwable t) {
		}
	}

	protected void reflectComponentSetFocusable(Component comp, boolean val) {
		try {
			Class<? extends Component> component = comp.getClass();
			Method rolloverMethod = component.getMethod("setFocusable", Boolean.TYPE);
			if (rolloverMethod != null) {
				rolloverMethod.invoke(comp, (val ? Boolean.TRUE : Boolean.FALSE));
			}
		} catch (Throwable t) {
		}
	}

	private Font buildButtonFont() {

		return new Font("Helvetica", Font.PLAIN, 9);
	}

}
