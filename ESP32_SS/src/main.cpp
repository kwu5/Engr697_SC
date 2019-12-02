#include <Arduino.h>
#include <HX711.h>
#include <WiFi.h>
#include <FirebaseESP32.h>

#define  SSID "KINAGO 8270"
#define  PASS "1C2t8(77"

#define FIREBASE_HOST "smartscale697.firebaseio.com" 
#define FIREBASE_AUTH "sjPs17K5ixPBqPWhyeYsEHQCgdXLWfkarxEzSSgO" 

#define DOUT  25  // Scale data pin
#define CLK   26  // Scale clock pin
#define Threshold 40
touch_pad_t touchPin;

class HX711 scale;
float calF = -12450; // Calibration Factor -7050, -12450.00,

FirebaseData UserId;
FirebaseData WEIGHT;

void callback() {
Serial.println("touchpad wake");
Serial.println(touchRead(15));
Serial.println(touchRead(4));
scale.power_up();
}

void setup() {
  Serial.begin(115200);
  delay(1000); //Take some time to open up the Serial Monitor

  // Setup interrupt on (GPIO04 and GPIO15)
  touchAttachInterrupt(15, callback, Threshold);  // sleepwake center capacitive button?
  touchAttachInterrupt( 4, callback, Threshold);

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
  Serial.print("Connecting to Wi-Fi \n"); 
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print("."); 
    delay(500); 
    }
 
  Serial.println(); 
  Serial.print("Connected to IP: "); 
  Serial.println(WiFi.localIP()); 
   
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
  Firebase.getShallowData(UserId, "user");
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
  
  Firebase.setFloat(WEIGHT, "user/user_1/weight", weight);

  // Sleep protocol
  delay(30000);
  scale.power_down();
  Serial.println("Scale entering sleep mode");
  esp_deep_sleep_start();
  Serial.println("Verify sleep");
}
