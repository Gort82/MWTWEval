package wrd.ibw.gui;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import javax.swing.JButton;
import wrd.ibw.da.DBConnection;
import wrd.ibw.utils.Util;
import wrd.ibw.utils.WordCarrier;
import wrd.ibw.wn.WNCaller;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
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
import javax.swing.JRadioButton;

public class FrmExtractWM extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;

	private DBConnection dbConnection = null;

	private JComboBox<String> cbTable = null;
	private JTextField tfFractTupl;
	private JTextField tfPrivateKey;
	private JButton btnSave;

	private WNCaller tempWNCaller = null;

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

	private DefaultTableModel carriersModel = null;

	private int imageWidth = 0;
	private int imageHeight = 0;

	private BufferedImage img = null;
	private BufferedImage img2 = null;

	private JTextField tfHeight;
	private JTextField tfWidth;

	private int recoveredInfo[][];
	private Vector<Integer> mayorityInfo[][];



	private int respRecoveredInfo[][];

	public int[][] getImageMatrix(){
		return recoveredInfo;
	}

	private JTextField txTotalPx;
	private JTextField txExtractedPx;
	private JTextField txEPP;
	private JTextField txTotalTupl;
	private JTextField txTotalTuples;
	private JTextField txMTP;

	/**
	 * @wbp.nonvisual location=552,209
	 */
	private JTextField txBinaryLength;
	private JTextField txSentLength;
	private JTable tbCarriers = null;
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
			this.setSize(697,488);
			this.getContentPane().setLayout(null);

			JLabel lblRelationToMark = new JLabel("Relation:");
			lblRelationToMark.setHorizontalAlignment(SwingConstants.RIGHT);
			lblRelationToMark.setBounds(10, 11, 85, 14);
			getContentPane().add(lblRelationToMark);
			getContentPane().add(getJCBTable());

			JLabel lblFractionOrRelations = new JLabel("TF");
			lblFractionOrRelations.setHorizontalAlignment(SwingConstants.RIGHT);
			lblFractionOrRelations.setBounds(152, 240, 32, 14);
			getContentPane().add(lblFractionOrRelations);

			tfFractTupl = new JTextField();
			tfFractTupl.setText(pTuplFract);
			tfFractTupl.setBounds(187, 237, 47, 20);
			getContentPane().add(tfFractTupl);
			tfFractTupl.setColumns(10);

			JPanel panel = new JPanel();
			panel.setLayout(null);
			panel.setBorder(BorderFactory.createTitledBorder(null, "  Carriers (Multi-Words)  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel.setBounds(10, 70, 224, 156);
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
			spCarriers.setBounds(10, 22, 204, 123);
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
			lblPrivateKey.setBounds(20, 240, 37, 14);
			getContentPane().add(lblPrivateKey);

			tfPrivateKey = new JTextField();
			tfPrivateKey.setText(pSecretKey);
			tfPrivateKey.setColumns(10);
			tfPrivateKey.setBounds(60, 237, 78, 20);
			getContentPane().add(tfPrivateKey);

			JPanel pnImageSelector = new JPanel();
			pnImageSelector.setLayout(null);
			pnImageSelector.setBorder(BorderFactory.createTitledBorder(null, "  Extracted Image  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			pnImageSelector.setBounds(291, 178, 387, 236);
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
			
						btnSave = new JButton("Save");
						btnSave.setBounds(288, 24, 85, 23);
						pnImageSelector.add(btnSave);
						btnSave.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								preSaveImg();
								correctImg();
								try {
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
												}
										}
									}

									ImageIO.write(img2, "bmp", new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"ext.bmp"));

									JOptionPane.showMessageDialog(null, "The extracted image was successfully saved...");

								} catch (Exception e2) {
									e2.printStackTrace();
								}
							}
						});




			DefaultTableModel model = new DefaultTableModel();
			model.addColumn("Get");
			model.addColumn("Name");
			model.addColumn("Type");

			JButton btnStart = new JButton("Extract");
			btnStart.setBounds(144, 286, 90, 23);
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

					String extListNounsB = null;        
					String extSynonymSetB = null;       


					String[] excluded = {"a", "at", "an", "as", "ar", "are", "be", "can", "coming", "come", "do", "going", "have", "in", "it", "might", "more", "now", "or", "one", "over", "out", "say", "us", "so", "today", "will"};

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

					String bruteParagraph = "";
					Synset synsetWord = null;
					Vector<String> substitutes = new Vector<String>();

					String lockStream = "";
					int lockSup = 0;
					Long sentKey = Long.valueOf(0);

					int height_pos = 0;
					int width_pos = 0;
					int image_element = 0;

					Vector<String> carrierAttr = new Vector<String>();
					for (int i = 0; i < tbCarriers.getRowCount(); i++) {
						if(tbCarriers.getModel().getValueAt(i, 0).equals(true)){
							carrierAttr.add(tbCarriers.getModel().getValueAt(i, 1).toString());
						}
					}

					CallableStatement csTuples = null;
					CallableStatement csCarriers = null;
					CallableStatement csSVK = null;
					CallableStatement csLoadCheck = null;

					ResultSet rsTuples = null;
					ResultSet rsAttrVK = null;

					int MAX_COTR = 0;

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

						csLoadCheck = dbConnection.getConnection().prepareCall ("{ ? = call RTW_GET_EXT_BCHECK (?,?)}");
						csLoadCheck.registerOutParameter (1, OracleTypes.VARCHAR);
						csLoadCheck.setString (2,cbTable.getSelectedItem().toString());

						while (rsTuples.next ()){
							//System.out.println(rsTuples.getInt ("ID"));
							csLoadCheck.setInt(3, rsTuples.getInt ("ID"));

							try {
								if(rsTuples.getInt ("TUPL_FACTOR")==0){
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

										if((bruteParagraph != null) && (bruteParagraph.trim().length() != 0)){

											Vector<String> sentences = Util.splitParagraph(bruteParagraph);

											if(sentences.size() >= Integer.valueOf(txMSPar.getText())){  

												for (int j = 0; j < sentences.size(); j++) {

													extListNounsB = "";   
													extSynonymSetB = "";  

													String[] words = sentences.elementAt(j).split("\\s+");
													
													if(words.length > 0) {

													if (words[words.length-1].substring(words[words.length-1].length()-1).equals(".")) {
														words[words.length-1] = words[words.length-1].substring(0, words[words.length-1].length()-1);
														sentences.set(j, sentences.elementAt(j).substring(0,sentences.elementAt(j).length()-1));
													}


													if (words.length >= Integer.valueOf(txMWSent.getText())) {  
														lockStream = Util.getLockStream(tempWNCaller, sentences.elementAt(j), pWordRoles, considerRest, rsTuples.getInt ("ID"));
														lockSup = Util.parseString(lockStream,Integer.valueOf(txBinaryLength.getText()));

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
														}

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

															synsetWord = tempWNCaller.getSynsetWord(currentCarrier, sentences.elementAt(j), 1);

															if(synsetWord != null){

																substitutes = tempWNCaller.getSubsSet(synsetWord, Integer.valueOf(txMinCarrSize.getText()));

																int count = 0;
																while (!synUpperCase && count < substitutes.size()) {
																	if(!substitutes.elementAt(count).equals(substitutes.elementAt(count).toLowerCase()))
																		synUpperCase = true;
																	count++;
																} 

																if(!synUpperCase){

																	if((imageWidth * (height_pos) + width_pos) > 0  ){
																		if (substitutes.size()>1) {

																			for (int m = 0; m < substitutes.size(); m++) {                                
																				extSynonymSetB = extSynonymSetB + "|" + substitutes.elementAt(m);      
																			}                                                                             


																			if(currentCarrier.getWord().equals(substitutes.elementAt(0))){
																				image_element = 1;
																			}
																			else{
																				image_element = 0;
																			}

																			Vector<Integer> tempStore = mayorityInfo[height_pos][width_pos];

																			csLoadCheck.execute ();

																			tempStore.add(image_element);
																			mayorityInfo[height_pos][width_pos] = tempStore;
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
									}
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
							finally {
								if(csCarriers != null)
									csCarriers.close();
								if(csSVK != null)
									csSVK.close();

							}
						}

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
							}
						}

						Image scaledInstance = img.getScaledInstance(lbImageViewer.getWidth(), lbImageViewer.getHeight(), Image.SCALE_DEFAULT);
						ImageIcon imageIcon = new ImageIcon(scaledInstance);
						lbImageViewer.setIcon(imageIcon);

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

						System.out.println("-----------------------------------------------");
						Calendar cal1 = Calendar.getInstance();
						cal1.getTime();
						SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
						System.out.println ("PROCESS COMPLETED AT: "+sdf1.format(cal1.getTime()));
						System.out.println("-----------------------------------------------");
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null, "Extraction Process completed...");

					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						try {
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
			btnExit.setBounds(556, 425, 122, 23);
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
			panel_5.setBounds(10, 275, 124, 147);
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

			JPanel panel_7 = new JPanel();
			panel_7.setLayout(null);
			panel_7.setBorder(BorderFactory.createTitledBorder(null, "  Role of each Word  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_7.setBounds(466, 9, 212, 158);
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

			JLabel label_15 = new JLabel("Min. Words:");
			label_15.setHorizontalAlignment(SwingConstants.RIGHT);
			label_15.setBounds(20, 42, 104, 14);
			getContentPane().add(label_15);

			txSentLength = new JTextField();
			txSentLength.setText("10");
			txSentLength.setColumns(10);
			txSentLength.setBounds(127, 39, 38, 20);
			getContentPane().add(txSentLength);

			JButton btnAttrs = new JButton("...");
			btnAttrs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					try {
						ResultSetMetaData rsmd = dbConnection.getAttrMD(cbTable.getSelectedItem().toString());
						int cons = 0;
						carriersModel.setRowCount(0);

						for (int i = 0; i < rsmd.getColumnCount(); i++) {
							if (rsmd.getColumnType(i+1)== OracleTypes.NUMBER) {
								if ((!rsmd.getColumnName(i+1).equals("ID")) && (!rsmd.getColumnName(i+1).equals("VPK"))) {
								}
							} else {
								cons = dbConnection.getMultiWordAttr(cbTable.getSelectedItem().toString(), rsmd.getColumnName(i+1), Integer.valueOf(txSentLength.getText())-1);

								if (cons == 0) {
									//model.addRow(new Object[]{Boolean.FALSE, rsmd.getColumnName(i+1), "STRING"});
									carriersModel.addRow(new Object[]{Boolean.FALSE, rsmd.getColumnName(i+1)});
								} else {
									//model.addRow(new Object[]{Boolean.TRUE, rsmd.getColumnName(i+1), "STRING"});
									carriersModel.addRow(new Object[]{Boolean.TRUE, rsmd.getColumnName(i+1)});
								}

							}
						}

					} catch (Exception e2) {
						e2.printStackTrace();
					}

				}
			});
			btnAttrs.setBounds(184, 36, 50, 23);
			getContentPane().add(btnAttrs);

			JPanel panel_5_1 = new JPanel();
			panel_5_1.setLayout(null);
			panel_5_1.setBorder(BorderFactory.createTitledBorder(null, " Sentence Selection ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_5_1.setBounds(244, 9, 212, 140);
			getContentPane().add(panel_5_1);

			JLabel label_20 = new JLabel("Minimum Sentences:");
			label_20.setBounds(34, 28, 127, 14);
			panel_5_1.add(label_20);
			label_20.setHorizontalAlignment(SwingConstants.RIGHT);

			JLabel label_12 = new JLabel("Minimum Carrier Size:");
			label_12.setBounds(34, 56, 126, 14);
			panel_5_1.add(label_12);
			label_12.setHorizontalAlignment(SwingConstants.RIGHT);

			JLabel label_21 = new JLabel("Minimum Words:");
			label_21.setBounds(33, 84, 127, 14);
			panel_5_1.add(label_21);
			label_21.setHorizontalAlignment(SwingConstants.RIGHT);

			JLabel label_13 = new JLabel("Binary Length:");
			label_13.setBounds(33, 112, 135, 14);
			panel_5_1.add(label_13);
			label_13.setHorizontalAlignment(SwingConstants.RIGHT);

			txBinaryLength = new JTextField();
			txBinaryLength.setBounds(169, 109, 32, 20);
			panel_5_1.add(txBinaryLength);
			txBinaryLength.setText("20");
			txBinaryLength.setColumns(10);

			txMWSent = new JTextField();
			txMWSent.setBounds(164, 81, 37, 20);
			panel_5_1.add(txMWSent);
			txMWSent.setText("5");
			txMWSent.setColumns(10);

			txMinCarrSize = new JTextField();
			txMinCarrSize.setBounds(164, 53, 37, 20);
			panel_5_1.add(txMinCarrSize);
			txMinCarrSize.setText("2");
			txMinCarrSize.setColumns(10);

			txMSPar = new JTextField();
			txMSPar.setBounds(165, 25, 37, 20);
			panel_5_1.add(txMSPar);
			txMSPar.setText("1");
			txMSPar.setColumns(10);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JComboBox<String> getJCBTable(){
		if(this.cbTable == null){
			try {
				this.cbTable = new JComboBox<String>();
				cbTable.setModel(new DefaultComboBoxModel<String>(new String[] {"TEX_DOCUMENTS","UNIVE_SYLLABUS"}));
				cbTable.setSelectedIndex(1);
				this.cbTable.setBounds(110, 8, 124, 20);
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
	
	private void preSaveImg() {
		respRecoveredInfo = new int[imageHeight][imageWidth];

		for (int i = 0; i < imageWidth; i++) {
			for (int j = 0; j < imageHeight; j++) {
				respRecoveredInfo[j][i] = recoveredInfo[j][i];
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
					}
			}
		}

		DecimalFormat df = new DecimalFormat("##.##");
		df.setRoundingMode(RoundingMode.DOWN);
		btnSave.setEnabled(true);
	}
	
	private void correctImg(){
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
	}
}
