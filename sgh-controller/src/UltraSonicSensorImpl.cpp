#include <Arduino.h>
#include "UltraSonicSensorImpl.h"

UltraSonicSensorImpl::UltraSonicSensorImpl(int echoPin, int triggerPin) {
	this->echoPin = echoPin;
	pinMode(this->echoPin, INPUT);
	this->triggerPin = triggerPin;
	pinMode(this->triggerPin, OUTPUT);
}

float UltraSonicSensorImpl::getDistance() {
	digitalWrite(this->triggerPin, LOW);
	delayMicroseconds(3);
	digitalWrite(this->triggerPin, HIGH);
	delayMicroseconds(5);
	digitalWrite(this->triggerPin, LOW);

	float ultraSoundTime = pulseIn(this->echoPin, HIGH);
	float elapsedTime = ultraSoundTime / 1000.0 / 1000.0 / 2;
	return elapsedTime * SOUND_SPEED;
}
