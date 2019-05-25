import com.phidget22.* ;
import ddf.minim.*;
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
void setup() {   
  fullScreen();
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
  lineHeight = 0.9 * gameGraphic.height ;
  barrier = new Barrier(0.4 * width, lineHeight - 0.02 * gameGraphic.height, 0.2 * width, 0.02 * height, width, gameGraphic) ;
  shellVelo = 0.005 * width ;
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
  brickX = 0.05 * gameGraphic.width ;
  brickY = 0.05 * gameGraphic.height ;
  score = 0 ;
  comboRate = 1 ;
  combo = 0 ;
  maxLife = 5 ;
  life = 2 ;
  showCombo = false ;
  showFail = false ;
  button = 1 ;
  failX = 0.25 * gameGraphic.width ;
  failY = 0.25 * gameGraphic.height ;
  failW = 0.5 * gameGraphic.width ; 
  failH = 0.5 * gameGraphic.height ;
  buttonMark = 0 ;
  maxButtonMark = 90 ;
} 

void draw() {  
  drawScore() ;
  drawGame() ;
  image(scoreGraphic, 0, 0) ;
  image(gameGraphic, 0, height - gameGraphic.height) ;
}

void generateBricks() {
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

void drawGame() {
  gameGraphic.beginDraw() ;
  gameGraphic.background(0) ; 
  if (bricks.size() == 0)
    generateBricks() ;
  gameGraphic.stroke(255) ;
  gameGraphic.line(0, lineHeight, width, lineHeight) ;
  try {
    if(!showFail)
      barrier.setAngle((float)chRota.getSensorValue() * PI + PI / 2) ;
    float xC = (float)chStick.getSensorValue() - 0.5;
    float fp = (float)chLight.getSensorValue();
    if (Math.abs(xC) > 0.05 && !showFail)
      barrier.move(xC) ;
    if ((fp == 0 ) && !showFail){
      if(barrier.isPrepare()){
        shell = new Shell(barrier.getX() + barrier.getWidth() / 2, lineHeight, 0.03 * width, 0.03 * width,
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
              shellVelo *= 1.2 ;
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

void drawScore() {
  scoreGraphic.beginDraw() ;
  scoreGraphic.background(127) ;
  scoreGraphic.stroke(255) ;
  scoreGraphic.fill(127) ;
  for (int i = 0; i < maxLife; i ++)
    scoreGraphic.ellipse(0.05 * scoreGraphic.width + 0.04 * scoreGraphic.width * i, 0.5 * scoreGraphic.height, scoreGraphic.height / 2, scoreGraphic.height / 2) ;
  scoreGraphic.fill(255, 0, 0) ;
  for (int i = 0; i < life; i ++)
    scoreGraphic.ellipse(0.05 * scoreGraphic.width + 0.04 * scoreGraphic.width * i, 0.5 * scoreGraphic.height, scoreGraphic.height / 2, scoreGraphic.height / 2) ;
  scoreGraphic.textFont(f) ;
  scoreGraphic.textSize(scoreGraphic.width / 30) ;
  scoreGraphic.textAlign(CENTER, CENTER) ;
  scoreGraphic.fill(0) ;
  scoreGraphic.text("Score " + score, 0.5 * scoreGraphic.width, 0.3 * scoreGraphic.height) ;
  scoreGraphic.textAlign(RIGHT, CENTER) ;
  scoreGraphic.text("Level " + level, 0.95 * scoreGraphic.width, 0.3 * scoreGraphic.height) ;
  scoreGraphic.endDraw() ;
}

void drawCombo() {
  gameGraphic.textFont(f) ;
  gameGraphic.textSize(gameGraphic.height / 15) ;
  gameGraphic.textAlign(LEFT, CENTER) ;
  gameGraphic.fill(255) ;
  if (combo > 0 && combo % 50 == 0 && life < maxLife) {
    gameGraphic.text("COMBO " + combo, 0.05 * gameGraphic.width, 0.92 * gameGraphic.height) ; 
    float tw = gameGraphic.textWidth("COMBO " + combo) ;
    gameGraphic.fill(255, 0, 0) ;
    gameGraphic.text("LIFE + 1", 0.1 * gameGraphic.width + tw, 0.92 * gameGraphic.height) ;
  } else
    gameGraphic.text("COMBO " + combo, 0.05 * gameGraphic.width, 0.92 * gameGraphic.height) ; 
    
}

void drawFail() {
  gameGraphic.fill(255) ;
  gameGraphic.strokeWeight(0.05 * gameGraphic.height) ;
  gameGraphic.rect(failX, failY, failW, failH) ;
  gameGraphic.strokeWeight(1) ;
  gameGraphic.textFont(f) ;
  gameGraphic.textSize(gameGraphic.height / 20) ;
  gameGraphic.textAlign(CENTER, CENTER) ;
  gameGraphic.fill(0) ;
  gameGraphic.text("Your Score:", 0.5 * gameGraphic.width, 0.4 * gameGraphic.height) ;
  gameGraphic.text(score, 0.5 * gameGraphic.width, 0.5 * gameGraphic.height) ;
  color bc = color(127, 255 * ((float)Math.abs(buttonMark - maxButtonMark / 2 ) / (maxButtonMark / 2))) ;
  if(button == 1)
    gameGraphic.fill(bc) ;
  else
    gameGraphic.fill(255) ;
  gameGraphic.rect(failX + 0.15 * failW, failY + failH * 0.7, 0.3 * failW, 0.2 * failH) ;
  if(button == 2)
    gameGraphic.fill(bc) ;
  else
    gameGraphic.fill(255) ;
  gameGraphic.rect(failX + 0.55 * failW, failY + failH * 0.7, 0.3 * failW, 0.2 * failH) ;
  buttonMark++ ;
  if(buttonMark == maxButtonMark)
    buttonMark = 0 ;
  gameGraphic.fill(0) ;
  gameGraphic.text("Replay", failX + 0.3 * failW, failY + failH * 0.75) ;
  gameGraphic.text("Exit", failX + 0.7 * failW, failY + failH * 0.75) ;
  try{
    float buttonSwitch = (float)chStick.getSensorValue() - 0.5;
    if(buttonSwitch > 0.3)
      button = 2 ;
    else if(buttonSwitch < -0.3)
      button = 1 ;
    if(inConfirm.getState()){
      if(button == 1){
        level = 1 ;
        showFail = false ;
        score = 0 ;
        life = 5 ;
        generateBricks() ;
        shellVelo = 0.005 * width ;
      }
      else
        exit() ;
    }
    
  }
  catch(Exception e){
    println(e.toString()) ;
  }
}
