package wrd.ibw.gui.att;

import java.awt.ComponentOrientation;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import java.awt.Color;

import javax.swing.JButton;

import wrd.ibw.da.DBConnection;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import oracle.jdbc.internal.OracleTypes;

public class FrmAttSupersetInsert extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	
	private DBConnection dbConnection = null;
	
	private JComboBox<String> cbTable = null;
	
	private JTextField txTotalTupl;
	private JTextField txTotalTuples;
	private JTextField txPercAttack;
	private JTextField txSK;
	private JTextField txTF;

	public FrmAttSupersetInsert(DBConnection pDBConnection) {
		this.dbConnection = pDBConnection;
		try {
		//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setTitle("Superset Adding Attack...");
		this.setSize(458,251);
		this.getContentPane().setLayout(null);
		
		JLabel lblRelationToMark = new JLabel("Relation Marked:");
		lblRelationToMark.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRelationToMark.setBounds(10, 11, 110, 14);
		getContentPane().add(lblRelationToMark);
		getContentPane().add(getJCBTable());
		
		JButton btnStart = new JButton("Start");
		btnStart.setBounds(308, 44, 122, 23);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					/*CallableStatement updater = null;
					CallableStatement attr_value_cs = null;
					CallableStatement hav_value_cs = null;*/
					CallableStatement tuple_inserter = null;
					
					CallableStatement vpk_filler = null;
					CallableStatement cons_factor_filler = null;
					
					int tuplAttack = Integer.valueOf(String.valueOf(dbConnection.getAllRows(cbTable.getSelectedItem().toString())*Integer.valueOf(txPercAttack.getText())/100)).intValue();
					
					
					//EMBEDDING PROCESS OVERVIEW
					System.out.println("-----------------------------------------------");
					System.out.println("WM EMBEDDING PROCESS ");
					System.out.println("-----------------------------------------------");
					System.out.println("RELATION TO MARK: " + cbTable.getSelectedItem().toString());
					System.out.println("TUPLES TO INSERT: " + String.valueOf(tuplAttack));
					System.out.println("-----------------------------------------------");
					System.out.println("-----------------------------------------------");
					
					//STARTING TIME
					Calendar cal = Calendar.getInstance();
			    	cal.getTime();
			    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			    	System.out.println("PROCESS STARTED AR: " + sdf.format(cal.getTime()) );
			    	System.out.println("-----------------------------------------------");
				
			    	if(tuple_inserter == null){
	        			tuple_inserter = dbConnection.getConnection().prepareCall ("{ call RTW_INSERT_TUPLE (?,?)}");
    				}	
			    	
			    	if(vpk_filler == null){
			    		vpk_filler = dbConnection.getConnection().prepareCall ("{ call RTW_ASSIGN_VPK (?,?)}");
    				}	
			    	
			    	if(cons_factor_filler == null){
			    		cons_factor_filler = dbConnection.getConnection().prepareCall ("{ call RTW_GENERATE_CONSFACTOR (?,?)}");
    				}	
			    	
			    	vpk_filler.setString(1, cbTable.getSelectedItem().toString());
			    	vpk_filler.setString(2, txSK.getText());
			    	
			    	cons_factor_filler.setString(1, cbTable.getSelectedItem().toString());
			    	cons_factor_filler.setInt(2, Integer.valueOf(txTF.getText()));
    				
	        		tuple_inserter.setString(1, cbTable.getSelectedItem().toString());
	        		tuple_inserter.setInt(2,cbTable.getSelectedIndex());
	        		
		        	for (int i = 0; i < tuplAttack; i++) {
		        		tuple_inserter.execute ();
					}
		        	
		        	//GET THE GENERAL INFORMATION
					//CallableStatement gen_inf = dbConnection.getConnection().prepareCall ("{ ? = call GET_RANDOM_MARKED_IDS (?,?,?,?)}");
					//gen_inf.registerOutParameter (1, OracleTypes.CURSOR);
					//gen_inf.setString (2,cbTable.getSelectedItem().toString());
					//gen_inf.setString (3,tfPrivateKey.getText());
					//gen_inf.setInt (4, cant_attack);
					//gen_inf.setInt (5, Integer.parseInt(tfFractTupl.getText()));
					//gen_inf.execute ();
					
				    //ResultSet rset_info = (ResultSet)gen_inf.getObject (1);
				    
				    //rset_info.close();
				    //gen_inf.close();
		        	
		        	vpk_filler.execute();
		        	cons_factor_filler.execute();
					
					
				    //RESULTS REPORT BUIL SECTION
					DecimalFormat df = new DecimalFormat("##.##");
					df.setRoundingMode(RoundingMode.DOWN);
					txTotalTuples.setText(String.valueOf(dbConnection.getAllRows(cbTable.getSelectedItem().toString())));
					txTotalTupl.setText(String.valueOf(tuplAttack));
					
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
			btnExit.setBounds(308, 76, 122, 23);
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
			panel_5.setBorder(BorderFactory.createTitledBorder(null, "  Tuples Information ", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD | Font.ITALIC, 12), new Color(51, 51, 51)));
			panel_5.setBounds(20, 36, 243, 65);
			getContentPane().add(panel_5);
			
			JLabel lblInserted = new JLabel("Inserted:");
			lblInserted.setHorizontalAlignment(SwingConstants.RIGHT);
			lblInserted.setBounds(124, 32, 52, 14);
			panel_5.add(lblInserted);
			
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
			
			JLabel lblPercAttackedTuples = new JLabel("Inserted Tuples (%):");
			lblPercAttackedTuples.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPercAttackedTuples.setBounds(273, 10, 116, 14);
			getContentPane().add(lblPercAttackedTuples);
			
			txPercAttack = new JTextField();
			txPercAttack.setText("10");
			txPercAttack.setColumns(10);
			txPercAttack.setBounds(392, 7, 37, 20);
			getContentPane().add(txPercAttack);
			
			JLabel lblSecretKey = new JLabel("Secret Key:");
			lblSecretKey.setHorizontalAlignment(SwingConstants.RIGHT);
			lblSecretKey.setBounds(30, 121, 116, 14);
			getContentPane().add(lblSecretKey);
			
			txSK = new JTextField();
			txSK.setText("Secu48102304782K");
			txSK.setColumns(10);
			txSK.setBounds(149, 118, 110, 20);
			getContentPane().add(txSK);
			
			JLabel lblPercAttackedTuples_1_1 = new JLabel("Tuple Fraction:");
			lblPercAttackedTuples_1_1.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPercAttackedTuples_1_1.setBounds(30, 149, 116, 14);
			getContentPane().add(lblPercAttackedTuples_1_1);
			
			txTF = new JTextField();
			txTF.setText("1");
			txTF.setColumns(10);
			txTF.setBounds(149, 146, 37, 20);
			getContentPane().add(txTF);
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
				cbTable.setModel(new DefaultComboBoxModel(new String[] {"TEX_DOCUMENTS", "UNIVE_SYLLABUS"}));
				cbTable.setSelectedIndex(0);
				this.cbTable.setBounds(123, 8, 140, 20);
				//this.cbTable.setModel(new DefaultComboBoxModel(this.dbConnection.getTables().toArray()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.cbTable;
	}
}
