package game.actors;


import game.AnimationData
import game.AnimationPlayer
import game.extension.Double2D;
import javafx.scene.canvas.GraphicsContext;
import game.map.Door;
import game.map.Room;
import game.Gl;
import game.items.HeartPickup;
import kotlin.math.absoluteValue
import javafx.scene.paint.Color


abstract class Enemy(room:Room,size:Double2D = Double2D(80.0,100.0)) : Actor(Double2D(),size,100){
    companion object{
        fun createEnemies(level:Int, room:Room):List<Enemy>{
            val count = level+1;
            val index = Gl.randomInt(0, enemyTypes.size);
            return enemyTypes[index](room,count);
        }
        //array of enemy constructors with different number of enemies returned
        private val enemyTypes = listOf(
            fun(room:Room,count:Int):List<Enemy>{return List(Gl.randomInt(0, count+1)){ TowerEnemy(room) }},
            fun(room:Room,count:Int):List<Enemy>{return List(Gl.randomInt(0, count+2)){ TankEnemy(room) }},
            fun(room:Room,count:Int):List<Enemy>{return List(Gl.randomInt(0, count+3)){ SpasticEnemy(room) }}
        );
    }

    abstract val anim: AnimationPlayer;
    var currentRoom: Room = room;

    init{
        val min:Double2D = room.position+Door.doorSize*2;
        val max:Double2D = room.position+room.size-Door.doorSize*2;
        position = Double2D(Gl.randomDouble(min.x,max.x),Gl.randomDouble(min.y,max.y));
        collider.rigid = true;
        collider.onLayer = 0b0010;
        collider.useLayer = 0b1100;
        collider.static = false;
        collider.active = false;
        collider.position = position;
        active = false;
    }
 
    override fun update(elapsed_ms:Long){
        collider.position = position;
        
        shoot((Player.player!!.position-position));
        anim.Update(elapsed_ms);
    }

    override fun draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(anim.sprite, pos.x, pos.y, size.x, size.y);
    }

    override fun die(){
        if(Gl.randomDouble() < ((Player.player!!.maxHealth-Player.player!!.health) / (Player.player!!.maxHealth / 2))){
            HeartPickup(position);
        }
        Gl.score += 100;
        super.die();
    }

    override fun dispose(){
        if(!isDisposed) {
            currentRoom.enemies.remove(this);
            super.dispose();
        }
    }

    
}

class TowerEnemy(room:Room) : Enemy(room){

    override val anim: AnimationPlayer = AnimationPlayer(
        "src/main/resources/enemies/tower.png",
        listOf(
			AnimationData("idle",4,true,2),
            AnimationData("shoot",4,false,1),
        )
    );

    init{
        fireRate = 0.5;
        collider.static = true;
    }

    override fun shoot(vector:Double2D):Boolean{
        if(Gl.elapsedTime-lastShot > 1000/fireRate){
            lastShot = Gl.elapsedTime;
            val p = Projectile(this,1, center, 15.0, vector,bulletSpeed);
            p.fillColor = Color.PURPLE;
            anim.Animate("shoot");
            return true;
        }
        return false;
    }

}

class TankEnemy(room:Room) : Enemy(room){
    override val anim: AnimationPlayer = AnimationPlayer(
            "src/main/resources/enemies/tank.png",
            listOf(
                    AnimationData("down",4,true,3),
                    AnimationData("up",4,true,3),
                    AnimationData("left",4,true,3),
                    AnimationData("right",4,true,3)
            )
    );
    init{
        speedMultiplier = 0.2;
        health = 5;
        anim.fps = 6;
    }
    override fun update(elapsed_ms:Long){
        val elapsed_s = (1000f/elapsed_ms.toFloat());
        val delta = (Player.player!!.position - position).normalized();
        if(delta.x.absoluteValue > delta.y.absoluteValue){
            if(delta.x > 0){
                anim.Animate("right");
            }
            else {
                anim.Animate("left");
            }
        }
        else{
            if(delta.y < 0){
                anim.Animate("up");
            }
            else {
                anim.Animate("down");
            }
        }
        position += delta*speed*elapsed_s;
        collider.position = position;
        anim.Update(elapsed_ms);
    }
}

class SpasticEnemy(room:Room) : Enemy(room,Double2D(64.0,64.0)){
    var targetPos = position;
    override val anim: AnimationPlayer = AnimationPlayer(
            "src/main/resources/enemies/spastic.png",
            listOf(
                    AnimationData("down",4,true,3),
                    AnimationData("up",4,true,3),
                    AnimationData("left",4,true,3),
                    AnimationData("right",4,true,3)
            )
    );
    init{
        speedMultiplier = 0.9;
        health = 2;
    }
    override fun update(elapsed_ms:Long){
        val elapsed_s = (1000f/elapsed_ms.toFloat());

        if(targetPos.distance(position) < 5.0){
            targetPos = Player.player!!.center;
            val pos = targetPos-position;
            if(pos.x.absoluteValue > pos.y.absoluteValue){
                targetPos.y = position.y;
            }
            else{
                targetPos.x = position.x;
            }
        }
        val delta = (targetPos - position).normalized();
        if(delta.x.absoluteValue > delta.y.absoluteValue){
            if(delta.x > 0){
                anim.Animate("right");
            }
            else {
                anim.Animate("left");
            }
        }
        else{
            if(delta.y < 0){
                anim.Animate("up");
            }
            else {
                anim.Animate("down");
            }
        }


        position += delta*speed*elapsed_s;
        collider.position = position;

        anim.Update(elapsed_ms);
    }
}