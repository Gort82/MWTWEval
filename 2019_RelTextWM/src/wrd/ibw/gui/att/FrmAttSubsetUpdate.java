package wrd.ibw.gui.att;

import java.awt.ComponentOrientation;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import oracle.jdbc.internal.OracleTypes;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class FrmAttSubsetUpdate extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	
	private DBConnection dbConnection = null;
	
	private JComboBox<String> cbTable = null;
	private JTextField tfFractTupl;
	private JTextField tfMSB;
	private JTextField tfLSB;
	private JTextField tfPrivateKey;
	private JSpinner spAF;
	
	private JTable tbFields;
	
	private String[] fixedFilds = {"ELEVATION", "ASPECT","SLOPE","HOR_DIST_TO_HYDROLOGY","VERT_DIST_TO_HYDROLOGY","HOR_DIST_TO_ROADWAYS","HILLSHADE_9AM","HILLSHADE_NOON","HILLSHADE_3PM","HOR_DIST_TO_FIRE_POINTS"};
	
	private JTextField txTotalTupl;
	private JTextField txTotalTuples;
	private JTextField txMTP;
	
	private JRadioButton rbOrigMeth;
	private JRadioButton rbExtMeth;
	
	private JCheckBox cbZDist;
	private JCheckBox cbMSB;
	private JCheckBox cbAF;
	private JCheckBox cbTF;
	/**
	 * @wbp.nonvisual location=642,99
	 */
	private final ButtonGroup bgMethod = new ButtonGroup();
	/**
	 * @wbp.nonvisual location=552,209
	 */
	private final ButtonGroup bgExtOpt = new ButtonGroup();
	private JTextField txPercAttack;
	private JTextField textField_1;
	private JTextField textField_2;

	public FrmAttSubsetUpdate(DBConnection pDBConnection) {
		this.dbConnection = pDBConnection;
		try {
		//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setTitle("Subset Attack Simulator...");
		this.setSize(551,398);
		this.getContentPane().setLayout(null);
		
		JLabel lblRelationToMark = new JLabel("Relation Marked:");
		lblRelationToMark.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRelationToMark.setBounds(10, 11, 110, 14);
		getContentPane().add(lblRelationToMark);
		getContentPane().add(getJCBTable());
		
		tfFractTupl = new JTextField();
		tfFractTupl.setText("40");
		tfFractTupl.setBounds(165, 57, 47, 20);
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
		
		tfPrivateKey = new JTextField();
		tfPrivateKey.setText("82MR");
		tfPrivateKey.setColumns(10);
		tfPrivateKey.setBounds(123, 34, 89, 20);
		getContentPane().add(tfPrivateKey);
		
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
		ResultSet rs = this.dbConnection.getFields(this.cbTable.getSelectedItem().toString());
	    model.addColumn("Get");
	    model.addColumn("Name");
	    model.addColumn("Type");

	    //TO MAKE MORE FLEXIBLE THE FIELDS SELECTION
	    /*while (rs.next()) {
	        Vector data = new Vector();
	        for (int col = 0; col < rsmd.getColumnCount(); col++) {
	            data.add(rs.getObject(col + 1));
	        }
	        //data.add(Boolean.FALSE);
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
		
		
		JButton btnStart = new JButton("Start");
		btnStart.setBounds(400, 291, 122, 23);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					CallableStatement updater = null;
					CallableStatement attr_value_cs = null;
					CallableStatement hav_value_cs = null;
					
					//BUILDING THE ATTRIBUTES LIST
					Vector<String> attributes = new Vector<String>();
					for (int i = 0; i < tbFields.getRowCount(); i++) {
						 if(tbFields.getModel().getValueAt(i, 0).equals(true)){
							 attributes.add(tbFields.getModel().getValueAt(i, 1).toString());
						 }
					}
				
					
					
					
					//EMBEDDING PROCESS OVERVIEW
					System.out.println("-----------------------------------------------");
					System.out.println("WM EMBEDDING PROCESS ");
					System.out.println("-----------------------------------------------");
					System.out.println("RELATION TO MARK: " + cbTable.getSelectedItem().toString());
					System.out.println("SECRET KEY: " + tfPrivateKey.getText());
					System.out.println("TUPLES FRACTION: " + tfFractTupl.getText());
					//System.out.println("NO. APROX. TUPLES: " + String.valueOf(markedTupl));
					System.out.println("MOST SIGNIFICANT BIT (MSB): " + tfMSB.getText());
					System.out.println("LESS SIGNIFICANT BIT (LSB): " + tfLSB.getText());
					System.out.println("ATTRIBUTE LIST: ");
					for (int i = 0; i < attributes.size(); i++) {
						System.out.println("  " + String.valueOf(i+1) + ". " + attributes.elementAt(i));
					}
					System.out.println("-----------------------------------------------");
					//System.out.println("WIDTH: " + String.valueOf(imageWidth)); 
					//System.out.println("HIGH: " + String.valueOf(imageHeight));
					System.out.println("-----------------------------------------------");
					
					//STARTING TIME
					Calendar cal = Calendar.getInstance();
			    	cal.getTime();
			    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			    	System.out.println("PROCESS STARTED AT: " + sdf.format(cal.getTime()) );
			    	System.out.println("-----------------------------------------------");
				
				    int pos_attr_to_mark = 0;		Float number_value;				 
				    String attr_to_mark = "";		String binary_main = "";		 
				    int lsb_pos = 0;				String new_binary = ""; 		 
				    int lsb_value = 0;				String hav_value;
				    								int temp_decimal = 0;
				    
				    
		        	int msb_pos = 0;				boolean signed = false;			int real_value = 0;
		        	int absolute_value = 0;			int value_to_insert = 0;
		        	String prevID = "";
		        	int cant_attack = 0;			int markedTupl = 0;
		        	
		        	Vector<Integer> attrToMark = new Vector<Integer>();
		        	Vector<String> havValues = new Vector<String>();
		        	
		        	CallableStatement gen_inf; 
		        	
		        	if (cbTF.isSelected()) {
		        		markedTupl = dbConnection.getNoRows(cbTable.getSelectedItem().toString(), tfPrivateKey.getText(), Integer.valueOf(tfFractTupl.getText()));
			        	cant_attack = markedTupl * Integer.valueOf(txPercAttack.getText())/100;
			        	
			        	//GET THE GENERAL INFORMATION
						gen_inf = dbConnection.getConnection().prepareCall ("{ ? = call GET_RANDOM_MARKED_IDS (?,?,?,?)}");
						gen_inf.registerOutParameter (1, OracleTypes.CURSOR);
						gen_inf.setString (2,cbTable.getSelectedItem().toString());
						gen_inf.setString (3,tfPrivateKey.getText());
						gen_inf.setInt (4, cant_attack);
						gen_inf.setInt (5, Integer.parseInt(tfFractTupl.getText()));
					} else {
						int tuplAttack = Integer.valueOf(String.valueOf(dbConnection.getAllRows(cbTable.getSelectedItem().toString())*Integer.valueOf(txPercAttack.getText())/100)).intValue();
						
						gen_inf = dbConnection.getConnection().prepareCall ("{ ? = call GET_RANDOM_IDS (?,?,?)}");
						gen_inf.registerOutParameter (1, OracleTypes.CURSOR);
						gen_inf.setString (2,cbTable.getSelectedItem().toString());
						gen_inf.setString (3,tfPrivateKey.getText());
						gen_inf.setInt (4, tuplAttack);
						
					}
					
		        	gen_inf.execute ();
		        	
				    ResultSet rset_info = (ResultSet)gen_inf.getObject (1);
				    
				    if(attr_value_cs == null){
				    	attr_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call GET_VALUE_OF_ATTR (?,?,?)}");
				    }
				    attr_value_cs.registerOutParameter (1, Types.FLOAT);
					
				    if(rbExtMeth.isSelected()){
				    	if(hav_value_cs == null){
				    		hav_value_cs = dbConnection.getConnection().prepareCall ("{ ? = call CREATE_HAV (?,?,?)}");
		    			}
				    	hav_value_cs.registerOutParameter (1, Types.INTEGER);
				    }
		        	
		        	while (rset_info.next ()){
		        		if(rbOrigMeth.isSelected()){
			        		attrToMark.clear();
				        	havValues.clear();
					        	
			        		pos_attr_to_mark = (new BigInteger(rset_info.getString ("VPK")).mod(BigInteger.valueOf(attributes.size()))).intValue();
			        		attr_value_cs.setString(2, cbTable.getSelectedItem().toString());
			        		attr_value_cs.setString(3, attributes.elementAt(pos_attr_to_mark));
			        		attr_value_cs.setString(4, rset_info.getString ("ID"));
				        	attr_value_cs.execute ();
				        	number_value = attr_value_cs.getFloat(1);
			        	
				        	absolute_value = Math.abs(number_value.intValue());
				        	binary_main = Integer.toBinaryString(absolute_value);
				        	if (binary_main.length() >= (Integer.valueOf(tfLSB.getText()))+Integer.valueOf(tfMSB.getText()))
				        		attrToMark.addElement(pos_attr_to_mark);
			        	}else{
			        		attrToMark.clear();
				        	havValues.clear();
				        	
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
							        	
						        	hav_value_cs.setString(2, rset_info.getString ("ID"));
						        	hav_value_cs.setString (3,tfPrivateKey.getText());
						        	hav_value_cs.setFloat(4, real_value);
						        	hav_value_cs.execute ();
							        	
						        	hav_value = hav_value_cs.getString(1);
							        	
						        	if(new BigInteger(hav_value).mod(new BigInteger(spAF.getValue().toString()))== BigInteger.valueOf(new Long(0))){
										attrToMark.addElement(i);
										havValues.addElement(hav_value);
						        	}
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
				        	if(number_value < 0){ signed = true;}
				        	
				        	binary_main = Integer.toBinaryString(absolute_value);
				        	
				        	temp_decimal = new Integer(String.valueOf(number_value).substring(String.valueOf(number_value).indexOf ( "." )+1));
				        	if(temp_decimal!=0){ /*binary_decimal = Integer.toBinaryString(temp_decimal);*/}
				        	
				        	if(new Integer(tfLSB.getText())*2 < binary_main.length()){
				        		if(rbOrigMeth.isSelected()){
				        		    msb_pos = (new BigInteger(rset_info.getString ("VPK")).mod(new BigInteger(tfMSB.getText()))).intValue() + 1;
					        		lsb_pos = (new BigInteger(rset_info.getString ("VPK")).mod(new BigInteger(tfLSB.getText()))).intValue() + 1;
				        		}else{
									 msb_pos = (new BigInteger(havValues.elementAt(i)).mod(new BigInteger(tfMSB.getText()))).intValue() + 1;
						        	 lsb_pos = (new BigInteger(havValues.elementAt(i)).mod(new BigInteger(tfLSB.getText()))).intValue() + 1;
				        		}
				        		
				        		if((msb_pos - 1) < (binary_main.length()-lsb_pos)){ //creo que ya lo salva la condicion de qeu LSB + MSB <= Binary.length
			        				if(rset_info.getString ("ID") != prevID){
			        					prevID = rset_info.getString ("ID");
			        					
			        					lsb_value = Character.getNumericValue(binary_main.charAt(binary_main.length()-lsb_pos));
				        				if (lsb_value == 1) {
				        					value_to_insert = 0;
				        				} else {
											value_to_insert = 1;
										}
				        				new_binary = binary_main.substring(0, binary_main.length() - lsb_pos) + value_to_insert + binary_main.substring(binary_main.length() - lsb_pos+1, binary_main.length()) ;
				        				
				        				if(updater == null){
				        					updater = dbConnection.getConnection().prepareCall ("{ call UPDATE_ATTR_AT (?,?,?,?)}");
				        				}			
				        				
				        				updater.setString(1, cbTable.getSelectedItem().toString());
				        				updater.setString (2, attr_to_mark);
				        				
				        				real_value = Integer.parseInt(new_binary,2);
				        				if(signed){
				        					real_value = 0-real_value;
				        					signed = false;
				        				}
				        				updater.setInt(3, real_value);
				        				updater.setInt(4, Integer.valueOf(rset_info.getString ("ID")));
				        				updater.execute ();
			        				}
				        		}
			        		}
			        	}
		        	}
				    
				    attr_value_cs.close();
				    rset_info.close();
				    gen_inf.close();
					
					
				    //RESULTS REPORT BUIL SECTION
				    txTotalTupl.setText(String.valueOf(markedTupl));
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
					JOptionPane.showMessageDialog(null, "Process completed...");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
			getContentPane().add(btnStart);
		
			JButton btnExit = new JButton("Close");
			btnExit.setBounds(400, 325, 122, 23);
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
			panel_5.setBounds(10, 244, 356, 110);
			getContentPane().add(panel_5);
			
			JLabel label_4 = new JLabel("Marked:");
			label_4.setHorizontalAlignment(SwingConstants.RIGHT);
			label_4.setBounds(124, 32, 52, 14);
			panel_5.add(label_4);
			
			txTotalTupl = new JTextField();
			txTotalTupl.setText("0");
			txTotalTupl.setEditable(false);
			txTotalTupl.setColumns(10);
			txTotalTupl.setBounds(180, 29, 46, 20);
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
			label_7.setBounds(241, 29, 52, 14);
			panel_5.add(label_7);
			
			txMTP = new JTextField();
			txMTP.setText("0");
			txMTP.setEditable(false);
			txMTP.setColumns(10);
			txMTP.setBounds(297, 26, 46, 20);
			panel_5.add(txMTP);
			
			JLabel label_9 = new JLabel("_____________________________");
			label_9.setOpaque(true);
			label_9.setHorizontalAlignment(SwingConstants.LEFT);
			label_9.setBounds(134, 57, 209, 14);
			panel_5.add(label_9);
			
			JLabel lblAttacked = new JLabel("Attacked:");
			lblAttacked.setHorizontalAlignment(SwingConstants.RIGHT);
			lblAttacked.setBounds(124, 80, 52, 14);
			panel_5.add(lblAttacked);
			
			textField_1 = new JTextField();
			textField_1.setText("0");
			textField_1.setEditable(false);
			textField_1.setColumns(10);
			textField_1.setBounds(180, 77, 46, 20);
			panel_5.add(textField_1);
			
			JLabel label_2 = new JLabel("Percent:");
			label_2.setHorizontalAlignment(SwingConstants.RIGHT);
			label_2.setBounds(241, 77, 52, 14);
			panel_5.add(label_2);
			
			textField_2 = new JTextField();
			textField_2.setText("0");
			textField_2.setEditable(false);
			textField_2.setColumns(10);
			textField_2.setBounds(297, 74, 46, 20);
			panel_5.add(textField_2);
			
			JPanel pnAlgorithms = new JPanel();
			pnAlgorithms.setLayout(null);
			pnAlgorithms.setBorder(BorderFactory.createTitledBorder(null, "  Algorithm Variations ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			pnAlgorithms.setBounds(327, 40, 197, 205);
			getContentPane().add(pnAlgorithms);
			
			rbOrigMeth = new JRadioButton("Original Method");
			rbOrigMeth.setSelected(true);
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
						spAF.setEnabled(true);
						cbMSB.setEnabled(true);
						cbAF.setEnabled(true);
						cbAF.setSelected(true);
						tfMSB.setEnabled(true);
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
						spAF.setEnabled(false);
						cbMSB.setEnabled(false);
						cbAF.setEnabled(false);
						cbAF.setSelected(false);
						tfMSB.setEnabled(false);
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
			
			spAF = new JSpinner();
			spAF.setModel(new SpinnerNumberModel(1, 1, 10, 1));
			spAF.setEnabled(false);
			spAF.setBounds(148, 167, 38, 20);
			pnAlgorithms.add(spAF);
			
			cbAF = new JCheckBox("Fraction of Attributes:");
			cbAF.setHorizontalAlignment(SwingConstants.RIGHT);
			cbAF.setEnabled(false);
			cbAF.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					if (arg0.getStateChange() == ItemEvent.SELECTED) {
						spAF.setEnabled(true);
				    }else{
				    	spAF.setEnabled(false);
				    }
				}
			});
			cbAF.setBounds(5, 166, 143, 23);
			pnAlgorithms.add(cbAF);
			
			JLabel lblPercAttackedTuples = new JLabel("Perc. Attacked Tuples:");
			lblPercAttackedTuples.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPercAttackedTuples.setBounds(328, 10, 154, 14);
			getContentPane().add(lblPercAttackedTuples);
			
			txPercAttack = new JTextField();
			txPercAttack.setText("1");
			txPercAttack.setColumns(10);
			txPercAttack.setBounds(486, 7, 37, 20);
			getContentPane().add(txPercAttack);
			
			JCheckBox cbPK = new JCheckBox("Private Key:");
			cbPK.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					if (arg0.getStateChange() == ItemEvent.SELECTED) {
						tfPrivateKey.setEnabled(true);
				    }else{
				    	tfPrivateKey.setEnabled(false);
				    }
				}
			});
			cbPK.setSelected(true);
			cbPK.setHorizontalAlignment(SwingConstants.RIGHT);
			cbPK.setBounds(8, 32, 114, 23);
			getContentPane().add(cbPK);
			
			cbTF = new JCheckBox("Fraction of Tuples:");
			cbTF.setSelected(true);
			cbTF.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					if (arg0.getStateChange() == ItemEvent.SELECTED) {
						tfFractTupl.setEnabled(true);
				    }else{
				    	tfFractTupl.setEnabled(false);
				    }
				}
			});
			cbTF.setHorizontalAlignment(SwingConstants.RIGHT);
			cbTF.setBounds(10, 56, 154, 23);
			getContentPane().add(cbTF);
			
			cbMSB = new JCheckBox("MSB:");
			cbMSB.setSelected(true);
			cbMSB.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					if (arg0.getStateChange() == ItemEvent.SELECTED) {
						tfMSB.setEnabled(true);
				    }else{
				    	tfMSB.setEnabled(false);
				    }
				}
			});
			cbMSB.setHorizontalAlignment(SwingConstants.RIGHT);
			cbMSB.setEnabled(false);
			cbMSB.setBounds(211, 33, 70, 23);
			getContentPane().add(cbMSB);
			
			JCheckBox cbLSB = new JCheckBox("LSB:");
			cbLSB.setSelected(true);
			cbLSB.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					if (arg0.getStateChange() == ItemEvent.SELECTED) {
						tfLSB.setEnabled(true);
				    }else{
				    	tfLSB.setEnabled(false);
				    }
				}
			});
			cbLSB.setHorizontalAlignment(SwingConstants.RIGHT);
			cbLSB.setBounds(211, 57, 70, 23);
			getContentPane().add(cbLSB);
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
				cbTable.setModel(new DefaultComboBoxModel(new String[] {"COVERTYPE_A","COVERTYPE_B","COVERTYPE_C","COVERTYPE_D","COVERTYPE_E","COVERTYPE_F", "COVERTYPE_G", "COVERTYPE_H", "COVERTYPE_I", "COVERTYPE_J", "COVERTYPE_K"}));
				cbTable.setSelectedIndex(0);
				this.cbTable.setBounds(123, 8, 195, 20);
				//this.cbTable.setModel(new DefaultComboBoxModel(this.dbConnection.getTables().toArray()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.cbTable;
	}
}
