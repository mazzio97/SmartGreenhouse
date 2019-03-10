#ifndef __PUMP_TASK__
#define __PUMP_TASK__

#include "Task.h"
#include "LightExt.h"
#include "ServoMotor.h"
#include "UltraSonicSensor.h"

class PumpTask : public Task {

public:
	PumpTask(int ledPin, int servoPin);
	void tick();

private:
	LightExt *led;
	ServoMotor *servo;
	enum TaskState {P0, P1, P2} taskState;
};

#endif
