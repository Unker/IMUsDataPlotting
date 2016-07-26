package additional;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CompassPlot;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import additional.CombinedSerialPlot;
import additional.WorkWithSerialPort;
import jssc.SerialPort;
import jssc.SerialPortList;

public final class PanelCollection {
	/** Serial port settings fields */
	static private JComboBox<String> 	portNameField;
	static private JComboBox<String> 	portRateField;
	static JToggleButton 				btnConnect;
	static private JToggleButton 		btnStart;
	static private WorkWithSerialPort 	serialPort;



	// определяем положения элементов на фрейме
	public static int 	xPosChart = 0,
				xPosAutoRange = 2,
				xPosSettings = xPosChart + xPosAutoRange + 1;
	public static int 	yPosChart = 0,
				yPosAutoRangeX = 0,
				yPosAutoRangeY = yPosAutoRangeX + 7,
				yPosAutoRangeZ = yPosAutoRangeY + 1,
				yPosSettings = 0;
	
	private static int sentCnt = 0;
	
	public static JPanel creatSerialPortPanel(){
		
		JPanel settingsSerialPortPanel = new JPanel(new GridBagLayout());
		settingsSerialPortPanel.setFont(new Font("SansSerif", Font.BOLD, 20));////////		
		settingsSerialPortPanel.setBorder(BorderFactory.createTitledBorder("Serial port settings"));
//		add(settingsSerialPortPanel, new GridBagConstraints(xPosSettings, yPosSettings, 2, 1, 0, 0,
//		        GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 0), 0, 0));
		
		settingsSerialPortPanel.add(new JLabel("Port name:"), new GridBagConstraints(xPosSettings, yPosSettings, 1, 1, 0, 0,
	            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));			
		portNameField = new JComboBox<String>(SerialPortList.getPortNames());
		settingsSerialPortPanel.add(portNameField, new GridBagConstraints(xPosSettings+1, yPosSettings, 1, 1, 0, 0,
	            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
		yPosSettings++;

		String[] listBaudRate = {
				Integer.toString(SerialPort.BAUDRATE_110),
				Integer.toString(SerialPort.BAUDRATE_300),
				Integer.toString(SerialPort.BAUDRATE_600),
				Integer.toString(SerialPort.BAUDRATE_1200),
				Integer.toString(SerialPort.BAUDRATE_4800),
				Integer.toString(SerialPort.BAUDRATE_9600),
				Integer.toString(SerialPort.BAUDRATE_14400),
				Integer.toString(SerialPort.BAUDRATE_19200),
				Integer.toString(SerialPort.BAUDRATE_38400),
				Integer.toString(SerialPort.BAUDRATE_57600),
				Integer.toString(SerialPort.BAUDRATE_115200),
				Integer.toString(SerialPort.BAUDRATE_128000),
				Integer.toString(SerialPort.BAUDRATE_256000),
		};
		portRateField = new JComboBox<String>(listBaudRate);
		portRateField.setSelectedIndex(9);
		settingsSerialPortPanel.add(new JLabel("Baudrate:"), new GridBagConstraints(xPosSettings, yPosSettings, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		settingsSerialPortPanel.add(portRateField, new GridBagConstraints(xPosSettings+1, yPosSettings, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
		yPosSettings++;
        
		settingsSerialPortPanel.add(new JLabel("Data bits:"), new GridBagConstraints(xPosSettings, yPosSettings, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		settingsSerialPortPanel.add(new JLabel("8"), new GridBagConstraints(xPosSettings+1, yPosSettings, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
		yPosSettings++;
       
		settingsSerialPortPanel.add(new JLabel("Stop bits:"), new GridBagConstraints(xPosSettings, yPosSettings, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		settingsSerialPortPanel.add(new JLabel("1"), new GridBagConstraints(xPosSettings+1, yPosSettings, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
		yPosSettings++;
       
		settingsSerialPortPanel.add(new JLabel("Parity:"), new GridBagConstraints(xPosSettings, yPosSettings, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		settingsSerialPortPanel.add(new JLabel("none"), new GridBagConstraints(xPosSettings+1, yPosSettings, 1, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 0), 0, 0));
		yPosSettings++;
       
        btnConnect = new JToggleButton("Connect");
        btnConnect.setEnabled(false);
        btnConnect.addActionListener(event -> {
		  	if(btnConnect.isSelected()) {
		        	btnConnect.setText("Disconnect");
		        	if(serialPort == null) {
		        		serialPort = new WorkWithSerialPort( (String)PanelCollection.getPortNameField().getSelectedItem() );
		        		serialPort.openSerialPort(Integer.parseInt((String) portRateField.getSelectedItem()));
		        	}
		        	else serialPort.openSerialPort(Integer.parseInt((String) portRateField.getSelectedItem()));
		        	
		        	setEnabledSettingsSerialPort(false);
		  	}
		  	else {
		        	btnConnect.setText("Connect");
		        	if(btnStart.isSelected()){
		        		btnStart.setSelected(false);
		        		btnStart.setText("Start");
		        	}
		        	
		        	if(serialPort != null && serialPort.isOpened()) {
		        		try {
							serialPort.killClass();
						} catch (Exception e) {
							e.printStackTrace();
						}
		        	}
		        	
		        	setEnabledSettingsSerialPort(true);
		  	}
        });
        settingsSerialPortPanel.add(btnConnect, new GridBagConstraints(xPosSettings, yPosSettings, 2, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        yPosSettings++;
        
        btnStart = new JToggleButton("Start"); // btnStart.
        btnStart.setEnabled(false);
        btnStart.addActionListener(event -> {
			  	if(btnStart.isSelected()) {
			  		if(serialPort != null) {
				  		if(serialPort.isOpened()){
				  			btnStart.setText("Stop");
					  		try {
					  			serialPort.setCycleWriteEnable(true);
					  			boolean b;
								b = serialPort.writeByte(serialPort.startByte);
								b = serialPort.writeByte(serialPort.sendStatorByte);
								
							} catch (Exception e) {
								e.printStackTrace();
							}	
				  		}
				  		else {
				  			JOptionPane.showMessageDialog(null, "Serial port does't opened", "Start error", JOptionPane.ERROR_MESSAGE);
				  			btnStart.setSelected(false);
				  		}
			  		}
			  		else { 
			  			JOptionPane.showMessageDialog(null, "Please, connect to device", "Start error", JOptionPane.ERROR_MESSAGE);
			  			btnStart.setSelected(false);
			  		}	  		
			  	}
			  	else {
			  		btnStart.setText("Start");
			  		if(serialPort != null ) {
			  			if(serialPort.isOpened()) {
			  				try {
			  					serialPort.writeByte(serialPort.stopByte);
							} catch (Exception e) {
								e.printStackTrace();
							}
			  				serialPort.setCycleWriteEnable(false);
			  			}
			  		}
			  		
			  	}
        });
        settingsSerialPortPanel.add(btnStart, new GridBagConstraints(xPosSettings, yPosSettings, 2, 1, 0, 0,
            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        yPosSettings++;
        
		settingsSerialPortPanel.setMinimumSize(new Dimension(150, 250));
        
        return settingsSerialPortPanel;
	}
	
	static void setEnabledSettingsSerialPort(boolean b) {
    	portNameField.setEnabled(b);
    	portRateField.setEnabled(b);
	}
	
	public static void setEnabledBtns(boolean b) {
		
		if(b == false && btnConnect.isSelected()) {
			btnConnect.doClick();
		}
		btnStart.setEnabled(b);
		btnConnect.setEnabled(b);
		
	}
	
	public static JComboBox<String> getPortNameField() {
		return portNameField;
	}

	public static WorkWithSerialPort getSerialPort() {
		return serialPort;
	}
	static void setSerialPort(WorkWithSerialPort sp) {
		serialPort = sp;
	}
	

	
	private static CombinedSerialPlot combinedSerialPlot;
	
	public static JTabbedPane creatTabbedPane(int maxAge) {
		combinedSerialPlot = new CombinedSerialPlot(maxAge);
		
		ChartPanel chartPanelAcc = combinedSerialPlot.getChartPanelAcc();	
		JPanel mainPanel = creatPanelWithChart(chartPanelAcc);
		
              
        ChartPanel chartPanelGyro = combinedSerialPlot.getChartPanelGyro();
        JPanel gyroPanel = creatPanelWithChart(chartPanelGyro);
        
        ChartPanel chartPanelMag = combinedSerialPlot.getChartPanelMag();
        JPanel magPanel = creatPanelWithChart(chartPanelMag);
        
        //List<MeterPlot> listMeterPlot = combinedSerialPlot.getListMeterPlot();
        Map<String, MeterPlot> listMeterPlot = combinedSerialPlot.getListMeterPlot();
        JPanel anglesPanel = creatPanelWithMeterPlot(listMeterPlot);

        
        // табсики
        final JTabbedPane tabbedPane = new JTabbedPane(); 
        tabbedPane.setMinimumSize(new Dimension(800, 600));
		tabbedPane.addTab("Акселерометры", mainPanel);
		tabbedPane.addTab("Гироскопы", gyroPanel);
		tabbedPane.addTab("Магнитометры", magPanel);
		tabbedPane.addTab("Углы Эйлера", anglesPanel);

		return tabbedPane;
	}
	
	
	public static Map<String, TimeSeries> getTimeSeries(ChartPanel chartPanel) {
		if(chartPanel != null) {
			CombinedDomainXYPlot cmb = ((CombinedDomainXYPlot) chartPanel.getChart().getXYPlot());
	        List<XYPlot> listSubPlots = castList(XYPlot.class, cmb.getSubplots());
	        
	        Map<String, TimeSeries> listSeries = new HashMap<String, TimeSeries>();
	        
	        // собираем массив графиков
	        if(listSubPlots.size() > 0) {
	        	// для каждого участка графиков определяем количество наборов
	    		for(int indexSubPlot = 0; indexSubPlot < listSubPlots.size(); indexSubPlot++ ) {
	    			int cntDataSet = listSubPlots.get(indexSubPlot).getDatasetCount();
	    			if(cntDataSet > 0) {
	    				// для каждого набора определяем количество графиков
	    				for(int indexDataSet = 0; indexDataSet < cntDataSet; indexDataSet++ ) {
	    					TimeSeriesCollection ds = (TimeSeriesCollection) listSubPlots.get(indexSubPlot).getDataset(indexDataSet);
	    					int countSerier = ds.getSeriesCount();
	    					for(int indexSeries = 0; indexSeries < countSerier; indexSeries++ ) {
		    					TimeSeries ts = ds.getSeries(indexSeries);
		    					listSeries.put((String) ts.getKey(), ts);
		    				}
	    				}				
	    			}
	            }
	    	}
	        listSubPlots.get(listSubPlots.size()-1).getDomainAxis().setTickLabelsVisible(true); //////////////////////////////////
	        return listSeries;
		}
		else return null;
	}
	
	static JPanel creatPanelWithChart(ChartPanel chartPanel) {

		// создаю основную панель с графиками и чекбоксами
        JPanel panel = new JPanel(new GridBagLayout());
     // панель с графиками
        panel.add(chartPanel, new GridBagConstraints(PanelCollection.xPosChart, PanelCollection.yPosChart, 1, 3, 0, 0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
		
        // получаю список графиков
        CombinedDomainXYPlot cmb = ((CombinedDomainXYPlot) chartPanel.getChart().getXYPlot());
        List<XYPlot> listSubPlots = castList(XYPlot.class, cmb.getSubplots());
        
        
        // создаю количество чекбоксов по количесту subplot'ов
    	if(listSubPlots.size() > 0) {
    		Boolean[] bAutoRange = new Boolean[listSubPlots.size()];
    		for(int i = 0; i < listSubPlots.size(); i++ ) {
    			bAutoRange[i] = true; // значение поумолчанию
    		}
    		
    		for(int indxCheckBox = 0; indxCheckBox < listSubPlots.size(); indxCheckBox++ ) {
            	JCheckBox checkAutoRange = new JCheckBox();
            	checkAutoRange.setSelected(true);
            	final int innerIndex = indxCheckBox;
            	
    	        checkAutoRange.addActionListener(event -> { 
    	        		TimeSeriesCollection ds = (TimeSeriesCollection) listSubPlots.get(innerIndex).getDataset(0);
    	        		TimeSeries ts = ds.getSeries(0);
    	        		String typePlot = (String) ts.getKey();
    	        		if(typePlot.indexOf("Acc") != -1) {
    	        			double range = CombinedSerialPlot.FULL_SCALE_ACC * 1.1; // + 10%
    	        			listSubPlots.get(innerIndex).getRangeAxis().setRange(-range, range);
    	        		}
    	        		if(typePlot.indexOf("Gyro") != -1) {
    	        			double range = CombinedSerialPlot.FULL_SCALE_GYRO * 1.1; // + 10%
    	        			listSubPlots.get(innerIndex).getRangeAxis().setRange(-range, range);
    	        		}
    	        		if(typePlot.indexOf("Mag") != -1) {
    	        			double range = CombinedSerialPlot.FULL_SCALE_MAG * 1.1; // + 10%
    	        			listSubPlots.get(innerIndex).getRangeAxis().setRange(-range, range);
    	        		}
    	        				
    	          		
    	          		listSubPlots.get(innerIndex).getRangeAxis().setAutoRange(!bAutoRange[innerIndex]);
    	          		bAutoRange[innerIndex] = listSubPlots.get(innerIndex).getRangeAxis().isAutoRange();
    	          		checkAutoRange.setSelected(bAutoRange[innerIndex]); 
    	  		});
    	        panel.add(checkAutoRange, new GridBagConstraints(PanelCollection.xPosAutoRange, indxCheckBox, 1, 1, 0, 1, GridBagConstraints.CENTER,
    	  				GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
            }
    	}

		
        return panel;
	}
	
	public static <T> List<T> castList(Class<? extends T> clazz, Collection<?> c) {
	    List<T> r = new ArrayList<T>(c.size());
	    for(Object o: c)
	      r.add(clazz.cast(o));
	    return r;
	}
	
	static JPanel creatPanelWithMeterPlot(Map<String, MeterPlot> listMeterPlots) {
		// создаю основную панель с индикаторами
        JPanel panel = new JPanel(new GridBagLayout());	
        
        // панель с индикаторами
        for(int i = 0; i < listMeterPlots.size(); i++) {
        	JFreeChart chart = new JFreeChart("",
    				new Font("SansSerif", Font.BOLD, 14), listMeterPlots.get(getCombinedSerialPlot().namesMeterPlot[i]), false);        
            ChartPanel chartPanel = new ChartPanel(chart);
        	panel.add(chartPanel, new GridBagConstraints(i%3, (int)i/3, 1, 1, 10, 10, GridBagConstraints.NORTH,
    				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        for(int i = listMeterPlots.size(); i < listMeterPlots.size() + 3; i++) {
        	//int index = getCombinedSerialPlot().namesMeterPlot[i];
        	List<MeterInterval> list = listMeterPlots.get(getCombinedSerialPlot().namesMeterPlot[i%3]).getIntervals();
    		MeterInterval mi =  list.get(0);
        	JLabel label = new JLabel(mi.getLabel());
        	label.setHorizontalAlignment(JLabel.CENTER);
        	label.setFont(new Font("SansSerif", Font.BOLD, 14));
        	panel.add(label, new GridBagConstraints(i%3, (int)i/3, 1, 1, 1, 1, GridBagConstraints.CENTER,
    				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        panel.setPreferredSize(new Dimension(80, 60));
        
		return panel;
	}
	
	public static CombinedSerialPlot getCombinedSerialPlot() {
		return combinedSerialPlot;
	}
	
	
	/** Work with data files */
	private static DataInFiles dataInFiles;
	
	public static JPanel creatButtons(){
		JPanel buttonsPanel = new JPanel(new GridBagLayout());
		
		 JButton btnProbe = new JButton("Probe");
        btnProbe.addActionListener(event -> {
        	try {
//					serialPort.getClassSerialPort().writeByte(sentCnt++);
			} catch (Exception e) { 
				e.printStackTrace();
			}
		});
        buttonsPanel.add(btnProbe, new GridBagConstraints(PanelCollection.xPosSettings, PanelCollection.yPosSettings, 2, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        PanelCollection.yPosSettings++;
        
        JButton btnExit = new JButton("Exit");
		btnExit.addActionListener(event -> {
			if(serialPort != null) serialPort.killClass();
			if(dataInFiles != null) dataInFiles.closeFile();
			System.exit(0);
		});
		buttonsPanel.add(btnExit, new GridBagConstraints(PanelCollection.xPosSettings+1, PanelCollection.yPosSettings, 1, 1, 1, 1, 
				GridBagConstraints.SOUTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        PanelCollection.yPosSettings++;
        
        return buttonsPanel;
	}
	
	public static void setDataInFiles(DataInFiles dataInFiles) {
		PanelCollection.dataInFiles = dataInFiles;
	}
	
}
