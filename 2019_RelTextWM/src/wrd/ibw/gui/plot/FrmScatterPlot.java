package wrd.ibw.gui.plot;

import java.awt.Color;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class FrmScatterPlot extends ApplicationFrame {
	 private static final long serialVersionUID = 1L;
	 
	 XYDataset inputData;
	 JFreeChart chart;
	 
	 public FrmScatterPlot(Integer[] pX, Integer[] pY) throws IOException {
		 super("Technobium - Linear Regression");
		 
		 inputData = createDataset(pX,pY);
		 
		 chart = createScatterChart(inputData);
		 
		 ChartPanel chartPanel = new ChartPanel(chart);
		 chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		 setContentPane(chartPanel);
	}
	 
	 public XYDataset createDataset(Integer[] pX, Integer[] pY){
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("Scatter Item");
		for (int i = 0; i < pY.length; i++) {
			series.add(pX[i], pY[i]);
		}
		dataset.addSeries(series);
		return dataset;
	}
	 
	 
	 private JFreeChart createScatterChart(XYDataset inputData){
		 JFreeChart chart = ChartFactory.createScatterPlot("Scatter Chart", "Extracted Marks", "Embedded Marks", inputData, PlotOrientation.VERTICAL, true, true, false);
		 XYPlot plot = chart.getXYPlot();
		 plot.getRenderer().setSeriesPaint(0, Color.blue);
		 return chart;
	}	
	 
	 
	 
}
