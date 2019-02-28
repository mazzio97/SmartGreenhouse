#ifndef __AUTO_MODE_TASK__
#define __AUTO_MODE_TASK__

#include "Task.h"
#include "Light.h"
#include "UltraSonicSensor.h"

#define HUMIDITY_TAG "hum"
#define SUPPLY_TAG "sup"

enum TaskState {AM0, AM1, AM2};

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
