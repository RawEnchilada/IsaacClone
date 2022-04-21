package GunGame;

import GunGame.Math.Int2D;
import GunGame.PathFinding.AStar;


class Floor(var level: Int){
    var levelSize:Int = level*2+4;

    var rooms:MutableList<Room>;


    init{
        //Place the special rooms separate from each other on a corner of the floor
        val quadrants = mutableListOf(Int2D(0,0),Int2D(levelSize/2,0),Int2D(0,levelSize/2),Int2D(levelSize/2,levelSize/2));

        var qri = Gl.randomInt(0, 3);
        var quadrant = quadrants[qri];
        val start = StartRoom(0,Gl.randomInt(quadrant.x, quadrant.x+levelSize/2),Gl.randomInt(quadrant.y, quadrant.y+levelSize/2));
        quadrants.removeAt(qri);

        qri = Gl.randomInt(0, 2);
        quadrant = quadrants[qri];
        val boss = BossRoom(1,Gl.randomInt(quadrant.x, quadrant.x+levelSize/2),Gl.randomInt(quadrant.y, quadrant.y+levelSize/2));
        quadrants.removeAt(qri);

        qri = Gl.randomInt(0, 1);
        quadrant = quadrants[qri];
        val item = ItemRoom(2,Gl.randomInt(quadrant.x, quadrant.x+levelSize/2),Gl.randomInt(quadrant.y, quadrant.y+levelSize/2));
        quadrants.removeAt(qri);

        val room1 = Room(3,Gl.randomInt(0, levelSize),Gl.randomInt(0, levelSize));
        val room2 = Room(4,Gl.randomInt(0, levelSize),Gl.randomInt(0, levelSize));

        rooms = mutableListOf( start,boss,item,room1,room2 );

        val grid = Array(levelSize){Array(levelSize){Gl.randomDouble()}};

        val star = AStar(grid);
        val paths = arrayOf(star.find(start.gridPosition,boss.gridPosition),
                star.find(start.gridPosition,item.gridPosition),
                star.find(start.gridPosition,room1.gridPosition),
                star.find(start.gridPosition,room2.gridPosition));

        val findRoom = fun(pos: Int2D):Room?{
            for(room in rooms){
                if(room.gridPosition == pos)return room;
            }
            return null;
        };

        val addPath = fun(path:Array<Int2D>){
            var parent:Room = start;
            for(i in path.indices){
                val result = findRoom(path[i]);
                if(result == null){
                    var d = Direction.getDirection(parent.gridPosition-path[i]);
                    val r = Room(rooms.size,path[i],parent,d);
                    parent = r;
                    rooms.add(r);
                }
                else{
                    parent = result;
                }
            }
        }

        for(p in paths){
            addPath(p);
        }


    }

    fun getRoom(gridPos:Int2D):Room?{
        for(r in rooms){
            if(r.gridPosition == gridPos)return r;
        }
        return null;
    }

    fun Finalize(){
        for(r in rooms){
            r.Finalize(this);
        }
    }
}