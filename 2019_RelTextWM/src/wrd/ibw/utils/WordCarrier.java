package wrd.ibw.utils;

//import edu.cmu.lti.jawjaw.pobj.POS;

import net.didion.jwnl.data.POS;

public class WordCarrier {
    private String word;
    private POS role;
    private int index;
    
    public WordCarrier(String pWword, POS pRole, int pIndex){
    	this.word = pWword;
    	this.role = pRole;
    	this.index = pIndex;
    }
    
    public String getWord(){
    	return this.word;
    }
    
    public POS getRole(){
    	return this.role;
    }
    
    public int getIndex(){
    	return this.index;
    }
}
