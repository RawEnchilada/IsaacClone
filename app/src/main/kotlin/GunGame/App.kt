package GunGame;

import javafx.scene.Scene;
import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.event.EventType;
import GunGame.Math.Int2D;
import GunGame.Math.Double2D;
import GunGame.UI.Minimap;


class Main : Application(){
    val width = 1280.0;
    val height = 720.0;

    override fun start(stage:Stage){

        val canvas = Canvas(width,height);
        val gc = canvas.getGraphicsContext2D();  
        val pane = Pane(canvas);
        val scene = Scene(pane,width, height);
        Gl.scene = scene;
        Gl.initialize(width,height);
        val fps = 60;

        gc.setImageSmoothing(false);

        var lastFrameTime = System.nanoTime();
        object:AnimationTimer() {
            override fun handle(current_ns: Long) {
                val elapsed_ms = (current_ns - lastFrameTime) / 1_000_000;
                if(elapsed_ms > 1000/fps){
                    lastFrameTime = current_ns;

                    Component.UpdateAll(elapsed_ms);
                    Collider.Collide();

                    gc.fill = Color.ALICEBLUE;
                    gc.clearRect(0.0,0.0,width,height);
                    Drawable.DrawAll(gc);


                    gc.fill = Color.WHITE;
                    gc.fillText("${1000 / elapsed_ms} fps", 10.0, 10.0);
                }
            }
        }.start();

        scene.addEventHandler(KeyEvent.KEY_PRESSED, fun(key){
            InputListener.Setter.InputPressed(key);
        });
        scene.addEventHandler(KeyEvent.KEY_RELEASED, fun(key){
            InputListener.Setter.InputReleased(key);
        });
        scene.addEventHandler(MouseEvent.MOUSE_MOVED, fun(key){
            InputListener.Setter.MouseMoved(key);
        });
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, fun(key){
            InputListener.Setter.MouseMoved(key);
        });
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, fun(key){
            InputListener.Setter.MousePressed(key);
        });
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, fun(key){
            InputListener.Setter.MouseReleased(key);
        });


        val f = Floor(1);
        Minimap(f.rooms);
        f.Finalize();

      
        println(Component.components.size);
        stage.scene = scene;
        stage.show();
    }


}

fun main(args: Array<String>){
    Application.launch(Main::class.java, *args);   
}



