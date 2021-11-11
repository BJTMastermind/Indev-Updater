package me.bjtmastermind.i2ic;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import me.bjtmastermind.i2ic.toInfdev.CreateChunkFile;
import me.bjtmastermind.i2ic.toInfdev.CreateFolderStructure;
import me.bjtmastermind.i2ic.toInfdev.CreateLevelFile;
import me.bjtmastermind.nbt.io.NBTUtil;
import me.bjtmastermind.nbt.io.NamedTag;
import me.bjtmastermind.nbt.tag.CompoundTag;

public class GUI {
	public static File file;
	public static JFrame frame;
	public static JTextField fileSelected;
	public static JTextArea console;
	public static JScrollPane scroll;
	public static JLabel typeLabel;
	public static JButton uploadBtn, convertBtn;
	public static JRadioButton inf, beta, release;
	public static JFileChooser fc = new JFileChooser();
	
	public static void open() {
		frame = new JFrame("Indev to Infdev World Converter");
		frame.getContentPane().setBackground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 546, 350);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(false);
		populateWindow();
	}
	
	private static void populateWindow() {
		fileSelected = new JTextField("Select World");
		fileSelected.setBounds(10, 30, 466, 50);
		fileSelected.setEditable(false);
		fileSelected.setVisible(true);
		frame.getContentPane().add(fileSelected);
				
		uploadBtn = new JButton("");
		uploadBtn.setBounds(478, 30, 50, 50);
		ImageIcon icon = new ImageIcon(GUI.class.getClassLoader().getResource("icons/select_world_icon.png"));
		Image img = icon.getImage();
		Image img1 = img.getScaledInstance(uploadBtn.getWidth() - 20, uploadBtn.getHeight() - 20, Image.SCALE_SMOOTH);
		uploadBtn.setIcon(new ImageIcon(img1));
		uploadBtn.setVisible(true);
		uploadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Minecraft Indev World (*.mclevel)","mclevel");
				fc.setFileFilter(filter);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				if (e.getSource() == uploadBtn) {
					int returnVal = fc.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						fileSelected.setText(fc.getSelectedFile().toString());
						file = fc.getSelectedFile();
						
						if(fileSelected.getText() != "Select World") {
							convertBtn.setEnabled(true);
							console.setText("");
						}
					}
				}
			}
		});
		frame.getContentPane().add(uploadBtn);
		
		typeLabel = new JLabel("Convert To");
		typeLabel.setBounds(10, 50, 100, 100);
		typeLabel.setVisible(true);
		frame.getContentPane().add(typeLabel);
		
		inf = new JRadioButton("Infdev", true);
		inf.setBounds(13, 110, 70, 30);
		inf.setVisible(true);
		inf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(inf.isSelected()) {
					beta.setSelected(false);
					release.setSelected(false);
				} else if(!inf.isSelected() && !beta.isSelected() && !release.isSelected()) {
					inf.setSelected(true);
				}
			}
		});
		frame.getContentPane().add(inf);
		
		beta = new JRadioButton("Beta");
		beta.setBounds(13, 135, 70, 30);
		beta.setEnabled(false);
		beta.setVisible(true);
		beta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(beta.isSelected()) {
					inf.setSelected(false);
					release.setSelected(false);
				} else if(!inf.isSelected() && !beta.isSelected() && !release.isSelected()) {
					beta.setSelected(true);
				}
			}
		});
		frame.getContentPane().add(beta);
		
		release = new JRadioButton("1.12.2");
		release.setBounds(13, 160, 70, 30);
		release.setEnabled(false);
		release.setVisible(true);
		release.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(release.isSelected()) {
					beta.setSelected(false);
					inf.setSelected(false);
					release.setSelected(false);
				} else if(!inf.isSelected() && !beta.isSelected() && !release.isSelected()) {
					release.setSelected(true);
				}
			}
		});
		frame.getContentPane().add(release);
		
		convertBtn = new JButton("Start Convert");
		convertBtn.setBounds(100, 95, 427, 50);
		convertBtn.setEnabled(false);
		convertBtn.setVisible(true);
		convertBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RunConversion run = new RunConversion();
				run.start();
			}
		});
		frame.getContentPane().add(convertBtn);
				
		console = new JTextArea();
		console.setBounds(100, 150, 427, 155);
		console.setBackground(Color.BLACK);
		console.setForeground(Color.WHITE);
		console.setEditable(false);
		console.setVisible(true);		
		frame.getContentPane().add(console);
				
		frame.invalidate();
		frame.validate();
		frame.repaint();
	}
	
	public static void appendConsole(String text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String newText = console.getText() + text + "\n";
				console.setText(newText);
			}
		});
	}
	
	public static void reset() {
		uploadBtn.setEnabled(true);
		fileSelected.setText("Select World");
		inf.setEnabled(true);
		beta.setEnabled(false);
		release.setEnabled(false);
	}
}

class RunConversion extends Thread {
	@Override
	public void run() {
		GUI.uploadBtn.setEnabled(false);
		GUI.convertBtn.setEnabled(false);
										
		GUI.inf.setEnabled(false);
		GUI.beta.setEnabled(false);
		GUI.release.setEnabled(false);
						
		try {
		
			NamedTag ml = NBTUtil.read(GUI.file.toString());
			CompoundTag mclevel = (CompoundTag) ml.getTag();
		
			int widthOfMap = (int) mclevel.getCompoundTag("Map").getShort("Width");
			int lengthOfMap = (int) mclevel.getCompoundTag("Map").getShort("Length");
						
			if(GUI.inf.isSelected()) {
				System.out.println("To Infdev");
				
				GUI.appendConsole("Converting Chunks . . .");
				CreateFolderStructure.create(GUI.file.toString());
				Thread.sleep(200);
				
				String fileName = GUI.file.getName();
				String path = GUI.file.toString().split(fileName)[0];
			
				for(int x = 0; x <= widthOfMap - 16; x += 16) {
					for(int z = 0; z <= lengthOfMap - 16; z += 16) {
						CreateChunkFile.createChunk(GUI.file.toString(), path, x, z);
					}
				}					
				GUI.appendConsole("Chunks Converted!");
				
				CreateLevelFile.createLevel(GUI.file.toString(), path);
				GUI.appendConsole("Conversion of " + GUI.file.getName() + " Complete!");
				GUI.reset();						
			} else if(GUI.beta.isSelected()) {
				System.out.println("To Beta");
			} else if(GUI.release.isSelected()) {
				System.out.println("To 1.12.2");
			}
		
		} catch(IOException | InterruptedException ex) {
			ex.printStackTrace();
		}
		
		//console.setText("Converting Chunks . . .\nChunks Converted!\nCreating level.dat . . .\nlevel.dat Creating!\nRecalculating Lighting . . .\nLighting Recalulated!");
	}
}
