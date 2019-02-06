#ifndef __AUTO_MODE_TASK__
#define __AUTO_MODE_TASK__

#include "Task.h"
#include "Light.h"
#include "UltraSonicSensor.h"

#define TMAX 5000

enum TaskState {A0, A1, A2, A3, A4, CNT};

class AutoModeTask : public Task {

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
