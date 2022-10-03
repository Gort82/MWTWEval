package wrd.ibw;

import wrd.ibw.gui.FrmMain;


public class Run {
	//Forms
	private static FrmMain frmDB = null;
	

	public static void main(String[] args) {
		
		//Display the DBForm for establish the connection...
		if(frmDB == null){
			frmDB = new FrmMain();
			frmDB.setLocationRelativeTo(null);
		}
		frmDB.setVisible(true);

	}

}
