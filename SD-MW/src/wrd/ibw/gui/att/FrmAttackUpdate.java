package wrd.ibw.gui.att;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Toolkit;

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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import oracle.jdbc.internal.OracleTypes;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class FrmAttackUpdate extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	
	private DBConnection dbConnection = null;
	
	private JComboBox<String> cbTable = null;
	
	private JTextField txTotalTupl;
	private JTextField txTotalTuples;
	private JTextField txPercAttack;
	private JSpinner spAtr;
	
	public FrmAttackUpdate(DBConnection pDBConnection) {
		///String[ ] attribute_array = {"EMB_MOD_VER_DETT_ENG", "EMB_RIS_APPR_ENG", "EMB_CONTENUTI_ENG", "EMB_OBIETT_FORM_ENG"}; 
		String[ ] attribute_array = {"EMB_TEXT"}; 
    	
		
		this.dbConnection = pDBConnection;
		try {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setTitle("Subset Update Attack...");
		this.setSize(458,196);
		this.getContentPane().setLayout(null);
		
		JLabel lblRelationToMark = new JLabel("Relation Marked:");
		lblRelationToMark.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRelationToMark.setBounds(10, 11, 110, 14);
		getContentPane().add(lblRelationToMark);
		getContentPane().add(getJCBTable());
		
		JButton btnStart = new JButton("Start");
		btnStart.setBounds(165, 127, 122, 23);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					CallableStatement gen_inf = null;
					CallableStatement updater = null;
					
					int tuplAttack = Integer.valueOf(String.valueOf(dbConnection.getAllRows(cbTable.getSelectedItem().toString())*Integer.valueOf(txPercAttack.getText())/100)).intValue();
					
					//EMBEDDING PROCESS OVERVIEW
					System.out.println("-----------------------------------------------");
					System.out.println("STARTING ATTACK SIMMULATION ");
					System.out.println("-----------------------------------------------");
					System.out.println("RELATION TO MARK: " + cbTable.getSelectedItem().toString());
					System.out.println("TUPLES TO UPDATE: " + String.valueOf(tuplAttack));
					System.out.println("-----------------------------------------------");
					System.out.println("-----------------------------------------------");
					
					//STARTING TIME
					Calendar cal = Calendar.getInstance();
			    	cal.getTime();
			    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			    	System.out.println("PROCESS STARTED AT: " + sdf.format(cal.getTime()) );
			    	System.out.println("-----------------------------------------------");
				
		        	gen_inf = dbConnection.getConnection().prepareCall ("{ ? = call GET_RANDOM_TUPLES (?,?)}");
					gen_inf.registerOutParameter (1, OracleTypes.CURSOR);
					gen_inf.setString (2,cbTable.getSelectedItem().toString());
					gen_inf.setInt (3, tuplAttack);
					
					/*gen_val = dbConnection.getConnection().prepareCall ("{ ? = call RTW_GET_RANDOM_VALUE (?,?)}");
					gen_val.registerOutParameter (1, OracleTypes.CLOB);
					gen_val.setString (2,cbTable.getSelectedItem().toString());*/
					
					gen_inf.execute ();
	        	
					ResultSet rset_info = (ResultSet)gen_inf.getObject (1);
			    
					if(updater == null){
    					updater = dbConnection.getConnection().prepareCall ("{ call RTW_UPDATE_ATTR_AT (?,?,?)}");
    				}
					
					/*if(gen_val == null){
						gen_val = dbConnection.getConnection().prepareCall ("{ ? = call RTW_GET_RANDOM_VALUE (?,?)}");
						gen_val.setString (2,cbTable.getSelectedItem().toString());
    				}*/
					
				    updater.setString(1, cbTable.getSelectedItem().toString());
				    
				    
				    while (rset_info.next ()){
				    	//Vector<Integer> temp = new Vector<Integer>();
				    	//int i = 0;
				    	//do {
				    		//int index = (int) (Math.random() * 10);
				    	//	if(!temp.contains(index)) {
				    	//		temp.add(index);
				    	//		updater.setString (2, attribute_array[index]);
				    			updater.setString (2, attribute_array[0]);
				    			
				    			gen_inf.setString (2,cbTable.getSelectedItem().toString());
				    			
				    			//gen_val.setString (3,attribute_array[index]);
				    			//gen_val.execute ();
								//ResultSet rset_val = (ResultSet)gen_val.getObject (1);
								
								//while (rset_val.next ()){updater.setClob(3, rset_val.getClob (1));}
								updater.setInt(3, Integer.valueOf(rset_info.getString ("ID")));
								updater.execute ();
								//i++;
				    		//}
				    	//}while(i < (Integer)spAtr.getValue());
				    	//temp.clear();
				    }
					
				    rset_info.close();
				    gen_inf.close();
					
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
			    	Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "Subset delete attack completed...");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
			getContentPane().add(btnStart);
		
			JButton btnExit = new JButton("Close");
			btnExit.setBounds(308, 127, 122, 23);
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
			
			JLabel lblPercAttackedTuples = new JLabel("Updates Tuples (%):");
			lblPercAttackedTuples.setHorizontalAlignment(SwingConstants.RIGHT);
			lblPercAttackedTuples.setBounds(273, 10, 116, 14);
			getContentPane().add(lblPercAttackedTuples);
			
			txPercAttack = new JTextField();
			txPercAttack.setText("10");
			txPercAttack.setColumns(10);
			txPercAttack.setBounds(392, 7, 37, 20);
			getContentPane().add(txPercAttack);
			
			JLabel lblNoAttrs = new JLabel("No. Attrs:");
			lblNoAttrs.setHorizontalAlignment(SwingConstants.RIGHT);
			lblNoAttrs.setBounds(273, 38, 116, 14);
			getContentPane().add(lblNoAttrs);
			
			spAtr = new JSpinner();
			spAtr.setModel(new SpinnerNumberModel(1, 1, 10, 1));
			spAtr.setBounds(392, 35, 38, 20);
			getContentPane().add(spAtr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private JComboBox<String> getJCBTable(){
		if(this.cbTable == null){
			try {
				this.cbTable = new JComboBox<String>();
				///cbTable.setModel(new DefaultComboBoxModel<String>(new String[] {"UNIVE_SYLLABUS"}));
				cbTable.setModel(new DefaultComboBoxModel<String>(new String[] {"TEX_DOCUMENTS"}));
				cbTable.setSelectedIndex(0);
				this.cbTable.setBounds(123, 8, 140, 20);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this.cbTable;
	}
}
