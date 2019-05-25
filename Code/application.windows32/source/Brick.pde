class Brick{ 
  PVector position ;
  float width ;
  float height ;
  PGraphics graphic ;
  
  Brick(float x, float y, float w, PGraphics pg){
    this.position = new PVector(x, y) ;
    this. width = w ;
    this.height = w / 4 ;
    this.graphic = pg ;
  }
  
  float getX() {
    return this.position.x ;
  }

  float getY() {
    return this.position.y ;
  }

  float getWidth() {
    return this.width ;
  }

  float getHeight() {
    return this.height ;
  }
  void draw(){
    graphic.fill(255) ;
    graphic.stroke(127) ;
    graphic.rect(position.x, position.y, width, height) ;
  }
  
}
