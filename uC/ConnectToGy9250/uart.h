#ifndef UART_H
#define UART_H

//
// Title        : Simple UART driver
// Author       : Lars Pontoppidan Larsen
// Date         : June, 2006
// Version      : 1.01
// Target MCU   : Atmel AVR Series
//



void uart_init(uint16_t BAUD_RATE);

void uart_print(const char *ptr);
void uart_printlen(const char *ptr, unsigned short len);
void uart_printprog(const char *ptr);
void uart_putc(const char c);
unsigned char uart_getc(void); 

void uart_printchar(unsigned char c);

unsigned char uart_buffer_empty(void);

void USART_Transmit( unsigned char data );


#endif
