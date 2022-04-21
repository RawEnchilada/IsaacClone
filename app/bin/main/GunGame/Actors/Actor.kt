package GunGame;

import javafx.scene.canvas.GraphicsContext;
import GunGame.Math.Int2D;
import GunGame.Math.Double2D;


abstract class Actor(pos:Double2D,size:Double2D,zindex:Int) : Drawable(pos,size,zindex){
    var speed = 0.3;
    val center get() = position+size/2;

    var collider = Rectangle(this, pos, size);

    var fireRate = 1.5;
    var bulletSpeed = 5.0;
    var lastShot = Gl.elapsedTime;

    
    var health = 3;

    override fun Update(elapsed_ms:Long){
        collider.position = position;
    }

    fun gotHit(bullet:Projectile){
        health--;
        if(health <= 0)Dispose();
    }

    fun Shoot(vector:Double2D){
        if(Gl.elapsedTime-lastShot > 1000/fireRate){
            lastShot = Gl.elapsedTime;
            Projectile(this,10.0, center, 15.0, vector.normalized()*bulletSpeed);
        }
    }

    override fun Dispose(){
        collider.Dispose();
        super.Dispose();
    }
    
}