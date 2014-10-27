// Oscilloscope
// Input defined by ANALOG_IN
// SIG_OUT true puts a 2kHz wave on DIGITAL_OUT for testing.

#define ANALOG_IN 0
#define DIGITAL_OUT 13
bool SIG_OUT = false;

void setup() {
  Serial.begin(9600);
  //Serial.begin(1200);
  
  // Generate a signal to examine (testing)
  if(SIG_OUT){
    pinMode(DIGITAL_OUT, OUTPUT);
    
    // initialize timer1 
    noInterrupts();           // disable all interrupts
    TCCR1A = 0;
    TCCR1B = 0;
    TCNT1  = 0;
  
    OCR1A = 31250;            // compare match register 16MHz/256/2Hz
    TCCR1B |= (1 << WGM12);   // CTC mode
    TCCR1B |= (1 << CS12);    // 256 prescaler 
    TIMSK1 |= (1 << OCIE1A);  // enable timer compare interrupt
    interrupts();             // enable all interrupts
  }
}

// Interrupt based
ISR(TIMER1_COMPA_vect){
  digitalWrite(DIGITAL_OUT, digitalRead(DIGITAL_OUT) ^ 1);   // Toggle
}

void loop() {
  int val = analogRead(ANALOG_IN);
  Serial.write( 0xff );
  Serial.write( (val >> 8) & 0xff );
  Serial.write( val & 0xff );
}

