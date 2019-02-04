#include "GreenHouse.h"

State GreenHouse::state = AUTO;
unsigned int GreenHouse::waterFlowRate = 0;

bool GreenHouse::checkState(State s) {
	return state == s;
}

void GreenHouse::changeState(State s) {
	state = s;
}

void GreenHouse::setFlowRate(unsigned int rate) {
	waterFlowRate = rate;
}

unsigned int GreenHouse::getFlowRate() {
	return waterFlowRate;
}

void GreenHouse::resetFlowRate() {
	setFlowRate(0);
}