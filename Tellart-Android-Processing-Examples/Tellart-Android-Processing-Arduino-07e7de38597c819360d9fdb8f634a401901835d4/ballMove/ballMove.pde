
int pos = 0;
boolean direction = true;
int speed = 10;

void setup() {
  background(255);
  noFill();
  strokeWeight(4);
  orientation( PORTRAIT ); //Lock PORTRAIT view

}

void draw() {
  background(speed * 4);  
  stroke(255- (speed * 4));
  
  
  if (direction) {
    pos += speed;
  }
  else {
    pos -= speed;
  }

  //if falls off the side, revers the direction 
  if (pos > width || pos < 0) direction = !direction;
 
  
  //adjust speed based on where you touch
  if(mousePressed) speed = (int)map(mouseY, 0, height, 0, 64);
 

  ellipse(pos, height/2, 100, 100);
}

