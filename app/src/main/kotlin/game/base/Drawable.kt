package game;

import game.base.Collider
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import game.extension.Int2D;
import game.extension.Double2D;
import javafx.scene.image.Image
import javafx.scene.text.Font
import java.io.FileInputStream

abstract class Drawable(pos:Double2D,size:Double2D,zindex:Int) : Component(pos){
    
    companion object {
        var drawables = mutableListOf<Drawable>();
        private var disposing = mutableListOf<Drawable>();
        private val roomBackground = Image(FileInputStream("src/main/resources/room.png"));

        fun drawAll(gc:GraphicsContext, elapsed_ms:Long){//TODO possible optimization is to store components in fixed size arrays, and set active should add and remove them like in colliders.
            gc.clearRect(0.0,0.0,Gl.wSize.x,Gl.wSize.y);
            gc.drawImage(roomBackground, 0.0, 0.0, Gl.wSize.x, Gl.wSize.y);
            for(d in drawables){
                if(d.active)d.draw(gc);
            }    
            if(Gl.show_colliders){
                for(c in Collider.colliders){
                    if(c.active)gc.stroke = Color.GREEN;
                    else gc.stroke = Color.RED;
                    c.drawOutline(gc);
                }
            }
            if(Gl.show_fps){
                gc.font = Font.getDefault();
                gc.fill = Color.BLACK;
                gc.fillText("${1000 / elapsed_ms} fps", 10.0, 10.0);
            }
        }


        fun dispose(){
            drawables.removeAll(disposing);
            disposing.clear();
        }

        var cameraPosition = Double2D();
        var cameraSize = Int2D();

        //return the position where you need to render this object this frame.
        fun getDrawPosition(vec:Double2D):Double2D{
            return vec-cameraPosition;
        }
        fun centerCamera(vec:Double2D){
            cameraPosition = vec-cameraSize/2;
        }
    }

    var zIndex = zindex;
    var size = size;

    abstract fun draw(gc:GraphicsContext);


    init{
        if(drawables.size == 0 || zIndex >= drawables.last().zIndex){
            drawables.add(this);
        }
        else{
            for(i in 0 until drawables.size){
                if(drawables[i].zIndex >= this.zIndex ){
                    drawables.add(i, this);
                    break;
                }
            }
        }
    }

    constructor(pos:Double2D,zindex:Int):this(pos,cameraSize.toDouble2D(),zindex);
    constructor(pos:Double2D,size:Int2D,zindex:Int):this(pos,size.toDouble2D(),zindex);

    override fun dispose(){
        disposing.add(this);
        super.dispose();
    }
    
}