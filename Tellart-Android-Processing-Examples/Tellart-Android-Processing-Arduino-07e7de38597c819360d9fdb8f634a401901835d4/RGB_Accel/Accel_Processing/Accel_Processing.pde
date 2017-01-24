import cc.arduino.*;
ArduinoAdkUsb arduino;

long timer = millis();


void setup() {
  arduino = new ArduinoAdkUsb( this ); //init arduino
  if ( arduino.list() != null ) arduino.connect( arduino.list()[0] ); //if there is an arduino to connect to, do so

  orientation( PORTRAIT );
  noStroke();
  
  size(screenWidth, screenHeight, A2D);
}


void draw() {
  //mag_values[0];
  //mag_values[1];
  //mag_values[2];
  
  //acc_values[0];
  //acc_values[1];
  //acc_values[2];



  //float red = constrain(map(acc_values[0], -10, 10, 0, 255), 0, 255);
  //float green = constrain(map(acc_values[1], -10, 10, 0, 255), 0, 255);
  //float blue = constrain(map(acc_values[2], -10, 10, 0, 255), 0, 255);
  
  println(mag_values[0]);
  
  
  float red = constrain(map(mag_values[0], -60, 35, 0, 255), 0, 255);
  float green = constrain(map(mag_values[1], -60, 35, 0, 255), 0, 255);
  float blue = constrain(map(mag_values[2], -60, 35, 0, 255), 0, 255);
  

  int fillColor = color(red, green, blue);
  fill(fillColor);
  rect(0, 0, screenWidth, screenHeight);
  
   
   if ( arduino.isConnected() ) {
    
    
    if( millis() - timer > 100  ){
      arduino.write('R');
      arduino.write( (byte)red ); 
      
      arduino.write('G');
      arduino.write( (byte)green ); 
      
      arduino.write('B');
      arduino.write( (byte)blue ); 
      
      timer = millis(); //reset timer
    }
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




////////////////////////////////////////////////////////////////////////////////////
// Imports required for sensor usage:
////////////////////////////////////////////////////////////////////////////////////
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
////////////////////////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////////////////////////
// Screen Data:
////////////////////////////////////////////////////////////////////////////////////
String[] fontList;
PFont androidFont;

// Setup variables for the SensorManager, the SensorEventListeners,
// the Sensors, and the arrays to hold the resultant sensor values:
SensorManager mSensorManager;
MySensorEventListener accSensorEventListener;
MySensorEventListener magSensorEventListener;
Sensor acc_sensor;
float[] acc_values;
Sensor mag_sensor;
float[] mag_values;
////////////////////////////////////////////////////////////////////////////////////




//-----------------------------------------------------------------------------------------
// Override the parent (super) Activity class:
// States onCreate(), onStart(), and onStop() aren't called by the sketch.  Processing is entered at
// the 'onResume()' state, and exits at the 'onPause()' state, so just override them:

void onResume() {
  super.onResume();
  println("RESUMED! (Sketch Entered...)");
  // Build our SensorManager:
  mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
  // Build a SensorEventListener for each type of sensor:
  magSensorEventListener = new MySensorEventListener();
  accSensorEventListener = new MySensorEventListener();
  // Get each of our Sensors:
  acc_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
  mag_sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
  // Register the SensorEventListeners with their Sensor, and their SensorManager:
  mSensorManager.registerListener(accSensorEventListener, acc_sensor, SensorManager.SENSOR_DELAY_GAME);
  mSensorManager.registerListener(magSensorEventListener, mag_sensor, SensorManager.SENSOR_DELAY_GAME);
}

void onPause() {
  // Unregister all of our SensorEventListeners upon exit:
  mSensorManager.unregisterListener(accSensorEventListener);
  mSensorManager.unregisterListener(magSensorEventListener);
  println("PAUSED! (Sketch Exited...)");
  super.onPause();
} 

//-----------------------------------------------------------------------------------------

// Setup our SensorEventListener
class MySensorEventListener implements SensorEventListener {
  void onSensorChanged(SensorEvent event) {
    int eventType = event.sensor.getType();
    if (eventType == Sensor.TYPE_ACCELEROMETER) {
      acc_values = event.values;
    }
    else if (eventType == Sensor.TYPE_MAGNETIC_FIELD) {
      mag_values = event.values;
    }
  }
  void onAccuracyChanged(Sensor sensor, int accuracy) {
    // do nuthin'...
  }
}  

