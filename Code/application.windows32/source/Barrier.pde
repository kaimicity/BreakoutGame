class Barrier {
  PVector position ;
  float width ;
  float height ;
  float angle ;
  String status ;
  float moveIncrement ;
  float rightBorder ;
  float totalLine ;
  PGraphics graphic ;

  Barrier(float x, float y, float w, float h, float rightBorder, PGraphics pg) {
    this.position = new PVector(x, y) ;
    this.width = w ;
    this.height = h ;
    this.moveIncrement = width / 6 ;
    status = "PREPARE" ;
    this.rightBorder = rightBorder ;
    this.totalLine = rightBorder * 2 ;
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

  void setAngle(float angle) {
    this.angle = angle ;
  }
  
  float getAngle(){
    return this.angle ;
  }

  void fire() {
    this.status = "FIRE" ;
  }

  void prepare() {
    this.status = "PREPARE" ;
  }

  boolean isFire() {
    if (this.status.equals("FIRE"))
      return true ;
    else 
      return false ;
  }
  
  boolean isPrepare() {
    if (this.status.equals("PREPARE"))
      return true ;
    else 
      return false ;
  }
  void move(float x) {
    if (position.x + x * moveIncrement >= 0 && position.x + width + x * moveIncrement <= rightBorder)
      position.x += x * moveIncrement ;
  }

  void draw() {
    graphic.fill(255, 0, 0) ;
    graphic.rect(position.x, position.y, width, height) ;
    graphic.pushMatrix() ;
    graphic.translate(position.x + width / 2, position.y + height) ;
    graphic.rotate(angle) ;
    if (status.equals("PREPARE")) {
      graphic.strokeWeight(height / 10) ;
      for (float i = 0; i < totalLine; i+= totalLine / 50) {
        graphic.line(0, i, 0, i + totalLine / 100) ;
      }
    }
    graphic.popMatrix() ;
  }
}
