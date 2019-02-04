#ifndef __MSGSERVICE__
#define __MSGSERVICE__

#include "Arduino.h"

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
	virtual boolean match(const Msg& m) = 0;
};

class MsgServiceClass {

public:
	Msg* currentMsg;
	bool msgAvailable;

	void init();

	bool isMsgAvailable();
	Msg* receiveMsg();

	bool isMsgAvailable(Pattern& pattern);
	Msg* receiveMsg(Pattern& pattern);

	void sendMsg(const String& msg);
};

extern MsgServiceClass MsgService;

#endif

