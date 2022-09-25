package wrd.ibw.utils;

public class MetrStat {
    private double value;
    private int amount;
    
    public MetrStat(double pValue, int pAmount){
    	this.value = pValue;
    	this.amount = pAmount;
    }
    
    public double getValue(){
    	return this.value;
    }
    
    public int getAmount(){
    	return this.amount;
    }
    
}
