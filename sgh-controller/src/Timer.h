#ifndef __TIMER__
#define __TIMER__

class Timer {

public:
	Timer();
	void setupPeriod(int period); // period in ms
	void waitForNextTick();
};

#endif

