#ifndef __SERVO_MOTOR_IMPL__
#define __SERVO_MOTOR_IMPL__

#include "ServoMotor.h"
#include "ServoTimer2.h"

#define DELTA 4

class ServoMotorImpl: public ServoMotor {

public:
  ServoMotorImpl(int pin);

  void on();
  void setPosition(int angle);
  int getPosition();
  void off();
  void open();
  void close();
    
private:
  int pin; 
  int currAngle;
  bool dir;
  ServoTimer2 motor; 
};

#endif