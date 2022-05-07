package game;

import game.base.Collider
import game.Item.Item
import game.actors.Player
import game.extension.Double2D
import game.extension.Int2D
import game.ui.HealthBar
import game.ui.Minimap
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import kotlin.random.Random
import game.map.*;
import game.ui.Menu

object Gl{

    var score = 0;
    private var r:Random = Random(0);
    private var seed:Int = 0;
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
    var running:Boolean = true
        private set

    fun initialize(w:Double,h:Double){

        wSize = Double2D(w,h);
        Drawable.cameraSize = Int2D(w,h);
        seed = (0..(Int.MAX_VALUE-50000)).random();
        r = Random(seed);
        println("seed: $seed");

        scene.addEventHandler(KeyEvent.KEY_PRESSED, fun(key){
            when (key.code) {
                KeyCode.F1 -> show_colliders = !show_colliders
                KeyCode.F2 -> ghost_mode = !ghost_mode
                KeyCode.F3 -> show_fps = !show_fps
                else -> {}
            };
        });

        Item.initItems();
        Menu();
    }
    fun restart(){
        score = 0;
        seed = (0..(Int.MAX_VALUE-50000)).random();
        r = Random(seed);
        println("seed: $seed");
        level = 0;
        nextFloor();
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
        var p: Player? = null;
        if(Player.player != null){
            score += 1000;
            p = Player.player;
        };
        level++;
        floor = null;
        Component.components.clear();
        Drawable.drawables.clear();
        Collider.colliders.clear();

        floor = Floor(level);
        Minimap(floor!!.gridAsList);
        HealthBar();
        game.ui.Cursor(false);

        if(p != null){
            Component.components.add(p);
            Drawable.drawables.add(p);
            Collider.colliders.add(p.collider);
        };
    }

    fun exit() {
        running = false;
    }

    fun disposeAll() {
        for(i in Component.components.size-1 downTo 0)Component.components[i].dispose();
        for(i in Drawable.drawables.size-1 downTo 0)Drawable.drawables[i].dispose();
        for(i in Collider.colliders.size-1 downTo 0)Collider.colliders[i].Dispose();
    }
}