package wrd.ibw.gui;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.Color;

import javax.swing.JButton;

import wrd.ibw.da.DBConnection;
import wrd.ibw.utils.Util;
import wrd.ibw.utils.WordCarrier;
import wrd.ibw.wn.WNCaller;

import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import oracle.jdbc.internal.OracleTypes;
import javax.swing.border.LineBorder;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class FrmExtractWM extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	
	private DBConnection dbConnection = null;
	
	private JComboBox<String> cbTable = null;
	private JTextField tfFractTupl;
	private JTextField tfPrivateKey;
	private JButton btnEnhance;
	private JButton btnSave;
	private JCheckBox cbSymmetricImage;
	
	private WNCaller tempWNCaller = null;
	
	private TableColumn tmMSB = null; 
	private JComboBox cbMSB = null;
	
	private JRadioButton rbSubL = null;
	private JRadioButton rbSubC = null;
	private JRadioButton rbSubE = null;
	private JRadioButton rbVerbE = null;
	private JRadioButton rbVerbC = null;
	private JRadioButton rbVerbL = null;
	private JRadioButton rbAdjE = null;
	private JRadioButton rbAdjC = null;
	private JRadioButton rbAdjL = null;
	private JRadioButton rbAdvE = null;
	private JRadioButton rbAdvC = null;
	private JRadioButton rbAdvL = null;
	private JRadioButton rbOME = null;
	private JRadioButton rbOML = null;
	private JRadioButton rbOMC = null;
	
	private JRadioButton rbMetrWUP = null;
	private JRadioButton rbMetrJCN = null;
	private JRadioButton rbMetrLCH = null;
	private JRadioButton rbMetrLIN = null;
	private JRadioButton rbMetrHSO = null;
	private JRadioButton rbMetrLESK = null;
	private JRadioButton rbMetrPATH = null;
	private JRadioButton rbMetrRES = null;
	
	private DefaultTableModel carriersModel = null;
	private DefaultTableModel locksNumModel = null;
	private DefaultTableModel locksSWModel = null;
	private DefaultTableModel locksMWModel = null;
	
	private JCheckBox rbWSDLesk = null;
	private JLabel lbEnhancedImage ;
	
	private int imageWidth = 0;
	private int imageHeight = 0;
	
	private BufferedImage img = null;
	private BufferedImage img2 = null;
	
	private JTextField tfHeight;
	private JTextField tfWidth;
	
	private int recoveredInfo[][];
	private Vector<Integer> mayorityInfo[][];
	
	
	
	private int respRecoveredInfo[][];
	private int filteredImage [][];
	
	public int[][] getImageMatrix(){
		return recoveredInfo;
	}
	
	private JTextField txTotalPx;
	private JTextField txExtractedPx;
	private JTextField txEPP;
	private JTextField txTotalTupl;
	private JTextField txTotalTuples;
	private JTextField txMTP;
	private JTextField txRecPx;
	private JTextField txMissPx;
	private JTextField txEnPerc;
	private JTextField txRecPerc;
	
	private JButton btnAssign = null;
	private JButton btnErode = null;
	private JButton btnDilate = null;
	
	private JRadioButton rbOrigMeth;
	private JRadioButton rbExtMeth;
	
	private JCheckBox cbZDist;
	/**
	 * @wbp.nonvisual location=642,99
	 */
	private final ButtonGroup bgMethod = new ButtonGroup();
	/**
	 * @wbp.nonvisual location=552,209
	 */
	private final ButtonGroup bgExtOpt = new ButtonGroup();
	private JTextField txMSBESCheme;
	private JTextField txBinaryLength;
	private JTextField txMaxDist;
	private JTextField txSentLength;
	private JTable tbCarriers = null;
	private JTextField txLSB;
	private JTextField txMSB;
	private JTable tbLocksNum;
	private JTextField txMWCat;
	private JTable tbLocksSW;
	private JTextField txMWB;
	private JTable tbLocksMW;
	private JTextField txMSPar;
	private JTextField txMWSent;
	private JTextField txMinCarrSize;
	

	public FrmExtractWM(DBConnection pDBConnection, String pSecretKey, String pTuplFract) {
		this.dbConnection = pDBConnection;
		try {
			tempWNCaller = new WNCaller();
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setTitle("[Sardroudi & Ibrahim, 2010] Extract Watermark...");
		this.setSize(1033,757);
		this.getContentPane().setLayout(null);
		
		this.cbMSB = new JComboBox<>();
		
		JLabel lblRelationToMark = new JLabel("Relation Marked:");
		lblRelationToMark.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRelationToMark.setBounds(10, 11, 110, 14);
		getContentPane().add(lblRelationToMark);
		getContentPane().add(getJCBTable());
		
		JLabel lblFractionOrRelations = new JLabel("TF");
		lblFractionOrRelations.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFractionOrRelations.setBounds(10, 67, 32, 14);
		getContentPane().add(lblFractionOrRelations);
		
		tfFractTupl = new JTextField();
		tfFractTupl.setText(pTuplFract);
		tfFractTupl.setBounds(45, 64, 47, 20);
		getContentPane().add(tfFractTupl);
		tfFractTupl.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createTitledBorder(null, "  Carriers (Multi-Words)  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		panel.setBounds(114, 68, 204, 156);
		getContentPane().add(panel);
		
		tbCarriers = new JTable( ){
            private static final long serialVersionUID = 1L;
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                    	return Boolean.class;
                    case 1:
                        return String.class;
                    default:
                        return Boolean.class;
                }
            }
        };
        
        JScrollPane spCarriers = new JScrollPane();
		spCarriers.setBounds(10, 22, 183, 123);
		panel.add(spCarriers);
		
		tbCarriers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		carriersModel = new DefaultTableModel();
		carriersModel.addColumn("Get");
		carriersModel.addColumn("Name");
		
		this.tbCarriers.setModel(carriersModel);
		this.tbCarriers.getColumnModel().getColumn(0).setPreferredWidth(37);
		this.tbCarriers.getColumnModel().getColumn(1).setPreferredWidth(128);
		this.tbCarriers.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		
		spCarriers.setViewportView(tbCarriers);
		
		JLabel lblPrivateKey = new JLabel("SK");
		lblPrivateKey.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPrivateKey.setBounds(10, 39, 37, 14);
		getContentPane().add(lblPrivateKey);
		
		tfPrivateKey = new JTextField();
		tfPrivateKey.setText(pSecretKey);
		tfPrivateKey.setColumns(10);
		tfPrivateKey.setBounds(50, 36, 50, 20);
		getContentPane().add(tfPrivateKey);
		
		JPanel pnImageSelector = new JPanel();
		pnImageSelector.setLayout(null);
		pnImageSelector.setBorder(BorderFactory.createTitledBorder(null, "  Extracted Image  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnImageSelector.setBounds(330, 9, 387, 236);
		getContentPane().add(pnImageSelector);
		
		JLabel lbImageViewer = new JLabel();
		lbImageViewer.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lbImageViewer.setBounds(10, 56, 184, 169);
		pnImageSelector.add(lbImageViewer);
		
		tfHeight = new JTextField();
		tfHeight.setBounds(58, 25, 37, 20);
		pnImageSelector.add(tfHeight);
		tfHeight.setText("80");
		tfHeight.setColumns(10);
		
		tfWidth = new JTextField();
		tfWidth.setBounds(147, 25, 37, 20);
		pnImageSelector.add(tfWidth);
		tfWidth.setText("82");
		tfWidth.setColumns(10);
		
		JLabel lblWidth = new JLabel("Width:");
		lblWidth.setBounds(105, 28, 37, 14);
		pnImageSelector.add(lblWidth);
		lblWidth.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblImageHeb = new JLabel("Height:");
		lblImageHeb.setBounds(10, 28, 47, 14);
		pnImageSelector.add(lblImageHeb);
		lblImageHeb.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(211, 49, 162, 95);
		pnImageSelector.add(panel_1);
		panel_1.setLayout(null);
		panel_1.setBorder(BorderFactory.createTitledBorder(null, "  Legend ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setBackground(Color.RED);
		panel_2.setBounds(14, 25, 15, 15);
		panel_1.add(panel_2);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_3.setBackground(Color.WHITE);
		panel_3.setBounds(14, 49, 15, 15);
		panel_1.add(panel_3);
		
		JLabel label = new JLabel("Missed Pixel");
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setBounds(33, 26, 95, 14);
		panel_1.add(label);
		
		JLabel label_1 = new JLabel("Encrusted Pixel (0)");
		label_1.setHorizontalAlignment(SwingConstants.LEFT);
		label_1.setBounds(33, 49, 119, 14);
		panel_1.add(label_1);
		
		JLabel label_2 = new JLabel("Encrusted Pixel (1)");
		label_2.setHorizontalAlignment(SwingConstants.LEFT);
		label_2.setBounds(33, 67, 119, 14);
		panel_1.add(label_2);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_4.setBackground(Color.BLACK);
		panel_4.setBounds(14, 67, 15, 15);
		panel_1.add(panel_4);
		
		JLabel label_3 = new JLabel("___________________");
		label_3.setOpaque(true);
		label_3.setHorizontalAlignment(SwingConstants.LEFT);
		label_3.setBounds(14, 32, 138, 14);
		panel_1.add(label_3);
		
		txTotalPx = new JTextField();
		txTotalPx.setText("0");
		txTotalPx.setEditable(false);
		txTotalPx.setColumns(10);
		txTotalPx.setBounds(322, 150, 49, 20);
		pnImageSelector.add(txTotalPx);
		
		txExtractedPx = new JTextField();
		txExtractedPx.setText("0");
		txExtractedPx.setEditable(false);
		txExtractedPx.setColumns(10);
		txExtractedPx.setBounds(321, 174, 50, 20);
		pnImageSelector.add(txExtractedPx);
		
		JLabel lblExtracted = new JLabel("Extracted:");
		lblExtracted.setHorizontalAlignment(SwingConstants.RIGHT);
		lblExtracted.setBounds(211, 177, 107, 14);
		pnImageSelector.add(lblExtracted);
		
		JLabel lblTotalExpected = new JLabel("Total Expected:");
		lblTotalExpected.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotalExpected.setBounds(211, 153, 108, 14);
		pnImageSelector.add(lblTotalExpected);
		
		txEPP = new JTextField();
		txEPP.setText("0");
		txEPP.setEditable(false);
		txEPP.setColumns(10);
		txEPP.setBounds(321, 205, 50, 20);
		pnImageSelector.add(txEPP);
		
		JLabel label_6 = new JLabel("Percent:");
		label_6.setHorizontalAlignment(SwingConstants.RIGHT);
		label_6.setBounds(211, 207, 107, 14);
		pnImageSelector.add(label_6);
		
		
		
		
		DefaultTableModel model = new DefaultTableModel();
		ResultSet rs = this.dbConnection.getFields(this.cbTable.getSelectedItem().toString());
	    ResultSetMetaData rsmd = rs.getMetaData();
	    /*for (int col = 0; col < rsmd.getColumnCount(); col++) {
	        model.addColumn(rsmd.getColumnName(col + 1));
	    }*/
	    model.addColumn("Get");
	    model.addColumn("Name");
	    model.addColumn("Type");

		JButton btnStart = new JButton("Start");
		btnStart.setBounds(196, 399, 122, 23);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imageWidth = Integer.parseInt(tfWidth.getText());
				imageHeight = Integer.parseInt(tfHeight.getText());
				txTotalPx.setText(String.valueOf(imageWidth*imageHeight));
				
				recoveredInfo = new int[imageHeight][imageWidth];
				mayorityInfo = new Vector[imageHeight][imageWidth];
				for (int i = 0; i < imageWidth; i++) {
 					for (int j = 0; j < imageHeight; j++) {
 						recoveredInfo[j][i] = -1;
 						mayorityInfo[j][i] = new Vector<Integer>(0);
 					}
 				}
				
				Calendar cal = Calendar.getInstance();
		    	cal.getTime();
		    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		    	System.out.println("-----------------------------------------------");
		    	System.out.println("EMBEDDING PROCESS STARTED AT: " + sdf.format(cal.getTime()) );
		    	
		    	boolean considerRest = false;
				Vector<POS> pWordRoles = new Vector<POS>();
				Vector<POS> pWordCarriers = new Vector<POS>();
				Vector<WordCarrier> carrierForSentence = new Vector<WordCarrier>();
				WordCarrier currentCarrier = null;
				
				boolean contains = false;
				boolean hasUpperCase = false;
				boolean contnumb = false;
				
				boolean synUpperCase = false;
				
				String extListNounsB = null;       ////////////////////////////////////
				String extLockStreamB = null;      ////////////////////////////////////
				String extSenseKeyB = null;        ////////////////////////////////////
				String extNounB = null;            ////////////////////////////////////
				String extSynonymSetB = null;      ////////////////////////////////////
				String extMarkB = null;  ////////////////////////////////////
				String extUsedSynonymB = null;     ////////////////////////////////////
				
				
				String[] excluded = {"a", "at", "an", "as", "ar", "are", "be", "can", "coming", "come", "do", "going", "have", "in", "it", "might", "more", "now", "or", "one", "over", "out", "say", "us", "so", "today", "will"};
				
				String num = "";
				
				if (rbOML.isSelected()) {
					considerRest = true;
					if (rbSubC.isSelected()||rbSubE.isSelected()) pWordRoles.addElement(POS.NOUN);
					if (rbVerbC.isSelected()||rbVerbE.isSelected()) pWordRoles.addElement(POS.VERB);
					if (rbAdjC.isSelected()||rbAdjE.isSelected()) pWordRoles.addElement(POS.ADJECTIVE);
					if (rbAdvC.isSelected()||rbAdvE.isSelected()) pWordRoles.addElement(POS.ADVERB);
				}else{
					considerRest = false;
					if (rbSubL.isSelected()) pWordRoles.addElement(POS.NOUN);
					if (rbVerbL.isSelected()) pWordRoles.addElement(POS.VERB);
					if (rbAdjL.isSelected()) pWordRoles.addElement(POS.ADJECTIVE);
					if (rbAdvL.isSelected()) pWordRoles.addElement(POS.ADVERB);
				}
				
				if (rbSubC.isSelected()) pWordCarriers.addElement(POS.NOUN);
				if (rbVerbC.isSelected()) pWordCarriers.addElement(POS.VERB);
				if (rbAdjC.isSelected()) pWordCarriers.addElement(POS.ADJECTIVE);
				if (rbAdvC.isSelected()) pWordCarriers.addElement(POS.ADVERB);
				
				int simMetr = 0;
				String simCap = "";
				
				if (rbMetrWUP.isSelected()) {simMetr = 3; simCap = "WUP";}
				if (rbMetrJCN.isSelected()) {simMetr = 5; simCap = "JCN";}
				if (rbMetrLCH.isSelected()) {simMetr = 1; simCap = "LCH";}
				if (rbMetrLIN.isSelected()) {simMetr = 6; simCap = "LIN";}
				if (rbMetrRES.isSelected()) {simMetr = 4; simCap = "RES";}
				if (rbMetrPATH.isSelected()){simMetr = 7; simCap = "PATH";} 
				if (rbMetrLESK.isSelected()){simMetr = 2; simCap = "LESK";}
				if (rbMetrHSO.isSelected()) {simMetr = 0; simCap = "HSO";}
				
				String bruteParagraph = "";
				Synset synsetWord = null;
				Vector<String> substitutes = new Vector<String>();
				
				String lockStream = "";
				int lockSup = 0;
				Long sentKey = new Long(0);
				int wsdMethod = 0;
				
				int height_pos = 0;
				int width_pos = 0;
				int image_element = 0;
				boolean embedded = false;
				
				Vector<Integer> metrStat = new Vector<Integer>();
				Vector<Double> metrFlags = new Vector<Double>();
				
				if (rbWSDLesk.isSelected()) {
					wsdMethod = 1;
				}else{
					wsdMethod = 2;
				}
				
				
				Vector<String> carrierAttr = new Vector<String>();
				for (int i = 0; i < tbCarriers.getRowCount(); i++) {
					 if(tbCarriers.getModel().getValueAt(i, 0).equals(true)){
						 carrierAttr.add(tbCarriers.getModel().getValueAt(i, 1).toString());
					 }
				}
					
				CallableStatement csTuples = null;
				CallableStatement csCarriers = null;
				CallableStatement csSVK = null;
				///// 21.09.21  CallableStatement csCheckUpdater = null;
				CallableStatement csLoadCheck = null;
				
				ResultSet rsTuples = null;
				ResultSet rsAttrVK = null;
				
				int MAX_COTR = 0;
				
				int MetrCounter = 0;
				int MetrCounter2 = 0;
				int TuplCounter = 0;
				
				double valMetr = 0;
				
				try {
					csTuples = dbConnection.getConnection().prepareCall ("{ ? = call RTW_GET_GENINF (?,?,?,?)}");
					csTuples.registerOutParameter (1, OracleTypes.CURSOR);
					csTuples.setString (2,cbTable.getSelectedItem().toString());
					csTuples.setInt (3,Integer.valueOf(tfFractTupl.getText()));
					csTuples.setInt (4, imageHeight-1);
					csTuples.setInt (5, imageWidth-1);
					csTuples.execute ();
					
					rsTuples = (ResultSet)csTuples.getObject (1);
					
					csCarriers = dbConnection.getConnection().prepareCall ("{ ? = call RTW_ATTR_VALUE (?,?,?)}");
					csCarriers.registerOutParameter (1, Types.LONGNVARCHAR);
					csCarriers.setString(2, cbTable.getSelectedItem().toString());
					
					csSVK = dbConnection.getConnection().prepareCall ("{ ? = call RTW_GENERATE_AVK (?,?,?,?,?,?)}");
					csSVK.registerOutParameter (1, OracleTypes.CURSOR);
					csSVK.setString(2, tfPrivateKey.getText());
					
					//csCheckUpdater = dbConnection.getConnection().prepareCall ("{call RTW_EXT_BCHECK (?,?,?,?,?,?,?,?)}");
				///// 21.09.21  csCheckUpdater = dbConnection.getConnection().prepareCall ("{call RTW_EXT_WORD (?,?,?,?,?)}");
					
					csLoadCheck = dbConnection.getConnection().prepareCall ("{ ? = call RTW_GET_EXT_BCHECK (?,?)}");
					csLoadCheck.registerOutParameter (1, OracleTypes.VARCHAR);
					csLoadCheck.setString (2,cbTable.getSelectedItem().toString());
					
					int val = 0;
					int id_val;
					
					while (rsTuples.next ()){
						
						val++;
						id_val = rsTuples.getInt ("ID");
						
						System.out.println(rsTuples.getInt ("ID"));
						csLoadCheck.setInt(3, rsTuples.getInt ("ID"));
						
						
						try {
							if(rsTuples.getInt ("TUPL_FACTOR")==0){
								TuplCounter = 1;
								for (int i = 0; i < carrierAttr.size(); i++) {
									
									if(csCarriers.isClosed()){
										csCarriers = dbConnection.getConnection().prepareCall ("{ ? = call RTW_ATTR_VALUE (?,?,?)}");
										csCarriers.registerOutParameter (1, Types.LONGNVARCHAR);
										csCarriers.setString(2, cbTable.getSelectedItem().toString());
									}
									csCarriers.setString(3, carrierAttr.elementAt(i));  
									csCarriers.setInt(4, rsTuples.getInt ("ID"));
									csCarriers.execute ();
									bruteParagraph = csCarriers.getNString(1);
									
									num = "";
									
									//System.out.println("CARRIER ATTR -- " + carrierAttr.elementAt(i));
									
									//check content in the value 
									if((bruteParagraph != null) && (bruteParagraph.trim().length() != 0)){
										
										//System.out.println("PÁRRAFO DE LA TUPLA -- " + rsTuples.getInt ("ID") + " -- ES -- " + bruteParagraph);
										
										//String sentences[] = Util.splitParagraph(bruteParagraph, rsTuples.getInt ("ID"), carrierAttr.elementAt(i));
										Vector<String> sentences = Util.splitParagraph(bruteParagraph);
										
										if(sentences.size() >= Integer.valueOf(txMSPar.getText())){ //check the number of sentences per paragraph
									
											for (int j = 0; j < sentences.size(); j++) {
												
												extListNounsB = "";  ///////////////////
												extLockStreamB = ""; ///////////////////
												extSenseKeyB = "";   /////////////////// 
												extNounB = "";       ///////////////////  
												extSynonymSetB = ""; /////////////////// 
												extMarkB = ""; //////////
												extUsedSynonymB = ""; ///////////////////
												
												String[] words = sentences.elementAt(j).split("\\s+");
												
												if (words[words.length-1].substring(words[words.length-1].length()-1).equals(".")) {
													words[words.length-1] = words[words.length-1].substring(0, words[words.length-1].length()-1);
													sentences.set(j, sentences.elementAt(j).substring(0,sentences.elementAt(j).length()-1));
												}
												
												//System.out.println("SENTENSE -- " + sentences[j]);
												
												if (words.length >= Integer.valueOf(txMWSent.getText())) { //check the number of words per sentence
													
													//if(rsTuples.getInt ("ID") == 1){
														lockStream = Util.getLockStream(tempWNCaller, sentences.elementAt(j), pWordRoles, considerRest, rsTuples.getInt ("ID"));
														lockSup = Util.parseString(lockStream,Integer.valueOf(txBinaryLength.getText()));
													//}
													
														extLockStreamB = Integer.toString(lockSup);  ////////////////////////
													
													if(csSVK.isClosed()){
														csSVK = dbConnection.getConnection().prepareCall ("{ ? = call RTW_GENERATE_AVK (?,?,?,?,?,?)}");
														csSVK.registerOutParameter (1, OracleTypes.CURSOR);
														csSVK.setString(2, tfPrivateKey.getText());
													}
													csSVK.setLong(3, rsTuples.getLong("VPK"));
													csSVK.setInt(4, lockSup);
													csSVK.setInt(5, Integer.valueOf(tfFractTupl.getText()));
													csSVK.setInt(6, imageHeight-1);
													csSVK.setInt(7, imageWidth-1);
													csSVK.execute ();
													
													
													/*if(rsTuples.getInt ("ID") == 1){
														System.out.println("VPK: " + rsTuples.getLong("VPK"));
														System.out.println("STRING BLOQUEADO: " + lockSup);
														System.out.println("TUPLE FRACTION: " + Integer.valueOf(tfFractTupl.getText()));
														System.out.println("ALTURA: " + imageHeight);
														System.out.println("ANCHO: " + imageWidth);
													}*/
													
													
													if (MAX_COTR < 10000) {
														rsAttrVK = (ResultSet)csSVK.getObject (1);
														MAX_COTR = MAX_COTR + 1; 
													}else{	
														rsAttrVK.close();
														rsAttrVK = (ResultSet)csSVK.getObject (1);
														MAX_COTR = 1;
													}
													
													
													
													
													
													
													while (rsAttrVK.next ()){
														sentKey = rsAttrVK.getLong(1);
														height_pos = rsAttrVK.getInt(2);
														width_pos = rsAttrVK.getInt(3);
														
														//System.out.println("POS ALTO: " + height_pos);
														//System.out.println("POS ANCHO: " + width_pos);
														
													}
													
													extSenseKeyB = Long.toString(sentKey); 
													
													/*if(rsTuples.getInt ("ID") == 1){
														System.out.println("LIIIIII: " + sentKey);
														sentKey = new Long(356603013);
													}*/
														
														
														
														 
													for (int k = 0; k < words.length; k++) {
														for (int l = 0; l < pWordCarriers.size(); l++) {
															try {
																
																contains = false;
																hasUpperCase = false;
																contnumb = false;
																
																synUpperCase = false;
																
																if (tempWNCaller.getDictionary().getIndexWord(pWordCarriers.elementAt(l), words[k]) != null) {
																	
																	contains = Arrays.stream(excluded).anyMatch(words[k]::equals);
																	hasUpperCase = !words[k].equals(words[k].toLowerCase());
																	contnumb = words[k].matches(".*\\d.*");
																	
																	if(!contains && !hasUpperCase && !contnumb && words[k].length()>Integer.valueOf(txMinCarrSize.getText())){
																		carrierForSentence.add(new WordCarrier(words[k], pWordCarriers.elementAt(l), k));
																		extListNounsB = extListNounsB + "|" + words[k]; ////////////////
																	}
																}
															} catch (Exception e) {
																e.printStackTrace();
															}
														}
													}
													
													if (carrierForSentence.size()>0) {
														
														currentCarrier = carrierForSentence.elementAt((int)(sentKey % carrierForSentence.size()));
														
														extNounB = currentCarrier.getWord(); 
														
														
														//////////////////////////////////////////////////////////////////////////
														//////////////////////////////////////////////////////////////////////////
														//////////////////////////////////////////////////////////////////////////
														//////////////////////////////////////////////////////////////////////////														
														synsetWord = tempWNCaller.getSynsetWord(currentCarrier, sentences.elementAt(j), wsdMethod);
														
														if(synsetWord != null){
														
															substitutes = tempWNCaller.getSubsSet(synsetWord, Integer.valueOf(txMinCarrSize.getText()));
															
															//if(rsTuples.getInt ("ID") == 30){
															//	System.out.println("SYYYYYYYYYYYYYYYYYY:" + substitutes);
															//}
															
															int count = 0;
															while (!synUpperCase && count < substitutes.size()) {
																if(!substitutes.elementAt(count).equals(substitutes.elementAt(count).toLowerCase()))
																	synUpperCase = true;
																count++;
															} 
															
															if(!synUpperCase){
															
																	if((imageWidth * (height_pos) + width_pos) > 0  ){
																		if (substitutes.size()>1) {
																			
																			for (int m = 0; m < substitutes.size(); m++) {                               ///////////////////////////
																				extSynonymSetB = extSynonymSetB + "|" + substitutes.elementAt(m);     ///////////////////////////
																			}                                                                            ///////////////////////////  
																			
																			
																			if(currentCarrier.getWord().equals(substitutes.elementAt(0))){
																				image_element = 1;
																			}
																			else{
																				image_element = 0;
																			}
																			
																			extMarkB = String.valueOf(image_element);
																			
																			extUsedSynonymB = currentCarrier.getWord();
																		
																			Vector<Integer> tempStore = mayorityInfo[height_pos][width_pos];
									        				
								        									if (tempStore.size()>0) {
								        										int o = 0;
								        										o++;
								        									}
								        									
								        									
								        									csLoadCheck.execute ();
																			if(csLoadCheck.getString(1) != null)
																				num =  csLoadCheck.getString(1) + "|";
																			
								        									
								        									/*csCheckUpdater.setString (1, extListNounsB);
																			csCheckUpdater.setString (2, extLockStreamB);
																			csCheckUpdater.setString (3, num + extSenseKeyB);
																			csCheckUpdater.setString (4, extNounB);
																			csCheckUpdater.setString (5, extSynonymSetB);
																			csCheckUpdater.setString (6, extMarkB);
																			csCheckUpdater.setString (7, extUsedSynonymB);
																			csCheckUpdater.setInt (8, rsTuples.getInt ("ID"));
																			csCheckUpdater.execute (); */
																			
																		///// 21.09.21  csCheckUpdater.setInt (1, rsTuples.getInt ("ID"));
																		///// 21.09.21  csCheckUpdater.setLong (2, sentKey);
																		///// 21.09.21  csCheckUpdater.setString (3, currentCarrier.getWord());
																		///// 21.09.21  csCheckUpdater.setLong (4, synsetWord.getOffset());
																		///// 21.09.21  csCheckUpdater.setString (5, extMarkB);
																			
																		///// 21.09.21  csCheckUpdater.execute (); 
																			
								        									
								        									/*System.out.println("ID--------------------------------------------");
																			System.out.println(rsTuples.getInt ("ID"));
																			
																			System.out.println("SENTENCE--------------------------------------");
																			System.out.println(sentences[j]);
																			
																			System.out.println("LIST OF NOUNS---------------------------------");
																			for (int k = 0; k < carrierForSentence.size(); k++) {
																				System.out.println(carrierForSentence.elementAt(k).getWord());
																			}
																		
																			System.out.println("WORD------------------------------------------");
																			System.out.println(currentCarrier.getWord());
																			
																			System.out.println("LIST OF SUBSTITUTES---------------------------");
																			for (int xx = 0; xx < substitutes.size(); xx++) {
																				System.out.println(substitutes.elementAt(xx));
																			}
																			
																			System.out.println("MARK------------------------------------------");
																			System.out.println(image_element);
																			
																			System.out.println("SENSE KEY-------------------------------------");
																			System.out.println(sentKey);*/
																			
																			
																			
																			//if(rsTuples.getInt ("ID") == 42)
																				//System.out.println("MARK:" + extMarkB);
																			
									        				
								        									tempStore.add(image_element);
								        									mayorityInfo[height_pos][width_pos] = tempStore;
								        									//tempStore.clear();
																		}
																	}
															}
														}
														carrierForSentence.clear();
													}
												}
											}
										}
									}
								}
								TuplCounter = 0;
							}
							
			        		} catch (Exception e) {
								e.printStackTrace();
							}
			        		finally {
			        			if(csCarriers != null)
					    			csCarriers.close();
					    		if(csSVK != null)
					    			csSVK.close();
					    		
			        			/*try {
									//rsAttrVK.close();
									//rsTuples.close();
									//csTuples.close();
								} catch (Exception e2) {
									e2.printStackTrace();
								}*/
							}
						}
						

						//System.out.println("-----------------------------------------------");
		        	
						int cant_0 = 0;
						int cant_1 = 0;
						
			        	for (int i = 0; i < imageWidth; i++) {
		 					for (int j = 0; j < imageHeight; j++) {
		 						Vector<Integer> tempStore = mayorityInfo[j][i]; 
		 						cant_0 = 0;
		 			        	cant_1 = 0;
		 						for (int k = 0; k < tempStore.size(); k++) {
		 							if (tempStore.elementAt(k) == 0) {
										cant_0++;
									} else {
										cant_1++;
									}
								}
		 						
		 						if ((cant_0 != 0)||(cant_1 != 0)) {
			 						if (cant_0 > cant_1) {
			 							recoveredInfo[j][i] = 0;
									}else{
										recoveredInfo[j][i] = 1;
									}
		 						}
		 					}
		 				}
		        	
					    //RESULTS REPORT BUIL SECTION
						
						int no_recovered = 0;
						for (int i = 0; i < imageWidth; i++) {
		 					for (int j = 0; j < imageHeight; j++) {
		 						if(recoveredInfo[j][i] != -1)
		 							no_recovered++;
		 					}
		 				}
						
						 
						BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
						
						for (int i = 0; i < imageWidth; i++) {
		 					for (int j = 0; j < imageHeight; j++) {
		 						if(recoveredInfo[j][i] == 1)
		 							img.setRGB(i, j, Color.BLACK.getRGB());
		 						else
		 							if(recoveredInfo[j][i] == 0)
		 								img.setRGB(i, j, Color.WHITE.getRGB());
		 							else
		 								img.setRGB(i, j, Color.RED.getRGB());
		 								//img.setRGB(i, j, Color.BLACK.getRGB());
		 								//img.setRGB(i, j, Color.WHITE.getRGB());
		 					}
		 				}
		            
						Image scaledInstance = img.getScaledInstance(lbImageViewer.getWidth(), lbImageViewer.getHeight(), Image.SCALE_DEFAULT);
						ImageIcon imageIcon = new ImageIcon(scaledInstance);
						lbImageViewer.setIcon(imageIcon);
		            	
						lbEnhancedImage.setIcon(imageIcon);
						
						respRecoveredInfo = new int[imageHeight][imageWidth];
						
						
						for (int i = 0; i < imageWidth; i++) {
		 					for (int j = 0; j < imageHeight; j++) {
		 						respRecoveredInfo[j][i] = recoveredInfo[j][i];
		 					}
		 				}
						
						txExtractedPx.setText(String.valueOf(no_recovered));
						txTotalTupl.setText(String.valueOf(dbConnection.getNoRows(cbTable.getSelectedItem().toString(), tfPrivateKey.getText(), Integer.valueOf(tfFractTupl.getText()))));
						DecimalFormat df = new DecimalFormat("##.##");
						df.setRoundingMode(RoundingMode.DOWN);
						
						if (Float.valueOf(txTotalPx.getText())!=0) {
							txEPP.setText(String.valueOf(df.format(Float.valueOf(txExtractedPx.getText())*100/Float.valueOf(txTotalPx.getText()))));
						}
						txTotalTuples.setText(String.valueOf(dbConnection.getAllRows(cbTable.getSelectedItem().toString())));
						
						if (Float.valueOf(txTotalTuples.getText())!=0) {
							txMTP.setText(String.valueOf(df.format(Float.valueOf(txTotalTupl.getText())*100/Float.valueOf(txTotalTuples.getText()))));
						}
						
						btnEnhance.setEnabled(true);
						cbSymmetricImage.setEnabled(true);
					
					
					System.out.println("-----------------------------------------------");
			    	//ENDING TIME
				    Calendar cal1 = Calendar.getInstance();
			    	cal1.getTime();
			    	SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
			    	System.out.println ("PROCESS COMPLETED AT: "+sdf1.format(cal1.getTime()));
			    	System.out.println("-----------------------------------------------");
			    	
			    	//btnEnhance.doClick();
					//JOptionPane.showMessageDialog(null, "Extraction Process completed...");
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "Extraction Process completed...");
						
						} catch (Exception e) {
							e.printStackTrace();
						}finally {
							/*try {
								//rsAttrVK.close();
								//rsTuples.close();
								//csTuples.close();
							} catch (Exception e2) {
								e2.printStackTrace();
							}*/
							
							try {
								//rsAttrVK.close();
								rsTuples.close();
								csTuples.close();
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
				
						try {
							csCarriers.close();
							csSVK.close();
							rsAttrVK.close();
							rsTuples.close();
							csTuples.close();
						} 
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				getContentPane().add(btnStart);
		
			JButton btnExit = new JButton("Close");
			btnExit.setBounds(196, 427, 122, 23);
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
			
			JPanel panel_5 = new JPanel();
			panel_5.setLayout(null);
			panel_5.setBorder(BorderFactory.createTitledBorder(null, "  Relation Tuples ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_5.setBounds(196, 247, 124, 147);
			getContentPane().add(panel_5);
			
			JLabel label_4 = new JLabel("Marked:");
			label_4.setHorizontalAlignment(SwingConstants.RIGHT);
			label_4.setBounds(12, 63, 52, 14);
			panel_5.add(label_4);
			
			txTotalTupl = new JTextField();
			txTotalTupl.setText("0");
			txTotalTupl.setEditable(false);
			txTotalTupl.setColumns(10);
			txTotalTupl.setBounds(68, 60, 46, 20);
			panel_5.add(txTotalTupl);
			
			JLabel label_5 = new JLabel("Total:");
			label_5.setHorizontalAlignment(SwingConstants.RIGHT);
			label_5.setBounds(14, 32, 43, 14);
			panel_5.add(label_5);
			
			txTotalTuples = new JTextField();
			txTotalTuples.setText("0");
			txTotalTuples.setEditable(false);
			txTotalTuples.setColumns(10);
			txTotalTuples.setBounds(61, 29, 53, 20);
			panel_5.add(txTotalTuples);
			
			JLabel label_7 = new JLabel("Percent:");
			label_7.setHorizontalAlignment(SwingConstants.RIGHT);
			label_7.setBounds(12, 110, 52, 14);
			panel_5.add(label_7);
			
			txMTP = new JTextField();
			txMTP.setText("0");
			txMTP.setEditable(false);
			txMTP.setColumns(10);
			txMTP.setBounds(68, 107, 46, 20);
			panel_5.add(txMTP);
			
			JLabel label_9 = new JLabel("______________");
			label_9.setOpaque(true);
			label_9.setHorizontalAlignment(SwingConstants.LEFT);
			label_9.setBounds(14, 85, 103, 14);
			panel_5.add(label_9);
			
			JPanel panel_6 = new JPanel();
			panel_6.setLayout(null);
			panel_6.setBorder(BorderFactory.createTitledBorder(null, "  Enhanced Image  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_6.setBounds(330, 249, 387, 207);
			getContentPane().add(panel_6);
			
			lbEnhancedImage = new JLabel();
			lbEnhancedImage.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			lbEnhancedImage.setBounds(10, 26, 184, 169);
			panel_6.add(lbEnhancedImage);
			
			txRecPx = new JTextField();
			txRecPx.setText("0");
			txRecPx.setEditable(false);
			txRecPx.setColumns(10);
			txRecPx.setBounds(325, 86, 49, 20);
			panel_6.add(txRecPx);
			
			txMissPx = new JTextField();
			txMissPx.setText("0");
			txMissPx.setEditable(false);
			txMissPx.setColumns(10);
			txMissPx.setBounds(324, 110, 50, 20);
			panel_6.add(txMissPx);
			
			JLabel lblStillMissed = new JLabel("Still Missed:");
			lblStillMissed.setHorizontalAlignment(SwingConstants.RIGHT);
			lblStillMissed.setBounds(217, 113, 104, 14);
			panel_6.add(lblStillMissed);
			
			JLabel lblRecovered = new JLabel("Recovered:");
			lblRecovered.setHorizontalAlignment(SwingConstants.RIGHT);
			lblRecovered.setBounds(218, 89, 104, 14);
			panel_6.add(lblRecovered);
			
			txEnPerc = new JTextField();
			txEnPerc.setText("0");
			txEnPerc.setEditable(false);
			txEnPerc.setColumns(10);
			txEnPerc.setBounds(338, 153, 36, 20);
			panel_6.add(txEnPerc);
			
			JLabel lblPercentOfEnhanced = new JLabel("Percent of Enhanced:");
			lblPercentOfEnhanced.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPercentOfEnhanced.setBounds(204, 156, 131, 14);
			panel_6.add(lblPercentOfEnhanced);
			
			JLabel lblPercentOfRecovered = new JLabel("Percent of Recovered:");
			lblPercentOfRecovered.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPercentOfRecovered.setBounds(204, 178, 131, 14);
			panel_6.add(lblPercentOfRecovered);
			
			txRecPerc = new JTextField();
			txRecPerc.setText("0");
			txRecPerc.setEditable(false);
			txRecPerc.setColumns(10);
			txRecPerc.setBounds(338, 175, 36, 20);
			panel_6.add(txRecPerc);
			
			btnEnhance = new JButton("Enhance");
			btnEnhance.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int cant_0 = 0;
					int cant_1 = 0;
					int cant_null = 0;
					int no_recovered = 0;
					int remain_miss = 0;
					int we, he;
					
					respRecoveredInfo = new int[imageHeight][imageWidth];
					
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						respRecoveredInfo[j][i] = recoveredInfo[j][i];
	 					}
	 				}
					
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(respRecoveredInfo[j][i] == -1){
	 							//majority neighbour pixels value
	 							if(i < 1) we = i; else we = i-1;
	 							if(j < 1) he = j; else he = j-1;
	 							
	 							for (int w = we;w < i+2; w++) {
	 			 					for (int h = he; h < j+2; h++) {
	 			 						if((h != j)||(w != i)){
	 			 							if((h < imageHeight)&&(w < imageWidth)){
	 			 								if(respRecoveredInfo[h][w] == 0) cant_0++;
	 			 								else if(respRecoveredInfo[h][w] == 1) cant_1++;
	 			 								 	else cant_null++;
	 			 							}
	 			 							
	 			 						}
	 			 					}
	 							}
	 							if((cant_0 > cant_1)&&(cant_0 + cant_1 > cant_null)) {respRecoveredInfo[j][i] = 0; no_recovered++;}
		 						else 
		 							if((cant_0 < cant_1)&&(cant_0 + cant_1 > cant_null)) {respRecoveredInfo[j][i] = 1; no_recovered++;}
	 								else 
	 									if(cbSymmetricImage.isSelected()){
		 									//assuming logo simetry
		 									if(respRecoveredInfo[j][imageWidth-i-1] != -1){
		 										respRecoveredInfo[j][i] = respRecoveredInfo[j][imageWidth-i-1];
		 										no_recovered++;
		 									}
	 								}
	 							cant_0 = 0; cant_1 = 0; cant_null = 0;
	 						}
	 					}
	 				}
					
					img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
					img2 = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(respRecoveredInfo[j][i] == 1)
	 							img.setRGB(i, j, Color.BLACK.getRGB());
	 						else
	 							if(respRecoveredInfo[j][i] == 0)
	 								img.setRGB(i, j, Color.WHITE.getRGB());
	 							else {	
	 								img.setRGB(i, j, Color.RED.getRGB());
	 								//img.setRGB(i, j, Color.BLACK.getRGB());
	 								remain_miss++;
	 							}
	 					}
	 				}
					
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(recoveredInfo[j][i] == 1)
	 							img2.setRGB(i, j, Color.BLACK.getRGB());
	 						else
	 							if(recoveredInfo[j][i] == 0)
	 								img2.setRGB(i, j, Color.WHITE.getRGB());
	 							else {	
	 								img2.setRGB(i, j, Color.RED.getRGB());
	 								//img.setRGB(i, j, Color.BLACK.getRGB());
	 								remain_miss++;
	 							}
	 					}
	 				}
					
					
					
					
					
		            
					Image scaledInstance = img.getScaledInstance(lbEnhancedImage.getWidth(), lbEnhancedImage.getHeight(), Image.SCALE_DEFAULT);
					ImageIcon imageIcon = new ImageIcon(scaledInstance);
					lbEnhancedImage.setIcon(imageIcon);
					
					txRecPx.setText(String.valueOf(no_recovered));
					txMissPx.setText(String.valueOf(remain_miss));
					
					DecimalFormat df = new DecimalFormat("##.##");
					df.setRoundingMode(RoundingMode.DOWN);
					txEnPerc.setText(String.valueOf(df.format(100*Float.valueOf(txRecPx.getText())/(Float.valueOf(txRecPx.getText())+Float.valueOf(txMissPx.getText())))));
					txRecPerc.setText(String.valueOf(df.format(100*(Float.valueOf(txTotalPx.getText())-Float.valueOf(txMissPx.getText()))/Float.valueOf(txTotalPx.getText()))));
					
					btnSave.setEnabled(true);
					
					
					
					int black = 0;
					int white = 0;
					int red = 0;
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(respRecoveredInfo[j][i] == 1)
	 							black++;
	 						else
	 							if(respRecoveredInfo[j][i] == 0)
	 								white++;
	 							else {	
	 								red++;
	 							}
	 					}
	 				}
					
					
					
					System.out.println("BLACK - PIXELS: " + String.valueOf(black));
					System.out.println("WHITE - PIXELS: " + String.valueOf(white));
					System.out.println("RED --- PIXELS: " + String.valueOf(red));
					
				}
			});
			btnEnhance.setEnabled(false);
			btnEnhance.setBounds(204, 25, 85, 23);
			panel_6.add(btnEnhance);
			
			btnSave = new JButton("Save");
			btnSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						
						int remain_miss = 0;
						
						img2 = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

						for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(recoveredInfo[j][i] == 1)
	 							img2.setRGB(i, j, Color.BLACK.getRGB());
	 						else
	 							if(recoveredInfo[j][i] == 0)
	 								img2.setRGB(i, j, Color.WHITE.getRGB());
	 							else {	
	 								img2.setRGB(i, j, Color.RED.getRGB());
	 								//img.setRGB(i, j, Color.BLACK.getRGB());
	 								remain_miss++;
	 							}
	 					}
	 				}
						
						System.out.println("EXCLUIDOSSS: " + remain_miss);
						
						
						//ImageIO.write(img, "bmp", new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"original.bmp"));
						
						ImageIO.write(img2, "bmp", new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"ext.bmp"));
						
						JOptionPane.showMessageDialog(null, "The extracted image was successfully saved...");
						
						
						//JOptionPane.showMessageDialog(null, "The extracted image was successfully saved...");
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			});
			btnSave.setBounds(291, 25, 85, 23);
			panel_6.add(btnSave);
			
			JLabel label_10 = new JLabel("________________________");
			label_10.setOpaque(true);
			label_10.setHorizontalAlignment(SwingConstants.LEFT);
			label_10.setBounds(206, 129, 169, 14);
			panel_6.add(label_10);
			
			cbSymmetricImage = new JCheckBox("Symmetric Image");
			cbSymmetricImage.setSelected(true);
			cbSymmetricImage.setEnabled(false);
			cbSymmetricImage.setBounds(200, 56, 152, 23);
			panel_6.add(cbSymmetricImage);
			
			JPanel pnAlgorithms = new JPanel();
			pnAlgorithms.setLayout(null);
			pnAlgorithms.setBorder(BorderFactory.createTitledBorder(null, "  Algorithm Variations ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			pnAlgorithms.setBounds(10, 247, 184, 205);
			getContentPane().add(pnAlgorithms);
			
			
			
			rbOrigMeth = new JRadioButton("Original Method");
			//rbOrigMeth.setSelected(true);
			rbOrigMeth.setBounds(10, 24, 152, 23);
			pnAlgorithms.add(rbOrigMeth);
			
			rbExtMeth = new JRadioButton("Ext. (Mult Attr)");
			rbExtMeth.setBounds(10, 70, 109, 23);
			
			rbExtMeth.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					if (arg0.getStateChange() == ItemEvent.SELECTED) {
						cbZDist.setEnabled(true);
						/*cbMDist.setEnabled(true);
						txMinLSB.setEditable(true);
						cbLsbM.setEnabled(true);*/
				    }
				    else if (arg0.getStateChange() == ItemEvent.DESELECTED) {
				    	cbZDist.setEnabled(false);
						/*cbMDist.setEnabled(false);
						txMinLSB.setEditable(false);
						cbLsbM.setEnabled(false);*/
						
						cbZDist.setSelected(false);
						/*cbMDist.setSelected(false);
						txMinLSB.setText(String.valueOf(0));
						cbLsbM.setSelected(false);*/
				    }
				}
			});
			pnAlgorithms.add(rbExtMeth);
			
			bgMethod.add(rbOrigMeth);
			bgMethod.add(rbExtMeth);
			
			JLabel label_8 = new JLabel("----------------------------");
			label_8.setOpaque(true);
			label_8.setHorizontalAlignment(SwingConstants.LEFT);
			label_8.setBounds(15, 49, 138, 14);
			pnAlgorithms.add(label_8);
			
			cbZDist = new JCheckBox("Zero-Distortion");
			cbZDist.setEnabled(false);
			cbZDist.setBounds(36, 92, 119, 23);
			pnAlgorithms.add(cbZDist);
			
			bgExtOpt.add(cbZDist);
			
			btnAssign = new JButton("Assign");
			btnAssign.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					Random rand = new Random();
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(respRecoveredInfo[j][i] == -1){
	 							respRecoveredInfo[j][i] = rand.nextInt((1) + 1);
	 						}
	 					}
	 				}
					    
					
					img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(respRecoveredInfo[j][i] == 1)
	 							img.setRGB(i, j, Color.BLACK.getRGB());
	 						else
	 							if(respRecoveredInfo[j][i] == 0)
	 								img.setRGB(i, j, Color.WHITE.getRGB());
	 							else {	
	 								img.setRGB(i, j, Color.RED.getRGB());
	 							}
	 					}
	 				}

					Image scaledInstance = img.getScaledInstance(lbEnhancedImage.getWidth(), lbEnhancedImage.getHeight(), Image.SCALE_DEFAULT);
					ImageIcon imageIcon = new ImageIcon(scaledInstance);
					lbEnhancedImage.setIcon(imageIcon);
					
				}
			});
			//btnAssign.setEnabled(false);
			btnAssign.setBounds(727, 275, 85, 23);
			getContentPane().add(btnAssign);
			
			btnErode = new JButton("Erode");
			btnErode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int we, he;
					
					filteredImage = new int[imageHeight][imageWidth];
					
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						filteredImage[j][i] = respRecoveredInfo[j][i];
	 					}
	 				}
					
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(respRecoveredInfo[j][i] == 0){
	 							if(i < 1) we = i; else we = i-1;
	 							if(j < 1) he = j; else he = j-1;
	 							
	 							for (int w = we;w < i+2; w++) {
	 			 					for (int h = he; h < j+2; h++) {
	 			 						if((h != j)||(w != i)){
	 			 							if((h < imageHeight)&&(w < imageWidth)){
	 			 								if(respRecoveredInfo[h][w] == 1){
	 			 									filteredImage[h][w] = 0;
	 			 								}
	 			 							}
	 			 						}
	 			 					}
	 							}
	 						}
	 					}
	 				}
					
					img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(filteredImage[j][i] == 1)
	 							img.setRGB(i, j, Color.BLACK.getRGB());
	 						else
	 							if(filteredImage[j][i] == 0)
	 								img.setRGB(i, j, Color.WHITE.getRGB());
	 							else {	
	 								img.setRGB(i, j, Color.RED.getRGB());
	 							}
	 					}
	 				}

					Image scaledInstance = img.getScaledInstance(lbEnhancedImage.getWidth(), lbEnhancedImage.getHeight(), Image.SCALE_DEFAULT);
					ImageIcon imageIcon = new ImageIcon(scaledInstance);
					lbEnhancedImage.setIcon(imageIcon);
					
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						respRecoveredInfo[j][i] = filteredImage[j][i];
	 					}
	 				}
					
					
				}
			});
			//btnErode.setEnabled(false);
			btnErode.setBounds(727, 371, 85, 23);
			getContentPane().add(btnErode);
			
			btnDilate = new JButton("Dilate");
			btnDilate.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int we, he;
					
					filteredImage = new int[imageHeight][imageWidth];
					
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						filteredImage[j][i] = respRecoveredInfo[j][i];
	 					}
	 				}
					
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(respRecoveredInfo[j][i] == 1){
	 							if(i < 1) we = i; else we = i-1;
	 							if(j < 1) he = j; else he = j-1;
	 							
	 							for (int w = we;w < i+2; w++) {
	 			 					for (int h = he; h < j+2; h++) {
	 			 						if((h != j)||(w != i)){
	 			 							if((h < imageHeight)&&(w < imageWidth)){
	 			 								if(respRecoveredInfo[h][w] == 0){
	 			 									filteredImage[h][w] = 1;
	 			 								}
	 			 							}
	 			 						}
	 			 					}
	 							}
	 						}
	 					}
	 				}
					
					img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(filteredImage[j][i] == 1)
	 							img.setRGB(i, j, Color.BLACK.getRGB());
	 						else
	 							if(filteredImage[j][i] == 0)
	 								img.setRGB(i, j, Color.WHITE.getRGB());
	 							else {	
	 								img.setRGB(i, j, Color.RED.getRGB());
	 							}
	 					}
	 				}

					Image scaledInstance = img.getScaledInstance(lbEnhancedImage.getWidth(), lbEnhancedImage.getHeight(), Image.SCALE_DEFAULT);
					ImageIcon imageIcon = new ImageIcon(scaledInstance);
					lbEnhancedImage.setIcon(imageIcon);
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						respRecoveredInfo[j][i] = filteredImage[j][i];
	 					}
	 				}
					

				}
			});
			//btnDilate.setEnabled(false);
			btnDilate.setBounds(727, 343, 85, 23);
			getContentPane().add(btnDilate);
			
			txMSBESCheme = new JTextField();
			txMSBESCheme.setText("3");
			txMSBESCheme.setColumns(10);
			txMSBESCheme.setBounds(155, 460, 37, 20);
			getContentPane().add(txMSBESCheme);
			
			JPanel panel_7 = new JPanel();
			panel_7.setLayout(null);
			panel_7.setBorder(BorderFactory.createTitledBorder(null, "  Role of each Word  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_7.setBounds(727, 11, 212, 158);
			getContentPane().add(panel_7);
			
			JLabel label_11 = new JLabel("L       C       E");
			label_11.setHorizontalAlignment(SwingConstants.CENTER);
			label_11.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
			label_11.setBounds(10, 28, 92, 14);
			panel_7.add(label_11);
			
			rbSubL = new JRadioButton("");
			rbSubL.setBounds(17, 46, 27, 23);
			panel_7.add(rbSubL);
			
			rbSubC = new JRadioButton("");
			rbSubC.setSelected(true);
			rbSubC.setBounds(46, 46, 27, 23);
			panel_7.add(rbSubC);
			
			rbSubE = new JRadioButton(" Noun");
			rbSubE.setBounds(75, 46, 118, 23);
			panel_7.add(rbSubE);
			
			rbVerbE = new JRadioButton(" Verbs");
			rbVerbE.setBounds(75, 64, 124, 23);
			panel_7.add(rbVerbE);
			
			rbVerbC = new JRadioButton("");
			rbVerbC.setBounds(46, 64, 27, 23);
			panel_7.add(rbVerbC);
			
			rbVerbL = new JRadioButton("");
			rbVerbL.setSelected(true);
			rbVerbL.setBounds(17, 64, 27, 23);
			panel_7.add(rbVerbL);
			
			rbAdjE = new JRadioButton("Adjective");
			rbAdjE.setBounds(75, 82, 124, 23);
			panel_7.add(rbAdjE);
			
			rbAdjC = new JRadioButton("");
			rbAdjC.setBounds(46, 82, 27, 23);
			panel_7.add(rbAdjC);
			
			rbAdjL = new JRadioButton("");
			rbAdjL.setSelected(true);
			rbAdjL.setBounds(17, 82, 27, 23);
			panel_7.add(rbAdjL);
			
			rbAdvE = new JRadioButton("Adverb");
			rbAdvE.setSelected(true);
			rbAdvE.setBounds(75, 100, 124, 23);
			panel_7.add(rbAdvE);
			
			rbAdvC = new JRadioButton("");
			rbAdvC.setBounds(46, 100, 27, 23);
			panel_7.add(rbAdvC);
			
			rbAdvL = new JRadioButton("");
			rbAdvL.setBounds(17, 100, 27, 23);
			panel_7.add(rbAdvL);
			
			rbOME = new JRadioButton(" Other Modifiers");
			rbOME.setBounds(75, 118, 124, 23);
			panel_7.add(rbOME);
			
			rbOML = new JRadioButton("");
			rbOML.setSelected(true);
			rbOML.setBounds(17, 118, 27, 23);
			panel_7.add(rbOML);
			
			rbOMC = new JRadioButton("");
			rbOMC.setEnabled(false);
			rbOMC.setBounds(46, 118, 27, 23);
			panel_7.add(rbOMC);
			
			txBinaryLength = new JTextField();
			txBinaryLength.setText("20");
			txBinaryLength.setColumns(10);
			txBinaryLength.setBounds(869, 180, 32, 20);
			getContentPane().add(txBinaryLength);
			
			JLabel label_13 = new JLabel("Binary Length:");
			label_13.setHorizontalAlignment(SwingConstants.RIGHT);
			label_13.setBounds(727, 183, 141, 14);
			getContentPane().add(label_13);
			
			JPanel panel_8 = new JPanel();
			panel_8.setLayout(null);
			panel_8.setBorder(BorderFactory.createTitledBorder(null, "  Similarity Metric  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_8.setBounds(844, 275, 163, 273);
			getContentPane().add(panel_8);
			
			rbMetrWUP = new JRadioButton("WUP");
			rbMetrWUP.setBounds(6, 25, 66, 23);
			panel_8.add(rbMetrWUP);
			
			rbMetrJCN = new JRadioButton("JCN");
			rbMetrJCN.setBounds(6, 51, 66, 23);
			panel_8.add(rbMetrJCN);
			
			rbMetrLCH = new JRadioButton("LCH");
			rbMetrLCH.setBounds(6, 79, 66, 23);
			panel_8.add(rbMetrLCH);
			
			rbMetrLIN = new JRadioButton("LIN");
			rbMetrLIN.setSelected(true);
			rbMetrLIN.setBounds(6, 104, 66, 23);
			panel_8.add(rbMetrLIN);
			
			rbMetrHSO = new JRadioButton("HSO");
			rbMetrHSO.setBounds(74, 104, 66, 23);
			panel_8.add(rbMetrHSO);
			
			rbMetrLESK = new JRadioButton("LESK");
			rbMetrLESK.setBounds(74, 79, 66, 23);
			panel_8.add(rbMetrLESK);
			
			rbMetrPATH = new JRadioButton("PATH");
			rbMetrPATH.setBounds(74, 51, 66, 23);
			panel_8.add(rbMetrPATH);
			
			rbMetrRES = new JRadioButton("RES");
			rbMetrRES.setBounds(74, 25, 66, 23);
			panel_8.add(rbMetrRES);
			
			txMaxDist = new JTextField();
			txMaxDist.setText("0.5");
			txMaxDist.setColumns(10);
			txMaxDist.setBounds(113, 225, 40, 20);
			panel_8.add(txMaxDist);
			
			JLabel label_14 = new JLabel("Max. Allow. Pert:");
			label_14.setHorizontalAlignment(SwingConstants.RIGHT);
			label_14.setBounds(6, 228, 87, 14);
			panel_8.add(label_14);
			
			JRadioButton rdbtnSolo1 = new JRadioButton("Solo 1");
			rdbtnSolo1.setSelected(true);
			rdbtnSolo1.setBounds(6, 137, 66, 23);
			panel_8.add(rdbtnSolo1);
			
			JRadioButton rdbtnSolo0 = new JRadioButton("Solo 0");
			rdbtnSolo0.setBounds(74, 134, 66, 23);
			panel_8.add(rdbtnSolo0);
			
			JRadioButton rdbtnNada = new JRadioButton("Nada");
			rdbtnNada.setBounds(6, 174, 66, 23);
			panel_8.add(rdbtnNada);
			
			JRadioButton rdbtnMayor = new JRadioButton("Mayor");
			rdbtnMayor.setSelected(true);
			rdbtnMayor.setBounds(74, 174, 66, 23);
			panel_8.add(rdbtnMayor);
			
			JPanel panel_9 = new JPanel();
			panel_9.setLayout(null);
			panel_9.setBorder(BorderFactory.createTitledBorder(null, "  WSD Algorithm ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_9.setBounds(828, 211, 155, 61);
			getContentPane().add(panel_9);
			
			rbWSDLesk = new JCheckBox("Lesk");
			rbWSDLesk.setSelected(true);
			rbWSDLesk.setBounds(22, 25, 109, 23);
			panel_9.add(rbWSDLesk);
			
			JLabel label_15 = new JLabel("Min. Words:");
			label_15.setHorizontalAlignment(SwingConstants.RIGHT);
			label_15.setBounds(118, 40, 104, 14);
			getContentPane().add(label_15);
			
			txSentLength = new JTextField();
			txSentLength.setText("10");
			txSentLength.setColumns(10);
			txSentLength.setBounds(225, 37, 38, 20);
			getContentPane().add(txSentLength);
			
			JButton btnAttrs = new JButton("...");
			btnAttrs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					try {
						ResultSetMetaData rsmd = dbConnection.getAttrMD(cbTable.getSelectedItem().toString());
						int cons = 0;
						int cons2 = 0;
						//System.out.println("---Name:" + rsmd.getColumnCount());
						carriersModel.setRowCount(0);
						locksNumModel.setRowCount(0);
						locksSWModel.setRowCount(0);  //txMWCat
						locksMWModel.setRowCount(0);  //txMWCat
						
						tmMSB = tbLocksNum.getColumnModel().getColumn(2);
						
						cbMSB.removeAllItems();
						cbMSB.addItem("ALL");
						for (int j = 0; j < Integer.valueOf(txMSB.getText()); j++) {
							cbMSB.addItem(j+1);
						} 
						tmMSB.setCellEditor(new DefaultCellEditor(cbMSB));
						
						for (int i = 0; i < rsmd.getColumnCount(); i++) {
							if (rsmd.getColumnType(i+1)== OracleTypes.NUMBER) {
								if ((!rsmd.getColumnName(i+1).equals("ID")) && (!rsmd.getColumnName(i+1).equals("VPK"))) {
									//model.addRow(new Object[]{Boolean.FALSE, rsmd.getColumnName(i+1), "NUMERIC"});
									locksNumModel.addRow(new Object[]{Boolean.FALSE, rsmd.getColumnName(i+1), "ALL"});
								}
							} else {
								cons = dbConnection.getMultiWordAttr(cbTable.getSelectedItem().toString(), rsmd.getColumnName(i+1), Integer.valueOf(txSentLength.getText())-1);
								cons2 = dbConnection.getMultiWordAttr(cbTable.getSelectedItem().toString(), rsmd.getColumnName(i+1), Integer.valueOf(txMWB.getText())-1);
								
								if (cons == 0) {
									//model.addRow(new Object[]{Boolean.FALSE, rsmd.getColumnName(i+1), "STRING"});
									carriersModel.addRow(new Object[]{Boolean.FALSE, rsmd.getColumnName(i+1)});
								} else {
									//model.addRow(new Object[]{Boolean.TRUE, rsmd.getColumnName(i+1), "STRING"});
									carriersModel.addRow(new Object[]{Boolean.TRUE, rsmd.getColumnName(i+1)});
								}
								
								if (dbConnection.getAttrSW(cbTable.getSelectedItem().toString(), rsmd.getColumnName(i+1), Integer.valueOf(txMWCat.getText())-1)) {
									locksSWModel.addRow(new Object[]{Boolean.FALSE, rsmd.getColumnName(i+1)});
								}
								
								if (cons2 != 0) {
									locksMWModel.addRow(new Object[]{Boolean.FALSE, rsmd.getColumnName(i+1)});
								}
							}
						}
						
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				
				}
			});
			btnAttrs.setBounds(268, 36, 50, 23);
			getContentPane().add(btnAttrs);
			
			JPanel panel_10 = new JPanel();
			panel_10.setLayout(null);
			panel_10.setBorder(BorderFactory.createTitledBorder(null, "  Locks (Numerical) ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_10.setBounds(45, 491, 245, 194);
			getContentPane().add(panel_10);
			
			JScrollPane spLocks = new JScrollPane();
			spLocks.setBounds(10, 22, 223, 129);
			panel_10.add(spLocks);
			
			tbLocksNum = new JTable() {
				public Class getColumnClass(int column) {
					return (Class) null;
				}
			};
			
	        tbLocksNum = new JTable( ){
	            private static final long serialVersionUID = 1L;
	            @Override
	            public Class getColumnClass(int column) {
	                switch (column) {
	                    case 0:
	                    	return Boolean.class;
	                    case 1:
	                        return String.class;
	                    case 2:
	                    	 return Integer.class;
	                    default:
	                        return Boolean.class;
	                }
	            }
	        };
	        
	        tbLocksSW = new JTable( ){
	            private static final long serialVersionUID = 1L;
	            @Override
	            public Class getColumnClass(int column) {
	                switch (column) {
	                    case 0:
	                    	return Boolean.class;
	                    case 1:
	                        return String.class;
	                    default:
	                        return Boolean.class;
	                }
	            }
	        };
	        
	        
			tbLocksNum.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			locksNumModel = new DefaultTableModel();
			locksNumModel.addColumn("Get");
			locksNumModel.addColumn("Name");
			locksNumModel.addColumn("MSB");
			
			this.tbLocksNum.setModel(locksNumModel);
			this.tbLocksNum.getColumnModel().getColumn(0).setPreferredWidth(37);
			this.tbLocksNum.getColumnModel().getColumn(1).setPreferredWidth(128);
			this.tbLocksNum.getColumnModel().getColumn(2).setPreferredWidth(40);
			this.tbLocksNum.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			spLocks.setViewportView(tbLocksNum);
			
			txLSB = new JTextField();
			txLSB.setText("1");
			txLSB.setColumns(10);
			txLSB.setBounds(104, 162, 28, 20);
			panel_10.add(txLSB);
			
			JLabel label_16 = new JLabel("LSB:");
			label_16.setHorizontalAlignment(SwingConstants.RIGHT);
			label_16.setBounds(70, 165, 32, 14);
			panel_10.add(label_16);
			
			txMSB = new JTextField();
			txMSB.setText("3");
			txMSB.setColumns(10);
			txMSB.setBounds(43, 162, 28, 20);
			panel_10.add(txMSB);
			
			JLabel label_17 = new JLabel("MSB:");
			label_17.setHorizontalAlignment(SwingConstants.RIGHT);
			label_17.setBounds(10, 165, 32, 14);
			panel_10.add(label_17);
			
			JCheckBox cbAbsVal = new JCheckBox("Abs.");
			cbAbsVal.setSelected(true);
			cbAbsVal.setEnabled(false);
			cbAbsVal.setBounds(138, 158, 47, 23);
			panel_10.add(cbAbsVal);
			
			JCheckBox cbIntPart = new JCheckBox("Int.");
			cbIntPart.setSelected(true);
			cbIntPart.setEnabled(false);
			cbIntPart.setBounds(187, 158, 52, 23);
			panel_10.add(cbIntPart);
			
			JPanel panel_11 = new JPanel();
			panel_11.setLayout(null);
			panel_11.setBorder(BorderFactory.createTitledBorder(null, "  Locks (Light Text)  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_11.setBounds(304, 491, 205, 194);
			getContentPane().add(panel_11);
			
			JScrollPane spLockSW = new JScrollPane();
			spLockSW.setBounds(10, 22, 183, 129);
			panel_11.add(spLockSW);
			
			tbLocksSW.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			locksSWModel = new DefaultTableModel();
			locksSWModel.addColumn("Get");
			locksSWModel.addColumn("Name");
			
			this.tbLocksSW.setModel(locksSWModel);
			this.tbLocksSW.getColumnModel().getColumn(0).setPreferredWidth(37);
			this.tbLocksSW.getColumnModel().getColumn(1).setPreferredWidth(128);
			this.tbLocksSW.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			spLockSW.setViewportView(tbLocksSW);
			
			JLabel label_18 = new JLabel("Max. Words:");
			label_18.setHorizontalAlignment(SwingConstants.RIGHT);
			label_18.setBounds(10, 160, 142, 14);
			panel_11.add(label_18);
			
			txMWCat = new JTextField();
			txMWCat.setText("3");
			txMWCat.setColumns(10);
			txMWCat.setBounds(152, 157, 41, 20);
			panel_11.add(txMWCat);
			
			JPanel panel_12 = new JPanel();
			panel_12.setLayout(null);
			panel_12.setBorder(BorderFactory.createTitledBorder(null, "  Multi-Words  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_12.setBounds(523, 486, 204, 158);
			getContentPane().add(panel_12);
			
			JScrollPane spLockMW = new JScrollPane();
			spLockMW.setBounds(10, 22, 183, 99);
			panel_12.add(spLockMW);
			
			tbLocksMW = new JTable( ){
	            private static final long serialVersionUID = 1L;
	            @Override
	            public Class getColumnClass(int column) {
	                switch (column) {
	                    case 0:
	                    	return Boolean.class;
	                    case 1:
	                        return String.class;
	                    default:
	                        return Boolean.class;
	                }
	            }
	        };
			
			
			tbLocksMW.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			locksMWModel = new DefaultTableModel();
			locksMWModel.addColumn("Get");
			locksMWModel.addColumn("Name");
			
			this.tbLocksMW.setModel(locksMWModel);
			this.tbLocksMW.getColumnModel().getColumn(0).setPreferredWidth(37);
			this.tbLocksMW.getColumnModel().getColumn(1).setPreferredWidth(128);
			this.tbLocksMW.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			spLockMW.setViewportView(tbLocksMW);
			
			txMWB = new JTextField();
			txMWB.setText("3");
			txMWB.setColumns(10);
			txMWB.setBounds(152, 127, 41, 20);
			panel_12.add(txMWB);
			
			JLabel label_19 = new JLabel("Min. Words:");
			label_19.setHorizontalAlignment(SwingConstants.RIGHT);
			label_19.setBounds(10, 130, 142, 14);
			panel_12.add(label_19);
			
			txMSPar = new JTextField();
			txMSPar.setText("1");
			txMSPar.setColumns(10);
			txMSPar.setBounds(902, 594, 37, 20);
			getContentPane().add(txMSPar);
			
			JLabel label_20 = new JLabel("Minimum Sentences:");
			label_20.setHorizontalAlignment(SwingConstants.RIGHT);
			label_20.setBounds(761, 597, 137, 14);
			getContentPane().add(label_20);
			
			txMWSent = new JTextField();
			txMWSent.setText("5");
			txMWSent.setColumns(10);
			txMWSent.setBounds(916, 665, 37, 20);
			getContentPane().add(txMWSent);
			
			JLabel label_21 = new JLabel("Minimum Words:");
			label_21.setHorizontalAlignment(SwingConstants.RIGHT);
			label_21.setBounds(775, 668, 137, 14);
			getContentPane().add(label_21);
			
			JLabel label_12 = new JLabel("Minimum Carrier Size:");
			label_12.setHorizontalAlignment(SwingConstants.RIGHT);
			label_12.setBounds(519, 668, 137, 14);
			getContentPane().add(label_12);
			
			txMinCarrSize = new JTextField();
			txMinCarrSize.setText("2");
			txMinCarrSize.setColumns(10);
			txMinCarrSize.setBounds(660, 665, 37, 20);
			getContentPane().add(txMinCarrSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JComboBox<String> getJCBTable(){
		if(this.cbTable == null){
			try {
				this.cbTable = new JComboBox<String>();
				cbTable.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						//JOptionPane.showMessageDialog(null, cbTable.getSelectedItem().toString());
					}
				});
				cbTable.setModel(new DefaultComboBoxModel<String>(new String[] {"TEX_ARTWORK","TEX_DOCUMENTS","TEX_NEWS","UNIVE_SYLLABUS"}));
				cbTable.setSelectedIndex(2);
				this.cbTable.setBounds(71, 8, 139, 20);
				//this.cbTable.setModel(new DefaultComboBoxModel(this.dbConnection.getTables().toArray()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.cbTable;
	}
	
	public int[][] getExtractedImg(){
		return this.recoveredInfo;
	}
	
	public int[][] getEnhancedImg(){
		return this.respRecoveredInfo;
	}
	
	
	
	
	
	public int getExtImgHeigh(){
		return imageHeight;
	}
	
	public int getExtImgWidth(){
		return imageWidth;
	}
}
