//#define F_CPU 16000000UL
#define UART_RX_BUFFER_SIZE 64
#define UART_TX_BUFFER_SIZE 64

#include <avr/io.h>
#include <inttypes.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include <avr/pgmspace.h>
#include <stdlib.h>
#include "millis.h"
#include "Wire.h" 
#include "I2Cdev.h"
#include "uart.h"
#include "MPU9150.h"
#include <math.h>

void setup(void);
void ScanWire(void);
void setConfigMPU (MPU9150 mpu);
void sendData (MPU9150 mpu, uint16_t dT);

MPU9150 rotorAccGyro;
MPU9150 statorAccGyro(MPU9150_ADDRESS_AD0_HIGH);
unsigned char glob = 0;
	int16_t ax, ay, az;
	int16_t gx, gy, gz;
	int16_t mx, my, mz;


int main(void) 
{
	int16_t i=0;
	setup();
	bool startSend = 0;
	millis_t time = millis_get();
	millis_t lastTime = 0;
	uint16_t dTRotor=0;
	uint16_t dTStator=0;

	while(1)
	{
		//char numb[6];
		if(!uart_buffer_empty()){
			glob = uart_getc();
			switch(glob){
				case 'S':{
					startSend = 1;
					break;
				}
				case 'Z':{
					startSend = 0;
					//i = 0;
					break;
				}
				case 't':{
					lastTime = time; 
					time = millis_get();
					if(time - lastTime > 0){
						dTRotor = time - lastTime;
					}
					else{
						dTRotor = dTRotor;
					}
					sendData(rotorAccGyro, dTRotor);
					break;
				}
				case 'r':{
					lastTime = time;
					time = millis_get();
					if(time - lastTime > 0){
						dTStator = time - lastTime;
					}
					else{
						dTStator = dTStator;
					}
					sendData(statorAccGyro, dTStator);
					break;
				}
				default:{
					break;
				}
					
			}
			
		}

	}
return(0);	
}

void setup()
{
	uart_init(57600);
	sei();
	millis_init();
	Wire.begin();
	
	//ScanWire();
	

	
	setConfigMPU(rotorAccGyro);
	setConfigMPU(statorAccGyro);
	
//	uart_print("\n\raccelgyro rate = "); uart_printchar(accelgyro.getRate());
//	uart_print("\n\rgetDeviceID = "); uart_printchar(accelgyro.getDeviceID());
	//_delay_ms(3000);
//	uart_print("\r\nGO\r\n");
}

void setConfigMPU (MPU9150 mpu)
{
	mpu.initialize();
	mpu.setRate(7); //0
	mpu.setFullScaleAccelRange(MPU9150_ACCEL_FS_8);//
	mpu.setFullScaleGyroRange(MPU9150_GYRO_FS_1000);//
}

void sendData (MPU9150 mpu, uint16_t dT)
{				
	mpu.getMotion9(&ax, &ay, &az, &gx, &gy, &gz, &mx, &my, &mz);
	//accelgyro.getMotion6(&ax, &ay, &az, &gx, &gy, &gz);					
	uart_putc( (unsigned char)(dT>>8) ); uart_putc( (unsigned char)(dT&0x00FF) );
	uart_putc( (unsigned char)(ax>>8) ); uart_putc( (unsigned char)(ax&0x00FF) ); 
	uart_putc( (unsigned char)(ay>>8) ); uart_putc( (unsigned char)(ay&0x00FF) ); 
	uart_putc( (unsigned char)(az>>8) ); uart_putc( (unsigned char)(az&0x00FF) ); 
	uart_putc( (unsigned char)(gx>>8) ); uart_putc( (unsigned char)(gx&0x00FF) );
	uart_putc( (unsigned char)(gy>>8) ); uart_putc( (unsigned char)(gy&0x00FF) );
	uart_putc( (unsigned char)(gz>>8) ); uart_putc( (unsigned char)(gz&0x00FF) );
	uart_putc( (unsigned char)(mx>>8) ); uart_putc( (unsigned char)(mx&0x00FF) );
	uart_putc( (unsigned char)(my>>8) ); uart_putc( (unsigned char)(my&0x00FF) );
	uart_putc( (unsigned char)(mz>>8) ); uart_putc( (unsigned char)(mz&0x00FF) );//*/
					
	//uart_print("G");
	/*uart_printchar( (unsigned char)(ax>>8) ); uart_printchar( (unsigned char)(ax&0x00FF) ); uart_print(" ");
	uart_printchar( (unsigned char)(ay>>8) ); uart_printchar( (unsigned char)(ay&0x00FF) ); uart_print(" ");
	uart_printchar( (unsigned char)(az>>8) ); uart_printchar( (unsigned char)(az&0x00FF) ); uart_print(" ");
	uart_printchar( (unsigned char)(mx>>8) ); uart_printchar( (unsigned char)(mx&0x00FF) ); uart_print(" ");
	uart_printchar( (unsigned char)(my>>8) ); uart_printchar( (unsigned char)(my&0x00FF) ); uart_print(" ");
	uart_printchar( (unsigned char)(mz>>8) ); uart_printchar( (unsigned char)(mz&0x00FF) ); uart_print("\n\r");*/
}

void ScanWire()
{
		uint8_t error, address;
		int nDevices;
		
		_delay_ms(3000);
		
		uart_print("\r\nScanning...");
		
		nDevices = 0;
		for(address = 0; address <= 127; address++ )
		{
			// The i2c_scanner uses the return value of
			// the Write.endTransmisstion to see if
			// a device did acknowledge to the address.
			Wire.beginTransmission(address);
			error = Wire.endTransmission();
			
			if (error == 0)
			{
				uart_print("\r\nI2C device found at address 0x");
				uart_printchar(address);
				uart_print(" !");
				
				nDevices++;
			}
			else if (error==4)
			{
				uart_print("\r\nUnknow error at address 0x");
				uart_printchar(address);
			}
		}
		if (nDevices == 0)
		uart_print("No I2C devices found\r\n");
		else
		uart_print("done\r\n");
		
		_delay_ms(3000); // wait 8 seconds for next scan		
}