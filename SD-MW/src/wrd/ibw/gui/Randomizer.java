package wrd.ibw.gui;

import java.awt.ComponentOrientation;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;

import wrd.ibw.da.DBConnection;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import oracle.jdbc.OracleTypes;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Randomizer extends JFrame {
	private static final long serialVersionUID = -1512879679573325942L;
	
	private DBConnection dbConnection = null;
	private JTextField txtPK;
	private JTextField txtAlto;
	private JTextField txtAncho;
	
	public Randomizer(DBConnection pDBConnection) {
		this.dbConnection = pDBConnection;
		try {
			
			//myProgressBar = new wrd.ibw.utils.ProgressBar();
			
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		this.setTitle("TESTER");
		this.setSize(233,144);
		this.getContentPane().setLayout(null);
		
		JButton btnStart = new JButton("Start");
		btnStart.setBounds(63, 71, 97, 23);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				//GET THE IMAGE ARRAY
				try {
					
					Calendar cal = Calendar.getInstance();
			    	cal.getTime();
			    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			    	System.out.println("Process started at: " + sdf.format(cal.getTime()) );
			    	
			    	int[][] image = new int[Integer.parseInt(txtAlto.getText())][Integer.parseInt(txtAncho.getText())];
			    	
			    	for (int y = 0; y < Integer.parseInt(txtAlto.getText()); y++) {
			    		for (int x = 0; x < Integer.parseInt(txtAncho.getText()); x++) {
			    			image[y][x]=0;
						}
					}
			    	
			    	
					CallableStatement ids = dbConnection.getConnection().prepareCall ("{ ? = call RANDOMIZER (?,?,?)}");
					ids.registerOutParameter (1, OracleTypes.CURSOR);
					ids.setString (2,txtPK.getText());
					ids.setInt (3, Integer.parseInt(txtAlto.getText())-1);
					ids.setInt (4, Integer.parseInt(txtAncho.getText())-1);
					
					
					
					
					ids.execute ();
				    ResultSet rset_pk = (ResultSet)ids.getObject (1);
				    
		        	int width_pos = 0;
		        	int height_pos = 0;
		        	
		    	    int cc=1;
		    	    
		    	    boolean complete = false;
				    
				    while (rset_pk.next () && !complete){
				        //System.out.println (rset_pk.getString ("ID")+" VPK: "+rset_pk.getString ("VPK"));
				    	
				    	height_pos = rset_pk.getInt("H");//randomizer.getInt(4);
				        width_pos =  rset_pk.getInt("W");//randomizer.getInt(5);
				        	
				        //if(cc<101){
				        	System.out.println ("No: " + cc + "! ID: " + rset_pk.getString ("ID") + "! VPK: " + rset_pk.getString ("VPK") + "  !! COORD_X: " + width_pos + "  !! COORD_Y: " + height_pos);
				        	
				        	image[height_pos][width_pos]=1;
				        	
				        	cc++;
				        	
				        	for (int y = 0; y < Integer.parseInt(txtAlto.getText()); y++) {
					    		for (int x = 0; x < Integer.parseInt(txtAncho.getText()); x++) {
					    			
					    			if(image[y][x]==0){
					    				complete = false;
					    				break;
					    			}else{
					    				complete = true;
					    			}
								}
							}
				        	
				        //}
				        
				    }
				    
				    rset_pk.close();
				    ids.close();
					
				    Calendar cal1 = Calendar.getInstance();
			    	cal1.getTime();
			    	SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
			    	System.out.println ("Process completed at: "+sdf1.format(cal1.getTime()));
					JOptionPane.showMessageDialog(null, "Process completed...");
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				
				
				 
			}
		});
		getContentPane().add(btnStart);
		
		JLabel lblLlave = new JLabel("Llave:");
		lblLlave.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLlave.setBounds(10, 11, 39, 14);
		getContentPane().add(lblLlave);
		
		JLabel lblAlto = new JLabel("Alto:");
		lblAlto.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAlto.setBounds(121, 11, 39, 14);
		getContentPane().add(lblAlto);
		
		JLabel lblAncho = new JLabel("Ancho:");
		lblAncho.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAncho.setBounds(121, 43, 39, 14);
		getContentPane().add(lblAncho);
		
		txtPK = new JTextField();
		txtPK.setText("82MR");
		txtPK.setBounds(52, 8, 59, 20);
		getContentPane().add(txtPK);
		txtPK.setColumns(10);
		
		txtAlto = new JTextField();
		txtAlto.setText("3");
		txtAlto.setBounds(163, 8, 46, 20);
		getContentPane().add(txtAlto);
		txtAlto.setColumns(10);
		
		txtAncho = new JTextField();
		txtAncho.setText("3");
		txtAncho.setBounds(163, 40, 46, 20);
		getContentPane().add(txtAncho);
		txtAncho.setColumns(10);
		
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
}
