#include <Arduino.h>
#include "PumpTask.h"
#include "MsgService.h"
#include "GreenHouse.h"
#include "ServoMotorImpl.h"
#include "Led.h"

PumpTask::PumpTask(int ledPin, int servoPin) {
	this->taskState = P0;
	this->led = new Led(ledPin);
	this->servo = new ServoMotorImpl(servoPin);
}

void PumpTask::tick() {
		switch (this->taskState) {
		case P0:
			if (GreenHouse::getFlowRate() > 0) {
				this->servo->on();
				this->taskState = P1;
			}
			break;

		case P1:
			this->servo->open();
			if (!this->led->isOn()) {
				this->led->switchOn();
			}
			if (GreenHouse::getFlowRate() <= 0) {
				this->taskState = P2;
			}
			break;
		case P2:
			this->servo->close();
			if (GreenHouse::getFlowRate() > 0) {
				this->taskState = P1;
			} else if (this->servo->getPosition() <= 0) {
				this->servo->off();
				this->led->switchOff();
				this->taskState = P0;
			}
			break;
	}
}
