
import com.yourinventit.processing.android.serial.*;

Serial SerialPort;
boolean Toggle;

void setup()    
{
  //println(Serial.list(this));
  
  // this simple initialisation works only when one Serial consumer (e.g. an Arduino)
  // is connected. Serial.list provides a list with all Usb Serial devices attached
  // to this machine.
  SerialPort = new Serial(this, Serial.list(this)[0], 9600);
}

void draw()
{

}

void mousePressed() {
  Toggle = !Toggle;
  SerialPort.write( Toggle?"1":"0");
}