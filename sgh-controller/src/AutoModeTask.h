#ifndef __AUTO_MODE_TASK__
#define __AUTO_MODE_TASK__

#include "Task.h"
#include "Light.h"
#include "UltraSonicSensor.h"

class AutoModeTask : public Task {

public:
	AutoModeTask(int ledPin);
	void tick();

private:
	Light *led;
};

#endif
