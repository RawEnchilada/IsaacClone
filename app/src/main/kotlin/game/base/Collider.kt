package game.base;

import game.Component
import game.Drawable
import game.extension.Double2D;
import game.extension.clamp;
import javafx.scene.canvas.GraphicsContext


abstract class Collider(var parent: Component, private var pos: Double2D){
    companion object{
        var colliders = mutableListOf<Collider>();
        private var disposing = mutableListOf<Collider>();

        fun collideAll(){

            for(i in colliders.size-1 downTo 0){
                val first = colliders[i];
                for(n in i-1 downTo 0){
                    val second = colliders[n];
                    if(!first.matchingLayer(second))continue;
                    val colliding = first.isColliding(second);
                    if(first.isIntersecting(second.closestPointTo(first))){
                        if(colliding){
                            first.onStay(second);
                            second.onStay(first);
                        }
                        else{
                            first.collisions.add(second);
                            second.collisions.add(first);
                            first.onEnter(second);
                            second.onEnter(first);
                        }
                        if(first.rigid && second.rigid){
                            if(first.moved) first.moveBack(second);
                            else if(second.moved) second.moveBack(first);
                        }
                    }
                    else{
                        if(colliding){
                            first.collisions.remove(second);
                            second.collisions.remove(first);
                            first.onExit(second);
                            second.onExit(first);
                        }
                    }
                }
                first.applyVectors();
            }
        }

        fun dispose(){
            colliders.removeAll(disposing);
            disposing.clear();
        }
    }
    private var deltaVector = Double2D();
    private fun applyVectors(){
        currentCenter += deltaVector;
        deltaVector = Double2D();
        if(!static){
            parent.position = position
            lastPosition = position;
        };
    }

    init{
        colliders.add(this);
    }

    
    private val moved:Boolean get() = lastPosition != position && !static;
    protected var lastPosition = Double2D();
    var position:Double2D get() = pos;
                          set(value) {
                              lastPosition = pos;
                              pos = value;
                          }

    var rigid = true;

    //layers allow objects on the same layer to not collide with each other
    var onLayer = 0b0000;
    var useLayer = 0b0000;

    //can it move from collisions
    var static = false;

    //is the collider enabled
    private var _active = true;
    var active:Boolean get() = _active;
                set(value) {
                    if(value != _active){
                        if(value){
                            colliders.add(this);
                        }
                        else{
                            colliders.remove(this);
                        }
                        _active = value;
                    }
                }

    //how much can you push this collider [0..1]
    var weight = 0.5;

    //storing the colliders this object is in contact with
    private var collisions = mutableSetOf<Collider>();

    abstract var currentCenter:Double2D;
    protected abstract var lastCenter:Double2D;
    abstract val padding:Double2D;


    var onEnter: (other: Collider) -> Unit={};
    var onStay: (other: Collider) -> Unit={};
    var onExit: (other: Collider) -> Unit={};

    abstract fun closestPointTo(other: Collider):Double2D;
    abstract fun isIntersecting(point:Double2D):Boolean;
    abstract fun nearOnRay(origin:Double2D,dir:Double2D,offset:Double2D):Double2D;
    abstract fun farOnRay(origin:Double2D,dir:Double2D,offset:Double2D):Double2D;
    abstract fun getNormal(at:Double2D,dir: Double2D):Double2D;

    abstract fun drawOutline(gc:GraphicsContext);

    private fun isColliding(other: Collider):Boolean{
        return collisions.contains(other) || other.collisions.contains(this);
    }
    private fun matchingLayer(other: Collider):Boolean{
        return ((onLayer and other.useLayer) + (useLayer and other.onLayer)) > 0;
    }
    private fun moveBack(other: Collider){
        val rayDir = (currentCenter-lastCenter);
        val near = other.nearOnRay(lastCenter,rayDir,padding);
        val far = other.farOnRay(lastCenter,rayDir,padding);

        if(near.x > far.x){
            val t = near.x;
            near.x = far.x;
            far.x = t;
        }
        if(near.y > far.y){
            val t = near.y;
            near.y = far.y;
            far.y = t;
        }

        val nf = Double2D(near.x.coerceAtLeast(near.y),far.x.coerceAtMost(far.y)).clamp(0.0,1.0);
        val normal = getNormal(near,rayDir);


        val vec = (normal * rayDir.absolute() * (1-nf.x));
        var f1 = other.weight/(weight+other.weight);
        var f2 = weight/(weight+other.weight);
        if(other.static){
            f1 = 1.0;
            f2 = 0.0;
        }
        deltaVector += vec*f1;
        other.position -= vec*f2; //TODO prevent pushing inside colliders, maybe store if the collider has been moved back already this pass.
    }

    fun Dispose(){
        disposing.add(this);
    }

}

class Rectangle(parent: Component, pos:Double2D, var size:Double2D): Collider(parent,pos){

    override var currentCenter:Double2D
        get() = position+size/2;
        set(value) {position = value-size/2;}
    override var lastCenter:Double2D
        get() = lastPosition+size/2;
        set(value) {lastPosition = value-size/2;}
    override val padding: Double2D
        get() = size/2;
    

    override fun closestPointTo(other: Collider):Double2D{
        val rvec = other.currentCenter-currentCenter;
        rvec.x = rvec.x.clamp(-size.x/2,size.x/2);
        rvec.y = rvec.y.clamp(-size.y/2,size.y/2);
        return currentCenter + rvec;
    }

    override fun isIntersecting(point:Double2D):Boolean{
        return (position.x+size.x > point.x && position.x <= point.x &&
        position.y+size.y > point.y && position.y <= point.y);
    }

    override fun nearOnRay(origin: Double2D, dir: Double2D,offset: Double2D): Double2D {
        return ((position-offset-origin)*dir.inverse());
    }

    override fun farOnRay(origin: Double2D, dir: Double2D,offset: Double2D): Double2D {
        return ((position+size+offset-origin)*dir.inverse());
    }

    override fun getNormal(at: Double2D, dir: Double2D): Double2D {
        var normal = Double2D();
        if (at.x > at.y) {//normal pointing at x
            if (dir.x < 0)normal.x = 1.0;
            else normal.x = -1.0;
        }
        else if (at.x < at.y) {//normal pointing at y
            if (dir.y < 0)normal.y = 1.0;
            else normal.y = -1.0;
        }
        else{
            normal = (dir.normalized()*-1.0);
        }
        return normal;
    }

    override fun drawOutline(gc:GraphicsContext){
        val pos = Drawable.getDrawPosition(position);
        gc.strokeRect(pos.x, pos.y, size.x, size.y);
    }
    
}
