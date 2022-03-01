package GunGame;

import kotlin.random.Random;
import GunGame.Math.Int2D;
import GunGame.Math.Double2D;
import GunGame.UI.Minimap;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.event.EventType;

object Gl{

    var r:Random = Random(0);
    var seed:Int = 0;
    var wSize:Double2D = Double2D();
    var minimap:Minimap? = null;
    var DEBUG = false;
    lateinit var scene:Scene;
    private val msAtStart:Long = System.nanoTime();
    val elapsedTime get() = (System.nanoTime()-msAtStart)/1_000_000;

    fun initialize(w:Double,h:Double){
        wSize = Double2D(w,h);
        Drawable.cameraSize = Int2D(w,h);
        seed = (0..(Int.MAX_VALUE-50000)).random();
        r = Random(seed);
        println("seed: $seed");

        Gl.scene.addEventHandler(KeyEvent.KEY_PRESSED, fun(key){
            if(key.code == KeyCode.F1)DEBUG = !DEBUG;
        });
    }
    fun randomDouble(from:Double = 0.0,to:Double = 1.0):Double{
        val result = r.nextDouble(from,to);
        r = Random(seed++);
        return result;
    }
    fun randomInt(from:Int,to:Int):Int{
        val result = r.nextInt(from, to);
        r = Random(seed++);
        return result;
    }
}