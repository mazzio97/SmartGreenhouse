#include <Arduino.h>
#include <avr/sleep.h>
#include <avr/power.h>
#include "Scheduler.h"

int gcd(int a, int b) {
	return (a == 0) ? b : gcd(b % a, a);
}

Scheduler::Scheduler() {
	nTasks = 0;
	basePeriod = 0;
}

bool Scheduler::addTask(Task* task) {
	if (nTasks == MAX_TASKS) {
		return false;
	}
	int curr = 0;
	while (curr < nTasks && task->getPeriod() > taskList[curr]->getPeriod()) {
		curr++;
	}
	nTasks++;
	for (int i = nTasks; i>curr; i--) {
		taskList[i] = taskList[i-1];
	}
	taskList[curr] = task;
	basePeriod = gcd(basePeriod, task->getPeriod());
	timer.setupPeriod(basePeriod);
	return true;
}

void Scheduler::schedule() {
	timer.waitForNextTick();
	for (int i = 0; i < nTasks; i++) {
		if (taskList[i]->updateAndCheckTime(basePeriod)) {
		taskList[i]->tick();
		}
	}
}
