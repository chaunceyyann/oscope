import cc.arduino.*;
ArduinoAdkUsb arduino;

long timer = millis();


void setup() {
  arduino = new ArduinoAdkUsb( this ); //init arduino
  if ( arduino.list() != null ) arduino.connect( arduino.list()[0] ); //if there is an arduino to connect to, do so

  size(480, 800); 
  background(255);
  orientation( PORTRAIT ); //Lock PORTRAIT view
}



void draw() {


  int y = (int) map(mouseY, 0, height, 0, 255);
  background(y);

  if ( arduino.isConnected() ) {
    if ( millis() - timer > 100  ) {

      arduino.write( (byte)y ); 

      timer = millis(); //reset timer
    }
  }
  
  connected()
  
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

