#include <ESP8266WiFi.h>

#define NAME "Marchetti Fabio"
#define PASS "0f0f0f0f0f"

const char* host = "192.168.1.115";

WiFiClient client;

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
  if (client.connect(host, 9876))
  {
    Serial.print("Connected to: ");
    Serial.println(host);

    /* Send "connected" to the server so it knows we are ready for data */
    client.println("deviceconnected");
  } else {
    Serial.println("Connection problems");
  }
  delay(5000);
}
