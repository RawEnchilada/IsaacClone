package game

import game.extension.Double2D
import game.extension.Int2D
import javafx.geometry.Rectangle2D
import javafx.scene.SnapshotParameters
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import java.io.FileInputStream

class AnimationPlayer(
        sheetSrc:String,
        private val frameSize: Double2D,
        private val frameCount:Map<String,Int>
) {
    companion object{
        private val params = SnapshotParameters();
        init {
            params.fill = Color.TRANSPARENT;
        }
    }

    val sheet:Image;
    var sprite: WritableImage = WritableImage(frameSize.x.toInt(),frameSize.y.toInt());
    var fps:Int = 10;
    private var timeTilNextFrame:Long = 0;
    private val animations = mutableMapOf<String,ImageView>();
    private val grid:Int2D;

    private var cf:Int = 0;
    var currentFrame:Int get() = cf;
                       set(value){
                           cf = value;
                           if(value >= frameCount[ca]!!){
                               cf = 0;
                           }
                           writeFrame();
                       }

    private var ca:String = "";
    var currentAnimation:String get() = ca;
                                set(value){
                                    if(ca != value){
                                        ca = value;
                                        cf = 0;
                                    }
                                }

    init{
        if(frameCount.size == 0)throw Exception("No animations provided.");

        sheet = Image(FileInputStream(sheetSrc));
        grid = Int2D(sheet.width/frameSize.x,sheet.height/frameSize.y);
        var i = 0;
        for(anim in frameCount.entries){
            animations[anim.key] = ImageView(sheet);
        }
        currentAnimation = animations.keys.first();
    }
    private fun writeFrame(){
        animations[currentAnimation]!!.viewport = getViewPort();
        animations[currentAnimation]!!.snapshot(params,sprite);
    }
    private fun getViewPort():Rectangle2D{
        val x = frameSize.x*(cf % grid.x);
        val y = frameSize.y*Math.floor(cf.toDouble() / grid.x);
        return Rectangle2D(x,y,frameSize.x,frameSize.y);
    }

    fun Update(elapsed_ms:Long){
        timeTilNextFrame -= elapsed_ms;
        if(timeTilNextFrame <= 0){
            timeTilNextFrame = 1000L/fps;
            currentFrame++;
        }
    }


}