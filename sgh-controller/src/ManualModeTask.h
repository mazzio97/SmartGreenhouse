#ifndef __MANUAL_MODE_TASK__
#define __MANUAL_MODE_TASK__

#include "Task.h"
#include "Light.h"
#include "UltraSonicSensor.h"

class ManualModeTask : public Task {

public:
	ManualModeTask(int ledPin, int echoPin, int triggerPin);
	void tick();

private:
	Light *led;
	UltraSonicSensor *us;
	enum TaskState {MM0, MM1, MM2, MM3} currTaskState;
};

#endif
