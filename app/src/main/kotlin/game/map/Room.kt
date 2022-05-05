package game.map;

import game.Drawable;
import game.base.Rectangle;
import game.Gl;
import game.Actor.Actor
import game.Actor.Enemy
import game.Actor.Player
import game.Item.Item
import game.items.ItemPickup
import game.extension.Int2D;
import game.extension.Double2D;
import javafx.scene.canvas.GraphicsContext;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import kotlin.concurrent.thread

enum class Direction(val value:Int){
    left(0),
    right(1),
    up(2),
    down(3);

    companion object{
        fun flip(d:Direction):Direction{
            return when(d){
                left -> right;
                right -> left;
                up -> down;
                down -> up;
            }
        }

        fun getVector(d:Direction): Int2D {
            return when(d){
                left -> Int2D.constants.left;
                right -> Int2D.constants.right;
                up -> Int2D.constants.up;
                down -> Int2D.constants.down;
            }
        }

        fun getDirection(v: Int2D):Direction{
            return when(v){
                Int2D.constants.left -> Direction.left;
                Int2D.constants.right -> Direction.right;
                Int2D.constants.up -> Direction.up;
                Int2D.constants.down -> Direction.down;
                else -> Direction.left;
            }
        }
        fun getValues():Array<Int>{
            return arrayOf(0,1,2,3);
        }
    }
}


open class Room(val index:Int,v:Int2D,parent:Room?,direction:Direction) : Drawable(roomSize*v,10){
    companion object{
        val roomSize = Double2D(Gl.wSize.x,Gl.wSize.y);
        private val roomBackground = Image(FileInputStream("src/main/resources/room.png")); 
    }
    var gridPosition = v;
    val center:Double2D
        get() = position+roomSize/2;

    var neighbors: Array<Room?> = Array<Room?>(4){null};

    var background:Image;

    var isKnown = false;
    var colliders = mutableListOf<Rectangle>();
    var doors = mutableListOf<Door>();
    var enemies = mutableListOf<Actor>();
    var cleared = false;
    open val minimapColor = Color.color(0.6, 0.6, 0.6, 0.7);
    open val minimapColorCurrent = Color.color(0.8, 0.8, 0.8, 0.7);

    constructor(index:Int,x:Int,y:Int):this(index,Int2D(x,y),null,Direction.left);

    constructor(index:Int,v: Int2D):this(index,v,null,Direction.left);

    init{
        background = roomBackground;
        if(parent != null){
            neighbors[direction.ordinal] = parent;
            parent.neighbors[Direction.flip(direction).ordinal] = this;
        }        
        active = false;
    }

    fun Finalize(floor:Floor){
        for(d in Direction.getValues()){
            //check neighbors
            val direction = Direction.values()[d];
            val room = floor.getRoom(this.gridPosition+Direction.getVector(direction));
            if(room != null && neighbors[d] == null){
                neighbors[direction.ordinal] = room;
                room.neighbors[Direction.flip(direction).ordinal] = this;
            }

            val pos = Double2D();
            val doorPos = Double2D();
            val s = Double2D();
            when(d){
                0 -> {
                    s.y = roomSize.y;
                    s.x = Door.doorSize.x;
                    doorPos.y = roomSize.y/2-Door.doorSize.y/2;
                }
                1 -> {
                    pos.x = roomSize.x-Door.doorSize.x;
                    s.y = roomSize.y;
                    s.x = Door.doorSize.x;
                    doorPos.y = roomSize.y/2-Door.doorSize.y/2;
                    doorPos.x = roomSize.x-Door.doorSize.x;
                }
                2 -> {
                    s.y = Door.doorSize.y;
                    s.x = roomSize.x;
                    doorPos.x = roomSize.x/2-Door.doorSize.x/2;
                }
                3 -> {
                    pos.y = roomSize.y-Door.doorSize.y;
                    s.y = Door.doorSize.y;
                    s.x = roomSize.x;
                    doorPos.y = roomSize.y-Door.doorSize.y;
                    doorPos.x = roomSize.x/2-Door.doorSize.x/2;
                }
            }
            val r = Rectangle(this, position+pos, s);
            r.static = true;
            r.rigid = true;
            r.onLayer = 0b1000;
            r.useLayer = 0b0000;
            colliders.add(r);
            //create colliders and doors
            if(neighbors[d] != null){
                doors.add(Door(this, neighbors[d]!!, position+doorPos, Direction.values()[d]));
            }
        }
        PrepareRoom(floor);
    }

    open protected fun PrepareRoom(floor:Floor){
        enemies.addAll(Enemy.getEnemies(floor.level, this));
    }

    override fun Update(elapsed_ms:Long){
        if(enemies.size <= 0){
            if(!cleared)Cleared();
            for(d in doors){
                if(!d.isOpen)
                    d.open();
            }
        }
    }

    override fun Draw(gc:GraphicsContext){
        val pos = getDrawPosition(position);
        gc.drawImage(background, pos.x.toDouble(), pos.y.toDouble(), size.x.toDouble(), size.y.toDouble());

    }

    open fun Cleared(){
        cleared = true;
    }

    fun focusRoom(){
        Drawable.CenterCamera(center);
        Gl.minimap?.SetCurrentTile(index);
        for(n in neighbors){
            if (n != null) {
                n.isKnown = true
            };
        }
        thread {
            Thread.sleep(500);
            SetActive(true);
        }
    }

    fun SetActive(b:Boolean){
        active = b;
        for(c in colliders){
            c.active = b;
        }
        for(d in doors){
            d.collider.active = b;
        }
    }

    open fun onEnter(){
        if(enemies.size == 0)return;
        for(d in doors){
            d.close();
        }
        for(e in enemies){
            e.active = true;
            e.collider.active = true;
        }
    }

    override fun Dispose() {
        for(c in colliders)c.dispose();
        for(d in doors)d.Dispose();
        for(e in enemies)e.Dispose();
        super.Dispose();
    }
    
}

class StartRoom(index:Int,v:Int2D) : Room(index,v) {
    init{
        if(Player.player != null) {
            Player.player!!.position = center;
            Player.player!!.currentRoom = this;
        }
        else Player(this,center);

        isKnown = true;
    }
    constructor(index:Int,x:Int,y:Int):this(index,Int2D(x,y));

    override fun PrepareRoom(floor:Floor){
        focusRoom();
    }
}


class EndRoom(index:Int,v:Int2D) : Room(index,v) {
    override val minimapColor = Color.color(0.6, 0.1, 0.1, 0.7);
    override val minimapColorCurrent = Color.color(0.8, 0.2, 0.2, 0.7);

    constructor(index:Int,x:Int,y:Int):this(index,Int2D(x,y));

    val trapDoor = TrapDoor(this);

    override fun Update(elapsed_ms: Long) {
        if(enemies.size <= 0){
            if(!cleared)Cleared();
            for(d in doors){
                if(!d.isOpen)d.open();
            }
        }
    }

    override fun Cleared() {
        ItemPickup(center+Double2D(-16.0,64.0),Item.getRandomItem());
        trapDoor.open();
        super.Cleared();
    }
}


class ItemRoom(index:Int,v:Int2D) : Room(index,v) {
    constructor(index:Int,x:Int,y:Int):this(index,Int2D(x,y));
    override val minimapColor = Color.color(0.6, 0.6, 0.1, 0.7);
    override val minimapColorCurrent = Color.color(0.8, 0.8, 0.2, 0.7);

    override fun PrepareRoom(floor:Floor){
        ItemPickup(center,Item.getRandomItem());
    }
}