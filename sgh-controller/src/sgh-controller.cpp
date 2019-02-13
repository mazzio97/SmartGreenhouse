/*
 * Nome Progetto: Smart Green House
 * Membri Gruppo: Diego Mazzieri, Milo Marchetti
 */

#include "Arduino.h"
#include "Scheduler.h"
#include "MsgService.h"
#include "ModeManagerTask.h"
#include "PumpTask.h"
#include "AutoModeTask.h"
#include "ManualModeTask.h"

#define SONAR_ECHO_PIN 7
#define SONAR_TRIGGER_PIN 4
#define SERVO_MOTOR_PIN 8
#define LED1_PIN 13
#define LED2_PIN 5
#define LED3_PIN 12

Scheduler sched;

void setup() {
	Serial.begin(9600);

	MsgService.init();
  MsgServiceBT.init();

  Task* modeManagerTask = new ModeManagerTask(SONAR_ECHO_PIN, SONAR_TRIGGER_PIN);
  modeManagerTask->init(100);
  sched.addTask(modeManagerTask);

  Task* pumpTask = new PumpTask(LED2_PIN, SERVO_MOTOR_PIN);
  pumpTask->init(100);
  sched.addTask(pumpTask);

	Task* autoModeTask = new AutoModeTask(LED1_PIN);
	autoModeTask->init(100);
	sched.addTask(autoModeTask);

  Task* manualModeTask = new ManualModeTask(LED3_PIN);
  manualModeTask->init(100);
  sched.addTask(manualModeTask);
}

void loop() {
	sched.schedule();
}
