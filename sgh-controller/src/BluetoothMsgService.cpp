#include <Arduino.h>
#include "BluetoothMsgService.h"


BluetoothMsgService::BluetoothMsgService(int rxPin, int txPin){
  this->channel = new SoftwareSerial(rxPin, txPin);
}

void BluetoothMsgService::init(){
  this->content.reserve(256);
  this->channel->begin(9600);
}

bool BluetoothMsgService::sendMsg(Msg msg){
  this->channel->println(msg.getContent());  
}

bool BluetoothMsgService::isMsgAvailable(){
  return this->channel->available();
}

Msg* BluetoothMsgService::receiveMsg(){
  while (this->channel->available()) {
    char ch = (char) this->channel->read();
    if (ch == '\n'){
      Msg* msg = new Msg(this->content);  
      this->content = "";
      return msg;    
    } else {
      this->content += ch;      
    }
  }
  return NULL;  
}
