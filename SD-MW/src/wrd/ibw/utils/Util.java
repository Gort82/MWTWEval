package wrd.ibw.utils;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.BreakIterator;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import static java.lang.Math.pow;


import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import JSci.maths.AbstractMath;
import JSci.maths.ArrayMath;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
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
import net.didion.jwnl.data.POS;
import wrd.ibw.utils.jssim.*;
import wrd.ibw.wn.WNCaller;

import java.awt.*;

public class Util {
	private static File imageFie = null;
	private static int matrixWidth = 0;  //corresponding to the x - values
	private static int matrixHeight = 0; //corresponding to the y - values
	private static int pixelMatrix[][];
	private static Vector<Integer> vector = new Vector<>();
	
	public static void clearImage(){
		vector.clear();
		pixelMatrix = null;
	}
	
	public static int[][] getImageMatrix(){
		return pixelMatrix;
	}
	
	
	
	
	//Method for get the matrix from the image file
	private static void getMatrixFromImageFile(File imgFile) throws Exception{
		try {
			imageFie = imgFile;
			BufferedImage imgBuffered = ImageIO.read(imageFie);
			Raster raster = imgBuffered.getData();
			matrixWidth = raster.getWidth(); 
			matrixHeight = raster.getHeight();
			pixelMatrix = new int[matrixHeight][matrixWidth];
			for (int x = 0; x < matrixWidth; x++){
		        for(int y = 0; y < matrixHeight; y++){
		        	pixelMatrix[y][x] = raster.getSample(x,y,0);
		            //System.out.println("x:" + x + ", y:" + y + " "+ pixelMatrix[y][x]);
		        }
		    }
		} catch (Exception e) {
			throw e;
		}
	}
		
		//Method for convert the matrix into a vector
	public static void defineImageArray(File imgFile)throws Exception{
		Util.getMatrixFromImageFile(imgFile);
		for(int y = 0; y < matrixHeight; y++){
			for (int x = 0; x < matrixWidth; x++){
	        	vector.add(pixelMatrix[y][x]);
	        }
	    }
		
		for(int z = 0; z < vector.size(); z++){
			//System.out.println(vector.elementAt(z)+", ");
		}
	}
	
	public static int parseString(String pBinaryString, int pMaxLEngth){
		int fragments = pBinaryString.length()/pMaxLEngth;
		int incompl = pBinaryString.length() % pMaxLEngth;
		int z = 0;
		
		String invertedString = "";
		String finalString = "";
		String tempString  = "";
		
		for (int i = 0; i < pMaxLEngth; i++) {
			finalString = finalString + '0';
		}
		
		for (int i = 0; i < pBinaryString.length(); i++) {
			invertedString = invertedString + pBinaryString.charAt(pBinaryString.length()-i-1);
		}
		
		for (int i = 0; i < fragments; i++) {
			z = 0;
			for (int j = pMaxLEngth * i  ; j < pMaxLEngth + (pMaxLEngth*i); j++) {
				tempString = tempString + (byte) (finalString.charAt(z) ^ invertedString.charAt(j));
				z++;
			}
			finalString = tempString;
			tempString = "";
		}
		z = 0;
		if (incompl != 0) {
			for (int i = 0; i <incompl; i++) {
				tempString = tempString + (byte) (finalString.charAt(z) ^ invertedString.charAt(fragments * pMaxLEngth + i));
				z++;
			}
			for (int i = incompl; i < pMaxLEngth; i++) {
				tempString = tempString + finalString.charAt(i);
			}
			finalString = tempString;
			tempString = "";
		}
		//System.out.println(finalString.length());
		return Integer.parseInt(finalString,2);
	}
	
	
	
