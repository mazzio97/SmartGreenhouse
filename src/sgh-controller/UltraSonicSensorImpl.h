#ifndef __ULTRA_SONIC_SENSOR_IMPL__
#define __ULTRA_SONIC_SENSOR_IMPL__

#include "UltraSonicSensor.h"

#define SOUND_SPEED (331.5 + 0.6 * 20); // 20° estimated

class UltraSonicSensorImpl : public UltraSonicSensor {

public:
	UltraSonicSensorImpl(int echoPin, int triggerPin);
	float getDistance();

private:
	int echoPin;
	int triggerPin;
};

#endif
