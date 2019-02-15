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
	Pattern supplyPattern(SUPPLY_TAG);
	Pattern humidityPattern(HUMIDITY_TAG);

	switch (this->currTaskState) {
		case AM0:
			if (GreenHouse::checkState(AUTO)) {
				this->led->switchOn();
				updateTaskState(AM1);
				if (MsgService.isMsgAvailable()) {
					MsgService.receiveMsg();
				}
			}
			break;
		case AM1:
			if (GreenHouse::checkState(MANUAL)) {
				this->led->switchOff();
				updateTaskState(AM0);
			} else if (MsgService.isMsgAvailable()) {
				int flowRate = 0;
				if (MsgService.isMsgAvailable(humidityPattern)) {
					MsgService.receiveMsg();
				} else {
					flowRate = MsgService.receiveMsg(supplyPattern)->convertToInt();
				}
				if (flowRate > 0) {
					GreenHouse::setFlowRate(flowRate);
					updateTaskState(AM2);
				}
			}
			break;
		case AM2:
			if (GreenHouse::checkState(MANUAL) 
				|| (MsgService.isMsgAvailable(supplyPattern) 
					&& MsgService.receiveMsg(supplyPattern)->convertToInt() == 0)) {
				updateTaskState(AM3);
			}
			break;
		case AM3:
			GreenHouse::setFlowRate(0);
			updateTaskState(AM1);
			break;
	}
}
