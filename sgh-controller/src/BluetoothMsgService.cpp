#include <Arduino.h>
// #include "BluetoothMsgService.h"
#include "MsgService.h"

#define TX_PIN 2
#define RX_PIN 6

BluetoothMsgService MsgServiceBT;

void BluetoothMsgService::init() {
  this->channel = new SoftwareSerial(TX_PIN, RX_PIN);
  this->content.reserve(256);
  this->channel->begin(9600);
  this->content = "";
}

void BluetoothMsgService::sendMsg(Msg msg){
  this->channel->println(msg.getContent());  
}

bool BluetoothMsgService::isMsgAvailable(){
  return this->content != "" || this->channel->available();
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

Msg* BluetoothMsgService::receiveMsg(Pattern& pattern) {
  if (this->content == "") {
    while (this->channel->available()) {
      char ch = (char) this->channel->read();
      if (ch != '\n'){
        this->content += ch;    
      }
    }
  }
  Msg* msg = new Msg(this->content);  
  if (pattern.match(*msg)) {
    msg = &pattern.withoutMatch(*msg);
    this->content = "";
    return msg;
  }
  return NULL;
}