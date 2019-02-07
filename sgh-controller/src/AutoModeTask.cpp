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
			int flowRate = 0;
			if (MsgService.isMsgAvailable()) {
				flowRate = MsgService.receiveMsg()->convertToInt();
			}
			if (GreenHouse::checkState(MANUAL)) {
				updateTaskState(AM0);
			} else if (flowRate > 0) {
				GreenHouse::setFlowRate(flowRate);
				updateTaskState(AM2);
			}
			break;
		}
		case AM2: {
			if (MsgService.isMsgAvailable() && MsgService.receiveMsg()->convertToInt() <= 0) {
				updateTaskState(AM3);
			}
			break;
		}
		case AM3:
			GreenHouse::setFlowRate(0);
			updateTaskState(AM1);
			break;
	}
}
