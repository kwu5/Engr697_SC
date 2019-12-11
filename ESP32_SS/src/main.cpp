#include <Arduino.h>
#include <HX711.h>
#include <WiFi.h>
#include <FirebaseESP32.h>
#include <time.h>

/* Hotspot credentials */
#define  SSID "KINAGO 8270"
#define  PASS "1C2t8(77"

/* Firebase authorization & init*/
#define FIREBASE_HOST "smartscale697.firebaseio.com" 
#define FIREBASE_AUTH "sjPs17K5ixPBqPWhyeYsEHQCgdXLWfkarxEzSSgO" 
// #define FIREBASE_HOST "test-d83d0.firebaseio.com"
// #define FIREBASE_AUTH "xjh8oFC7o3HEWIUnJ7G3qmfmSVEPKJJd5w3ZvMi1" 

FirebaseData TIME;
FirebaseData WEIGHT;

/* NTP server credentials */
const char* ntpServer = "pool.ntp.org";
const long  gmtOffset_sec = -28800; // PST: -8h
const int   daylightOffset_sec = 3600;
char timestamp[50];

/* Pins */
#define DOUT  15 // Scale data pin
#define CLK   4  // Scale clock pin
#define CP    14  //control pin/reset
#define Threshold 40

/* Wake interrupt */
void wakeup(){
  esp_restart();
}
/* HX711 call */
class HX711 scale;
float calF = -22185; // Calibration Factor

void setup() {
  Serial.begin(115200);
  // delay(1000); //Take some time to open up the Serial Monitor
 
 // Configure wakeup source
  touchAttachInterrupt(CP, wakeup, Threshold);
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
  Serial.print("Connecting to Wi-Fi \n"); 
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print("."); 
    digitalWrite(32, ~ digitalRead(32));
    delay(500); 
    }
 
  Serial.println(); 
  Serial.print("Connected to IP: "); 
  Serial.println(WiFi.localIP()); 

  // Firebase connect   
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);

  // init and get the time
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
  struct tm timeinfo;
    if(!getLocalTime(&timeinfo)) {
      Serial.println("Failed to obtain time");
      return;
    }
  strftime(timestamp, 50, "%B %d, %Y %l:%M:%S %p", &timeinfo);
  Serial.println(timestamp);
  Firebase.setString(TIME, "ESP_SS/TIMESET", timestamp);
  
}

void loop() {
  digitalWrite(32, HIGH);
  delay(5000);
  scale.set_scale(calF); 
  Serial.print("Calibration factor: ");
  Serial.println(calF);
  Serial.print("Reading: ");
  float weight = scale.get_units(10);
  Serial.print(weight);
  Serial.println(" kg");  
  Firebase.setDouble(WEIGHT, "ESP_SS/WEIGHT", weight);


  // Sleep protocol
  delay(30000);
  digitalWrite(32, LOW);
  scale.power_down();
  Serial.println("Scale entering sleep mode");
  esp_deep_sleep_start();
  Serial.println("Verify sleep");

/* wake from deep sleep */
  Serial.println("touch pin wake");
  scale.power_up();
