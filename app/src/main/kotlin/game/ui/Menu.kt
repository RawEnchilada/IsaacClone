package game.ui

import game.Gl
import game.extension.Double2D
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font
import java.io.File

class Menu(pos: Double2D = Double2D(), size: Double2D = Gl.wSize) : UIElement(pos,size,1000) {

    private val buttons:List<UIButton>;
    private var text = "";

    init {
        Cursor(true);
        buttons = listOf(
            UIButton(Double2D(50.0,380.0), Double2D(125.0,60.0),"Start",fun(){
                this.dispose();
                Gl.nextFloor();
            }),
            UIButton(Double2D(50.0,460.0), Double2D(100.0,60.0),"Exit",fun(){
                this.dispose();
                Gl.exit();
            })
        );
        val f = File("highScore.dat");
        if(f.exists() && f.readText() != ""){
            text = "HighScore: ${f.readText()}";
        }
        else{
            f.writeText("");
        }
    }

    override fun draw(gc: GraphicsContext) {
        gc.fill = Color.BLACK;
        gc.font = Font.font(25.0);
        gc.fillText(text,50.0,250.0);
    }

    override fun update(elapsed_ms: Long) {

    }

    override fun dispose() {
        for(b in buttons)b.dispose();
        super.dispose();
    }
}