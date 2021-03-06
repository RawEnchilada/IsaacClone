package game

import game.extension.Double2D
import game.extension.Int2D
import javafx.geometry.Rectangle2D
import javafx.scene.SnapshotParameters
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.io.FileInputStream

class AnimationPlayer(
        sheetSrc:String,
        private val anims:List<AnimationData>
) {
    companion object{
        private val params = SnapshotParameters();
        init {
            params.fill = Color.TRANSPARENT;
        }
    }

    private var image: ImageView;
    private val writeableImage:WritableImage;
    val sprite: WritableImage get(){
        writeFrame();
        return writeableImage;
    }

    var fps:Int = 10;
    private val frameSize: Double2D;
    private var timeTilNextFrame:Long = 0;
    private val animations = mutableListOf<AnimationData>();
    private val grid:Int2D;
    private var lastAnimation:AnimationData;

    private var cf:Int = 0;
    var currentFrame:Int get() = cf;
                       set(value){
                           cf = value;
                           if(value >= ca.frameCount){
                               cf = 0;
                           }
                       }

    private var ca:AnimationData;

    /***
     * Change an animation, overriding priorities.
     */
    var animating:AnimationData get() = ca;
                                set(value){
                                    if(ca != value){
                                        if(ca.repeat)lastAnimation = ca;
                                        ca = value;
                                        cf = 0;
                                    }
                                }

    init{
        if(anims.size == 0)throw Exception("No animations provided.");

        val sheet = Image(FileInputStream(sheetSrc));
        var biggest = 0;
        for (anim in anims){if(anim.frameCount > biggest)biggest = anim.frameCount;}
        frameSize = Double2D(sheet.width/biggest,sheet.height/anims.size);
        grid = Int2D(sheet.width/frameSize.x,sheet.height/frameSize.y);
        writeableImage = WritableImage(frameSize.x.toInt(), frameSize.y.toInt());
        image = ImageView(sheet);
        for((i, anim) in anims.withIndex()){
            animations.add(anim);
            anim.index = i;
        }
        ca = animations.first();
        lastAnimation = animations.first();
    }
    private fun writeFrame(){
        image.viewport = getViewPort();
        image.snapshot(params,writeableImage);
    }
    private fun getViewPort():Rectangle2D{
        val x = frameSize.x*(cf % grid.x);
        val y = frameSize.y*ca.index;
        return Rectangle2D(x,y,frameSize.x,frameSize.y);
    }

    fun Update(elapsed_ms:Long){
        timeTilNextFrame -= elapsed_ms;
        if(timeTilNextFrame <= 0){
            timeTilNextFrame = 1000L/fps;
            if(!animating.repeat && currentFrame == animating.frameCount-1){
                animating = lastAnimation;
            }
            else{
                currentFrame++;
            }
        }
    }
    /***
     * Change an animation, with respect to priorities.
     */
    fun Animate(name:String) {
        val a = animations.find { n -> n.name == name; };
        if(a != null && a.priority <= ca.priority)animating = a;
    }

}

class AnimationData(
    val name:String,
    val frameCount:Int,
    val repeat:Boolean,
    val priority:Int = 10
){
    var index: Int = 0;
}