package wrd.ibw.utils;

import java.util.Vector;

import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import wrd.ibw.wn.WNCaller;
public class StringPlayer {
	private WNCaller fWNCaller = null;
	private String currentSentence = "";
	private String[] currentWords = null;
	private Vector<String> candidateWords = null;
	private Vector<String> keySense = null;
	
	public StringPlayer(String pSentence){
		this.currentSentence = pSentence;
		this.fWNCaller = new WNCaller();
		this.candidateWords = new Vector<String>();
		this.keySense = new Vector<String>();
	}
	
	public void splitSentence(){
		currentWords = null;
		currentWords = this.currentSentence.split("\\s+");
	}
	
	public void findCandidates(){
		for (int j = 0; j < currentWords.length; j++) {
			try {
				if (this.fWNCaller.getDictionary().getIndexWord(POS.ADJECTIVE, currentWords[j]) != null) {
					candidateWords.add(currentWords[j]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getCandidates(){
		for (int j = 0; j < candidateWords.size(); j++) {
			System.out.println(candidateWords);
		}
	}
	
	public void getSenseKeys(){
		for (int j = 0; j < keySense.size(); j++) {
			System.out.println(keySense);
		}
	}
	
	
	public void printSynonyms(){
		String tempStr = "";
		try{
			IndexWord indexWord = null;
			indexWord = this.fWNCaller.getDictionary().getIndexWord(POS.ADJECTIVE, candidateWords.firstElement());
            Synset[] synsets = indexWord.getSenses();
			for (Synset set : synsets) {
				
				
				for (Word w : set.getWords()) {
                    if (w.getLemma().equalsIgnoreCase(indexWord.getLemma())) {
                    	keySense.add(w.getSenseKey());
                    }
                }
				
				
				
				//segmento que me da la palabra key_sense sola, no su estructura.
				 //System.out.println(set.toString());
				/* Word[] words = set.getWords();
				 tempStr = words[0].getSenseKey();
				 tempStr = tempStr.substring(tempStr.indexOf(":00:")+7);
				 tempStr = tempStr.substring(0, tempStr.indexOf(":00"));
				 keySense.add(tempStr);*/
				 
				 
				 
				 //for(int i = 0;i < words.length; i++){
				 		//System.out.println("TODO:" + words[i].toString());
				 		// lemma -- set de palabras para remplazar, por ahora afuera 
					 	//las usare despues   System.out.println("LEMA:" + words[i].getLemma());
		         //7}
			}
		}
		
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}

	
}
