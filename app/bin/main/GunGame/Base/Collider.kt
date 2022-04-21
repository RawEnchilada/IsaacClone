package GunGame;

import GunGame.Math.Double2D;
import GunGame.Extension.clamp;
import GunGame.Extension.closestPointOnLineSegment;
import javafx.scene.canvas.GraphicsContext


abstract class Collider(parent:Component,pos:Double2D){
    companion object{
        var colliders = mutableListOf<Collider>();

        fun Collide(){
            for(i in colliders.size-1 downTo 0){
                for(n in i-1 downTo 0){
                    if(!colliders[i].active || !colliders[n].active)continue;
                    val colliding = colliders[i].isColliding(colliders[n]);
                    if(colliders[i].isIntersecting(colliders[n].closestPointTo(colliders[i]))){
                        if(colliding){
                            colliders[i].onStay(colliders[n]);
                            colliders[n].onStay(colliders[i]);
                        }
                        else{
                            colliders[i].collisions.add(colliders[n]);
                            colliders[n].collisions.add(colliders[i]);
                            colliders[i].onEnter(colliders[n]);
                            colliders[n].onEnter(colliders[i]);
                        }
                        if(colliders[i].rigid && colliders[n].rigid){
                            colliders[i].moveBack(colliders[n]);
                            colliders[n].moveBack(colliders[i]);
                        }
                    }
                    else{
                        if(colliding){
                            colliders[i].collisions.remove(colliders[n]);
                            colliders[n].collisions.remove(colliders[i]);
                            colliders[i].onExit(colliders[n]);
                            colliders[n].onExit(colliders[i]);
                        }
                    }
                }
            }
        }
    }

    var parent = parent;
    var position = pos;
    var rigid = true;
    var static = false;
    var active = true;

    private var collisions = mutableSetOf<Collider>();

    open val center:Double2D
        get() = position;

    init{
        colliders.add(this);
    }
    fun Dispose(){
        colliders.remove(this);
    }

    var onEnter: (other:Collider) -> Unit={};
    var onStay: (other:Collider) -> Unit={};
    var onExit: (other:Collider) -> Unit={};

    abstract fun closestPointTo(other:Collider):Double2D;
    abstract fun collisionVectorTo(point:Double2D):Double2D;
    abstract fun collisionVectorOrigin(point:Double2D):Double2D;
    abstract fun isIntersecting(point:Double2D):Boolean;

    abstract fun DrawOutline(gc:GraphicsContext);
    private fun isColliding(other:Collider):Boolean{
        return collisions.contains(other) || other.collisions.contains(this);
    }
    fun moveBack(other:Collider){
        if(!static){
            val closest1 = other.closestPointTo(this);
            val closest2 = closestPointTo(other);
            var vector = other.collisionVectorTo(closest1)-other.collisionVectorTo(closest2);

            if(other.static){
                parent.position += vector;
            }
            else{
                parent.position += vector/2;
            }
        }

    }


}

class Rectangle(parent:Component,pos:Double2D,size:Double2D):Collider(parent,pos){
    private var s:Double2D = size;
    private var inlinePoint1:Double2D = Double2D();
    private var inlinePoint2:Double2D = Double2D();

    var size: Double2D
        get() = s;
        set(value){
            s = value;
            if(s.x < s.y){
                inlinePoint1 = Double2D(s.x/2,s.x/2);
                inlinePoint2 = Double2D(s.x/2,s.y-s.x/2);
            }
            else{
                inlinePoint1 = Double2D(s.y/2,s.y/2);
                inlinePoint2 = Double2D(s.x-s.y/2,s.y/2);
            }
        }

    init{
        this.size = size;
    }

    override val center:Double2D
        get() = position+size/2;
    

    override fun closestPointTo(other:Collider):Double2D{
        val rvec = other.center-center;
        rvec.x = rvec.x.clamp(-size.x/2,size.x/2);
        rvec.y = rvec.y.clamp(-size.y/2,size.y/2);
        return center + rvec;
    }

    override fun isIntersecting(point:Double2D):Boolean{
        return (position.x+size.x > point.x && position.x < point.x &&
        position.y+size.y > point.y && position.y < point.y);
    }

    override fun collisionVectorTo(point:Double2D):Double2D{
        val p = point.closestPointOnLineSegment(position+inlinePoint1,position+inlinePoint2);
        return point-p;
    }
    override fun collisionVectorOrigin(point:Double2D):Double2D{
        return point.closestPointOnLineSegment(position+inlinePoint1,position+inlinePoint2);
    }

    override fun DrawOutline(gc:GraphicsContext){
        val pos = Drawable.getDrawPosition(position);
        gc.strokeRect(pos.x, pos.y, size.x, size.y);
    }
    
}

class Circle(parent:Component,pos:Double2D,radius:Double):Collider(parent,pos){
    var radius = radius;

    override val center:Double2D
        get() = position;


    override fun closestPointTo(other:Collider):Double2D{
        val rvec = other.center-center;
        return if(rvec.magnitude() > radius)rvec.normalized()*radius+center;
        else rvec;
    }

    override fun isIntersecting(point:Double2D):Boolean{
        return (center.distance(point) < radius);
    }
    override fun collisionVectorTo(point:Double2D):Double2D{
        val rvec = point-center;
        return if(rvec.magnitude() > radius)rvec.normalized()*radius+center;
        else rvec;
    }
    override fun collisionVectorOrigin(point:Double2D):Double2D{
        return center;
    }

    override fun DrawOutline(gc:GraphicsContext){
        val pos = Drawable.getDrawPosition(position-Double2D(radius,radius));
        gc.strokeOval(pos.x, pos.y, radius, radius);
    }
}