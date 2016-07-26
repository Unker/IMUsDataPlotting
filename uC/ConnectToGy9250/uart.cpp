/*
// Title        : Simple UART driver
// Author       : Lars Pontoppidan Larsen
// Date         : June, 2006
// Version      : 1.01
// Target MCU   : Atmel AVR Series
//
// DESCRIPTION:
// This module implements simple UART communication. Strings can be transmitted 
// from sram or program memory and must be either '0' terminated or limited by a 
// length parameter. 
//
// The send functions block until done, and uart_getc waits until a char is 
// recieved.
//
// Received characters are stored in a ring buffer.
// 
// USAGE:
// Call uart_init() at startup. 
// 
// Query uart_buffer_empty() to check if bytes are pending in recieve buffer.
// Get these bytes with uart_getc().
//
// Send a char with uart_putc or a string with a uart_print* function.
//
// DISCLAIMER:
// The author is in no way responsible for any problems or damage caused by
// using this code. Use at your own risk.
//
// LICENSE:
// This code is distributed under the GNU Public License
// which can be found at http://www.gnu.org/licenses/gpl.txt
//

//
// VERSION LOG:
//
// Version 1.01 June, 2006  
// This version has the same functionality but was cleaned up
//
// Version 1.00 February, 2006  
// Initial version
*/


#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/pgmspace.h>

#include "uart2.h"


//#define BAUD_RATE 9600

/* Recieve buffer size */
#define RX_BUFFER_SIZE 10

/* Increase ring buffer macro: */
#define INC_RING(var, bsize) if (var == ((bsize)-1)) var=0; else var++

#ifndef TRUE
#define TRUE 1
#endif
#ifndef FALSE
#define FALSE 0
#endif




unsigned char rx_buffer[RX_BUFFER_SIZE];
volatile unsigned char rx_buffer_head;
volatile unsigned char rx_buffer_tail;

volatile char tx_complete;


// UART transmit interrupt handler.
//
ISR(USART0_TX_vect) // before: SIGNAL(SIG_USART_TRANS)
//ISR(UART0_TRANSMITE_INTERRUPT)
{
  tx_complete = TRUE;
}


// UART recieve interrupt handler.
//   
// If the buffer overflows, data will be overwritten without warning
//
ISR(USART0_RX_vect) // before: SIGNAL(SIG_USART_RECV)
//ISR(UART0_RECEIVE_INTERRUPT)
{
  char c = UDR0;
  rx_buffer[rx_buffer_head] = c;
  INC_RING(rx_buffer_head, RX_BUFFER_SIZE);
  // one might deal with head==tail here (buffer full)
}


// Returns true if recieve buffer is empty
//
unsigned char uart_buffer_empty(void)
{
  return (rx_buffer_tail == rx_buffer_head);
}


// Get char from buffer. 
// Note that this function BLOCKS until a char is recieved.
//
unsigned char uart_getc(void) 
{
  unsigned char c;
  
  while (rx_buffer_tail == rx_buffer_head);
  
  c = rx_buffer[rx_buffer_tail];
  INC_RING(rx_buffer_tail,RX_BUFFER_SIZE);
  return c;
  /*if (rx_buffer_tail != rx_buffer_head){
	  c = rx_buffer[rx_buffer_tail];
	  INC_RING(rx_buffer_tail, RX_BUFFER_SIZE);
	  return c;
  }
  else  return 255;*/
}

// Send a single char to uart. Waits until the char is send.
//
void uart_putc(const char c){
  while (!tx_complete);
  tx_complete=FALSE;
  UDR0 = c;
}



// Send 0 terminated data from sram.
//
void uart_print(const char *ptr){
  while(*ptr) {
    while (!tx_complete);
    tx_complete=FALSE;
    UDR0 = *(ptr++);  
  }
}



// Send 'len' bytes from sram.
//
void uart_printlen(const char *ptr, unsigned short len){
  
  while(len--) {
    while (!tx_complete);
    tx_complete=FALSE;
    UDR0 = *(ptr++);  
  }
}


// Send 0 terminated data from program memory.
//
void uart_printprog(const char *ptr){
  while(pgm_read_byte_near(ptr)) {
    while (!tx_complete);
    tx_complete=FALSE;
    UDR0 = pgm_read_byte_near(ptr++);  
  }
}

// Prints a byte as hexadecimal ascii
//
void uart_printchar(unsigned char c)
{
  char buffer[2];
  
  if ((c>>4) > 9)
    buffer[0] = (c>>4) - 10 + 'A';
  else
    buffer[0] = (c>>4) + '0';
  
  if ((c&0x0F) > 9)
    buffer[1] = (c&0x0F) - 10 + 'A';
  else
    buffer[1] = (c&0x0F) + '0';
  
  uart_printlen(buffer,2);
  
}

// Initialization of uart.
// 
// Configured for UART 0 on ATMega2560
//
void uart_init(uint16_t BAUD_RATE)
{
  unsigned short s;
  
  /* Set baud rate */
  s = (double)F_CPU / (BAUD_RATE*16.0) - 1.0; // this computes compile time
  UBRR0H = (s & 0xFF00);
  UBRR0L = (s & 0x00FF);

  /* Receive complete interrupt enable, Receiver & Transmitter enable */
  UCSR0B = (1<<RXCIE0)|(1<<TXCIE0)|(1<<RXEN0)|(1<<TXEN0);
  //UCSR0B = (1<<RXEN0)|(1<<TXEN0);
  UCSR0C = (1<<UCSZ01)|(1<<UCSZ00);
  
  /* Set DDR TX to output */ 
  DDRD |= (1<< 1);      
  
  /* Set DDR RX to input */
  DDRD &= ~(1<< 0);
  
  /* Reset buffers */
  rx_buffer_head = 0;
  rx_buffer_tail = 0;
  
  tx_complete = TRUE;

}

void USART_Transmit( unsigned char data )
{
/* Wait for empty transmit buffer */
while ( !( UCSR0A & (1<<UDRE0)) )
;
/* Put data into buffer, sends the data */
UDR0 = data;
}
