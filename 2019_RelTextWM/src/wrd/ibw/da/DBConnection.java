package wrd.ibw.da;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Vector;

import oracle.jdbc.OracleTypes;

public class DBConnection {
	private Connection connection = null;
	
	public DBConnection(String server, String sid, String user, String pass){
		try{
			Class.forName("oracle.jdbc.OracleDriver");
			connection = DriverManager.getConnection("jdbc:oracle:thin:@//"+server+":1521/"+sid, user, pass);
						
			if (connection != null) {
				System.out.println("You made it, take control your database now!");
			} else {
				System.out.println("Failed to make connection!");
			}
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	
	
	public ArrayList<String> getTables() throws Exception{
		try {
			Statement smt = connection.createStatement();
			ResultSet rs = smt.executeQuery("SELECT DISTINCT OBJECT_NAME FROM USER_OBJECTS WHERE OBJECT_TYPE = 'TABLE'");
			ArrayList<String> tableNames = new ArrayList<String>();
			while(rs.next()){
				tableNames.add(rs.getString("OBJECT_NAME"));
			}
			return tableNames;
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	
	public ResultSet getFields(String table) throws Exception{
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT column_name, data_type FROM user_tab_cols WHERE table_name='" + table +"' order by data_type");
			return pst.executeQuery();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public ResultSetMetaData getAttrMD(String table) throws Exception{
		ResultSetMetaData rsmd = null; 
		ResultSet rs = null;
		try {
			Statement smt = connection.createStatement();
			rs = smt.executeQuery("SELECT * FROM " + table);
			rsmd = rs.getMetaData();
			return rsmd;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public boolean getAttrSW(String table, String pAttrName, int pMaxWords) throws Exception{
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		int noRowsCond = 0;
		int noRows = 0;
		boolean result = false;
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) FROM " + table + " WHERE REGEXP_COUNT(" + pAttrName + ", ' ') <= " + pMaxWords);
			rs1 = pst.executeQuery();
			
			Statement smt = connection.createStatement();
			rs2 = smt.executeQuery("SELECT COUNT(*) FROM " + table);
			
			if (rs1.next()) {
				noRowsCond = rs1.getInt(1);
			}  
			
			if (rs2.next()) {
				noRows = rs2.getInt(1);
			} 
			
			if (noRowsCond == noRows) {
				result = true;
			}  
			
			return result;
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public int getMultiWordAttr(String table, String pAttrName, int pNoSpace) throws Exception{
		ResultSet rs = null;
		int numberOfRows = 0;
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT COUNT(*) FROM " + table + " WHERE REGEXP_COUNT(" + pAttrName + ", ' ') >" + pNoSpace);
			rs = pst.executeQuery();
			
			if (rs.next()) {
				numberOfRows = rs.getInt(1);
			}  
			return numberOfRows;
		} catch (Exception e) {
			throw e;
		}
	}
	
	

    
    
    
	
	
	
	
	
	
	public ResultSet getTableData(String table) throws Exception{
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT * FROM " + table);
			return pst.executeQuery();
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public Connection getConnection(){
		return this.connection;
	}
	
	
	public int getNoRows(String pRelation, String pSecretKey, int pTupleFract)throws Exception{
		int no_rows = 0;
		try {
			CallableStatement no_tuples = connection.prepareCall ("{ ? = call GET_CANT_ROWS (?,?,?)}");
			no_tuples.registerOutParameter (1, Types.INTEGER);
			no_tuples.setString (2,pRelation);
			no_tuples.setString (3,pSecretKey);
			no_tuples.setInt (4, pTupleFract);
			no_tuples.execute ();
			no_rows = no_tuples.getInt(1);
			no_tuples.close();
		} catch (Exception e) {
			throw e;
		}
		
		return no_rows;
	}
	
	public int getAllRows(String pRelation)throws Exception{
		int no_rows = 0;
		try {
			CallableStatement no_tuples = connection.prepareCall ("{ ? = call GET_ALL_ROWS (?)}");
			no_tuples.registerOutParameter (1, Types.INTEGER);
			no_tuples.setString (2,pRelation);
			no_tuples.execute ();
			no_rows = no_tuples.getInt(1);
			no_tuples.close();
		} catch (Exception e) {
			throw e;
		}
		
		return no_rows;
	}
	
	public Vector<Integer> getQuantilQuotes(String pRelation, String pAttribute, int pSegments)throws Exception{
		Vector<Integer> no_di = new Vector<Integer>();
		try {
			CallableStatement attr_query = connection.prepareCall ("{ ? = call GET_QUANTIL_QUOTAS (?,?,?)}");
			ResultSet attr_results = null;
			
			attr_query.registerOutParameter (1, OracleTypes.CURSOR);
			attr_query.setString (2,pRelation);
			attr_query.setInt (3,pSegments);
			attr_query.setString (4,pAttribute);
			attr_query.execute ();
			attr_results = (ResultSet)attr_query.getObject (1);
			
			no_di.add(0);
			
			while (attr_results.next ()){
				no_di.add(attr_results.getInt("MAX_VAL"));
			}
			attr_results.close();
			attr_query.close();
		} catch (Exception e) {
			throw e;
		}
		return no_di;
	}
	
	

}
