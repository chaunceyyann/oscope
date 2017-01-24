import cc.arduino.*;
ArduinoAdkUsb arduino;

int sliderValue;
long timer = millis();
HScrollbar slider1;


void setup() {
  arduino = new ArduinoAdkUsb( this ); //init arduino
  if ( arduino.list() != null ) arduino.connect( arduino.list()[0] ); //if there is an arduino to connect to, do so

  orientation( PORTRAIT );
  noStroke();
  slider1 = new HScrollbar(0, 50, width, 50, 1);
}

void draw() {


  if ( arduino.isConnected() ) {
    
    
    if( millis() - timer > 100  ){
      sliderValue = slider1.getValue();
      arduino.write( (byte)sliderValue ); 
      timer = millis();
    }
  }

  connected();
  slider1.update();
  slider1.display();
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



class HScrollbar {
  int swidth, sheight, value;    // width and height of bar
  int xpos, ypos;         // x and y position of bar
  float spos, newspos;    // x position of slider
  int sposMin, sposMax;   // max and min values of slider
  int loose;              // how loose/heavy
  boolean over;           // is the mouse over the slider?
  boolean locked;
  float ratio;

  HScrollbar (int xp, int yp, int sw, int sh, int l) {
    swidth = sw;
    sheight = sh;
    int widthtoheight = sw - sh;
    ratio = (float)sw / (float)widthtoheight;
    xpos = xp;
    ypos = yp-sheight/2;
    spos = xpos + swidth/2 - sheight/2;
    newspos = spos;
    sposMin = xpos;
    sposMax = xpos + swidth - sheight;
    loose = l;
  }

  void update() {
    if (over()) {
      over = true;
    } 
    else {
      over = false;
    }
    if (mousePressed && over) {
      locked = true;
    }
    if (!mousePressed) {
      locked = false;
    }
    if (locked) {
      newspos = constrain(mouseX-sheight/2, sposMin, sposMax);
    }
    if (abs(newspos - spos) > 1) {
      spos = spos + (newspos-spos)/loose;
    }
  }

  int constrain(int val, int minv, int maxv) {
    return min(max(val, minv), maxv);
  }

  boolean over() {
    if (mouseX > xpos && mouseX < xpos+swidth &&
      mouseY > ypos && mouseY < ypos+sheight) {
      return true;
    } 
    else {
      return false;
    }
  }

  void display() {
    fill(255);
    rect(xpos, ypos, swidth, sheight);
    if (over || locked) {
      fill(153, 102, 0);
    } 
    else {
      fill(102, 102, 102);
    }
    rect(spos, ypos, sheight, sheight);
  }

  float getPos() {
    // Convert spos to be values between
    // 0 and the total width of the scrollbar
    return spos * ratio;
  }

  int getValue() {
    int value = int(((spos * ratio) /width) * 255);
    return constrain(value, 0, 255);
  }
}

