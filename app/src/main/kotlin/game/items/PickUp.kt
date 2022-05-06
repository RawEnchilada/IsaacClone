package game.items

import game.*
import game.actors.Player
import game.base.Collider
import game.base.Rectangle
import game.Item.Item
import game.extension.Double2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream

abstract class PickUp(pos: Double2D, size: Double2D) : Drawable(pos,size,19) {
    abstract val sprite: Image;
    val collider = Rectangle(this, Double2D(),size);
    init{
        collider.rigid = false;
        collider.onLayer = 0b0001;
        collider.useLayer = 0b0100;
        collider.onEnter = fun(other: Collider){
            if(other.parent is Player){
                onPickup(other.parent as Player);
                dispose();
            };
        }
    }

    override fun draw(gc: GraphicsContext) {
        val pos = getDrawPosition(position);
        gc.drawImage(sprite,pos.x,pos.y,size.x,size.y);
    }

    override fun update(elapsed_ms: Long) {
        collider.position = position;
    }

    abstract fun onPickup(p: Player);

    override fun dispose() {
        collider.Dispose();
        super.dispose();
    }


}

class HeartPickup(pos:Double2D) : PickUp(pos,Double2D(32.0,32.0)){
    override val sprite:Image = Image(FileInputStream("src/main/resources/half_heart.png"));

    private var force:Double2D;
    init{
        val x = Gl.randomDouble(-2.0,2.0);
        val y = Gl.randomDouble(-2.0,2.0);
        force = Double2D(x,y);
    }

    override fun update(elapsed_ms: Long) {
        position += force;
        force *= 0.95;
        super.update(elapsed_ms);
    }

    override fun onPickup(p: Player) {
        Gl.score += 25;
        p.health++;
    }
}

class ItemPickup(pos:Double2D, private val item: Item): PickUp(pos,Double2D(32.0,32.0)){
    init{
        collider.static = true;
    }

    override val sprite:Image = Image(FileInputStream(item.path));

    override fun onPickup(p: Player) {
        Gl.score += 50;
        p.items.add(item);
        item.onPickup(p);
    }
}