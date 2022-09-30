package wrd.ibw.gui;

import java.awt.ComponentOrientation;
import java.awt.Font;

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
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Vector;
import java.awt.event.ActionEvent;

import wrd.ibw.da.DBConnection;
import wrd.ibw.gui.att.FrmAttSubsetDelete;
import wrd.ibw.gui.att.FrmAttSupersetInsert;
import wrd.ibw.gui.att.FrmAttackUpdate;
import wrd.ibw.utils.jssim.SsimCalculator;

import javax.swing.SwingConstants;

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
	
	private JTextField txEmb_Ext;
	private JTextField txOrig_Ext;
	private JTextField txSSIMOrig_Ext;
	private JTextField txSSIMEmb_Ext;
	private JTextField txOrig_Emb;
	private JTextField txSSIMOrig_Emb;
	
	private JButton btnInsertAtt;
	private JButton btnUpdate;
	private JButton btnDelete;
	
	
	
	public FrmMain() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setTitle("Relational Textual Database Watermarking...");
		this.setSize(646,322);
		this.getContentPane().setLayout(null);
		
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
		tfSID.setText("orcl");
		tfSID.setColumns(10);
		tfSID.setBounds(77, 54, 197, 20);
		pnConnection.add(tfSID);
		
		tfUser = new JTextField();
		tfUser.setText("system");
		tfUser.setColumns(10);
		tfUser.setBounds(77, 79, 197, 20);
		pnConnection.add(tfUser);
		
		tfPassword = new JPasswordField();
		tfPassword.setText("koko");
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
							//getBtnExtractWM().setEnabled(true);
							//btnMetrics.setEnabled(true);
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
		panel.setBounds(304, 11, 320, 177);
		getContentPane().add(panel);
		
		btnMetrics = new JButton("Calculate");
		btnMetrics.setEnabled(false);
		btnMetrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				computeSSIM();
				
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
					double cf = 0;        
					double cf3 = 0;
					double cf4 = 0;
					try {
						for (int i = 0; i < originalImgWidth; i++) {
		 					for (int j = 0; j < originalImgHeight; j++) {
		 						if(recoveredImage[j][i] != -1){
		 							cumul = cumul + (originalImage[j][i] ^ (-1*(recoveredImage[j][i]-1)));
		 							mse1 = mse1 + (float)Math.pow((originalImage[j][i] - recoveredImage[j][i]),2);
		 						}
		 						
		 						if(embeddedImage[j][i] != -1){
		 							cumul3 = cumul3 + (embeddedImage[j][i] ^ (-1*(recoveredImage[j][i]-1)));
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
						cf3 = 100*cumul3/embLinear.size();
						
						DecimalFormat df = new DecimalFormat("##.##");
						df.setRoundingMode(RoundingMode.DOWN);
						
						
						System.out.println("Correction Factor: " + cf);
						
						txEmb_Ext.setText(String.valueOf(df.format(cf3)));
						txOrig_Ext.setText(String.valueOf(df.format(cf)));
						
						txOrig_Emb.setText(String.valueOf(df.format(cf4)));
						
						System.out.println("Embebida contra extraida: " + cf3);
						System.out.println("Original contra extraida: " + cf);
						
						DecimalFormat df1 = new DecimalFormat("##.######");
						df1.setRoundingMode(RoundingMode.DOWN);
						
						DecimalFormat df2 = new DecimalFormat("###.####");
						df2.setRoundingMode(RoundingMode.DOWN);
						
				
					} 
					catch (Exception e1) {
						e1.printStackTrace();
					}
					
				}else{
					JOptionPane.showMessageDialog(null, "The dimension of the extracted image is different to the embedded image...");
				}
			}
		});
		btnMetrics.setBounds(199, 139, 106, 23);
		panel.add(btnMetrics);
		
		txSSIMOrig_Emb = new JTextField();
		txSSIMOrig_Emb.setBounds(245, 108, 60, 20);
		panel.add(txSSIMOrig_Emb);
		txSSIMOrig_Emb.setText("0");
		txSSIMOrig_Emb.setEditable(false);
		txSSIMOrig_Emb.setColumns(10);
		
		JLabel label_1_1 = new JLabel("Orig. vs. Emb");
		label_1_1.setBounds(168, 112, 75, 14);
		panel.add(label_1_1);
		label_1_1.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel label_1 = new JLabel("Orig. vs. Ext");
		label_1.setBounds(168, 84, 75, 14);
		panel.add(label_1);
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txSSIMOrig_Ext = new JTextField();
		txSSIMOrig_Ext.setBounds(245, 80, 60, 20);
		panel.add(txSSIMOrig_Ext);
		txSSIMOrig_Ext.setText("0");
		txSSIMOrig_Ext.setEditable(false);
		txSSIMOrig_Ext.setColumns(10);
		
		txSSIMEmb_Ext = new JTextField();
		txSSIMEmb_Ext.setBounds(245, 51, 60, 20);
		panel.add(txSSIMEmb_Ext);
		txSSIMEmb_Ext.setText("0");
		txSSIMEmb_Ext.setEditable(false);
		txSSIMEmb_Ext.setColumns(10);
		
		JLabel label_2 = new JLabel("Emb vs. Ext");
		label_2.setBounds(168, 55, 75, 14);
		panel.add(label_2);
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblSsim_1 = new JLabel("SSIM");
		lblSsim_1.setBounds(245, 32, 60, 14);
		panel.add(lblSsim_1);
		lblSsim_1.setHorizontalAlignment(SwingConstants.CENTER);
		
		txOrig_Emb = new JTextField();
		txOrig_Emb.setBounds(87, 110, 60, 20);
		panel.add(txOrig_Emb);
		txOrig_Emb.setText("0");
		txOrig_Emb.setEditable(false);
		txOrig_Emb.setColumns(10);
		
		JLabel lblOrigVsEmb = new JLabel("Orig. vs. Emb");
		lblOrigVsEmb.setBounds(10, 114, 75, 14);
		panel.add(lblOrigVsEmb);
		lblOrigVsEmb.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txOrig_Ext = new JTextField();
		txOrig_Ext.setBounds(87, 82, 60, 20);
		panel.add(txOrig_Ext);
		txOrig_Ext.setText("0");
		txOrig_Ext.setEditable(false);
		txOrig_Ext.setColumns(10);
		
		JLabel lblOrigVsExt = new JLabel("Orig. vs. Ext");
		lblOrigVsExt.setBounds(10, 86, 75, 14);
		panel.add(lblOrigVsExt);
		lblOrigVsExt.setHorizontalAlignment(SwingConstants.RIGHT);
		
		txEmb_Ext = new JTextField();
		txEmb_Ext.setBounds(87, 53, 60, 20);
		panel.add(txEmb_Ext);
		txEmb_Ext.setText("0");
		txEmb_Ext.setEditable(false);
		txEmb_Ext.setColumns(10);
		
		JLabel lblEmbVsExt = new JLabel("Emb vs. Ext");
		lblEmbVsExt.setBounds(10, 57, 75, 14);
		panel.add(lblEmbVsExt);
		lblEmbVsExt.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblCf_1 = new JLabel("CF");
		lblCf_1.setBounds(87, 32, 60, 14);
		panel.add(lblCf_1);
		lblCf_1.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnExit.setBounds(498, 256, 125, 23);
		getContentPane().add(btnExit);
		
		JPanel pnWatermark_1 = new JPanel();
		pnWatermark_1.setLayout(null);
		pnWatermark_1.setBorder(BorderFactory.createTitledBorder(null, "  Queries  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnWatermark_1.setBounds(304, 189, 320, 63);
		getContentPane().add(pnWatermark_1);
		
		btnInsertAtt = new JButton("INSERT");
		btnInsertAtt.setEnabled(false);
		btnInsertAtt.setBounds(10, 25, 87, 23);
		pnWatermark_1.add(btnInsertAtt);
		
		btnUpdate = new JButton("UPDATE");
		btnUpdate.setEnabled(false);
		btnUpdate.setBounds(115, 25, 87, 23);
		pnWatermark_1.add(btnUpdate);
		
		btnDelete = new JButton("DELETE");
		btnDelete.setEnabled(false);
		btnDelete.setBounds(223, 25, 87, 23);
		pnWatermark_1.add(btnDelete);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(frmAttDelete == null){
						frmAttDelete = new FrmAttSubsetDelete(dbConnection, frmEmbedWM.getRelIndex());
						frmAttDelete.setLocationRelativeTo(null);
					} else
						frmAttDelete.setTablndex(frmEmbedWM.getRelIndex());
					frmAttDelete.setVisible(true);
				} catch (Exception eX) {
					eX.printStackTrace();
				}
			}
		});
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(frmAttUpdate == null){
						frmAttUpdate = new FrmAttackUpdate(dbConnection,  frmEmbedWM.getRelIndex());
						frmAttUpdate.setLocationRelativeTo(null);
					} else
						frmAttUpdate.setTablndex(frmEmbedWM.getRelIndex());
					frmAttUpdate.setVisible(true);
				} catch (Exception eX) {
					eX.printStackTrace();
				}
			}
		});
		btnInsertAtt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if(frmAttInsert == null){
						frmAttInsert = new FrmAttSupersetInsert(dbConnection, frmEmbedWM.getRelIndex());
						frmAttInsert.setLocationRelativeTo(null);
					} else
						frmAttInsert.setTablndex(frmEmbedWM.getRelIndex());
					frmAttInsert.setVisible(true);
				} catch (Exception eX) {
					eX.printStackTrace();
				}
			}
		});
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
					
					btnExtractWM.setEnabled(true);
					btnInsertAtt.setEnabled(true);
					btnUpdate.setEnabled(true);
					btnDelete.setEnabled(true);
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
					
					btnMetrics.setEnabled(true);
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
	
	private void computeSSIM() {
		try {
			File fileEmb = new File("C://WORKSPACE//SD-MW//img//ext.bmp");
			mySSIMCalc = new SsimCalculator(fileEmb);
			
			File fileExt = new File("C://WORKSPACE//SD-MW//img//emb.bmp");
			File fileExt2 = new File("C://WORKSPACE//SD-MW//img//org.bmp");
			
			File fileEmbb  = new File("C://WORKSPACE//SD-MW//img//emb.bmp");
			mySSIMCalc2 = new SsimCalculator(fileEmbb);
			
			
			Double tempSSIMEmb_Ext = Double.valueOf(0);
			tempSSIMEmb_Ext = mySSIMCalc.compareTo(fileExt);
			
			Double tempSSIMOrig_Ext = Double.valueOf(0);
			tempSSIMOrig_Ext = mySSIMCalc.compareTo(fileExt2);
			
			Double tempSSIMOrig_Emb = Double.valueOf(0);
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
	
}
