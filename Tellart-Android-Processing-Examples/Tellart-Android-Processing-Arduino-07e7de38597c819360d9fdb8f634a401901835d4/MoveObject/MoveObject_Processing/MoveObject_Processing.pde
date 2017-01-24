import cc.arduino.*;
ArduinoAdkUsb arduino;


//hold the color values
int posX = 0;
int posY = 0; 

void setup() {
  arduino = new ArduinoAdkUsb( this ); //init arduino
  if ( arduino.list() != null ) arduino.connect( arduino.list()[0] ); //if there is an arduino to connect to, do so
  
  orientation( PORTRAIT ); //Lock PORTRAIT view
  stroke(0);
  strokeWeight(4);
}

void draw() {
  
  background( 255 ); //clear image
  readPots();

  ellipse(posX, posY, 100, 100);

  connected();
}



void readPots() {
  
  if ( arduino.isConnected() ) {
    
    while ( arduino.available() > 0 ) {
      //read a character from the serial (from arduino)
      char readByte = arduino.readChar();
      
      if (readByte == 'X') {
        int tempX = arduino.readByte() & 0xFF; 
        posX = int( map(tempX, 0, 255, 0, width) ); 
      }else if (readByte == 'Y') {
        int tempY = arduino.readByte() & 0xFF; 
        posY = int( map(tempY, 0, 255, 0, height) ); 
      }
      
      
    }
    
  }

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
