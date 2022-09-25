package wrd.ibw.utils;

public class LockEntry {
    private String lockName;
    private int lockLength;
    
    public LockEntry(String pLockName, int pLockLength){
    	this.lockName = pLockName;
    	this.lockLength = pLockLength;
    }
    
    public String getLockName(){
    	return this.lockName;
    }
    
    public int getLockLength(){
    	return this.lockLength;
    }
}
