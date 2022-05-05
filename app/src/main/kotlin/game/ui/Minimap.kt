package game.ui;

import game.Actor.Player
import game.Gl;
import game.extension.Double2D;
import game.map.Room;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


class Minimap(floor:MutableList<Room>,pos:Double2D = Gl.wSize*0.05,size:Double2D = Double2D(32.0*5+3,18.0*5+3)) : UIElement(pos,size,1000){

    val margin = 3;
    val tileSize = Double2D(32.0,18.0);
    val tiles:Array<Double2D>;
    val mapCenter = Double2D(2.5,2.5)*tileSize+margin;
    var maximized = false;
    var viewPosition = Double2D();
    var currentTile:Double2D;
    val floor:MutableList<Room>;

    init{
        if(Gl.minimap != null)Gl.minimap!!.Dispose();
        Gl.minimap = this;
        this.floor = floor;
        tiles = Array(floor.size){Double2D()};
        for(i in 0 until floor.size){
            tiles[i] = (tileSize*floor[i].gridPosition)+margin;
        }
        currentTile = tiles[0];
    }

    override fun Update(elapsed_ms:Long){

    }

    fun SetCurrentTile(index:Int){
        //center view
        currentTile = tiles[index];
        viewPosition = currentTile+tileSize*0.5-mapCenter+size/Gl.wSize*tileSize;
    }

    override fun Draw(gc:GraphicsContext){
        if(maximized){

        }
        else{
            gc.fill = Color.color(0.2, 0.2, 0.2, 0.7);
            gc.fillRoundRect(position.x, position.y, size.x, size.y, 5.0, 5.0);
            for(t in 0 until tiles.size){
                if(!floor[t].isKnown)continue;
                val pos = position+tiles[t]-viewPosition;
                gc.fill = floor[t].minimapColor;
                if(currentTile == tiles[t]){
                    gc.fill = floor[t].minimapColorCurrent;
                    gc.fillRoundRect(pos.x, pos.y, tileSize.x, tileSize.y, 5.0, 5.0);
                }
                //is in bounds at least partially
                else if(tiles[t].x+tileSize.x > viewPosition.x && tiles[t].x < viewPosition.x+size.x && tiles[t].y+tileSize.y > viewPosition.y && tiles[t].y < viewPosition.y+size.y){
                    gc.fillRoundRect(pos.x,
                                    pos.y,
                                    tileSize.x,
                                    tileSize.y,
                                    5.0, 5.0);
                }
            }
            gc.fill = Color.RED;
            if(Player.player != null){
                val playerPos = position+(Player.player!!.position / Gl.wSize * tileSize)-viewPosition;
                gc.fillOval(playerPos.x,playerPos.y,7.5,7.5);
            }
        }
    }
}