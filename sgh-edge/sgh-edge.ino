#include <ESP8266WiFi.h>
#include "DHT.h"

#define NAME "EOLO - FRITZ!Box MC"
#define PASS "98241153892377645827"
#define DHTPIN 4
#define DHTTYPE DHT22

const char* host = "192.168.178.113";
WiFiClient client;
//DHT dht(DHTPIN, DHTTYPE);

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
  pinMode(A0, INPUT);
}

void loop() {
  //float h = dht.readHumidity();
  float h = analogRead(A0) / 10;
  if (client.connect(host, 5050))
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
  delay(1000);
}
