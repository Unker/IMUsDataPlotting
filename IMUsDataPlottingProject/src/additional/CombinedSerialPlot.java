package additional;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLabelLocation;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.needle.ArrowNeedle;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.CompassPlot;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.util.DefaultShadowGenerator;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

public class CombinedSerialPlot extends JPanel{

	private static final long serialVersionUID = -8165545806105592709L;
	
	public static final int FULL_SCALE_ACC = 8;
	public static final int FULL_SCALE_GYRO = 250;
	public static final int FULL_SCALE_MAG = 4800;
	
	private TimeSeries 			 rotorAccX, rotorAccY, rotorAccZ;
	private TimeSeries 			 rotorGyroX, rotorGyroY, rotorGyroZ;
	private TimeSeries 			 rotorMagX, rotorMagY, rotorMagZ;
	
	private TimeSeries 			 statorAccX, statorAccY, statorAccZ;
	private TimeSeries 			 statorGyroX, statorGyroY, statorGyroZ;	
	private TimeSeries 			 statorMagX, statorMagY, statorMagZ;
	
	private XYPlot				 subPlotAccX, subPlotAccY, subPlotAccZ;
	private XYPlot				 subPlotGyroX, subPlotGyroY, subPlotGyroZ;
	private XYPlot				 subPlotMagX, subPlotMagY, subPlotMagZ;
	
	private MeterPlot			 rotorPhi, rotorPsi, rotorTheta;
	private MeterPlot			 statorPhi, statorPsi, statorTheta;
	private MeterPlot			 calcPhi, calcPsi, calcTheta;
	//private List<MeterPlot> 	 listMeterPlot;
	private Map<String, MeterPlot> listMeterPlot;
	public String[] namesMeterPlot = {"rotorPhi",
										"rotorPsi",
										"rotorTheta",
										"statorPhi",
										"statorPsi",
										"statorTheta",
										"calcPhi",
										"calcPsi",
										"calcTheta",
										};
	
	private CombinedDomainXYPlot combPlot, combPlotGyro;
	private ChartPanel 			 chartPanelAcc, chartPanelGyro, chartPanelMag;

	private Color[] colors = 	 {	Color.RED,
									Color.BLUE,
									Color.BLACK,
									Color.CYAN,
									Color.PINK,
									Color.GRAY,
									Color.GREEN,
									Color.LIGHT_GRAY,
									Color.MAGENTA,
									Color.YELLOW};
	
