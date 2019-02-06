#include <Arduino.h>
#include "ServoMotorImpl.h"

ServoMotorImpl::ServoMotorImpl(int pin) {
  this->pin = pin;
  this->currAngle = 0; 
} 

void ServoMotorImpl::on(){
  this->motor.attach(pin);    
}

void ServoMotorImpl::setPosition(int angle) {
  // 750 -> 0, 2250 -> 180 
  // 750 + angle*(2250-750)/180
  float coeff = (2250.0-750.0)/180;
  this->motor.write(750 + angle * coeff);      
}

int ServoMotorImpl::getPosition() {
  return this->currAngle;      
}

void ServoMotorImpl::off(){
  this->motor.detach();    
}

void ServoMotorImpl::open() {
  int newAngle = this->currAngle + DELTA;
  this->setPosition(newAngle);
  this->currAngle = newAngle >= 180 ? 180 : newAngle;
	delay(15);
}

void ServoMotorImpl::close() {
  int newAngle = this->currAngle - DELTA;
  this->setPosition(newAngle);
  this->currAngle = newAngle <= 0 ? 0 : newAngle;
	delay(15);
}