	//public static String[] splitParagraph(String pParagraph, int pKey, String attrName){
	public static Vector<String> splitParagraph(String pParagraph){
		Vector<String> splittedString = new Vector<String>();
		BreakIterator bi = BreakIterator.getSentenceInstance();
		bi.setText(pParagraph);
		int index = 0;
		
		try {
			while (bi.next() != BreakIterator.DONE) {
				splittedString.add(pParagraph.substring(index, bi.current()));
				index = bi.current();
			}
			
			for (int i = 0; i < splittedString.size(); i++) {
				if (splittedString.elementAt(i).charAt(splittedString.elementAt(i).length()-1) == ' ') {
					splittedString.set(i, splittedString.elementAt(i).substring(0, splittedString.elementAt(i).length()-1));
				}
			}
			
			
			
		} catch (Exception e) {
			System.out.println(pParagraph);
		}
		return splittedString;
	}
	
	
	public static String getLockStream(WNCaller pWNCaller, String pSentence, Vector<POS> pItems, boolean pRest, int idTupl){
		Vector<String> streamWords = new Vector<String>();
		String finalStream = "";
		WNCaller tempWNCaller = pWNCaller;
		String[] wordsOfSentence = pSentence.split("\\s+");
		boolean include = false;
		boolean exclude = false;
		
		try {
			
		
		
		
		
		if (!pRest) {
			for (int i = 0; i < wordsOfSentence.length; i++) {
				for (int j = 0; j < pItems.size(); j++) {
					try {
						if (tempWNCaller.getDictionary().getIndexWord(pItems.elementAt(j), wordsOfSentence[i]) != null) {
							include = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (include) {
					streamWords.add(wordsOfSentence[i]);
					include = false;
				}
			}
			
			for (int i = 0; i < streamWords.size(); i++) {
				finalStream = finalStream + Util.getBinary(streamWords.get(i));
			}

		} else {
			Vector<String> fullWords = new Vector<String>();
			String wordToRemove = "";
			for (int i = 0; i < wordsOfSentence.length; i++) {
				fullWords.addElement(wordsOfSentence[i]);
			}
			for (int i = 0; i < wordsOfSentence.length; i++) {
				for (int j = 0; j < pItems.size(); j++) {
					try {
						if (tempWNCaller.getDictionary().getIndexWord(pItems.elementAt(j), wordsOfSentence[i]) != null) {
							exclude = true;
							wordToRemove = wordsOfSentence[i];
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (exclude) {
					fullWords.remove(wordToRemove);
					exclude = false;
				}
			}
			
			for (int i = 0; i < fullWords.size(); i++) {
				finalStream = finalStream + Util.getBinary(fullWords.get(i));
				
				
				if(idTupl == 42)
					System.out.println(fullWords.get(i));
				
			}
		}
		
		} catch (Exception e) {
			
		    //System.out.println("SENSE: " + pSentence);
			e.printStackTrace();
		}
		
		if(idTupl == 42)
			System.out.println("-------");
		
		return finalStream;
	}
	
	
	public static String getBinary(String pString){
		byte[] bytes = pString.getBytes();
		StringBuilder binaryVal = new StringBuilder();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				binaryVal.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
			//binaryVal.append(' ');
		}
		return binaryVal.toString();
	}
	
	public static Vector<Integer> getImageVector(){
		return Util.vector;
	}
	
	public static int getImageWidth(){
		return Util.matrixWidth;
	}
	
	public static int getImageHeight(){
		return Util.matrixHeight;
	}
	
	
	public static Image getImageFromArray(int[] pixels, int width, int height) {
        try {
	        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	        WritableRaster raster = (WritableRaster) image.getData();
	        //raster.setPixels(0,0,width,height,pixels);
	        raster.setPixels(0,0,3,3,pixels);
	        
	        
	        
	        
	        return image;
        }catch(Exception e){
        	e.printStackTrace();
        	return null;
        }
        
        
    }
	
	public static String minimizeVariation(String originalBinary, String changedBinary, int lsb_pos, int pos){
		String newBinary = changedBinary;
		
		char main_char = '0';
		char replace_char = '1';
		int change_counter = 0;
		
		if(changedBinary.charAt(changedBinary.length()-lsb_pos)=='1'){
			main_char = '1';
			replace_char = '0';
		}
		
		
		if((lsb_pos > 1) && (originalBinary.charAt(originalBinary.length()-lsb_pos) != changedBinary.charAt(changedBinary.length()-lsb_pos))){
			for (int i = changedBinary.length()-lsb_pos+1; i < changedBinary.length(); i++) {
				if(changedBinary.charAt(i) == main_char){
					newBinary = changedBinary.substring(0,i) + replace_char + changedBinary.substring(i+1,changedBinary.length());
					changedBinary = newBinary;
					change_counter++;
					
					if (pos == change_counter) {
						i = changedBinary.length();
					} 
					//i = changedBinary.length();
				}
			}
		}
		return newBinary;
	}
	
	public static String formSentence(String[] pWords){
		String sentence = "";
		for (int i = 0; i < pWords.length; i++) {
			sentence = sentence + pWords[i] + ' ';
		}
		//sentence = sentence.substring(0, sentence.length() - 1) + '.';
		sentence = sentence.substring(0, sentence.length() - 1);
		return sentence;
	}
	
	//public static String updateParagraph(String pParagraph, String pSentence, int pIndex, int pKey, String attrName){
	public static String updateParagraph(String pParagraph, String pSentence, int pIndex){
		String paragraph = "";
		//String sentences[] = Util.splitParagraph(pParagraph, pKey, attrName);
		Vector<String> sentences = Util.splitParagraph(pParagraph);
		
		for (int i = 0; i < sentences.size(); i++) {
			if (i != pIndex) {
				
				if(paragraph.length()==0)
					paragraph = paragraph + sentences.elementAt(i);
				else
					//paragraph = paragraph + ". " + sentences[i];
					paragraph = paragraph + " " + sentences.elementAt(i);
				
			}else{
				
				if(paragraph.length()==0)
					paragraph = paragraph + pSentence;
				else
					//paragraph = paragraph + ". " + pSentence;
					paragraph = paragraph + " " + pSentence;
			}
			
		}
		
		return paragraph;
	}
	
	public static BufferedImage toBufferedImage(Image img)
	{
	    if (img instanceof BufferedImage){
	        return (BufferedImage) img;
	    }

	    // Create a buffered image with transparency
	    //BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_BYTE_BINARY);
	    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_BGR);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(img, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
	
	public static double getCorrelation(Integer[] xs, Integer[] ys) {
	    //TODO: check here that arrays are not null, of the same length etc

	    double sx = 0.0;
	    double sy = 0.0;
	    double sxx = 0.0;
	    double syy = 0.0;
	    double sxy = 0.0;

	    int n = xs.length;

	    for(int i = 0; i < n; ++i) {
	      double x = xs[i];
	      double y = ys[i];

	      sx += x;
	      sy += y;
	      sxx += x * x;
	      syy += y * y;
	      sxy += x * y;
	    }

	    // covariation
	    double cov = sxy / n - sx * sy / n / n;
	    // standard error of x
	    double sigmax = Math.sqrt(sxx / n -  sx * sx / n / n);
	    // standard error of y
	    double sigmay = Math.sqrt(syy / n -  sy * sy / n / n);

	    // correlation is just a normalized covariation
	    return cov / sigmax / sigmay;
	  }

	
	
	
	
	
	
	public static double getMyCorrelation(Integer[] pX, Integer[] pY) {
		
		double[] temp1 = new double[pX.length];
		double[] temp2 = new double[pX.length];
		
		for (int i = 0; i < pY.length; i++) {
			temp1[i] = pX[i];
			temp2[i] = pY[i];
		}
		
		
		return ArrayMath.correlation(temp1, temp2);
	  }
	
	

	public static Vector<Integer> getVectorFromMatrix(int[][] pMatrix, int pWidth, int pHeight)throws Exception{
		Vector<Integer> result = new Vector<Integer>();
		for(int y = 0; y < pHeight; y++){
			for (int x = 0; x < pWidth; x++){
				result.add(pMatrix[y][x]);
	        }
	    }
		
		return result;
	}
	
	
	
	public static String md5Java(String message){
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
           
            //converting byte array to Hexadecimal String
           StringBuilder sb = new StringBuilder(2*hash.length);
           for(byte b : hash){
               sb.append(String.format("%02x", b&0xff));
           }
          
           digest = sb.toString();
          
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
        	ex.printStackTrace();
        }
        return digest;
    }
	
	public static String fillDif(String pValue){
		if((pValue.equals("0"))||(pValue.equals("-0")))
			return "";
		else return pValue;
	}
	
	
	public static float allowMovement(Vector<Integer> pBoundaries, float pNewValue, float pOldValue){
		float result = pOldValue;
		if ((pNewValue > pBoundaries.get(0))||(pNewValue < pBoundaries.get(1))) {
			result = pNewValue;
		}
		return result;
	}
	
	public static Vector<Integer> getBoundaries(float pValue, Vector<Integer> pElements){
		Vector<Integer> result = new Vector<Integer>(2);
		for (int i = 0; i < pElements.size(); i++) {
			if ((pValue > pElements.get(i))&&(pValue <= pElements.get(i+1))) {
				result.add(pElements.get(i));
				result.add(pElements.get(i+1));
				break;
			}
		}
		
		return result;
	}
	
	
	public static double getSimilarityValue(String pOrigWord, String pNewWord, int pMetric){
		ILexicalDatabase db = new NictWordNet();
		RelatednessCalculator[] rcs = { new HirstStOnge(db),new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
	                                    new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db) };
		
		WS4JConfiguration.getInstance().setMFS(true);
		
		return rcs[pMetric].calcRelatednessOfWords(pOrigWord, pNewWord);
		
	}
	
	
	
	
	
	
	
	
	
}
