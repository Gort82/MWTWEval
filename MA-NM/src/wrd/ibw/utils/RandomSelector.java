package wrd.ibw.utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class RandomSelector {
	ArrayList<Item> items = new ArrayList<Item>();
    Random rand = new Random();
    int totalSum = 0;
    
    public int getTotalSum() {
    	return this.totalSum;
    }

    public RandomSelector(Vector<Coord> marks, Vector<ProbItem> prob) {
    	totalSum = 0;
    	/*items.add(new Item('A', 70));
    	items.add(new Item('B', 20));
    	items.add(new Item('C', 10));*/
    	
    	for (int i = 0; i < marks.size(); i++) {
    		items.add(new Item(marks.elementAt(i),prob.elementAt(i))); 
		}
    	
        for(Item item : items) {
            totalSum = totalSum + item.getRelProb().getCurrVal();
        }
    }

    public Item getRandom(long Seed) {
    	
    	rand.setSeed(Seed);
    	
    	int index = rand.nextInt(totalSum);
    	
    	
    	
    	//rand.setSeed(totalSum);
    	//long index = rand.nextLong();
    	
    	int sum = 0;
        int i=0;
        while(sum < index ) {
        	sum = sum + items.get(i++).getRelProb().getCurrVal();
        }
        return items.get(Math.max(0,i-1));
    }
    
    public void setProbDistr(Vector<ProbItem> prob) {
    	totalSum = 0;
    	
    	for (int i = 0; i < prob.size(); i++) {
    		items.set(i, new Item(items.get(i).getCoord(), prob.elementAt(i))); 
		}
    	
    	for(Item item : items) {
            totalSum = totalSum + item.getRelProb().getCurrVal();
        }
    	
    	System.out.println(totalSum);
    }
}
