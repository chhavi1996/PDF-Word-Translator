package pdftrans;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Desktop;

import javax.swing.JScrollPane;
import javax.swing.JPanel;

public class PDFTranslator {

	private JFrame frame;
	private JTextArea ta;
	private JScrollPane jsp;
	private JPanel panel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PDFTranslator window = new PDFTranslator();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public PDFTranslator(){
		initialize();
		
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(200, 200, 650, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnUpload = new JButton("Open File...");
		btnUpload.setBounds(28, 34, 116, 25);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(btnUpload);
		
		panel = new JPanel();
		panel.setBounds(28, 91, 591, 376);
		frame.getContentPane().add(panel);
		
		ta = new JTextArea(22,50);
		//panel.add(ta);
		ta.setBackground(Color.WHITE);
		ta.setBounds(38, 71, 534, 363);
		jsp=new JScrollPane(ta,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(jsp);
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser=new JFileChooser();
				chooser.showOpenDialog(null);
				File f =chooser.getSelectedFile();
				String fileName=f.getAbsolutePath();
				//System.out.println(fileName);
				
				try{
					
					Desktop.getDesktop().open(f);
					
					
				}catch (Exception e) {
					
					System.out.println(e);
					JOptionPane.showMessageDialog(null, e);
				}
				
				
			}
		});
		
	}
}
