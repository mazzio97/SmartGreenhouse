#ifndef __GREEN_HOUSE__
#define __GREEN_HOUSE__

enum State { AUTO, MANUAL };

class GreenHouse {

public:
	static bool checkState(State s);
	static void changeState(State s);
	static void setFlowRate(unsigned int rate);
	static unsigned int getFlowRate();
	static void resetFlowRate();

private:
	GreenHouse();
	static State state;
	static unsigned int waterFlowRate;
};

#endif
