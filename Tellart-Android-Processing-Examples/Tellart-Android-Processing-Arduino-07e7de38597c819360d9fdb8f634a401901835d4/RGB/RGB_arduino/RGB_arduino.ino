////////////////////////////////////////////////////////////
//NEEDED FOR ALL ARDUINO ADK sketches
////////////////////////////////////////////////////////////
#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

// accessory descriptor. It's how Arduino identifies itself to Android
char applicationName[] = "Mega_ADK"; // the app on your phone
char accessoryName[] = "Mega_ADK"; // your Arduino board
char companyName[] = "Freeware";

// make up anything you want for these
char versionNumber[] = "1.0";
char serialNumber[] = "1";
char url[] = "http://labs.arduino.cc/adk/"; // the URL of your app online


//initialize the accessory:
AndroidAccessory usb(companyName, applicationName,
accessoryName,versionNumber,url,serialNumber);
////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////




// button variables
int redPin = A0; //analog 0
int greenPin = A1; //analog 1
int bluePin = A2; //analog 2
int redVal;
int greenVal;
int blueVal;

long timer = millis(); // counter to track last time we sent values


void setup() {
  usb.powerOn();  // start the connection to the device over the USB host:
}

void loop() {
  // Read the RGB Pots
  redVal = analogRead(redPin) /4;
  greenVal = analogRead(greenPin) /4;
  blueVal = analogRead(bluePin) /4;


  // Print to usb 
  if(millis() - timer > 100) { // Has it been over 100ms since last send?
    if (usb.isConnected()) { // isConnected makes sure the USB connection is open
      usb.beginTransmission();
      
      usb.write('R'); //SEND R for red, and the red value
      usb.write(redVal);
      
      usb.write('G');
      usb.write(greenVal);
      
      usb.write('B');
      usb.write(blueVal);
      
      usb.endTransmission();
    }
    timer = millis(); //reset the timer
  }
}

