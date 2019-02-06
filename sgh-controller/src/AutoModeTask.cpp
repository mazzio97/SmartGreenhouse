#include <Arduino.h>
#include "AutoModeTask.h"
#include "MsgService.h"
#include "GreenHouse.h"
#include "Led.h"

AutoModeTask::AutoModeTask(int ledPin) {
	this->led = new Led(ledPin);
	this->currTaskState = A0;
}

void AutoModeTask::updateTaskState(TaskState s) {
	this->prevTaskState = this->currTaskState;
	this->currTaskState = s;
}

void AutoModeTask::tick() {
		switch (this->currTaskState) {
		case A0:
			if (this->led->isOn()) {
				this->led->switchOff();
			}
			if (GreenHouse::checkState(AUTO)) {
				this->led->switchOn();
				this->timeCnt = 0;
				updateTaskState(A1);
			}
			break;
		case A1:
			if (GreenHouse::checkState(MANUAL)) {
				updateTaskState(A0);
			} else if (MsgService.isMsgAvailable()) {
				Msg *msg = MsgService.receiveMsg();
				GreenHouse::setFlowRate(msg->convertToInt());
				updateTaskState(A2);
			}
			break;
		case A2:
			if (this->timeCnt < TMAX / this->getPeriod()) {
				updateTaskState(CNT);
			} else if (MsgService.isMsgAvailable() || GreenHouse::checkState(MANUAL)) {
				updateTaskState(A4);
			} else if (this->timeCnt >= TMAX) {
				updateTaskState(A3);
			}
			
			break;
		case A3:
			MsgService.sendMsg("Segnalazione");
			updateTaskState(A4);
			break;
		case A4:
			GreenHouse::setFlowRate(0);
			updateTaskState(A1);
			break;
		case CNT:
			this->timeCnt++;
			updateTaskState(A2);
			break;
	}
}
