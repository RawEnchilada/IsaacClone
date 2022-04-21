package GunGame;

import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton
import GunGame.Math.Double2D;

object InputListener{
    private var keys = mutableSetOf<KeyCode>();
    private var mouseButtons = mutableSetOf<MouseButton>();
    var mousePosition = Double2D();

    object Setter{

        fun InputPressed(key:KeyEvent){
            keys.add(key.code);
        }
        fun InputReleased(key:KeyEvent){
            keys.remove(key.code);
        }
        fun MouseMoved(key:MouseEvent){
            mousePosition = Double2D(key.sceneX,key.screenY);
        }
        fun MousePressed(key:MouseEvent){
            mouseButtons.add(key.button);
        }
        fun MouseReleased(key:MouseEvent){
            mouseButtons.remove(key.button);
        }
    }

    fun isMouseDown(k:MouseButton):Boolean{
        return mouseButtons.contains(k);
    }
    fun isKeyDown(k:KeyCode):Boolean{
        return keys.contains(k);
    }
}