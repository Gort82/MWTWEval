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
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;

import java.awt.Color;

import javax.swing.JButton;

import wrd.ibw.da.DBConnection;
import wrd.ibw.utils.LockEntry;
import wrd.ibw.utils.StringPlayer;
import wrd.ibw.utils.Util;
import wrd.ibw.utils.WordCarrier;
import wrd.ibw.utils.jssim.SsimCalculator;
import wrd.ibw.wn.WNCaller;

import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import oracle.jdbc.OracleTypes;
import javax.swing.border.LineBorder;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

public class FrmEmbedWM extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	
	private DBConnection dbConnection = null;
	
	private JComboBox<String> cbTable = null;
	private JSpinner spAF = null;
	private JTextField tfFractTupl;
	private JTextField txSK;
	
	private TableColumn tmMSB = null; 
	private JTable tbCarriers = null;
	private JTable tbLocksNum = null;
	private JTable tbLocksSW = null;
	private JTable tbLocksMW = null;
	
	private DefaultTableModel carriersModel = null;
	private DefaultTableModel locksNumModel = null;
	private DefaultTableModel locksSWModel = null;
	private DefaultTableModel locksMWModel = null;
	
	private SsimCalculator mySSIMCalc = null;
	
	private JScrollPane spCarriers = null;
	private JScrollPane spLocks = null;
	private JScrollPane spLockSW = null;
	private JScrollPane spLockMW = null;
	
	private JComboBox cbMSB = null;
	
	private JFileChooser fileChooser;
	private File imageFile = null;
	private Image imageInfo = null;
	private JLabel lbImageViewer2 = new JLabel();
	
	
	private JButton btnStart = null;
	
	private int imageWidth = 0;
	private int imageHeight = 0;
	
	private Vector<Integer> imageVector = new Vector<Integer>();
	
	private Vector<Integer> directVector = new Vector<Integer>();
	
	private int embeddedInfo[][];
	private int acumInfo[][];
	
	private int originalImage[][];
	private JTextField txTotalPx;
	private JTextField txEmbeddedPx;
	private JLabel lbImgEmbedded;
	private JTextField txTotalTupl;
	private JTextField txTotalTuples;
	private JTextField txMTP;
	private JPanel pnResults;
	private JTextField txIntra;
	private JTextField txInter;
	private JTextField txTester;
	private JTextField txSentLength;
	private JTextField txMSB;
	
	private ButtonGroup bgSub = null;
	private ButtonGroup bgOD = null;
	private ButtonGroup bgOI = null;
	private ButtonGroup bgVerb = null;
	private ButtonGroup bgAdj = null;
	private ButtonGroup bgAdv = null;
	private ButtonGroup bgOM = null;
	private ButtonGroup bgMetrics = null;
	private ButtonGroup bgInclude = null;
	private JTextField textField_1;
	private JTextField txMWSent;
	private JTextField txLSB;
	private JCheckBox cbAbsVal = null;
	private JCheckBox cbIntPart = null;
	private JTextField txMWCat;
	private JTextField txBinaryLength;
	private JTextField txMWB;
	
	private WNCaller tempWNCaller = null;
	
	private JCheckBox rbWSDLesk = null;
	private JTextField txMSPar;
	
	private int recoveredInfo[][];
	private Vector<Integer> mayorityInfo[][];

	//protected AbstractButton rbMetrLIN;
	
	private JRadioButton rbMetrWUP = new JRadioButton("WUP");
	private JRadioButton rbMetrJCN = new JRadioButton("JCN");
	private JRadioButton rbMetrLCH = new JRadioButton("LCH");
	private JRadioButton rbMetrLIN = new JRadioButton("LIN");
	private JRadioButton rbMetrHSO = new JRadioButton("HSO");
	private JRadioButton rbMetrLESK = new JRadioButton("LESK");
	private JRadioButton rbMetrPATH = new JRadioButton("PATH");
	private JRadioButton rbMetrRES = new JRadioButton("RES");
	private JTextField txMaxDist;
	private JTextField txCF;
	private JTextField txSSIM;
	
	private JRadioButton rdbtnSolo1 = new JRadioButton("Solo 1");
	private JRadioButton rdbtnSolo0 = new JRadioButton("Solo 0");
	private JRadioButton rdbtnNada = new JRadioButton("Nada");
	private JRadioButton rdbtnMayor = new JRadioButton("Mayor");
	private JCheckBox cbLockCtrller = new JCheckBox("Lock Ctrller");
	private JTextField txMissP;
	private JTextField txErrRate;
	private JTextField txRateErr;
	private JTextField txMinCarrSize;
	
	public int[][] getImageMatrix(){
		return originalImage;
	}

	public FrmEmbedWM(DBConnection pDBConnection) {
		this.dbConnection = pDBConnection;
		try {
			tempWNCaller = new WNCaller();
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			this.setTitle("WM Embedding...");
			this.setSize(1175,629);
			this.getContentPane().setLayout(null);
			
			this.cbMSB = new JComboBox<>();
			
			this.bgSub = new ButtonGroup();
			this.bgOD = new ButtonGroup();
			this.bgOI = new ButtonGroup();
			this.bgVerb = new ButtonGroup();
			this.bgAdj = new ButtonGroup();
			this.bgAdv = new ButtonGroup();
			this.bgOM = new ButtonGroup();
			this.bgMetrics = new ButtonGroup();
			this.bgInclude = new ButtonGroup();
			
			fileChooser = new JFileChooser(System.getProperty("user.dir") + System.getProperty("file.separator") + "img");
			
			fileChooser.setAcceptAllFileFilterUsed(false);
			
			String[] suffices = ImageIO.getReaderFileSuffixes();
			
			for (int i = 0; i < suffices.length; i++) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image File (" + suffices[i] + ")", suffices[i]);
				if(suffices[i].equals("bmp"))
					fileChooser.addChoosableFileFilter(filter);
			}
			
			JLabel lblRelationToMark = new JLabel("Relation:");
			lblRelationToMark.setHorizontalAlignment(SwingConstants.RIGHT);
			lblRelationToMark.setBounds(10, 11, 58, 14);
			getContentPane().add(lblRelationToMark);
			getContentPane().add(getJCBTable());
		
			JLabel lblFractionOrRelations = new JLabel("TF:");
			lblFractionOrRelations.setHorizontalAlignment(SwingConstants.RIGHT);
			lblFractionOrRelations.setBounds(132, 228, 38, 14);
			getContentPane().add(lblFractionOrRelations);
		
			tfFractTupl = new JTextField();
			tfFractTupl.setText("40");
			tfFractTupl.setBounds(172, 225, 38, 20);
			getContentPane().add(tfFractTupl);
			tfFractTupl.setColumns(10);
			
			JLabel lblPrivateKey = new JLabel("SK:");
			lblPrivateKey.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPrivateKey.setBounds(10, 228, 78, 14);
			getContentPane().add(lblPrivateKey);
			
			txSK = new JTextField();
			txSK.setText("Secu48102304782K");
			txSK.setColumns(10);
			txSK.setBounds(91, 225, 47, 20);
			getContentPane().add(txSK);
			
			JPanel pnImageSelector = new JPanel();
			pnImageSelector.setLayout(null);
			pnImageSelector.setBorder(BorderFactory.createTitledBorder(null, "  WM Source  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			pnImageSelector.setBounds(10, 253, 204, 232);
			getContentPane().add(pnImageSelector);
			
			JLabel lbImageViewer = new JLabel();
			lbImageViewer.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			lbImageViewer.setBounds(10, 23, 184, 164);
			pnImageSelector.add(lbImageViewer);
		
			JButton btnOpen = new JButton("Select Image");
			btnOpen.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					int returnVal = fileChooser.showOpenDialog(FrmEmbedWM.this);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						imageFile = fileChooser.getSelectedFile();
			            try {
			            	imageVector.clear();
			            	imageInfo = ImageIO.read(imageFile);
			            	Image bImage = imageInfo.getScaledInstance(lbImageViewer.getWidth(), lbImageViewer.getHeight(), Image.SCALE_SMOOTH);
			            	ImageIcon imgIcon = new ImageIcon(bImage);
			            	lbImageViewer.setIcon(imgIcon);
			            	
			            	Util.defineImageArray(imageFile);
			            	
			            	originalImage = Util.getImageMatrix();
			            	
			            	imageWidth = Util.getImageWidth();
			            	imageHeight = Util.getImageHeight();
			            	
			            	txTotalPx.setText(String.valueOf(imageWidth * imageHeight));
			            	
			            	imageVector = Util.getImageVector(); //storing the image vector into the corresponding class field
			            	
			            	for (int i = 0; i < imageVector.size(); i++) {
			            		directVector.add(0);
							}
			            	
			            	btnStart.setEnabled(true);
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
			        }
				}
			});
			btnOpen.setBounds(10, 198, 184, 23);
			pnImageSelector.add(btnOpen);
		
			JPanel panel = new JPanel();
			panel.setLayout(null);
			panel.setBorder(BorderFactory.createTitledBorder(null, "  Carriers (Multi-Words)  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel.setBounds(10, 66, 204, 156);
			getContentPane().add(panel);
			
			JPanel panel_2 = new JPanel();
			panel_2.setLayout(null);
			panel_2.setBorder(BorderFactory.createTitledBorder(null, "  VPK Generation ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_2.setBounds(220, 9, 920, 226);
			getContentPane().add(panel_2);
			
			JPanel panel_3 = new JPanel();
			panel_3.setLayout(null);
			panel_3.setBorder(BorderFactory.createTitledBorder(null, "  Locks (Numerical) ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_3.setBounds(10, 22, 245, 194);
			panel_2.add(panel_3);
			
			JPanel panel_8 = new JPanel();
			panel_8.setLayout(null);
			panel_8.setBorder(BorderFactory.createTitledBorder(null, "  Locks (Light Text)  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_8.setBounds(265, 22, 205, 194);
			panel_2.add(panel_8);
			
			JPanel panel_9 = new JPanel();
			panel_9.setLayout(null);
			panel_9.setBorder(BorderFactory.createTitledBorder(null, "  Multi-Words  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_9.setBounds(480, 22, 204, 158);
			panel_2.add(panel_9);
			
			spCarriers = new JScrollPane();
			spCarriers.setBounds(10, 22, 183, 123);
			panel.add(spCarriers);
			
			spLocks = new JScrollPane();
			spLocks.setBounds(10, 22, 223, 129);
			panel_3.add(spLocks);
			
			spLockSW = new JScrollPane();
			spLockSW.setBounds(10, 22, 183, 129);
			panel_8.add(spLockSW);
			
			spLockMW = new JScrollPane();
			spLockMW.setBounds(10, 22, 183, 99);
			panel_9.add(spLockMW);
		
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
		
	        tbCarriers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			carriersModel = new DefaultTableModel();
			carriersModel.addColumn("Get");
			carriersModel.addColumn("Name");
			
			tbLocksNum.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			locksNumModel = new DefaultTableModel();
			locksNumModel.addColumn("Get");
			locksNumModel.addColumn("Name");
			locksNumModel.addColumn("MSB");
			
			tbLocksSW.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			locksSWModel = new DefaultTableModel();
			locksSWModel.addColumn("Get");
			locksSWModel.addColumn("Name");
			
			tbLocksMW.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			locksMWModel = new DefaultTableModel();
			locksMWModel.addColumn("Get");
			locksMWModel.addColumn("Name");
			
			this.tbCarriers.setModel(carriersModel);
			this.tbCarriers.getColumnModel().getColumn(0).setPreferredWidth(37);
			this.tbCarriers.getColumnModel().getColumn(1).setPreferredWidth(128);
			this.tbCarriers.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			this.tbLocksNum.setModel(locksNumModel);
			this.tbLocksNum.getColumnModel().getColumn(0).setPreferredWidth(37);
			this.tbLocksNum.getColumnModel().getColumn(1).setPreferredWidth(128);
			this.tbLocksNum.getColumnModel().getColumn(2).setPreferredWidth(40);
			this.tbLocksNum.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			this.tbLocksSW.setModel(locksSWModel);
			this.tbLocksSW.getColumnModel().getColumn(0).setPreferredWidth(37);
			this.tbLocksSW.getColumnModel().getColumn(1).setPreferredWidth(128);
			this.tbLocksSW.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			
			this.tbLocksMW.setModel(locksMWModel);
			this.tbLocksMW.getColumnModel().getColumn(0).setPreferredWidth(37);
			this.tbLocksMW.getColumnModel().getColumn(1).setPreferredWidth(128);
			this.tbLocksMW.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
			
			spCarriers.setViewportView(tbCarriers);
			spLocks.setViewportView(tbLocksNum);
			spLockSW.setViewportView(tbLocksSW);  
			spLockMW.setViewportView(tbLocksMW);
			
			txMWB = new JTextField();
			txMWB.setText("3");
			txMWB.setColumns(10);
			txMWB.setBounds(152, 127, 41, 20);
			panel_9.add(txMWB);
			
			JLabel lblMinWords = new JLabel("Min. Words:");
			lblMinWords.setHorizontalAlignment(SwingConstants.RIGHT);
			lblMinWords.setBounds(10, 130, 142, 14);
			panel_9.add(lblMinWords);
			
			JLabel lblMaximumWords = new JLabel("Max. Words:");
			lblMaximumWords.setHorizontalAlignment(SwingConstants.RIGHT);
			lblMaximumWords.setBounds(10, 160, 142, 14);
			panel_8.add(lblMaximumWords);
			
			txMWCat = new JTextField();
			txMWCat.setText("3");
			txMWCat.setColumns(10);
			txMWCat.setBounds(152, 157, 41, 20);
			panel_8.add(txMWCat);
			
			txLSB = new JTextField();
			txLSB.setBounds(104, 162, 28, 20);
			panel_3.add(txLSB);
			txLSB.setText("1");
			txLSB.setColumns(10);
			
			JLabel lblLsb = new JLabel("LSB:");
			lblLsb.setBounds(70, 165, 32, 14);
			panel_3.add(lblLsb);
			lblLsb.setHorizontalAlignment(SwingConstants.RIGHT);
			
			txMSB = new JTextField();
			txMSB.setBounds(43, 162, 28, 20);
			panel_3.add(txMSB);
			txMSB.setText("3");
			txMSB.setColumns(10);
			
			JLabel lblMsb = new JLabel("MSB:");
			lblMsb.setBounds(10, 165, 32, 14);
			panel_3.add(lblMsb);
			lblMsb.setHorizontalAlignment(SwingConstants.RIGHT);
			
			this.cbAbsVal = new JCheckBox("Abs.");
			cbAbsVal.setBounds(138, 158, 47, 23);
			panel_3.add(cbAbsVal);
			
			cbAbsVal.setEnabled(false);
			cbAbsVal.setSelected(true);
			this.cbIntPart = new JCheckBox("Int.");
			cbIntPart.setBounds(187, 158, 52, 23);
			panel_3.add(cbIntPart);
			
			cbIntPart.setSelected(true);
			cbIntPart.setEnabled(false);
			
			JPanel panel_6 = new JPanel();
			panel_6.setLayout(null);
			panel_6.setBorder(BorderFactory.createTitledBorder(null, "  Role of each Word  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_6.setBounds(694, 22, 212, 158);
			panel_2.add(panel_6);
			
			JLabel lblLCE = new JLabel("L       C       E");
			lblLCE.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
			lblLCE.setHorizontalAlignment(SwingConstants.CENTER);
			lblLCE.setBounds(10, 28, 92, 14);
			panel_6.add(lblLCE);
			
			JRadioButton rbSubL = new JRadioButton("");
			rbSubL.setBounds(17, 46, 27, 23);
			panel_6.add(rbSubL);
			
			JRadioButton rbSubC = new JRadioButton("");
			rbSubC.setSelected(true);
			rbSubC.setBounds(46, 46, 27, 23);
			panel_6.add(rbSubC);
			
			JRadioButton rbSubE = new JRadioButton(" Noun");
			rbSubE.setBounds(75, 46, 118, 23);
			panel_6.add(rbSubE);
			
			bgSub.add(rbSubL);
			bgSub.add(rbSubC);
			bgSub.add(rbSubE);
			
			JRadioButton rbVerbE = new JRadioButton(" Verbs");
			rbVerbE.setBounds(75, 64, 124, 23);
			panel_6.add(rbVerbE);
			
			JRadioButton rbVerbC = new JRadioButton("");
			rbVerbC.setBounds(46, 64, 27, 23);
			panel_6.add(rbVerbC);
			
			JRadioButton rbVerbL = new JRadioButton("");
			rbVerbL.setSelected(true);
			rbVerbL.setBounds(17, 64, 27, 23);
			panel_6.add(rbVerbL);
			
			this.bgVerb.add(rbVerbE);
			this.bgVerb.add(rbVerbC);
			this.bgVerb.add(rbVerbL);
			
			JRadioButton rbAdjE = new JRadioButton("Adjective");
			rbAdjE.setBounds(75, 82, 124, 23);
			panel_6.add(rbAdjE);
			
			JRadioButton rbAdjC = new JRadioButton("");
			rbAdjC.setBounds(46, 82, 27, 23);
			panel_6.add(rbAdjC);
			
			JRadioButton rbAdjL = new JRadioButton("");
			rbAdjL.setSelected(true);
			rbAdjL.setBounds(17, 82, 27, 23);
			panel_6.add(rbAdjL);
			
			this.bgAdj.add(rbAdjE);
			this.bgAdj.add(rbAdjC);
			this.bgAdj.add(rbAdjL);
			
			JRadioButton rbAdvE = new JRadioButton("Adverb");
			rbAdvE.setSelected(true);
			rbAdvE.setBounds(75, 100, 124, 23);
			panel_6.add(rbAdvE);
			
			JRadioButton rbAdvC = new JRadioButton("");
			rbAdvC.setBounds(46, 100, 27, 23);
			panel_6.add(rbAdvC);
			
			JRadioButton rbAdvL = new JRadioButton("");
			rbAdvL.setBounds(17, 100, 27, 23);
			panel_6.add(rbAdvL);
			
			this.bgAdv.add(rbAdvE);
			this.bgAdv.add(rbAdvC);
			this.bgAdv.add(rbAdvL);
			
			JRadioButton rbOME = new JRadioButton(" Other Modifiers");
			rbOME.setBounds(75, 118, 124, 23);
			panel_6.add(rbOME);
			
			JRadioButton rbOML = new JRadioButton("");
			rbOML.setSelected(true);
			rbOML.setBounds(17, 118, 27, 23);
			panel_6.add(rbOML);
			
			JRadioButton rbOMC = new JRadioButton("");
			rbOMC.setEnabled(false);
			rbOMC.setBounds(46, 118, 27, 23);
			panel_6.add(rbOMC);
			
			this.bgOM.add(rbOME);
			this.bgOM.add(rbOML);
			this.bgOM.add(rbOMC);
			
			JButton btnGenerateVpk = new JButton("VPK");
			btnGenerateVpk.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					CallableStatement csData = null;
					//CallableStatement csVPK = null;
					
					ResultSet rsData = null;
					String attrBinary = "";
					String stringBinary = "";
					String tuplNumBinary = "";
					String bruteStringValue = "";
					int intValue = 0;
					Float floatValue = new Float(0);
					int absValue = 0;
					
					Vector<LockEntry> lockNum = new Vector<LockEntry>();
					Vector<String> lockSW = new Vector<String>();
					Vector<String> lockMW = new Vector<String>();
					
					boolean considerRest = false;
					Vector<POS> pWordRoles = new Vector<POS>();
					if (rbOML.isSelected()) {
						considerRest = true;
						
						if (rbSubC.isSelected()||rbSubE.isSelected()) {
							pWordRoles.addElement(POS.NOUN);
						}
						if (rbVerbC.isSelected()||rbVerbE.isSelected()) {
							pWordRoles.addElement(POS.VERB);
						}
						if (rbAdjC.isSelected()||rbAdjE.isSelected()) {
							pWordRoles.addElement(POS.ADJECTIVE);
						}
						if (rbAdvC.isSelected()||rbAdvE.isSelected()) {
							pWordRoles.addElement(POS.ADVERB);
						}
						
					}else{
						considerRest = false;
						if (rbSubL.isSelected()) {
							pWordRoles.addElement(POS.NOUN);
						}
						if (rbVerbL.isSelected()) {
							pWordRoles.addElement(POS.VERB);
						}
						if (rbAdjL.isSelected()) {
							pWordRoles.addElement(POS.ADJECTIVE);
						}
						if (rbAdvL.isSelected()) {
							pWordRoles.addElement(POS.ADVERB);
						}
					}
					
					for (int i = 0; i < tbLocksNum.getRowCount(); i++) {
						if(tbLocksNum.getModel().getValueAt(i, 0).equals(true)){
							if (tbLocksNum.getModel().getValueAt(i, 2).equals("ALL")) {
								lockNum.add(new LockEntry(tbLocksNum.getModel().getValueAt(i, 1).toString(), 0));
							}else{
								lockNum.add(new LockEntry(tbLocksNum.getModel().getValueAt(i, 1).toString(), Integer.valueOf(tbLocksNum.getModel().getValueAt(i, 2).toString())));
							}
						}
					}
					
					for (int j = 0; j < tbLocksSW.getRowCount(); j++) {
						if(tbLocksSW.getModel().getValueAt(j, 0).equals(true)){
							lockSW.addElement(tbLocksSW.getValueAt(j, 1).toString());
						}
					}
					
					for (int j = 0; j < tbLocksMW.getRowCount(); j++) {
						if(tbLocksMW.getModel().getValueAt(j, 0).equals(true)){
							lockMW.addElement(tbLocksMW.getValueAt(j, 1).toString());
						}
					}
					
					Calendar cal = Calendar.getInstance();
			    	cal.getTime();
			    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			    	System.out.println("-----------------------------------------------");
			    	System.out.println("VPK GENERATION STARTED AT: " + sdf.format(cal.getTime()) );
					
					try {
						csData = dbConnection.getConnection().prepareCall ("{ ? = call RTW_GET_ALLDATA (?)}");
						csData.registerOutParameter (1, OracleTypes.CURSOR);
						csData.setString (2,cbTable.getSelectedItem().toString());
						csData.execute ();
						rsData = (ResultSet)csData.getObject(1);
						
						//if(csVPK == null){
						//	csVPK = dbConnection.getConnection().prepareCall ("{ call RTW_GENERATE_VPK (?,?,?,?)}");
	    				//}
						
						while (rsData.next ()){
							tuplNumBinary = "";
							for (int j = 0; j < lockNum.size(); j++) { //LOCK NUMERIC ATTRIBUTES 
								
								if (cbIntPart.isSelected()) {
									floatValue = rsData.getFloat(lockNum.elementAt(j).getLockName());
									intValue = floatValue.intValue();
								}
								
								if (cbAbsVal.isSelected()) {
									absValue = Math.abs(intValue);
								}
								
								attrBinary = Integer.toBinaryString(absValue);
								
								if (lockNum.elementAt(j).getLockLength() > 0) {
									if(lockNum.elementAt(j).getLockLength() + Integer.valueOf(txLSB.getText()) <= attrBinary.length()){
										//binaryString = binaryString.substring(0, binaryString.length() - lockNum.elementAt(j).getLockLength());
										attrBinary = attrBinary.substring(0, lockNum.elementAt(j).getLockLength());
									} else {
										attrBinary = "";
									}
								}  
								
								tuplNumBinary = tuplNumBinary + attrBinary;
								
								//System.out.println(rsData.getInt("ID") + "Element: " + lockNum.elementAt(j).getLockName() + " Value: " + rsData.getInt(lockNum.elementAt(j).getLockName()) + " Binary: " + attrBinary);
							}
							
							//System.out.println(tuplNumBinary);
							for (int j = 0; j < lockSW.size(); j++) { //LOCK ELEMENTS FROM LIGHT-WORD ATTRIBUTES: POTENTIAL CATEGORICAL DETECTION
								stringBinary = Util.getBinary(rsData.getString(lockSW.elementAt(j)));
								tuplNumBinary = tuplNumBinary + stringBinary;
							}
							
							
							for (int j = 0; j < lockMW.size(); j++) { //LOCK ELEMENTS FROM HEAVY-WORD ATTRIBUTES: POTENTIAL CARRIERS
								bruteStringValue = rsData.getString(lockMW.elementAt(j)); //NO NEED TO PARAGRAP FRAGMENTATION HERE
								stringBinary = Util.getLockStream(tempWNCaller, bruteStringValue, pWordRoles, considerRest, 0);
								tuplNumBinary = tuplNumBinary + stringBinary;
							}
							
							
							/*csVPK.setString(1, cbTable.getSelectedItem().toString());
							csVPK.setInt(2, rsData.getInt("ID"));
							csVPK.setInt(3, Util.parseString(tuplNumBinary,Integer.valueOf(txBinaryLength.getText())));
							csVPK.setString(4, txSK.getText());
							csVPK.execute ();*/
						}
						
						Calendar cal1 = Calendar.getInstance();
				    	cal1.getTime();
				    	SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
				    	System.out.println ("VPK GENERATION COMPLETED AT: "+sdf1.format(cal1.getTime()));
				    	System.out.println("-----------------------------------------------");
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			});
			btnGenerateVpk.setBounds(694, 193, 99, 23);
			panel_2.add(btnGenerateVpk);
			
			txBinaryLength = new JTextField();
			txBinaryLength.setText("20");
			txBinaryLength.setColumns(10);
			txBinaryLength.setBounds(622, 194, 32, 20);
			panel_2.add(txBinaryLength);
			
			JLabel lblStringBinaryLength = new JLabel("Binary Length:");
			lblStringBinaryLength.setHorizontalAlignment(SwingConstants.RIGHT);
			lblStringBinaryLength.setBounds(480, 197, 141, 14);
			panel_2.add(lblStringBinaryLength);
			
			JButton btnGenerateTer = new JButton("TER");
			btnGenerateTer.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					CallableStatement csTF = null;
					
					Calendar cal = Calendar.getInstance();
			    	cal.getTime();
			    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			    	System.out.println("-----------------------------------------------");
			    	System.out.println("EMBEDDING FACTOR GENERATION STARTED AT: " + sdf.format(cal.getTime()) );
					try {
						if(csTF == null){
							csTF = dbConnection.getConnection().prepareCall ("{ call RTW_GENERATE_CONSFACTOR (?,?)}");
	    				}
						
						csTF.setString(1, cbTable.getSelectedItem().toString());
						csTF.setInt(2, Integer.valueOf(tfFractTupl.getText()));
						csTF.execute ();
						
						Calendar cal1 = Calendar.getInstance();
				    	cal1.getTime();
				    	SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
				    	System.out.println ("EMBEDDING FACTOR GENERATION COMPLETED AT: "+sdf1.format(cal1.getTime()));
				    	System.out.println("-----------------------------------------------");
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			btnGenerateTer.setBounds(803, 193, 99, 23);
			panel_2.add(btnGenerateTer);
			
		btnStart = new JButton("Embed");
		btnStart.setEnabled(false);
		btnStart.setBounds(20, 520, 89, 23);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				int usedWords = 0;
				int sameWords = 0;
				
				//////////////////for (int ii = 0; ii < 6; ii++) {///////////////////
				
				boolean contains = false;
				boolean hasUpperCase = false;
				boolean contnumb = false;
				
				boolean synUpperCase = false;
				
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
				
				//int simMetr = 0;
				String simCap = "";
				
				/*if (rbMetrWUP.isSelected()) {simMetr = 3; simCap = "WUP";}
				if (rbMetrJCN.isSelected()) {simMetr = 5; simCap = "JCN";}
				if (rbMetrLCH.isSelected()) {simMetr = 1; simCap = "LCH";}
				if (rbMetrLIN.isSelected()) {simMetr = 6; simCap = "LIN";}
				if (rbMetrRES.isSelected()) {simMetr = 4; simCap = "RES";}
				if (rbMetrPATH.isSelected()){simMetr = 7; simCap = "PATH";} 
				if (rbMetrLESK.isSelected()){simMetr = 2; simCap = "LESK";}
				if (rbMetrHSO.isSelected()) {simMetr = 0; simCap = "HSO";}*/
				
				String bruteParagraph = "";
				Synset synsetWord = null;
				Vector<String> substitutes = new Vector<String>();
				
				String lockStream = "";
				String newWord = "";
				int lockSup = 0;
				Long sentKey = new Long(0);
				int wsdMethod = 0;
				
				int height_pos = 0;
				int width_pos = 0;
				int image_element = 0;
				boolean embedded = false;
				
				Vector<Integer> minurcarr = new Vector<Integer>();
				
				Vector<Integer> metrStat = new Vector<Integer>();
				Vector<Double> metrFlags = new Vector<Double>();
				
				//int chaos_sim = 0;
				
				
				if (rbWSDLesk.isSelected()) {
					wsdMethod = 1;
				}else{
					wsdMethod = 2;
				}
				
				txEmbeddedPx.setText("0");
				lbImgEmbedded.setBackground(Color.WHITE);
					
				embeddedInfo = new int[imageHeight][imageWidth];
				acumInfo = new int[imageHeight][imageWidth];
				for (int i = 0; i < imageWidth; i++) {
					for (int j = 0; j < imageHeight; j++) {
						embeddedInfo[j][i] = -1;
						acumInfo[j][i] = 0;
					}
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
				CallableStatement csUpdater = null;
			///// 21.09.21  CallableStatement csCheckUpdater = null;
				CallableStatement csLoadCheck = null;
				
				ResultSet rsTuples = null;
				ResultSet rsAttrVK = null;
				
				int no_measurements = 0;
				
				//Vector<Double> myMetrics = new Vector<Double>();
				//myMetrics.add(new Double(0));
				//myMetrics.add(new Double(0));
				//myMetrics.add(new Double(0));
				//myMetrics.add(new Double(0));
				/*myMetrics.add(new Double(0));
				myMetrics.add(new Double(0));
				myMetrics.add(new Double(0));
				myMetrics.add(new Double(0));*/
				
				//double myMetrics = new Double(0);
				
				boolean final_dot = false;
				
				int MAX_COTR = 0;
				
				int MetrCounter = 0;
				int MetrCounter2 = 0;
				int TuplCounter = 0;
				
				String embListNounsB = null;       ////////////////////////////////////
				String embLockStreamB = null;      ////////////////////////////////////
				String embSenseKeyB = null;        ////////////////////////////////////
				String embNounB = null;            ////////////////////////////////////
				String embSynonymSetB = null;      ////////////////////////////////////
				String embMarkB = null;            ////////////////////////////////////
				String embUsedSynonymB = null;     ////////////////////////////////////
				
				//imageVector.elementAt(imageWidth * (height_pos) + width_pos);
				/*for (int i = 0; i < imageVector.size(); i++) {
					System.out.println(imageVector.get(i));
				}*/
				
				double valMetr = 0;
					
				String[] excluded = {"a", "at", "an", "as", "ar", "are", "be", "can", "coming", "come", "do", "going", "have", "in", "it", "might", "more", "now", "or", "one", "over", "out", "say", "us", "so", "today", "will"};
				
				String num = "";
				
				int MMM = 0;
				
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
					csSVK.setString(2, txSK.getText());
					
					csUpdater = dbConnection.getConnection().prepareCall ("{call RTW_UPDATE_ATTR (?,?,?,?)}");
					csUpdater.setString(1, cbTable.getSelectedItem().toString());
					
					//csCheckUpdater = dbConnection.getConnection().prepareCall ("{call RTW_EMB_BCHECK (?,?,?,?,?,?,?,?)}");
				///// 21.09.21  csCheckUpdater = dbConnection.getConnection().prepareCall ("{call RTW_EMB_WORD (?,?,?,?,?)}");
					
					csLoadCheck = dbConnection.getConnection().prepareCall ("{ ? = call RTW_GET_EMB_BCHECK (?,?)}");
					csLoadCheck.registerOutParameter (1, OracleTypes.VARCHAR);
					csLoadCheck.setString(2, cbTable.getSelectedItem().toString());
					
					
					int val = 0;
					
					while (rsTuples.next ()){
						
						val++;
								
						System.out.println(rsTuples.getInt ("ID"));
						
						//csLoadCheck.setString(2, "TEX_DOCUMENTS");
						csLoadCheck.setInt(3, rsTuples.getInt ("ID"));
						
						try {
							
							if(rsTuples.getInt ("TUPL_FACTOR")==0){
								TuplCounter = 1;
								for (int i = 0; i < carrierAttr.size(); i++) {
									//csUpdater.setString (2, carrierAttr.elementAt(i));
									//csUpdater.setString (2, "EMB_HEADLINE");
									/////csUpdater.setString (2, "EMB_TEXT");
									csUpdater.setString (2, "EMB_" + carrierAttr.elementAt(i));
									
									
									
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
									
									//check content in the value 
									if(bruteParagraph != null){
										
										
										//System.out.println("PÁRRAFO DE LA TUPLA -- " + rsTuples.getInt ("ID") + " -- ES -- " + bruteParagraph);
									
										//String sentences[] = Util.splitParagraph(bruteParagraph, rsTuples.getInt ("ID"), carrierAttr.elementAt(i));
										Vector<String> sentences = Util.splitParagraph(bruteParagraph);
										
										if(sentences.size() >= Integer.valueOf(txMSPar.getText())){ //check the number of sentences per paragraph
									
											for (int j = 0; j < sentences.size(); j++) {
												
												embListNounsB = "";  ///////////////////
												embLockStreamB = ""; ///////////////////
												embSenseKeyB = "";   /////////////////// 
												embNounB = "";       ///////////////////  
												embSynonymSetB = ""; /////////////////// 
												embMarkB = "";       ///////////////////
												embUsedSynonymB = ""; ///////////////////
												
												String[] words = sentences.elementAt(j).split("\\s+");
												
												final_dot = false;
												
												if (words[words.length-1].substring(words[words.length-1].length()-1).equals(".")) {
													words[words.length-1] = words[words.length-1].substring(0, words[words.length-1].length()-1);
													sentences.set(j, sentences.elementAt(j).substring(0,sentences.elementAt(j).length()-1));
													final_dot = true;
												}
												
												//System.out.println("SENTENSE -- " + sentences[j]);
												
												if (words.length >= Integer.valueOf(txMWSent.getText())) { //check the number of words per sentence
													
													//if(rsTuples.getInt ("ID") == 1){
														lockStream = Util.getLockStream(tempWNCaller, sentences.elementAt(j), pWordRoles, considerRest, rsTuples.getInt ("ID"));
														lockSup = Util.parseString(lockStream,Integer.valueOf(txBinaryLength.getText()));
														
														embLockStreamB = Integer.toString(lockSup);  ////////////////////////
													//}
													
													if(csSVK.isClosed()){
														csSVK = dbConnection.getConnection().prepareCall ("{ ? = call RTW_GENERATE_AVK (?,?,?,?,?,?)}");
														csSVK.registerOutParameter (1, OracleTypes.CURSOR);
														csSVK.setString(2, txSK.getText());
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
														
														/*System.out.println("POS ALTO: " + height_pos);
														System.out.println("POS ANCHO: " + width_pos);*/
														
													}
													
													embSenseKeyB = Long.toString(sentKey);    //////////////////////////////////
														 
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
																		//excluir preposiciones y sustantivos propios
																		carrierForSentence.add(new WordCarrier(words[k], pWordCarriers.elementAt(l), k));
																		embListNounsB = embListNounsB + "|" + words[k]; ////////////////
																	}
																}
															} catch (Exception e) {
																e.printStackTrace();
															}
														}
													}
													
													
													if (carrierForSentence.size()>0) {
														//get the word to be changed
														currentCarrier = carrierForSentence.elementAt((int)(sentKey % carrierForSentence.size())); 
														
														
														embNounB = currentCarrier.getWord();  ///////////////////////////////////
														
														if(embNounB.length()<=2)
															minurcarr.add(rsTuples.getInt ("ID"));
														
														//////////////////////////////////////////////////////////////////////////
														//////////////////////////////////////////////////////////////////////////
														//////////////////////////////////////////////////////////////////////////
														//////////////////////////////////////////////////////////////////////////
														//get the synset of the word given the sentence, the word and the algorithm
														synsetWord = tempWNCaller.getSynsetWord(currentCarrier, sentences.elementAt(j), wsdMethod);
														
														//System.out.println("ORIGINAL SENTENCE:" + sentences[j]);
														
														//System.out.println("TYPE OF CARRIER:" + currentCarrier.getRole());
														
														//System.out.println("CARRIER:" + currentCarrier.getWord());
														
														//SDFSA
														
														//synsetWord = tempWNCaller.getSynsetWord(currentCarrier, sentences[j], 1);
														//System.out.println("Synte1:" + synsetWord);
														//synsetWord = tempWNCaller.getSynsetWord(currentCarrier, sentences[j], 2);
														//System.out.println("Synte2:" + synsetWord);
														
														///////////get the set of synonyms of the word according to the sense 
														/////substitutes = tempWNCaller.getSubsSet(currentCarrier, synsetWord); 
													
														//chaos_sim = sentKey + ;
														
														if(synsetWord != null){
															substitutes = tempWNCaller.getSubsSet(synsetWord,Integer.valueOf(txMinCarrSize.getText()));
															
															//if(rsTuples.getInt ("ID") == 30){
																//System.out.println("SYYYYYYYYYYYYYYYYYY:" + substitutes);
															//}
															
															int count = 0;
															while (!synUpperCase && count < substitutes.size()) {
																if(!substitutes.elementAt(count).equals(substitutes.elementAt(count).toLowerCase()))
																	synUpperCase = true;
																count++;
															} 

															if(!synUpperCase){
																				if((imageWidth * (height_pos) + width_pos) > 0  ){
																					image_element = imageVector.elementAt(imageWidth * (height_pos) + width_pos);
																				
																					//select new word as mark embedding signal
																					if (substitutes.size()>1) {
																						
																						for (int m = 0; m < substitutes.size(); m++) {                               ///////////////////////////
																							embSynonymSetB = embSynonymSetB + "|" + substitutes.elementAt(m);     ///////////////////////////
																						}                                                                            ///////////////////////////  
																						
																						embMarkB = String.valueOf(image_element); ///////////////////////////////////// 
																						
																						if (image_element == 1) {
																							newWord = substitutes.elementAt(0);
																						}else{
																							newWord = substitutes.elementAt(1);
																						}
																						
																						if (embNounB.equals(newWord)) {
																							sameWords++;
																						}
																						
																						usedWords++;
																						
																						
																						embUsedSynonymB = newWord; ///////////////////////////////////////
																								
																						//perform the replacement of the word in the sentence
																						words[currentCarrier.getIndex()] = newWord;
																						sentences.set(j, Util.formSentence(words));
																						
																						if (final_dot) {
																							sentences.set(j, sentences.elementAt(j) + ".");
																						}
																					
																						//perform the replacement of the sentence in the paragraph
																		            	
																						//if(rsTuples.getInt ("ID") == 1){
																							bruteParagraph = Util.updateParagraph(bruteParagraph, sentences.elementAt(j), j);
																						//}
																						
																		            	//perform the update of the attribute value
																						csUpdater.setString (3, bruteParagraph);
																						csUpdater.setInt (4, rsTuples.getInt ("ID"));
																						
																						
																						
																						////valMetr = Util.getSimilarityValue(currentCarrier.getWord(), newWord, 6);
																						
																						
																						//for (int k = 0; k < myMetrics.size(); k++) {
																						//	myMetrics.set(k, myMetrics.get(k) + tempWNCaller.getPairScore(currentCarrier.getWord(), newWord, k));
																						//}
																						//myMetrics = myMetrics + tempWNCaller.getPairScore(currentCarrier.getWord(), newWord, MMM);
																						no_measurements++;
																						//valMetr = tempWNCaller.getPairScore(currentCarrier.getWord(), newWord, simMetr);
																						
																						csLoadCheck.execute ();
																						if(csLoadCheck.getString(1) != null)
																							num =  csLoadCheck.getString(1) + "|";
																						
																						/*csCheckUpdater.setString (1, embListNounsB);
																						csCheckUpdater.setString (2, embLockStreamB);
																						csCheckUpdater.setString (3, num + embSenseKeyB);
																						csCheckUpdater.setString (4, embNounB);
																						csCheckUpdater.setString (5, embSynonymSetB);
																						csCheckUpdater.setString (6, embMarkB);
																						csCheckUpdater.setString (7, embUsedSynonymB);
																						csCheckUpdater.setInt (8, rsTuples.getInt ("ID"));*/
																						
																					///// 21.09.21  csCheckUpdater.setInt (1, rsTuples.getInt ("ID"));
																					///// 21.09.21  csCheckUpdater.setLong (2, sentKey);
																					///// 21.09.21  csCheckUpdater.setString (3, newWord);
																					///// 21.09.21  csCheckUpdater.setLong (4, synsetWord.getOffset());
																					///// 21.09.21  csCheckUpdater.setString (5, embMarkB);
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
																						}*/
																						
																						/*System.out.println("SENTENCE KEY" + sentKey);
														                                System.out.println("HEIGHT POS: " + height_pos);
																						System.out.println("WIDTH POS: " + width_pos); 
														
																						System.out.println("MARK------------------------------------------");
																						System.out.println(image_element);
																						
																						System.out.println("SELECTED WORD---------------------------------");
																						System.out.println(newWord);
																						
																						System.out.println("SENSE KEY-------------------------------------");
																						System.out.println(sentKey);*/
																						
																						
																						
																						
																						
																						
																						
																						if(!currentCarrier.getWord().equals(newWord)){
																							//System.out.println("OLD WORD:" + currentCarrier.getWord());
																							//System.out.println("NEW WORD:" + newWord);
																						}
																						
																						//if(rsTuples.getInt ("ID") == 42)
																							//System.out.println("MARK:" + embMarkB);
																						
																						csUpdater.execute (); 
																						embedded = true;
																						MetrCounter++;
																						
																					/*	if (rdbtnNada.isSelected()) {
																							csUpdater.execute (); ///////////
																							embedded = true;
																						} else{
																							if (rdbtnSolo1.isSelected()) {
																								if (valMetr == 1) {
																									csUpdater.execute (); ///////////
																									embedded = true;
																									MetrCounter++;
																								}
																							}else{
																								if (rdbtnSolo0.isSelected()) {
																									if (valMetr == 0) {
																										csUpdater.execute (); ///////////
																										embedded = true;
																										MetrCounter++;
																									}
																								}else{
																									if (rdbtnMayor.isSelected()) {
																										if (Double.valueOf(txMaxDist.getText()) <= valMetr) {
																											csUpdater.execute (); ///////////
																											embedded = true;
																											MetrCounter++;
																										}
																									}
																								}
																							}
																						}
																						
																						*/
																						//System.out.println(simCap + ": " + valMetr);
																						
																						
																						if (metrFlags.contains(valMetr)) {
																							metrStat.setElementAt(metrStat.elementAt(metrFlags.indexOf(valMetr)) + 1, metrFlags.indexOf(valMetr));
																						} else {
																							metrFlags.add(valMetr);
																							metrStat.addElement(new Integer(1));
																						}
																						
																						valMetr = 0;
																					}
																				}
								
																				if (embedded) {
											    									embeddedInfo[height_pos][width_pos] = image_element;
											    									acumInfo[height_pos][width_pos]++;
											    									image_element = -1;
											    									embedded = false;
																				}
															}
														}
														carrierForSentence.clear();
													}
													else
														if (final_dot) {
															sentences.set(j, sentences.elementAt(j) + ".");
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
				    	}finally {
				    		//csUpdater.close();
				    		if(csCarriers != null)
				    			csCarriers.close();
				    		if(csSVK != null)
				    			csSVK.close();
				    		//rsAttrVK.close();
				    	}
					}
					
					for (int k = 0; k < metrFlags.size(); k++) {
						System.out.println(metrFlags.get(k) + ": " + metrStat.get(k));
					}
					
					metrFlags.clear();
					metrStat.clear();
					
					System.out.println("MENORESSSS-----------------------------");
					
						System.out.println(minurcarr);
					
					int no_embedd = 0;
					
					int acum = 0;
					
					double mean_acum = 0;
					double std_acum = 0;
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						acum = acum + acumInfo[j][i];
	 						if(embeddedInfo[j][i] != -1)
	 							no_embedd++;
	 					}
	 				}
					
					
					mean_acum = acum/(imageWidth*imageHeight);
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						std_acum += Math.pow((acumInfo[j][i] - mean_acum),2);
	 					}
	 				}
					
					std_acum = Math.sqrt(std_acum / (imageWidth*imageHeight));
					
					
					System.out.println("THE MEAN IS ------>>> " +  mean_acum);
					System.out.println("THE STD_DEV IS --->>> " + std_acum);
					
					System.out.println("*********** VALORES DE LAS METRICAS *********");
					
					//String[] captions = {"WUP","JCN","LCH","LIN","RES","PATH","LESK","HSO"};
					
					                          //String[] captions = {"RES","PATH","LESK","HSO"};
					                          
					                          //String[] captions = {"HSO"};
					                          
					                          
					
					//for (int i = 0; i < myMetrics.size(); i++) {
					//	System.out.println(captions[i] + "--> " + String.valueOf(myMetrics.get(i)));
					//}
					
					//System.out.println("VALOR METRICA --->>> " + myMetrics);
					
					System.out.println("CANTIDAD DE MEDICIONES --->>> " + no_measurements);
					
					
					BufferedImage orig_img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
					

					for (int i = 0; i < imageWidth; i++) {
						for (int j = 0; j < imageHeight; j++) {
							if(originalImage[j][i] == 1){
								orig_img.setRGB(i, j, Color.BLACK.getRGB());
								}else	if(originalImage[j][i] == 0){
									orig_img.setRGB(i, j, Color.WHITE.getRGB());
										} else {
											orig_img.setRGB(i, j, Color.RED.getRGB());
										}
							}
						}
					
					ImageIO.write(orig_img, "bmp", new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"incrusted.bmp"));
					//JOptionPane.showMessageDialog(null, "The extracted image was successfully saved...");
					

					
					//BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_BINARY);
					BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
					
					for (int i = 0; i < imageWidth; i++) {
						for (int j = 0; j < imageHeight; j++) {
							if(embeddedInfo[j][i] == 1){
								img.setRGB(i, j, Color.BLACK.getRGB());
	 						}else	if(embeddedInfo[j][i] == 0){
	 									img.setRGB(i, j, Color.WHITE.getRGB());
	 								} else {
	 									img.setRGB(i, j, Color.RED.getRGB());
	 								}
 						}
	 				}
					
					
					
					
					
			
					Image scaledInstance = img.getScaledInstance(lbImgEmbedded.getWidth(), lbImgEmbedded.getHeight(), Image.SCALE_DEFAULT);
					ImageIcon imageIcon = new ImageIcon(scaledInstance);
					lbImgEmbedded.setIcon(imageIcon);
					
					txEmbeddedPx.setText(String.valueOf(no_embedd));
					txTotalTupl.setText(String.valueOf(dbConnection.getNoRows(cbTable.getSelectedItem().toString(), txSK.getText(), Integer.valueOf(tfFractTupl.getText()))));
					//txTotalTupl.setText(String.valueOf(total_tuplas_marcadas));
					
					DecimalFormat df = new DecimalFormat("##.##");
					df.setRoundingMode(RoundingMode.DOWN);
					
					txTotalTuples.setText(String.valueOf(dbConnection.getAllRows(cbTable.getSelectedItem().toString())));
					txMTP.setText(String.valueOf(df.format(Float.valueOf(txTotalTupl.getText())*100/Float.valueOf(txTotalTuples.getText()))));
					
					//System.out.println("-----------------------------------------------");
	        		
			    	//ENDING TIME
				    Calendar cal1 = Calendar.getInstance();
			    	cal1.getTime();
			    	SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
			    	System.out.println ("PROCESS COMPLETED AT: "+sdf1.format(cal1.getTime()));
			    	System.out.println("-----------------------------------------------");
			    	Toolkit.getDefaultToolkit().beep();
			    	
			    	System.out.println("Final No. Embedde Marks: " + MetrCounter);
			    	
			    	System.out.println("PALABRAS QUE NO CAMBIARON: " + sameWords);
			    	System.out.println("TOTAL DE PALABRAS USADAS: " + usedWords);
				
			    	
			    	///////JOptionPane.showMessageDialog(null, "Embedding Process completed...");
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					try {
						rsAttrVK.close();
						rsTuples.close();
						csTuples.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				
				///////////////////////}//////////////////
				
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
				
				
		}});
		getContentPane().add(btnStart);
		
		pnResults = new JPanel();
		pnResults.setLayout(null);
		pnResults.setBorder(BorderFactory.createTitledBorder(null, "  Results of the Embedding Process ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnResults.setBounds(611, 234, 329, 294);
		getContentPane().add(pnResults);
		
		JPanel pnCapacity = new JPanel();
		pnCapacity.setBounds(10, 23, 305, 198);
		pnResults.add(pnCapacity);
		pnCapacity.setLayout(null);
		pnCapacity.setBorder(BorderFactory.createTitledBorder(null, "  Embedded Pixels ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		
		lbImgEmbedded = new JLabel();
		lbImgEmbedded.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lbImgEmbedded.setBounds(10, 22, 184, 164);
		lbImgEmbedded.setOpaque(true);
		lbImgEmbedded.setBackground(Color.WHITE);
		pnCapacity.add(lbImgEmbedded);
		
		JLabel lblTotal = new JLabel("Total:");
		lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotal.setBounds(204, 25, 43, 14);
		pnCapacity.add(lblTotal);
		
		JLabel lblEmbeddeds = new JLabel("Emb");
		lblEmbeddeds.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEmbeddeds.setBounds(204, 53, 43, 14);
		pnCapacity.add(lblEmbeddeds);
		
		txTotalPx = new JTextField();
		txTotalPx.setEditable(false);
		txTotalPx.setText("0");
		txTotalPx.setColumns(10);
		txTotalPx.setBounds(257, 22, 38, 20);
		pnCapacity.add(txTotalPx);
		
		txEmbeddedPx = new JTextField();
		txEmbeddedPx.setEditable(false);
		txEmbeddedPx.setText("0");
		txEmbeddedPx.setColumns(10);
		txEmbeddedPx.setBounds(257, 50, 38, 20);
		pnCapacity.add(txEmbeddedPx);
		
		JPanel pnLegend = new JPanel();
		pnLegend.setBounds(204, 91, 88, 95);
		pnCapacity.add(pnLegend);
		pnLegend.setLayout(null);
		pnLegend.setBorder(BorderFactory.createTitledBorder(null, "  Legend ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_4.setBackground(Color.RED);
		panel_4.setBounds(14, 25, 15, 15);
		pnLegend.add(panel_4);
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_5.setBackground(Color.WHITE);
		panel_5.setBounds(14, 49, 15, 15);
		pnLegend.add(panel_5);
		
		JLabel lblMissed = new JLabel("Missed");
		lblMissed.setHorizontalAlignment(SwingConstants.LEFT);
		lblMissed.setBounds(33, 26, 42, 14);
		pnLegend.add(lblMissed);
		
		JLabel lblEncrustedPixelval = new JLabel("(0)");
		lblEncrustedPixelval.setHorizontalAlignment(SwingConstants.LEFT);
		lblEncrustedPixelval.setBounds(33, 49, 42, 14);
		pnLegend.add(lblEncrustedPixelval);
		
		JLabel lblEncrustedPixelval_1 = new JLabel("(1)");
		lblEncrustedPixelval_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblEncrustedPixelval_1.setBounds(33, 67, 42, 14);
		pnLegend.add(lblEncrustedPixelval_1);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBackground(Color.BLACK);
		panel_1.setBounds(14, 67, 15, 15);
		pnLegend.add(panel_1);
		
		JPanel pnMarkedRelation = new JPanel();
		pnMarkedRelation.setBounds(10, 224, 266, 62);
		pnResults.add(pnMarkedRelation);
		pnMarkedRelation.setLayout(null);
		pnMarkedRelation.setBorder(BorderFactory.createTitledBorder(null, "  Relation Tuples ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		
		JLabel lblMark = new JLabel("Mark:");
		lblMark.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMark.setBounds(103, 30, 34, 14);
		pnMarkedRelation.add(lblMark);
		
		txTotalTupl = new JTextField();
		txTotalTupl.setText("0");
		txTotalTupl.setEditable(false);
		txTotalTupl.setColumns(10);
		txTotalTupl.setBounds(136, 27, 45, 20);
		pnMarkedRelation.add(txTotalTupl);
		
		
		JLabel lblTot = new JLabel("Tot:");
		lblTot.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTot.setBounds(10, 30, 34, 14);
		pnMarkedRelation.add(lblTot);
		
		txTotalTuples = new JTextField();
		txTotalTuples.setText("0");
		txTotalTuples.setEditable(false);
		txTotalTuples.setColumns(10);
		txTotalTuples.setBounds(54, 27, 39, 20);
		pnMarkedRelation.add(txTotalTuples);
		
		JLabel lblPerc = new JLabel("Perc");
		lblPerc.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPerc.setBounds(191, 30, 34, 14);
		pnMarkedRelation.add(lblPerc);
		
		txMTP = new JTextField();
		txMTP.setText("0");
		txMTP.setEditable(false);
		txMTP.setColumns(10);
		txMTP.setBounds(225, 27, 34, 20);
		pnMarkedRelation.add(txMTP);
		
		txIntra = new JTextField();
		txIntra.setText("40");
		txIntra.setColumns(10);
		txIntra.setBounds(382, 420, 38, 20);
		getContentPane().add(txIntra);
		
		txInter = new JTextField();
		txInter.setText("40");
		txInter.setColumns(10);
		txInter.setBounds(382, 451, 38, 20);
		getContentPane().add(txInter);
		
		JLabel lblIntra = new JLabel("Intra:");
		lblIntra.setHorizontalAlignment(SwingConstants.RIGHT);
		lblIntra.setBounds(333, 423, 47, 14);
		getContentPane().add(lblIntra);
		
		JLabel lblInter = new JLabel("Inter:");
		lblInter.setHorizontalAlignment(SwingConstants.RIGHT);
		lblInter.setBounds(333, 454, 47, 14);
		getContentPane().add(lblInter);
		
		JButton btnTester = new JButton("TESTER");
		btnTester.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				StringPlayer temp = new StringPlayer(txTester.getText());
				temp.splitSentence();
				temp.findCandidates();
				//temp.getCandidates();
				temp.printSynonyms();
				temp.getSenseKeys();
				
			}
		});
		btnTester.setBounds(234, 522, 89, 23);
		getContentPane().add(btnTester);
		
		txTester = new JTextField();
		txTester.setText("The amazing man of the country.");
		txTester.setColumns(10);
		txTester.setBounds(335, 523, 67, 20);
		getContentPane().add(txTester);
		
		JButton btnAttrs = new JButton("...");
		btnAttrs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
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
		btnAttrs.setBounds(160, 32, 50, 23);
		getContentPane().add(btnAttrs);
		
		txSentLength = new JTextField();
		txSentLength.setText("10");
		txSentLength.setColumns(10);
		txSentLength.setBounds(117, 33, 38, 20);
		getContentPane().add(txSentLength);
		
		JLabel lblSentenceLength = new JLabel("Min. Words:");
		lblSentenceLength.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSentenceLength.setBounds(10, 36, 104, 14);
		getContentPane().add(lblSentenceLength);
		
		JPanel panel_7 = new JPanel();
		panel_7.setLayout(null);
		panel_7.setBorder(BorderFactory.createTitledBorder(null, "  Multi-Sentence Values ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		panel_7.setBounds(224, 253, 204, 156);
		getContentPane().add(panel_7);
		
		JLabel lblMinimumWords = new JLabel("Sentence Fraction:");
		lblMinimumWords.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMinimumWords.setBounds(16, 88, 138, 14);
		panel_7.add(lblMinimumWords);
		
		textField_1 = new JTextField();
		textField_1.setText("1");
		textField_1.setColumns(10);
		textField_1.setBounds(156, 85, 37, 20);
		panel_7.add(textField_1);
		
		JRadioButton rdbtnFirstSentence = new JRadioButton("First Sentence");
		rdbtnFirstSentence.setSelected(true);
		rdbtnFirstSentence.setBounds(41, 40, 124, 23);
		panel_7.add(rdbtnFirstSentence);
		
		JRadioButton rdbtnBestOption = new JRadioButton("Best Option");
		rdbtnBestOption.setBounds(41, 60, 124, 23);
		panel_7.add(rdbtnBestOption);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Single Sentence");
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.setBounds(16, 19, 137, 23);
		panel_7.add(chckbxNewCheckBox);
		
		JLabel lblMinimumWords_1 = new JLabel("Minimum Words:");
		lblMinimumWords_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMinimumWords_1.setBounds(16, 128, 137, 14);
		panel_7.add(lblMinimumWords_1);
		
		txMWSent = new JTextField();
		txMWSent.setText("5");
		txMWSent.setColumns(10);
		txMWSent.setBounds(157, 125, 37, 20);
		panel_7.add(txMWSent);
		
		JLabel label_7 = new JLabel("__________________");
		label_7.setOpaque(true);
		label_7.setHorizontalAlignment(SwingConstants.LEFT);
		label_7.setBounds(37, 102, 124, 14);
		panel_7.add(label_7);
		
		JLabel lblMetric = new JLabel("Metric:");
		lblMetric.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMetric.setBounds(224, 423, 48, 14);
		getContentPane().add(lblMetric);
		
		JComboBox<String> cbMetric = new JComboBox<String>();
		cbMetric.setModel(new DefaultComboBoxModel<String>(new String[] {"JNC","LCH","LIN","PATH","RES","WUP",}));
		cbMetric.setSelectedIndex(0);
		cbMetric.setBounds(273, 420, 58, 20);
		getContentPane().add(cbMetric);
		
		JButton btnExit = new JButton("Close");
		btnExit.setBounds(777, 537, 89, 23);
		getContentPane().add(btnExit);
		
		JButton button = new JButton("TESTER");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String p = "This is how I tried to split a paragraph into a sentence. But, there is a problem. My paragraph includes dates like Jan.13, 2014 , words like U.S and numbers like 2.2. They all got split by the above code.";
				System.out.println(p);
		/////////		String temp[] = Util.splitParagraph(p);
		//////		for (int i = 0; i < temp.length; i++) {
		///////			System.out.println(temp[i]);
		//////		}


			}
		});
		button.setBounds(657, 537, 89, 23);
		getContentPane().add(button);
		
		JPanel panel_10 = new JPanel();
		panel_10.setLayout(null);
		panel_10.setBorder(BorderFactory.createTitledBorder(null, "  WSD Algorithm ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		panel_10.setBounds(438, 234, 155, 61);
		getContentPane().add(panel_10);
		
		rbWSDLesk = new JCheckBox("Lesk");
		rbWSDLesk.setSelected(true);
		rbWSDLesk.setBounds(22, 25, 109, 23);
		panel_10.add(rbWSDLesk);
		
		txMSPar = new JTextField();
		txMSPar.setText("1");
		txMSPar.setColumns(10);
		txMSPar.setBounds(361, 491, 37, 20);
		getContentPane().add(txMSPar);
		
		JLabel lblMinimumSentences = new JLabel("Minimum Sentences:");
		lblMinimumSentences.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMinimumSentences.setBounds(220, 494, 137, 14);
		getContentPane().add(lblMinimumSentences);
		
		JPanel panel_11 = new JPanel();
		panel_11.setLayout(null);
		panel_11.setBorder(BorderFactory.createTitledBorder(null, "  Similarity Metric  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		panel_11.setBounds(438, 306, 163, 273);
		getContentPane().add(panel_11);
		
		rbMetrWUP.setBounds(6, 25, 66, 23);
		panel_11.add(rbMetrWUP);
		
		rbMetrJCN.setBounds(6, 51, 66, 23);
		panel_11.add(rbMetrJCN);
		
		rbMetrLCH.setBounds(6, 79, 66, 23);
		panel_11.add(rbMetrLCH);
		rbMetrLIN.setSelected(true);
		
		rbMetrLIN.setBounds(6, 104, 66, 23);
		panel_11.add(rbMetrLIN);
		
		rbMetrHSO.setBounds(74, 104, 66, 23);
		panel_11.add(rbMetrHSO);
		
		rbMetrLESK.setBounds(74, 79, 66, 23);
		panel_11.add(rbMetrLESK);
		
		rbMetrPATH.setBounds(74, 51, 66, 23);
		panel_11.add(rbMetrPATH);
		
		rbMetrRES.setBounds(74, 25, 66, 23);
		panel_11.add(rbMetrRES);
		
		bgMetrics.add(rbMetrWUP);
		bgMetrics.add(rbMetrJCN);
		bgMetrics.add(rbMetrLCH);
		bgMetrics.add(rbMetrLIN);
		bgMetrics.add(rbMetrRES);
		bgMetrics.add(rbMetrPATH);
		bgMetrics.add(rbMetrLESK);
		bgMetrics.add(rbMetrHSO);
		
		
		
		txMaxDist = new JTextField();
		txMaxDist.setText("0.5");
		txMaxDist.setColumns(10);
		txMaxDist.setBounds(113, 225, 40, 20);
		panel_11.add(txMaxDist);
		
		JLabel lblMaxAllowPert = new JLabel("Max. Allow. Pert:");
		lblMaxAllowPert.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMaxAllowPert.setBounds(6, 228, 87, 14);
		panel_11.add(lblMaxAllowPert);
		rdbtnSolo1.setSelected(true);
		
		rdbtnSolo1.setBounds(6, 137, 66, 23);
		panel_11.add(rdbtnSolo1);
		
		rdbtnSolo0.setBounds(74, 134, 66, 23);
		panel_11.add(rdbtnSolo0);
		rdbtnNada.setBounds(6, 174, 66, 23);
		panel_11.add(rdbtnNada);
		
		rdbtnMayor.setSelected(true);
		rdbtnMayor.setBounds(74, 174, 66, 23);
		panel_11.add(rdbtnMayor);
		
		bgInclude.add(rdbtnSolo1);
		bgInclude.add(rdbtnSolo0);
		bgInclude.add(rdbtnNada);
		bgInclude.add(rdbtnMayor);
		
		lbImageViewer2.setOpaque(true);
		lbImageViewer2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lbImageViewer2.setBackground(Color.WHITE);
		lbImageViewer2.setBounds(950, 271, 184, 164);
		getContentPane().add(lbImageViewer2);
		
		txCF = new JTextField();
		txCF.setText("0");
		txCF.setEditable(false);
		txCF.setColumns(10);
		txCF.setBounds(1101, 451, 39, 20);
		getContentPane().add(txCF);
		
		JLabel lblCf = new JLabel("CF");
		lblCf.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCf.setBounds(1057, 454, 34, 14);
		getContentPane().add(lblCf);
		
		txSSIM = new JTextField();
		txSSIM.setText("0");
		txSSIM.setEditable(false);
		txSSIM.setColumns(10);
		txSSIM.setBounds(1101, 482, 39, 20);
		getContentPane().add(txSSIM);
		
		JLabel lblSsim = new JLabel("SSIM");
		lblSsim.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSsim.setBounds(1057, 485, 34, 14);
		getContentPane().add(lblSsim);
		
		JButton btnMetrics = new JButton("Metrics");
		btnMetrics.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				double cf = 0;
				int cumul = 0;
				
				int wrong_marks = 0;
				double err_rate = 0.0;
				
				DecimalFormat df = new DecimalFormat("##.##");
				df.setRoundingMode(RoundingMode.DOWN);
				
				try {
					
					
					File fileEmb = new File("C://WORKSPACE//2019_RelTextWM//img//incrusted.bmp");
					mySSIMCalc = new SsimCalculator(fileEmb);
					
					File fileExt = new File("C://WORKSPACE//2019_RelTextWM//img//extracted.bmp");
					
					Double tempSSIM_A = new Double(0);
					tempSSIM_A = mySSIMCalc.compareTo(fileExt);
					
					txSSIM.setText(String.valueOf(df.format(tempSSIM_A)));
					
					
					for (int i = 0; i < imageWidth; i++) {
						for (int j = 0; j < imageHeight; j++) {
							if(recoveredInfo[j][i] != -1){
								cumul = cumul + (originalImage[j][i] ^ (-1*(recoveredInfo[j][i]-1)));
							}
							if(originalImage[j][i] != recoveredInfo[j][i]){
								wrong_marks ++;
							}
						}
					}
					
					cf = 100*cumul/(imageWidth*imageHeight);
					
					txCF.setText(String.valueOf(df.format(cf)));
					
					txErrRate.setText(String.valueOf(wrong_marks));
					
					err_rate = wrong_marks * 100 / (imageWidth * imageHeight);
					txRateErr.setText(String.valueOf(df.format(err_rate)));
					
					//
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		btnMetrics.setBounds(958, 481, 89, 23);
		getContentPane().add(btnMetrics);
		
		txMissP = new JTextField();
		txMissP.setText("0");
		txMissP.setEditable(false);
		txMissP.setColumns(10);
		txMissP.setBounds(1111, 520, 39, 20);
		getContentPane().add(txMissP);
		
		JLabel lblMiss = new JLabel("Miss");
		lblMiss.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMiss.setBounds(1067, 523, 34, 14);
		getContentPane().add(lblMiss);
		cbLockCtrller.setBounds(968, 450, 109, 23);
		getContentPane().add(cbLockCtrller);
		
		txErrRate = new JTextField();
		txErrRate.setText("0");
		txErrRate.setEditable(false);
		txErrRate.setColumns(10);
		txErrRate.setBounds(1098, 554, 39, 20);
		getContentPane().add(txErrRate);
		
		JLabel lblErrrate = new JLabel("No. Wrong");
		lblErrrate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblErrrate.setBounds(1004, 557, 84, 14);
		getContentPane().add(lblErrrate);
		
		JLabel label = new JLabel("Err.Rate");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(850, 562, 84, 14);
		getContentPane().add(label);
		
		txRateErr = new JTextField();
		txRateErr.setText("0");
		txRateErr.setEditable(false);
		txRateErr.setColumns(10);
		txRateErr.setBounds(944, 559, 39, 20);
		getContentPane().add(txRateErr);
		
		JLabel lblMinimumCarrierSize = new JLabel("Minimum Carrier Size:");
		lblMinimumCarrierSize.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMinimumCarrierSize.setBounds(53, 562, 137, 14);
		getContentPane().add(lblMinimumCarrierSize);
		
		txMinCarrSize = new JTextField();
		txMinCarrSize.setText("2");
		txMinCarrSize.setColumns(10);
		txMinCarrSize.setBounds(194, 559, 37, 20);
		getContentPane().add(txMinCarrSize);
		
		JButton btnSaveOriginal = new JButton("SAVE ORIGINAL");
		btnSaveOriginal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BufferedImage orig_img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
					for (int i = 0; i < imageWidth; i++) {
						for (int j = 0; j < imageHeight; j++) {
							if(originalImage[j][i] == 1){
								orig_img.setRGB(i, j, Color.BLACK.getRGB());
								}else	if(originalImage[j][i] == 0){
									orig_img.setRGB(i, j, Color.WHITE.getRGB());
										} else {
											orig_img.setRGB(i, j, Color.RED.getRGB());
										}
							}
						}
					
					int cantexc = 0;
					for (int i = 0; i < imageWidth; i++) {
						for (int j = 0; j < imageHeight; j++) {
							if(originalImage[j][i] == -1){
								cantexc++;
							}
						}
					}
					
					System.out.println("EXCLUIDOSSS: " + cantexc);
						
					ImageIO.write(orig_img, "bmp", new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"org.bmp"));
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					
					BufferedImage emb_img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
					for (int i = 0; i < imageWidth; i++) {
						for (int j = 0; j < imageHeight; j++) {
							if(embeddedInfo[j][i] == 1){
								emb_img.setRGB(i, j, Color.BLACK.getRGB());
								}else	if(embeddedInfo[j][i] == 0){
									emb_img.setRGB(i, j, Color.WHITE.getRGB());
										} else {
											emb_img.setRGB(i, j, Color.RED.getRGB());
										}
							}
						}
					
					int cantexc2 = 0;
					for (int i = 0; i < imageWidth; i++) {
						for (int j = 0; j < imageHeight; j++) {
							if(embeddedInfo[j][i] == -1){
								cantexc2++;
							}
						}
					}
					
					System.out.println("EXCLUIDOSSS: " + cantexc2);
						
					
					ImageIO.write(emb_img, "bmp", new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"emb.bmp"));
					//JOptionPane.showMessageDialog(null, "The extracted image was successfully saved...");
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}	
			}
		});
		btnSaveOriginal.setBounds(257, 556, 163, 23);
		getContentPane().add(btnSaveOriginal);
		
		JButton btnOi = new JButton("O(I)");
		btnOi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String number = "";
				Vector<Integer> collector = new Vector<Integer>();
				float acum = 0;
				for (int i = 0; i < imageHeight-2; i++) { 
					for (int j = 0; j < imageWidth-2; j++) { 
						number = "";
						for (int ii = i; ii < i+3; ii++) { 
							for (int jj = j; jj < j+3; jj++) { 
								//number = number + originalImage[jj][ii];
								number = number + originalImage[ii][jj];
							}
						}
						
						if(!collector.contains(Integer.parseInt(number,2))) {
							collector.add(Integer.parseInt(number,2));
							acum ++;
						}
					}
				}
				
				float last = acum/((imageWidth-2)*(imageHeight-2));
				
				//originalImage = Util.getImageMatrix();
            	
            	//imageWidth = Util.getImageWidth();
            	//imageHeight = Util.getImageHeight();
			}
		});
		btnOi.setBounds(950, 515, 89, 23);
		getContentPane().add(btnOi);
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
		
		
		
		}
		catch (Exception e) {
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
	
	public int[][] getImgToEmbed(){
		return originalImage;
	}
	
	public int gettEmbedImgHeigh(){
		return imageHeight;
	}
	
	public int getEmbedImgWidth(){
		return imageWidth;
	}
	
	public String getSecretKey(){
		return txSK.getText();
	}
	
	public String getTupleFract(){
		return tfFractTupl.getText();
	}
	
	public int getAttrFract(){
		return Integer.valueOf(spAF.getValue().toString());
	}
	
	public void getVPKResp(){
		CallableStatement vpkResp = null;
		
		try {
			if(vpkResp == null){
				vpkResp = dbConnection.getConnection().prepareCall ("{ call RESP_LLP (?)}");
			}
			vpkResp.setString(1, cbTable.getSelectedItem().toString());
			vpkResp.execute ();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				vpkResp.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	public int[][] getEmbeddedImg(){
		return this.embeddedInfo;
	}
	
	public void getAttrResp(){
		CallableStatement attrResp = null;
		
		try {
			if(attrResp == null){
				attrResp = dbConnection.getConnection().prepareCall ("{ call RESP_ATTR (?,?)}");
			}
			attrResp.setString(1, cbTable.getSelectedItem().toString());
			attrResp.execute ();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				attrResp.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
