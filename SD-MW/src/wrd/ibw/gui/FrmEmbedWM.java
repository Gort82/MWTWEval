package wrd.ibw.gui;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import java.awt.Color;
import javax.swing.JButton;
import wrd.ibw.da.DBConnection;
import wrd.ibw.utils.Util;
import wrd.ibw.utils.WordCarrier;
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
	private JTable tbCarriers = null;
	private DefaultTableModel carriersModel = null;
	private JScrollPane spCarriers = null;
	private JFileChooser fileChooser;
	private File imageFile = null;
	private Image imageInfo = null;
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
	private JTextField txSentLength;
	private JTextField txMSB;

	private ButtonGroup bgSub = null;
	private ButtonGroup bgVerb = null;
	private ButtonGroup bgAdj = null;
	private ButtonGroup bgAdv = null;
	private ButtonGroup bgOM = null;
	private JTextField txMWSent;
	private JTextField txLSB;
	private JCheckBox cbAbsVal = null;
	private JCheckBox cbIntPart = null;
	private JTextField txBinaryLength;
	private WNCaller tempWNCaller = null;
	private JTextField txMSPar;
	private JTextField txMinCarrSize;
	
	private JRadioButton rbOML = new JRadioButton("");
	private JRadioButton rbSubC = new JRadioButton("");
	private JRadioButton rbSubE = new JRadioButton(" Noun");
	private JRadioButton rbVerbC = new JRadioButton("");
	private JRadioButton rbVerbE = new JRadioButton(" Verbs");
	private JRadioButton rbAdjC = new JRadioButton("");
	private JRadioButton rbAdjE = new JRadioButton("Adjective");
	private JRadioButton rbAdvC = new JRadioButton("");
	private JRadioButton rbAdvE = new JRadioButton("Adverb");
	private JRadioButton rbSubL = new JRadioButton("");
	private JRadioButton rbVerbL = new JRadioButton("");
	private JRadioButton rbAdjL = new JRadioButton("");
	private JRadioButton rbAdvL = new JRadioButton("");

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
			this.setSize(658,558);
			this.getContentPane().setLayout(null);

			this.bgSub = new ButtonGroup();
			this.bgVerb = new ButtonGroup();
			this.bgAdj = new ButtonGroup();
			this.bgAdv = new ButtonGroup();
			this.bgOM = new ButtonGroup();

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
			tfFractTupl.setText("5");
			tfFractTupl.setBounds(172, 225, 38, 20);
			getContentPane().add(tfFractTupl);
			tfFractTupl.setColumns(10);

			JLabel lblPrivateKey = new JLabel("SK:");
			lblPrivateKey.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPrivateKey.setBounds(10, 228, 38, 14);
			getContentPane().add(lblPrivateKey);

			txSK = new JTextField();
			txSK.setText("Secu48102304782K");
			txSK.setColumns(10);
			txSK.setBounds(49, 225, 89, 20);
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

			spCarriers = new JScrollPane();
			spCarriers.setBounds(10, 22, 183, 123);
			panel.add(spCarriers);

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

			tbCarriers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			carriersModel = new DefaultTableModel();
			carriersModel.addColumn("Get");
			carriersModel.addColumn("Name");

			this.tbCarriers.setModel(carriersModel);
			this.tbCarriers.getColumnModel().getColumn(0).setPreferredWidth(37);
			this.tbCarriers.getColumnModel().getColumn(1).setPreferredWidth(128);
			this.tbCarriers.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			spCarriers.setViewportView(tbCarriers);

			txLSB = new JTextField();
			txLSB.setBounds(104, 162, 28, 20);
			txLSB.setText("1");
			txLSB.setColumns(10);

			JLabel lblLsb = new JLabel("LSB:");
			lblLsb.setBounds(70, 165, 32, 14);
			lblLsb.setHorizontalAlignment(SwingConstants.RIGHT);

			txMSB = new JTextField();
			txMSB.setBounds(43, 162, 28, 20);
			txMSB.setText("3");
			txMSB.setColumns(10);

			JLabel lblMsb = new JLabel("MSB:");
			lblMsb.setBounds(10, 165, 32, 14);
			lblMsb.setHorizontalAlignment(SwingConstants.RIGHT);

			this.cbAbsVal = new JCheckBox("Abs.");
			cbAbsVal.setBounds(138, 158, 47, 23);

			cbAbsVal.setEnabled(false);
			cbAbsVal.setSelected(true);
			this.cbIntPart = new JCheckBox("Int.");
			cbIntPart.setBounds(187, 158, 52, 23);

			cbIntPart.setSelected(true);
			cbIntPart.setEnabled(false);

			btnStart = new JButton("Embed");
			btnStart.setEnabled(false);
			btnStart.setBounds(121, 496, 89, 23);
			btnStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
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

					String bruteParagraph = "";
					Synset synsetWord = null;
					Vector<String> substitutes = new Vector<String>();

					String lockStream = "";
					String newWord = "";
					int lockSup = 0;
					Long sentKey = Long.valueOf(0);

					int height_pos = 0;
					int width_pos = 0;
					int image_element = 0;
					boolean embedded = false;

					Vector<Integer> minurcarr = new Vector<Integer>();

					Vector<Integer> metrStat = new Vector<Integer>();
					Vector<Double> metrFlags = new Vector<Double>();

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
					CallableStatement csLoadCheck = null;

					ResultSet rsTuples = null;
					ResultSet rsAttrVK = null;

					boolean final_dot = false;

					int MAX_COTR = 0;

					String embListNounsB = null;       ////////////////////////////////////
					String embNounB = null;            ////////////////////////////////////
					String embSynonymSetB = null;      ////////////////////////////////////

					double valMetr = 0;

					String[] excluded = {"a", "at", "an", "as", "ar", "are", "be", "can", "coming", "come", "do", "going", "have", "in", "it", "might", "more", "now", "or", "one", "over", "out", "say", "us", "so", "today", "will"};

					try {
						
						computeVPK();
						
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

						csLoadCheck = dbConnection.getConnection().prepareCall ("{ ? = call RTW_GET_EMB_BCHECK (?,?)}");
						csLoadCheck.registerOutParameter (1, OracleTypes.VARCHAR);
						csLoadCheck.setString(2, cbTable.getSelectedItem().toString());


						while (rsTuples.next ()){
							System.out.println(rsTuples.getInt ("ID"));

							//csLoadCheck.setString(2, "TEX_DOCUMENTS");
							csLoadCheck.setInt(3, rsTuples.getInt ("ID"));

							try {

								if(rsTuples.getInt ("TUPL_FACTOR")==0){
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

										if(bruteParagraph != null){
											//String sentences[] = Util.splitParagraph(bruteParagraph, rsTuples.getInt ("ID"), carrierAttr.elementAt(i));
											Vector<String> sentences = Util.splitParagraph(bruteParagraph);

											if(sentences.size() >= Integer.valueOf(txMSPar.getText())){ //check the number of sentences per paragraph

												for (int j = 0; j < sentences.size(); j++) {

													embListNounsB = "";  ///////////////////
													embNounB = "";       ///////////////////  
													embSynonymSetB = ""; /////////////////// 

													String[] words = sentences.elementAt(j).split("\\s+");

													if(words.length > 0) {
													
													final_dot = false;

													/*if((rsTuples.getInt ("ID") == 1873) || (rsTuples.getInt ("ID") == 1874)) {
														System.out.println(rsTuples.getInt ("ID"));
														System.out.println(words);
													}*/
													
													
													
													if (words[words.length-1].substring(words[words.length-1].length()-1).equals(".")) {
														words[words.length-1] = words[words.length-1].substring(0, words[words.length-1].length()-1);
														sentences.set(j, sentences.elementAt(j).substring(0,sentences.elementAt(j).length()-1));
														final_dot = true;
													}

													if (words.length >= Integer.valueOf(txMWSent.getText())) { //check the number of words per sentence

														lockStream = Util.getLockStream(tempWNCaller, sentences.elementAt(j), pWordRoles, considerRest, rsTuples.getInt ("ID"));
														lockSup = Util.parseString(lockStream,Integer.valueOf(txBinaryLength.getText()));


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
															currentCarrier = carrierForSentence.elementAt((int)(sentKey % carrierForSentence.size())); 


															embNounB = currentCarrier.getWord();  ///////////////////////////////////

															if(embNounB.length()<=2)
																minurcarr.add(rsTuples.getInt ("ID"));

															synsetWord = tempWNCaller.getSynsetWord(currentCarrier, sentences.elementAt(j), 1);

															if(synsetWord != null){
																substitutes = tempWNCaller.getSubsSet(synsetWord,Integer.valueOf(txMinCarrSize.getText()));

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

																			if (image_element == 1) {
																				newWord = substitutes.elementAt(0);
																			}else{
																				newWord = substitutes.elementAt(1);
																			}


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

																			csLoadCheck.execute ();
																			csUpdater.execute (); 
																			embedded = true;

																			if (metrFlags.contains(valMetr)) {
																				metrStat.setElementAt(metrStat.elementAt(metrFlags.indexOf(valMetr)) + 1, metrFlags.indexOf(valMetr));
																			} else {
																				metrFlags.add(valMetr);
																				metrStat.addElement(Integer.valueOf(1));
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
												}}
											}
										}
									}
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

						metrFlags.clear();
						metrStat.clear();

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

						System.out.println("-----------------------------------------------");

						//ENDING TIME
						Calendar cal1 = Calendar.getInstance();
						cal1.getTime();
						SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
						System.out.println ("PROCESS COMPLETED AT: "+sdf1.format(cal1.getTime()));
						System.out.println("-----------------------------------------------");
						Toolkit.getDefaultToolkit().beep();

						saveImages();
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
			pnResults.setBounds(313, 189, 329, 294);
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

			JButton btnAttrs = new JButton("...");
			btnAttrs.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						ResultSetMetaData rsmd = dbConnection.getAttrMD(cbTable.getSelectedItem().toString());
						int cons = 0;
						carriersModel.setRowCount(0);

						for (int i = 0; i < rsmd.getColumnCount(); i++) {
							if (rsmd.getColumnType(i+1)== OracleTypes.NUMBER) {

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
			panel_7.setBorder(BorderFactory.createTitledBorder(null, " Sentence Selection ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_7.setBounds(216, 9, 204, 140);
			getContentPane().add(panel_7);

			JLabel lblMinimumWords_1 = new JLabel("Minimum Words:");
			lblMinimumWords_1.setHorizontalAlignment(SwingConstants.RIGHT);
			lblMinimumWords_1.setBounds(16, 81, 137, 14);
			panel_7.add(lblMinimumWords_1);

			txMWSent = new JTextField();
			txMWSent.setText("5");
			txMWSent.setColumns(10);
			txMWSent.setBounds(157, 78, 37, 20);
			panel_7.add(txMWSent);

			JLabel lblMinimumSentences = new JLabel("Minimum Sentences:");
			lblMinimumSentences.setBounds(16, 25, 137, 14);
			panel_7.add(lblMinimumSentences);
			lblMinimumSentences.setHorizontalAlignment(SwingConstants.RIGHT);

			txMSPar = new JTextField();
			txMSPar.setBounds(157, 22, 37, 20);
			panel_7.add(txMSPar);
			txMSPar.setText("1");
			txMSPar.setColumns(10);

			JLabel lblMinimumCarrierSize = new JLabel("Minimum Carrier Size:");
			lblMinimumCarrierSize.setBounds(16, 53, 137, 14);
			panel_7.add(lblMinimumCarrierSize);
			lblMinimumCarrierSize.setHorizontalAlignment(SwingConstants.RIGHT);

			txMinCarrSize = new JTextField();
			txMinCarrSize.setBounds(157, 50, 37, 20);
			panel_7.add(txMinCarrSize);
			txMinCarrSize.setText("2");
			txMinCarrSize.setColumns(10);

			JLabel lblStringBinaryLength = new JLabel("Binary Length:");
			lblStringBinaryLength.setBounds(20, 109, 141, 14);
			panel_7.add(lblStringBinaryLength);
			lblStringBinaryLength.setHorizontalAlignment(SwingConstants.RIGHT);

			txBinaryLength = new JTextField();
			txBinaryLength.setBounds(162, 106, 32, 20);
			panel_7.add(txBinaryLength);
			txBinaryLength.setText("20");
			txBinaryLength.setColumns(10);

			JButton btnExit = new JButton("Close");
			btnExit.setBounds(547, 494, 89, 23);
			getContentPane().add(btnExit);

			JPanel panel_6 = new JPanel();
			panel_6.setBounds(430, 9, 212, 158);
			getContentPane().add(panel_6);
			panel_6.setLayout(null);
			panel_6.setBorder(BorderFactory.createTitledBorder(null, "  Role of each Word  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));

			JLabel lblLCE = new JLabel("L       C       E");
			lblLCE.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
			lblLCE.setHorizontalAlignment(SwingConstants.CENTER);
			lblLCE.setBounds(10, 28, 92, 14);
			panel_6.add(lblLCE);

			rbSubL.setBounds(17, 46, 27, 23);
			panel_6.add(rbSubL);

			rbSubC.setSelected(true);
			rbSubC.setBounds(46, 46, 27, 23);
			panel_6.add(rbSubC);

			rbSubE.setBounds(75, 46, 118, 23);
			panel_6.add(rbSubE);

			bgSub.add(rbSubL);
			bgSub.add(rbSubC);
			bgSub.add(rbSubE);

			rbVerbE.setBounds(75, 64, 124, 23);
			panel_6.add(rbVerbE);

			rbVerbC = new JRadioButton("");
			rbVerbC.setBounds(46, 64, 27, 23);
			panel_6.add(rbVerbC);

			rbVerbL.setSelected(true);
			rbVerbL.setBounds(17, 64, 27, 23);
			panel_6.add(rbVerbL);

			this.bgVerb.add(rbVerbE);
			this.bgVerb.add(rbVerbC);
			this.bgVerb.add(rbVerbL);

			rbAdjE.setBounds(75, 82, 124, 23);
			panel_6.add(rbAdjE);

			rbAdjC.setBounds(46, 82, 27, 23);
			panel_6.add(rbAdjC);

			rbAdjL.setSelected(true);
			rbAdjL.setBounds(17, 82, 27, 23);
			panel_6.add(rbAdjL);

			this.bgAdj.add(rbAdjE);
			this.bgAdj.add(rbAdjC);
			this.bgAdj.add(rbAdjL);

			rbAdvE.setSelected(true);
			rbAdvE.setBounds(75, 100, 124, 23);
			panel_6.add(rbAdvE);

			rbAdvC.setBounds(46, 100, 27, 23);
			panel_6.add(rbAdvC);

			rbAdvL.setBounds(17, 100, 27, 23);
			panel_6.add(rbAdvL);

			this.bgAdv.add(rbAdvE);
			this.bgAdv.add(rbAdvC);
			this.bgAdv.add(rbAdvL);

			JRadioButton rbOME = new JRadioButton(" Other Modifiers");
			rbOME.setBounds(75, 118, 124, 23);
			panel_6.add(rbOME);

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
				cbTable.setModel(new DefaultComboBoxModel<String>(new String[] {"TEX_DOCUMENTS","UNIVE_SYLLABUS"}));
				cbTable.setSelectedIndex(1);
				this.cbTable.setBounds(71, 8, 139, 20);
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


	private void saveImages() {
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

			ImageIO.write(emb_img, "bmp", new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"emb.bmp"));

		} catch (Exception e2) {
			e2.printStackTrace();
		}	
	}
	
	private void computeVPK() {
		CallableStatement csVPKGen = null;
		CallableStatement csConsFact = null;
		
		try {
			csVPKGen = dbConnection.getConnection().prepareCall ("{call RTW_ASSIGN_VPK (?,?)}");
			csVPKGen.setString(1, cbTable.getSelectedItem().toString());
			csVPKGen.setString(2, txSK.getText());
			csVPKGen.execute (); 
			
			csConsFact = dbConnection.getConnection().prepareCall ("{call RTW_GENERATE_CONSFACTOR (?,?)}");
			csConsFact.setString(1, cbTable.getSelectedItem().toString());
			csConsFact.setInt (2,Integer.valueOf(tfFractTupl.getText()));
			csConsFact.execute (); 
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try {
				csVPKGen.close();
				csConsFact.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
