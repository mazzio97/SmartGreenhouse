#include <Arduino.h>
#include "GreenHouse.h"
#include "ManualModeTask.h"
#include "MsgService.h"
#include "Led.h"

Pattern humidityPattern("hum");
Pattern supplyPattern2("sup");

ManualModeTask::ManualModeTask(int ledPin) {
	this->led = new Led(ledPin);
	this->currTaskState = MM0;
}

void ManualModeTask::tick() {
	switch (this->currTaskState)
	{
		case MM0:
			if(GreenHouse::checkState(State::MANUAL)) {
				this->currTaskState = MM1;
				this->led->switchOn();
			}
			break;
		case MM1:
			if(GreenHouse::checkState(State::AUTO)) {
				this->currTaskState = MM0;
				this->led->switchOff();
			} else if (MsgServiceBT.isMsgAvailable()) {
				Msg* flow = MsgServiceBT.receiveMsg(supplyPattern2);
				if (flow != NULL) {
					GreenHouse::setFlowRate(flow->convertToInt());
					MsgService.sendMsg("pump" + flow->getContent());
				}
			}
			break;
	}
	
}
