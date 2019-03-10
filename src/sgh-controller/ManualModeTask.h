#ifndef __MANUAL_MODE_TASK__
#define __MANUAL_MODE_TASK__

#include "Task.h"
#include "Light.h"

#define HUMIDITY_TAG "hum"
#define SUPPLY_TAG "sup"
#define PUMP_TAG "pump"

class ManualModeTask : public Task {

public:
	ManualModeTask(int ledPin);
	void tick();

private:
	Light *led;
	enum TaskState {MM0, MM1} currTaskState;
};

#endif
