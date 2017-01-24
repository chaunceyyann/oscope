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

int LED1 = 0;
int LED2 = 0;

int LED1Pin = 2;
int LED2Pin = 3;


void setup() {
  Serial.begin( 9600 ); //so we can see the input
  usb.powerOn(); // start the connection to the device over the USB host:
  
  pinMode(LED1Pin, OUTPUT);
  pinMode(LED2Pin, OUTPUT);
}

void loop() {
  if (usb.isConnected()) { // isConnected makes sure the USB connection is ope

    char head = usb.read();

    if(head == 'X'){
      LED1 = usb.read();
    }
    else if(head == 'Y'){
      LED2 = usb.read();
    }
    
    analogWrite(LED1Pin, LED1);
    analogWrite(LED2Pin, LED2);


    delay(10);

  }
}










