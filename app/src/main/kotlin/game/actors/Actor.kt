package game.Actor;

import game.Drawable
import game.Gl
import game.extension.Double2D;
import game.Projectile
import game.base.Rectangle


abstract class Actor(pos:Double2D,size:Double2D,zindex:Int) : Drawable(pos,size,zindex){
    private val baseSpeed = 0.1;
    var speedMultiplier = 1.0;
    val speed get() = baseSpeed * speedMultiplier;
    val center get() = position+size/2;

    var collider = Rectangle(this, pos, size);

    var fireRate = 1.5;
    var bulletSpeed = 5.0;
    var lastShot = Gl.elapsedTime;

    private var _health = 3;
    var health get() = _health;
            set(value){
                _health = value;     
                if(_health <= 0)Dispose();
            }


    open fun ReduceHealth(amount:Int){
        health-=amount;
    }

    override fun Update(elapsed_ms:Long){
        collider.position = position;
    }

    open fun gotHit(bullet: Projectile){
        ReduceHealth(bullet.damage);
    }

    open fun Die(){
        Dispose();
    }

    open fun Shoot(vector:Double2D){
        if(Gl.elapsedTime-lastShot > 1000/fireRate){
            lastShot = Gl.elapsedTime;
            Projectile(this,1, center, 15.0, vector,bulletSpeed);
        }
    }

    override fun Dispose(){
        collider.dispose();
        super.Dispose();
    }
    
}