#include <Arduino.h>
#include <HX711.h>
#include <WiFi.h>
#include <FirebaseESP32.h>
#include "WiFiConfig.h"

#define FIREBASE_HOST "test-d83d0.firebaseio.com"
#define FIREBASE_AUTH "xjh8oFC7o3HEWIUnJ7G3qmfmSVEPKJJd5w3ZvMi1"
//#define FIREBASE_HOST "espSS.firebaseio.com" 
//#define FIREBASE_AUTH "sjPs17K5ixPBqPWhyeYsEHQCgdXLWfkarxEzSSgO" 

#define DOUT  25  // Scale data pin
#define CLK   26  // Scale clock pin
#define Threshold 40 /* Greater the value, more the sensitivity */
touch_pad_t touchPin;

class HX711 scale;
float calF = -12450; // Calibration Factor -7050, -12450.00,
int n;
FirebaseData DT;
FirebaseData WEIGHT;

void callback() {
Serial.println("touchpad wake");
scale.power_up(); //power up scale
}

void setup() {
  Serial.begin(115200);
  delay(1000); //Take some time to open up the Serial Monitor

  // Setup interrupt on (GPIO04 and GPIO15)
  touchAttachInterrupt(15, callback, Threshold);
  touchAttachInterrupt(4, callback, Threshold);

  // Configure Touchpad as wakeup source
  esp_sleep_enable_touchpad_wakeup();

  // Scale startup
  scale.begin(DOUT, CLK);
  scale.set_scale();
  scale.tare(); //Reset the scale to 0
  long zero_factor = scale.read_average(); //Get a baseline reading
  Serial.print("Zero factor: "); // Removes need for init tare
  Serial.println(zero_factor);

   // Wifi connect 
  WiFi.begin(SSID, PASS);
  // WiFi.softAP(ssid, password);
  IPAddress myIP = WiFi.softAPIP();
  Serial.print("Connecting to Wi-Fi"); 
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print("."); 
    delay(500); 
  }
  Serial.println(); 
  Serial.print("Connected to IP: "); 
  Serial.println(WiFi.localIP()); 
   
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  String path = "/ESP_SS/WEIGHT_RAW";
}

void loop() {
  scale.set_scale(calF); 
  Serial.print("Reading: ");
  float weight = scale.get_units(10);
  Serial.print(weight);
  Serial.print(" kg");
  Serial.print(" calibration factor: ");
  Serial.print(calF);
  Serial.println();
  
  Firebase.pushTimestamp(DT, "path");
  Firebase.pushFloat(WEIGHT, "path", weight);
  delay(120000);
  scale.power_down();
    //Go to sleep
  Serial.println("Going to sleep now");
  esp_deep_sleep_start();
  Serial.println("This will never be printed");
}