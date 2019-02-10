#include <Arduino.h>
#include "BluetoothMsgService.h"


BluetoothMsgService::BluetoothMsgService(int txPin, int rxPin, int statusPin){
  this->channel = new SoftwareSerial(txPin, rxPin);
  this->statusPin = statusPin;
}

void BluetoothMsgService::init(){
  this->content.reserve(256);
  this->channel->begin(9600);
}

void BluetoothMsgService::sendMsg(BTMsg msg){
  this->channel->println(msg.getContent());  
}

bool BluetoothMsgService::isMsgAvailable(){
  return this->channel->available();
}

BTMsg* BluetoothMsgService::receiveMsg(){
  while (this->channel->available()) {
    char ch = (char) this->channel->read();
    if (ch == '\n'){
      BTMsg* msg = new BTMsg(this->content);  
      this->content = "";
      return msg;    
    } else {
      this->content += ch;      
    }
  }
  return NULL;  
}

bool BluetoothMsgService::isConnected() {
  return digitalRead(this->statusPin) == HIGH;
}
