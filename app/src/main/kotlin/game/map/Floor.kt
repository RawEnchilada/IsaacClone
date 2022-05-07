package game.map;

import game.Gl
import game.extension.Int2D;


class Floor(var level: Int){

    var levelSize:Int = 4+level;

    private val grid = Array(levelSize) {Array<Room?>(levelSize) {null} };
    val gridAsList: MutableList<Room>
        get() {
            val list = mutableListOf<Room>();
            for(row in grid){
                for(r in row) {
                    if(r != null)list.add(r);
                }
            }
            list.sortBy { r -> r.index }
            return list;
        }

    private val directions = mutableListOf(
            Direction.down,
            Direction.up,
            Direction.left,
            Direction.right
    )

    init{
        var index = 0;
        val start = StartRoom(index++,levelSize/2,levelSize/2);
        grid[levelSize/2][levelSize/2] = start;

        val todo = mutableListOf<Room>(start);
        val toplace = mutableListOf<Room>();

        for (i in 0 until levelSize-1){
            toplace.add(Room(index++));
        }
        toplace.add(ItemRoom(index++));
        toplace.add(EndRoom(index));

        /*
        * place all the rooms in toplace in random directions starting from the StartRoom
        * when there is no room left to place, run through the rest and set their neighbors too
        */

        while (toplace.size > 0 || todo.size > 0){
            directions.shuffle();
            val current = todo.removeFirst();
            val neighbours = current.neighbors.count { r -> r != null }
            var denom = 4.0-neighbours;
            for (d in directions){
                val pos = current.gridPosition+Direction.getVector(d);
                if(pos.x < 0 || pos.x >= levelSize || pos.y < 0 || pos.y >= levelSize)continue;
                var room = getRoom(pos);

                //if the space in this direction is empty
                if(room == null  && Gl.randomDouble() < (1.0/denom) && toplace.size > 0){
                    room = toplace.removeFirst();
                    room.gridPosition = pos;
                    grid[pos.x][pos.y] = room;
                    todo.add(room);
                }

                if(room != null){
                    current.neighbors[d.value] = room;
                    room.neighbors[Direction.flip(d).value] = current;
                }

                denom--;
            }
        }

        for(row in grid){
            for(r in row) {
                r?.Finalize(this);
            }
        }

    }

    fun getRoom(gridPos:Int2D):Room?{
        return grid[gridPos.x][gridPos.y];
    }

    fun print(){
        for(row in grid){
            for( r in row){
                if(r == null)continue;
                println("${r.toString()} - gridpos:${r.gridPosition}, pos: ${r.position}");
                var i = -1;
                for (n in r.neighbors){
                    i++;
                    if (n == null)continue
                    println("\t$i. ${n.toString()}");
                }
            }
        }
    }


}