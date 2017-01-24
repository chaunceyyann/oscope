/**
 * you can put a one sentence description of your tool here.
 *
 * (c) 2011
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		D. Cuartielles, A. Goransson
 * @modified	06/12/2011
 * @version		0.1a
 */

 package cc.arduino.adk.processing;
 
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import processing.app.Editor;
import processing.app.Sketch;
import processing.app.Base;
import processing.app.tools.Tool;
import processing.core.PApplet;

public class Arduino_ADK extends JFrame implements Tool, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Editor editor;

	/* GUI */
	private JPanel contentPane;
	private JTextField textModel;
	private JTextField textManufacturer;
	private JButton btnSave;
	private JLabel lblVersion;
	private JTextField textVersion;

	// when creating a tool, the name of the main class which implements Tool
	 // must be the same as the value defined for project.name in your build.properties
 
	public String getMenuTitle() {
		return "Arduino ADK";
	}
 
	public void init(Editor theEditor) {
		this.editor = theEditor;
	}
 
	public void run() {
		// save it before running, or it will not compile
		Sketch sketch = editor.getSketch();
		try {
			sketch.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.loadGUI();
		this.setVisible(true);
		loadInformation();
		System.out.println("Arduino_ADK 0.1a");
		System.out.println("by D. Cuartielles, A. Goransson");
		System.out.println("at http://arduino.cc, http://1scale1.com");
		this.editor.statusNotice("Arduino_ADK launched");
	}

	/**
	 * Create the frame.
	 * @return 
	 */
	public void loadGUI() {

		/* GUI */
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 250, 120);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JLabel lblModel = new JLabel("Model");
		GridBagConstraints gbc_lblModel = new GridBagConstraints();
		gbc_lblModel.anchor = GridBagConstraints.EAST;
		gbc_lblModel.insets = new Insets(0, 0, 5, 5);
		gbc_lblModel.gridx = 0;
		gbc_lblModel.gridy = 0;
		contentPane.add(lblModel, gbc_lblModel);

		textModel = new JTextField();
		GridBagConstraints gbc_textModel = new GridBagConstraints();
		gbc_textModel.insets = new Insets(0, 0, 5, 0);
		gbc_textModel.fill = GridBagConstraints.HORIZONTAL;
		gbc_textModel.gridx = 1;
		gbc_textModel.gridy = 0;
		contentPane.add(textModel, gbc_textModel);
		textModel.setColumns(10);

		JLabel lblManufacturer = new JLabel("Manufacturer");
		GridBagConstraints gbc_lblManufacturer = new GridBagConstraints();
		gbc_lblManufacturer.insets = new Insets(0, 0, 5, 5);
		gbc_lblManufacturer.anchor = GridBagConstraints.EAST;
		gbc_lblManufacturer.gridx = 0;
		gbc_lblManufacturer.gridy = 1;
		contentPane.add(lblManufacturer, gbc_lblManufacturer);

		textManufacturer = new JTextField();
		GridBagConstraints gbc_textManufacturer = new GridBagConstraints();
		gbc_textManufacturer.insets = new Insets(0, 0, 5, 0);
		gbc_textManufacturer.fill = GridBagConstraints.HORIZONTAL;
		gbc_textManufacturer.gridx = 1;
		gbc_textManufacturer.gridy = 1;
		contentPane.add(textManufacturer, gbc_textManufacturer);
		textManufacturer.setColumns(10);

		btnSave = new JButton("Run on Device");
		btnSave.addActionListener(this);

		lblVersion = new JLabel("Version");
		GridBagConstraints gbc_lblVersion = new GridBagConstraints();
		gbc_lblVersion.anchor = GridBagConstraints.EAST;
		gbc_lblVersion.insets = new Insets(0, 0, 5, 5);
		gbc_lblVersion.gridx = 0;
		gbc_lblVersion.gridy = 2;
		contentPane.add(lblVersion, gbc_lblVersion);

		textVersion = new JTextField();
		GridBagConstraints gbc_textVersion = new GridBagConstraints();
		gbc_textVersion.insets = new Insets(0, 0, 5, 0);
		gbc_textVersion.fill = GridBagConstraints.HORIZONTAL;
		gbc_textVersion.gridx = 1;
		gbc_textVersion.gridy = 2;
		contentPane.add(textVersion, gbc_textVersion);
		textVersion.setColumns(10);
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSave.gridx = 1;
		gbc_btnSave.gridy = 3;
		contentPane.add(btnSave, gbc_btnSave);

		this.pack();
		
		this.addWindowListener(new WindowAdapter() {
			// Invoked when a window is in the process of being closed.
			public void windowClosing(WindowEvent e) {
				System.out.println("Arduino_ADK closed without uploading");
				editor.statusError("Arduino_ADK closed without uploading");
				editor.stopIndeterminate();
				/* Close */
				dispose();
			}
		});

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		editor.startIndeterminate();
		this.editor.statusNotice("writing accessory manifest");
		String model = textModel.getText().trim();
		String manufacturer = textManufacturer.getText().trim();
		String version = textVersion.getText().trim();

		/* Get the content */
		String[] lines = PApplet.loadStrings(new File(editor.getSketch()
				.getFolder(), "accessory_filter.xml"));

		/* Find the line that correspond to the pattern 'model="' */
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].indexOf("manufacturer=\"") > -1) {
				String line = lines[i];

				line = "<usb-accessory manufacturer=\"" + manufacturer
						+ "\" model=\"" + model + "\" version=\"" + version
						+ "\" />";

				lines[i] = line;
			}
		}

		/* Write the lines to the file */
		final PrintWriter writer = PApplet.createWriter(new File(editor
				.getSketch().getFolder(), "accessory_filter.xml"));

		for (int i = 0; i < lines.length; i++) {
			writer.println(lines[i]);
		}

		writer.flush();
		writer.close();

		// we launch the compilation thread and forget about it
		// it is launched as a daemon and we don't need to take care of it
		// it will be reporting to the editor by itself
		MyCompileThread compile = new MyCompileThread(editor);

		/* Close */
		this.dispose();
	}

	/**
	 * Loads the information from the present xml into the GUI, if there is a
	 * file, otherwise it creates the default file. For the Android DemoKit.
	 */
	private void loadInformation() {

		File accessory_filter = new File(editor.getSketch().getFolder(),
				"accessory_filter.xml");

		if (accessory_filter.exists()) {
			/* Load information */

			/* Get the content */
			String[] lines = PApplet.loadStrings(accessory_filter);

			PApplet.println("TEST TEST: " + lines.length);

			/* Search the lines */
			for (int i = 0; i < lines.length; i++) {
				if (lines[i].indexOf("manufacturer=\"") > -1) {
					String manufacturerPattern = "manufacturer=\"";
					int manufacturerStart = lines[i]
							.indexOf(manufacturerPattern);
					int manufacturerEnd = lines[i].indexOf("\"",
							manufacturerStart + manufacturerPattern.length());

					textManufacturer.setText(lines[i].substring(
							manufacturerStart + manufacturerPattern.length(),
							manufacturerEnd));

					String modelPattern = "model=\"";
					int modelStart = lines[i].indexOf(modelPattern);
					int modelEnd = lines[i].indexOf("\"", modelStart
							+ modelPattern.length());

					textModel.setText(lines[i].substring(modelStart
							+ modelPattern.length(), modelEnd));

					String versionPattern = "version=\"";
					int versionStart = lines[i].indexOf(versionPattern);
					int versionEnd = lines[i].indexOf("\"", versionStart
							+ versionPattern.length());

					textVersion.setText(lines[i].substring(versionStart
							+ versionPattern.length(), versionEnd));

				}
			}
		} else {
			/* Create file */
			/* Default values for the Google ADK */
			String model = "Mega_ADK";
			String manufacturer = "Arduino SA";
			String version = "1.0";

			/* Create the file */
			final PrintWriter writer = PApplet.createWriter(accessory_filter);

			/* Write the xml */
			writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
			writer.println("<resources>");
			writer.println("<usb-accessory manufacturer=\"" + manufacturer
					+ "\" model=\"" + model + "\" version=\"" + version
					+ "\" />");
			writer.println("</resources>");

			/* Save */
			writer.flush();
			writer.close();

			/* Finally load the information into the GUI */
			loadInformation();
		}
	}
}



