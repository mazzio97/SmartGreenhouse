#include "LedExt.h"
#include "Arduino.h"
#include "MsgService.h"

LedExt::LedExt(int pin) : Led(pin) {
  currentIntensity = 128;
  on = false;
}

LedExt::LedExt(int pin, int intensity) : Led(pin) {
  on = false;
  currentIntensity = intensity;
}

void LedExt::switchOn(){
  analogWrite(pin,currentIntensity);
  on = true;
}

void LedExt::setIntensity(int value){
 currentIntensity = value;  
 if (on){
   analogWrite(pin,currentIntensity);   
 }
}

void LedExt::switchOff(){
  analogWrite(pin,0);
  on = false;
}

bool LedExt::isOn(){
  return on;
}

