package game.ui;

import game.Gl
import game.InputListener
import game.base.Rectangle
import javafx.scene.canvas.GraphicsContext;
import java.io.FileInputStream;
import game.extension.Double2D;
import javafx.scene.Cursor
import javafx.scene.image.Image;


class Cursor(private val useDefault:Boolean) : UIElement(Double2D(100.0,100.0),Double2D(32.0,32.0),1001){
    private val cursor = Image(FileInputStream("src/main/resources/cursor.png"));
    private val collider = Rectangle(this,position, Double2D(1.0,1.0));
    init {
        collider.onLayer = 0b00001;
        collider.useLayer = 0b00001;
        collider.rigid = false;
        if(!useDefault)
            Gl.scene.cursor = Cursor.NONE;
        else
            Gl.scene.cursor = Cursor.DEFAULT;
    }

    override fun draw(gc:GraphicsContext){
        if(!useDefault)gc.drawImage(cursor,position.x-size.x/2.0,position.y-size.y/2.0,size.x,size.y);
    }

    override fun update(elapsed_ms:Long){
        position = InputListener.mousePosition;
        collider.position = position;
    }

    override fun dispose() {
        Gl.scene.cursor = Cursor.DEFAULT;
        super.dispose()
    }
}