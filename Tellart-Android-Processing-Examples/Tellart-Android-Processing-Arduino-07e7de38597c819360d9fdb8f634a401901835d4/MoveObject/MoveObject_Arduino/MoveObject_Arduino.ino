////////////////////////////////////////////////////////////
//NEEDED FOR ALL ARDUINO ADK sketches
////////////////////////////////////////////////////////////
#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

// accessory descriptor. It's how Arduino identifies itself to Android
char applicationName[] = "Mega_ADK"; // the app on your phone
char accessoryName[] = "Mega_ADK"; // your Arduino board
char companyName[] = "Arduino SA";

// make up anything you want for these
char versionNumber[] = "1.0";
char serialNumber[] = "1";
char url[] = "http://labs.arduino.cc/adk/"; // the URL of your app online


//initialize the accessory:
AndroidAccessory usb(companyName, applicationName,
accessoryName,versionNumber,url,serialNumber);
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////



int xPin = A0; //analog 0
int yPin = A1; //analog 1

int xVal;
int yVal;

long timer = millis(); // counter to track last time we sent values


void setup() {
  usb.powerOn();  // start the connection to the device over the USB host:
  Serial.begin(9600);
}

void loop() {
  // Read the RGB Pots
  xVal = analogRead(xPin) /4;
  yVal = analogRead(yPin) /4;


  // Print to usb 
  if(millis() - timer > 100) { // Has it been over 100ms since last send?
    if (usb.isConnected()) { // isConnected makes sure the USB connection is open
      usb.beginTransmission();
      
      usb.write('X'); //SEND X to mark the X head
      usb.write(255 - xVal);
      
      usb.write('Y');
      usb.write(yVal);
      
      Serial.print(xVal);
      Serial.print(" | ");
      Serial.println(yVal);

      usb.endTransmission();
    }
    timer = millis(); //reset the timer
  }
}

