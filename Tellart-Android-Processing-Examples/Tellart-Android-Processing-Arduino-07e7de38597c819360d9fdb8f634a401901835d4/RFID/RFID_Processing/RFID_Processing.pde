import cc.arduino.*;

ArduinoAdkUsb arduino;

int rectcolor = color(0, 0, 0);

void setup() {
  arduino = new ArduinoAdkUsb( this );

  if ( arduino.list() != null )
    arduino.connect( arduino.list()[0] );

  /* Lock PORTRAIT view */
  orientation( PORTRAIT );
}

void draw() {
  if ( arduino.isConnected() ) {
    if ( arduino.available() > 0 ) {  

      char arduinoByte = arduino.readChar();


      if ( arduinoByte == 'a' ) {
        rectcolor = color( 255, 0, 0 );
      }
      else if (arduinoByte == 'b') {
        rectcolor = color( 0, 255, 0 );
      }
      else if (arduinoByte == '3') {
        rectcolor = color( 0, 0, 255 );
      }
      else if (arduinoByte == 'c') {
        rectcolor = color( 255, 255, 0 );
      }
      else if (arduinoByte == 'd') {
        rectcolor = color( 0, 255, 255 );
      }
    }

    fill( rectcolor );
    rect( 0, 0, sketchWidth(), sketchHeight() ); //fill screen
  }

  
  connected();
}

void onStop() {
  finish();
}

void connected() {
/* Just draws a red/green rect in the corner based on the state of the connection */

  if ( arduino.isConnected() ) {
    fill( 0, 255, 0 ); //on is green
  }
  else {
    fill( 255, 0, 0 ); //off is red
  }  

  ellipse( 16, 16, 30, 30 );
}

