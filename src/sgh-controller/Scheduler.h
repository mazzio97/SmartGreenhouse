#ifndef __SCHEDULER__
#define __SCHEDULER__

#include "Timer.h"
#include "Task.h"

#define MAX_TASKS 10

class Scheduler {

public:
	Scheduler();
	virtual bool addTask(Task* task);
	virtual void schedule();

private:
	int basePeriod;
	int nTasks;
	Task* taskList[MAX_TASKS];
	Timer timer;
};

#endif

