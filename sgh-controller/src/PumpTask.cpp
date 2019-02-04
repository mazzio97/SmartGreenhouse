#include <Arduino.h>
#include "PumpTask.h"
#include "MsgService.h"
#include "Led.h"

PumpTask::PumpTask(int ledPin) {
	this->led = new Led(ledPin);
}

void PumpTask::tick() {
	Serial.println("Bye from Pump!");
}
