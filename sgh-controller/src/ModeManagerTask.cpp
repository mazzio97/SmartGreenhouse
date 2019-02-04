#include <Arduino.h>
#include "ModeManagerTask.h"
#include "MsgService.h"
#include "UltraSonicSensorImpl.h"
#include "GreenHouse.h"

ModeManagerTask::ModeManagerTask(int echoPin, int triggerPin) {
	this->taskState = MM0;
	this->us = new UltraSonicSensorImpl(echoPin, triggerPin);
}

void ModeManagerTask::tick() {
	switch (this->taskState) {
		case MM0:
			GreenHouse::changeState(State::AUTO);
			if (this->us->getDistance() <= DIST) {
				this->taskState = MM1;
			}
			Serial.println("State AUTO");
			break;

		case MM1:
			if (this->us->getDistance() >= DIST) {
				this->taskState = MM0;
			}
			Serial.println("Waiting Bluetooth connection");
			break;
		case MM2:

			break;
	}
}
