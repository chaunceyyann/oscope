import cc.arduino.*;

ArduinoAdkUsb arduino;

int rectcolor = color(0, 0, 0);
int redVal;
int greenVal;
int blueVal;

void setup() {
  arduino = new ArduinoAdkUsb( this );

  if ( arduino.list() != null ){
    arduino.connect( arduino.list()[0] );
  }
  
  orientation( PORTRAIT ); /* Lock PORTRAIT view */
  rectMode( CENTER );
}

void draw() {
  background( 255 );

  if ( arduino.isConnected() ) {
    
    if ( arduino.available() > 0 ) {
      
      
      char readByte = arduino.readChar();

      if (readByte == 'R') {
        redVal = arduino.readByte() & 0xFF;
      }
      else if (readByte == 'G') {
        greenVal = arduino.readByte() & 0xFF;
      }
      else if (readByte == 'B') {
        blueVal = arduino.readByte() & 0xFF;
      }

      rectcolor = color(redVal, greenVal, blueVal);
      
      

    }
    
    fill( rectcolor );
    rect( sketchWidth()/2, sketchHeight()/2, sketchWidth(), sketchHeight() );

  }

  connected();
}

void onStop() {
  finish();
}

void connected() {
  pushMatrix();
  
  translate( 20, 20 );
  
  if ( arduino.isConnected() ){
  
    fill( 0, 255, 0 ); //on is green
  }else{
    fill( 255, 0, 0 ); //off is red
  }  
    
  ellipse( 0, 0, 30, 30 );
  popMatrix();
}