	public CombinedSerialPlot(int maxAge) {
		// назначаем графики для отображения показаний аксклерометров подвижной части
		rotorAccX = creatTimeSeries("X, g", maxAge); 
		rotorAccX.setKey("rotorAccX");
		rotorAccY = creatTimeSeries("Y, g", maxAge); 
		rotorAccY.setKey("rotorAccY");
		rotorAccZ = creatTimeSeries("Z, g", maxAge);
		rotorAccZ.setKey("rotorAccZ");
		
		// ... и неподвижной
		statorAccX = creatTimeSeries("X, g", maxAge); 
		statorAccX.setKey("statorAccX");
		statorAccY = creatTimeSeries("Y, g", maxAge); 
		statorAccY.setKey("statorAccY");
		statorAccZ = creatTimeSeries("Z, g", maxAge);
		statorAccZ.setKey("statorAccZ");
		
		subPlotAccX = creatSubPlot(rotorAccX, statorAccX);		
		subPlotAccY = creatSubPlot(rotorAccY, statorAccY);
		subPlotAccZ = creatSubPlot(rotorAccZ, statorAccZ);	
		combPlot = creatCombinedPlot(subPlotAccX, subPlotAccY, subPlotAccZ);  
		//combPlot.getRenderer().setSeriesPaint(series, paint);
		
		chartPanelAcc = creatChartPanel("Accelerometer data", combPlot); 

		
		// назначаем графики для отображения показаний гироскопа подвижной части
		rotorGyroX = creatTimeSeries("rotX, град/c", maxAge);
		rotorGyroX.setKey("rotorGyroX");
		rotorGyroY = creatTimeSeries("rotY, град/c", maxAge);
		rotorGyroY.setKey("rotorGyroY");
		rotorGyroZ = creatTimeSeries("rotZ, град/c", maxAge);
		rotorGyroZ.setKey("rotorGyroZ");
		
		// ... и неподвижной
		statorGyroX = creatTimeSeries("rotX, град/c", maxAge);
		statorGyroX.setKey("statorGyroX");
		statorGyroY = creatTimeSeries("rotY, град/c", maxAge);
		statorGyroY.setKey("statorGyroY");
		statorGyroZ = creatTimeSeries("rotZ, град/c", maxAge);
		statorGyroZ.setKey("statorGyroZ");
		
		subPlotGyroX = creatSubPlot(rotorGyroX, statorGyroX);
		subPlotGyroY = creatSubPlot(rotorGyroY, statorGyroY);		
		subPlotGyroZ = creatSubPlot(rotorGyroZ, statorGyroZ);
		combPlotGyro = creatCombinedPlot(subPlotGyroX, subPlotGyroY, subPlotGyroZ);
		
		chartPanelGyro = creatChartPanel("Gyroscope data", combPlotGyro);
		
		
		// назначаем графики для отображения показаний магнитометров подвижной части
		rotorMagX = creatTimeSeries("X, uT", maxAge); 
		rotorMagX.setKey("rotorMagX");
		rotorMagY = creatTimeSeries("Y, uT", maxAge); 
		rotorMagY.setKey("rotorMagY");
		rotorMagZ = creatTimeSeries("Z, uT", maxAge);
		rotorMagZ.setKey("rotorMagZ");
		
		// ... и неподвижной
		statorMagX = creatTimeSeries("X, uT", maxAge); 
		statorMagX.setKey("statorMagX");
		statorMagY = creatTimeSeries("Y, uT", maxAge); 
		statorMagY.setKey("statorMagY");
		statorMagZ = creatTimeSeries("Z, uT", maxAge);
		statorMagZ.setKey("statorMagZ");
		
		subPlotMagX = creatSubPlot(rotorMagX, statorMagX);		
		subPlotMagY = creatSubPlot(rotorMagY, statorMagY);
		subPlotMagZ = creatSubPlot(rotorMagZ, statorMagZ);	
		combPlot = creatCombinedPlot(subPlotMagX, subPlotMagY, subPlotMagZ);  
		
		chartPanelMag = creatChartPanel("Magnetometr data", combPlot);
		
		
		// назначаем графики для отображения вычисленных углов Эйлера
		//listMeterPlot = new ArrayList<MeterPlot>();
		listMeterPlot = new HashMap<String, MeterPlot>();
		String phi = "φ, град"; 
		String psi = "ψ, град";
		String theta = "θ, град";
		rotorPhi = creatMeterPlot(phi, "rotorPhi"); 
		rotorPsi = creatMeterPlot(psi, "rotorPsi"); 
		rotorTheta = creatMeterPlot(theta, "rotorTheta");
		statorPhi = creatMeterPlot(phi, "statorPhi"); 
		statorPsi = creatMeterPlot(psi, "statorPsi");
		statorTheta = creatMeterPlot(theta, "statorTheta"); 
		calcPhi = creatMeterPlot(phi, "calcPhi"); 
		calcPsi = creatMeterPlot(psi, "calcPsi"); 
		calcTheta = creatMeterPlot(theta, "calcTheta"); 
		
//		listMeterPlot.add(rotorPhi);
//		listMeterPlot.add(rotorPsi);
//		listMeterPlot.add(rotorTheta);
//		listMeterPlot.add(statorPhi);
//		listMeterPlot.add(statorPsi);
//		listMeterPlot.add(statorTheta);
//		listMeterPlot.add(calcPhi);
//		listMeterPlot.add(calcPsi);
//		listMeterPlot.add(calcTheta); 
		
		listMeterPlot.put("rotorPhi", rotorPhi);
		listMeterPlot.put("rotorPsi", rotorPsi);
		listMeterPlot.put("rotorTheta", rotorTheta);
		listMeterPlot.put("statorPhi", statorPhi);
		listMeterPlot.put("statorPsi", statorPsi);
		listMeterPlot.put("statorTheta", statorTheta);
		listMeterPlot.put("calcPhi", calcPhi);
		listMeterPlot.put("calcPsi", calcPsi);
		listMeterPlot.put("calcTheta", calcTheta); 
		
		 
	}
	
	private TimeSeries creatTimeSeries(String nameAxis, int maximumItemAge) {
		TimeSeries series = new TimeSeries(nameAxis);
		series.setMaximumItemAge(maximumItemAge); 
		series.setRangeDescription(nameAxis);
		return series;
	}
	
