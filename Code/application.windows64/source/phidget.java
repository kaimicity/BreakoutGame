import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import com.phidget22.*; 
import ddf.minim.*; 

import com.phidget22.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class phidget extends PApplet {



float lineHeight ;
Minim minim ;
AudioPlayer laughPlayer ;
AudioPlayer collidePlayer ;
VoltageRatioInput chRota; 
VoltageRatioInput chStick; 
VoltageRatioInput chLight; 
DigitalOutput outPrepare ;
DigitalOutput outFire ;
DigitalOutput outFlash ;
DigitalInput inConfirm ;
Barrier barrier ;
Shell shell ;
float shellVelo ;
int flashMark ;
int maxFlashMark ;
int flashTime ;
int flashInterval ;
float brickX, brickY, brickWidth ;
int level ;
int score ;
int comboRate ;
int combo ;
int life ;
int maxLife ;
ArrayList bricks ;
PGraphics gameGraphic ;
PGraphics scoreGraphic ;
PFont f ;
boolean showCombo ;
boolean showFail ;
int button ;
float failX, failY, failW, failH ;
int buttonMark ;
int maxButtonMark ;
public void setup() {   
  
  gameGraphic = createGraphics(width, 9 * height / 10) ;
  scoreGraphic = createGraphics(width, height / 10) ;
  try {     
    chRota = new VoltageRatioInput();     
    chRota.setDeviceSerialNumber(407803);     
    chRota.setChannel(4);     
    chRota.open();
    chStick = new VoltageRatioInput();     
    chStick.setDeviceSerialNumber(407803);     
    chStick.setChannel(0);  
    chStick.open();
    chLight = new VoltageRatioInput();     
    chLight.setDeviceSerialNumber(407803);     
    chLight.setChannel(6);     
    chLight.open();
    outPrepare = new DigitalOutput();     
    outPrepare.setDeviceSerialNumber(407803);     
    outPrepare.setChannel(1);     
    outPrepare.open();
    outFire = new DigitalOutput();     
    outFire.setDeviceSerialNumber(407803);     
    outFire.setChannel(0);     
    outFire.open();
    outFlash = new DigitalOutput();     
    outFlash.setDeviceSerialNumber(407803);     
    outFlash.setChannel(2);     
    outFlash.open();
    inConfirm = new DigitalInput();     
    inConfirm.setDeviceSerialNumber(407803);     
    inConfirm.setChannel(0);     
    inConfirm.open();
  } 
  catch (PhidgetException e) {    
    System.out.println(e);
  }
  lineHeight = 0.9f * gameGraphic.height ;
  barrier = new Barrier(0.4f * width, lineHeight - 0.02f * gameGraphic.height, 0.2f * width, 0.02f * height, width, gameGraphic) ;
  shellVelo = 0.005f * width ;
  maxFlashMark = 90 ;
  flashMark = maxFlashMark ;
  flashTime = 30 ;
  flashInterval = maxFlashMark / flashTime ;
  minim = new Minim(this) ;
  laughPlayer = minim.loadFile("laugh.mp3") ;
  collidePlayer = minim.loadFile("collide.mp3") ;
  f = createFont("u27fog.ttf", 1) ;
  level = 1 ;
  bricks = new ArrayList() ;
  brickX = 0.05f * gameGraphic.width ;
  brickY = 0.05f * gameGraphic.height ;
  score = 0 ;
  comboRate = 1 ;
  combo = 0 ;
  maxLife = 5 ;
  life = 2 ;
  showCombo = false ;
  showFail = false ;
  button = 1 ;
  failX = 0.25f * gameGraphic.width ;
  failY = 0.25f * gameGraphic.height ;
  failW = 0.5f * gameGraphic.width ; 
  failH = 0.5f * gameGraphic.height ;
  buttonMark = 0 ;
  maxButtonMark = 90 ;
} 

public void draw() {  
  drawScore() ;
  drawGame() ;
  image(scoreGraphic, 0, 0) ;
  image(gameGraphic, 0, height - gameGraphic.height) ;
}

public void generateBricks() {
  bricks = new ArrayList() ;
  float bw =(width - brickX * 2) / 10 ;
  float bh = bw / 4 ;
  for (int i = 0; i < 10; i++) {
    int l = 10 / 2 + (int)(Math.random() * 10) ;
    for (int j = 0; j < l; j++) {
      Brick b = new Brick(brickX + i * bw, brickY + j * bh, bw, gameGraphic) ;
      bricks.add(b) ;
    }
  }
}

public void drawGame() {
  gameGraphic.beginDraw() ;
  gameGraphic.background(0) ; 
  if (bricks.size() == 0)
    generateBricks() ;
  gameGraphic.stroke(255) ;
  gameGraphic.line(0, lineHeight, width, lineHeight) ;
  try {
    if(!showFail)
      barrier.setAngle((float)chRota.getSensorValue() * PI + PI / 2) ;
    float xC = (float)chStick.getSensorValue() - 0.5f;
    float fp = (float)chLight.getSensorValue();
    if (Math.abs(xC) > 0.05f && !showFail)
      barrier.move(xC) ;
    if ((fp == 0 ) && !showFail){
      if(barrier.isPrepare()){
        shell = new Shell(barrier.getX() + barrier.getWidth() / 2, lineHeight, 0.03f * width, 0.03f * width,
                new PVector(shellVelo * (-sin(barrier.getAngle())), shellVelo * cos(barrier.getAngle())), width, collidePlayer, gameGraphic) ;
      }
      barrier.fire() ;
    }
    if (barrier.isFire() && !showFail) {
      outFire.setState(true) ;
      outPrepare.setState(false) ;
      shell.move(barrier) ;
      shell.draw() ;
      if(shell.collisionWithGround(lineHeight)){
        flashMark = 0 ;
        combo = 0 ;
        comboRate = 1 ;
        showCombo = false ;
        laughPlayer.rewind() ;
        laughPlayer.play() ;
        barrier.prepare() ;
        life-- ;
        if(life == 0)
          showFail = true ;
      }
      for(int i = 0 ; i < bricks.size() ; i++){
        if(shell.collisionWithBrick((Brick)(bricks.get(i)))){
          bricks.remove(i) ;
          if(bricks.size() == 0){
            level++ ;
            score += 20 ;
            barrier.prepare() ;
            if(level % 5 == 0)
              shellVelo *= 1.2f ;
            if(life < maxLife)
              life ++ ;
          }

          combo ++ ;
          showCombo = true ;
          if(combo % 50 == 0 && combo > 0 && life < maxLife)
            life ++ ;
          comboRate = combo / 10 + 1 ;
          score += comboRate ;
          break ;
        }
      }

    } else if ( barrier.isPrepare() && !showFail) {
      outPrepare.setState(true) ;
      outFire.setState(false) ;
    }
    if(flashMark < maxFlashMark){
      if((flashMark / flashInterval) % 2 == 0)
        outFlash.setState(true) ;
      else
        outFlash.setState(false) ;
      flashMark ++ ;
    }
  }
  catch (Exception e) {   
    System.out.println(e.toString());
  }
  for (Object o : bricks)
    ((Brick)o).draw() ;
  barrier.draw() ;
  if (showCombo)
    drawCombo() ;
  if (showFail)
    drawFail() ;
  gameGraphic.endDraw() ;
}

public void drawScore() {
  scoreGraphic.beginDraw() ;
  scoreGraphic.background(127) ;
  scoreGraphic.stroke(255) ;
  scoreGraphic.fill(127) ;
  for (int i = 0; i < maxLife; i ++)
    scoreGraphic.ellipse(0.05f * scoreGraphic.width + 0.04f * scoreGraphic.width * i, 0.5f * scoreGraphic.height, scoreGraphic.height / 2, scoreGraphic.height / 2) ;
  scoreGraphic.fill(255, 0, 0) ;
  for (int i = 0; i < life; i ++)
    scoreGraphic.ellipse(0.05f * scoreGraphic.width + 0.04f * scoreGraphic.width * i, 0.5f * scoreGraphic.height, scoreGraphic.height / 2, scoreGraphic.height / 2) ;
  scoreGraphic.textFont(f) ;
  scoreGraphic.textSize(scoreGraphic.width / 30) ;
  scoreGraphic.textAlign(CENTER, CENTER) ;
  scoreGraphic.fill(0) ;
  scoreGraphic.text("Score " + score, 0.5f * scoreGraphic.width, 0.3f * scoreGraphic.height) ;
  scoreGraphic.textAlign(RIGHT, CENTER) ;
  scoreGraphic.text("Level " + level, 0.95f * scoreGraphic.width, 0.3f * scoreGraphic.height) ;
  scoreGraphic.endDraw() ;
}

public void drawCombo() {
  gameGraphic.textFont(f) ;
  gameGraphic.textSize(gameGraphic.height / 15) ;
  gameGraphic.textAlign(LEFT, CENTER) ;
  gameGraphic.fill(255) ;
  if (combo > 0 && combo % 50 == 0 && life < maxLife) {
    gameGraphic.text("COMBO " + combo, 0.05f * gameGraphic.width, 0.92f * gameGraphic.height) ; 
    float tw = gameGraphic.textWidth("COMBO " + combo) ;
    gameGraphic.fill(255, 0, 0) ;
    gameGraphic.text("LIFE + 1", 0.1f * gameGraphic.width + tw, 0.92f * gameGraphic.height) ;
  } else
    gameGraphic.text("COMBO " + combo, 0.05f * gameGraphic.width, 0.92f * gameGraphic.height) ; 
    
}

public void drawFail() {
  gameGraphic.fill(255) ;
  gameGraphic.strokeWeight(0.05f * gameGraphic.height) ;
  gameGraphic.rect(failX, failY, failW, failH) ;
  gameGraphic.strokeWeight(1) ;
  gameGraphic.textFont(f) ;
  gameGraphic.textSize(gameGraphic.height / 20) ;
  gameGraphic.textAlign(CENTER, CENTER) ;
  gameGraphic.fill(0) ;
  gameGraphic.text("Your Score:", 0.5f * gameGraphic.width, 0.4f * gameGraphic.height) ;
  gameGraphic.text(score, 0.5f * gameGraphic.width, 0.5f * gameGraphic.height) ;
  int bc = color(127, 255 * ((float)Math.abs(buttonMark - maxButtonMark / 2 ) / (maxButtonMark / 2))) ;
  if(button == 1)
    gameGraphic.fill(bc) ;
  else
    gameGraphic.fill(255) ;
  gameGraphic.rect(failX + 0.15f * failW, failY + failH * 0.7f, 0.3f * failW, 0.2f * failH) ;
  if(button == 2)
    gameGraphic.fill(bc) ;
  else
    gameGraphic.fill(255) ;
  gameGraphic.rect(failX + 0.55f * failW, failY + failH * 0.7f, 0.3f * failW, 0.2f * failH) ;
  buttonMark++ ;
  if(buttonMark == maxButtonMark)
    buttonMark = 0 ;
  gameGraphic.fill(0) ;
  gameGraphic.text("Replay", failX + 0.3f * failW, failY + failH * 0.75f) ;
  gameGraphic.text("Exit", failX + 0.7f * failW, failY + failH * 0.75f) ;
  try{
    float buttonSwitch = (float)chStick.getSensorValue() - 0.5f;
    if(buttonSwitch > 0.3f)
      button = 2 ;
    else if(buttonSwitch < -0.3f)
      button = 1 ;
    if(inConfirm.getState()){
      if(button == 1){
        level = 1 ;
        showFail = false ;
        score = 0 ;
        life = 5 ;
        generateBricks() ;
        shellVelo = 0.005f * width ;
      }
      else
        exit() ;
    }
    
  }
  catch(Exception e){
    println(e.toString()) ;
  }
}
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

  public float getX() {
    return this.position.x ;
  }

  public float getY() {
    return this.position.y ;
  }

  public float getWidth() {
    return this.width ;
  }

  public float getHeight() {
    return this.height ;
  }

  public void setAngle(float angle) {
    this.angle = angle ;
  }
  
  public float getAngle(){
    return this.angle ;
  }

  public void fire() {
    this.status = "FIRE" ;
  }

  public void prepare() {
    this.status = "PREPARE" ;
  }

  public boolean isFire() {
    if (this.status.equals("FIRE"))
      return true ;
    else 
      return false ;
  }
  
  public boolean isPrepare() {
    if (this.status.equals("PREPARE"))
      return true ;
    else 
      return false ;
  }
  public void move(float x) {
    if (position.x + x * moveIncrement >= 0 && position.x + width + x * moveIncrement <= rightBorder)
      position.x += x * moveIncrement ;
  }

  public void draw() {
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
  
  public float getX() {
    return this.position.x ;
  }

  public float getY() {
    return this.position.y ;
  }

  public float getWidth() {
    return this.width ;
  }

  public float getHeight() {
    return this.height ;
  }
  public void draw(){
    graphic.fill(255) ;
    graphic.stroke(127) ;
    graphic.rect(position.x, position.y, width, height) ;
  }
  
}
static class CollisionDetector{
   public static boolean circle2Square(float cx, float cy, float cr, float sx, float sy, float sw, float sh){
    float cLeft = cx - cr ;
    float cRight = cx + cr ;
    float cTop = cy - cr ;
    float cBottom = cy + cr ;
    float sLeft = sx - sw / 2 ;
    float sRight = sx + sw / 2 ;
    float sTop = sy - sh / 2 ;
    float sBottom = sy + sh / 2 ;
    if(cx == sx){
      if(Math.abs(cy - sy) <= cr + sh /2)
        return true ;
      else
        return false ;
    }
    else if((cLeft <= sLeft && cRight >= sRight) && (cTop <= sTop && cBottom >= sBottom)){
      return true ; 
    }
    else if((cLeft >= sLeft && cRight <= sRight) && (cTop >= sTop && cBottom <= sBottom)){
      return true ; 
    }
    else if((cLeft <= sLeft && cRight >= sRight) && (cTop >= sTop && cBottom <= sBottom))
      return true ;
    else if((cLeft >= sLeft && cRight <= sRight) && (cTop <= sTop && cBottom >= sBottom))
      return true ;
    else{
      double k = (sy - cy) / (sx - cx) ;
      double b = sy - k * sx ; 
      double fa = k * k + 1 ;
      double fb = 2 * (k * b - k * cy -cx) ;
      double fc = Math.pow((b - cy), 2) + cx * cx - cr * cr ;
      double x1 = (- fb + Math.sqrt(fb * fb - 4 * fa * fc)) / (2 * fa) ;
      double x2 = (- fb - Math.sqrt(fb * fb - 4 * fa * fc)) / (2 * fa) ;
      double y1 = k * x1 + b ;
      double y2 = k * x2 + b ;
      boolean p1InSquare = x1 < sx + sw / 2 && x1 > sx - sw / 2 && y1 < sy + sh /2 && y1 > sy -sh / 2 ;
      boolean p2InSquare = x2 < sx + sw / 2 && x2 > sx - sw / 2 && y2 < sy + sh /2 && y2 > sy -sh / 2 ;
      if(p1InSquare || p2InSquare){
        return true ;
      }
      else 
        return false ;
    }
  }
}
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

  public float getX() {
    return this.position.x ;
  }

  public float getY() {
    return this.position.y ;
  }

  public void setPosition(PVector pst) {
    this.position = pst ;
  }

  public float getWidth() {
    return this.width ;
  }

  public float getHeight() {
    return this.height ;
  }

  public PVector getVelocity() {
    return this.velocity ;
  }

  public void setVelocity(PVector v) {
    this.velocity = v ;
  }

  public void draw() {
    graphic.fill(0, 255, 0) ;
    graphic.ellipse(position.x, position.y, width, height) ;
  }

  public void move(Barrier b) {
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

  public boolean collisionWithGround(float line) {
    if (position.y  > line && velocity.y > 0)
      return true ;
    else 
    return false ;
  }

  public void playSound() {
    player.rewind() ;
    player.play() ;
  }

  public boolean collisionWithBrick(Brick b) {
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
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "phidget" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
