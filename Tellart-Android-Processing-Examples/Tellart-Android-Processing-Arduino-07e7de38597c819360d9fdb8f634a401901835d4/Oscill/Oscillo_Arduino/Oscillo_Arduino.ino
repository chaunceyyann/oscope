////////////////////////////////////////////////////////////
//NEEDED FOR ALL ARDUINO ADK sketches
////////////////////////////////////////////////////////////
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

long timer = millis(); // counter to track last time we sent values

void setup() {
  usb.powerOn();  // start the connection to the device over the USB host:
}

void loop() {
  if(millis() - timer > 100) { // Has it been over 100ms since last send?
    if (usb.isConnected()) { // isConnected makes sure the USB connection is open
      int analogValue = analogRead(A0) /4;
      
      //analogValue = map(analogValue, 0, 1023, 0, 255);
      timer = millis();
      
      usb.beginTransmission();  
      usb.write(analogValue);
      usb.endTransmission();
    }
  }
}

