import cc.arduino.*;
ArduinoAdkUsb arduino;


int val;      // Data received from the serial port
int[] values;


void setup() {
  arduino = new ArduinoAdkUsb( this ); //init arduino
  if ( arduino.list() != null ) arduino.connect( arduino.list()[0] ); //if there is an arduino to connect to, do so
  orientation( PORTRAIT ); //Lock PORTRAIT view

  values = new int[width];
  smooth();
}

void draw() {
  background(0);
  stroke(255);

  if ( arduino.isConnected() ) {

      if ( arduino.available() > 0 ) {
        int tempVal = arduino.readByte() & 0xFF; 
        val = int( map(tempVal, 0, 255, 0, height) );
      }else{
        
       //val = int(random(0, height));
        
      }
      
      
      
      for (int i=0; i < width-1; i++){
        values[i] = values[i+1]; //shift over
      }
        
      values[width-1] = val;
      
      for (int x = 1; x < width; x++) {
        line(width - x, values[x-1],  width - (x +1), values[x] );
      }

  }

  connected();
}



void onStop() {
  finish();
}

void connected() {

  if ( arduino.isConnected() ) {
    fill( 0, 255, 0 ); //on is green
  }
  else {
    fill( 255, 0, 0 ); //off is red
  }  

  ellipse( 16, 16, 30, 30 );
}




