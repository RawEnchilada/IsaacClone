package GunGame;

import GunGame.Actor.Actor
import GunGame.Actor.Enemy
import GunGame.Extension.Double2D;
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import GunGame.Extension.Event;

open class Projectile(parent:Actor, var damage: Int, position:Double2D, var radius: Double, force:Double2D, speed:Double = 1.0):Drawable(position,20){

    private var force:Double2D = force.normalized();
    var collider:Collider = Circle(this, position, radius);
    var fillColor: Color = Color.WHITE;
    var strokeColor: Color = Color.BLACK;
    var ttl = 5000L;
    var updateEvent = Event<Long,Unit>();
    //if there is a false return value then the bullet will not be destroyed on hit.
    var onEnterEvent = Event<Collider,Boolean>();
    private val baseSpeed = 0.01;
    var speedMult = speed;

    init{
        collider.onLayer = parent.collider.onLayer;
        collider.useLayer = parent.collider.useLayer;
        collider.rigid = false;
        onEnterEvent += fun(o):Boolean{return o.parent is Enemy;}

        this.collider.onEnter = fun(other:Collider){
            if(other.parent is Actor)(other.parent as Actor).gotHit(this);
            var destroy = true;
            for (i in 0 until onEnterEvent.size){
                destroy = destroy && onEnterEvent[i](other);
            }
            if(destroy)Dispose();
        }
    }

    override fun Update(elapsed_ms:Long){
        val elapsed_s = (1000f/elapsed_ms.toFloat());
        updateEvent(elapsed_ms);
        this.position += force*elapsed_s*baseSpeed*speedMult;
        collider.position = position;
        (collider as Circle).radius = radius;
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