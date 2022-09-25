package wrd.ibw.wn;

import java.io.FileInputStream;
import java.util.List;
import java.util.Vector;

import edu.cmu.lti.jawjaw.util.WordNetUtil;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;
import wrd.ibw.utils.WordCarrier;

public class WNCaller {
	private Dictionary dictionary = null;
	public WNCaller(){
		try{
			JWNL.initialize(new FileInputStream("C:/Workspace/2019_RelTextWM/Properties.XML"));
			dictionary = Dictionary.getInstance();
		}
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	
	public Dictionary getDictionary(){
		return this.dictionary;
	}
	
	public double getPairScore(String pOrigWord, String pNewWord, int pRC){
		ILexicalDatabase db = new NictWordNet();
		WS4JConfiguration.getInstance().setMFS(true);
		
		//RelatednessCalculator rc = new Path(db);
		
		//RelatednessCalculator[] rcs = { new HirstStOnge(db),new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
        //        new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db) };
		
		//RelatednessCalculator[] rcs = { new WuPalmer(db), new JiangConrath(db), new LeacockChodorow(db), new Lin(db), new Resnik(db), new Path(db), new Lesk(db), new HirstStOnge(db) };
		
		                                                                             //RelatednessCalculator[] rcs = {new Resnik(db), new Path(db), new Lesk(db), new HirstStOnge(db) };
		                                                                             
																					RelatednessCalculator[] rcs = {new HirstStOnge(db) };
		
		                                                                            // RelatednessCalculator[] rcs = {new Resnik(db), new Path(db), new Lesk(db)                      };
		                                                                             
		                                                                           // lentoo //RelatednessCalculator[] rcs = {new Resnik(db), new Path(db),               new HirstStOnge(db) };
		                                                                             
		                                                                            //lentoo RelatednessCalculator[] rcs = {new Resnik(db),               new Lesk(db), new HirstStOnge(db) };
		                                                                             
		                                                                            //lentoo  RelatednessCalculator[] rcs = {                new Path(db), new Lesk(db), new HirstStOnge(db) };
		
		List<edu.cmu.lti.jawjaw.pobj.POS[]> posPairs = rcs[pRC].getPOSPairs();
		double maxScore = -1D;
		for (edu.cmu.lti.jawjaw.pobj.POS[] posPair: posPairs) {
			List<Concept> synsets1 = (List<Concept>)db.getAllConcepts(pOrigWord, posPair[0].toString());
			List<Concept> synsets2 = (List<Concept>)db.getAllConcepts(pNewWord, posPair[1].toString());
			for (Concept synset1: synsets1) {
				for (Concept synset2: synsets2) {
					Relatedness relateness = rcs[pRC].calcRelatednessOfSynset(synset1, synset2);
					double score = relateness.getScore();
					if (score > maxScore) {
						maxScore = score;
					}
				}
			}
		}
		
		if (maxScore == -1D) {
			maxScore = 0.0;
		}
		
		return maxScore;
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////
	public Synset getSynsetWord(WordCarrier pWordCarrier, String pContent, int pMethod){
		// is pMethod = 1 --> lesk algorithm;
		Synset sense = null;
		String glossary = ""; 
		String[] contentWords = pContent.split("\\s+");
		String[] glosaryWords = null;
		int sensIndex = 0;
		try {
			if (pMethod == 1) { // LESK ALGORITHM
				
				IndexWord indexWord = this.dictionary.getIndexWord(pWordCarrier.getRole(), pWordCarrier.getWord());
				
				Synset[] senses = indexWord.getSenses();
				
				//List<edu.cmu.lti.jawjaw.pobj.Synset> synsets = WordNetUtil.wordToSynsets(pWordCarrier.getWord(), pWordCarrier.getRole());
			
				int[] contSenses = new int[senses.length];
				
				//int[] contSenses = new int[synsets.size()];
				
				
				for (Synset set : senses) {
				//for (edu.cmu.lti.jawjaw.pobj.Synset set : synsets) {
					
					glossary = set.getGloss();
					//glossary = set.get
					
					glosaryWords = glossary.split("\\s+");
					
					
					for (int i = 0; i < glosaryWords.length; i++) {
						for (int j = 0; j < contentWords.length; j++) {
							if (glosaryWords[i].equals(contentWords[j])) {
								contSenses[sensIndex]++;
							}
						}
					}
					sensIndex++;
				}
				
				
				int pos = 0;
				int max = contSenses[pos];
				
				for (int i = 1; i < contSenses.length; i++) {
					
					if (max < contSenses[i]) {
						pos = i;
						max = contSenses[pos];
					}
				}
				
				
				return senses[pos];
				
				
			} else {
				ILexicalDatabase db = new NictWordNet();
				
				WS4JConfiguration.getInstance().setMFS(true);
					
				IndexWord indexWord = this.dictionary.getIndexWord(pWordCarrier.getRole(), pWordCarrier.getWord());
				
				Synset[] senses = indexWord.getSenses();
			
				double[] contSenses = new double[senses.length];
				
				
				for (Synset set : senses) {
					
					glossary = set.getGloss();
					glosaryWords = glossary.split("\\s+");
					
					
					for (int i = 0; i < glosaryWords.length; i++) {
						for (int j = 0; j < contentWords.length; j++) {
							contSenses[sensIndex] = contSenses[sensIndex] + new Lesk(db).calcRelatednessOfWords(glosaryWords[i],contentWords[j]);
						}
					}
					sensIndex++;
				}
				
				
				int pos = 0;
				double max = contSenses[pos];
				
				for (int i = 1; i < contSenses.length; i++) {
					
					if (max < contSenses[i]) {
						pos = i;
						max = contSenses[pos];
					}
				}
				
				return senses[pos];

			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return sense;
	}
	
	//public Vector<String> getSubsSet(WordCarrier pWordCarrier, String pSenseWord){
	public Vector<String> getSubsSet(Synset pSet, int minSize){
		Vector<String> subsWords = new Vector<String>(); 
		try{
			////IndexWord indexWord = null;
			////indexWord = this.dictionary.getIndexWord(pWordCarrier.getRole(), pWordCarrier.getWord());
			
            ////Synset[] senses = indexWord.getSenses();
			//for (Synset set : senses) {
            	////Synset set = senses[0];
				 /////Word[] words = set.getWords();
				 Word[] words = pSet.getWords();
				 //if(words[0].getSenseKey().indexOf(pSenseWord) != -1){
					 
					 for(int i = 0;i < words.length; i++){
						 if((words[i].getLemma().length() > minSize)&&(words[i].getLemma().equals(words[i].getLemma().toLowerCase()))&&(!words[i].getLemma().matches(".*\\d.*")))
							 subsWords.addElement(words[i].getLemma());
			         }
				 //}
			//}
		}
		
		catch(Exception ex){
			System.out.println(ex.getMessage());
		}
		return subsWords;
	}

}
