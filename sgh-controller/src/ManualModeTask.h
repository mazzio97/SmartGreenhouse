#ifndef __MANUAL_MODE_TASK__
#define __MANUAL_MODE_TASK__

#include "Task.h"
#include "Light.h"
#include "UltraSonicSensor.h"

class ManualModeTask : public Task {

public:
	ManualModeTask(int ledPin);
	void tick();

private:
	Light *led;
};

#endif
