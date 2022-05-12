package game.ui

import game.Gl
import game.InputListener
import game.base.Collider
import game.base.Rectangle
import game.extension.Double2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.input.MouseButton
import javafx.scene.paint.Color
import javafx.scene.text.Font
import org.w3c.dom.css.Rect

class UIButton(
        pos: Double2D = Double2D(),
        size: Double2D = Double2D(50.0,30.0),
        var text:String,
        var onClick: () -> Unit = {}
) : UIElement(pos,size,1000) {

    private var hovering = false;
    private val collider = Rectangle(this,position-Double2D(0.0,size.y*0.9),size);

    init{
        collider.onLayer = 0b00001;
        collider.useLayer = 0b00001;
        collider.rigid = false;
        collider.static = true;
        collider.onEnter = fun(other:Collider){
            if(other.parent is Cursor){
                hovering = true;
                Gl.scene.cursor = javafx.scene.Cursor.HAND;
            }
        }
        collider.onExit = fun(other:Collider){
            if(other.parent is Cursor){
                hovering = false;
                Gl.scene.cursor = javafx.scene.Cursor.DEFAULT;
            }
        }
    }

    override fun draw(gc: GraphicsContext) {
        if(hovering){
            gc.fill = Color.ORANGE;
            gc.font = Font.font(size.y+1);
            gc.fillText(text, position.x+5, position.y);
        }
        else {
            gc.fill = Color.WHITE;
            gc.font = Font.font(size.y);
            gc.fillText(text, position.x, position.y);
        }
    }

    override fun update(elapsed_ms: Long) {
        if(hovering && InputListener.isMouseDown(MouseButton.PRIMARY)){
            onClick();
        }
    }

}