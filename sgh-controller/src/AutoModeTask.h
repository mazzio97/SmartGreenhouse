#ifndef __AUTO_MODE_TASK__
#define __AUTO_MODE_TASK__

#include "Task.h"
#include "Light.h"
#include "UltraSonicSensor.h"

#define TMAX 10000


class AutoModeTask : public Task {
enum TaskState {AM0, AM1, AM2, AM3, AM4, CNT};

public:
	AutoModeTask(int ledPin);
	void updateTaskState(TaskState s);
	void tick();

private:
	Light *led;
	unsigned int timeCnt;
	TaskState prevTaskState;
	TaskState currTaskState;
};

#endif
