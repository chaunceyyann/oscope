////////////////////////////////////////////////////////////////////////
//
// AndroidLedOn
//
// simpel serial in. If a 1 is received via serial, the led is 
// switched on. All other chars will switch of the leds.
//
#define NUMBER_OF_CHANNELS 8
#define PINLED1 13

volatile char lastReceivedCharFromSerialIn = '\0';

void setup() {

  pinMode(PINLED1, OUTPUT);
  Serial.begin(9600);
 
}

void loop() {
  digitalWrite( PINLED1, lastReceivedCharFromSerialIn == '1');
}

void serialEvent() {
  while (Serial.available()) {
    // get the new byte:
    lastReceivedCharFromSerialIn = (char)Serial.read(); 
  }
}
