package GunGame.Map

import GunGame.*

import GunGame.Actor.Player
import GunGame.Extension.Double2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import java.io.FileInputStream


class TrapDoor(parent: Room) : Drawable(parent.center-Double2D(trapDoorSize.x/2, trapDoorSize.y),trapDoorSize,parent.zIndex+1){
    companion object{
        private val trapDoorOpen = Image(FileInputStream("src/main/resources/trapDoor.png"));
        private val trapDoorClosed = Image(FileInputStream("src/main/resources/trapDoorClosed.png"));
        val trapDoorSize = Double2D(trapDoorOpen.width*2,trapDoorOpen.height*2);
    }

    var sprite:Image = trapDoorClosed;
    var collider: Rectangle = Rectangle(this, position, Double2D(trapDoorSize.x,trapDoorSize.y));
    var isOpen = false;

    init{
        collider.rigid = false;
        collider.onLayer = 0b0001;
        collider.useLayer = 0b0100;
        collider.static = true;
        collider.onEnter = fun(other: Collider){
            if(isOpen && other.parent is Player){
                Gl.nextFloor();
                collider.active = false;
                Dispose();
            }
        }
    }

    override fun Update(elapsed_ms:Long){}

    override fun Draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(sprite, pos.x, pos.y, size.x, size.y);
    }

    fun open(){
        sprite = trapDoorOpen;
        isOpen = true;
    }
    fun close(){
        sprite = trapDoorClosed;
        isOpen = false;
    }
}