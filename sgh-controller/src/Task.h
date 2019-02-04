#ifndef __TASK__
#define __TASK__

class Task {

public:
	virtual void init(unsigned int period) {
		myPeriod = period;
		timeElapsed = 0;
	}

	virtual void tick() = 0;

	bool updateAndCheckTime(int basePeriod) {
		timeElapsed += basePeriod;
		if (timeElapsed >= myPeriod){
		timeElapsed = 0;
		return true;
		}
		return false;
	}

	unsigned int getPeriod() {
		return myPeriod;
	}

private:
	unsigned int myPeriod;
	unsigned int timeElapsed;
};

#endif
