import cc.arduino.*;
ArduinoAdkUsb arduino;

ColorPicker colorPicker;

long timer = millis();


void setup() {
  arduino = new ArduinoAdkUsb( this ); //init arduino
  if ( arduino.list() != null ) arduino.connect( arduino.list()[0] ); //if there is an arduino to connect to, do so

  orientation( PORTRAIT );
  noStroke();

  colorPicker = new ColorPicker( 0, 0, width,  height, 255 );
}

void draw () {
  colorPicker.render();


  if ( arduino.isConnected() ) {
    if ( millis() - timer > 100  ) {
      arduino.write('R');
      arduino.write( (byte)colorPicker.getRed() ); 

      arduino.write('G');
      arduino.write( (byte)colorPicker.getGreen() ); 

      arduino.write('B');
      arduino.write( (byte)colorPicker.getBlue() ); 

      timer = millis(); //reset timer
    }
  }
  
    connected();

  
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


////////////////////////////////////////////////////////////////////////////////////////////////////////////
// modified code from http://www.julapy.com/processing/ColorPicker.pde
////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class ColorPicker 
{
  int x, y, w, h, c;
  PImage cpImage;
  
   float redVal = 0;
   float greenVal = 0;
   float blueVal = 0;
	
  public ColorPicker ( int x, int y, int w, int h, int c )
  {
    
    h -= 40;
    
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.c = c;
    
    cpImage = new PImage( w, h );
    init();
  }
	
  private void init ()
  {
    // draw color.
    int cw = w;
    for( int i=0; i<cw; i++ ) 
    {
      float nColorPercent = i / (float)cw;
      float rad = (-360 * nColorPercent) * (PI / 180);
      int nR = (int)(cos(rad) * 127 + 128) << 16;
      int nG = (int)(cos(rad + 2 * PI / 3) * 127 + 128) << 8;
      int nB = (int)(Math.cos(rad + 4 * PI / 3) * 127 + 128);
      int nColor = nR | nG | nB;
			
      setGradient( i, 0, 1, h/2, 0xFFFFFF, nColor );
      setGradient( i, (h/2), 1, h/2, nColor, 0x000000 );
    }
		
  }

  private void setGradient(int x, int y, float w, float h, int c1, int c2 ){
    float deltaR = red(c2) - red(c1);
    float deltaG = green(c2) - green(c1);
    float deltaB = blue(c2) - blue(c1);

    for (int j = y; j<(y+h); j++)
    {
      int c = color( red(c1)+(j-y)*(deltaR/h), green(c1)+(j-y)*(deltaG/h), blue(c1)+(j-y)*(deltaB/h) );
      cpImage.set( x, j, c );
    }
  }
	
  private void drawRect( int rx, int ry, int rw, int rh, int rc )
  {
    for(int i=rx; i<rx+rw; i++) 
    {
      for(int j=ry; j<ry+rh; j++) 
      {
        cpImage.set( i, j, rc );
      }
    }
  }
  
  public int getRed(){
    return int(redVal);
  }
  
  public int getGreen(){
    return int(greenVal);
  }
  
  public int getBlue(){
    return int(blueVal);
  }
  	
  public void render ()
  {
    

    
    image( cpImage, x, y );
    if( mousePressed &&
	mouseX >= x && 
	mouseX < x + w &&
	mouseY >= y &&
	mouseY < y + h )
    {
      redVal = red(get( mouseX, mouseY ));
      greenVal = green(get( mouseX, mouseY ));
      blueVal = blue(get( mouseX, mouseY ));
    }
    
    int testcolor = color(redVal, greenVal, blueVal);
    
    fill( testcolor );
    rect( 0, height - 40, width, 40 );
  }
}
