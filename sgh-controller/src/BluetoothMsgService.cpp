#include <Arduino.h>
// #include "BluetoothMsgService.h"
#include "MsgService.h"

#define TX_PIN 2
#define RX_PIN 6

BluetoothMsgService MsgServiceBT;

// BluetoothMsgService::BluetoothMsgService(int txPin, int rxPin){
  
// }

void BluetoothMsgService::init() {
  this->channel = new SoftwareSerial(TX_PIN, RX_PIN);
  this->content.reserve(256);
  this->channel->begin(9600);
}

void BluetoothMsgService::sendMsg(Msg msg){
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

Msg* BluetoothMsgService::receiveMsg(Pattern& pattern) {
  while (this->channel->available()) {
    char ch = (char) this->channel->read();
    if (ch == '\n'){
      Msg* msg = new Msg(this->content);  
      this->content = "";
      return &(pattern.withoutMatch(*msg));    
    } else {
      this->content += ch;      
    }
  }
  return NULL;
}