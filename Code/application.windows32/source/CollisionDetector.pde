static class CollisionDetector{
   static boolean circle2Square(float cx, float cy, float cr, float sx, float sy, float sw, float sh){
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
