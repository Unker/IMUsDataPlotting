
package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.general.DefaultValueDataset;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.util.Log;

import additional.CombinedSerialPlot;
import additional.DataInFiles;
import additional.PanelCollection;
import additional.WorkWithSerialPort;
import jssc.*;		// jSSC (Java Simple Serial Connector) - serial port communication library

/**
 * Данная программа предназначения для приема данных от 
 * гироскопов, акселерометров и магентометров MPU9250 через запросы к МК.
 * Полученные данные отображаются на графиках, проводится фильтрация
 * показаний датчиков и определения положения датчиков в пространстве
 * с последующим выводом на стрелочные индикаторы(отображение углов Эйлера)
 * 
 * @author Unker
 */
public class IMUsDataPlotting extends JPanel {

	private static final long serialVersionUID = 7253877951296984229L;


	/** Time series for plot data accelerometer */
	private TimeSeries 		 rotorAccXSeries, rotorAccYSeries, rotorAccZSeries;	
	private TimeSeries 		 statorAccXSeries, statorAccYSeries, statorAccZSeries;
	
	
	/** Time series for plot data gyroscope */
	private TimeSeries 		 rotorGyroXSeries, rotorGyroYSeries, rotorGyroZSeries;
	private TimeSeries 		 statorGyroXSeries, statorGyroYSeries, statorGyroZSeries;
	
	/** Time series for plot data magnetometr */
	private TimeSeries 		 rotorMagXSeries, rotorMagYSeries, rotorMagZSeries;
	private TimeSeries 		 statorMagXSeries, statorMagYSeries, statorMagZSeries;
	
	/** Data accelerometer */
	private double 			 rotorAccXData, rotorAccYData, rotorAccZData;	
	private double 			 statorAccXData, statorAccYData, statorAccZData;
	
	
	/** Data gyroscope */
	private double 			 rotorGyroXData, rotorGyroYData, rotorGyroZData;
	private double 			 statorGyroXData, statorGyroYData, statorGyroZData;
	
	/** Data magnetometr */
	private double 			 rotorMagXData, rotorMagYData, rotorMagZData;
	private double 			 statorMagXData, statorMagYData, statorMagZData;
	
	/** Data Euler angles */
	private MeterPlot		rotorPhi, rotorPsi, rotorTheta;
	private MeterPlot		statorPhi, statorPsi, statorTheta;
	private MeterPlot		calcPhi, calcPsi, calcTheta;
	
	private DefaultValueDataset rotorPhiData, rotorPsiData, rotorThetaData;
	
	private long	 		 rotorTime, statorTime;
	static long 			 oldTime;
	
	
	static private JLabel 		infoLbl;
	JTabbedPane 				tabbedPane;
	
	
	/** Work with file */
	private static DataInFiles dataInFiles;
	

	
	/**
	* Creates a new application.
	*
	* @param maxAge the maximum age (in milliseconds).
	*/
	public IMUsDataPlotting(int maxAge) {
		
		super(new GridBagLayout());
				
		
		JPanel serialPortPanel = PanelCollection.creatSerialPortPanel(); 
		add(serialPortPanel, new GridBagConstraints(PanelCollection.xPosSettings, PanelCollection.yPosSettings, 2, 1, 0, 0,
		        GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 0), 0, 0));
       
		PanelCollection.setDataInFiles(dataInFiles);
		JPanel buttonsPanel = PanelCollection.creatButtons();
		add(buttonsPanel, new GridBagConstraints(PanelCollection.xPosSettings, PanelCollection.yPosSettings-1, 2, 1, 0, 0,
		        GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(5, 5, 5, 0), 0, 0));
  
        infoLbl = new JLabel("*************");
        infoLbl.setVerticalAlignment(JLabel.CENTER);
        infoLbl.setHorizontalAlignment(JLabel.RIGHT);
        add(infoLbl, new GridBagConstraints(PanelCollection.xPosChart, PanelCollection.yPosSettings, PanelCollection.xPosSettings+2, 1, 0, 0, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));
        
       
        tabbedPane = PanelCollection.creatTabbedPane(maxAge);
		add(tabbedPane, new GridBagConstraints(PanelCollection.xPosChart, PanelCollection.yPosChart, 2, PanelCollection.yPosSettings, 1, 1, GridBagConstraints.NORTHWEST,
				GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));
        