	private XYPlot creatSubPlot(TimeSeries... serieses) {
		TimeSeriesCollection dataSet = new TimeSeriesCollection();
		// add the serieses... 
		for(TimeSeries series:serieses) {
			dataSet.addSeries(series); 
		}

		NumberAxis range = new NumberAxis(serieses[0].getRangeDescription());
		range.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		range.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
		range.setStandardTickUnits(NumberAxis.createStandardTickUnits());
		range.setAutoRange(true);
			
		XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
		//renderer.setSeriesPaint(0, color);
		for(int i = 0; i <= dataSet.getSeriesCount(); i++) {
			renderer.setSeriesPaint(i, colors[i%10] );
		}
		renderer.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL)); 
		
		XYPlot subPlot = new XYPlot(dataSet, null, range, renderer); 
		//subPlot.setBackgroundPaint(Color.LIGHT_GRAY); // 0xC0C0C0
//		subPlot.setBackgroundPaint(new Color(0.85f, 0.85f, 0.85f));
		subPlot.setDomainGridlinePaint(Color.LIGHT_GRAY);
		subPlot.setRangeGridlinePaint(Color.LIGHT_GRAY);
		subPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
		
		return subPlot;
	}
	
	private CombinedDomainXYPlot creatCombinedPlot(XYPlot...subPlots) {
		NumberAxis domain = new NumberAxis("Time, ms");
		domain.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 12));
		domain.setLabelFont(new Font("SansSerif", Font.PLAIN, 14));
		domain.setAutoRange(true);
		domain.setLowerMargin(0.0);
		domain.setUpperMargin(0.0);
		domain.setTickLabelsVisible(false);
		domain.setAutoRangeIncludesZero(false);
		
		CombinedDomainXYPlot plot = new CombinedDomainXYPlot(domain);   //CombinedRangeXYPlot
        plot.setGap(2.0); 
        plot.setOrientation(PlotOrientation.VERTICAL);   
        plot.setBackgroundPaint(Color.LIGHT_GRAY);
		plot.setDomainGridlinePaint(Color.WHITE);
		plot.setRangeGridlinePaint(Color.WHITE); 
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		
        // add the subplots... 
		for(XYPlot subPlot:subPlots) {
			plot.add(subPlot, 1); 
		}

		plot.setShadowGenerator(new DefaultShadowGenerator());
        return plot;
	}
	
	private ChartPanel creatChartPanel(String nameChart, CombinedDomainXYPlot combPlot) {
		JFreeChart chart = new JFreeChart(nameChart,
				new Font("SansSerif", Font.BOLD, 14), combPlot, true);
		chart.setBackgroundPaint(Color.WHITE); 
		
		ChartPanel chartPanel = new ChartPanel(chart);
//		JPanel chartPanel = CombinedXYPlot.createPanel(); 
		
		chartPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 4, 4, 4),
				BorderFactory.createLineBorder(Color.black)));
		chartPanel.setPreferredSize(new Dimension(800, 600));
		chartPanel.setMinimumSize(new Dimension(800, 600));
		
		return chartPanel;
	}
	
	private MeterPlot creatMeterPlot(String nameAxis, String key) {
		// needle - стрелка
		MeterPlot plot = new MeterPlot(new DefaultValueDataset(60.0));
		plot.setMeterAngle(360);	// используем весь круг
		plot.setDialBackgroundPaint(Color.DARK_GRAY);
		plot.setDrawBorder(false);
		plot.setNeedlePaint(Color.RED); 
		plot.setTickSize(45);
		plot.setTickLabelsVisible(false);
		plot.setRange(new Range(0.0, 360.0));
		plot.setUnits("");
		plot.setValueFont(new Font("SansSerif", Font.BOLD, 24));
        plot.addInterval(new MeterInterval(nameAxis, new Range(0.0, 360.0)));
        plot.setNoDataMessage(key);
		return plot;
	}
	
	
	public ChartPanel getChartPanelAcc() {
		return this.chartPanelAcc;
	}
	public ChartPanel getChartPanelGyro() {
		return this.chartPanelGyro;
	}
	public ChartPanel getChartPanelMag() {
		return this.chartPanelMag;
	}
	//public List<MeterPlot> getListMeterPlot() {
	public Map<String, MeterPlot> getListMeterPlot() {
		return this.listMeterPlot;
	}

	

//	private boolean toggleAutoRange(XYPlot subPlot, boolean auto) {
//		double lower = -130,
//				upper = 130;
//		subPlot.getRangeAxis().setRange(lower, upper);
//		subPlot.getRangeAxis().setAutoRange( auto );
//		return subPlot.getRangeAxis().isAutoRange();
//	}

}
