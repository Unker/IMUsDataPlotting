# IMUsDataPlotting
Collects data from 9-axis IMU sensors MPU9250 and plotting on the frame. Also filtering data is done, and determined the position in space. [Собираем данные с двух 9-осных датчиков (ДУСы, акселерометры и магнитометры), фильтруем данные, определяем положение в пространстве (вычисляем углы Эйлера), отображаем показания датчиков на графиках]

IDE Java: Eclipse;
Java project powered by

jSSC (Java Simple Serial Connector)
https://github.com/scream3r/java-simple-serial-connector

jfreechart-1.0.19
http://www.jfree.org/jfreechart/download.html
https://sourceforge.net/projects/jfreechart/files/

jcommon-1.0.23
http://www.jfree.org/jcommon/
https://sourceforge.net/projects/jfreechart/files/3.%20JCommon/

slf4j-1.7.21
https://github.com/qos-ch/slf4j

logback-1.1.7
https://github.com/qos-ch/logback

IDE MCU: AtmelStudio6; MCU: ATMega256 on base of Arduino Mega2560;
project powered by

Lightweight millisecond tracking library

millis.h, millis.cpp
http://blog.zakkemble.co.uk/millisecond-tracking-library-for-avr/
https://github.com/zkemble/millis

TWI/I2C library for Wiring & Arduino
twi.h, twi.cpp
https://github.com/codebendercc/arduino-library-files/tree/master/libraries/Wire/utility

wire.h, wire.cpp
https://github.com/codebendercc/arduino-library-files/tree/master/libraries/Wire

MPU9250 Driver for Arduino
https://github.com/Snowda/MPU9250


Описание работы:
1) Емеется МК ATMega256 к которому подключены два 9-осных датчика MPU9250 по I2C (в последствии необходима заменить на SPI, т.к. у магнитометров один адрес на шине I2C, не зависимо от бита AD0). 
С другой стороны подключен ПК по UART.
МК слушает команды по UART, опрашивает датчики и выдает результаты обратно в UART
2) На ПК программа IMUsDataPlotting отправляет запросы, получает ответ и отрисовывает на графиках полученные данные от МК.
Что дальше:
1) Будет добавлена фильтрация показаний датчиков и вычисление углов Эйлера
2) В перспективе будет отображение положений датчиков на 3D моделях