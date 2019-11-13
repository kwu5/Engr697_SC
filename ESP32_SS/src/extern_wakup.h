/*
Deep Sleep with Touch Wake Up
=====================================
This code displays how to use deep sleep with
a touch as a wake up source and how to store data in
RTC memory to use it over reboots

This code is under Public Domain License.

Author:
Pranav Cherukupalli <cherukupallip@gmail.com>
*/
#include <Arduino.h>

#define Threshold 40 /* Greater the value, more the sensitivity */
touch_pad_t touchPin;

void callback() {
  //placeholder callback function
}

void setup() {
  Serial.begin(115200);
  delay(1000); //Take some time to open up the Serial Monitor

  Serial.println("touchpad wake");

  //Setup interrupt on (GPIO04 and GPIO15)
  touchAttachInterrupt(15, callback, Threshold);
  touchAttachInterrupt(4, callback, Threshold);

  //Configure Touchpad as wakeup source
  esp_sleep_enable_touchpad_wakeup();

  //Go to sleep
  Serial.println("Going to sleep now");
  delay(120000);
  esp_deep_sleep_start();
  Serial.println("This will never be printed");
}

void loop() {
  //This will never be reached
}