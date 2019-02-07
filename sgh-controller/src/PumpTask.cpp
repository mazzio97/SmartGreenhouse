#include <Arduino.h>
#include "PumpTask.h"
#include "MsgService.h"
#include "GreenHouse.h"
#include "ServoMotorImpl.h"
#include "LedExt.h"

PumpTask::PumpTask(int ledPin, int servoPin) {
	this->taskState = P0;
	this->led = new LedExt(ledPin, 0);
	// this->led->switchOn();
	// this->led->setIntensity(250);
	this->servo = new ServoMotorImpl(servoPin);
}

void PumpTask::tick() {
		switch (this->taskState) {
		case P0:
			if (GreenHouse::getFlowRate() > 0) {
				this->led->switchOn();
				this->led->setIntensity(GreenHouse::getFlowRate());
				this->servo->on();
				this->taskState = P1;
			}
			break;

		case P1:
			this->servo->open();
			if (GreenHouse::getFlowRate() <= 0) {
				this->taskState = P2;
			}
			break;
		case P2:
			this->servo->close();
			if (GreenHouse::getFlowRate() > 0) {
				this->taskState = P1;
			} else if (this->servo->getPosition() <= 0) {
				this->led->switchOff();
				this->servo->off();
				this->taskState = P0;
			}
			break;
	}
}
