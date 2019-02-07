#include <Arduino.h>
#include "AutoModeTask.h"
#include "MsgService.h"
#include "GreenHouse.h"
#include "Led.h"

AutoModeTask::AutoModeTask(int ledPin) {
	this->led = new Led(ledPin);
	this->currTaskState = AM0;
}

void AutoModeTask::updateTaskState(TaskState s) {
	this->prevTaskState = this->currTaskState;
	this->currTaskState = s;
}

void AutoModeTask::tick() {
		switch (this->currTaskState) {
		case AM0:
			if (this->led->isOn()) {
				this->led->switchOff();
			}
			if (GreenHouse::checkState(AUTO)) {
				this->led->switchOn();
				updateTaskState(AM1);
			}
			break;
		case AM1: {
			int val = 0;
			if (MsgService.isMsgAvailable()) {
				Msg *msg = MsgService.receiveMsg();
				val = msg->convertToInt();
			}
			if (GreenHouse::checkState(MANUAL)) {
				updateTaskState(AM0);
			} else if (val > 0) {
				GreenHouse::setFlowRate(val);
				this->timeCnt = 0;
				updateTaskState(AM2);
			}
			break;
		}
		case AM2: {
			int val = GreenHouse::getFlowRate();
			if (MsgService.isMsgAvailable()) {
				Msg *msg = MsgService.receiveMsg();
				val = msg->convertToInt();
			}
			if (this->timeCnt >= (TMAX / this->getPeriod()) || val <= 0) {
				this->timeCnt = 0;
				updateTaskState(AM4);
			} else if (this->timeCnt < (TMAX / this->getPeriod())) {
				updateTaskState(CNT);
			} 
			break;
		}
		case AM3:
			//MsgService.sendMsg("Segnalazione");
			updateTaskState(AM4);
			break;
		case AM4:
			GreenHouse::setFlowRate(0);
			updateTaskState(AM1);
			break;
		case CNT:
			this->timeCnt++;
			updateTaskState(this->prevTaskState);
			break;
	}
}
