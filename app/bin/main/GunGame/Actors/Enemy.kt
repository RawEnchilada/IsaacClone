package GunGame;


import GunGame.Math.Int2D;
import GunGame.Math.Double2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import GunGame.Extension.clamp;


class Enemy(room:Room,pos:Double2D,size:Double2D = Double2D(100.0,77.7)) : Actor(pos,size,100){

    var sprite: Image;
    var currentRoom:Room = room;

    init{
        sprite = Image(FileInputStream("src/main/resources/player.png")); 
        collider.rigid = true;
        collider.static = false;
        collider.active = false;
        active = false;
        team = 2;
    }
 
    override fun Update(elapsed_ms:Long){
        collider.position = position;
        
        Shoot((Player.player!!.position-position));
    }

    override fun Draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(sprite, pos.x.toDouble(), pos.y.toDouble(), size.x.toDouble(), size.y.toDouble());
    }

    override fun Dispose(){
        currentRoom.enemies.remove(this);
        super.Dispose();
    }

    
}