#ifndef __BLUETOOTH_MSGSERVICE__
#define __BLUETOOTH_MSGSERVICE__

#include "Arduino.h"
#include "SoftwareSerial.h"

class BTMsg {
  String content;

public:
  BTMsg(const String& content){
    this->content = content;
  }
  
  String getContent(){
    return content;
  }
};

class BluetoothMsgService {
    
public: 
  BluetoothMsgService(int rxPin, int txPin, int statusPin);  
  void init();  
  bool isMsgAvailable();
  BTMsg* receiveMsg();
  void sendMsg(BTMsg msg);
  bool isConnected();

private:
  int statusPin;
  String content;
  SoftwareSerial* channel;
};

#endif
