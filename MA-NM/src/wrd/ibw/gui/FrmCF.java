package wrd.ibw.gui;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Color;
import javax.swing.JButton;
import wrd.ibw.da.DBConnection;
import wrd.ibw.utils.Util;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.SwingConstants;

public class FrmCF extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	private JFileChooser embFileChooser;
	private JFileChooser extFileChooser;
	private File embImageFile = null;
	private File extImageFile = null;
	private Image embImageInfo = null;
	private Image extImageInfo = null;
	
	private JButton btnStart = null;
	
	private int embImageWidth = 0;
	private int embImageHeight = 0;
	private int extImageWidth = 0;
	private int extImageHeight = 0;
	
	private int embeddedImage[][];
	private int extractedImage[][];
	
	private JTextField txCF;
	
	public FrmCF() {
		try {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			this.setTitle("[Sardroudi & Ibrahim, 2010] Embed Watermark...");
			this.setSize(448,320);
			this.getContentPane().setLayout(null);
			
			embFileChooser = new JFileChooser(System.getProperty("user.dir") + System.getProperty("file.separator") + "img");
			extFileChooser = new JFileChooser(System.getProperty("user.dir") + System.getProperty("file.separator") + "img");
			
			embFileChooser.setAcceptAllFileFilterUsed(false);
			extFileChooser.setAcceptAllFileFilterUsed(false);
			
			String[] suffices = ImageIO.getReaderFileSuffixes();
			
			for (int i = 0; i < suffices.length; i++) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image File (" + suffices[i] + ")", suffices[i]);
				if(suffices[i].equals("bmp")){
					embFileChooser.addChoosableFileFilter(filter);
					extFileChooser.addChoosableFileFilter(filter);
				}
			}
			
			JPanel pnEmbImageSelector = new JPanel();
			pnEmbImageSelector.setLayout(null);
			pnEmbImageSelector.setBorder(BorderFactory.createTitledBorder(null, "  Embedded Image  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			pnEmbImageSelector.setBounds(11, 8, 204, 232);
			getContentPane().add(pnEmbImageSelector);
			
			JLabel lbEmbImgViever = new JLabel();
			lbEmbImgViever.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			lbEmbImgViever.setBounds(10, 23, 184, 164);
			pnEmbImageSelector.add(lbEmbImgViever);
		
			JButton btnOpenEmbImage = new JButton("Load Image");
			btnOpenEmbImage.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int returnVal = embFileChooser.showOpenDialog(FrmCF.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						embImageFile = embFileChooser.getSelectedFile();
			            try {
			            	embImageInfo = ImageIO.read(embImageFile);
			            	Image bImage = embImageInfo.getScaledInstance(lbEmbImgViever.getWidth(), lbEmbImgViever.getHeight(), Image.SCALE_SMOOTH);
			            	ImageIcon imgIcon = new ImageIcon(bImage);
			            	lbEmbImgViever.setIcon(imgIcon);
			            	Util.defineImageArray(embImageFile);
			            	
			            	embeddedImage = Util.getImageMatrix();
			            	embImageWidth = Util.getImageWidth();
			            	embImageHeight = Util.getImageHeight();
	                    } 
			            catch (IOException e) {
			            	btnStart.setEnabled(false);
	                        e.printStackTrace();
	                    }
			            catch (Exception ex) {
			            	btnStart.setEnabled(false);
	                        ex.printStackTrace();
	                    }
			        } 
					else {
						btnStart.setEnabled(false);
			            //log.append("Open command cancelled by user." + newline);
			        }
				}
			});
			btnOpenEmbImage.setBounds(10, 198, 184, 23);
			pnEmbImageSelector.add(btnOpenEmbImage);
		btnStart = new JButton("Calculate");
		btnStart.setEnabled(false);
		btnStart.setBounds(224, 248, 97, 23);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int cumul = 0;
				float cf = 0;
				try {
					for (int i = 0; i < embImageWidth; i++) {
	 					for (int j = 0; j < embImageHeight; j++) {
	 						cumul = cumul + (embeddedImage[j][i]^extractedImage[j][i]); 
	 					}
	 				}
					cf = 100*cumul/(embImageWidth*embImageHeight);
					DecimalFormat df = new DecimalFormat("##.##");
					df.setRoundingMode(RoundingMode.DOWN);
					txCF.setText(String.valueOf(df.format(cf)));	
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		getContentPane().add(btnStart);
		
		JButton btnExit = new JButton("Close");
		btnExit.setBounds(327, 248, 95, 23);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dispose();
				} 
				catch (Exception ex) {
					System.out.println(ex.getMessage());
				}
			}
		});
		getContentPane().add(btnExit);
		
		JPanel pnEctrImageSelector = new JPanel();
		pnEctrImageSelector.setLayout(null);
		pnEctrImageSelector.setBorder(BorderFactory.createTitledBorder(null, "  Extracted Image  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnEctrImageSelector.setBounds(220, 8, 204, 232);
		getContentPane().add(pnEctrImageSelector);
		
		JLabel lbExtrImgViever = new JLabel();
		lbExtrImgViever.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lbExtrImgViever.setBounds(10, 23, 184, 164);
		pnEctrImageSelector.add(lbExtrImgViever);
		
		JButton btnOpenEctrImage = new JButton("Load Image");
		btnOpenEctrImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = extFileChooser.showOpenDialog(FrmCF.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					extImageFile = extFileChooser.getSelectedFile();
		            try {
		            	extImageInfo = ImageIO.read(extImageFile);
		            	Image bImage = extImageInfo.getScaledInstance(lbExtrImgViever.getWidth(), lbExtrImgViever.getHeight(), Image.SCALE_SMOOTH);
		            	ImageIcon imgIcon = new ImageIcon(bImage);
		            	lbExtrImgViever.setIcon(imgIcon);
		            	Util.defineImageArray(extImageFile);
		            	
		            	extractedImage = Util.getImageMatrix();
		            	
		            	
		            	extImageWidth = Util.getImageWidth();
		            	extImageHeight = Util.getImageHeight();
		            	
		            	int black = 0;
						int white = 0;
						int red = 0;
						for (int i = 0; i < extImageWidth; i++) {
		 					for (int j = 0; j < extImageHeight; j++) {
		 						if(extractedImage[j][i] == 0)
		 							white++;
		 						else
		 							if(extractedImage[j][i] == 1)
		 								black++;
		 							else {	
		 								red++;
		 							}
		 					}
		 				}
						
						
						
						System.out.println("BLACK - PIXELS: " + String.valueOf(black));
						System.out.println("WHITE - PIXELS: " + String.valueOf(white));
						System.out.println("RED --- PIXELS: " + String.valueOf(red));
						
						
                    } 
		            catch (IOException e) {
		            	btnStart.setEnabled(false);
                        e.printStackTrace();
                    }
		            catch (Exception ex) {
		            	btnStart.setEnabled(false);
                        ex.printStackTrace();
                    }
		        } 
				else {
					btnStart.setEnabled(false);
		            //log.append("Open command cancelled by user." + newline);
		        }
				
				btnStart.setEnabled(true);
			}
		});
		btnOpenEctrImage.setBounds(10, 198, 184, 23);
		pnEctrImageSelector.add(btnOpenEctrImage);
		
		
		JLabel lblCorrectionFactorcf = new JLabel("Correction Factor (CF):");
		lblCorrectionFactorcf.setBounds(11, 254, 132, 14);
		getContentPane().add(lblCorrectionFactorcf);
		lblCorrectionFactorcf.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txCF = new JTextField();
		txCF.setBounds(147, 251, 51, 20);
		getContentPane().add(txCF);
		txCF.setText("0");
		txCF.setEditable(false);
		txCF.setColumns(10);
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		

	}
}
