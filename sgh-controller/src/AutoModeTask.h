#ifndef __AUTO_MODE_TASK__
#define __AUTO_MODE_TASK__

#include "Task.h"
#include "Light.h"
#include "UltraSonicSensor.h"

enum TaskState {AM0, AM1, AM2, AM3};

class AutoModeTask : public Task {

public:
	AutoModeTask(int ledPin);
	void updateTaskState(TaskState s);
	void tick();

private:
	Light *led;
	TaskState prevTaskState;
	TaskState currTaskState;
};

#endif
