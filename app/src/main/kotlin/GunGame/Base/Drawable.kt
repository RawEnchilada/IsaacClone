package GunGame;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import GunGame.Math.Int2D;
import GunGame.Math.Double2D;

abstract class Drawable(pos:Double2D,size:Double2D,zindex:Int) : Component(pos){
    
    companion object {
        var drawables = mutableListOf<Drawable>();

        
        fun DrawAll(gc:GraphicsContext){
            for(d in drawables){
                d.Draw(gc);
            }    
            if(Gl.DEBUG){
                gc.stroke = Color.RED;
                for(c in Collider.colliders){                    
                    c.DrawOutline(gc);
                }
            }        
        }

        var cameraPosition = Double2D();
        var cameraSize = Int2D();

        //return the position where you need to render this object this frame.
        fun getDrawPosition(vec:Double2D):Double2D{
            return vec-cameraPosition;
        }
        fun CenterCamera(vec:Double2D){
            cameraPosition = vec-cameraSize/2;
        }
    }

    public var zIndex = zindex;
    public var size = size;

    abstract fun Draw(gc:GraphicsContext);


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

    override fun Dispose(){
        drawables.remove(this);  
        super.Dispose();      
    }
    
}