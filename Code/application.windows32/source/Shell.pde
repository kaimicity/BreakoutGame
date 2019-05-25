class Shell { 
  PVector position ;
  float width ;
  float height ;
  PVector velocity ;
  float moveIncrement ;
  float rightBorder ;
  AudioPlayer player;
  PGraphics graphic ;

  Shell(float x, float y, float w, float h, PVector velocity, float rb, AudioPlayer ap, PGraphics pg) {
    this.position = new PVector(x, y) ;
    this.width = w ;
    this.height = h ;
    this.velocity = velocity ;
    this.rightBorder = rb ;
    this.player = ap ;
    this.graphic = pg ;
  }

  float getX() {
    return this.position.x ;
  }

  float getY() {
    return this.position.y ;
  }

  void setPosition(PVector pst) {
    this.position = pst ;
  }

  float getWidth() {
    return this.width ;
  }

  float getHeight() {
    return this.height ;
  }

  PVector getVelocity() {
    return this.velocity ;
  }

  void setVelocity(PVector v) {
    this.velocity = v ;
  }

  void draw() {
    graphic.fill(0, 255, 0) ;
    graphic.ellipse(position.x, position.y, width, height) ;
  }

  void move(Barrier b) {
    if (position.x + width / 2 + velocity.x > rightBorder || position.x - width / 2 + velocity.x < 0) {
      playSound() ;
      velocity.x = - velocity.x ;
    }
    if (position.y - width / 2 + velocity.y < 0) {
      playSound() ;
      velocity.y = - velocity.y ;
    }
    if (CollisionDetector.circle2Square(position.x + velocity.x, position.y + velocity.y, width / 2, b.getX() + b.getWidth() / 2, b. getY() + b.getHeight(), b.getWidth(), b.getHeight())) {
      float bx = b.getX() ;
      float by = b.getY() ;
      float bw = b.getWidth() ;
      float bh = b.getHeight() ;
      if (position.x + velocity.x > bx && position.x + velocity.x < bx + bw && velocity.y > 0) {
        playSound() ;
        velocity.y = - velocity.y ;
      } else if (position.y + velocity.y > by && position.y + velocity.y  < by + bh && velocity.y > 0)
        velocity.x = - velocity.x ;
      else if (velocity.y > 0) {
        playSound() ;
        if (velocity.x > 0 && position.x <= bx) {
          velocity.y = - velocity.y ;
          velocity.x = - velocity.x ;
        } else if(velocity.x > 0 && position.x > bx){
          velocity.y = - velocity.y ;
        } else if (velocity.x < 0 && position.x > bx) {
          velocity.y = - velocity.y ;
          velocity.x = - velocity.x ;
        } else if(velocity.x < 0 && position.x <= bx){
          velocity.y = - velocity.y ;
        }
      }
    }
    position.add(velocity) ;
  }

  boolean collisionWithGround(float line) {
    if (position.y  > line && velocity.y > 0)
      return true ;
    else 
    return false ;
  }

  void playSound() {
    player.rewind() ;
    player.play() ;
  }

  boolean collisionWithBrick(Brick b) {
    if (CollisionDetector.circle2Square(position.x + velocity.x, position.y + velocity.y, width / 2, b.getX() + b.getWidth() / 2, b. getY() + b.getHeight(), b.getWidth(), b.getHeight())) {
      float bx = b.getX() ;
      float by = b.getY() ;
      float bw = b.getWidth() ;
      float bh = b.getHeight() ;
      if (position.x + velocity.x > bx && position.x + velocity.x < bx + bw ) {
        velocity.y = - velocity.y ;
      } else if (position.y + velocity.y > by && position.y + velocity.y  < by + bh)
        velocity.x = - velocity.x ;
      else {
        velocity.y = - velocity.y ;
        velocity.x = - velocity.x ;
      }
      playSound() ;
      return true ;
    } else 
    return false ;
  }
}
