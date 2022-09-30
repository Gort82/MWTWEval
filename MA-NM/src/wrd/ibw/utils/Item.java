package wrd.ibw.utils;

public class Item {
	private Coord coord;
	private ProbItem relProb;
    
	public Coord getCoord() {
		return this.coord;
	}
	
	public ProbItem getRelProb() {
		return this.relProb;
	}
	
	public void setName(Coord pCoord) {
		this.coord = pCoord;
	}
	
	public void setRelProb(ProbItem pProb) {
		this.relProb = pProb;
	}

    //Getters Setters and Constructor
    
    
    public Item (Coord pCoord, ProbItem pProb) {
    	this.coord = pCoord;
    	this.relProb = pProb;
    }
    
}
