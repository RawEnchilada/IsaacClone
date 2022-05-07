package game.actors;

import game.Drawable
import game.base.Collider
import game.base.Rectangle
import game.extension.Double2D;
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import game.extension.Event;

open class Projectile(parent: Actor, var damage: Int, position:Double2D, var radius: Double, force:Double2D, speed:Double = 1.0): Drawable(position,20){

    private var force:Double2D = force.normalized();
    var collider: Rectangle = Rectangle(this, position, Double2D(radius-1,radius-1));
    var fillColor: Color = Color.WHITE;
    var strokeColor: Color = Color.BLACK;
    var ttl = 5000L;
    var updateEvent = Event<Long,Unit>();
    //if there is a false return value then the bullet will not be destroyed on hit.
    var onEnterEvent = Event<Collider,Boolean>();
    private val baseSpeed = 0.01;
    var speedMult = speed;

    init{
        collider.onLayer = 0b000001;
        collider.useLayer = parent.collider.useLayer;
        collider.rigid = false;
        //onEnterEvent += fun(o):Boolean{return o.parent is Enemy;}

        this.collider.onEnter = fun(other: Collider){
            if(other.parent is Actor)(other.parent as Actor).gotHit(this);
            var destroy = true;
            for (i in 0 until onEnterEvent.size){
                destroy = destroy && onEnterEvent[i](other);
            }
            if(destroy) dispose();
        }
    }

    override fun update(elapsed_ms:Long){
        val elapsed_s = (1000f/elapsed_ms.toFloat());
        updateEvent(elapsed_ms);
        this.position += force*elapsed_s*baseSpeed*speedMult;
        collider.position = position;
        collider.size = Double2D(radius-1,radius-1);
        ttl -= elapsed_ms;
        if(ttl <= 0) dispose();
    }

    override fun draw(gc:GraphicsContext){
        val pos = getDrawPosition(position-Double2D(0.5,0.5));
        gc.stroke = strokeColor;
        gc.fill = fillColor;
        gc.fillOval(pos.x, pos.y, radius, radius);
    }

    override fun dispose(){
        collider.Dispose();
        super.dispose();
    }
}