        ChartPanel cp = PanelCollection.getCombinedSerialPlot().getChartPanelAcc();        
        Map<String, TimeSeries> listSeries = PanelCollection.getTimeSeries(cp);
        this.rotorAccXSeries = listSeries.get("rotorAccX");
        this.statorAccXSeries = listSeries.get("statorAccX");
        this.rotorAccYSeries = listSeries.get("rotorAccY");
        this.statorAccYSeries = listSeries.get("statorAccY");
        this.rotorAccZSeries = listSeries.get("rotorAccZ");
        this.statorAccZSeries = listSeries.get("statorAccZ");
        
        cp = PanelCollection.getCombinedSerialPlot().getChartPanelGyro();
        listSeries = PanelCollection.getTimeSeries(cp);
        this.rotorGyroXSeries = listSeries.get("rotorGyroX");
        this.statorGyroXSeries = listSeries.get("statorGyroX");
        this.rotorGyroYSeries = listSeries.get("rotorGyroY");
        this.statorGyroYSeries = listSeries.get("statorGyroY");
        this.rotorGyroZSeries = listSeries.get("rotorGyroZ");
        this.statorGyroZSeries = listSeries.get("statorGyroZ");
        
        cp = PanelCollection.getCombinedSerialPlot().getChartPanelMag();
        listSeries = PanelCollection.getTimeSeries(cp);
        this.rotorMagXSeries = listSeries.get("rotorMagX");
        this.statorMagXSeries = listSeries.get("statorMagX");
        this.rotorMagYSeries = listSeries.get("rotorMagY");
        this.statorMagYSeries = listSeries.get("statorMagY");
        this.rotorMagZSeries = listSeries.get("rotorMagZ");
        this.statorMagZSeries = listSeries.get("statorMagZ");
        
