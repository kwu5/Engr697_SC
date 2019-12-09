/*
 Example using the SparkFun HX711 breakout board with a scale
 By: Nathan Seidle
 SparkFun Electronics
 Date: November 19th, 2014
 License: This code is public domain but you buy me a beer if you use this and we meet someday (Beerware license).

 This is the calibration sketch. Use it to determine the calibration_factor that the main example uses. It also
 outputs the zero_factor useful for projects that have a permanent mass on the scale in between power cycles.

 Setup your scale and start the sketch WITHOUT a weight on the scale
 Once readings are displayed place the weight on the scale
 Press +/- or a/z to adjust the calibration_factor until the output readings match the known weight
 Use this calibration_factor on the example sketch

 This example assumes pounds (lbs). If you prefer kilograms, change the Serial.print(" lbs"); line to kg. The
 calibration factor will be significantly different but it will be linearly related to lbs (1 lbs = 0.453592 kg).

 Your calibration factor may be very positive or very negative. It all depends on the setup of your scale system
 and the direction the sensors deflect from zero state
 This example code uses bogde's excellent library:"https://github.com/bogde/HX711"
 bogde's library is released under a GNU GENERAL PUBLIC LICENSE
 Arduino pin 4 -> HX711 CLK
 2 -> DOUT
 5V -> VCC
 GND -> GND

 Most any pin on the Arduino Uno will be compatible with DOUT/CLK.

 The HX711 board can be powered from 2.7V to 5V so the Arduino 5V power should be fine.

*/
#include <Arduino.h>
#include <HX711.h>

#define DOUT  2
#define CLK  4

class HX711 scale;

float calibration_factor; //= -23600; //default -7050 -22840

void setup() {
  Serial.begin(115200);

  scale.begin(DOUT, CLK);
  scale.set_scale();
  scale.tare(); //Reset the scale to 0

  long zero_factor = scale.read_average(20); //Get a baseline reading
  Serial.print("Zero factor: "); //This can be used to remove the need to tare the scale. Useful in permanent scale projects.
  Serial.println(zero_factor);
  long test_factor = scale.get_units(10);
  Serial.print("test factor: ");
  Serial.println(test_factor);
  // calibration_factortt = test_factor*3;
  calibration_factor = -22185;

}

void loop() {
  scale.set_scale(calibration_factor);

  Serial.print("Reading: ");
  Serial.print(scale.get_units(3));
  Serial.print(" kg");
  Serial.print(" calibration_factor: ");
  Serial.print(calibration_factor);
  Serial.println();
  // delay(100);

  if(Serial.available()) {
    char temp = Serial.read();
    if(temp == 'T' || temp == 't')
    void esp_restart(void);

    if(temp == '+' || temp == 'a' || temp == 'A')
      calibration_factor += 10;
    if(temp == 'S' || temp == 's') 
      calibration_factor += 1;
    if(temp == '-' || temp == 'z' || temp == 'Z')
      calibration_factor -= 10;
    if(temp == '+' || temp == 'X' || temp == 'x')
      calibration_factor -= 1;

  }
}