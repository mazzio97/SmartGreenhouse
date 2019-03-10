#ifndef __MODE_MANAGER_TASK__
#define __MODE_MANAGER_TASK__

#include "Task.h"
#include "UltraSonicSensor.h"

#define DIST 0.3
#define STATUS_TAG "status"
#define MODE_TAG "mode"

class ModeManagerTask : public Task {

public:
	ModeManagerTask(int echoPin, int triggerPin);
	void tick();

private:
	UltraSonicSensor *us;
	enum TaskState {MM0, MM1, MM2} taskState;
};

#endif
