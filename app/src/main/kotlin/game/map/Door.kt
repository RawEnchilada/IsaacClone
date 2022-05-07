package game.map;

import game.Drawable;
import game.base.Rectangle;
import game.base.Collider;
import game.actors.Player
import game.extension.Double2D
import javafx.geometry.Rectangle2D
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import java.io.FileInputStream


class Door(parent:Room, var target: Room, pos:Double2D, direction:Direction) : Drawable(pos-Double2D(4.0,4.0),doorSize,parent.zIndex+1){
    companion object{
        private val doorOpen = Image(FileInputStream("src/main/resources/door.png"));
        val doorSize = Double2D(doorOpen.width/2,doorOpen.height);
        val params = SnapshotParameters();

        init{
            params.fill = Color.TRANSPARENT;
        }
    }

    val sprite:WritableImage = WritableImage(doorSize.x.toInt(), doorSize.y.toInt());
    private val imgview:ImageView;
    val collider: Rectangle;
    var isOpen = true;
    var rotation = 0.0;

    init{
        when(direction){
            Direction.left -> rotation = -90.0;
            Direction.right -> rotation = 90.0;
            Direction.up -> rotation = 0.0;
            Direction.down -> rotation = 180.0;
        }
        imgview = ImageView(doorOpen);
        imgview.rotate = rotation;
        open();

        collider = Rectangle(this, pos-Double2D(4.0,4.0), doorSize+Double2D(8.0,8.0));
        collider.rigid = false;
        collider.onLayer = 0b0001;
        collider.useLayer = 0b0100;
        collider.static = true;
        collider.onEnter = fun(other: Collider){
            if(isOpen && other.parent is Player){
                (other.parent as Player).moveToRoom(target,direction);
            }
        }
    }

    override fun update(elapsed_ms:Long){}

    override fun draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(sprite, pos.x, pos.y, size.x+8.0, size.y+8.0);
    }

    fun open(){
        imgview.viewport = Rectangle2D(0.0,0.0, doorSize.x, doorSize.y);
        imgview.snapshot(params,sprite);
        isOpen = true;
    }
    fun close(){
        imgview.viewport = Rectangle2D(doorSize.x,0.0, doorSize.x, doorSize.y);
        imgview.snapshot(params,sprite);
        isOpen = false;
    }
}