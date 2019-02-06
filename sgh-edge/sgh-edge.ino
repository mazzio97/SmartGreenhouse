#include <ESP8266WiFi.h>
#include "DHT.h"

#define NAME "Marchetti Fabio"
#define PASS "0f0f0f0f0f"
#define DHTPIN 4
#define DHTTYPE DHT22

const char* host = "192.168.178.113";
WiFiClient client;
DHT dht(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(115200);
  Serial.println();

  /* Set Client up as station */
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
}

void loop() {
  float h = dht.readHumidity();
  if (client.connect(host, 9876))
  {
    Serial.print("Connected to: ");
    Serial.println(host);

    Serial.println(h);
//    if (isnan(h)) {
//      client.println("DHT read failed");
//    } else {
      client.println(h);
//    }
  } else {
    Serial.println("Connection problems");
  }
  delay(2000);
}
