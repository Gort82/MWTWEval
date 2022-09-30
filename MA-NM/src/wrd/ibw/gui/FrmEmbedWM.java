package wrd.ibw.gui;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;

import javax.swing.JButton;

import wrd.ibw.da.DBConnection;
import wrd.ibw.utils.Coord;
import wrd.ibw.utils.Item;
import wrd.ibw.utils.ProbItem;
import wrd.ibw.utils.RandomSelector;
import wrd.ibw.utils.Util;

import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import javax.swing.JCheckBox;
import javax.swing.border.LineBorder;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.awt.event.ItemListener;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class FrmEmbedWM extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	
	private RandomSelector checker;
	private float no_sim = 0;
	private float cant_A = 0;
	private float cant_B = 0;
	
	private int max_recurr = 0;
	private Vector<Coord> marks = new Vector<Coord>();
	private Vector<ProbItem> probH = new Vector<ProbItem>();
	private Vector<Integer> cantEmb = new Vector<Integer>();
	
	private Vector<ProbItem> probHBackup = new Vector<ProbItem>();
	
	private int cta2 = 0, cta1 = 0; 
	private int counter[][];
	private int counter2[][];

	
	
	private DBConnection dbConnection = null;
	
	private JComboBox<String> cbTable = null;
	private JSpinner spAF = null;
	private JTextField tfFractTupl;
	private JTextField tfMSB;
	private JTextField tfLSB;
	private JTextField tfPrivateKey;
	
	
	private JFileChooser fileChooser;
	private File imageFile = null;
	private Image imageInfo = null;
	private JTable tbFields;
	
	private JButton btnStart = null;
	
	private int imageWidth = 0;
	private int imageHeight = 0;
	
	//private wrd.ibw.utils.ProgressBar myProgressBar = null;
	
	private String[] fixedFilds = {"ELEVATION", "ASPECT","SLOPE","HOR_DIST_TO_HYDROLOGY","VERT_DIST_TO_HYDROLOGY","HOR_DIST_TO_ROADWAYS","HILLSHADE_9AM","HILLSHADE_NOON","HILLSHADE_3PM","HOR_DIST_TO_FIRE_POINTS"};
	
	private Vector<Integer> imageVector = new Vector<Integer>();
	private Vector<Integer> selectorCounter = new Vector<Integer>();
	
	
	
	private Vector<Integer> directVector = new Vector<Integer>();
	
	private int embeddedInfo[][];
	
	private int originalImage[][];
	private JTextField txTotalPx;
	private JTextField txEmbeddedPx;
	private JLabel lbImgEmbedded;
	private JTextField txTotalTupl;
	private JTextField txTotalTuples;
	private JTextField txMTP;
	private JTextField txEPP;
	private JPanel pnResults;
	private JTextField txMinLSB;
	
	private JRadioButton rbOrigMeth;
	private JRadioButton rbExtMeth;
	
	private JCheckBox cbZDist;
	private JCheckBox cbMDist;
	private JCheckBox cbLsbM;
	
	private JRadioButton rbSScheme;
	private JRadioButton rbEScheme;
	private JRadioButton rbMScheme;
	private JRadioButton rbOScheme;
	private JComboBox<String> cbAttributes;
	
	private JCheckBox cbESCheme;
	
	/**
	 * @wbp.nonvisual location=642,99
	 */
	private final ButtonGroup bgMethod = new ButtonGroup();
	/**
	 * @wbp.nonvisual location=552,209
	 */
	private final ButtonGroup bgExtOpt = new ButtonGroup();
	private JTextField txPromAttr;
	private JTextField txTotMark;
	private JTextField txTotAttr;
	private JTextField txPromMarkedAttr;
	private JTextField tfMatch;
	private JTextField tfUnmatch;
	private JTextField tfTotalMatch;
	private JTextField txCountFP;
	private JTextField txCountRW;
	private JTextField txCountElev;
	private JTextField txCountHDHy;
	private JTextField txCountHill9;
	private JTextField txCountAsp;
	private JTextField txCountVDHy;
	private JTextField txCountHill3;
	private JTextField txCountSlope;
	private JTextField txCountHill12;
	private JTextField txCountFP_2;
	private JTextField txCountRW_2;
	private JTextField txCountElev_2;
	private JTextField txCountHDHy_2;
	private JTextField txCountAsp_2;
	private JTextField txCountVDHy_2;
	private JTextField txCountHill3_2;
	private JTextField txCountHill9_2;
	private JTextField txCountHill12_2;
	private JTextField txCountSlope_2;
	private JCheckBox chbDirSel = new JCheckBox("Direct Selection");
	/**
	 * @wbp.nonvisual location=842,89
	 */
	private final ButtonGroup bgVPK = new ButtonGroup();
	private JTextField txMSBSSheme;
	/**
	 * @wbp.nonvisual location=912,189
	 */
	private final ButtonGroup bgSOptions = new ButtonGroup();
	private JTextField txMSBMScheme;
	private JTextField txMSChemeNoAttr;
	private JTextField txMSBF;
	private JTextField txMSBESCheme;
	private JTextField txMaxSim;
	private JTextField txMinSim;
	
	private JComboBox<String> cbDelAttr;
	private JTextField txMaxIter;
	private JTextField txRecurrentMax;
	private JCheckBox chbprob;
	
	public int[][] getImageMatrix(){
		return originalImage;
	}

	public FrmEmbedWM(DBConnection pDBConnection) {
		//Window win = SwingUtilities.getWindowAncestor(this);
		this.dbConnection = pDBConnection;
		try {
			//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			this.setTitle("[Sardroudi & Ibrahim, 2010] Embed Watermark...");
			this.setSize(1154,701);
			this.getContentPane().setLayout(null);
			
			fileChooser = new JFileChooser(System.getProperty("user.dir") + System.getProperty("file.separator") + "img");
			
			fileChooser.setAcceptAllFileFilterUsed(false);
			
			String[] suffices = ImageIO.getReaderFileSuffixes();
			
			for (int i = 0; i < suffices.length; i++) {
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Image File (" + suffices[i] + ")", suffices[i]);
				if(suffices[i].equals("bmp"))
					fileChooser.addChoosableFileFilter(filter);
			}
			
			JLabel lblRelationToMark = new JLabel("Relation to Mark:");
			lblRelationToMark.setHorizontalAlignment(SwingConstants.RIGHT);
			lblRelationToMark.setBounds(10, 11, 110, 14);
			getContentPane().add(lblRelationToMark);
			getContentPane().add(getJCBTable());
		
			JLabel lblFractionOrRelations = new JLabel("Fraction of Tuples:");
			lblFractionOrRelations.setHorizontalAlignment(SwingConstants.RIGHT);
			lblFractionOrRelations.setBounds(10, 60, 110, 14);
			getContentPane().add(lblFractionOrRelations);
		
			tfFractTupl = new JTextField();
			tfFractTupl.setText("1");
			tfFractTupl.setBounds(123, 57, 47, 20);
			getContentPane().add(tfFractTupl);
			tfFractTupl.setColumns(10);
		
			tfMSB = new JTextField();
			tfMSB.setText("3");
			tfMSB.setColumns(10);
			tfMSB.setBounds(257, 35, 37, 20);
			getContentPane().add(tfMSB);
			
			tfLSB = new JTextField();
			tfLSB.setText("1");
			tfLSB.setColumns(10);
			tfLSB.setBounds(257, 58, 37, 20);
			getContentPane().add(tfLSB);
		
			JLabel lblMsb = new JLabel("MSB:");
			lblMsb.setHorizontalAlignment(SwingConstants.RIGHT);
			lblMsb.setBounds(222, 38, 31, 14);
			getContentPane().add(lblMsb);
			
			JLabel lblLsb = new JLabel("LSB:");
			lblLsb.setHorizontalAlignment(SwingConstants.RIGHT);
			lblLsb.setBounds(222, 61, 31, 14);
			getContentPane().add(lblLsb);
			
			JLabel lblPrivateKey = new JLabel("Private Key:");
			lblPrivateKey.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPrivateKey.setBounds(10, 37, 110, 14);
			getContentPane().add(lblPrivateKey);
			
			tfPrivateKey = new JTextField();
			tfPrivateKey.setText("Secu48102304782K");
			tfPrivateKey.setColumns(10);
			tfPrivateKey.setBounds(123, 34, 89, 20);
			getContentPane().add(tfPrivateKey);
			
			JPanel pnImageSelector = new JPanel();
			pnImageSelector.setLayout(null);
			pnImageSelector.setBorder(BorderFactory.createTitledBorder(null, "  Image to Embed  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			pnImageSelector.setBounds(304, 9, 204, 232);
			getContentPane().add(pnImageSelector);
			
			JLabel lbImageViewer = new JLabel();
			lbImageViewer.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			lbImageViewer.setBounds(10, 23, 184, 164);
			pnImageSelector.add(lbImageViewer);
		
			JButton btnOpen = new JButton("Load Image");
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
			            	//imgIcon.setImage(bImage);
			            	lbImageViewer.setIcon(imgIcon);
			            	
			            	Util.defineImageArray(imageFile);
			            	
			            	originalImage = Util.getImageMatrix();
			            	counter = Util.getCounterCells();
			            	counter2 = counter;
			            	
			            	
			            	imageWidth = Util.getImageWidth();
			            	imageHeight = Util.getImageHeight();
			            	
			            	
			            	
			            	txTotalPx.setText(String.valueOf(imageWidth * imageHeight));
			            	
			            	
			            	
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	
			            	if (chbprob.isSelected()) {
				            	max_recurr = Integer.valueOf(txRecurrentMax.getText());
				            	
				            	for (int j = 0; j < imageHeight; j++) {
				            		for (int i = 0; i < imageWidth; i++) {
				            			marks.add(new Coord(i, j));
				            			probH.add(null);
				            			probHBackup.add(null);
				            			cantEmb.add(max_recurr);
									}
								}
				            	
				            	
				            	float elem = 0;
				            	for (int cnter = 0; cnter < probH.size()-3; cnter++) {
				            		elem = elem + probH.size()-2 - cnter;
								}
				            	
				            	
				            	for (int xx = 0; xx < probH.size(); xx++) {
				            		double vasVal = (probH.size()-1 - xx) * 1/elem;
				            		int projVal = (probH.size()-1 - xx);
				            		int extVal = (probH.size()-1 - xx) * max_recurr;
				            		int cuttVal = (probH.size()-1 - xx) * max_recurr;
				            		
			            			probH.set(xx, new ProbItem(vasVal, projVal, extVal, cuttVal, xx, max_recurr));
			            			probHBackup.set(xx, new ProbItem(vasVal, projVal, extVal, cuttVal, xx, max_recurr));
								}
				            	probH.set(0,new ProbItem(0, 0, 0, 0, 0, 0));
				            	probHBackup.set(0,new ProbItem(0, 0, 0, 0, 0, 0));
				            	
				            	checker = new RandomSelector(marks, probH); 
				            	
				            	for (Iterator iterator = probH.iterator(); iterator.hasNext();) {
				            		ProbItem integer = (ProbItem) iterator.next();
				            		
				            		
				            		System.out.println(integer.getBasVal());
									
								}
			            	}
			            	
			            	
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	///// GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG
			            	
			            	imageVector = Util.getImageVector(); //storing the image vector into the corresponding class field
			            	
			            	for (int qq = 0; qq < imageVector.size(); qq++) {
			            		selectorCounter.add(0);
							}
			            	
			            	
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
			            //log.append("Open command cancelled by user." + newline);
			        }
				}
			});
			btnOpen.setBounds(10, 198, 184, 23);
			pnImageSelector.add(btnOpen);
		
			JPanel panel = new JPanel();
			panel.setLayout(null);
			panel.setBorder(BorderFactory.createTitledBorder(null, "  Fields to Consider  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel.setBounds(10, 85, 284, 156);
			getContentPane().add(panel);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(10, 22, 264, 123);
			panel.add(scrollPane);
		
			tbFields = new JTable( ){
	            private static final long serialVersionUID = 1L;
	            /*@Override
	            public Class getColumnClass(int column) {
	            return getValueAt(0, column).getClass();
	            }*/
	            @Override
	            public Class getColumnClass(int column) {
	                switch (column) {
	                    case 0:
	                    	return Boolean.class;
	                    case 1:
	                        return String.class;
	                    case 2:
	                    	 return String.class;
	                    default:
	                        return Boolean.class;
	                }
	            }
	        };
		
			tbFields.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			DefaultTableModel model = new DefaultTableModel();
			//ResultSet rs = this.dbConnection.getFields(this.cbTable.getSelectedItem().toString());
		    //ResultSetMetaData rsmd = rs.getMetaData();
		    /*for (int col = 0; col < rsmd.getColumnCount(); col++) {
		        model.addColumn(rsmd.getColumnName(col + 1));
		    }*/
		    model.addColumn("Get");
		    model.addColumn("Name");
		    model.addColumn("Type");

		    //TO MAKE MORE FLEXIBLE THE FIELDS SELECTION
		    /*while (rs.next()) {
		        Vector data = new Vector();
		        for (int col = 0; col < rsmd.getColumnCount(); col++) {
		            data.add(rs.getObject(col + 1));
		        }
		        data.add(0, Boolean.FALSE);
		        model.addRow(data);
		    }*/
		    
		    for (int i = 0; i < fixedFilds.length; i++) {
		    	model.addRow(new Object[]{Boolean.TRUE, fixedFilds[i], "FLOAT"});
			}
	    
	    //tbFields.setModel(DbUtils.resultSetToTableModel(this.dbConnection.getFields(this.cbTable.getSelectedItem().toString())));
		this.tbFields.setModel(model);
		
		this.tbFields.getColumnModel().getColumn(0).setPreferredWidth(37);
		this.tbFields.getColumnModel().getColumn(1).setPreferredWidth(145);
		this.tbFields.getColumnModel().getColumn(2).setPreferredWidth(64);
		this.tbFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		scrollPane.setViewportView(tbFields);
		
		btnStart = new JButton("Start");
		btnStart.setEnabled(false);
		btnStart.setBounds(514, 208, 89, 23);
		btnStart.addActionListener(new ActionListener() {
			/* (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent arg0) {
				
				//FOR
				
				int max_iterante = 0;
				
				max_iterante = Integer.parseInt(txMaxIter.getText());
				
				
				for (int iterante = 0; iterante < max_iterante; iterante++) {
					
				 
				
				
				txEmbeddedPx.setText("0");
				lbImgEmbedded.setBackground(Color.WHITE);
				
				embeddedInfo = new int[imageHeight][imageWidth];
				for (int i = 0; i < imageWidth; i++) {
 					for (int j = 0; j < imageHeight; j++) {
 						embeddedInfo[j][i] = -1;
 					}
 				}
				
				CallableStatement ids = null;
				ResultSet rset_pk = null;
				
				
				CallableStatement updater = null;
				CallableStatement attr_value_cs = null;
				CallableStatement hav_value_cs = null;
				CallableStatement attr_pos_cs = null;
				
				int countFP = 0;		int countRW = 0;		int countElev = 0;
				int countHDHy = 0;		int countHill9 = 0;		int countAsp = 0;
				int countVDHy = 0;		int countHill3 = 0;		int countSlope = 0;
				int countHill12 = 0;
				
				
				int countFP_2 = 0;		int countRW_2 = 0;		int countElev_2 = 0;
				int countHDHy_2 = 0;	int countHill9_2 = 0;	int countAsp_2 = 0;
				int countVDHy_2 = 0;	int countHill3_2 = 0;	int countSlope_2 = 0;
				int countHill12_2 = 0;
				
				//changes from inside the resulset cicle
			    int pos_attr_to_mark = 0;	 Float number_value;                 int width_pos = 0;
			    String attr_to_mark = "";    String binary_main = "";            int height_pos = 0;
			    int lsb_pos = 0;             String new_binary = "";             int image_element = 0;
			                                 /*String binary_decimal = "";*/     String hav_value;
			                                 int temp_decimal = 0;   
			    
			                                 
	        	int msb_pos = 0;             boolean signed = false;             int value_to_insert = 0; 
	        	int msb_value = 0;           int absolute_value = 0;             int real_value = 0;
        	
	        	Vector<Integer> attrToMark = new Vector<Integer>();
	        	Vector<String> havValues = new Vector<String>();
	        	
	        	String[ ] attribute_array = {"ELEVATION", "ASPECT", "SLOPE", "HOR_DIST_TO_HYDROLOGY", "VERT_DIST_TO_HYDROLOGY", "HOR_DIST_TO_ROADWAYS", "HILLSHADE_9AM", "HILLSHADE_NOON", "HILLSHADE_3PM", "HOR_DIST_TO_FIRE_POINTS"}; 
	        	
			    boolean embedded = false;
			    
			    int tot_Mark = 0;
			    int tupl_mark = 0;
			    
			    int tot_gen_attr = 0;
			    
			    String prevID = "";
							    
			    int attr_total = 0;     int attr_cero = 0;    	int attr_uno = 0;
			    int attr_dos = 0;       int attr_tres = 0;    	int attr_cuatro = 0;
			    int attr_cinco = 0;		int attr_seis = 0;		int attr_siete = 0;
			    int attr_ocho = 0;		int attr_nueve = 0;		int attr_diez = 0;
			    int attr_once = 0;		
			    
			    int lsb_total = 0;		int lsb_cero = 0;		int lsb_uno = 0;
			    int lsb_dos = 0;		int lsb_tres = 0;		int lsb_cuatro = 0;
			    int lsb_cinco = 0;		int lsb_seis = 0;		int msb_total = 0;
			    int msb_cero = 0;		int msb_uno = 0;		int msb_dos = 0;
			    int msb_tres = 0;		int msb_cuatro = 0;		int msb_cinco = 0;
			    int msb_seis = 0;
							    
							    
			    int match = 0;			 
			    int unmatch = 0;
			    int lsb_value = 2;
				
				try {
					//Temporal segmento para el uso de todos los quantiles
					//En este caso lo que se quiere es usar toda la capacidad, pero evitar que los valores se muevan 
					//de un quartil a otro al ser actualizados
					
					/*int total_tuplas_marcadas = 0;
					boolean first = false;*/
					
					//BUILDING THE ATTRIBUTES LIST
					Vector<String> attributes = new Vector<String>();
					for (int i = 0; i < tbFields.getRowCount(); i++) {
						 if(tbFields.getModel().getValueAt(i, 0).equals(true)){
							 attributes.add(tbFields.getModel().getValueAt(i, 1).toString());
						 }
					}
					
					//EMBEDDING PROCESS OVERVIEW
			//		System.out.println("-----------------------------------------------");
			//		System.out.println("WM EMBEDDING PROCESS ");
			//		System.out.println("-----------------------------------------------");
			//		System.out.println("RELATION TO MARK: " + cbTable.getSelectedItem().toString());
			//		System.out.println("SECRET KEY: " + tfPrivateKey.getText());
			//		System.out.println("TUPLES FRACTION: " + tfFractTupl.getText());
			//		System.out.println("NO. APROX. TUPLES: " + dbConnection.getNoRows(cbTable.getSelectedItem().toString(), tfPrivateKey.getText(), Integer.valueOf(tfFractTupl.getText())));
			//		System.out.println("MOST SIGNIFICANT BIT (MSB): " + tfMSB.getText());
			//		System.out.println("LESS SIGNIFICANT BIT (LSB): " + tfLSB.getText());
			//		System.out.println("ATTRIBUTE LIST: ");
			//		for (int i = 0; i < attributes.size(); i++) {
			//			System.out.println("  " + String.valueOf(i+1) + ". " + attributes.elementAt(i));
			//		}
			//		System.out.println("-----------------------------------------------");
			//		System.out.println("IMAGE TO EMBED: " + imageFile.getName());
			//		System.out.println("WIDTH: " + String.valueOf(imageWidth)); 
			//		System.out.println("HIGH: " + String.valueOf(imageHeight));
			//		System.out.println("-----------------------------------------------");
					
					System.out.println("-----------------" + iterante + "-----------------");
					//STARTING TIME
					Calendar cal = Calendar.getInstance();
			    	cal.getTime();
			    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			    	System.out.println("PROCESS STARTED AT: " + sdf.format(cal.getTime()) );
			//    	System.out.println("-----------------------------------------------");
			    	
			    	int totKeys = 0;
			    	int passedKets = 0;
			    	
			    	if (cbESCheme.isSelected()) {
			    		for (int i = 0; i < 10; i++) {
			    			ids = dbConnection.getConnection().prepareCall ("{ ? = call GENERATE_E (?,?,?,?,?,?,?,?,?)}");
							ids.registerOutParameter (1, OracleTypes.CURSOR);
							ids.setString (2,cbTable.getSelectedItem().toString());
							ids.setInt (3, Integer.valueOf(txMSBESCheme.getText())-1);
							ids.setInt (4, Integer.valueOf(tfLSB.getText())-1);
							ids.setInt (5, attributes.size());
							ids.setInt (6, i+1);
							ids.setString (7,tfPrivateKey.getText());
							ids.setInt (8, Integer.parseInt(tfFractTupl.getText()));
							ids.setInt (9, imageHeight-1);
							ids.setInt (10, imageWidth-1);
							ids.execute ();
							
							
				//			System.out.println("Excecution of attr: " + Integer.valueOf(i+1) + "---Attribute: " + attribute_array[i]);
							
							
							
							
							
							
							
							
							
							
							
							
							rset_pk = (ResultSet)ids.getObject (1);
							
						    if(updater == null){
		    					updater = dbConnection.getConnection().prepareCall ("{ call UPDATE_ATTR_AT (?,?,?,?)}");
		    				}
							
							while (rset_pk.next ()){
								 totKeys++;
						    	try {
						    		//System.out.println("CONS_FACT: " + rset_pk.getInt ("CONS_FACT") );
						    		if(rset_pk.getInt ("CONS_FACT")==0){
									    passedKets++;
						    			number_value = rset_pk.getFloat("ATTR_VALUE");
						    			
						    			//System.out.println("No: " + passedKets + "----ATTRIBUTE ID: " + rset_pk.getFloat("ATTR_ID")+ "----ATTRIBUTE VALUE: " + rset_pk.getFloat("ATTR_VALUE"));
						    			
							        	absolute_value = Math.abs(number_value.intValue());
							        	binary_main = Integer.toBinaryString(absolute_value);
							        	
							        	System.out.println("BINARY: " + binary_main.length());
					    				System.out.println("lsb: " + String.valueOf(tfLSB.getText()));
					    				System.out.println("msb: " + String.valueOf(tfMSB.getText()));
							        	
							        	
							        	if (binary_main.length()>=(Integer.valueOf(tfLSB.getText()))+Integer.valueOf(tfMSB.getText())){
							        		if(number_value < 0){ signed = true;}
							        		temp_decimal = Integer.valueOf(String.valueOf(number_value).substring(String.valueOf(number_value).indexOf ( "." )+1));
						    				if(temp_decimal!=0){ /*binary_decimal = Integer.toBinaryString(temp_decimal);*/}

						    				
						    				
						    				if(new Integer(tfLSB.getText())*2 < binary_main.length()){
						    					height_pos = rset_pk.getInt("H"); 
						    					width_pos =  rset_pk.getInt("W"); 
											        		   	
						    					msb_pos = rset_pk.getInt("MSB_POS") + 1  ;
						    					lsb_pos = rset_pk.getInt("LSB_POS") + 1  ;
						    					
						    					
						    					System.out.println("HEIGHT: " + height_pos);
						    					System.out.println("WDTH: " + width_pos);
						    					
						    					
						    					System.out.println("MSB: " + msb_pos);
						    					System.out.println("LSB: " + lsb_pos);
						    					
						    					
												        		
						    					if((msb_pos - 1) < (binary_main.length()-lsb_pos)){ //creo que ya lo salva la condicion de qeu LSB + MSB <= Binary.length
						    						msb_value = Character.getNumericValue(binary_main.charAt(msb_pos-1));
											        			
						    						if((imageWidth * (height_pos) + width_pos) > 0  ){
						    							image_element = imageVector.elementAt(imageWidth * (height_pos) + width_pos);
						    							value_to_insert = image_element ^ msb_value;
											        				
						    							lsb_value = Character.getNumericValue(binary_main.charAt(binary_main.length()-lsb_pos));
											        				
						    							if (!(cbZDist.isSelected())) {
						    								new_binary = binary_main.substring(0, binary_main.length() - lsb_pos) + value_to_insert + binary_main.substring(binary_main.length() - lsb_pos+1, binary_main.length()) ;
						    								int min_pos = 1;
						    								if (cbMDist.isSelected()) {
						    									min_pos = Integer.valueOf(txMinLSB.getText());
						    								}
						    								new_binary = Util.minimizeVariation(binary_main, new_binary, lsb_pos, min_pos);
												        				
						    								//************************************* UPDATE BLOCK ********************************************
						    								if(updater.isClosed()){
						    									updater = dbConnection.getConnection().prepareCall ("{ call UPDATE_ATTR_AT (?,?,?,?)}");
						    								}			
						    								updater.setString(1, cbTable.getSelectedItem().toString());
						    								updater.setString (2, attribute_array[i]);
												        				
						    								real_value = Integer.parseInt(new_binary,2);

						    								if(signed){
						    									real_value = 0-real_value;
						    									signed = false;
						    								}
						    								updater.setInt(3, real_value);
						    								updater.setInt(4, Integer.valueOf(rset_pk.getString ("ID")));
						    								updater.execute ();
												        				
						    								embedded = true;
						    								
						    								System.out.println("OLD VALUE: " + lsb_value);
									    					System.out.println("NEW VALUE: " + value_to_insert);
									    					
						    							}else{
						    								for (int j = 0; j < binary_main.length(); j++) {
						    									if (binary_main.charAt(j) == value_to_insert) {
						    										j = binary_main.length();
						    										embedded = true;
						    									}
						    								}
						    							}
											        				
						    							if (embedded) {
						    								embeddedInfo[height_pos][width_pos] = image_element;
						    							}
						    						}	
						    					}
						    				}
							        	}
		        	
							        	tot_gen_attr = tot_gen_attr + attrToMark.size();
					        	
							        	//updater.close();
							        	//updater = null;tfhytrfy
					        	
							        	DecimalFormat dff = new DecimalFormat("##.##");
										dff.setRoundingMode(RoundingMode.DOWN);
							        	tfMatch.setText(String.valueOf(dff.format(Float.valueOf(match)*100/Float.valueOf(match+unmatch))));
							        	tfUnmatch.setText(String.valueOf(dff.format(Float.valueOf(unmatch)*100/Float.valueOf(match+unmatch))));
						        					
							        	txCountAsp.setText(String.valueOf(countAsp));
							        	txCountElev.setText(String.valueOf(countElev));
							        	txCountSlope.setText(String.valueOf(countSlope));
							        	txCountHDHy.setText(String.valueOf(countHDHy));
							        	txCountVDHy.setText(String.valueOf(countVDHy));
							        	txCountRW.setText(String.valueOf(countRW));
							        	txCountHill9.setText(String.valueOf(countHill9));
							        	txCountHill12.setText(String.valueOf(countHill12));
							        	txCountHill3.setText(String.valueOf(countHill3));
							        	txCountFP.setText(String.valueOf(countFP));
										        	
							        	txCountAsp_2.setText(String.valueOf(countAsp_2));
							        	txCountElev_2.setText(String.valueOf(countElev_2));
							        	txCountSlope_2.setText(String.valueOf(countSlope_2));
							        	txCountHDHy_2.setText(String.valueOf(countHDHy_2));
							        	txCountVDHy_2.setText(String.valueOf(countVDHy_2));
							        	txCountRW_2.setText(String.valueOf(countRW_2));
							        	txCountHill9_2.setText(String.valueOf(countHill9_2));
							        	txCountHill12_2.setText(String.valueOf(countHill12_2));
							        	txCountHill3_2.setText(String.valueOf(countHill3_2));
							        	txCountFP_2.setText(String.valueOf(countFP_2));
						    		}
						    	} catch (Exception e) {
						    		e.printStackTrace();
						    	}finally {
						    		updater.close();
						    		if(hav_value_cs!=null)
						    			hav_value_cs.close();
						    		if(attr_pos_cs!=null)
						    			attr_pos_cs.close();
						    	}
							}
			    		}
					} else {
				    	//ORIGINAL METHOD PROPOSED BY SARDROUDI AND IBRAHIM, 2010
				    	//if(rbOrigMeth.isSelected()){
						//GET THE GENERAL INFORMATION
						ids = dbConnection.getConnection().prepareCall ("{ ? = call GET_GENERAL_INFO (?,?,?,?,?,?,?,?)}");
						ids.registerOutParameter (1, OracleTypes.CURSOR);
						ids.setString (2,cbTable.getSelectedItem().toString());
						ids.setString (3,tfPrivateKey.getText());
						ids.setInt (4, Integer.parseInt(tfFractTupl.getText()));
						ids.setInt (5, imageHeight-1);
						ids.setInt (6, imageWidth-1);
						ids.setInt (7, attributes.size()-1);
						ids.setInt (8, Integer.valueOf(tfMSB.getText())-1);
						ids.setInt (9, Integer.valueOf(tfLSB.getText())-1);
						ids.execute ();
									
						rset_pk = (ResultSet)ids.getObject (1);
								    
						if(attr_value_cs == null){
	    					attr_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_VALUE_OF_ATTR (?,?,?)}");
	    				}
					    attr_value_cs.registerOutParameter (1, Types.FLOAT);
									    
					    if(rbExtMeth.isSelected()){
					    	if(hav_value_cs == null){
					    		hav_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call CREATE_HAV (?,?,?)}");
			    			}
					    	hav_value_cs.registerOutParameter (1, Types.INTEGER);
									    	
					    	if(attr_pos_cs == null){
					    		attr_pos_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_ATTR_POS (?,?,?,?,?,?)}");
			    			}
					    	attr_pos_cs.registerOutParameter (1, OracleTypes.CURSOR);
					    }
									    
					    if(updater == null){
	    					updater = dbConnection.getConnection().prepareCall ("{ call UPDATE_ATTR_AT (?,?,?,?)}");
	    				}	
									    
									    
					    
									    
					    while (rset_pk.next ()){
					    	 totKeys++;
					    	try {
					    		//System.out.println("CONS_FACT: " + rset_pk.getInt ("CONS_FACT") );
					    	
					    		if(rset_pk.getInt ("CONS_FACT")==0){
					    			passedKets++;
					    			if(rbOrigMeth.isSelected()){
					    				attrToMark.clear();
					    				havValues.clear();
										        	
							        	if(attr_value_cs.isClosed()){
					    					attr_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_VALUE_OF_ATTR (?,?,?)}");
					    					attr_value_cs.registerOutParameter (1, Types.FLOAT);
					    				}
												    
						        		//pos_attr_to_mark = (new BigInteger(rset_pk.getString ("VPK")).mod(BigInteger.valueOf(attributes.size()))).intValue();
									        		
						        		pos_attr_to_mark = rset_pk.getInt("ATTR_POS");
									        		
						        		attr_total++;
						        		if(pos_attr_to_mark == 0) attr_cero++;
						        		else if(pos_attr_to_mark == 1) attr_uno++;
						        		else if(pos_attr_to_mark == 2) attr_dos++;
						        		else if(pos_attr_to_mark == 3) attr_tres++;
						        		else if(pos_attr_to_mark == 4) attr_cuatro++;
						        		else if(pos_attr_to_mark == 5) attr_cinco++;
						        		else if(pos_attr_to_mark == 6) attr_seis++;
						        		else if(pos_attr_to_mark == 7) attr_siete++;
						        		else if(pos_attr_to_mark == 8) attr_ocho++;
						        		else if(pos_attr_to_mark == 9) attr_nueve++;
						        		else if(pos_attr_to_mark == 10) attr_diez++;
						        		else if(pos_attr_to_mark == 11) attr_once++;
										        	
						        		attr_value_cs.setString(2, cbTable.getSelectedItem().toString());
						        		attr_value_cs.setString(3, attributes.elementAt(pos_attr_to_mark));
						        		attr_value_cs.setString(4, rset_pk.getString ("ID"));
							        	attr_value_cs.execute ();
							        	number_value = attr_value_cs.getFloat(1);
										        	
							        	absolute_value = Math.abs(number_value.intValue());
							        	binary_main = Integer.toBinaryString(absolute_value);
							        	if (binary_main.length()>=(Integer.valueOf(tfLSB.getText()))+Integer.valueOf(tfMSB.getText()))
							        		attrToMark.addElement(pos_attr_to_mark);
						        	}else{
						        		attrToMark.clear();
						        		havValues.clear();
										        	
						        		if(attr_value_cs.isClosed()){
						        			attr_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_VALUE_OF_ATTR (?,?,?)}");
						        			attr_value_cs.registerOutParameter (1, Types.FLOAT);
						        		}
										        	
						        		for (int i = 0; i < attributes.size(); i++) {
						        			attr_value_cs.setString(2, cbTable.getSelectedItem().toString());
						        			attr_value_cs.setString(3, attributes.elementAt(i));
						        			attr_value_cs.setString(4, rset_pk.getString ("ID"));
						        			attr_value_cs.execute ();
						        			number_value = attr_value_cs.getFloat(1);
						        			
						        			absolute_value = Math.abs(number_value.intValue());
						        			binary_main = Integer.toBinaryString(absolute_value);
											        	
						        			if (binary_main.length()>=(Integer.valueOf(tfLSB.getText()))+Integer.valueOf(tfMSB.getText())){
						        				new_binary = binary_main.substring(0, binary_main.length() - (new BigInteger(tfLSB.getText()).intValue())) ;
						        				real_value = Integer.parseInt(new_binary,2);
											        		
						        				if(hav_value_cs.isClosed()){
						        					hav_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call CREATE_HAV (?,?,?)}");
						        					hav_value_cs.registerOutParameter (1, Types.INTEGER);
						        				}
												        	
						        				hav_value_cs.setString(2, rset_pk.getString ("ID"));
						        				hav_value_cs.setString (3,tfPrivateKey.getText());
						        				hav_value_cs.setFloat(4, real_value);
						        				hav_value_cs.execute ();
												        	
						        				hav_value = hav_value_cs.getString(1);
												        	
						        				//System.out.println("PK: " + rset_pk.getString ("ID") + "  ARRT: " + attributes.elementAt(i)+ "  MOD: " + new BigInteger(hav_value).mod(new BigInteger(spAF.getValue().toString())));
												        	
						        				if(new BigInteger(hav_value).mod(new BigInteger(spAF.getValue().toString()))== BigInteger.valueOf(new Long(0))){                       
						        					attrToMark.addElement(i);
						        					havValues.addElement(hav_value);
						        				}
						        			}
						        		}
						        	}
									        	
					    			for (int i = 0; i < attrToMark.size(); i++) {
					    				embedded = false;
					    				attr_to_mark = attributes.elementAt(attrToMark.elementAt(i));
					    				attr_value_cs.setString(2, cbTable.getSelectedItem().toString());
					    				attr_value_cs.setString(3, attr_to_mark);
					    				attr_value_cs.setString(4, rset_pk.getString ("ID"));
					    				attr_value_cs.execute ();
					    				number_value = attr_value_cs.getFloat(1);
										        	
					    				absolute_value = Math.abs(number_value.intValue());
					    				if(number_value < 0){ signed = true;}
					    				
					    				binary_main = Integer.toBinaryString(absolute_value);
					    				
					    				temp_decimal = new Integer(String.valueOf(number_value).substring(String.valueOf(number_value).indexOf ( "." )+1));
					    				if(temp_decimal!=0){ /*binary_decimal = Integer.toBinaryString(temp_decimal);*/}
										        	
					    				if(new Integer(tfLSB.getText())*2 < binary_main.length()){
					    					if(rbOrigMeth.isSelected()){
					    						height_pos = rset_pk.getInt("H"); 
					    						width_pos =  rset_pk.getInt("W"); 
										        		   
					    						//msb_pos = (new BigInteger(rset_pk.getString ("VPK")).mod(new BigInteger(tfMSB.getText()))).intValue() + 1;
					    						//lsb_pos = (new BigInteger(rset_pk.getString ("VPK")).mod(new BigInteger(tfLSB.getText()))).intValue() + 1;
											        		
					    						msb_pos = rset_pk.getInt("MSB_POS") + 1  ;
					    						lsb_pos = rset_pk.getInt("LSB_POS") + 1  ;
											        		
					    						lsb_total++;
					    						if(lsb_pos == 0) lsb_cero++;
					    						else if(lsb_pos == 1) lsb_uno++;
					    						else if(lsb_pos == 2) lsb_dos++;
					    						else if(lsb_pos == 3) lsb_tres++;
					    						else if(lsb_pos == 4) lsb_cuatro++;
					    						else if(lsb_pos == 5) lsb_cinco++;
					    						else if(lsb_pos == 6) lsb_seis++;
											        		
					    						msb_total++;
					    						if(msb_pos == 0) msb_cero++;
					    						else if(msb_pos == 1) msb_uno++;
					    						else if(msb_pos == 2) msb_dos++;
					    						else if(msb_pos == 3) msb_tres++;
					    						else if(msb_pos == 4) msb_cuatro++;
					    						else if(msb_pos == 5) msb_cinco++;
					    						else if(msb_pos == 6) msb_seis++;
					    					}else{
					    						if(attr_pos_cs.isClosed()){
					    							attr_pos_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_ATTR_POS (?,?,?,?,?,?)}");
					    							attr_pos_cs.registerOutParameter (1, OracleTypes.CURSOR);
					    						}
										        			
					    						attr_pos_cs.setString (2,havValues.elementAt(i));
					    						attr_pos_cs.setInt (3, (Integer)spAF.getValue());
					    						attr_pos_cs.setInt (4, imageHeight-1);
					    						attr_pos_cs.setInt (5, imageWidth-1);
					    						attr_pos_cs.setInt (6, Integer.valueOf(tfMSB.getText())-1);
					    						attr_pos_cs.setInt (7, Integer.valueOf(tfLSB.getText())-1);
										        			
					    						attr_pos_cs.execute ();
														
					    						ResultSet rset_attr_hav = (ResultSet)attr_pos_cs.getObject (1);
										        			
					    						while (rset_attr_hav.next ()){
					    							//// GORT: height_pos = rset_attr_hav.getInt("H"); 
					    							//// GORT: width_pos =  rset_attr_hav.getInt("W"); 
					    							
					    							msb_pos = rset_attr_hav.getInt("MSB_POS") + 1  ;
					    							lsb_pos = rset_attr_hav.getInt("LSB_POS") + 1  ;
					    							
					    							if (chbprob.isSelected()) {
					    								//Meta-mark selection
					    								
					    								String idd = rset_pk.getString("ID");
					    								long seed_gr = rset_pk.getLong("GR");
					    								
					    								if(checker.getTotalSum()>0) {
					    									
						    							Item itemm  = checker.getRandom(seed_gr);
						    							
						    							if (itemm.getRelProb().getCurrVal() != 0) {
						    							//if(itemm.getRelProb().getLeftSel()!=0)	{
						    								
						    								height_pos = itemm.getCoord().getY(); 
							    							width_pos =  itemm.getCoord().getX(); 
							    							
							    							if(((height_pos == 37) && (width_pos == 28)) || ((height_pos == 12) && (width_pos == 22))) {
							    								System.out.println("Probabilidad: " + itemm.getRelProb().getCurrVal());
							    								cta1++;
							    							}
							    							
							    							//Localization of index for the position of the meta-mark selected
							    							int key_index = 0;
							    							for (int kk = 0; kk < marks.size(); kk++) {
							    								if ((itemm.getCoord().getX() == marks.get(kk).getX()) && (itemm.getCoord().getY() == marks.get(kk).getY())) {
							    									key_index = kk;
							    									break;
																}
							    							}
							    							
							    							int cant = cantEmb.get(key_index)-1;
							    							cantEmb.set(key_index, cant);
							    							
							    							
							    							if(itemm.getRelProb().getCantEmb() > 5) {
							    								System.out.println("Left Sel: " + itemm.getRelProb().getLeftSel());
							    								System.out.println("Extendido: " + itemm.getRelProb().getExtVal());
								    							System.out.println("Current: " + itemm.getRelProb().getCurrVal());
								    							System.out.println("Projected: " + itemm.getRelProb().getProjVal());
							    							}
							    							
							    							ProbItem tempProItem = probH.get(key_index);
							    							//tempProItem.setCurrVal(probH.get(key_index).getCurrVal() - probH.get(key_index).getProjVal());
							    							tempProItem.setCurrVal(0);
							    							//tempProItem.setLeftSel(probH.get(key_index).getLeftSel()-1);
							    							//tempProItem.incCantEmb();
							    							
							    							
							    							//CurrVal(probH.get(key_index).getCurrVal() - probH.get(key_index).getProjVal());
							    							
							    							//System.out.println("Extendido: " + itemm.getRelProb().getExtVal());
							    							//System.out.println("Current: " + itemm.getRelProb().getCurrVal());
							    							//System.out.println("Projected: " + itemm.getRelProb().getProjVal());
							    							
							    							probH.set(key_index, tempProItem);
							    							probHBackup.set(tempProItem.getInitIndex(), tempProItem);
							    							
							    							//Restarting of PH probabilities array 
							    							for (int jj = 0; jj < probH.size(); jj++) {
						    									//probH.add(Float.valueOf(0));
							    								probH.set(jj, null);
							    							}
							    							
							    							//Rotation of the PH probability array. 
							    							for (int ss = 0; ss < marks.size(); ss++) {
							    								probH.set(key_index, probHBackup.get(ss));
							    								
							    								if(key_index < marks.size()-1) {
							    									key_index++;
							    								}else {
							    									key_index = 0;
							    								}
							    							}
							    							
							    							for (int jjj = 0; jjj < probH.size(); jjj++) {
						    									//probH.add(Float.valueOf(0));
							    								
							    								probH.get(jjj).setCurrVal(cantEmb.get(jjj) * probH.get(jjj).getProjVal());
							    								
							    							}
							    							
							    							/*for (int sss = 0; sss < marks.size(); sss++) {
							    								probHBackup.set(sss, probH.get(sss));
							    							}*/
							    							
							    							
							    							//GORT: actualiza aqui
							    							
									    					if((msb_pos - 1) < (binary_main.length()-lsb_pos)){ //creo que ya lo salva la condicion de qeu LSB + MSB <= Binary.length
									    						msb_value = Character.getNumericValue(binary_main.charAt(msb_pos-1));
														        			
									    						if((imageWidth * (height_pos) + width_pos) > 0  ){
									    								image_element = imageVector.elementAt(imageWidth * (height_pos) + width_pos);
									    							
									    							directVector.setElementAt(1, rset_pk.getInt("DIRECT_POS"));
									    							
									    							value_to_insert = image_element ^ msb_value;
														        				
									    							lsb_value = Character.getNumericValue(binary_main.charAt(binary_main.length()-lsb_pos));
														        				
									    							if (!(cbZDist.isSelected())) {
									    								new_binary = binary_main.substring(0, binary_main.length() - lsb_pos) + value_to_insert + binary_main.substring(binary_main.length() - lsb_pos+1, binary_main.length()) ;
									    								int min_pos = 1;
									    								if (cbMDist.isSelected()) {
									    									min_pos = Integer.valueOf(txMinLSB.getText());
									    								}
									    								new_binary = Util.minimizeVariation(binary_main, new_binary, lsb_pos, min_pos);
															        				
									    								//************************************* UPDATE BLOCK ********************************************
									    								if(updater.isClosed()){
									    									updater = dbConnection.getConnection().prepareCall ("{ call UPDATE_ATTR_AT (?,?,?,?)}");
									    								}			
															        				
									    								updater.setString(1, cbTable.getSelectedItem().toString());
									    								updater.setString (2, attr_to_mark);
															        				
									    								real_value = Integer.parseInt(new_binary,2);
					
									    								//System.out.println("ID: " + rset_pk.getString ("ID") + "-----Attribute:" + attrToMark.elementAt(i));
															        				
									    								if(signed){
									    									real_value = 0-real_value;
									    									signed = false;
									    								}
									    								updater.setInt(3, real_value);
									    								updater.setInt(4, Integer.valueOf(rset_pk.getString ("ID")));
									    								
									    								selectorCounter.set(imageWidth * (height_pos) + width_pos, selectorCounter.elementAt(imageWidth * (height_pos) + width_pos) + 1);
									    								counter[height_pos][width_pos] = counter[height_pos][width_pos] +1;
									    								
										    							
									    								if(selectorCounter.elementAt(imageWidth * (height_pos) + width_pos) + 1 > 5)
									    									cta2++;
									    									
									    								
									    								updater.execute ();
															        				
									    								if(value_to_insert==lsb_value){
									    									match++;
									    								}else{
									    									unmatch++;
									    								}
															        				
									    								
															        				
									    								lsb_value = 2;
									    								
									    								embedded = true;
									    								
									    								tot_Mark++;
															        				
									    								if(!rset_pk.getString ("ID").equals(new String(prevID))){
									    									tupl_mark++;
									    									prevID = rset_pk.getString ("ID");
									    								}
															        				
									    							} 
														        				
									    							if (embedded) {
									    								embeddedInfo[height_pos][width_pos] = image_element;
									    							}
									    						}	
									    					}
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
							    							
									    					System.out.println(checker.getTotalSum());
							    							checker.setProbDistr(probH);
							    							/////System.out.println(checker.getTotalSum());
					    								} 
					    							}
					    								
					    							}else {
					    								height_pos = rset_attr_hav.getInt("H"); 
						    							width_pos =  rset_attr_hav.getInt("W"); 
					    							}
					    						}
															 
					    						//msb_pos = (new BigInteger(havValues.elementAt(i)).mod(new BigInteger(tfMSB.getText()))).intValue() + 1;
					    						//lsb_pos = (new BigInteger(havValues.elementAt(i)).mod(new BigInteger(tfLSB.getText()))).intValue() + 1;
												        	 
					    						lsb_total++;
					    						if(lsb_pos == 0) lsb_cero++;
					    						else if(lsb_pos == 1) lsb_uno++;
					    						else if(lsb_pos == 2) lsb_dos++;
					    						else if(lsb_pos == 3) lsb_tres++;
					    						else if(lsb_pos == 4) lsb_cuatro++;
					    						else if(lsb_pos == 5) lsb_cinco++;
					    						else if(lsb_pos == 6) lsb_seis++;
												        		
					    						msb_total++;
					    						if(msb_pos == 0) msb_cero++;
					    						else if(msb_pos == 1) msb_uno++;
					    						else if(msb_pos == 2) msb_dos++;
					    						else if(msb_pos == 3) msb_tres++;
					    						else if(msb_pos == 4) msb_cuatro++;
					    						else if(msb_pos == 5) msb_cinco++;
					    						else if(msb_pos == 6) msb_seis++;
					    					}
					    					//if(proceed) {  
					    					if (!chbprob.isSelected()) {
						    					if((msb_pos - 1) < (binary_main.length()-lsb_pos)){ //creo que ya lo salva la condicion de qeu LSB + MSB <= Binary.length
						    						msb_value = Character.getNumericValue(binary_main.charAt(msb_pos-1));
											        			
						    						if((imageWidth * (height_pos) + width_pos) > 0  ){
						    							
						    							
						    							//DIRECTTTTTT
						    							int temp = rset_pk.getInt("DIRECT_POS");
						    							int keyyy = rset_pk.getInt("ID");
						    							
						    							if(chbDirSel.isSelected()){
						    								image_element = imageVector.elementAt(rset_pk.getInt("DIRECT_POS"));
						    								height_pos = rset_pk.getInt("DIRECT_POS")/imageWidth;
						    								width_pos = rset_pk.getInt("DIRECT_POS")-height_pos*imageWidth;
						    							}
						    							else
						    								image_element = imageVector.elementAt(imageWidth * (height_pos) + width_pos);
						    							
						    							directVector.setElementAt(1, rset_pk.getInt("DIRECT_POS"));
						    							
						    							value_to_insert = image_element ^ msb_value;
											        				
						    							lsb_value = Character.getNumericValue(binary_main.charAt(binary_main.length()-lsb_pos));
											        				
						    							if (!(cbZDist.isSelected())) {
						    								new_binary = binary_main.substring(0, binary_main.length() - lsb_pos) + value_to_insert + binary_main.substring(binary_main.length() - lsb_pos+1, binary_main.length()) ;
						    								int min_pos = 1;
						    								if (cbMDist.isSelected()) {
						    									min_pos = Integer.valueOf(txMinLSB.getText());
						    								}
						    								new_binary = Util.minimizeVariation(binary_main, new_binary, lsb_pos, min_pos);
												        				
						    								//************************************* UPDATE BLOCK ********************************************
						    								if(updater.isClosed()){
						    									updater = dbConnection.getConnection().prepareCall ("{ call UPDATE_ATTR_AT (?,?,?,?)}");
						    								}			
												        				
						    								updater.setString(1, cbTable.getSelectedItem().toString());
						    								updater.setString (2, attr_to_mark);
												        				
						    								real_value = Integer.parseInt(new_binary,2);
		
						    								//System.out.println("ID: " + rset_pk.getString ("ID") + "-----Attribute:" + attrToMark.elementAt(i));
												        				
						    								if(signed){
						    									real_value = 0-real_value;
						    									signed = false;
						    								}
						    								updater.setInt(3, real_value);
						    								updater.setInt(4, Integer.valueOf(rset_pk.getString ("ID")));
						    								
						    								selectorCounter.set(imageWidth * (height_pos) + width_pos, selectorCounter.elementAt(imageWidth * (height_pos) + width_pos) + 1);
						    								counter[height_pos][width_pos]=counter[height_pos][width_pos]+1;
						    									
						    								
						    								updater.execute ();
												        				
						    								if(value_to_insert==lsb_value){
						    									match++;
						    								}else{
						    									unmatch++;
						    								}
												        				
						    								if(attr_to_mark.equals("ASPECT")){
						    									countAsp++;
						    									if(value_to_insert==lsb_value)countAsp_2++;
						    								}
						    								else if(attr_to_mark.equals("ELEVATION")){
						    									countElev++;
						    									if(value_to_insert==lsb_value)countElev_2++;
						    								}
						    								else if(attr_to_mark.equals("SLOPE")){
						    									countSlope++;
						    									if(value_to_insert==lsb_value)countSlope_2++;
						    								}
						    								else if(attr_to_mark.equals("HOR_DIST_TO_HYDROLOGY")){
						    									countHDHy++;
						    									if(value_to_insert==lsb_value)countHDHy_2++;
						    								}
						    								else if(attr_to_mark.equals("VERT_DIST_TO_HYDROLOGY")){
						    									countVDHy++;
						    									if(value_to_insert==lsb_value)countVDHy_2++;
						    								}
						    								else if(attr_to_mark.equals("HOR_DIST_TO_ROADWAYS")){
						    									countRW++;
						    									if(value_to_insert==lsb_value)countRW_2++;
						    								}
						    								else if(attr_to_mark.equals("HILLSHADE_9AM")){
						    									countHill9++;
						    									if(value_to_insert==lsb_value)countHill9_2++;
						    								}
						    								else if(attr_to_mark.equals("HILLSHADE_NOON")){
						    									countHill12++;
						    									if(value_to_insert==lsb_value)countHill12_2++;
						    								}
						    								else if(attr_to_mark.equals("HILLSHADE_3PM")){
						    									countHill3++;
						    									if(value_to_insert==lsb_value)countHill3_2++;
						    								}
						    								else if(attr_to_mark.equals("HOR_DIST_TO_FIRE_POINTS")){
						    									countFP++;
						    									if(value_to_insert==lsb_value)countFP_2++;
						    								}
												        				
						    								lsb_value = 2;
						    								
						    								embedded = true;
						    								
						    								tot_Mark++;
												        				
						    								if(!rset_pk.getString ("ID").equals(new String(prevID))){
						    									tupl_mark++;
						    									prevID = rset_pk.getString ("ID");
						    								}
												        				
						    							}else{
						    								for (int j = 0; j < binary_main.length(); j++) {
						    									if (binary_main.charAt(j) == value_to_insert) {
						    										j = binary_main.length();
						    										embedded = true;
						    									}
						    								}
						    							}
											        				
						    							if (embedded) {
						    								embeddedInfo[height_pos][width_pos] = image_element;
						    							}
						    						}	
						    					}
					    					}
					    				}
									}
									        	
						        	tot_gen_attr = tot_gen_attr + attrToMark.size();
									        	
						        	//updater.close();
						        	//updater = null;tfhytrfy
									        	
						        	DecimalFormat dff = new DecimalFormat("##.##");
									dff.setRoundingMode(RoundingMode.DOWN);
						     /*  ///// 	tfMatch.setText(String.valueOf(dff.format(Float.valueOf(match)*100/Float.valueOf(match+unmatch))));
						        	tfUnmatch.setText(String.valueOf(dff.format(Float.valueOf(unmatch)*100/Float.valueOf(match+unmatch))));
					        					
						        	txCountAsp.setText(String.valueOf(countAsp));
						        	txCountElev.setText(String.valueOf(countElev));
						        	txCountSlope.setText(String.valueOf(countSlope));
						        	txCountHDHy.setText(String.valueOf(countHDHy));
						        	txCountVDHy.setText(String.valueOf(countVDHy));
						        	txCountRW.setText(String.valueOf(countRW));
						        	txCountHill9.setText(String.valueOf(countHill9));
						        	txCountHill12.setText(String.valueOf(countHill12));
						        	txCountHill3.setText(String.valueOf(countHill3));
						        	txCountFP.setText(String.valueOf(countFP));
									        	
						        	txCountAsp_2.setText(String.valueOf(countAsp_2));
						        	txCountElev_2.setText(String.valueOf(countElev_2));
						        	txCountSlope_2.setText(String.valueOf(countSlope_2));
						        	txCountHDHy_2.setText(String.valueOf(countHDHy_2));
						        	txCountVDHy_2.setText(String.valueOf(countVDHy_2));
						        	txCountRW_2.setText(String.valueOf(countRW_2));
						        	txCountHill9_2.setText(String.valueOf(countHill9_2));
						        	txCountHill12_2.setText(String.valueOf(countHill12_2));
						        	txCountHill3_2.setText(String.valueOf(countHill3_2));
						        	txCountFP_2.setText(String.valueOf(countFP_2));        /////// */
					    		}
									        
					    	} catch (Exception e) {
					    		e.printStackTrace();
					    	}finally {
					    		updater.close();
					    		if(hav_value_cs!=null)
					    			hav_value_cs.close();
					    		if(attr_pos_cs!=null)
					    			attr_pos_cs.close();
					    		attr_value_cs.close();
					    	}
					    }
					}
				    //}
				    
					//RESULTS REPORT BUIL SECTION***************************
					
					int no_embedd = 0;
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(embeddedInfo[j][i] != -1)
	 							no_embedd++;
	 					}
	 				}
					
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
					
					
					
					
					
					/*int black = 0;
					int white = 0;
					int red = 0;
					
					for (int i = 0; i < imageWidth; i++) {
	 					for (int j = 0; j < imageHeight; j++) {
	 						if(originalImage[j][i] == 1){
	 							img.setRGB(i, j, Color.BLACK.getRGB());
	 							black++;
	 						} else if(originalImage[j][i] == 0){
	 								 img.setRGB(i, j, Color.WHITE.getRGB());
	 								 white++;
	 							   } else {
	 									img.setRGB(i, j, Color.RED.getRGB());
	 									red++;
	 								}
 						}
	 				}
					
					System.out.println("BLACK - PIXELS: " + String.valueOf(black));
					System.out.println("WHITE - PIXELS: " + String.valueOf(white));
					System.out.println("RED --- PIXELS: " + String.valueOf(red)); */
					
					Image scaledInstance = img.getScaledInstance(lbImgEmbedded.getWidth(), lbImgEmbedded.getHeight(), Image.SCALE_DEFAULT);
					ImageIcon imageIcon = new ImageIcon(scaledInstance);
					lbImgEmbedded.setIcon(imageIcon);
					
					txEmbeddedPx.setText(String.valueOf(no_embedd));
					txTotalTupl.setText(String.valueOf(dbConnection.getNoRows(cbTable.getSelectedItem().toString(), tfPrivateKey.getText(), Integer.valueOf(tfFractTupl.getText()))));
					//txTotalTupl.setText(String.valueOf(total_tuplas_marcadas));
					
					DecimalFormat df = new DecimalFormat("##.##");
					df.setRoundingMode(RoundingMode.DOWN);
					txEPP.setText(String.valueOf(df.format(Float.valueOf(txEmbeddedPx.getText())*100/Float.valueOf(txTotalPx.getText()))));
					
					txTotalTuples.setText(String.valueOf(dbConnection.getAllRows(cbTable.getSelectedItem().toString())));
					txMTP.setText(String.valueOf(df.format(Float.valueOf(txTotalTupl.getText())*100/Float.valueOf(txTotalTuples.getText()))));
					
					if (tupl_mark != 0) {
						txPromMarkedAttr.setText(String.valueOf(tot_Mark/tupl_mark));
					}
					
					
					txTotAttr.setText(String.valueOf(tot_gen_attr));
					
					if (Integer.valueOf(txTotalTupl.getText()) != 0) {
						txPromAttr.setText(String.valueOf(tot_gen_attr/Integer.valueOf(txTotalTupl.getText())));
					}
					txTotMark.setText(String.valueOf(tot_Mark));
					
			//		System.out.println("-----------------------------------------------");
					
	        		/*System.out.println("LSB_0: " + lsb_cero);
	        		System.out.println("LSB_1: " + lsb_uno);
	        		System.out.println("LSB_2: " + lsb_dos);
	        		System.out.println("LSB_3: " + lsb_tres);
	        		System.out.println("LSB_4: " + lsb_cuatro);
	        		System.out.println("LSB_5: " + lsb_cinco);
	        		System.out.println("LSB_6: " + lsb_seis);
	        		System.out.println("-----------------------------------------------");
	        		System.out.println("LSB_TOTAL: " + lsb_total);*/
	        		
			//		System.out.println("-----------------------------------------------");
					
	        	/*	System.out.println("MSB_0: " + msb_cero);
	        		System.out.println("MSB_1: " + msb_uno);
	        		System.out.println("MSB_2: " + msb_dos);
	        		System.out.println("MSB_3: " + msb_tres);
	        		System.out.println("MSB_4: " + msb_cuatro);
	        		System.out.println("MSB_5: " + msb_cinco);
	        		System.out.println("MSB_6: " + msb_seis);
	        		System.out.println("-----------------------------------------------");
	        		System.out.println("MSB_TOTAL: " + msb_total);*/
	        		
	  //      		System.out.println("-----------------------------------------------");
	        		
      //  			System.out.println("-----------------------------------------------");
					
	        		/*System.out.println("ATTR_0: " + attr_cero*100/attr_total);
	        		System.out.println("ATTR_1: " + attr_uno*100/attr_total);
	        		System.out.println("ATTR_2: " + attr_dos*100/attr_total);
	        		System.out.println("ATTR_3: " + attr_tres*100/attr_total);
	        		System.out.println("ATTR_4: " + attr_cuatro*100/attr_total);
	        		System.out.println("ATTR_5: " + attr_cinco*100/attr_total);
	        		System.out.println("ATTR_6: " + attr_seis*100/attr_total);
	        		System.out.println("ATTR_7: " + attr_siete*100/attr_total);
	        		System.out.println("ATTR_8: " + attr_ocho*100/attr_total);
	        		System.out.println("ATTR_9: " + attr_nueve*100/attr_total);
	        		System.out.println("ATTR_10: " + attr_diez*100/attr_total);
	        		System.out.println("ATTR_11: " + attr_once*100/attr_total);
	        		System.out.println("-----------------------------------------------");
	        		System.out.println("ATTR_TOTAL: " + attr_total);*/
	        		
        //			System.out.println("-----------------------------------------------");
	        		
			    	//ENDING TIME
				    Calendar cal1 = Calendar.getInstance();
			    	cal1.getTime();
			    	SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
			    	System.out.println ("PROCESS COMPLETED AT: "+sdf1.format(cal1.getTime()));
			    	System.out.println("-----------------------------------------------");
			    	Toolkit.getDefaultToolkit().beep();
			    	
			    //	System.out.println ("TOTAL OF KEYS: "+totKeys);
			    	System.out.println ("KEYS THAT PASED: "+passedKets);
			    	
			    	int tempo = 0;
			    	for (int i = 0; i < directVector.size(); i++) {
						if (directVector.elementAt(i) == 1) {
							tempo ++;
						}
					}
			   // 	System.out.println ("TEMPO ES: "+tempo);
			    	
			//		JOptionPane.showMessageDialog(null, "Embedding Process completed...");
					
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					try {
						rset_pk.close();
						ids.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				
			} //END REPEATER
				
				int max = selectorCounter.elementAt(0);
				int max2 = counter[0][0];
				int alt = 0;
				int anch = 0;
				
				double avg = 0;
				double avg2 = 0;
				
				double primm = 0;
				double primm2 = 0;
				
				double stddev = 0;
				double stddev2 = 0;
				
				for (int i = 0; i < selectorCounter.size(); i++) {
					avg = avg + selectorCounter.elementAt(i);
					if (selectorCounter.elementAt(i) > max) {
						max = selectorCounter.elementAt(i);
					}
				}
				
				for (int i = 0; i < imageWidth; i++) {
 					for (int j = 0; j < imageHeight; j++) {
 						avg2 = avg2 + counter[j][i];
 						if (counter[j][i] > max2) {
 							max2 = counter[j][i];
 							alt = j;
 							anch = i;
 									
 						}
 					}
 				}
				
				primm = avg/selectorCounter.size();
				primm2 = avg2/(imageWidth * imageHeight);
				
				
				for (int i = 0; i < selectorCounter.size(); i++) {
					stddev = stddev + Math.pow((selectorCounter.elementAt(i) - primm), 2);
				}
				
				for (int i = 0; i < imageWidth; i++) {
 					for (int j = 0; j < imageHeight; j++) {
 						stddev2 = stddev2 + Math.pow((counter[j][i] - primm2), 2);
 					}
 				}
				
				stddev = stddev/selectorCounter.size();
				stddev = Math.sqrt(stddev);
				
				stddev2 = stddev2/(imageWidth * imageHeight);
				stddev2 = Math.sqrt(stddev2);
				
				System.out.println("Maximum embedding: " + max);
				System.out.println("Maximum2 embedding: " + max2);
				
				System.out.println("Altura: " + alt);
				System.out.println("Anchura: " + anch);
				
				
				System.out.println("Averaggee: " + primm);
				System.out.println("StdDevvvv: " + stddev);
				
				System.out.println("Averaggee222: " + primm2);
				System.out.println("StdDevvvv222: " + stddev2);
				
				
				System.out.println("permitidos en probabilidades: " + cta1);
				System.out.println("permitidos en updates: " + cta2);
				
				System.out.println("END OF EXPERIMENT");
			}
		});
		getContentPane().add(btnStart);
		
		JButton btnExit = new JButton("Close");
		btnExit.setBounds(609, 208, 89, 23);
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
		
		pnResults = new JPanel();
		pnResults.setLayout(null);
		pnResults.setBorder(BorderFactory.createTitledBorder(null, "  Results of the Embedding Process ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnResults.setBounds(10, 282, 688, 273);
		getContentPane().add(pnResults);
		
		JPanel pnCapacity = new JPanel();
		pnCapacity.setBounds(10, 23, 503, 239);
		pnResults.add(pnCapacity);
		pnCapacity.setLayout(null);
		pnCapacity.setBorder(BorderFactory.createTitledBorder(null, "  Embedded Pixels ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		
		lbImgEmbedded = new JLabel();
		lbImgEmbedded.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lbImgEmbedded.setBounds(10, 30, 184, 164);
		lbImgEmbedded.setOpaque(true);
		lbImgEmbedded.setBackground(Color.WHITE);
		pnCapacity.add(lbImgEmbedded);
		
		JLabel lblTotal = new JLabel("Total:");
		lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotal.setBounds(401, 33, 38, 14);
		pnCapacity.add(lblTotal);
		
		JLabel lblEmbeddeds = new JLabel("Embedded:");
		lblEmbeddeds.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEmbeddeds.setBounds(356, 61, 83, 14);
		pnCapacity.add(lblEmbeddeds);
		
		txTotalPx = new JTextField();
		txTotalPx.setEditable(false);
		txTotalPx.setText("0");
		txTotalPx.setColumns(10);
		txTotalPx.setBounds(442, 30, 51, 20);
		pnCapacity.add(txTotalPx);
		
		txEmbeddedPx = new JTextField();
		txEmbeddedPx.setEditable(false);
		txEmbeddedPx.setText("0");
		txEmbeddedPx.setColumns(10);
		txEmbeddedPx.setBounds(442, 58, 51, 20);
		pnCapacity.add(txEmbeddedPx);
		
		JPanel pnLegend = new JPanel();
		pnLegend.setBounds(331, 133, 162, 95);
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
		
		JLabel label_3 = new JLabel("Missed Pixel");
		label_3.setHorizontalAlignment(SwingConstants.LEFT);
		label_3.setBounds(33, 26, 95, 14);
		pnLegend.add(label_3);
		
		JLabel lblEncrustedPixelval = new JLabel("Encrusted Pixel (0)");
		lblEncrustedPixelval.setHorizontalAlignment(SwingConstants.LEFT);
		lblEncrustedPixelval.setBounds(33, 49, 119, 14);
		pnLegend.add(lblEncrustedPixelval);
		
		JLabel lblEncrustedPixelval_1 = new JLabel("Encrusted Pixel (1)");
		lblEncrustedPixelval_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblEncrustedPixelval_1.setBounds(33, 67, 119, 14);
		pnLegend.add(lblEncrustedPixelval_1);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBackground(Color.BLACK);
		panel_1.setBounds(14, 67, 15, 15);
		pnLegend.add(panel_1);
		
		JLabel label_4 = new JLabel("___________________");
		label_4.setHorizontalAlignment(SwingConstants.LEFT);
		label_4.setBounds(14, 32, 138, 14);
		label_4.setOpaque(true);
		pnLegend.add(label_4);
		
		JLabel label_5 = new JLabel("Percent:");
		label_5.setHorizontalAlignment(SwingConstants.RIGHT);
		label_5.setBounds(356, 96, 83, 14);
		pnCapacity.add(label_5);
		
		txEPP = new JTextField();
		txEPP.setText("0");
		txEPP.setEditable(false);
		txEPP.setColumns(10);
		txEPP.setBounds(442, 93, 51, 20);
		pnCapacity.add(txEPP);
		
		JLabel label_8 = new JLabel("__________________");
		label_8.setOpaque(true);
		label_8.setHorizontalAlignment(SwingConstants.LEFT);
		label_8.setBounds(366, 73, 130, 14);
		pnCapacity.add(label_8);
		
		JPanel pnMarkedRelation = new JPanel();
		pnMarkedRelation.setBounds(523, 23, 155, 130);
		pnResults.add(pnMarkedRelation);
		pnMarkedRelation.setLayout(null);
		pnMarkedRelation.setBorder(BorderFactory.createTitledBorder(null, "  Relation Tuples ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		
		JLabel label_1 = new JLabel("Marked:");
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		label_1.setBounds(17, 58, 59, 14);
		pnMarkedRelation.add(label_1);
		
		txTotalTupl = new JTextField();
		txTotalTupl.setText("0");
		txTotalTupl.setEditable(false);
		txTotalTupl.setColumns(10);
		txTotalTupl.setBounds(79, 55, 64, 20);
		pnMarkedRelation.add(txTotalTupl);
		
		
		JLabel label = new JLabel("Total:");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBounds(12, 30, 64, 14);
		pnMarkedRelation.add(label);
		
		txTotalTuples = new JTextField();
		txTotalTuples.setText("0");
		txTotalTuples.setEditable(false);
		txTotalTuples.setColumns(10);
		txTotalTuples.setBounds(79, 27, 64, 20);
		pnMarkedRelation.add(txTotalTuples);
		
		JLabel label_2 = new JLabel("Percent:");
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		label_2.setBounds(17, 96, 59, 14);
		pnMarkedRelation.add(label_2);
		
		txMTP = new JTextField();
		txMTP.setText("0");
		txMTP.setEditable(false);
		txMTP.setColumns(10);
		txMTP.setBounds(79, 93, 64, 20);
		pnMarkedRelation.add(txMTP);
		
		JLabel label_9 = new JLabel("__________________");
		label_9.setOpaque(true);
		label_9.setHorizontalAlignment(SwingConstants.LEFT);
		label_9.setBounds(16, 71, 131, 14);
		pnMarkedRelation.add(label_9);
		
		JLabel label_6 = new JLabel("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		label_6.setOpaque(true);
		label_6.setHorizontalAlignment(SwingConstants.LEFT);
		label_6.setBounds(12, 255, 686, 14);
		getContentPane().add(label_6);
		
		JPanel pnAlgorithms = new JPanel();
		pnAlgorithms.setLayout(null);
		pnAlgorithms.setBorder(BorderFactory.createTitledBorder(null, "  Algorithm Variations ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnAlgorithms.setBounds(513, 9, 185, 188);
		getContentPane().add(pnAlgorithms);
		
		rbOrigMeth = new JRadioButton("Original Method");
		rbOrigMeth.setBounds(10, 24, 152, 23);
		pnAlgorithms.add(rbOrigMeth);
		
		rbExtMeth = new JRadioButton("Ext. (Mult Attr)");
		rbExtMeth.setSelected(true);
		rbExtMeth.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange() == ItemEvent.SELECTED) {
					cbZDist.setEnabled(true);
					cbMDist.setEnabled(true);
					txMinLSB.setEditable(true);
					cbLsbM.setEnabled(true);
					spAF.setEnabled(true);
			    }
			    else if (arg0.getStateChange() == ItemEvent.DESELECTED) {
			    	cbZDist.setEnabled(false);
					cbMDist.setEnabled(false);
					txMinLSB.setEditable(false);
					cbLsbM.setEnabled(false);
					
					cbZDist.setSelected(false);
					cbMDist.setSelected(false);
					txMinLSB.setText(String.valueOf(0));
					cbLsbM.setSelected(false);
					spAF.setEnabled(false);
			    }
			}
		});
		rbExtMeth.setBounds(10, 62, 109, 23);
		pnAlgorithms.add(rbExtMeth);
		
		bgMethod.add(rbOrigMeth);
		bgMethod.add(rbExtMeth);
		
		JLabel label_7 = new JLabel("----------------------------");
		label_7.setOpaque(true);
		label_7.setHorizontalAlignment(SwingConstants.LEFT);
		label_7.setBounds(15, 46, 138, 14);
		pnAlgorithms.add(label_7);
		
		cbZDist = new JCheckBox("Zero-Distortion");
		cbZDist.setEnabled(false);
		cbZDist.setBounds(36, 84, 119, 23);
		pnAlgorithms.add(cbZDist);
		
		cbMDist = new JCheckBox("Min-Distortion");
		cbMDist.setEnabled(false);
		cbMDist.setBounds(36, 105, 119, 23);
		pnAlgorithms.add(cbMDist);
		
		bgExtOpt.add(cbZDist);
		bgExtOpt.add(cbMDist);
		
		JLabel lblBits = new JLabel("Bits:");
		lblBits.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBits.setBounds(30, 131, 52, 14);
		pnAlgorithms.add(lblBits);
		
		txMinLSB = new JTextField();
		txMinLSB.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				if (Integer.valueOf(txMinLSB.getText())>Integer.valueOf(tfLSB.getText())) {
					txMinLSB.setText(tfLSB.getText());
				}
				if (cbLsbM.isSelected()) {
					txMinLSB.setText(tfLSB.getText());
				}
			}
		});
		txMinLSB.setText("0");
		txMinLSB.setEditable(false);
		txMinLSB.setColumns(10);
		txMinLSB.setBounds(86, 128, 26, 20);
		pnAlgorithms.add(txMinLSB);
		
		cbLsbM = new JCheckBox("LSB");
		cbLsbM.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				txMinLSB.setText(tfLSB.getText());
			}
		});
		cbLsbM.setEnabled(false);
		cbLsbM.setBounds(114, 127, 52, 23);
		pnAlgorithms.add(cbLsbM);
		
		JLabel lblFractionOfAttributes = new JLabel("Fraction of Attributes:");
		lblFractionOfAttributes.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFractionOfAttributes.setBounds(10, 160, 119, 14);
		pnAlgorithms.add(lblFractionOfAttributes);
		
		spAF = new JSpinner();
		spAF.setModel(new SpinnerNumberModel(9, 1, 10, 1));
		spAF.setBounds(137, 157, 38, 20);
		pnAlgorithms.add(spAF);
		
		JLabel lblMarkedElements = new JLabel("Prom. Attr. Sel. x Tuples:");
		lblMarkedElements.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMarkedElements.setBounds(220, 569, 146, 14);
		getContentPane().add(lblMarkedElements);
		
		txPromAttr = new JTextField();
		txPromAttr.setText("0");
		txPromAttr.setEditable(false);
		txPromAttr.setColumns(10);
		txPromAttr.setBounds(368, 566, 86, 20);
		getContentPane().add(txPromAttr);
		
		JLabel label_10 = new JLabel("Marked Elements:");
		label_10.setHorizontalAlignment(SwingConstants.RIGHT);
		label_10.setBounds(220, 592, 146, 14);
		getContentPane().add(label_10);
		
		txTotMark = new JTextField();
		txTotMark.setText("0");
		txTotMark.setEditable(false);
		txTotMark.setColumns(10);
		txTotMark.setBounds(368, 589, 86, 20);
		getContentPane().add(txTotMark);
		
		txTotAttr = new JTextField();
		txTotAttr.setText("0");
		txTotAttr.setEditable(false);
		txTotAttr.setColumns(10);
		txTotAttr.setBounds(612, 583, 86, 20);
		getContentPane().add(txTotAttr);
		
		txPromMarkedAttr = new JTextField();
		txPromMarkedAttr.setText("0");
		txPromMarkedAttr.setEditable(false);
		txPromMarkedAttr.setColumns(10);
		txPromMarkedAttr.setBounds(612, 560, 86, 20);
		getContentPane().add(txPromMarkedAttr);
		
		JLabel lblPromAttrMark = new JLabel("Prom. Attr. Mark. x Tuples:");
		lblPromAttrMark.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPromAttrMark.setBounds(464, 563, 146, 14);
		getContentPane().add(lblPromAttrMark);
		
		JLabel lblTotAttr = new JLabel("Tot. Attr:");
		lblTotAttr.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotAttr.setBounds(464, 586, 146, 14);
		getContentPane().add(lblTotAttr);
		
		JLabel lblMatch = new JLabel("Match:");
		lblMatch.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMatch.setBounds(10, 566, 65, 14);
		getContentPane().add(lblMatch);
		
		JLabel lblUnmatch = new JLabel("Unmatch:");
		lblUnmatch.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUnmatch.setBounds(10, 592, 65, 14);
		getContentPane().add(lblUnmatch);
		
		tfMatch = new JTextField();
		tfMatch.setText("0");
		tfMatch.setEditable(false);
		tfMatch.setColumns(10);
		tfMatch.setBounds(84, 566, 65, 20);
		getContentPane().add(tfMatch);
		
		tfUnmatch = new JTextField();
		tfUnmatch.setText("0");
		tfUnmatch.setEditable(false);
		tfUnmatch.setColumns(10);
		tfUnmatch.setBounds(85, 589, 65, 20);
		getContentPane().add(tfUnmatch);
		
		JLabel lblTotal_1 = new JLabel("Total:");
		lblTotal_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTotal_1.setBounds(20, 617, 65, 14);
		getContentPane().add(lblTotal_1);
		
		tfTotalMatch = new JTextField();
		tfTotalMatch.setText("0");
		tfTotalMatch.setEditable(false);
		tfTotalMatch.setColumns(10);
		tfTotalMatch.setBounds(84, 620, 65, 20);
		getContentPane().add(tfTotalMatch);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(BorderFactory.createTitledBorder(null, "  Usability Analysis  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		panel_2.setBounds(708, 257, 254, 266);
		getContentPane().add(panel_2);
		
		txCountFP = new JTextField();
		txCountFP.setText("0");
		txCountFP.setEditable(false);
		txCountFP.setColumns(10);
		txCountFP.setBounds(135, 38, 47, 20);
		panel_2.add(txCountFP);
		
		txCountRW = new JTextField();
		txCountRW.setText("0");
		txCountRW.setEditable(false);
		txCountRW.setColumns(10);
		txCountRW.setBounds(135, 60, 47, 20);
		panel_2.add(txCountRW);
		
		JLabel lblHdfirepoints = new JLabel("HD-Fire-Points:");
		lblHdfirepoints.setHorizontalAlignment(SwingConstants.RIGHT);
		lblHdfirepoints.setBounds(10, 42, 121, 14);
		panel_2.add(lblHdfirepoints);
		
		JLabel lblHdroadways = new JLabel("HD-Roadways:");
		lblHdroadways.setHorizontalAlignment(SwingConstants.RIGHT);
		lblHdroadways.setBounds(10, 63, 121, 14);
		panel_2.add(lblHdroadways);
		
		JLabel lblElevation = new JLabel("Elevation:");
		lblElevation.setHorizontalAlignment(SwingConstants.RIGHT);
		lblElevation.setBounds(10, 85, 121, 14);
		panel_2.add(lblElevation);
		
		txCountElev = new JTextField();
		txCountElev.setText("0");
		txCountElev.setEditable(false);
		txCountElev.setColumns(10);
		txCountElev.setBounds(135, 82, 47, 20);
		panel_2.add(txCountElev);
		
		JLabel label_16 = new JLabel("HD-Hydrology:");
		label_16.setHorizontalAlignment(SwingConstants.RIGHT);
		label_16.setBounds(10, 107, 121, 14);
		panel_2.add(label_16);
		
		txCountHDHy = new JTextField();
		txCountHDHy.setText("0");
		txCountHDHy.setEditable(false);
		txCountHDHy.setColumns(10);
		txCountHDHy.setBounds(135, 104, 47, 20);
		panel_2.add(txCountHDHy);
		
		JLabel lblAspect = new JLabel("Aspect:");
		lblAspect.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAspect.setBounds(10, 129, 121, 14);
		panel_2.add(lblAspect);
		
		JLabel lblHillshadepm = new JLabel("Hillshade-3pm:");
		lblHillshadepm.setHorizontalAlignment(SwingConstants.RIGHT);
		lblHillshadepm.setBounds(10, 173, 121, 14);
		panel_2.add(lblHillshadepm);
		
		JLabel lblVdhydrology = new JLabel("VD-Hydrology:");
		lblVdhydrology.setHorizontalAlignment(SwingConstants.RIGHT);
		lblVdhydrology.setBounds(10, 151, 121, 14);
		panel_2.add(lblVdhydrology);
		
		JLabel lblSlope = new JLabel("Slope:");
		lblSlope.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSlope.setBounds(10, 238, 121, 14);
		panel_2.add(lblSlope);
		
		JLabel lblHillshadeam = new JLabel("Hillshade-9am:");
		lblHillshadeam.setHorizontalAlignment(SwingConstants.RIGHT);
		lblHillshadeam.setBounds(10, 195, 121, 14);
		panel_2.add(lblHillshadeam);
		
		JLabel lblHillshadenoon = new JLabel("Hillshade-noon:");
		lblHillshadenoon.setHorizontalAlignment(SwingConstants.RIGHT);
		lblHillshadenoon.setBounds(10, 217, 121, 14);
		panel_2.add(lblHillshadenoon);
		
		txCountHill9 = new JTextField();
		txCountHill9.setText("0");
		txCountHill9.setEditable(false);
		txCountHill9.setColumns(10);
		txCountHill9.setBounds(135, 192, 47, 20);
		panel_2.add(txCountHill9);
		
		txCountAsp = new JTextField();
		txCountAsp.setText("0");
		txCountAsp.setEditable(false);
		txCountAsp.setColumns(10);
		txCountAsp.setBounds(135, 126, 47, 20);
		panel_2.add(txCountAsp);
		
		txCountVDHy = new JTextField();
		txCountVDHy.setText("0");
		txCountVDHy.setEditable(false);
		txCountVDHy.setColumns(10);
		txCountVDHy.setBounds(135, 148, 47, 20);
		panel_2.add(txCountVDHy);
		
		txCountHill3 = new JTextField();
		txCountHill3.setText("0");
		txCountHill3.setEditable(false);
		txCountHill3.setColumns(10);
		txCountHill3.setBounds(135, 170, 47, 20);
		panel_2.add(txCountHill3);
		
		txCountSlope = new JTextField();
		txCountSlope.setText("0");
		txCountSlope.setEditable(false);
		txCountSlope.setColumns(10);
		txCountSlope.setBounds(135, 236, 47, 20);
		panel_2.add(txCountSlope);
		
		txCountHill12 = new JTextField();
		txCountHill12.setText("0");
		txCountHill12.setEditable(false);
		txCountHill12.setColumns(10);
		txCountHill12.setBounds(135, 214, 47, 20);
		panel_2.add(txCountHill12);
		
		txCountFP_2 = new JTextField();
		txCountFP_2.setText("0");
		txCountFP_2.setEditable(false);
		txCountFP_2.setColumns(10);
		txCountFP_2.setBounds(192, 38, 47, 20);
		panel_2.add(txCountFP_2);
		
		txCountRW_2 = new JTextField();
		txCountRW_2.setText("0");
		txCountRW_2.setEditable(false);
		txCountRW_2.setColumns(10);
		txCountRW_2.setBounds(192, 60, 47, 20);
		panel_2.add(txCountRW_2);
		
		txCountElev_2 = new JTextField();
		txCountElev_2.setText("0");
		txCountElev_2.setEditable(false);
		txCountElev_2.setColumns(10);
		txCountElev_2.setBounds(192, 82, 47, 20);
		panel_2.add(txCountElev_2);
		
		txCountHDHy_2 = new JTextField();
		txCountHDHy_2.setText("0");
		txCountHDHy_2.setEditable(false);
		txCountHDHy_2.setColumns(10);
		txCountHDHy_2.setBounds(192, 104, 47, 20);
		panel_2.add(txCountHDHy_2);
		
		txCountAsp_2 = new JTextField();
		txCountAsp_2.setText("0");
		txCountAsp_2.setEditable(false);
		txCountAsp_2.setColumns(10);
		txCountAsp_2.setBounds(192, 126, 47, 20);
		panel_2.add(txCountAsp_2);
		
		txCountVDHy_2 = new JTextField();
		txCountVDHy_2.setText("0");
		txCountVDHy_2.setEditable(false);
		txCountVDHy_2.setColumns(10);
		txCountVDHy_2.setBounds(192, 148, 47, 20);
		panel_2.add(txCountVDHy_2);
		
		txCountHill3_2 = new JTextField();
		txCountHill3_2.setText("0");
		txCountHill3_2.setEditable(false);
		txCountHill3_2.setColumns(10);
		txCountHill3_2.setBounds(192, 170, 47, 20);
		panel_2.add(txCountHill3_2);
		
		txCountHill9_2 = new JTextField();
		txCountHill9_2.setText("0");
		txCountHill9_2.setEditable(false);
		txCountHill9_2.setColumns(10);
		txCountHill9_2.setBounds(192, 192, 47, 20);
		panel_2.add(txCountHill9_2);
		
		txCountHill12_2 = new JTextField();
		txCountHill12_2.setText("0");
		txCountHill12_2.setEditable(false);
		txCountHill12_2.setColumns(10);
		txCountHill12_2.setBounds(192, 214, 47, 20);
		panel_2.add(txCountHill12_2);
		
		txCountSlope_2 = new JTextField();
		txCountSlope_2.setText("0");
		txCountSlope_2.setEditable(false);
		txCountSlope_2.setColumns(10);
		txCountSlope_2.setBounds(192, 236, 47, 20);
		panel_2.add(txCountSlope_2);
		
		JPanel pnVPK = new JPanel();
		pnVPK.setLayout(null);
		pnVPK.setBorder(BorderFactory.createTitledBorder(null, "    Use VPK ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
		pnVPK.setBounds(708, 11, 420, 230);
		getContentPane().add(pnVPK);
		
		JCheckBox cbVPK = new JCheckBox();
		cbVPK.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				 if(arg0.getStateChange() == ItemEvent.SELECTED) { 
					 rbSScheme.setEnabled(true);
					 rbEScheme.setEnabled(true);
					 rbMScheme.setEnabled(true);
					 rbOScheme.setEnabled(true);
			     } else { 
			    	 rbSScheme.setEnabled(false);
			    	 rbEScheme.setEnabled(false);
			    	 rbMScheme.setEnabled(false);
			    	 rbOScheme.setEnabled(false);
			     };
			}
		});
		
		cbVPK.setBounds(60, -3, 21, 23);
		pnVPK.add(cbVPK);
		
		rbSScheme = new JRadioButton("S-Scheme");
		rbSScheme.setEnabled(false);
		rbSScheme.setSelected(true);
		rbSScheme.setBounds(18, 23, 105, 23);
		rbSScheme.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				 if(arg0.getStateChange() == ItemEvent.SELECTED) { 
					 cbAttributes.setEnabled(true);
					 txMSBSSheme.setEnabled(true);
			     } else { 
			    	 cbAttributes.setEnabled(false);
			    	 txMSBSSheme.setEnabled(false);
			     };
			}
		});
		pnVPK.add(rbSScheme);
		
		rbEScheme = new JRadioButton("E-Scheme");
		rbEScheme.setEnabled(false);
		rbEScheme.setBounds(18, 97, 105, 23);
		pnVPK.add(rbEScheme);
		
		rbMScheme = new JRadioButton("M-Scheme");
		rbMScheme.setEnabled(false);
		rbMScheme.setBounds(18, 130, 105, 23);
		pnVPK.add(rbMScheme);
		
		rbOScheme = new JRadioButton("O-Scheme");
		rbOScheme.setEnabled(false);
		rbOScheme.setBounds(18, 166, 105, 23);
		pnVPK.add(rbOScheme);
		
		bgVPK.add(rbSScheme);
		bgVPK.add(rbEScheme);
		bgVPK.add(rbMScheme);
		bgVPK.add(rbOScheme);
		
		cbAttributes = new JComboBox<String>();
		cbAttributes.setModel(new DefaultComboBoxModel<String>(new String[] {"ELEVATION","ASPECT","SLOPE","HOR_DIST_TO_HYDROLOGY","VERT_DIST_TO_HYDROLOGY","HOR_DIST_TO_ROADWAYS", "HILLSHADE_9AM", "HILLSHADE_NOON", "HILLSHADE_3PM", "HOR_DIST_TO_FIRE_POINTS"}));
		cbAttributes.setSelectedIndex(0);
		cbAttributes.setBounds(139, 23, 168, 20);
		pnVPK.add(cbAttributes);
		
		JLabel label_11 = new JLabel("MSB:");
		label_11.setHorizontalAlignment(SwingConstants.RIGHT);
		label_11.setBounds(331, 26, 31, 14);
		pnVPK.add(label_11);
		
		txMSBSSheme = new JTextField();
		txMSBSSheme.setText("3");
		txMSBSSheme.setColumns(10);
		txMSBSSheme.setBounds(363, 23, 37, 20);
		pnVPK.add(txMSBSSheme);
		
		JLabel label_12 = new JLabel("----------------------------------------------------------------------------------------------");
		label_12.setOpaque(true);
		label_12.setHorizontalAlignment(SwingConstants.LEFT);
		label_12.setBounds(22, 84, 392, 14);
		pnVPK.add(label_12);
		
		JLabel label_13 = new JLabel("----------------------------------------------------------------------------------------------");
		label_13.setOpaque(true);
		label_13.setHorizontalAlignment(SwingConstants.LEFT);
		label_13.setBounds(22, 115, 392, 14);
		pnVPK.add(label_13);
		
		txMSBMScheme = new JTextField();
		txMSBMScheme.setText("3");
		txMSBMScheme.setColumns(10);
		txMSBMScheme.setBounds(363, 130, 37, 20);
		pnVPK.add(txMSBMScheme);
		
		JLabel label_14 = new JLabel("MSB:");
		label_14.setHorizontalAlignment(SwingConstants.RIGHT);
		label_14.setBounds(331, 133, 31, 14);
		pnVPK.add(label_14);
		
		txMSChemeNoAttr = new JTextField();
		txMSChemeNoAttr.setText("2");
		txMSChemeNoAttr.setColumns(10);
		txMSChemeNoAttr.setBounds(284, 130, 37, 20);
		pnVPK.add(txMSChemeNoAttr);
		
		JLabel lblNoAttr = new JLabel("No. Attr:");
		lblNoAttr.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNoAttr.setBounds(216, 133, 67, 14);
		pnVPK.add(lblNoAttr);
		
		JLabel label_15 = new JLabel("----------------------------------------------------------------------------------------------");
		label_15.setOpaque(true);
		label_15.setHorizontalAlignment(SwingConstants.LEFT);
		label_15.setBounds(22, 152, 392, 14);
		pnVPK.add(label_15);
		
		JLabel lblMsbf = new JLabel("MSBF:");
		lblMsbf.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMsbf.setBounds(139, 169, 53, 14);
		pnVPK.add(lblMsbf);
		
		txMSBF = new JTextField();
		txMSBF.setText("3");
		txMSBF.setColumns(10);
		txMSBF.setBounds(193, 166, 37, 20);
		pnVPK.add(txMSBF);
		
		JButton btnVPKGen = new JButton("Generate VPK");
		btnVPKGen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CallableStatement vpkModifier = null;
				
				try {
					if (rbSScheme.isSelected()) { // S-SCHEME
						if(vpkModifier == null){
							vpkModifier = dbConnection.getConnection().prepareCall ("{ call GENERATE_S_I (?,?,?)}");
						}
						vpkModifier.setString(1, cbTable.getSelectedItem().toString());
						vpkModifier.setString (2, cbAttributes.getSelectedItem().toString());
						vpkModifier.setInt(3, Integer.valueOf(txMSBSSheme.getText()));
						vpkModifier.execute ();
					}else{
						if (rbMScheme.isSelected()) {  //M-SCHEME
							if(vpkModifier == null){
								vpkModifier = dbConnection.getConnection().prepareCall ("{ call GENERATE_M (?,?,?,?,?)}");
							}
							vpkModifier.setString(1, cbTable.getSelectedItem().toString());
							vpkModifier.setInt(2, Integer.valueOf(txMSBMScheme.getText()));
							vpkModifier.setInt(3, Integer.valueOf(tfLSB.getText()));
							vpkModifier.setInt(4, Integer.valueOf(txMSChemeNoAttr.getText()));
							vpkModifier.setString (5,tfPrivateKey.getText());
							vpkModifier.execute ();
						}else{
							if (rbOScheme.isSelected()) {  //OUR-SCHEME
								if(vpkModifier == null){
									vpkModifier = dbConnection.getConnection().prepareCall ("{ call GENERATE_O (?,?)}");
								}
								vpkModifier.setString(1, cbTable.getSelectedItem().toString());
								vpkModifier.setInt(2, Integer.valueOf(txMSBF.getText()));
								vpkModifier.execute ();
							}else{ //E-SCHEME
								
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					try {
						vpkModifier.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					
				}
				
					
			}
		});
		btnVPKGen.setBounds(18, 196, 126, 23);
		pnVPK.add(btnVPKGen);
		
		JButton btnRespVPK = new JButton("CREATE VPK RESP.");
		btnRespVPK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
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
		});
		btnRespVPK.setBounds(263, 196, 147, 23);
		pnVPK.add(btnRespVPK);
		
		cbESCheme = new JCheckBox("Use E-Scheme");
		cbESCheme.setBounds(125, 97, 105, 23);
		pnVPK.add(cbESCheme);
		
		txMSBESCheme = new JTextField();
		txMSBESCheme.setText("3");
		txMSBESCheme.setColumns(10);
		txMSBESCheme.setBounds(270, 98, 37, 20);
		pnVPK.add(txMSBESCheme);
		
		JLabel label_17 = new JLabel("MSB:");
		label_17.setHorizontalAlignment(SwingConstants.RIGHT);
		label_17.setBounds(234, 101, 31, 14);
		pnVPK.add(label_17);
		
		JLabel lblAttribute = new JLabel("Attribute:");
		lblAttribute.setHorizontalAlignment(SwingConstants.CENTER);
		lblAttribute.setBounds(985, 259, 138, 14);
		getContentPane().add(lblAttribute);
		
		cbDelAttr = new JComboBox<String>();
		cbDelAttr.setModel(new DefaultComboBoxModel<String>(new String[] {"ELEVATION","ASPECT","SLOPE","HOR_DIST_TO_HYDROLOGY","VERT_DIST_TO_HYDROLOGY","HOR_DIST_TO_ROADWAYS", "HILLSHADE_9AM", "HILLSHADE_NOON", "HILLSHADE_3PM", "HOR_DIST_TO_FIRE_POINTS"}));
		cbDelAttr.setSelectedIndex(0);
		cbDelAttr.setBounds(986, 281, 137, 20);
		getContentPane().add(cbDelAttr);
		
		JButton btnDelAttr = new JButton("Delete Attribute");
		btnDelAttr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CallableStatement csSimul = null;
				
				getVPKResp();
				getAttrResp();
					
				try {
					if(csSimul == null){
						csSimul = dbConnection.getConnection().prepareCall ("{ call SIMULATE_RESP (?,?,?,?)}");
					}
					csSimul.setString(1, cbTable.getSelectedItem().toString());
					csSimul.setString(2, cbDelAttr.getSelectedItem().toString());
					csSimul.setInt(3, Integer.valueOf(txMinSim.getText()));
					csSimul.setInt(4, Integer.valueOf(txMaxSim.getText()));
					csSimul.execute ();
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					try {
						csSimul.close();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
				
			}
		});
		btnDelAttr.setBounds(985, 312, 138, 23);
		getContentPane().add(btnDelAttr);
		
		JLabel lblTop = new JLabel("Max:");
		lblTop.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTop.setBounds(985, 349, 53, 14);
		getContentPane().add(lblTop);
		
		txMaxSim = new JTextField();
		txMaxSim.setText("3");
		txMaxSim.setColumns(10);
		txMaxSim.setBounds(1039, 346, 37, 20);
		getContentPane().add(txMaxSim);
		
		JLabel lblMin = new JLabel("Min:");
		lblMin.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMin.setBounds(985, 377, 53, 14);
		getContentPane().add(lblMin);
		
		txMinSim = new JTextField();
		txMinSim.setText("3");
		txMinSim.setColumns(10);
		txMinSim.setBounds(1039, 374, 37, 20);
		getContentPane().add(txMinSim);
		
		JButton btnRestValues = new JButton("Restore Values");
		btnRestValues.setBounds(985, 451, 138, 23);
		getContentPane().add(btnRestValues);
		
		
		chbDirSel.setBounds(215, 613, 119, 23);
		getContentPane().add(chbDirSel);
		
		JButton btnSampler = new JButton("Sampler");
		btnSampler.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				String temp = "=(";
				for (int i = 9; i < 3046; i++) {
					temp = temp + "(R" + i + "/$Q" + i + "/$Q" + i + ")+";
					//(R9/$Q9/$Q9)+
				}
				
				temp = temp + "R3048)*100/30000"; 
				System.out.println("-----------------------------------------------");
				System.out.println(temp);
				System.out.println("-----------------------------------------------");
			}
		});
		btnSampler.setBounds(761, 613, 138, 23);
		getContentPane().add(btnSampler);
		
		txMaxIter = new JTextField();
		txMaxIter.setText("1");
		txMaxIter.setColumns(10);
		txMaxIter.setBounds(1039, 525, 37, 20);
		getContentPane().add(txMaxIter);
		
		JLabel lblMaxIterante = new JLabel("Max Iterante:");
		lblMaxIterante.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMaxIterante.setBounds(940, 528, 89, 14);
		getContentPane().add(lblMaxIterante);
		
		JButton btnNewButton = new JButton("Item by Prob.");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				 
				
				/*
					
				for (int i = 0; i < 1000000; i++) {
					
				
				
				Item temp  = checker.getRandom();
				no_sim++;
				
				if (temp.getCoord().getX()==2 && temp.getCoord().getY()==0) {
					cant_A++;
				} else if (temp.getCoord().getX()==0 && temp.getCoord().getY()==2) {
					cant_B++;
				}  
				
				
				
				
				
				}
				
				System.out.println("General: " + no_sim);
				System.out.println("AAAAAAA: " + cant_A/no_sim);
				System.out.println("BBBBBBB: " + cant_B/no_sim);
				
				System.out.println("------------------------------------------------");*/
			}
		});
		btnNewButton.setBounds(949, 613, 127, 23);
		getContentPane().add(btnNewButton);
		
		JLabel lblMaximumAllowedRecurrent = new JLabel("Maximum Allowed Recurrent Embedding:");
		lblMaximumAllowedRecurrent.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMaximumAllowedRecurrent.setBounds(761, 569, 223, 14);
		getContentPane().add(lblMaximumAllowedRecurrent);
		
		txRecurrentMax = new JTextField();
		txRecurrentMax.setText("3");
		txRecurrentMax.setColumns(10);
		txRecurrentMax.setBounds(985, 566, 37, 20);
		getContentPane().add(txRecurrentMax);
		
		chbprob = new JCheckBox("Probability-based Embedding");
		chbprob.setBounds(708, 540, 238, 23);
		getContentPane().add(chbprob);
		
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
				cbTable.setModel(new DefaultComboBoxModel<String>(new String[] {"COVERTYPE_A","COVERTYPE_B","COVERTYPE_C","COVERTYPE_D","COVERTYPE_E","COVERTYPE_F", "COVERTYPE_G", "COVERTYPE_H", "COVERTYPE_I", "COVERTYPE_J", "COVERTYPE_K"}));
				cbTable.setSelectedIndex(0);
				this.cbTable.setBounds(123, 8, 171, 20);
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
		return tfPrivateKey.getText();
	}
	
	public String getTupleFract(){
		return tfFractTupl.getText();
	}
	
	public int getAttrFract(){
		return Integer.valueOf(spAF.getValue().toString());
	}
	
	public boolean isExtAF(){
		return rbExtMeth.isSelected();
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
	
	public void getAttrResp(){
		CallableStatement attrResp = null;
		
		try {
			if(attrResp == null){
				attrResp = dbConnection.getConnection().prepareCall ("{ call RESP_ATTR (?,?)}");
			}
			attrResp.setString(1, cbTable.getSelectedItem().toString());
			attrResp.setString(2, cbDelAttr.getSelectedItem().toString());
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
	
	
	
	
	
	/*
	
	public int getMainMaxRecurr(){
		return this.main_MaxRecurr;
	}
	
	public Vector<Coord> getMainMarks(){
		return this.mainMarks;
	}
	
	public Vector<ProbItem> getMainProbH(){
		return this.mainProbH;
	}
	
	public Vector<Integer> getMainCantEmb(){
		return this.mainCantEmb;
	}
	
	public Vector<ProbItem> getMainProbHBackup(){
		return this.mainProbHBackup;
	}*/
	
	public int[][] getMainCounter(){
		return this.counter2;
	}
}
