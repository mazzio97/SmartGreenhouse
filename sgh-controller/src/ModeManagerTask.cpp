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
	Pattern statusPattern(STATUS_TAG);

	switch (this->taskState) {
		case MM0:
			if (this->us->getDistance() <= DIST) {
				this->taskState = MM1;
			}
			break;

		case MM1: 
			if (this->us->getDistance() >= DIST) {
				this->taskState = MM0;
			} else if(MsgServiceBT.isMsgAvailable()) {
				Msg* status = MsgServiceBT.receiveMsg(statusPattern);
				if (status != NULL && status->getContent() == "ON") {
					this->taskState = MM2;
					GreenHouse::changeState(State::MANUAL);
					MsgService.sendMsg((String) MODE_TAG + "manual");
					Msg conn("CONNECTED");
					MsgService.sendMsg(conn.getContent());
					MsgServiceBT.sendMsg(conn);
				}
			}
			break;

		case MM2:
			if(MsgServiceBT.isMsgAvailable()) {
				Msg* status = MsgServiceBT.receiveMsg(statusPattern);
				if (status != NULL && status->getContent() == "OFF") {
					this->taskState = MM0;
					GreenHouse::changeState(State::AUTO);
					MsgService.sendMsg((String) MODE_TAG + "auto");
					Msg disc("DISCONNECTED");
					MsgService.sendMsg(disc.getContent());
					MsgServiceBT.sendMsg(disc);
				}
			}
			break;
	}
}
