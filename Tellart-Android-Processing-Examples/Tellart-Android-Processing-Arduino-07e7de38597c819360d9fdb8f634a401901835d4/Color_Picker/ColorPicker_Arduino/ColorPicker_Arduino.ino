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

int red = 0;
int green = 0;
int blue = 0;

int redPin = 2;
int greenPin = 3;
int bluePin = 4;


void setup() {
  Serial.begin( 9600 ); //so we can see the input
  usb.powerOn(); // start the connection to the device over the USB host:
  
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);
}

void loop() {
  if (usb.isConnected()) { // isConnected makes sure the USB connection is ope

    char head = usb.read();

    if(head == 'R'){
      red = usb.read();
    }
    else if(head == 'G'){
      green = usb.read();
    }
    else if(head == 'B'){
      blue = usb.read();
    }
    
    
    analogWrite(redPin, red);
    analogWrite(greenPin, green);
    analogWrite(bluePin, blue);

    delay(10);

  }
}










