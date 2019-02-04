#ifndef __PUMP_TASK__
#define __PUMP_TASK__

#include "Task.h"
#include "Light.h"
#include "UltraSonicSensor.h"

class PumpTask : public Task {

public:
	PumpTask(int ledPin);
	void tick();

private:
	Light *led;
};

#endif
