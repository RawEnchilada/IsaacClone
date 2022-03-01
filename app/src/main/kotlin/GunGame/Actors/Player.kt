package GunGame;


import GunGame.Math.Int2D;
import GunGame.Math.Double2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.event.EventType;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import GunGame.Extension.clamp;


class Player(starting:Room,pos:Double2D,size:Double2D = Double2D(100.0,77.7)) : Actor(pos,size,100){
    companion object{
        var player:Player? = null;
    }

    var sprite: Image;
    var currentRoom:Room = starting;

    init{
        sprite = Image(FileInputStream("src/main/resources/player.png")); 
        if(player == null)player = this;
        team = 1;
    }
 
    override fun Update(elapsed_ms:Long){
        val elapsed_s = (1000f/elapsed_ms.toFloat());

        if(InputListener.isKeyDown(KeyCode.W))position+=Double2D.constants.up*speed*elapsed_s;
        else if(InputListener.isKeyDown(KeyCode.S))position+=Double2D.constants.down*speed*elapsed_s;
        if(InputListener.isKeyDown(KeyCode.A))position+=Double2D.constants.left*speed*elapsed_s;
        else if(InputListener.isKeyDown(KeyCode.D))position+=Double2D.constants.right*speed*elapsed_s;
        if(InputListener.isKeyDown(KeyCode.SPACE))Drawable.CenterCamera(center);

        if(InputListener.isMouseDown(MouseButton.PRIMARY))Shoot((InputListener.mousePosition-getDrawPosition(center)));

        
        collider.position = position;
    }

    override fun Draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(sprite, pos.x.toDouble(), pos.y.toDouble(), size.x.toDouble(), size.y.toDouble());
    }

    fun moveToRoom(target:Room,d:Direction){
        currentRoom.SetActive(false);
        currentRoom = target;
        target.focusRoom();
        target.onEnter();
        when(d){
            Direction.left -> position.x = target.position.x+Room.roomSize.x-Door.doorSize.x-size.x;
            Direction.right -> position.x = target.position.x+Door.doorSize.x;
            Direction.up -> position.y = target.position.y+Room.roomSize.y-Door.doorSize.y-size.y;
            Direction.down -> position.y = target.position.y+Door.doorSize.y;
        }
    }

    
}