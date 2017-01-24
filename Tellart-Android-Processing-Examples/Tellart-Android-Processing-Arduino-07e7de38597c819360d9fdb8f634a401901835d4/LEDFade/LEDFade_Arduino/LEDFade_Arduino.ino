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

int ledPin = 10;
long timer = millis();


void setup() {
  Serial.begin( 9600 ); //so we can see the input
  usb.powerOn(); // start the connection to the device over the USB host:
  pinMode(ledPin, OUTPUT);   
}

void loop() {
  if (usb.isConnected()) { // isConnected makes sure the USB connection is ope
      int val = usb.read();
      if(val < 0) val += 255; //values seems to come in as 0-128, -128 - 0; This fixes that.

      Serial.println(val);
      analogWrite( ledPin, val );
    
  }
}









