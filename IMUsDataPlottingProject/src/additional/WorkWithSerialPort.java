package additional;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;
import main.IMUsDataPlotting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkWithSerialPort extends SerialPort {
	
	
	static final Logger LOG = LoggerFactory.getLogger(IMUsDataPlotting.class);
	
	private Queue<Byte> fifoDataIn;
	private static byte sentCnt = 0;
	private boolean cycleWriteEnable = false;
	public final byte startByte = 'S';
	public final byte stopByte = 'Z';
	public final byte sendRotorByte = 'r';
	public final byte sendStatorByte = 't';
	
	public final static byte LENGTH_DATA_INPUT = 40; // ���������� ���� �� ������� ������ 2 �� 20
	
	WorkWithSerialPort(String namePort) {
		super(namePort);
//		fifoDataIn = new PriorityQueue<Byte>(100);//
		fifoDataIn = new ConcurrentLinkedQueue<Byte>();
		fifoDataIn.clear();
	}

	public String openSerialPort(int baudrate) {
		String returnVal;

		try {			
            //��������� ����
            if(this.openPort()) {
            	returnVal = "Open-OK";
            	this.purgePort(PURGE_RXCLEAR | PURGE_TXCLEAR);
            	
            	//���������� ���������
            	this.setParams(baudrate, 
	                             SerialPort.DATABITS_8,
	                             SerialPort.STOPBITS_1,
	                             SerialPort.PARITY_NONE);
                
                //��������� ���������� ���������� �������
            	this.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                
                //������������� ����� ������� � �����
            	this.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);

            }
            else {
            	returnVal = "Open-nOK";
            }
            
        }
        catch (SerialPortException ex) {
        	//ex.printStackTrace();
        	LOG.error("�������� ���� �� ������", ex);
            returnVal = "SerialPortException:" + ex;
//            infoLbl.setText(ex.toString());
        }
	
    	return returnVal;
	}
	
	public void setCycleWriteEnable(boolean enable){
		cycleWriteEnable = enable;
	}

	
	WorkWithSerialPort getClassSerialPort() {
		return this;
	}
	
	private class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    //�������� ����� �� ����������, ������������ ������ � �.�.
                	byte[] read;
					try {
						read = readBytes(LENGTH_DATA_INPUT, 500);	
						for (int i = 0; i < read.length; i++) {
	                		fifoDataIn.add(read[i]);
	                	}
					} catch (SerialPortTimeoutException ex) {
						//ex.printStackTrace();
						LOG.error("��������� ����� �������� ������", ex);
					}

                    //� ����� ���������� ������
                    if(cycleWriteEnable) {
                    	writeByte(sendStatorByte); //������
                    	writeByte(sendRotorByte); //�����
                    }
                }
                catch (SerialPortException ex) {
                	//ex.printStackTrace();
                	LOG.error("���-�� �� ��", ex);
    				PanelCollection.getSerialPort().killClass();
                }
            }            
        }
    }
	
	public Queue<Byte> getQueueReadData() {
		return fifoDataIn;
	}

	public void killClass() {
		if(this.isOpened()){
			try {
				this.removeEventListener();
				this.closePort();
			} catch (SerialPortException ex) {
				//System.out.println("KillClass");
				//ex.printStackTrace();
				LOG.error("������ ��� �������� �����", ex);
			}
		}
	}
	

}
