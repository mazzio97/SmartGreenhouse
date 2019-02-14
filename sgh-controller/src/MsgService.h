#ifndef __MSGSERVICE__
#define __MSGSERVICE__

#include "Arduino.h"
#include "SoftwareSerial.h"

class Msg {

public:
	Msg(String content) {
		this->content = content;
	}

	String getContent() {
		return content;
	}

	int convertToInt() {
		int res = 0;
		int msgLength = content.length();
		for (int i = msgLength - 1; i >= 0 ; i--) {
			res += ((content.charAt(i) - 48) * pow(10, msgLength - i - 1));
		}
		return res;
	}

private:
	String content;
};

class Pattern {

public:
	Pattern(String key) {
		this->keyword = key;
		this->length = this->keyword.length();
	}

	Msg& withoutMatch(Msg& s) {
		if (match(s)) {
			String content = s.getContent();
			s = Msg(content.substring(length, content.length()));
		}
		return s;
	}

	bool match(Msg& m) {
		return m.getContent().substring(0, length) == keyword;
	}

private:
	String keyword;
	int length;
};

class SerialMsgService {
public:
	void init();

	bool isMsgAvailable();
	Msg* receiveMsg();

	bool isMsgAvailable(Pattern& pattern);
	Msg* receiveMsg(Pattern& pattern);

	void sendMsg(const String& msg);

	Msg* currentMsg;
	bool msgAvailable;
};

class BluetoothMsgService {
    
public:
	void init();

  	bool isMsgAvailable();

  	Msg* receiveMsg();
  	Msg* receiveMsg(Pattern& pattern);

  	void sendMsg(Msg msg);

private:
  	String content;
  	SoftwareSerial* channel;
};

extern SerialMsgService MsgService;
extern BluetoothMsgService MsgServiceBT;

#endif

