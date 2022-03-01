package GunGame;

import GunGame.Math.Double2D;
import GunGame.Component;
import GunGame.Drawable;
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

open class Projectile(parent:Actor,damage:Double,position:Double2D,radius:Double,force:Double2D):Drawable(position,20){

    var damage:Double;
    var force:Double2D;
    var collider:Collider;
    var radius:Double;
    var fillColor = Color.WHITE;
    var strokeColor = Color.BLACK;
    var ttl = 10000L;

    init{
        this.damage = damage;
        this.force = force;
        this.radius = radius;
        this.collider = Circle(this, position, radius);
        this.collider.rigid = false;
        team = parent.team;

        this.collider.onEnter = fun(other:Collider){
            if(other.rigid && other.parent.team != team){
                if(other.parent is Actor)(other.parent as Actor).gotHit(this);
                Dispose();
            }
            
        }
    }

    override fun Update(elapsed_ms:Long){
        this.position += force;
        collider.position = position;
        ttl -= elapsed_ms;
        if(ttl <= 0)Dispose();
    }

    override fun Draw(gc:GraphicsContext){
        val pos = getDrawPosition(position-Double2D(radius,radius));
        gc.stroke = strokeColor;
        gc.fill = fillColor;
        gc.fillOval(pos.x, pos.y, radius, radius);
    }

    override fun Dispose(){
        collider.Dispose();
        super.Dispose();
    }
}