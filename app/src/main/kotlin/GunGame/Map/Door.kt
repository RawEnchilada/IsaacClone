package GunGame;

import GunGame.Math.Double2D;
import javafx.scene.canvas.GraphicsContext;
import java.io.FileInputStream;
import javafx.scene.image.Image;

class Door(parent:Room,target:Room,pos:Double2D,direction:Direction) : Drawable(pos,doorSize,parent.zIndex+1){
    companion object{
        private val doorUp = Image(FileInputStream("src/main/resources/doorup.png"));
        private val doorDown = Image(FileInputStream("src/main/resources/doordown.png"));
        private val doorRight = Image(FileInputStream("src/main/resources/doorright.png"));
        private val doorLeft = Image(FileInputStream("src/main/resources/doorleft.png"));
        val doorSize = Double2D(doorUp.width*2,doorUp.height*2);
    }

    var sprite:Image;
    var target = target;
    var collider:Rectangle;
    var isOpen = true;

    init{
        when(direction){
            Direction.left -> sprite = doorLeft;
            Direction.right -> sprite = doorRight;
            Direction.up -> sprite = doorUp;
            Direction.down -> sprite = doorDown;
        }
        collider = Rectangle(this, pos-Double2D(8.0,8.0), Double2D(doorSize.x+16.0,doorSize.y+16.0));
        collider.rigid = false;
        collider.static = true;
        collider.onEnter = fun(other:Collider){
            if(isOpen && other.parent is Player){
                (other.parent as Player).moveToRoom(target,direction);
            }
        }
    }

    override fun Update(elapsed_ms:Long){}

    override fun Draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(sprite, pos.x.toDouble(), pos.y.toDouble(), size.x.toDouble(), size.y.toDouble());
    }

    fun open(){
        isOpen = true;
    }
    fun close(){
        isOpen = false;
    }
}