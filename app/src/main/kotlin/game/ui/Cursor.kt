package game.ui;

import game.InputListener
import javafx.scene.canvas.GraphicsContext;
import java.io.FileInputStream;
import game.extension.Double2D;
import javafx.scene.image.Image;


class Cursor() : UIElement(Double2D(100.0,100.0),Double2D(32.0,32.0),1001){
    private val cursor = Image(FileInputStream("src/main/resources/cursor.png"));


    override fun Draw(gc:GraphicsContext){
        gc.drawImage(cursor,position.x-size.x/2.0,position.y-size.y/2.0,size.x,size.y);
    }

    override fun Update(elapsed_ms:Long){
        position = InputListener.mousePosition;
    }
}