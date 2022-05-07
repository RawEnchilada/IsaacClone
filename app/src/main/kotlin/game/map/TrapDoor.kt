package game.map

import game.*

import game.actors.Player
import game.base.Collider
import game.base.Rectangle
import game.extension.Double2D
import javafx.geometry.Rectangle2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import java.io.FileInputStream


class TrapDoor(parent: Room) : Drawable(parent.center-Double2D(trapDoorSize.x/2, trapDoorSize.y),trapDoorSize,parent.zIndex+1){
    companion object{
        private val trapDoor = Image(FileInputStream("src/main/resources/trapDoor.png"));
        val trapDoorSize = Double2D(trapDoor.width/2,trapDoor.height);
    }

    val sprite: WritableImage = WritableImage(Door.doorSize.x.toInt(), Door.doorSize.y.toInt());
    private val imgview: ImageView;
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
                collider.Active = false;
                dispose();
            }
        }
        imgview = ImageView(trapDoor);
        close();
    }

    override fun update(elapsed_ms:Long){}

    override fun draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(sprite, pos.x, pos.y, size.x, size.y);
    }

    fun open(){
        imgview.viewport = Rectangle2D(0.0,0.0, trapDoorSize.x, trapDoorSize.y);
        imgview.snapshot(Door.params,sprite);
        isOpen = true;
    }
    fun close(){
        imgview.viewport = Rectangle2D(trapDoorSize.x,0.0, trapDoorSize.x, trapDoorSize.y);
        imgview.snapshot(Door.params,sprite);
        isOpen = false;
    }
}