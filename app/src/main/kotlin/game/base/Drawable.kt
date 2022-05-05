package game;

import game.base.Collider
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import game.extension.Int2D;
import game.extension.Double2D;

abstract class Drawable(pos:Double2D,size:Double2D,zindex:Int) : Component(pos){
    
    companion object {
        var drawables = mutableListOf<Drawable>();
        val debugPoints = mutableListOf<Double2D>(); //TODO remove
        private var disposing = mutableListOf<Drawable>();
        
        fun DrawAll(gc:GraphicsContext,elapsed_ms:Long){
            gc.clearRect(0.0,0.0,Gl.wSize.x,Gl.wSize.y);
            for(d in drawables){
                d.Draw(gc);
            }    
            if(Gl.show_colliders){
                for(c in Collider.colliders){
                    if(c.active)gc.stroke = Color.GREEN;
                    else gc.stroke = Color.RED;
                    c.drawOutline(gc);
                }
            }
            if(Gl.show_fps){
                gc.fill = Color.BLACK;
                gc.fillText("${1000 / elapsed_ms} fps", 10.0, 10.0);
            }
            for (p in debugPoints){
                gc.fill = Color.PURPLE;
                gc.fillOval(p.x-5.0,p.y-5.0,10.0,10.0);
            }
            debugPoints.clear();
        }


        fun Dispose(){
            drawables.removeAll(disposing);
            disposing.clear();
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
        disposing.add(this);
        super.Dispose();      
    }
    
}