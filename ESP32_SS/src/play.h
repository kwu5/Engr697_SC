#include <Arduino.h>
#include <HX711.h>
#include <WiFi.h>
#include <FirebaseESP32.h>

#define FIREBASE_HOST "smartscale697.firebaseio.com" 
#define FIREBASE_AUTH "sjPs17K5ixPBqPWhyeYsEHQCgdXLWfkarxEzSSgO" 
#define WIFI_SSID "Barley TEA" 
#define WIFI_PASSWORD "S1Mark47Sle3p3r" 

#define DOUT  25
#define CLK   26

#define touchPIN 4

int threshold = 100;
bool ctdetected = false;

void Touch() {
 ctdetected = true;
}

class HX711 scale;
float calF = -12450; // Calibration Factor -7050, -12450.00,

FirebaseData DT;
FirebaseData WEIGHT;
int n;

void calibrate() {
    if(Serial.available()) {
    char temp = Serial.read();
      if(temp == '+' || temp == 'a')
        calF += 10;
      else if(temp == '-' || temp == 'z')
        calF -= 10;
    }
  }

void setup() {
  Serial.begin(9600);
  //check for touch
  touchAttachInterrupt(touchPIN, Touch, threshold);
  if(ctdetected){
    ctdetected = false;
    Serial.println("Touch detected, powering on scale.");
    scale.power_up(); //power up scale
  } 

  // Scale startup
  scale.begin(DOUT, CLK);
  scale.set_scale();
  scale.tare(); //Reset the scale to 0

  long zero_factor = scale.read_average(); //Get a baseline reading
  Serial.print("Zero factor: "); // Removes need for init tare
  Serial.println(zero_factor);

   // Wifi connect 
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD); 
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
  if(Serial.available())
    calibrate();

  Firebase.pushTimestamp(DT, "path");
  Firebase.pushFloat(WEIGHT, "path", weight);
  delay(120000);
  scale.power_down();
}