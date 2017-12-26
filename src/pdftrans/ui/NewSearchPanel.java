package pdftrans.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.icepdf.core.pobjects.Document;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.utility.search.SearchPanel;

import pdftrans.DictionaryAPICall;

public class NewSearchPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JTextField searchText;
	private JButton searchButton;
	private ResourceBundle messageBundle;
	private JButton clearSearchButton;
	private NewSwingController controller;
	private GridBagConstraints constraints;
	private JEditorPane result;
	private JScrollPane jsc;

	public NewSearchPanel(NewSwingController controller) {

		super(true);
		setFocusable(true);
		this.controller = controller;
		this.messageBundle = this.controller.getMessageBundle();
		setGui();
		setDocument();
	}

	private void setGui() {

		JLabel searchLabel = new JLabel(messageBundle.getString("viewer.utilityPane.search.searchText.label"));
		searchText = new JTextField("", 20);
		searchText.addActionListener(this);

		searchButton = new JButton(messageBundle.getString("viewer.utilityPane.search.searchButton.label"));
		searchButton.addActionListener(this);
		clearSearchButton = new JButton(messageBundle.getString("viewer.utilityPane.search.clearSearchButton.label"));
		clearSearchButton.addActionListener(this);

		result = new JEditorPane();
		result.setPreferredSize(new Dimension(200, 400));
		result.setEditable(false);
		jsc = new JScrollPane(result, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JLabel resultLabel = new JLabel("Result");

		GridBagLayout layout = new GridBagLayout();
		constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 1.0;
		constraints.weighty = 0;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(10, 5, 1, 5);

		JPanel searchPanel = new JPanel(layout);

		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(searchPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.NORTH);

		addGB(searchPanel, searchLabel, 0, 0, 2, 1);

		constraints.insets = new Insets(1, 1, 1, 5);
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		addGB(searchPanel, searchText, 0, 1, 2, 1);

		constraints.insets = new Insets(1, 1, 1, 5);
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.EAST;
		addGB(searchPanel, searchButton, 0, 2, 1, 1);

		constraints.insets = new Insets(1, 1, 1, 5);
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.REMAINDER;
		addGB(searchPanel, clearSearchButton, 1, 2, 1, 1);

		constraints.insets = new Insets(10, 5, 1, 5);
		constraints.fill = GridBagConstraints.NONE;
		addGB(searchPanel, resultLabel, 0, 8, 2, 1);

		constraints.insets = new Insets(1, 5, 1, 5);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		addGB(searchPanel, jsc, 0, 9, 2, 1);

	}

	private void addGB(JPanel panel, JComponent comp, int x, int y, int rowspan, int colspan) {

		constraints.gridx = x;
		constraints.gridy = y;
		constraints.gridwidth = rowspan;
		constraints.gridheight = colspan;

		panel.add(comp, constraints);

	}

	public void setDocument() {

		if (searchText != null)
			searchText.setText("");
		if (searchButton != null)
			searchButton.setText(messageBundle.getString("viewer.utilityPane.search.tab.title"));

	}

	public JTextField getResultField() {
		return searchText;
	}

	public JButton getSearchButton() {
		return searchButton;
	}

	public void actionPerformed(ActionEvent event) {

		Object source = event.getSource();

		if (searchText.getText().length() > 0 && (source == searchButton || source == searchText)) {
			String word = searchText.getText();
			DictionaryAPICall dic = new DictionaryAPICall();
			try {
				dic.callAPI(word, result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (source == clearSearchButton) {
			searchText.setText("");
			result.setText("");
		}
	}

}
