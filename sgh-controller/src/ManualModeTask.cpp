#include <Arduino.h>
#include "ManualModeTask.h"
#include "MsgService.h"
#include "Led.h"
#include "UltraSonicSensorImpl.h"

ManualModeTask::ManualModeTask(int ledPin, int echoPin, int triggerPin) {
	this->led = new Led(ledPin);
	this->us = new UltraSonicSensorImpl(echoPin, triggerPin);
	this->currTaskState = MM0;
}

void ManualModeTask::tick() {
	switch (this->currTaskState)
	{
		case MM0:
		
			break;
		case MM1:
	
			break;
		case MM2:
			
			break;
		case MM3:
			
			break;
		default:
			break;
	}
	
}
