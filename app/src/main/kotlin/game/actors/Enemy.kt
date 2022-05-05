package game.Actor;


import game.AnimationPlayer
import game.extension.Double2D;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import game.map.Door;
import game.map.Room;
import game.Gl;
import game.items.HeartPickup;


abstract class Enemy(room:Room,size:Double2D = Double2D(80.0,100.0)) : Actor(Double2D(),size,100){
    companion object{
        fun getEnemies(level:Int,room:Room):List<Enemy>{
            val count = level+1;
            val index = Gl.randomInt(0, enemyTypes.size);
            return enemyTypes[index](room,count);
        }
        //array of enemy constructors with different number of enemies returned
        private val enemyTypes = listOf(
            fun(room:Room,count:Int):List<Enemy>{return List(Gl.randomInt(0, count+1)){TowerEnemy(room)}},
            fun(room:Room,count:Int):List<Enemy>{return List(Gl.randomInt(0, count+2)){TankEnemy(room)}},
            fun(room:Room,count:Int):List<Enemy>{return List(Gl.randomInt(0, count+3)){SpasticEnemy(room)}}
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
 
    override fun Update(elapsed_ms:Long){
        collider.position = position;
        
        Shoot((Player.player!!.position-position));
        anim.Update(elapsed_ms);
    }

    override fun Draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(anim.sprite, pos.x.toDouble(), pos.y.toDouble(), size.x.toDouble(), size.y.toDouble());
    }

    override fun Die(){
        if(Gl.randomDouble() < 0.3){
            HeartPickup(position);
        }
        Player.player?.score?.plus(100);
        super.Die();
    }

    override fun Dispose(){
        currentRoom.enemies.remove(this);
        super.Dispose();
    }

    
}

class TowerEnemy(room:Room) : Enemy(room){

    override val anim: AnimationPlayer = AnimationPlayer(
        "src/main/resources/tower.png",
        Double2D(80.0,100.0),
        mapOf(
                Pair("move",4)
        )
    );

    init{
        fireRate = 0.5;
    }

}

class TankEnemy(room:Room) : Enemy(room){
    override val anim: AnimationPlayer = AnimationPlayer(
            "src/main/resources/tank.png",
            Double2D(80.0,100.0),
            mapOf(
                    Pair("move",4)
            )
    );
    init{
        speedMultiplier = 0.2;
    }
    override fun Update(elapsed_ms:Long){
        val elapsed_s = (1000f/elapsed_ms.toFloat());
        position += (Player.player!!.position - position).normalized()*speed*elapsed_s;
        collider.position = position;
        anim.Update(elapsed_ms);
    }
}

class SpasticEnemy(room:Room) : Enemy(room,Double2D(64.0,64.0)){
    override val anim: AnimationPlayer = AnimationPlayer(
            "src/main/resources/spastic.png",
            Double2D(80.0,100.0),
            mapOf(
                    Pair("move",4)
            )
    );
    init{
        speedMultiplier = 1.0;
    }
    override fun Update(elapsed_ms:Long){
        val elapsed_s = (1000f/elapsed_ms.toFloat());
        val x = Math.random()*2-1;
        val y = Math.random()*2-1;
        position += (Double2D(x,y)*speed*elapsed_s);
        collider.position = position;
        anim.Update(elapsed_ms);
    }
}