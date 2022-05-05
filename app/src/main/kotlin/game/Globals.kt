package game;

import game.Actor.Player
import game.base.Collider
import game.Item.Item
import game.extension.Double2D
import game.extension.Int2D
import game.ui.HealthBar
import game.ui.Minimap
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import kotlin.random.Random
import game.map.*;

object Gl{

    var r:Random = Random(0);
    var seed:Int = 0;
    var wSize:Double2D = Double2D();
    var minimap:Minimap? = null;
    var show_colliders = false;
    var ghost_mode = false;
    var show_fps = false;
    lateinit var scene:Scene;
    private val msAtStart:Long = System.nanoTime();
    val elapsedTime get() = (System.nanoTime()-msAtStart)/1_000_000;
    private var level = 0;
    private var floor:Floor? = null;

    fun initialize(w:Double,h:Double){

        wSize = Double2D(w,h);
        Drawable.cameraSize = Int2D(w,h);
        seed = (0..(Int.MAX_VALUE-50000)).random();
        r = Random(seed);
        println("seed: $seed");

        Gl.scene.addEventHandler(KeyEvent.KEY_PRESSED, fun(key){
            if(key.code == KeyCode.F1)show_colliders = !show_colliders;
            else if(key.code == KeyCode.F2)ghost_mode = !ghost_mode;
            else if(key.code == KeyCode.F3)show_fps = !show_fps;
        });
        Gl.scene.setCursor(Cursor.NONE);

        Item.initItems();
        nextFloor(); //TODO render menu first.
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

    fun nextFloor(){
        var p:Player? = null;
        if(Player.player != null){
            Player.player!!.score.plus(1000);
            p = Player.player;
        };
        level++;
        floor = null;
        Component.components.clear();
        Drawable.drawables.clear();
        Collider.colliders.clear();

        floor = Floor(level);
        Minimap(floor!!.rooms);
        HealthBar();
        game.ui.Cursor();
        floor!!.Finalize();

        if(p != null){
            Component.components.add(p);
            Drawable.drawables.add(p);
            Collider.colliders.add(p.collider);
        };
    }
}