import cc.arduino.*;
ArduinoAdkUsb arduino;


//hold the color values
int rectcolor = color(0, 0, 0);
int redVal; 
int greenVal;
int blueVal;

void setup() {
  arduino = new ArduinoAdkUsb( this ); //init arduino

  if ( arduino.list() != null ) arduino.connect( arduino.list()[0] ); //if there is an arduino to connect to, do so
  
  orientation( PORTRAIT ); //Lock PORTRAIT view
}

void draw() {
  background( 255 );

  if ( arduino.isConnected() ) {
    
    if ( arduino.available() > 0 ) {
      
      
      //read a character from the serial (from arduino)
      char readByte = arduino.readChar();
      
      //If the character is R,G, or B the following character is the value. So read that and store it.

      
      if (readByte == 'R') {
        redVal = arduino.readByte() & 0xFF;
      }else if (readByte == 'G') {
        greenVal = arduino.readByte() & 0xFF;
      }else if (readByte == 'B') {
        blueVal = arduino.readByte() & 0xFF;
      }

      rectcolor = color(redVal, greenVal, blueVal);
    }
    
    fill( rectcolor ); //set fill color to the RGB values
    rect( 0,0, sketchWidth(), sketchHeight() ); //fill screen
  }

  connected();
}

void onStop() {
  finish();
}

void connected() {
  
  if ( arduino.isConnected() ){
    fill( 0, 255, 0 ); //on is green
  }else{
    fill( 255, 0, 0 ); //off is red
  }  
    
  ellipse( 16, 16, 30, 30 );
 
}
