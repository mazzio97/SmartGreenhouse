#include <Arduino.h>
#include "ModeManagerTask.h"
#include "MsgService.h"
#include "UltraSonicSensorImpl.h"
#include "BluetoothMsgService.h"
#include "GreenHouse.h"

ModeManagerTask::ModeManagerTask(int echoPin, int triggerPin, int txPin, int rxPin, int statusPin) {
	this->taskState = MM0;
	this->us = new UltraSonicSensorImpl(echoPin, triggerPin);
	this->btms = new BluetoothMsgService(txPin, rxPin, statusPin);
}

void ModeManagerTask::tick() {
	switch (this->taskState) {
		case MM0:
			GreenHouse::changeState(State::AUTO);
			if (this->us->getDistance() <= DIST) {
				this->taskState = MM1;
			}
			break;

		case MM1:
			Serial.println("READY TO CONNECT");
			if (this->us->getDistance() >= DIST) {
				this->taskState = MM0;
			} else if(this->btms->isConnected()) {
				this->taskState = MM2;
			}
			break;
		case MM2:
			Serial.println("CONNECTED");
			if(!this->btms->isConnected()) {
				this->taskState = MM0;
				Serial.println("DISCONNECTED");
			}
			break;
	}
}
