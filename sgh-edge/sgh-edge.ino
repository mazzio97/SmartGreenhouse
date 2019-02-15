#include <ESP8266WiFi.h>
// #include "DHT.h"

#define NAME "Vodafone-A47870842"
#define PASS "w57t646yt8mpwpw4"

const char* host = "192.168.1.13";
WiFiClient client;


// #define DHTPIN 4
// #define DHTTYPE DHT22

//DHT dht(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(115200);
  Serial.println();

  WiFi.mode(WIFI_STA);

  WiFi.begin(NAME, PASS);

  /* Connect to the network */
  Serial.print("Connecting");
  while(WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println();

  Serial.print("Connected, IP address: ");
  Serial.println(WiFi.localIP());
 
  pinMode(A0, INPUT);
}

void loop() {
  //float h = dht.readHumidity();
  float h = analogRead(A0) / 10;
  if (client.connect(host, 5050)) {
    Serial.print("Connected to: ");
    Serial.println(host);

    Serial.println(h);
    client.println(h);
//    if (isnan(h)) {
//      client.println("DHT read failed");
//    } else {
//      client.println(h);
//    }
  } else {
    Serial.println("Connection problems");
  }
  delay(1000);
}
