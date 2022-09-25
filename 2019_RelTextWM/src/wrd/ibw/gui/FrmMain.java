package wrd.ibw.gui;

import java.awt.ComponentOrientation;
import java.awt.Font;

import javax.print.PrintService;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.TitledBorder;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Vector;
import java.awt.event.ActionEvent;

import wrd.ibw.da.DBConnection;
import wrd.ibw.gui.att.FrmAttSubsetDelete;
import wrd.ibw.gui.att.FrmAttSupersetInsert;
import wrd.ibw.gui.att.FrmAttackUpdate;
import wrd.ibw.utils.Util;
import wrd.ibw.utils.jssim.SsimCalculator;

import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

public class FrmMain extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	private SsimCalculator mySSIMCalc = null;
	private SsimCalculator mySSIMCalc2 = null;
	private JLabel lblStatus = null;
	private JTextField tfServer;
	private JTextField tfSID;
	private JTextField tfUser;
	private JPasswordField tfPassword;
	private DBConnection dbConnection = null;
	
	private static FrmAttSubsetDelete frmAttDelete = null;
	private static FrmAttSupersetInsert frmAttInsert = null; 
	private static FrmAttackUpdate frmAttUpdate = null; 
	
	private JButton btnEmbedWM = null;
	private JButton btnExtractWM = null;
	private JButton btnMetrics;
	
	private static FrmEmbedWM frmEmbedWM = null;
	private static FrmExtractWM frmExtractWM = null;
	
	private int originalImage[][];
	private int embeddedImage[][];
	private int recoveredImage[][];
	private int enhancedImage[][];
	private int originalImgHeight;
	private int originalImgWidth;
	
	private JTextField txCF;
	private JTextField txCF2;
	private JTextField txMSE1;
	private JTextField txMSE2;
	private JTextField txPSNR1;
	private JTextField txPSNR2;
	private JTextField txSSIM_Ext;
	private JTextField txSSIM_Enh;
	private JTextField txEmb_Ext;
	private JTextField txOrig_Ext;
	private JTextField txSSIMOrig_Ext;
	private JTextField txSSIMEmb_Ext;
	private JTextField txOrig_Emb;
	private JTextField txSSIMOrig_Emb;
	private JCheckBox cbNoExt = null;
	
	
	public FrmMain() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setTitle("Relational Textual Database Watermarking...");
		this.setSize(552,421);
		this.getContentPane().setLayout(null);
		
		/*frmEmbedWM = new FrmEmbedWM(dbConnection);
		frmEmbedWM.setLocationRelativeTo(null);*/
		/*frmExtractWM = new FrmExtractWM(dbConnection);
		frmExtractWM.setLocationRelativeTo(null);*/
		
		JPanel pnConnection = new JPanel();
		pnConnection.setBounds(10, 11, 284, 167);
		getContentPane().add(pnConnection);
		pnConnection.setBorder(BorderFactory.createTitledBorder(null, "  Database Connection  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnConnection.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Server:");
		lblNewLabel.setBounds(10, 32, 77, 14);
		pnConnection.add(lblNewLabel);
		
		JLabel lblDatabase = new JLabel("SID:");
		lblDatabase.setBounds(10, 57, 77, 14);
		pnConnection.add(lblDatabase);
		
		JLabel lblUser = new JLabel("User:");
		lblUser.setBounds(10, 82, 77, 14);
		pnConnection.add(lblUser);
		
		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(10, 107, 77, 14);
		pnConnection.add(lblPassword);
		
		tfServer = new JTextField();
		tfServer.setText("localhost");
		tfServer.setBounds(77, 29, 197, 20);
		pnConnection.add(tfServer);
		tfServer.setColumns(10);
		
		tfSID = new JTextField();
		tfSID.setText("xe");
		tfSID.setColumns(10);
		tfSID.setBounds(77, 54, 197, 20);
		pnConnection.add(tfSID);
		
		tfUser = new JTextField();
		tfUser.setText("system");
		tfUser.setColumns(10);
		tfUser.setBounds(77, 79, 197, 20);
		pnConnection.add(tfUser);
		
		tfPassword = new JPasswordField();
		tfPassword.setText("gort");
		tfPassword.setColumns(10);
		tfPassword.setBounds(77, 104, 197, 20);
		pnConnection.add(tfPassword);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(btnConnect.getText().equals("Connect")){
					if(dbConnection == null){
						dbConnection = new DBConnection(tfServer.getText(), tfSID.getText(), tfUser.getText(), String.copyValueOf(tfPassword.getPassword()));
						if(dbConnection.getConnection() != null){
							getBtnEmbedWM().setEnabled(true);
							getBtnExtractWM().setEnabled(true);
							btnMetrics.setEnabled(true);
							btnConnect.setText("Disconnect");
							lblStatus.setForeground(new Color(34, 139, 34));
							lblStatus.setText("Connected...");
						}else{
							JOptionPane.showMessageDialog(null, "Connection failure!!!");
							btnConnect.setText("Connect");
							lblStatus.setForeground(Color.RED);
							lblStatus.setText("Disconnected...");
							getBtnEmbedWM().setEnabled(false);
							getBtnExtractWM().setEnabled(false);
							btnMetrics.setEnabled(false);
						}
					}
				}else{
					if(dbConnection.getConnection() != null){
						try {
							dbConnection.getConnection().close();
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Some problem!!!");
							lblStatus.setForeground(Color.RED);
							lblStatus.setText("Disconnected...");
						}
					} 
					dbConnection = null;
					//JOptionPane.showMessageDialog(null, "Connection closed!!!");
					lblStatus.setForeground(Color.RED);
					lblStatus.setText("Disconnected...");
					getBtnEmbedWM().setEnabled(false);
					getBtnExtractWM().setEnabled(false);
					btnConnect.setText("Connect");
					btnMetrics.setEnabled(false);
				}
			}
		});
		
		
		btnConnect.setBounds(162, 135, 112, 23);
		pnConnection.add(btnConnect);
		
		lblStatus = new JLabel("Disconnected...");
		lblStatus.setBounds(54, 142, 95, 14);
		pnConnection.add(lblStatus);
		lblStatus.setForeground(Color.RED);
		
		JLabel lblCaption = new JLabel("Status: ");
		lblCaption.setBounds(10, 142, 46, 14);
		pnConnection.add(lblCaption);
		
		JPanel pnWatermark = new JPanel();
		pnWatermark.setLayout(null);
		pnWatermark.setBorder(BorderFactory.createTitledBorder(null, "  Watermarking Processes  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnWatermark.setBounds(10, 189, 284, 63);
		getContentPane().add(pnWatermark);
		pnWatermark.add(getBtnExtractWM());
		pnWatermark.add(getBtnEmbedWM());
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createTitledBorder(null, "  Evaluation Metrics  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		panel.setBounds(304, 11, 231, 205);
		getContentPane().add(panel);
		
		btnMetrics = new JButton("Calculate");
		btnMetrics.setEnabled(false);
		btnMetrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*if(frmCF == null){
					frmCF = new FrmCF();
					frmCF.setLocationRelativeTo(null);
				}
				frmCF.setVisible(true);*/
				
				originalImgWidth = frmEmbedWM.getEmbedImgWidth();
				originalImgHeight = frmEmbedWM.gettEmbedImgHeigh();
				
				originalImage = new int[originalImgHeight][originalImgWidth]; 
				originalImage = frmEmbedWM.getImgToEmbed();
				
				if((originalImgWidth == frmExtractWM.getExtImgWidth())&&(originalImgHeight == frmExtractWM.getExtImgHeigh())){
					recoveredImage = new int[frmExtractWM.getExtImgHeigh()][frmExtractWM.getExtImgWidth()]; 
					recoveredImage = frmExtractWM.getExtractedImg();
					
					embeddedImage = new int[frmExtractWM.getExtImgHeigh()][frmExtractWM.getExtImgWidth()]; 
					embeddedImage = frmEmbedWM.getEmbeddedImg();
					
					enhancedImage = new int[frmExtractWM.getExtImgHeigh()][frmExtractWM.getExtImgWidth()];
					enhancedImage  = frmExtractWM.getEnhancedImg();
					
					double cumul = 0;		float mse1 = 0;
					double cumul2 = 0;
					double cumul3 = 0;	float mse2 = 0;
					double cumul4 = 0;	float mse4 = 0;
					double cf = 0;       float me1 = 0;
					double cf2 = 0;      float me2 = 0;
					double cf3 = 0;
					double cf4 = 0;
					int embedded_size = 0;
					try {
						for (int i = 0; i < originalImgWidth; i++) {
		 					for (int j = 0; j < originalImgHeight; j++) {
		 						if(recoveredImage[j][i] != -1){
		 							cumul = cumul + (originalImage[j][i] ^ (-1*(recoveredImage[j][i]-1)));
		 							mse1 = mse1 + (float)Math.pow((originalImage[j][i] - recoveredImage[j][i]),2);
		 						}
		 						if(embeddedImage[j][i] != -1){
		 							cumul3 = cumul3 + (embeddedImage[j][i] ^ (-1*(recoveredImage[j][i]-1)));
		 							//mse1 = mse1 + (float)Math.pow((embeddedImage[j][i] - recoveredImage[j][i]),2);
		 							
		 							//for the original vs the embeded one
		 							cumul4 = cumul4 + (originalImage[j][i] ^ (-1*(embeddedImage[j][i]-1)));
		 							mse4 = mse4 + (float)Math.pow((originalImage[j][i] - embeddedImage[j][i]),2);
		 						}
		 						if(enhancedImage[j][i] != -1){
		 							cumul2 = cumul2 + (originalImage[j][i] ^ (-1*(enhancedImage[j][i]-1)));
		 							mse2 = mse2 + (float)Math.pow((originalImage[j][i] - enhancedImage[j][i]),2);
		 						}
		 					}
		 				}
						
						int differentes = 0;
						
						for (int i = 0; i < originalImgWidth; i++) {
		 					for (int j = 0; j < originalImgHeight; j++) {
		 						if(recoveredImage[j][i] != embeddedImage[j][i]){
		 							differentes++;
		 						}
		 						 
		 					}
		 				}
						
						
						System.out.println("DIFERENTESSSS: " + differentes);
						
						Vector<Integer> embLinear = new Vector<Integer>();
						Vector<Integer> extLinear = new Vector<Integer>();
						
						for (int i = 0; i < originalImgWidth; i++) {
		 					for (int j = 0; j < originalImgHeight; j++) {
		 						if(embeddedImage[j][i] != -1){
		 							embLinear.add(embeddedImage[j][i]);
		 							embLinear.add(recoveredImage[j][i]);
		 						}
		 					}
		 				}
						
						cumul3 = 0;
						
						for (int i = 0; i < embLinear.size(); i++) {
							cumul3 = cumul3 + (embLinear.elementAt(i) ^ (-1*(embLinear.elementAt(i)-1)));
						}
						
						
						cf = 100*cumul/(originalImgWidth*originalImgHeight);
						cf4 = 100*cumul4/(originalImgWidth*originalImgHeight);
						cf2 = 100*cumul2/(originalImgWidth*originalImgHeight);
						cf3 = 100*cumul3/embLinear.size();
						
						
						me1 = mse1/(originalImgWidth*originalImgHeight);
						me2 = mse2/(originalImgWidth*originalImgHeight);
						
						
						DecimalFormat df = new DecimalFormat("##.##");
						df.setRoundingMode(RoundingMode.DOWN);
						
						
						System.out.println("Correction Factor: " + cf);
						
						txCF.setText(String.valueOf(df.format(cf)));
						txCF2.setText(String.valueOf(df.format(cf2)));	
						txEmb_Ext.setText(String.valueOf(df.format(cf3)));
						txOrig_Ext.setText(String.valueOf(df.format(cf)));
						
						txOrig_Emb.setText(String.valueOf(df.format(cf4)));
						
						System.out.println("Embebida contra extraida: " + cf3);
						System.out.println("Original contra extraida: " + cf);
						
						DecimalFormat df1 = new DecimalFormat("##.######");
						df1.setRoundingMode(RoundingMode.DOWN);
						
						txMSE1.setText(String.valueOf(df1.format(me1)));
						txMSE2.setText(String.valueOf(df1.format(me2)));
						
						DecimalFormat df2 = new DecimalFormat("###.####");
						df2.setRoundingMode(RoundingMode.DOWN);
						
						
						
						
						txPSNR1.setText(String.valueOf(df2.format(20*Math.log10(1)-10*Math.log10(me1))));
						txPSNR2.setText(String.valueOf(df2.format(20*Math.log10(1)-10*Math.log10(me2))));
				
					} 
					catch (Exception e1) {
						e1.printStackTrace();
					}
					
				}else{
					JOptionPane.showMessageDialog(null, "The dimension of the extracted image is different to the embedded image...");
				}
				
				
				
				
					
						
					
				/*
				
				int black = 0;
				int white = 0;
				int red = 0;
				for (int i = 0; i < originalImgWidth; i++) {
 					for (int j = 0; j < originalImgHeight; j++) {
 						if(recoveredImage[j][i] == 1)
 							black++;
 						else
 							if(recoveredImage[j][i] == 0)
 								white++;
 							else {	
 								red++;
 							}
 					}
 				}
				
				
				
				System.out.println("BLACK - PIXELS: " + String.valueOf(black));
				System.out.println("WHITE - PIXELS: " + String.valueOf(white));
				System.out.println("RED --- PIXELS: " + String.valueOf(red));
				
				*/
				
				
			}
		});
		btnMetrics.setBounds(111, 175, 106, 23);
		panel.add(btnMetrics);
		
		txCF = new JTextField();
		txCF.setText("0");
		txCF.setEditable(false);
		txCF.setColumns(10);
		txCF.setBounds(87, 38, 60, 20);
		panel.add(txCF);
		
		JLabel lblRecoveredImage = new JLabel("Extracted");
		lblRecoveredImage.setBounds(87, 23, 60, 14);
		panel.add(lblRecoveredImage);
		
		JLabel lblEnhanced = new JLabel("Enhanced");
		lblEnhanced.setBounds(153, 23, 60, 14);
		panel.add(lblEnhanced);
		
		txCF2 = new JTextField();
		txCF2.setText("0");
		txCF2.setEditable(false);
		txCF2.setColumns(10);
		txCF2.setBounds(152, 38, 59, 20);
		panel.add(txCF2);
		
		JLabel lblCf = new JLabel("CF:");
		lblCf.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCf.setBounds(10, 42, 75, 14);
		panel.add(lblCf);
		
		JLabel lblPsnr = new JLabel("MSE:");
		lblPsnr.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPsnr.setBounds(10, 85, 75, 14);
		panel.add(lblPsnr);
		
		txMSE1 = new JTextField();
		txMSE1.setText("0");
		txMSE1.setEditable(false);
		txMSE1.setColumns(10);
		txMSE1.setBounds(87, 82, 60, 20);
		panel.add(txMSE1);
		
		txMSE2 = new JTextField();
		txMSE2.setText("0");
		txMSE2.setEditable(false);
		txMSE2.setColumns(10);
		txMSE2.setBounds(152, 82, 59, 20);
		panel.add(txMSE2);
		
		JLabel label = new JLabel("PSNR:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(10, 107, 75, 14);
		panel.add(label);
		
		txPSNR1 = new JTextField();
		txPSNR1.setText("0");
		txPSNR1.setEditable(false);
		txPSNR1.setColumns(10);
		txPSNR1.setBounds(87, 104, 60, 20);
		panel.add(txPSNR1);
		
		txPSNR2 = new JTextField();
		txPSNR2.setText("0");
		txPSNR2.setEditable(false);
		txPSNR2.setColumns(10);
		txPSNR2.setBounds(152, 104, 59, 20);
		panel.add(txPSNR2);
		
		JLabel lblSsim = new JLabel("SSIM:");
		lblSsim.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSsim.setBounds(10, 153, 75, 14);
		panel.add(lblSsim);
		
		txSSIM_Ext = new JTextField();
		txSSIM_Ext.setText("0");
		txSSIM_Ext.setEditable(false);
		txSSIM_Ext.setColumns(10);
		txSSIM_Ext.setBounds(87, 150, 60, 20);
		panel.add(txSSIM_Ext);
		
		txSSIM_Enh = new JTextField();
		txSSIM_Enh.setText("0");
		txSSIM_Enh.setEditable(false);
		txSSIM_Enh.setColumns(10);
		txSSIM_Enh.setBounds(153, 150, 59, 20);
		panel.add(txSSIM_Enh);
		
		JButton btnSsim = new JButton("SSIM");
		btnSsim.setBounds(20, 175, 86, 23);
		panel.add(btnSsim);
		btnSsim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					File fileEmb = new File("C://WORKSPACE//2019_RelTextWM//img//ext.bmp");
					mySSIMCalc = new SsimCalculator(fileEmb);
					
					File fileExt = new File("C://WORKSPACE//2019_RelTextWM//img//emb.bmp");
					File fileExt2 = new File("C://WORKSPACE//2019_RelTextWM//img//org.bmp");
					
					File fileEmbb  = new File("C://WORKSPACE//2019_RelTextWM//img//emb.bmp");
					mySSIMCalc2 = new SsimCalculator(fileEmbb);
					
					Double tempSSIMEmb_Ext = new Double(0);
					tempSSIMEmb_Ext = mySSIMCalc.compareTo(fileExt);
					
					Double tempSSIMOrig_Ext = new Double(0);
					tempSSIMOrig_Ext = mySSIMCalc.compareTo(fileExt2);
					
					Double tempSSIMOrig_Emb = new Double(0);
					tempSSIMOrig_Emb = mySSIMCalc2.compareTo(fileExt2);
				 	
					DecimalFormat df = new DecimalFormat("##.##");
					df.setRoundingMode(RoundingMode.DOWN);
					
					txSSIMEmb_Ext.setText(String.valueOf(df.format(tempSSIMEmb_Ext)));
					
					txSSIMOrig_Ext.setText(String.valueOf(df.format(tempSSIMOrig_Ext)));
					
					txSSIMOrig_Emb.setText(String.valueOf(df.format(tempSSIMOrig_Emb)));
					
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		});
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnExit.setBounds(410, 221, 125, 23);
		getContentPane().add(btnExit);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Util.parseString("1011001001111",5);
				String command = "python C:/video/sumas.py";
				try {
					Process p = Runtime.getRuntime().exec(command);
					//Process p = Runtime.getRuntime().exec(command + param );
					BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String ret = in.readLine();
				    System.out.println("value is : "+ret);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				
				
				/*Process process = null;
			       try{
			             process = Runtime.getRuntime().exec("python C:/video/sumas.py");
			       }catch(Exception e) {
			          System.out.println("Exception Raised" + e.toString());
			       }
			       InputStream stdout = process.getInputStream();
			       BufferedReader reader = new BufferedReader(new InputStreamReader(stdout,StandardCharsets.UTF_8));
			       String line;
			       try{
			          while((line = reader.readLine()) != null){
			               System.out.println("stdout: "+ line);
			          }
			       }catch(IOException e){
			             System.out.println("Exception in reading output"+ e.toString());
			       }*/
				
			}
		});
		btnNewButton.setBounds(304, 221, 89, 23);
		getContentPane().add(btnNewButton);
		
		JLabel lblEmbVsExt = new JLabel("Emb vs. Ext");
		lblEmbVsExt.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEmbVsExt.setBounds(20, 302, 75, 14);
		getContentPane().add(lblEmbVsExt);
		
		txEmb_Ext = new JTextField();
		txEmb_Ext.setText("0");
		txEmb_Ext.setEditable(false);
		txEmb_Ext.setColumns(10);
		txEmb_Ext.setBounds(97, 298, 60, 20);
		getContentPane().add(txEmb_Ext);
		
		JLabel lblOrigVsExt = new JLabel("Orig. vs. Ext");
		lblOrigVsExt.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOrigVsExt.setBounds(20, 331, 75, 14);
		getContentPane().add(lblOrigVsExt);
		
		txOrig_Ext = new JTextField();
		txOrig_Ext.setText("0");
		txOrig_Ext.setEditable(false);
		txOrig_Ext.setColumns(10);
		txOrig_Ext.setBounds(97, 327, 60, 20);
		getContentPane().add(txOrig_Ext);
		
		txSSIMOrig_Ext = new JTextField();
		txSSIMOrig_Ext.setText("0");
		txSSIMOrig_Ext.setEditable(false);
		txSSIMOrig_Ext.setColumns(10);
		txSSIMOrig_Ext.setBounds(299, 325, 60, 20);
		getContentPane().add(txSSIMOrig_Ext);
		
		JLabel label_1 = new JLabel("Orig. vs. Ext");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1.setBounds(222, 329, 75, 14);
		getContentPane().add(label_1);
		
		JLabel label_2 = new JLabel("Emb vs. Ext");
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		label_2.setBounds(222, 300, 75, 14);
		getContentPane().add(label_2);
		
		txSSIMEmb_Ext = new JTextField();
		txSSIMEmb_Ext.setText("0");
		txSSIMEmb_Ext.setEditable(false);
		txSSIMEmb_Ext.setColumns(10);
		txSSIMEmb_Ext.setBounds(299, 296, 60, 20);
		getContentPane().add(txSSIMEmb_Ext);
		
		JLabel lblCf_1 = new JLabel("CF");
		lblCf_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCf_1.setBounds(78, 263, 75, 14);
		getContentPane().add(lblCf_1);
		
		JLabel lblSsim_1 = new JLabel("SSIM");
		lblSsim_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSsim_1.setBounds(284, 263, 75, 14);
		getContentPane().add(lblSsim_1);
		
		JButton button = new JButton("Python");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					Process p = Runtime.getRuntime().exec(new String[]{"python", "C:/video/sumas.py", "43", "1"});
					BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String mesage = in.readLine();
					System.out.println(mesage);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		button.setBounds(446, 247, 89, 23);
		getContentPane().add(button);
		
		JButton btnNewButton_1 = new JButton("DELETION");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(frmAttDelete == null){
						frmAttDelete = new FrmAttSubsetDelete(dbConnection);
						frmAttDelete.setLocationRelativeTo(null);
					}
					frmAttDelete.setVisible(true);
				} catch (Exception eX) {
					eX.printStackTrace();
				}
			}
		});
		btnNewButton_1.setBounds(410, 293, 125, 23);
		getContentPane().add(btnNewButton_1);
		
		JButton btnInsertAtt = new JButton("INSERTION");
		btnInsertAtt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(frmAttInsert == null){
						frmAttInsert = new FrmAttSupersetInsert(dbConnection);
						frmAttInsert.setLocationRelativeTo(null);
					}
					frmAttInsert.setVisible(true);
				} catch (Exception eX) {
					eX.printStackTrace();
				}
			}
		});
		btnInsertAtt.setBounds(410, 326, 125, 23);
		getContentPane().add(btnInsertAtt);
		
		JButton btnUpdate = new JButton("UPDATE");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(frmAttUpdate == null){
						frmAttUpdate = new FrmAttackUpdate(dbConnection);
						frmAttUpdate.setLocationRelativeTo(null);
					}
					frmAttUpdate.setVisible(true);
				} catch (Exception eX) {
					eX.printStackTrace();
				}
			}
		});
		btnUpdate.setBounds(410, 360, 125, 23);
		getContentPane().add(btnUpdate);
		
		JLabel lblOrigVsEmb = new JLabel("Orig. vs. Emb");
		lblOrigVsEmb.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOrigVsEmb.setBounds(20, 359, 75, 14);
		getContentPane().add(lblOrigVsEmb);
		
		txOrig_Emb = new JTextField();
		txOrig_Emb.setText("0");
		txOrig_Emb.setEditable(false);
		txOrig_Emb.setColumns(10);
		txOrig_Emb.setBounds(97, 355, 60, 20);
		getContentPane().add(txOrig_Emb);
		
		txSSIMOrig_Emb = new JTextField();
		txSSIMOrig_Emb.setText("0");
		txSSIMOrig_Emb.setEditable(false);
		txSSIMOrig_Emb.setColumns(10);
		txSSIMOrig_Emb.setBounds(299, 353, 60, 20);
		getContentPane().add(txSSIMOrig_Emb);
		
		JLabel label_1_1 = new JLabel("Orig. vs. Emb");
		label_1_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1_1.setBounds(222, 357, 75, 14);
		getContentPane().add(label_1_1);
	}
	
	private JButton getBtnEmbedWM(){
		if (this.btnEmbedWM == null){
			this.btnEmbedWM = new JButton("Embed WM");
			btnEmbedWM.setBounds(10, 26, 127, 23);
			this.btnEmbedWM.setEnabled(false);
			this.btnEmbedWM.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(frmEmbedWM == null){
						frmEmbedWM = new FrmEmbedWM(dbConnection);
						frmEmbedWM.setLocationRelativeTo(null);
					}
					frmEmbedWM.setVisible(true);
				}
			});
		}
		return this.btnEmbedWM;
	}
	
	private JButton getBtnExtractWM(){
		if (this.btnExtractWM == null){
			this.btnExtractWM = new JButton("Extract WM");
			btnExtractWM.setBounds(147, 26, 127, 23);
			this.btnExtractWM.setEnabled(false);
			this.btnExtractWM.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(frmExtractWM == null){
						frmExtractWM = new FrmExtractWM(dbConnection, frmEmbedWM.getSecretKey(), frmEmbedWM.getTupleFract());
						frmExtractWM.setLocationRelativeTo(null);
					}
					frmExtractWM.setVisible(true);
				}
			});
		}
		return this.btnExtractWM;
	}
	
	public void setOriginalData(int pData[][],int pHeight, int pWidth){
		this.originalImage = new int[pHeight][pWidth];
		for (int i = 0; i < pWidth; i++) {
			for (int j = 0; j < pHeight; j++) {
				originalImage[j][i] = pData[j][i];
			}
		}
	}
	
	public void setRecoveredData(int pData[][],int pHeight, int pWidth){
		this.recoveredImage = new int[pHeight][pWidth];
		for (int i = 0; i < pWidth; i++) {
			for (int j = 0; j < pHeight; j++) {
				recoveredImage[j][i] = pData[j][i];
			}
		}
	}
}
