package game.ui

import game.actors.Player
import game.Gl
import game.extension.Double2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import java.io.FileInputStream
import kotlin.math.floor

class HealthBar(pos:Double2D = Double2D(Gl.wSize.x/2.0,32.0),size:Double2D = Double2D(72.0,32.0)) : UIElement(pos,size,1000) {
    private val half_heart = Image(FileInputStream("src/main/resources/half_heart.png"));
    private val full_heart = Image(FileInputStream("src/main/resources/full_heart.png"));



    override fun draw(gc: GraphicsContext) {
        if(Player.player == null)return;
        val num = Player.player!!.health;
        val fullhearts = floor(num / 2.0).toInt();
        val halfhearts = (num % 2);
        size.x = ((fullhearts+halfhearts)*40).toDouble();
        val pos = position-size/2;
        for (i in 0 until fullhearts){
            gc.drawImage(full_heart,pos.x+i*40,pos.y,32.0,32.0);
        }
        if(halfhearts % 2 == 1){
            gc.drawImage(half_heart,pos.x+fullhearts*40,pos.y,32.0,32.0);
        }
    }

    override fun update(elapsed_ms: Long) {
    }

}