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
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

import oracle.jdbc.internal.OracleTypes;
import javax.swing.border.LineBorder;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class FrmExtractWM extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	private DBConnection dbConnection = null;

	private JComboBox<String> cbTable = null;
	private JTextField tfFractTupl;
	private JTextField tfMSB;
	private JTextField tfLSB;
	private JTextField tfPrivateKey;
	private JButton btnEnhance;
	private JButton btnSave;
	private JCheckBox cbSymmetricImage;
	private JSpinner spAF;

	private JTable tbFields;
	private JLabel lbEnhancedImage ;

	private int imageWidth = 0;
	private int imageHeight = 0;

	private BufferedImage img = null;
	private BufferedImage img2 = null;

	private String[] fixedFilds = {"ELEVATION", "ASPECT","SLOPE","HOR_DIST_TO_HYDROLOGY","VERT_DIST_TO_HYDROLOGY","HOR_DIST_TO_ROADWAYS","HILLSHADE_9AM","HILLSHADE_NOON","HILLSHADE_3PM","HOR_DIST_TO_FIRE_POINTS"};
	private Vector<Integer> imageArray = null;

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
	private JTextField txRecPx;
	private JTextField txMissPx;
	private JTextField txEnPerc;
	private JTextField txRecPerc;

	public FrmExtractWM(DBConnection pDBConnection, String pSecretKey, String pTuplFract, int pAttrFract) {
		this.dbConnection = pDBConnection;
		try {
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
			this.setTitle("WM extraction...");
			this.setSize(735,497);
			this.getContentPane().setLayout(null);

			JLabel lblRelationToMark = new JLabel("Relation Marked:");
			lblRelationToMark.setHorizontalAlignment(SwingConstants.RIGHT);
			lblRelationToMark.setBounds(10, 11, 110, 14);
			getContentPane().add(lblRelationToMark);
			getContentPane().add(getJCBTable());

			JLabel lblFractionOrRelations = new JLabel("Fraction of Tuples:");
			lblFractionOrRelations.setHorizontalAlignment(SwingConstants.RIGHT);
			lblFractionOrRelations.setBounds(10, 60, 110, 14);
			getContentPane().add(lblFractionOrRelations);

			tfFractTupl = new JTextField();
			tfFractTupl.setText(pTuplFract);
			tfFractTupl.setBounds(123, 57, 47, 20);
			getContentPane().add(tfFractTupl);
			tfFractTupl.setColumns(10);

			tfMSB = new JTextField();
			tfMSB.setText("3");
			tfMSB.setColumns(10);
			tfMSB.setBounds(281, 35, 37, 20);
			getContentPane().add(tfMSB);

			tfLSB = new JTextField();
			tfLSB.setText("1");
			tfLSB.setColumns(10);
			tfLSB.setBounds(281, 58, 37, 20);
			getContentPane().add(tfLSB);

			JLabel lblMsb = new JLabel("MSB:");
			lblMsb.setHorizontalAlignment(SwingConstants.RIGHT);
			lblMsb.setBounds(246, 38, 31, 14);
			getContentPane().add(lblMsb);

			JLabel lblLsb = new JLabel("LSB:");
			lblLsb.setHorizontalAlignment(SwingConstants.RIGHT);
			lblLsb.setBounds(246, 61, 31, 14);
			getContentPane().add(lblLsb);

			JLabel lblPrivateKey = new JLabel("Private Key:");
			lblPrivateKey.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPrivateKey.setBounds(10, 37, 110, 14);
			getContentPane().add(lblPrivateKey);

			tfPrivateKey = new JTextField();
			tfPrivateKey.setText(pSecretKey);
			tfPrivateKey.setColumns(10);
			tfPrivateKey.setBounds(123, 34, 89, 20);
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
			tfHeight.setText("21");
			tfHeight.setColumns(10);

			tfWidth = new JTextField();
			tfWidth.setBounds(147, 25, 37, 20);
			pnImageSelector.add(tfWidth);
			tfWidth.setText("20");
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

			JPanel panel = new JPanel();
			panel.setLayout(null);
			panel.setBorder(BorderFactory.createTitledBorder(null, "  Fields to Consider  ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel.setBounds(10, 85, 310, 160);
			getContentPane().add(panel);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(10, 22, 290, 127);
			panel.add(scrollPane);

			tbFields = new JTable( ){

				private static final long serialVersionUID = 1L;
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
			model.addColumn("Get");
			model.addColumn("Name");
			model.addColumn("Type");

			for (int i = 0; i < fixedFilds.length; i++) {
				model.addRow(new Object[]{Boolean.TRUE, fixedFilds[i], "FLOAT"});
			}
			this.tbFields.setModel(model);
			this.tbFields.getColumnModel().getColumn(0).setPreferredWidth(37);
			this.tbFields.getColumnModel().getColumn(1).setPreferredWidth(145);
			this.tbFields.getColumnModel().getColumn(2).setPreferredWidth(64);
			this.tbFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

			scrollPane.setViewportView(tbFields);

			JButton btnStart = new JButton("Start");
			btnStart.setBounds(10, 428, 122, 23);
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

					CallableStatement attr_value_cs = null;
					CallableStatement hav_value_cs = null;
					CallableStatement attr_pos_cs = null;
					CallableStatement gen_inf = null;

					ResultSet rset_info = null;

					int cant_total = 0;

					Float number_value;				int width_pos = 0;
					String attr_to_mark = "";		String binary_main = "";		int height_pos = 0;
					int lsb_pos = 0;				String new_binary = ""; 		int image_element = 0;
					int lsb_value = 0;				String hav_value;
					int temp_decimal = 0;

					int msb_pos = 0;				int real_value = 0;
					int msb_value = 0;				int absolute_value = 0;

					Vector<Integer> attrToMark = new Vector<Integer>();
					Vector<String> havValues = new Vector<String>();

					int vnd = 0;
					int vne = 0;

					try {
						Vector<String> attributes = new Vector<String>();
						for (int i = 0; i < tbFields.getRowCount(); i++) {
							if(tbFields.getModel().getValueAt(i, 0).equals(true)){
								attributes.add(tbFields.getModel().getValueAt(i, 1).toString());
							}
						}

						System.out.println("-----------------------------------------------");
						System.out.println("WM EMBEDDING PROCESS ");
						System.out.println("-----------------------------------------------");
						System.out.println("RELATION TO MARK: " + cbTable.getSelectedItem().toString());
						System.out.println("SECRET KEY: " + tfPrivateKey.getText());
						System.out.println("TUPLES FRACTION: " + tfFractTupl.getText());
						System.out.println("NO. APROX. TUPLES: " + dbConnection.getNoRows(cbTable.getSelectedItem().toString(), tfPrivateKey.getText(), Integer.valueOf(tfFractTupl.getText())));
						System.out.println("MOST SIGNIFICANT BIT (MSB): " + tfMSB.getText());
						System.out.println("LESS SIGNIFICANT BIT (LSB): " + tfLSB.getText());
						System.out.println("ATTRIBUTE LIST: ");
						for (int i = 0; i < attributes.size(); i++) {
							System.out.println("  " + String.valueOf(i+1) + ". " + attributes.elementAt(i));
						}
						System.out.println("-----------------------------------------------");
						System.out.println("WIDTH: " + String.valueOf(imageWidth)); 
						System.out.println("HIGH: " + String.valueOf(imageHeight));
						System.out.println("-----------------------------------------------");

						//STARTING TIME
						Calendar cal = Calendar.getInstance();
						cal.getTime();
						SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
						System.out.println("PROCESS STARTED AR: " + sdf.format(cal.getTime()) );
						System.out.println("-----------------------------------------------");

						imageArray = new Vector<Integer>(imageHeight * imageWidth);

						for (int i = 0; i < (imageHeight * imageWidth); i++){
							imageArray.add(i,0);
						}

						gen_inf = dbConnection.getConnection().prepareCall ("{ ? = call GET_GENERAL_INFO (?,?,?,?,?,?,?,?)}");
						gen_inf.registerOutParameter (1, OracleTypes.CURSOR);
						gen_inf.setString (2,cbTable.getSelectedItem().toString());
						gen_inf.setString (3,tfPrivateKey.getText());
						gen_inf.setInt (4, Integer.parseInt(tfFractTupl.getText()));
						gen_inf.setInt (5, Integer.parseInt(tfHeight.getText())-1);
						gen_inf.setInt (6, Integer.parseInt(tfWidth.getText())-1);
						gen_inf.setInt (7, attributes.size()-1);
						gen_inf.setInt (8, Integer.valueOf(tfMSB.getText())-1);
						gen_inf.setInt (9, Integer.valueOf(tfLSB.getText())-1);
						gen_inf.execute ();

						rset_info = (ResultSet)gen_inf.getObject (1);

						if(attr_value_cs == null){
							attr_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_VALUE_OF_ATTR (?,?,?)}");
						}
						attr_value_cs.registerOutParameter (1, Types.FLOAT);

						if(hav_value_cs == null){
							hav_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call CREATE_HAV (?,?,?)}");
						}
						hav_value_cs.registerOutParameter (1, Types.INTEGER);

						if(attr_pos_cs == null){
							attr_pos_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_ATTR_POS (?,?,?,?,?,?)}");
						}
						attr_pos_cs.registerOutParameter (1, OracleTypes.CURSOR);

						while (rset_info.next ()){
							try {
								if(rset_info.getInt ("CONS_FACT")==0){

									attrToMark.clear();
									havValues.clear();

									if(attr_value_cs.isClosed()){
										attr_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_VALUE_OF_ATTR (?,?,?)}");
										attr_value_cs.registerOutParameter (1, Types.FLOAT);
									}

									for (int i = 0; i < attributes.size(); i++) {
										attr_value_cs.setString(2, cbTable.getSelectedItem().toString());
										attr_value_cs.setString(3, attributes.elementAt(i));
										attr_value_cs.setString(4, rset_info.getString ("ID"));
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

											hav_value_cs.setString(2, rset_info.getString ("ID"));
											hav_value_cs.setString (3,tfPrivateKey.getText());
											hav_value_cs.setFloat(4, real_value);
											hav_value_cs.execute ();

											hav_value = hav_value_cs.getString(1);

											if(new BigInteger(hav_value).mod(new BigInteger(spAF.getValue().toString()))== BigInteger.valueOf(Long.valueOf(0))){
												attrToMark.addElement(i);
												havValues.addElement(hav_value);
											}
										}
									}

									for (int i = 0; i < attrToMark.size(); i++) {
										attr_to_mark = attributes.elementAt(attrToMark.elementAt(i));

										attr_value_cs.setString(2, cbTable.getSelectedItem().toString());
										attr_value_cs.setString(3, attr_to_mark);
										attr_value_cs.setString(4, rset_info.getString ("ID"));
										attr_value_cs.execute ();
										number_value = attr_value_cs.getFloat(1);

										absolute_value = Math.abs(number_value.intValue());
										binary_main = Integer.toBinaryString(absolute_value);

										temp_decimal = Integer.valueOf(String.valueOf(number_value).substring(String.valueOf(number_value).indexOf ( "." )+1));
										if(temp_decimal!=0){ /*binary_decimal = Integer.toBinaryString(temp_decimal);*/}

										if(Integer.valueOf(tfLSB.getText())*2 < binary_main.length()){

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
												msb_pos = rset_attr_hav.getInt("MSB_POS") + 1  ;
												lsb_pos = rset_attr_hav.getInt("LSB_POS") + 1  ;

												height_pos = rset_attr_hav.getInt("H"); 
												width_pos =  rset_attr_hav.getInt("W"); 
											}

											if((msb_pos - 1) < (binary_main.length()-lsb_pos)){ //creo que ya lo salva la condicion de qeu LSB + MSB <= Binary.length
												msb_value = Character.getNumericValue(binary_main.charAt(msb_pos-1));

												if((imageWidth * (height_pos) + width_pos) > 0  ){
													lsb_value = Character.getNumericValue(binary_main.charAt(binary_main.length()-lsb_pos));
													image_element = lsb_value ^ msb_value;

													Vector<Integer> tempStore = mayorityInfo[height_pos][width_pos];

													if(Integer.parseInt(rset_info.getString ("ID")) > 30000) {
														if(tempStore.size() > 0 ) {
															if(tempStore.get(tempStore.size()-1) != image_element)
																vnd++;
															else
																vne++;
														}
													}

													tempStore.add(image_element);
													mayorityInfo[height_pos][width_pos] = tempStore;
													cant_total++;
												}
											}
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							finally {
								if(hav_value_cs!=null)
									hav_value_cs.close();
								if(attr_pos_cs!=null)
									attr_pos_cs.close();
								attr_value_cs.close();
							}
						}

						System.out.println("-----------------------------------------------");

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
						Toolkit.getDefaultToolkit().beep();
						//btnEnhance.doClick();


						System.out.println("Elementos encontrados sin diferenciar entre iguales: " + cant_total);

						System.out.println("Vne: " + vne);
						System.out.println("Vnd: " + vnd);

						JOptionPane.showMessageDialog(null, "Extraction Process completed...");


					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						try {
							rset_info.close();
							gen_inf.close();

						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
			});
			getContentPane().add(btnStart);

			JButton btnExit = new JButton("Close");
			btnExit.setBounds(196, 428, 122, 23);
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
				}
			});
			btnEnhance.setEnabled(false);
			btnEnhance.setBounds(204, 25, 85, 23);
			panel_6.add(btnEnhance);

			btnSave = new JButton("Save");
			btnSave.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						ImageIO.write(img, "bmp", new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"enhanced.bmp"));
						ImageIO.write(img2, "bmp", new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "img"+ System.getProperty("file.separator") +"extracted.bmp"));
						assignValues();
						JOptionPane.showMessageDialog(null, "The extracted image was successfully saved...");
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			});
			btnSave.setEnabled(false);
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
			pnAlgorithms.setBounds(10, 247, 184, 58);
			getContentPane().add(pnAlgorithms);

			JLabel label_12 = new JLabel("Fraction of Attributes:");
			label_12.setHorizontalAlignment(SwingConstants.RIGHT);
			label_12.setBounds(10, 28, 127, 14);
			pnAlgorithms.add(label_12);

			spAF = new JSpinner();
			spAF.setModel(new SpinnerNumberModel(9, 1, 10, 1));
			spAF.setEnabled(false);
			spAF.setBounds(140, 25, 38, 20);
			spAF.setValue(pAttrFract);
			pnAlgorithms.add(spAF);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JComboBox<String> getJCBTable(){
		if(this.cbTable == null){
			try {
				this.cbTable = new JComboBox<String>();
				cbTable.setModel(new DefaultComboBoxModel<String>(new String[] {"COVERTYPE_A"}));
				cbTable.setSelectedIndex(0);
				this.cbTable.setBounds(123, 8, 195, 20);
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

	private void assignValues() {
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
}
