#include "Led.h"
#include "Arduino.h"

Led::Led(int pin) {
	this->pin = pin;
	pinMode(this->pin,OUTPUT);
}

void Led::switchOn() {
	digitalWrite(this->pin, HIGH);
}

void Led::switchOff() {
	digitalWrite(this->pin, LOW);
};

bool Led::isOn() {
	return digitalRead(this->pin) == HIGH;
}

int Led::getPin() {
	return this->pin;
}
