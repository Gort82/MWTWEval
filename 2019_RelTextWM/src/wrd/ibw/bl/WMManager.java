package wrd.ibw.bl;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.util.Vector;

import javax.imageio.ImageIO;

import wrd.ibw.da.DBConnection;

public class WMManager {
	private File imageFie = null;
	private int matrixWidth;
	private int matrixHeight;
	private int pixelMatrix[][];
	private Vector<Integer> vector;
	
	
	public WMManager(DBConnection pDBConnection){
		this.matrixWidth = 0;
		this.matrixHeight = 0;
		this.vector = new Vector<>();
		
	}
	
	/**************** ENCODING AREA ****************/
	//Method for embedding the WM in the RDB 
	public void embedWatermarking(File imgFile, String pTable) throws Exception{
		try {
			this.getMatrixFromImageFile(imgFile);
			this.getVectorFromPixelMatrix();
		} 
		catch (Exception e) {
			throw e;
		}
		
		
		
	}
	
	//Method for get the matrix from the image file
	private void getMatrixFromImageFile(File imgFile) throws Exception{
		try {
			this.imageFie = imgFile;
			BufferedImage imgBuffered = ImageIO.read(this.imageFie);
			Raster raster = imgBuffered.getData();
			this.matrixWidth = raster.getWidth();
			this.matrixHeight = raster.getHeight();
			this.pixelMatrix = new int[this.matrixWidth][this.matrixHeight];
			for (int x = 0; x < this.matrixWidth; x++){
		        for(int y = 0; y < this.matrixHeight; y++){
		        	this.pixelMatrix[x][y] = raster.getSample(x,y,0);
		            System.out.println("x:" + x + ", y:" + y + " "+ this.pixelMatrix[x][y]);
		        }
		    }
		} catch (Exception e) {
			throw e;
		}
	}
	
	//Method for convert the matrix into a vector
	private void getVectorFromPixelMatrix(){
		for (int x = 0; x < this.matrixWidth; x++){
	        for(int y = 0; y < this.matrixHeight; y++){
	        	this.vector.add(this.pixelMatrix[x][y]);
	        }
	    }
		
		for(int z = 0; z < this.vector.size(); z++){
			System.out.println(this.vector.elementAt(z)+", ");
		}
	}
	
	
	
	
	
	

	
	
	/**************** DENCODING AREA ****************/
	//Method for decoding the WM contained in the RDB
	public void decodeWatermarking(){
		
	}
}