        Map<String, MeterPlot> listMeterPlot = PanelCollection.getCombinedSerialPlot().getListMeterPlot();
        this.rotorPhi = listMeterPlot.get("rotorPhi");	
        rotorPhiData = new DefaultValueDataset(0.0);
        rotorPhi.setDataset(rotorPhiData);
        this.rotorPsi = listMeterPlot.get("rotorPsi");	
        rotorPsiData = new DefaultValueDataset(0.0);
        rotorPsi.setDataset(rotorPsiData);
        this.rotorTheta = listMeterPlot.get("rotorTheta");	
        rotorThetaData = new DefaultValueDataset(0.0);
        rotorTheta.setDataset(rotorThetaData);
        
		
	}
	
	
	/**
	* Adds data to the time series.
	*
	* @param TimeSeries.
	* @param time in ms.
	* @param data.
	*/
	private void addXYZ(TimeSeries ts, long time, double in) {
		ts.addOrUpdate(new FixedMillisecond((long) time), in);
	}
	
	/**
	* Set notify for time serieses
	*
	* @param b boolean
	*/
	private void setNotify(boolean b) {
//		  this.rotorAccXSeries.setNotify(b);
//        this.statorAccXSeries.setNotify(b);
//        this.rotorAccYSerise.setNotify(b);
//        this.statorAccYSeries.setNotify(b);
//        this.rotorAccZSeries.setNotify(b);
//        this.statorAccZSeries.setNotify(b);
//        
//        this.rotorGyroXSeries.setNotify(b);
//        this.statorGyroXSeries.setNotify(b);
//        this.rotorGyroYSeries.setNotify(b);
//        this.statorGyroYSeries.setNotify(b);
//        this.rotorGyroZSeries.setNotify(b);
//        this.statorGyroZSeries.setNotify(b);
//        
//        this.rotorMagXSeries.setNotify(b);
//        this.statorMagXSeries.setNotify(b);
//        this.rotorMagYSeries.setNotify(b);
//        this.statorMagYSeries.setNotify(b);
//        this.rotorMagZSeries.setNotify(b);
//        this.statorMagZSeries.setNotify(b);
	}
	
	/**
	* Class getter data from the serial port
	*/
	class DataGetter extends Timer implements ActionListener {
		
		private static final long serialVersionUID = 563400142066538505L;
		private Queue<Byte> queueData;
		private long timeData;

		/**
		* Constructor.
		*
		* @param interval the interval repeats (in milliseconds)
		*/
		DataGetter(int interval) {			
			super(interval, null);
			timeData = 0;
			addActionListener(this);
		}
		
		/**
		* Adds a new data reading to the dataset.
		*
		* @param event the action event.
		*/
		public void actionPerformed(ActionEvent event) {
	
			if(PanelCollection.getSerialPort() != null) {
				if(queueData == null) queueData = PanelCollection.getSerialPort().getQueueReadData();
					
				if ( queueData.size() == WorkWithSerialPort.LENGTH_DATA_INPUT ) { // пакет полный?	
					while (! queueData.isEmpty() ) {
						rotorTime = getTimeData(queueData);
						rotorAccXData = getAccData(queueData);
						rotorAccYData = getAccData(queueData);
						rotorAccZData = getAccData(queueData);
						
						rotorGyroXData = getGyroData(queueData);
						rotorGyroYData = getGyroData(queueData);
						rotorGyroZData = getGyroData(queueData);
						
						rotorMagYData = getMagData(queueData);
						rotorMagXData = getMagData(queueData);
						rotorMagZData = -getMagData(queueData);
						
						addXYZ(rotorAccXSeries, rotorTime,rotorAccXData);
						addXYZ(rotorAccYSeries, rotorTime,rotorAccYData);
						addXYZ(rotorAccZSeries, rotorTime,rotorAccZData);
						
						addXYZ(rotorGyroXSeries, rotorTime,rotorGyroXData);
						addXYZ(rotorGyroYSeries, rotorTime,rotorGyroYData);
						addXYZ(rotorGyroZSeries, rotorTime,rotorGyroZData);
						
						addXYZ(rotorMagXSeries, rotorTime,rotorMagXData);
						addXYZ(rotorMagYSeries, rotorTime,rotorMagYData);
						addXYZ(rotorMagZSeries, rotorTime,rotorMagZData);
						
						statorTime = getTimeData(queueData);
						statorAccXData = getAccData(queueData);
						statorAccYData = getAccData(queueData);
						statorAccZData = getAccData(queueData);
						
						statorGyroXData = getGyroData(queueData);
						statorGyroYData = getGyroData(queueData);
						statorGyroZData = getGyroData(queueData);
						
						statorMagYData = getMagData(queueData);
						statorMagXData = getMagData(queueData);
						statorMagZData = -getMagData(queueData);
						
						addXYZ(statorAccXSeries, statorTime, statorAccXData);
						addXYZ(statorAccYSeries, statorTime, statorAccYData);
						addXYZ(statorAccZSeries, statorTime, statorAccZData);
						
						addXYZ(statorGyroXSeries, statorTime, statorGyroXData);
						addXYZ(statorGyroYSeries, statorTime, statorGyroYData);
						addXYZ(statorGyroZSeries, statorTime, statorGyroZData);
						
						addXYZ(statorMagXSeries, statorTime, statorMagXData);
						addXYZ(statorMagYSeries, statorTime, statorMagYData);
						addXYZ(statorMagZSeries, statorTime, statorMagZData);
						
						// перенести на таймер? вычислять углы на каждом шаге, а отрисовывать по таймеру
						rotorPhiData.setValue( ( Math.abs(statorAccXData*360)) );
						rotorPsiData.setValue( ( Math.abs(statorAccYData*360)) );
						rotorThetaData.setValue( ( Math.abs(statorAccZData*360)) );
							
					} // повторный запрос на получение новых данных отправляется автоматически
					
				} else if(queueData.size() > WorkWithSerialPort.LENGTH_DATA_INPUT) { // слишком много данных, возможно мусор
					// очищаем приемный буфер
					queueData.clear();
				}
				// иначе ждем заполнения буфера полным пакетом
			}
			
			
			if( infoLbl != null ) infoLbl.setText("Probe=" + (rotorTime-oldTime) );
			oldTime = rotorTime;
				
		}
		private long getTimeData(Queue<Byte> queue){
			timeData += queueData.remove()*0xFF + queueData.remove(); // cтаршая и младшая части;
			return timeData;
		}
		private double getAccData(Queue<Byte> queue){
			int dataAcc = queueData.remove()*0xFF + queueData.remove();
			return ((double)dataAcc*CombinedSerialPlot.FULL_SCALE_ACC)/Math.pow(2, 15); // cтаршая и младшая части
		}
		private double getGyroData(Queue<Byte> queue){
			int dataAcc = queueData.remove()*0xFF + queueData.remove();
			return ((double)dataAcc*CombinedSerialPlot.FULL_SCALE_GYRO)/Math.pow(2, 15); // cтаршая и младшая части
		}
		private double getMagData(Queue<Byte> queue){
			int dataAcc = queueData.remove()*0xFF + queueData.remove();
			return ((double)dataAcc*CombinedSerialPlot.FULL_SCALE_MAG)/Math.pow(2, 15); // cтаршая и младшая части
		}
	}
	
	/**
	* Serial plot refresh
	*/
	class SerialRefreshTimer implements ActionListener {
		/**
		* Action performed
		*
		* @param event the action event.
		*/
		public void actionPerformed(ActionEvent event)
		{
			//разрешаем обновить данные на графике
			setNotify(true);
			setNotify(false);
		}
	}
	
	/**
	* Serial port scanner
	*/
	// сканируем состояние портов
	class SerialPortScannerTimer implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			// обновляем список СОМ портов, найденных в системе
			PanelCollection.getPortNameField().removeAllItems();
			String[] strPortNames = SerialPortList.getPortNames();
			if(strPortNames.length == 0) {
				
				PanelCollection.setEnabledBtns(false);
			} else {
				for(String name:strPortNames){
					PanelCollection.getPortNameField().addItem(name);
				}
				PanelCollection.setEnabledBtns(true);
			}			
		}
	}

	
	/**
	* Entry point for the sample application.
	*
	* @param args ignored.
	*/
	public static void main(String[] args) {
		setNimbusLaF();
		JFrame frame = new JFrame("Plot data Acc Gyro");
		IMUsDataPlotting panel = new IMUsDataPlotting(5000);
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
		GraphicsDevice myDevice = ge.getDefaultScreenDevice();; 
		DisplayMode dm = myDevice.getDisplayMode();
		
		frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.getContentPane().setBackground(Color.green);
        frame.pack();	// размер по содержимому
        frame.setResizable(false);
        frame.setMinimumSize(new Dimension(80, 60));
		frame.setVisible(true);
		panel.new DataGetter(10).start();	
		
		dataInFiles = new DataInFiles("readRS232.txt");
		
		ActionListener listener = panel.new SerialRefreshTimer();		
		Timer t = new Timer(1000/dm.getRefreshRate(), listener);	// обновляем графики с частотой кадров
		t.start();
		
		listener = panel.new SerialPortScannerTimer();		
		Timer t2 = new Timer(500, listener);
		t2.start();
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(PanelCollection.getSerialPort() != null) PanelCollection.getSerialPort().killClass();
				dataInFiles.closeFile();
				System.exit(0);
			}
		});
//		frame.addWindowStateListener((WindowEvent event) -> {
//			if(serialPort != null) serialPort.killClass();
//			dataInFiles.closeFile();
//			System.exit(0);
//		});
		
		
	}
	
	private static void setNimbusLaF() {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception ex) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
			Log.warn("NimbusLaF not found", ex);
		}
	}
	
}


