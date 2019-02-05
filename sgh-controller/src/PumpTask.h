#ifndef __PUMP_TASK__
#define __PUMP_TASK__

#include "Task.h"
#include "Light.h"
#include "ServoMotor.h"
#include "UltraSonicSensor.h"

class PumpTask : public Task {

public:
	PumpTask(int ledPin, int servoPin);
	void tick();

private:
	Light *led;
	ServoMotor *servo;
	enum TaskState {P0, P1, P2} taskState;
};

#endif
