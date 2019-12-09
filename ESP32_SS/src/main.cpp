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
FirebaseData TIMEST;
FirebaseData WEIGHT;

/* NTP server credentials */
const char* ntpServer = "pool.ntp.org";
const long  gmtOffset_sec = -28800; // PST: -8h
const int   daylightOffset_sec = 3600;
char timestamp[50];

/* Pins */
#define DOUT  15 // Scale data pin
#define CLK   4  // Scale clock pin
#define CP    2  //control pin/reset
#define Threshold 40
touch_pad_t touchpad;

/* HX711 call */
class HX711 scale;
float calF = -22185; // Calibration Factor

/* wake from deep sleep */
void callback(){
  Serial.println("touch pin wake");
  scale.power_up();
}

/* Get timestamp from server */
void printLocalTime() {
  struct tm timeinfo;
  if(!getLocalTime(&timeinfo)) {
    Serial.println("Failed to obtain time");
    return;
  }
  Serial.println(&timeinfo, "%A, %B %d %Y %l:%M:%S %p");
  strftime(timestamp, 50, "%B %d, %Y %l:%M:%S %p", &timeinfo);
  Firebase.pushString(TIMEST, "user/user1/scale/TIME", timestamp);
  Serial.printf(timestamp);
}

void setup() {
  Serial.begin(115200);
  // delay(1000); //Take some time to open up the Serial Monitor
 
 // Configure wakeup source
  touchAttachInterrupt(CP, callback, Threshold);
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
    delay(500); 
    }
 
  Serial.println(); 
  Serial.print("Connected to IP: "); 
  Serial.println(WiFi.localIP()); 

  // init and get the time
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
  printLocalTime();

  // Firebase connect   
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  }

void loop() {
  delay(10000);
  // pinMode(2, true);
  scale.set_scale(calF); 
  Serial.print("Reading: ");
  float weight = scale.get_units(10);
  Serial.print(weight);
  Serial.print(" kg");
  Serial.print(" calibration factor: ");
  Serial.print(calF);
  Serial.println();
  
  Firebase.pushFloat(WEIGHT, "user/user1/scale/WEIGHT", weight);

  // Sleep protocol
  delay(30000);
  scale.power_down();
  Serial.println("Scale entering sleep mode");
  esp_deep_sleep_start();
  Serial.println("Verify sleep");
}
