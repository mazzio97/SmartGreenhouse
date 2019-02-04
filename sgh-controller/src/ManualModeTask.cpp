#include <Arduino.h>
#include "ManualModeTask.h"
#include "MsgService.h"
#include "Led.h"

ManualModeTask::ManualModeTask(int ledPin) {
	this->led = new Led(ledPin);
}

void ManualModeTask::tick() {
	Serial.println("Bye from ManualMode!");
}
