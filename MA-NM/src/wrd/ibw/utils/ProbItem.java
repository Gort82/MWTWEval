package wrd.ibw.utils;

public class ProbItem {
	private double bas_val = 0;   //according to the paper, It is the one having decimals... ej. 0.042712342
	private int proj_val = 0;     //the one taken based in the image w x h  size (basic PH)  ej. 7
	private int ext_val = 0;      //the one considering PV projection (extended * maximum_allowed:recurrences) ej. 7 * 2 = 21
	private int curr_val = 0;     //the current one considering the number of times the mark has been embedded already 21 - 7 - 7 etc...
	private int init_index = 0;
	private int left_selec = 0;
	private int cant_emb = 0;
	public ProbItem(double pBasVal, int pProjVal, int pExtVal, int pCurrVal, int pInitIndex, int pLeftSel) {
		this.bas_val = pBasVal;
		this.proj_val = pProjVal;
		this.ext_val = pExtVal;
		this.curr_val = pCurrVal;
		this.init_index = pInitIndex;
		this.left_selec = pLeftSel;
		this.cant_emb = 0;
	}
	
	//setters...
	public void setBasVal(double pBasVal) {
		this.bas_val = pBasVal;
	}
	
	public void setProjVal(int pProjVal) {
		this.proj_val = pProjVal;
	}
	
	public void setExtVal(int pExtVal) {
		this.ext_val = pExtVal;
	}
	
	public void setCurrVal(int pCurrVal) {
		this.curr_val = pCurrVal;
	}
	
	public void setInitIndex(int pInitIndex) {
		this.init_index = pInitIndex;
	}
	
	public void setLeftSel(int pLeftSel) {
		this.left_selec = pLeftSel;
	}
	
	public void incCantEmb() {
		this.cant_emb++;
	}
	
	//getters...
	public double getBasVal() {
		return this.bas_val;
	}
	
	public int getProjVal() {
		return this.proj_val;
	}
	
	public int getExtVal() {
		return this.ext_val;
	}
	
	public int getCurrVal() {
		return this.curr_val;
	}
	
	public int getInitIndex() {
		return this.init_index;
	}
	
	public int getLeftSel() {
		return this.left_selec;
	}
	
	public int getCantEmb() {
		return this.cant_emb;
	}
}



