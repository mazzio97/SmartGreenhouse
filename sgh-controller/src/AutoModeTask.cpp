#include <Arduino.h>
#include "AutoModeTask.h"
#include "MsgService.h"
#include "Led.h"

AutoModeTask::AutoModeTask(int ledPin) {
	this->led = new Led(ledPin);
}

void AutoModeTask::tick() {
	Serial.println("Bye from AutoMode!");
}
