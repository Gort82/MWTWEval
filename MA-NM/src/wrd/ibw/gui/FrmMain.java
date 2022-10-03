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
import java.awt.event.ActionEvent;

import wrd.ibw.da.DBConnection;
import wrd.ibw.gui.att.FrmAttSubsetDelete;
import wrd.ibw.gui.att.FrmAttSupersetInsert;
import wrd.ibw.gui.att.FrmAttackUpdate;
import wrd.ibw.utils.jssim.SsimCalculator;

import javax.swing.SwingConstants;

public class FrmMain extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	private JLabel lblStatus = null;
	private JTextField tfServer;
	private JTextField tfSID;
	private JTextField tfUser;
	private JPasswordField tfPassword;
	private DBConnection dbConnection = null;
	
	private JButton btnEmbedWM = null;
	private JButton btnExtractWM = null;
	private JButton btnMetrics;
	private JButton btnFixedUpdate = null;
	private JButton btnSubsetUpdate;
	private JButton btnRandomUpdate = null;
	private JButton btnDeleteAttack = null;
	private JButton btnAddingAttack = null;
	
	private static FrmEmbedWM frmEmbedWM = null;
	private static FrmExtractWM frmExtractWM = null;
	private static FrmAttackUpdate frmAttackUpdate = null;
	
	private static FrmAttSubsetDelete frmAttDelete = null;
	private static FrmAttSupersetInsert frmAttInsert = null;
	
	private int originalImage[][];
	private int recoveredImage[][];
	private int enhancedImage[][];
	private int originalImgHeight;
	private int originalImgWidth;
	
	private SsimCalculator mySSIMCalc = null;
	private JTextField txCF;
	private JTextField txCF2;
	private JTextField txSSIM_Ext;
	private JTextField txSSIM_Enh;
	
	
	public FrmMain() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setTitle("Numerical covertype watermarking...");
		this.setSize(556,363);
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
							getBtnExtractWM().setEnabled(true);
							btnSubsetUpdate.setEnabled(true);
							btnDeleteAttack.setEnabled(true);
							btnAddingAttack.setEnabled(true);
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
							btnSubsetUpdate.setEnabled(false);
							btnDeleteAttack.setEnabled(false);
							btnAddingAttack.setEnabled(false);
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
					lblStatus.setForeground(Color.RED);
					lblStatus.setText("Disconnected...");
					getBtnEmbedWM().setEnabled(false);
					getBtnExtractWM().setEnabled(false);
					btnConnect.setText("Connect");
					
					btnFixedUpdate.setEnabled(false);
					btnRandomUpdate.setEnabled(false);
					btnDeleteAttack.setEnabled(false);
					btnAddingAttack.setEnabled(false);
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
		pnWatermark.setBounds(10, 216, 284, 63);
		getContentPane().add(pnWatermark);
		pnWatermark.add(getBtnExtractWM());
		pnWatermark.add(getBtnEmbedWM());
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createTitledBorder(null, "  Evaluation Metrics  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		panel.setBounds(304, 149, 231, 130);
		getContentPane().add(panel);
		
		btnMetrics = new JButton("Calculate");
		btnMetrics.setEnabled(false);
		btnMetrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				originalImgWidth = frmEmbedWM.getEmbedImgWidth();
				originalImgHeight = frmEmbedWM.gettEmbedImgHeigh();
				
				originalImage = new int[originalImgHeight][originalImgWidth]; 
				originalImage = frmEmbedWM.getImgToEmbed();
				
				if((originalImgWidth == frmExtractWM.getExtImgWidth())&&(originalImgHeight == frmExtractWM.getExtImgHeigh())){
					recoveredImage = new int[frmExtractWM.getExtImgHeigh()][frmExtractWM.getExtImgWidth()]; 
					recoveredImage = frmExtractWM.getExtractedImg();
					
					enhancedImage = new int[frmExtractWM.getExtImgHeigh()][frmExtractWM.getExtImgWidth()];
					enhancedImage  = frmExtractWM.getEnhancedImg();
					
					float cumul = 0;		float mse1 = 0;
					float cumul2 = 0;		float mse2 = 0;
					float cf = 0;        
					float cf2 = 0;       
					try {
						for (int i = 0; i < originalImgWidth; i++) {
		 					for (int j = 0; j < originalImgHeight; j++) {
		 						if(recoveredImage[j][i] != -1){
		 							cumul = cumul + (originalImage[j][i] ^ (-1*(recoveredImage[j][i]-1)));
		 							mse1 = mse1 + (float)Math.pow((originalImage[j][i] - recoveredImage[j][i]),2);
		 						}
		 						if(enhancedImage[j][i] != -1){
		 							cumul2 = cumul2 + (originalImage[j][i] ^ (-1*(enhancedImage[j][i]-1)));
		 							mse2 = mse2 + (float)Math.pow((originalImage[j][i] - enhancedImage[j][i]),2);
		 						}
		 					}
		 				}
						
						cf = 100*cumul/(originalImgWidth*originalImgHeight);
						cf2 = 100*cumul2/(originalImgWidth*originalImgHeight);
						
						DecimalFormat df = new DecimalFormat("##.##");
						df.setRoundingMode(RoundingMode.DOWN);
						
						
						System.out.println("Correction Factor: " + cf);
						
						txCF.setText(String.valueOf(df.format(cf)));
						txCF2.setText(String.valueOf(df.format(cf2)));	
						
						DecimalFormat df1 = new DecimalFormat("##.######");
						df1.setRoundingMode(RoundingMode.DOWN);
						
						DecimalFormat df2 = new DecimalFormat("###.####");
						df2.setRoundingMode(RoundingMode.DOWN);
						
						getSSIM();
				
					} 
					catch (Exception e1) {
						e1.printStackTrace();
					}
					
				}else{
					JOptionPane.showMessageDialog(null, "The dimension of the extracted image is different to the embedded image...");
				}
			}
		});
		btnMetrics.setBounds(107, 95, 106, 23);
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
		
		JLabel lblSsim = new JLabel("SSIM:");
		lblSsim.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSsim.setBounds(10, 67, 75, 14);
		panel.add(lblSsim);
		
		txSSIM_Ext = new JTextField();
		txSSIM_Ext.setText("0");
		txSSIM_Ext.setEditable(false);
		txSSIM_Ext.setColumns(10);
		txSSIM_Ext.setBounds(87, 64, 60, 20);
		panel.add(txSSIM_Ext);
		
		txSSIM_Enh = new JTextField();
		txSSIM_Enh.setText("0");
		txSSIM_Enh.setEditable(false);
		txSSIM_Enh.setColumns(10);
		txSSIM_Enh.setBounds(153, 64, 59, 20);
		panel.add(txSSIM_Enh);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		btnExit.setBounds(410, 296, 125, 23);
		getContentPane().add(btnExit);
		
		JPanel pnTuplAttack = new JPanel();
		pnTuplAttack.setBounds(304, 11, 186, 130);
		getContentPane().add(pnTuplAttack);
		pnTuplAttack.setLayout(null);
		pnTuplAttack.setBorder(BorderFactory.createTitledBorder(null, "  Tuple Attacks  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12)));
		
		btnDeleteAttack = new JButton("Subset Delete");
		btnDeleteAttack.setBounds(10, 79, 165, 23);
		pnTuplAttack.add(btnDeleteAttack);
		btnDeleteAttack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(frmAttDelete == null){
						frmAttDelete = new FrmAttSubsetDelete(dbConnection);
						frmAttDelete.setLocationRelativeTo(null);
					}
					frmAttDelete.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnDeleteAttack.setForeground(Color.RED);
		btnDeleteAttack.setEnabled(false);
		
		btnAddingAttack = new JButton("Subset Addition");
		btnAddingAttack.setBounds(10, 52, 165, 23);
		pnTuplAttack.add(btnAddingAttack);
		btnAddingAttack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(frmAttInsert == null){
						frmAttInsert = new FrmAttSupersetInsert(dbConnection);
						frmAttInsert.setLocationRelativeTo(null);
					}
					frmAttInsert.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnAddingAttack.setForeground(Color.RED);
		btnAddingAttack.setEnabled(false);
		
		btnSubsetUpdate = new JButton("Subset Update");
		btnSubsetUpdate.setBounds(10, 25, 165, 23);
		pnTuplAttack.add(btnSubsetUpdate);
		btnSubsetUpdate.setEnabled(false);
		btnSubsetUpdate.setForeground(Color.RED);
		
		btnSubsetUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(frmAttackUpdate == null){
						frmAttackUpdate = new FrmAttackUpdate(dbConnection);
						frmAttackUpdate.setLocationRelativeTo(null);
					}
					frmAttackUpdate.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
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
						frmExtractWM = new FrmExtractWM(dbConnection, frmEmbedWM.getSecretKey(), frmEmbedWM.getTupleFract(), frmEmbedWM.getAttrFract());
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
	
	private void getSSIM() {
		try {
		
			File fileEmb = new File("C://WORKSPACE//MA-NM/img//incrusted.bmp");
			mySSIMCalc = new SsimCalculator(fileEmb);
			
			File fileExt = new File("C://WORKSPACE//MA-NM//img//extracted.bmp");
			
			File fileExt2 = new File("C://WORKSPACE//MA-NM//img//enhanced.bmp");
			
			Double tempSSIM_A = Double.valueOf(0);
			tempSSIM_A = mySSIMCalc.compareTo(fileExt);
			
			Double tempSSIM_B = Double.valueOf(0);
			tempSSIM_B = mySSIMCalc.compareTo(fileExt2);
		 	
			DecimalFormat df = new DecimalFormat("##.##");
			df.setRoundingMode(RoundingMode.DOWN);
			
			txSSIM_Ext.setText(String.valueOf(df.format(tempSSIM_A)));
			txSSIM_Enh.setText(String.valueOf(df.format(tempSSIM_B)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
