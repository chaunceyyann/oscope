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
char url[] = "http://labs.arduino.cc/adk/ADK_count"; // the URL of your app online

// initialize the accessory:
AndroidAccessory usb(companyName, applicationName,
accessoryName,versionNumber,url,serialNumber);

int LEDpin = 10;
int RFIDResetPin = 13;

//Register your RFID tags here
char tag1[13] = "0102ACBD9381";
char tag2[13] = "010230F28243";
char tag3[13] = "0102ACB7B1A9";
char tag4[13] = "01023C072C14";
char tag5[13] = "01023BFFB077";


void setup(){
  pinMode(RFIDResetPin, OUTPUT);
  pinMode(LEDpin, OUTPUT);
  
  
  digitalWrite(RFIDResetPin, HIGH);
  //Serial.begin(9600); //USB
  Serial1.begin(9600); //RFID
  usb.powerOn();
}

void loop(){
  
  if (usb.isConnected()) {
    readRFID();
  }
}

void readRFID(){
  char tagString[13];
  int index = 0;
  boolean reading = false;

  while(Serial1.available()){

    int readByte = Serial1.read(); //read next available byte

    if(readByte == 2) reading = true; //begining of tag
    if(readByte == 3) reading = false; //end of tag

    if(reading && readByte != 2 && readByte != 10 && readByte != 13){
      //store the tag
      tagString[index] = readByte;
      index ++;
    }
  }

  checkTag(tagString); //Check if it is a match
  clearTag(tagString); //Clear the char of all value
  resetReader(); //reset the RFID reader
}

void checkTag(char tag[]){
///////////////////////////////////
//Check the read tag against known tags
///////////////////////////////////

  if(strlen(tag) == 0) return; //empty, no need to contunue
  
  //Serial.println(tag);
  
  if(compareTag(tag, tag1)){ // if matched tag1, do this
    sendTag(1);

  }else if(compareTag(tag, tag2)){ //if matched tag2, do this
    sendTag(2);

  }else if(compareTag(tag, tag3)){
    sendTag(3);

  }else if(compareTag(tag, tag4)){
    sendTag(4);

  }else if(compareTag(tag, tag5)){
    sendTag(5);
  }else{
    //Serial1.println(tag); //read out any unknown tag
  }

}

void sendTag(int tag){
///////////////////////////////////
//Turn on LED on pin "pin" for 250ms
///////////////////////////////////
  Serial.println(tag);
  
  
  usb.beginTransmission();  
  usb.write(tag);
  usb.endTransmission();

  digitalWrite(LEDpin, HIGH);
  delay(100);
  digitalWrite(LEDpin, LOW);

}

void resetReader(){
///////////////////////////////////
//Reset the RFID reader to read again.
///////////////////////////////////
  digitalWrite(RFIDResetPin, LOW);
  digitalWrite(RFIDResetPin, HIGH);
  delay(150);
}

void clearTag(char one[]){
///////////////////////////////////
//clear the char array by filling with null - ASCII 0
//Will think same tag has been read otherwise
///////////////////////////////////
  for(int i = 0; i < strlen(one); i++){
    one[i] = 0;
  }
}

boolean compareTag(char one[], char two[]){
///////////////////////////////////
//compare two value to see if same,
//strcmp not working 100% so we do this
///////////////////////////////////

  if(strlen(one) == 0) return false; //empty

  for(int i = 0; i < 12; i++){
    if(one[i] != two[i]) return false;
  }

  return true; //no mismatches
}
