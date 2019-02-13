#include <Arduino.h>
#include "ModeManagerTask.h"
#include "MsgService.h"
#include "UltraSonicSensorImpl.h"
#include "GreenHouse.h"

Pattern btStatusPattern("status");

ModeManagerTask::ModeManagerTask(int echoPin, int triggerPin) {
	this->taskState = MM0;
	this->us = new UltraSonicSensorImpl(echoPin, triggerPin);
}

void ModeManagerTask::tick() {
	switch (this->taskState) {
		case MM0: {
			if (this->us->getDistance() <= DIST) {
				this->taskState = MM1;
			}
			break;
		}

		case MM1: 
			if (this->us->getDistance() >= DIST) {
				this->taskState = MM0;
			} else if(MsgServiceBT.isMsgAvailable()) {
				Msg* prova = MsgServiceBT.receiveMsg(btStatusPattern);
				MsgService.sendMsg("***" + prova->getContent() + "***");
				if (prova->getContent() == "ON") {
					this->taskState = MM2;
					GreenHouse::changeState(State::MANUAL);
					MsgService.sendMsg((String)MSG_TAG + "manual");
				}
			}
			break;

		case MM2:
			if(MsgServiceBT.isMsgAvailable()
			   && MsgServiceBT.receiveMsg(btStatusPattern)->getContent() == "OFF") {
				//this->taskState = MM3;
				MsgService.sendMsg((String)MSG_TAG + "auto");
				this->taskState = MM0;
				GreenHouse::changeState(State::AUTO);
			}
			break;
		case MM3: 
			// if (MsgService.isMsgAvailable()) {
			// 	MsgService.flush();
			// } else {
			// 	MsgService.sendMsg("IO SONO PRONTO");
			// }
			break;
	}
}
