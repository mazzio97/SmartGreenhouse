#ifndef __MODE_MANAGER_TASK__
#define __MODE_MANAGER_TASK__

#include "Task.h"
#include "UltraSonicSensor.h"
#include "BluetoothMsgService.h"

#define DIST 0.3

class ModeManagerTask : public Task {

public:
	ModeManagerTask(int echoPin, int triggerPin, int txPin, int rxPin, int statusPin);
	void tick();

private:
	UltraSonicSensor *us;
	BluetoothMsgService *btms;
	enum TaskState {MM0, MM1, MM2} taskState;
};

#endif
