import cc.arduino.*;

ArduinoAdkUsb arduino;

int rectcolor = color(0, 0, 0);

void setup() {
  arduino = new ArduinoAdkUsb( this );

  if ( arduino.list() != null )
    arduino.connect( arduino.list()[0] );

  /* Lock PORTRAIT view */
  orientation( PORTRAIT );

  rectMode( CENTER );
}

void draw() {
  if ( arduino.isConnected() ) {
    if ( arduino.available() > 0 ) { 
     
     
      int redVal = arduino.readByte() & 0xFF; // 0-255
      int greenVal = arduino.readByte() & 0xFF; // 0-255
      int blueVal = arduino.readByte() & 0xFF; // 0-255
      
      
      
      rectcolor = color(redVal, greenVal, blueVal);
      
    }

    fill( rectcolor );
    rect( sketchWidth()/2, sketchHeight()/2, 300, 300 );
  }

  /* Just draws a red/green rect in the corner based on the state of the connection */
  connected( arduino.isConnected() );
}

void onStop() {
  finish();
}

void connected( boolean state ) {
  pushMatrix();
  translate( 20, 20 );
  if ( state )
    fill( 0, 255, 0 );
  else
    fill( 255, 0, 0 );
  rect( 0, 0, 30, 30 );
  popMatrix();
}
