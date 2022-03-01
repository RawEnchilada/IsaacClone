package GunGame;

import GunGame.Math.Int2D;
import GunGame.Math.Double2D;
import javafx.scene.canvas.GraphicsContext;
import java.io.FileInputStream;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

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


open class Room(index:Int,v:Int2D,parent:Room?,direction:Direction) : Drawable(roomSize*v,10){
    companion object{
        val roomSize = Double2D(Gl.wSize.x,Gl.wSize.y);
        private val roomBackground = Image(FileInputStream("src/main/resources/room.png")); 
    }
    var gridPosition = v;
    val center:Double2D
        get() = position+roomSize/2;

    var neighbors: Array<Room?> = Array<Room?>(4){null};

    var background:Image;
    val index = index;

    var colliders = mutableListOf<Rectangle>();
    var doors = mutableListOf<Door>();
    var enemies = mutableListOf<Actor>();

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
            
            //create colliders and doors
            if(neighbors[d] == null){
                var pos = Double2D();
                var s = Double2D();
                when(d){
                    0 -> {
                        s.y = roomSize.y;
                        s.x = Door.doorSize.x;
                    }
                    1 -> {
                        pos.x = roomSize.x-Door.doorSize.x;
                        s.y = roomSize.y;
                        s.x = Door.doorSize.x;
                    }
                    2 -> {
                        s.y = Door.doorSize.y;
                        s.x = roomSize.x;
                    }
                    3 -> {
                        pos.y = roomSize.y-Door.doorSize.y;
                        s.y = Door.doorSize.y;
                        s.x = roomSize.x;
                    }
                }
                var r = Rectangle(this, position+pos, s);
                r.static = true;
                colliders.add(r);
            }
            else{
                var pos = Double2D();
                var s = Double2D();
                var w1 = Double2D();
                var w2 = Double2D();
                when(d){
                    0 -> {
                        s.y = roomSize.y/2-Door.doorSize.y/2;
                        s.x = Door.doorSize.x;
                        pos.y = s.y;
                        pos.x = 0.0;
                        w2.y = s.y+Door.doorSize.y;
                    }
                    1 -> {
                        s.y = roomSize.y/2-Door.doorSize.y/2;
                        s.x = Door.doorSize.x;
                        pos.y = s.y;
                        pos.x = roomSize.x-Door.doorSize.x;
                        w1.x = pos.x;
                        w2.x = w1.x;
                        w2.y = s.y+Door.doorSize.y;
                    }
                    2 -> {
                        s.y = Door.doorSize.y;
                        s.x = roomSize.x/2-Door.doorSize.x/2;
                        pos.y = 0.0;
                        pos.x = s.x;
                        w2.x = s.x+Door.doorSize.x;
                    }
                    3 -> {
                        s.y = Door.doorSize.y;
                        s.x = roomSize.x/2-Door.doorSize.x/2;
                        pos.y = roomSize.y-Door.doorSize.y;
                        pos.x = s.x;
                        w1.y = pos.y;
                        w2.y = w1.y;
                        w2.x = s.x+Door.doorSize.x;
                    }
                }
                doors.add(Door(this, neighbors[d]!!, position+pos, Direction.values()[d]));
                var r = Rectangle(this, position+w1, s);
                r.static = true;
                colliders.add(r);
                r = Rectangle(this, position+w2, s);
                r.static = true;
                colliders.add(r);
            }
        }
        PrepareRoom(floor);
    }

    open protected fun PrepareRoom(floor:Floor){
        for(i in 0..Gl.randomInt(0, floor.level)){
            val x = Gl.randomDouble(position.x+Door.doorSize.x,position.x+size.x-Door.doorSize.x);
            val y = Gl.randomDouble(position.y+Door.doorSize.y,position.y+size.y-Door.doorSize.y);
            enemies.add(Enemy(this,Double2D(x,y)));
        }
    }

    override fun Update(elapsed_ms:Long){
        if(enemies.size <= 0){
            for(d in doors){
                d.open();
            }
        }
    }

    override fun Draw(gc:GraphicsContext){
        var pos = getDrawPosition(position);
        gc.drawImage(background, pos.x.toDouble(), pos.y.toDouble(), size.x.toDouble(), size.y.toDouble());

    }

    fun focusRoom(){
        Drawable.CenterCamera(center);
        Gl.minimap?.SetCurrentTile(index);
        SetActive(true);
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

    
}

class StartRoom(index:Int,v:Int2D) : Room(index,v) {
    init{
        Player(this,center);
        focusRoom();
    }
    constructor(index:Int,x:Int,y:Int):this(index,Int2D(x,y));

    override protected fun PrepareRoom(floor:Floor){
    }
}


class BossRoom(index:Int,v:Int2D) : Room(index,v) {
    constructor(index:Int,x:Int,y:Int):this(index,Int2D(x,y));

    override protected fun PrepareRoom(floor:Floor){
    }
}


class ItemRoom(index:Int,v:Int2D) : Room(index,v) {
    constructor(index:Int,x:Int,y:Int):this(index,Int2D(x,y));
    //val item:Item;

    init{
        //generate item
    }

    override protected fun PrepareRoom(floor:Floor){
    }
}